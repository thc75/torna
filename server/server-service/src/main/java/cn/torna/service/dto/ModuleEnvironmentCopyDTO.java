package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ModuleEnvironmentCopyDTO {

    /**
     * 被拷贝的环境id
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long fromEnvId;

    /**
     * 目标模块
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long destModuleId;

    /**
     * 环境名称
     */
    private String name;

    /** 
     * 调试路径
     */
    private String url;

    /** 
     * 是否公开
     */
    private Byte isPublic;


}