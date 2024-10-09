package cn.torna.web.controller.project.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author qiuyu
 */
@Data
public class ProjectReleaseBindParam {

    @NotNull(message = "项目ID不能为空")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

    @NotNull(message = "项目版本ID不能为空")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long releaseId;

}
