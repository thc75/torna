package torna.web.controller.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
import torna.dao.entity.Space;
import torna.service.ProjectService;
import torna.service.SpaceService;
import torna.service.dto.ProjectAddDTO;
import torna.service.dto.ProjectInfoDTO;
import torna.service.dto.ProjectUpdateDTO;
import torna.web.controller.project.param.ProjectParam;
import torna.web.controller.project.vo.ProjectSpaceVO;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collections;

/**
 * 项目信息
 * @author tanghc
 */
@RestController
@RequestMapping("project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SpaceService spaceService;

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

    @GetMapping("space")
    public Result<ProjectSpaceVO> space(@HashId Long projectId) {
        Project project = projectService.getById(projectId);
        Space space = spaceService.getById(project.getSpaceId());
        ProjectSpaceVO projectSpaceVO = new ProjectSpaceVO();
        projectSpaceVO.setProjectId(projectId);
        projectSpaceVO.setProjectName(project.getName());
        projectSpaceVO.setSpaceId(space.getId());
        projectSpaceVO.setSpaceName(space.getName());
        return Result.ok(projectSpaceVO);
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
        param.setModifierName(user.getNickname());
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
        projectAddDTO.setCreatorName(user.getNickname());
        if (CollectionUtils.isEmpty(projectAddDTO.getAdminIds())) {
            projectAddDTO.setAdminIds(Collections.singletonList(user.getUserId()));
        }
        projectService.addProject(projectAddDTO);
        return Result.ok();
    }


}