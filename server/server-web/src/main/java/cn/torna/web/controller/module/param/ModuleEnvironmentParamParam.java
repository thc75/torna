package cn.torna.web.controller.module.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ModuleEnvironmentParamParam {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /**
     * 唯一id，md5(doc_id:parent_id:style:name)
     */
    private String dataId;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 是否必须，1：是，0：否
     */
    private Byte required;

    /**
     * 最大长度
     */
    private String maxLength;

    /**
     * 示例值
     */
    private String example;

    /**
     * 描述
     */
    private String description;

    /**
     * enum_info.id
     */
    private Long enumId;

    /**
     * module_environment.id
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long environmentId;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long parentId;

    /**
     * 0：path, 1：header， 2：请求参数，3：返回参数，4：错误码
     */
    private Byte style;

    /**
     * 新增操作方式，0：人工操作，1：开放平台推送
     */
    private Byte createMode;

    /**
     * 修改操作方式，0：人工操作，1：开放平台推送
     */
    private Byte modifyMode;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long creatorId;

    private String creatorName;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long modifierId;

    private String modifierName;

    /**
     * 排序索引
     */
    private Integer orderIndex;

    private Byte isDeleted;

}