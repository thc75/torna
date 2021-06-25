package cn.torna.swaggerplugin.util;

import cn.torna.swaggerplugin.builder.DataType;
import org.springframework.util.ReflectionUtils;
import sun.reflect.generics.repository.ClassRepository;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author tanghc
 */
public class PluginUtil {

    public static final String METHOD_GET_GENERIC_INFO = "getGenericInfo";

    private static final List<String> SYSTEM_PACKAGE_LIST = Arrays.asList(
            "java.", "sun.", "org.springframework.", "javax."
    );

    public static String getParameterType(Parameter parameter) {
        Class<?> type = parameter.getType();
        String simpleName = type.getSimpleName().toLowerCase();
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
     *
     * @param clazz 类class
     * @return true是
     */
    public static boolean isPojo(Class<?> clazz) {
        if (clazz.isArray() || Collection.class.isAssignableFrom(clazz) || clazz.isPrimitive()) {
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

    public static Type getGenericTypeDeeply(Type type) {
        if (isGenericType(type)) {
            Type genericType = getGenericType(type);
            return getGenericTypeDeeply(genericType);
        } else {
            return type;
        }
    }

    /**
     * 是否是泛型类型
     * @param type 类型
     * @return true：是泛型类型
     */
    public static boolean isGenericType(Type type) {
        return type instanceof ParameterizedType;
    }

    public static String getGenericParamKey(Class<?> rawType, String name) {
        return rawType.getName() + "." + name;
    }

    public static boolean isCollectionOrArray(Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type);
    }

    public static String getDataType(Class<?> type) {
        if (isCollectionOrArray(type)) {
            return DataType.ARRAY.getValue();
        }
        return isPojo(type) ? DataType.OBJECT.getValue() : type.getSimpleName().toLowerCase();
    }

    /**
     * 将泛型实际参数存储到genericParamMap中。<br>
     * Result<Page<Order>>
     * key: xxx.Result.T  value: Page.class
     * key: xxx.Page.T    value: Order.class
     * @param genericParamMap 存储泛型信息
     * @param genericParameterType 泛型类型
     */
    public static void appendGenericParamMap(Map<String, Class<?>> genericParamMap, Type genericParameterType) {
        // 如果有泛型
        if (PluginUtil.isGenericType(genericParameterType)) {
            ParameterizedType parameterType = (ParameterizedType) genericParameterType;
            // 泛型参数
            Type[] actualTypeArguments = parameterType.getActualTypeArguments();
            // 原始类型
            Class<?> rawType = (Class<?>) parameterType.getRawType();
            Class<? extends Class> rawTypeClass = rawType.getClass();
            Method getGenericInfo = ReflectionUtils.findMethod(rawTypeClass, METHOD_GET_GENERIC_INFO);
            if (getGenericInfo != null) {
                ReflectionUtils.makeAccessible(getGenericInfo);
                ClassRepository classRepository = (ClassRepository) ReflectionUtils.invokeMethod(getGenericInfo, rawType);
                if (classRepository != null) {
                    TypeVariable<?>[] typeParameters = classRepository.getTypeParameters();
                    for (int i = 0; i < typeParameters.length; i++) {
                        String key = getGenericParamKey(rawType, typeParameters[i].getName());
                        Type actualTypeArgument = actualTypeArguments[i];
                        boolean isGeneric = PluginUtil.isGenericType(actualTypeArgument);
                        Class <?> value = isGeneric ?
                                (Class<?>) ((ParameterizedType) actualTypeArgument).getRawType() : (Class<?>) actualTypeArgument;
                        genericParamMap.put(key, value);
                        if (isGeneric) {
                            appendGenericParamMap(genericParamMap, actualTypeArgument);
                        }
                    }
                }
            }
        }
    }

}
