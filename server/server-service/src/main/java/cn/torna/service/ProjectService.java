package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.enums.RoleEnum;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.Project;
import cn.torna.dao.entity.ProjectUser;
import cn.torna.dao.entity.Space;
import cn.torna.dao.entity.SpaceUser;
import cn.torna.dao.entity.UserInfo;
import cn.torna.dao.mapper.ProjectMapper;
import cn.torna.dao.mapper.ProjectUserMapper;
import cn.torna.service.dto.ProjectAddDTO;
import cn.torna.service.dto.ProjectDTO;
import cn.torna.service.dto.ProjectInfoDTO;
import cn.torna.service.dto.ProjectUpdateDTO;
import cn.torna.service.dto.ProjectUserDTO;
import cn.torna.service.dto.SpaceDTO;
import cn.torna.service.dto.SpaceInfoDTO;
import cn.torna.service.dto.UserInfoDTO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class ProjectService extends BaseService<Project, ProjectMapper> implements InitializingBean {

    // key: projectId, value:spaceId
    private static final Map<Long, Long> projectIdSpaceIdMap = new HashMap<>(8);

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private SpaceService spaceService;

    /**
     * 创建项目
     * @param projectAddDTO 项目信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void addProject(ProjectAddDTO projectAddDTO) {
        Query query = new Query().eq("space_id", projectAddDTO.getSpaceId())
                .eq("name", projectAddDTO.getName());
        Project exist = get(query);
        Assert.isNull(exist, () -> projectAddDTO.getName() + "已存在");
        Project project = CopyUtil.copyBean(projectAddDTO, Project::new);
        project.setModifierId(projectAddDTO.getCreatorId());
        project.setModifierName(projectAddDTO.getCreatorName());
        this.save(project);

        this.saveLeader(project.getId(), projectAddDTO.getAdminIds());
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveLeader(long projectId, Collection<Long> adminIds) {
        Assert.notEmpty(adminIds, () -> "项目管理员不能为空");
        // 1. 移除现有项目管理员
        removeProjectAdmin(projectId);
        // 2. 添加新项目管理员
        List<ProjectUser> projectUsers = adminIds
                .stream()
                .map(adminId -> {
                    ProjectUser projectUser = new ProjectUser();
                    projectUser.setUserId(adminId);
                    projectUser.setProjectId(projectId);
                    projectUser.setRoleCode(RoleEnum.ADMIN.getCode());
                    return projectUser;
                })
                .collect(Collectors.toList());
        projectUserMapper.insertBatch(projectUsers);
    }

    private void removeProjectAdmin(long projectId) {
        projectUserMapper.removeProjectLeader(projectId, RoleEnum.ADMIN.getCode());
    }

    /**
     * 修改项目用户角色
     * @param projectId projectId
     * @param userId userId
     * @param roleEnum 角色
     */
    public void updateProjectUserRole(long projectId, long userId, RoleEnum roleEnum) {
        Assert.notNull(roleEnum, () -> "角色不能为空");
        Map<String, Object> set = new HashMap<>(4);
        set.put("role_code", roleEnum.getCode());
        Query query = new Query().eq("project_id", projectId)
                .eq("user_id", userId);
        projectUserMapper.updateByMap(set, query);
    }

    /**
     * 移除项目用户
     * @param projectId
     * @param userId
     */
    public void removeProjectUser(long projectId, long userId) {
        Query query = new Query()
                .eq("project_id", projectId)
                .eq("user_id", userId);
        projectUserMapper.forceDeleteByQuery(query);
    }

    /**
     * 添加项目用户
     * @param projectId 空间id
     * @param userIds 用户id列表
     * @param roleEnum 角色
     */
    public void addProjectUser(long projectId, List<Long> userIds, RoleEnum roleEnum) {
        Assert.notEmpty(userIds, () -> "用户不能为空");
        Query query = new Query()
                .eq("project_id", projectId)
                .in("user_id", userIds).setQueryAll(true);
        List<ProjectUser> existUsers = projectUserMapper.list(query);
        userInfoService.checkExist(existUsers, ProjectUser::getUserId);
        List<ProjectUser> tobeSaveList = userIds.stream()
                .map(userId -> {
                    ProjectUser projectUser = new ProjectUser();
                    projectUser.setUserId(userId);
                    projectUser.setProjectId(projectId);
                    projectUser.setRoleCode(roleEnum.getCode());
                    return projectUser;
                }).collect(Collectors.toList());
        projectUserMapper.insertBatch(tobeSaveList);
    }

    /**
     * 修改项目信息
     * @param projectUpdateDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(ProjectUpdateDTO projectUpdateDTO) {
        Project project = getById(projectUpdateDTO.getId());
        Long spaceIdOld = project.getSpaceId();
        Long spaceIdNew = projectUpdateDTO.getSpaceId();
        CopyUtil.copyProperties(projectUpdateDTO, project);
        this.update(project);

        List<Long> adminIds = projectUpdateDTO.getAdminIds();
        this.saveLeader(project.getId(), adminIds);
        // 如果两个空间id不一样，表示项目转移到另外一个空间
        // 需要把原项目的用户复制到新空间
        if (!Objects.equals(spaceIdOld, spaceIdNew)) {
            List<ProjectUser> projectUsers = this.listProjectUser(project.getId(), null);
            spaceService.transformSpaceUser(projectUsers, spaceIdNew);
        }
    }

    /**
     * 获取项目下的用户
     * @param projectId 项目id
     * @param username 登录名
     * @param roleEnum 项目角色
     * @return
     */
    public List<ProjectUserDTO> pageProjectUser(long projectId, String username, RoleEnum roleEnum) {
        List<ProjectUser> spaceUsers = listProjectUser(projectId, roleEnum);
        if (CollectionUtils.isEmpty(spaceUsers)) {
            return Collections.emptyList();
        }
        Map<Long, ProjectUser> userIdMap = spaceUsers.stream()
                .collect(Collectors.toMap(ProjectUser::getUserId, Function.identity()));
        Query query = new Query()
                .in("id", userIdMap.keySet())
                .orderby("id", Sort.DESC);
        if (StringUtils.hasText(username)) {
            query.and(q -> q.like("username", username)
                    .orLike("nickname", username)
                    .orLike("email", username)
            );
        }
        List<UserInfo> userInfos = userInfoService.listAll(query);
        List<ProjectUserDTO> projectUserDTOList = CopyUtil.copyList(userInfos, ProjectUserDTO::new);
        // 设置添加时间
        projectUserDTOList.forEach(userInfoDTO -> {
            ProjectUser projectUser = userIdMap.get(userInfoDTO.getId());
            userInfoDTO.setGmtCreate(projectUser.getGmtCreate());
            userInfoDTO.setRoleCode(projectUser.getRoleCode());
        });
        return projectUserDTOList;
    }

    /**
     * 获取项目信息
     * @param projectId
     * @return
     */
    public ProjectInfoDTO getProjectInfo(long projectId) {
        Project project = this.getById(projectId);
        Space space = spaceService.getById(project.getSpaceId());
        ProjectInfoDTO projectInfoDTO = CopyUtil.copyBean(project, ProjectInfoDTO::new);
        List<UserInfoDTO> userInfoDTOS = this.listProjectLeader(projectId);
        projectInfoDTO.setAdmins(userInfoDTOS);
        projectInfoDTO.setSpace(CopyUtil.copyBean(space, SpaceDTO::new));
        return projectInfoDTO;
    }

    /**
     * 获取项目组长
     * @param projectId
     * @return
     */
    public List<UserInfoDTO> listProjectLeader(long projectId) {
        List<ProjectUser> projectUsers = this.listProjectUser(projectId, RoleEnum.ADMIN);
        List<Long> adminIds = CopyUtil.copyList(projectUsers, ProjectUser::getUserId);
        return userInfoService.listUserInfo(adminIds);
    }

    public List<ProjectUser> listProjectUser(long projectId, RoleEnum roleEnum) {
        Query query = new Query()
                .eq("project_id", projectId)
                .setQueryAll(true);
        if (roleEnum != null) {
            query.eq("role_code", roleEnum.getCode());
        }
        return projectUserMapper.list(query);
    }

    public List<ProjectUser> listUserProject(long userId) {
        return projectUserMapper.listByColumn("user_id", userId);
    }

    /**
     * 获取用户能看到的项目
     * @param user 用户
     * @return 没有返回空list
     */
    public List<Project> listUserProject(User user) {
        if (user.isSuperAdmin()) {
            return this.listAll();
        }
        List<ProjectUser> projectUsers = listUserProject(user.getUserId());
        if (CollectionUtils.isEmpty(projectUsers)) {
            return Collections.emptyList();
        }
        List<Long> projectIds = projectUsers.stream().map(ProjectUser::getProjectId).distinct().collect(Collectors.toList());
        Query query = new Query()
                .in("id", projectIds)
                .orderby("id", Sort.ASC);
        return list(query);
    }

    /**
     * 查询用户在空间下能访问的项目
     * @param spaceId 空间ID
     * @param user user
     * @return 返回项目列表
     */
    public List<ProjectDTO> listSpaceUserProject(long spaceId, User user) {
        SpaceUser spaceUser = spaceService.getSpaceUser(spaceId, user.getUserId());
        // 不是超级管理员且不在空间里面，无法访问项目
        if (!user.isSuperAdmin() && spaceUser == null) {
            return Collections.emptyList();
        }
        // 返回空间下所有的项目
        List<Project> spaceAllProjects = this.listSpaceProject(spaceId);
        // 如果是超级管理员，可查看所有
        if (user.isSuperAdmin()) {
            return CopyUtil.copyList(spaceAllProjects, ProjectDTO::new, projectDTO -> projectDTO.setRoleCode(RoleEnum.ADMIN.getCode()));
        }
        List<Long> adminIdList = spaceService.listSpaceAdminId(spaceId);
        // 如果是空间管理员，可以访问下面所有项目，且角色是项目管理员
        if (adminIdList.contains(user.getUserId())) {
            return CopyUtil.copyList(spaceAllProjects, ProjectDTO::new, projectDTO -> projectDTO.setRoleCode(RoleEnum.ADMIN.getCode()));
        }
        // 查询用户加入的项目
        List<ProjectUser> projectUsers = this.listUserProject(user.getUserId());
        // key: projectId, key: roleCode
        Map<Long, String> userProjectRoleMap = projectUsers.stream()
                .collect(Collectors.toMap(ProjectUser::getProjectId, ProjectUser::getRoleCode));
        return spaceAllProjects.stream()
                .filter(project -> {
                    // 如果是空间下的公开项目，可以访问
                    if (!Booleans.isTrue(project.getIsPrivate())) {
                        return true;
                    }
                    // 用户是否被加入到该项目中
                    return userProjectRoleMap.containsKey(project.getId());
                })
                .map(project -> {
                    ProjectDTO projectDTO = CopyUtil.copyBean(project, ProjectDTO::new);
                    String roleCode = userProjectRoleMap.getOrDefault(project.getId(), RoleEnum.GUEST.getCode());
                    projectDTO.setRoleCode(roleCode);
                    return projectDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 查询空间下所有项目
     * @param spaceId 空间ID
     * @return 返回项目列表
     */
    private List<Project> listSpaceProject(long spaceId) {
        return this.list("space_id", spaceId);
    }

    /**
     * 获取spaceId
     * @param projectId
     * @return
     */
    public Long getSpaceId(Long projectId) {
        return projectIdSpaceIdMap.computeIfAbsent(projectId, k -> {
            Project project = getById(projectId);
            return Optional.ofNullable(project).map(Project::getSpaceId).orElse(null);
        });

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<Long, Long> map = this.list(new Query())
                .stream()
                .collect(Collectors.toMap(Project::getId, Project::getSpaceId));
        projectIdSpaceIdMap.putAll(map);
    }
}