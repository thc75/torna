package cn.torna.web.controller.third.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author thc
 */
@Data
public class MeterSphereModuleSaveParam {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    private String msModuleId;

    private String msModuleName;

    private String msProjectId;

    private String msProjectName;

}
