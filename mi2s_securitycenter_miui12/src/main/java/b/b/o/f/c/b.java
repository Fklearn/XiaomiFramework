package b.b.o.f.c;

import android.content.Context;
import android.net.ConnectivityManager;
import b.b.o.g.e;

public class b extends a {

    /* renamed from: a  reason: collision with root package name */
    private static b f1884a;

    /* renamed from: b  reason: collision with root package name */
    private ConnectivityManager f1885b;

    private b(Context context) {
        this.f1885b = (ConnectivityManager) context.getSystemService("connectivity");
    }

    public static synchronized b a(Context context) {
        b bVar;
        synchronized (b.class) {
            if (f1884a == null) {
                f1884a = new b(context);
            }
            bVar = f1884a;
        }
        return bVar;
    }

    public void a(boolean z) {
        try {
            e.a((Object) this.f1885b, "setMobileDataEnabled", (Class<?>[]) new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean a() {
        try {
            return ((Boolean) e.a((Object) this.f1885b, "getMobileDataEnabled", (Class<?>[]) null, new Object[0])).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
