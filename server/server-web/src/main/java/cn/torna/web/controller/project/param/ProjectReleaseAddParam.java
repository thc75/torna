package cn.torna.web.controller.project.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author qiuyu
 */
@Data
public class ProjectReleaseAddParam {

    @NotNull(message = "项目ID不能为空")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

    /** 版本号 */
    @NotEmpty(message = "版本号不能为空")
    private String releaseNo;

    /** 版本描述 */
    private String releaseDesc;

    /** 状态 1-有效 0-无效 */
    @NotNull(message = "版本状态不能为空")
    private Integer status;

    /** 钉钉机器人webhook */
    @URL(message = "地址格式不正确")
    private String dingdingWebhook;

    /** 关联文档Map （key：模块hashId  value: 文档hashId集合） */
    private Map<String, List<String>> moduleSourceIdMap;
}
