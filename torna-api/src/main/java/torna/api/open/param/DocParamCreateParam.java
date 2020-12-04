package torna.api.open.param;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import torna.common.enums.OperationMode;
import torna.common.support.IdCodec;

import java.util.Date;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class DocParamCreateParam {

    /** 字段名称, 数据库字段：name */
    @ApiDocField(description = "参数名", required = true, example = "goodsName")
    private String name;

    /** 字段类型, 数据库字段：type */
    @ApiDocField(description = "字段类型", required = true, example = "string")
    private String type;

    /** 是否必须，1：是，0：否, 数据库字段：required */
    @ApiDocField(description = "是否必须，1：是，0：否", required = true, example = "1")
    private Byte required;

    /** 最大长度, 数据库字段：max_length */
    @ApiDocField(description = "最大长度", example = "128")
    private String maxLength;

    /** 示例值, 数据库字段：example */
    @ApiDocField(description = "示例值", example = "iphone12")
    private String example;

    /** 描述, 数据库字段：description */
    @ApiDocField(description = "描述", example = "商品名称描述")
    private String description;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long enumId;

    /** 父节点, 数据库字段：parent_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @ApiDocField(description = "父节点, 没有填空字符串", dataType = DataType.STRING)
    private Long parentId;

    private Byte createMode = OperationMode.OPEN.getType();

    private Byte modifyMode = OperationMode.OPEN.getType();

}
