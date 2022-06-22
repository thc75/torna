package cn.torna.web.controller.space;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.service.ComposeProjectService;
import cn.torna.service.ProjectService;
import cn.torna.service.SpaceService;
import cn.torna.service.dto.ComposeProjectDTO;
import cn.torna.service.dto.ProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 空间项目
 * @author tanghc
 */
@RestController
@RequestMapping("space/project")
public class SpaceProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ComposeProjectService composeProjectService;

    @Autowired
    private SpaceService spaceService;

    /**
     * 返回空间中的项目
     * @param spaceId 空间id
     * @return
     */
    @GetMapping("/list")
    public Result<List<ProjectDTO>> listSpaceProject(@HashId Long spaceId) {
        User user = UserContext.getUser();
        List<ProjectDTO> projectDTOS = projectService.listSpaceUserProject(spaceId, user);
        return Result.ok(projectDTOS);
    }

    /**
     * 返回空间中的聚合项目
     * @param spaceId 空间id
     * @return
     */
    @GetMapping("/compose/list")
    public Result<List<ComposeProjectDTO>> listSpaceComposeProject(@HashId Long spaceId) {
        User user = UserContext.getUser();
        List<ComposeProjectDTO> projectDTOS = composeProjectService.listSpaceUserProject(spaceId, user);
        return Result.ok(projectDTOS);
    }


}
