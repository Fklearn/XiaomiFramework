package com.xiaomi.analytics.a.a;

import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class n {

    /* renamed from: a  reason: collision with root package name */
    private static ThreadPoolExecutor f8291a;

    /* renamed from: b  reason: collision with root package name */
    private static int f8292b = Runtime.getRuntime().availableProcessors();

    /* renamed from: c  reason: collision with root package name */
    public static final ExecutorService f8293c = Executors.newSingleThreadExecutor();

    static {
        int i = f8292b;
        f8291a = new ThreadPoolExecutor(i, i, 1, TimeUnit.SECONDS, new LinkedBlockingQueue());
        f8291a.allowCoreThreadTimeOut(true);
    }

    public static void a(Runnable runnable) {
        try {
            f8291a.execute(runnable);
        } catch (Exception e) {
            Log.e(a.a("TaskRunner"), "execute e", e);
        }
    }
}
