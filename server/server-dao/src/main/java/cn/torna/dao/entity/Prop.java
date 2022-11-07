package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：prop
 * 备注：属性表
 *
 * @author tanghc
 */
@Table(name = "prop", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class Prop {

    /**  数据库字段：id */
    private Long id;

    /** 关联id, 数据库字段：ref_id */
    private Long refId;

    /** 类型，0：doc_info属性, 数据库字段：type */
    private Byte type;

    /**  数据库字段：name */
    private String name;

    /**  数据库字段：val */
    private String val;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}