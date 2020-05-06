package com.miui.internal.vip.utils;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RunnableHelper {
    private static final Executor sBgThread = createSingleThreadPool();
    private static final Handler sUIHandler = new Handler(Looper.getMainLooper());

    private static ThreadFactory getThreadFactory() {
        return new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setPriority(3);
                return thread;
            }
        };
    }

    public static Executor createSingleThreadPool() {
        return new ThreadPoolExecutor(0, 1, 1, TimeUnit.MINUTES, new LinkedBlockingDeque(), getThreadFactory(), new RejectedExecutionHandler() {
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                Utils.logI("runnable %s is rejected by current pool %s", r, executor);
            }
        });
    }

    public static void runInUIThread(Runnable task) {
        sUIHandler.post(task);
    }

    public static void runInBackground(Runnable task) {
        if (Utils.isInMainThread()) {
            sBgThread.execute(task);
        } else {
            task.run();
        }
    }
}
