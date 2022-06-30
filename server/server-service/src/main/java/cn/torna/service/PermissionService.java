package cn.torna.service;

import cn.torna.common.bean.User;
import cn.torna.common.util.IdUtil;
import cn.torna.dao.entity.SpaceUser;
import cn.torna.service.dto.ProjectDTO;
import cn.torna.service.dto.RightDTO;
import cn.torna.service.dto.RoleDTO;
import cn.torna.service.dto.UserPermDTO;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class PermissionService {

    public static final String PREFIX_SPACE = "space";
    public static final String PREFIX_PROJECT = "project";

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private ProjectService projectService;

    public UserPermDTO getUserPerm(User user) {
        if (user == null) {
            return null;
        }
        RightDTO rightDTO = getUserPermission(user);
        List<RoleDTO> roles = rightDTO.getRoles();
        // key: space:id value: dev
        Map<String, String> roleMap = roles
                .stream()
                .collect(Collectors.toMap(roleDTO -> roleDTO.getPrefix() + ":" + IdUtil.encode(roleDTO.getId()), RoleDTO::getRole));
        UserPermDTO userPermDTO = new UserPermDTO();
        userPermDTO.setRoleData(roleMap);
        userPermDTO.setIsSuperAdmin(BooleanUtils.toIntegerObject(user.isSuperAdmin()).byteValue());
        return userPermDTO;
    }


    private RightDTO getUserPermission(User user) {
        RightDTO rightDTO = new RightDTO();
        // 用户所在的空间
        List<SpaceUser> spaceUsers = spaceService.listUserSpace(user.getUserId());
        RightDTO rightSpace = this.buildSpacePerm(spaceUsers);
        RightDTO rightProject = this.buildProjectPerm(spaceUsers, user);
        rightDTO.addAll(rightSpace);
        rightDTO.addAll(rightProject);
        return rightDTO;
    }

    /**
     * 构建空间权限
     * @param spaceUsers 用户空间
     * @return 返回权限信息
     */
    private RightDTO buildSpacePerm(List<SpaceUser> spaceUsers) {
        List<RoleDTO> roles = new ArrayList<>();
        // 空间权限
        for (SpaceUser spaceUser : spaceUsers) {
            roles.add(new RoleDTO(PREFIX_SPACE, spaceUser.getSpaceId(), spaceUser.getRoleCode()));
        }
        return new RightDTO(roles);
    }

    /**
     * 构建空间下的项目权限
     * @param spaceUsers 空间用户
     * @param user 当前用户
     * @return 返回项目权限
     */
    private RightDTO buildProjectPerm(List<SpaceUser> spaceUsers, User user) {
        List<RoleDTO> roles = new ArrayList<>();
        for (SpaceUser spaceUser : spaceUsers) {
            List<ProjectDTO> projectDTOS = projectService.listSpaceUserProject(spaceUser.getSpaceId(), user);
            for (ProjectDTO projectUser : projectDTOS) {
                roles.add(new RoleDTO(PREFIX_PROJECT, projectUser.getId(), projectUser.getRoleCode()));
            }
        }
        return new RightDTO(roles);
    }

}
