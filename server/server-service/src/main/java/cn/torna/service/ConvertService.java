package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.util.TreeUtil;
import cn.torna.dao.entity.Module;
import cn.torna.manager.doc.postman.Body;
import cn.torna.manager.doc.postman.Header;
import cn.torna.manager.doc.postman.Info;
import cn.torna.manager.doc.postman.Item;
import cn.torna.manager.doc.postman.Param;
import cn.torna.manager.doc.postman.Postman;
import cn.torna.manager.doc.postman.Request;
import cn.torna.manager.doc.postman.Url;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.service.metersphere.v3.constants.ParameterIn;
import cn.torna.service.metersphere.v3.model.ApiDefinition;
import cn.torna.service.metersphere.v3.model.DataTypes;
import cn.torna.service.metersphere.v3.model.HttpMethod;
import cn.torna.service.metersphere.v3.model.Property;
import cn.torna.service.metersphere.v3.model.RequestBodyType;
import cn.torna.service.metersphere.v3.openapi.OpenApiDataConvert;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author thc
 */
@Service
public class ConvertService {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private ModuleService moduleService;

    public OpenAPI convertToOpenAPI(Long moduleId) {
        List<DocInfoDTO> docInfos = docInfoService.listDocDetail(moduleId);
//        List<DocInfoDTO> docInfos = docInfoService.listTreeDoc(moduleId);
        List<ApiDefinition> apis = buildApiDefinition(docInfos);
        return new OpenApiDataConvert().convert(apis);
    }

    private List<ApiDefinition> buildApiDefinition(List<DocInfoDTO> docInfos) {
        Map<Long, DocInfoDTO> docIdMap = docInfos.stream()
                .collect(Collectors.toMap(DocInfoDTO::getId, Function.identity()));
        return docInfos.stream()
//                .filter(docInfoDTO -> Objects.equals(docInfoDTO.getIsFolder(), Booleans.FALSE))
                .map(docInfoDTO -> this.buildApiDefinition(docInfoDTO, docIdMap))
                .collect(Collectors.toList());
    }

    private ApiDefinition buildApiDefinition(DocInfoDTO docInfoDTO, Map<Long, DocInfoDTO> docIdMap) {
        DocInfoDTO parent = docIdMap.get(docInfoDTO.getParentId());
        ApiDefinition apiDefinition = new ApiDefinition();
        String category = parent != null ? parent.getName() : docInfoDTO.getName();
        apiDefinition.setCategory(category);
        apiDefinition.setIsFolder(Objects.equals(docInfoDTO.getIsFolder(), Booleans.TRUE));
        apiDefinition.setSummary(docInfoDTO.getName());
        apiDefinition.setTags(Collections.singletonList(category));
        apiDefinition.setMethod(HttpMethod.of(docInfoDTO.getHttpMethod()));
        apiDefinition.setPath(docInfoDTO.getUrl());
        apiDefinition.setDescription(docInfoDTO.getDescription());
        apiDefinition.setDeprecated(!Objects.equals("$false$", docInfoDTO.getDeprecated()));
        apiDefinition.setParameters(buildParams(docInfoDTO, ParamStyleEnum.QUERY));
        RequestBodyType requestBodyType = buildRequestBodyType(docInfoDTO);
        apiDefinition.setRequestBodyType(requestBodyType);
        apiDefinition.setRequestBody(buildBody(docInfoDTO));
        apiDefinition.setRequestBodyForm(buildParams(docInfoDTO, ParamStyleEnum.REQUEST));
        apiDefinition.setResponses(buildResponse(docInfoDTO));
        return apiDefinition;
    }

    private List<Property> buildParams(DocInfoDTO docInfoDTO, ParamStyleEnum paramStyleEnum) {
        List<DocParamDTO> queryParams = docInfoDTO.getQueryParams();
        if (CollectionUtils.isEmpty(queryParams)) {
            return Collections.emptyList();
        }
        return queryParams.stream()
                .filter(docParamDTO -> Objects.equals(docParamDTO.getStyle(), paramStyleEnum.getStyle()))
                .map(this::convertProperty)
                .collect(Collectors.toList());
    }

