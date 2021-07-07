-- 适用于MySQL5.6.5之前的版本

CREATE DATABASE IF NOT EXISTS `torna` DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
USE `torna`;





DROP TABLE IF EXISTS `user_subscribe`;
DROP TABLE IF EXISTS `user_message`;
DROP TABLE IF EXISTS `user_info`;
DROP TABLE IF EXISTS `user_dingtalk_info`;
DROP TABLE IF EXISTS `system_config`;
DROP TABLE IF EXISTS `space_user`;
DROP TABLE IF EXISTS `space`;
DROP TABLE IF EXISTS `share_content`;
DROP TABLE IF EXISTS `share_config`;
DROP TABLE IF EXISTS `prop`;
DROP TABLE IF EXISTS `project_user`;
DROP TABLE IF EXISTS `project`;
DROP TABLE IF EXISTS `open_user`;
DROP TABLE IF EXISTS `module_config`;
DROP TABLE IF EXISTS `module`;
DROP TABLE IF EXISTS `mock_config`;
DROP TABLE IF EXISTS `enum_item`;
DROP TABLE IF EXISTS `enum_info`;
DROP TABLE IF EXISTS `doc_param`;
DROP TABLE IF EXISTS `doc_info`;
DROP TABLE IF EXISTS `compose_project`;
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
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_projectid` (`project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='文档引用';


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
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1：有效，0：无效',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_spaceid` (`space_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='组合项目表';


