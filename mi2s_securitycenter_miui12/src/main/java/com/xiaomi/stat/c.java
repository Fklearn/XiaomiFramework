package com.xiaomi.stat;

import android.os.Handler;
import android.os.HandlerThread;
import com.xiaomi.stat.d.k;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8459a = "DBExecutor";

    /* renamed from: b  reason: collision with root package name */
    private static String f8460b = "mistat_db";

    /* renamed from: c  reason: collision with root package name */
    private static final String f8461c = "mistat";

    /* renamed from: d  reason: collision with root package name */
    private static final String f8462d = "db.lk";
    private static Handler e;
    private static FileLock f;
    private static FileChannel g;

    private static class a implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private Runnable f8463a;

        public a(Runnable runnable) {
            this.f8463a = runnable;
        }

        public void run() {
            if (c.d()) {
                Runnable runnable = this.f8463a;
                if (runnable != null) {
                    runnable.run();
                }
                c.e();
            }
        }
    }

    public static void a(Runnable runnable) {
        c();
        e.post(new a(runnable));
    }

    private static void c() {
        if (e == null) {
            synchronized (c.class) {
                if (e == null) {
                    HandlerThread handlerThread = new HandlerThread(f8460b);
                    handlerThread.start();
                    e = new Handler(handlerThread.getLooper());
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static boolean d() {
        File file = new File(ak.a().getFilesDir(), f8461c);
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            try {
                g = new FileOutputStream(new File(file, f8462d)).getChannel();
                f = g.lock();
                k.c(f8459a, "acquire lock for db");
                return true;
            } catch (Exception e2) {
                k.c(f8459a, "acquire lock for db failed with " + e2);
                try {
                    g.close();
                    g = null;
                } catch (Exception e3) {
                    k.c(f8459a, "close file stream failed with " + e3);
                }
                return false;
            }
        } catch (Exception e4) {
            k.c(f8459a, "acquire lock for db failed with " + e4);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public static void e() {
        try {
            if (f != null) {
                f.release();
                f = null;
            }
            k.c(f8459a, "release sDBFileLock for db");
        } catch (Exception e2) {
            k.c(f8459a, "release sDBFileLock for db failed with " + e2);
        }
        try {
            if (g != null) {
                g.close();
                g = null;
            }
            k.c(f8459a, "release sLockFileChannel for db");
        } catch (Exception e3) {
            k.c(f8459a, "release sLockFileChannel for db failed with " + e3);
        }
    }
}
