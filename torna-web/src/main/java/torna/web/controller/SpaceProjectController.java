package torna.web.controller;

import torna.common.bean.Result;
import torna.service.ProjectService;
import torna.service.dto.ProjectDTO;
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

    /**
     * 返回空间中的项目
     * @param spaceId 空间id
     * @return
     */
    @GetMapping("/list")
    public Result<List<ProjectDTO>> listSpaceProject(long spaceId) {
        List<ProjectDTO> projectDTOS = projectService.listSpaceProject(spaceId);
        return Result.ok(projectDTOS);
    }


}
