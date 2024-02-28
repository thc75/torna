package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.enums.DocStatusEnum;
import cn.torna.common.enums.ModuleConfigTypeEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.TreeUtil;
import cn.torna.dao.entity.ColumnInfo;
import cn.torna.dao.entity.ConstantInfo;
import cn.torna.dao.entity.DocParam;
import cn.torna.dao.entity.ModuleConfig;
import cn.torna.dao.entity.ModuleEnvironment;
import cn.torna.dao.mapper.ConstantInfoMapper;
import cn.torna.dao.mapper.UpgradeMapper;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.service.dto.ModuleEnvironmentParamDTO;
import cn.torna.service.dto.SystemConfigDTO;
import com.gitee.fastmybatis.core.query.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.FileCopyUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 升级
 *
 * @author tanghc
 */
@Service
@Slf4j
public class UpgradeService {

    private static final int VERSION = 12610;

    private static final String TORNA_VERSION_KEY = "torna.version";

    @Autowired
    private UpgradeMapper upgradeMapper;

    @Autowired
    private ModuleConfigService moduleConfigService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private ModuleEnvironmentService moduleEnvironmentService;

    @Autowired
    private DocInfoService docInfoService;

    @Resource
    private ConstantInfoMapper errorCodeInfoMapper;

    @Resource
    private DocDiffRecordService docDiffRecordService;

    @Resource
    private DocSnapshotService docSnapshotService;


    @Value("${spring.datasource.driver-class-name}")
    private String driverClass;


    /**
     * 升级
     */
    public void upgrade() {
        createTable("system_config", "upgrade/1.3.3_ddl.txt", "upgrade/1.3.3_ddl_compatible.txt");
        int oldVersion = getVersion();
        doUpgrade(oldVersion);
        // 最后更新当前版本到数据库
        saveVersion(oldVersion);
    }


    /**
     * 升级
     *
     * @param oldVersion 本地老版本
     */
    private void doUpgrade(int oldVersion) {
        // 对之前的版本会进行一次升级
        // 下次更新不会再运行
        if (oldVersion < 3) {
            v1_0_2();
            v1_1_0();
            v1_2_0();
            v1_3_0();
        }
        v1_4_0(oldVersion);
        v1_5_0(oldVersion);
        v1_6_2(oldVersion);
        v1_6_3(oldVersion);
        v1_6_4(oldVersion);
        v1_8_0(oldVersion);
        v1_8_1(oldVersion);
        v1_9_3(oldVersion);
        v1_9_5(oldVersion);
        v1_12_0(oldVersion);
        v1_13_0(oldVersion);
        v1_13_2(oldVersion);
        v1_15_0(oldVersion);
        v1_15_3(oldVersion);
        v1_15_4(oldVersion);
        v1_16_0(oldVersion);
        v1_18_0(oldVersion);
        v1_20_0(oldVersion);
        v1_22_0(oldVersion);
        v1_22_1(oldVersion);
        v1_24_0(oldVersion);
        v1_25_0(oldVersion);
        v1_26_0(oldVersion);
        v1_26_1(oldVersion);
    }

    private void v1_26_1(int oldVersion) {
        if (oldVersion < 12610) {
            log.info("Upgrade version to 1.26.1");
            // 添加表字段 分享配置表新增字段 `过期时间`
            addColumn("share_config", "expiration_time",
                    "ALTER TABLE `share_config` ADD COLUMN `expiration_time` date COMMENT '过期时间。null:永久有效' AFTER `password`;");

            log.info("Upgrade 1.26.1 finished.");
        }
    }


