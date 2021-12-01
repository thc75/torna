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

    /** doc_id */
    private Long docId;

    /** 文件名称 */
    private String filename;

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