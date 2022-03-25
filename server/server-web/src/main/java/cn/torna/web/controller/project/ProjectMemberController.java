package cn.torna.web.controller.project;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.enums.RoleEnum;
import cn.torna.service.ProjectService;
import cn.torna.service.dto.ProjectUserDTO;
import cn.torna.web.controller.project.param.ProjectMemberAddParam;
import cn.torna.web.controller.project.param.ProjectMemberRemoveParam;
import cn.torna.web.controller.project.param.ProjectMemberUpdateParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

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
    @GetMapping("/list")
    public Result<List<ProjectUserDTO>> page(
            @HashId
            Long projectId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false)  String roleCode
    ) {
        List<ProjectUserDTO> projectUserDTOList = projectService.pageProjectUser(
                projectId
                , username
                , RoleEnum.of(roleCode)
        );
        return Result.ok(projectUserDTOList);
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