    private void v1_26_0(int oldVersion) {
        if (oldVersion < 12600) {
            log.info("Upgrade version to 1.26.0");
            // 添加表字段
            addColumn("doc_diff_record", "doc_key",
                    "ALTER TABLE `doc_diff_record` ADD COLUMN `doc_key` varchar(64) NOT NULL DEFAULT '' COMMENT '文档唯一key' AFTER `doc_id`;");

            addColumn("doc_snapshot", "doc_key",
                    "ALTER TABLE `doc_snapshot` ADD COLUMN `doc_key` varchar(64) NOT NULL DEFAULT '' COMMENT '文档唯一key' AFTER `doc_id`;");

            addColumn("doc_info", "doc_key",
                    "ALTER TABLE `doc_info` ADD COLUMN `doc_key` varchar(64) NOT NULL DEFAULT '' COMMENT '文档唯一key' AFTER `data_id`;");

            // 填充数据，可重复执行
            docInfoService.fillDocKey();
            docDiffRecordService.fillDocKey();
            docSnapshotService.fillDocKey();

            // 创建表
            createTable("ms_space_config", "upgrade/1.26.0_ddl_1.txt");
            createTable("ms_module_config", "upgrade/1.26.0_ddl_2.txt");

            // 添加索引
            runSqlIgnoreError("ALTER TABLE `doc_info` " +
                    "ADD INDEX `idx_parentid`(`parent_id`) USING BTREE," +
                    "ADD INDEX `idx_dockey`(`doc_key`) USING BTREE;");
            // 修改字段长度
            runSqlIgnoreError("ALTER TABLE `doc_snapshot` MODIFY COLUMN `content` longtext NULL COMMENT '修改内容' AFTER `modifier_time`;");
            runSqlIgnoreError("ALTER TABLE `mock_config` MODIFY COLUMN `name` varchar(128) NOT NULL DEFAULT '' COMMENT '名称' AFTER `id`;");
            // 添加字段
            runSqlIgnoreError("ALTER TABLE `mock_config` ADD COLUMN `version` int(11) NULL DEFAULT 0 COMMENT 'mock版本' AFTER `data_id`;");

            log.info("Upgrade 1.26.0 finished.");
        }
    }

    private void v1_25_0(int oldVersion) {
        if (oldVersion < 12500) {
            log.info("Upgrade version to 1.25.0");
            createTable("user_wecom_info", "upgrade/1.25.0_ddl_user_wecom_info.txt");
            log.info("Upgrade 1.25.0 finished.");
        }
    }

    private void v1_24_0(int oldVersion) {
        if (oldVersion < 12400) {
            log.info("Upgrade version to 1.24.0");
            createTable("doc_snapshot", "upgrade/1.24.0_ddl_doc_snapshot.txt");
            createTable("debug_script", "upgrade/1.24.0_ddl_debug_script.txt");
            createTable("doc_diff_record", "upgrade/1.24.0_ddl_doc_diff_record.txt");
            createTable("doc_diff_detail", "upgrade/1.24.0_ddl_doc_diff_detail.txt");
            addColumn("doc_info", "status", "ALTER TABLE `doc_info` ADD COLUMN `status` TINYINT NULL DEFAULT '" + DocStatusEnum.DOING.getStatus() + "' COMMENT '文档状态,见：DocStatusEnum' AFTER `is_locked`");
            try {
                runSql("ALTER TABLE `module_config` CHANGE COLUMN `config_value` `config_value` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '配置值' AFTER `config_key`");
                runSql("INSERT INTO `system_config`(`config_key`, `config_value`, `remark`) VALUES ('front.param.type-array', '[\"string\",\"number\",\"boolean\",\"object\",\"array\",\"num_array\",\"str_array\",\"file\",\"file[]\",\"enum\"]', '参数类型配置');");
            } catch (Exception e) {
                // IGNORE
            }
            log.info("Upgrade 1.24.0 finished.");
        }
    }

    private void v1_22_1(int oldVersion) {
        if (oldVersion < 12300) {
            log.info("Upgrade version to 1.22.1");
            runSql("ALTER TABLE `doc_info` CHANGE `description` `description` longtext NULL COMMENT '文档描述'");
            createTable("system_login_token", "upgrade/1.22.1_ddl.txt");
            log.info("Upgrade 1.22.1 finished.");
        }
    }

    private void v1_22_0(int oldVersion) {
        if (oldVersion < 12200) {
            log.info("Upgrade version to 1.22.0");
            addColumn("doc_info", "version", "ALTER TABLE `doc_info` ADD COLUMN `version` varchar(32) NULL DEFAULT '' COMMENT '版本号，默认空字符串' after url");
            log.info("Upgrade 1.22.0 finished.");
        }
    }

    private void v1_20_0(int oldVersion) {
        if (oldVersion < 12000) {
            log.info("Upgrade version to 1.20.0");
            createTable("system_i18n_config", "upgrade/1.20.0_ddl.txt");
            log.info("Upgrade 1.20.0 finished.");
        }
    }

