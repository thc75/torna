package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.HttpHelper;
import cn.torna.common.bean.User;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.message.MessageFactory;
import cn.torna.common.util.DataIdUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.DocParam;
import cn.torna.dao.entity.Module;
import cn.torna.manager.doc.DataType;
import cn.torna.manager.doc.DocParser;
import cn.torna.manager.doc.IParam;
import cn.torna.manager.doc.postman.Body;
import cn.torna.manager.doc.postman.Header;
import cn.torna.manager.doc.postman.Item;
import cn.torna.manager.doc.postman.Param;
import cn.torna.manager.doc.postman.Postman;
import cn.torna.manager.doc.postman.Request;
import cn.torna.manager.doc.postman.Url;
import cn.torna.manager.doc.swagger.DocBean;
import cn.torna.manager.doc.swagger.DocItem;
import cn.torna.manager.doc.swagger.DocModule;
import cn.torna.manager.doc.swagger.DocParameter;
import cn.torna.manager.doc.swagger.Server;
import cn.torna.service.dto.DocItemCreateDTO;
import cn.torna.service.dto.ImportPostmanDTO;
import cn.torna.service.dto.ImportSwaggerDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.gitee.fastmybatis.core.util.ClassUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author tanghc
 */
@Service
@Slf4j
public class DocImportService {

    @Autowired
    @Qualifier("swaggerDocParserV2")
    private DocParser<DocBean> docParserV2;

    @Autowired
    @Qualifier("swaggerDocParserV3")
    private DocParser<DocBean> docParserV3;

    @Autowired
    @Qualifier("postmanDocParser")
    private DocParser<Postman> postmanParser;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private DocParamService docParamService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ModuleEnvironmentService moduleEnvironmentService;

    private static final Map<String, String> CONTEXT_TYPE_MAP = new HashMap<>();
    static {
        CONTEXT_TYPE_MAP.put("json", "application/json");
        CONTEXT_TYPE_MAP.put("xml", "application/xml");
        CONTEXT_TYPE_MAP.put("text", "text/plain");
    }


    /**
     * 导入swagger文档
     *
     * @param importSwaggerDTO importSwaggerDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public Module importSwagger(ImportSwaggerDTO importSwaggerDTO) {
        String json;
        String url = importSwaggerDTO.getImportUrl();
        try {
            HttpHelper.ResponseResult responseResult = HttpHelper
                    .create()
                    .basicAuth(importSwaggerDTO.getBasicAuthUsername(), importSwaggerDTO.getBasicAuthPassword())
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
            json = body;
        } catch (IOException e) {
            log.error("导入swagger异常, url:{}", url, e);
            throw new BizException("导入异常, msg:" + e.getMessage());
        }
        JSONObject docRoot = JSON.parseObject(json, Feature.OrderedField, Feature.DisableCircularReferenceDetect);
        DocParser<DocBean> docParser = docRoot.containsKey("openapi") ? docParserV3 : docParserV2;
        DocBean docBean = docParser.parseJson(json);
        return this.saveDocToDb(docBean, importSwaggerDTO);
    }

    /**
     * 导入postman文档
     *
     * @param importPostmanDTO 导入配置
     * @return 返回模块
     */
    @Transactional(rollbackFor = Exception.class)
    public Module importPostman(ImportPostmanDTO importPostmanDTO) {
        String json = importPostmanDTO.getJson();
        Postman postman = postmanParser.parseJson(json);
        return this.savePostmanToDB(postman, importPostmanDTO);
    }

    private Module savePostmanToDB(Postman postman, ImportPostmanDTO importPostmanDTO) {
        return this.createPostmanModule(postman, importPostmanDTO);
    }

    private Module createPostmanModule(Postman postman, ImportPostmanDTO importPostmanDTO) {
        User user = importPostmanDTO.getUser();
        String title = postman.getInfo().getName();
        // 创建模块
        Module module = moduleService.createPostmanModule(importPostmanDTO, title);
        List<Item> items = postman.getItem();
        this.saveItems(items, null, module, user);
        return module;
    }

