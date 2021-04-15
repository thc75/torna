package cn.torna.web.controller.doc;

import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.service.DocInfoService;
import cn.torna.web.controller.doc.param.DocFolderAddParam;
import cn.torna.web.controller.doc.param.DocFolderUpdateParam;
import cn.torna.web.controller.doc.vo.DocInfoVO;
import cn.torna.web.controller.doc.vo.IdVO;
import cn.torna.web.controller.system.param.IdParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.torna.common.annotation.HashId;
import cn.torna.service.dto.DocInfoDTO;

import javax.validation.Valid;
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
        DocInfo docInfo = docInfoService.saveDocInfo(docInfoDTO, user);
        return Result.ok(new IdVO(docInfo.getId()));
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



}