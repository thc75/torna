package cn.torna.swaggerplugin.util;

import cn.torna.swaggerplugin.scaner.ClassScanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


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

//    public static void main(String[] args) {
//        Set<Class<?>> classes = getClasses("cn.torna.swaggerplugin", Api.class);
//        classes.forEach(System.out::println);
//    }

}