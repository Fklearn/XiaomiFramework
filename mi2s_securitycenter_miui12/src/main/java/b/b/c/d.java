package b.b.c;

import android.content.pm.ApplicationInfo;
import b.b.o.g.c;
import java.util.List;

public class d {
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
}
