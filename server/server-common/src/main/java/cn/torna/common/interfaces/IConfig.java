package cn.torna.common.interfaces;

public interface IConfig {

    String getConfig(String key);

    String getConfig(String key, String defaultValue);

}
