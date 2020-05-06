package b.b.o.f.a;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import b.b.o.g.c;

public class d extends a {

    /* renamed from: a  reason: collision with root package name */
    private static d f1877a;

    private d() {
    }

    public static synchronized d a() {
        d dVar;
        synchronized (d.class) {
            if (f1877a == null) {
                f1877a = new d();
            }
            dVar = f1877a;
        }
        return dVar;
    }

    public boolean a(IBinder iBinder, int i, Intent intent) {
        try {
            c.a a2 = c.a.a("android.app.ActivityManagerNative");
            a2.b("getDefault", (Class<?>[]) null, new Object[0]);
            a2.e();
            a2.a("finishActivity", new Class[]{IBinder.class, Integer.TYPE, Intent.class, Integer.TYPE}, iBinder, Integer.valueOf(i), intent, 0);
            return a2.a();
        } catch (Exception e) {
            Log.e("IActivityManager_lte24", " finishActivity error ", e);
            return false;
        }
    }
}
