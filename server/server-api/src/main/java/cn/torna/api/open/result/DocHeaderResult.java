package cn.torna.api.open.result;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author tanghc
 */
@Data
public class DocHeaderResult {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @ApiDocField(description = "参数id", example = "asdf", maxLength = "12", dataType = DataType.STRING)
    private Long id;

    /** 字段名称, 数据库字段：name */
    @ApiDocField(description = "参数名", required = true, maxLength = "50", example = "token")
    private String name;

    /** 是否必须，1：是，0：否, 数据库字段：required */
    @ApiDocField(description = "是否必须，1：是，0：否", required = true, example = "1")
    private Byte required;

    /** 示例值, 数据库字段：example */
    @ApiDocField(description = "示例值", maxLength = "100", example = "asdfasdfd")
    private String example;

    /** 描述, 数据库字段：description */
    @ApiDocField(description = "描述", maxLength = "200", example = "请求token")
    private String description;

    /** 创建人 */
    @ApiDocField(description = "创建人", maxLength = "50", example = "jim")
    private String creatorName;

    /** 修改人 */
    @ApiDocField(description = "修改人", maxLength = "50", example = "jim")
    private String modifierName;

    /**  数据库字段：gmt_create */
    @ApiDocField(description = "创建时间")
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    @ApiDocField(description = "最后修改时间")
    private LocalDateTime gmtModified;

}
