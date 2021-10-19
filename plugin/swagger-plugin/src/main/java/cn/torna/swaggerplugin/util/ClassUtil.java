package cn.torna.swaggerplugin.util;

import cn.torna.swaggerplugin.scaner.ClassScanner;

import java.io.IOException;
import java.util.Collections;
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

//    public static void main(String[] args) {
//        Set<Class<?>> classes = getClasses("cn.torna.swaggerplugin", Api.class);
//        classes.forEach(System.out::println);
//    }

}