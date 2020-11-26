package torna.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import torna.common.bean.User;
import torna.common.context.UserContext;
import torna.common.enums.RoleEnum;
import torna.common.util.IdUtil;
import torna.dao.entity.ProjectUser;
import torna.dao.entity.SpaceUser;
import torna.service.dto.PermDTO;
import torna.service.dto.RightDTO;
import torna.service.dto.RoleDTO;
import torna.service.dto.UserPermDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class PermissionService {

    private static final String PERM_C = "c";
    private static final String PERM_R = "r";
    private static final String PERM_U = "u";
    private static final String PERM_D = "d";
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
        List<PermDTO> perms = rightDTO.getPerms();
        List<RoleDTO> roles = rightDTO.getRoles();
        // key: space:id value:perms
        Map<String, List<String>> permMap = perms
                .stream()
                .collect(Collectors.toMap(permDTO -> permDTO.getPrefix() + ":" + IdUtil.encode(permDTO.getId()), PermDTO::getPerms));
        // key: space:id value: dev
        Map<String, String> roleMap = roles
                .stream()
                .collect(Collectors.toMap(roleDTO -> roleDTO.getPrefix() + ":" + IdUtil.encode(roleDTO.getId()), RoleDTO::getRole));
        UserPermDTO userPermDTO = new UserPermDTO();
        userPermDTO.setPermData(permMap);
        userPermDTO.setRoleData(roleMap);
        userPermDTO.setIsAdmin(BooleanUtils.toIntegerObject(user.isAdmin()).byteValue());
        return userPermDTO;
    }


    public RightDTO getUserPermission(User user) {
        RightDTO rightDTO = new RightDTO();
        RightDTO rightSpace = this.buildSpacePerm(user);
        RightDTO rightProject = this.buildProjectPerm(user);
        rightDTO.addAll(rightSpace);
        rightDTO.addAll(rightProject);
        return rightDTO;
    }

    private RightDTO buildSpacePerm(User user) {
        List<PermDTO> permDTOS = new ArrayList<>();
        List<RoleDTO> roles = new ArrayList<>();
        List<SpaceUser> spaceUsers = spaceService.listUserSpace(user.getUserId());
        // 空间权限
        for (SpaceUser spaceUser : spaceUsers) {
            roles.add(new RoleDTO(PREFIX_SPACE, spaceUser.getSpaceId(), spaceUser.getRoleCode()));
            RoleEnum roleEnum = RoleEnum.of(spaceUser.getRoleCode());
            List<String> perms = buildPerms(roleEnum);
            permDTOS.add(new PermDTO(PREFIX_SPACE, spaceUser.getSpaceId(), perms));
        }
        return new RightDTO(permDTOS, roles);
    }

    private RightDTO buildProjectPerm(User user) {
        List<PermDTO> permDTOS = new ArrayList<>();
        List<RoleDTO> roles = new ArrayList<>();
        List<ProjectUser> projectUsers = projectService.listUserProject(user);
        for (ProjectUser projectUser : projectUsers) {
            roles.add(new RoleDTO(PREFIX_PROJECT, projectUser.getProjectId(), projectUser.getRoleCode()));
            RoleEnum roleEnum = RoleEnum.of(projectUser.getRoleCode());
            List<String> perms = buildPerms(roleEnum);
            permDTOS.add(new PermDTO(PREFIX_PROJECT, projectUser.getProjectId(), perms));
        }
        return new RightDTO(permDTOS, roles);
    }

    private List<String> buildPerms(RoleEnum roleEnum) {
        List<String> perms = Lists.newArrayList(PERM_R);
        if (roleEnum == RoleEnum.DEV || roleEnum == RoleEnum.LEADER) {
            perms.add(PERM_U);
        }
        if (roleEnum == RoleEnum.LEADER) {
            perms.add(PERM_D);
        }
        return perms;
    }
}
