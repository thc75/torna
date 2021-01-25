package cn.torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：enum_item
 * 备注：枚举详情
 *
 * @author tanghc
 */
@Table(name = "enum_item")
@Data
public class EnumItem {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** enum_info.id, 数据库字段：enum_id */
    private Long enumId;

    /** 名称，字面值, 数据库字段：name */
    private String name;

    /** 类型, 数据库字段：type */
    private String type;

    /** 枚举值, 数据库字段：value */
    private String value;

    /** 枚举描述, 数据库字段：description */
    private String description;

    /** 排序索引, 数据库字段：order_index */
    private Integer orderIndex;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}