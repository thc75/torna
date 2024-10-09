package cn.torna.service.gen.dto;

import com.gitee.fastmybatis.core.support.TreeNode;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 备注：代码生成模板
 *
 * @author tanghc
 */
@Data
public class GenTemplateDTO implements TreeNode<GenTemplateDTO, Long> {

    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板内容
     */
    private String content;

    private Integer isDeleted;

    /**
     * 分组
     */
    private String groupName;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private List<GenTemplateDTO> children;

    @Override
    public Long takeId() {
        return id;
    }

    @Override
    public Long takeParentId() {
        return 0L;
    }

    @Override
    public void setChildren(List<GenTemplateDTO> list) {
        this.children = list;
    }
}