    private void v1_18_0(int oldVersion) {
        if (oldVersion < 11800) {
            log.info("Upgrade version to 1.18.0");
            createTable("constant_info", "upgrade/1.18.0_ddl.txt");
            createTable("push_ignore_field", "upgrade/1.18.0_ddl-2.txt");
            this.runSql("ALTER TABLE `doc_info` CHANGE COLUMN `description` `description` TEXT NULL COMMENT '文档描述' COLLATE 'utf8mb4_general_ci' AFTER `name`");
            moveModuleErrorCode();
            log.info("Upgrade 1.18.0 finished.");
        }
    }

    private void moveModuleErrorCode() {
        Query query = new Query()
                .eq("type", ModuleConfigTypeEnum.GLOBAL_ERROR_CODES.getType());
        List<ModuleConfig> list = moduleConfigService.list(query);
        Map<Long, List<ModuleConfig>> map = list.stream()
                .collect(Collectors.groupingBy(ModuleConfig::getModuleId));
        List<ConstantInfo> tobeSaveList = map.entrySet()
                .stream()
                .map(entry -> {
                    Long moduleId = entry.getKey();
                    List<ModuleConfig> moduleConfigs = entry.getValue();
                    return moduleConfigService.buildConstantInfo(moduleId, moduleConfigs);
                })
                .collect(Collectors.toList());

        errorCodeInfoMapper.saveBatchIgnoreNull(tobeSaveList, 100);
    }

    private void v1_16_0(int oldVersion) {
        if (oldVersion < 1160) {
            createTable("module_swagger_config", "upgrade/1.16.0_ddl.txt");
        }
    }

    private void v1_15_4(int oldVersion) {
        if (oldVersion < 1154) {
            // 公共参数都设置成必填
            runSql("UPDATE module_environment_param SET required=1");
        }
    }

    private void v1_15_3(int oldVersion) {
        if (oldVersion < 1153) {
            runSql("ALTER TABLE `doc_info` MODIFY COLUMN `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注' AFTER `order_index`");
            docInfoService.convertMarkdown2Html();
        }
    }

    private void v1_15_0(int oldVersion) {
        if (oldVersion < 1150) {
            addColumn("share_config",
                    "is_all_selected_debug",
                    "ALTER TABLE `share_config` ADD COLUMN `is_all_selected_debug` tinyint(4) NOT NULL DEFAULT '1'  COMMENT '调试环境是否全选， 1-全选， 0-不选' AFTER is_show_debug"
            );
            createTable("share_environment", "upgrade/1.15.0_ddl.txt");
        }
    }

    private void v1_13_2(int oldVersion) {
        if (oldVersion < 1132) {
            addColumn("doc_info",
                    "deprecated",
                    "ALTER TABLE `doc_info` ADD COLUMN `deprecated` VARCHAR(128) DEFAULT '$false$'  COMMENT '废弃信息' AFTER `content_type`"
            );
        }
    }

    private void v1_13_0(int oldVersion) {
        if (oldVersion < 1130) {
            addColumn("share_config", "is_show_debug", "ALTER TABLE `share_config` ADD COLUMN `is_show_debug` tinyint NOT NULL DEFAULT 1 COMMENT '是否显示调试' AFTER `creator_name`");
        }
    }

    private void v1_12_0(int oldVersion) {
        if (oldVersion < 1120) {
            log.info("upgrade to 1.12.0");
            // DDL
            createTable("module_environment", "upgrade/1.12.0_ddl_1.txt");
            createTable("module_environment_param", "upgrade/1.12.0_ddl_2.txt");
            // transfer data
            List<ModuleConfig> moduleConfigs = moduleConfigService.listDebugHost();
            for (ModuleConfig moduleConfig : moduleConfigs) {
                Long moduleId = moduleConfig.getModuleId();
                // create env
                ModuleEnvironment moduleEnvironment = moduleEnvironmentService.setDebugEnv(
                        moduleId,
                        moduleConfig.getConfigKey(),
                        moduleConfig.getConfigValue(),
                        Booleans.isTrue(moduleConfig.getExtendId())
                );
                // transfer headers
                this.transferParams(moduleEnvironment, ModuleConfigTypeEnum.GLOBAL_HEADERS);
                this.transferParams(moduleEnvironment, ModuleConfigTypeEnum.GLOBAL_PARAMS);
                this.transferParams(moduleEnvironment, ModuleConfigTypeEnum.GLOBAL_RETURNS);
                this.transferParams(moduleEnvironment, ModuleConfigTypeEnum.GLOBAL_ERROR_CODES);
            }
        }
    }

