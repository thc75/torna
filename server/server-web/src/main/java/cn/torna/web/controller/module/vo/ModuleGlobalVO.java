package cn.torna.web.controller.module.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ModuleGlobalVO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

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

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long enumId;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long extendId;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long parentId;

}