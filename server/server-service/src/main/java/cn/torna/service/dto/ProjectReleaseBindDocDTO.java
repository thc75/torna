package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author qiuyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectReleaseBindDocDTO {

    /** project_release.id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long releaseId;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    private String label;

    private String name;

    private String httpMethod;

    private String url;

    private String version;

    private Integer isFolder;

    private List<ProjectReleaseBindDocDTO> children = new ArrayList<>();

    public ProjectReleaseBindDocDTO(Long releaseId) {
        this.releaseId = releaseId;
    }
}