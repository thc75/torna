package cn.torna.swaggerplugin.util;

import cn.torna.swaggerplugin.bean.ControllerInfo;
import cn.torna.swaggerplugin.bean.PushFeature;
import cn.torna.swaggerplugin.bean.TornaConfig;
import cn.torna.swaggerplugin.builder.DataType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.beans.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tanghc
 */
public class PluginUtil {

    public static final String METHOD_GET_GENERIC_INFO = "getGenericInfo";

    private static final List<String> SYSTEM_PACKAGE_LIST = Arrays.asList(
            "java.", "sun.", "org.springframework.", "javax.", "jakarta."
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

    /**
     * 字段是否被transient关键字修饰或有@Transient注解
     *
     * @param field
     * @return 是返回true
     */
    public static boolean isTransientField(Field field) {
        Transient transientAnno = field.getAnnotation(Transient.class);
        return transientAnno != null || Modifier.isTransient(field.getModifiers());
    }

    public static <T extends Annotation> boolean isExistAnnotation(Annotation[] annotations, Class<T> annoClass) {
        if (annotations == null) {
            return false;
        }
        for (Annotation annotation : annotations) {
            if (annotation.getClass() == annoClass) {
                return true;
            }
        }
        return false;
    }

    public static Type getGenericType(Field field) {
        ReflectionUtils.makeAccessible(field);
        Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(Field.class);
        for (Method allDeclaredMethod : allDeclaredMethods) {
            if (allDeclaredMethod.getName().equals("getGenericSignature")) {
                ReflectionUtils.makeAccessible(allDeclaredMethod);
                Object o = ReflectionUtils.invokeMethod(allDeclaredMethod, field);
                // Lcn/torna/tornaexample/controller/eg/vo/CommonPage<Lcn/torna/tornaexample/controller/eg/vo/LockingDto;>;
                String signature = String.valueOf(o);
                if (signature.startsWith("L") && signature.endsWith(";>;")) {
                    String className = signature.replace(";", "");
                    // Lcn/torna/tornaexample/controller/eg/vo/LockingDto
                    className = StringUtil.findBetweenChar(className, '<', '>');
                    if (className.startsWith("L")) {
                        // cn/torna/tornaexample/controller/eg/vo/LockingDto
                        className = className.substring(1);
                    }
                    className = StringUtil.splashToDot(className);
                    try {
                        return ClassUtils.forName(className, Thread.currentThread().getContextClassLoader());
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                }

            }
        }
        return field.getGenericType();
    }


    /**
     * 获取泛型参数类型，如：List{@literal <String> }  返回String
     *
     * @param type 泛型参数
     * @return 返回泛型参数类型
     */
    public static Type getGenericType(Type type) {
        Type[] params = ((ParameterizedType) type).getActualTypeArguments();
        if (params.length == 0) {
            return Object.class;
        }
        Type param = params[0];
        // List<? extends Pojo>,
        if (param instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) param;
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds != null && upperBounds.length > 0) {
                // Pojo.class
                return upperBounds[0];
            }
            Type[] lowerBounds = wildcardType.getLowerBounds();
            if (lowerBounds != null && lowerBounds.length > 0) {
                return lowerBounds[0];
            }
        }
        return param;
    }

