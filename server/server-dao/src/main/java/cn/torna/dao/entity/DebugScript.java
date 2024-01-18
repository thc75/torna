package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表名：debug_script
 *
 * @author tanghc
 */
@Table(name = "debug_script", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class DebugScript {

    /**  数据库字段：id */
    private Long id;

    /** 名称, 数据库字段：name */
    private String name;

    /** 描述, 数据库字段：description */
    private String description;

    /** 脚本内容, 数据库字段：content */
    private String content;

    /** 类型，0：pre，1：after, 数据库字段：type */
    private Byte type;

    /** 作用域，0：当前文档，1：当前应用，2：当前项目, 数据库字段：scope */
    private Byte scope;

    /** 关联id, 数据库字段：ref_id */
    private Long refId;

    /** 创建人昵称, 数据库字段：creator_name */
    private String creatorName;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /** 是否启用 */
    private Byte enabled;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}
