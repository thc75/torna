package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.service.DocInfoService;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.web.controller.doc.param.DocFolderAddParam;
import cn.torna.web.controller.doc.param.DocFolderUpdateParam;
import cn.torna.web.controller.doc.param.DocInfoSearch;
import cn.torna.web.controller.doc.vo.DocInfoVO;
import cn.torna.web.controller.doc.vo.IdVO;
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

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc")
public class DocController {

    @Autowired
    private DocInfoService docInfoService;

    /**
     * 获取项目文档目录，可用于文档菜单
     * @param moduleId 模块id
     * @return 返回结果
     */
    @GetMapping("list")
    public Result<List<DocInfoVO>> listProjectDoc(@HashId Long moduleId) {
        List<DocInfo> docInfos = docInfoService.listDocMenu(moduleId);
        List<DocInfoVO> docInfoVOS = CopyUtil.copyList(docInfos, DocInfoVO::new);
        return Result.ok(docInfoVOS);
    }

    /**
     * 保存文档信息
     * @param docInfoDTO
     * @return
     */
    @PostMapping("save")
    public Result<IdVO> save(@RequestBody @Valid DocInfoDTO docInfoDTO) {
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
        for (DocParamDTO docParamDTO : docParamDTOList) {
            docParamDTO.setId(null);
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
     * 根据主键查询
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
        docInfoService.createDocFolder(name, moduleId, user);
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
        docInfoService.updateDocFolderName(param.getId(), name, user);
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


}