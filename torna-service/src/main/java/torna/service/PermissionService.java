package torna.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import torna.common.bean.User;
import torna.common.util.IdUtil;
import torna.dao.entity.ProjectUser;
import torna.dao.entity.SpaceUser;
import torna.service.dto.RightDTO;
import torna.service.dto.RoleDTO;
import torna.service.dto.UserPermDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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
        RightDTO rightSpace = this.buildSpacePerm(user);
        RightDTO rightProject = this.buildProjectPerm(user);
        rightDTO.addAll(rightSpace);
        rightDTO.addAll(rightProject);
        return rightDTO;
    }

    private RightDTO buildSpacePerm(User user) {
        List<RoleDTO> roles = new ArrayList<>();
        List<SpaceUser> spaceUsers = spaceService.listUserSpace(user.getUserId());
        // 空间权限
        for (SpaceUser spaceUser : spaceUsers) {
            roles.add(new RoleDTO(PREFIX_SPACE, spaceUser.getSpaceId(), spaceUser.getRoleCode()));
        }
        return new RightDTO(roles);
    }

    private RightDTO buildProjectPerm(User user) {
        List<RoleDTO> roles = new ArrayList<>();
        List<ProjectUser> projectUsers = projectService.listUserProject(user);
        for (ProjectUser projectUser : projectUsers) {
            roles.add(new RoleDTO(PREFIX_PROJECT, projectUser.getProjectId(), projectUser.getRoleCode()));
        }
        return new RightDTO(roles);
    }

    private static <T> LoadingCache<Long, Optional<T>> buildCache(int timeout) {
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        if (timeout > 0) {
            cacheBuilder.expireAfterAccess(timeout, TimeUnit.SECONDS);
        }
        return cacheBuilder
                .build(new CacheLoader<Long, Optional<T>>() {
                    @Override
                    public Optional<T> load(Long key) throws Exception {
                        return Optional.empty();
                    }
                });
    }
}
