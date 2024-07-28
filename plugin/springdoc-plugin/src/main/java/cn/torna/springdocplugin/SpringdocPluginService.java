package cn.torna.springdocplugin;

import cn.torna.sdk.client.OpenClient;
import cn.torna.sdk.param.DebugEnv;
import cn.torna.sdk.param.DocItem;
import cn.torna.sdk.param.DocParamCode;
import cn.torna.sdk.param.DocParamHeader;
import cn.torna.sdk.param.DocParamPath;
import cn.torna.sdk.param.DocParamReq;
import cn.torna.sdk.param.DocParamResp;
import cn.torna.sdk.param.EnumInfoParam;
import cn.torna.sdk.param.IParam;
import cn.torna.sdk.request.DocPushRequest;
import cn.torna.sdk.request.EnumBatchPushRequest;
import cn.torna.sdk.response.DocPushResponse;
import cn.torna.sdk.response.EnumPushResponse;
import cn.torna.springdocplugin.bean.ApiParamWrapper;
import cn.torna.springdocplugin.bean.Booleans;
import cn.torna.springdocplugin.bean.CodeInfo;
import cn.torna.springdocplugin.bean.CodeItem;
import cn.torna.springdocplugin.bean.ControllerInfo;
import cn.torna.springdocplugin.bean.DocParamInfo;
import cn.torna.springdocplugin.bean.ModeEnum;
import cn.torna.springdocplugin.bean.PluginConstants;
import cn.torna.springdocplugin.bean.PushFeature;
import cn.torna.springdocplugin.bean.TornaConfig;
import cn.torna.springdocplugin.builder.ApiDocBuilder;
import cn.torna.springdocplugin.builder.DataType;
import cn.torna.springdocplugin.builder.FieldDocInfo;
import cn.torna.springdocplugin.builder.MvcRequestInfoBuilder;
import cn.torna.springdocplugin.builder.RequestInfoBuilder;
import cn.torna.springdocplugin.exception.HiddenException;
import cn.torna.springdocplugin.exception.IgnoreException;
import cn.torna.springdocplugin.util.ClassUtil;
import cn.torna.springdocplugin.util.PluginUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
public class SpringdocPluginService {

    private final TornaConfig tornaConfig;
    private final OpenClient client;
    private boolean existsApiIgnore = true;

    public SpringdocPluginService(TornaConfig tornaConfig) {
        this.tornaConfig = tornaConfig;
        client = new OpenClient(tornaConfig.getUrl());
        try {
            Class.forName("springfox.documentation.annotations.Hidden");
        } catch (ClassNotFoundException e) {
            existsApiIgnore = false;
            System.out.println("Warning: no 'springfox-core' dependency is imported, 'Hidden' check will be skipped.");
        }
    }

    public void pushDoc() {
        if (!tornaConfig.getEnable()) {
            return;
        }
        String basePackage = tornaConfig.getBasePackage();
        if (StringUtils.isEmpty(basePackage)) {
            throw new IllegalArgumentException("basePackage can not empty.");
        }
        this.doPush();
        this.pushCode();
    }

    protected void doPush() {
        String packageConfig = tornaConfig.getBasePackage();
        String[] pkgs = packageConfig.split(";");
        Set<Class<?>> classes = new HashSet<>();
        for (String basePackage : pkgs) {
            Set<Class<?>> clazzs = ClassUtil.getClasses(basePackage, Tag.class);
            classes.addAll(clazzs);
        }
        Map<ControllerInfo, List<DocItem>> controllerDocMap = new HashMap<>(32);
        for (Class<?> clazz : classes) {
            ControllerInfo controllerInfo;
            try {
                controllerInfo = buildControllerInfo(clazz);
            } catch (HiddenException | IgnoreException e) {
                System.out.println(e.getMessage());
                continue;
            }
            List<DocItem> docItems = controllerDocMap.computeIfAbsent(controllerInfo, k -> new ArrayList<>());
            ReflectionUtils.doWithMethods(clazz, method -> {
                try {
                    DocItem apiInfo = buildDocItem(new MvcRequestInfoBuilder(method, tornaConfig));
                    docItems.add(apiInfo);
                } catch (HiddenException | IgnoreException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.printf("Create doc error, method:%s%n", method);
                    throw new RuntimeException(e.getMessage(), e);
                }
            }, this::match);
        }
        List<DocItem> docItems = mergeSameFolder(controllerDocMap);
        this.push(docItems);
    }

