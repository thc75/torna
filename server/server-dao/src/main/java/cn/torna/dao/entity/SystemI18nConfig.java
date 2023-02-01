package cn.torna.dao.entity;

import java.time.LocalDateTime;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;

import lombok.Data;

/**
 * 表名：system_i18n_config
 * 备注：国际化配置
 *
 * @author tanghc
 */
@Table(name = "system_i18n_config", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class SystemI18nConfig {

    /** 
     * 主键id
     */
    private Long id;


    /** 
     * 语言简写，如:zh,en
     */
    private String lang;


    /** 
     * 描述，如：简体中文
     */
    private String description;


    /** 
     * 配置项，properties文件内容
     */
    private String content;


    private LocalDateTime gmtCreate;


    private LocalDateTime gmtModified;



}