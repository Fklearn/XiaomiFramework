package b.b.d;

import android.content.Context;
import android.util.Log;
import java.lang.reflect.Method;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static Object f1771a;

    /* renamed from: b  reason: collision with root package name */
    private static Class<?> f1772b;

    /* renamed from: c  reason: collision with root package name */
    private static Method f1773c;

    /* renamed from: d  reason: collision with root package name */
    private static Method f1774d;
    private static Method e;
    private static Method f;

    static {
        try {
            f1772b = Class.forName("com.android.id.impl.IdProviderImpl");
            f1771a = f1772b.newInstance();
            f1773c = f1772b.getMethod("getUDID", new Class[]{Context.class});
            f1774d = f1772b.getMethod("getOAID", new Class[]{Context.class});
            e = f1772b.getMethod("getVAID", new Class[]{Context.class});
            f = f1772b.getMethod("getAAID", new Class[]{Context.class});
        } catch (Exception e2) {
            Log.e("IdentifierManager", "reflect exception!", e2);
        }
    }

    public static String a(Context context) {
        return a(context, f1774d);
    }

    private static String a(Context context, Method method) {
        Object obj = f1771a;
        if (obj == null || method == null) {
            return null;
        }
        try {
            Object invoke = method.invoke(obj, new Object[]{context});
            if (invoke != null) {
                return (String) invoke;
            }
            return null;
        } catch (Exception e2) {
            Log.e("IdentifierManager", "invoke exception!", e2);
            return null;
        }
    }

    public static boolean a() {
        return (f1772b == null || f1771a == null) ? false : true;
    }
}
