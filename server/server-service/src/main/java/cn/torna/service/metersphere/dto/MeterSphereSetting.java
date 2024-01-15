package cn.torna.service.metersphere.dto;

import lombok.Data;

/**
 * @author thc
 */
@Data
public class MeterSphereSetting {

    /**
     * MeterSphere服务器地址
     */
    private String msAddress;


    /**
     * MeterSphere的access_key
     */
    private String msAccessKey;

    /**
     * MeterSphere的secret_key
     */
    private String msSecretKey;
}
