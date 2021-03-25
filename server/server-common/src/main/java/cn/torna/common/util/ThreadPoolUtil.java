package cn.torna.common.util;

import cn.torna.common.context.SpringContext;
import org.springframework.scheduling.annotation.AsyncConfigurer;

/**
 * @author tanghc
 */
public class ThreadPoolUtil {

    public static void execute(Runnable runnable) {
        AsyncConfigurer asyncConfigurer = SpringContext.getBean(AsyncConfigurer.class);
        asyncConfigurer.getAsyncExecutor().execute(runnable);
    }

}
