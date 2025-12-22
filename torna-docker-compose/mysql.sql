-- 适用于MySQL5.6.5+
-- 备注：5.5.3开始支持utf8mb4，5.6.5开始支持CURRENT_TIMESTAMP(datetime)

-- 导出 torna 的数据库结构
CREATE DATABASE IF NOT EXISTS `torna` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `torna`;



DROP TABLE IF EXISTS `compose_additional_page`;
CREATE TABLE `compose_additional_page` (
                                           `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                           `project_id` bigint(20) unsigned NOT NULL COMMENT 'compose_project.id',
                                           `title` varchar(64) NOT NULL COMMENT '文档标题',
                                           `content` text COMMENT '文档内容',
                                           `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序值',
                                           `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1:启用，0：禁用',
                                           `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                           `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                           PRIMARY KEY (`id`) USING BTREE,
                                           KEY `idx_projectid` (`project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='聚合文档附加页';

DROP TABLE IF EXISTS `compose_common_param`;
CREATE TABLE `compose_common_param` (
                                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                        `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT '唯一id，md5(doc_id:parent_id:style:name)',
                                        `name` varchar(64) NOT NULL DEFAULT '' COMMENT '字段名称',
                                        `type` varchar(64) NOT NULL DEFAULT 'String' COMMENT '字段类型',
                                        `required` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否必须，1：是，0：否',
                                        `max_length` varchar(64) NOT NULL DEFAULT '-' COMMENT '最大长度',
                                        `example` varchar(1024) NOT NULL DEFAULT '' COMMENT '示例值',
                                        `description` text NOT NULL COMMENT '描述',
                                        `enum_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'enum_info.id',
                                        `compose_project_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'compose_project.id',
                                        `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                                        `style` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0：path, 1：header， 2：请求参数，3：返回参数，4：错误码',
                                        `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                                        `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                        `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                        `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE KEY `uk_dataid` (`data_id`) USING BTREE,
                                        KEY `idx_compose_project_id` (`compose_project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='聚合文档公共参数';

DROP TABLE IF EXISTS `compose_doc`;
CREATE TABLE `compose_doc` (
                               `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                               `doc_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'doc_info.id',
                               `project_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'compose_project.id',
                               `is_folder` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否文件夹',
                               `folder_name` varchar(64) NOT NULL DEFAULT '' COMMENT '文件夹名称',
                               `parent_id` bigint(20) NOT NULL DEFAULT '0',
                               `origin` varchar(128) NOT NULL DEFAULT '' COMMENT '文档来源',
                               `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                               `creator` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
                               `order_index` int(10) unsigned NOT NULL DEFAULT '0',
                               `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                               `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (`id`) USING BTREE,
                               KEY `idx_projectid` (`project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文档引用';

DROP TABLE IF EXISTS `compose_project`;
CREATE TABLE `compose_project` (
                                   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                   `name` varchar(64) NOT NULL DEFAULT '' COMMENT '项目名称',
                                   `description` varchar(128) NOT NULL DEFAULT '' COMMENT '项目描述',
                                   `space_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '所属空间，space.id',
                                   `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '访问形式，1：公开，2：加密',
                                   `password` varchar(64) NOT NULL DEFAULT '' COMMENT '访问密码',
                                   `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建者userid',
                                   `creator_name` varchar(64) NOT NULL DEFAULT '',
                                   `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                                   `modifier_name` varchar(64) NOT NULL DEFAULT '',
                                   `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                                   `show_debug` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否显示调试',
                                   `gateway_url` varchar(128) NOT NULL DEFAULT '' COMMENT '网关url',
                                   `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1：有效，0：无效',
                                   `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                   `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                   `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `idx_spaceid` (`space_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='组合项目表';

DROP TABLE IF EXISTS `constant_info`;
CREATE TABLE `constant_info` (
                                 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                 `project_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'project.id',
                                 `module_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'module.id',
                                 `doc_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'doc_info.id',
                                 `content` text NOT NULL COMMENT '内容',
                                 `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `idx_projectid` (`project_id`) USING BTREE,
                                 KEY `idx_moduleid` (`module_id`) USING BTREE,
                                 KEY `idx_docid` (`doc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='常量信息';

DROP TABLE IF EXISTS `debug_script`;
CREATE TABLE `debug_script` (
                                `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                `NAME` varchar(64) NOT NULL DEFAULT '' COMMENT '名称',
                                `description` varchar(128) NOT NULL DEFAULT '' COMMENT '描述',
                                `content` text COMMENT '脚本内容',
                                `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '类型，0：pre，1：after',
                                `scope` tinyint(4) NOT NULL DEFAULT '0' COMMENT '作用域，0：当前文档，1：当前应用，2：当前项目',
                                `ref_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '关联id',
                                `creator_name` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人昵称',
                                `enabled` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否启用',
                                `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`),
                                KEY `idx_refid` (`ref_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调试脚本表';

DROP TABLE IF EXISTS `doc_diff_detail`;
CREATE TABLE `doc_diff_detail` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                   `record_id` bigint(20) NOT NULL COMMENT 'doc_diff_record.id',
                                   `position_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '变更位置，0：文档名称，1：文档描述，2：contentType，3：httpMethod，10：参数名称，11：参数类型，12：参数必填，13：参数最大长度，14：参数描述，15：参数示例值',
                                   `target_name` varchar(64) NOT NULL COMMENT '目标名称',
                                   `content` text COMMENT '内容',
                                   `modify_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '变更类型，0：修改，1：新增，2：删除',
                                   `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                   `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`),
                                   KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档比较记录';

DROP TABLE IF EXISTS `doc_diff_record`;
CREATE TABLE `doc_diff_record` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                   `doc_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'doc_info.id',
                                   `doc_key` varchar(64) NOT NULL DEFAULT '' COMMENT '文档唯一key',
                                   `md5_old` varchar(64) NOT NULL DEFAULT '' COMMENT '旧MD5',
                                   `md5_new` varchar(64) NOT NULL DEFAULT '' COMMENT '新MD5',
                                   `modify_source` tinyint(4) NOT NULL DEFAULT '0' COMMENT '修改方式，0：推送，1：表单编辑',
                                   `modify_user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改人id',
                                   `modify_nickname` varchar(64) NOT NULL DEFAULT '' COMMENT '修改人',
                                   `modify_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '变更类型，0：修改，1：新增，2：删除',
                                   `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
                                   `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                   `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `idx_doc_id` (`doc_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档比较记录';

DROP TABLE IF EXISTS `doc_info`;
CREATE TABLE `doc_info` (
                            `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                            `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT '唯一id，接口规则：md5(module_id:parent_id:url:http_method)。分类规则：md5(module_id:parent_id:name)',
                            `doc_key` varchar(64) NOT NULL DEFAULT '' COMMENT '文档唯一key',
                            `md5` varchar(32) NOT NULL DEFAULT '' COMMENT '文档内容的md5值',
                            `name` varchar(128) NOT NULL DEFAULT '' COMMENT '文档名称',
                            `description` longtext COMMENT '文档描述',
                            `author` varchar(64) NOT NULL DEFAULT '' COMMENT '维护人',
                            `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:http,1:dubbo',
                            `url` varchar(256) NOT NULL DEFAULT '' COMMENT '访问URL',
                            `version` varchar(32) DEFAULT '' COMMENT '版本号，默认空字符串',
                            `http_method` varchar(12) NOT NULL DEFAULT '' COMMENT 'http方法',
                            `content_type` varchar(128) NOT NULL DEFAULT '' COMMENT 'contentType',
                            `deprecated` varchar(128) DEFAULT '$false$' COMMENT '废弃信息',
                            `is_folder` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否是分类，0：不是，1：是',
                            `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '父节点',
                            `module_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '模块id，module.id',
                            `is_use_global_headers` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否使用全局请求参数',
                            `is_use_global_params` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否使用全局请求参数',
                            `is_use_global_returns` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否使用全局返回参数',
                            `is_request_array` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否请求数组',
                            `is_response_array` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否返回数组',
                            `request_array_type` varchar(16) NOT NULL DEFAULT 'object' COMMENT '请求数组时元素类型',
                            `response_array_type` varchar(16) NOT NULL DEFAULT 'object' COMMENT '返回数组时元素类型',
                            `create_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '新增操作方式，0：人工操作，1：开放平台推送',
                            `modify_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '修改操作方式，0：人工操作，1：开放平台推送',
                            `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建人',
                            `creator_name` varchar(64) NOT NULL DEFAULT '' COMMENT '创建者昵称user_info.nickname',
                            `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '修改人',
                            `modifier_name` varchar(64) NOT NULL DEFAULT '' COMMENT '创建者昵称user_info.realname',
                            `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                            `remark` text COMMENT '备注',
                            `is_show` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否显示',
                            `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                            `is_locked` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否锁住',
                            `status` tinyint(4) DEFAULT '5' COMMENT '文档状态,见：DocStatusEnum',
                            `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                            `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE KEY `uk_data_id` (`data_id`) USING BTREE,
                            KEY `idx_moduleid` (`module_id`) USING BTREE,
                            KEY `idx_parentid` (`parent_id`) USING BTREE,
                            KEY `idx_dockey` (`doc_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文档信息';

DROP TABLE IF EXISTS `doc_param`;
CREATE TABLE `doc_param` (
                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                             `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT '唯一id，md5(doc_id:parent_id:style:name)',
                             `name` varchar(64) NOT NULL DEFAULT '' COMMENT '字段名称',
                             `type` varchar(64) NOT NULL DEFAULT 'String' COMMENT '字段类型',
                             `required` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否必须，1：是，0：否',
                             `max_length` varchar(64) NOT NULL DEFAULT '-' COMMENT '最大长度',
                             `example` varchar(1024) NOT NULL DEFAULT '' COMMENT '示例值',
                             `description` text NOT NULL COMMENT '描述',
                             `enum_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'enum_info.id',
                             `doc_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'doc_info.id',
                             `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                             `style` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0：path, 1：header， 2：body参数，3：返回参数，4：错误码, 5：query参数',
                             `create_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '新增操作方式，0：人工操作，1：开放平台推送',
                             `modify_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '修改操作方式，0：人工操作，1：开放平台推送',
                             `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                             `creator_name` varchar(64) NOT NULL DEFAULT '',
                             `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                             `modifier_name` varchar(64) NOT NULL DEFAULT '',
                             `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                             `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                             `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                             `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE KEY `uk_dataid` (`data_id`) USING BTREE,
                             KEY `idx_docid` (`doc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=814 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文档参数';

DROP TABLE IF EXISTS `doc_snapshot`;
CREATE TABLE `doc_snapshot` (
                                `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                `doc_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'doc_info.id',
                                `doc_key` varchar(64) NOT NULL DEFAULT '' COMMENT '文档唯一key',
                                `md5` varchar(64) NOT NULL DEFAULT '' COMMENT '文档md5',
                                `modifier_name` varchar(64) NOT NULL DEFAULT '' COMMENT '修改人',
                                `modifier_time` datetime NOT NULL COMMENT '修改时间',
                                `content` longtext COMMENT '修改内容',
                                `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`),
                                KEY `idx_docid` (`doc_id`) USING BTREE,
                                KEY `idx_md5` (`md5`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档快照表';

DROP TABLE IF EXISTS `enum_info`;
CREATE TABLE `enum_info` (
                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                             `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT '唯一id，md5(module_id:name)',
                             `name` varchar(128) NOT NULL DEFAULT '' COMMENT '枚举名称',
                             `description` varchar(128) NOT NULL DEFAULT '' COMMENT '枚举说明',
                             `module_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'module.id',
                             `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                             `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                             `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`) USING BTREE,
                             KEY `idx_moduleid` (`module_id`) USING BTREE,
                             KEY `idx_dataid` (`data_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='枚举信息';

DROP TABLE IF EXISTS `enum_item`;
CREATE TABLE `enum_item` (
                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                             `enum_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'enum_info.id',
                             `name` varchar(128) NOT NULL DEFAULT '' COMMENT '名称，字面值',
                             `type` varchar(64) NOT NULL DEFAULT '' COMMENT '类型',
                             `value` varchar(64) NOT NULL DEFAULT '' COMMENT '枚举值',
                             `description` varchar(128) NOT NULL DEFAULT '' COMMENT '枚举描述',
                             `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                             `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                             `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                             `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`) USING BTREE,
                             KEY `idx_enumid` (`enum_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='枚举详情';

DROP TABLE IF EXISTS `gen_template`;
CREATE TABLE `gen_template` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `name` varchar(64) NOT NULL DEFAULT '' COMMENT '模板名称',
                                `content` text NOT NULL COMMENT '模板内容',
                                `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                `group_name` varchar(64) NOT NULL DEFAULT 'Default' COMMENT '分组',
                                `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='代码生成模板';

DROP TABLE IF EXISTS `mock_config`;
CREATE TABLE `mock_config` (
                               `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                               `name` varchar(128) NOT NULL DEFAULT '' COMMENT '名称',
                               `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT 'md5(path+query+body)',
                               `version` int(11) DEFAULT '0' COMMENT 'mock版本',
                               `path` varchar(128) NOT NULL DEFAULT '',
                               `ip` varchar(64) NOT NULL DEFAULT '' COMMENT '过滤ip',
                               `request_data` text NOT NULL COMMENT '请求参数',
                               `request_data_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '参数类型，0：KV形式，1：json形式',
                               `http_status` int(11) NOT NULL DEFAULT '200' COMMENT 'http状态',
                               `delay_mills` int(11) NOT NULL DEFAULT '0' COMMENT '延迟时间，单位毫秒',
                               `result_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '返回类型，0：自定义内容，1：脚本内容',
                               `response_headers` text NOT NULL COMMENT '响应header，数组结构',
                               `response_body` text NOT NULL COMMENT '响应结果',
                               `mock_script` text COMMENT 'mock脚本',
                               `mock_result` text COMMENT 'mock结果',
                               `doc_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '文档id',
                               `remark` varchar(128) NOT NULL DEFAULT '' COMMENT '备注',
                               `creator_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建人id',
                               `creator_name` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人姓名',
                               `modifier_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改人id',
                               `modifier_name` varchar(64) DEFAULT NULL COMMENT '修改人',
                               `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                               `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                               `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (`id`) USING BTREE,
                               KEY `idx_docid` (`doc_id`) USING BTREE,
                               KEY `idx_dataid` (`data_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='mock配置';

DROP TABLE IF EXISTS `module`;
CREATE TABLE `module` (
                          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                          `name` varchar(64) NOT NULL DEFAULT '' COMMENT '模块名称',
                          `project_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'project.id',
                          `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '模块类型，0：自定义添加，1：swagger导入，2：postman导入',
                          `import_url` varchar(128) NOT NULL DEFAULT '' COMMENT '导入url',
                          `basic_auth_username` varchar(128) NOT NULL DEFAULT '' COMMENT 'basic认证用户名',
                          `basic_auth_password` varchar(128) NOT NULL DEFAULT '' COMMENT 'basic认证密码',
                          `token` varchar(128) NOT NULL DEFAULT '' COMMENT '开放接口调用token',
                          `create_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '新增操作方式，0：人工操作，1：开放平台推送',
                          `modify_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '修改操作方式，0：人工操作，1：开放平台推送',
                          `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                          `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                          `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                          `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                          `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                          `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`id`) USING BTREE,
                          UNIQUE KEY `uk_token` (`token`) USING BTREE,
                          KEY `idx_projectid` (`project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目模块';

DROP TABLE IF EXISTS `module_config`;
CREATE TABLE `module_config` (
                                 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                 `module_id` bigint(11) unsigned NOT NULL DEFAULT '0',
                                 `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '配置类型，1：全局header',
                                 `config_key` varchar(128) NOT NULL DEFAULT '' COMMENT '配置key',
                                 `config_value` varchar(256) NOT NULL DEFAULT '' COMMENT '配置值',
                                 `extend_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                                 `description` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
                                 `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                 `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `idx_moduleid_type` (`module_id`,`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='系统配置';

DROP TABLE IF EXISTS `module_environment`;
CREATE TABLE `module_environment` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                      `module_id` bigint(20) NOT NULL COMMENT 'module.id',
                                      `name` varchar(64) NOT NULL DEFAULT '' COMMENT '环境名称',
                                      `url` varchar(255) NOT NULL DEFAULT '' COMMENT '调试路径',
                                      `is_public` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否公开',
                                      `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                      `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`) USING BTREE,
                                      KEY `uk_moduleid_name` (`module_id`,`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='模块调试环境';

DROP TABLE IF EXISTS `module_environment_param`;
CREATE TABLE `module_environment_param` (
                                            `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                            `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT '唯一id，md5(doc_id:parent_id:style:name)',
                                            `name` varchar(64) NOT NULL DEFAULT '' COMMENT '字段名称',
                                            `type` varchar(64) NOT NULL DEFAULT 'String' COMMENT '字段类型',
                                            `required` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否必须，1：是，0：否',
                                            `max_length` varchar(64) NOT NULL DEFAULT '-' COMMENT '最大长度',
                                            `example` varchar(1024) NOT NULL DEFAULT '' COMMENT '示例值',
                                            `description` text NOT NULL COMMENT '描述',
                                            `enum_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'enum_info.id',
                                            `environment_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'module_environment.id',
                                            `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                                            `style` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0：path, 1：header， 2：请求参数，3：返回参数，4：错误码',
                                            `create_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '新增操作方式，0：人工操作，1：开放平台推送',
                                            `modify_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '修改操作方式，0：人工操作，1：开放平台推送',
                                            `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                                            `creator_name` varchar(64) NOT NULL DEFAULT '',
                                            `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                                            `modifier_name` varchar(64) NOT NULL DEFAULT '',
                                            `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                                            `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                            `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                            `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                            PRIMARY KEY (`id`) USING BTREE,
                                            UNIQUE KEY `uk_dataid` (`data_id`) USING BTREE,
                                            KEY `idx_environmentid` (`environment_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='模块公共参数';

DROP TABLE IF EXISTS `module_swagger_config`;
CREATE TABLE `module_swagger_config` (
                                         `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                         `module_id` bigint(20) NOT NULL COMMENT 'module.id',
                                         `url` varchar(256) NOT NULL DEFAULT '' COMMENT 'swagger url',
                                         `content` longtext NOT NULL COMMENT 'swagger内容',
                                         `auth_username` varchar(128) NOT NULL DEFAULT '' COMMENT '认证用户名',
                                         `auth_password` varchar(128) NOT NULL DEFAULT '' COMMENT '认证密码',
                                         `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                         `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         PRIMARY KEY (`id`),
                                         KEY `idx_moduleid` (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='swagger配置表';

DROP TABLE IF EXISTS `ms_module_config`;
CREATE TABLE `ms_module_config` (
                                    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                    `module_id` bigint(20) NOT NULL COMMENT 'module.id',
                                    `ms_project_id` varchar(64) NOT NULL COMMENT 'MeterSphere项目id',
                                    `ms_module_id` varchar(64) NOT NULL COMMENT 'MeterSphere模块id',
                                    `ms_cover_module` tinyint(4) NOT NULL DEFAULT '1' COMMENT '默认覆盖',
                                    `name` varchar(100) NOT NULL,
                                    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE KEY `uk_moduleid` (`module_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MeterSphere模块配置';

DROP TABLE IF EXISTS `ms_space_config`;
CREATE TABLE `ms_space_config` (
                                   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                   `space_id` bigint(20) NOT NULL COMMENT '空间id',
                                   `ms_space_id` varchar(64) NOT NULL COMMENT 'MeterSphere空间id',
                                   `ms_space_name` varchar(64) DEFAULT NULL,
                                   `ms_address` varchar(100) NOT NULL COMMENT 'MeterSphere服务器地址',
                                   `ms_access_key` varchar(100) NOT NULL COMMENT 'MeterSphere的access_key',
                                   `ms_secret_key` varchar(100) NOT NULL COMMENT 'MeterSphere的secret_key',
                                   `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                   `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE KEY `uk_spaceid` (`space_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MeterSphere空间配置';

DROP TABLE IF EXISTS `open_user`;
CREATE TABLE `open_user` (
                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                             `app_key` varchar(100) NOT NULL COMMENT 'appKey',
                             `secret` varchar(200) DEFAULT NULL COMMENT 'secret',
                             `status` tinyint(4) unsigned NOT NULL DEFAULT '1' COMMENT '1启用，0禁用',
                             `applicant` varchar(64) NOT NULL DEFAULT '',
                             `space_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'space.id',
                             `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                             `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                             `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE KEY `uk_app_key` (`app_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='开放用户';

DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
                           `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                           `name` varchar(64) NOT NULL DEFAULT '' COMMENT '项目名称',
                           `description` varchar(128) NOT NULL DEFAULT '' COMMENT '项目描述',
                           `space_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '所属空间，space.id',
                           `is_private` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否私有项目，1：是，0：否',
                           `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建者userid',
                           `creator_name` varchar(64) NOT NULL DEFAULT '',
                           `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                           `modifier_name` varchar(64) NOT NULL DEFAULT '',
                           `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                           `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                           `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                           `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`) USING BTREE,
                           KEY `uk_name` (`creator_id`,`name`) USING BTREE,
                           KEY `idx_spaceid` (`space_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目表';

DROP TABLE IF EXISTS `project_release`;
CREATE TABLE `project_release` (
                                   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                   `project_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'project.id',
                                   `release_no` varchar(20) NOT NULL COMMENT '版本号',
                                   `release_desc` varchar(200) DEFAULT '' COMMENT '版本描述',
                                   `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 1-有效 0-无效',
                                   `dingding_webhook` varchar(200) DEFAULT '' COMMENT '钉钉机器人webhook',
                                   `we_com_webhook` varchar(200) NULL DEFAULT '' COMMENT '企业微信机器人webhook',
                                   `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                   `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                   `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `idx_projectid_releaseno` (`project_id`,`release_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='项目版本表';

DROP TABLE IF EXISTS `project_release_doc`;
CREATE TABLE `project_release_doc` (
                                       `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                       `project_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'project.id',
                                       `release_id` bigint(20) NOT NULL COMMENT 'project_release.id',
                                       `module_id` bigint(20) NOT NULL COMMENT 'module.id',
                                       `source_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'doc_info.id',
                                       `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                       `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                       `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `idx_projectid_releaseid_sourceid` (`project_id`,`release_id`,`source_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='项目版本关联文档表';

DROP TABLE IF EXISTS `project_user`;
CREATE TABLE `project_user` (
                                `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                `project_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'project.id',
                                `user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
                                `role_code` varchar(64) NOT NULL DEFAULT '0' COMMENT '角色，guest：访客，dev：开发者，admin：项目管理员',
                                `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE KEY `uk_projectid_userid` (`project_id`,`user_id`) USING BTREE,
                                KEY `idx_userid` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目用户关系表';

DROP TABLE IF EXISTS `prop`;
CREATE TABLE `prop` (
                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                        `ref_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '关联id',
                        `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '类型，0：doc_info属性',
                        `name` varchar(128) NOT NULL DEFAULT '',
                        `val` text NOT NULL,
                        `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                        `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`) USING BTREE,
                        UNIQUE KEY `uk_docid_name` (`ref_id`,`type`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='属性表';

DROP TABLE IF EXISTS `push_ignore_field`;
CREATE TABLE `push_ignore_field` (
                                     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                     `module_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'module.id',
                                     `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT 'doc_info.data_id',
                                     `field_name` varchar(64) NOT NULL DEFAULT '' COMMENT 'doc_info.name',
                                     `field_description` varchar(64) NOT NULL DEFAULT '' COMMENT '字段描述',
                                     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE KEY `uk_moduleid_dataid_fieldname` (`module_id`,`data_id`,`field_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推送忽略字段';

DROP TABLE IF EXISTS `share_config`;
CREATE TABLE `share_config` (
                                `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '分享形式，1：公开，2：加密',
                                `password` varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
                                `expiration_time` date DEFAULT NULL COMMENT '过期时间。null:永久有效',
                                `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态，1：有效，0：无效',
                                `module_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'module.id',
                                `is_all` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为全部文档',
                                `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                `remark` varchar(128) NOT NULL DEFAULT '' COMMENT '备注',
                                `creator_name` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
                                `is_show_debug` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否显示调试',
                                `is_all_selected_debug` tinyint(4) NOT NULL DEFAULT '1' COMMENT '调试环境是否全选， 1-全选， 0-不选',
                                `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                `share_name` varchar(128) NOT NULL DEFAULT '' COMMENT '分享名称',
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `idx_moduleid` (`module_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='分享配置表';

DROP TABLE IF EXISTS `share_content`;
CREATE TABLE `share_content` (
                                 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                 `share_config_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'share_config.id',
                                 `doc_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '文档id',
                                 `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '父id',
                                 `is_share_folder` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否分享整个分类',
                                 `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                 `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `idx_shareconfigid_docid` (`share_config_id`,`doc_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='分享详情';

DROP TABLE IF EXISTS `share_environment`;
CREATE TABLE `share_environment` (
                                     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                     `share_config_id` bigint(20) unsigned DEFAULT '0' COMMENT '分享配置id',
                                     `module_environment_id` bigint(20) unsigned DEFAULT '0' COMMENT '模块环境id',
                                     PRIMARY KEY (`id`),
                                     KEY `share_environment_share_config_id_index` (`share_config_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='分享环境关联表';

DROP TABLE IF EXISTS `space`;
CREATE TABLE `space` (
                         `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                         `name` varchar(64) NOT NULL DEFAULT '' COMMENT '空间名称',
                         `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建者userid',
                         `creator_name` varchar(64) NOT NULL DEFAULT '',
                         `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建者userid',
                         `modifier_name` varchar(64) NOT NULL DEFAULT '',
                         `is_compose` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否组合空间',
                         `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                         `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                         `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='分组表';

DROP TABLE IF EXISTS `space_user`;
CREATE TABLE `space_user` (
                              `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                              `user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
                              `space_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'space.id',
                              `role_code` varchar(64) NOT NULL DEFAULT '' COMMENT '角色，guest：访客，dev：开发者，admin：空间管理员',
                              `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                              `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                              `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE KEY `uk_groupid_userid` (`space_id`,`user_id`) USING BTREE,
                              KEY `idx_userid` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='分组用户关系表';

DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
                                 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                 `config_key` varchar(64) NOT NULL DEFAULT '',
                                 `config_value` varchar(256) NOT NULL DEFAULT '',
                                 `remark` varchar(128) NOT NULL DEFAULT '',
                                 `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                 `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE KEY `uk_configkey` (`config_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='系统配置表';

DROP TABLE IF EXISTS `system_i18n_config`;
CREATE TABLE `system_i18n_config` (
                                      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                      `lang` varchar(8) NOT NULL DEFAULT '' COMMENT '语言简写，如:zh,en',
                                      `description` varchar(16) NOT NULL DEFAULT '' COMMENT '描述，如：简体中文',
                                      `content` text NOT NULL COMMENT '配置项，json内容',
                                      `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                      `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                      `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`) USING BTREE,
                                      KEY `idx_lang` (`lang`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国际化配置';

DROP TABLE IF EXISTS `system_login_token`;
CREATE TABLE `system_login_token` (
                                      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                      `login_key` varchar(64) NOT NULL DEFAULT '' COMMENT '登录key，MD5',
                                      `token` varchar(256) NOT NULL DEFAULT '' COMMENT '登录token',
                                      `expire_time` datetime NOT NULL COMMENT '过期时间',
                                      `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                      `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE KEY `uk_loginkey` (`login_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录token';

DROP TABLE IF EXISTS `user_dingtalk_info`;
CREATE TABLE `user_dingtalk_info` (
                                      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                      `nick` varchar(64) NOT NULL DEFAULT '' COMMENT '用户在钉钉上面的昵称',
                                      `name` varchar(64) NOT NULL DEFAULT '' COMMENT '员工名称。',
                                      `email` varchar(128) NOT NULL DEFAULT '' COMMENT '员工邮箱。',
                                      `userid` varchar(128) NOT NULL DEFAULT '' COMMENT '员工的userid。',
                                      `unionid` varchar(128) NOT NULL DEFAULT '' COMMENT '用户在当前开放应用所属企业的唯一标识。',
                                      `openid` varchar(128) NOT NULL DEFAULT '' COMMENT '用户在当前开放应用内的唯一标识。',
                                      `user_info_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
                                      `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                      `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE KEY `uk_unionid` (`unionid`) USING BTREE,
                                      KEY `idx_openid` (`openid`) USING BTREE,
                                      KEY `idx_userid` (`user_info_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='钉钉开放平台用户';

DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                             `username` varchar(128) NOT NULL DEFAULT '' COMMENT '登录账号/邮箱',
                             `password` varchar(128) NOT NULL DEFAULT '' COMMENT '登录密码',
                             `nickname` varchar(64) NOT NULL DEFAULT '' COMMENT '昵称',
                             `is_super_admin` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否是超级管理员',
                             `source` varchar(64) NOT NULL DEFAULT 'register',
                             `email` varchar(128) NOT NULL DEFAULT '',
                             `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0：禁用，1：启用，2：重设密码',
                             `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                             `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                             `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户表';

ALTER TABLE `user_info` ADD COLUMN `mfa_sk` varchar(128) NOT NULL DEFAULT '' COMMENT '多因素认证密钥';

DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message` (
                                `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                `user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
                                `message` varchar(256) NOT NULL DEFAULT '',
                                `is_read` tinyint(4) NOT NULL DEFAULT '0',
                                `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '同user_subscribe.type',
                                `source_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '同user_subscribe.source_id',
                                `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `idx_userid` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='站内消息';

DROP TABLE IF EXISTS `user_subscribe`;
CREATE TABLE `user_subscribe` (
                                  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                  `user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
                                  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '类型，1：订阅接口，2：订阅项目',
                                  `source_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '关联id，当type=1对应doc_info.id；type=2对应project.id',
                                  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE KEY `uk_userid_type_sourceid` (`user_id`,`type`,`source_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户订阅表';

DROP TABLE IF EXISTS `user_wecom_info`;
CREATE TABLE `user_wecom_info` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                   `mobile` varchar(20) DEFAULT NULL COMMENT '企业微信绑定手机号码',
                                   `user_info_id` bigint(20) NOT NULL COMMENT 'user_info.id',
                                   `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                   `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `idx_userid` (`user_info_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `constant_info` (`id`, `project_id`, `module_id`, `doc_id`, `content`, `gmt_create`, `gmt_modified`) VALUES
                                                                                                                     (1, 5, 0, 0, '<h2>全局状态码</h2><figure class=\"table\"><table><thead><tr><th><strong>HTTP状态码</strong></th><th><strong>说明</strong></th></tr></thead><tbody><tr><td>200</td><td>OK</td></tr><tr><td>400</td><td>Bad Request</td></tr><tr><td>401</td><td>Unauthorized</td></tr><tr><td>403</td><td>Forbidden</td></tr><tr><td>404</td><td>Not Found</td></tr><tr><td>405</td><td>Method Not Allowed</td></tr><tr><td>500</td><td>Internal Server Error</td></tr><tr><td>502</td><td>Bad Gateway</td></tr><tr><td>503</td><td>Service Unavailable</td></tr><tr><td>504</td><td>Gateway Timeout</td></tr></tbody></table></figure>', '2022-11-03 20:32:43', '2022-11-03 20:32:43'),
                                                                                                                     (2, 0, 5, 0, '<h2>商品中心错误码</h2><ul><li>10001：商品不存在</li><li>10002：分类不存在</li><li>10003：商品已下架</li><li>10004：商品已售罄</li></ul>', '2022-11-03 20:33:53', '2022-11-03 20:33:53');

INSERT INTO `doc_info` (`id`, `data_id`, `doc_key`, `md5`, `name`, `description`, `author`, `type`, `url`, `version`, `http_method`, `content_type`, `deprecated`, `is_folder`, `parent_id`, `module_id`, `is_use_global_headers`, `is_use_global_params`, `is_use_global_returns`, `is_request_array`, `is_response_array`, `request_array_type`, `response_array_type`, `create_mode`, `modify_mode`, `creator_id`, `creator_name`, `modifier_id`, `modifier_name`, `order_index`, `remark`, `is_show`, `is_deleted`, `is_locked`, `status`, `gmt_create`, `gmt_modified`) VALUES
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 (97, '672fe4f587d9b4059baf187c8dbd02b0', '8421b3dc53855aa82cd9a9f493b08376', '', '产品模块', '', 'thc', 0, '', '', '', '', '$false$', 1, 0, 5, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 99999, 'thc', 99999, 'thc', 2, NULL, 1, 0, 0, 10, '2022-11-03 20:26:07', '2024-01-17 14:46:46'),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 (98, '002264d7a6da2efe73783132ad776333', '85ecc3ba09e5e42522e7db18cceb6564', '6965a2919c3e33a4d5cfadbfe2d6f82e', '新增产品', '新增产品', 'thc', 0, '/shop/product', '', 'POST', 'application/json', '$false$', 0, 97, 5, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 99999, 'thc', 99999, 'thc', 1, NULL, 1, 0, 0, 10, '2022-11-03 20:26:07', '2024-01-17 14:46:46'),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 (99, '5703af1fd4bfc0b45203e4becad84ac9', 'f2684aaabe8d7ab4cc2163d31deba8fb', '3de4ea5522a4486b63f0d9dc3d1408ea', '修改产品', '修改产品', 'thc', 0, '/shop/product', '', 'PUT', 'application/json', '$false$', 0, 97, 5, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 99999, 'thc', 99999, 'thc', 2, NULL, 1, 0, 0, 10, '2022-11-03 20:26:07', '2024-01-17 14:46:46'),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 (100, '3f3baebee5dc24796a97d10ec0d918a2', '887332d7dca1444e6e50ba30d950f6ff', 'e89523f48f3a8c0b6de371eaf4c6d3a7', '查询产品', '查询产品', 'thc', 0, '/shop/product', '', 'GET', 'application/x-www-form-urlencoded;charset=UTF-8', '$false$', 0, 97, 5, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 99999, 'thc', 99999, 'thc', 3, NULL, 1, 0, 0, 10, '2022-11-03 20:26:07', '2024-01-17 14:46:46'),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 (101, '4d7b0a76943dbbd674ea0a0312c3f78f', 'd9addaac101a3c37b680b3eff5e74b81', '', '订单查询', '', '', 0, '', '', '', '', '$false$', 1, 0, 6, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 16, '超级管理员', 16, '超级管理员', 0, NULL, 1, 0, 0, 10, '2022-11-03 20:37:49', '2024-01-17 14:46:46'),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 (102, 'bd114ef0f21490c54376d5294ba1b9cd', '09fc7ac7e58010f990ba4ebe3f362bcc', '94a9a4170c569c3a8c1d3e77dc39eb2e', '获取订单详情', '', 'jim', 0, '/order/detail', '', 'GET', '', '$false$', 0, 101, 6, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 16, '超级管理员', 16, '超级管理员', 10000, '', 1, 0, 0, 10, '2022-11-03 20:38:40', '2024-01-17 14:46:46'),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 (103, '1ce92448229d61a9f2d6da55e9fc36d0', '2dd9dc7d62a7b5f5f0a94c791775b6a5', '', '登录模块', '', '', 0, '', '', '', '', '$false$', 1, 0, 7, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 16, '超级管理员', 16, '超级管理员', 0, NULL, 1, 0, 0, 10, '2022-11-03 20:47:04', '2024-01-17 14:46:46'),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 (104, '74277e6463369602f9d4b1a55c8da843', 'ddf9207b0dafebb1e7ac1dea25b071cc', '', '分类模块', '', 'tanghc', 0, '', '', '', '', '$false$', 1, 0, 5, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 99999, 'thc', 99999, 'thc', 1, NULL, 1, 0, 0, 10, '2022-11-03 20:55:13', '2024-01-17 14:46:46'),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 (105, 'dc1395406d2e13186a0cf684dbf46ba7', '53f32756159dc3e4fca045ee2cfa29fe', 'c0fcaef099b3f06a6711f8bb2370418b', '添加分类', '添加分类', 'tanghc', 0, '/category/add', '', 'POST', 'application/json', '$false$', 0, 104, 5, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 99999, 'thc', 99999, 'thc', 1, NULL, 1, 0, 0, 10, '2022-11-03 20:55:13', '2024-01-17 14:46:46'),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 (106, 'e8c5689b060fffef10e24dfae3c393d7', '275818ece7ed570485168ba7c34e456b', 'd6ecdb4ff1eed507cb011fed25c4c5a2', '查询分类', '查询分类', 'tanghc', 0, '/category/get', '', 'POST', 'application/x-www-form-urlencoded;charset=UTF-8', '$false$', 0, 104, 5, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 99999, 'thc', 99999, 'thc', 2, NULL, 1, 0, 0, 10, '2022-11-03 20:55:13', '2024-01-17 14:46:46'),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 (118, 'a86ea675dd6ecd5147d957932a75df7a', '72b239dc0c86e0f0f8bc06994ed4e9d3', '5a193f3d886e786c398c414fec1e6806', '登录', '', 'jim', 0, 'login', '', 'POST', 'application/json', '$false$', 0, 103, 7, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 16, '超级管理员', 16, '超级管理员', 10000, '', 1, 0, 0, 10, '2022-11-03 20:57:43', '2024-01-17 14:46:46'),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 (119, '97b338a61dace4633820e38f69621249', '281b28fd705b7f08ab55c964ff34ddc7', '', '退出', '', '', 0, 'logout', '', 'GET', '', '$false$', 0, 103, 7, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 16, '超级管理员', 16, '超级管理员', 10010, '', 1, 0, 0, 10, '2022-11-03 20:58:18', '2024-01-17 14:46:46');

INSERT INTO `doc_param` (`id`, `data_id`, `name`, `type`, `required`, `max_length`, `example`, `description`, `enum_id`, `doc_id`, `parent_id`, `style`, `create_mode`, `modify_mode`, `creator_id`, `creator_name`, `modifier_id`, `modifier_name`, `order_index`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
                                                                                                                                                                                                                                                                                                                    (697, '6d0c7aa7d6ad2f0ae98d22867599d0e1', 'username', 'string', 0, '-', 'jim', '用户名', 0, 95, 0, 2, 1, 1, 99999, 'thc', 99999, 'thc', 1, 1, '2022-11-03 20:26:07', '2022-11-03 20:55:13'),
                                                                                                                                                                                                                                                                                                                    (698, '9dffc368418c6ddbc950a25fe7a00813', 'password', 'string', 0, '-', '123', '密码', 0, 95, 0, 2, 1, 1, 99999, 'thc', 99999, 'thc', 2, 1, '2022-11-03 20:26:07', '2022-11-03 20:55:13'),
                                                                                                                                                                                                                                                                                                                    (699, '140f64afcc79260bf84f3f0ae81165f9', 'code', 'int32', 0, '-', '0', '返回码,0表示成功，其它失败', 0, 95, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 1, 1, '2022-11-03 20:26:07', '2022-11-03 20:55:13'),
                                                                                                                                                                                                                                                                                                                    (700, 'dba7c3f7015673ee189e6c43e678bd50', 'data', 'object', 0, '', '', '返回数据', 0, 95, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 2, 1, '2022-11-03 20:26:07', '2022-11-03 20:55:13'),
                                                                                                                                                                                                                                                                                                                    (701, '9fedeadcd8dea4d6839943445509e82a', 'userId', 'int32', 0, '-', '11', '用户id', 0, 95, 700, 3, 1, 1, 99999, 'thc', 99999, 'thc', 3, 1, '2022-11-03 20:26:07', '2022-11-03 20:55:13'),
                                                                                                                                                                                                                                                                                                                    (702, '5f1414b3189c6427ce7a5f4edb592627', 'username', 'string', 0, '-', 'jim', '用户名', 0, 95, 700, 3, 1, 1, 99999, 'thc', 99999, 'thc', 4, 1, '2022-11-03 20:26:07', '2022-11-03 20:55:13'),
                                                                                                                                                                                                                                                                                                                    (703, '66d991fc24461a130d503bc548aedf1d', 'token', 'string', 0, '-', 'xx', 'token', 0, 95, 700, 3, 1, 1, 99999, 'thc', 99999, 'thc', 5, 1, '2022-11-03 20:26:07', '2022-11-03 20:55:13'),
                                                                                                                                                                                                                                                                                                                    (704, '9bf4b1dbd940e93bc815552325e4e5fc', 'msg', 'string', 0, '-', 'OK', '错误消息', 0, 95, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 6, 1, '2022-11-03 20:26:07', '2022-11-03 20:55:13'),
                                                                                                                                                                                                                                                                                                                    (705, '69dcc015a2e72b19b017b859146b69ae', 'code', 'int32', 0, '-', '0', '返回码,0表示成功，其它失败', 0, 96, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 1, 1, '2022-11-03 20:26:07', '2022-11-03 20:55:13'),
                                                                                                                                                                                                                                                                                                                    (706, '3a2f0ceb7285c1660a951d580a563ef1', 'data', 'object', 0, '-', '', '返回数据', 0, 96, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 2, 1, '2022-11-03 20:26:07', '2022-11-03 20:55:13'),
                                                                                                                                                                                                                                                                                                                    (707, '4b4a499fb3001957e21538db1918d673', 'msg', 'string', 0, '-', 'OK', '错误消息', 0, 96, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 3, 1, '2022-11-03 20:26:07', '2022-11-03 20:55:13'),
                                                                                                                                                                                                                                                                                                                    (708, '87aacd47f8df31220676c1d8f4a8647b', 'productNo', 'string', 0, '-', 'aa', '产品id', 0, 98, 0, 2, 1, 1, 99999, 'thc', 99999, 'thc', 1, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (709, '4ab71029575f1e154649e2f2f166a829', 'remark', 'string', 0, '-', 'xxx', '备注', 0, 98, 0, 2, 1, 1, 99999, 'thc', 99999, 'thc', 2, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (710, '1863f6e2b26e66c0181b6db99ad1bc91', 'code', 'int32', 0, '-', '0', '返回码,0表示成功，其它失败', 0, 98, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 1, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (711, 'f5c6d52f0cc1f2270868299785c65ad5', 'data', 'string', 0, '-', '', '返回数据', 0, 98, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 2, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (712, 'b57e9de2cfc702cf8e2bcb583c187095', 'msg', 'string', 0, '-', 'OK', '错误消息', 0, 98, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 3, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (713, '15cee4e78cbe980d9365a0d46ce2342a', 'productNo', 'string', 0, '-', 'aa', '产品id', 0, 99, 0, 2, 1, 1, 99999, 'thc', 99999, 'thc', 1, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (714, '91229547a9849c329d0d2e7321fb03ca', 'remark', 'string', 0, '-', 'xxx', '备注', 0, 99, 0, 2, 1, 1, 99999, 'thc', 99999, 'thc', 2, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (715, '63c262d93317abd3b213b1ede6414c19', 'code', 'int32', 0, '-', '0', '返回码,0表示成功，其它失败', 0, 99, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 1, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (716, '3a84d6b29c00cb2e0a83236a063d03f8', 'data', 'object', 0, '', '', '返回数据', 0, 99, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 2, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (717, '86229614609aea0382c9a0ae021b63da', 'productNo', 'string', 0, '-', 'aa', '产品id', 0, 99, 716, 3, 1, 1, 99999, 'thc', 99999, 'thc', 3, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (718, '2bfd75998e84f6cce8b5a76eb8330dee', 'remark', 'string', 0, '-', 'xxx', '备注', 0, 99, 716, 3, 1, 1, 99999, 'thc', 99999, 'thc', 4, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (719, '6fc91716d033ebeed4a3a572af877102', 'productDetailVO', 'object', 0, '', '', '产品详情', 0, 99, 716, 3, 1, 1, 99999, 'thc', 99999, 'thc', 5, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (720, '7a8996d3ee2b3edb32067f8aff324990', 'productNo', 'string', 0, '-', 'aa', '产品id', 0, 99, 719, 3, 1, 1, 99999, 'thc', 99999, 'thc', 6, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (721, '4771a64ed10f0ed47fecfd3d6e402c31', 'remark', 'string', 0, '-', 'e6oj41', '备注', 0, 99, 719, 3, 1, 1, 99999, 'thc', 99999, 'thc', 7, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (722, '886ca8c3c8e0268db0417ddc04a4b4e8', 'msg', 'string', 0, '-', 'OK', '错误消息', 0, 99, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 8, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (723, 'e55e59e3476c8cfffcb8315a7221bf12', 'productNo', 'int32', 1, '-', '123', '产品id', 0, 100, 0, 5, 1, 1, 99999, 'thc', 99999, 'thc', 1, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (724, '9672e9ae31453c7f4cfe91a72f8e9c86', 'code', 'int32', 0, '-', '0', '返回码,0表示成功，其它失败', 0, 100, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 1, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (725, '4c98db43a9608d915aafc062aa30da3d', 'data', 'object', 0, '', '', '返回数据', 0, 100, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 2, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (726, '20633f0f0226af0389af78c951f773e8', 'productNo', 'string', 0, '-', 'aa', '产品id', 0, 100, 725, 3, 1, 1, 99999, 'thc', 99999, 'thc', 3, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (727, '77e4890e0422e5afd7833a54146be1a2', 'remark', 'string', 0, '-', 'xxx', '备注', 0, 100, 725, 3, 1, 1, 99999, 'thc', 99999, 'thc', 4, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (728, 'df7d62138fb04d21752ff03ae8715462', 'productDetailVO', 'object', 0, '', '', '产品详情', 0, 100, 725, 3, 1, 1, 99999, 'thc', 99999, 'thc', 5, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (729, '22792d15059d505599bb3bd10ff82e5c', 'productNo', 'string', 0, '-', 'aa', '产品id', 0, 100, 728, 3, 1, 1, 99999, 'thc', 99999, 'thc', 6, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (730, '8cc9eb5b012c8e2cea8909fac1a3052d', 'remark', 'string', 0, '-', 'js26ts', '备注', 0, 100, 728, 3, 1, 1, 99999, 'thc', 99999, 'thc', 7, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (731, 'df6f4a37db42291f6a2c5d7ee8edce3a', 'msg', 'string', 0, '-', 'OK', '错误消息', 0, 100, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 8, 0, '2022-11-03 20:26:07', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (732, '2c39b57252b29bea5abe080c043df1bd', 'orderNo', 'string', 1, '64', '100001', '订单号', 0, 102, 0, 5, 0, 0, 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 20:38:40', '2022-11-03 20:38:40'),
                                                                                                                                                                                                                                                                                                                    (733, '8722180a8e809266eaee1a6f7aee8533', 'id', 'int32', 0, '-', '1', '分类id', 0, 105, 0, 2, 1, 1, 99999, 'thc', 99999, 'thc', 1, 0, '2022-11-03 20:55:13', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (734, '70b33ba815bf0d69426c4d59354bc247', 'name', 'string', 0, '-', '手机', '分类名称', 0, 105, 0, 2, 1, 1, 99999, 'thc', 99999, 'thc', 2, 0, '2022-11-03 20:55:13', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (735, 'b6f81f84555bf24177048e38be5c58c7', 'code', 'int32', 0, '-', '0', '返回码,0表示成功，其它失败', 0, 105, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 1, 0, '2022-11-03 20:55:13', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (736, '06ceadf33d2ead81240983074da12b8d', 'data', 'int32', 0, '-', '', '返回数据', 0, 105, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 2, 0, '2022-11-03 20:55:13', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (737, 'c8908921ecde188ffc21bc50ee4d928b', 'msg', 'string', 0, '-', 'OK', '错误消息', 0, 105, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 3, 0, '2022-11-03 20:55:13', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (738, '834523c04f05b52f18a74490d9095209', 'id', 'int32', 0, '-', '407', 'No comments found.', 0, 106, 0, 5, 1, 1, 99999, 'thc', 99999, 'thc', 1, 0, '2022-11-03 20:55:13', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (739, '6546f3a87fe176e094838d3668fa359f', 'code', 'int32', 0, '-', '0', '返回码,0表示成功，其它失败', 0, 106, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 1, 0, '2022-11-03 20:55:13', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (740, '2b6e1d458788353d48129c4f3253495c', 'data', 'object', 0, '', '', '返回数据', 0, 106, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 2, 0, '2022-11-03 20:55:13', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (741, '6cfaea67c713a19350636aefd8157e4c', 'id', 'int32', 0, '-', '1', '分类id', 0, 106, 740, 3, 1, 1, 99999, 'thc', 99999, 'thc', 3, 0, '2022-11-03 20:55:13', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (742, 'f83366b3cb607864f4b2e914c4f386e0', 'name', 'string', 0, '-', '手机', '分类名称', 0, 106, 740, 3, 1, 1, 99999, 'thc', 99999, 'thc', 4, 0, '2022-11-03 20:55:13', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (743, '5740aa05a4d0fd16f390d14e204f25bc', 'msg', 'string', 0, '-', 'OK', '错误消息', 0, 106, 0, 3, 1, 1, 99999, 'thc', 99999, 'thc', 5, 0, '2022-11-03 20:55:13', '2022-11-03 20:56:26'),
                                                                                                                                                                                                                                                                                                                    (803, '3ede4dd71a319dd99d9f41f8b3c3bdfe', 'username', 'string', 1, '64', 'Jim', '用户名', 0, 118, 0, 2, 0, 0, 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 20:57:43', '2022-11-03 20:57:43'),
                                                                                                                                                                                                                                                                                                                    (804, '12d3df7b6f4feef59617f23c8bd6528e', 'password', 'string', 1, '64', 'xx', '密码', 0, 118, 0, 2, 0, 0, 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 20:57:43', '2022-11-03 20:57:43'),
                                                                                                                                                                                                                                                                                                                    (805, 'e3bbecd30239ac4fb541263d32e7812f', 'code', 'number', 1, '64', '0', '', 0, 102, 0, 3, 0, 0, 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 20:59:33', '2022-11-03 20:59:33'),
                                                                                                                                                                                                                                                                                                                    (806, '6f9e267c3c4babb69394b2ee9b942a94', 'data', 'object', 1, '', '', '', 0, 102, 0, 3, 0, 0, 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 20:59:33', '2022-11-03 20:59:33'),
                                                                                                                                                                                                                                                                                                                    (807, '35f5f5584504d8cbf6bb45e67ce016bc', 'orderNo', 'string', 1, '64', 'aa', '订单号', 0, 102, 806, 3, 0, 0, 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 20:59:33', '2022-11-03 20:59:33'),
                                                                                                                                                                                                                                                                                                                    (808, '38e3e71cf709183ebffc5cf92219e2a8', 'remark', 'string', 1, '64', 'xxx', '备注', 0, 102, 806, 3, 0, 0, 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 20:59:33', '2022-11-03 20:59:33'),
                                                                                                                                                                                                                                                                                                                    (809, '7216cf40f2ea884a94cf733ad2df88da', 'msg', 'string', 1, '64', 'OK', '', 0, 102, 0, 3, 0, 0, 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 20:59:33', '2022-11-03 20:59:33'),
                                                                                                                                                                                                                                                                                                                    (810, '81284f9eaf7e78c3a40f65aff12c3e46', 'code', 'number', 1, '64', '0', '', 0, 118, 0, 3, 0, 0, 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 21:00:03', '2022-11-03 21:00:03'),
                                                                                                                                                                                                                                                                                                                    (811, 'a35656f391a5678247952e20895d6b99', 'data', 'object', 1, '', '', '', 0, 118, 0, 3, 0, 0, 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 21:00:03', '2022-11-03 21:00:03'),
                                                                                                                                                                                                                                                                                                                    (812, '64030857ff745dcffc27afefaa516401', 'token', 'string', 1, '64', 'xxx', '登录成功后的token', 0, 118, 811, 3, 0, 0, 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 21:00:03', '2022-11-03 21:00:03'),
                                                                                                                                                                                                                                                                                                                    (813, 'f574f1cbbf5dd8a2cbbc9079e1a7a549', 'msg', 'string', 1, '64', 'OK', '', 0, 118, 0, 3, 0, 0, 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 21:00:03', '2022-11-03 21:00:03');

INSERT INTO `enum_info` (`id`, `data_id`, `name`, `description`, `module_id`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
    (5, '50c0c6ce9bf63a5f7f192e1a12c76e0e', '商品分类', '商品分类', 5, 0, '2022-11-03 20:26:42', '2022-11-03 20:26:42');

INSERT INTO `enum_item` (`id`, `enum_id`, `name`, `type`, `value`, `description`, `order_index`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
                                                                                                                                                 (9, 5, 'MOBILE', 'number', '1', '手机', 0, 0, '2022-11-03 20:27:20', '2022-11-03 20:27:20'),
                                                                                                                                                 (10, 5, 'COMPUTER', 'number', '2', '电脑', 0, 0, '2022-11-03 20:27:36', '2022-11-03 20:27:36'),
                                                                                                                                                 (11, 5, 'CAR', 'number', '3', '车品', 0, 0, '2022-11-03 20:27:56', '2022-11-03 20:27:56'),
                                                                                                                                                 (12, 5, 'FOOD', 'number', '4', '食品', 0, 0, '2022-11-03 20:28:15', '2022-11-03 20:28:15');

INSERT INTO `gen_template` (`id`, `name`, `content`, `is_deleted`, `group_name`, `gmt_create`, `gmt_modified`) VALUES
                                                                                                                   (1, 'aaa', 'import lombok.Data;\n\n@Data\npublic class MyClass implements Serializable {\n\n    private static final long serialVersionUID = ${sys.nextId}L;\n\n#foreach($param in ${doc.requestParams})\n    /**\n     * ${param.description}\n     */\n    private ${param.type} ${param.name};\n\n#end\n}\n', 1, '默认分组', '2024-07-25 21:19:41', '2024-07-25 22:51:55'),
                                                                                                                   (2, 'curl', 'curl --location \'${doc.url}?${doc.queryString}\' \\\n#foreach($param in ${doc.headerParams})\n--header \'${param.name}: ${param.example}\' \\\n#end\n#if(${doc.httpMethod} == \'POST\' || ${doc.httpMethod} == \'PUT\')\n--data \'${doc.requestExample}\'\n#end\n', 0, '默认分组', '2024-07-25 22:53:43', '2024-07-27 14:27:52'),
                                                                                                                   (3, 'vvvv', 'import lombok.Data;\n\n@Data\npublic class MyClass implements Serializable {\n\n    private static final long serialVersionUID = ${sys.nextId}L;\n\n#foreach($param in ${doc.requestParams})\n    /**\n     * ${param.description}\n     */\n    private ${param.type} ${param.name};\n\n#end\n}\n', 1, '默认分组', '2024-07-25 22:55:19', '2024-07-25 22:55:26'),
                                                                                                                   (4, 'dddd', '#set($jq=\"$\")\n<template>\n  <div class=\"app-container\">\n    <el-form :inline=\"true\" :model=\"searchFormData\" class=\"demo-form-inline\" size=\"mini\">\n      #foreach($param in ${doc.queryParams})\n        <el-form-item\n      	#if( \"${param.description}\" != \"\" )\n        label=\"${param.description}\"\n        #else\n        label=\"${param.name}\"\n        #end\n      >\n      	<el-input v-model=\"searchFormData.${param.name}\" :clearable=\"true\" placeholder=\"${param.description}\" style=\"width: 250px;\" />\n      </el-form-item>\n      #end\n      <el-form-item>\n        <el-button type=\"primary\" icon=\"el-icon-search\" @click=\"loadTable\">查询</el-button>\n      </el-form-item>\n    </el-form>\n\n 	<el-table\n      :data=\"pageInfo.rows\"\n      border\n      highlight-current-row\n    >\n    #foreach($param in ${doc.responseParams})\n      <el-table-column\n        prop=\"${param.name}\"\n        #if( \"${param.description}\" != \"\" )\n        label=\"${param.description}\"\n        #else\n        label=\"${param.name}\"\n        #end\n      />\n     #end\n     <el-table-column>\n        <template slot-scope=\"scope\">\n          <el-button type=\"text\" size=\"mini\" @click=\"onTableUpdate(scope.row)\">修改</el-button>\n          <el-button type=\"text\" size=\"mini\" @click=\"onTableDelete(scope.row)\">删除</el-button>\n        </template>\n      </el-table-column>\n    </el-table>\n    <el-pagination\n      background\n      style=\"margin-top: 5px\"\n      :current-page=\"searchFormData.pageIndex\"\n      :page-size=\"searchFormData.pageSize\"\n      :page-sizes=\"[5, 10, 20, 40]\"\n      :total=\"pageInfo.total\"\n      layout=\"total, sizes, prev, pager, next\"\n      @size-change=\"onSizeChange\"\n      @current-change=\"onPageIndexChange\"\n    />\n\n  </div>\n</template>\n\n<script>\nimport axios from \'axios\'\n// 创建axios实例\nconst client = axios.create({\n  baseURL: process.env.VUE_APP_BASE_API, // api 的 base_url\n  timeout: 60000 // 请求超时时间,60秒\n})\nexport default {\n  data() {\n    return {\n      searchFormData: {\n        #foreach($column in $columns)\n        ${column.javaFieldName}: \'\',\n        #end\n        pageIndex: 1,\n        pageSize: 10\n      },\n      pageInfo: {\n        rows: [],\n        total: 0\n      },\n      dialogVisible: false,\n      dialogTitle: \'\',\n      dialogFormData: {\n        #foreach($column in $columns)\n        #if($velocityCount > 1),#end ${column.javaFieldName}: \'\'\n        #end\n      },\n      dialogFormRules: {\n        #foreach($column in $columns)\n        #if($velocityCount > 1),#end ${column.javaFieldName}: [\n          { required: true, message: \'不能为空\', trigger: \'blur\' }\n        ]\n        #end\n      }\n    }\n  },\n  created() {\n    this.loadTable()\n  },\n  methods: {\n    loadTable() {\n      this.post(\'${doc.url}\', this.searchFormData, resp => {\n        this.pageInfo.rows = resp.data\n      })\n    },\n    onSizeChange(size) {\n      this.searchFormData.pageSize = size\n      this.loadTable()\n    },\n    onPageIndexChange(pageIndex) {\n      this.searchFormData.pageIndex = pageIndex\n      this.loadTable()\n    },\n    /**\n     * 请求接口\n     * @param uri uri\n     * @param data 请求数据\n     * @param callback 成功时回调\n     * @param errorCallback 错误时回调\n     */\n    post(uri, data, callback, errorCallback) {\n      const that = this\n      client.post(uri, data).then(function(response) {\n        const resp = response.data\n        const code = resp.code\n        if (code === \'0\') { // 成功\n          callback && callback.call(that, resp)\n        } else {\n          that.${jq}message.error(resp.msg)\n        }\n      }).catch(function(error) {\n        console.error(error)\n        errorCallback && errorCallback(error)\n        that.${jq}message.error(error.message)\n      })\n    }\n  }\n}\n</script>\n', 0, '前端', '2024-07-25 22:56:40', '2024-07-25 22:56:40'),
                                                                                                                   (5, 'Test', 'export interface Res {\n	code: string;\n    data: Record<string, unknow>;\n    msg: string;\n}\n\n/**\n * ${doc.name}\n#foreach($param in ${doc.responseParams}) \n * @param {${param.type}} ${param.name} ${param.description}\n#end\n * returns\n*/\nexport function fun(\n#foreach($param in ${doc.responseParams}) \n    ${param.name}: ${param.type}\n#end\n) {\n	var params = []\n#foreach($param in ${doc.responseParams}) \n    params.push(\'${param.name}=\'+${param.name})\n#end\n	return request.get(\'${doc.url}?\' + param.join(\'&\'))\n}\n', 0, '默认分组', '2024-07-27 10:34:06', '2024-07-27 10:34:06'),
                                                                                                                   (6, 'Test2', 'export interface Res {\n	code: string;\n    data: Record<string, unknow>;\n    msg: string;\n}\n\n/**\n * ${doc.name}\n#foreach($param in ${doc.queryParams})\n * @param {${param.type}} ${param.name} ${param.description}\n#end\n * returns\n*/\nexport function fun(\n#foreach($param in ${doc.queryParams}) \n    ${param.name}: ${param.type}\n#end\n) {\n	var params = []\n#foreach($param in ${doc.queryParams}) \n    params.push(\'${param.name}=\'+${param.name})\n#end\n	return request.get(\'${doc.url}?\' + param.join(\'&\'))\n}\n', 0, '默认分组', '2024-07-27 10:40:26', '2024-07-27 11:41:41'),
                                                                                                                   (7, 'Test', 'export interface Res {\n	code: string;\n    data: Record<string, unknow>;\n    msg: string;\n}\n\n/**\n * ${doc.name}\n#foreach($param in ${doc.queryParams})\n * @param {${param.type}} ${param.name} ${param.description}\n#end\n * returns\n*/\nexport function fun(\n#foreach($param in ${doc.queryParams}) \n    ${param.name}: ${param.type}\n#end\n) {\n	var params = []\n#foreach($param in ${doc.queryParams}) \n    params.push(\'${param.name}=\'+${param.name})\n#end\n	return request.get(\'${doc.url}?\' + param.join(\'&\'))\n}\n', 0, '默认分组', '2024-07-27 10:40:30', '2024-07-27 10:40:30'),
                                                                                                                   (8, '222', 'import lombok.Data;\n\n@Data\npublic class MyClass implements Serializable {\n\n    private static final long serialVersionUID = ${sys.nextId}L;\n\n#foreach($param in ${doc.requestParams})\n    /**\n     * ${param.description}\n     */\n    private ${param.type} ${param.name};\n\n#end\n}\n', 1, '默认分组', '2024-07-27 10:42:26', '2024-07-27 10:43:23'),
                                                                                                                   (9, '222', 'import lombok.Data;\n\n@Data\npublic class MyClass implements Serializable {\n\n    private static final long serialVersionUID = ${sys.nextId}L;\n\n#foreach($param in ${doc.requestParams})\n    /**\n     * ${param.description}\n     */\n    private ${param.type} ${param.name};\n\n#end\n}\n', 1, '默认分组', '2024-07-27 10:42:28', '2024-07-27 10:43:20'),
                                                                                                                   (10, '2222', '接口编号: ${doc.docKey}\n接口名称: ${doc.name}\nURL: ${doc.url}\nMETHOD： ${doc.httpMethod}\ncontentType: ${doc.contentType}\ndescription: ${doc.description}\n\n-- headers\n#foreach($param in ${doc.headerParams}) \n    ${param.name}\n#end\n\n-- pathParams\n#foreach($param in ${doc.pathParams}) \n    ${param.name}\n#end\n\n-- queryParams\n#foreach($param in ${doc.queryParams}) \n    ${param.name}\n#end\n\n-- requestParams\n#foreach($param in ${doc.requestParams}) \n    ${param.name}\n#end\n\n-- responseParams\n#foreach($param in ${doc.responseParams}) \n    name: ${param.name}\n    type: ${param.type}\n    required: ${param.require}\n    example: ${param.example}\n    description: ${param.description}\n    ======\n    \n#end\n\n-- req\n${doc.requestExample}\n\n-- resp\n${doc.responseExample}\n', 0, '默认分组', '2024-07-27 10:43:27', '2024-07-27 14:28:56'),
                                                                                                                   (11, 'JAVA_RESPONSE', '## 首字母大写\n#macro(capitalizeFirstLetter $input)\n    #if($input && $input.length() > 0)\n        #set($firstChar = $input.substring(0, 1).toUpperCase())\n        #set($rest = $input.substring(1))\n$firstChar$rest#end\n#end\n## 截取路径的最后一个单词并首字母大写\n#macro(getLastPartCapitalized $path)\n    #set($parts = $path.split(\"/\"))\n    #set($lastPart = $parts[$parts.size() - 1])\n    #set($capitalizedLastPart = $lastPart.substring(0, 1).toUpperCase() + $lastPart.substring(1))\n$capitalizedLastPart#end\n## 转换为java类\n#macro(getType $type $name)\n#if ($type == \"string\")String#elseif ($type == \"number\")BigDecimal#elseif ($type == \"array\")List<${name}>#elseif ($type == \"num_array\")List<BigDecimal>#elseif ($type == \"str_array\")List<String>#elseif ($type == \"enum\")String#elseif ($type == \"boolean\")Boolean\n#else${name}#end#end\n## 声明递归生成静态内部类\n#macro(generateStaticInnerClasses $responseParam)\n    /** 静态内部类${responseParam.name} */\n    public static class #capitalizeFirstLetter($responseParam.name) {\n    #foreach($childParam in ${responseParam.children})\n\n        /** ${childParam.description} */\n        #set($capitalizedName = $childParam.name.substring(0, 1).toUpperCase() + $childParam.name.substring(1))\n        private #getType(${childParam.type},$capitalizedName) ${childParam.name};\n    #end\n    #foreach($childParam in ${responseParam.children})\n        #set($capitalizedName = $childParam.name.substring(0, 1).toUpperCase() + $childParam.name.substring(1))\n        public #getType(${childParam.type},$capitalizedName) get$capitalizedName() {\n            return ${childParam.name};\n        }\n        public void set$capitalizedName(#getType(${childParam.type},$capitalizedName) ${childParam.name}) {\n            this.${childParam.name} = ${childParam.name};\n        }\n    #end\n    #foreach($childParam in ${responseParam.children})\n        #if(${childParam.children})\n        ## 递归生成静态内部类\n        #generateStaticInnerClasses($childParam)\n\n        #end\n    #end\n    }   \n#end\n\nimport java.util.*;\nimport java.math.*;\n\n/**\n * name : ${doc.name}响应类\n * author : ${doc.author}\n * description : ${doc.description}\n * version : ${doc.version}\n */\npublic class #getLastPartCapitalized($doc.url)ReponseDTO {\n    #foreach($responseParam in ${doc.responseParams})\n\n        /** ${responseParam.description} */\n        #set($capitalizedName = $responseParam.name.substring(0, 1).toUpperCase() + $responseParam.name.substring(1))\n        private #getType(${responseParam.type},$capitalizedName) ${responseParam.name};\n    #end\n    #foreach($responseParam in ${doc.responseParams})\n\n        #set($capitalizedName = $responseParam.name.substring(0, 1).toUpperCase() + $responseParam.name.substring(1))\n        public #getType(${responseParam.type},$capitalizedName) get$capitalizedName() {\n            return ${responseParam.name};\n        }\n        public void set$capitalizedName(#getType(${responseParam.type},$capitalizedName) ${responseParam.name}) {\n            this.${responseParam.name} = ${responseParam.name};\n        }\n    #end\n    #foreach($responseParam in ${doc.responseParams})\n    #if(${responseParam.children})\n        ## 生成静态内部类\n        #generateStaticInnerClasses($responseParam)\n\n        #end\n    #end\n}\n', 0, 'JAVA', '2024-07-27 10:43:27', '2025-05-31 22:41:29'),
                                                                                                                   (12, 'JAVA_REQUEST', '## 首字母大写\n#macro(capitalizeFirstLetter $input)\n    #if($input && $input.length() > 0)\n        #set($firstChar = $input.substring(0, 1).toUpperCase())\n        #set($rest = $input.substring(1))\n$firstChar$rest#end\n#end\n## 截取路径的最后一个单词并首字母大写\n#macro(getLastPartCapitalized $path)\n    #set($parts = $path.split(\"/\"))\n    #set($lastPart = $parts[$parts.size() - 1])\n    #set($capitalizedLastPart = $lastPart.substring(0, 1).toUpperCase() + $lastPart.substring(1))\n$capitalizedLastPart#end\n## 转换为java类\n#macro(getType $type $name)\n#if ($type == \"string\")String#elseif ($type == \"number\")BigDecimal#elseif ($type == \"array\")List<${name}>#elseif ($type == \"num_array\")List<BigDecimal>#elseif ($type == \"str_array\")List<String>#elseif ($type == \"enum\")String#elseif ($type == \"boolean\")Boolean\n#else${name}#end#end\n## 声明递归生成静态内部类\n#macro(generateStaticInnerClasses $requestParam)\n    /** 静态内部类${requestParam.name} */\n    public static class #capitalizeFirstLetter($requestParam.name) {\n    #foreach($childParam in ${requestParam.children})\n\n        /** ${childParam.description} */\n        #set($capitalizedName = $childParam.name.substring(0, 1).toUpperCase() + $childParam.name.substring(1))\n        private #getType(${childParam.type},$capitalizedName) ${childParam.name};\n    #end\n    #foreach($childParam in ${requestParam.children})\n        #set($capitalizedName = $childParam.name.substring(0, 1).toUpperCase() + $childParam.name.substring(1))\n        public #getType(${childParam.type},$capitalizedName) get$capitalizedName() {\n            return ${childParam.name};\n        }\n        public void set$capitalizedName(#getType(${childParam.type},$capitalizedName) ${childParam.name}) {\n            this.${childParam.name} = ${childParam.name};\n        }\n    #end\n    #foreach($childParam in ${requestParam.children})\n        #if(${childParam.children})\n        ## 递归生成静态内部类\n        #generateStaticInnerClasses($childParam)\n\n        #end\n    #end\n    }   \n#end\n\nimport java.util.*;\nimport java.math.*;\n\n/**\n * name : ${doc.name}请求类\n * author : ${doc.author}\n * description : ${doc.description}\n * version : ${doc.version}\n */\npublic class #getLastPartCapitalized($doc.url)RequestDTO {\n    #foreach($requestParam in ${doc.requestParams})\n\n        /** ${requestParam.description} */\n        #set($capitalizedName = $requestParam.name.substring(0, 1).toUpperCase() + $requestParam.name.substring(1))\n        private #getType(${requestParam.type},$capitalizedName) ${requestParam.name};\n    #end\n    #foreach($requestParam in ${doc.requestParams})\n\n        #set($capitalizedName = $requestParam.name.substring(0, 1).toUpperCase() + $requestParam.name.substring(1))\n        public #getType(${requestParam.type},$capitalizedName) get$capitalizedName() {\n            return ${requestParam.name};\n        }\n        public void set$capitalizedName(#getType(${requestParam.type},$capitalizedName) ${requestParam.name}) {\n            this.${requestParam.name} = ${requestParam.name};\n        }\n    #end\n    #foreach($requestParam in ${doc.requestParams})\n    #if(${requestParam.children})\n        ## 生成静态内部类\n        #generateStaticInnerClasses($requestParam)\n\n        #end\n    #end\n}\n', 0, 'JAVA', '2024-07-27 10:43:27', '2025-05-31 22:38:48'),
                                                                                                                   (13, 'JAVA_CONTROLLER', '## 截取路径部分\n#macro(splitPath $path)\n    #set($parts = $path.split(\"/\"))\n    #if($parts.size() > 0)\n        #set($pathPart = \"\")\n        #foreach($part in $parts)\n            #if($foreach.index < $parts.size() - 1)\n                #set($pathPart = $pathPart + $part + \"/\")\n            #end\n        #end\n$pathPart#end\n#end\n## 截取路径的最后一个单词\n#macro(getLastPart $path)\n    #set($parts = $path.split(\"/\"))\n    #set($lastPart = $parts[$parts.size() - 1])\n$lastPart#end\n## 首字母大写\n#macro(capitalizeFirstLetter $input)\n    #if($input && $input.length() > 0)\n        #set($firstChar = $input.substring(0, 1).toUpperCase())\n        #set($rest = $input.substring(1))\n$firstChar$rest#end\n#end\n## 截取路径的最后一个单词并首字母大写\n#macro(getLastPartCapitalized $path)\n    #set($parts = $path.split(\"/\"))\n    #set($lastPart = $parts[$parts.size() - 1])\n    #set($capitalizedLastPart = $lastPart.substring(0, 1).toUpperCase() + $lastPart.substring(1))\n$capitalizedLastPart#end\n#set($hasResponseParams = $doc.responseParams && $$doc.responseParams.size() > 0)\n#set($hasRequestParams = $doc.requestParams && $doc.requestParams.size() > 0)\n\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PostMapping;\nimport org.springframework.web.bind.annotation.RequestBody;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;\n\n@RestController\n@RequestMapping(\"#splitPath($doc.url)\")\npublic class #getLastPartCapitalized($doc.url)Controller {\n    /**\n     * name : ${doc.name}请求类\n     * author : ${doc.author}\n     * description : ${doc.description}\n     * version : ${doc.version}\n    */\n    @PostMapping(\"#getLastPart($doc.url)\")\n    public #if($hasResponseParams)#getLastPartCapitalized($doc.url)ReponseDTO#else void#end #getLastPart($doc.url)(#if($hasRequestParams)@RequestBody #getLastPartCapitalized($doc.url)RequestDTO param#else #end) {\n        #if($hasResponseParams)\n            // 返回响应参数\n            return null;\n        #else\n            // 无返回值\n        #end\n    }\n}\n', 0, 'JAVA', '2025-05-31 21:10:54', '2025-05-31 22:17:59');


INSERT INTO `module` (`id`, `name`, `project_id`, `type`, `import_url`, `basic_auth_username`, `basic_auth_password`, `token`, `create_mode`, `modify_mode`, `creator_id`, `modifier_id`, `order_index`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
                                                                                                                                                                                                                                                         (5, '商品中心', 5, 0, '', '', '', 'b414086531524fb0bc14f757346fec92', 0, 0, 16, 16, 0, 0, '2022-11-03 20:25:35', '2022-11-03 20:25:35'),
                                                                                                                                                                                                                                                         (6, '订单中心', 5, 0, '', '', '', 'bbe60f26676c4e92893170213ad05197', 0, 0, 16, 16, 0, 0, '2022-11-03 20:37:04', '2022-11-03 20:37:04'),
                                                                                                                                                                                                                                                         (7, '用户中心', 5, 0, '', '', '', '0e6cd661ea60487188d8cbbdcfe1228b', 0, 0, 16, 16, 0, 0, '2022-11-03 20:46:47', '2022-11-03 20:46:47');

INSERT INTO `module_environment` (`id`, `module_id`, `name`, `url`, `is_public`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
                                                                                                                                 (3, 5, '本地环境', 'http://127.0.0.1:8080', 0, 0, '2022-11-03 20:26:07', '2022-11-03 20:26:07'),
                                                                                                                                 (4, 6, '本地环境', 'http://127.0.0.1:8081', 0, 0, '2022-11-03 20:37:16', '2022-11-03 20:37:16'),
                                                                                                                                 (5, 7, '本地环境', 'http://127.0.0.1:8082', 0, 0, '2022-11-03 21:00:26', '2022-11-03 21:00:26');

INSERT INTO `project` (`id`, `name`, `description`, `space_id`, `is_private`, `creator_id`, `creator_name`, `modifier_id`, `modifier_name`, `order_index`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
    (5, '商城项目', '商城项目', 20, 1, 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 20:25:20', '2022-11-03 20:25:20');

INSERT INTO `project_release` (`id`, `project_id`, `release_no`, `release_desc`, `status`, `dingding_webhook`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
    (1, 5, '1', '111', 1, '', 0, '2024-08-10 17:32:21', '2024-08-10 17:32:43');

INSERT INTO `project_release_doc` (`id`, `project_id`, `release_id`, `module_id`, `source_id`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
                                                                                                                                               (1, 5, 1, 5, 7715, 1, '2024-08-10 17:32:21', '2024-08-10 17:32:42'),
                                                                                                                                               (2, 5, 1, 5, 7715, 0, '2024-08-10 17:32:43', '2024-08-10 17:32:43'),
                                                                                                                                               (3, 5, 1, 5, 7717, 0, '2024-08-10 17:32:43', '2024-08-10 17:32:43');

INSERT INTO `project_user` (`id`, `project_id`, `user_id`, `role_code`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
    (11, 5, 16, 'admin', 0, '2022-11-03 20:25:20', '2022-11-03 20:25:20');

INSERT INTO `space` (`id`, `name`, `creator_id`, `creator_name`, `modifier_id`, `modifier_name`, `is_compose`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
    (20, '研发中心', 16, '超级管理员', 16, '超级管理员', 0, 0, '2022-11-03 20:24:59', '2022-11-03 20:24:59');

INSERT INTO `space_user` (`id`, `user_id`, `space_id`, `role_code`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
    (26, 16, 20, 'admin', 0, '2022-11-03 20:24:59', '2022-11-03 20:24:59');

INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `remark`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
                                                                                                                           (3, 'torna.version', '12900', '当前内部版本号。不要删除这条记录！！', 0, '2022-11-03 20:20:53', '2024-08-10 20:08:49'),
                                                                                                                           (4, 'front.param.type-array', '[\"string\",\"number\",\"boolean\",\"object\",\"array\",\"num_array\",\"str_array\",\"file\",\"file[]\",\"enum\"]', '参数类型配置', 0, '2023-11-20 09:46:39', '2023-11-20 09:46:39');


select 'finished';
