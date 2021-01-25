package cn.torna.web.controller.module.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ModuleVO {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 模块名称, 数据库字段：name */
    private String name;

    /** project.id, 数据库字段：project_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

    /** 模块类型，0：自定义添加，1：swagger导入，2：postman导入, 数据库字段：type */
    private Byte type;

    private String importUrl;
}
