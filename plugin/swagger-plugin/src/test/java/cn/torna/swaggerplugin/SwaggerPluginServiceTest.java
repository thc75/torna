package cn.torna.swaggerplugin;

import cn.torna.swaggerplugin.bean.TornaConfig;
import cn.torna.swaggerplugin.util.ClassUtil;
import io.swagger.annotations.Api;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * @author 六如
 */
public class SwaggerPluginServiceTest {

    @Test
    public void exclude() {
        TornaConfig tornaConfig = new TornaConfig();
        tornaConfig.setExcludePackage("cn.torna.swaggerplugin.pkg1.pkg2;cn.torna.swaggerplugin.pkg1.pkg3");
        tornaConfig.setBasePackage("cn.torna.swaggerplugin.pkg1");
        tornaConfig.setScanApis(Arrays.asList("cn.torna.swaggerplugin.pkg1.UserInterface"));
        SwaggerPluginService swaggerPluginService = new SwaggerPluginService(tornaConfig);
        Set<Class<?>> clazzs = ClassUtil.getClasses(tornaConfig.getBasePackage(), Api.class);
        for (Class<?> clazz : clazzs) {
            for (Method method : clazz.getMethods()) {
                if (swaggerPluginService.match(method)) {
                    System.out.println(method);
                }
            }
        }
    }



}
