package cn.torna.dao.mapper;

import cn.torna.dao.entity.Prop;
import com.gitee.fastmybatis.core.mapper.CrudMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tanghc
 */
public interface PropMapper extends CrudMapper<Prop, Long> {

    int saveProps(@Param("items") List<Prop> items);

}