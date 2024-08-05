CREATE TABLE `project_release`  (
    `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
    `project_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT 'project.id',
    `release_no` varchar(20) NOT NULL COMMENT '版本号',
    `release_desc` varchar(200) NULL DEFAULT '' COMMENT '版本描述',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 1-有效 0-无效',
    `dingding_webhook` varchar(200) NULL DEFAULT '' COMMENT '钉钉机器人webhook',
    `is_deleted` tinyint NOT NULL DEFAULT 0,
    `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_projectid_releaseno`(`project_id`, `release_no`) USING BTREE
) ENGINE = InnoDB  COMMENT = '项目版本表';


CREATE TABLE `project_release_doc`  (
    `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
    `project_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT 'project.id',
    `release_id` bigint NOT NULL COMMENT 'project_release.id',
    `module_id` bigint NOT NULL COMMENT 'module.id',
    `source_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT 'doc_info.id',
    `is_deleted` tinyint NOT NULL DEFAULT 0,
    `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_projectid_releaseid_sourceid`(`project_id`, `release_id`, `source_id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '项目版本关联文档表';

