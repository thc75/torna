package torna.service;

import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import torna.common.bean.Booleans;
import torna.common.bean.User;
import torna.common.context.DocConstants;
import torna.common.enums.ParamStyleEnum;
import torna.common.exception.BizException;
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

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
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
        List<DocInfo> folders = this.listFolders(docInfo.getModuleId());
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
        docInfoDTO.setFolders(CopyUtil.copyList(folders, DocInfoDTO::new));
        return docInfoDTO;
    }

    public void saveDocInfo(DocInfoDTO docInfoDTO) {

    }

    /**
     * 查询模块下面所有分类
     * @param moduleId 模块id
     * @return 返回分类
     */
    public List<DocInfo> listFolders(long moduleId) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("is_folder", Booleans.TRUE);
        return this.listAll(query);
    }


    public DocInfo getByUniqueId(String uniqueId) {
        Query query = new Query()
                .eq("unique_id", uniqueId)
                .enableForceQuery();
        return this.get(query);
    }

    /**
     * md5(name:module_id:parent_id)
     * @param name
     * @param moduleId
     * @param parentId
     * @return
     */
    private static String buildUniqueId(String name, long moduleId, long parentId) {
        String content = String.format(DocConstants.UNIQUE_ID_TPL, name, moduleId, parentId);
        return DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

    public boolean isExistFolderForAdd(String folderName, long moduleId, long parentId) {
        return this.isExistFolder(folderName, moduleId, parentId, Objects::nonNull);
    }

    public boolean isExistFolderForUpdate(long id, String folderName, long moduleId, long parentId) {
        String uniqueId = buildUniqueId(folderName, moduleId, parentId);
        Query query = new Query()
                .eq("unique_id", uniqueId)
                .notEq("id", id);
        return get(query) != null;
    }

    /**
     * 分类是否存在
     * @param folderName 分类名称
     * @param moduleId 模块id
     * @param parentId 父节点
     * @return
     */
    public boolean isExistFolder(String folderName, long moduleId, long parentId, Predicate<DocInfo> predicate) {
        String uniqueId = buildUniqueId(folderName, moduleId, parentId);
        DocInfo folder = getByUniqueId(uniqueId);
        return predicate.test(folder);
    }

    /**
     * 修改分类名称
     *
     * @param id
     * @param name
     * @param user
     */
    public void updateDocFolderName(long id, String name, User user) {
        DocInfo folder = getById(id);
        Long moduleId = folder.getModuleId();
        if (isExistFolderForUpdate(id, name, moduleId, 0)) {
            throw new BizException(name + " 已存在");
        }
        String uniqueId = buildUniqueId(name, moduleId, folder.getParentId());
        folder.setUniqueId(uniqueId);
        folder.setName(name);
        folder.setModifyMode(user.getOperationModel());
        folder.setModifierId(user.getUserId());
        folder.setIsDeleted(Booleans.FALSE);
        this.updateIgnoreNull(folder);
    }

    /**
     * 删除文档
     * @param id 文档注解
     * @param user 用户
     */
    public void deleteDocInfo(long id, User user) {
        DocInfo docInfo = getById(id);
        docInfo.setModifyMode(user.getOperationModel());
        docInfo.setModifierId(user.getUserId());
        docInfo.setIsDeleted(Booleans.TRUE);
        this.updateIgnoreNull(docInfo);
    }

    /**
     * 创建文档分类，会检查名称是否存在
     * @param folderName 分类名称
     * @param moduleId 模块id
     * @param user 操作人
     */
    public void createDocFolder(String folderName, long moduleId, User user) {
        if (isExistFolderForAdd(folderName, moduleId, 0)) {
            throw new BizException(folderName + " 已存在");
        }
        this.createDocFolderNoCheck(folderName, moduleId, user);
    }

    /**
     * 创建文档分类，不检查名称是否存在
     * @param folderName 分类名称
     * @param moduleId 模块id
     * @param user 操作人
     * @return 返回添加后的文档
     */
    public DocInfo createDocFolderNoCheck(String folderName, long moduleId, User user) {
        DocFolderCreateDTO docFolderCreateDTO = new DocFolderCreateDTO();
        docFolderCreateDTO.setName(folderName);
        docFolderCreateDTO.setModuleId(moduleId);
        docFolderCreateDTO.setParentId(0L);
        docFolderCreateDTO.setUser(user);
        return this.createDocFolder(docFolderCreateDTO);
    }

    /**
     * 创建文档目录
     * @param docFolderCreateDTO 分类信息
     * @return DocInfo
     */
    public DocInfo createDocFolder(DocFolderCreateDTO docFolderCreateDTO) {
        User user = docFolderCreateDTO.getUser();
        DocItemCreateDTO docItemCreateDTO = new DocItemCreateDTO();
        docItemCreateDTO.setModuleId(docFolderCreateDTO.getModuleId());
        docItemCreateDTO.setName(docFolderCreateDTO.getName());
        docItemCreateDTO.setParentId(docFolderCreateDTO.getParentId());
        docItemCreateDTO.setUser(user);
        docItemCreateDTO.setIsFolder(Booleans.TRUE);
        return this.createDocItem(docItemCreateDTO);
    }

    public DocInfo createDocItem(DocItemCreateDTO docItemCreateDTO) {
        User user = docItemCreateDTO.getUser();
        Byte isFolder = docItemCreateDTO.getIsFolder();
        String uniqueId = buildUniqueId(docItemCreateDTO.getName(), docItemCreateDTO.getModuleId(), docItemCreateDTO.getParentId());
        DocInfo docInfo = getByUniqueId(uniqueId);
        if (docInfo == null) {
            docInfo = new DocInfo();
            docInfo.setUniqueId(uniqueId);
            docInfo.setName(docItemCreateDTO.getName());
            docInfo.setModuleId(docItemCreateDTO.getModuleId());
            docInfo.setParentId(docItemCreateDTO.getParentId());
            docInfo.setIsFolder(isFolder);
            docInfo.setCreateMode(user.getOperationModel());
            docInfo.setModifyMode(user.getOperationModel());
            docInfo.setCreatorId(user.getUserId());
            docInfo.setModifierId(user.getUserId());
            saveIgnoreNull(docInfo);
        } else {
            docInfo.setIsFolder(isFolder);
            docInfo.setModifyMode(user.getOperationModel());
            docInfo.setModifierId(user.getUserId());
            docInfo.setIsDeleted(Booleans.FALSE);
            updateIgnoreNull(docInfo);
        }
        return docInfo;
    }
}