package cn.torna.web.controller.compose.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author tanghc
 */
@Data
public class ComposeDocVO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** doc_info.id, 数据库字段：doc_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long docId;

    /** 文档来源, 数据库字段：origin */
    private String origin;

    /** compose_project.id, 数据库字段：project_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

    /** 是否文件夹, 数据库字段：is_folder */
    private Byte isFolder;

    /**  数据库字段：parent_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long parentId;

    /** 创建人, 数据库字段：creator */
    private String creator;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /** 文件夹名称, 数据库字段：folder_name */
    private String name;

    /** 访问URL, 数据库字段：url */
    private String url;

    /** http方法, 数据库字段：http_method */
    private String httpMethod;

    private Integer orderIndex;
}
