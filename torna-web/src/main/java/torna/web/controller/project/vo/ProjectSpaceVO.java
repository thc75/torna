package torna.web.controller.project.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

/**
 * @author tanghc
 */
@Data
public class ProjectSpaceVO {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;
    private String projectName;
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long spaceId;
    private String spaceName;
}
