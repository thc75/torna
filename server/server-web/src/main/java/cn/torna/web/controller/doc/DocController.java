package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.ModuleEnvironment;
import cn.torna.dao.entity.ModuleEnvironmentParam;
import cn.torna.service.DocInfoService;
import cn.torna.service.ModuleEnvironmentParamService;
import cn.torna.service.ModuleEnvironmentService;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.service.dto.ModuleEnvironmentDTO;
import cn.torna.service.dto.UpdateDocFolderDTO;
import cn.torna.web.controller.doc.param.DocFolderAddParam;
import cn.torna.web.controller.doc.param.DocFolderUpdateParam;
import cn.torna.web.controller.doc.param.DocInfoSaveParam;
import cn.torna.web.controller.doc.param.DocInfoSearch;
import cn.torna.web.controller.doc.param.UpdateOrderIndexParam;
import cn.torna.web.controller.doc.vo.DocInfoVO;
import cn.torna.web.controller.doc.vo.IdVO;
import cn.torna.web.controller.module.vo.ModuleGlobalParamsVO;
import cn.torna.web.controller.system.param.IdParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc")
public class DocController {

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private ModuleEnvironmentParamService moduleEnvironmentParamService;

    @Autowired
    private ModuleEnvironmentService moduleEnvironmentService;

    /**
     * 获取项目文档目录，可用于文档菜单
     * @param moduleId 模块id
     * @return 返回结果
     */
    @GetMapping("list")
    public Result<List<DocInfoVO>> listProjectDoc(@HashId Long moduleId) {
        List<DocInfo> docInfos = docInfoService.listModuleDoc(moduleId);
        List<DocInfoVO> docInfoVOS = CopyUtil.copyList(docInfos, DocInfoVO::new);
        return Result.ok(docInfoVOS);
    }

    /**
     * 保存文档信息
     * @param param
     * @return
     */
    @PostMapping("save")
    public Result<IdVO> save(@RequestBody @Valid DocInfoSaveParam param) {
        DocInfoDTO docInfoDTO = CopyUtil.deepCopy(param, DocInfoDTO.class);
        User user = UserContext.getUser();
        Long id = docInfoDTO.getId();
        DocInfo docInfo;
        if (id == null) {
            // 检查是否存在
            String dataId = docInfoDTO.buildDataId();
            DocInfo exist = docInfoService.getByDataId(dataId);
            if (exist != null) {
                String tpl = "【%s】%s";
                throw new BizException(String.format(tpl, exist.getHttpMethod(), exist.getUrl()) + " 已存在");
            }
            this.nullParamsId(docInfoDTO);
            // 不存在则保存文档
            docInfo = docInfoService.saveDocInfo(docInfoDTO, user);
        } else {
            docInfo = docInfoService.updateDocInfo(docInfoDTO, user);
        }
        return Result.ok(new IdVO(docInfo.getId()));
    }

    /**
     * 将参数的id设置成null
     * @param docInfoDTO docInfoDTO
     */
    private void nullParamsId(DocInfoDTO docInfoDTO) {
        nullId(docInfoDTO.getHeaderParams());
        nullId(docInfoDTO.getPathParams());
        nullId(docInfoDTO.getQueryParams());
        nullId(docInfoDTO.getRequestParams());
        nullId(docInfoDTO.getResponseParams());
        nullId(docInfoDTO.getErrorCodeParams());
    }

    private void nullId(List<DocParamDTO> docParamDTOList) {
        if (CollectionUtils.isEmpty(docParamDTOList)) {
            return;
        }
        long docId = 0;
        for (DocParamDTO docParamDTO : docParamDTOList) {
            docParamDTO.setId(null);
            docParamDTO.setDocId(docId);
            List<DocParamDTO> children = docParamDTO.getChildren();
            nullId(children);
        }
    }


    /**
     * 删除
     * @param param
     * @return
     */
    @PostMapping("delete")
    public Result delete(@RequestBody @Valid IdParam param) {
        User user = UserContext.getUser();
        docInfoService.deleteDocInfo(param.getId(), user);
        return Result.ok();
    }

    /**
     * 查询文档详细信息
     *
     * @param id 主键
     * @return 返回记录，没有返回null
     */
    @GetMapping("detail")
    public Result<DocInfoDTO> detail(@HashId Long id) {
        DocInfoDTO docInfoDTO = docInfoService.getDocDetail(id);
        return Result.ok(docInfoDTO);
    }

    /**
     * 查询文档表单详细信息
     * @param id
     * @return
     */
    @GetMapping("form")
    public Result<DocInfoDTO> form(@HashId Long id) {
        DocInfoDTO docInfoDTO = docInfoService.getDocForm(id);
        return Result.ok(docInfoDTO);
    }

    /**
     * 查询文档详细信息，不需要登录
     *
     * @param id 主键
     * @return 返回记录，没有返回null
     */
    @GetMapping("view")
    @NoLogin
    public Result<DocInfoDTO> view(@HashId Long id) {
        if (id == null) {
            throw new BizException("文档不存在");
        }
        DocInfoDTO docInfoDTO = docInfoService.getDocDetailView(id);
        return Result.ok(docInfoDTO);
    }

    @PostMapping("lock")
    public Result lock(@RequestBody IdParam param) {
        DocInfo docInfo = docInfoService.getById(param.getId());
        docInfo.setIsLocked(Booleans.TRUE);
        docInfoService.update(docInfo);
        return Result.ok();
    }