    /**
     * 推送错误码
     */
    protected void pushCode() {
        List<CodeInfo> codes = this.tornaConfig.getCodes();
        if (CollectionUtils.isEmpty(codes)) {
            return;
        }
        checkCode(codes);
        formatCode(codes);
        String json = JSONArray.toJSONString(codes);
        List<EnumInfoParam> enumInfoParams = JSONArray.parseArray(json, EnumInfoParam.class);
        String token = tornaConfig.getToken();
        EnumBatchPushRequest request = new EnumBatchPushRequest(token);
        request.setEnums(enumInfoParams);
        // 发送请求
        EnumPushResponse response = client.execute(request);
        if (!response.isSuccess()) {
            System.out.println("Push code error，errorCode:" + response.getCode() + ",errorMsg:" + response.getMsg());
        }
    }

    private void checkCode(List<CodeInfo> codeInfos) {
        for (CodeInfo codeInfo : codeInfos) {
            if (StringUtils.isEmpty(codeInfo.getName())) {
                throw getDefineCodeException("missing code name");
            }
            List<CodeItem> items = codeInfo.getItems();
            if (CollectionUtils.isEmpty(items)) {
                throw getDefineCodeException("missing code items");
            }
            for (CodeItem item : items) {
                if (StringUtils.isEmpty(item.getValue())) {
                    throw getDefineCodeException("missing item.value");
                }
                if (StringUtils.isEmpty(item.getDescription())) {
                    throw getDefineCodeException("missing item.description");
                }
            }
        }
    }

    private static IllegalArgumentException getDefineCodeException(String msg) {
        return new IllegalArgumentException("Define code error, " + msg);
    }

    private void formatCode(List<CodeInfo> codeInfos) {
        for (CodeInfo codeInfo : codeInfos) {
            List<CodeItem> items = codeInfo.getItems();
            if (CollectionUtils.isEmpty(items)) {
                continue;
            }
            for (CodeItem item : items) {
                if (StringUtils.isEmpty(item.getType())) {
                    String type = codeInfo.getItemType();
                    item.setType(StringUtils.isEmpty(type) ? "string" : type);
                }
                if (StringUtils.isEmpty(item.getName())) {
                    item.setName(item.getValue());
                }
            }
        }
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
        formatIndex(docItems);
        String token = tornaConfig.getToken();
        DocPushRequest request = new DocPushRequest(token);
        // 设置请求参数
        request.setApis(docItems);
        request.setDebugEnvs(buildDebugEnvs());
        request.setAuthor(tornaConfig.getAuthor());
        request.setIsReplace(Booleans.toValue(tornaConfig.getIsReplace()));
        if (tornaConfig.getDebug()) {
            Boolean printFormat = tornaConfig.getDebugPrintFormat();
            System.out.println("-------- Torna config --------");
            printJson(tornaConfig, printFormat);
            System.out.println("-------- Push data --------");
            printJson(request, printFormat);
        }
        // 发送请求
        DocPushResponse response = client.execute(request);
        if (response.isSuccess()) {
            System.out.println("Push success");
        } else {
            System.out.println("Push error，errorCode:" + response.getCode() + ",errorMsg:" + response.getMsg());
        }
    }

    protected void formatIndex(List<DocItem> docItems) {
        for (DocItem docItem : docItems) {
            if (docItem.getIsFolder() == 1) {
                formatIndex(docItem.getItems());
            } else {
                formatDocParamReqIndex(docItem.getQueryParams());
                formatDocParamReqIndex(docItem.getRequestParams());
                formatDocParamRespIndex(docItem.getResponseParams());
            }
        }
    }

    protected void formatDocParamReqIndex(List<DocParamReq> docParamReqs) {
        int sum = docParamReqs.stream()
                .mapToInt(val -> {
                    Integer orderIndex = val.getOrderIndex();
                    return orderIndex == null ? 0 : orderIndex;
                })
                .sum();
        // 等于0表示所有字段都没有指定orderIndex，那么自动给他们排序从0开始
        if (sum == 0) {
            int i = 0;
            for (DocParamReq docParamReq : docParamReqs) {
                docParamReq.setOrderIndex(i++);
                List<DocParamReq> children = docParamReq.getChildren();
                if (children != null && children.size() > 0) {
                    formatDocParamReqIndex(children);
                }
            }
        }
    }

