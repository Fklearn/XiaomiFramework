package miui.yellowpage;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    private static int THREAD_POOL_COUNT = 32;
    private static ThreadPoolExecutor sExecutor;

    static {
        int i = THREAD_POOL_COUNT;
        sExecutor = new ThreadPoolExecutor(i, i, 1, TimeUnit.SECONDS, new LinkedBlockingQueue());
        sExecutor.allowCoreThreadTimeOut(true);
    }

    private ThreadPool() {
    }

    public static void execute(Runnable runnable) {
        sExecutor.execute(runnable);
    }
}
