-- pro 升级脚本

CREATE TABLE doc_snapshot
(
    id            BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    doc_id        BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT 'doc_info.id',
    modifier_name VARCHAR(64)         NOT NULL DEFAULT '' COMMENT '修改人',
    modifier_time datetime            NOT NULL COMMENT '修改时间',
    content       text COMMENT '修改内容',
    gmt_create    datetime                     DEFAULT CURRENT_TIMESTAMP,
    gmt_modified  datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_docid (doc_id) USING BTREE
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4 COMMENT = '文档快照表';


CREATE TABLE debug_script
(
    id           BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    NAME         VARCHAR(64)         NOT NULL DEFAULT '' COMMENT '名称',
    description  VARCHAR(128)        NOT NULL DEFAULT '' COMMENT '描述',
    content      text COMMENT '脚本内容',
    type         TINYINT(4)          NOT NULL DEFAULT '0' COMMENT '类型，0：pre，1：after',
    scope        TINYINT(4)          NOT NULL DEFAULT '0' COMMENT '作用域，0：当前文档，1：当前模块，2：当前项目',
    ref_id       BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT '关联id',
    creator_name VARCHAR(64)         NOT NULL DEFAULT '' COMMENT '创建人昵称',
    is_deleted   TINYINT(4)          NOT NULL DEFAULT '0',
    gmt_create   datetime                     DEFAULT CURRENT_TIMESTAMP,
    gmt_modified datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_refid (ref_id) USING BTREE
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4 COMMENT = '调试脚本表';

CREATE TABLE `doc_comment`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `doc_id`       bigint(20)          NOT NULL COMMENT 'doc_info.id',
    `user_id`      bigint(20)          NOT NULL COMMENT '评论人',
    `nickname`     varchar(64)         NOT NULL COMMENT '评论人昵称',
    `content`      varchar(255)        NOT NULL DEFAULT '' COMMENT '评论内容',
    `reply_id`     bigint(20)          NOT NULL DEFAULT '0' COMMENT '回复id，即：parent_id',
    `is_deleted`   tinyint(4)          NOT NULL DEFAULT '0',
    `gmt_create`   datetime                     DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_docid` (`doc_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='文档评论表';

CREATE TABLE `attachment`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `source_type`  tinyint(4)          NOT NULL DEFAULT '0' COMMENT '来源，0：文档附件，1：评论附件',
    `ref_id`       bigint(20)          NOT NULL COMMENT '文档附件时存doc_id,评论附件存comment_id',
    `filename`     varchar(64)         NOT NULL COMMENT '文件名称',
    `suffix`       varchar(8)          NOT NULL DEFAULT '' COMMENT '文件后缀',
    `save_dir`     varchar(128)                 DEFAULT NULL COMMENT '本地保存路径',
    `file_id`      varchar(64)         NOT NULL COMMENT '文件唯一id',
    `user_id`      bigint(20)          NOT NULL COMMENT '上传人',
    `module_id`    bigint(20)          NOT NULL COMMENT 'module.id',
    `is_deleted`   tinyint(4)          NOT NULL DEFAULT '0',
    `gmt_create`   datetime                     DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_fileid` (`file_id`) USING BTREE,
    KEY `idx_sourcetype_refid` (`source_type`, `ref_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='附件表';