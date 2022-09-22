package cn.torna.common.bean;

import cn.torna.common.context.SpringContext;
import cn.torna.common.interfaces.IConfig;

/**
 * 配置工具类
 * @author thc
 */
public class Configs {

    /**
     * 获取配置参数
     * @param key 配置key
     * @return 返回配参数，没有则返回null
     */
    public static String getValue(String key) {
        return getValue(key, null);
    }

    /**
     * 获取配置参数
     * @param key 配置key
     * @param defaultValue 默认值
     * @return 返回配参数，没有则返回默认值
     */
    public static String getValue(String key, String defaultValue) {
        return SpringContext.getBean(IConfig.class).getConfig(key, defaultValue);
    }

}