    private void transferParams(ModuleEnvironment moduleEnvironment, ModuleConfigTypeEnum moduleConfigTypeEnum) {
        // transfer headers
        List<DocParam> docParams = moduleConfigService.listGlobalOld(moduleEnvironment.getModuleId(), moduleConfigTypeEnum);
        List<ModuleEnvironmentParamDTO> docParamDTOS = CopyUtil.copyList(docParams, ModuleEnvironmentParamDTO::new);
        List<ModuleEnvironmentParamDTO> tree = TreeUtil.convertTree(docParamDTOS, 0L);
        moduleEnvironmentService.doCopy(tree, moduleEnvironment.getId(), 0L);
    }

    private void v1_9_5(int oldVersion) {
        if (oldVersion < 13) {
            addColumn("doc_info",
                    "is_locked",
                    "ALTER TABLE `doc_info` ADD COLUMN `is_locked` TINYINT DEFAULT 0  NOT NULL  COMMENT '是否锁住' AFTER `is_deleted`"
            );
            addColumn("doc_info",
                    "md5",
                    "ALTER TABLE `doc_info` ADD COLUMN `md5` varchar(32) NOT NULL DEFAULT '' COMMENT '文档内容的md5值' AFTER `data_id`"
            );
        }
    }

    private void v1_9_3(int oldVersion) {
        if (oldVersion < 12) {
            createTable("compose_common_param", "upgrade/1.9.3_ddl.txt");
            createTable("compose_additional_page", "upgrade/1.9.3_ddl2.txt");
            addColumn("compose_project",
                    "gateway_url",
                    "ALTER TABLE `compose_project` ADD COLUMN `gateway_url` VARCHAR(128) DEFAULT ''  NOT NULL  COMMENT '网关地址' AFTER `modifier_name`");
            addColumn("compose_project",
                    "show_debug",
                    "ALTER TABLE `compose_project` ADD COLUMN `show_debug` TINYINT(4) DEFAULT 1  NOT NULL   COMMENT '是否显示调试' AFTER `order_index`");
        }
    }

    private void v1_8_1(int oldVersion) {
        if (oldVersion < 11) {
            runSql("ALTER TABLE `doc_info` CHANGE COLUMN `description` `description` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL  COMMENT '文档描述' AFTER `name`");
            runSql("ALTER TABLE `doc_param` CHANGE COLUMN `description` `description` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL  COMMENT '描述' AFTER `example`");
            runSql("ALTER TABLE `doc_param` CHANGE COLUMN `example` `example` VARCHAR(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT ''  COMMENT '示例值' AFTER `max_length`");
            addColumn("doc_info",
                    "is_request_array",
                    "ALTER TABLE `doc_info` ADD COLUMN `is_request_array` TINYINT NOT NULL DEFAULT 0 COMMENT '是否请求数组' AFTER `is_use_global_returns`");
            addColumn("doc_info",
                    "is_response_array",
                    "ALTER TABLE `doc_info` ADD COLUMN `is_response_array` TINYINT NOT NULL DEFAULT 0 COMMENT '是否返回数组' AFTER `is_request_array`");
            addColumn("doc_info",
                    "request_array_type",
                    "ALTER TABLE `doc_info` ADD COLUMN `request_array_type` VARCHAR(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'object' COMMENT '请求数组时元素类型' AFTER `is_response_array`");
            addColumn("doc_info",
                    "response_array_type",
                    "ALTER TABLE `doc_info` ADD COLUMN `response_array_type` VARCHAR(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'object' COMMENT '返回数组时元素类型' AFTER `request_array_type`");
        }
    }

