package cn.torna.dao.mapper;

import cn.torna.dao.entity.ProjectUser;
import com.gitee.fastmybatis.core.mapper.CrudMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tanghc
 */
public interface ProjectUserMapper extends CrudMapper<ProjectUser, Long> {
    int insertBatch(@Param("items") List<ProjectUser> items);

    int removeProjectLeader(@Param("projectId") long projectId, @Param("adminCode") String adminCode);
}