package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：share_content
 * 备注：分享详情
 *
 * @author tanghc
 */
@Table(name = "share_content", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class ShareContent {

    /**  数据库字段：id */
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
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}