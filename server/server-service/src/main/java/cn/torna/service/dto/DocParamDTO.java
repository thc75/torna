package cn.torna.service.dto;

import cn.torna.common.annotation.Diff;
import cn.torna.common.bean.TreeAware;
import cn.torna.common.enums.PositionType;
import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class DocParamDTO implements TreeAware<DocParamDTO, Long> {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 字段名称, 数据库字段：name */
    @Diff(positionType = PositionType.PARAM_NAME)
    private String name;

    /** 字段类型, 数据库字段：type */
    @Diff(positionType = PositionType.PARAM_TYPE)
    private String type;

    /** 是否必须，1：是，0：否, 数据库字段：required */
    @Diff(positionType = PositionType.PARAM_REQUIRED)
    private Byte required;

    /** 最大长度, 数据库字段：max_length */
    @Diff(positionType = PositionType.PARAM_MAXLENGTH)
    private String maxLength;

    /** 示例值, 数据库字段：example */
    @Diff(positionType = PositionType.PARAM_EXAMPLE)
    private String example;

    /** 描述, 数据库字段：description */
    @Diff(positionType = PositionType.PARAM_DESCRIPTION)
    private String description;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long enumId;

    /** doc_info.id, 数据库字段：doc_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long docId;

    /** 父节点, 数据库字段：parent_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long parentId;

    private EnumInfoDTO enumInfo;

    /** 0：header, 1：请求参数，2：返回参数，3：错误码, 数据库字段：style */
    private Byte style;

    /** 新增操作方式，0：人工操作，1：开放平台推送, 数据库字段：create_mode */
    private Byte createMode;

    /** 修改操作方式，0：人工操作，1：开放平台推送, 数据库字段：modify_mode */
    private Byte modifyMode;

    /** 创建人 */
    private String creatorName;

    /** 修改人 */
    private String modifierName;

    /** 排序 */
    private Integer orderIndex;

    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;

    private boolean global;

    private List<DocParamDTO> children;

}
