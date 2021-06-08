package cn.torna.dao.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 表名：prop
 * 备注：属性表
 *
 * @author tanghc
 */
@Table(name = "prop")
@Data
public class Prop {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}