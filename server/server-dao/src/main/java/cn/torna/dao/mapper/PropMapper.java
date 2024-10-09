package cn.torna.dao.mapper;

import cn.torna.dao.entity.Prop;
import com.gitee.fastmybatis.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tanghc
 */
public interface PropMapper extends BaseMapper<Prop> {

    int saveProps(@Param("items") List<Prop> items);

}
