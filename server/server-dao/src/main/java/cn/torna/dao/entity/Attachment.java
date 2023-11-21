package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表名：attachment 备注：附件表
 * 
 * @author tanghc
 */
@Table(name = "attachment", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class Attachment {

    /**  */
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
    private LocalDateTime gmtCreate;

    /**  */
    private LocalDateTime gmtModified;


}