package b.c.a.b;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import b.c.a.a.a.a.a.d;
import b.c.a.a.a.a.b;
import b.c.a.b.c.c;
import b.c.a.c.g;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class a {

    /* renamed from: b.c.a.b.a$a  reason: collision with other inner class name */
    private static class C0032a implements ThreadFactory {

        /* renamed from: a  reason: collision with root package name */
        private static final AtomicInteger f1947a = new AtomicInteger(1);

        /* renamed from: b  reason: collision with root package name */
        private final ThreadGroup f1948b;

        /* renamed from: c  reason: collision with root package name */
        private final AtomicInteger f1949c = new AtomicInteger(1);

        /* renamed from: d  reason: collision with root package name */
        private final String f1950d;
        private final int e;

        C0032a(int i, String str) {
            this.e = i;
            this.f1948b = Thread.currentThread().getThreadGroup();
            this.f1950d = str + f1947a.getAndIncrement() + "-thread-";
        }

        public Thread newThread(Runnable runnable) {
            ThreadGroup threadGroup = this.f1948b;
            Thread thread = new Thread(threadGroup, runnable, this.f1950d + this.f1949c.getAndIncrement(), 0);
            if (thread.isDaemon()) {
                thread.setDaemon(false);
            }
            thread.setPriority(this.e);
            return thread;
        }
    }

    @TargetApi(11)
    private static int a(ActivityManager activityManager) {
        return activityManager.getLargeMemoryClass();
    }

    public static b.c.a.a.a.a a(Context context, b.c.a.a.a.b.a aVar, long j, int i) {
        File b2 = b(context);
        if (j > 0 || i > 0) {
            File b3 = g.b(context);
            File file = new File(context.getCacheDir(), "imgloader");
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                return new d(b3, b2, aVar, j, i, file);
            } catch (IOException e) {
                b.c.a.c.d.a((Throwable) e);
            }
        }
        return new b(g.a(context), b2, aVar);
    }

    public static b.c.a.a.b.a a(Context context, int i) {
        if (i == 0) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
            int memoryClass = activityManager.getMemoryClass();
            if (d() && c(context)) {
                memoryClass = a(activityManager);
            }
            i = (memoryClass * ExtractorMediaSource.DEFAULT_LOADING_CHECK_INTERVAL_BYTES) / 8;
        }
        return new b.c.a.a.b.a.b(i);
    }

    public static b.c.a.b.b.b a(boolean z) {
        return new b.c.a.b.b.a(z);
    }

    public static b.c.a.b.c.a a() {
        return new c();
    }

    public static b.c.a.b.d.d a(Context context) {
        return new b.c.a.b.d.b(context);
    }

    public static Executor a(int i, int i2, b.c.a.b.a.g gVar) {
        return new ThreadPoolExecutor(i, i, 0, TimeUnit.MILLISECONDS, gVar == b.c.a.b.a.g.LIFO ? new b.c.a.b.a.a.c() : new LinkedBlockingQueue(), a(i2, "uil-pool-"));
    }

    private static ThreadFactory a(int i, String str) {
        return new C0032a(i, str);
    }

    public static b.c.a.a.a.b.a b() {
        return new b.c.a.a.a.b.b();
    }

    private static File b(Context context) {
        File a2 = g.a(context, false);
        File file = new File(a2, "uil-images");
        return (file.exists() || file.mkdir()) ? file : a2;
    }

    public static Executor c() {
        return Executors.newCachedThreadPool(a(5, "uil-pool-d-"));
    }

    @TargetApi(11)
    private static boolean c(Context context) {
        return (context.getApplicationInfo().flags & ExtractorMediaSource.DEFAULT_LOADING_CHECK_INTERVAL_BYTES) != 0;
    }

    private static boolean d() {
        return Build.VERSION.SDK_INT >= 11;
    }
}
