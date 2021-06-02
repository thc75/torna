package cn.torna.swaggerplugin.util;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author tanghc
 */
public class PluginUtil {

    private static final List<String> SYSTEM_PACKAGE_LIST = Arrays.asList(
            "java.", "sun." ,"org.springframework.", "javax."
    );

    public static String getParameterType(Parameter parameter) {
        Class<?> type = parameter.getType();
        String simpleName = type.getSimpleName();
        if ("MultipartFile".equalsIgnoreCase(simpleName)) {
            return "file";
        }
        if ("MultipartFile[]".equalsIgnoreCase(simpleName)) {
            return "file[]";
        }
        return simpleName;
    }

    /**
     * 是否普通的java类
     * @param clazz 类class
     * @return true是
     */
    public static boolean isPojo(Class<?> clazz) {
        if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
            return false;
        }
        String name = clazz.getName();
        for (String prefix : SYSTEM_PACKAGE_LIST) {
            if (name.startsWith(prefix)) {
                return false;
            }
        }
        return true;
    }

    public static Type getGenericType(Type type) {
        Type[] params = ((ParameterizedType) type).getActualTypeArguments();
        if (params.length == 0) {
            return Object.class;
        }
        return params[0];
    }

}
