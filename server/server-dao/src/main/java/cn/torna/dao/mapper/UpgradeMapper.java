package cn.torna.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author tanghc
 */
@Mapper
public interface UpgradeMapper {

    void runSql(@Param("sql") String sql);

    /**
     * 查看MYSQL表字段信息
     * @param tableName 表名
     * @return 返回字段信息
     */
    List<Map<String, Object>> listColumnInfo(@Param("tableName") String tableName);

    List<String> listTableName();

    List<Map<String, Object>> listTableIndex(@Param("tableName") String tableName);
}
