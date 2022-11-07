package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：doc_param
 * 备注：文档参数
 *
 * @author tanghc
 */
@Table(name = "doc_param", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class DocParam {

    /**  数据库字段：id */
    private Long id;

    /** 唯一id，md5(doc_id:parent_id:style:name), 数据库字段：data_id */
    private String dataId;

    /** 字段名称, 数据库字段：name */
    private String name;

    /** 字段类型, 数据库字段：type */
    private String type;

    /** 是否必须，1：是，0：否, 数据库字段：required */
    private Byte required;

    /** 最大长度, 数据库字段：max_length */
    private String maxLength;

    /** 示例值, 数据库字段：example */
    private String example;

    /** 描述, 数据库字段：description */
    private String description;

    /** enum_info.id, 数据库字段：enum_id */
    private Long enumId;

    /** doc_info.id, 数据库字段：doc_id */
    private Long docId;

    /**  数据库字段：parent_id */
    private Long parentId;

    /** 0：path, 1：header， 2：请求参数，3：返回参数，4：错误码, 数据库字段：style */
    private Byte style;

    /** 新增操作方式，0：人工操作，1：开放平台推送, 数据库字段：create_mode */
    private Byte createMode;

    /** 修改操作方式，0：人工操作，1：开放平台推送, 数据库字段：modify_mode */
    private Byte modifyMode;

    /**  数据库字段：creator_id */
    private Long creatorId;

    /**  数据库字段：creator_name */
    private String creatorName;

    /**  数据库字段：modifier_id */
    private Long modifierId;

    /**  数据库字段：modifier_name */
    private String modifierName;

    /** 排序索引, 数据库字段：order_index */
    private Integer orderIndex;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}