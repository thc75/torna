package torna.api.open.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import torna.common.support.IdCodec;

import java.util.Date;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class DocParamDTO {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @ApiDocField(description = "参数id", example = "asdf")
    private Long id;

    /** 字段名称, 数据库字段：name */
    @ApiDocField(description = "参数名")
    private String name;

    /** 字段类型, 数据库字段：type */
    @ApiDocField(description = "字段类型")
    private String type;

    /** 是否必须，1：是，0：否, 数据库字段：required */
    @ApiDocField(description = "是否必须，1：是，0：否")
    private Byte required;

    /** 最大长度, 数据库字段：max_length */
    @ApiDocField(description = "最大长度")
    private String maxLength;

    /** 示例值, 数据库字段：example */
    @ApiDocField(description = "示例值")
    private String example;

    /** 描述, 数据库字段：description */
    @ApiDocField(description = "描述")
    private String description;

    /** 参数枚举值,json数组格式，如：[{"code":"0",type:"string","msg":"已支付"}], 数据库字段：enum_content */
    @ApiDocField(description = "参数枚举值,json数组格式")
    private String enumContent;

    /** doc_info.id, 数据库字段：doc_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @ApiDocField(description = "文档id")
    private Long docId;

    /** 父节点, 数据库字段：parent_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @ApiDocField(description = "父节点")
    private Long parentId;

    /** 0：header, 1：请求参数，2：返回参数，3：错误码, 数据库字段：style */
    @ApiDocField(description = "参数形式，0：header, 1：请求参数，2：返回参数，3：错误码")
    private Byte style;

    /** 新增操作方式，0：人工操作，1：开放平台推送, 数据库字段：create_mode */
    @ApiDocField(description = "新增操作方式，0：人工操作，1：开放平台推送")
    private Byte createMode;

    /** 修改操作方式，0：人工操作，1：开放平台推送, 数据库字段：modify_mode */
    @ApiDocField(description = "修改操作方式，0：人工操作，1：开放平台推送")
    private Byte modifyMode;

    /** 创建人, 数据库字段：creator */
    @ApiDocField(description = "创建人")
    private String creator;

    /** 修改人, 数据库字段：modifier */
    @ApiDocField(description = "最后修改人")
    private String modifier;

    /**  数据库字段：gmt_create */
    @ApiDocField(description = "创建时间")
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    @ApiDocField(description = "最后修改时间")
    private Date gmtModified;

}
