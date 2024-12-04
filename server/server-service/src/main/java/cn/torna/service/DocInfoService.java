package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.User;
import cn.torna.common.context.SpringContext;
import cn.torna.common.enums.DocSortType;
import cn.torna.common.enums.DocTypeEnum;
import cn.torna.common.enums.ModifySourceEnum;
import cn.torna.common.enums.OperationMode;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.enums.PropTypeEnum;
import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.IdGen;
import cn.torna.common.util.Markdown2HtmlUtil;
import cn.torna.common.util.TreeUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.DocParam;
import cn.torna.dao.entity.EnumInfo;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.ModuleEnvironment;
import cn.torna.dao.entity.ModuleEnvironmentParam;
import cn.torna.dao.entity.UserDingtalkInfo;
import cn.torna.dao.entity.UserWeComInfo;
import cn.torna.dao.mapper.DocInfoMapper;
import cn.torna.dao.mapper.UserDingtalkInfoMapper;
import cn.torna.dao.mapper.UserWeComInfoMapper;
import cn.torna.manager.doc.DataType;
import cn.torna.service.dto.DocFolderCreateDTO;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocItemCreateDTO;
import cn.torna.service.dto.DocListFormDTO;
import cn.torna.service.dto.DocMeta;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.service.dto.DocRefDTO;
import cn.torna.service.dto.DubboInfoDTO;
import cn.torna.service.dto.EnumInfoDTO;
import cn.torna.service.dto.EnumItemDTO;
import cn.torna.service.dto.ModuleEnvironmentDTO;
import cn.torna.service.dto.UpdateDocFolderDTO;
import cn.torna.service.event.DocAddEvent;
import cn.torna.service.event.DocUpdateEvent;
import com.gitee.fastmybatis.core.PageInfo;
import com.gitee.fastmybatis.core.query.LambdaQuery;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.param.PageParam;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
@Slf4j
public class DocInfoService extends BaseLambdaService<DocInfo, DocInfoMapper> {

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

    @Resource
    private UserDingtalkInfoMapper userDingtalkInfoMapper;

    @Resource
    private UserWeComInfoMapper userWeComInfoWrapper;

    @Autowired
    private UserSubscribeService userSubscribeService;

    @Autowired
    private MockConfigService mockConfigService;


    /**
     * 查询模块下的所有文档
     *
     * @param moduleId 模块id
     * @return 返回文档
     */
    public List<DocInfo> listModuleDoc(long moduleId) {
        List<DocInfo> docInfoList = list(DocInfo::getModuleId, moduleId);
        sortDocInfo(docInfoList);
        return docInfoList;
    }

