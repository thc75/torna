package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.User;
import cn.torna.common.enums.DocSortType;
import cn.torna.common.enums.DocTypeEnum;
import cn.torna.common.enums.OperationMode;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.enums.PropTypeEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.IdGen;
import cn.torna.common.util.Markdown2HtmlUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.DocParam;
import cn.torna.dao.entity.EnumInfo;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.ModuleEnvironment;
import cn.torna.dao.entity.ModuleEnvironmentParam;
import cn.torna.dao.mapper.DocInfoMapper;
import cn.torna.manager.doc.DataType;
import cn.torna.service.dto.DocFolderCreateDTO;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocItemCreateDTO;
import cn.torna.service.dto.DocMeta;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.service.dto.DubboInfoDTO;
import cn.torna.service.dto.EnumInfoDTO;
import cn.torna.service.dto.EnumItemDTO;
import cn.torna.service.dto.ModuleEnvironmentDTO;
import cn.torna.service.dto.UpdateDocFolderDTO;
import cn.torna.service.login.NotNullStringBuilder;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import com.gitee.fastmybatis.core.query.param.PageParam;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class DocInfoService extends BaseService<DocInfo, DocInfoMapper> {

    private static final String REGEX_BR = "<br\\s*/*>";

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

    @Autowired
    private ModuleEnvironmentService moduleEnvironmentService;

    @Autowired
    private ModuleEnvironmentParamService moduleEnvironmentParamService;

    @Autowired
    private EnumInfoService enumInfoService;

    @Autowired
    private EnumService enumService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PushIgnoreFieldService pushIgnoreFieldService;

    /**
     * 查询模块下的所有文档
     * @param moduleId 模块id
     * @return 返回文档
     */
    public List<DocInfo> listModuleDoc(long moduleId) {
        List<DocInfo> docInfoList = list("module_id", moduleId);
        sortDocInfo(docInfoList);
        return docInfoList;
    }

    public static void sortDocInfo(List<DocInfo> docInfoList) {
        if (CollectionUtils.isEmpty(docInfoList)) {
            return;
        }
        String value = EnvironmentKeys.TORNA_DOC_SORT_TYPE.getValue();
        Comparator<DocInfo> comparator;
        switch (DocSortType.of(value)) {
            case BY_URL:
                comparator = Comparator.comparing(DocInfo::getUrl).thenComparing(DocInfo::getOrderIndex);
                break;
            case BY_NAME:
                comparator = Comparator.comparing(DocInfo::getName).thenComparing(DocInfo::getOrderIndex);
                break;
            default:{
                comparator = Comparator.comparing(DocInfo::getOrderIndex);
            }
        }
        docInfoList.sort(comparator);
    }

    public List<DocInfo> listDocMenuView(long moduleId) {
        return this.listModuleDoc(moduleId)
                .stream()
                .filter(docInfo -> docInfo.getIsShow() == Booleans.TRUE)
                .collect(Collectors.toList());
    }

    public PageEasyui<DocInfo> pageDocByIds(List<Long> docIds, PageParam pageParam) {
        if (CollectionUtils.isEmpty(docIds)) {
            return new PageEasyui<>();
        }
        Query query = pageParam.toQuery()
                .in("id", docIds)
                .orderby("order_index", Sort.ASC);
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
     * 返回文档详情
     * @param docId 文档id
     * @return 返回文档详情
     */
    public DocInfoDTO getDocForm(long docId) {
        DocInfo docInfo = this.getById(docId);
        return getDocInfoDTO(docInfo);
    }

    private DocInfoDTO getDocInfoDTO(DocInfo docInfo) {
        Assert.notNull(docInfo, () -> "文档不存在");
        DocInfoDTO docInfoDTO = CopyUtil.copyBean(docInfo, DocInfoDTO::new);
        Long moduleId = docInfo.getModuleId();
        Module module = moduleService.getById(moduleId);
        docInfoDTO.setSpaceId(projectService.getSpaceId(module.getProjectId()));
        docInfoDTO.setProjectId(module.getProjectId());
        docInfoDTO.setModuleType(module.getType());
        List<ModuleEnvironment> debugEnvs = moduleEnvironmentService.listModuleEnvironment(moduleId);
        docInfoDTO.setDebugEnvs(CopyUtil.copyList(debugEnvs, ModuleEnvironmentDTO::new));
        List<DocParam> params = docParamService.list("doc_id", docInfo.getId());
        params.sort(Comparator.comparing(DocParam::getOrderIndex));
        Map<Byte, List<DocParam>> paramsMap = params.stream()
                .collect(Collectors.groupingBy(DocParam::getStyle));
        List<DocParam> pathParams = paramsMap.getOrDefault(ParamStyleEnum.PATH.getStyle(), Collections.emptyList());
        List<DocParam> headerParams = paramsMap.getOrDefault(ParamStyleEnum.HEADER.getStyle(), Collections.emptyList());
        List<DocParam> queryParams = paramsMap.getOrDefault(ParamStyleEnum.QUERY.getStyle(), Collections.emptyList());
        List<DocParam> requestParams = paramsMap.getOrDefault(ParamStyleEnum.REQUEST.getStyle(), Collections.emptyList());
        List<DocParam> responseParams = paramsMap.getOrDefault(ParamStyleEnum.RESPONSE.getStyle(), Collections.emptyList());
        List<DocParam> errorCodeParams = paramsMap.getOrDefault(ParamStyleEnum.ERROR_CODE.getStyle(), new ArrayList<>(0));
        docInfoDTO.setPathParams(CopyUtil.copyList(pathParams, DocParamDTO::new));
        docInfoDTO.setHeaderParams(CopyUtil.copyList(headerParams, DocParamDTO::new));
        docInfoDTO.setQueryParams(CopyUtil.copyList(queryParams, DocParamDTO::new));
        docInfoDTO.setRequestParams(CopyUtil.copyList(requestParams, DocParamDTO::new));
        docInfoDTO.setResponseParams(CopyUtil.copyList(responseParams, DocParamDTO::new));
        docInfoDTO.setErrorCodeParams(CopyUtil.copyList(errorCodeParams, DocParamDTO::new));
        // 绑定枚举信息
        bindEnumInfo(docInfoDTO.getQueryParams());
        bindEnumInfo(docInfoDTO.getRequestParams());
        DubboInfoDTO dubboInfoDTO = buildDubboInfoDTO(docInfo);
        docInfoDTO.setDubboInfo(dubboInfoDTO);
        return docInfoDTO;
    }

    private DubboInfoDTO buildDubboInfoDTO(DocInfo docInfo) {
        if (docInfo.getType() == DocTypeEnum.DUBBO.getType()) {
            Map<String, String> docProps = propService.getDocProps(docInfo.getParentId());
            DubboInfoDTO dubboInfoDTO = new DubboInfoDTO();
            dubboInfoDTO.setProtocol(docProps.get("protocol"));
            dubboInfoDTO.setDependency(docProps.get("dependency"));
            dubboInfoDTO.setAuthor(docProps.get("author"));
            dubboInfoDTO.setInterfaceName(docProps.get("interfaceName"));
            return dubboInfoDTO;
        }
        return null;
    }

    /**
     * 绑定枚举信息
     * @param docParamDTOS
     */
    private void bindEnumInfo(List<DocParamDTO> docParamDTOS) {
        for (DocParamDTO docParamDTO : docParamDTOS) {
            Long enumId = docParamDTO.getEnumId();
            if (enumId != null && enumId > 0) {
                EnumInfo enumInfo = enumInfoService.getById(enumId);
                if (enumInfo == null) {
                    continue;
                }
                EnumInfoDTO enumInfoDTO = CopyUtil.copyBean(enumInfo, EnumInfoDTO::new);
                List<EnumItemDTO> enumItemDTOS = enumService.listItems(enumId);
                enumInfoDTO.setItems(enumItemDTOS);
                docParamDTO.setEnumInfo(enumInfoDTO);
            } else if (DataType.ENUM.equalsIgnoreCase(docParamDTO.getType()) && StringUtils.hasText(docParamDTO.getDescription())) {
                String description = docParamDTO.getDescription();
                EnumInfoDTO enumInfoDTO = new EnumInfoDTO();
                String[] arr;
                if (description.contains("<br")) {
                    arr = description.split(REGEX_BR);
                } else if (description.contains("、")) {
                    arr = description.split("、");
                } else {
                    arr = new String[]{description};
                }
                List<EnumItemDTO> items = Arrays.stream(arr)
                        .map(val -> {
                            EnumItemDTO enumItemDTO = new EnumItemDTO();
                            enumItemDTO.setName(val);
                            enumItemDTO.setValue(val);
                            return enumItemDTO;
                        })
                        .collect(Collectors.toList());
                enumInfoDTO.setItems(items);
                docParamDTO.setEnumInfo(enumInfoDTO);
            }

        }
    }


    public List<DocInfoDTO> listDocDetail(Collection<Long> docIdList) {
        if (CollectionUtils.isEmpty(docIdList)) {
            return Collections.emptyList();
        }
        List<DocInfo> docInfos = this.listDocByIds(docIdList);
        return docInfos.stream()
                .map(this::getDocDetail)
                .collect(Collectors.toList());
    }

    public List<DocInfo> listDocByIds(Collection<Long> docIdList) {
        if (CollectionUtils.isEmpty(docIdList)) {
            return Collections.emptyList();
        }
        Query query = new Query()
                .in("id", docIdList);
        List<DocInfo> list = this.list(query);
        sortDocInfo(list);
        return list;
    }


    private DocInfoDTO getDocDetail(DocInfo docInfo) {
        DocInfoDTO docInfoDTO = this.getDocInfoDTO(docInfo);
        Long moduleId = docInfoDTO.getModuleId();
        List<DocParam> globalHeaders = moduleConfigService.listGlobalHeaders(moduleId);
        List<DocParam> globalParams = moduleConfigService.listGlobalParams(moduleId);
        List<DocParam> globalReturns = moduleConfigService.listGlobalReturns(moduleId);
        docInfoDTO.setGlobalHeaders(CopyUtil.copyList(globalHeaders, DocParamDTO::new));
        docInfoDTO.setGlobalParams(CopyUtil.copyList(globalParams, DocParamDTO::new));
        docInfoDTO.setGlobalReturns(CopyUtil.copyList(globalReturns, DocParamDTO::new));
        docInfoDTO.getGlobalHeaders().forEach(docParamDTO -> docParamDTO.setGlobal(true));
        return docInfoDTO;
    }

    /**
     * 查询模块全局错误码
     * @param moduleId 模块id
     * @return 返回全局错误码
     */
    private List<DocParam> listCommonErrorCodes(long moduleId) {
        return moduleConfigService.listCommonErrorCodes(moduleId);
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

    public DocInfo doPushSaveDocInfo(DocInfoDTO docInfoDTO, User user, boolean isOverride) {
        // 修改基本信息
        DocInfo docInfo = this.insertDocInfo(docInfoDTO, user);
        if (isOverride) {
            // 删除文档对应的参数
            docParamService.deletePushParam(Collections.singletonList(docInfo.getId()));
        }
        // 修改参数
        this.doUpdateParams(docInfo, docInfoDTO, user);
        return docInfo;
    }

    public List<DocMeta> listDocMeta(long moduleId) {
        Query query = new Query().eq("module_id", moduleId);
        return this.getMapper().listBySpecifiedColumns(Arrays.asList("data_id", "is_locked", "md5"), query, DocMeta.class);
    }

    public static boolean isLocked(String dataId, List<DocMeta> docMetas) {
        if (CollectionUtils.isEmpty(docMetas)) {
            return false;
        }
        for (DocMeta docMeta : docMetas) {
            if (Objects.equals(dataId, docMeta.getDataId()) && docMeta.getIsLocked() == Booleans.TRUE) {
                return true;
            }
        }
        return false;
    }

    public static boolean isContentChanged(String dataId, String newMd5, List<DocMeta> docMetas) {
        if (CollectionUtils.isEmpty(docMetas)) {
            return false;
        }
        for (DocMeta docMeta : docMetas) {
            // 为空的不校验
            if (StringUtils.isEmpty(docMeta.getMd5())) {
                continue;
            }
            if (Objects.equals(dataId, docMeta.getDataId()) && !Objects.equals(newMd5, docMeta.getMd5())) {
                return true;
            }
        }
        return false;
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
        return this.insertDocInfo(docInfoDTO, user);
    }

    private DocInfo insertDocInfo(DocInfoDTO docInfoDTO, User user) {
        DocInfo docInfo = buildDocInfo(docInfoDTO, user);
        this.getMapper().saveDocInfo(docInfo);
        return docInfo;
    }

    private DocInfo modifyDocInfo(DocInfoDTO docInfoDTO, User user) {
        DocInfo docInfo = this.getById(docInfoDTO.getId());
        String descriptionOld = docInfo.getDescription();
        String descriptionNew = docInfoDTO.getDescription();
        String oldMd5 = docInfo.getMd5();
        String newMd5 = getDocMd5(docInfoDTO);
        CopyUtil.copyPropertiesIgnoreNull(docInfoDTO, docInfo);
        docInfo.setMd5(newMd5);
        // 手动赋值
        docInfo.setCreateMode(user.getOperationModel());
        docInfo.setModifyMode(user.getOperationModel());
        docInfo.setCreatorId(user.getUserId());
        docInfo.setCreatorName(user.getNickname());
        docInfo.setModifierId(user.getUserId());
        docInfo.setModifierName(user.getNickname());
        docInfo.setDataId(docInfoDTO.buildDataId());
        if (docInfo.getDescription() == null) {
            docInfo.setDescription("");
        }
        if (docInfo.getDeprecated() == null) {
            docInfo.setDeprecated("$false$");
        }
        this.update(docInfo);
        if (StringUtils.hasText(oldMd5) && !Objects.equals(oldMd5, newMd5)) {
            // 发送站内信
            userMessageService.sendMessageByModifyDoc(docInfo);
        }
        // 如果描述内容不一样，以手动修改的为准
        if (!Objects.equals(descriptionNew, descriptionOld)) {
            pushIgnoreFieldService.addField(docInfo, "description", "文档信息.描述");
        }
        return docInfo;
    }

    public static String getDocMd5(DocInfoDTO docInfoDTO) {
        NotNullStringBuilder content = new NotNullStringBuilder()
                .append(docInfoDTO.getName())
                .append(docInfoDTO.getDescription())
                .append(docInfoDTO.getAuthor())
                .append(docInfoDTO.getUrl())
                .append(docInfoDTO.getHttpMethod())
                .append(docInfoDTO.getParentId())
                .append(docInfoDTO.getModuleId())
                .append(docInfoDTO.getProjectId())
                .append(docInfoDTO.getIsUseGlobalHeaders())
                .append(docInfoDTO.getIsUseGlobalParams())
                .append(docInfoDTO.getIsUseGlobalReturns())
                .append(docInfoDTO.getIsRequestArray())
                .append(docInfoDTO.getIsResponseArray())
                .append(docInfoDTO.getRemark())
                .append(getDocParamsMd5(docInfoDTO))
                ;
        return DigestUtils.md5Hex(content.toString());
    }

    private static String getDocParamsMd5(DocInfoDTO docInfoDTO) {
        StringBuilder content = new StringBuilder()
                .append(getParamsContent(docInfoDTO.getPathParams()))
                .append(getParamsContent(docInfoDTO.getHeaderParams()))
                .append(getParamsContent(docInfoDTO.getQueryParams()))
                .append(getParamsContent(docInfoDTO.getRequestParams()))
                .append(getParamsContent(docInfoDTO.getResponseParams()))
                .append(getParamsContent(docInfoDTO.getErrorCodeParams()))
                ;
        return DigestUtils.md5Hex(content.toString());
    }

    private static String getParamsContent(List<DocParamDTO> docParamDTOS) {
        if (CollectionUtils.isEmpty(docParamDTOS)) {
            return "";
        }
        return docParamDTOS.stream()
                .filter(docParamDTO -> Objects.equals(docParamDTO.getIsDeleted(), Booleans.FALSE))
                .map(docParamDTO -> {
                    NotNullStringBuilder stringBuilder = new NotNullStringBuilder()
                            .append(docParamDTO.getName())
                            .append(docParamDTO.getType())
                            .append(docParamDTO.getRequired())
                            .append(docParamDTO.getExample())
                            .append(docParamDTO.getDescription())
                            .append(docParamDTO.getEnumId())
                            .append(docParamDTO.getIsDeleted())
                            .append(getParamsContent(docParamDTO.getChildren()))
                            ;
                    return stringBuilder.toString();
                })
                .collect(Collectors.joining());
    }

    private DocInfo buildDocInfo(DocInfoDTO docInfoDTO, User user) {
        DocInfo docInfo = CopyUtil.copyBean(docInfoDTO, DocInfo::new);
        // 手动赋值
        docInfo.setCreateMode(user.getOperationModel());
        docInfo.setModifyMode(user.getOperationModel());
        docInfo.setCreatorId(user.getUserId());
        docInfo.setCreatorName(user.getNickname());
        docInfo.setModifierId(user.getUserId());
        docInfo.setModifierName(user.getNickname());
        docInfo.setDataId(docInfoDTO.buildDataId());
        if (docInfo.getDescription() == null) {
            docInfo.setDescription("");
        }
        if (docInfo.getDeprecated() == null) {
            docInfo.setDeprecated("$false$");
        }
        // 描述字段忽略
        if (pushIgnoreFieldService.isPushIgnore(docInfoDTO.getModuleId(), docInfoDTO.buildDataId(), "description")) {
            docInfo.setDescription(null);
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
        return this.listModuleDoc(moduleId)
                .stream()
                .filter(docInfo -> docInfo.getIsFolder() == Booleans.TRUE)
                .collect(Collectors.toList());
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
     * @param updateDocFolderDTO
     */
    public void updateDocFolderName(UpdateDocFolderDTO updateDocFolderDTO) {
        Long id = updateDocFolderDTO.getId();
        String name = updateDocFolderDTO.getName();
        User user = updateDocFolderDTO.getUser();
        Long parentId = updateDocFolderDTO.getParentId();
        if (parentId == null) {
            parentId = 0L;
        }

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
        folder.setParentId(parentId);
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
        if (parentId == null) {
            parentId = 0L;
        }
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
        docInfoDTO.setAuthor(docFolderCreateDTO.getAuthor());
        docInfoDTO.setOrderIndex(docFolderCreateDTO.getOrderIndex());
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
     */
    public void deleteOpenAPIModuleDocs(long moduleId) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("create_mode", OperationMode.OPEN.getType())
                .eq("is_locked", Booleans.FALSE);
        // 查询出文档id
        List<Long> docIdList = this.getMapper().listBySpecifiedColumns(Collections.singletonList("id"), query, Long.class);
        if (CollectionUtils.isEmpty(docIdList)) {
            return;
        }
        // 删除文档
        Query delQuery = new Query()
                .in("id", docIdList);
        // DELETE FROM doc_info WHERE id in (..)
        this.getMapper().deleteByQuery(delQuery);

        // 删除文档对应的参数
        docParamService.deletePushParam(docIdList);
    }

    /**
     * 获取指定环境下的header
     * @param envId 环境id
     * @param docId 文档id
     * @return 返回所有的header，全局+文档
     */
    public List<DocParamDTO> listDocHeaders(Long envId, Long docId) {
        ParamStyleEnum header = ParamStyleEnum.HEADER;
        Query query = new Query()
                .eq("id", docId)
                .eq("style", header.getStyle());
        List<DocParam> docHeaders = docParamService.list(query);
        List<DocParamDTO> ret = new ArrayList<>();
        List<DocParamDTO> headers = CopyUtil.copyList(docHeaders, DocParamDTO::new);
        if (envId != null) {
            List<ModuleEnvironmentParam> globalHeaders = moduleEnvironmentParamService.listByEnvironmentAndStyle(envId, header.getStyle());
            List<DocParamDTO> globalHeaderDTOs = CopyUtil.copyList(globalHeaders, DocParamDTO::new);
            ret.addAll(globalHeaderDTOs);
        }
        ret.addAll(headers);
        return ret;
    }

    /**
     * 将markdown内容转化成html
     */
    public void convertMarkdown2Html() {
        List<DocInfo> docInfos = this.listAll();
        Map<String, Object> set = new HashMap<>();
        for (DocInfo docInfo : docInfos) {
            set.put("description", Markdown2HtmlUtil.markdown2Html(docInfo.getDescription()));
            set.put("remark", Markdown2HtmlUtil.markdown2Html(docInfo.getRemark()));
            this.getMapper().updateByMap(set, new Query().eq("id", docInfo.getId()));
        }
    }
}