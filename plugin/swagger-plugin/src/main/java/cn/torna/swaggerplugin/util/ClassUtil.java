package cn.torna.swaggerplugin.util;

import cn.torna.swaggerplugin.scaner.ClasspathPackageScanner;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;


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
     * https://www.cnblogs.com/zhjh256/p/5706648.html
     * @param packages
     * @return
     */
    public static Set<Class<?>> getClasses(String packages) {
        Set<String> classNameList = PackageUtil.findPackageClass(packages);
        return classNameList.stream()
                .map(className -> {
                    try {
                        return ClassUtils.forName(className, null);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

//    public static void main(String[] args) {
//        Set<Class<?>> classes = getClasses("cn.torna.swaggerplugin");
//        classes.forEach(System.out::println);
//    }

}