    protected void formatDocParamRespIndex(List<DocParamResp> docParamReqs) {
        int sum = docParamReqs.stream()
                .mapToInt(val -> {
                    Integer orderIndex = val.getOrderIndex();
                    return orderIndex == null ? 0 : orderIndex;
                })
                .sum();
        // 等于0表示所有字段都没有指定orderIndex，那么自动给他们排序从0开始
        if (sum == 0) {
            int i = 0;
            for (DocParamResp docParamResp : docParamReqs) {
                docParamResp.setOrderIndex(i++);
                List<DocParamResp> children = docParamResp.getChildren();
                if (children != null && children.size() > 0) {
                    formatDocParamRespIndex(children);
                }
            }
        }
    }


    private static void printJson(Object obj, boolean format) {
        if (format) {
            System.out.println(JSON.toJSONString(obj, SerializerFeature.PrettyFormat));
        } else {
            System.out.println(JSON.toJSONString(obj));
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

    protected DocItem buildDocItem(RequestInfoBuilder requestInfoBuilder) throws HiddenException, IgnoreException {
        Method method = requestInfoBuilder.getMethod();
        Operation apiOperation = method.getAnnotation(Operation.class);
        if (apiOperation.hidden()) {
            throw new HiddenException("Hidden API（@Operation.hidden=true）：" + apiOperation.summary());
        }
        if (existsApiIgnore) {
            Hidden apiIgnore = method.getAnnotation(Hidden.class);
            if (apiIgnore != null) {
                throw new IgnoreException("Ignore API（@Hidden）：" + apiOperation.summary());
            }
        }
        return doBuildDocItem(requestInfoBuilder);
    }

    protected DocItem doBuildDocItem(RequestInfoBuilder requestInfoBuilder) {
        Operation apiOperation = requestInfoBuilder.getApiOperation();
        Method method = requestInfoBuilder.getMethod();
        DocItem docItem = new DocItem();
        String httpMethod = requestInfoBuilder.getHttpMethod();
        docItem.setAuthor(buildAuthor(apiOperation));
        docItem.setName(apiOperation.summary());
        docItem.setDescription(apiOperation.description());
        docItem.setOrderIndex(buildOrder(apiOperation, method));
        docItem.setUrl(requestInfoBuilder.buildUrl());
        String contentType = requestInfoBuilder.buildContentType();
        docItem.setHttpMethod(httpMethod);
        docItem.setContentType(contentType);
        docItem.setIsFolder(PluginConstants.FALSE);
        docItem.setPathParams(buildPathParams(method));
        docItem.setHeaderParams(buildHeaderParams(method));
        docItem.setQueryParams(buildQueryParams(method, httpMethod));
        DocParamReqWrapper reqWrapper = buildRequestParams(method, httpMethod);
        DocParamRespWrapper respWrapper = buildResponseParams(method);
        docItem.setRequestParams(reqWrapper.getData());
        docItem.setResponseParams(respWrapper.getData());
        docItem.setIsRequestArray(reqWrapper.getIsArray());
        docItem.setRequestArrayType(reqWrapper.getArrayType());
        docItem.setIsResponseArray(respWrapper.getIsArray());
        docItem.setResponseArrayType(respWrapper.getArrayType());
        docItem.setErrorCodeParams(buildErrorCodes(apiOperation));
        return docItem;
    }

    protected int buildOrder(Operation apiOperation, Method method) {
        Order order = method.getAnnotation(Order.class);
        if (order != null) {
            return order.value();
        } else {
//            return apiOperation.position();
            return 0;
        }
    }

    protected String buildAuthor(Operation apiOperation) {
        return filterExtension(apiOperation, "author")
                .stream()
                .map(ExtensionProperty::name)
                .collect(Collectors.joining(","));
    }

    protected boolean isIgnoreParameter(java.lang.reflect.Parameter parameter) {
        Class<?> type = parameter.getType();
        if (ClassUtil.isSpecialType(type)) {
            return true;
        }
        Parameter apiModelProperty = AnnotationUtils.findAnnotation(parameter, Parameter.class);
        return apiModelProperty != null && apiModelProperty.hidden();
    }

    protected List<DocParamPath> buildPathParams(Method method) {
        List<Parameter> apiImplicitParamList = buildApiImplicitParams(method,
                param -> "path".equalsIgnoreCase(param.in().toString()));
        List<DocParamPath> docParamPaths = new ArrayList<>(apiImplicitParamList.size());
        if (!apiImplicitParamList.isEmpty()) {
            for (Parameter apiImplicitParam : apiImplicitParamList) {
                DocParamPath docParamPath = new DocParamPath();
                docParamPath.setName(apiImplicitParam.name());
                docParamPath.setRequired(Booleans.toValue(apiImplicitParam.required()));
                docParamPath.setDescription(apiImplicitParam.description());
                docParamPath.setExample(apiImplicitParam.example());
                docParamPath.setType(getDataType(apiImplicitParam));
                docParamPaths.add(docParamPath);
            }
        }
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        for (java.lang.reflect.Parameter parameter : parameters) {
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
                if (containsName(docParamPaths, name) || isIgnoreParameter(parameter)) {
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

    private static DocParamInfo buildDocParamInfo(java.lang.reflect.Parameter parameter) {
        ApiParamWrapper apiParamWrapper = new ApiParamWrapper(parameter.getAnnotation(Parameter.class));
        String example = apiParamWrapper.getExample();
        String desc = apiParamWrapper.getDescription();
        String type = apiParamWrapper.getType();
        boolean required = apiParamWrapper.getRequired();
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

    protected List<DocParamHeader> buildHeaderParams(Method method) {
        List<Parameter> apiImplicitParamList = buildApiImplicitParams(method,
                param -> "header".equalsIgnoreCase(param.in().toString()));
        List<DocParamHeader> docParamHeaders = new ArrayList<>(apiImplicitParamList.size());
        if (!apiImplicitParamList.isEmpty()) {
            for (Parameter apiImplicitParam : apiImplicitParamList) {
                DocParamHeader docParamHeader = new DocParamHeader();
                docParamHeader.setName(apiImplicitParam.name());
                docParamHeader.setRequired(Booleans.toValue(apiImplicitParam.required()));
                docParamHeader.setDescription(apiImplicitParam.description());
                docParamHeader.setExample(apiImplicitParam.example());
                docParamHeaders.add(docParamHeader);
            }
        }
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        for (java.lang.reflect.Parameter parameter : parameters) {
            RequestHeader requestHeader = parameter.getAnnotation(RequestHeader.class);
            if (requestHeader != null) {
                String name = getParameterName(parameter);
                // 如果已经有了不添加
                if (containsName(docParamHeaders, name) || isIgnoreParameter(parameter)) {
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

    protected List<DocParamReq> buildQueryParams(Method method, String httpMethod) {
        List<Parameter> apiImplicitParamList = buildApiImplicitParams(method, param ->
                "query".equalsIgnoreCase(param.in().toString()));
        List<DocParamReq> docParamReqs = new ArrayList<>(apiImplicitParamList.size());
        if (!apiImplicitParamList.isEmpty()) {
            for (Parameter apiImplicitParam : apiImplicitParamList) {
                DocParamReq paramReq = new DocParamReq();
                paramReq.setName(apiImplicitParam.name());
                paramReq.setRequired(Booleans.toValue(apiImplicitParam.required()));
                paramReq.setDescription(apiImplicitParam.description());
                paramReq.setExample(apiImplicitParam.example());
                paramReq.setType(getDataType(apiImplicitParam));
                docParamReqs.add(paramReq);
            }
        }
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        for (java.lang.reflect.Parameter parameter : parameters) {
            PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                continue;
            }
            RequestHeader requestHeader = parameter.getAnnotation(RequestHeader.class);
            if (requestHeader != null) {
                continue;
            }
            Class<?> parameterType = parameter.getType();
            String name = getParameterName(parameter);
            // 如果已经有了不添加
            if (containsName(docParamReqs, name) || isIgnoreParameter(parameter)) {
                continue;
            }
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            // 如果是Get请求
            if (httpMethod.equalsIgnoreCase(HttpMethod.GET.name()) || requestParam != null) {
                boolean isPojo = PluginUtil.isPojo(parameterType);
                // 当get请求时，遇到普通类则认为类中的属性都是query参数
                if (isPojo) {
                    List<DocParamReq> docParamReqList = buildReqClassParams(parameterType);
                    docParamReqs.addAll(docParamReqList);
                } else {
                    DocParamReq docParamReq = buildDocParamReq(parameter);
                    Optional<Boolean> requiredOpt = Optional.ofNullable(requestParam).map(RequestParam::required);
                    // 如果定义了RequestParam，required由它来指定
                    requiredOpt.ifPresent(aBoolean -> docParamReq.setRequired(Booleans.toValue(aBoolean)));
                    docParamReqs.add(docParamReq);
                }
            }
        }
        return docParamReqs;
    }

    protected DocParamReq buildDocParamReq(java.lang.reflect.Parameter parameter) {
        DocParamInfo docParamInfo = buildDocParamInfo(parameter);
        DocParamReq docParamReq = new DocParamReq();
        docParamReq.setName(getParameterName(parameter));
        docParamReq.setType(docParamInfo.getType());
        docParamReq.setRequired(Booleans.toValue(docParamInfo.getRequired()));
        docParamReq.setDescription(docParamInfo.getDescription());
        docParamReq.setExample(docParamInfo.getExample());
        return docParamReq;
    }

    /**
     * 获取参数名称
     *
     * @param parameter 参数
     * @return 返回参数名称
     */
    protected String getParameterName(java.lang.reflect.Parameter parameter) {
        ApiParamWrapper apiParamWrapper = new ApiParamWrapper(parameter.getAnnotation(Parameter.class));
        String fieldName = apiParamWrapper.getName();
        // 优先使用ApiParam中的name名称
        if (StringUtils.hasText(fieldName)) {
            return fieldName;
        }
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
        RequestHeader requestHeader = parameter.getAnnotation(RequestHeader.class);
        String name = parameter.getName();
        if (requestParam != null) {
            String val = requestParam.value();
            if (StringUtils.isEmpty(val)) {
                val = requestParam.name();
            }
            if (StringUtils.hasText(val)) {
                name = val;
            }
        }
        if (pathVariable != null) {
            String val = pathVariable.value();
            if (StringUtils.isEmpty(val)) {
                val = pathVariable.name();
            }
            if (StringUtils.hasText(val)) {
                name = val;
            }
        }
        if (requestHeader != null) {
            String val = requestHeader.value();
            if (StringUtils.isEmpty(val)) {
                val = requestHeader.name();
            }
            if (StringUtils.hasText(val)) {
                name = val;
            }
        }
        return name;
    }

    protected DocParamReqWrapper buildRequestParams(Method method, String httpMethod) {
        List<Parameter> apiImplicitParamList = buildApiImplicitParams(method, param ->
                "form".equalsIgnoreCase(param.in().toString())
                        || "body".equalsIgnoreCase(param.in().toString())
                        || param.in().toString().toLowerCase().contains("file")
        );
        List<DocParamReq> docParamReqs = new ArrayList<>(apiImplicitParamList.size());
        if (!apiImplicitParamList.isEmpty()) {
            for (Parameter apiImplicitParam : apiImplicitParamList) {
                // TODO: 待确认
                Class<?> dataTypeClass = apiImplicitParam.schema().implementation();
                if (dataTypeClass != Void.class) {
                    List<DocParamReq> docParamReqList = buildReqClassParams(dataTypeClass);
                    docParamReqs.addAll(docParamReqList);
                } else {
                    DocParamReq paramReq = new DocParamReq();
                    paramReq.setName(apiImplicitParam.name());
                    paramReq.setRequired(Booleans.toValue(apiImplicitParam.required()));
                    paramReq.setDescription(apiImplicitParam.description());
                    paramReq.setExample(apiImplicitParam.example());
                    paramReq.setType(getDataType(apiImplicitParam));
                    docParamReqs.add(paramReq);
                }
            }
        }
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        boolean array = false;
        String arrayElementDataType = DataType.OBJECT.getValue();
        for (java.lang.reflect.Parameter parameter : parameters) {
            try {
                String name = getParameterName(parameter);
                if (containsName(docParamReqs, name) || !isBodyParameter(parameter, httpMethod) || isIgnoreParameter(parameter)) {
                    continue;
                }
                int mode = tornaConfig.getMode();
                RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
                Class<?> type = parameter.getType();
                Type parameterizedType = parameter.getParameterizedType();
                ApiParamWrapper apiParamWrapper = new ApiParamWrapper(parameter.getAnnotation(Parameter.class));
                array = PluginUtil.isCollectionOrArray(type);
                Map<String, Class<?>> genericParamMap = buildParamsByGeneric(parameterizedType);
                if (requestBody != null || mode == ModeEnum.DUBBO.getValue()) {
                    List<DocParamReq> docParamReqList;
                    if (array) {
                        // 获取数元素类型
                        arrayElementDataType = this.getArrayElementType(apiParamWrapper, type, parameterizedType);
                        // 如果元素类型是对象
                        if (Objects.equals(DataType.OBJECT.getValue(), arrayElementDataType)) {
                            docParamReqList = buildReqClassParams(genericParamMap, type);
                        } else {
                            // 否则元素类型是基本类型
                            DocParamReq docParamReq = new DocParamReq();
                            docParamReq.setType(arrayElementDataType);
                            docParamReq.setDescription(apiParamWrapper.getDescription());
                            docParamReq.setExample(apiParamWrapper.getExample());
                            docParamReqList = Collections.singletonList(docParamReq);
                        }
                    } else {
                        docParamReqList = buildReqClassParams(genericParamMap, type);
                    }
                    docParamReqs.addAll(docParamReqList);
                } else {
                    boolean isPojo = PluginUtil.isPojo(type);
                    // 遇到普通类则认为类中的属性都是body参数
                    if (isPojo) {
                        List<DocParamReq> docParamReqList = buildReqClassParams(type);
                        docParamReqs.addAll(docParamReqList);
                    } else {
                        DocParamReq docParamReq = buildDocParamReq(parameter);
                        docParamReqs.add(docParamReq);
                    }
                }
            } catch (Exception e) {
                System.out.println("Create doc parameters error, parameter：" + parameter.toString() + "，method:" + method + "， msg:" + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return new DocParamReqWrapper(Booleans.toValue(array), arrayElementDataType, docParamReqs);
    }

    /**
     * 是否body体参数
     *
     * @param parameter  参数
     * @param httpMethod 请求方法
     * @return true：是body参数
     */
    protected boolean isBodyParameter(java.lang.reflect.Parameter parameter, String httpMethod) {
        if (PluginUtil.isFileParameter(parameter)) {
            return true;
        }
        if (this.tornaConfig.getMode() == ModeEnum.DUBBO.getValue()) {
            return true;
        }
        Annotation[] annotations = parameter.getAnnotations();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> aClass = annotation.annotationType();
            if (aClass == RequestBody.class) {
                return true;
            }
            if (aClass == RequestParam.class || aClass == PathVariable.class || aClass == RequestHeader.class) {
                return false;
            }
        }
        String hasBodyMethods = tornaConfig.getHasBodyMethods().toLowerCase();
        return hasBodyMethods.contains(httpMethod.toLowerCase());
    }


    protected DocParamRespWrapper buildResponseParams(Method method) {
        Type genericParameterType = method.getGenericReturnType();
        Map<String, Class<?>> genericParamMap = buildParamsByGeneric(genericParameterType);
        Class<?> type = method.getReturnType();
        boolean isCollection = PluginUtil.isCollectionOrArray(type);
        boolean array = PluginUtil.isCollectionOrArray(type) || isCollection;
        // 数组元素
        String arrayElementDataType = DataType.OBJECT.getValue();
        List<DocParamResp> docParamRespList;
        ApiParamWrapper apiParamWrapper = new ApiParamWrapper(method.getAnnotation(Parameter.class));
        // 如果只返回数组
        if (array) {
            // 获取数元素类型
            arrayElementDataType = this.getArrayElementType(apiParamWrapper, type, genericParameterType);
            // 如果元素类型是对象
            if (Objects.equals(DataType.OBJECT.getValue(), arrayElementDataType)) {
                docParamRespList = buildRespClassParams(genericParamMap, type);
            } else {
                // 否则元素类型是基本类型
                DocParamResp docParamResp = new DocParamResp();
                docParamResp.setType(arrayElementDataType);
                docParamResp.setDescription(apiParamWrapper.getDescription());
                docParamResp.setExample(apiParamWrapper.getExample());
                docParamRespList = Collections.singletonList(docParamResp);
            }
        } else {
            if (ClassUtil.isPrimitive(type.getName())) {
                // 否则元素类型是基本类型
                DocParamResp docParamResp = new DocParamResp();
                docParamResp.setType(type.getSimpleName());
                docParamResp.setDescription(apiParamWrapper.getDescription());
                docParamResp.setExample(apiParamWrapper.getExample());
                docParamRespList = Collections.singletonList(docParamResp);
            } else {
                docParamRespList = buildRespClassParams(genericParamMap, type);
            }
        }
        return new DocParamRespWrapper(Booleans.toValue(array), arrayElementDataType, docParamRespList);
    }

    protected String getArrayElementType(ApiParamWrapper apiParamWrapper, Class<?> type, Type genericParameterType) {
        String dataType = apiParamWrapper.getType();
        if (StringUtils.hasText(dataType)) {
            return dataType;
        }
        if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            return PluginUtil.getDataType(componentType);
        }
        Class<?> realType = type;
        // 如果有泛型
        if (PluginUtil.isGenericType(genericParameterType)) {
            realType = (Class<?>) PluginUtil.getGenericType(genericParameterType);
        }
        return PluginUtil.getDataType(realType);
    }

    protected Map<String, Class<?>> buildParamsByGeneric(Type genericParameterType) {
        Map<String, Class<?>> genericParamMap = new HashMap<>(8);
        PluginUtil.appendGenericParamMap(genericParamMap, genericParameterType);
        return genericParamMap;
    }

    protected List<DocParamCode> buildErrorCodes(Operation apiOperation) {
        return filterExtension(apiOperation, "code")
                .stream()
                .map(extensionProperty -> {
                    DocParamCode docParamCode = new DocParamCode();
                    docParamCode.setCode(extensionProperty.name());
                    docParamCode.setMsg(extensionProperty.value());
                    return docParamCode;
                })
                .collect(Collectors.toList());
    }

    protected List<ExtensionProperty> filterExtension(Operation apiOperation, String name) {
        Extension[] extensions = apiOperation.extensions();
        if (extensions.length == 0) {
            return Collections.emptyList();
        }
        for (Extension extension : extensions) {
            if (name.equals(extension.name())) {
                ExtensionProperty[] properties = extension.properties();
                return Arrays.asList(properties);
            }
        }
        return Collections.emptyList();
    }

    protected List<DocParamReq> buildReqClassParams(Class<?> clazz) {
        return buildReqClassParams(null, clazz);
    }

    protected List<DocParamReq> buildReqClassParams(Map<String, Class<?>> genericParamMap, Class<?> clazz) {
        clazz = getClassFromArrayType(clazz);
        List<FieldDocInfo> fieldDocInfoList = new ApiDocBuilder(genericParamMap, tornaConfig).buildFieldDocInfo(clazz);
        return fieldDocInfoList.stream()
                .map(this::convertReqParam)
                .collect(Collectors.toList());
    }

    protected List<DocParamResp> buildRespClassParams(Map<String, Class<?>> genericParamMap, Class<?> clazz) {
        clazz = getClassFromArrayType(clazz);
        List<FieldDocInfo> fieldDocInfoList = new ApiDocBuilder(genericParamMap, tornaConfig).buildFieldDocInfo(clazz);
        return fieldDocInfoList.stream()
                .map(this::convertRespParam)
                .collect(Collectors.toList());
    }

    protected Class<?> getClassFromArrayType(Class<?> clazz) {
        if (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        return clazz;
    }

    private DocParamReq convertReqParam(FieldDocInfo fieldDocInfo) {
        DocParamReq docParamReq = new DocParamReq();
        BeanUtils.copyProperties(fieldDocInfo, docParamReq);
        List<FieldDocInfo> fieldDocInfoChildren = fieldDocInfo.getChildren();
        if (!CollectionUtils.isEmpty(fieldDocInfoChildren)) {
            List<DocParamReq> children = new ArrayList<>(fieldDocInfoChildren.size());
            for (FieldDocInfo child : fieldDocInfoChildren) {
                children.add(convertReqParam(child));
            }
            docParamReq.setChildren(children);
        }
        return docParamReq;
    }

    private DocParamResp convertRespParam(FieldDocInfo fieldDocInfo) {
        DocParamResp docParamResp = new DocParamResp();
        BeanUtils.copyProperties(fieldDocInfo, docParamResp);
        List<FieldDocInfo> fieldDocInfoChildren = fieldDocInfo.getChildren();
        if (!CollectionUtils.isEmpty(fieldDocInfoChildren)) {
            List<DocParamResp> children = new ArrayList<>(fieldDocInfoChildren.size());
            for (FieldDocInfo child : fieldDocInfoChildren) {
                children.add(convertRespParam(child));
            }
            docParamResp.setChildren(children);
        }
        return docParamResp;
    }

    protected String getDataType(Parameter apiImplicitParam) {
//        Class<?> dataTypeClass = apiImplicitParam.dataTypeClass();
//        if (dataTypeClass != Void.class) {
//            return dataTypeClass.getSimpleName();
//        } else {
//            return apiImplicitParam.schema().type();
//        }
        return apiImplicitParam.schema().type();
    }

    protected List<Parameter> buildApiImplicitParams(Method method, ParamFilter filter) {
        Parameters apiImplicitParams = method.getAnnotation(Parameters.class);
        if (apiImplicitParams == null) {
            return Collections.emptyList();
        }
        List<Parameter> apiImplicitParamList = new ArrayList<>(apiImplicitParams.value().length);
        for (Parameter apiImplicitParam : apiImplicitParams.value()) {
            if (filter.filter(apiImplicitParam)) {
                apiImplicitParamList.add(apiImplicitParam);
            }
        }
        return apiImplicitParamList;
    }

    private ControllerInfo buildControllerInfo(Class<?> controllerClass) throws HiddenException, IgnoreException {
        Tag api = AnnotationUtils.findAnnotation(controllerClass, Tag.class);
//        if (api != null && api.hidden()) {
//            throw new HiddenException("Hidden doc（@Tag.hidden=true）：" + api.value());
//        }
        if (existsApiIgnore) {
            Hidden apiIgnore = AnnotationUtils.findAnnotation(controllerClass, Hidden.class);
            if (apiIgnore != null) {
                throw new IgnoreException("Ignore doc（@Hidden）：" + controllerClass.getName());
            }
        }
        String name, description;
        int position = 0;
        if (api == null) {
            name = controllerClass.getSimpleName();
            description = "";
        } else {
            name = api.name();
            List<String> tags = getTags(api);
            if (StringUtils.isEmpty(name) && tags.size() > 0) {
                name = tags.get(0);
            }
            if (PluginUtil.isEnableFeature(tornaConfig, PushFeature.USE_API_TAGS) && tags.size() > 0) {
                name = tags.get(0);
            }
            description = api.description();
            if (StringUtils.isEmpty(description)) {
                description = name;
            }
            Order order = AnnotationUtils.findAnnotation(controllerClass, Order.class);
            position = order == null ? 0 : order.value();
        }
        ControllerInfo controllerInfo = new ControllerInfo();
        controllerInfo.setName(name);
        controllerInfo.setDescription(description);
        controllerInfo.setPosition(position);
        return controllerInfo;
    }

    protected List<String> getTags(Tag api) {
        if (api == null || api.name() == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(api.name());
    }

    public TornaConfig getTornaConfig() {
        return tornaConfig;
    }

    public boolean match(Method method) {
        List<String> scanApis = this.tornaConfig.getScanApis();
        if (CollectionUtils.isEmpty(scanApis)) {
            return method.getAnnotation(Operation.class) != null;
        }
        for (String scanApi : scanApis) {
            String methodName = method.toString();
            if (methodName.contains(scanApi)) {
                return true;
            }
        }
        return false;
    }

    private interface ParamFilter {
        boolean filter(Parameter param);
    }

    private static class DocParamRespWrapper extends DocParamWrapper<DocParamResp> {
        public DocParamRespWrapper(Byte isArray, String arrayType, List<DocParamResp> data) {
            super(isArray, arrayType, data);
        }
    }

    private static class DocParamReqWrapper extends DocParamWrapper<DocParamReq> {
        public DocParamReqWrapper(Byte isArray, String arrayType, List<DocParamReq> data) {
            super(isArray, arrayType, data);
        }
    }

    @Data
    @AllArgsConstructor
    private static class DocParamWrapper<T> {
        /**
         * 是否数组
         */
        private Byte isArray;

        /**
         * 数组元素类型
         */
        private String arrayType;

        private List<T> data;
    }

}
