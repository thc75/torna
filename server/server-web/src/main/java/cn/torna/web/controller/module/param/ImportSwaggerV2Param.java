package cn.torna.web.controller.module.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class ImportSwaggerV2Param {

    @NotNull
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

    /** swagger文档url, 数据库字段：url */
    private String url;

    /** swagger文档内容, 数据库字段：content */
    private String content;

    /** 刷新时间间隔, 数据库字段：refresh_minutes */
    private Integer refreshMinutes;

    /** 认证用户名, 数据库字段：auth_username */
    private String authUsername;

    /** 认证密码, 数据库字段：auth_password */
    private String authPassword;

    /** 刷新状态，1：启用，0：禁用 */
    private Byte refreshStatus;

}
