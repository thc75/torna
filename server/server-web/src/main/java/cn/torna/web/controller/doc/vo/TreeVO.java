package cn.torna.web.controller.doc.vo;

import cn.torna.common.bean.TreeAware;
import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class TreeVO implements TreeAware<TreeVO, String> {

    private String id;

    private String label;

    private String parentId;

    private List<TreeVO> children;

    public TreeVO(String id, String label, String parentId) {
        this.id = id;
        this.label = label;
        this.parentId = parentId;
    }

    @Override
    public void setChildren(List<TreeVO> children) {
        this.children = children;
    }

}
