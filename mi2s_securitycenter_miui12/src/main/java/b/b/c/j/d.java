package b.b.c.j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static ExecutorService f1754a = Executors.newFixedThreadPool(7);

    public static void a(Runnable runnable) {
        f1754a.execute(runnable);
    }
}
