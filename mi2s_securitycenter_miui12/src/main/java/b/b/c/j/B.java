package b.b.c.j;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import b.b.o.g.c;
import b.b.o.g.e;
import miui.securitycenter.utils.SecurityCenterHelper;

public class B {

    /* renamed from: a  reason: collision with root package name */
    private static final String f1752a = "B";

    /* renamed from: b  reason: collision with root package name */
    private static int f1753b;

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Integer num = (Integer) e.a((Class<?>) UserHandle.class, "myUserId", (Class<?>[]) null, new Object[0]);
                if (num != null) {
                    f1753b = num.intValue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int a() {
        c.a a2 = c.a.a("android.os.UserHandle");
        a2.b("getCallingUserId", (Class<?>[]) null, new Object[0]);
        return a2.c();
    }

    public static final int a(int i) {
        c.a a2 = c.a.a("android.os.UserHandle");
        a2.b("getAppId", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        return a2.c();
    }

    public static final int a(int i, int i2) {
        c.a a2 = c.a.a("android.os.UserHandle");
        Class cls = Integer.TYPE;
        a2.b("getUid", new Class[]{cls, cls}, Integer.valueOf(i), Integer.valueOf(i2));
        return a2.c();
    }

    public static int a(ContentResolver contentResolver, String str, int i, int i2) {
        c.a a2 = c.a.a("android.provider.Settings$Secure");
        Class cls = Integer.TYPE;
        a2.b("getIntForUser", new Class[]{ContentResolver.class, String.class, cls, cls}, contentResolver, str, Integer.valueOf(i), Integer.valueOf(i2));
        return a2.c();
    }

    public static boolean a(Context context) {
        return a(context.getContentResolver(), "second_user_id", UserHandle.USER_NULL, 0) != -10000;
    }

    public static boolean a(Context context, int i) {
        try {
            c.a a2 = c.a.a((Object) (UserManager) context.getSystemService("user"));
            a2.a("getUserInfo", new Class[]{Integer.TYPE}, Integer.valueOf(i));
            a2.e();
            a2.a("isManagedProfile", (Class<?>[]) null, new Object[0]);
            return a2.a();
        } catch (Exception e) {
            Log.e(f1752a, "isManagedProfile exception: ", e);
            return false;
        }
    }

    public static int b(int i) {
        return i == 0 ? 0 : 1;
    }

    public static UserHandle b() {
        return e(-2);
    }

    public static int c() {
        c.a a2 = c.a.a("miui.securityspace.CrossUserUtils");
        a2.b("getCurrentUserId", (Class<?>[]) null, new Object[0]);
        return a2.c();
    }

    public static int c(int i) {
        return SecurityCenterHelper.getUserId(i);
    }

    public static UserHandle d() {
        return e(-1);
    }

    public static boolean d(int i) {
        return c(i) == 0;
    }

    public static int e() {
        return UserHandle.USER_NULL;
    }

    public static UserHandle e(int i) {
        try {
            return UserHandle.class.getConstructor(new Class[]{Integer.TYPE}).newInstance(new Object[]{Integer.valueOf(i)});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final boolean f() {
        return j() == 0;
    }

    public static boolean g() {
        return c() != 0;
    }

    public static boolean h() {
        c.a a2 = c.a.a("miui.securityspace.ConfigUtils");
        a2.b("isSupportSecuritySpace", (Class<?>[]) null, new Object[0]);
        return a2.a();
    }

    public static final boolean i() {
        return j() == 999;
    }

    public static final int j() {
        return f1753b;
    }

    public static UserHandle k() {
        return e(0);
    }
}
