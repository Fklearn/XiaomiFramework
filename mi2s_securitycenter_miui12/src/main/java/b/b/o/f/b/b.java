package b.b.o.f.b;

import android.app.ActivityManager;
import android.content.Context;
import b.b.o.g.e;

public class b extends a {

    /* renamed from: a  reason: collision with root package name */
    private static b f1880a;

    /* renamed from: b  reason: collision with root package name */
    private Context f1881b;

    private b(Context context) {
        this.f1881b = context;
    }

    public static synchronized b b(Context context) {
        b bVar;
        synchronized (b.class) {
            if (f1880a == null) {
                f1880a = new b(context);
            }
            bVar = f1880a;
        }
        return bVar;
    }

    public void a(int i) {
        ActivityManager activityManager = (ActivityManager) this.f1881b.getSystemService("activity");
        try {
            e.a((Object) activityManager, "removeTask", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE}, Integer.valueOf(i), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