    private RequestBodyType buildRequestBodyType(DocInfoDTO docInfoDTO) {
        String contentType = docInfoDTO.getContentType();
        if (contentType == null) {
            contentType = "";
        }
        String contentTypeLowerCase = contentType.toLowerCase();
        String httpMethod = docInfoDTO.getHttpMethod();
        switch (HttpMethod.of(httpMethod)) {
            case POST:
            case PUT:
                if (contentTypeLowerCase.contains("json")) {
                    return RequestBodyType.json;
                }
                if (contentTypeLowerCase.contains("multipart")) {
                    return RequestBodyType.form_data;
                }
                if (contentTypeLowerCase.contains("form")) {
                    return RequestBodyType.form;
                }
                return RequestBodyType.raw;
            default: {
                return null;
            }
        }
    }

    private Property buildBody(DocInfoDTO docInfoDTO) {
        List<DocParamDTO> requestParams = docInfoDTO.getRequestParams();
        if (CollectionUtils.isEmpty(requestParams)) {
            return null;
        }
        Byte isRequestArray = docInfoDTO.getIsRequestArray();
        return buildProperty(requestParams, isRequestArray);
    }

    private Property buildResponse(DocInfoDTO docInfoDTO) {
        List<DocParamDTO> responseParams = docInfoDTO.getResponseParams();
        if (CollectionUtils.isEmpty(responseParams)) {
            return null;
        }
        Byte isResponseArray = docInfoDTO.getIsResponseArray();
        return buildProperty(responseParams, isResponseArray);
    }

    private Property buildProperty(List<DocParamDTO> responseParams, Byte isArray) {
        Property property = new Property();
        property.setRequired(false);
        property.setType(DataTypes.OBJECT);
        Map<String, Property> propertyMap = new LinkedHashMap<>();
        for (DocParamDTO responseParam : responseParams) {
            Property prop = convertProperty(responseParam);
            propertyMap.put(responseParam.getName(), prop);
        }
        if (isArray == Booleans.TRUE) {
            Property items = new Property();
            items.setRequired(false);
            items.setType(DataTypes.OBJECT);
            items.setProperties(propertyMap);
            property.setItems(items);
        } else {
            property.setProperties(propertyMap);
        }
        return property;
    }

    private Property convertProperty(DocParamDTO docParamDTO) {
        Property property = new Property();
        property.setName(docParamDTO.getName());
        property.setType(docParamDTO.getType());
        property.setDateFormat(DATE_FORMAT);
        property.setDescription(docParamDTO.getDescription());
        property.setIn(buildParameterIn(docParamDTO));
        property.setRequired(docParamDTO.getRequire());
        property.setDeprecated(false);
        property.setExample(docParamDTO.getExample());
        property.setDefaultValue(docParamDTO.getExample());
        property.setValues(new ArrayList<>());
        property.setUniqueItems(false);
        property.setMinLength(0);
        property.setMaxLength(NumberUtils.toInt(docParamDTO.getMaxLength(), 0));
        List<DocParamDTO> children = docParamDTO.getChildren();
        String type = docParamDTO.getType();
        if (!CollectionUtils.isEmpty(children)) {
            if (type != null && type.toLowerCase().contains("object")) {
                Property prop = buildProperty(children, Booleans.FALSE);
                property.setProperties(prop.getProperties());
            } else if (isArray(type)) {
                Property prop = buildProperty(children, Booleans.TRUE);
                property.setItems(prop.getItems());
            }
        }
        property.setMinimum(new BigDecimal("0"));
        property.setMaximum(new BigDecimal("0"));
        return property;
    }

