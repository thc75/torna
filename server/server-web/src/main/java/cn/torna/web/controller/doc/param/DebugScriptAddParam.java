package cn.torna.web.controller.doc.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @author tanghc
 */
@Data
public class DebugScriptAddParam {

    /** 名称, 数据库字段：name */
    private String name;

    /** 描述, 数据库字段：description */
    private String description;

    /** 脚本内容, 数据库字段：content */
    private String content;

    /** 类型，0：pre，1：after, 数据库字段：type */
    private Byte type;

    /** 作用域，0：当前文档，1：当前应用，2：当前项目, 数据库字段：scope */
    private Byte scope;

    private Byte enabled;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long docId;

}
