package b.b.o.f.a;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import b.b.o.g.c;

public class b extends a {

    /* renamed from: a  reason: collision with root package name */
    private static b f1875a;

    private b() {
    }

    public static synchronized b a() {
        b bVar;
        synchronized (b.class) {
            if (f1875a == null) {
                f1875a = new b();
            }
            bVar = f1875a;
        }
        return bVar;
    }

    public boolean a(IBinder iBinder, int i, Intent intent) {
        try {
            c.a a2 = c.a.a("android.app.ActivityManagerNative");
            a2.b("getDefault", (Class<?>[]) null, new Object[0]);
            a2.e();
            a2.a("finishActivity", new Class[]{IBinder.class, Integer.TYPE, Intent.class}, iBinder, Integer.valueOf(i), intent);
            return a2.a();
        } catch (Exception e) {
            Log.e("IActivityManager_lte19", " finishActivity error ", e);
            return false;
        }
    }
}
