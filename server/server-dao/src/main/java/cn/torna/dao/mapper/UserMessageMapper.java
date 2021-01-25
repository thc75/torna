package cn.torna.dao.mapper;

import cn.torna.dao.entity.UserMessage;
import com.gitee.fastmybatis.core.mapper.CrudMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author tanghc
 */
public interface UserMessageMapper extends CrudMapper<UserMessage, Long> {

    @Update("UPDATE user_message SET is_read=1 WHERE user_id=#{userId} AND is_read=0")
    int readAll(@Param("userId") long userId);

}