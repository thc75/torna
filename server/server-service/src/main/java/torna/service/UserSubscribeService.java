package torna.service;

import com.gitee.fastmybatis.core.query.Query;
import torna.common.bean.Booleans;
import torna.common.enums.UserSubscribeTypeEnum;
import torna.common.support.BaseService;
import torna.dao.entity.UserSubscribe;
import torna.dao.mapper.UserSubscribeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tanghc
 */
@Service
public class UserSubscribeService extends BaseService<UserSubscribe, UserSubscribeMapper> {

    public List<UserSubscribe> listUserSubscribe(long userId, UserSubscribeTypeEnum userSubscribeTypeEnum) {
        Query query = new Query()
                .eq("user_id", userId)
                .eq("type", userSubscribeTypeEnum.getType());
        return this.list(query);
    }

    public UserSubscribe getSubscribe(long userId, UserSubscribeTypeEnum userSubscribeTypeEnum, long sourceId) {
        Query query = new Query()
                .eq("user_id", userId)
                .eq("type", userSubscribeTypeEnum.getType())
                .eq("source_id", sourceId)
                .enableForceQuery();
        return this.get(query);
    }

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

    public void cancelSubscribe(long userId, UserSubscribeTypeEnum userSubscribeTypeEnum, long sourceId) {
        UserSubscribe userSubscribe = this.getSubscribe(userId, userSubscribeTypeEnum, sourceId);
        if (userSubscribe != null) {
            this.delete(userSubscribe);
        }
    }

}