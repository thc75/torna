package cn.torna.web.controller.system.vo;

import lombok.Data;

@Data
public class AdminConfigVO {

    private ConfigItemVO regEnable;

    public static ConfigItemVO buildItem(String key, String value) {
        ConfigItemVO itemVO = new ConfigItemVO();
        itemVO.setKey(key);
        itemVO.setValue(value);
        return itemVO;
    }
}
