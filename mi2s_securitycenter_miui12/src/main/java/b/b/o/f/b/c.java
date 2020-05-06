package b.b.o.f.b;

import android.app.ActivityManager;
import android.content.Context;
import b.b.o.g.e;

public class c extends a {

    /* renamed from: a  reason: collision with root package name */
    private static c f1882a;

    /* renamed from: b  reason: collision with root package name */
    private Context f1883b;

    private c(Context context) {
        this.f1883b = context;
    }

    public static synchronized c b(Context context) {
        c cVar;
        synchronized (c.class) {
            if (f1882a == null) {
                f1882a = new c(context);
            }
            cVar = f1882a;
        }
        return cVar;
    }

    public void a(int i) {
        ActivityManager activityManager = (ActivityManager) this.f1883b.getSystemService("activity");
        try {
            e.a((Object) activityManager, "removeTask", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
