package cn.torna.swaggerplugin.util;

import cn.torna.swaggerplugin.scaner.ClassScanner;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.Principal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;


/**
 * 扫描2包下的所有类
 * <p>Title: ClassUtil.java</p>
 * <p>Description: </p>
 *
 * @author lichao1
 * @version 1.0
 * @date 2018年12月3日
 */
public class ClassUtil {

    /**
     * 从包package中获取所有的Class
     * @param packageName
     * @return 返回class集合
     */
    public static Set<Class<?>> getClasses(String packageName, Class<?> scanClass) {
        try {
            return new ClassScanner(new String[]{packageName}, scanClass).getClassSet();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Collections.emptySet();
    }

    /**
     * 获取自身以及上层父类class，父类的父类也算，不包括Object
     * @param clazz 当前class
     * @return 没有返回空
     */
    public static List<Class<?>> getAllClasses(Class<?> clazz) {
        List<Class<?>> allClasses = new ArrayList<>();
        allClasses.add(clazz);
        findSuperclass(clazz, allClasses);
        return allClasses;
    }

    private static void findSuperclass(Class<?> clazz, List<Class<?>> allClasses) {
        if (clazz == null) {
            return;
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != Object.class) {
            allClasses.add(superclass);
            findSuperclass(superclass, allClasses);
        }
    }

    public static boolean isSpecialType(Class<?> paramType) {
        // 特殊参数
        boolean special = (
                WebRequest.class.isAssignableFrom(paramType) ||
                        MultipartRequest.class.isAssignableFrom(paramType) ||
                        Principal.class.isAssignableFrom(paramType) ||
                        InputStream.class.isAssignableFrom(paramType) ||
                        Reader.class.isAssignableFrom(paramType) ||
                        HttpMethod.class == paramType ||
                        Locale.class == paramType ||
                        TimeZone.class == paramType ||
                        ZoneId.class == paramType ||
                        OutputStream.class.isAssignableFrom(paramType) ||
                        Writer.class.isAssignableFrom(paramType)
        );
        return paramType.getName().startsWith("javax") || special;
    }

    /**
     * Check if it is the basic data type of json data
     *
     * @param type0 java class name
     * @return boolean
     */
    public static boolean isPrimitive(String type0) {
        if (Objects.isNull(type0)) {
            return true;
        }
        String type = type0.contains("java.lang") ? type0.substring(type0.lastIndexOf(".") + 1, type0.length()) : type0;
        type = type.toLowerCase();
        switch (type) {
            case "string":
            case "integer":
            case "int":
            case "object":
            case "void":
            case "long":
            case "double":
            case "float":
            case "short":
            case "bigdecimal":
            case "char":
            case "character":
            case "number":
            case "boolean":
            case "byte":
            case "uuid":
            case "biginteger":
            case "java.sql.timestamp":
            case "java.util.date":
            case "java.time.localdatetime":
            case "java.time.localtime":
            case "localtime":
            case "date":
            case "localdatetime":
            case "localdate":
            case "zoneddatetime":
            case "java.time.localdate":
            case "java.time.zoneddatetime":
            case "java.math.bigdecimal":
            case "java.math.biginteger":
            case "java.util.uuid":
            case "java.io.serializable":
                return true;
            default:
                return false;
        }
    }

//    public static void main(String[] args) {
//        Set<Class<?>> classes = getClasses("cn.torna.swaggerplugin", Api.class);
//        classes.forEach(System.out::println);
//    }

}