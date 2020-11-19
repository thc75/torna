package torna.web.controller;

import torna.common.bean.Result;
import torna.common.enums.RoleEnum;
import torna.service.ProjectService;
import torna.service.dto.ProjectUserDTO;
import torna.web.controller.param.ProjectMemberAddParam;
import torna.web.controller.param.ProjectMemberRemoveParam;
import torna.web.controller.param.ProjectMemberSearchParam;
import torna.web.controller.param.ProjectMemberUpdateParam;
import com.gitee.fastmybatis.core.support.PageEasyui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 分页查询空间成员
     * @param param
     * @return
     */
    @GetMapping("/page")
    public Result<PageEasyui<ProjectUserDTO>> page(@Valid ProjectMemberSearchParam param) {
        PageEasyui<ProjectUserDTO> projectUser = projectService.pageProjectUser(
                param.getProjectId()
                , param.getUsername()
                , RoleEnum.of(param.getRoleCode())
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
