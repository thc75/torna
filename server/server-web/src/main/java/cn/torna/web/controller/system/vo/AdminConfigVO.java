package cn.torna.web.controller.system.vo;

import cn.torna.common.bean.EnvironmentKeys;
import lombok.Data;

@Data
public class AdminConfigVO {

    private ConfigItemVO regEnable;
    private ConfigItemVO dingdingWebhookUrl;

    public static ConfigItemVO buildItem(EnvironmentKeys environmentKeys) {
        return buildItem(environmentKeys.getKey(), environmentKeys.getValue());
    }

    public static ConfigItemVO buildItem(String key, String value) {
        ConfigItemVO itemVO = new ConfigItemVO();
        itemVO.setKey(key);
        itemVO.setValue(value);
        return itemVO;
    }
}
