package torna.api.open.result;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import torna.common.support.IdCodec;

import java.util.Date;

/**
 * @author tanghc
 */
@Data
public class DocHeaderResult {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @ApiDocField(description = "参数id", example = "asdf", dataType = DataType.STRING)
    private Long id;

    /** 字段名称, 数据库字段：name */
    @ApiDocField(description = "参数名", required = true, example = "token")
    private String name;

    /** 是否必须，1：是，0：否, 数据库字段：required */
    @ApiDocField(description = "是否必须，1：是，0：否", required = true, example = "1")
    private Byte required;

    /** 示例值, 数据库字段：example */
    @ApiDocField(description = "示例值", example = "asdfasdfd")
    private String example;

    /** 描述, 数据库字段：description */
    @ApiDocField(description = "描述", example = "请求token")
    private String description;

    /**  数据库字段：gmt_create */
    @ApiDocField(description = "创建时间")
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    @ApiDocField(description = "最后修改时间")
    private Date gmtModified;

}
