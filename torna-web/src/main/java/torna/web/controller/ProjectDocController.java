package torna.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.bean.Result;
import torna.common.util.CopyUtil;
import torna.dao.entity.DocInfo;
import torna.dao.entity.Module;
import torna.service.DocImportService;
import torna.service.DocInfoService;
import torna.service.ModuleService;
import torna.service.dto.DocInfoDTO;
import torna.service.dto.ImportSwaggerDTO;
import torna.web.controller.param.ImportSwaggerParam;
import torna.web.controller.vo.DocInfoVO;
import torna.web.controller.vo.ModuleVO;

import javax.validation.Valid;
import java.util.List;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("project/doc")
public class ProjectDocController {

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private DocImportService docImportService;

    @Autowired
    private ModuleService moduleService;

    /**
     * 获取项目模块
     * @param projectId
     * @return
     */
    @GetMapping("/module/list")
    public Result<List<ModuleVO>> listModule(long projectId) {
        List<Module> modules = moduleService.list("project_id", projectId);
        List<ModuleVO> moduleVOS = CopyUtil.copyList(modules, ModuleVO::new);
        return Result.ok(moduleVOS);
    }

    /**
     * 获取项目文档目录，可用于文档菜单
     * @param moduleId 模块id
     * @return 返回结果
     */
    @GetMapping("list")
    public Result<List<DocInfoVO>> listProjectDoc(long moduleId) {
        List<DocInfo> docInfos = docInfoService.list("module_id", moduleId);
        List<DocInfoVO> docInfoVOS = CopyUtil.copyList(docInfos, DocInfoVO::new);
        return Result.ok(docInfoVOS);
    }

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 返回记录，没有返回null
     */
    @GetMapping("detail")
    public Result<DocInfoDTO> detail(Long id) {
        DocInfoDTO docDetail = docInfoService.getDocDetail(id);
        return Result.ok(docDetail);
    }

    /**
     * 导入swagger
     * @param param
     * @return
     */
    @PostMapping("import/swagger")
    public Result importSwaggerDoc(@RequestBody @Valid ImportSwaggerParam param) {
        ImportSwaggerDTO importSwaggerDTO = CopyUtil.copyBean(param, ImportSwaggerDTO::new);
        docImportService.importSwagger(importSwaggerDTO);
        return Result.ok();
    }
     
    
    
}