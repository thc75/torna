package cn.torna.api.open;

import cn.torna.api.bean.ApiUser;
import cn.torna.api.bean.RequestContext;
import cn.torna.api.open.param.DebugEnvParam;
import cn.torna.api.open.param.DocParamPushParam;
import cn.torna.api.open.param.DocPushItemParam;
import cn.torna.api.open.param.DocPushParam;
import cn.torna.api.open.param.EnumInfoCreateParam;
import cn.torna.api.open.param.EnumItemCreateParam;
import cn.torna.api.open.param.HeaderParamPushParam;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.HttpHelper;
import cn.torna.common.bean.User;
import cn.torna.common.enums.DocTypeEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.MockUtil;
import cn.torna.dao.entity.Module;
import cn.torna.manager.doc.DataType;
import cn.torna.service.ModuleService;
import cn.torna.service.ModuleSwaggerConfigService;
import cn.torna.service.dto.ImportSwaggerV2DTO;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author thc
 */
@Service
@Slf4j
public class SwaggerApi {

    private static final ThreadLocal<AtomicInteger> threadLocal = new ThreadLocal<>();
    private static final String STATUS_200 = "200";
    private static final String TYPE_ENUM = "enum";
    private static final String TYPE_OBJECT = "object";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_ARRAY = "array";

    @Autowired
    private DocApi docApi;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ModuleSwaggerConfigService moduleSwaggerConfigService;

    /**
     * 导入swagger文档
     * @param importSwaggerV2DTO importSwaggerV2DTO
     */
    public Module importSwagger(ImportSwaggerV2DTO importSwaggerV2DTO) {
        return importSwagger(importSwaggerV2DTO, null);
    }

    public Module importSwagger(ImportSwaggerV2DTO importSwaggerV2DTO, Module module) {
        String content = buildSwaggerDocContent(importSwaggerV2DTO);
        User user = importSwaggerV2DTO.getUser();
        String nickname = user.getNickname();
        ApiUser apiUser = new ApiUser();
        apiUser.setId(user.getUserId());
        apiUser.setNickname(user.getNickname());
        OpenAPI openAPI = getOpenAPI(content);
        DocPushParam docPushParam = buildDocPushParam(nickname, openAPI);
        if (module == null) {
            module = createModule(importSwaggerV2DTO, getTitle(openAPI));
        }
        RequestContext.getCurrentContext().setModule(module);
        RequestContext.getCurrentContext().setIp(importSwaggerV2DTO.getIp());
        RequestContext.getCurrentContext().setApiUser(apiUser);
        // 保存配置
        moduleSwaggerConfigService.create(importSwaggerV2DTO, content, module);
        // 推送文档
        docApi.pushDoc(docPushParam);
        return module;
    }

    public static DocPushParam buildDocPushParam(String pushUser, OpenAPI openAPI) {
        try {
            threadLocal.set(new AtomicInteger());
            return convertDocInfo(pushUser, openAPI);
        } catch (Exception e) {
            log.error("解析Swagger文档失败", e);
            throw new BizException("解析Swagger文档失败");
        } finally {
            threadLocal.remove();
        }
    }


    public static OpenAPI getOpenAPI(String content) {
        SwaggerParseResult result = new OpenAPIParser().readContents(content, null, null);
        OpenAPI openAPI = result.getOpenAPI();
        if (result.getMessages() != null) {
            for (String message : result.getMessages()) {
                log.error("解析Swagger文档, msg={}", message);
            }
        }
        checkOpenAPI(openAPI);
        return openAPI;
    }

    private static void checkOpenAPI(OpenAPI openAPI) {
        if (openAPI == null) {
            throw new BizException("swagger文档格式错误");
        }
        Info info = openAPI.getInfo();
        String title = info.getTitle();
        if (StringUtils.isEmpty(title)) {
            throw new BizException("swagger title 属性不能为空");
        }
    }

    private static String getTitle(OpenAPI openAPI) {
        Info info = openAPI.getInfo();
        return info.getTitle();
    }