    /**
     * 查询模块下的所有文档
     *
     * @param moduleId 模块id
     * @return 返回文档
     */
    public List<DocInfo> listModuleDoc(long moduleId, LambdaQuery<DocInfo> query) {
        query.eq(DocInfo::getModuleId, moduleId);
        List<DocInfo> docInfoList = list(query);
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
            default: {
                comparator = Comparator.comparing(DocInfo::getOrderIndex);
            }
        }
        docInfoList.sort(comparator);
    }

    public List<DocInfo> listDocMenuView(long moduleId) {
        return this.listModuleMenuDoc(moduleId);
    }

    /**
     * 查询模块下的所有文档
     *
     * @param moduleId 模块id
     * @return 返回文档
     */
    public List<DocInfo> listModuleMenuDoc(long moduleId) {
        Query query = this.query()
                .select(DocInfo::getId, DocInfo::getName, DocInfo::getParentId, DocInfo::getUrl, DocInfo::getIsFolder,
                        DocInfo::getModuleId, DocInfo::getType, DocInfo::getHttpMethod, DocInfo::getDeprecated,
                        DocInfo::getVersion, DocInfo::getOrderIndex, DocInfo::getIsShow, DocInfo::getIsLocked, DocInfo::getStatus)
                .eq(DocInfo::getModuleId, moduleId)
                .eq(DocInfo::getIsShow, Booleans.TRUE);
        List<DocInfo> docInfoList = list(query);
        sortDocInfo(docInfoList);
        return docInfoList;
    }

    /**
     * 查询模块下的所有文档
     *
     * @param moduleId 模块id
     * @return 返回文档
     */
    public List<DocInfo> listModuleTableDoc(long moduleId) {
        Query query = this.query()
                .select(DocInfo::getId, DocInfo::getName, DocInfo::getParentId, DocInfo::getUrl, DocInfo::getIsFolder,
                        DocInfo::getModuleId, DocInfo::getType, DocInfo::getHttpMethod, DocInfo::getDeprecated,
                        DocInfo::getAuthor, DocInfo::getModifierName, DocInfo::getGmtModified,
                        DocInfo::getOrderIndex, DocInfo::getIsShow, DocInfo::getIsLocked, DocInfo::getStatus)
                .eq(DocInfo::getModuleId, moduleId)
                .eq(DocInfo::getIsShow, Booleans.TRUE);
        List<DocInfo> docInfoList = list(query);
        sortDocInfo(docInfoList);
        return docInfoList;
    }

    /**
     * 查询模块下的所有文档
     *
     * @param docListFormDTO docListFormDTO
     * @return 返回文档
     */
    public List<DocInfo> listModuleTableDoc(DocListFormDTO docListFormDTO) {
        Query query;
        if (docListFormDTO.getStatus() == null) {
            query = LambdaQuery.create(DocInfo.class)
                    .eq(DocInfo::getModuleId, docListFormDTO.getModuleId());
        } else {
            query = LambdaQuery.create(DocInfo.class)
                    .select(DocInfo::getId, DocInfo::getName, DocInfo::getParentId, DocInfo::getUrl, DocInfo::getIsFolder,
                            DocInfo::getType, DocInfo::getModuleId, DocInfo::getHttpMethod, DocInfo::getDeprecated, DocInfo::getVersion,
                            DocInfo::getAuthor, DocInfo::getModifierName, DocInfo::getGmtModified,
                            DocInfo::getOrderIndex, DocInfo::getIsShow, DocInfo::getIsLocked, DocInfo::getStatus)
                    .eq(DocInfo::getModuleId, docListFormDTO.getModuleId())
                    .eq(docListFormDTO.getStatus() != null, DocInfo::getStatus, docListFormDTO.getStatus())
                    .orLambda(q -> q.eq(DocInfo::getModuleId, docListFormDTO.getModuleId()).eq(DocInfo::getIsFolder, Booleans.TRUE));
        }
        List<DocInfo> docInfoList = this.list(query);
        sortDocInfo(docInfoList);
        return docInfoList;
    }

    public PageInfo<DocInfo> pageDocByIds(List<Long> docIds, PageParam pageParam) {
        if (CollectionUtils.isEmpty(docIds)) {
            PageInfo<DocInfo> pageInfo = new PageInfo<>();
            pageInfo.setList(Collections.emptyList());
            return pageInfo;
        }
        Query query = pageParam
                .toQuery()
                .toLambdaQuery(DocInfo.class)
                .in(DocInfo::getId, docIds)
                .orderByAsc(DocInfo::getOrderIndex);
        return page(query);
    }

    /**
     * 返回文档详情
     *
     * @param docId 文档id
     * @return 返回文档详情
     */
    public DocInfoDTO getDocDetailView(long docId) {
        Query query = this.query()
                .eq(DocInfo::getId, docId)
                .eq(DocInfo::getIsShow, Booleans.TRUE);
        DocInfo docInfo = get(query);
        return getDocDetail(docInfo);
    }

    /**
     * 返回文档详情
     *
     * @param docIds 文档id
     * @return 返回文档详情
     */
    public List<DocInfoDTO> getDocDetailsView(List<Long> docIds) {
        Query query = this.query()
                .in(DocInfo::getId, docIds)
                .eq(DocInfo::getIsShow, Booleans.TRUE);
        List<DocInfo> docInfoList = list(query);
        return docInfoList.parallelStream()
                .map(this::getDocDetail)
                .collect(Collectors.toList());
    }

    /**
     * 返回文档详情
     *
     * @param docId 文档id
     * @return 返回文档详情
     */
    public DocInfoDTO getDocDetail(long docId) {
        DocInfo docInfo = this.getById(docId);
        return getDocDetail(docInfo);
    }

    /**
     * 返回文档详情
     *
     * @param docId 文档id
     * @return 返回文档详情
     */
    public DocInfoDTO getDocForm(long docId) {
        DocInfo docInfo = this.getById(docId);
        return getDocInfoDTO(docInfo);
    }

    public DocInfoDTO getDocInfoDTO(DocInfo docInfo) {
        Assert.notNull(docInfo, () -> "文档不存在");
        DocInfoDTO docInfoDTO = CopyUtil.copyBean(docInfo, DocInfoDTO::new);
        Long moduleId = docInfo.getModuleId();
        Module module = moduleService.getById(moduleId);
        docInfoDTO.setSpaceId(projectService.getSpaceId(module.getProjectId()));
        docInfoDTO.setProjectId(module.getProjectId());
        docInfoDTO.setModuleType(module.getType());
        List<ModuleEnvironment> debugEnvs = moduleEnvironmentService.listModuleEnvironment(moduleId);
        docInfoDTO.setDebugEnvs(CopyUtil.copyList(debugEnvs, ModuleEnvironmentDTO::new));
        List<DocParam> params = docParamService.list(DocParam::getDocId, docInfo.getId());
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
        docInfoDTO.setHeaderParamsRaw(CopyUtil.copyList(headerParams, DocParamDTO::new));
        docInfoDTO.setQueryParams(CopyUtil.copyList(queryParams, DocParamDTO::new));
        docInfoDTO.setRequestParams(CopyUtil.copyList(requestParams, DocParamDTO::new));
        docInfoDTO.setResponseParams(CopyUtil.copyList(responseParams, DocParamDTO::new));
        docInfoDTO.setErrorCodeParams(CopyUtil.copyList(errorCodeParams, DocParamDTO::new));
        // 绑定枚举信息
        bindEnumInfo(docInfoDTO.getPathParams());
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
     *
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

    public List<DocInfoDTO> listDocDetail(Long moduleId) {
        return this.query()
                .eq(DocInfo::getModuleId, moduleId)
                .list()
                .stream()
                .map(this::getDocDetail)
                .collect(Collectors.toList());
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
        Query query = this.query()
                .in(DocInfo::getId, docIdList);
        List<DocInfo> list = this.list(query);
        sortDocInfo(list);
        return list;
    }


    public DocInfoDTO getDocDetail(DocInfo docInfo) {
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
     *
     * @param moduleId 模块id
     * @return 返回全局错误码
     */
    private List<DocParam> listCommonErrorCodes(long moduleId) {
        return moduleConfigService.listCommonErrorCodes(moduleId);
    }

    /**
     * 保存文档信息
     *
     * @param docInfoDTO 文档内容
     * @param user       用户
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized DocInfo saveDocInfo(DocInfoDTO docInfoDTO, User user) {
        return doSaveDocInfo(docInfoDTO, user);
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized DocInfo updateDocInfo(DocInfoDTO docInfoDTO, User user) {
        if (ObjectUtils.isEmpty(docInfoDTO.getParentId())) {
            docInfoDTO.setParentId(0L);
        }
        return doUpdateDocInfo(docInfoDTO, user);
    }

    public void doPushSaveDocInfo(DocInfoDTO docInfoDTO, User user) {
        // 修改基本信息
        DocInfo docInfo = this.insertDocInfo(docInfoDTO, user);
        // 删除文档对应的参数
        docParamService.deletePushParam(Collections.singletonList(docInfo.getId()));
        // 修改参数
        this.doUpdateParams(docInfo, docInfoDTO, user);
    }

    private void createDocMock(DocInfo docInfo) {
        mockConfigService.createDocDefaultMock(docInfo.getId());
    }

    public List<DocMeta> listDocMeta(long moduleId) {
        Query query = this.query()
                .select(DocInfo::getDataId, DocInfo::getDocKey, DocInfo::getIsLocked, DocInfo::getMd5)
                .eq(DocInfo::getModuleId, moduleId);
        List<DocInfo> list = this.list(query);
        return CopyUtil.copyList(list, DocMeta::new);
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
        SpringContext.publishEvent(new DocAddEvent(docInfo.getId(), ModifySourceEnum.FORM));
        createDocMock(docInfo);
        return docInfo;
    }

    public DocInfo doUpdateDocInfo(DocInfoDTO docInfoDTO, User user) {
        DocInfo docInfoOld = this.getById(docInfoDTO.getId());
        String oldMd5 = docInfoOld.getMd5();
        // 修改基本信息
        DocInfo docInfo = this.modifyDocInfo(docInfoOld, docInfoDTO, user);
        // 修改参数
        this.doUpdateParams(docInfo, docInfoDTO, user);
        ModifySourceEnum sourceFromEnum = DocTypeEnum.isTextType(docInfo.getType()) ? ModifySourceEnum.TEXT : ModifySourceEnum.FORM;
        SpringContext.publishEvent(new DocUpdateEvent(docInfoOld.getId(), oldMd5, sourceFromEnum));
        createDocMock(docInfo);
        return docInfo;
    }

    /**
     * 修改文档基本信息。参数除外
     *
     * @param docInfoDTO
     * @param user
     * @return
     */
    public DocInfo doUpdateDocBaseInfo(DocInfoDTO docInfoDTO, User user) {
        DocInfo docInfoOld = this.getById(docInfoDTO.getId());
        String oldMd5 = docInfoOld.getMd5();
        // 修改基本信息
        DocInfo docInfo = this.modifyDocInfo(docInfoOld, docInfoDTO, user);
        SpringContext.publishEvent(new DocUpdateEvent(docInfoOld.getId(), oldMd5, ModifySourceEnum.FORM));
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
        String docMd5 = getDocMd5(docInfoDTO);
        docInfo.setMd5(docMd5);
        this.getMapper().saveDocInfo(docInfo);
        Long id = docInfo.getId();
        // 修复使用非MYSQL数据库插入数据id不返回问题
        if (id == null) {
            DocInfo one = this.getByDataId(docInfo.getDataId());
            if (one != null) {
                id = one.getId();
                docInfo.setId(id);
            }
        }
        if (id != null) {
            docInfoDTO.setId(id);
        }
        return docInfo;
    }

    private DocInfo modifyDocInfo(DocInfo docInfo, DocInfoDTO docInfoDTO, User user) {
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
        docInfo.setDocKey(docInfoDTO.buildDocKey());
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
        return DocMd5BuilderManager.getBuilder().buildMd5(docInfoDTO);
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
        docInfo.setDocKey(docInfoDTO.buildDocKey());
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
        return get(DocInfo::getDataId, dataId);
    }

    /**
     * 查询模块下面所有分类
     *
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
        Query query = this.query()
                .eq(DocInfo::getModuleId, moduleId)
                .eq(DocInfo::getParentId, parentId)
                .eq(DocInfo::getName, name);
        return get(query);
    }

    /**
     * 修改分类名称
     *
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
     *
     * @param id   文档注解
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
     *
     * @param folderName 分类名称
     * @param moduleId   模块id
     * @param user       操作人
     */
    public DocInfo createDocFolder(String folderName, long moduleId, User user) {
        return createDocFolder(folderName, moduleId, user, 0L);
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
     *
     * @param moduleId 模块id
     */
    public void deleteOpenAPIModuleDocs(long moduleId) {
        Query query = this.query()
                .eq(DocInfo::getModuleId, moduleId)
                .eq(DocInfo::getCreateMode, OperationMode.OPEN.getType())
                .eq(DocInfo::getIsLocked, Booleans.FALSE)
                // 如果已经定义了版本号，不被删除
                .isEmpty(DocInfo::getVersion);
        // 查询出文档id
        List<Long> docIdList = listValue(query, DocInfo::getId);
        if (CollectionUtils.isEmpty(docIdList)) {
            return;
        }
        // 删除文档
        Query delQuery = this.query()
                .in(DocInfo::getId, docIdList);
        // DELETE FROM doc_info WHERE id in (..)
        this.getMapper().deleteByQuery(delQuery);

        // 删除文档对应的参数
        docParamService.deletePushParam(docIdList);
    }

    /**
     * 获取指定环境下的header
     *
     * @param envId 环境id
     * @param docId 文档id
     * @return 返回所有的header，全局+文档
     */
    public List<DocParamDTO> listDocHeaders(Long envId, Long docId) {
        ParamStyleEnum header = ParamStyleEnum.HEADER;
        Query query = docParamService.query()
                .eq(DocParam::getId, docId)
                .eq(DocParam::getStyle, header.getStyle());
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
        List<DocInfo> docInfos = this.list(new Query());
        Map<String, Object> set = new HashMap<>();
        for (DocInfo docInfo : docInfos) {
            LambdaQuery<DocInfo> lambdaQuery = this.query()
                    .set(DocInfo::getDescription, Markdown2HtmlUtil.markdown2Html(docInfo.getDescription()))
                    .set(DocInfo::getRemark, Markdown2HtmlUtil.markdown2Html(docInfo.getRemark()))
                    .eq(DocInfo::getId, docInfo.getId());
            this.update(lambdaQuery);
        }
    }


    public void updateVersion(Long docId, String version) {
        if (version == null) {
            version = "";
        }
        DocInfo docInfo = getById(docId);
        DocInfoDTO docInfoDTO = CopyUtil.copyBean(docInfo, DocInfoDTO::new);
        docInfoDTO.setVersion(version);
        // 重新设置下dataId
        String dataId = docInfoDTO.buildDataId();
        if (this.checkExist(DocInfo::getDataId, dataId, docInfo.getId())) {
            throw new BizException("相同版本号已存在");
        }
        docInfo.setDataId(dataId);
        docInfo.setVersion(version);
        this.update(docInfo);
    }

    /**
     * 获取文档所有的关注人员的钉钉userid
     *
     * @param docId
     * @return 返回钉钉的userid
     */
    public List<String> listSubscribeDocDingDingUserIds(long docId) {
        List<Long> userIds = userSubscribeService.listUserIds(UserSubscribeTypeEnum.DOC, docId);
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>(0);
        }
        List<UserDingtalkInfo> dingtalkInfoList = userDingtalkInfoMapper.list(UserDingtalkInfo::getUserInfoId, userIds);
        if (CollectionUtils.isEmpty(dingtalkInfoList)) {
            // 如果都未绑定钉钉，为了能发送消息，给一个默认值
            return Collections.singletonList("");
        }
        return dingtalkInfoList.stream()
                .map(UserDingtalkInfo::getUserid)
                .collect(Collectors.toList());
    }


    /**
     * 获取文档所有的关注人员的企业微信用户手机号码
     *
     * @param docId 文档id
     * @return 返回 企业微信用户手机号码
     */
    public List<String> listSubscribeDocWeComUserMobiles(long docId) {
        List<Long> userIds = userSubscribeService.listUserIds(UserSubscribeTypeEnum.DOC, docId);
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>(0);
        }
        List<UserWeComInfo> userInfoId = userWeComInfoWrapper.list(UserWeComInfo::getUserInfoId, userIds);
        return userInfoId.stream()
                .map(UserWeComInfo::getMobile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public DocRefDTO getDocRefInfo(long docId) {
        DocRefDTO docRefDTO = new DocRefDTO();
        DocInfo docInfo = this.getById(docId);
        docRefDTO.setDocId(docId);
        docRefDTO.setModuleId(docInfo.getModuleId());
        Module module = moduleService.getById(docInfo.getModuleId());
        docRefDTO.setProjectId(module.getProjectId());
        return docRefDTO;
    }

    /**
     * 更新文档状态
     *
     * @param docId
     * @param status
     * @param user
     */
    public void updateStatus(long docId, byte status, User user) {
        DocInfoDTO docDetail = this.getDocForm(docId);
        if (Objects.equals(status, docDetail.getStatus())) {
            return;
        }
        docDetail.setStatus(status);
        this.doUpdateDocBaseInfo(docDetail, user);
    }

    public String getDocKey(Long docId) {
        return this.getValue(new Query().eq("id", docId), DocInfo::getDocKey);
    }

    public List<DocInfo> listByDocKey(String docKey) {
        return list(DocInfo::getDocKey, docKey);
    }

    public void fillDocKey() {
        LambdaQuery<DocInfo> query = this.query()
                .select(DocInfo::getId, DocInfo::getName, DocInfo::getParentId, DocInfo::getUrl, DocInfo::getIsFolder, DocInfo::getModuleId,
                        DocInfo::getType, DocInfo::getHttpMethod, DocInfo::getDeprecated, DocInfo::getVersion)
                .eq(DocInfo::getType, DocTypeEnum.HTTP.getType());
        List<DocInfo> list = list(query);

        for (DocInfo docInfo : list) {
            DocInfoDTO docInfoDTO = CopyUtil.copyBean(docInfo, DocInfoDTO::new);
            String docKey = docInfoDTO.buildDocKey();
            if (StringUtils.hasText(docKey)) {
                String dataId = docInfoDTO.buildDataId();
                LambdaQuery<DocInfo> updateQuery = this.query();
                updateQuery.set(DocInfo::getDocKey, docKey)
                        .set(DocInfo::getDataId, dataId)
                        .eq(DocInfo::getId, docInfo.getId());

                try {
                    this.update(updateQuery);
                } catch (Exception e) {
                    String newDataId = dataId + docInfo.getId();
                    updateQuery.set(DocInfo::getDataId, DigestUtils.md5DigestAsHex(newDataId.getBytes(StandardCharsets.UTF_8)));
                    this.update(updateQuery);
                }
            }
        }
    }

    public List<DocInfoDTO> listTreeDoc(Long moduleId) {
        List<DocInfo> docInfos = listModuleDoc(moduleId);
        return convertTree(docInfos);
    }

    private List<DocInfoDTO> convertTree(List<DocInfo> docInfos) {
        List<DocInfoDTO> docInfoDTOList = docInfos.stream()
                .map(this::getDocDetail)
                .collect(Collectors.toList());
        return TreeUtil.convertTree(docInfoDTOList, 0L);
    }

    public List<DocInfo> listByParentId(Long parentId) {
        return list(DocInfo::getParentId, parentId);
    }

}
