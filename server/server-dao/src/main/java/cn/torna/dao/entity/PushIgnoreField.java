package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表名：push_ignore_field
 * 备注：推送忽略字段
 *
 * @author tanghc
 */
@Table(name = "push_ignore_field", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class PushIgnoreField {

    private Long id;


    /** 
     * module.id
     */
    private Long moduleId;


    /** 
     * doc_info.data_id
     */
    private String dataId;


    /** 
     * doc_info.name
     */
    private String fieldName;


    /** 
     * 字段描述
     */
    private String fieldDescription;


    private LocalDateTime gmtCreate;



}