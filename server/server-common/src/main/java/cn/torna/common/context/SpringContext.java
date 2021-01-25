package cn.torna.common.context;

import org.springframework.context.ApplicationContext;

/**
 * @author tanghc
 */
public class SpringContext {

    private static ApplicationContext ctx;

    public static <T> T getBean(Class<T> clazz) {
        return ctx.getBean(clazz);
    }

    public static Object getBean(String beanName) {
        return ctx.getBean(beanName);
    }

    public static void setApplicationContext(ApplicationContext ctx) {
        SpringContext.ctx = ctx;
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }
}