    private void saveItems(List<Item> items, DocInfo parent, Module module, User user) {
        for (Item item : items) {
            // 如果是文件夹
            if (item.isFolder()) {
                Long parentId = parent == null ? 0L : parent.getId();
                DocInfo folder = docInfoService.createDocFolder(item.getName(), module.getId(), user, parentId);
                // 创建模块下的文档
                List<Item> subItems = item.getItem();
                this.saveItems(subItems, folder, module, user);
            } else {
                DocItemCreateDTO docItemCreateDTO = this.buildPostmanDocItemCreateDTO(item, parent, module, user);
                DocInfo docItem = docInfoService.createDocItem(docItemCreateDTO);

                // path参数
                Url url = item.getRequest().getUrl();
                List<Param> variable = url.getVariable();
                this.savePostmanParams(variable, docItem, docParameter -> {
                    return ParamStyleEnum.PATH;
                }, user);
                // query参数
                List<Param> queryParams = url.getQuery();
                this.savePostmanParams(queryParams, docItem, docParameter -> {
                    return ParamStyleEnum.QUERY;
                }, user);
                // body参数
                BodyWrapper bodyWrapper = this.buildPostmanParams(item);
                List<Param> params = bodyWrapper.getParams();
                boolean arrayBody = bodyWrapper.isArrayBody();
                if (arrayBody) {
                    String requestArrayType;
                    if (params.size() > 1) {
                        requestArrayType = DataType.OBJECT;
                    } else {
                        Param param = params.get(0);
                        requestArrayType = StringUtils.hasText(param.getKey()) ? "object" : param.getType();
                    }
                    docItem.setRequestArrayType(requestArrayType);
                }
                docItem.setIsRequestArray(Booleans.toValue(arrayBody));
                this.savePostmanParams(params, docItem, docParameter -> {
                    return ParamStyleEnum.REQUEST;
                }, user);

                docInfoService.update(docItem);
            }
        }
    }

    private BodyWrapper buildPostmanParams(Item item) {
        Request request = item.getRequest();
        Body body = request.getBody();
        if (body != null) {
            return this.parseBody(body);
        }
        return new BodyWrapper(false, Collections.emptyList());
    }

    private BodyWrapper parseBody(Body body) {
        List<Param> list = new ArrayList<>();
        String mode = body.getMode();
        boolean isArrayBody = false;
        if(mode == null){
            return new BodyWrapper(list);
        }
        switch (mode) {
            case "raw":
                String json = body.getRaw();
                if (StringUtils.isEmpty(json)) {
                    return new BodyWrapper(list);
                }
                JSON jsonObj;
                Object parseObj = JSON.parse(json);
                if (parseObj instanceof JSONArray) {
                    isArrayBody = true;
                    jsonObj = (JSONArray) parseObj;
                } else if (parseObj instanceof JSONObject) {
                    jsonObj = (JSONObject) parseObj;
                } else {
                    throw new IllegalArgumentException("error json type:" + parseObj);
                }
                List<Param> params = this.parseParams(jsonObj);
                list.addAll(params);
                break;
            case "urlencoded":
                List<Param> urlencodedParams = body.getUrlencoded();
                list.addAll(urlencodedParams);
                break;
            default: {}
        }
        return new BodyWrapper(isArrayBody, list);
    }

    private static boolean isSingleValueArray(JSONArray jsonArray) {
        if (CollectionUtils.isEmpty(jsonArray)) {
            return false;
        }
        Object o = jsonArray.get(0);
        return ClassUtil.isPrimitive(o.getClass().getName());
    }

