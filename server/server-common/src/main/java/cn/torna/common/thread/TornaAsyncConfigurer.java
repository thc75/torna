package cn.torna.common.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步执行配置
 *
 * @author tanghc
 */
@Slf4j
public class TornaAsyncConfigurer implements AsyncConfigurer {

    private final ThreadPoolExecutor threadPoolExecutor;

    public TornaAsyncConfigurer(String threadName, int poolSize) {
        threadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new MyNamedThreadFactory(threadName));
    }

    @Override
    public Executor getAsyncExecutor() {
        return threadPoolExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable e, Method method, Object... args) -> {
            log.error("异步运行方法出错, method:{}, args:{}, message:{}", method, Arrays.deepToString(args), e.getMessage(), e);
        };
    }
}
