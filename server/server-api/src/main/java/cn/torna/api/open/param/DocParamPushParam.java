package cn.torna.api.open.param;

import cn.torna.common.bean.Booleans;
import cn.torna.common.enums.OperationMode;
import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author tanghc
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DocParamPushParam {

    /** 字段名称, 数据库字段：name */
    @ApiDocField(description = "参数名", required = true, example = "goodsName")
    @NotBlank(message = "参数名不能为空")
    @Length(max = 50, message = "参数名不能超过50")
    private String name;

    /** 字段类型, 数据库字段：type */
    @ApiDocField(description = "字段类型", required = true, example = "string/array/object")
    @NotBlank(message = "字段类型不能为空")
    @Length(max = 50, message = "字段类型不能超过50")
    private String type;

    /** 是否必须，1：是，0：否, 数据库字段：required */
    @ApiDocField(description = "是否必须，1：是，0：否", required = true, example = "1")
    @NotNull(message = "是否必须不能为空")
    private Byte required;

    /** 最大长度, 数据库字段：max_length */
    @ApiDocField(description = "最大长度", example = "128")
    @Length(max = 50, message = "最大长度不能超过50")
    private String maxLength;

    /** 示例值, 数据库字段：example */
    @ApiDocField(description = "示例值", example = "iphone12")
    @Length(max = 100, message = "示例值长度不能超过100")
    private String example;

    /** 描述, 数据库字段：description */
    @ApiDocField(description = "描述", example = "商品名称描述", maxLength = "256")
    @Length(max = 256, message = "描述长度不能超过256")
    private String description;

    /** 父节点, 数据库字段：parent_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @ApiDocField(description = "父节点, 没有填空字符串", dataType = DataType.STRING)
    private Long parentId;

    @ApiDocField(description = "参数对应的枚举，如果参数是枚举，可以顺便把枚举信息填进来")
    private EnumInfoCreateParam enumInfo;

    @ApiDocField(description = "子节点，内容同父节点")
    private List<DocParamPushParam> children;

    @Builder.Default
    private Byte createMode = OperationMode.OPEN.getType();

    @Builder.Default
    private Byte modifyMode = OperationMode.OPEN.getType();

    @Builder.Default
    private Byte isDeleted = Booleans.FALSE;

    /** 排序 */
    @ApiDocField(description = "排序字段", example = "1")
    private Integer orderIndex;
}
