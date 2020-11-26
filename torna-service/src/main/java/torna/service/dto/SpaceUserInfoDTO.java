package torna.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import java.util.Date;

/**
 * @author tanghc
 */
@Data
public class SpaceUserInfoDTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 登录账号/邮箱, 数据库字段：username */
    private String username;

    /** 真实姓名, 数据库字段：realname */
    private String realname;

    private String roleCode;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

}
