package cn.torna.swaggerplugin;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ClassTest {

    @Test
    public void test() {
        List<Class<?>> parents = getSuperclass(RedApple.class);
        List<Class<?>> parents2 = getSuperclass(Apple.class);
        List<Class<?>> parents3 = getSuperclass(Food.class);
        System.out.println(parents);
        System.out.println(parents2);
        System.out.println(parents3);
    }

    private static List<Class<?>> getSuperclass(Class<?> clazz) {
        List<Class<?>> parents = new ArrayList<>();
        findSuperclass(clazz, parents);
        return parents;
    }

    private static void findSuperclass(Class<?> clazz, List<Class<?>> parents) {
        if (clazz == null) {
            return;
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != Object.class) {
            parents.add(superclass);
            findSuperclass(superclass, parents);
        }
    }

    static class Food {}
    static class Apple extends Food {}
    static class RedApple extends Apple {}

}
