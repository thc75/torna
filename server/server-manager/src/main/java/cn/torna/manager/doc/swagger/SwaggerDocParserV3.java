package cn.torna.manager.doc.swagger;

import cn.torna.manager.doc.ApiInfo;
import cn.torna.manager.doc.DataType;
import cn.torna.manager.doc.DocParser;
import cn.torna.manager.doc.ParseConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.alibaba.fastjson.parser.Feature;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
 * 解析swagger3的json内容
 *
 * @author tanghc
 */
@Service("swaggerDocParserV3")
public class SwaggerDocParserV3 implements DocParser<DocBean> {

    private final Set<String> cycleCache = new HashSet<>(8);

    @Override
    public DocBean parseJson(String json, ParseConfig config) {
        cycleCache.clear();
        JSONObject docRoot = JSON.parseObject(json, Feature.OrderedField, Feature.DisableCircularReferenceDetect);
        JSONObject info = docRoot.getJSONObject("info");
        List<Server> servers = this.getServers(docRoot);
        List<DocItem> docItems = new ArrayList<>();
        String allowMethod = config.getAllowMethod();

        JSONObject paths = docRoot.getJSONObject("paths");
        if (paths == null) {
            paths = new JSONObject();
        }
        Set<String> pathNameSet = paths.keySet();
        for (String path : pathNameSet) {
            JSONObject pathInfo = paths.getJSONObject(path);
            Map<String, List<DocItem>> sameMap = new HashMap<>(16);
            // key: get,post,head...
            Set<String> httpMethodList = pathInfo.keySet();
            for (String method : httpMethodList) {
                ApiInfo apiInfo = new ApiInfo();
                apiInfo.setPath(path);
                apiInfo.setMethod(method);

                JSONObject docBean = pathInfo.getJSONObject(method);
                DocItem docItem = buildDocItem(apiInfo, docBean, docRoot);
                String key = docItem.getSummary() + docItem.getDescription();
                List<DocItem> docItemList = sameMap.computeIfAbsent(key, (k) -> new ArrayList<>());
                docItemList.add(docItem);
            }

            if (sameMap.size() == 1 && httpMethodList.size() != 1) {
                String key = sameMap.keySet().iterator().next();
                List<DocItem> docItemList = sameMap.get(key);
                // 使用了@RequestMapping，没有指定方法
                if (docItemList.size() == httpMethodList.size()) {
                    String allowMethodTemp = allowMethod;
                    // 取其中一个，判断是否是文件上传接口
                    boolean uploadRequest = docItemList.get(0).getUploadRequest();
                    // 如果是上传文件接口强制使用POST
                    if (uploadRequest) {
                        allowMethodTemp = "POST";
                    }
                    for (DocItem docItem : docItemList) {
                        if (allowMethodTemp.contains(docItem.getMethod())) {
                            docItems.add(docItem);
                        }
                    }
                }
            } else {
                for (List<DocItem> docItemList : sameMap.values()) {
                    docItems.addAll(docItemList);
                }
            }
        }
        // 先根据路径排序，再根据描述排序
        docItems.sort(Comparator.comparing(DocItem::getPath).thenComparing(DocItem::getSummary));

        List<DocModule> docModuleList = docItems.stream()
                .collect(Collectors.groupingBy(DocItem::getModule))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<DocItem> docItemList = entry.getValue();
                    DocModule docModule = new DocModule();
                    docModule.setModule(entry.getKey());
                    docModule.setItems(docItemList);
                    return docModule;
                })
                // 模块排序
                .sorted(Comparator.comparing(DocModule::getOrder).thenComparing(DocModule::getModule))
                .collect(Collectors.toList());

        DocBean docBean = new DocBean();

        docBean.setTitle(info.getString("title"));
        docBean.setVersion(info.getString("version"));
        docBean.setDescription(info.getString("description"));
        docBean.setServers(servers);
        docBean.setDocModules(docModuleList);
        return docBean;
    }

    private DocItem buildDocItem(ApiInfo apiInfo, JSONObject docBean, JSONObject docRoot) {
        DocItem docItem = new DocItem();
        docItem.setId(DigestUtils.md5DigestAsHex((apiInfo.toString()).getBytes(StandardCharsets.UTF_8)));
        docItem.setRequestUrl(apiInfo.getRequestUrl());
        docItem.setPath(apiInfo.getPath());
        docItem.setMethod(apiInfo.getMethod().toUpperCase());
        docItem.setSummary(docBean.getString("summary"));
        docItem.setDescription(docBean.getString("description"));
        docItem.setMultiple(docBean.getString("multiple") != null);
        docItem.setProduces(getProduces(docBean));
        docItem.setConsumes(getConsumes(docBean));
        String moduleName = this.buildModuleName(docBean, docRoot);
        docItem.setModule(moduleName);
        List<DocParameter> requestParameterList = this.buildRequestParameterList(apiInfo, docBean, docRoot);
        List<DocParameter> queryParameterList = this.buildQueryParameterList(apiInfo, docBean, docRoot);
        boolean hasUploadParam = hasUploadParam(requestParameterList);
        docItem.setUploadRequest(hasUploadParam);
        docItem.setRequestParameters(requestParameterList);
        docItem.setQueryParameters(queryParameterList);

        // 清除缓存，可以再次使用
        cycleCache.clear();

        List<DocParameter> responseParameterList = this.buildResponseParameterList(apiInfo, docBean, docRoot);
        docItem.setResponseParameters(responseParameterList);

        return docItem;
    }

    private boolean hasUploadParam(List<DocParameter> docParameterList) {
        for (DocParameter requestParameter : docParameterList) {
            String type = requestParameter.getType();
            if (DataType.FILE.equalsIgnoreCase(type) || DataType.FILE.equals(requestParameter.getElementType())) {
                return true;
            }
        }
        return false;
    }

    private String buildModuleName(JSONObject docBean, JSONObject docRoot) {
        String title = docRoot.getJSONObject("info").getString("title");
        JSONArray tags = docBean.getJSONArray("tags");
        if (tags != null && tags.size() > 0) {
            return tags.getString(0);
        }
        return title;
    }

    private List<DocParameter> buildRequestParameterList(ApiInfo apiInfo, JSONObject docBean, JSONObject docRoot) {
        JSONArray parameters = this.getParameterObject(docBean);
        List<DocParameter> docParameterList = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
            JSONObject fieldJson = parameters.getJSONObject(i);
            RefInfo refInfo = getRefInfo(fieldJson.getJSONObject("schema"));
            if (refInfo == null) {
                refInfo = getRefInfo(fieldJson.getJSONObject("items"));
            }
            if (refInfo != null) {
                if (refInfo.getRef().contains("MultipartFile")) {
                    DocParameter docParameter = fieldJson.toJavaObject(DocParameter.class);
                    docParameter.setType("file");
                    docParameterList.add(docParameter);
                } else {
                    String fieldType = getFieldType(fieldJson);
                    List<DocParameter> parameterList = this.buildDocParameters(apiInfo, refInfo, docRoot, true);
                    parameterList.forEach(docParameter -> docParameter.setParentType(fieldType));
                    docParameterList.addAll(parameterList);
                }
            } else {
                fieldJson = formatFieldJson(docRoot, fieldJson);
                DocParameter docParameter = fieldJson.toJavaObject(DocParameter.class);
                // @RequestBody String reqEntity
                // 这种情况下name为null
                if (docParameter.getName() == null) {
                    continue;
                }
                this.setType(fieldJson, docParameter);
                docParameterList.add(docParameter);
            }
        }
        return formatDocParameters(docParameterList);
    }

    private List<DocParameter> buildQueryParameterList(ApiInfo apiInfo, JSONObject docBean, JSONObject docRoot) {
        Optional<JSONArray> parametersOptional = Optional.ofNullable(docBean.getJSONArray("parameters"));
        JSONArray parameters = parametersOptional.orElseGet(JSONArray::new);
        List<DocParameter> docParameterList = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
            /*
            {
                        "name": "id",
                        "in": "query",
                        "description": "id",
                        "required": true,
                        "style": "form",
                        "schema": {
                            "type": "integer",
                            "format": "int32"
                        }
                    }
             */
            JSONObject fieldJson = parameters.getJSONObject(i);
            fieldJson = formatFieldJson(docRoot, fieldJson);
            DocParameter docParameter = fieldJson.toJavaObject(DocParameter.class);
            // @RequestBody String reqEntity
            // 这种情况下name为null
            if (docParameter.getName() == null) {
                continue;
            }
            this.setType(fieldJson, docParameter);
            docParameterList.add(docParameter);
        }
        return docParameterList;
    }

    private List<DocParameter> formatDocParameters(List<DocParameter> docParameterList) {
        Map<String, List<DocParameter>> collect = docParameterList.stream()
                .filter(docParameter -> docParameter.getName().contains("."))
                .map(docParameter -> {
                    String name = docParameter.getName();
                    int index = name.indexOf('.');
                    String module = name.substring(0, index);
                    String newName = name.substring(index + 1);
                    DocParameter ret = new DocParameter();
                    BeanUtils.copyProperties(docParameter, ret);
                    ret.setName(newName);
                    ret.setModule(module);
                    return ret;
                })
                .collect(Collectors.groupingBy(DocParameter::getModule));

        collect.forEach((key, value) -> {
            DocParameter moduleDoc = new DocParameter();
            moduleDoc.setName(key);
            moduleDoc.setType("object");
            moduleDoc.setRefs(value);
            docParameterList.add(moduleDoc);
        });

        return docParameterList.stream()
                .filter(docParameter -> !docParameter.getName().contains("."))
                .collect(Collectors.toList());
    }

    private void setType(JSONObject fieldJson, DocParameter docParameter) {
        String type = getFieldType(fieldJson);
        docParameter.setType(type);
        if (DataType.ARRAY.equals(type)) {
            JSONObject items = fieldJson.getJSONObject("items");
            Optional<String> optionalType = Optional.ofNullable(items)
                    .map(jsonObject -> jsonObject.getString("type"));
            if (!optionalType.isPresent()) {
                optionalType = Optional.ofNullable(items)
                        .flatMap(jsonObject -> {
                            String ref = jsonObject.getString("$ref");
                            String refName = getRefName(ref);
                            return Optional.ofNullable(refName);
                        });
            }

            String elementType = optionalType.orElseGet(() -> Optional.ofNullable(fieldJson.getJSONObject("schema"))
                    .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("items")))
                    .map(jsonObject -> {
                        String actType = jsonObject.getString("type");
                        if (actType == null) {
                            String $ref = jsonObject.getString("$ref");
                            return getRefName($ref);
                        }
                        return actType;
                    })
                    .orElse("string"));
            docParameter.setElementType(elementType);
        }
        this.setEnum(fieldJson, docParameter);
    }



    private void setEnum(JSONObject fieldJson, DocParameter docParameter) {
        JSONArray enumsArr = getEnum(fieldJson);
        // 如果是枚举字段
        if (enumsArr != null) {
            docParameter.setEnums(enumsArr.toJavaList(String.class));
            docParameter.setType(DataType.ENUM);
        }
    }

    private JSONArray getEnum(JSONObject fieldJson) {
        JSONArray array = fieldJson.getJSONArray("enum");
        if (array == null) {
            array = Optional.ofNullable(fieldJson.getJSONObject("schema"))
                    .flatMap(schema -> Optional.ofNullable(schema.getJSONArray("enum")))
                    .orElse(null);
        }
        return array;
    }

    private List<DocParameter> buildResponseParameterList(ApiInfo apiInfo, JSONObject docBean, JSONObject docRoot) {
        if (isJsonArray(docBean)) {
            DocParameter docParameter = new DocParameter();
            docParameter.setType(DataType.ARRAY);
            return Collections.singletonList(docParameter);
        } else {
            RefInfo refInfo = getResponseRefInfo(docBean);
            List<DocParameter> respParameterList = Collections.emptyList();
            if (refInfo != null) {
                respParameterList = this.buildDocParameters(apiInfo, refInfo, docRoot, true);
                // 如果返回数组
                if (refInfo.getIsArray()) {
                    DocParameter docParameter = new DocParameter();
                    docParameter.setName(refInfo.getRef());
                    docParameter.setType(DataType.ARRAY);
                    docParameter.setRefs(respParameterList);
                    respParameterList = Collections.singletonList(docParameter);
                }
            }
            return respParameterList;
        }
    }

    private boolean isJsonArray(JSONObject docBean) {
        return Optional.ofNullable(docBean.getJSONObject("responses"))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("200")))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("content")))
                .map(jsonObject -> {
                    String mediaType = jsonObject.keySet().iterator().next();
                    JSONObject schema = jsonObject
                            .getJSONObject(mediaType)
                            .getJSONObject("schema");
                    if (mediaType.contains("json")) {
                        boolean isArray = DataType.ARRAY.equals(schema.getString("type"));
                        if (isArray) {
                            JSONObject items = schema.getJSONObject("items");
                            String elementType = items.getString("type");
                            return elementType != null;
                        }
                    }
                    return false;
                })
                .orElse(false);
    }

    private List<DocParameter> buildDocParameters(ApiInfo apiInfo, RefInfo currentRef, JSONObject docRoot, boolean doSubRef) {
        String ref = currentRef.getRef();
        JSONObject responseObject = getDefinitions(docRoot, currentRef).getJSONObject(ref);
        if (responseObject == null) {
            return Collections.emptyList();
        }
        String className = responseObject.getString("title");
        JSONObject properties = responseObject.getJSONObject("properties");
        List<DocParameter> docParameterList = new ArrayList<>();
        if (properties == null) {
            return docParameterList;
        }
        Set<String> fieldNames = properties.keySet();
        for (String fieldName : fieldNames) {
            /*
               {
                  "description": "分类故事",
                  "$ref": "#/definitions/StoryVO"
                }
             */
            JSONObject fieldInfo = properties.getJSONObject(fieldName);
            DocParameter docParameter = fieldInfo.toJavaObject(DocParameter.class);
            docParameter.setIn("body");
            docParameter.setName(fieldName);
            this.setType(fieldInfo, docParameter);
            RefInfo refInfo = this.getRefInfo(fieldInfo);
            if (refInfo != null) {
                docParameter.setType(DataType.OBJECT);
            }
            if (refInfo != null && doSubRef) {
                String subRef = refInfo.getRef();
                String key = apiInfo.toString() + className + fieldName + ref + subRef;
                // 避免对象相互依赖导致无限循环
                boolean nextDoRef = !isSameRef(ref, subRef) && !cycleCache.contains(key);
                if (nextDoRef) {
                    cycleCache.add(key);
                }
                List<DocParameter> refs = buildDocParameters(apiInfo, refInfo, docRoot, nextDoRef);
                docParameter.setType(refInfo.getIsArray() ? DataType.ARRAY : DataType.OBJECT);
                docParameter.setRefs(refs);
            }
            this.setEnum(fieldInfo, docParameter);
            docParameterList.add(docParameter);
        }
        return docParameterList;
    }


    private boolean isSameRef(String ref, String subRef) {
        return Objects.equals(ref, subRef);
    }

    private RefInfo getRefInfo(JSONObject schema) {
        if (schema == null) {
            return null;
        }
        String ref;
        boolean isArray = DataType.ARRAY.equals(schema.getString("type"));
        if (isArray) {
            JSONObject items = schema.getJSONObject("items");
            ref = items.getString("$ref");
        } else {
            // #/definitions/Category
            ref = schema.getString("$ref");
        }
        if (schema.containsKey("additionalProperties")) {
            String additionalProperties = schema.getString("additionalProperties");
            if (JSONValidator.from(additionalProperties).validate() && additionalProperties.contains("$ref")) {
                ref = JSON.parseObject(additionalProperties).getString("$ref");
            }
        }
        if (ref == null) {
            return null;
        }
        RefInfo refInfo = new RefInfo();
        refInfo.setIsArray(isArray);
        refInfo.setRawRef(ref);
        refInfo.setRef(getRefName(ref));
        return refInfo;
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

    
    private List<Server> getServers(JSONObject docRoot) {
        /*
        "servers": [
            {
                "url": "http://localhost:8080",
                "description": "Inferred Url"
            }
        ]
         */
        JSONArray servers = Optional.ofNullable(docRoot)
                .flatMap(root -> Optional.ofNullable(root.getJSONArray("servers")))
                .orElse(new JSONArray(0));
        return servers.toJavaList(Server.class);
    }

    /**
     * <pre>
     * "requestBody": {
     *                     "content": {
     *                         "application/json": {
     *                             "schema": {
     *                                 "$ref": "#/components/schemas/用户基本信息"
     *                             }
     *                         }
     *                     }
     *                 }
     * </pre>
     * @param docBean 文档内容
     * @return 返回consume
     */
    
    private List<String> getConsumes(JSONObject docBean) {
        Set<String> consumes = Optional.ofNullable(docBean.getJSONObject("requestBody"))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("content")))
                .map(JSONObject::keySet)
                .orElse(Collections.emptySet());
        return new ArrayList<>(consumes);
    }

    
    private List<String> getProduces(JSONObject docBean) {
        Set<String> consumes = Optional.ofNullable(docBean.getJSONObject("responses"))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("200")))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("content")))
                .map(JSONObject::keySet)
                .orElse(Collections.emptySet());
        return new ArrayList<>(consumes);
    }

    
    private JSONObject formatFieldJson(JSONObject docRoot, JSONObject fieldJson) {
        if (fieldJson.containsKey("$ref")) {
            RefInfo refInfo = getRefInfo(fieldJson);
            return getDefinitions(docRoot, refInfo).getJSONObject(refInfo.getRef());
        }
        return fieldJson;
    }

    // components.schemas.xx
    // components.parameters.xx
    
    private JSONObject getDefinitions(JSONObject docRoot, RefInfo refInfo) {
        String rawRef = refInfo.getRawRef();
        if (rawRef.contains("schemas")) {
            return docRoot.getJSONObject("components").getJSONObject("schemas");
        } else if (rawRef.contains("parameters")) {
            return docRoot.getJSONObject("components").getJSONObject("parameters");
        }
        return new JSONObject();
    }

    
    private RefInfo getResponseRefInfo(JSONObject docBean) {
        return Optional.ofNullable(docBean.getJSONObject("responses"))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("200")))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("content")))
                .flatMap(jsonObject -> {
                    String mediaType = jsonObject.keySet().iterator().next();
                    JSONObject schema = jsonObject
                            .getJSONObject(mediaType)
                            .getJSONObject("schema");
                    return Optional.ofNullable(schema);
                })
                .map(this::getRefInfo)
                .orElse(null);
    }

    /*
    "requestBody": {
                    "content": {
                        "application/json": {
                            "schema": {
                                "$ref": "#/components/schemas/用户基本信息"
                            }
                        }
                    }
                }
     */
    
    private JSONArray getParameterObject(JSONObject docBean) {
        JSONArray jsonArray = new JSONArray();
        JSONObject requestBody = docBean.getJSONObject("requestBody");
        Optional.ofNullable(requestBody)
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("content")))
                .flatMap(jsonObject -> {
                    String mediaType = jsonObject.keySet().iterator().next();
                    JSONObject schema = jsonObject
                            .getJSONObject(mediaType);
                    return Optional.ofNullable(schema);
                })
                .ifPresent(jsonArray::add);
        return jsonArray;
    }

    /*
        {
                            "name": "pageIndex",
                            "in": "query",
                            "description": "查看第几页",
                            "required": true,
                            "style": "form",
                            "schema": {
                                "type": "integer",
                                "format": "int32"
                            }
                        }
         */
    
    private String getFieldType(JSONObject fieldJson) {
        JSONObject schema = fieldJson.getJSONObject("schema");
        if (schema == null) {
            return "string";
        }
        return schema.getString("type");
    }
}
