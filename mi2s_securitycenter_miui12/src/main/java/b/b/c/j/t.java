package b.b.c.j;

import b.b.o.g.e;

public class t {

    /* renamed from: a  reason: collision with root package name */
    private static boolean f1761a;

    static {
        try {
            Object a2 = e.a(Class.forName("miui.mqsas.sdk.MQSEventManagerDelegate"), "getInstance", (Class<?>[]) null, new Object[0]);
            if (a2 != null) {
                if (a2.getClass().getDeclaredMethod("reportEventV2", new Class[]{String.class, String.class, String.class, Boolean.TYPE}) != null) {
                    f1761a = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            f1761a = false;
        }
    }

    public static void a(String str, String str2, String str3, boolean z) {
        try {
            Object a2 = e.a(Class.forName("miui.mqsas.sdk.MQSEventManagerDelegate"), "getInstance", (Class<?>[]) null, new Object[0]);
            if (a2 != null) {
                e.a(a2, "reportEventV2", (Class<?>[]) new Class[]{String.class, String.class, String.class, Boolean.TYPE}, str, str2, str3, Boolean.valueOf(z));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean a() {
        return f1761a;
    }
}
