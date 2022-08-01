package cn.torna.web.controller.module;

import cn.torna.api.open.SwaggerApi;
import cn.torna.api.open.SwaggerRefreshApi;
import cn.torna.common.annotation.HashId;
import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.enums.ModuleTypeEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.IdUtil;
import cn.torna.common.util.RequestUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.Module;
import cn.torna.service.DocImportService;
import cn.torna.service.DocInfoService;
import cn.torna.service.ModuleService;
import cn.torna.service.dto.ImportPostmanDTO;
import cn.torna.service.dto.ImportSwaggerDTO;
import cn.torna.service.dto.ImportSwaggerV2DTO;
import cn.torna.web.controller.module.param.ImportSwaggerParam;
import cn.torna.web.controller.module.param.ImportSwaggerV2Param;
import cn.torna.web.controller.module.param.ModuleAddParam;
import cn.torna.web.controller.module.param.ModuleDeleteParam;
import cn.torna.web.controller.module.param.ModuleUpdateNameParam;
import cn.torna.web.controller.module.vo.ModuleVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("module")
@Slf4j
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private DocImportService docImportService;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private SwaggerApi swaggerApi;

    @Autowired
    private SwaggerRefreshApi swaggerRefreshApi;

    @GetMapping("info")
    public Result<ModuleVO> info(@HashId Long moduleId) {
        Module module = moduleService.getById(moduleId);
        ModuleVO moduleVO = CopyUtil.copyBean(module, ModuleVO::new);
        return Result.ok(moduleVO);
    }

    @GetMapping("infoByDocId")
    @NoLogin
    public Result<ModuleVO> infoByDocId(@HashId Long docId) {
        DocInfo docInfo = docInfoService.getById(docId);
        Module module = moduleService.getById(docInfo.getModuleId());
        ModuleVO moduleVO = CopyUtil.copyBean(module, ModuleVO::new);
        return Result.ok(moduleVO);
    }


    /**
     * 获取项目模块
     * @param projectId
     * @return
     */
    @GetMapping("list")
    public Result<List<ModuleVO>> listModule(@HashId Long projectId) {
        List<Module> modules = moduleService.listProjectModules(projectId);
        List<ModuleVO> moduleVOS = CopyUtil.copyList(modules, ModuleVO::new);
        return Result.ok(moduleVOS);
    }

    /**
     * 添加模块
     * @param param
     * @return
     */
    @PostMapping("add")
    public Result<ModuleVO> add(@RequestBody @Valid ModuleAddParam param) {
        User user = UserContext.getUser();
        Module module = moduleService.addModule(param.getName(), param.getProjectId(), user);
        ModuleVO moduleVO = CopyUtil.copyBean(module, ModuleVO::new);
        return Result.ok(moduleVO);
    }

    /**
     * 修改模块名称
     * @param param
     * @return
     */
    @PostMapping("name/update")
    public Result updateName(@RequestBody @Valid ModuleUpdateNameParam param) {
        User user = UserContext.getUser();
        Module module = moduleService.getById(param.getId());
        module.setName(param.getName());
        module.setModifierId(user.getUserId());
        module.setModifyMode(user.getOperationModel());
        moduleService.update(module);
        return Result.ok();
    }

    /**
     * 删除模块
     * @param param
     * @return
     */
    @PostMapping("delete")
    public Result delete(@RequestBody @Valid ModuleDeleteParam param) {
        User user = UserContext.getUser();
        moduleService.delete(param.getId(), user);
        return Result.ok();
    }

    /**
     * 导入swagger
     * @param param
     * @return
     */
    @PostMapping("import/swagger")
    public Result importSwaggerDoc(@RequestBody @Valid ImportSwaggerParam param) {
        ImportSwaggerDTO importSwaggerDTO = CopyUtil.copyBean(param, ImportSwaggerDTO::new);
        User user = UserContext.getUser();
        importSwaggerDTO.setUser(user);
        Module module = docImportService.importSwagger(importSwaggerDTO);
        ModuleVO moduleVO = CopyUtil.copyBean(module, ModuleVO::new);
        return Result.ok(moduleVO);
    }

    /**
     * 导入swagger
     * @param param
     * @return
     */
    @PostMapping("import/swaggerV2")
    public Result<ModuleVO> importSwaggerDocV2(@RequestBody @Valid ImportSwaggerV2Param param, HttpServletRequest request) {
        ImportSwaggerV2DTO importSwaggerV2DTO = CopyUtil.copyBean(param, ImportSwaggerV2DTO::new);
        User user = UserContext.getUser();
        String ip = RequestUtil.getIP(request);
        importSwaggerV2DTO.setUser(user);
        importSwaggerV2DTO.setIp(ip);
        Module module = swaggerApi.importSwagger(importSwaggerV2DTO);
        ModuleVO moduleVO = CopyUtil.copyBean(module, ModuleVO::new);
        return Result.ok(moduleVO);
    }

    /**
     * 导入postman
     * @param file postman导出文件
     * @param projectId 项目id
     * @return
     */
    @PostMapping("import/postman")
    public Result<ModuleVO> importSwaggerDoc(MultipartFile file, String projectId) {
        User user = UserContext.getUser();
        try {
            String json = IOUtils.toString(file.getInputStream(), StandardCharsets.UTF_8);
            ImportPostmanDTO importPostmanDTO = new ImportPostmanDTO();
            importPostmanDTO.setJson(json);
            importPostmanDTO.setProjectId(IdUtil.decode(projectId));
            importPostmanDTO.setUser(user);
            Module module = docImportService.importPostman(importPostmanDTO);
            ModuleVO moduleVO = CopyUtil.copyBean(module, ModuleVO::new);
            return Result.ok(moduleVO);
        } catch (Exception e) {
            log.error("导入文件失败", e);
            throw new BizException("导入失败，请查看日志");
        }
    }

    /**
     * 刷新swagger
     * @param moduleId
     * @return
     */
    @GetMapping("refresh/swagger")
    public Result refreshSwaggerDoc(@HashId Long moduleId) {
        Module module = moduleService.getById(moduleId);
        if (module != null && module.getType() == ModuleTypeEnum.SWAGGER_IMPORT.getType()) {
            ImportSwaggerDTO importSwaggerDTO = CopyUtil.copyBean(module, ImportSwaggerDTO::new);
            User user = UserContext.getUser();
            importSwaggerDTO.setUser(user);
            docImportService.importSwagger(importSwaggerDTO);
        }
        return Result.ok();
    }

    /**
     * 刷新swagger
     * @param moduleId
     * @return
     */
    @GetMapping("refresh/swaggerV2")
    public Result refreshSwaggerDocV2(@HashId Long moduleId, HttpServletRequest request) {
        swaggerRefreshApi.refresh(moduleId, RequestUtil.getIP(request));
        return Result.ok();
    }

}
