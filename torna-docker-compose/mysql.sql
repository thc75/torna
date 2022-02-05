-- 适用于MySQL5.6.5+
-- 备注：5.5.3开始支持utf8mb4，5.6.5开始支持CURRENT_TIMESTAMP(datetime)


CREATE
DATABASE IF NOT EXISTS `torna` DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_general_ci;
USE
`torna`;


SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for compose_additional_page
-- ----------------------------
DROP TABLE IF EXISTS `compose_additional_page`;
CREATE TABLE `compose_additional_page`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `project_id`   bigint(20) UNSIGNED NOT NULL COMMENT 'compose_project.id',
    `title`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文档标题',
    `content`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文档内容',
    `order_index`  int(11) NOT NULL DEFAULT 0 COMMENT '排序值',
    `status`       tinyint(4) NOT NULL DEFAULT 1 COMMENT '1:启用，0：禁用',
    `is_deleted`   tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`   datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX          `idx_projectid` (`project_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '聚合文档附加页'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of compose_additional_page
-- ----------------------------

-- ----------------------------
-- Table structure for compose_common_param
-- ----------------------------
DROP TABLE IF EXISTS `compose_common_param`;
CREATE TABLE `compose_common_param`
(
    `id`                 bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `data_id`            varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '唯一id，md5(doc_id:parent_id:style:name)',
    `name`               varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '字段名称',
    `type`               varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT 'String' COMMENT '字段类型',
    `required`           tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否必须，1：是，0：否',
    `max_length`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '-' COMMENT '最大长度',
    `example`            varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '示例值',
    `description`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '描述',
    `enum_id`            bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'enum_info.id',
    `compose_project_id` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'compose_project.id',
    `parent_id`          bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `style`              tinyint(4) NOT NULL DEFAULT 0 COMMENT '0：path, 1：header， 2：请求参数，3：返回参数，4：错误码',
    `order_index`        int(11) NOT NULL DEFAULT 0 COMMENT '排序索引',
    `is_deleted`         tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`         datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`       datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_dataid` (`data_id`) USING BTREE,
    INDEX                `idx_compose_project_id` (`compose_project_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '聚合文档公共参数'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of compose_common_param
-- ----------------------------

-- ----------------------------
-- Table structure for compose_doc
-- ----------------------------
DROP TABLE IF EXISTS `compose_doc`;
CREATE TABLE `compose_doc`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `doc_id`       bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'doc_info.id',
    `project_id`   bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'compose_project.id',
    `is_folder`    tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否文件夹',
    `folder_name`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '文件夹名称',
    `parent_id`    bigint(20) NOT NULL DEFAULT 0,
    `origin`       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文档来源',
    `is_deleted`   tinyint(4) NOT NULL DEFAULT 0,
    `creator`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `order_index`  int(10) UNSIGNED NOT NULL DEFAULT 0,
    `gmt_create`   datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX          `idx_projectid` (`project_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '文档引用'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of compose_doc
-- ----------------------------
INSERT INTO `compose_doc`
VALUES (1, 0, 1, 1, '订单模块', 0, '', 0, '超级管理员', 0, '2021-05-25 18:05:23', '2021-05-25 18:05:23');
INSERT INTO `compose_doc`
VALUES (2, 0, 1, 1, '产品模块', 0, '', 0, '超级管理员', 0, '2021-05-25 18:05:30', '2021-05-25 18:05:30');
INSERT INTO `compose_doc`
VALUES (3, 2, 1, 0, '', 1, '研发一部/商城项目/故事API', 0, '超级管理员', 0, '2021-05-25 18:05:46', '2021-05-25 18:05:46');
INSERT INTO `compose_doc`
VALUES (4, 3, 1, 0, '', 1, '研发一部/商城项目/故事API', 0, '超级管理员', 0, '2021-05-25 18:05:46', '2021-05-25 18:05:46');
INSERT INTO `compose_doc`
VALUES (5, 10, 1, 0, '', 2, '研发一部/商城项目/故事API', 0, '超级管理员', 0, '2021-05-25 18:05:53', '2021-05-25 18:05:53');
INSERT INTO `compose_doc`
VALUES (6, 11, 1, 0, '', 2, '研发一部/商城项目/故事API', 0, '超级管理员', 0, '2021-05-25 18:05:53', '2021-05-25 18:05:53');

-- ----------------------------
-- Table structure for compose_project
-- ----------------------------
DROP TABLE IF EXISTS `compose_project`;
CREATE TABLE `compose_project`
(
    `id`            bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '项目名称',
    `description`   varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '项目描述',
    `space_id`      bigint(20) NOT NULL DEFAULT 0 COMMENT '所属空间，space.id',
    `type`          tinyint(4) NOT NULL DEFAULT 1 COMMENT '访问形式，1：公开，2：加密',
    `password`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '访问密码',
    `creator_id`    bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建者userid',
    `creator_name`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '',
    `modifier_id`   bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `modifier_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '',
    `order_index`   int(11) NOT NULL DEFAULT 0 COMMENT '排序索引',
    `show_debug`    tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否显示调试',
    `gateway_url`   varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '网关url',
    `status`        tinyint(4) NOT NULL DEFAULT 1 COMMENT '1：有效，0：无效',
    `is_deleted`    tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`    datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`  datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX           `idx_spaceid` (`space_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '组合项目表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of compose_project
-- ----------------------------
INSERT INTO `compose_project`
VALUES (1, '聚合接口', '提供给第三方的接口', 18, 1, '', 1, '超级管理员', 1, '超级管理员', 0, 1, '', 1, 0, '2021-05-25 18:05:14',
        '2021-05-25 18:05:14');

-- ----------------------------
-- Table structure for doc_info
-- ----------------------------
DROP TABLE IF EXISTS `doc_info`;
CREATE TABLE `doc_info`
(
    `id`                    bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `data_id`               varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '唯一id，接口规则：md5(module_id:parent_id:url:http_method)。分类规则：md5(module_id:parent_id:name)',
    `md5`                   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '文档内容的md5值',
    `name`                  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文档名称',
    `description`           text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文档描述',
    `author`                varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '维护人',
    `type`                  tinyint(4) NOT NULL DEFAULT 0 COMMENT '0:http,1:dubbo',
    `url`                   varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '访问URL',
    `http_method`           varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT 'http方法',
    `content_type`          varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'contentType',
    `is_folder`             tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否是分类，0：不是，1：是',
    `parent_id`             bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '父节点',
    `module_id`             bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '模块id，module.id',
    `is_use_global_headers` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否使用全局请求参数',
    `is_use_global_params`  tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否使用全局请求参数',
    `is_use_global_returns` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否使用全局返回参数',
    `is_request_array`      tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否请求数组',
    `is_response_array`     tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否返回数组',
    `request_array_type`    varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT 'object' COMMENT '请求数组时元素类型',
    `response_array_type`   varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT 'object' COMMENT '返回数组时元素类型',
    `create_mode`           tinyint(4) NOT NULL DEFAULT 0 COMMENT '新增操作方式，0：人工操作，1：开放平台推送',
    `modify_mode`           tinyint(4) NOT NULL DEFAULT 0 COMMENT '修改操作方式，0：人工操作，1：开放平台推送',
    `creator_id`            bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建人',
    `creator_name`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建者昵称user_info.nickname',
    `modifier_id`           bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '修改人',
    `modifier_name`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建者昵称user_info.realname',
    `order_index`           int(11) NOT NULL DEFAULT 0 COMMENT '排序索引',
    `remark`                varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
    `is_show`               tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否显示',
    `is_deleted`            tinyint(4) NOT NULL DEFAULT 0,
    `is_locked`             tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否锁住',
    `gmt_create`            datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`          datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_data_id` (`data_id`) USING BTREE,
    INDEX                   `idx_moduleid` (`module_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 40
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '文档信息'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doc_info
-- ----------------------------
INSERT INTO `doc_info`
VALUES (1, '5fa7cd78bc872cd8fdc09ee3d6afedd2', '', '故事接口', '', '', 0, '', '', '', 1, 0, 1, 1, 1, 1, 0, 0, 'object',
        'object', 0, 0, 3, '', 3, '', 0, '', 1, 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_info`
VALUES (2, '3292c28718c1e74d6e14e0160eecf379', '', '获取分类信息', '', '', 0, '/story/category/get/v1', 'POST',
        'application/json', 0, 1, 1, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 3, '', 1, '超级管理员', 0, '', 1, 0, 0,
        '2020-12-15 10:01:48', '2021-01-19 17:03:29');
INSERT INTO `doc_info`
VALUES (3, '48ae195650ab5cd4d521b62704d15b57', '', '忽略签名验证', '', '', 0, '/story/get/ignore/v1', 'POST',
        'application/json', 0, 1, 1, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 3, '', 3, '', 0, '', 1, 0, 0,
        '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_info`
VALUES (4, 'd623ea4fe35e903a42530643e6087795', '', '获取故事信息（首位）', '', '', 0, '/story/get/v1', 'POST', 'application/json',
        0, 1, 1, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 3, '', 3, '', 0, '', 1, 0, 0, '2020-12-15 10:01:48',
        '2020-12-15 10:01:48');
INSERT INTO `doc_info`
VALUES (5, '1319b1ba77712fc4c6668d04b05fd68a', '', '获取故事信息', '', '', 0, '/story/get/v2', 'POST', 'application/json', 0,
        1, 1, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 3, '', 3, '', 0, '', 1, 0, 0, '2020-12-15 10:01:48',
        '2020-12-15 10:01:48');
INSERT INTO `doc_info`
VALUES (6, '3a15e3407182ccf902b6e9f5c1b1b875', '', '返回数组结果（第二）', '', '', 0, '/story/list/v1', 'POST',
        'application/json', 0, 1, 1, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 3, '', 3, '', 0, '', 1, 0, 0,
        '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_info`
VALUES (7, 'fa3324c4dd6b978434f8541f73393118', '', '树状返回', '', '', 0, '/story/tree/v1', 'POST', 'application/json', 0,
        1, 1, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 3, '', 3, '', 0, '', 1, 0, 0, '2020-12-15 10:01:48',
        '2020-12-15 10:01:48');
INSERT INTO `doc_info`
VALUES (8, 'ac0c66314833ef010bf7df1cf5692b43', '', '传递token', '', '', 0, '/token', 'POST', 'application/json', 0, 1, 1,
        1, 1, 1, 0, 0, 'object', 'object', 0, 0, 3, '', 1, '', 0, '', 1, 0, 0, '2020-12-15 10:01:48',
        '2021-01-19 17:03:30');
INSERT INTO `doc_info`
VALUES (9, '7ededf48dec7c2b582affe2d08d7d0b2', '', '文件上传', '', '', 0, '', '', '', 1, 0, 1, 1, 1, 1, 0, 0, 'object',
        'object', 0, 0, 3, '', 3, '', 0, '', 1, 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_info`
VALUES (10, '84d41cbee05d97aa568b7a06759cc19c', '', '文件上传例1', '', '', 0, '/upload/file1', 'POST', 'multipart/form-data',
        0, 9, 1, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 3, '', 3, '', 0, '', 1, 0, 0, '2020-12-15 10:01:48',
        '2020-12-15 10:01:48');
INSERT INTO `doc_info`
VALUES (11, '1172c623f531324750aad5c2fa72a751', '', '文件上传例2', '', '', 0, '/upload/file2', 'POST', 'multipart/form-data',
        0, 9, 1, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 3, '', 3, '', 0, '', 1, 0, 0, '2020-12-15 10:01:48',
        '2020-12-15 10:01:48');
INSERT INTO `doc_info`
VALUES (12, '92d2fa5b0ae7a0f2e29648929c5e435f', '', '文件下载', '', '', 0, '', '', '', 1, 0, 1, 1, 1, 1, 0, 0, 'object',
        'object', 0, 0, 3, '', 3, '', 0, '', 1, 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_info`
VALUES (13, 'f184470a11e0fd559e1f56294b53d4cb', '', '文件下载', '', '', 0, '/download/file1', 'POST', 'application/json', 0,
        12, 1, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 3, '', 3, '', 0, '', 1, 0, 0, '2020-12-15 10:01:48',
        '2020-12-15 10:01:48');
INSERT INTO `doc_info`
VALUES (14, '3f3aab998c9785fe13d41e02c95f7ef4', '', '食物接口', '', '', 0, '', '', '', 1, 0, 1, 1, 1, 1, 0, 0, 'object',
        'object', 0, 0, 3, '', 3, '', 0, '', 1, 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_info`
VALUES (15, '4942c1bd41bbf75d995722986f70a9f4', '', '获取食物', '', '', 0, '/food/getFoodById', 'GET', '', 0, 14, 1, 1, 1,
        1, 0, 0, 'object', 'object', 0, 0, 3, '', 3, '', 0, '', 1, 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_info`
VALUES (16, '940818d032209bc3628895d483463ce1', '', '获取用户接口', '获取用户接口', '', 0, 'get/group/{groupId}/id/{id}', 'GET', '',
        0, 0, 2, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 12, '李四', 1, '超级管理员', 0, 'ccc', 1, 0, 0,
        '2020-12-15 10:15:55', '2021-01-18 15:05:16');
INSERT INTO `doc_info`
VALUES (17, '2df0b4e87fc3a8c37d08e3d7a95d0cf4', '', 'comment', '', '', 0, 'phoenix/web/v1/comment/list/101426186',
        'GET', '', 0, 0, 1, 1, 1, 1, 0, 0, 'object', 'object', 0, 0, 2, '研发一部经理', 1, '研发一部经理', 0, '', 1, 1, 0,
        '2021-01-19 17:29:23', '2021-01-22 11:52:21');
INSERT INTO `doc_info`
VALUES (18, 'c018597c3da062833820d46754d8d74f', '', '订单模块', '', '', 0, '', '', '', 1, 0, 3, 1, 1, 1, 0, 0, 'object',
        'object', 1, 1, 2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0, '2021-06-08 13:54:22', '2021-06-08 13:54:22');
INSERT INTO `doc_info`
VALUES (19, '1d3c432b05bf4b7203ebace0bf044c1f', '', '提交数组/返回数组5', 'List方式', '', 0, '/order/postArr5', 'POST',
        'application/json', 0, 18, 3, 1, 1, 1, 1, 1, 'integer', 'integer', 1, 1, 2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0,
        '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (20, '4b5436a9e81ac22371e0f7b6b00afb8c', '', '提交数组/返回数组3', '', '', 0, '/order/postArr3', 'POST',
        'application/json', 0, 18, 3, 1, 1, 1, 1, 1, 'object', 'integer', 1, 1, 2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0,
        '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (21, '2ced912c74e4eb9a53479413f39d4b19', '', '提交数组/返回数组2', '', '', 0, '/order/postArr2', 'POST',
        'application/json', 0, 18, 3, 1, 1, 1, 1, 1, 'integer', 'object', 1, 1, 2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0,
        '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (22, 'b60a5a7e94a05644bf50254d9fdf6eab', '', '提交数组/返回数组4', 'List方式', '', 0, '/order/postArr4', 'POST',
        'application/json', 0, 18, 3, 1, 1, 1, 1, 1, 'object', 'object', 1, 1, 2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0,
        '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (23, '1d8483ef6bb80ccd79cf114d56461b05', '', '提交数组/返回数组', '', '', 0, '/order/postArr', 'POST',
        'application/json', 0, 18, 3, 1, 1, 1, 1, 1, 'object', 'object', 1, 1, 2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0,
        '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (24, '0e36106f99a0adb4d4f3160c1c2ae6c3', '', '演示query参数,不加@RequestParam', '演示query参数,不加@RequestParam', '', 0,
        '/order/get', 'GET', '', 0, 18, 3, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0,
        '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (25, '1d0253f8f8fb2f3e2ae352dddfa5f2be', '', '提交订单信息，application/json', '提交订单信息，application/json', 'Tom', 0,
        '/order/create', 'POST', 'application/json', 0, 18, 3, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 2, 'Jim', 2,
        'Jim', 0, '', 1, 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (26, '1081d1f598bcc0df2a48894a506212ae', '', '全部参数form，全部由@ApiImplicitParams实现', '全部由@ApiImplicitParams实现', '',
        0, '/order/submit3/{productNo}', 'POST', 'application/x-www-form-urlencoded', 0, 18, 3, 1, 1, 1, 0, 0, 'object',
        'object', 1, 1, 2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (27, '73e15ad9d976104a63ae2771dc5223d2', '', '全部参数json，全部由@ApiImplicitParams实现', '全部由@ApiImplicitParams实现', '',
        0, '/order/submit2/{productNo}', 'POST', 'application/json', 0, 18, 3, 1, 1, 1, 0, 0, 'object', 'object', 1, 1,
        2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (28, '874136230933126ceb9e164411ae059b', '', '全部参数upload，全部由@ApiImplicitParams实现', '全部由@ApiImplicitParams实现', '',
        0, '/order/submit4/{productNo}', 'POST', 'multipart/form-data', 0, 18, 3, 1, 1, 1, 0, 0, 'object', 'object', 1,
        1, 2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (29, 'b9bdb068beda69e48579c7725a835030', '', '提交订单信息，application/x-www-form-urlencoded',
        '提交订单信息，application/x-www-form-urlencoded', '', 0, '/order/create2', 'POST',
        'application/x-www-form-urlencoded', 0, 18, 3, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 2, 'Jim', 2, 'Jim', 0,
        '', 1, 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (30, 'e7b907533f53eb25306c649c2f3cc2f9', '', '演示path参数', '演示path参数', '', 0, '/order/get/{orderNo}', 'GET', '', 0,
        18, 3, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0, '2021-06-08 13:54:23',
        '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (31, 'b34e5dcb5db2e798861857ec008a9bc9', '', '演示query参数,加@RequestParam', '演示query参数,加@RequestParam', '', 0,
        '/order/get2', 'GET', '', 0, 18, 3, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0,
        '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (32, '52cf7d54d887867c18a229ab93fe4df1', '', '演示path参数+query参数+body参数+header', '提交订单信息说明。。', '', 0,
        '/order/submit/{productNo}', 'POST', 'application/json', 0, 18, 3, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 2,
        'Jim', 2, 'Jim', 0, '', 1, 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (33, '4fbc6d999497b96a284eff0f110e5fcb', '', '获取某个产品下的订单', '<strong>注意：</strong>html演示。。', '', 0,
        '/order/get/{productNo}', 'GET', '', 0, 18, 3, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 2, 'Jim', 2, 'Jim', 0,
        '', 1, 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (34, '16f0ece214cf202e41fe482494b0ebce', '', '产品模块', '', '', 0, '', '', '', 1, 0, 3, 1, 1, 1, 0, 0, 'object',
        'object', 1, 1, 2, 'Jim', 2, 'Jim', -1, '', 1, 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (35, '1ab4dee15c5cb5f16205446f16e8ba01', '', '返回树', '', '', 0, '/product/getTree', 'GET', '', 0, 34, 3, 1, 1, 1,
        0, 0, 'object', 'object', 1, 1, 2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0, '2021-06-08 13:54:23',
        '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (36, '2146fa58a9dcdb3f7c52d350e9732a27', '', '获取产品', '获取产品说明。。', 'Tom,Jim', 0, '/product/get/{productNo}', 'GET',
        '', 0, 34, 3, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 2, 'Jim', 2, 'Jim', 0, '', 1, 0, 0,
        '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (37, 'd8fe88fd74144ac90b50d94bde7414ee', '', '上传文件', '', '', 0, '/product/upload', 'POST', 'multipart/form-data',
        0, 34, 3, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 2, 'Jim', 2, 'Jim', 11, '', 1, 0, 0, '2021-06-08 13:54:23',
        '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (38, '9edd7624130fe72727016b02df1e9d77', '', '上传文件2', '', '', 0, '/product/upload2', 'POST',
        'multipart/form-data', 0, 34, 3, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 2, 'Jim', 2, 'Jim', 12, '', 1, 0, 0,
        '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_info`
VALUES (39, '8bccd6dd861c2671738b56e179d8804d', '', '上传文件3', '', '', 0, '/product/upload3', 'POST',
        'multipart/form-data', 0, 34, 3, 1, 1, 1, 0, 0, 'object', 'object', 1, 1, 2, 'Jim', 2, 'Jim', 13, '', 1, 0, 0,
        '2021-06-08 13:54:23', '2021-06-08 13:54:23');

-- ----------------------------
-- Table structure for doc_param
-- ----------------------------
DROP TABLE IF EXISTS `doc_param`;
CREATE TABLE `doc_param`
(
    `id`            bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `data_id`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '唯一id，md5(doc_id:parent_id:style:name)',
    `name`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '字段名称',
    `type`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT 'String' COMMENT '字段类型',
    `required`      tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否必须，1：是，0：否',
    `max_length`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '-' COMMENT '最大长度',
    `example`       varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '示例值',
    `description`   text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '描述',
    `enum_id`       bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'enum_info.id',
    `doc_id`        bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'doc_info.id',
    `parent_id`     bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `style`         tinyint(4) NOT NULL DEFAULT 0 COMMENT '0：path, 1：header， 2：请求参数，3：返回参数，4：错误码',
    `create_mode`   tinyint(4) NOT NULL DEFAULT 0 COMMENT '新增操作方式，0：人工操作，1：开放平台推送',
    `modify_mode`   tinyint(4) NOT NULL DEFAULT 0 COMMENT '修改操作方式，0：人工操作，1：开放平台推送',
    `creator_id`    bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `creator_name`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '',
    `modifier_id`   bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `modifier_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '',
    `order_index`   int(11) NOT NULL DEFAULT 0 COMMENT '排序索引',
    `is_deleted`    tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`    datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`  datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_dataid` (`data_id`) USING BTREE,
    INDEX           `idx_docid` (`doc_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 271
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '文档参数'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doc_param
-- ----------------------------
INSERT INTO `doc_param`
VALUES (1, '9cedd1b23736a816e96bca9603c2b5f4', 'categoryName', 'string', 0, '-', '娱乐', '分类名称', 0, 2, 0, 2, 0, 0, 3,
        '商城项目admin', 1, '超级管理员', 0, 0, '2020-12-15 10:01:48', '2021-01-19 16:39:35');
INSERT INTO `doc_param`
VALUES (2, 'df34a965ab9c44e2e6b0b282c711e9b9', 'createTime', 'string', 0, '-', '2019-09-25 17:12:52', '创建时间', 0, 2, 0,
        2, 0, 0, 3, '商城项目admin', 1, '超级管理员', 0, 0, '2020-12-15 10:01:48', '2021-01-19 16:39:35');
INSERT INTO `doc_param`
VALUES (3, '841d66d19b087e9982b76ba0a0aab7a4', 'storyParam', 'object', 0, '-', '', '', 0, 2, 0, 2, 0, 0, 3, '商城项目admin',
        1, '超级管理员', 0, 0, '2020-12-15 10:01:48', '2021-01-19 16:39:35');
INSERT INTO `doc_param`
VALUES (4, 'ae90af481f84971aff6d1457b11609d5', 'id', 'integer', 0, '-', '111', '故事ID', 0, 2, 3, 2, 0, 0, 3, '商城项目admin',
        1, '超级管理员', 0, 0, '2020-12-15 10:01:48', '2021-01-19 16:39:35');
INSERT INTO `doc_param`
VALUES (5, 'b4bd22cc240b23325eb3bfeadab642e3', 'remark', 'string', 0, '64', 'xx', '备注 (第二)', 0, 2, 3, 2, 0, 0, 3,
        '商城项目admin', 1, '超级管理员', 0, 0, '2020-12-15 10:01:48', '2021-01-19 16:39:35');
INSERT INTO `doc_param`
VALUES (6, '5d13d5536d163c8a165f613b7d678f76', 'name', 'string', 1, '20', '白雪公主', '故事名称', 0, 2, 3, 2, 0, 0, 3,
        '商城项目admin', 1, '超级管理员', 0, 0, '2020-12-15 10:01:48', '2021-01-19 16:39:35');
INSERT INTO `doc_param`
VALUES (7, 'f3a0327795da96eb4955ea525b64cb20', 'categoryName', 'string', 0, '-', '娱乐', '分类名称', 0, 2, 0, 3, 0, 0, 3,
        '商城项目admin', 1, '超级管理员', 0, 0, '2020-12-15 10:01:48', '2021-01-19 16:39:35');
INSERT INTO `doc_param`
VALUES (8, 'b63b34c6077d10ca10d3a5d6a5001828', 'storyResult', 'object', 0, '-', '', '分类故事', 0, 2, 0, 3, 0, 0, 3,
        '商城项目admin', 1, '超级管理员', 0, 0, '2020-12-15 10:01:48', '2021-01-19 16:39:35');
INSERT INTO `doc_param`
VALUES (9, 'a8ccc97411881fa1be3086800d199ecc', 'gmt_create', 'string', 0, '-', '2019-04-14 19:02:12', '创建时间', 0, 2, 8,
        3, 0, 0, 3, '商城项目admin', 1, '超级管理员', 0, 0, '2020-12-15 10:01:48', '2021-01-19 16:39:35');
INSERT INTO `doc_param`
VALUES (10, 'bedf8a65ba8809d6603e013e57b2832c', 'id', 'integer', 0, '-', '1', '故事ID', 0, 2, 8, 3, 0, 0, 3, '商城项目admin',
        1, '超级管理员', 0, 0, '2020-12-15 10:01:48', '2021-01-19 16:39:35');
INSERT INTO `doc_param`
VALUES (11, 'b48f0fb65540b52e13ce16b515f758cc', 'name', 'string', 0, '-', '海底小纵队', '故事名称', 0, 2, 8, 3, 0, 0, 3,
        '商城项目admin', 1, '超级管理员', 0, 0, '2020-12-15 10:01:48', '2021-01-19 16:39:35');
INSERT INTO `doc_param`
VALUES (12, '2e5aa28eade28333f5564a9abb759e83', 'id', 'integer', 0, '-', '111', '故事ID', 0, 3, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (13, '6248cfafe3b1491846475d5f8e20e248', 'remark', 'string', 0, '-', 'xx', '备注 (第二)', 0, 3, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (14, '6c3c6428f5853b3b0fb6639552519dd6', 'name', 'string', 0, '-', '白雪公主', '故事名称', 0, 3, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (15, 'eda2c2531888b200aceb76b5f6596b6a', 'gmt_create', 'string', 0, '-', '2019-04-14 19:02:12', '创建时间', 0, 3, 0,
        3, 0, 0, 3, '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (16, '30dd8d1e23a460172d35a6a65a30262c', 'id', 'integer', 0, '-', '1', '故事ID', 0, 3, 0, 3, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (17, '593b2b0cb3eb79276b02dcf8ab56eb63', 'name', 'string', 0, '-', '海底小纵队', '故事名称', 0, 3, 0, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (18, 'd28b974ec3fe2bad4b934c696c4d96c8', 'id', 'integer', 0, '-', '111', '故事ID', 0, 4, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (19, 'cb37ba05364c6a2e0acf059f5c9d3533', 'remark', 'string', 0, '64', 'xx', '备注 (第二)', 0, 4, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (20, '014141306e4e9d2497b3f261152f585a', 'name', 'string', 1, '20', '白雪公主', '故事名称', 0, 4, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (21, '40172ca4277e0c52a5a662e879109161', 'gmt_create', 'string', 0, '-', '2019-04-14 19:02:12', '创建时间', 0, 4, 0,
        3, 0, 0, 3, '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (22, 'd3f4a37bc011a373bbf6bcfcc8e4608f', 'id', 'integer', 0, '-', '1', '故事ID', 0, 4, 0, 3, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (23, 'c508f91b8ff7aede0f7ac39fbb8dd2fb', 'name', 'string', 0, '-', '海底小纵队', '故事名称', 0, 4, 0, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (24, 'cac09a927ec388edfc19dd1039641d67', 'id', 'integer', 0, '-', '111', '故事ID', 0, 5, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (25, '339101d3efffe3160dac1ecae7871c4b', 'remark', 'string', 0, '64', 'xx', '备注 (第二)', 0, 5, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (26, '88fc4120e465887ab41b9ca91da87bb3', 'name', 'string', 1, '20', '白雪公主', '故事名称', 0, 5, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (27, '9e65ce76eb39ad0f82095929482e7693', 'gmt_create', 'string', 0, '-', '2019-04-14 19:02:12', '创建时间', 0, 5, 0,
        3, 0, 0, 3, '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (28, 'da337cdcf39f1ce880850a3189cf660b', 'id', 'integer', 0, '-', '1', '故事ID', 0, 5, 0, 3, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (29, 'be0c8d746c7e16c0adfcf0104676a9ff', 'name', 'string', 0, '-', '海底小纵队', '故事名称', 0, 5, 0, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (30, '46daac3b859ef0d48650f1162311c6a0', 'id', 'integer', 0, '-', '111', '故事ID', 0, 6, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (31, '6a08c7298b39910d2a1c1a0ba9fc1f1d', 'remark', 'string', 0, '64', 'xx', '备注 (第二)', 0, 6, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (32, '4301980bc8dc1d8e14fadfb31a9ec85a', 'name', 'string', 1, '20', '白雪公主', '故事名称', 0, 6, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (33, '4561245e7c6e270233ed6595f6e818b5', 'StoryResult', 'array', 0, '-', '', '', 0, 6, 0, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (34, '8c181ef7670cdfd45d80d6698200b7b8', 'gmt_create', 'string', 0, '-', '2019-04-14 19:02:12', '创建时间', 0, 6, 33,
        3, 0, 0, 3, '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (35, 'b7c676a3e2783b2709b7ec4accf375b6', 'id', 'integer', 0, '-', '1', '故事ID', 0, 6, 33, 3, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (36, '051b23a59ff6a74ad79122fc2230422e', 'name', 'string', 0, '-', '海底小纵队', '故事名称', 0, 6, 33, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (37, '5533aa3704c7b4518471f5a326f47551', 'id', 'integer', 0, '-', '111', '故事ID', 0, 7, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (38, 'f0f92b425a93640fb206bfb0902b1366', 'remark', 'string', 0, '64', 'xx', '备注 (第二)', 0, 7, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (39, '112d37f2c76b5b0251949cac69d68fe1', 'name', 'string', 1, '20', '白雪公主', '故事名称', 0, 7, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (40, 'dd4a92a31102d4220ffb7cca2b1714e7', 'children', 'array', 0, '-', '', '子节点', 0, 7, 0, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (41, '61c83cc34f795b1bcd8a92dd45a5d2e7', 'children', 'object', 0, '-', '', '子节点', 0, 7, 40, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (42, '4f54764326fd4743e55850149891308c', 'id', 'integer', 0, '-', '', 'id', 0, 7, 40, 3, 0, 0, 3, '商城项目admin', 3,
        '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (43, 'cbe98e824b7a32fd86cd9dd160292d06', 'name', 'string', 0, '-', '', '名称', 0, 7, 40, 3, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (44, 'bcda07eff74d7fe716899745136d9253', 'pid', 'integer', 0, '-', '', '父id', 0, 7, 40, 3, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (45, 'a4f91fa41249fe004bbd76c972257b85', 'id', 'integer', 0, '-', '', 'id', 0, 7, 0, 3, 0, 0, 3, '商城项目admin', 3,
        '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (46, '41849928604437f4478c6a8d163b0442', 'name', 'string', 0, '-', '', '名称', 0, 7, 0, 3, 0, 0, 3, '商城项目admin', 3,
        '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (47, '0b3b92569b57c5a027cefacd4c932a26', 'pid', 'integer', 0, '-', '', '父id', 0, 7, 0, 3, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (48, '20ff1b4db324d1b09cf59c690c1aae03', 'id', 'integer', 0, '-', '111', '故事ID', 0, 8, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (49, 'c6456cfdda2dc41104a27618852b3f1f', 'remark', 'string', 0, '64', 'xx', '备注 (第二)', 0, 8, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (50, '54722d47865c573c3671527e22797319', 'name', 'string', 1, '20', '白雪公主', '故事名称', 0, 8, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (51, '0d757e958fc3fa7665a8b8eabc212303', 'gmt_create', 'string', 0, '-', '2019-04-14 19:02:12', '创建时间', 0, 8, 0,
        3, 0, 0, 3, '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (52, 'a4fa420275434607ba73f33e2152422f', 'id', 'integer', 0, '-', '1', '故事ID', 0, 8, 0, 3, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (53, 'b8d81b117a75d84d6ff87d569c366ae6', 'name', 'string', 0, '-', '海底小纵队', '故事名称', 0, 8, 0, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (54, 'f5c01b74aee2f850e7d7e06833241acc', 'file1', 'file', 1, '-', '', '文件1', 0, 10, 0, 2, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (55, 'e98f7c9c934e4867f563e1b9b01b5cba', 'file2', 'file', 1, '-', '', '文件2', 0, 10, 0, 2, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (56, 'f30ba8fb274890cc3f85a03f6e00d921', 'remark', 'string', 0, '-', '', '', 0, 10, 0, 2, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (57, '5c8ba24461e3947251047e5d06d1fa1a', 'files', 'array', 0, '-', '', '', 0, 10, 0, 3, 0, 0, 3, '商城项目admin', 3,
        '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (58, 'be3e412c33827b677b953bea02711246', 'content', 'string', 0, '-', '啊啊啊', '文件内容', 0, 10, 57, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (59, '8cd5101036784bf60ccb9a2faf9f577d', 'filename', 'string', 0, '-', '1.txt', '文件名称', 0, 10, 57, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (60, 'a9db899e347349533d828f64f822b020', 'size', 'integer', 0, '-', '109', '文件大小', 0, 10, 57, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (61, '476448d9914541d3a39280510335d9d3', 'remark', 'string', 0, '-', '', '', 0, 10, 0, 3, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (62, 'ccc42e5b6ca0526300e867bbb1d2ba08', 'remark', 'string', 0, '-', '', '', 0, 11, 0, 2, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (63, '5a1e26b6c5784250a14780863207ea2c', 'files', 'array', 0, '-', '', '', 0, 11, 0, 3, 0, 0, 3, '商城项目admin', 3,
        '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (64, '7580d08580a198b1cb507078767fe924', 'content', 'string', 0, '-', '啊啊啊', '文件内容', 0, 11, 63, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (65, '6989d6adeeb1f8d7bc48d82d94992503', 'filename', 'string', 0, '-', '1.txt', '文件名称', 0, 11, 63, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (66, 'ce9866be95c15c861a6ccf518249d123', 'size', 'integer', 0, '-', '109', '文件大小', 0, 11, 63, 3, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (67, 'eb777cbc1343e3eea07b3d4da8b35dae', 'remark', 'string', 0, '-', '', '', 0, 11, 0, 3, 0, 0, 3, '商城项目admin',
        3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (68, 'f7c99023c97bb8ff02e23044e1d4dea5', 'id', 'integer', 0, '-', '111', '故事ID', 0, 13, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (69, '9d1fc88492273e49154e2e9b53f3668c', 'remark', 'string', 0, '64', 'xx', '备注 (第二)', 0, 13, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (70, 'edebdefbc34e83211c4b177104a6298b', 'name', 'string', 1, '20', '白雪公主', '故事名称', 0, 13, 0, 2, 0, 0, 3,
        '商城项目admin', 3, '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (71, 'b6e6da845523c535f5517f7c33bb6731', 'id', 'integer', 0, '-', '', 'id', 0, 15, 0, 2, 0, 0, 3, '商城项目admin', 3,
        '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (72, '555b29db1207e5377f3b1ee342f4a392', 'id', 'integer', 0, '-', '', '', 0, 15, 0, 3, 0, 0, 3, '商城项目admin', 3,
        '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (73, 'bdfdcfe4e5ee99561bb064aebfbf6751', 'name', 'string', 0, '-', '', '', 0, 15, 0, 3, 0, 0, 3, '商城项目admin', 3,
        '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (74, '60ba1291613b945087ae8cfe6bff1bbc', 'price', 'number', 0, '-', '', '', 0, 15, 0, 3, 0, 0, 3, '商城项目admin', 3,
        '商城项目admin', 0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `doc_param`
VALUES (75, 'f788250eb074c6af22cf7755ad313431', 'id', 'number', 1, '64', '123', '用户id', 0, 16, 0, 2, 0, 0, 12, '李四', 1,
        '超级管理员', 0, 0, '2020-12-15 10:15:55', '2021-01-18 15:05:16');
INSERT INTO `doc_param`
VALUES (76, 'fb0d734de29a47397d24d3f9ed2b6bf0', 'id', 'number', 1, '64', '123', '用户id', 0, 16, 0, 3, 0, 0, 12, '李四', 1,
        '超级管理员', 0, 0, '2020-12-15 10:15:55', '2021-01-18 15:05:16');
INSERT INTO `doc_param`
VALUES (77, 'e40f2386a330283bfe9a4c8f06882224', 'name', 'string', 1, '64', 'jim', '用户名称', 0, 16, 0, 3, 0, 0, 12, '李四',
        1, '超级管理员', 0, 0, '2020-12-15 10:15:55', '2021-01-18 15:05:16');
INSERT INTO `doc_param`
VALUES (78, '6e3e6a9a659dbb452e1c35a87d27cafc', 'groupId', 'string', 1, '64', '1', '分组id', 0, 16, 0, 0, 0, 0, 11,
        '后台项目负责人', 1, '超级管理员', 0, 0, '2020-12-16 15:35:09', '2021-01-18 15:05:16');
INSERT INTO `doc_param`
VALUES (79, '0e799f616e11beb007d4d8e9cf919a2f', 'id', 'number', 1, '64', '1', '用户id', 0, 16, 0, 0, 0, 0, 11, '后台项目负责人',
        1, '超级管理员', 0, 0, '2020-12-16 15:35:09', '2021-01-18 15:05:16');
INSERT INTO `doc_param`
VALUES (80, '9584e01340755ba6f8c82df9eb032d89', 'token', 'string', 1, '64', '1111', 'token', 0, 2, 0, 1, 0, 0, 1,
        '超级管理员', 1, '超级管理员', 0, 0, '2021-01-19 10:32:09', '2021-01-19 16:39:35');
INSERT INTO `doc_param`
VALUES (81, 'e50bb76340fd01b339e1a2aa8d3215f3', 'page', 'string', 1, '64', '1', '', 0, 17, 0, 2, 0, 0, 2, '研发一部经理', 2,
        '研发一部经理', 0, 0, '2021-01-19 17:29:23', '2021-01-19 17:29:23');
INSERT INTO `doc_param`
VALUES (82, '227be5c273852f1927611268522efa00', 'size', 'string', 1, '64', '10', '', 0, 17, 0, 2, 0, 0, 2, '研发一部经理', 2,
        '研发一部经理', 0, 0, '2021-01-19 17:29:23', '2021-01-19 17:29:23');
INSERT INTO `doc_param`
VALUES (83, '14c5f6044d9ce6850dc4e1951d0151f2', 'commentId', 'string', 1, '64', '', '', 0, 17, 0, 2, 0, 0, 2, '研发一部经理',
        2, '研发一部经理', 0, 0, '2021-01-19 17:29:23', '2021-01-19 17:29:23');
INSERT INTO `doc_param`
VALUES (84, 'a5b6cf48c787af0c5134e913be3d9430', '', 'integer', 0, '-', '1,2,3', '订单id', 0, 19, 0, 2, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (85, 'f9af347c3f5ed5c3806cf599d3fbe451', '', 'integer', 0, '-', '4,5,6', '返回订单id', 0, 19, 0, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (86, 'd6d70fa7d4772f7ebc78202136e5bf68', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 20, 0, 2, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (87, 'd052a21bd76ba3ed86c5c9ac6bc1955c', 'remark', 'string', 0, '-', '订单', '备注', 0, 20, 0, 2, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (88, 'f7c14baf034ef3dd91bb5ef6313fe3e9', '', 'integer', 0, '-', '1,2,3', '订单id', 0, 20, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (89, '05d8e546708fef42108b7f635c7ec5dc', '', 'integer', 0, '-', '1,2,3', '订单id', 0, 21, 0, 2, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (90, '5f9026861a38004ef982d262742db7c1', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 21, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (91, 'e7f04c283b820db984a3ca6bd27993be', 'remark', 'string', 0, '-', '订单', '备注', 0, 21, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (92, '6ef100c9aa2e124f83c5f801d85f796a', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 22, 0, 2, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (93, '4e50d9fba9c00acaaf63c8c2ed886460', 'remark', 'string', 0, '-', '订单', '备注', 0, 22, 0, 2, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (94, '6355281c7ff265f27fbeebc32ce42a01', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 22, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (95, '4ad48afcca35dad0317a4fbbd7f9ac21', 'remark', 'string', 0, '-', '订单', '备注', 0, 22, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (96, '0c4e2f79eb1e495953f3dfa0a4159f66', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 23, 0, 2, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (97, '7add78ade786825bf96865a71562898f', 'remark', 'string', 0, '-', '订单', '备注', 0, 23, 0, 2, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (98, 'e90aa6c4c29198933369486e39984ec8', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 23, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (99, 'afc4724bc99d08b6250a809e1b1040a1', 'remark', 'string', 0, '-', '订单', '备注', 0, 23, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (100, '7ea1604dd117d666c8c84670a7cf69c1', 'orderNo', 'String', 0, '-', 'xxx', '订单id', 0, 24, 0, 5, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (101, '091108ac8e8bb814395b016000a7e782', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 24, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (102, 'a978c803be3842a481df0332070a3d90', 'data', 'object', 0, '', '', '数据', 0, 24, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (103, '77ef81567422ca97bc902a7b349115a5', 'userId', 'number', 0, '-', '123', '用户id', 0, 24, 102, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (104, 'd37b4490cc780f62d3fad9fb1db38d67', 'orderNo', 'string', 0, '-', 'xxx', '订单号', 0, 24, 102, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (105, 'e649fc70bccec7d4f626bb686c73ea9d', 'payTime', 'date', 0, '-', '2021-05-24 19:59:51', '订单id', 0, 24, 102,
        3, 1, 1, 2, 'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (106, 'd21177825d839cebb97c81990e70fc8d', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 25, 0, 2, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (107, '87affcf6288d27285a9050f5b3d002cc', 'remark', 'string', 0, '-', '订单', '备注', 0, 25, 0, 2, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (108, '1a6b14f345d366d3af77eea2b55ebe1e', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 25, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (109, '27ad96bbf6ec46b1ebbabb224a072171', 'data', 'object', 0, '', '', '数据', 0, 25, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (110, 'b3e7da61e988ee4c0dee9aaa6686fe07', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 25, 109, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (111, '70a57deae301407483abc9db8e93e502', 'remark', 'string', 0, '-', '订单', '备注', 0, 25, 109, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (112, '36460eff715e9a7a21e82625dc88837b', '100001', 'String', 0, '-', '', 'id错误', 0, 25, 0, 4, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (113, 'cefb9f0a1892401bae4be1e82dc9cd2c', '100002', 'String', 0, '-', '', '错误描述2', 0, 25, 0, 4, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (114, '0ff32e576117c67e72089874cb3881ed', '100003', 'String', 0, '-', '', '错误描述3', 0, 25, 0, 4, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (115, 'e3bcac59c41eba2c6b20bc35df75ce3b', 'productNo', 'integer', 1, '-', '11', '产品id', 0, 26, 0, 0, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (116, '812ab1c9b1aea48d2c66f3d1004b323b', 'token', 'String', 1, '-', 'asdfe', 'token', 0, 26, 0, 1, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (117, '6114eff1c655e6cfaaba4b4becb87683', 'qid', 'string', 1, '-', 'xx', '查询id', 0, 26, 0, 5, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (118, '3d3efbc3c37dfad9e7dd888afe453b1b', 'id', 'integer', 0, '-', '123', 'id', 0, 26, 0, 2, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (119, '01c1470e7d40c141bc35b13f802c4f1c', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 26, 0, 2, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (122, '20934eaeb137fe4d252b855ce54e8226', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 26, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (123, '630e4eb3e3e14642a23ab7de50cc2c32', 'data', 'object', 0, '-', '', '数据', 0, 26, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (124, '30bf99206592c3c5cda8e88cf07ed843', 'productNo', 'integer', 1, '-', '11', '产品id', 0, 27, 0, 0, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (125, '1c47d4981f3794628413f9d2487070cd', 'token', 'String', 1, '-', 'asdfe', 'token', 0, 27, 0, 1, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (126, '9e2c90b18330dbff3c6385fa91b291c6', 'qid', 'string', 1, '-', 'xx', '查询id', 0, 27, 0, 5, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (127, '411e79440592ce90e4f1c962d980c394', 'id', 'integer', 0, '-', '123', 'id', 0, 27, 0, 2, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (128, '493df97a0e7c4deeb10747cfcf90413b', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 27, 0, 2, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (131, '80b140ca86e47d8ffdc2c2b76389e342', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 27, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (132, '4829a5875f0615563a0f1690e0bdf8aa', 'data', 'object', 0, '', '', '数据', 0, 27, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (133, 'c0bd0b0494c82f80f8b5009e7c33b94e', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 27, 132, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (134, '073a7c8d6311158093f418612c60f79a', 'remark', 'string', 0, '-', '订单', '备注', 0, 27, 132, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (135, '40b407613393b5f780d7406094408dcf', 'orderDetail', 'object', 0, '', '', '订单详情', 0, 27, 132, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 2, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (136, '008d41e5fe3b99793b6c8b5cf11f4367', 'userId', 'number', 0, '-', '123', '用户id', 0, 27, 135, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (137, '18d2124e2ff8eb7db18fafa333b4862b', 'orderNo', 'string', 0, '-', 'xxx', '订单号', 0, 27, 135, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (138, '238f52a609b6419e3efff0779bb42a60', 'payTime', 'date', 0, '-', '2021-05-24 19:59:51', '订单id', 0, 27, 135,
        3, 1, 1, 2, 'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (139, '92abae892c22b8d8cda54d42fc4b317c', 'products', 'array', 0, '', '', '产品详情', 0, 27, 132, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 3, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (140, 'ba3d41d86a0711cdc47b9422fbc326b8', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 27, 139, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (141, 'c44f35372e4b2b5f4c09c12eaf5acf71', 'remark', 'string', 0, '-', '小米', '备注', 0, 27, 139, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (142, 'a2b600f413f8650bdefb138b6142f8b8', 'productDetailVO', 'object', 0, '', '', '产品详情', 0, 27, 139, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (143, '418edf852a19682a02deec259c264cca', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 27, 142, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (144, '376a4476b37dd4b074c8af0db90a2c17', 'remark', 'string', 0, '-', '小米', '备注', 0, 27, 142, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (145, '77605c8e1debfe3e3fa924c23a10921c', 'products2', 'array', 0, '', '', '产品详情2', 0, 27, 132, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 4, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (146, '9ceea0a4180f9f2a3ef3f1728fa60882', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 27, 145, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (147, 'ed9061be7a63df541ade512f2235c88e', 'remark', 'string', 0, '-', '小米', '备注', 0, 27, 145, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (148, 'ac5b31efb27c156c5a9ffc9974fa86b6', 'productDetailVO', 'object', 0, '', '', '产品详情', 0, 27, 145, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (149, 'e590f30b98f1a3df9eb9196a170dc7f2', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 27, 148, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (150, 'a329de67e21b67540195f06224e30d3d', 'remark', 'string', 0, '-', '小米', '备注', 0, 27, 148, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (151, '7ed2b828ed63209954ef76b74fbdd15d', 'products3', 'array', 0, '', '', '产品详情3', 0, 27, 132, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 5, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (152, '335aa1d39325afcf300d3447d6d550d9', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 27, 151, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (153, '912a57df158ee9fb98fa8cb0aee7025e', 'remark', 'string', 0, '-', '小米', '备注', 0, 27, 151, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (154, '1aba8c51efdb50af0a69dad5b8b676ca', 'productDetailVO', 'object', 0, '', '', '产品详情', 0, 27, 151, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (155, '4dd4f1c544ac437ed4119db5050dcda0', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 27, 154, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (156, 'f8712c097fa1d7555220161602e14ea6', 'remark', 'string', 0, '-', '小米', '备注', 0, 27, 154, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (157, '783167554ab8b18505c1cd357a1d735c', 'productArr', 'array', 0, '', '', '产品详情arr', 0, 27, 132, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 6, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (158, '06d10c6b7f5eb1bbc6997063969dfe54', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 27, 157, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (159, 'e51e5b0705a70b9f284ae222349d2845', 'remark', 'string', 0, '-', '小米', '备注', 0, 27, 157, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (160, '361db781672ecbbb9aee554536d6167f', 'productDetailVO', 'object', 0, '', '', '产品详情', 0, 27, 157, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (161, '5eaed8547bd3e93341bba30c9943f168', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 27, 160, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (162, '87d7638b15d71216e4231a2e60679af0', 'remark', 'string', 0, '-', '小米', '备注', 0, 27, 160, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (163, 'cb13d1285b6be3f1b3f67decd3013d2a', 'productNo', 'integer', 1, '-', '11', '产品id', 0, 28, 0, 0, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (164, 'eb54f27393dad80ad9f6fcf1ca3f345a', 'token', 'String', 1, '-', 'asdfe', 'token', 0, 28, 0, 1, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (165, '9fbdb578a090adef2742b305e673a8ec', 'qid', 'string', 1, '-', 'xx', '查询id', 0, 28, 0, 5, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (166, 'a9fc89056f306181cd8b0dd541a3fcbb', 'file', 'file', 1, '-', '', '文件', 0, 28, 0, 2, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (167, '800accd0038c97f428c5259de89c5c5f', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 28, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (168, '89527b4d92f55c290d87cbc843b32f2b', 'data', 'object', 0, '-', '', '数据', 0, 28, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (169, '29ddea9191d77a38facf5ad51e743e7d', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 29, 0, 2, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (170, '05a8322c9a6c2a161b3a3f967aedc851', 'remark', 'string', 0, '-', '订单', '备注', 0, 29, 0, 2, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (171, 'a2ef677b2a0f9de1049066b22789fc82', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 29, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (172, '17b3538b64573f55d4f2dd3f7d9e340b', 'data', 'object', 0, '', '', '数据', 0, 29, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (173, '2607d8f643981968c0181b2bd034bb64', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 29, 172, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (174, '9f48f962adf03de7547311670345368e', 'remark', 'string', 0, '-', '订单', '备注', 0, 29, 172, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (175, '635f25e5d792caaf985ab84537200f4d', 'orderNo', 'String', 1, '-', 'xxx', '订单id', 0, 30, 0, 0, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (176, '7a658cf66f3fe5d6c956ab9db0dcba00', 'orderNo', 'String', 0, '-', 'xxx', '订单id', 0, 30, 0, 5, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (177, '09565b835b400eb7ff300eaca52dcb7f', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 30, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (178, '9a47544fd3e0d1d18550658e81601e4e', 'data', 'object', 0, '', '', '数据', 0, 30, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (179, '12270736d940c7a6b12d9100fc04dccd', 'userId', 'number', 0, '-', '123', '用户id', 0, 30, 178, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (180, '1d00eba251f5f620901c87d4ca1432c6', 'orderNo', 'string', 0, '-', 'xxx', '订单号', 0, 30, 178, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (181, '9dc25c45377fc5a9414d53162694c47b', 'payTime', 'date', 0, '-', '2021-05-24 19:59:51', '订单id', 0, 30, 178,
        3, 1, 1, 2, 'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (182, 'ffd132900589dcae6503c424943e9e4c', 'orderNo', 'String', 0, '-', 'xxx', '订单id', 0, 31, 0, 5, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (183, 'ba80f33728800383642d5e2f681c008c', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 31, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (184, '8be75c2c683e4cad7b094f42e5f5f9ce', 'data', 'object', 0, '', '', '数据', 0, 31, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (185, '8e7cb6f00fd77e4b433517aeef7962b4', 'userId', 'number', 0, '-', '123', '用户id', 0, 31, 184, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (186, '94d8979c238fde4e19413ba5f982e44a', 'orderNo', 'string', 0, '-', 'xxx', '订单号', 0, 31, 184, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (187, '7b95ebe4a4e3058f9b7b54beb27c11ee', 'payTime', 'date', 0, '-', '2021-05-24 19:59:51', '订单id', 0, 31, 184,
        3, 1, 1, 2, 'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (188, '9013ae7e090a1d78a88a3add789a77f5', 'productNo', 'Integer', 1, '-', '11', '产品id', 0, 32, 0, 0, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (189, '194fee94586186f23954a223cb7f3be7', 'token', 'String', 1, '-', 'asdfe', 'token', 0, 32, 0, 1, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (190, '45417aba02cd8f62b95310b1977ed9c5', 'qid', 'String', 0, '-', 'xx', '查询id', 0, 32, 0, 5, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (191, '4e8ee7ee5db785e025df5da6a4be52d2', 'id', 'integer', 0, '-', '123', 'id', 0, 32, 0, 2, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (192, '1e98f88ae58383c9c38c903021d81ed1', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 32, 0, 2, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (193, '99a8616ca96b3764796d25982a6410c6', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 32, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (194, 'b7bdde2bd2de256e47cbca48ff541ca0', 'data', 'object', 0, '', '', '数据', 0, 32, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (195, '9e86472c86bf53f791f12eda4ef885bf', 'orderNo', 'string', 0, '-', 'aaaa', '订单id', 0, 32, 194, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (196, '4360655b9cd479ffa3ee7e3c3e9effd2', 'remark', 'string', 0, '-', '订单', '备注', 0, 32, 194, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (197, '51d099a09028b5ffa86683f90a3e893c', 'orderDetail', 'object', 0, '', '', '订单详情', 0, 32, 194, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 2, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (198, '2e4421daada6bb561ea9d4ce17293176', 'userId', 'number', 0, '-', '123', '用户id', 0, 32, 197, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (199, 'd8154aa3347a09292ac7276973f1a84c', 'orderNo', 'string', 0, '-', 'xxx', '订单号', 0, 32, 197, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (200, 'a022cbc10e9807d285ca0f81b5a04326', 'payTime', 'date', 0, '-', '2021-05-24 19:59:51', '订单id', 0, 32, 197,
        3, 1, 1, 2, 'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (201, 'f5cb61d2604178f894e45594a425ce97', 'products', 'array', 0, '', '', '产品详情', 0, 32, 194, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 3, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (202, '8b2b379b755f0e9e776331c26cc629c1', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 32, 201, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (203, 'a80b6d57b8ebd715e591fb3495ccc1ff', 'remark', 'string', 0, '-', '小米', '备注', 0, 32, 201, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (204, '392b4c1999136e82af3f8df461200fb2', 'productDetailVO', 'object', 0, '', '', '产品详情', 0, 32, 201, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (205, '87fdd7c17f5ab1f36dedb605cb8bcfe2', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 32, 204, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (206, 'a4e84a92dffb419991412907087fde65', 'remark', 'string', 0, '-', '小米', '备注', 0, 32, 204, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (207, 'fb4f0382a10404bb580496754f9daa02', 'products2', 'array', 0, '', '', '产品详情2', 0, 32, 194, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 4, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (208, 'a9a1ffc1c291254126313236b89f2e73', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 32, 207, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (209, '08325c86d943776d490446808c8856fa', 'remark', 'string', 0, '-', '小米', '备注', 0, 32, 207, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (210, '959abba7ee8c1591773f029ed156276e', 'productDetailVO', 'object', 0, '', '', '产品详情', 0, 32, 207, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (211, '3eff17b737d7ec788d832c2386bcdaf2', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 32, 210, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (212, 'da91f958dd723257785a8e4f239d7512', 'remark', 'string', 0, '-', '小米', '备注', 0, 32, 210, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (213, 'cc77b4577154bb7a3d11be3c9d40d9c4', 'products3', 'array', 0, '', '', '产品详情3', 0, 32, 194, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 5, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (214, '50f92b6dff998eb1ea6f04eb2e049d2a', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 32, 213, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (215, 'ec1d74c13715b6850755e1ca09242013', 'remark', 'string', 0, '-', '小米', '备注', 0, 32, 213, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (216, '171fd4c155b34ff25b3d5dd01624edfa', 'productDetailVO', 'object', 0, '', '', '产品详情', 0, 32, 213, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (217, '3e4959ab4fddddf8dca2bd6f64829fee', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 32, 216, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (218, '07732a51890afd0f7fa49fdad82490e2', 'remark', 'string', 0, '-', '小米', '备注', 0, 32, 216, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (219, 'e98334be70e2db4010aa2f9acd4446c5', 'productArr', 'array', 0, '', '', '产品详情arr', 0, 32, 194, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 6, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (220, 'f2d59b634f94eebb1d1d3e6004e6e7f7', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 32, 219, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (221, '548d553e2ac6ce7d4371fa00e95fd165', 'remark', 'string', 0, '-', '小米', '备注', 0, 32, 219, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (222, 'ecd05b58c4a684520fc558513aa845a3', 'productDetailVO', 'object', 0, '', '', '产品详情', 0, 32, 219, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (223, '0bb0d90ab68cb1f29481ae97f85a84ab', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 32, 222, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (224, '46c293af244b047b340bdb4ef9710a3a', 'remark', 'string', 0, '-', '小米', '备注', 0, 32, 222, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (225, 'ffa37f95b949f54b48d68e5737860d33', 'productNo', 'Integer', 1, '-', '', '', 0, 33, 0, 0, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (226, 'e512fefcd6798cca4a6c61f3387b1828', 'token', 'String', 1, '-', '', '', 0, 33, 0, 1, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (227, 'cb997b29362e102257401aeb1ce1736d', 'qid', 'String', 0, '-', '', '', 0, 33, 0, 5, 1, 1, 2, 'Jim', 2, 'Jim',
        0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (228, '470811a0ef439cff787fefa1928f1953', 'productNo', 'Integer', 0, '-', '', '', 0, 33, 0, 5, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (229, '4ac70e1396bbca9a3135f2f261bb40b3', 'token', 'String', 0, '-', '', '', 0, 33, 0, 5, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (230, '6eaa61d80915cd5e39663663e3c46cf8', 'id', 'integer', 1, '-', '1', '查询id', 0, 33, 0, 5, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (231, '1a8643766058a62d720a4f31958446c3', 'orderNo', 'integer', 0, '-', 'xxx', '订单id', 0, 33, 0, 5, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (232, '1fd1770abb5c6dbaec548c4ce64a4749', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 33, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (233, 'd1b9be6652142432b2b00c3dd210eecc', 'data', 'array', 0, '', '', '数据', 0, 33, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (234, '2a7093b3e7a9b0a05d9bf4e79dc4a206', 'userId', 'number', 0, '-', '123', '用户id', 0, 33, 233, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (235, '1cb84c7c0ed96d582d3a9d9116dee89e', 'orderNo', 'string', 0, '-', 'xxx', '订单号', 0, 33, 233, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (236, 'bffa32e265bb443213a91ef3ada21305', 'payTime', 'date', 0, '-', '2021-05-24 19:59:51', '订单id', 0, 33, 233,
        3, 1, 1, 2, 'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (237, '4bad03085b444504c796679865ca8108', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 35, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (238, 'cfef20e6813836873cf506952879c382', 'data', 'object', 0, '', '', '数据', 0, 35, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (239, 'db18308c17fbf630b57d0303a7a9af03', 'id', 'integer', 0, '-', '', '', 0, 35, 238, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (240, '126f895f424fe90eefc86d3b9a0e8561', 'name', 'string', 0, '-', '', '名称', 0, 35, 238, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (241, 'f4413905452fe4821eeb52872604f435', 'parentId', 'integer', 0, '-', '', '父id', 0, 35, 238, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (242, '5d622869a4ee2b93129b991904662273', 'children', 'array', 0, '', '', '子节点', 0, 35, 238, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (243, '404c22450e9a8d80f55964d6acd1f9bb', 'id', 'integer', 0, '-', '', '', 0, 35, 242, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (244, '253240af265774bbf2f25bc84e264cad', 'name', 'string', 0, '-', '', '名称', 0, 35, 242, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (245, '470885a96535f07de1af8c682b3119d5', 'parentId', 'integer', 0, '-', '', '父id', 0, 35, 242, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (246, '25c8203117d1147a8694fc87a21bdd58', 'children', 'array', 0, '-', '', '子节点', 0, 35, 242, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (247, '748c4ab34e286c9f56a42820772f26b8', 'productNo', 'Integer', 1, '-', '123', '产品id', 0, 36, 0, 0, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (248, '339733d17bd4a7bd0ef50e9c9195b559', 'productNo', 'Integer', 0, '-', '123', '产品id', 0, 36, 0, 5, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (249, '036bd027d059a5df1a68da8564a2fbe3', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 36, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (250, '4f990edab98fe3a3f9a99cc89350a5ac', 'remark', 'string', 0, '-', '小米', '备注', 0, 36, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (251, '894ae45061adcdd417619fb633575b4b', 'productDetailVO', 'object', 0, '', '', '产品详情', 0, 36, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (252, 'fdd2db7c376976b31cfff04798d7a52f', 'productNo', 'string', 0, '-', 'aaaa', '产品id', 0, 36, 251, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (253, '29883c730cf9d36bba9758d5bb52fa3e', 'remark', 'string', 0, '-', '小米', '备注', 0, 36, 251, 3, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (254, 'b04de9854965a39a2f3268e3b9d4c0d1', '100001', 'String', 0, '-', '', 'id错误', 0, 36, 0, 4, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (255, '788b90ef067d5c8f977fcf98606fc073', '100002', 'String', 0, '-', '', '错误描述2', 0, 36, 0, 4, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (256, '32bf6eb90790a489eb16c6ead71549af', '100003', 'String', 0, '-', '', '错误描述3', 0, 36, 0, 4, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (257, '20fa7cf58fd296569d59ccdfe2708177', 'file', 'file', 1, '-', '', '产品模板文件', 0, 37, 0, 2, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (258, 'f7b2209cabe7d77128a20f3feab79e5a', 'productNo', 'String', 1, '-', '111', '产品id', 0, 37, 0, 2, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (259, 'ed4a68feeca8bbc3116be7d3c02238f2', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 37, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (260, '993aac43e5e977be867b163404057717', 'data', 'object', 0, '-', '', '数据', 0, 37, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (261, 'fb948d90f6987762ba09bd4b59ad11ca', 'file', 'file', 0, '-', '', '产品模板文件', 0, 38, 0, 2, 1, 1, 2, 'Jim', 2,
        'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (262, '8b5e84f261c5707090f05806a7d10982', 'files', 'file[]', 0, '-', '', '产品模板文件Array', 0, 38, 0, 2, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (263, '4c96dba27235f710168a893889ab7874', 'fileList', 'file[]', 0, '-', '', '产品模板文件List', 0, 38, 0, 2, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (264, '3d8d331903744306c6d76a0c150f515b', 'productNo', 'string', 1, '-', '111', '产品id', 0, 38, 0, 2, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (265, '3b3fb4ae48177d3d7c76e9319393635a', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 38, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (266, '82dd57c7dee4618f97068bf271cdb2f8', 'data', 'object', 0, '-', '', '数据', 0, 38, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (267, '166e20da9e17c235d9e9e25c25f43640', 'files', 'file[]', 1, '-', '', '产品模板文件', 0, 39, 0, 2, 1, 1, 2, 'Jim',
        2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (268, '39a5f028ec63030df158e4aeff9da4ed', 'productNo', 'String', 1, '-', '111', '产品id', 0, 39, 0, 2, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (269, 'db930770f6a9ec313407f8f4aa7e0448', 'code', 'integer', 0, '-', '0', '状态码，0：成功', 0, 39, 0, 3, 1, 1, 2,
        'Jim', 2, 'Jim', 0, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');
INSERT INTO `doc_param`
VALUES (270, 'df5ac720266566ed4e6e66df96330af8', 'data', 'object', 0, '-', '', '数据', 0, 39, 0, 3, 1, 1, 2, 'Jim', 2,
        'Jim', 1, 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');

-- ----------------------------
-- Table structure for enum_info
-- ----------------------------
DROP TABLE IF EXISTS `enum_info`;
CREATE TABLE `enum_info`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `data_id`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '唯一id，md5(module_id:name)',
    `name`         varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '枚举名称',
    `description`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '枚举说明',
    `module_id`    bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'module.id',
    `is_deleted`   tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`   datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX          `idx_moduleid` (`module_id`) USING BTREE,
    INDEX          `idx_dataid` (`data_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '枚举信息'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of enum_info
-- ----------------------------
INSERT INTO `enum_info`
VALUES (1, '0b28cfdbc7f250d85614eacda8248eb4', '性别', '', 2, 0, '2020-12-16 13:57:09', '2020-12-16 13:57:09');

-- ----------------------------
-- Table structure for enum_item
-- ----------------------------
DROP TABLE IF EXISTS `enum_item`;
CREATE TABLE `enum_item`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `enum_id`      bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'enum_info.id',
    `name`         varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '名称，字面值',
    `type`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '类型',
    `value`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '枚举值',
    `description`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '枚举描述',
    `order_index`  int(11) NOT NULL DEFAULT 0 COMMENT '排序索引',
    `is_deleted`   tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`   datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX          `idx_enumid` (`enum_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '枚举详情'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of enum_item
-- ----------------------------

-- ----------------------------
-- Table structure for mock_config
-- ----------------------------
DROP TABLE IF EXISTS `mock_config`;
CREATE TABLE `mock_config`
(
    `id`                bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`              varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '名称',
    `data_id`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT 'md5(path+query+body)',
    `path`              varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    `ip`                varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '过滤ip',
    `request_data`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求参数',
    `request_data_type` tinyint(4) NOT NULL DEFAULT 0 COMMENT '参数类型，0：KV形式，1：json形式',
    `http_status`       int(11) NOT NULL DEFAULT 200 COMMENT 'http状态',
    `delay_mills`       int(11) NOT NULL DEFAULT 0 COMMENT '延迟时间，单位毫秒',
    `result_type`       tinyint(4) NOT NULL DEFAULT 0 COMMENT '返回类型，0：自定义内容，1：脚本内容',
    `response_headers`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '响应header，数组结构',
    `response_body`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '响应结果',
    `mock_script`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'mock脚本',
    `mock_result`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'mock结果',
    `doc_id`            bigint(20) NOT NULL DEFAULT 0 COMMENT '文档id',
    `remark`            varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
    `creator_id`        bigint(20) NOT NULL DEFAULT 0 COMMENT '创建人id',
    `creator_name`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人姓名',
    `modifier_id`       bigint(20) NOT NULL DEFAULT 0 COMMENT '修改人id',
    `modifier_name`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
    `is_deleted`        tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`        datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`      datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX               `idx_docid` (`doc_id`) USING BTREE,
    INDEX               `idx_dataid` (`data_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'mock配置'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mock_config
-- ----------------------------

-- ----------------------------
-- Table structure for module
-- ----------------------------
DROP TABLE IF EXISTS `module`;
CREATE TABLE `module`
(
    `id`                  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`                varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '模块名称',
    `project_id`          bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'project.id',
    `type`                tinyint(4) NOT NULL DEFAULT 0 COMMENT '模块类型，0：自定义添加，1：swagger导入，2：postman导入',
    `import_url`          varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '导入url',
    `basic_auth_username` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'basic认证用户名',
    `basic_auth_password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'basic认证密码',
    `token`               varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开放接口调用token',
    `create_mode`         tinyint(4) NOT NULL DEFAULT 0 COMMENT '新增操作方式，0：人工操作，1：开放平台推送',
    `modify_mode`         tinyint(4) NOT NULL DEFAULT 0 COMMENT '修改操作方式，0：人工操作，1：开放平台推送',
    `creator_id`          bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `modifier_id`         bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `order_index`         int(11) NOT NULL DEFAULT 0 COMMENT '排序索引',
    `is_deleted`          tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`          datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`        datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_token` (`token`) USING BTREE,
    INDEX                 `idx_projectid` (`project_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '项目模块'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of module
-- ----------------------------
INSERT INTO `module`
VALUES (1, '故事API', 1, 1, 'http://localhost:2222/v2/api-docs', '', '', 'c16931fa6590483fb7a4e85340fcbfef', 0, 0, 3, 3,
        0, 0, '2020-12-15 10:01:48', '2020-12-15 10:01:48');
INSERT INTO `module`
VALUES (2, '用户模块', 2, 0, '', '', '', 'b215b7010db5451e98305152a5fa2ddf', 0, 0, 12, 12, 0, 0, '2020-12-15 10:14:55',
        '2020-12-15 10:14:55');
INSERT INTO `module`
VALUES (3, 'swagger-push', 1, 0, '', '', '', '931167d9347e4aec9409f2b275437431', 0, 0, 1, 1, 0, 0,
        '2021-06-02 10:26:27', '2021-06-02 10:26:27');

-- ----------------------------
-- Table structure for module_config
-- ----------------------------
DROP TABLE IF EXISTS `module_config`;
CREATE TABLE `module_config`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `module_id`    bigint(11) UNSIGNED NOT NULL DEFAULT 0,
    `type`         tinyint(4) NOT NULL DEFAULT 0 COMMENT '配置类型，1：全局header',
    `config_key`   varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置key',
    `config_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置值',
    `extend_id`    bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `description`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '描述',
    `is_deleted`   tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`   datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX          `idx_moduleid_type` (`module_id`, `type`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '系统配置'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of module_config
-- ----------------------------
INSERT INTO `module_config`
VALUES (1, 1, 0, 'debughost_1', 'http://10.1.30.165:2222', 0, '', 0, '2020-12-15 10:01:48', '2021-01-22 11:51:05');
INSERT INTO `module_config`
VALUES (2, 2, 0, 'debughost_2', 'http://www.aaa.com', 0, '', 0, '2020-12-16 15:41:14', '2020-12-16 15:41:14');
INSERT INTO `module_config`
VALUES (3, 3, 2, 'test', 'http://127.0.0.1:8088', 0, '', 0, '2021-06-08 13:54:23', '2021-06-08 13:54:23');

-- ----------------------------
-- Table structure for module_environment
-- ----------------------------
DROP TABLE IF EXISTS `module_environment`;
CREATE TABLE `module_environment`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `module_id`    bigint(20) NOT NULL COMMENT 'module.id',
    `name`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '环境名称',
    `url`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '调试路径',
    `is_public`    tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否公开',
    `is_deleted`   tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`   datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX          `uk_moduleid_name` (`module_id`, `name`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '模块调试环境'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of module_environment
-- ----------------------------
INSERT INTO `module_environment`
VALUES (1, 3, 'test', 'http://127.0.0.1:8088', 0, 0, '2022-01-07 15:54:28', '2022-01-07 15:54:28');

-- ----------------------------
-- Table structure for module_environment_param
-- ----------------------------
DROP TABLE IF EXISTS `module_environment_param`;
CREATE TABLE `module_environment_param`
(
    `id`             bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `data_id`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '唯一id，md5(doc_id:parent_id:style:name)',
    `name`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '字段名称',
    `type`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT 'String' COMMENT '字段类型',
    `required`       tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否必须，1：是，0：否',
    `max_length`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '-' COMMENT '最大长度',
    `example`        varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '示例值',
    `description`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '描述',
    `enum_id`        bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'enum_info.id',
    `environment_id` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'module_environment.id',
    `parent_id`      bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `style`          tinyint(4) NOT NULL DEFAULT 0 COMMENT '0：path, 1：header， 2：请求参数，3：返回参数，4：错误码',
    `create_mode`    tinyint(4) NOT NULL DEFAULT 0 COMMENT '新增操作方式，0：人工操作，1：开放平台推送',
    `modify_mode`    tinyint(4) NOT NULL DEFAULT 0 COMMENT '修改操作方式，0：人工操作，1：开放平台推送',
    `creator_id`     bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `creator_name`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '',
    `modifier_id`    bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `modifier_name`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '',
    `order_index`    int(11) NOT NULL DEFAULT 0 COMMENT '排序索引',
    `is_deleted`     tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`     datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`   datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_dataid` (`data_id`) USING BTREE,
    INDEX            `idx_environmentid` (`environment_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '模块公共参数'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of module_environment_param
-- ----------------------------

-- ----------------------------
-- Table structure for open_user
-- ----------------------------
DROP TABLE IF EXISTS `open_user`;
CREATE TABLE `open_user`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `app_key`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'appKey',
    `secret`       varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'secret',
    `status`       tinyint(4) UNSIGNED NOT NULL DEFAULT 1 COMMENT '1启用，0禁用',
    `applicant`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '',
    `space_id`     bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'space.id',
    `is_deleted`   tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`   datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_app_key` (`app_key`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '开放用户'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of open_user
-- ----------------------------
INSERT INTO `open_user`
VALUES (1, '20201215788408948171472896', 'WeNAdSucgduD$M?*83?dJR&IvgU~Re75', 1, '张三', 0, 0, '2020-12-15 14:07:21',
        '2021-01-25 09:59:10');
INSERT INTO `open_user`
VALUES (2, '20201216788835306945118208', 'W.ZyGMOB9Q0UqujVxnfi@.I#V&tUUYZR', 1, '张三', 9, 0, '2020-12-16 18:21:33',
        '2021-01-25 09:59:10');
INSERT INTO `open_user`
VALUES (3, '20201216788835536872669184', 'Bq.XRN!S0$t8!UYpWgSOl7oHlY#XeenJ', 1, '张三', 9, 0, '2020-12-16 18:22:28',
        '2021-01-25 09:59:11');

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project`
(
    `id`            bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '项目名称',
    `description`   varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '项目描述',
    `space_id`      bigint(20) NOT NULL DEFAULT 0 COMMENT '所属空间，space.id',
    `is_private`    tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否私有项目，1：是，0：否',
    `creator_id`    bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建者userid',
    `creator_name`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '',
    `modifier_id`   bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `modifier_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '',
    `order_index`   int(11) NOT NULL DEFAULT 0 COMMENT '排序索引',
    `is_deleted`    tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`    datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`  datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX           `uk_name` (`creator_id`, `name`) USING BTREE,
    INDEX           `idx_spaceid` (`space_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '项目表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project
-- ----------------------------
INSERT INTO `project`
VALUES (1, '商城项目', '商城项目', 9, 0, 3, '商城项目admin', 1, '超级管理员', 0, 0, '2020-12-15 09:56:39', '2021-01-25 08:31:33');
INSERT INTO `project`
VALUES (2, '后台项目', '后台项目', 11, 1, 11, '后台项目负责人', 11, '后台项目负责人', 0, 0, '2020-12-15 10:12:37', '2020-12-15 10:12:37');
INSERT INTO `project`
VALUES (3, '测试项目', '这是一个测试项目', 17, 1, 14, '测试A', 14, '测试A', 0, 0, '2021-01-25 09:10:47', '2021-01-25 09:10:47');

-- ----------------------------
-- Table structure for project_user
-- ----------------------------
DROP TABLE IF EXISTS `project_user`;
CREATE TABLE `project_user`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `project_id`   bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'project.id',
    `user_id`      bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'user_info.id',
    `role_code`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '角色，guest：访客，dev：开发者，admin：项目管理员',
    `is_deleted`   tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`   datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_projectid_userid` (`project_id`, `user_id`) USING BTREE,
    INDEX          `idx_userid` (`user_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 10
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '项目用户关系表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project_user
-- ----------------------------
INSERT INTO `project_user`
VALUES (1, 1, 3, 'admin', 0, '2020-12-15 09:56:38', '2021-01-25 08:31:33');
INSERT INTO `project_user`
VALUES (2, 1, 4, 'dev', 0, '2020-12-15 09:58:35', '2021-01-25 08:31:38');
INSERT INTO `project_user`
VALUES (3, 1, 9, 'guest', 1, '2020-12-15 09:58:44', '2020-12-15 09:59:01');
INSERT INTO `project_user`
VALUES (4, 2, 11, 'admin', 0, '2020-12-15 10:12:37', '2020-12-15 10:12:37');
INSERT INTO `project_user`
VALUES (5, 2, 12, 'dev', 0, '2020-12-15 10:12:52', '2020-12-15 10:12:52');
INSERT INTO `project_user`
VALUES (6, 2, 13, 'guest', 0, '2020-12-15 11:35:21', '2020-12-15 11:35:21');
INSERT INTO `project_user`
VALUES (9, 3, 14, 'admin', 0, '2021-01-25 09:10:47', '2021-01-25 09:10:47');

-- ----------------------------
-- Table structure for prop
-- ----------------------------
DROP TABLE IF EXISTS `prop`;
CREATE TABLE `prop`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `ref_id`       bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '关联id',
    `type`         tinyint(4) NOT NULL DEFAULT 0 COMMENT '类型，0：doc_info属性',
    `name`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    `val`          text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `gmt_create`   datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_docid_name` (`ref_id`, `type`, `name`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '属性表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of prop
-- ----------------------------

-- ----------------------------
-- Table structure for share_config
-- ----------------------------
DROP TABLE IF EXISTS `share_config`;
CREATE TABLE `share_config`
(
    `id`            bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `type`          tinyint(4) NOT NULL DEFAULT '0' COMMENT '分享形式，1：公开，2：加密',
    `password`      varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
    `status`        tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态，1：有效，0：无效',
    `module_id`     bigint(20) NOT NULL DEFAULT '0' COMMENT 'module.id',
    `is_all`        tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为全部文档',
    `is_deleted`    tinyint(4) NOT NULL DEFAULT '0',
    `remark`        varchar(128) NOT NULL DEFAULT '' COMMENT '备注',
    `creator_name`  varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `is_show_debug` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否显示调试',
    `gmt_create`    datetime              DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`  datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY             `idx_moduleid` (`module_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='分享配置表';

-- ----------------------------
-- Records of share_config
-- ----------------------------

-- ----------------------------
-- Table structure for share_content
-- ----------------------------
DROP TABLE IF EXISTS `share_content`;
CREATE TABLE `share_content`
(
    `id`              bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `share_config_id` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'share_config.id',
    `doc_id`          bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '文档id',
    `parent_id`       bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '父id',
    `is_share_folder` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否分享整个分类',
    `is_deleted`      tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`      datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`    datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX             `idx_shareconfigid_docid` (`share_config_id`, `doc_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '分享详情'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of share_content
-- ----------------------------

-- ----------------------------
-- Table structure for space
-- ----------------------------
DROP TABLE IF EXISTS `space`;
CREATE TABLE `space`
(
    `id`            bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '空间名称',
    `creator_id`    bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建者userid',
    `creator_name`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    `modifier_id`   bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建者userid',
    `modifier_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    `is_compose`    tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否组合空间',
    `is_deleted`    tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`    datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`  datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 19
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '分组表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of space
-- ----------------------------
INSERT INTO `space`
VALUES (9, '研发一部', 2, '研发一部经理', 2, '研发一部经理', 0, 0, '2020-12-15 09:48:13', '2020-12-15 09:48:13');
INSERT INTO `space`
VALUES (11, '研发二部', 5, '研发二部经理', 5, '研发二部经理', 0, 0, '2020-12-15 10:08:39', '2020-12-15 10:08:39');
INSERT INTO `space`
VALUES (14, '研发三部', 11, '后台项目负责人', 11, '后台项目负责人', 0, 1, '2020-12-16 10:04:13', '2020-12-16 10:04:13');
INSERT INTO `space`
VALUES (16, '研发三部', 11, '后台项目负责人', 11, '后台项目负责人', 0, 1, '2020-12-16 10:06:20', '2020-12-16 10:06:20');
INSERT INTO `space`
VALUES (17, '测试空间', 14, '测试A', 14, '测试A', 0, 0, '2021-01-25 09:10:02', '2021-01-25 09:10:02');
INSERT INTO `space`
VALUES (18, '聚合空间', 1, '超级管理员', 1, '超级管理员', 1, 0, '2021-05-25 18:04:34', '2021-05-25 18:04:34');

-- ----------------------------
-- Table structure for space_user
-- ----------------------------
DROP TABLE IF EXISTS `space_user`;
CREATE TABLE `space_user`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`      bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'user_info.id',
    `space_id`     bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'space.id',
    `role_code`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色，guest：访客，dev：开发者，admin：空间管理员',
    `is_deleted`   tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`   datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_groupid_userid` (`space_id`, `user_id`) USING BTREE,
    INDEX          `idx_userid` (`user_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 25
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '分组用户关系表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of space_user
-- ----------------------------
INSERT INTO `space_user`
VALUES (1, 2, 1, 'admin', 0, '2020-12-15 09:41:16', '2020-12-15 09:41:16');
INSERT INTO `space_user`
VALUES (2, 3, 2, 'admin', 0, '2020-12-15 09:41:58', '2020-12-15 09:41:58');
INSERT INTO `space_user`
VALUES (3, 4, 3, 'admin', 0, '2020-12-15 09:42:12', '2020-12-15 09:42:12');
INSERT INTO `space_user`
VALUES (4, 5, 4, 'admin', 0, '2020-12-15 09:42:29', '2020-12-15 09:42:29');
INSERT INTO `space_user`
VALUES (5, 6, 5, 'admin', 0, '2020-12-15 09:42:47', '2020-12-15 09:42:47');
INSERT INTO `space_user`
VALUES (6, 7, 6, 'admin', 0, '2020-12-15 09:43:00', '2020-12-15 09:43:00');
INSERT INTO `space_user`
VALUES (7, 8, 7, 'admin', 0, '2020-12-15 09:43:18', '2020-12-15 09:43:18');
INSERT INTO `space_user`
VALUES (8, 9, 8, 'admin', 0, '2020-12-15 09:43:32', '2020-12-15 09:43:32');
INSERT INTO `space_user`
VALUES (9, 2, 9, 'admin', 0, '2020-12-15 09:48:12', '2020-12-15 09:48:12');
INSERT INTO `space_user`
VALUES (10, 3, 9, 'dev', 0, '2020-12-15 09:49:18', '2020-12-15 09:49:18');
INSERT INTO `space_user`
VALUES (11, 4, 9, 'dev', 0, '2020-12-15 09:49:18', '2020-12-15 09:49:18');
INSERT INTO `space_user`
VALUES (12, 10, 10, 'admin', 0, '2020-12-15 09:50:00', '2020-12-15 09:50:00');
INSERT INTO `space_user`
VALUES (13, 9, 9, 'guest', 0, '2020-12-15 09:50:28', '2020-12-15 09:50:28');
INSERT INTO `space_user`
VALUES (14, 5, 11, 'admin', 0, '2020-12-15 10:08:38', '2020-12-15 10:08:38');
INSERT INTO `space_user`
VALUES (15, 11, 12, 'admin', 0, '2020-12-15 10:10:17', '2020-12-15 10:10:17');
INSERT INTO `space_user`
VALUES (16, 12, 13, 'admin', 0, '2020-12-15 10:10:31', '2020-12-15 10:10:31');
INSERT INTO `space_user`
VALUES (17, 11, 11, 'dev', 0, '2020-12-15 10:12:00', '2020-12-15 10:12:00');
INSERT INTO `space_user`
VALUES (18, 12, 11, 'dev', 0, '2020-12-15 10:12:00', '2020-12-15 10:12:00');
INSERT INTO `space_user`
VALUES (19, 10, 11, 'guest', 0, '2020-12-15 10:12:08', '2020-12-15 10:12:08');
INSERT INTO `space_user`
VALUES (20, 13, 11, 'guest', 0, '2020-12-15 11:34:59', '2020-12-15 11:34:59');
INSERT INTO `space_user`
VALUES (21, 11, 14, 'admin', 0, '2020-12-16 10:04:13', '2020-12-16 10:04:13');
INSERT INTO `space_user`
VALUES (22, 11, 16, 'admin', 0, '2020-12-16 10:06:19', '2020-12-16 10:06:19');
INSERT INTO `space_user`
VALUES (23, 14, 17, 'admin', 0, '2021-01-25 09:10:01', '2021-01-25 09:10:01');
INSERT INTO `space_user`
VALUES (24, 2, 18, 'admin', 0, '2021-05-25 18:04:33', '2021-05-25 18:04:33');

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `config_key`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '',
    `config_value` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    `remark`       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    `is_deleted`   tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`   datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_configkey` (`config_key`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '系统配置表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config`
VALUES (1, 'torna.version', '1120', '当前内部版本号。不要删除这条记录！！', 0, '2021-05-25 18:03:08', '2022-01-07 15:54:28');

-- ----------------------------
-- Table structure for user_dingtalk_info
-- ----------------------------
DROP TABLE IF EXISTS `user_dingtalk_info`;
CREATE TABLE `user_dingtalk_info`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `nick`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '用户在钉钉上面的昵称',
    `name`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '员工名称。',
    `email`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '员工邮箱。',
    `userid`       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '员工的userid。',
    `unionid`      varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户在当前开放应用所属企业的唯一标识。',
    `openid`       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户在当前开放应用内的唯一标识。',
    `user_info_id` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'user_info.id',
    `gmt_create`   datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_unionid` (`unionid`) USING BTREE,
    INDEX          `idx_openid` (`openid`) USING BTREE,
    INDEX          `idx_userid` (`user_info_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '钉钉开放平台用户'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_dingtalk_info
-- ----------------------------

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`
(
    `id`             bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `username`       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登录账号/邮箱',
    `password`       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登录密码',
    `nickname`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '昵称',
    `is_super_admin` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否是超级管理员',
    `source`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT 'register',
    `email`          varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    `status`         tinyint(4) NOT NULL DEFAULT 1 COMMENT '0：禁用，1：启用，2：重设密码',
    `is_deleted`     tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`     datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`   datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 15
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info`
VALUES (1, 'admin@torna.cn', 'f1e27f8ec06b0ea415583c26457dd111', '超级管理员', 1, 'register', 'admin@torna.cn', 1, 0,
        '2020-12-15 09:04:13', '2021-05-25 18:03:07');
INSERT INTO `user_info`
VALUES (2, 'dev1admin@torna.cn', '1231d3ef9f1a6b3771d19e3ae453b07d', '研发一部经理', 0, 'register', 'dev1admin@torna.cn', 1,
        0, '2020-12-15 09:41:16', '2021-05-25 18:03:07');
INSERT INTO `user_info`
VALUES (3, 'dev1shop_admin@torna.cn', '8429b4b5d6ef210811a25ad6e2e47403', '商城项目admin', 0, 'register',
        'dev1shop_admin@torna.cn', 1, 0, '2020-12-15 09:41:58', '2021-05-25 18:03:07');
INSERT INTO `user_info`
VALUES (4, 'dev1shop_zhangsan@torna.cn', '6903a801b91e1a81247a97bf7cf6b7ee', '张三', 0, 'register',
        'dev1shop_zhangsan@torna.cn', 1, 0, '2020-12-15 09:42:12', '2021-05-25 18:03:07');
INSERT INTO `user_info`
VALUES (5, 'dev2admin@torna.cn', '8a73c745cd35093af0c72b5e97c7f908', '研发二部经理', 0, 'register', 'dev2admin@torna.cn', 1,
        0, '2020-12-15 09:42:30', '2021-05-25 18:03:07');
INSERT INTO `user_info`
VALUES (9, 'dev1guest_wangwu@torna.cn', '997a3b669d4be9b034b23e620fc1c48e', '王五', 0, 'register',
        'dev1guest_wangwu@torna.cn', 1, 0, '2020-12-15 09:43:33', '2021-05-25 18:03:07');
INSERT INTO `user_info`
VALUES (10, 'dev2guest_zhaoliu@torna.cn', 'db4c69808f677df84b76b902203a807e', '赵六', 0, 'register',
        'dev2guest_zhaoliu@torna.cn', 1, 0, '2020-12-15 09:50:01', '2021-05-25 18:03:07');
INSERT INTO `user_info`
VALUES (11, 'dev2back_admin@torna.cn', 'de017957932b285ff6032a4d0c584471', '后台项目负责人', 0, 'register',
        'dev2back_admin@torna.cn', 1, 0, '2020-12-15 10:10:17', '2021-05-25 18:03:07');
INSERT INTO `user_info`
VALUES (12, 'dev2back_lisi@torna.cn', 'cfb8d9b2e8447520fe0df35242566096', '李四', 0, 'register', 'dev2back_lisi@torna.cn',
        1, 0, '2020-12-15 10:10:32', '2021-05-25 18:03:07');
INSERT INTO `user_info`
VALUES (13, 'dev2back_guest@torna.cn', '211b7fb1fce482e3bd312fcdb5a89d7f', '后台访客', 0, 'register',
        'dev2back_guest@torna.cn', 1, 0, '2020-12-15 11:21:07', '2021-05-25 18:03:07');
INSERT INTO `user_info`
VALUES (14, 'test@torna.cn', 'c19b85cecd8787ba8712ff764bf70f81', '测试A', 0, 'register', 'test@torna.cn', 1, 0,
        '2021-01-25 09:08:21', '2021-05-25 18:03:07');

-- ----------------------------
-- Table structure for user_message
-- ----------------------------
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`      bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'user_info.id',
    `message`      varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    `is_read`      tinyint(4) NOT NULL DEFAULT 0,
    `type`         tinyint(4) NOT NULL DEFAULT 0 COMMENT '同user_subscribe.type',
    `source_id`    bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '同user_subscribe.source_id',
    `gmt_create`   datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX          `idx_userid` (`user_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 11
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '站内消息'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_message
-- ----------------------------
INSERT INTO `user_message`
VALUES (3, 2, '超级管理员 修改了文档 获取分类信息', 1, 1, 2, '2021-01-19 13:38:20', '2021-01-19 16:28:54');
INSERT INTO `user_message`
VALUES (4, 2, '超级管理员 更新了【获取分类信息】，备注: 111', 1, 1, 2, '2021-01-19 13:43:42', '2021-01-19 16:30:36');
INSERT INTO `user_message`
VALUES (5, 2, '超级管理员 更新了【获取分类信息】，备注: 3333', 1, 1, 2, '2021-01-19 13:43:45', '2021-01-19 15:27:41');
INSERT INTO `user_message`
VALUES (6, 2, '超级管理员 更新了【获取分类信息】，备注: 本次需改了很多内容，这是一句很长的话', 1, 1, 2, '2021-01-19 13:49:55', '2021-01-19 16:21:17');
INSERT INTO `user_message`
VALUES (7, 2, '超级管理员 更新了【获取分类信息】', 1, 1, 2, '2021-01-19 15:33:41', '2021-01-19 15:49:55');
INSERT INTO `user_message`
VALUES (8, 2, '超级管理员 更新了【获取分类信息】，备注: 1231231', 1, 1, 2, '2021-01-19 15:33:44', '2021-01-19 16:21:29');
INSERT INTO `user_message`
VALUES (9, 2, '超级管理员 更新了【获取分类信息】', 1, 1, 2, '2021-01-19 16:39:35', '2021-01-19 16:48:32');
INSERT INTO `user_message`
VALUES (10, 2, '超级管理员 删除了【获取分类信息】', 1, 1, 2, '2021-01-19 16:44:37', '2021-01-19 16:46:06');

-- ----------------------------
-- Table structure for user_subscribe
-- ----------------------------
DROP TABLE IF EXISTS `user_subscribe`;
CREATE TABLE `user_subscribe`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`      bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'user_info.id',
    `type`         tinyint(4) NOT NULL DEFAULT 0 COMMENT '类型，1：订阅接口，2：订阅项目',
    `source_id`    bigint(20) NOT NULL DEFAULT 0 COMMENT '关联id，当type=1对应doc_info.id；type=2对应project.id',
    `is_deleted`   tinyint(4) NOT NULL DEFAULT 0,
    `gmt_create`   datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_userid_type_sourceid` (`user_id`, `type`, `source_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 9
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户订阅表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_subscribe
-- ----------------------------
INSERT INTO `user_subscribe`
VALUES (1, 1, 1, 16, 0, '2021-01-18 16:33:51', '2021-01-18 16:38:58');
INSERT INTO `user_subscribe`
VALUES (2, 1, 1, 4, 0, '2021-01-18 16:41:09', '2021-01-18 17:18:12');
INSERT INTO `user_subscribe`
VALUES (3, 1, 1, 6, 0, '2021-01-18 17:17:57', '2021-01-18 17:17:57');
INSERT INTO `user_subscribe`
VALUES (4, 1, 1, 5, 0, '2021-01-18 17:17:59', '2021-01-18 17:35:14');
INSERT INTO `user_subscribe`
VALUES (5, 1, 1, 8, 1, '2021-01-18 17:18:04', '2021-01-18 17:21:19');
INSERT INTO `user_subscribe`
VALUES (6, 1, 1, 2, 0, '2021-01-18 17:18:07', '2021-01-18 17:18:07');
INSERT INTO `user_subscribe`
VALUES (7, 1, 1, 3, 0, '2021-01-18 17:18:10', '2021-01-18 17:18:10');
INSERT INTO `user_subscribe`
VALUES (8, 2, 1, 2, 0, '2021-01-19 10:24:11', '2021-01-19 10:24:11');

SET
FOREIGN_KEY_CHECKS = 1;