    /**
     * 获取泛型参数的原理类型，如：List{@literal <String> }  返回List
     *
     * @param type 泛型参数
     * @return 返回泛型参数原始类型
     */
    public static Type getGenericRawType(Type type) {
        Type rawType = ((ParameterizedType) type).getRawType();
        if (rawType == null) {
            return Object.class;
        }
        return rawType;
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
     *
     * @param type 类型
     * @return true：是泛型类型
     */
    public static boolean isGenericType(Type type) {
        return type instanceof ParameterizedType;
    }

    public static String getGenericParamKey(Class<?> rawType, String name) {
//        return rawType.getName() + "." + name;
        return rawType.getName();
    }

    public static boolean isCollectionOrArray(Class<?> type) {
        return type.isArray() || isCollection(type);
    }

    public static boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    public static String getDataType(Class<?> type) {
        if (isCollectionOrArray(type)) {
            return DataType.ARRAY.getValue();
        }
        return isPojo(type) ? DataType.OBJECT.getValue() : type.getSimpleName().toLowerCase();
    }

    public static List<String> getClassGenericParamName(Class<?> clazz) {
        TypeVariable<? extends Class<?>>[] typeParameters = clazz.getTypeParameters();
        List<String> paramNames = new ArrayList<>();
        for (TypeVariable<?> typeParameter : typeParameters) {
            paramNames.add(typeParameter.getName());
        }
        return paramNames;
    }

    /**
     * 将泛型实际参数存储到genericParamMap中。<br>
     * <pre>
     * {@literal
     * Result<Page<Order>>
     * key: xxx.Result.T  value: Page.class
     * key: xxx.Page.T    value: Order.class
     * }
     * </pre>
     *
     * @param genericParamMap      存储泛型信息
     * @param genericParameterType 泛型类型
     */
    public static void appendGenericParamMap1(Map<String, Class<?>> genericParamMap, Type genericParameterType) {
        // 如果有泛型
        if (PluginUtil.isGenericType(genericParameterType)) {
            ParameterizedType parameterType = (ParameterizedType) genericParameterType;
            // 泛型参数
            Type[] actualTypeArguments = parameterType.getActualTypeArguments();
            // 原始类型
            Class<?> rawType = (Class<?>) parameterType.getRawType();
            Class<? extends Class> rawTypeClass = rawType.getClass();
            TypeVariable<?>[] typeParameters = rawTypeClass.getTypeParameters();
            for (int i = 0; i < typeParameters.length; i++) {
                String key = getGenericParamKey(rawType, typeParameters[i].getName());
                Type actualTypeArgument = actualTypeArguments[i];
                // 如果泛型填的?,即：Result<?>
                if (actualTypeArgument instanceof WildcardType) {
                    genericParamMap.put(key, Object.class);
                    continue;
                }
                boolean isGeneric = PluginUtil.isGenericType(actualTypeArgument);
                Class<?> value = isGeneric ?
                        (Class<?>) ((ParameterizedType) actualTypeArgument).getRawType() : (Class<?>) actualTypeArgument;
                genericParamMap.put(key, value);
                if (isGeneric) {
                    appendGenericParamMap1(genericParamMap, actualTypeArgument);
                }
            }
        }
    }

    /**
     * 将泛型实际参数存储到genericParamMap中。<br>
     * <pre>
     * {@literal
     * Result<Page<Order>>
     * key: xxx.Result  value: Page.class
     * key: xxx.Page    value: Order.class
     * }
     * </pre>
     *
     * @param genericParamMap      存储泛型信息
     * @param genericParameterType 泛型类型
     */
    public static void appendGenericParamMap(ControllerInfo controllerInfo, Map<String, Class<?>> genericParamMap, Type genericParameterType) {
        // 如果有泛型
        if (PluginUtil.isGenericType(genericParameterType)) {
            ParameterizedType parameterizedType = (ParameterizedType) genericParameterType;
            Type rawType = parameterizedType.getRawType();
            Type target = parameterizedType.getActualTypeArguments()[0];
            Class targetClass;
            if (target instanceof ParameterizedType) {
                ParameterizedType parameterizedTypeTarget = (ParameterizedType) target;
                targetClass = (Class) parameterizedTypeTarget.getRawType();
                genericParamMap.put(rawType.getTypeName(), targetClass);
                appendGenericParamMap(controllerInfo, genericParamMap, target);
            } else if (target instanceof TypeVariable) {
                TypeVariable parameterizedTypeTarget = (TypeVariable) target;
                Class<?> genericDeclaration = (Class<?>) parameterizedTypeTarget.getGenericDeclaration();
                String key = genericDeclaration.getName() + "#" + parameterizedTypeTarget.getName();
                Class<?> realClass = controllerInfo.getGenericParamMap().get(key);
                if (realClass != null) {
                    targetClass = realClass;
                    genericParamMap.put(rawType.getTypeName(), targetClass);
                }
            } else {
                if (target instanceof WildcardType) {
                    return;
                }
                targetClass = (Class) target;
                genericParamMap.put(rawType.getTypeName(), targetClass);
            }
        }
    }

    /**
     * 属性拷贝,第一个参数中的属性值拷贝到第二个参数中<br>
     * 注意:当第一个参数中的属性有null值时,不会拷贝进去
     *
     * @param from 源对象
     * @param to   目标对象
     * @throws BeansException
     */
    public static void copyPropertiesIgnoreNull(Object from, Object to, String... ignoreProperties)
            throws BeansException {
        Assert.notNull(from, "Source must not be null");
        Assert.notNull(to, "Target must not be null");

        Class<?> actualEditable = to.getClass();
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(from.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(from);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            // 这里判断value是否为空 当然这里也能进行一些特殊要求的处理
                            // 例如绑定时格式转换等等
                            if (value != null) {
                                if (!Modifier.isPublic(writeMethod
                                        .getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                writeMethod.invoke(to, value);
                            }
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

    public static boolean isFileParameter(Parameter parameter) {
        Class<?> type = parameter.getType();
        boolean pojo = PluginUtil.isPojo(type);
        if (pojo) {
            AtomicInteger fileCount = new AtomicInteger();
            ReflectionUtils.doWithFields(type, field -> {
                String fieldType = field.getType().getSimpleName();
                if (fieldType.contains("MultipartFile")) {
                    fileCount.incrementAndGet();
                }
            });
            return fileCount.get() > 0;
        } else {
            String parameterType = PluginUtil.getParameterType(parameter);
            return parameterType.equals("file") || parameterType.equals("file[]");
        }
    }

    /**
     * 字段是否包含某些注解
     *
     * @param field               字段
     * @param annotationClassname 注解名称
     * @return 如果有返回true
     */
    public static boolean hasAnyAnnotation(Field field, List<String> annotationClassname) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            String annotationClassName = annotation.annotationType().getName();
            for (String name : annotationClassname) {
                if (annotationClassName.contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isEnableFeature(TornaConfig config, PushFeature feature) {
        List<String> features = config.getFeatures();
        if (features == null) {
            return false;
        }
        for (String feat : features) {
            if (feature.name().equalsIgnoreCase(feat)) {
                return true;
            }
        }
        return false;
    }

}
