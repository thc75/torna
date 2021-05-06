package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.enums.DocTypeEnum;
import cn.torna.common.enums.OperationMode;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.enums.PropTypeEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.IdGen;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.DocParam;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.ModuleConfig;
import cn.torna.dao.mapper.DocInfoMapper;
import cn.torna.service.dto.DebugHostDTO;
import cn.torna.service.dto.DocFolderCreateDTO;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocItemCreateDTO;
import cn.torna.service.dto.DocParamDTO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.param.SchPageableParam;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
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
    private UserMessageService userMessageService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private PropService propService;

    public List<DocInfo> listDocMenu(long moduleId) {
        return list("module_id", moduleId);
    }

    public List<DocInfo> listDocMenuView(long moduleId) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("is_show", Booleans.TRUE);
        return listAll(query);
    }

    public PageEasyui<DocInfo> pageDocByIds(List<Long> docIds, SchPageableParam pageParam) {
        if (CollectionUtils.isEmpty(docIds)) {
            return new PageEasyui<>();
        }
        Query query = new Query()
                .in("id", docIds)
                .limit(pageParam.getStart(), pageParam.getLimit());
        return MapperUtil.queryForEasyuiDatagrid(this.getMapper(), query);
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

    public List<DocInfoDTO> listDocDetail(Collection<Long> docIdList) {
        if (CollectionUtils.isEmpty(docIdList)) {
            return Collections.emptyList();
        }
        Query query = new Query()
                .in("id", docIdList);
        List<DocInfo> docInfos = this.list(query);
        return docInfos.stream()
                .map(this::getDocDetail)
                .collect(Collectors.toList());
    }


    private DocInfoDTO getDocDetail(DocInfo docInfo) {
        Assert.notNull(docInfo, () -> "文档不存在");
        DocInfoDTO docInfoDTO = CopyUtil.copyBean(docInfo, DocInfoDTO::new);
        Long moduleId = docInfo.getModuleId();
        Module module = moduleService.getById(moduleId);
        docInfoDTO.setProjectId(module.getProjectId());
        docInfoDTO.setModuleType(module.getType());
        List<ModuleConfig> debugEnvs = moduleConfigService.listDebugHost(moduleId);
        docInfoDTO.setDebugEnvs(CopyUtil.copyList(debugEnvs, DebugHostDTO::new));
        List<DocParam> params = docParamService.list("doc_id", docInfo.getId());
        Map<Byte, List<DocParam>> paramsMap = params.stream()
                .collect(Collectors.groupingBy(DocParam::getStyle));
        List<DocParam> pathParams = paramsMap.getOrDefault(ParamStyleEnum.PATH.getStyle(), Collections.emptyList());
        List<DocParam> globalHeaders = moduleConfigService.listGlobalHeaders(moduleId);
        List<DocParam> globalParams = moduleConfigService.listGlobalParams(moduleId);
        List<DocParam> globalReturns = moduleConfigService.listGlobalReturns(moduleId);
        List<DocParam> headerParams = paramsMap.getOrDefault(ParamStyleEnum.HEADER.getStyle(), Collections.emptyList());
        List<DocParam> queryParams = paramsMap.getOrDefault(ParamStyleEnum.QUERY.getStyle(), Collections.emptyList());
        List<DocParam> requestParams = paramsMap.getOrDefault(ParamStyleEnum.REQUEST.getStyle(), Collections.emptyList());
        List<DocParam> responseParams = paramsMap.getOrDefault(ParamStyleEnum.RESPONSE.getStyle(), Collections.emptyList());
        List<DocParam> errorCodeParams = paramsMap.getOrDefault(ParamStyleEnum.ERROR_CODE.getStyle(), Collections.emptyList());

        docInfoDTO.setPathParams(CopyUtil.copyList(pathParams, DocParamDTO::new));
        docInfoDTO.setHeaderParams(CopyUtil.copyList(headerParams, DocParamDTO::new));
        docInfoDTO.setQueryParams(CopyUtil.copyList(queryParams, DocParamDTO::new));
        docInfoDTO.setRequestParams(CopyUtil.copyList(requestParams, DocParamDTO::new));
        docInfoDTO.setResponseParams(CopyUtil.copyList(responseParams, DocParamDTO::new));
        docInfoDTO.setErrorCodeParams(CopyUtil.copyList(errorCodeParams, DocParamDTO::new));

        docInfoDTO.setGlobalHeaders(CopyUtil.copyList(globalHeaders, DocParamDTO::new));
        docInfoDTO.setGlobalParams(CopyUtil.copyList(globalParams, DocParamDTO::new));
        docInfoDTO.setGlobalReturns(CopyUtil.copyList(globalReturns, DocParamDTO::new));

        return docInfoDTO;
    }

    /**
     * 保存文档信息
     * @param docInfoDTO 文档内容
     * @param user 用户
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized DocInfo saveDocInfo(DocInfoDTO docInfoDTO, User user) {
        return doSaveDocInfo(docInfoDTO, user);
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized DocInfo updateDocInfo(DocInfoDTO docInfoDTO, User user) {
        return doUpdateDocInfo(docInfoDTO, user);
    }

    public DocInfo doSaveDocInfo(DocInfoDTO docInfoDTO, User user) {
        // 修改基本信息
        DocInfo docInfo = this.saveBaseInfo(docInfoDTO, user);
        // 修改参数
        this.doUpdateParams(docInfo, docInfoDTO, user);
        return docInfo;
    }

    public DocInfo doUpdateDocInfo(DocInfoDTO docInfoDTO, User user) {
        // 修改基本信息
        DocInfo docInfo = this.modifyDocInfo(docInfoDTO, user);
        // 修改参数
        this.doUpdateParams(docInfo, docInfoDTO, user);
        return docInfo;
    }

    private void doUpdateParams(DocInfo docInfo, DocInfoDTO docInfoDTO, User user) {
        docParamService.saveParams(docInfo, docInfoDTO.getPathParams(), ParamStyleEnum.PATH, user);
        docParamService.saveParams(docInfo, docInfoDTO.getHeaderParams(), ParamStyleEnum.HEADER, user);
        docParamService.saveParams(docInfo, docInfoDTO.getQueryParams(), ParamStyleEnum.QUERY, user);
        docParamService.saveParams(docInfo, docInfoDTO.getRequestParams(), ParamStyleEnum.REQUEST, user);
        docParamService.saveParams(docInfo, docInfoDTO.getResponseParams(), ParamStyleEnum.RESPONSE, user);
        docParamService.saveParams(docInfo, docInfoDTO.getErrorCodeParams(), ParamStyleEnum.ERROR_CODE, user);
    }

    private DocInfo saveBaseInfo(DocInfoDTO docInfoDTO, User user) {
        DocInfo docInfo = this.insertDocInfo(docInfoDTO, user);
        // 发送站内信
        userMessageService.sendMessageByModifyDoc(docInfo);
        return docInfo;
    }

    private DocInfo insertDocInfo(DocInfoDTO docInfoDTO, User user) {
        DocInfo docInfo = buildDocInfo(docInfoDTO, user);
        this.getMapper().saveDocInfo(docInfo);
        return docInfo;
    }

    private DocInfo modifyDocInfo(DocInfoDTO docInfoDTO, User user) {
        DocInfo docInfo = buildDocInfo(docInfoDTO, user);
        this.update(docInfo);
        return docInfo;
    }

    private DocInfo buildDocInfo(DocInfoDTO docInfoDTO, User user) {
        Byte isFolder = docInfoDTO.getIsFolder();
        String dataId = docInfoDTO.buildDataId();
        DocInfo docInfo = new DocInfo();
        docInfo.setId(docInfoDTO.getId());
        docInfo.setDataId(dataId);
        docInfo.setName(docInfoDTO.getName());
        docInfo.setDescription(docInfoDTO.getDescription());
        docInfo.setType(docInfoDTO.getType());
        docInfo.setUrl(docInfoDTO.getUrl());
        docInfo.setHttpMethod(docInfoDTO.getHttpMethod());
        docInfo.setContentType(docInfoDTO.getContentType());
        docInfo.setIsFolder(isFolder);
        docInfo.setParentId(docInfoDTO.getParentId());
        docInfo.setModuleId(docInfoDTO.getModuleId());
        docInfo.setIsUseGlobalHeaders(docInfoDTO.getIsUseGlobalHeaders());
        docInfo.setIsUseGlobalParams(docInfoDTO.getIsUseGlobalParams());
        docInfo.setIsUseGlobalReturns(docInfoDTO.getIsUseGlobalReturns());
        docInfo.setCreateMode(user.getOperationModel());
        docInfo.setModifyMode(user.getOperationModel());
        docInfo.setCreatorId(user.getUserId());
        docInfo.setCreatorName(user.getNickname());
        docInfo.setModifierId(user.getUserId());
        docInfo.setModifierName(user.getNickname());
        docInfo.setRemark(docInfoDTO.getRemark());
        docInfo.setIsShow(docInfoDTO.getIsShow());
        docInfo.setIsDeleted(docInfoDTO.getIsDeleted());
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
        this.userMessageService.sendMessageByDeleteDoc(docInfo);
        // 设置一个dataId，不与其它文档冲突
        docInfo.setDataId(IdGen.nextId());
        this.update(docInfo);
    }

    /**
     * 创建文档分类，如果已存在，直接返回已存在的
     * @param folderName 分类名称
     * @param moduleId 模块id
     * @param user 操作人
     */
    public DocInfo createDocFolder(String folderName, long moduleId, User user) {
        return createDocFolder(folderName,  moduleId, user, 0L);
    }

    /**
     * 创建文档分类，不检查名称是否存在
     *
     * @param folderName 分类名称
     * @param moduleId   模块id
     * @param user       操作人
     * @param parentId   父节点id
     * @return 返回添加后的文档
     */
    public DocInfo createDocFolder(String folderName, long moduleId, User user, Long parentId) {
        DocFolderCreateDTO docFolderCreateDTO = new DocFolderCreateDTO();
        docFolderCreateDTO.setName(folderName);
        docFolderCreateDTO.setModuleId(moduleId);
        docFolderCreateDTO.setParentId(parentId);
        docFolderCreateDTO.setUser(user);
        docFolderCreateDTO.setDocTypeEnum(DocTypeEnum.HTTP);
        return createDocFolder(docFolderCreateDTO);
    }

    public DocInfo createDocFolder(DocFolderCreateDTO docFolderCreateDTO) {
        DocInfoDTO docInfoDTO = new DocInfoDTO();
        docInfoDTO.setName(docFolderCreateDTO.getName());
        docInfoDTO.setModuleId(docFolderCreateDTO.getModuleId());
        docInfoDTO.setParentId(docFolderCreateDTO.getParentId());
        if (docFolderCreateDTO.getDocTypeEnum() != null) {
            docInfoDTO.setType(docFolderCreateDTO.getDocTypeEnum().getType());
        }
        docInfoDTO.setIsFolder(Booleans.TRUE);
        DocInfo docInfo = insertDocInfo(docInfoDTO, docFolderCreateDTO.getUser());
        Map<String, ?> props = docFolderCreateDTO.getProps();
        propService.saveProps(props, docInfo.getId(), PropTypeEnum.DOC_INFO_PROP);
        return docInfo;
    }

    public DocInfo createDocItem(DocItemCreateDTO docItemCreateDTO) {
        User user = docItemCreateDTO.getUser();
        DocInfoDTO docInfoDTO = CopyUtil.copyBean(docItemCreateDTO, DocInfoDTO::new);
        docInfoDTO.setIsDeleted(Booleans.FALSE);
        return insertDocInfo(docInfoDTO, user);
    }

    /**
     * 删除模块下所有文档
     * @param moduleId 模块id
     * @param userId 用户id，只能删除自己创建的
     */
    public void deleteOpenAPIModuleDocs(long moduleId, long userId) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("create_mode", OperationMode.OPEN.getType())
                .eq("creator_id", userId);
        // 查询出文档id
        List<Long> idList = this.getMapper().listBySpecifiedColumns(Collections.singletonList("id"), query, Long.class);
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }
        // 删除文档
        Query delQuery = new Query()
                .in("id", idList);
        // DELETE FROM doc_info WHERE id in (..)
        this.getMapper().deleteByQuery(delQuery);

        // 删除文档对应的参数
        Query paramDelQuery = new Query()
                .in("doc_id", idList);
        // DELETE FROM doc_param WHERE doc_id in (..)
        docParamService.getMapper().deleteByQuery(paramDelQuery);
    }
}