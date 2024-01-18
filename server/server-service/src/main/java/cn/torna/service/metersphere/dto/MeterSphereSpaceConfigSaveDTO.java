package cn.torna.service.metersphere.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author thc
 */
@Data
public class MeterSphereSpaceConfigSaveDTO {

    /**
     * MeterSphere服务器地址
     */
    @NotBlank(message = "MeterSphere服务器地址不能为空")
    private String msAddress;


    /**
     * MeterSphere的access_key
     */
    @NotBlank(message = "MeterSphere accessKey不能为空")
    private String msAccessKey;

    /**
     * MeterSphere的secret_key
     */
    @NotBlank(message = "MeterSphere secretKey不能为空")
    private String msSecretKey;

    @NotNull(message = "spaceId不能为空")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long spaceId;

    @NotBlank(message = "MeterSphere空间不能为空")
    private String msSpaceId;

    /**
     * 空间名称
     */
    private String msSpaceName;

}
