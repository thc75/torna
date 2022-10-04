package cn.torna.web.controller.doc.vo;

import cn.torna.common.bean.TreeAware;
import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class DocInfoVO implements TreeAware<DocInfoVO, Long> {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 文档名称, 数据库字段：name */
    private String name;

    /** 文档概述, 数据库字段：description */
    private String description;

    private String author;

    /** 访问URL, 数据库字段：url */
    private String url;

    /** http方法, 数据库字段：http_method */
    private String httpMethod;

    /** contentType, 数据库字段：content_type */
    private String contentType;

    private String deprecated;

    /** 是否是分类，0：不是，1：是, 数据库字段：is_folder */
    private Byte isFolder;

    /** 父节点, 数据库字段：parent_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long parentId;

    /** 模块id，module.id, 数据库字段：module_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    private String creatorName;

    private String modifierName;

    private Byte isShow;

    private Byte isDeleted;

    private Byte isLocked;

    private Integer orderIndex;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    @JSONField(serialize = false)
    private DocInfoVO parent;

    private int apiCount;

    public void addApiCount() {
        this.apiCount++;
        if (parent != null) {
            parent.addApiCount();
        }
    }

    @Override
    public void setChildren(List<DocInfoVO> children) {

    }

    @Override
    public void setParent(DocInfoVO parent) {
        this.parent = parent;
    }
}
