package torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：doc_info
 * 备注：文档信息
 *
 * @author tanghc
 */
@Table(name = "doc_info")
@Data
public class DocInfo {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 唯一id，接口规则：md5(module_id:parent_id:url:http_method)。分类规则：md5(module_id:parent_id:name), 数据库字段：data_id */
    private String dataId;

    /** 文档名称, 数据库字段：name */
    private String name;

    /** 文档描述, 数据库字段：description */
    private String description;

    /** 访问URL, 数据库字段：url */
    private String url;

    /** http方法, 数据库字段：http_method */
    private String httpMethod;

    /** contentType, 数据库字段：content_type */
    private String contentType;

    /** 是否是分类，0：不是，1：是, 数据库字段：is_folder */
    private Byte isFolder;

    /** 父节点, 数据库字段：parent_id */
    private Long parentId;

    /** 模块id，module.id, 数据库字段：module_id */
    private Long moduleId;

    /** 新增操作方式，0：人工操作，1：开放平台推送, 数据库字段：create_mode */
    private Byte createMode;

    /** 修改操作方式，0：人工操作，1：开放平台推送, 数据库字段：modify_mode */
    private Byte modifyMode;

    /** 创建人, 数据库字段：creator_id */
    private Long creatorId;

    /** 创建者昵称user_info.nickname, 数据库字段：creator_name */
    private String creatorName;

    /** 修改人, 数据库字段：modifier_id */
    private Long modifierId;

    /** 创建者昵称user_info.nickname, 数据库字段：modifier_name */
    private String modifierName;

    /** 是否显示, 数据库字段：is_show */
    private Byte isShow;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}