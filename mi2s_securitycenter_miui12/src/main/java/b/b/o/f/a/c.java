package b.b.o.f.a;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import b.b.o.g.c;

public class c extends a {

    /* renamed from: a  reason: collision with root package name */
    private static c f1876a;

    private c() {
    }

    public static synchronized c a() {
        c cVar;
        synchronized (c.class) {
            if (f1876a == null) {
                f1876a = new c();
            }
            cVar = f1876a;
        }
        return cVar;
    }

    public boolean a(IBinder iBinder, int i, Intent intent) {
        try {
            c.a a2 = c.a.a("android.app.ActivityManagerNative");
            a2.b("getDefault", (Class<?>[]) null, new Object[0]);
            a2.e();
            a2.a("finishActivity", new Class[]{IBinder.class, Integer.TYPE, Intent.class, Boolean.TYPE}, iBinder, Integer.valueOf(i), intent, false);
            return a2.a();
        } catch (Exception e) {
            Log.e("IActivityManager_lte21", " finishActivity error ", e);
            return false;
        }
    }
}