    private void v1_8_0(int oldVersion) {
        if (oldVersion < 9) {
            // 添加索引
            runSqlIgnoreError("CREATE INDEX `idx_spaceid` USING BTREE ON `project` (`space_id`)");
            runSqlIgnoreError("CREATE INDEX `idx_userid` USING BTREE ON `project_user` (`user_id`)");
            runSqlIgnoreError("CREATE INDEX `idx_userid` USING BTREE ON `space_user` (`user_id`)");
            addColumn("space", "is_compose", "ALTER TABLE `space` ADD COLUMN `is_compose` TINYINT(4) NOT NULL DEFAULT 0  COMMENT '是否组合空间' AFTER `modifier_name`");
            createTable("compose_doc", "upgrade/1.8.0_1_ddl.txt");
            createTable("compose_project", "upgrade/1.8.0_2_ddl.txt");
        }
    }

    private void v1_6_4(int oldVersion) {
        if (oldVersion < 8) {
            createTable("user_dingtalk_info", "upgrade/1.6.4_ddl.txt");
        }
    }

    private void v1_6_3(int oldVersion) {
        if (oldVersion < 7) {
            addColumn("mock_config", "path",
                    "ALTER TABLE `mock_config` ADD COLUMN `path` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' AFTER `name`");
            addColumn("mock_config", "data_id",
                    "ALTER TABLE `mock_config` ADD COLUMN `data_id` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'md5(path+query+body)' AFTER `name`");
            try {
                runSql("CREATE INDEX `idx_dataid` USING BTREE ON `mock_config` (`data_id`)");
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private void v1_6_2(int oldVersion) {
        if (oldVersion < 6) {
            addColumn("doc_info",
                    "author",
                    "ALTER TABLE `doc_info` ADD COLUMN `author` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '维护人' AFTER `description`");
        }
    }


    private void saveVersion(int oldVersion) {
        if (oldVersion < VERSION) {
            SystemConfigDTO systemConfigDTO = new SystemConfigDTO();
            systemConfigDTO.setConfigKey(TORNA_VERSION_KEY);
            systemConfigDTO.setConfigValue(String.valueOf(VERSION));
            systemConfigDTO.setRemark("当前内部版本号。不要删除这条记录！！");
            systemConfigService.setConfig(systemConfigDTO);
        }
    }

    private void v1_5_0(int dbVersion) {
        if (dbVersion < 5) {
            this.createTable("prop", "upgrade/1.5.0_ddl.txt", "upgrade/1.5.0_ddl_compatible.txt");
            this.addColumn("doc_info",
                    "type",
                    "ALTER TABLE `doc_info` ADD COLUMN `type` TINYINT NOT NULL DEFAULT 0 COMMENT '0:http,1:dubbo' AFTER `description`"
            );
        }
    }

    private void v1_4_0(int dbVersion) {
        if (dbVersion < 4) {
            this.createTable("share_config", "upgrade/1.4.0_ddl_1.txt", "upgrade/1.4.0_ddl_1_compatible.txt");
            this.createTable("share_content", "upgrade/1.4.0_ddl_2.txt", "upgrade/1.4.0_ddl_2_compatible.txt");
        }
    }

    private void v1_3_0() {
        this.addColumn("user_info",
                "source",
                "ALTER TABLE `user_info` ADD COLUMN `source` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'register' AFTER `is_super_admin`");
        boolean success = this.addColumn("user_info",
                "email",
                "ALTER TABLE `user_info` ADD COLUMN `email` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' AFTER `source`");
        // 更新邮箱
        if (success) {
            this.runSql("UPDATE user_info SET email=username WHERE email='' AND locate('@', username)");
        }
    }

    private void v1_2_0() {
        String alterColumnUseGlobalHeaders = "ALTER TABLE `doc_info` ADD COLUMN `is_use_global_headers` TINYINT(4) NOT NULL DEFAULT 1  COMMENT '是否使用全局请求参数' AFTER `module_id`";
        String alterColumnUseGlobalParams = "ALTER TABLE `doc_info` ADD COLUMN `is_use_global_params` TINYINT NOT NULL DEFAULT 1 COMMENT '是否使用全局请求参数' AFTER `is_use_global_headers`";
        String alterColumnUseGlobalReturns = "ALTER TABLE `doc_info` ADD COLUMN `is_use_global_returns` TINYINT NOT NULL DEFAULT 1 COMMENT '是否使用全局返回参数' AFTER `is_use_global_params`";
        this.addColumn("doc_info", "is_use_global_headers", alterColumnUseGlobalHeaders);
        this.addColumn("doc_info", "is_use_global_params", alterColumnUseGlobalParams);
        this.addColumn("doc_info", "is_use_global_returns", alterColumnUseGlobalReturns);

        String alterColumnExtendId = "ALTER TABLE `module_config` ADD COLUMN `extend_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 AFTER `config_value`";
        this.addColumn("module_config", "extend_id", alterColumnExtendId);

        this.transferData();
    }

    private void transferData() {
        ModuleConfigTypeEnum moduleConfigTypeEnum = ModuleConfigTypeEnum.GLOBAL_HEADERS;
        Query query = new Query()
                .eq("type", moduleConfigTypeEnum.getType())
                .eq("extend_id", 0);

        List<ModuleConfig> moduleConfigList = moduleConfigService.list(query);
        for (ModuleConfig moduleConfig : moduleConfigList) {
            DocParamDTO docParamDTO = new DocParamDTO();
            docParamDTO.setName(moduleConfig.getConfigKey());
            docParamDTO.setExample(moduleConfig.getConfigValue());
            docParamDTO.setDescription(moduleConfig.getDescription());
            moduleConfigService.saveDocParam(docParamDTO, moduleConfigTypeEnum, docParam -> {
                moduleConfig.setExtendId(docParam.getId());
                moduleConfigService.update(moduleConfig);
            });
        }
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

    private int getVersion() {
        String version = systemConfigService.getConfigValue(TORNA_VERSION_KEY, "0");
        return NumberUtils.toInt(version);
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

    protected void runSql(String sql) {
        if (isLowerVersion()) {
            sql = sql.replace("utf8mb4", "utf8");
        }
        upgradeMapper.runSql(sql);
    }

    protected void runSqlIgnoreError(String sql) {
        if (isLowerVersion()) {
            sql = sql.replace("utf8mb4", "utf8");
        }
        try {
            upgradeMapper.runSql(sql);
        } catch (Exception e) {
            log.warn("运行SQL报错，可能已经存在索引或字段。(不影响程序运行)，sql={}, msg={}", sql, e.getMessage());
        }
    }

    /**
     * 表是否有对应索引
     *
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
     *
     * @param tableName  表名
     * @param columnName 字段名
     * @param sql        添加字段sql
     * @return 返回true，插入成功
     */
    protected boolean addColumn(String tableName, String columnName, String sql) {
        if (isColumnExist(tableName, columnName)) {
            return false;
        }
        if (isLowerVersion()) {
            sql = sql.replace("utf8mb4", "utf8");
        }
        upgradeMapper.runSql(sql);
        return true;
    }

    /**
     * 创建表
     *
     * @param tableName         表名
     * @param ddlFile           DDL文件
     * @param ddlFileCompatible 低版本DDL文件
     * @return 创建成功返回true
     */
    @Deprecated
    private boolean createTable(String tableName, String ddlFile, String ddlFileCompatible) {
        return createTable(tableName, ddlFile);
    }

    /**
     * 创建表
     *
     * @param tableName 表名
     * @param ddlFile   DDL文件
     * @return 创建成功返回true
     */
    protected boolean createTable(String tableName, String ddlFile) {
        if (!isTableExist(tableName)) {
            String sql = this.loadDDL(ddlFile);
            if (isLowerVersion()) {
                sql = sql.replace("DEFAULT CURRENT_TIMESTAMP", "DEFAULT NULL");
                sql = sql.replace("DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", "DEFAULT NULL");
                sql = sql.replace("utf8mb4", "utf8");
            }
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
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return true：存在
     */
    private boolean isColumnExist(String tableName, String columnName) {
        return findColumn(tableName, columnName).isPresent();
    }


    /**
     * 表是否存在
     *
     * @param tableName
     * @return
     */
    private boolean isTableExist(String tableName) {
        List<String> tableNameList = upgradeMapper.listTableName();
        return tableNameList != null && tableNameList.contains(tableName);
    }

    /**
     * 是否是低版本mysql
     *
     * @return
     */
    private boolean isLowerVersion() {
        return Objects.equals(driverClass, "com.mysql.jdbc.Driver");
    }

}
