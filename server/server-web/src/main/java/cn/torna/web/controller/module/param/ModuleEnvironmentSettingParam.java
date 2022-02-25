package cn.torna.web.controller.module.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class ModuleEnvironmentSettingParam {

    /**
     * module.id
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    /** 
     * 环境名称
     */
    @NotBlank
    private String name;

    /** 
     * 调试路径
     */
    @NotBlank
    private String url;

    /** 
     * 是否公开
     */
    @NotNull
    private Byte isPublic;


}