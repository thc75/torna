package cn.torna.service;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.IdGen;
import cn.torna.common.util.IdUtil;
import cn.torna.common.util.TreeUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.Project;
import cn.torna.dao.entity.Space;
import cn.torna.service.dto.DocTreeDTO;
import cn.torna.service.dto.ProjectDTO;
import cn.torna.service.dto.TreeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author thc
 */
@Service
@Slf4j
public class DocViewService {

    public static final byte TYPE_PROJECT = 0;
    public static final byte TYPE_MODULE = 1;
    public static final byte TYPE_FOLDER = 2;
    public static final byte TYPE_DOC = 3;

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
     *
     * @param spaceId 空间id
     * @return 返回树状菜单
     */
    public List<TreeDTO> data(@HashId Long spaceId, User user) {
        // 获取空间下的项目
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
                    docInfoVO.setVersion(docInfo.getVersion());
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
        List<TreeDTO> retList = filterEmptyFolder(list);
        return retList;
    }

    /**
     * 文档预览页左侧树，项目维度
     *
     * @param projectId 项目id
     * @return 返回树状菜单
     */
    public List<TreeDTO> dataByProject(@HashId Long projectId) {
        Project project = projectService.getById(projectId);
        ProjectDTO projectDTO = CopyUtil.copyBean(project, ProjectDTO::new);
        List<TreeDTO> list = new ArrayList<>();
        List<Module> modules = moduleService.listProjectModules(projectDTO.getId());
        for (Module module : modules) {
            TreeDTO moduleVO = new TreeDTO(IdGen.nextId(), module.getName(), "", TYPE_MODULE);
            list.add(moduleVO);
            StopWatch stopWatch1 = new StopWatch();
            stopWatch1.start();
            List<DocInfo> docInfos = docInfoService.listDocMenuView(module.getId());
            stopWatch1.stop();
            log.debug("查询【{}】下的接口，耗时:{}", module.getName(), stopWatch1.getTotalTimeSeconds());
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
                docInfoVO.setVersion(docInfo.getVersion());
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
        return filterEmptyFolder(list);
    }

    /**
     * 文档预览页左侧树，应用维度
     *
     * @param module module
     * @return 返回树状菜单
     */
    public List<TreeDTO> dataByModule(Module module) {
        List<TreeDTO> list = new ArrayList<>();
        TreeDTO moduleVO = new DocTreeDTO(IdGen.nextId(), module.getName(), "", TYPE_MODULE);
        list.add(moduleVO);
        List<DocInfo> docInfos = docInfoService.listDocMenuView(module.getId());
        String base = moduleVO.getId();
        for (DocInfo docInfo : docInfos) {
            boolean isFolder = Booleans.isTrue(docInfo.getIsFolder());
            String id = isFolder ? DocViewService.buildId(base, docInfo.getId()) : IdUtil.encode(docInfo.getId());
            String parentId = DocViewService.buildParentId(base, docInfo.getParentId());
            byte type = isFolder ? TYPE_FOLDER : TYPE_DOC;
            TreeDTO docInfoVO = new DocTreeDTO(id, docInfo.getName(), parentId, type);
            docInfoVO.setHttpMethod(docInfo.getHttpMethod());
            docInfoVO.setDocType(docInfo.getType());
            docInfoVO.setDocId(docInfo.getId());
            docInfoVO.setVersion(docInfo.getVersion());
            // 如果是文档
            if (!isFolder) {
                docInfoVO.setUrl(docInfo.getUrl());
                List<String> originInfo = Arrays.asList(
                        "",
                        "",
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
        DocViewService.calcDocCount(list);
        return filterEmptyFolder(list);
    }

    private List<TreeDTO> filterEmptyFolder(List<TreeDTO> list) {
        return list.stream()
                .filter(treeDTO -> {
                    // 空文件夹不显示
                    if (Objects.equals(treeDTO.getType(), TYPE_FOLDER) && treeDTO.getApiCount() == 0) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * 计算文档数量
     *
     * @param list 文档列表
     */
    public static void calcDocCount(List<TreeDTO> list) {
        Map<String, TreeDTO> idMap = list.stream().collect(Collectors.toMap(TreeDTO::getId, Function.identity()));
        TreeUtil.initParent(list, "", idMap);
        for (TreeDTO TreeDTO : list) {
            // 如果是文档类型，父节点数量+1
            if (Objects.equals(TreeDTO.getType(), TYPE_DOC)) {
                TreeDTO parent = TreeDTO.getParent();
                if (parent != null) {
                    parent.addApiCount();
                }
            }
        }
    }

    /**
     * list转换成tree
     *
     * @param list
     * @param parentId 当前父节点id
     * @return
     */
    public static List<TreeDTO> convertTree(List<TreeDTO> list, String parentId, Map<String, TreeDTO> idMap) {
        if (parentId == null) {
            parentId = "";
        }
        if (list == null) {
            return Collections.emptyList();
        }
        List<TreeDTO> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TreeDTO item = list.get(i);
            // 如果有子节点
            if (Objects.equals(item.getParentId(), parentId)) {
                List<TreeDTO> children = convertTree(list, item.getId(), idMap);
                item.setChildren(children);
                TreeDTO parent = idMap.get(item.getParentId());
                item.setParent(parent);
                temp.add(item);
            }
        }
        return temp;
    }


    public static String buildId(String base, Long id) {
        return DigestUtils.md5DigestAsHex((base + id).getBytes(StandardCharsets.UTF_8));
    }

    public static String buildParentId(String base, Long parentId) {
        if (parentId == null || parentId == 0) {
            return base;
        }
        return buildId(base, parentId);
    }

}
