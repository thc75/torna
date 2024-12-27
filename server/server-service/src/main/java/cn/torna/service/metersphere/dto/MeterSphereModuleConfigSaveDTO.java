package cn.torna.service.metersphere.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author thc
 */
@Data
public class MeterSphereModuleConfigSaveDTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long releaseId;

    private String msModuleId;

    private String msModuleName;

    private String msProjectId;

    private String msProjectName;

    private String name;

}
