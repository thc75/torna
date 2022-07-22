package cn.torna.api.open;

import cn.torna.api.open.param.DebugEnvParam;
import cn.torna.api.open.param.DocParamPushParam;
import cn.torna.api.open.param.DocPushItemParam;
import cn.torna.api.open.param.DocPushParam;
import cn.torna.api.open.param.HeaderParamPushParam;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Configs;
import cn.torna.common.enums.DocTypeEnum;
import cn.torna.common.exception.BizException;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author thc
 */
@Service
@Slf4j
public class SwaggerParserApi {

    private static final ThreadLocal<AtomicInteger> threadLocal = new ThreadLocal<>();

    @Autowired
    private DocApi docApi;

    /**
     * 解析swagger文档
     * @param content swagger文档内容，json或yaml格式
     */
    public void parse(String content) {
        threadLocal.set(new AtomicInteger());
        try {
            SwaggerParseResult result = new OpenAPIParser().readContents(content, null, null);
            OpenAPI openAPI = result.getOpenAPI();
            if (result.getMessages() != null) {
                for (String message : result.getMessages()) {
                    log.error("解析Swagger文档, msg={}", message);
                }
            }
            DocPushParam docPushParam = this.convertDocInfo(openAPI);
            docApi.pushDoc(docPushParam);
        } catch (Exception e) {
            log.error("解析Swagger文档失败", e);
            throw new BizException("解析Swagger文档失败");
        } finally {
            threadLocal.remove();
        }
    }

    private DocPushParam convertDocInfo(OpenAPI openAPI) {
        if (openAPI == null) {
            return null;
        }
        DocPushParam docPushParam = new DocPushParam();
        Info info = openAPI.getInfo();
        String defaultAuthor = Configs.getValue("swagger.default-author", "swagger");
        String author = Optional.ofNullable(info).map(Info::getContact).map(Contact::getName).orElse(defaultAuthor);
        List<DebugEnvParam> debugEnvParams = buildDebugEnvParams(openAPI);
        List<DocPushItemParam> apis = buildDocPushItemParams(openAPI);
        docPushParam.setAuthor(author);
        docPushParam.setDebugEnvs(debugEnvParams);
        docPushParam.setApis(apis);
        return docPushParam;
    }

    private List<DebugEnvParam> buildDebugEnvParams(OpenAPI openAPI) {
        List<Server> servers = openAPI.getServers();
        if (CollectionUtils.isEmpty(servers)) {
            return Collections.emptyList();
        }
        AtomicInteger i = new AtomicInteger();
        return servers.stream()
                .map(server -> {
                    DebugEnvParam debugEnvParam = new DebugEnvParam();
                    debugEnvParam.setName(Optional.ofNullable(server.getDescription()).orElse("test" + i.incrementAndGet()));
                    debugEnvParam.setUrl(server.getUrl());
                    return debugEnvParam;
                })
                .collect(Collectors.toList());
    }

    private List<DocPushItemParam> buildDocPushItemParams(OpenAPI openAPI) {

        // 生成目录
        List<DocPushItemParam> folders = buildFolders(openAPI);
        // 生成文档
        List<DocPushItemParam> items = buildItems(openAPI);
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

    private List<DocPushItemParam> buildFolders(OpenAPI openAPI) {
        List<Tag> tags = openAPI.getTags();
        if (CollectionUtils.isEmpty(tags)) {
            return Collections.emptyList();
        }
        return tags.stream()
                .map(tag -> {
                    DocPushItemParam docPushItemParam = new DocPushItemParam();
                    docPushItemParam.setName(tag.getName());
                    docPushItemParam.setDescription(tag.getDescription());
                    docPushItemParam.setIsFolder(Booleans.TRUE);
                    docPushItemParam.setItems(new ArrayList<>());
                    docPushItemParam.setOrderIndex(threadLocal.get().getAndIncrement());
                    return docPushItemParam;
                })
                .collect(Collectors.toList());
    }

    private List<DocPushItemParam> buildItems(OpenAPI openAPI) {
        Paths paths = openAPI.getPaths();
        if (CollectionUtils.isEmpty(paths)) {
            return Collections.emptyList();
        }
        return paths.entrySet()
                .stream()
                .flatMap(entry-> this.buildItems(entry, openAPI).stream())
                .collect(Collectors.toList());
    }

    private List<DocPushItemParam> buildItems(Map.Entry<String, PathItem> entry, OpenAPI openAPI) {
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
                    .deprecated(operation.getDeprecated() ? "deprecated" : null)
                    .orderIndex(threadLocal.get().getAndIncrement())
                    .headerParams(buildHeaderParamPushParams(operation))
                    .pathParams(buildDocParamPushParams(operation, parameter -> Objects.equals("path", parameter.getIn())))
                    .queryParams(buildDocParamPushParams(operation, parameter -> Objects.equals("query", parameter.getIn())))
                    .requestParams(buildRequestParams(operation, openAPI))
                    .tag(CollectionUtils.isEmpty(operation.getTags()) ? "" : operation.getTags().get(0))
                    .build();

            items.add(docPushItemParam);
        }
        return items;
    }

