package torna.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
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

    /** 参数枚举值,json数组格式，如：[{"code":"0",type:"string","msg":"已支付"}], 数据库字段：enum_content */
    private String enumContent;

    /** doc_info.id, 数据库字段：doc_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long docId;

    /** 父节点, 数据库字段：parent_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long parentId;

    /** 0：header, 1：请求参数，2：返回参数，3：错误码, 数据库字段：style */
    private Byte style;

    /** 新增操作方式，0：人工操作，1：开放平台推送, 数据库字段：create_mode */
    private Byte createMode;

    /** 修改操作方式，0：人工操作，1：开放平台推送, 数据库字段：modify_mode */
    private Byte modifyMode;

    /** 创建人, 数据库字段：creator */
    private String creator;

    /** 修改人, 数据库字段：modifier */
    private String modifier;

    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;

    private List<DocParamDTO> children;

}
