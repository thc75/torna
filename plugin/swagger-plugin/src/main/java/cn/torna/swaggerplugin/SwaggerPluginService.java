package cn.torna.swaggerplugin;

import cn.torna.sdk.client.OpenClient;
import cn.torna.sdk.param.DebugEnv;
import cn.torna.sdk.param.DocItem;
import cn.torna.sdk.param.DocParamHeader;
import cn.torna.sdk.param.DocParamPath;
import cn.torna.sdk.param.DocParamReq;
import cn.torna.sdk.param.DocParamResp;
import cn.torna.sdk.param.IParam;
import cn.torna.sdk.request.DocPushRequest;
import cn.torna.sdk.response.DocPushResponse;
import cn.torna.swaggerplugin.bean.Booleans;
import cn.torna.swaggerplugin.bean.ControllerInfo;
import cn.torna.swaggerplugin.bean.DocParamInfo;
import cn.torna.swaggerplugin.bean.PluginConstants;
import cn.torna.swaggerplugin.bean.TornaConfig;
import cn.torna.swaggerplugin.builder.ApiDocBuilder;
import cn.torna.swaggerplugin.builder.ApiDocFieldDefinition;
import cn.torna.swaggerplugin.builder.DataType;
import cn.torna.swaggerplugin.exception.HiddenException;
import cn.torna.swaggerplugin.exception.IgnoreException;
import cn.torna.swaggerplugin.util.PluginUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springfox.documentation.annotations.ApiIgnore;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
public class SwaggerPluginService {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final TornaConfig tornaConfig;
    private final OpenClient client;
    private final ApiDocBuilder apiDocBuilder = new ApiDocBuilder();

