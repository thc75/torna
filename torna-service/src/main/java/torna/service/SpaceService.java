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
import torna.dao.entity.Space;
import torna.dao.entity.SpaceUser;
import torna.dao.entity.UserInfo;
import torna.dao.mapper.SpaceMapper;
import torna.dao.mapper.SpaceUserMapper;
import torna.service.dto.SpaceAddDTO;
import torna.service.dto.SpaceDTO;
import torna.service.dto.SpaceInfoDTO;
import torna.service.dto.SpaceUserInfoDTO;
import torna.service.dto.UserInfoDTO;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class SpaceService extends BaseService<Space, SpaceMapper> {

    @Autowired
    private SpaceUserMapper spaceUserMapper;

    @Autowired
    private UserInfoService userInfoService;

    @Transactional(rollbackFor = Exception.class)
    public void addSpace(SpaceAddDTO spaceAddDTO) {
        String spaceName = spaceAddDTO.getName();
        Query query = new Query().eq("creator_id", spaceAddDTO.getCreatorId())
                .eq("name", spaceName);
        Space existGroup = this.get(query);
        Assert.isNull(existGroup, () -> spaceName + "已存在");
        Space space = new Space();
        space.setName(spaceName);
        space.setCreatorId(spaceAddDTO.getCreatorId());
        space.setCreatorName(spaceAddDTO.getCreatorName());
        space.setModifierId(spaceAddDTO.getCreatorId());
        space.setModifierName(spaceAddDTO.getCreatorName());
        space.setIsDefault(spaceAddDTO.getIsDefault());
        this.save(space);

        // 添加管理员
        this.addSpaceUser(space.getId(), spaceAddDTO.getAdminIds(), RoleEnum.ADMIN);
    }

    /**
     * 添加空间用户
     * @param spaceId 空间id
     * @param userIds 用户id列表
     * @param roleEnum 角色
     */
    public void addSpaceUser(long spaceId, List<Long> userIds, RoleEnum roleEnum) {
        Assert.notEmpty(userIds, () -> "用户不能为空");
        Query query = new Query()
                .eq("space_id", spaceId)
                .in("user_id", userIds).setQueryAll(true);
        List<SpaceUser> existUsers = spaceUserMapper.list(query);
        userInfoService.checkExist(existUsers, SpaceUser::getUserId);
        List<SpaceUser> tobeSaveList = userIds.stream()
                .map(userId -> {
                    SpaceUser spaceUser = new SpaceUser();
                    spaceUser.setUserId(userId);
                    spaceUser.setSpaceId(spaceId);
                    spaceUser.setRoleCode(roleEnum.getCode());
                    return spaceUser;
                }).collect(Collectors.toList());
        spaceUserMapper.insertBatch(tobeSaveList);
    }

    /**
     * 修改空间用户角色
     * @param spaceId spaceId
     * @param userId userId
     * @param roleEnum 角色
     */
    public void updateSpaceUserRole(long spaceId, long userId, RoleEnum roleEnum) {
        Assert.notNull(roleEnum, () -> "角色不能为空");
        Map<String, Object> set = new HashMap<>(4);
        set.put("role_code", roleEnum.getCode());
        Query query = new Query().eq("space_id", spaceId)
                .eq("user_id", userId);
        spaceUserMapper.updateByMap(set, query);
    }

    /**
     * 获取空间下的用户
     * @param spaceId 空间id
     * @param username 登录名
     * @return
     */
    public PageEasyui<SpaceUserInfoDTO> pageSpaceUser(long spaceId, String username) {
        List<SpaceUser> spaceUsers = listSpaceUser(spaceId);
        if (CollectionUtils.isEmpty(spaceUsers)) {
            return new PageEasyui<>();
        }
        Map<Long, SpaceUser> userIdMap = spaceUsers.stream()
                .collect(Collectors.toMap(SpaceUser::getUserId, Function.identity()));
        Query query = new Query()
                .in("id", userIdMap.keySet())
                .orderby("id", Sort.DESC);
        if (StringUtils.hasLength(username)) {
            query.like("username", username);
        }
        PageEasyui<SpaceUserInfoDTO> pageInfo = MapperUtil.queryForEasyuiDatagrid(userInfoService.getMapper(), query, SpaceUserInfoDTO.class);
        // 设置添加时间
        pageInfo.getRows().forEach(userInfoDTO -> {
            SpaceUser spaceUser = userIdMap.get(userInfoDTO.getId());
            userInfoDTO.setGmtCreate(spaceUser.getGmtCreate());
            userInfoDTO.setRoleCode(spaceUser.getRoleCode());
        });
        return pageInfo;
    }

    /**
     * 查询空间用户
     * @param spaceId
     * @param username 右边匹配 like 'xx%'
     * @return
     */
    public List<UserInfoDTO> searchSpaceUser(long spaceId, String username) {
        if (StringUtils.isEmpty(username)) {
            return Collections.emptyList();
        }
        List<SpaceUser> spaceUsers = listSpaceUser(spaceId);
        Map<Long, SpaceUser> userIdMap = spaceUsers.stream()
                .collect(Collectors.toMap(SpaceUser::getUserId, Function.identity()));
        Query query = new Query()
                .in("id", userIdMap.keySet())
                .likeRight("username", username)
                .orderby("id", Sort.DESC);
        List<UserInfo> userInfoList = userInfoService.list(query);
        return CopyUtil.copyList(userInfoList, UserInfoDTO::new);
    }

    public SpaceInfoDTO getSpaceInfo(long spaceId) {
        Space space = getById(spaceId);
        SpaceInfoDTO spaceInfoDTO = CopyUtil.copyBean(space, SpaceInfoDTO::new);
        List<UserInfoDTO> leaders = this.listSpaceLeader(spaceId);
        spaceInfoDTO.setLeaders(leaders);
        return spaceInfoDTO;
    }

    /**
     * 移除空间成员
     * @param spaceId
     * @param userId
     */
    public void removeMember(long spaceId, long userId) {
        Map<String, Object> set = new HashMap<>(4);
        set.put("is_deleted", Booleans.TRUE);
        Query query = new Query().eq("space_id", spaceId)
                .eq("user_id", userId);
        spaceUserMapper.updateByMap(set, query);
    }


    /**
     * 查询空间管理员信息
     * @param spaceId
     * @return
     */
    public List<UserInfoDTO> listSpaceLeader(long spaceId) {
        Query query = new Query()
                .eq("space_id", spaceId)
                .eq("role_code", RoleEnum.ADMIN.getCode())
                .setQueryAll(true);
        List<SpaceUser> spaceLeaders = spaceUserMapper.list(query);
        List<Long> adminIds = CopyUtil.copyList(spaceLeaders, SpaceUser::getUserId);
        return userInfoService.listUserInfo(adminIds);
    }

    public List<SpaceUser> listSpaceUser(long spaceId) {
        return spaceUserMapper.listByColumn("space_id", spaceId);
    }

    public List<SpaceUser> listUserSpace(long userId) {
        return spaceUserMapper.listByColumn("user_id", userId);
    }

    /**
     * 查询用户可以访问的空间
     * @param user 用户
     * @return 返回空间信息
     */
    public List<SpaceDTO> listSpace(User user) {
        if (user.isSuperAdmin()) {
            return this.listAll(SpaceDTO::new);
        }
        List<Long> spaceIds;
        List<SpaceUser> spaceUserList = spaceUserMapper.listByColumn("user_id", user.getUserId());
        if (CollectionUtils.isEmpty(spaceUserList)) {
            return Collections.emptyList();
        }
        spaceIds = spaceUserList.stream()
                .map(SpaceUser::getSpaceId)
                .collect(Collectors.toList());

        List<Space> spaces = this.list(new Query().in("id", spaceIds));
        return CopyUtil.copyList(spaces, SpaceDTO::new);
    }

}