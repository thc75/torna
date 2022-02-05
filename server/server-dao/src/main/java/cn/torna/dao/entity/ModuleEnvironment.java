package cn.torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：module_environment
 * 备注：模块调试环境
 *
 * @author tanghc
 */
@Table(name = "module_environment")
@Data
public class ModuleEnvironment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 
     * module.id
     */
    private Long moduleId;

    /** 
     * 环境名称
     */
    private String name;

    /** 
     * 调试路径
     */
    private String url;

    /** 
     * 是否公开
     */
    private Byte isPublic;

    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    private Date gmtCreate;

    private Date gmtModified;


}