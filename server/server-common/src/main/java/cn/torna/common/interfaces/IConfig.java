package cn.torna.common.interfaces;

public interface IConfig {

    String getConfig(String key);

    default String getConfig(String key, String defaultValue) {
        String value = getConfig(key);
        return value == null ? defaultValue : value;
    };

}
