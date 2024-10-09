package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.enums.RoleEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.ProjectUser;
import cn.torna.dao.entity.Space;
import cn.torna.dao.entity.SpaceUser;
import cn.torna.dao.entity.UserInfo;
import cn.torna.dao.mapper.SpaceMapper;
import cn.torna.dao.mapper.SpaceUserMapper;
import cn.torna.service.dto.SpaceAddDTO;
import cn.torna.service.dto.SpaceDTO;
import cn.torna.service.dto.SpaceInfoDTO;
import cn.torna.service.dto.SpaceUserInfoDTO;
import cn.torna.service.dto.UserInfoDTO;
import com.gitee.fastmybatis.core.PageInfo;
import com.gitee.fastmybatis.core.query.LambdaQuery;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import com.gitee.fastmybatis.core.query.param.PageParam;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class SpaceService extends BaseLambdaService<Space, SpaceMapper> {

    @Resource
    private SpaceUserMapper spaceUserMapper;

    @Autowired
    private UserInfoService userInfoService;

    @Transactional(rollbackFor = Exception.class)
    public Space addSpace(SpaceAddDTO spaceAddDTO) {
        String spaceName = spaceAddDTO.getName();
        Query query = this.query()
                .eq(Space::getCreatorId, spaceAddDTO.getCreatorId())
                .eq(Space::getName, spaceName);
        Space existGroup = this.get(query);
        Assert.isNull(existGroup, () -> spaceName + "已存在");
        Space space = new Space();
        space.setName(spaceName);
        space.setCreatorId(spaceAddDTO.getCreatorId());
        space.setCreatorName(spaceAddDTO.getCreatorName());
        space.setModifierId(spaceAddDTO.getCreatorId());
        space.setModifierName(spaceAddDTO.getCreatorName());
        space.setIsCompose(spaceAddDTO.getIsCompose());
        this.save(space);

        // 添加管理员
        this.addSpaceUser(space.getId(), spaceAddDTO.getAdminIds(), RoleEnum.ADMIN);
        return space;
    }

    /**
     * 添加空间用户
     *
     * @param spaceId  空间id
     * @param userIds  用户id列表
     * @param roleEnum 角色
     */
    public void addSpaceUser(long spaceId, List<Long> userIds, RoleEnum roleEnum) {
        Assert.notEmpty(userIds, () -> "用户不能为空");
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
     *
     * @param spaceId  spaceId
     * @param userId   userId
     * @param roleEnum 角色
     */
    public void updateSpaceUserRole(long spaceId, long userId, RoleEnum roleEnum) {
        Assert.notNull(roleEnum, () -> "角色不能为空");
        LambdaQuery<SpaceUser> query = spaceUserMapper.query();
        query.set(SpaceUser::getRoleCode, roleEnum.getCode())
                .eq(SpaceUser::getSpaceId, spaceId)
                .eq(SpaceUser::getUserId, userId);

        spaceUserMapper.update(query);
    }

    /**
     * 获取空间下的用户
     *
     * @param spaceId   空间id
     * @param username  查询用户
     * @param pageParam 分页信息
     * @return
     */
    public PageInfo<SpaceUserInfoDTO> pageSpaceUser(Long spaceId, String username, PageParam pageParam) {
        PageInfo spaceUserPageInfo = pageSpaceUser(spaceId, pageParam.getPageIndex(), pageParam.getPageSize());
        if (spaceUserPageInfo == null) {
            return new PageInfo<>();
        }
        Map<Long, SpaceUser> userIdMap = ((PageInfo<SpaceUser>) spaceUserPageInfo).getList().stream()
                .collect(Collectors.toMap(SpaceUser::getUserId, Function.identity()));
        LambdaQuery<UserInfo> query = userInfoService.query();
        query.in(UserInfo::getId, userIdMap.keySet());
        if (StringUtils.hasText(username)) {
            query.andLambda(q -> q.like(UserInfo::getUsername, username)
                    .orLike(UserInfo::getNickname, username)
                    .orLike(UserInfo::getEmail, username)
            );
        }
        List<UserInfo> userInfos = userInfoService.list(query);
        List<SpaceUserInfoDTO> spaceUserInfoDTOS = CopyUtil.copyList(userInfos, SpaceUserInfoDTO::new);
        // 设置添加时间
        spaceUserInfoDTOS.forEach(userInfoDTO -> {
            SpaceUser spaceUser = userIdMap.get(userInfoDTO.getId());
            userInfoDTO.setGmtCreate(spaceUser.getGmtCreate());
            userInfoDTO.setRoleCode(spaceUser.getRoleCode());
        });
        spaceUserInfoDTOS.sort(Comparator.comparing(SpaceUserInfoDTO::getGmtCreate).reversed());
        spaceUserPageInfo.setList(spaceUserInfoDTOS);
        return spaceUserPageInfo;
    }

    /**
     * 查询空间用户
     *
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
        LambdaQuery<UserInfo> query = userInfoService.query();
        query.in(UserInfo::getId, userIdMap.keySet());
        query.andLambda(q -> q.like(UserInfo::getUsername, username)
                .orLike(UserInfo::getNickname, username)
                .orLike(UserInfo::getEmail, username)
        );

        List<UserInfo> userInfoList = userInfoService.list(query);
        return CopyUtil.copyList(userInfoList, UserInfoDTO::new);
    }

    /**
     * 查询空间用户
     *
     * @param spaceId
     * @return
     */
    public List<UserInfoDTO> listAllSpaceUser(long spaceId) {
        List<SpaceUser> spaceUsers = listSpaceUser(spaceId);
        Map<Long, SpaceUser> userIdMap = spaceUsers.stream()
                .collect(Collectors.toMap(SpaceUser::getUserId, Function.identity()));
        Query query = userInfoService.query()
                .in(UserInfo::getId, userIdMap.keySet())
                .orderBy(UserInfo::getId, Sort.DESC);

        List<UserInfo> userInfoList = userInfoService.list(query);
        return CopyUtil.copyList(userInfoList, UserInfoDTO::new);
    }

    public SpaceInfoDTO getSpaceInfo(long spaceId) {
        Space space = getById(spaceId);
        SpaceInfoDTO spaceInfoDTO = CopyUtil.copyBean(space, SpaceInfoDTO::new);
        List<UserInfoDTO> leaders = this.listSpaceAdmin(spaceId);
        spaceInfoDTO.setLeaders(leaders);
        return spaceInfoDTO;
    }

    /**
     * 移除空间成员
     *
     * @param spaceId 空间id
     * @param userId  空间用户
     */
    public void removeMember(long spaceId, long userId) {
        Query query = spaceUserMapper.query()
                .eq(SpaceUser::getSpaceId, spaceId)
                .eq(SpaceUser::getUserId, userId);
        spaceUserMapper.forceDeleteByQuery(query);
    }

    public SpaceUser getSpaceUser(Long spaceId, Long userId) {
        if (spaceId == null || userId == null) {
            return null;
        }
        Query query = spaceUserMapper.query()
                .eq(SpaceUser::getSpaceId, spaceId)
                .eq(SpaceUser::getUserId, userId);
        return spaceUserMapper.get(query);
    }


    /**
     * 查询空间管理员信息
     *
     * @param spaceId
     * @return
     */
    public List<UserInfoDTO> listSpaceAdmin(long spaceId) {
        Query query = spaceUserMapper.query()
                .eq(SpaceUser::getSpaceId, spaceId)
                .eq(SpaceUser::getRoleCode, RoleEnum.ADMIN.getCode())
                .setQueryAll(true);
        List<SpaceUser> spaceLeaders = spaceUserMapper.list(query);
        List<Long> adminIds = CopyUtil.copyList(spaceLeaders, SpaceUser::getUserId);
        return userInfoService.listUserInfo(adminIds);
    }

    /**
     * 获取空间管理员用户id
     *
     * @param spaceId 空间
     * @return 返回用户id
     */
    public List<Long> listSpaceAdminId(long spaceId) {
        return listSpaceAdmin(spaceId)
                .stream()
                .map(UserInfoDTO::getId)
                .collect(Collectors.toList());
    }

    public List<SpaceUser> listSpaceUser(Long spaceId) {
        if (spaceId == null) {
            return Collections.emptyList();
        }
        Query query = spaceUserMapper.query()
                .eq(SpaceUser::getSpaceId, spaceId)
                .orderBy(SpaceUser::getGmtCreate, Sort.DESC);
        return spaceUserMapper.list(query);
    }

    public PageInfo<SpaceUser> pageSpaceUser(Long spaceId, int pageIndex, int pageSize) {
        if (spaceId == null) {
            return null;
        }
        Query query = spaceUserMapper.query()
                .eq(SpaceUser::getSpaceId, spaceId)
                .orderBy(SpaceUser::getGmtCreate, Sort.DESC)
                .page(pageIndex, pageSize);
        return spaceUserMapper.page(query);
    }

    /**
     * 返回用户所在的空间
     *
     * @param userId 用户
     * @return 返回用户空间
     */
    public List<SpaceUser> listUserSpace(long userId) {
        return spaceUserMapper.list(SpaceUser::getUserId, userId);
    }

    /**
     * 查询用户可以访问的空间
     *
     * @param user 用户
     * @return 返回空间信息
     */
    public List<SpaceDTO> listSpace(User user) {
        if (user.isSuperAdmin()) {
            List<Space> list = this.list(new Query());
            return CopyUtil.copyList(list, SpaceDTO::new);
        }
        List<Long> spaceIds;
        List<SpaceUser> spaceUserList = spaceUserMapper.list(SpaceUser::getUserId, user.getUserId());
        if (CollectionUtils.isEmpty(spaceUserList)) {
            return Collections.emptyList();
        }
        spaceIds = spaceUserList.stream()
                .map(SpaceUser::getSpaceId)
                .collect(Collectors.toList());

        Query query = this.query()
                .in(Space::getId, spaceIds);
        List<Space> spaces = this.list(query);
        return CopyUtil.copyList(spaces, SpaceDTO::new);
    }

    /**
     * 清理空间已删除的用户
     *
     * @param spaceId 空间id
     */
    public void cleanDeletedUser(long spaceId) {
        Query query = spaceUserMapper.query()
                .eq(SpaceUser::getSpaceId, spaceId)
                .eq(SpaceUser::getIsDeleted, Booleans.TRUE);
        spaceUserMapper.forceDeleteByQuery(query);
    }

    /**
     * 转移项目成员到新空间
     *
     * @param projectUsers 项目用户
     * @param spaceIdNew   目标空间
     */
    public void transformSpaceUser(List<ProjectUser> projectUsers, long spaceIdNew) {
        this.cleanDeletedUser(spaceIdNew);
        List<SpaceUser> spaceUsersNew = this.listSpaceUser(spaceIdNew);
        List<Long> destSpaceUserIds = spaceUsersNew.stream()
                .map(SpaceUser::getUserId)
                .collect(Collectors.toList());

        List<SpaceUser> tobeSaveList = new ArrayList<>();

        for (ProjectUser projectUser : projectUsers) {
            Long userId = projectUser.getUserId();
            // 如果不存在，添加新用户
            if (!destSpaceUserIds.contains(userId)) {
                SpaceUser spaceUser = new SpaceUser();
                spaceUser.setUserId(userId);
                spaceUser.setSpaceId(spaceIdNew);
                spaceUser.setRoleCode(projectUser.getRoleCode());
                tobeSaveList.add(spaceUser);
            }
        }
        spaceUserMapper.saveBatchIgnoreNull(tobeSaveList);
    }


    /**
     * 移除空间成员
     *
     * @param userId 空间用户
     */
    public void removeMember(long userId) {
        Query query = spaceUserMapper.query()
                .eq(SpaceUser::getUserId, userId);
        spaceUserMapper.forceDeleteByQuery(query);
    }

}
