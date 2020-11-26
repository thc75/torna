package torna.service;

import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import torna.common.bean.Booleans;
import torna.common.bean.User;
import torna.common.enums.RoleEnum;
import torna.common.support.BaseService;
import torna.common.util.CopyUtil;
import torna.dao.entity.Project;
import torna.dao.entity.ProjectUser;
import torna.dao.entity.UserInfo;
import torna.dao.mapper.ProjectMapper;
import torna.dao.mapper.ProjectUserMapper;
import torna.service.dto.ProjectAddDTO;
import torna.service.dto.ProjectDTO;
import torna.service.dto.ProjectInfoDTO;
import torna.service.dto.ProjectUpdateDTO;
import torna.service.dto.ProjectUserDTO;
import torna.service.dto.UserInfoDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class ProjectService extends BaseService<Project, ProjectMapper> {

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 创建项目
     * @param projectAddDTO 项目信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void addProject(ProjectAddDTO projectAddDTO) {
        Query query = new Query().eq("creator_id", projectAddDTO.getCreatorId())
                .eq("name", projectAddDTO.getName());
        Project exist = get(query);
        Assert.isNull(exist, () -> projectAddDTO.getName() + "已存在");
        Project project = CopyUtil.copyBean(projectAddDTO, Project::new);
        this.saveIgnoreNull(project);

        this.saveLeader(project.getId(), projectAddDTO.getLeaderIds());
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveLeader(long projectId, List<Long> leaderIds) {
        Assert.notEmpty(leaderIds, () -> "组长不能为空");
        // 1. 移除现有的组长
        projectUserMapper.removeProjectLeader(projectId);
        // 2. 添加新组长
        List<ProjectUser> projectUsers = leaderIds
                .stream()
                .map(leaderId -> {
                    ProjectUser projectUser = new ProjectUser();
                    projectUser.setUserId(leaderId);
                    projectUser.setProjectId(projectId);
                    projectUser.setRoleCode(RoleEnum.LEADER.getCode());
                    return projectUser;
                })
                .collect(Collectors.toList());
        projectUserMapper.insertBatch(projectUsers);
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
        Map<String, Object> set = new HashMap<>(4);
        set.put("is_deleted", Booleans.TRUE);
        Query query = new Query().eq("project_id", projectId)
                .eq("user_id", userId);
        projectUserMapper.updateByMap(set, query);
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
        CopyUtil.copyProperties(projectUpdateDTO, project);
        this.updateIgnoreNull(project);

        List<Long> leaderIds = projectUpdateDTO.getLeaderIds();
        this.saveLeader(project.getId(), leaderIds);
    }

    /**
     * 获取项目下的用户
     * @param projectId 项目id
     * @param username 登录名
     * @param roleEnum 项目角色
     * @return
     */
    public PageEasyui<ProjectUserDTO> pageProjectUser(long projectId, String username, RoleEnum roleEnum) {
        List<ProjectUser> spaceUsers = listProjectUser(projectId, roleEnum);
        if (CollectionUtils.isEmpty(spaceUsers)) {
            return new PageEasyui<>();
        }
        Map<Long, ProjectUser> userIdMap = spaceUsers.stream()
                .collect(Collectors.toMap(ProjectUser::getUserId, Function.identity()));
        Query query = new Query()
                .in("id", userIdMap.keySet())
                .orderby("id", Sort.DESC);
        if (StringUtils.hasLength(username)) {
            query.like("username", username);
        }
        PageEasyui<ProjectUserDTO> pageInfo = MapperUtil.queryForEasyuiDatagrid(userInfoService.getMapper(), query, ProjectUserDTO.class);
        // 设置添加时间
        pageInfo.getRows().forEach(userInfoDTO -> {
            ProjectUser projectUser = userIdMap.get(userInfoDTO.getId());
            userInfoDTO.setGmtCreate(projectUser.getGmtCreate());
            userInfoDTO.setRoleCode(projectUser.getRoleCode());
        });
        return pageInfo;
    }

    /**
     * 获取项目信息
     * @param projectId
     * @return
     */
    public ProjectInfoDTO getProjectInfo(long projectId) {
        Project project = this.getById(projectId);
        ProjectInfoDTO projectInfoDTO = CopyUtil.copyBean(project, ProjectInfoDTO::new);
        List<UserInfoDTO> userInfoDTOS = this.listProjectLeader(projectId);
        Long creatorId = project.getCreatorId();
        UserInfo userInfo = userInfoService.getById(creatorId);
        projectInfoDTO.setCreator(userInfo.getRealname());
        projectInfoDTO.setLeaders(userInfoDTOS);
        return projectInfoDTO;
    }

    /**
     * 获取项目组长
     * @param projectId
     * @return
     */
    public List<UserInfoDTO> listProjectLeader(long projectId) {
        List<ProjectUser> projectUsers = this.listProjectUser(projectId, RoleEnum.LEADER);
        List<Long> leaderIds = CopyUtil.copyList(projectUsers, ProjectUser::getUserId);
        return userInfoService.listUserInfo(leaderIds);
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

    public List<ProjectUser> listUserProject(User user) {
        return projectUserMapper.listByColumn("user_id", user.getUserId());
    }

    /**
     * 查询空间下所有项目
     * @param spaceId 空间ID
     * @return 返回项目列表
     */
    public List<ProjectDTO> listSpaceProject(long spaceId) {
        List<Project> projects = this.list("space_id", spaceId);
        return CopyUtil.copyList(projects, ProjectDTO::new);
    }
   
}