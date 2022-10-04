package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：doc_info
 * 备注：文档信息
 *
 * @author tanghc
 */
@Table(name = "doc_info", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class DocInfo {

    /**  数据库字段：id */
    private Long id;

    /** 唯一id，接口规则：md5(module_id:parent_id:url:http_method)。分类规则：md5(module_id:parent_id:name), 数据库字段：data_id */
    private String dataId;

    /** 文档内容的md5值, 数据库字段：md5 */
    private String md5;

    /** 文档名称, 数据库字段：name */
    private String name;

    /** 文档描述, 数据库字段：description */
    private String description;

    /** 维护人, 数据库字段：author */
    private String author;

    /** 0:http,1:dubbo, 数据库字段：type */
    private Byte type;

    /** 访问URL, 数据库字段：url */
    private String url;

    /** http方法, 数据库字段：http_method */
    private String httpMethod;

    /** contentType, 数据库字段：content_type */
    private String contentType;

    /** 废弃信息, 数据库字段：deprecated */
    private String deprecated;

    /** 是否是分类，0：不是，1：是, 数据库字段：is_folder */
    private Byte isFolder;

    /** 父节点, 数据库字段：parent_id */
    private Long parentId;

    /** 模块id，module.id, 数据库字段：module_id */
    private Long moduleId;

    /** 是否使用全局请求参数, 数据库字段：is_use_global_headers */
    private Byte isUseGlobalHeaders;

    /** 是否使用全局请求参数, 数据库字段：is_use_global_params */
    private Byte isUseGlobalParams;

    /** 是否使用全局返回参数, 数据库字段：is_use_global_returns */
    private Byte isUseGlobalReturns;

    /** 是否请求数组, 数据库字段：is_request_array */
    private Byte isRequestArray;

    /** 是否返回数组, 数据库字段：is_response_array */
    private Byte isResponseArray;

    /** 请求数组时元素类型, 数据库字段：request_array_type */
    private String requestArrayType;

    /** 返回数组时元素类型, 数据库字段：response_array_type */
    private String responseArrayType;

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

    /** 创建者昵称user_info.realname, 数据库字段：modifier_name */
    private String modifierName;

    /** 排序索引, 数据库字段：order_index */
    private Integer orderIndex;

    /** 备注, 数据库字段：remark */
    private String remark;

    /** 是否显示, 数据库字段：is_show */
    private Byte isShow;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /** 是否锁住, 数据库字段：is_locked */
    private Byte isLocked;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}