    private static String buildSwaggerDocContent(ImportSwaggerV2DTO importSwaggerV2DTO) {
        String url = importSwaggerV2DTO.getUrl();
        if (StringUtils.hasText(url)) {
            try {
                HttpHelper.ResponseResult responseResult = HttpHelper
                        .create()
                        .basicAuth(importSwaggerV2DTO.getAuthUsername(), importSwaggerV2DTO.getAuthPassword())
                        .url(url)
                        .method("get")
                        .execute();
                String body = responseResult.asString();
                int status = responseResult.getStatus();
                if (status == HttpStatus.UNAUTHORIZED.value()) {
                    throw new BizException("认证失败");
                }
                if (status != HttpStatus.OK.value()) {
                    log.error("导入swagger错误，url:{}, \n{}", url, body);
                    throw new BizException("导入错误，请查看日志");
                }
                return body;
            } catch (IOException e) {
                log.error("导入swagger异常, url:{}", url, e);
                throw new BizException("导入异常, msg:" + e.getMessage());
            }
        } else {
            String content = importSwaggerV2DTO.getContent();
            if (StringUtils.isEmpty(content)) {
                throw new BizException("swagger文档内容不能为空");
            }
            return content;
        }
    }

    private Module createModule(ImportSwaggerV2DTO importSwaggerV2DTO, String title) {
        // 创建模块
        return moduleService.createSwaggerModule(importSwaggerV2DTO, title);
    }

    /**
     * OpenAPI转换成Torna推送文档参数
     * @param pushUser 推送人
     * @param openAPI openAPI对象
     * @return 返回Torna需要的参数
     */
    private static DocPushParam convertDocInfo(String pushUser, OpenAPI openAPI) {
        DocPushParam docPushParam = new DocPushParam();
        List<DebugEnvParam> debugEnvParams = buildDebugEnvParams(openAPI);
        List<DocPushItemParam> apis = buildDocPushItemParams(openAPI);
        docPushParam.setAuthor(pushUser);
        docPushParam.setDebugEnvs(debugEnvParams);
        docPushParam.setApis(apis);
        return docPushParam;
    }

    /**
     * 构建调试环境
     * @param openAPI openAPI
     * @return 返回调试环境，没有返回空List
     */
    private static List<DebugEnvParam> buildDebugEnvParams(OpenAPI openAPI) {
        List<Server> servers = openAPI.getServers();
        if (CollectionUtils.isEmpty(servers)) {
            return Collections.emptyList();
        }
        AtomicInteger i = new AtomicInteger();
        return servers.stream()
                .map(server -> {
                    DebugEnvParam debugEnvParam = new DebugEnvParam();
                    debugEnvParam.setName(Optional.ofNullable(server.getDescription()).orElse("test" + i.incrementAndGet()));
                    String url = server.getUrl();
                    if (url.startsWith("//")) {
                        url = "http:" + url;
                    }
                    debugEnvParam.setUrl(url);
                    return debugEnvParam;
                })
                .collect(Collectors.toList());
    }

    private static List<DocPushItemParam> buildDocPushItemParams(OpenAPI openAPI) {
        // 生成文档
        List<DocPushItemParam> items = buildItems(openAPI);
        // 生成目录
        List<DocPushItemParam> folders = buildFolders(items);
        // 如果没有目录，直接返回文档
        if (folders.isEmpty()) {
            return items;
        }
        Map<String, DocPushItemParam> folderMap = folders.stream()
                .collect(Collectors.toMap(DocPushItemParam::getName, Function.identity()));
        for (DocPushItemParam item : items) {
            String tag = item.getTag();
            DocPushItemParam folder = folderMap.get(tag);
            if (folder != null) {
                folder.getItems().add(item);
            } else {
                folders.add(item);
            }
        }
        return folders;
    }

