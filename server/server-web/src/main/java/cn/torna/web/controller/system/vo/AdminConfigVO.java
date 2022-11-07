package cn.torna.web.controller.system.vo;

import cn.torna.common.bean.EnvironmentKeys;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdminConfigVO {

    private List<ConfigItemVO> configs = new ArrayList<>(8);

    public void addConfig(ConfigItemVO configItemVO) {
        configs.add(configItemVO);
    }

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