    public SwaggerPluginService(RequestMappingHandlerMapping requestMappingHandlerMapping, TornaConfig tornaConfig) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.tornaConfig = tornaConfig;
        client = new OpenClient(tornaConfig.getUrl(), tornaConfig.getAppKey(), tornaConfig.getSecret());
    }

    public void init() {
        Objects.requireNonNull(requestMappingHandlerMapping, "requestMappingHandlerMapping can not null");
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        Map<ControllerInfo, List<DocItem>> controllerDocMap = new HashMap<>(32);
        List<Class<?>> hiddenClass = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (!match(handlerMethod)) {
                continue;
            }
            Class<?> controllerClass = handlerMethod.getBeanType();
            if (hiddenClass.contains(controllerClass)) {
                continue;
            }
            ControllerInfo controllerInfo;
            try {
                controllerInfo = buildControllerInfo(controllerClass);
            } catch (HiddenException | IgnoreException e) {
                hiddenClass.add(controllerClass);
                System.out.println(e.getMessage());
                continue;
            }
            List<DocItem> docItems = controllerDocMap.computeIfAbsent(controllerInfo, k -> new ArrayList<>());
            RequestMappingInfo requestMappingInfo = entry.getKey();
            try {
                DocItem apiInfo = buildDocItem(requestMappingInfo, handlerMethod);
                docItems.add(apiInfo);
            } catch (HiddenException | IgnoreException e) {
                System.out.println(e.getMessage());
            }
        }
        List<DocItem> docItems = mergeSameFolder(controllerDocMap);
        this.push(docItems);
    }

    private List<DocItem> mergeSameFolder(Map<ControllerInfo, List<DocItem>> controllerDocMap) {
        // key：文件夹，value：文档
        Map<String, List<DocItem>> folderDocMap = new HashMap<>();
        controllerDocMap.forEach((key, value) -> {
            List<DocItem> docItems = folderDocMap.computeIfAbsent(key.getName(), k -> new ArrayList<>());
            docItems.addAll(value);
        });
        List<ControllerInfo> controllerInfoList = controllerDocMap.keySet()
                .stream()
                .sorted(Comparator.comparing(ControllerInfo::getPosition))
                .collect(Collectors.toList());

        List<DocItem> folders = new ArrayList<>(controllerDocMap.size());
        for (Map.Entry<String, List<DocItem>> entry : folderDocMap.entrySet()) {
            String name = entry.getKey();
            ControllerInfo info = controllerInfoList
                    .stream()
                    .filter(controllerInfo -> name.equals(controllerInfo.getName()))
                    .findFirst()
                    .orElse(null);
            if (info == null) {
                continue;
            }
            DocItem docItem = new DocItem();
            docItem.setName(name);
            docItem.setDefinition(info.getDescription());
            docItem.setOrderIndex(info.getPosition());
            docItem.setIsFolder(Booleans.TRUE);
            List<DocItem> items = entry.getValue();
            items.sort(Comparator.comparing(DocItem::getOrderIndex));
            docItem.setItems(items);
            folders.add(docItem);
        }
        return folders;
    }

    protected void push(List<DocItem> docItems) {
        String token = tornaConfig.getToken();
        if (StringUtils.isEmpty(token)) {
            System.err.println("token不能为空");
            return;
        }
        DocPushRequest request = new DocPushRequest(token);
        // 设置请求参数
        request.setApis(docItems);
        request.setDebugEnvs(buildDebugEnvs());
        request.setAuthor(tornaConfig.getAuthor());
        if ("true".equals(tornaConfig.getDebug())) {
            System.out.println("-------- Torna配置 --------");
            System.out.println(JSON.toJSONString(tornaConfig, SerializerFeature.PrettyFormat));
            System.out.println("-------- 推送数据 --------");
            System.out.println(JSON.toJSONString(request));
        }
        // 发送请求
        DocPushResponse response = client.execute(request);
        if (response.isSuccess()) {
            System.out.println("推送成功");
        } else {
            System.out.println("推送失败，errorCode:" + response.getCode() + ",errorMsg:" + response.getMsg());
        }
    }

    protected List<DebugEnv> buildDebugEnvs() {
        String debugEnv = tornaConfig.getDebugEnv();
        if (StringUtils.isEmpty(debugEnv)) {
            return Collections.emptyList();
        }
        String[] envs = debugEnv.split("\\|");
        List<DebugEnv> debugEnvs = new ArrayList<>(envs.length);
        for (String env : envs) {
            String[] info = env.split(",");
            debugEnvs.add(new DebugEnv(info[0], info[1]));
        }
        return debugEnvs;
    }

    protected DocItem buildDocItem(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) throws HiddenException, IgnoreException {
        ApiOperation apiOperation = handlerMethod.getMethodAnnotation(ApiOperation.class);
        ApiIgnore apiIgnore = handlerMethod.getMethodAnnotation(ApiIgnore.class);
        if (apiOperation.hidden()) {
            throw new HiddenException("swagger接口隐藏（@ApiOperation.hidden=true）：" + apiOperation.value());
        }
        if (apiIgnore != null) {
            throw new IgnoreException("swagger接口忽略（@ApiIgnore）：" + apiOperation.value());
        }
        return buildDocItem(apiOperation, requestMappingInfo, handlerMethod);
    }

    protected DocItem buildDocItem(ApiOperation apiOperation, RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
        DocItem docItem = new DocItem();
        docItem.setName(apiOperation.value());
        docItem.setDescription(apiOperation.notes());
        docItem.setOrderIndex(apiOperation.position());
        docItem.setUrl(buildUrl(requestMappingInfo));
        String httpMethod = buildHttpMethod(apiOperation, handlerMethod);
        String contentType = buildContentType(httpMethod, apiOperation, handlerMethod);
        docItem.setHttpMethod(httpMethod);
        docItem.setContentType(contentType);
        docItem.setIsFolder(PluginConstants.FALSE);
        docItem.setPathParams(buildPathParams(handlerMethod));
        docItem.setHeaderParams(buildHeaderParams(handlerMethod));
        docItem.setQueryParams(buildQueryParams(handlerMethod, httpMethod));
        docItem.setRequestParams(buildRequestParams(handlerMethod, httpMethod));
        docItem.setResponseParams(buildResponseParams(handlerMethod));
        return docItem;
    }

    protected List<DocParamPath> buildPathParams(HandlerMethod handlerMethod) {
        List<ApiImplicitParam> apiImplicitParamList = buildApiImplicitParams(handlerMethod, param -> tornaConfig.getPathName().equalsIgnoreCase(param.paramType()));
        List<DocParamPath> docParamPaths = new ArrayList<>(apiImplicitParamList.size());
        if (!apiImplicitParamList.isEmpty()) {
            for (ApiImplicitParam apiImplicitParam : apiImplicitParamList) {
                DocParamPath docParamPath = new DocParamPath();
                docParamPath.setName(apiImplicitParam.name());
                docParamPath.setRequired(Booleans.toValue(apiImplicitParam.required()));
                docParamPath.setDescription(apiImplicitParam.value());
                docParamPath.setExample(apiImplicitParam.example());
                docParamPath.setType(getDataType(apiImplicitParam));
                docParamPaths.add(docParamPath);
            }
        }
        Method method = handlerMethod.getMethod();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                String name = pathVariable.value();
                if (StringUtils.isEmpty(name)) {
                    name = pathVariable.name();
                }
                if (StringUtils.isEmpty(name)) {
                    name = parameter.getName();
                }
                // 如果已经有了不添加
                if (containsName(docParamPaths, name)) {
                    continue;
                }
                DocParamInfo docParamInfo = buildDocParamInfo(parameter);
                DocParamPath docParamPath = new DocParamPath();
                docParamPath.setName(name);
                docParamPath.setType(docParamInfo.getType());
                docParamPath.setRequired(Booleans.toValue(pathVariable.required()));
                docParamPath.setDescription(docParamInfo.getDescription());
                docParamPath.setExample(docParamInfo.getExample());
                docParamPaths.add(docParamPath);
            }
        }
        return docParamPaths;
    }

    private static DocParamInfo buildDocParamInfo(Parameter parameter) {
        ApiParam apiParam = parameter.getAnnotation(ApiParam.class);
        String example = "";
        String desc = "";
        String type = "";
        boolean required = false;
        if (apiParam != null) {
            desc = apiParam.value();
            example = apiParam.example();
            type = apiParam.type();
            required = apiParam.required();
        }
        if (StringUtils.isEmpty(type)) {
            type = PluginUtil.getParameterType(parameter);
        }
        DocParamInfo docParamInfo = new DocParamInfo();
        docParamInfo.setDescription(desc);
        docParamInfo.setExample(example);
        docParamInfo.setType(type);
        docParamInfo.setRequired(required);
        return docParamInfo;
    }

    private static boolean containsName(List<? extends IParam> docParamPaths, String name) {
        if (docParamPaths == null) {
            return false;
        }
        for (IParam param : docParamPaths) {
            if (name.equalsIgnoreCase(param.getName())) {
                return true;
            }
        }
        return false;
    }

    protected List<DocParamHeader> buildHeaderParams(HandlerMethod handlerMethod) {
        List<ApiImplicitParam> apiImplicitParamList = buildApiImplicitParams(handlerMethod, param -> tornaConfig.getHeaderName().equalsIgnoreCase(param.paramType()));
        List<DocParamHeader> docParamHeaders = new ArrayList<>(apiImplicitParamList.size());
        if (!apiImplicitParamList.isEmpty()) {
            for (ApiImplicitParam apiImplicitParam : apiImplicitParamList) {
                DocParamHeader docParamHeader = new DocParamHeader();
                docParamHeader.setName(apiImplicitParam.name());
                docParamHeader.setRequired(Booleans.toValue(apiImplicitParam.required()));
                docParamHeader.setDescription(apiImplicitParam.value());
                docParamHeader.setExample(apiImplicitParam.example());
                docParamHeaders.add(docParamHeader);
            }
        }
        Method method = handlerMethod.getMethod();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            RequestHeader requestHeader = parameter.getAnnotation(RequestHeader.class);
            if (requestHeader != null) {
                String name = requestHeader.value();
                if (StringUtils.isEmpty(name)) {
                    name = requestHeader.name();
                }
                if (StringUtils.isEmpty(name)) {
                    name = parameter.getName();
                }
                // 如果已经有了不添加
                if (containsName(docParamHeaders, name)) {
                    continue;
                }
                DocParamInfo docParamInfo = buildDocParamInfo(parameter);
                DocParamHeader docParamHeader = new DocParamHeader();
                docParamHeader.setName(name);
                docParamHeader.setRequired(Booleans.toValue(requestHeader.required()));
                docParamHeader.setDescription(docParamInfo.getDescription());
                docParamHeader.setExample(docParamInfo.getExample());
                docParamHeaders.add(docParamHeader);
            }
        }
        return docParamHeaders;
    }

    protected List<DocParamReq> buildQueryParams(HandlerMethod handlerMethod, String httpMethod) {
        List<ApiImplicitParam> apiImplicitParamList = buildApiImplicitParams(handlerMethod, param -> tornaConfig.getQueryName().equalsIgnoreCase(param.paramType()));
        List<DocParamReq> docParamReqs = new ArrayList<>(apiImplicitParamList.size());
        if (!apiImplicitParamList.isEmpty()) {
            for (ApiImplicitParam apiImplicitParam : apiImplicitParamList) {
                DocParamReq paramReq = new DocParamReq();
                paramReq.setName(apiImplicitParam.name());
                paramReq.setRequired(Booleans.toValue(apiImplicitParam.required()));
                paramReq.setDescription(apiImplicitParam.value());
                paramReq.setExample(apiImplicitParam.example());
                paramReq.setType(getDataType(apiImplicitParam));
                docParamReqs.add(paramReq);
            }
        }
        Method method = handlerMethod.getMethod();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                String name = requestParam.value();
                if (StringUtils.isEmpty(name)) {
                    name = requestParam.name();
                }
                if (StringUtils.isEmpty(name)) {
                    name = parameter.getName();
                }
                // 如果已经有了不添加
                if (containsName(docParamReqs, name)) {
                    continue;
                }
                DocParamReq docParamReq = new DocParamReq();
                docParamReq.setName(name);
                docParamReq.setType(PluginUtil.getParameterType(parameter));
                docParamReq.setRequired(Booleans.toValue(requestParam.required()));
                docParamReqs.add(docParamReq);
            } else if (parameter.getAnnotations().length == 0) {
                if (HttpMethod.GET.name().equalsIgnoreCase(httpMethod)) {
                    Class<?> parameterType = parameter.getType();
                    boolean isPojo = PluginUtil.isPojo(parameterType);
                    // 当get请求时，遇到普通类则认为类中的属性都是query参数
                    if (isPojo) {
                        List<DocParamReq> docParamReqList = buildClassParams(parameterType);
                        docParamReqs.addAll(docParamReqList);
                    } else {
                        DocParamInfo docParamInfo = buildDocParamInfo(parameter);
                        DocParamReq docParamReq = new DocParamReq();
                        docParamReq.setName(parameter.getName());
                        docParamReq.setType(docParamInfo.getType());
                        docParamReq.setRequired(Booleans.toValue(docParamInfo.getRequired()));
                        docParamReq.setDescription(docParamInfo.getDescription());
                        docParamReq.setExample(docParamInfo.getExample());
                        docParamReqs.add(docParamReq);
                    }
                }
            }
        }
        return docParamReqs;
    }

    protected List<DocParamReq> buildRequestParams(HandlerMethod handlerMethod, String httpMethod) {
        List<ApiImplicitParam> apiImplicitParamList = buildApiImplicitParams(handlerMethod, param ->
                tornaConfig.getFormName().equalsIgnoreCase(param.paramType()) || tornaConfig.getBodyName().equalsIgnoreCase(param.paramType()));
        List<DocParamReq> docParamReqs = new ArrayList<>(apiImplicitParamList.size());
        if (!apiImplicitParamList.isEmpty()) {
            for (ApiImplicitParam apiImplicitParam : apiImplicitParamList) {
                DocParamReq paramReq = new DocParamReq();
                paramReq.setName(apiImplicitParam.name());
                paramReq.setRequired(Booleans.toValue(apiImplicitParam.required()));
                paramReq.setDescription(apiImplicitParam.value());
                paramReq.setExample(apiImplicitParam.example());
                paramReq.setType(getDataType(apiImplicitParam));
                docParamReqs.add(paramReq);
            }
        }
        Method method = handlerMethod.getMethod();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
            if (requestBody != null) {
                List<DocParamReq> docParamReqList = buildClassParams(parameter.getType());
                docParamReqs.addAll(docParamReqList);
            } else if (parameter.getAnnotations().length == 0) {
                Class<?> parameterType = parameter.getType();
                boolean isPojo = PluginUtil.isPojo(parameterType);
                // 当POST请求时，遇到普通类则认为类中的属性都是body参数
                boolean hasBodyMethod = HttpMethod.POST.name().equalsIgnoreCase(httpMethod)
                        || HttpMethod.PUT.name().equalsIgnoreCase(httpMethod);
                if (hasBodyMethod && isPojo) {
                    List<DocParamReq> docParamReqList = buildClassParams(parameterType);
                    docParamReqs.addAll(docParamReqList);
                }
            }
        }
        return docParamReqs;
    }

    protected List<DocParamResp> buildResponseParams(HandlerMethod handlerMethod) {
        MethodParameter returnType = handlerMethod.getReturnType();
        Class<?> type = returnType.getParameterType();
        Type genericParameterType = returnType.getGenericParameterType();
        if (!type.toString().equals(genericParameterType.toString())) {
            type = PluginUtil.getGenericType(genericParameterType);
        }
        return buildClassParams(type, DocParamResp::new);
    }

    protected <T extends DocParamReq> List<T> buildClassParams(Class<?> clazz, Supplier<T> supplier) {
        List<ApiDocFieldDefinition> apiDocFieldDefinitions = apiDocBuilder.buildApiDocFieldDefinition(clazz);
        return apiDocFieldDefinitions.stream()
                .map(apiDocFieldDefinition -> this.convert(apiDocFieldDefinition, supplier))
                .collect(Collectors.toList());
    }

    protected List<DocParamReq> buildClassParams(Class<?> clazz) {
        return buildClassParams(clazz, DocParamReq::new);
    }

    private <T extends DocParamReq> T convert(ApiDocFieldDefinition apiDocFieldDefinition, Supplier<T> supplier) {
        T docParamReq = supplier.get();
        BeanUtils.copyProperties(apiDocFieldDefinition, docParamReq);
        List<ApiDocFieldDefinition> childrenDefinition = apiDocFieldDefinition.getChildren();
        if (!CollectionUtils.isEmpty(childrenDefinition)) {
            List<DocParamReq> children = new ArrayList<>(childrenDefinition.size());
            for (ApiDocFieldDefinition child : childrenDefinition) {
                children.add(convert(child, supplier));
            }
            docParamReq.setChildren(children);
        }
        return docParamReq;
    }

    protected String getDataType(ApiImplicitParam apiImplicitParam) {
        Class<?> dataTypeClass = apiImplicitParam.dataTypeClass();
        if (dataTypeClass != Void.class) {
            return dataTypeClass.getSimpleName();
        } else {
            return apiImplicitParam.dataType();
        }
    }

    protected List<ApiImplicitParam> buildApiImplicitParams(HandlerMethod handlerMethod, ParamFilter filter) {
        ApiImplicitParams apiImplicitParams = handlerMethod.getMethodAnnotation(ApiImplicitParams.class);
        if (apiImplicitParams == null) {
            return Collections.emptyList();
        }
        List<ApiImplicitParam> apiImplicitParamList = new ArrayList<>(apiImplicitParams.value().length);
        for (ApiImplicitParam apiImplicitParam : apiImplicitParams.value()) {
            if (filter.filter(apiImplicitParam)) {
                apiImplicitParamList.add(apiImplicitParam);
            }
        }
        return apiImplicitParamList;
    }

    public String buildUrl(RequestMappingInfo requestMappingInfo) {
        Set<String> patterns = requestMappingInfo.getPatternsCondition().getPatterns();
        return patterns.iterator().next();
    }

    private String buildContentType(String httpMethod, ApiOperation apiOperation, HandlerMethod handlerMethod) {
        String consumes = apiOperation.consumes();
        if (StringUtils.hasText(consumes)) {
            return consumes;
        }
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        for (MethodParameter methodParameter : methodParameters) {
            RequestBody requestBody = methodParameter.getParameterAnnotation(RequestBody.class);
            if (requestBody != null) {
                return MediaType.APPLICATION_JSON_VALUE;
            }
            if (MultipartFile.class.isAssignableFrom(methodParameter.getParameterType())) {
                return MediaType.MULTIPART_FORM_DATA_VALUE;
            }
        }
        if (httpMethod.equalsIgnoreCase(HttpMethod.POST.name())) {
            return MediaType.APPLICATION_FORM_URLENCODED_VALUE;
        }
        return "";
    }

    protected String buildHttpMethod(ApiOperation apiOperation, HandlerMethod handlerMethod) {
        String httpMethod = apiOperation.httpMethod();
        if (StringUtils.hasText(httpMethod)) {
            return httpMethod;
        }
        RequestMapping requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            RequestMethod[] methods = requestMapping.method();
            if (methods.length == 0) {
                return this.tornaConfig.getMethodWhenMulti();
            } else {
                return methods[0].name();
            }
        }
        GetMapping getMapping = handlerMethod.getMethodAnnotation(GetMapping.class);
        if (getMapping != null) return HttpMethod.GET.name();
        PostMapping postMapping = handlerMethod.getMethodAnnotation(PostMapping.class);
        if (postMapping != null) return HttpMethod.POST.name();
        PutMapping putMapping = handlerMethod.getMethodAnnotation(PutMapping.class);
        if (putMapping != null) return HttpMethod.PUT.name();
        DeleteMapping deleteMapping = handlerMethod.getMethodAnnotation(DeleteMapping.class);
        if (deleteMapping != null) return HttpMethod.DELETE.name();
        PatchMapping patchMapping = handlerMethod.getMethodAnnotation(PatchMapping.class);
        if (patchMapping != null) return HttpMethod.PATCH.name();
        return HttpMethod.GET.name();
    }

    private ControllerInfo buildControllerInfo(Class<?> controllerClass) throws HiddenException, IgnoreException {
        Api api = AnnotationUtils.findAnnotation(controllerClass, Api.class);
        ApiIgnore apiIgnore = AnnotationUtils.findAnnotation(controllerClass, ApiIgnore.class);
        if (api != null && api.hidden()) {
            throw new HiddenException("swagger文档隐藏（@Api.hidden=true）：" + api.value());
        }
        if (apiIgnore != null) {
            throw new IgnoreException("swagger文档忽略（@ApiIgnore）：" + controllerClass.getName());
        }
        String name, description;
        int position = 0;
        if (api == null) {
            name = controllerClass.getSimpleName();
            description = "";
        } else {
            name = api.value();
            if (StringUtils.isEmpty(name) && api.tags().length > 0) {
                name = api.tags()[0];
            }
            description = api.description();
            position = api.position();
        }
        ControllerInfo controllerInfo = new ControllerInfo();
        controllerInfo.setName(name);
        controllerInfo.setDescription(description);
        controllerInfo.setPosition(position);
        return controllerInfo;
    }

    public boolean match(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(ApiOperation.class);
    }

    private interface ParamFilter {
        boolean filter(ApiImplicitParam param);
    }

}