    private List<HeaderParamPushParam> buildHeaderParamPushParams(Operation operation) {
        List<Parameter> parameters = operation.getParameters();
        if (CollectionUtils.isEmpty(parameters)) {
            return null;
        }
        return parameters.stream()
                .filter(parameter -> Objects.equals("header", parameter.getIn()))
                .map(parameter -> {
                    return HeaderParamPushParam.builder()
                            .name(parameter.getName())
                            .required(Booleans.toValue(parameter.getRequired()))
                            .description(parameter.getDescription())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<DocParamPushParam> buildRequestParams(Operation operation, OpenAPI openAPI) {
        RequestBody requestBody = operation.getRequestBody();
        if (requestBody != null) {
            Collection<MediaType> values = requestBody.getContent().values();
            MediaType mediaType = values.iterator().next();
            Schema<?> schema = mediaType.getSchema();
            String $ref = schema.get$ref();
            return buildObjectParam($ref, openAPI);
        } else {
            return buildDocParamPushParams(operation, parameter -> "formData".equals(parameter.getIn()));
        }
    }


    private List<DocParamPushParam> buildObjectParam(String $ref, OpenAPI openAPI) {
        JsonSchema jsonSchema = getJsonSchema($ref, openAPI);
        Map<String, Object> properties = jsonSchema.getProperties();
        return properties.entrySet()
                .stream()
                .map(entry -> {
                    String name = entry.getKey();
                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                    DocParamPushParam param = DocParamPushParam.builder()
                            .name(name)
                            .type(String.valueOf(value.get("type")))
                            .required(Booleans.toValue(Objects.equals("true", String.valueOf(value.getOrDefault("required", "false")))))
                            .description(String.valueOf(value.getOrDefault("description", "")))
                            .build();
                    String type = String.valueOf(value.get("type"));
                    List<DocParamPushParam> children = null;
                    // 如果有子对象
                    if (value.containsKey("$ref")) {
                        type = "Object";
                        children = buildObjectParam(String.valueOf(value.get("$ref")), openAPI);
                    }
                    param.setChildren(children);
                    param.setType(type);
                    return param;
                })
                .collect(Collectors.toList());
    }

    private List<DocParamPushParam> buildDocParamPushParams(Operation operation, Predicate<Parameter> predicate) {
        List<Parameter> parameters = operation.getParameters();
        if (CollectionUtils.isEmpty(parameters)) {
            return null;
        }
        return parameters.stream()
                .filter(predicate)
                .map(parameter -> {
                    return DocParamPushParam.builder()
                            .name(parameter.getName())
                            .type(getType(parameter))
                            .required(Booleans.toValue(parameter.getRequired()))
                            .description(parameter.getDescription())
                            .maxLength(getMaxLength(parameter))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String getType(Parameter parameter) {
        Schema<?> schema = Optional.ofNullable(parameter)
                .map(Parameter::getSchema).orElse(null);
        return getType(schema);
    }

    private String getType(Schema<?> schema) {
        return Optional.ofNullable(schema)
                .map(Schema::getType)
                .orElse("String");
    }



    private String getMaxLength(Parameter parameter) {
        Integer maxLength = Optional.ofNullable(parameter)
                .map(Parameter::getSchema)
                .map(Schema::getMaxLength)
                .orElse(null);
        return maxLength == null ? "-" : maxLength.toString();
    }

    /**
     *
     * @param $ref #/components/schemas/Order
     * @param openAPI
     * @return
     */
    private JsonSchema getJsonSchema(String $ref, OpenAPI openAPI) {
        String refName = getRefName($ref);
        Schema<?> schema = openAPI.getComponents().getSchemas().get(refName);
        Map<String, Object> objectProperties = schema.getJsonSchema();
        String type = String.valueOf(objectProperties.getOrDefault("type", "Object"));
        Map<String, Object> properties = (Map<String, Object>) objectProperties.get("properties");
        return new JsonSchema(type, properties);
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

    @AllArgsConstructor
    @Data
    static class JsonSchema {
        private String type;
        private Map<String, Object> properties;
    }

}
