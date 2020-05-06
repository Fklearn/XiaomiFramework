package b.b.o.b.a;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import b.b.c.j.B;
import b.b.o.g.c;
import b.b.o.g.e;
import java.util.List;

public class a {
    public static PackageInfo a(String str, int i, int i2) {
        try {
            c.a a2 = c.a.a("android.app.ActivityThread");
            a2.b("getPackageManager", (Class<?>[]) null, new Object[0]);
            a2.e();
            a2.a("getPackageInfo", new Class[]{String.class, Integer.TYPE, Integer.TYPE}, str, Integer.valueOf(i), Integer.valueOf(i2));
            return (PackageInfo) a2.d();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ApplicationInfo> a(int i, int i2) {
        try {
            c.a a2 = c.a.a("android.app.ActivityThread");
            a2.b("getPackageManager", (Class<?>[]) null, new Object[0]);
            a2.e();
            a2.a("getInstalledApplications", new Class[]{Integer.TYPE, Integer.TYPE}, Integer.valueOf(i), Integer.valueOf(i2));
            a2.e();
            a2.a("getList", (Class<?>[]) null, new Object[0]);
            return (List) a2.d();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void a(PackageManager packageManager, String str, IPackageDeleteObserver iPackageDeleteObserver, int i) {
        Class[] clsArr = {String.class, IPackageDeleteObserver.class, Integer.TYPE};
        Class<PackageManager> cls = PackageManager.class;
        try {
            e.a((Object) packageManager, "deletePackage", (Class<?>) cls, (Class<?>[]) clsArr, str, iPackageDeleteObserver, Integer.valueOf(i));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void a(Object obj, String str, int i, IPackageDeleteObserver iPackageDeleteObserver, int i2, int i3) {
        if (Build.VERSION.SDK_INT > 25) {
            Class cls = Integer.TYPE;
            Class[] clsArr = {String.class, cls, IPackageDeleteObserver.class, cls, cls};
            try {
                e.a(obj, "deletePackageAsUser", (Class<?>[]) clsArr, str, Integer.valueOf(i), iPackageDeleteObserver, Integer.valueOf(i2), Integer.valueOf(i3));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Class cls2 = Integer.TYPE;
            Class[] clsArr2 = {String.class, IPackageDeleteObserver.class, cls2, cls2};
            e.a(obj, "deletePackageAsUser", (Class<?>[]) clsArr2, str, iPackageDeleteObserver, Integer.valueOf(i2), Integer.valueOf(i3));
        }
    }

    public static boolean a(Object obj, String str) {
        if (!B.f()) {
            return false;
        }
        try {
            return ((PackageInfo) e.a(obj, "getPackageInfo", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE}, str, 0, 999)) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
