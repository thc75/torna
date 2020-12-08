package torna.dao.mapper;

import com.gitee.fastmybatis.core.mapper.CrudMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import torna.dao.entity.UserInfo;

/**
 * @author tanghc
 */
public interface UserInfoMapper extends CrudMapper<UserInfo, Long> {

    @Update("UPDATE user_info SET token='' WHERE token=#{token}")
    int logout(@Param("token") String token);

    @Update("UPDATE user_info SET token=#{token} WHERE id=#{userId}")
    int updateToken(@Param("userId") long userId, @Param("token") String token);

}