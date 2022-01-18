package cn.torna.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gitee.fastmybatis.core.support.PageEasyui;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Slf4j
public class CopyUtil extends BeanUtils {

    /**
     * 属性拷贝,第一个参数中的属性值拷贝到第二个参数中<br>
     * 注意:当第一个参数中的属性有null值时,不会拷贝进去
     * @param from 源对象
     * @param to 目标对象
     * @throws BeansException
     */
    public static void copyPropertiesIgnoreNull(Object from, Object to, String ...ignoreProperties)
            throws BeansException {
        Assert.notNull(from, "Source must not be null");
        Assert.notNull(to, "Target must not be null");

        Class<?> actualEditable = to.getClass();
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(from.getClass(), targetPd.getName());
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
                        }
                        catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

    public static void copyProperties(Object from, Object to) {
        BeanUtils.copyProperties(from, to);
    }

    public static <T> T copyBean(Object from, Supplier<T> supplier) {
        Objects.requireNonNull(from);
        T to = supplier.get();
        BeanUtils.copyProperties(from, to);
        return to;
    }

    public static <T> T copyBeanNullable(Object from, Supplier<T> supplier) {
        if (from == null) {
            return supplier.get();
        }
        T to = supplier.get();
        BeanUtils.copyProperties(from, to);
        return to;
    }

    public static <T> T copyBean(Object from, Supplier<T> supplier, Consumer<T> after) {
        if (from == null) {
            return null;
        }
        T to = supplier.get();
        BeanUtils.copyProperties(from, to);
        after.accept(to);
        return to;
    }

    public static <T> List<T> copyList(List<?> fromList, Supplier<T> toElement) {
        if (fromList == null || fromList.isEmpty()) {
            return new ArrayList<>();
        }
        return fromList.stream()
                .map(source -> {
                    T target = toElement.get();
                    BeanUtils.copyProperties(source, target);
                    return target;
                })
                .collect(Collectors.toList());
    }

    public static <T> PageEasyui copyPage(PageEasyui pageInfo, Supplier<T> toElement) {
        if (pageInfo == null || pageInfo.getRows() == null) {
            return pageInfo;
        }
        List list = (List) pageInfo.getRows()
                .stream()
                .map(source -> {
                    Object target = toElement.get();
                    BeanUtils.copyProperties(source, target);
                    return target;
                })
                .collect(Collectors.toList());
        pageInfo.setList(list);
        return pageInfo;
    }

    public static <E, R> List<R> copyList(List<E> fromList, Function<E, R> function) {
        if (fromList == null) {
            return Collections.emptyList();
        }
        return fromList.stream()
                .map(source -> {
                    R target = function.apply(source);
                    BeanUtils.copyProperties(source, target);
                    return target;
                })
                .collect(Collectors.toList());
    }

    public static <T> List<T> copyList(List<?> fromList, Supplier<T> toElement, Consumer<T> after) {
        if (fromList == null) {
            return Collections.emptyList();
        }
        return fromList.stream()
                .map(source -> {
                    T target = toElement.get();
                    BeanUtils.copyProperties(source, target);
                    after.accept(target);
                    return target;
                })
                .collect(Collectors.toList());
    }

    /**
     * 深层次拷贝，通过json转换的方式实现
     *
     * @param from 待转换的类
     * @param toClass 目标类class
     * @param <T> 目标类
     * @return 返回目标类
     */
    public static <T> T deepCopy(Object from, Class<T> toClass) {
        String json = JSON.toJSONString(from, SerializerFeature.WriteDateUseDateFormat);
        return JSON.parseObject(json, toClass);
    }

}
