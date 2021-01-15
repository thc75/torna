package torna.web.controller.openuser.param;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import javax.validation.constraints.NotBlank;

/**
 * 表名：open_user
 * 备注：开放用户
 *
 * @author tanghc
 */
@Data
public class OpenUserAddParam  {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long spaceId;

    @NotBlank(message = "applicant not null")
    private String applicant;

}