    private boolean isArray(String type) {
        if (type == null) {
            return false;
        }
        type = type.toLowerCase();
        return type.contains("list")
                || type.contains("array")
                || type.contains("collection")
                || type.contains("set");
    }

    private ParameterIn buildParameterIn(DocParamDTO docParamDTO) {
        Byte style = docParamDTO.getStyle();
        switch (ParamStyleEnum.of(style)) {
            case HEADER:
                return ParameterIn.header;
            case PATH:
                return ParameterIn.path;
            default:
                return ParameterIn.query;
        }
    }

    public Postman getPostman(Long moduleId) {
        ConvertService.Config config = new ConvertService.Config();
        config.setNeedHost(false);
        return convertToPostman(moduleId, config);
    }

    public Postman convertToPostman(Long moduleId, Config config) {
        Module module = moduleService.getById(moduleId);
        Context context = new Context();
        context.setModuleId(moduleId);
        context.setAppName(module.getName());
        context.setConfig(config);
        Postman postman = new Postman();
        Info info = buildInfo(module);
        List<DocInfoDTO> docInfos = docInfoService.listTreeDoc(module.getId());
        List<Item> items = buildItems(docInfos, context);
        postman.setInfo(info);
        postman.setItem(items);
        return postman;
    }


    private Info buildInfo(Module module) {
        Info info = new Info();
        info.setName(module.getName());
        return info;
    }

    private List<Item> buildItems(List<DocInfoDTO> docInfos, Context context) {
        return docInfos.stream()
                .map(docInfoDTO -> this.buildItem(docInfoDTO, context))
                .collect(Collectors.toList());
    }

    private Item buildItem(DocInfoDTO docInfoDTO, Context context) {
        Item item = new Item();
        item.setName(docInfoDTO.getDocName());
        item.setRequest(buildRequest(docInfoDTO, context));
        List<DocInfoDTO> children = docInfoDTO.getChildren();
        if (!CollectionUtils.isEmpty(children)) {
            List<Item> childItems = children.stream()
                    .map(docInfo -> this.buildItem(docInfo, context))
                    .collect(Collectors.toList());
            item.setItem(childItems);
        }
        return item;
    }

    private Request buildRequest(DocInfoDTO docInfoDTO, Context context) {
        if (docInfoDTO.getIsFolder() == Booleans.TRUE) {
            return null;
        }
        Request request = new Request();
        request.setMethod(docInfoDTO.getHttpMethod());
        request.setHeader(buildHeaders(docInfoDTO));
        request.setUrl(buildUrl(docInfoDTO, context));
        request.setBody(buildBody(docInfoDTO, context));
        request.setDescription(docInfoDTO.getDescription());
        return request;
    }

    private List<Header> buildHeaders(DocInfoDTO docInfoDTO) {
        List<Header> headers = new ArrayList<>();
        List<DocParamDTO> allHeaders = new ArrayList<>();
        allHeaders.addAll(docInfoDTO.getGlobalHeaders());
        allHeaders.addAll(docInfoDTO.getHeaderParams());

        for (DocParamDTO headerParam : allHeaders) {
            Header header = buildHeader(headerParam);
            headers.add(header);
        }
        return headers;
    }

    private Header buildHeader(DocParamDTO docParamDTO) {
        Header header = new Header();
        header.setKey(docParamDTO.getName());
        header.setValue(docParamDTO.getExample());
        header.setType(docParamDTO.getType());
        header.setDescription(docParamDTO.getDescription());
        return header;
    }

    private Url buildUrl(DocInfoDTO docInfoDTO, Context context) {
        Config config = context.getConfig();
        String path = StringUtils.trimLeadingCharacter(docInfoDTO.getUrl(), '/');
        String host = config.isNeedHost() ? "{{host_" + context.getAppName() + "}}" : "";
        String fullUrl = host + "/" + path;
        Url url = new Url();
        url.setProtocol("http");
        url.setHost(Collections.singletonList(host));
        url.setRaw(fullUrl);
        url.setPort(null);
        url.setPath(Arrays.asList(path.split("/")));
//        url.setVariable(new ArrayList<>());
        url.setQuery(this.buildParams(docInfoDTO.getQueryParams()));
        if (config.isNeedHost()) {
            url.setProtocol("");
        }
        return url;
    }