CREATE TABLE `doc_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT '唯一id，接口规则：md5(module_id:parent_id:url:http_method)。分类规则：md5(module_id:parent_id:name)',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '文档名称',
  `description` text NOT NULL COMMENT '文档描述',
  `author` varchar(64) NOT NULL DEFAULT '' COMMENT '维护人',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:http,1:dubbo',
  `url` varchar(128) NOT NULL DEFAULT '' COMMENT '访问URL',
  `http_method` varchar(12) NOT NULL DEFAULT '' COMMENT 'http方法',
  `content_type` varchar(128) NOT NULL DEFAULT '' COMMENT 'contentType',
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
  `remark` varchar(128) NOT NULL DEFAULT '' COMMENT '备注',
  `is_show` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否显示',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_data_id` (`data_id`) USING BTREE,
  KEY `idx_moduleid` (`module_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 COMMENT='文档信息';


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
  `style` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0：path, 1：header， 2：请求参数，3：返回参数，4：错误码',
  `create_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '新增操作方式，0：人工操作，1：开放平台推送',
  `modify_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '修改操作方式，0：人工操作，1：开放平台推送',
  `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `creator_name` varchar(64) NOT NULL DEFAULT '',
  `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `modifier_name` varchar(64) NOT NULL DEFAULT '',
  `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dataid` (`data_id`) USING BTREE,
  KEY `idx_docid` (`doc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8 COMMENT='文档参数';


CREATE TABLE `enum_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT '唯一id，md5(module_id:name)',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '枚举名称',
  `description` varchar(128) NOT NULL DEFAULT '' COMMENT '枚举说明',
  `module_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'module.id',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_moduleid` (`module_id`) USING BTREE,
  KEY `idx_dataid` (`data_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='枚举信息';


CREATE TABLE `enum_item` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `enum_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'enum_info.id',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '名称，字面值',
  `type` varchar(64) NOT NULL DEFAULT '' COMMENT '类型',
  `value` varchar(64) NOT NULL DEFAULT '' COMMENT '枚举值',
  `description` varchar(128) NOT NULL DEFAULT '' COMMENT '枚举描述',
  `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_enumid` (`enum_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='枚举详情';


CREATE TABLE `mock_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '名称',
  `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT 'md5(path+query+body)',
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
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_docid` (`doc_id`) USING BTREE,
  KEY `idx_dataid` (`data_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='mock配置';


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
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_token` (`token`) USING BTREE,
  KEY `idx_projectid` (`project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='项目模块';


CREATE TABLE `module_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `module_id` bigint(11) unsigned NOT NULL DEFAULT '0',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '配置类型，1：全局header',
  `config_key` varchar(128) NOT NULL DEFAULT '' COMMENT '配置key',
  `config_value` varchar(128) NOT NULL DEFAULT '' COMMENT '配置值',
  `extend_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `description` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_moduleid_type` (`module_id`,`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='系统配置';


CREATE TABLE `open_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `app_key` varchar(100) NOT NULL COMMENT 'appKey',
  `secret` varchar(200) DEFAULT NULL COMMENT 'secret',
  `status` tinyint(4) unsigned NOT NULL DEFAULT '1' COMMENT '1启用，0禁用',
  `applicant` varchar(64) NOT NULL DEFAULT '',
  `space_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'space.id',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_key` (`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='开放用户';


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
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `uk_name` (`creator_id`,`name`) USING BTREE,
  KEY `idx_spaceid` (`space_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='项目表';


CREATE TABLE `project_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'project.id',
  `user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
  `role_code` varchar(64) NOT NULL DEFAULT '0' COMMENT '角色，guest：访客，dev：开发者，admin：项目管理员',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_projectid_userid` (`project_id`,`user_id`) USING BTREE,
  KEY `idx_userid` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='项目用户关系表';


CREATE TABLE `prop` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ref_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '关联id',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '类型，0：doc_info属性',
  `name` varchar(64) NOT NULL DEFAULT '',
  `val` text NOT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_docid_name` (`ref_id`,`type`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='属性表';


CREATE TABLE `share_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '分享形式，1：公开，2：加密',
  `password` varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态，1：有效，0：无效',
  `module_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'module.id',
  `is_all` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为全部文档',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `remark` varchar(128) NOT NULL DEFAULT '' COMMENT '备注',
  `creator_name` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_moduleid` (`module_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分享配置表';


CREATE TABLE `share_content` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `share_config_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'share_config.id',
  `doc_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '文档id',
  `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '父id',
  `is_share_folder` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否分享整个分类',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_shareconfigid_docid` (`share_config_id`,`doc_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分享详情';


CREATE TABLE `space` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '空间名称',
  `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建者userid',
  `creator_name` varchar(64) NOT NULL DEFAULT '',
  `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建者userid',
  `modifier_name` varchar(64) NOT NULL DEFAULT '',
  `is_compose` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否组合空间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COMMENT='分组表';


CREATE TABLE `space_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
  `space_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'space.id',
  `role_code` varchar(64) NOT NULL DEFAULT '' COMMENT '角色，guest：访客，dev：开发者，admin：空间管理员',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_groupid_userid` (`space_id`,`user_id`) USING BTREE,
  KEY `idx_userid` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COMMENT='分组用户关系表';


CREATE TABLE `system_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `config_key` varchar(64) NOT NULL DEFAULT '',
  `config_value` varchar(256) NOT NULL DEFAULT '',
  `remark` varchar(128) NOT NULL DEFAULT '',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configkey` (`config_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='系统配置表';


CREATE TABLE `user_dingtalk_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nick` varchar(64) NOT NULL DEFAULT '' COMMENT '用户在钉钉上面的昵称',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '员工名称。',
  `email` varchar(128) NOT NULL DEFAULT '' COMMENT '员工邮箱。',
  `userid` varchar(128) NOT NULL DEFAULT '' COMMENT '员工的userid。',
  `unionid` varchar(128) NOT NULL DEFAULT '' COMMENT '用户在当前开放应用所属企业的唯一标识。',
  `openid` varchar(128) NOT NULL DEFAULT '' COMMENT '用户在当前开放应用内的唯一标识。',
  `user_info_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_unionid` (`unionid`) USING BTREE,
  KEY `idx_openid` (`openid`) USING BTREE,
  KEY `idx_userid` (`user_info_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='钉钉开放平台用户';


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
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='用户表';


CREATE TABLE `user_message` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
  `message` varchar(256) NOT NULL DEFAULT '',
  `is_read` tinyint(4) NOT NULL DEFAULT '0',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '同user_subscribe.type',
  `source_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '同user_subscribe.source_id',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_userid` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='站内消息';


CREATE TABLE `user_subscribe` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '类型，1：订阅接口，2：订阅项目',
  `source_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '关联id，当type=1对应doc_info.id；type=2对应project.id',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_userid_type_sourceid` (`user_id`,`type`,`source_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='用户订阅表';




INSERT INTO `compose_doc` (`id`, `doc_id`, `project_id`, `is_folder`, `folder_name`, `parent_id`, `origin`, `is_deleted`, `creator`, `order_index`, `gmt_create`, `gmt_modified`) VALUES
	(1,0,1,1,'订单模块',0,'',0,'超级管理员',0,'2021-05-25 18:05:23','2021-05-25 18:05:23'),
	(2,0,1,1,'产品模块',0,'',0,'超级管理员',0,'2021-05-25 18:05:30','2021-05-25 18:05:30'),
	(3,2,1,0,'',1,'研发一部/商城项目/故事API',0,'超级管理员',0,'2021-05-25 18:05:46','2021-05-25 18:05:46'),
	(4,3,1,0,'',1,'研发一部/商城项目/故事API',0,'超级管理员',0,'2021-05-25 18:05:46','2021-05-25 18:05:46'),
	(5,10,1,0,'',2,'研发一部/商城项目/故事API',0,'超级管理员',0,'2021-05-25 18:05:53','2021-05-25 18:05:53'),
	(6,11,1,0,'',2,'研发一部/商城项目/故事API',0,'超级管理员',0,'2021-05-25 18:05:53','2021-05-25 18:05:53');


INSERT INTO `compose_project` (`id`, `name`, `description`, `space_id`, `type`, `password`, `creator_id`, `creator_name`, `modifier_id`, `modifier_name`, `order_index`, `status`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(1,'聚合接口','提供给第三方的接口',18,1,'',1,'超级管理员',1,'超级管理员',0,1,0,'2021-05-25 18:05:14','2021-05-25 18:05:14');


INSERT INTO `doc_info` (`id`, `data_id`, `name`, `description`, `author`, `type`, `url`, `http_method`, `content_type`, `is_folder`, `parent_id`, `module_id`, `is_use_global_headers`, `is_use_global_params`, `is_use_global_returns`, `is_request_array`, `is_response_array`, `request_array_type`, `response_array_type`, `create_mode`, `modify_mode`, `creator_id`, `creator_name`, `modifier_id`, `modifier_name`, `order_index`, `remark`, `is_show`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(1,'5fa7cd78bc872cd8fdc09ee3d6afedd2','故事接口','','',0,'','','',1,0,1,1,1,1,0,0,'object','object',0,0,3,'',3,'',0,'',1,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(2,'3292c28718c1e74d6e14e0160eecf379','获取分类信息','','',0,'/story/category/get/v1','POST','application/json',0,1,1,1,1,1,0,0,'object','object',0,0,3,'',1,'超级管理员',0,'',1,0,'2020-12-15 10:01:48','2021-01-19 17:03:29'),
	(3,'48ae195650ab5cd4d521b62704d15b57','忽略签名验证','','',0,'/story/get/ignore/v1','POST','application/json',0,1,1,1,1,1,0,0,'object','object',0,0,3,'',3,'',0,'',1,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(4,'d623ea4fe35e903a42530643e6087795','获取故事信息（首位）','','',0,'/story/get/v1','POST','application/json',0,1,1,1,1,1,0,0,'object','object',0,0,3,'',3,'',0,'',1,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(5,'1319b1ba77712fc4c6668d04b05fd68a','获取故事信息','','',0,'/story/get/v2','POST','application/json',0,1,1,1,1,1,0,0,'object','object',0,0,3,'',3,'',0,'',1,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(6,'3a15e3407182ccf902b6e9f5c1b1b875','返回数组结果（第二）','','',0,'/story/list/v1','POST','application/json',0,1,1,1,1,1,0,0,'object','object',0,0,3,'',3,'',0,'',1,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(7,'fa3324c4dd6b978434f8541f73393118','树状返回','','',0,'/story/tree/v1','POST','application/json',0,1,1,1,1,1,0,0,'object','object',0,0,3,'',3,'',0,'',1,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(8,'ac0c66314833ef010bf7df1cf5692b43','传递token','','',0,'/token','POST','application/json',0,1,1,1,1,1,0,0,'object','object',0,0,3,'',1,'',0,'',1,0,'2020-12-15 10:01:48','2021-01-19 17:03:30'),
	(9,'7ededf48dec7c2b582affe2d08d7d0b2','文件上传','','',0,'','','',1,0,1,1,1,1,0,0,'object','object',0,0,3,'',3,'',0,'',1,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(10,'84d41cbee05d97aa568b7a06759cc19c','文件上传例1','','',0,'/upload/file1','POST','multipart/form-data',0,9,1,1,1,1,0,0,'object','object',0,0,3,'',3,'',0,'',1,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(11,'1172c623f531324750aad5c2fa72a751','文件上传例2','','',0,'/upload/file2','POST','multipart/form-data',0,9,1,1,1,1,0,0,'object','object',0,0,3,'',3,'',0,'',1,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(12,'92d2fa5b0ae7a0f2e29648929c5e435f','文件下载','','',0,'','','',1,0,1,1,1,1,0,0,'object','object',0,0,3,'',3,'',0,'',1,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(13,'f184470a11e0fd559e1f56294b53d4cb','文件下载','','',0,'/download/file1','POST','application/json',0,12,1,1,1,1,0,0,'object','object',0,0,3,'',3,'',0,'',1,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(14,'3f3aab998c9785fe13d41e02c95f7ef4','食物接口','','',0,'','','',1,0,1,1,1,1,0,0,'object','object',0,0,3,'',3,'',0,'',1,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(15,'4942c1bd41bbf75d995722986f70a9f4','获取食物','','',0,'/food/getFoodById','GET','',0,14,1,1,1,1,0,0,'object','object',0,0,3,'',3,'',0,'',1,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(16,'940818d032209bc3628895d483463ce1','获取用户接口','获取用户接口','',0,'get/group/{groupId}/id/{id}','GET','',0,0,2,1,1,1,0,0,'object','object',0,0,12,'李四',1,'超级管理员',0,'ccc',1,0,'2020-12-15 10:15:55','2021-01-18 15:05:16'),
	(17,'2df0b4e87fc3a8c37d08e3d7a95d0cf4','comment','','',0,'phoenix/web/v1/comment/list/101426186','GET','',0,0,1,1,1,1,0,0,'object','object',0,0,2,'研发一部经理',1,'研发一部经理',0,'',1,1,'2021-01-19 17:29:23','2021-01-22 11:52:21');


INSERT INTO `doc_param` (`id`, `data_id`, `name`, `type`, `required`, `max_length`, `example`, `description`, `enum_id`, `doc_id`, `parent_id`, `style`, `create_mode`, `modify_mode`, `creator_id`, `creator_name`, `modifier_id`, `modifier_name`, `order_index`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(1,'9cedd1b23736a816e96bca9603c2b5f4','categoryName','string',0,'-','娱乐','分类名称',0,2,0,2,0,0,3,'商城项目admin',1,'超级管理员',0,0,'2020-12-15 10:01:48','2021-01-19 16:39:35'),
	(2,'df34a965ab9c44e2e6b0b282c711e9b9','createTime','string',0,'-','2019-09-25 17:12:52','创建时间',0,2,0,2,0,0,3,'商城项目admin',1,'超级管理员',0,0,'2020-12-15 10:01:48','2021-01-19 16:39:35'),
	(3,'841d66d19b087e9982b76ba0a0aab7a4','storyParam','object',0,'-','','',0,2,0,2,0,0,3,'商城项目admin',1,'超级管理员',0,0,'2020-12-15 10:01:48','2021-01-19 16:39:35'),
	(4,'ae90af481f84971aff6d1457b11609d5','id','integer',0,'-','111','故事ID',0,2,3,2,0,0,3,'商城项目admin',1,'超级管理员',0,0,'2020-12-15 10:01:48','2021-01-19 16:39:35'),
	(5,'b4bd22cc240b23325eb3bfeadab642e3','remark','string',0,'64','xx','备注 (第二)',0,2,3,2,0,0,3,'商城项目admin',1,'超级管理员',0,0,'2020-12-15 10:01:48','2021-01-19 16:39:35'),
	(6,'5d13d5536d163c8a165f613b7d678f76','name','string',1,'20','白雪公主','故事名称',0,2,3,2,0,0,3,'商城项目admin',1,'超级管理员',0,0,'2020-12-15 10:01:48','2021-01-19 16:39:35'),
	(7,'f3a0327795da96eb4955ea525b64cb20','categoryName','string',0,'-','娱乐','分类名称',0,2,0,3,0,0,3,'商城项目admin',1,'超级管理员',0,0,'2020-12-15 10:01:48','2021-01-19 16:39:35'),
	(8,'b63b34c6077d10ca10d3a5d6a5001828','storyResult','object',0,'-','','分类故事',0,2,0,3,0,0,3,'商城项目admin',1,'超级管理员',0,0,'2020-12-15 10:01:48','2021-01-19 16:39:35'),
	(9,'a8ccc97411881fa1be3086800d199ecc','gmt_create','string',0,'-','2019-04-14 19:02:12','创建时间',0,2,8,3,0,0,3,'商城项目admin',1,'超级管理员',0,0,'2020-12-15 10:01:48','2021-01-19 16:39:35'),
	(10,'bedf8a65ba8809d6603e013e57b2832c','id','integer',0,'-','1','故事ID',0,2,8,3,0,0,3,'商城项目admin',1,'超级管理员',0,0,'2020-12-15 10:01:48','2021-01-19 16:39:35'),
	(11,'b48f0fb65540b52e13ce16b515f758cc','name','string',0,'-','海底小纵队','故事名称',0,2,8,3,0,0,3,'商城项目admin',1,'超级管理员',0,0,'2020-12-15 10:01:48','2021-01-19 16:39:35'),
	(12,'2e5aa28eade28333f5564a9abb759e83','id','integer',0,'-','111','故事ID',0,3,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(13,'6248cfafe3b1491846475d5f8e20e248','remark','string',0,'-','xx','备注 (第二)',0,3,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(14,'6c3c6428f5853b3b0fb6639552519dd6','name','string',0,'-','白雪公主','故事名称',0,3,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(15,'eda2c2531888b200aceb76b5f6596b6a','gmt_create','string',0,'-','2019-04-14 19:02:12','创建时间',0,3,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(16,'30dd8d1e23a460172d35a6a65a30262c','id','integer',0,'-','1','故事ID',0,3,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(17,'593b2b0cb3eb79276b02dcf8ab56eb63','name','string',0,'-','海底小纵队','故事名称',0,3,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(18,'d28b974ec3fe2bad4b934c696c4d96c8','id','integer',0,'-','111','故事ID',0,4,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(19,'cb37ba05364c6a2e0acf059f5c9d3533','remark','string',0,'64','xx','备注 (第二)',0,4,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(20,'014141306e4e9d2497b3f261152f585a','name','string',1,'20','白雪公主','故事名称',0,4,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(21,'40172ca4277e0c52a5a662e879109161','gmt_create','string',0,'-','2019-04-14 19:02:12','创建时间',0,4,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(22,'d3f4a37bc011a373bbf6bcfcc8e4608f','id','integer',0,'-','1','故事ID',0,4,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(23,'c508f91b8ff7aede0f7ac39fbb8dd2fb','name','string',0,'-','海底小纵队','故事名称',0,4,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(24,'cac09a927ec388edfc19dd1039641d67','id','integer',0,'-','111','故事ID',0,5,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(25,'339101d3efffe3160dac1ecae7871c4b','remark','string',0,'64','xx','备注 (第二)',0,5,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(26,'88fc4120e465887ab41b9ca91da87bb3','name','string',1,'20','白雪公主','故事名称',0,5,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(27,'9e65ce76eb39ad0f82095929482e7693','gmt_create','string',0,'-','2019-04-14 19:02:12','创建时间',0,5,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(28,'da337cdcf39f1ce880850a3189cf660b','id','integer',0,'-','1','故事ID',0,5,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(29,'be0c8d746c7e16c0adfcf0104676a9ff','name','string',0,'-','海底小纵队','故事名称',0,5,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(30,'46daac3b859ef0d48650f1162311c6a0','id','integer',0,'-','111','故事ID',0,6,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(31,'6a08c7298b39910d2a1c1a0ba9fc1f1d','remark','string',0,'64','xx','备注 (第二)',0,6,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(32,'4301980bc8dc1d8e14fadfb31a9ec85a','name','string',1,'20','白雪公主','故事名称',0,6,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(33,'4561245e7c6e270233ed6595f6e818b5','StoryResult','array',0,'-','','',0,6,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(34,'8c181ef7670cdfd45d80d6698200b7b8','gmt_create','string',0,'-','2019-04-14 19:02:12','创建时间',0,6,33,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(35,'b7c676a3e2783b2709b7ec4accf375b6','id','integer',0,'-','1','故事ID',0,6,33,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(36,'051b23a59ff6a74ad79122fc2230422e','name','string',0,'-','海底小纵队','故事名称',0,6,33,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(37,'5533aa3704c7b4518471f5a326f47551','id','integer',0,'-','111','故事ID',0,7,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(38,'f0f92b425a93640fb206bfb0902b1366','remark','string',0,'64','xx','备注 (第二)',0,7,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(39,'112d37f2c76b5b0251949cac69d68fe1','name','string',1,'20','白雪公主','故事名称',0,7,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(40,'dd4a92a31102d4220ffb7cca2b1714e7','children','array',0,'-','','子节点',0,7,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(41,'61c83cc34f795b1bcd8a92dd45a5d2e7','children','object',0,'-','','子节点',0,7,40,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(42,'4f54764326fd4743e55850149891308c','id','integer',0,'-','','id',0,7,40,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(43,'cbe98e824b7a32fd86cd9dd160292d06','name','string',0,'-','','名称',0,7,40,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(44,'bcda07eff74d7fe716899745136d9253','pid','integer',0,'-','','父id',0,7,40,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(45,'a4f91fa41249fe004bbd76c972257b85','id','integer',0,'-','','id',0,7,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(46,'41849928604437f4478c6a8d163b0442','name','string',0,'-','','名称',0,7,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(47,'0b3b92569b57c5a027cefacd4c932a26','pid','integer',0,'-','','父id',0,7,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(48,'20ff1b4db324d1b09cf59c690c1aae03','id','integer',0,'-','111','故事ID',0,8,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(49,'c6456cfdda2dc41104a27618852b3f1f','remark','string',0,'64','xx','备注 (第二)',0,8,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(50,'54722d47865c573c3671527e22797319','name','string',1,'20','白雪公主','故事名称',0,8,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(51,'0d757e958fc3fa7665a8b8eabc212303','gmt_create','string',0,'-','2019-04-14 19:02:12','创建时间',0,8,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(52,'a4fa420275434607ba73f33e2152422f','id','integer',0,'-','1','故事ID',0,8,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(53,'b8d81b117a75d84d6ff87d569c366ae6','name','string',0,'-','海底小纵队','故事名称',0,8,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(54,'f5c01b74aee2f850e7d7e06833241acc','file1','file',1,'-','','文件1',0,10,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(55,'e98f7c9c934e4867f563e1b9b01b5cba','file2','file',1,'-','','文件2',0,10,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(56,'f30ba8fb274890cc3f85a03f6e00d921','remark','string',0,'-','','',0,10,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(57,'5c8ba24461e3947251047e5d06d1fa1a','files','array',0,'-','','',0,10,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(58,'be3e412c33827b677b953bea02711246','content','string',0,'-','啊啊啊','文件内容',0,10,57,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(59,'8cd5101036784bf60ccb9a2faf9f577d','filename','string',0,'-','1.txt','文件名称',0,10,57,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(60,'a9db899e347349533d828f64f822b020','size','integer',0,'-','109','文件大小',0,10,57,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(61,'476448d9914541d3a39280510335d9d3','remark','string',0,'-','','',0,10,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(62,'ccc42e5b6ca0526300e867bbb1d2ba08','remark','string',0,'-','','',0,11,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(63,'5a1e26b6c5784250a14780863207ea2c','files','array',0,'-','','',0,11,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(64,'7580d08580a198b1cb507078767fe924','content','string',0,'-','啊啊啊','文件内容',0,11,63,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(65,'6989d6adeeb1f8d7bc48d82d94992503','filename','string',0,'-','1.txt','文件名称',0,11,63,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(66,'ce9866be95c15c861a6ccf518249d123','size','integer',0,'-','109','文件大小',0,11,63,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(67,'eb777cbc1343e3eea07b3d4da8b35dae','remark','string',0,'-','','',0,11,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(68,'f7c99023c97bb8ff02e23044e1d4dea5','id','integer',0,'-','111','故事ID',0,13,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(69,'9d1fc88492273e49154e2e9b53f3668c','remark','string',0,'64','xx','备注 (第二)',0,13,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(70,'edebdefbc34e83211c4b177104a6298b','name','string',1,'20','白雪公主','故事名称',0,13,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(71,'b6e6da845523c535f5517f7c33bb6731','id','integer',0,'-','','id',0,15,0,2,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(72,'555b29db1207e5377f3b1ee342f4a392','id','integer',0,'-','','',0,15,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(73,'bdfdcfe4e5ee99561bb064aebfbf6751','name','string',0,'-','','',0,15,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(74,'60ba1291613b945087ae8cfe6bff1bbc','price','number',0,'-','','',0,15,0,3,0,0,3,'商城项目admin',3,'商城项目admin',0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(75,'f788250eb074c6af22cf7755ad313431','id','number',1,'64','123','用户id',0,16,0,2,0,0,12,'李四',1,'超级管理员',0,0,'2020-12-15 10:15:55','2021-01-18 15:05:16'),
	(76,'fb0d734de29a47397d24d3f9ed2b6bf0','id','number',1,'64','123','用户id',0,16,0,3,0,0,12,'李四',1,'超级管理员',0,0,'2020-12-15 10:15:55','2021-01-18 15:05:16'),
	(77,'e40f2386a330283bfe9a4c8f06882224','name','string',1,'64','jim','用户名称',0,16,0,3,0,0,12,'李四',1,'超级管理员',0,0,'2020-12-15 10:15:55','2021-01-18 15:05:16'),
	(78,'6e3e6a9a659dbb452e1c35a87d27cafc','groupId','string',1,'64','1','分组id',0,16,0,0,0,0,11,'后台项目负责人',1,'超级管理员',0,0,'2020-12-16 15:35:09','2021-01-18 15:05:16'),
	(79,'0e799f616e11beb007d4d8e9cf919a2f','id','number',1,'64','1','用户id',0,16,0,0,0,0,11,'后台项目负责人',1,'超级管理员',0,0,'2020-12-16 15:35:09','2021-01-18 15:05:16'),
	(80,'9584e01340755ba6f8c82df9eb032d89','token','string',1,'64','1111','token',0,2,0,1,0,0,1,'超级管理员',1,'超级管理员',0,0,'2021-01-19 10:32:09','2021-01-19 16:39:35'),
	(81,'e50bb76340fd01b339e1a2aa8d3215f3','page','string',1,'64','1','',0,17,0,2,0,0,2,'研发一部经理',2,'研发一部经理',0,0,'2021-01-19 17:29:23','2021-01-19 17:29:23'),
	(82,'227be5c273852f1927611268522efa00','size','string',1,'64','10','',0,17,0,2,0,0,2,'研发一部经理',2,'研发一部经理',0,0,'2021-01-19 17:29:23','2021-01-19 17:29:23'),
	(83,'14c5f6044d9ce6850dc4e1951d0151f2','commentId','string',1,'64','','',0,17,0,2,0,0,2,'研发一部经理',2,'研发一部经理',0,0,'2021-01-19 17:29:23','2021-01-19 17:29:23');


INSERT INTO `enum_info` (`id`, `data_id`, `name`, `description`, `module_id`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(1,'0b28cfdbc7f250d85614eacda8248eb4','性别','',2,0,'2020-12-16 13:57:09','2020-12-16 13:57:09');






INSERT INTO `module` (`id`, `name`, `project_id`, `type`, `import_url`, `basic_auth_username`, `basic_auth_password`, `token`, `create_mode`, `modify_mode`, `creator_id`, `modifier_id`, `order_index`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(1,'故事API',1,1,'http://localhost:2222/v2/api-docs','','','c16931fa6590483fb7a4e85340fcbfef',0,0,3,3,0,0,'2020-12-15 10:01:48','2020-12-15 10:01:48'),
	(2,'用户模块',2,0,'','','','b215b7010db5451e98305152a5fa2ddf',0,0,12,12,0,0,'2020-12-15 10:14:55','2020-12-15 10:14:55'),
	(3,'swagger-push',1,0,'','','','931167d9347e4aec9409f2b275437431',0,0,1,1,0,0,'2021-06-02 10:26:27','2021-06-02 10:26:27');


INSERT INTO `module_config` (`id`, `module_id`, `type`, `config_key`, `config_value`, `extend_id`, `description`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(1,1,0,'debughost_1','http://10.1.30.165:2222',0,'',0,'2020-12-15 10:01:48','2021-01-22 11:51:05'),
	(2,2,0,'debughost_2','http://www.aaa.com',0,'',0,'2020-12-16 15:41:14','2020-12-16 15:41:14');


INSERT INTO `open_user` (`id`, `app_key`, `secret`, `status`, `applicant`, `space_id`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(1,'20201215788408948171472896','WeNAdSucgduD$M?*83?dJR&IvgU~Re75',1,'张三',0,0,'2020-12-15 14:07:21','2021-01-25 09:59:10'),
	(2,'20201216788835306945118208','W.ZyGMOB9Q0UqujVxnfi@.I#V&tUUYZR',1,'张三',9,0,'2020-12-16 18:21:33','2021-01-25 09:59:10'),
	(3,'20201216788835536872669184','Bq.XRN!S0$t8!UYpWgSOl7oHlY#XeenJ',1,'张三',9,0,'2020-12-16 18:22:28','2021-01-25 09:59:11');


INSERT INTO `project` (`id`, `name`, `description`, `space_id`, `is_private`, `creator_id`, `creator_name`, `modifier_id`, `modifier_name`, `order_index`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(1,'商城项目','商城项目',9,0,3,'商城项目admin',1,'超级管理员',0,0,'2020-12-15 09:56:39','2021-01-25 08:31:33'),
	(2,'后台项目','后台项目',11,1,11,'后台项目负责人',11,'后台项目负责人',0,0,'2020-12-15 10:12:37','2020-12-15 10:12:37'),
	(3,'测试项目','这是一个测试项目',17,1,14,'测试A',14,'测试A',0,0,'2021-01-25 09:10:47','2021-01-25 09:10:47');


INSERT INTO `project_user` (`id`, `project_id`, `user_id`, `role_code`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(1,1,3,'admin',0,'2020-12-15 09:56:38','2021-01-25 08:31:33'),
	(2,1,4,'dev',0,'2020-12-15 09:58:35','2021-01-25 08:31:38'),
	(3,1,9,'guest',1,'2020-12-15 09:58:44','2020-12-15 09:59:01'),
	(4,2,11,'admin',0,'2020-12-15 10:12:37','2020-12-15 10:12:37'),
	(5,2,12,'dev',0,'2020-12-15 10:12:52','2020-12-15 10:12:52'),
	(6,2,13,'guest',0,'2020-12-15 11:35:21','2020-12-15 11:35:21'),
	(9,3,14,'admin',0,'2021-01-25 09:10:47','2021-01-25 09:10:47');








INSERT INTO `space` (`id`, `name`, `creator_id`, `creator_name`, `modifier_id`, `modifier_name`, `is_compose`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(9,'研发一部',2,'研发一部经理',2,'研发一部经理',0,0,'2020-12-15 09:48:13','2020-12-15 09:48:13'),
	(11,'研发二部',5,'研发二部经理',5,'研发二部经理',0,0,'2020-12-15 10:08:39','2020-12-15 10:08:39'),
	(14,'研发三部',11,'后台项目负责人',11,'后台项目负责人',0,1,'2020-12-16 10:04:13','2020-12-16 10:04:13'),
	(16,'研发三部',11,'后台项目负责人',11,'后台项目负责人',0,1,'2020-12-16 10:06:20','2020-12-16 10:06:20'),
	(17,'测试空间',14,'测试A',14,'测试A',0,0,'2021-01-25 09:10:02','2021-01-25 09:10:02'),
	(18,'聚合空间',1,'超级管理员',1,'超级管理员',1,0,'2021-05-25 18:04:34','2021-05-25 18:04:34');


INSERT INTO `space_user` (`id`, `user_id`, `space_id`, `role_code`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(1,2,1,'admin',0,'2020-12-15 09:41:16','2020-12-15 09:41:16'),
	(2,3,2,'admin',0,'2020-12-15 09:41:58','2020-12-15 09:41:58'),
	(3,4,3,'admin',0,'2020-12-15 09:42:12','2020-12-15 09:42:12'),
	(4,5,4,'admin',0,'2020-12-15 09:42:29','2020-12-15 09:42:29'),
	(5,6,5,'admin',0,'2020-12-15 09:42:47','2020-12-15 09:42:47'),
	(6,7,6,'admin',0,'2020-12-15 09:43:00','2020-12-15 09:43:00'),
	(7,8,7,'admin',0,'2020-12-15 09:43:18','2020-12-15 09:43:18'),
	(8,9,8,'admin',0,'2020-12-15 09:43:32','2020-12-15 09:43:32'),
	(9,2,9,'admin',0,'2020-12-15 09:48:12','2020-12-15 09:48:12'),
	(10,3,9,'dev',0,'2020-12-15 09:49:18','2020-12-15 09:49:18'),
	(11,4,9,'dev',0,'2020-12-15 09:49:18','2020-12-15 09:49:18'),
	(12,10,10,'admin',0,'2020-12-15 09:50:00','2020-12-15 09:50:00'),
	(13,9,9,'guest',0,'2020-12-15 09:50:28','2020-12-15 09:50:28'),
	(14,5,11,'admin',0,'2020-12-15 10:08:38','2020-12-15 10:08:38'),
	(15,11,12,'admin',0,'2020-12-15 10:10:17','2020-12-15 10:10:17'),
	(16,12,13,'admin',0,'2020-12-15 10:10:31','2020-12-15 10:10:31'),
	(17,11,11,'dev',0,'2020-12-15 10:12:00','2020-12-15 10:12:00'),
	(18,12,11,'dev',0,'2020-12-15 10:12:00','2020-12-15 10:12:00'),
	(19,10,11,'guest',0,'2020-12-15 10:12:08','2020-12-15 10:12:08'),
	(20,13,11,'guest',0,'2020-12-15 11:34:59','2020-12-15 11:34:59'),
	(21,11,14,'admin',0,'2020-12-16 10:04:13','2020-12-16 10:04:13'),
	(22,11,16,'admin',0,'2020-12-16 10:06:19','2020-12-16 10:06:19'),
	(23,14,17,'admin',0,'2021-01-25 09:10:01','2021-01-25 09:10:01'),
	(24,2,18,'admin',0,'2021-05-25 18:04:33','2021-05-25 18:04:33');


INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `remark`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(1,'torna.version','11','当前内部版本号。不要删除这条记录！！',0,'2021-05-25 18:03:08','2021-05-25 18:03:08');




INSERT INTO `user_info` (`id`, `username`, `password`, `nickname`, `is_super_admin`, `source`, `email`, `status`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(1,'admin@torna.cn','f1e27f8ec06b0ea415583c26457dd111','超级管理员',1,'register','admin@torna.cn',1,0,'2020-12-15 09:04:13','2021-05-25 18:03:07'),
	(2,'dev1admin@torna.cn','1231d3ef9f1a6b3771d19e3ae453b07d','研发一部经理',0,'register','dev1admin@torna.cn',1,0,'2020-12-15 09:41:16','2021-05-25 18:03:07'),
	(3,'dev1shop_admin@torna.cn','8429b4b5d6ef210811a25ad6e2e47403','商城项目admin',0,'register','dev1shop_admin@torna.cn',1,0,'2020-12-15 09:41:58','2021-05-25 18:03:07'),
	(4,'dev1shop_zhangsan@torna.cn','6903a801b91e1a81247a97bf7cf6b7ee','张三',0,'register','dev1shop_zhangsan@torna.cn',1,0,'2020-12-15 09:42:12','2021-05-25 18:03:07'),
	(5,'dev2admin@torna.cn','8a73c745cd35093af0c72b5e97c7f908','研发二部经理',0,'register','dev2admin@torna.cn',1,0,'2020-12-15 09:42:30','2021-05-25 18:03:07'),
	(9,'dev1guest_wangwu@torna.cn','997a3b669d4be9b034b23e620fc1c48e','王五',0,'register','dev1guest_wangwu@torna.cn',1,0,'2020-12-15 09:43:33','2021-05-25 18:03:07'),
	(10,'dev2guest_zhaoliu@torna.cn','db4c69808f677df84b76b902203a807e','赵六',0,'register','dev2guest_zhaoliu@torna.cn',1,0,'2020-12-15 09:50:01','2021-05-25 18:03:07'),
	(11,'dev2back_admin@torna.cn','de017957932b285ff6032a4d0c584471','后台项目负责人',0,'register','dev2back_admin@torna.cn',1,0,'2020-12-15 10:10:17','2021-05-25 18:03:07'),
	(12,'dev2back_lisi@torna.cn','cfb8d9b2e8447520fe0df35242566096','李四',0,'register','dev2back_lisi@torna.cn',1,0,'2020-12-15 10:10:32','2021-05-25 18:03:07'),
	(13,'dev2back_guest@torna.cn','211b7fb1fce482e3bd312fcdb5a89d7f','后台访客',0,'register','dev2back_guest@torna.cn',1,0,'2020-12-15 11:21:07','2021-05-25 18:03:07'),
	(14,'test@torna.cn','c19b85cecd8787ba8712ff764bf70f81','测试A',0,'register','test@torna.cn',1,0,'2021-01-25 09:08:21','2021-05-25 18:03:07');


INSERT INTO `user_message` (`id`, `user_id`, `message`, `is_read`, `type`, `source_id`, `gmt_create`, `gmt_modified`) VALUES
	(3,2,'超级管理员 修改了文档 获取分类信息',1,1,2,'2021-01-19 13:38:20','2021-01-19 16:28:54'),
	(4,2,'超级管理员 更新了【获取分类信息】，备注: 111',1,1,2,'2021-01-19 13:43:42','2021-01-19 16:30:36'),
	(5,2,'超级管理员 更新了【获取分类信息】，备注: 3333',1,1,2,'2021-01-19 13:43:45','2021-01-19 15:27:41'),
	(6,2,'超级管理员 更新了【获取分类信息】，备注: 本次需改了很多内容，这是一句很长的话',1,1,2,'2021-01-19 13:49:55','2021-01-19 16:21:17'),
	(7,2,'超级管理员 更新了【获取分类信息】',1,1,2,'2021-01-19 15:33:41','2021-01-19 15:49:55'),
	(8,2,'超级管理员 更新了【获取分类信息】，备注: 1231231',1,1,2,'2021-01-19 15:33:44','2021-01-19 16:21:29'),
	(9,2,'超级管理员 更新了【获取分类信息】',1,1,2,'2021-01-19 16:39:35','2021-01-19 16:48:32'),
	(10,2,'超级管理员 删除了【获取分类信息】',1,1,2,'2021-01-19 16:44:37','2021-01-19 16:46:06');


INSERT INTO `user_subscribe` (`id`, `user_id`, `type`, `source_id`, `is_deleted`, `gmt_create`, `gmt_modified`) VALUES
	(1,1,1,16,0,'2021-01-18 16:33:51','2021-01-18 16:38:58'),
	(2,1,1,4,0,'2021-01-18 16:41:09','2021-01-18 17:18:12'),
	(3,1,1,6,0,'2021-01-18 17:17:57','2021-01-18 17:17:57'),
	(4,1,1,5,0,'2021-01-18 17:17:59','2021-01-18 17:35:14'),
	(5,1,1,8,1,'2021-01-18 17:18:04','2021-01-18 17:21:19'),
	(6,1,1,2,0,'2021-01-18 17:18:07','2021-01-18 17:18:07'),
	(7,1,1,3,0,'2021-01-18 17:18:10','2021-01-18 17:18:10'),
	(8,2,1,2,0,'2021-01-19 10:24:11','2021-01-19 10:24:11');



