package cn.torna.web.controller.doc.vo;

import cn.torna.common.bean.TreeAware;
import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
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

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long docId;

    private String url;

    private Byte type;

    private String httpMethod;

    private Byte docType;

    private String deprecated;

    private String origin;

    /** 接口数量 */
    private int apiCount;

    @JSONField(serialize = false)
    private TreeVO parent;

    public TreeVO(String id, String label, String parentId, byte type) {
        this.id = id;
        this.label = label;
        this.parentId = parentId;
        this.type = type;
    }

    public void addApiCount() {
        this.apiCount++;
        if (parent != null) {
            parent.addApiCount();
        }
    }

    @Override
    public void setChildren(List<TreeVO> children) {
    }

    @Override
    public void setParent(TreeVO parent) {
        this.parent = parent;
    }
}
