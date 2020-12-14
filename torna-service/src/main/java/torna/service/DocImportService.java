package torna.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang.BooleanUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import torna.common.bean.HttpTool;
import torna.common.bean.User;
import torna.common.enums.ParamStyleEnum;
import torna.common.exception.BizException;
import torna.common.util.DataIdUtil;
import torna.dao.entity.DocInfo;
import torna.dao.entity.DocParam;
import torna.dao.entity.Module;
import torna.manager.doc.DocBean;
import torna.manager.doc.DocItem;
import torna.manager.doc.DocModule;
import torna.manager.doc.DocParameter;
import torna.manager.doc.DocParser;
import torna.service.dto.DocItemCreateDTO;
import torna.service.dto.ImportSwaggerDTO;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * @author tanghc
 */
@Service
@Slf4j
public class DocImportService {

    @Autowired
    @Qualifier("swaggerDocParserV2")
    private DocParser docParserV2;

    @Autowired
    @Qualifier("swaggerDocParserV3")
    private DocParser docParserV3;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private DocParamService docParamService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ModuleConfigService moduleConfigService;

    /**
     * 导入swagger文档
     *
     * @param importSwaggerDTO importSwaggerDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void importSwagger(ImportSwaggerDTO importSwaggerDTO) {
        String json;
        String url = importSwaggerDTO.getImportUrl();
        try {
            HttpTool httpTool = new HttpTool(importSwaggerDTO.getBasicAuthUsername(), importSwaggerDTO.getBasicAuthPassword());
            Response response = httpTool.requestForResponse(url, null, null, HttpTool.HTTPMethod.GET);
            String body = response.body().string();
            if (response.code() != HttpStatus.OK.value()) {
                throw new BizException(body);
            }
            json = body;
        } catch (IOException e) {
            log.error("导入swagger异常, url:{}", url, e);
            throw new BizException("导入异常, msg:" + e.getMessage());
        }
        JSONObject docRoot = JSON.parseObject(json, Feature.OrderedField, Feature.DisableCircularReferenceDetect);
        DocParser docParser = docRoot.containsKey("openapi") ? docParserV3 : docParserV2;
        DocBean docBean = docParser.parseJson(json);
        this.saveDocToDb(docBean, importSwaggerDTO);
    }

    /**
     * 保存文档到数据库
     *
     * @param docBean
     * @param importSwaggerDTO
     */
    private void saveDocToDb(DocBean docBean, ImportSwaggerDTO importSwaggerDTO) {
        User user = importSwaggerDTO.getUser();
        String title = docBean.getTitle();
        // 创建模块
        Module module = moduleService.createSwaggerModule(importSwaggerDTO, title);
        // 保存baseUrl
        moduleConfigService.setBaseUrl(module.getId(), docBean.getRequestUrl());
        // 创建文档分类
        List<DocModule> docModules = docBean.getDocModules();
        docModules.sort(Comparator.comparing(DocModule::getOrder));
        for (DocModule docModule : docModules) {
            DocInfo moduleDocInfo = docInfoService.createDocFolderNoCheck(docModule.getModule(), module.getId(), user);
            // 创建模块下的文档
            List<DocItem> items = docModule.getItems();
            for (DocItem item : items) {
                DocItemCreateDTO docItemCreateDTO = this.buildDocItemCreateDTO(item, moduleDocInfo, user);
                DocInfo docItem = docInfoService.createDocItem(docItemCreateDTO);
                // 创建参数
                List<DocParameter> requestParameters = item.getRequestParameters();
                this.saveParams(requestParameters, docItem, docParameter -> {
                    String in = docParameter.getIn();
                    if (in == null) {
                        in = "request";
                    }
                    switch (in) {
                        case "path":
                            return ParamStyleEnum.PATH;
                        case "header":
                            return ParamStyleEnum.HEADER;
                        default:
                            return ParamStyleEnum.REQUEST;
                    }
                }, user);
                List<DocParameter> responseParameters = item.getResponseParameters();
                this.saveParams(responseParameters, docItem, p -> ParamStyleEnum.RESPONSE, user);
            }
        }
    }

    private void saveParams(
            List<DocParameter> parameters
            , DocInfo docItem
            , Function<DocParameter, ParamStyleEnum> styleEnumFunction
            , User user
    ) {
        if (CollectionUtils.isEmpty(parameters)) {
            return;
        }
        for (DocParameter parameter : parameters) {
            this.saveDocParam(parameter, docItem, 0, styleEnumFunction, user);
        }

    }

    private void saveDocParam(
             DocParameter docParameter
            , DocInfo docInfo
            , long parentId
            , Function<DocParameter, ParamStyleEnum> styleEnumFunction
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
        docParam.setCreateMode(user.getOperationModel());
        docParam.setModifyMode(user.getOperationModel());
        docParam.setCreatorId(user.getUserId());
        docParam.setModifierId(user.getUserId());
        // 保存操作
        DocParam savedDoc = docParamService.saveParam(docParam, user);

        // 处理子节点
        List<DocParameter> children = docParameter.getRefs();
        if (children != null) {
            for (DocParameter child : children) {
                this.saveDocParam(child, docInfo, savedDoc.getId(), styleEnumFunction, user);
            }
        }
    }


    private DocItemCreateDTO buildDocItemCreateDTO(DocItem item, DocInfo parent, User user) {
        DocItemCreateDTO docItemCreateDTO = new DocItemCreateDTO();
        docItemCreateDTO.setName(item.getSummary());
        docItemCreateDTO.setUrl(item.getPath());
        docItemCreateDTO.setContentType(Strings.join(item.getConsumes(), ','));
        docItemCreateDTO.setHttpMethod(item.getMethod());
        docItemCreateDTO.setDescription(item.getDescription());
        docItemCreateDTO.setModuleId(parent.getModuleId());
        docItemCreateDTO.setParentId(parent.getId());
        docItemCreateDTO.setUser(user);
        return docItemCreateDTO;
    }

}
