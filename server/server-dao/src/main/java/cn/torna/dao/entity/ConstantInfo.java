package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表名：error_code_info
 * 备注：错误码
 *
 * @author tanghc
 */
@Table(name = "constant_info", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class ConstantInfo {

    private Long id;


    /**
     * project.id
     */
    private Long projectId;


    /**
     * module.id
     */
    private Long moduleId;


    /**
     * doc_info.id
     */
    private Long docId;


    /**
     * 错误码内容
     */
    private String content;


    private LocalDateTime gmtCreate;


    private LocalDateTime gmtModified;



}