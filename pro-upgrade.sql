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
  DEFAULT CHARSET = utf8mb4 COMMENT = '文档快照';


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
  DEFAULT CHARSET = utf8mb4;