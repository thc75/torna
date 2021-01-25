package cn.torna.dao.mapper;

import com.gitee.fastmybatis.core.mapper.CrudMapper;
import cn.torna.dao.entity.SpaceUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tanghc
 */
public interface SpaceUserMapper extends CrudMapper<SpaceUser, Long> {

    int insertBatch(@Param("items") List<SpaceUser> items);

}