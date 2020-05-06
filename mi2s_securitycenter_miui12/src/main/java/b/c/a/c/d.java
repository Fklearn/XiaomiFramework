package b.c.a.c;

import android.util.Log;
import b.c.a.b.f;

public final class d {

    /* renamed from: a  reason: collision with root package name */
    private static volatile boolean f2072a = false;

    /* renamed from: b  reason: collision with root package name */
    private static volatile boolean f2073b = true;

    private static void a(int i, Throwable th, String str, Object... objArr) {
        if (f2073b) {
            if (objArr.length > 0) {
                str = String.format(str, objArr);
            }
            if (th != null) {
                if (str == null) {
                    str = th.getMessage();
                }
                str = String.format("%1$s\n%2$s", new Object[]{str, Log.getStackTraceString(th)});
            }
            Log.println(i, f.f2028a, str);
        }
    }

    public static void a(String str, Object... objArr) {
        if (f2072a) {
            a(3, (Throwable) null, str, objArr);
        }
    }

    public static void a(Throwable th) {
        a(6, th, (String) null, new Object[0]);
    }

    public static void a(Throwable th, String str, Object... objArr) {
        a(6, th, str, objArr);
    }

    public static void a(boolean z) {
        f2072a = z;
    }

    public static void b(String str, Object... objArr) {
        a(6, (Throwable) null, str, objArr);
    }

    public static void c(String str, Object... objArr) {
        a(4, (Throwable) null, str, objArr);
    }

    public static void d(String str, Object... objArr) {
        a(5, (Throwable) null, str, objArr);
    }
}
