package cn.torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：doc_snapshoot
 * 备注：文档快照
 *
 * @author tanghc
 */
@Table(name = "doc_snapshot")
@Data
public class DocSnapshot {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** doc_info.id, 数据库字段：doc_id */
    private Long docId;

    /** 修改人, 数据库字段：modifier_name */
    private String modifierName;

    /** 修改时间, 数据库字段：modifier_time */
    private Date modifierTime;

    /** 修改内容, 数据库字段：content */
    private String content;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}