package cn.torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：attachment 备注：附件表
 * 
 * @author tanghc
 */
@Table(name = "attachment")
@Data
public class Attachment {

    /**  */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 来源，0：文档附件，1：评论附件 */
    private Byte sourceType;

    /** 文档附件时存doc_id,评论附件存comment_id */
    private Long refId;

    /** 文件名称 */
    private String filename;

    /** 文件后缀 */
    private String suffix;

    /** 本地保存路径 */
    private String saveDir;

    /** 文件唯一id */
    private String fileId;

    /** 上传人 */
    private Long userId;

    /** module.id */
    private Long moduleId;

    /**  */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  */
    private Date gmtCreate;

    /**  */
    private Date gmtModified;


}