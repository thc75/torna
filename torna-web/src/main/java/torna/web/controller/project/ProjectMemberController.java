package torna.web.controller.project;

import com.gitee.fastmybatis.core.support.PageEasyui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import torna.common.annotation.HashId;
import torna.common.bean.Result;
import torna.common.enums.RoleEnum;
import torna.service.ProjectService;
import torna.service.dto.ProjectUserDTO;
import torna.web.controller.project.param.ProjectMemberAddParam;
import torna.web.controller.project.param.ProjectMemberRemoveParam;
import torna.web.controller.project.param.ProjectMemberUpdateParam;

import javax.validation.Valid;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("project/member")
public class ProjectMemberController {

    @Autowired
    private ProjectService projectService;

    /**
     * 分页查询项目成员
     * @return
     */
    @GetMapping("/page")
    public Result<PageEasyui<ProjectUserDTO>> page(
            @HashId
            Long projectId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false)  String roleCode
    ) {
        PageEasyui<ProjectUserDTO> projectUser = projectService.pageProjectUser(
                projectId
                , username
                , RoleEnum.of(roleCode)
        );
        return Result.ok(projectUser);
    }

    /**
     * 添加项目成员
     * @param param
     * @return
     */
    @PostMapping("add")
    public Result add(@RequestBody @Valid ProjectMemberAddParam param) {
        projectService.addProjectUser(
                param.getProjectId()
                , param.getUserIds()
                , RoleEnum.of(param.getRoleCode())
        );
        return Result.ok();
    }

    /**
     * 修改成员
     * @param param
     * @return
     */
    @PostMapping("update")
    public Result update(@RequestBody @Valid ProjectMemberUpdateParam param) {
        projectService.updateProjectUserRole(
                param.getProjectId()
                , param.getUserId()
                , RoleEnum.of(param.getRoleCode())
        );
        return Result.ok();
    }

    /**
     * 移除用户
     * @param param
     */
    @PostMapping("remove")
    public Result remove(@RequestBody @Valid ProjectMemberRemoveParam param) {
        projectService.removeProjectUser(param.getProjectId(), param.getUserId());
        return Result.ok();
    }

}