    private static List<DocPushItemParam> buildFolders(List<DocPushItemParam> items) {
        if (CollectionUtils.isEmpty(items)) {
            return new ArrayList<>();
        }
        Set<String> tags = items.stream()
                .map(DocPushItemParam::getTag)
                .filter(StringUtils::hasText)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (CollectionUtils.isEmpty(tags)) {
            return new ArrayList<>();
        }
        return tags.stream()
                .map(tag -> {
                    return DocPushItemParam.builder()
                            .name(tag)
                            .description(tag)
                            .isFolder(Booleans.TRUE)
                            .items(new ArrayList<>())
                            .orderIndex(threadLocal.get().getAndIncrement())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static List<DocPushItemParam> buildItems(OpenAPI openAPI) {
        Paths paths = openAPI.getPaths();
        if (CollectionUtils.isEmpty(paths)) {
            return Collections.emptyList();
        }
        return paths.entrySet()
                .stream()
                .flatMap(entry-> buildItems(entry, openAPI).stream())
                .collect(Collectors.toList());
    }

    private static List<DocPushItemParam> buildItems(Map.Entry<String, PathItem> entry, OpenAPI openAPI) {
        List<DocPushItemParam> items = new ArrayList<>();
        String path = entry.getKey();
        PathItem pathItem = entry.getValue();
        Map<PathItem.HttpMethod, Operation> operationMap = pathItem.readOperationsMap();
        for (Map.Entry<PathItem.HttpMethod, Operation> operationEntry : operationMap.entrySet()) {
            String httpMethod = operationEntry.getKey().name();
            Operation operation = operationEntry.getValue();
            DocPushItemParam docPushItemParam = DocPushItemParam.builder()
                    .name(operation.getSummary())
                    .description(operation.getDescription())
                    .httpMethod(httpMethod)
                    .type(DocTypeEnum.HTTP.getType())
                    .url(path)
                    .isFolder(Booleans.FALSE)
                    .deprecated(Optional.ofNullable(operation.getDeprecated()).orElse(false) ? "" : null)
                    .orderIndex(threadLocal.get().getAndIncrement())
                    .headerParams(buildHeaderParamPushParams(operation))
                    .pathParams(buildDocParamPushParams(openAPI, operation, parameter -> Objects.equals("path", parameter.getIn())))
                    .queryParams(buildDocParamPushParams(openAPI, operation, parameter -> Objects.equals("query", parameter.getIn())))
                    .tag(CollectionUtils.isEmpty(operation.getTags()) ? "" : operation.getTags().get(0))
                    .build();

            RequestParamsWrapper requestParamsWrapper = buildRequestParamsWrapper(operation, openAPI);
            docPushItemParam.setRequestParams(requestParamsWrapper.getDocParamPushParams());
            docPushItemParam.setIsRequestArray(Booleans.toValue(requestParamsWrapper.isRequestArray()));
            docPushItemParam.setRequestArrayType(requestParamsWrapper.getRequestArrayType());
            docPushItemParam.setContentType(requestParamsWrapper.getContentType());

            ResponseParamsWrapper responseParamsWrapper = buildResponseParamsWrapper(operation, openAPI);
            docPushItemParam.setResponseParams(responseParamsWrapper.getDocParamPushParams());
            docPushItemParam.setIsResponseArray(Booleans.toValue(responseParamsWrapper.isResponseArray()));

            items.add(docPushItemParam);
        }
        return items;
    }

    private static List<HeaderParamPushParam> buildHeaderParamPushParams(Operation operation) {
        List<Parameter> parameters = operation.getParameters();
        if (CollectionUtils.isEmpty(parameters)) {
            return null;
        }
        return parameters.stream()
                .filter(parameter -> Objects.equals("header", parameter.getIn()))
                .map(parameter -> {
                    return HeaderParamPushParam.builder()
                            .name(parameter.getName())
                            .required(Booleans.toValue(isRequired(parameter)))
                            .description(parameter.getDescription())
                            .example(toString(parameter.getExample()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static RequestParamsWrapper buildRequestParamsWrapper(Operation operation, OpenAPI openAPI) {
        RequestBody requestBody = operation.getRequestBody();
        boolean isRequestArray = false;
        String requestArrayType = DataType.OBJECT;
        List<DocParamPushParam> docParamPushParams = Collections.emptyList();
        String contentType = "";
        // 如果是json请求
        if (requestBody != null) {
            Content content = requestBody.getContent();
            for (Map.Entry<String, MediaType> entry : content.entrySet()) {
                String key = entry.getKey();
                if (key.contains("json") || "*/*".equals(key)) {
                    contentType = "application/json";
                    MediaType mediaType = entry.getValue();
                    Schema<?> schema = mediaType.getSchema();
                    String $ref = schema.get$ref();
                    String type = schema.getType();
                    // 如果是数组参数
                    if ("array".equals(type)) {
                        isRequestArray = true;
                        Schema<?> items = schema.getItems();
                        $ref = items.get$ref();
                        if ($ref != null) {
                            docParamPushParams = buildObjectParam($ref, openAPI, new BuildObjectParamContext());
                        } else {
                            String itemType = items.getType();
                            if (StringUtils.hasText(itemType)) {
                                String format = items.getFormat();
                                if ("int64".equals(format)) {
                                    itemType = "long";
                                }
                                requestArrayType = itemType;
                                docParamPushParams = buildSingleArray(itemType, items);
                            }
                        }
                    } else if ($ref != null) {
                        docParamPushParams = buildObjectParam($ref, openAPI, new BuildObjectParamContext());
                    }
                } else if (key.contains("form")) {
                    contentType = key.contains("multipart") ? "multipart/form-data" : "application/x-www-form-urlencoded";
                    MediaType mediaType = entry.getValue();
                    Schema<?> schema = mediaType.getSchema();
                    Map<String, Schema> properties = schema.getProperties();
                    docParamPushParams = buildDocParamPushParams(operation, properties);
                }
            }
        } else {
            // 表单结构
            docParamPushParams = buildDocParamPushParams(openAPI, operation, parameter -> "formData".equals(parameter.getIn()));
        }
        return new RequestParamsWrapper(docParamPushParams, isRequestArray, requestArrayType, contentType);
    }

    private static List<DocParamPushParam> buildSingleArray(String type, Schema<?> items) {
        String example = toString(items.getExample());
        if (StringUtils.isEmpty(example)) {
            example = MockUtil.buildMockArrayValue(type);
        }
        DocParamPushParam param = DocParamPushParam.builder()
                .type(type)
                .required(Booleans.TRUE)
                .description(items.getDescription())
                .example(example)
                .build();
        return Collections.singletonList(param);
    }

    private static ResponseParamsWrapper buildResponseParamsWrapper(Operation operation, OpenAPI openAPI) {
        List<DocParamPushParam> docParamPushParams = Collections.emptyList();
        boolean isResponseArray = false;
        ApiResponses responses = operation.getResponses();
        if (responses != null && responses.containsKey(STATUS_200)) {
            ApiResponse apiResponse = responses.get(STATUS_200);
            Content content = apiResponse.getContent();
            if (content != null) {
                for (Map.Entry<String, MediaType> entry : content.entrySet()) {
                    String key = entry.getKey();
                    if (key.contains("json") || "*/*".equals(key)) {
                        MediaType mediaType = entry.getValue();
                        Schema<?> schema = mediaType.getSchema();
                        String $ref = schema.get$ref();
                        String type = schema.getType();
                        // 如果是数组参数
                        if ("array".equals(type)) {
                            isResponseArray = true;
                            Schema<?> items = schema.getItems();
                            $ref = items.get$ref();
                            docParamPushParams = buildObjectParam($ref, openAPI, new BuildObjectParamContext());
                        } else if ($ref != null && !$ref.endsWith("@")/*排除@*/) {
                            docParamPushParams = buildObjectParam($ref, openAPI, new BuildObjectParamContext());
                        }
                    }
                }
            }
        }
        return new ResponseParamsWrapper(docParamPushParams, isResponseArray);
    }

    private static class BuildObjectParamContext {
        private Set<String> $refSets;

        public BuildObjectParamContext() {
            $refSets = new HashSet<>();
        }

        public boolean add$ref(String $ref) {
            return $refSets.add($ref);
        }
    }

    private static List<DocParamPushParam> buildObjectParam(String $ref, OpenAPI openAPI, BuildObjectParamContext context) {
        // 防止树形对象死循环
        if (!context.add$ref($ref)) {
            return null;
        }
        JsonSchema jsonSchema = getJsonSchema($ref, openAPI);
        return buildObjectParam(jsonSchema, openAPI, context);
    }

    private static List<DocParamPushParam> buildObjectParam(JsonSchema jsonSchema, OpenAPI openAPI, BuildObjectParamContext context) {
        final Map<String, Schema> properties = jsonSchema.getSchema().getProperties();
        return Optional.ofNullable(properties)
                .orElse(Collections.emptyMap()).entrySet()
                .stream()
                .map(entry -> {
                    String name = entry.getKey();
                    Schema value = entry.getValue();
                    DocParamPushParam param = DocParamPushParam.builder()
                            .name(name)
                            .required(Booleans.toValue(jsonSchema.getRequired(name) || Objects.equals("true", String.valueOf(value.getRequired()))))
                            .description(value.getDescription())
                            .example(toString(value.getExample()))
                            .maxLength(getMaxLength(jsonSchema.getSchema(), value))
                            .build();
                    String type = value.getType();
                    List<DocParamPushParam> children = null;
                    // 如果有子对象的ref
                    if (value.get$ref() != null) {
                        type = TYPE_OBJECT;
                        final String child$ref = value.get$ref();
                        children = buildObjectParam(child$ref, openAPI, context);
                    }
                    // 如果直接书写了子对象的内容
                    if(value.getProperties()!=null){
                        type = TYPE_OBJECT;
                        children = buildObjectParam(getJsonSchema(value), openAPI, context);
                    }
                    // 如果是枚举字段
                    if (value.getEnum() != null) {
                        type = TYPE_ENUM;
                        List<?> list = value.getEnum();
                        setEnumDescription(list, param);
                    }
                    // list对象，List<XX>
                    if (TYPE_ARRAY.equals(type) && value.getItems() != null) {
                        Schema items = value.getItems();
                        String itemRef = items.get$ref();
                        if (itemRef != null) {
                            final String child$ref = itemRef;
                            children = buildObjectParam(child$ref, openAPI, context);
                            type = "array[object]";
                        }
                        String itemType = items.getType();
                        if (itemType != null) {
                            type = "array[" + itemType + "]";
                            if(TYPE_OBJECT.equals(itemType)){
                                children = buildObjectParam(getJsonSchema(items), openAPI, context);
                            }
                        }
                    }
                    param.setChildren(children);
                    param.setType(type);
                    return param;
                })
                .collect(Collectors.toList());
    }

    private static String buildEnumString(String description, List<?> list, String label) {
        if (!CollectionUtils.isEmpty(list)) {
            if (description == null) {
                description = "";
            }
            if (StringUtils.hasText(description)) {
                description += "<br>";
            }
            description += label +
                    list.stream().map(String::valueOf).collect(Collectors.joining(","));
        }
        return description;
    }

    private static EnumInfoCreateParam buildEnumInfoCreateParam(DocParamPushParam param, List<?> values, String type) {
        EnumInfoCreateParam enumInfoCreateParam = new EnumInfoCreateParam();
        enumInfoCreateParam.setName(param.getDescription());
        List<EnumItemCreateParam> items = values.stream()
                .map(val -> {
                    String value = String.valueOf(val);
                    EnumItemCreateParam enumItemCreateParam = new EnumItemCreateParam();
                    enumItemCreateParam.setName(value);
                    enumItemCreateParam.setValue(value);
                    enumItemCreateParam.setType(type);
                    enumItemCreateParam.setDescription("");
                    return enumItemCreateParam;
                })
                .collect(Collectors.toList());
        enumInfoCreateParam.setItems(items);
        return enumInfoCreateParam;
    }

    private static List<DocParamPushParam> buildDocParamPushParams(OpenAPI openAPI, Operation operation, Predicate<Parameter> predicate) {
        List<Parameter> parameters = operation.getParameters();
        if (CollectionUtils.isEmpty(parameters)) {
            return null;
        }
        return parameters.stream()
                .filter(predicate)
                .flatMap(parameter -> {
                    DocParamPushParam param = DocParamPushParam.builder()
                            .name(parameter.getName())
                            .type(getType(parameter))
                            .required(Booleans.toValue(isRequired(parameter)))
                            .description(parameter.getDescription())
                            .maxLength(getMaxLength(parameter))
                            .example(toString(parameter.getExample()))
                            .build();
                    Schema<?> schema = parameter.getSchema();
                    if (schema != null) {
                        String $ref = schema.get$ref();
                        String type = schema.getType();
                        // 如果是数组参数
                        if ("array".equals(type)) {
                            Schema<?> items = schema.getItems();
                            String itemsType = items.getType();
                            param.setType("array[" + itemsType + "]");
                            List<?> list = items.getEnum();
                            setEnumDescription(list, param);
                        } else if ($ref != null) {
                            return buildObjectParam($ref, openAPI, new BuildObjectParamContext()).stream();
                        }
                    }
                    return Stream.of(param);
                })
                .collect(Collectors.toList());
    }

    private static void setEnumDescription(List<?> list, DocParamPushParam param) {
        if (!CollectionUtils.isEmpty(list)) {
            String description = buildEnumString(param.getDescription(), list, "枚举值: ");
            param.setDescription(description);
            if (StringUtils.isEmpty(param.getExample())) {
                param.setExample(toString(list.get(0)));
            }
        }
    }

    private static List<DocParamPushParam> buildDocParamPushParams(Operation operation, Map<String, Schema> properties) {
        if (CollectionUtils.isEmpty(properties)) {
            return null;
        }
        return properties.entrySet().stream()
                .map(stringSchemaEntry -> {
                    Schema schema = stringSchemaEntry.getValue();
                    String type = schema.getType();
                    String format = schema.getFormat();
                    if ("binary".equals(format)) {
                        type = "file";
                    }
                    return DocParamPushParam.builder()
                            .name(schema.getName())
                            .type(type)
                            .description(schema.getDescription())
                            .example(toString(schema.getExample()))
                            .required(Booleans.toValue(isRequired(schema, schema.getName())))
                            .maxLength(getMaxLength(schema))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static String getType(Parameter parameter) {
        Schema<?> schema = Optional.ofNullable(parameter)
                .map(Parameter::getSchema).orElse(null);
        return getType(schema);
    }

    private static String getType(Schema<?> schema) {
        return Optional.ofNullable(schema)
                .map(Schema::getType)
                .orElse(TYPE_STRING);
    }



    private static String getMaxLength(Parameter parameter) {
        return Optional.ofNullable(parameter)
                .map(Parameter::getSchema)
                .map(SwaggerApi::getMaxLength)
                .orElse("-");
    }

    private static String getMaxLength(Schema<?> schema) {
        return Optional.ofNullable(schema)
                .map(Schema::getMaxLength)
                .map(String::valueOf)
                .orElse("-");
    }

    private static String getMaxLength(Schema<?> schema, Schema<?> value) {
        return Optional.ofNullable(schema)
                .map(Schema::getMaxLength)
                .map(String::valueOf)
                .orElseGet(() -> getMaxLength(value));
    }

    /**
     *
     * @param $ref #/components/schemas/Order
     * @param openAPI
     * @return
     */
    private static JsonSchema getJsonSchema(String $ref, OpenAPI openAPI) {
        String refName = getRefName($ref);
        Schema<?> schema = openAPI.getComponents().getSchemas().get(refName);
        return getJsonSchema(schema);
    }

    private static JsonSchema getJsonSchema(Schema<?> schema) {
        String type= null;
        try {
            type = schema.getType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (type == null) {
            Map<String, Object> objectProperties = Optional.ofNullable(schema.getJsonSchema()).orElse(Collections.emptyMap());
            type =  String.valueOf(objectProperties.getOrDefault("type", TYPE_OBJECT));
        }
        Map<String, Object> properties = getProperties(schema);
        return new JsonSchema(type, properties, schema);
    }

    private static Map<String, Object> getProperties(Schema<?> schema) {
        Map<String, Schema> properties = schema.getProperties();
        Map<String, Object> props = new LinkedHashMap<>(8);
        if (properties != null) {
            for (Map.Entry<String, Schema> entry : properties.entrySet()) {
                Schema value = entry.getValue();
                Map<String, Object> val = new LinkedHashMap<>(8);
                putVal(val,"required", value.getRequired());
                putVal(val, "format", value.getFormat());
                putVal(val, "type", value.getType());
                putVal(val, "description", value.getDescription());
                putVal(val, "example", value.getExample());
                putVal(val, "maxLength", value.getMaxLength());
                putVal(val, "$ref", value.get$ref());
                props.put(entry.getKey(), val);
            }
            return props;
        } else {
            Map<String, Object> objectProperties = Optional.ofNullable(schema.getJsonSchema()).orElse(Collections.emptyMap());
            return (Map<String, Object>) objectProperties.get("properties");
        }
    }

    private static void putVal(Map<String, Object> val, String key, Object value) {
        if (value != null) {
            val.put(key, value);
        }
    }

    private static String getRefName(String ref) {
        if (ref == null) {
            return null;
        }
        int index = ref.lastIndexOf("/");
        if (index > -1) {
            ref = ref.substring(index + 1);
        }
        return ref;
    }

    private static String toString(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private static boolean isRequired(Schema<?> schema, String name) {
        if (schema == null) {
            return false;
        }
        List<String> required = schema.getRequired();
        if (CollectionUtils.isEmpty(required)) {
            return false;
        }
        return required.contains(name);
    }

    private static boolean isRequired(Parameter parameter) {
        Schema<?> schema = parameter.getSchema();
        return isRequired(schema, parameter.getName()) || Objects.equals(parameter.getRequired(), true);
    }


    @AllArgsConstructor
    @Data
    static class JsonSchema {
        private String type;
        private Map<String, Object> properties;
        private Schema<?> schema;

        public boolean getRequired(String name) {
            return isRequired(this.schema, name);
        }
    }


    @AllArgsConstructor
    @Data
    static class RequestParamsWrapper {
        private List<DocParamPushParam> docParamPushParams;
        private boolean isRequestArray;
        private String requestArrayType;
        private String contentType;
    }

    @AllArgsConstructor
    @Data
    static class ResponseParamsWrapper {
        private List<DocParamPushParam> docParamPushParams;
        private boolean isResponseArray;
    }
}
