package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.IdGen;
import cn.torna.common.util.IdUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.Project;
import cn.torna.dao.entity.Space;
import cn.torna.dao.entity.SpaceUser;
import cn.torna.service.DocInfoService;
import cn.torna.service.DocViewService;
import cn.torna.service.ModuleService;
import cn.torna.service.ProjectService;
import cn.torna.service.SpaceService;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.ProjectDTO;
import cn.torna.service.dto.SpaceProjectDTO;
import cn.torna.service.dto.TreeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc/view")
public class ViewController {

    private static final byte TYPE_PROJECT = DocViewService.TYPE_PROJECT;
    private static final byte TYPE_MODULE = DocViewService.TYPE_MODULE;
    private static final byte TYPE_FOLDER = DocViewService.TYPE_FOLDER;
    private static final byte TYPE_DOC = DocViewService.TYPE_DOC;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private DocInfoService docInfoService;

    /**
     * 文档预览页左侧树，空间维度
     * @param spaceId 空间id
     * @return 返回树状菜单
     */
    @GetMapping("data")
    public Result<List<TreeDTO>> data(@HashId Long spaceId) {
        // 获取空间下的项目
        User user = UserContext.getUser();
        Space space = spaceService.getById(spaceId);
        List<ProjectDTO> projectDTOS = projectService.listSpaceUserProject(spaceId, user);
        List<TreeDTO> list = new ArrayList<>();
        for (ProjectDTO projectDTO : projectDTOS) {
            TreeDTO projectVO = new TreeDTO(IdGen.nextId(), projectDTO.getName(), "", TYPE_PROJECT);
            list.add(projectVO);
            List<Module> modules = moduleService.listProjectModules(projectDTO.getId());
            for (Module module : modules) {
                TreeDTO moduleVO = new TreeDTO(IdGen.nextId(), module.getName(), projectVO.getId(), TYPE_MODULE);
                list.add(moduleVO);
                List<DocInfo> docInfos = docInfoService.listDocMenuView(module.getId());
                String base = moduleVO.getId();
                for (DocInfo docInfo : docInfos) {
                    boolean isFolder = Booleans.isTrue(docInfo.getIsFolder());
                    String id = isFolder ? DocViewService.buildId(base, docInfo.getId()) : IdUtil.encode(docInfo.getId());
                    String parentId = DocViewService.buildParentId(base, docInfo.getParentId());
                    byte type = isFolder ? TYPE_FOLDER : TYPE_DOC;
                    TreeDTO docInfoVO = new TreeDTO(id, docInfo.getName(), parentId, type);
                    docInfoVO.setHttpMethod(docInfo.getHttpMethod());
                    docInfoVO.setDocType(docInfo.getType());
                    docInfoVO.setDocId(docInfo.getId());
                    // 如果是文档
                    if (!isFolder) {
                        docInfoVO.setUrl(docInfo.getUrl());
                        List<String> originInfo = Arrays.asList(
                                space.getName(),
                                projectDTO.getName(),
                                module.getName()
                                );
                        docInfoVO.setOrigin(String.join("/", originInfo));
                        String deprecated = docInfo.getDeprecated();
                        if (deprecated == null) {
                            deprecated = "$false$";
                        }
                        docInfoVO.setDeprecated(deprecated);
                    }
                    list.add(docInfoVO);
                }
            }
        }
        DocViewService.calcDocCount(list);
        return Result.ok(list);
    }

    /**
     * 文档预览页左侧树，项目维度
     * @param projectId 项目id
     * @return 返回树状菜单
     */
    @GetMapping("dataByProject")
    public Result<List<TreeDTO>> dataByProject(@HashId Long projectId) {
        Project project = projectService.getById(projectId);
        ProjectDTO projectDTO = CopyUtil.copyBean(project, ProjectDTO::new);
        List<TreeDTO> list = new ArrayList<>();
        List<Module> modules = moduleService.listProjectModules(projectDTO.getId());
        for (Module module : modules) {
            TreeDTO moduleVO = new TreeDTO(IdGen.nextId(), module.getName(), "", TYPE_MODULE);
            list.add(moduleVO);
            List<DocInfo> docInfos = docInfoService.listDocMenuView(module.getId());
            String base = moduleVO.getId();
            for (DocInfo docInfo : docInfos) {
                boolean isFolder = Booleans.isTrue(docInfo.getIsFolder());
                String id = isFolder ? DocViewService.buildId(base, docInfo.getId()) : IdUtil.encode(docInfo.getId());
                String parentId = DocViewService.buildParentId(base, docInfo.getParentId());
                byte type = isFolder ? TYPE_FOLDER : TYPE_DOC;
                TreeDTO docInfoVO = new TreeDTO(id, docInfo.getName(), parentId, type);
                docInfoVO.setHttpMethod(docInfo.getHttpMethod());
                docInfoVO.setDocType(docInfo.getType());
                docInfoVO.setDocId(docInfo.getId());
                // 如果是文档
                if (!isFolder) {
                    docInfoVO.setUrl(docInfo.getUrl());
                    List<String> originInfo = Arrays.asList(
                            "",
                            projectDTO.getName(),
                            module.getName()
                    );
                    String deprecated = docInfo.getDeprecated();
                    if (deprecated == null) {
                        deprecated = "$false$";
                    }
                    docInfoVO.setDeprecated(deprecated);
                    docInfoVO.setOrigin(String.join("/", originInfo));
                }
                list.add(docInfoVO);
            }
        }
        DocViewService.calcDocCount(list);
        return Result.ok(list);
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
        List<Space> spaces = spaceService.listByCollection("id", spaceIdList);

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
