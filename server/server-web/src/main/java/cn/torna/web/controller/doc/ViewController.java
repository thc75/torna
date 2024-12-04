package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.Project;
import cn.torna.dao.entity.Space;
import cn.torna.dao.entity.SpaceUser;
import cn.torna.service.DocInfoService;
import cn.torna.service.DocViewService;
import cn.torna.service.ProjectService;
import cn.torna.service.SpaceService;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.ProjectDTO;
import cn.torna.service.dto.SpaceProjectDTO;
import cn.torna.service.dto.TreeDTO;
import cn.torna.web.config.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Slf4j
@RestController
@RequestMapping("doc/view")
public class ViewController {

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private DocViewService docViewService;

    /**
     * 文档预览页左侧树，空间维度
     *
     * @param spaceId 空间id
     * @return 返回树状菜单
     */
    @GetMapping("data")
    public Result<List<TreeDTO>> data(@HashId Long spaceId) {
        // 获取空间下的项目
        User user = UserContext.getUser();
        List<TreeDTO> retList = docViewService.data(spaceId, user);
        return Result.ok(retList);
    }

    /**
     * 文档预览页左侧树，项目维度
     *
     * @param projectId 项目id
     * @return 返回树状菜单
     */
    @GetMapping("dataByProject")
    public Result<List<TreeDTO>> dataByProject(@HashId Long projectId) {
        List<TreeDTO> retList = docViewService.dataByProject(projectId);
        return Result.ok(retList);
    }

    @GetMapping("projects")
    public Result<List<SpaceProjectDTO>> projects() {
        // 获取空间下的项目
        User user = UserContext.getUser();
        List<Project> projects = projectService.listUserProject(user);
        Map<Long, List<Project>> spaceIdMap = projects.stream().collect(Collectors.groupingBy(Project::getSpaceId));
        Set<Long> spaceIdList = spaceIdMap.keySet();
        if (CollectionUtils.isEmpty(spaceIdList)) {
            return Result.ok(Collections.emptyList());
        }
        List<Space> spaces = spaceService.list(Space::getId, spaceIdList);

        List<SpaceProjectDTO> list = spaces.stream()
                .map(space -> {
                    SpaceProjectDTO spaceProjectDTO = CopyUtil.copyBean(space, SpaceProjectDTO::new);
                    List<Project> projectList = spaceIdMap.getOrDefault(space.getId(), Collections.emptyList());
                    spaceProjectDTO.setProjects(CopyUtil.copyList(projectList, ProjectDTO::new));
                    return spaceProjectDTO;
                })
                .collect(Collectors.toList());
        return Result.ok(list);
    }

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 返回记录，没有返回null
     */
    @GetMapping("detail")
    public Result<DocInfoDTO> detail(@HashId Long id) {
        DocInfoDTO docInfoDTO = docInfoService.getDocDetailView(id);
        User user = UserContext.getUser();
        SpaceUser spaceUser = spaceService.getSpaceUser(docInfoDTO.getSpaceId(), user.getUserId());
        if (!user.isSuperAdmin() && spaceUser == null) {
            throw new BizException("无权限访问");
        }
        return Result.ok(docInfoDTO);
    }


}
