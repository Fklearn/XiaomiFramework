package b.b.c;

import android.os.Build;
import android.util.Log;
import b.b.o.g.e;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static final String f1618a = "c";

    /* renamed from: b  reason: collision with root package name */
    public static int f1619b;

    /* renamed from: c  reason: collision with root package name */
    public static int f1620c;

    static {
        int intValue;
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                Class<?> cls = Class.forName("android.system.OsConstants");
                f1619b = ((Integer) e.a(cls, "AF_INET", Integer.class)).intValue();
                intValue = ((Integer) e.a(cls, "AF_INET6", Integer.class)).intValue();
            } else {
                Class<?> cls2 = Class.forName("libcore.io.OsConstants");
                f1619b = ((Integer) e.a(cls2, "AF_INET", Integer.class)).intValue();
                intValue = ((Integer) e.a(cls2, "AF_INET6", Integer.class)).intValue();
            }
            f1620c = intValue;
        } catch (Exception e) {
            Log.e(f1618a, "OsConstants", e);
        }
    }
}
