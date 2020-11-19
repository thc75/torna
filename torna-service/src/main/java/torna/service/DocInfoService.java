package torna.service;

import torna.common.bean.Booleans;
import torna.common.bean.User;
import torna.common.context.DocConstants;
import torna.common.context.UserContext;
import torna.common.enums.ParamStyleEnum;
import torna.common.support.BaseService;
import torna.common.util.CopyUtil;
import torna.dao.entity.DocInfo;
import torna.dao.entity.DocParam;
import torna.dao.mapper.DocInfoMapper;
import torna.dao.mapper.DocParamMapper;
import torna.service.dto.DocFolderCreateDTO;
import torna.service.dto.DocInfoDTO;
import torna.service.dto.DocItemCreateDTO;
import torna.service.dto.DocParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class DocInfoService extends BaseService<DocInfo, DocInfoMapper> {

    @Autowired
    private DocParamMapper docParamMapper;

    /**
     * 返回文档详情
     * @param docId 文档id
     * @return 返回文档详情
     */
    public DocInfoDTO getDocDetail(long docId) {
        DocInfo docInfo = this.getById(docId);
        Assert.notNull(docInfo, () -> "文档不存在");
        DocInfoDTO docInfoDTO = CopyUtil.copyBean(docInfo, DocInfoDTO::new);
        List<DocParam> params = docParamMapper.listByColumn("doc_id", docId);
        Map<Byte, List<DocParam>> paramsMap = params.stream()
                .collect(Collectors.groupingBy(DocParam::getStyle));
        List<DocParam> headerParams = paramsMap.getOrDefault(ParamStyleEnum.HEADER.getStyle(), Collections.emptyList());
        List<DocParam> requestParams = paramsMap.getOrDefault(ParamStyleEnum.REQUEST.getStyle(), Collections.emptyList());
        List<DocParam> responseParams = paramsMap.getOrDefault(ParamStyleEnum.RESPONSE.getStyle(), Collections.emptyList());
        List<DocParam> errorCodeParams = paramsMap.getOrDefault(ParamStyleEnum.ERROR_CODE.getStyle(), Collections.emptyList());
        docInfoDTO.setHeaderParams(CopyUtil.copyList(headerParams, DocParamDTO::new));
        docInfoDTO.setRequestParams(CopyUtil.copyList(requestParams, DocParamDTO::new));
        docInfoDTO.setResponseParams(CopyUtil.copyList(responseParams, DocParamDTO::new));
        docInfoDTO.setErrorCodeParams(CopyUtil.copyList(errorCodeParams, DocParamDTO::new));
        return docInfoDTO;
    }


    public DocInfo getByUniqueId(String uniqueId) {
        return this.get("unique_id", uniqueId);
    }

    /**
     * md5(name+project_id+parent_id)
     * @param name
     * @param projectId
     * @param parentId
     * @return
     */
    private static String buildUniqueId(String name, long projectId, long parentId) {
        String content = String.format(DocConstants.UNIQUE_ID_TPL, name, projectId, parentId);
        return DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 创建文档目录
     * @param docFolderCreateDTO
     * @return DocInfo
     */
    public DocInfo createDocFolder(DocFolderCreateDTO docFolderCreateDTO) {
        User user = UserContext.getUser();
        String uniqueId = buildUniqueId(docFolderCreateDTO.getFolderName(), docFolderCreateDTO.getProjectId(), docFolderCreateDTO.getParentId());
        DocInfo folder = getByUniqueId(uniqueId);
        if (folder == null) {
            folder = new DocInfo();
            folder.setUniqueId(uniqueId);
            folder.setName(docFolderCreateDTO.getFolderName());
            folder.setProjectId(docFolderCreateDTO.getProjectId());
            folder.setParentId(docFolderCreateDTO.getParentId());
            folder.setCreatorId(user.getUserId());
            folder.setModifierId(user.getUserId());
            this.saveIgnoreNull(folder);
        } else {
            folder.setModifierId(user.getUserId());
            folder.setIsDeleted(Booleans.FALSE);
            this.updateIgnoreNull(folder);
        }
        return folder;
    }

    public DocInfo createDocFolder(String folderName, DocInfo parent) {
        DocFolderCreateDTO docFolderCreateDTO = new DocFolderCreateDTO(
                parent.getProjectId()
                , folderName
                , parent.getId()
        );
        return this.createDocFolder(docFolderCreateDTO);
    }

    public DocInfo createDocItem(DocItemCreateDTO docItemCreateDTO) {
        User user = UserContext.getUser();
        String uniqueId = buildUniqueId(docItemCreateDTO.getName(), docItemCreateDTO.getProjectId(), docItemCreateDTO.getParentId());
        DocInfo docInfo = getByUniqueId(uniqueId);
        if (docInfo == null) {
            docInfo = CopyUtil.copyBean(docItemCreateDTO, DocInfo::new);
            docInfo.setUniqueId(uniqueId);
            docInfo.setCreatorId(user.getUserId());
            docInfo.setModifierId(user.getUserId());
            saveIgnoreNull(docInfo);
        } else {
            CopyUtil.copyProperties(docItemCreateDTO, docInfo);
            docInfo.setModifierId(user.getUserId());
            docInfo.setIsDeleted(Booleans.FALSE);
            updateIgnoreNull(docInfo);
        }
        return docInfo;
    }
}