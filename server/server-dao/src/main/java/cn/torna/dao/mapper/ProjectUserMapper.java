package cn.torna.dao.mapper;

import cn.torna.dao.entity.ProjectUser;
import com.gitee.fastmybatis.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tanghc
 */
public interface ProjectUserMapper extends BaseMapper<ProjectUser> {
    int insertBatch(@Param("items") List<ProjectUser> items);

    int removeProjectLeader(@Param("projectId") long projectId, @Param("adminCode") String adminCode);
}