    @PostMapping("unlock")
    public Result unlock(@RequestBody IdParam param) {
        DocInfo docInfo = docInfoService.getById(param.getId());
        docInfo.setIsLocked(Booleans.FALSE);
        docInfoService.update(docInfo);
        return Result.ok();
    }

    /**
     * 查询文档详细信息，不需要登录
     *
     * @param id 主键
     * @return 返回记录，没有返回null
     */
    @GetMapping("viewShow")
    @NoLogin
    public Result<DocInfoDTO> viewShow(@HashId Long id) {
        if (id == null) {
            throw new BizException("文档不存在");
        }
        DocInfoDTO docInfoDTO = docInfoService.getDocDetailView(id);
        List<ModuleEnvironmentDTO> debugEnvs = docInfoDTO.getDebugEnvs();
        if (debugEnvs != null) {
            // 只返回公开的调试环境
            byte isPublic = 1;
            List<ModuleEnvironmentDTO> finalDebugEnv = debugEnvs.stream()
                    .filter(debugHostDTO -> debugHostDTO.getIsPublic() == isPublic)
                    .collect(Collectors.toList());
            docInfoDTO.setDebugEnvs(finalDebugEnv);
        }
        return Result.ok(docInfoDTO);
    }

    /**
     * 获取模块分类
     * @param moduleId
     * @return
     */
    @GetMapping("folder/list")
    public Result<List<DocInfoDTO>> list(@HashId Long moduleId) {
        List<DocInfo> folders = docInfoService.listFolders(moduleId);
        return Result.ok(CopyUtil.copyList(folders, DocInfoDTO::new));
    }

    /**
     * 添加分类
     * @param param
     * @return
     */
    @PostMapping("folder/add")
    public Result addFolder(@RequestBody @Valid DocFolderAddParam param) {
        String name = param.getName();
        Long moduleId = param.getModuleId();
        User user = UserContext.getUser();
        docInfoService.createDocFolder(name, moduleId, user, param.getParentId());
        return Result.ok();
    }

    /**
     * 修改分类名称
     * @param param
     * @return
     */
    @PostMapping("folder/update")
    public Result updateFolder(@RequestBody @Valid DocFolderUpdateParam param) {
        String name = param.getName();
        User user = UserContext.getUser();
        UpdateDocFolderDTO updateDocFolderDTO = CopyUtil.copyBean(param, UpdateDocFolderDTO::new);
        updateDocFolderDTO.setUser(user);
        docInfoService.updateDocFolderName(updateDocFolderDTO);
        return Result.ok();
    }

    @PostMapping("detail/search")
    public Result<List<DocInfoDTO>> listDocInfoDetail(@RequestBody DocInfoSearch docInfoSearch) {
        List<Long> docIdList = docInfoSearch.getDocIdList();
        if (CollectionUtils.isEmpty(docIdList)) {
            return Result.ok(Collections.emptyList());
        }
        List<DocInfoDTO> docInfoDTOList = docInfoService.listDocDetail(docIdList);
        return Result.ok(docInfoDTOList);
    }

    @PostMapping("orderindex/update")
    public Result updateOrderIndex(@RequestBody UpdateOrderIndexParam param) {
        DocInfo docInfo = docInfoService.getById(param.getId());
        docInfo.setOrderIndex(param.getOrderIndex());
        docInfoService.update(docInfo);
        return Result.ok();
    }


    @GetMapping("headers/global")
    @NoLogin
    public Result<List<DocParamDTO>> headersGlobal(@HashId Long environmentId) {
        List<ModuleEnvironmentParam> moduleEnvironmentParams = moduleEnvironmentParamService.listByEnvironmentAndStyle(environmentId, ParamStyleEnum.HEADER.getStyle());
        List<DocParamDTO> docParamDTOS = CopyUtil.copyList(moduleEnvironmentParams, DocParamDTO::new, docParamDTO -> docParamDTO.setGlobal(true));
        return Result.ok(docParamDTOS);
    }


    @GetMapping("globals")
    public Result<ModuleGlobalParamsVO> globals(@HashId Long moduleId) {
        ModuleEnvironment moduleEnvironment = moduleEnvironmentService.getFirst(moduleId);
        if (moduleEnvironment == null) {
            return Result.ok();
        }
        Long environmentId = moduleEnvironment.getId();
        List<ModuleEnvironmentParam> list = moduleEnvironmentParamService.listAllByEnvironment(environmentId);
        // 根据style分组
        Map<Byte, List<ModuleEnvironmentParam>> styleMap = list.stream()
                .collect(Collectors.groupingBy(ModuleEnvironmentParam::getStyle));
        List<ModuleEnvironmentParam> globalHeaders = styleMap.getOrDefault(ParamStyleEnum.HEADER.getStyle(), Collections.emptyList());
        List<ModuleEnvironmentParam> globalRequest = styleMap.getOrDefault(ParamStyleEnum.REQUEST.getStyle(), Collections.emptyList());
        List<ModuleEnvironmentParam> globalResponse = styleMap.getOrDefault(ParamStyleEnum.RESPONSE.getStyle(), Collections.emptyList());
        ModuleGlobalParamsVO moduleGlobalParamsVO = new ModuleGlobalParamsVO();
        moduleGlobalParamsVO.setGlobalHeaders(CopyUtil.copyList(globalHeaders, DocParamDTO::new));
        moduleGlobalParamsVO.setGlobalParams(CopyUtil.copyList(globalRequest, DocParamDTO::new));
        moduleGlobalParamsVO.setGlobalReturns(CopyUtil.copyList(globalResponse, DocParamDTO::new));
        return Result.ok(moduleGlobalParamsVO);
    }

}