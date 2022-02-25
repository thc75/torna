package cn.torna.service;

import cn.torna.common.bean.User;
import cn.torna.common.enums.ComposeProjectTypeEnum;
import cn.torna.common.enums.RoleEnum;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.PasswordUtil;
import cn.torna.dao.entity.ComposeProject;
import cn.torna.dao.entity.SpaceUser;
import cn.torna.dao.mapper.ComposeProjectMapper;
import cn.torna.service.dto.ComposeProjectAddDTO;
import cn.torna.service.dto.ComposeProjectDTO;
import cn.torna.service.dto.ComposeProjectUpdateDTO;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class ComposeProjectService extends BaseService<ComposeProject, ComposeProjectMapper> {

    @Autowired
    private SpaceService spaceService;

    /**
     * 创建项目
     *
     * @param projectAddDTO 项目信息
     */
    public void addProject(ComposeProjectAddDTO projectAddDTO) {
        Query query = new Query().eq("space_id", projectAddDTO.getSpaceId())
                .eq("name", projectAddDTO.getName());
        ComposeProject exist = get(query);
        Assert.isNull(exist, () -> projectAddDTO.getName() + "已存在");
        ComposeProject project = CopyUtil.copyBean(projectAddDTO, ComposeProject::new);
        if (projectAddDTO.getType() == ComposeProjectTypeEnum.ENCRYPT.getType()) {
            String pwd = PasswordUtil.getRandomSimplePassword(4);
            project.setPassword(pwd);
        }
        project.setModifierId(projectAddDTO.getCreatorId());
        project.setModifierName(projectAddDTO.getCreatorName());
        this.save(project);
    }

    public void updateProject(ComposeProjectUpdateDTO composeProjectUpdateDTO) {
        ComposeProject composeProject = getById(composeProjectUpdateDTO.getId());
        CopyUtil.copyPropertiesIgnoreNull(composeProjectUpdateDTO, composeProject);
        if (composeProjectUpdateDTO.getType() == ComposeProjectTypeEnum.ENCRYPT.getType()) {
            if (StringUtils.isEmpty(composeProject.getPassword())) {
                String pwd = PasswordUtil.getRandomSimplePassword(4);
                composeProject.setPassword(pwd);
            }
        } else {
            composeProject.setPassword("");
        }
        update(composeProject);
    }

    /**
     * 查询用户在空间下能访问的项目
     * @param spaceId 空间ID
     * @param user user
     * @return 返回项目列表
     */
    public List<ComposeProjectDTO> listSpaceUserProject(long spaceId, User user) {
        // 返回空间下所有的项目
        List<ComposeProject> spaceAllProjects = this.listSpaceProject(spaceId);
        // 如果是超级管理员，可查看所有
        if (user.isSuperAdmin()) {
            return CopyUtil.copyList(spaceAllProjects, ComposeProjectDTO::new, projectDTO -> projectDTO.setRoleCode(RoleEnum.ADMIN.getCode()));
        }
        List<SpaceUser> spaceUsers = spaceService.listUserSpace(user.getUserId());
        List<Long> spaceIdList = spaceUsers.stream()
                .map(SpaceUser::getSpaceId)
                .collect(Collectors.toList());
        return spaceAllProjects.stream()
                .filter(project -> spaceIdList.contains(project.getSpaceId()))
                .map(project -> {
                    ComposeProjectDTO projectDTO = CopyUtil.copyBean(project, ComposeProjectDTO::new);
                    projectDTO.setRoleCode(RoleEnum.ADMIN.getCode());
                    return projectDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 查询空间下所有项目
     * @param spaceId 空间ID
     * @return 返回项目列表
     */
    private List<ComposeProject> listSpaceProject(long spaceId) {
        return this.list("space_id", spaceId);
    }

}