package torna.service;

import torna.common.bean.Booleans;
import torna.common.bean.User;
import torna.common.support.BaseService;
import torna.common.util.CopyUtil;
import torna.dao.entity.Space;
import torna.dao.entity.SpaceUser;
import torna.dao.entity.UserInfo;
import torna.dao.mapper.SpaceMapper;
import torna.dao.mapper.SpaceUserMapper;
import torna.service.dto.SpaceAddDTO;
import torna.service.dto.SpaceInfoDTO;
import torna.service.dto.UserInfoDTO;
import torna.service.dto.UserSpaceDTO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
        space.setModifierId(spaceAddDTO.getCreatorId());
        this.saveIgnoreNull(space);

        SpaceUser spaceUser = new SpaceUser();
        spaceUser.setUserId(spaceAddDTO.getLeaderId());
        spaceUser.setSpaceId(space.getId());
        // 默认创建者就是空间管理员
        spaceUser.setIsLeader(Booleans.TRUE);
        spaceUserMapper.saveIgnoreNull(spaceUser);
    }

    /**
     * 添加空间用户
     * @param spaceId 空间id
     * @param userIds 用户id列表
     */
    public void addSpaceUser(long spaceId, List<Long> userIds) {
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
                    spaceUser.setIsLeader(Booleans.FALSE);
                    return spaceUser;
                }).collect(Collectors.toList());
        spaceUserMapper.insertBatch(tobeSaveList);
    }

    /**
     * 获取空间下的用户
     * @param spaceId 空间id
     * @param username 登录名
     * @return
     */
    public PageEasyui<UserInfoDTO> pageSpaceUser(long spaceId, String username) {
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
        PageEasyui<UserInfoDTO> pageInfo = MapperUtil.queryForEasyuiDatagrid(userInfoService.getMapper(), query, UserInfoDTO.class);
        // 设置添加时间
        pageInfo.getRows().forEach(userInfoDTO -> {
            SpaceUser spaceUser = userIdMap.get(userInfoDTO.getId());
            userInfoDTO.setGmtCreate(spaceUser.getGmtCreate());
        });
        return pageInfo;
    }

    public SpaceInfoDTO getSpaceInfo(long spaceId) {
        Space space = getById(spaceId);
        SpaceInfoDTO spaceInfoDTO = CopyUtil.copyBean(space, SpaceInfoDTO::new);
        List<UserInfoDTO> leaders = this.listSpaceLeader(spaceId);
        Long creatorId = space.getCreatorId();
        UserInfo userInfo = userInfoService.getById(creatorId);
        spaceInfoDTO.setCreator(userInfo.getRealname());
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

    public List<Long> listSpaceUserId(long spaceId) {
        List<SpaceUser> spaceUserList = listSpaceUser(spaceId);
        if (CollectionUtils.isEmpty(spaceUserList)) {
            return Collections.emptyList();
        }
        return spaceUserList.stream()
                .map(SpaceUser::getUserId)
                .collect(Collectors.toList());
    }

    /**
     * 查询空间管理员信息
     * @param spaceId
     * @return
     */
    public List<UserInfoDTO> listSpaceLeader(long spaceId) {
        Query query = new Query()
                .eq("space_id", spaceId)
                .eq("is_leader", Booleans.TRUE)
                .setQueryAll(true);
        List<SpaceUser> spaceLeaders = spaceUserMapper.list(query);
        List<Long> leaderIds = CopyUtil.copyList(spaceLeaders, SpaceUser::getUserId);
        return userInfoService.listUserInfo(leaderIds);
    }

    public List<SpaceUser> listSpaceUser(long spaceId) {
        return spaceUserMapper.listByColumn("space_id", spaceId);
    }

    /**
     * 查询用户可以访问的空间
     * @param user 用户
     * @return 返回空间信息
     */
    public List<UserSpaceDTO> listUserSpace(User user) {
        List<Long> spaceIds;
        // 可管理的空间id
        List<Long> leaderSpaceIds;
        if (user.isAdmin()) {
            spaceIds = this.listAll(Space::getId);
            leaderSpaceIds = spaceIds;
        } else {
            List<SpaceUser> spaceUserList = spaceUserMapper.listByColumn("user_id", user.getUserId());
            spaceIds = spaceUserList.stream()
                    .map(SpaceUser::getSpaceId)
                    .collect(Collectors.toList());
            leaderSpaceIds = spaceUserList.stream()
                    .filter(spaceUser -> spaceUser.getIsLeader() == Booleans.TRUE)
                    .map(SpaceUser::getSpaceId)
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(spaceIds)) {
            return Collections.emptyList();
        }

        List<Space> spaces = this.list(new Query().in("id", spaceIds));
        List<UserSpaceDTO> userSpaceDTOS = CopyUtil.copyList(spaces, UserSpaceDTO::new);

        boolean admin = user.isAdmin();
        for (UserSpaceDTO userSpaceDTO : userSpaceDTOS) {
            // 能否操作空间
            boolean canOperate = admin || leaderSpaceIds.contains(userSpaceDTO.getId());
            userSpaceDTO.setCanOperate(BooleanUtils.toIntegerObject(canOperate).byteValue());
        }
        return userSpaceDTOS;
    }


}