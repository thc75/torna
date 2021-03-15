package cn.torna.dao.mapper;

import cn.torna.dao.entity.ColumnInfo;
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
     * 新增mysql表字段
     * @param tableName 表名
     * @param columnName 字段名
     * @param type 类型
     */
    void addColumn(@Param("tableName") String tableName, @Param("columnName")String columnName, @Param("type") String type);

    /**
     * 查看MYSQL表字段信息
     * @param tableName 表名
     * @return 返回字段信息
     */
    List<ColumnInfo> listColumnInfo(@Param("tableName") String tableName);

    List<String> listTableName();

    List<Map<String, Object>> listTableIndex(@Param("tableName") String tableName);
}
