package torna.service;

import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import torna.common.bean.Booleans;
import torna.common.bean.User;
import torna.common.enums.ParamStyleEnum;
import torna.common.exception.BizException;
import torna.common.support.BaseService;
import torna.common.util.CopyUtil;
import torna.common.util.ThreadPoolUtil;
import torna.dao.entity.DocInfo;
import torna.dao.entity.DocParam;
import torna.dao.entity.ModuleConfig;
import torna.dao.mapper.DocInfoMapper;
import torna.service.dto.DocFolderCreateDTO;
import torna.service.dto.DocInfoDTO;
import torna.service.dto.DocItemCreateDTO;
import torna.service.dto.DocParamDTO;

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
    private DocParamService docParamService;

    @Autowired
    private ModuleConfigService moduleConfigService;

    @Autowired
    private DocSnapshotService docSnapshotService;

    public List<DocInfo> listDocMenu(long moduleId) {
        return list("module_id", moduleId);
    }

    public List<DocInfo> listDocMenuView(long moduleId) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("is_show", Booleans.TRUE);
        return listAll(query);
    }

    /**
     * 返回文档详情
     * @param docId 文档id
     * @return 返回文档详情
     */
    public DocInfoDTO getDocDetailView(long docId) {
        Query query = new Query()
                .eq("id", docId)
                .eq("is_show", Booleans.TRUE);
        DocInfo docInfo = get(query);
        return getDocDetail(docInfo);
    }

    /**
     * 返回文档详情
     * @param docId 文档id
     * @return 返回文档详情
     */
    public DocInfoDTO getDocDetail(long docId) {
        DocInfo docInfo = this.getById(docId);
        return getDocDetail(docInfo);
    }

    /**
     * 返回模块下所有文档详情
     * @param moduleId 模块id
     * @return 返回文档详情
     */
    public List<DocInfoDTO> listDocDetail(long moduleId) {
        List<DocInfo> docInfos = this.list("module_id", moduleId);
        return docInfos.stream()
                .map(this::getDocDetail)
                .collect(Collectors.toList());
    }


    private DocInfoDTO getDocDetail(DocInfo docInfo) {
        Assert.notNull(docInfo, () -> "文档不存在");
        DocInfoDTO docInfoDTO = CopyUtil.copyBean(docInfo, DocInfoDTO::new);
        String baseUrl = moduleConfigService.getBaseUrl(docInfo.getModuleId());
        docInfoDTO.setBaseUrl(baseUrl);
        List<DocParam> params = docParamService.list("doc_id", docInfo.getId());
        Map<Byte, List<DocParam>> paramsMap = params.stream()
                .collect(Collectors.groupingBy(DocParam::getStyle));
        List<DocParam> pathParams = paramsMap.getOrDefault(ParamStyleEnum.PATH.getStyle(), Collections.emptyList());
        List<DocParam> globalHeaders = this.listGlobalHeaders(docInfo.getModuleId());
        List<DocParam> headerParams = paramsMap.getOrDefault(ParamStyleEnum.HEADER.getStyle(), Collections.emptyList());
        List<DocParam> requestParams = paramsMap.getOrDefault(ParamStyleEnum.REQUEST.getStyle(), Collections.emptyList());
        List<DocParam> responseParams = paramsMap.getOrDefault(ParamStyleEnum.RESPONSE.getStyle(), Collections.emptyList());
        List<DocParam> errorCodeParams = paramsMap.getOrDefault(ParamStyleEnum.ERROR_CODE.getStyle(), Collections.emptyList());

        docInfoDTO.setPathParams(CopyUtil.copyList(pathParams, DocParamDTO::new));
        docInfoDTO.setGlobalHeaderParams(CopyUtil.copyList(globalHeaders, DocParamDTO::new));
        docInfoDTO.setHeaderParams(CopyUtil.copyList(headerParams, DocParamDTO::new));
        docInfoDTO.setRequestParams(CopyUtil.copyList(requestParams, DocParamDTO::new));
        docInfoDTO.setResponseParams(CopyUtil.copyList(responseParams, DocParamDTO::new));
        docInfoDTO.setErrorCodeParams(CopyUtil.copyList(errorCodeParams, DocParamDTO::new));
        return docInfoDTO;
    }

    private List<DocParam> listGlobalHeaders(long moduleId) {
        List<ModuleConfig> globalHeaders = moduleConfigService.listGlobalHeaders(moduleId);
        return globalHeaders.stream()
                .map(moduleConfig -> {
                    DocParam docParam = new DocParam();
                    docParam.setName(moduleConfig.getConfigKey());
                    docParam.setExample(moduleConfig.getConfigValue());
                    docParam.setDescription(moduleConfig.getDescription());
                    docParam.setStyle(ParamStyleEnum.HEADER.getStyle());
                    return docParam;
                })
                .collect(Collectors.toList());
    }


    /**
     * 保存文档信息
     * @param docInfoDTO 文档内容
     * @param user 用户
     */
    @Transactional(rollbackFor = Exception.class)
    public DocInfo saveDocInfo(DocInfoDTO docInfoDTO, User user) {
        // 保存上一次的快照
        this.saveOldSnapshot(docInfoDTO);
        // 修改基本信息
        DocInfo docInfo = this.saveBaseInfo(docInfoDTO, user);
        // 修改参数
        docParamService.saveParams(docInfo, docInfoDTO.getPathParams(), ParamStyleEnum.PATH, user);
        docParamService.saveParams(docInfo, docInfoDTO.getHeaderParams(), ParamStyleEnum.HEADER, user);
        docParamService.saveParams(docInfo, docInfoDTO.getRequestParams(), ParamStyleEnum.REQUEST, user);
        docParamService.saveParams(docInfo, docInfoDTO.getResponseParams(), ParamStyleEnum.RESPONSE, user);
        docParamService.saveParams(docInfo, docInfoDTO.getErrorCodeParams(), ParamStyleEnum.ERROR_CODE, user);
        return docInfo;
    }

    private void saveOldSnapshot(DocInfoDTO docInfoDTO) {
        DocInfo docInfo;
        Long id = docInfoDTO.getId();
        String dataId = docInfoDTO.buildDataId();
        if (id != null) {
            docInfo = getById(id);
        } else {
            docInfo = getByDataId(dataId);
        }
        if (docInfo == null) {
            return;
        }
        ThreadPoolUtil.execute(() -> {
            DocInfoDTO detail = this.getDocDetail(docInfo);
            docSnapshotService.saveDocSnapshot(detail);
        });
    }

    private DocInfo saveBaseInfo(DocInfoDTO docInfoDTO, User user) {
        DocInfo docInfo;
        Long id = docInfoDTO.getId();
        String dataId = docInfoDTO.buildDataId();
        if (id != null) {
            docInfo = getById(id);
        } else {
            docInfo = getByDataId(dataId);
        }
        boolean save = false;
        if (docInfo == null) {
            docInfo = new DocInfo();
            docInfo.setCreateMode(user.getOperationModel());
            docInfo.setCreatorId(user.getUserId());
            docInfo.setCreatorName(user.getNickname());
            save = true;
        }
        docInfo.setDataId(dataId);
        docInfo.setName(docInfoDTO.getName());
        docInfo.setDescription(docInfoDTO.getDescription());
        docInfo.setUrl(docInfoDTO.getUrl());
        docInfo.setHttpMethod(docInfoDTO.getHttpMethod());
        docInfo.setContentType(docInfoDTO.getContentType());
        docInfo.setIsFolder(Booleans.FALSE);
        docInfo.setParentId(docInfoDTO.getParentId());
        docInfo.setModuleId(docInfoDTO.getModuleId());
        docInfo.setModifyMode(user.getOperationModel());
        docInfo.setModifierId(user.getUserId());
        docInfo.setModifierName(user.getNickname());
        docInfo.setIsShow(docInfoDTO.getIsShow());
        if (save) {
            this.save(docInfo);
        } else {
            this.update(docInfo);
        }
        return docInfo;
    }

    public DocInfo getByDataId(String dataId) {
        return get("data_id", dataId);
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


    public boolean isExistFolderForUpdate(long id, String folderName, long moduleId, long parentId) {
        DocInfo docInfo = getByModuleIdAndParentIdAndName(moduleId, parentId, folderName);
        return docInfo != null && docInfo.getId() != id;
    }

    public DocInfo getByModuleIdAndParentIdAndName(long moduleId, long parentId, String name) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("parent_id", parentId)
                .eq("name", name);
        return get(query);
    }

    /**
     * 修改分类名称
     *
     * @param id 文档id
     * @param name 文档名称
     * @param user 操作人
     */
    public void updateDocFolderName(long id, String name, User user) {
        DocInfo folder = getById(id);
        Assert.notNull(folder, name + " 分类不存在");
        Long moduleId = folder.getModuleId();
        if (isExistFolderForUpdate(id, name, moduleId, 0)) {
            throw new BizException(name + " 已存在");
        }
        folder.setName(name);
        folder.setModifyMode(user.getOperationModel());
        folder.setModifierId(user.getUserId());
        folder.setIsDeleted(Booleans.FALSE);
        this.update(folder);
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
        this.update(docInfo);
    }

    /**
     * 创建文档分类，如果已存在，直接返回已存在的
     * @param folderName 分类名称
     * @param moduleId 模块id
     * @param user 操作人
     */
    public DocInfo createDocFolder(String folderName, long moduleId, User user) {
        DocInfo folder = getByModuleIdAndParentIdAndName(moduleId, 0L, folderName);
        if (folder != null) {
            return folder;
        }
        return this.createDocFolderNoCheck(folderName, moduleId, user);
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
        String dataId = docItemCreateDTO.buildDataId();
        DocInfo docInfo = getByDataId(dataId);
        if (docInfo == null) {
            docInfo = new DocInfo();
            docInfo.setDataId(dataId);
            docInfo.setUrl(docItemCreateDTO.getUrl());
            docInfo.setHttpMethod(docItemCreateDTO.getHttpMethod());
            docInfo.setContentType(docItemCreateDTO.getContentType());
            docInfo.setName(docItemCreateDTO.getName());
            docInfo.setModuleId(docItemCreateDTO.getModuleId());
            docInfo.setParentId(docItemCreateDTO.getParentId());
            docInfo.setIsFolder(isFolder);
            docInfo.setCreateMode(user.getOperationModel());
            docInfo.setModifyMode(user.getOperationModel());
            docInfo.setCreatorId(user.getUserId());
            docInfo.setModifierId(user.getUserId());
            save(docInfo);
        } else {
            docInfo.setName(docItemCreateDTO.getName());
            docInfo.setContentType(docItemCreateDTO.getContentType());
            docInfo.setIsFolder(isFolder);
            docInfo.setDataId(dataId);
            docInfo.setModifyMode(user.getOperationModel());
            docInfo.setModifierId(user.getUserId());
            docInfo.setIsDeleted(Booleans.FALSE);
            update(docInfo);
        }
        return docInfo;
    }
}