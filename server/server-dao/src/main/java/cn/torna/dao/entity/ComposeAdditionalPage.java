package cn.torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p> 表名：compose_additional_page 备注：聚合文档附加页</p>
 *
 * @author tanghc
 * @description
 * @date 2021-07-18 19:15:20
 */
@Table(name = "compose_additional_page")
@Data
public class ComposeAdditionalPage {

    /**  */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** compose_project.id */
    private Long projectId;

    /** 文档标题 */
    private String title;

    /** 文档内容 */
    private String content;

    /** 排序值 */
    private Integer orderIndex;

    /**  */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  */
    private Date gmtCreate;

    /**  */
    private Date gmtModified;


}