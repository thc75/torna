package cn.torna.web.controller.module.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

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