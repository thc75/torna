package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表名：doc_snapshoot
 * 备注：文档快照
 *
 * @author tanghc
 */
@Table(name = "doc_snapshot", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class DocSnapshot {

    /**  数据库字段：id */
    private Long id;

    /** doc_info.id, 数据库字段：doc_id */
    private Long docId;

    /** 唯一id，接口规则：md5(module_id:parent_id:url:http_method)。分类规则：md5(module_id:parent_id:name), 数据库字段：doc_key */
    private String docKey;

    /** 文档md5 */
    private String md5;

    /** 修改人, 数据库字段：modifier_name */
    private String modifierName;

    /** 修改时间, 数据库字段：modifier_time */
    private LocalDateTime modifierTime;

    /** 修改内容, 数据库字段：content */
    private String content;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}
