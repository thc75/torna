package cn.torna.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author tanghc
 */
public class ThreadPoolUtil {

    private static final int POOL_SIZE = 2;

    private static final ExecutorService THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    public static void execute(Runnable runnable) {
        getPool().execute(runnable);
    }

    public static ExecutorService getPool() {
        return THREAD_POOL_EXECUTOR;
    }

}
