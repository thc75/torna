package cn.torna.service.dto;

import cn.torna.common.bean.User;
import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tanghc
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ImportSwaggerV2DTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

    /** swagger文档url, 数据库字段：url */
    private String url;

    /** swagger文档内容, 数据库字段：content */
    private String content;

    /** 认证用户名, 数据库字段：auth_username */
    private String authUsername;

    /** 认证密码, 数据库字段：auth_password */
    private String authPassword;

    private User user;

    private String ip;

}
