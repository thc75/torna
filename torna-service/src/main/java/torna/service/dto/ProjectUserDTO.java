package torna.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import java.util.Date;

/**
 * @author tanghc
 */
@Data
public class ProjectUserDTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 登录账号/邮箱, 数据库字段：username */
    private String username;

    /** 昵称, 数据库字段：nickname */
    private String nickname;

    private String roleCode;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

}
