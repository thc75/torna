package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：enum_info
 * 备注：枚举信息
 *
 * @author tanghc
 */
@Table(name = "enum_info", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class EnumInfo {

    /**  数据库字段：id */
    private Long id;

    /** 唯一id，md5(module_id:name), 数据库字段：data_id */
    private String dataId;

    /** 枚举名称, 数据库字段：name */
    private String name;

    /** 枚举说明, 数据库字段：description */
    private String description;

    /** module.id, 数据库字段：module_id */
    private Long moduleId;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}