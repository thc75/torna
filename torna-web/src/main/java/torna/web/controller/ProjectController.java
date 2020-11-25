package torna.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.annotation.HashId;
import torna.common.bean.Result;
import torna.common.bean.User;
import torna.common.context.UserContext;
import torna.dao.entity.Project;
import torna.service.ProjectService;
import torna.service.dto.ProjectAddDTO;
import torna.service.dto.ProjectInfoDTO;
import torna.service.dto.ProjectUpdateDTO;
import torna.web.controller.param.ProjectParam;

import javax.validation.Valid;

/**
 * 项目信息
 * @author tanghc
 */
@RestController
@RequestMapping("project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * 项目信息
     * @param projectId
     * @return
     */
    @GetMapping("info")
    public Result<ProjectInfoDTO> info(@HashId Long projectId) {
        ProjectInfoDTO projectInfo = projectService.getProjectInfo(projectId);
        return Result.ok(projectInfo);
    }

    /**
     * 修改项目
     * @param param
     * @return
     */
    @PostMapping("update")
    public Result update(@RequestBody @Valid ProjectUpdateDTO param) {
        User user = UserContext.getUser();
        param.setModifierId(user.getUserId());
        projectService.updateProject(param);
        return Result.ok();
    }

    /**
     * 删除项目
     * @param param
     * @return
     */
    @PostMapping("delete")
    public Result delete(@RequestBody @Valid ProjectParam param) {
        Project project = projectService.getById(param.getId());
        projectService.delete(project);
        return Result.ok();
    }

    /**
     * 添加项目
     * @param projectAddDTO
     * @return
     */
    @PostMapping("add")
    public Result add(@RequestBody ProjectAddDTO projectAddDTO) {
        User user = UserContext.getUser();
        projectAddDTO.setCreatorId(user.getUserId());
        projectAddDTO.setCreatorId(user.getUserId());
        projectAddDTO.setModifierId(user.getUserId());
        projectService.addProject(projectAddDTO);
        return Result.ok();
    }


}