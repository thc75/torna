package cn.torna.common.bean;

import org.springframework.core.env.Environment;

/**
 * 废弃，使用EnvironmentKeys代替
 * @author tanghc
 */
@Deprecated
public class EnvironmentContext {
    private static Environment environment;

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment environment) {
        EnvironmentContext.environment = environment;
    }

    /**
     * 废弃，使用EnvironmentKeys代替
     * @param key
     * @return
     */
    @Deprecated
    public static String getValue(String key) {
        return getValue(key, null);
    }

    /**
     * 废弃，使用EnvironmentKeys代替
     * @param key
     * @param defaultValue
     * @return
     */
    @Deprecated
    public static String getValue(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

}
