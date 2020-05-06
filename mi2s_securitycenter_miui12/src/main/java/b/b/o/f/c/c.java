package b.b.o.f.c;

import android.content.Context;
import android.telephony.TelephonyManager;
import b.b.o.g.e;

public class c extends a {

    /* renamed from: a  reason: collision with root package name */
    private static c f1886a;

    /* renamed from: b  reason: collision with root package name */
    private TelephonyManager f1887b;

    private c(Context context) {
        this.f1887b = (TelephonyManager) context.getSystemService("phone");
    }

    public static synchronized c a(Context context) {
        c cVar;
        synchronized (c.class) {
            if (f1886a == null) {
                f1886a = new c(context);
            }
            cVar = f1886a;
        }
        return cVar;
    }

    public void a(boolean z) {
        try {
            e.a((Object) this.f1887b, "setDataEnabled", (Class<?>[]) new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean a() {
        try {
            return ((Boolean) e.a((Object) this.f1887b, "getDataEnabled", (Class<?>[]) null, new Object[0])).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
