package cn.torna.swaggerplugin;

import cn.torna.swaggerplugin.util.PluginUtil;
import junit.framework.TestCase;
import lombok.Data;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 六如
 */
public class GenericParadigmUtilTest extends TestCase {


    @Test
    public void testA() {
        Class<Controller> controllerClass = Controller.class;
        Method method = ReflectionUtils.findMethod(controllerClass, "page");
        Type genericReturnType = method.getGenericReturnType();

        ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) genericReturnType;
        Type rawType = parameterizedType.getRawType();
        Type target = parameterizedType.getActualTypeArguments()[0];
        Class targetClass;
        if (target instanceof ParameterizedTypeImpl) {
            ParameterizedTypeImpl parameterizedTypeTarget = (ParameterizedTypeImpl) target;
            targetClass = parameterizedTypeTarget.getRawType();
            System.out.println(rawType.getTypeName() + ":" + targetClass.getName());
        } else {
            targetClass = (Class) target;
        }

    }

    @Test
    public void testB() {
        Class<Controller> controllerClass = Controller.class;
        Method method = ReflectionUtils.findMethod(controllerClass, "page");
        Type genericReturnType = method.getGenericReturnType();

        Map<String, Class<?>> classMap = new HashMap<>();

        PluginUtil.appendGenericParamMap(classMap, genericReturnType);

        System.out.println(classMap);

    }





    public static class Controller {

        public Result<Page<User>> page() {
            return null;
        }
    }

    @Data
    public static class Page<T> {
        private List<T> list;
    }


    @Data
    public static class User {
        private Integer id;
    }


    public static class Result<T> {

    }

}
