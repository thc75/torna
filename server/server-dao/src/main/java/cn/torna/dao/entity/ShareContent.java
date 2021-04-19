package cn.torna.dao.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 表名：share_content
 * 备注：分享详情
 *
 * @author tanghc
 */
@Table(name = "share_content")
@Data
public class ShareContent {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** share_config.id, 数据库字段：share_config_id */
    private Long shareConfigId;

    /** 文档id, 数据库字段：doc_id */
    private Long docId;

    /** 父id, 数据库字段：parent_id */
    private Long parentId;

    /** 是否分享整个分类, 数据库字段：is_share_folder */
    private Byte isShareFolder;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}