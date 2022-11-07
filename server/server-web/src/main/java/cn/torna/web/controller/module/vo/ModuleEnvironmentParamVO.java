package cn.torna.web.controller.module.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author tanghc
 */
@Data
public class ModuleEnvironmentParamVO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

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
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
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
     * 排序索引
     */
    private Integer orderIndex;


    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private byte global = 1;


}