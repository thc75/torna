package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Column;
import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：project_release
 * 备注：项目版本表
 *
 * @author qiuyu
 */
@Table(name = "project_release", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class ProjectRelease {

    /**  数据库字段：id */
    private Long id;

    /** project.id, 数据库字段：project_id */
    private Long projectId;

    /** 版本号 */
    private String releaseNo;

    /** 版本描述 */
    private String releaseDesc;

    /** 状态 1-有效 0-无效 */
    private Integer status;

    /** 钉钉机器人webhook */
    private String dingdingWebhook;

    /** 企业微信机器人webhook */
    private String weComWebhook;

    /**  数据库字段：is_deleted */
    @Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}