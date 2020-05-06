package b.b.c.c.a;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static ExecutorService f1621a = Executors.newFixedThreadPool(f1622b);

    /* renamed from: b  reason: collision with root package name */
    private static int f1622b = 10;

    public static void a(Runnable runnable) {
        f1621a.execute(runnable);
    }
}
