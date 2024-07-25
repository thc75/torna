package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.dao.entity.UserSubscribe;
import cn.torna.dao.mapper.UserSubscribeMapper;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class UserSubscribeService extends BaseLambdaService<UserSubscribe, UserSubscribeMapper> {

    /**
     * 获取用户关注列表
     *
     * @param userId                用户id
     * @param userSubscribeTypeEnum 关注类型
     * @return
     */
    public List<UserSubscribe> listUserSubscribe(long userId, UserSubscribeTypeEnum userSubscribeTypeEnum) {
        Query query = this.query()
                .eq(UserSubscribe::getUserId, userId)
                .eq(UserSubscribe::getType, userSubscribeTypeEnum.getType());
        return this.list(query);
    }

    /**
     * 获取某一个关注详细
     *
     * @param userId                用户id
     * @param userSubscribeTypeEnum 关注类型
     * @param sourceId              资源id
     * @return
     */
    public UserSubscribe getSubscribe(long userId, UserSubscribeTypeEnum userSubscribeTypeEnum, long sourceId) {
        Query query = this.query()
                .eq(UserSubscribe::getUserId, userId)
                .eq(UserSubscribe::getType, userSubscribeTypeEnum.getType())
                .eq(UserSubscribe::getSourceId, sourceId)
                .enableForceQuery();
        return this.get(query);
    }

    /**
     * 关注
     *
     * @param userId                用户id
     * @param userSubscribeTypeEnum 关注类型
     * @param sourceId              资源id
     */
    public void subscribe(long userId, UserSubscribeTypeEnum userSubscribeTypeEnum, long sourceId) {
        UserSubscribe userSubscribe = this.getSubscribe(userId, userSubscribeTypeEnum, sourceId);
        if (userSubscribe == null) {
            userSubscribe = new UserSubscribe();
            userSubscribe.setUserId(userId);
            userSubscribe.setType(userSubscribeTypeEnum.getType());
            userSubscribe.setSourceId(sourceId);
            this.save(userSubscribe);
        } else {
            userSubscribe.setIsDeleted(Booleans.FALSE);
            this.update(userSubscribe);
        }
    }

    /**
     * 取消关注
     *
     * @param userId                用户id
     * @param userSubscribeTypeEnum 关注类型
     * @param sourceId              资源id
     */
    public void cancelSubscribe(long userId, UserSubscribeTypeEnum userSubscribeTypeEnum, long sourceId) {
        UserSubscribe userSubscribe = this.getSubscribe(userId, userSubscribeTypeEnum, sourceId);
        if (userSubscribe != null) {
            this.delete(userSubscribe);
        }
    }


    public List<Long> listUserIds(UserSubscribeTypeEnum userSubscribeTypeEnum, long sourceId) {
        Query query = this.query()
                .eq(UserSubscribe::getType, userSubscribeTypeEnum.getType())
                .eq(UserSubscribe::getSourceId, sourceId);
        List<UserSubscribe> userSubscribes = this.list(query);
        return userSubscribes.stream()
                .map(UserSubscribe::getUserId)
                .collect(Collectors.toList());
    }

}