    private List<Param> buildParams(List<DocParamDTO> docParamDTOS) {
        return docParamDTOS.stream()
                .map(this::buildParam)
                .collect(Collectors.toList());
    }


    private Param buildParam(DocParamDTO docParamDTO) {
        Param param = new Param();
        param.setKey(docParamDTO.getName());
        param.setValue(docParamDTO.getExample());
        param.setType(docParamDTO.getType());
        param.setDescription(docParamDTO.getDescription());
        param.setChildren(Collections.emptyList());
        return param;
    }

    private Body buildBody(DocInfoDTO docInfoDTO, Context context) {
        String httpMethod = docInfoDTO.getHttpMethod();
        switch (httpMethod.toLowerCase()) {
            case "get":
            case "head":
                return null;
        }
        List<DocParamDTO> requestParams = docInfoDTO.getRequestParams();
        if (CollectionUtils.isEmpty(requestParams)) {
            return null;
        }
        String contentType = Optional.ofNullable(docInfoDTO.getContentType()).orElse("").toLowerCase();
        String mode = "raw";
        Body body = new Body();
        if (contentType.contains("urlencoded")) {
            mode = "urlencoded";
            List<Param> params = buildParams(requestParams);
            body.setUrlencoded(params);
        } else if (contentType.contains("multipart")) {
            mode = "formdata";
            List<Param> params = buildParams(requestParams);
            body.setFormdata(params);
        }
        if (contentType.contains("json")) {
            String json = buildJson(requestParams, context.getConfig().isFormatJson());
            Byte isResponseArray = docInfoDTO.getIsResponseArray();
            if (Objects.equals(isResponseArray, Booleans.TRUE)) {
                json = "[" + json + "]";
            }
            body.setRaw(json);
            body.setOptions(getJsonOptions());
        }
        body.setMode(mode);
        return body;
    }

    /*
    "raw": {
        "language": "json"
    }
     */
    private static JSONObject getJsonOptions() {
        JSONObject opt = new JSONObject();
        JSONObject raw = new JSONObject();
        raw.put("language", "json");
        opt.put("raw", raw);
        return opt;
    }

    public static String buildJson(List<DocParamDTO> requestParams, boolean format) {
        List<DocParamDTO> docParamDTOS = TreeUtil.convertTree(requestParams, 0L);
        JSONObject root = buildJsonObject(docParamDTOS);
        if (format) {
            return root.toString(SerializerFeature.PrettyFormat);
        } else {
            return root.toJSONString();
        }
    }

    public static JSONObject buildJsonObject(List<DocParamDTO> docParamDTOS) {
        JSONObject root = new JSONObject();
        for (DocParamDTO requestParam : docParamDTOS) {
            List<DocParamDTO> children = requestParam.getChildren();
            // 如果有子节点
            if (!CollectionUtils.isEmpty(children)) {
                Object child = buildJsonObject(children);
                if (DocParamService.isArrayType(requestParam.getType())) {
                    child = Collections.singletonList(child);
                }
                root.put(requestParam.getName(), child);
            } else {
                String description = requestParam.getDescription();
                String example = requestParam.getExample();
                if (StringUtils.hasText(example)) {
                    description = description + ",示例值:" + example;
                }
                root.put(requestParam.getName(), description);
            }
        }
        return root;
    }


    @Data
    private static class Context {
        private Long moduleId;
        private String appName;
        private Config config;
    }

    @Data
    public static class Config {
        private boolean needHost = true;
        private boolean formatJson = false;
    }

}
