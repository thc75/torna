package torna.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import torna.common.bean.HttpTool;
import torna.common.bean.User;
import torna.common.context.UserContext;
import torna.common.enums.ParamStyleEnum;
import torna.common.exception.BizException;
import torna.dao.entity.DocInfo;
import torna.dao.entity.DocParam;
import torna.manager.doc.DocBean;
import torna.manager.doc.DocItem;
import torna.manager.doc.DocModule;
import torna.manager.doc.DocParameter;
import torna.manager.doc.DocParser;
import torna.service.dto.DocFolderCreateDTO;
import torna.service.dto.DocItemCreateDTO;
import torna.service.dto.ImportSwaggerDTO;
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

    /**
     * 导入swagger文档
     *
     * @param importSwaggerDTO importSwaggerDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void importSwagger(ImportSwaggerDTO importSwaggerDTO) {
        String json;
        String url = importSwaggerDTO.getUrl();
        try {
            HttpTool httpTool = new HttpTool(importSwaggerDTO.getBasicAuthUsername(), importSwaggerDTO.getBasicAuthPassword());
            Response response = httpTool.requestForResponse(url, null, null, HttpTool.HTTPMethod.GET);
            String body = response.body().string();
            if (response.code() != HttpStatus.OK.value()) {
                throw new BizException(body);
            }
            json = body;
        } catch (IOException e) {
            log.error("导入json异常, url:{}", url, e);
            throw new BizException("导入json异常, msg:" + e.getMessage());
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
        String title = docBean.getTitle();
        Long projectId = importSwaggerDTO.getProjectId();
        long parentId = 0;
        DocFolderCreateDTO docFolderCreateDTO = new DocFolderCreateDTO(
                projectId, title, parentId
        );
        // 创建根目录
        DocInfo rootFolder = docInfoService.createDocFolder(docFolderCreateDTO);
        // 创建模块
        List<DocModule> docModules = docBean.getDocModules();
        docModules.sort(Comparator.comparing(DocModule::getOrder));
        for (DocModule docModule : docModules) {
            DocInfo moduleDocInfo = docInfoService.createDocFolder(docModule.getModule(), rootFolder);
            // 创建模块下的文档
            List<DocItem> items = docModule.getItems();
            for (DocItem item : items) {
                DocItemCreateDTO docItemCreateDTO = this.buildDocItemCreateDTO(item, moduleDocInfo);
                DocInfo docItem = docInfoService.createDocItem(docItemCreateDTO);
                // 创建参数
                List<DocParameter> requestParameters = item.getRequestParameters();
                this.saveParams(requestParameters, docItem, docParameter -> {
                    if ("header".equals(docParameter.getIn())) {
                        return ParamStyleEnum.HEADER;
                    } else {
                        return ParamStyleEnum.REQUEST;
                    }
                });
                List<DocParameter> responseParameters = item.getResponseParameters();
                this.saveParams(responseParameters, docItem,p -> ParamStyleEnum.RESPONSE);
            }
        }
    }

    private void saveParams(List<DocParameter> parameters, DocInfo docItem, Function<DocParameter, ParamStyleEnum> styleEnumFunction) {
        if (CollectionUtils.isEmpty(parameters)) {
            return;
        }
        for (DocParameter parameter : parameters) {
            this.saveDocParam(parameter, docItem, 0, styleEnumFunction);
        }

    }

    private void saveDocParam(
             DocParameter docParameter
            , DocInfo docInfo
            , long parentId
            , Function<DocParameter, ParamStyleEnum> styleEnumFunction
    ) {
        User user = UserContext.getUser();
        DocParam docParam = new DocParam();
        String uniqueId = DocParamService.buildUniqueId(docParameter.getName(), docInfo.getId(), parentId);
        docParam.setUniqueId(uniqueId);
        docParam.setName(docParameter.getName());
        docParam.setType(docParameter.getType());
        docParam.setRequired(BooleanUtils.toIntegerObject(docParameter.getRequired()).byteValue());
        docParam.setMaxLength(docParameter.getMaxLength());
        docParam.setExample(docParameter.getExample());
        docParam.setDescription(docParameter.getDescription());
        List<String> enums = docParameter.getEnums();
        docParam.setEnumContent(enums == null ? "[]" : JSON.toJSONString(enums));
        docParam.setDocId(docInfo.getId());
        docParam.setParentId(parentId);
        ParamStyleEnum styleEnum = styleEnumFunction.apply(docParameter);
        docParam.setStyle(styleEnum.getStyle());
        docParam.setCreatorId(user.getUserId());
        docParam.setModifierId(user.getUserId());
        // 保存操作
        DocParam savedDoc = docParamService.saveDoc(docParam);

        // 处理子节点
        List<DocParameter> children = docParameter.getRefs();
        if (children != null) {
            for (DocParameter child : children) {
                this.saveDocParam(child, docInfo, savedDoc.getId(), styleEnumFunction);
            }
        }
    }


    private DocItemCreateDTO buildDocItemCreateDTO(DocItem item, DocInfo parent) {
        DocItemCreateDTO docItemCreateDTO = new DocItemCreateDTO();
        docItemCreateDTO.setName(item.getSummary());
        docItemCreateDTO.setUrl(item.getPath());
        docItemCreateDTO.setContentType(Strings.join(item.getProduces(), ','));
        docItemCreateDTO.setHttpMethod(item.getMethod());
        docItemCreateDTO.setDescription(item.getDescription());
        docItemCreateDTO.setProjectId(parent.getProjectId());
        docItemCreateDTO.setParentId(parent.getId());
        return docItemCreateDTO;
    }

}
