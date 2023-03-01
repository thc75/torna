package cn.torna.web.controller.admin;

import cn.torna.common.bean.Result;
import cn.torna.dao.entity.Project;
import cn.torna.dao.entity.Space;
import cn.torna.service.ProjectService;
import cn.torna.service.SpaceService;
import cn.torna.web.controller.doc.vo.TreeVO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author thc
 */
@RestController
@RequestMapping("admin/project")
public class AdminProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SpaceService spaceService;

    @GetMapping("tree")
    public Result<List<TreeVO>> listProjectTree() {
        List<Project> projects = projectService.listAll();

        Map<Long, List<Project>> spaceIdProjects = projects.stream()
                .collect(Collectors.groupingBy(Project::getSpaceId));

        Query query = new Query()
                .in("id", spaceIdProjects.keySet())
                .orderby("id", Sort.ASC);

        Map<Long, Space> spaceIdMap = spaceService.list(query)
                .stream()
                .collect(Collectors.toMap(Space::getId, Function.identity()));

        List<TreeVO> treeVOS = new ArrayList<>(spaceIdMap.size());

        for (Map.Entry<Long, Space> entry : spaceIdMap.entrySet()) {
            Long spaceId = entry.getKey();
            Space space = entry.getValue();
            List<Project> projectsList = spaceIdProjects.get(spaceId);
            TreeVO treeVO = new TreeVO(spaceId.toString(), space.getName(), "", (byte) 0);
            List<TreeVO> children = projectsList.stream()
                    .map(project -> {
                        return new TreeVO(project.getId().toString(), project.getName(), spaceId.toString(), (byte) 0);
                    })
                    .collect(Collectors.toList());
            treeVO.setChildren(children);

            treeVOS.add(treeVO);
        }

        return Result.ok(treeVOS);
    }

}
