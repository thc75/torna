package cn.torna.service;

import cn.torna.dao.entity.ColumnInfo;
import cn.torna.dao.mapper.UpgradeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 升级
 * @author tanghc
 */
@Service
public class UpgradeService {


    @Autowired
    private UpgradeMapper upgradeMapper;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClass;


    /**
     * 升级
     */
    public void upgrade() {
        v1_0_2();
        v1_1_0();
    }

    /**
     * 升级v1.1.0，添加表
     */
    private void v1_1_0() {
        this.createTable("mock_config", "upgrade/1.1.0_ddl.txt", "upgrade/1.1.0_ddl_compatible.txt");
    }

    /**
     * 升级v1.0.2，添加索引
     */
    private void v1_0_2() {
        if (hasIndex("doc_param", "uk_dataid")) {
            return;
        }
        // 添加索引
        runSql("CREATE UNIQUE INDEX `uk_dataid` USING BTREE ON `doc_param` (`data_id`)");
    }


    private Optional<ColumnInfo> findColumn(String tableName, String columnName) {
        return this.upgradeMapper.listColumnInfo(tableName)
                .stream()
                .filter(map -> {
                    Object name = map.get("Field");
                    return Objects.equals(name, columnName);
                })
                .map(map -> {
                    Object field = map.get("Field");
                    ColumnInfo columnInfo = new ColumnInfo();
                    columnInfo.setName(String.valueOf(field));
                    return columnInfo;
                })
                .findFirst();
    }

    private void runSql(String sql) {
        upgradeMapper.runSql(sql);
    }

    /**
     * 表是否有对应索引
     * @param tableName 表名
     * @param indexName 索引名
     * @return 返回true：有对应索引
     */
    private boolean hasIndex(String tableName, String indexName) {
        String indexColumnName = "Key_name";
        List<Map<String, Object>> mapList = upgradeMapper.listTableIndex(tableName);
        for (Map<String, Object> indexInfo : mapList) {
            if (Objects.equals(indexInfo.get(indexColumnName), indexName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加表字段
     * @param tableName 表名
     * @param columnName 字段名
     * @param type 字段类型，如：varchar(128),text,integer
     * @return 返回true，插入成功
     */
    private boolean addColumn(String tableName, String columnName, String type) {
        upgradeMapper.addColumn(tableName, columnName, type);
        return false;
    }

    /**
     * 创建表
     * @param tableName 表名
     * @param ddlFile DDL文件
     * @param ddlFileCompatible 低版本DDL文件
     * @return 创建成功返回true
     */
    private boolean createTable(String tableName, String ddlFile, String ddlFileCompatible) {
        String file = isLowerVersion() ? ddlFileCompatible : ddlFile;
        if (!isTableExist(tableName)) {
            String sql = this.loadDDL(file);
            upgradeMapper.runSql(sql);
            return true;
        }
        return false;
    }

    private String loadDDL(String filename) {
        ClassPathResource resource = new ClassPathResource(filename);
        if (!resource.exists()) {
            throw new RuntimeException("找不到文件：" + filename);
        }
        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("打开文件出错", e);
        }
    }

    /**
     * 判断列是否存在
     * @param tableName 表名
     * @param columnName 列名
     * @return true：存在
     */
    private boolean isColumnExist(String tableName, String columnName) {
        return findColumn(tableName, columnName).isPresent();
    }


    /**
     * 表是否存在
     * @param tableName
     * @return
     */
    private boolean isTableExist(String tableName) {
        List<String> tableNameList = upgradeMapper.listTableName();
        return tableNameList != null && tableNameList.contains(tableName);
    }

    /**
     * 是否是低版本mysql
     * @return
     */
    private boolean isLowerVersion() {
        return Objects.equals(driverClass, "com.mysql.jdbc.Driver");
    }

}
