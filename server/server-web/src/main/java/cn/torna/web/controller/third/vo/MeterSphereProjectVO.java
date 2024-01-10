package cn.torna.web.controller.third.vo;

import cn.torna.common.support.IdCodec;
import cn.torna.service.metersphere.dto.MeterSphereProjectDTO;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author thc
 */
@Data
public class MeterSphereProjectVO {

    private Boolean spaceConfig = true;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long spaceId;

    private List<MeterSphereProjectDTO> projects;

}