    private List<Param> parseParams(JSON json) {
        try {
            List<Param> list = new ArrayList<>();
            JSONObject jsonObject;
            if (json instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) json;
                if (jsonArray.isEmpty()) {
                    return Collections.emptyList();
                }
                // 是否单值数组，如：[1,2,3]
                if (isSingleValueArray(jsonArray)) {
                    return parsePrueValueParam(jsonArray);
                } else {
                    // 数组类型默认取第一个对象
                    jsonObject = jsonArray.getJSONObject(0);
                }
            } else {
                jsonObject = (JSONObject) json;
            }
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                Param param = new Param();
                list.add(param);
                param.setKey(entry.getKey());
                param.setType("string");
                Object value = entry.getValue();
                if (value instanceof JSONObject) {
                    param.setType("object");
                    JSONObject valueObject = (JSONObject) value;
                    List<Param> params = this.parseParams(valueObject);
                    param.setChildren(params);
                } else if (value instanceof JSONArray) {
                    param.setType("array");
                    JSONArray array = (JSONArray) value;
                    if (array.size() > 0) {
                        if (isPureArray(array)) {
                            param.setValue(array.toJSONString());
                        } else {
                            JSONObject el = array.getJSONObject(0);
                            List<Param> params = this.parseParams(el);
                            param.setChildren(params);
                        }

                    }
                } else {
                    param.setValue(String.valueOf(entry.getValue()));
                }
            }
            return list;
        } catch (Exception e) {
            log.error("解析json出错, json={}", json);
            throw new RuntimeException(e);
        }
    }

    private List<Param> parsePrueValueParam(JSONArray jsonArray) {
        Object o = jsonArray.get(0);
        Param param = new Param();
        param.setType(o.getClass().getSimpleName().toLowerCase());
        param.setValue(String.valueOf(o));
        return Collections.singletonList(param);
    }

    private boolean isPureArray(JSONArray array) {
        for (int i = 0; i < array.size(); i++) {
            String value = array.getString(i);
            if (value.startsWith("{") && value.endsWith("}")) {
                return false;
            }
        }
        return true;
    }

    /**
     * 保存文档到数据库
     *
     * @param docBean
     * @param importSwaggerDTO
     */
    private Module saveDocToDb(DocBean docBean, ImportSwaggerDTO importSwaggerDTO) {
        User user = importSwaggerDTO.getUser();
        String title = docBean.getTitle();
        // 创建模块
        Module module = moduleService.createSwaggerModule(importSwaggerDTO, title);
        // 保存调试环境
        for (Server server : docBean.getServers()) {
            moduleEnvironmentService.setDebugEnv(module.getId(), server.getDescription(), server.getUrl());
        }
        // 创建文档分类
        List<DocModule> docModules = docBean.getDocModules();
        docModules.sort(Comparator.comparing(DocModule::getOrder));
        for (DocModule docModule : docModules) {
            DocInfo moduleDocInfo = docInfoService.createDocFolder(docModule.getModule(), module.getId(), user);
            // 创建模块下的文档
            List<DocItem> items = docModule.getItems();
            for (DocItem item : items) {
                DocItemCreateDTO docItemCreateDTO = this.buildDocItemCreateDTO(item, moduleDocInfo, user);
                DocInfo docItem = docInfoService.createDocItem(docItemCreateDTO);
                // query参数
                List<DocParameter> queryParameters = item.getQueryParameters();
                this.saveParams(queryParameters, docItem, this::buildStyleEnum, user);
                // body参数
                List<DocParameter> requestParameters = item.getRequestParameters();
                this.saveParams(requestParameters, docItem, this::buildStyleEnum, user);
                List<DocParameter> responseParameters = item.getResponseParameters();
                this.saveParams(responseParameters, docItem, p -> ParamStyleEnum.RESPONSE, user);
            }
        }
        return module;
    }

    private void saveParams(
            List<DocParameter> parameters
            , DocInfo docItem
            , Function<IParam, ParamStyleEnum> styleEnumFunction
            , User user
    ) {
        if (CollectionUtils.isEmpty(parameters)) {
            return;
        }
        for (DocParameter parameter : parameters) {
            this.saveDocParam(parameter, docItem, 0, styleEnumFunction, user);
        }

    }

    private void savePostmanParams(
            List<Param> parameters
            , DocInfo docItem
            , Function<IParam, ParamStyleEnum> styleEnumFunction
            , User user
    ) {
        if (CollectionUtils.isEmpty(parameters)) {
            return;
        }
        for (Param parameter : parameters) {
            this.saveDocParam(parameter, docItem, 0, styleEnumFunction, user);
        }

    }

    private void saveDocParam(
            IParam docParameter
            , DocInfo docInfo
            , long parentId
            , Function<IParam, ParamStyleEnum> styleEnumFunction
            , User user
    ) {
        DocParam docParam = new DocParam();
        ParamStyleEnum styleEnum = styleEnumFunction.apply(docParameter);
        String dataId = DataIdUtil.getDocParamDataId(docInfo.getId(), parentId, styleEnum.getStyle(), docParameter.getName());
        docParam.setDataId(dataId);
        docParam.setName(docParameter.getName());
        docParam.setType(docParameter.getType());
        docParam.setRequired(BooleanUtils.toIntegerObject(docParameter.getRequired()).byteValue());
        docParam.setMaxLength(docParameter.getMaxLength());
        docParam.setExample(docParameter.getExample());
        docParam.setDescription(docParameter.getDescription());
        docParam.setDocId(docInfo.getId());
        docParam.setParentId(parentId);
        docParam.setStyle(styleEnum.getStyle());
        docParam.setCreatorId(user.getUserId());
        docParam.setCreateMode(user.getOperationModel());
        docParam.setCreatorName(user.getNickname());
        docParam.setModifierId(user.getUserId());
        docParam.setModifyMode(user.getOperationModel());
        docParam.setModifierName(user.getNickname());
        docParam.setIsDeleted(Booleans.FALSE);
        // 保存操作
        DocParam savedDoc = docParamService.saveParam(docParam);

        // 处理子节点
        List<IParam> children = docParameter.getChildren();
        if (children != null) {
            for (IParam child : children) {
                this.saveDocParam(child, docInfo, savedDoc.getId(), styleEnumFunction, user);
            }
        }
    }


    private DocItemCreateDTO buildDocItemCreateDTO(DocItem item, DocInfo parent, User user) {
        DocItemCreateDTO docItemCreateDTO = new DocItemCreateDTO();
        docItemCreateDTO.setName(item.getSummary());
        docItemCreateDTO.setUrl(item.getPath());
        docItemCreateDTO.setContentType(String.join( ",", item.getConsumes()));
        docItemCreateDTO.setHttpMethod(item.getMethod());
        docItemCreateDTO.setDescription(item.getDescription());
        docItemCreateDTO.setModuleId(parent.getModuleId());
        docItemCreateDTO.setParentId(parent.getId());
        docItemCreateDTO.setUser(user);
        return docItemCreateDTO;
    }

    private DocItemCreateDTO buildPostmanDocItemCreateDTO(Item item, DocInfo parent, Module module, User user) {
        Request request = item.getRequest();
        String url = request.getUrl().getFullUrl();
        String contentType = this.buildContentType(request);
        DocItemCreateDTO docItemCreateDTO = new DocItemCreateDTO();
        docItemCreateDTO.setName(item.getName());
        docItemCreateDTO.setUrl(url);
        docItemCreateDTO.setContentType(contentType);
        docItemCreateDTO.setHttpMethod(request.getMethod());
        docItemCreateDTO.setDescription(request.getDescription());
        docItemCreateDTO.setModuleId(module.getId());
        if (parent != null) {
            docItemCreateDTO.setParentId(parent.getId());
        }
        docItemCreateDTO.setUser(user);
        return docItemCreateDTO;
    }

    private String buildContentType(Request request) {
        List<Header> headerList = request.getHeader();
        List<Header> headers = Optional.ofNullable(headerList).orElse(Collections.emptyList());
        Optional<String> headerOptional = headers.stream()
                .filter(header -> "Content-Type".equals(header.getKey()))
                .map(Header::getValue)
                .findFirst();
        if (headerOptional.isPresent()) {
            return headerOptional.get();
        }
        Body body = request.getBody();
        if (body == null) {
            return "";
        }
        String mode = body.getMode();
        if(mode == null){
            return "";
        }
        switch (mode) {
            case "raw":
                JSONObject options = body.getOptions();
                // json/xml/text
                String lang = Optional.ofNullable(options)
                        .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("raw")))
                        .map(jsonObject -> jsonObject.getString("language"))
                        .orElse("text");
                return CONTEXT_TYPE_MAP.get(lang);
            case "urlencoded":
                return "application/x-www-form-urlencoded";
            default:
                return "";
        }
    }

    private ParamStyleEnum buildStyleEnum(IParam docParameter) {
        String in = ((DocParameter)docParameter).getIn();
        if (in == null) {
            in = "request";
        }
        switch (in) {
            case "path":
                return ParamStyleEnum.PATH;
            case "query":
                return ParamStyleEnum.QUERY;
            case "header":
                return ParamStyleEnum.HEADER;
            default:
                return ParamStyleEnum.REQUEST;
        }
    }

    @Data
    @AllArgsConstructor
    private static class BodyWrapper {
        private boolean isArrayBody;
        private List<Param> params;

        public BodyWrapper(List<Param> params) {
            this.params = params;
        }
    }

}
