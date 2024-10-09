package cn.torna.service.metersphere.dto;

import cn.torna.service.metersphere.v3.model.state.AppSettingState;
import lombok.Data;

/**
 * @author thc
 */
@Data
public class MeterSphereSetting {

    /**
     * 1-2.x，2-3.x
     */
    private Integer version;

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

    public AppSettingState toAppSettingState() {
        AppSettingState appSettingState = new AppSettingState();
        appSettingState.setMeterSphereAddress(getMsAddress());
        appSettingState.setAccessKey(getMsAccessKey());
        appSettingState.setSecretKey(getMsSecretKey());
        return appSettingState;
    }
}
