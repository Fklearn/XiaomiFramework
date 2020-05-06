package b.b.f;

import android.content.Context;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.e;
import com.miui.appmanager.AppManageUtils;

public class a {
    public static int a(Context context, String str) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str, 0);
            if (packageInfo != null) {
                return packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("GreenGuardManager", "get " + str + " version code failed. ", e);
        }
        return 0;
    }

    public static boolean a(Context context) {
        try {
            return context.getPackageManager().getPackageInfo("com.miui.greenguard", 0) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("GreenGuardManager", "check greenguard is installed failed.");
        }
    }

    public static boolean a(String str) {
        return TextUtils.equals(str, "com.miui.greenguard");
    }

    public static boolean b(Context context) {
        return a(context, "com.miui.securitycore") > 13 && !d(context);
    }

    public static void c(Context context) {
        int a2;
        if (!(Settings.Secure.getInt(context.getContentResolver(), "green_guard_uninstalled", 0) == 1) && a(context) && b(context) && (a2 = a(context, "com.miui.greenguard")) != 0) {
            try {
                AppManageUtils.a(e.a(Class.forName("android.content.pm.IPackageManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "package")), "com.miui.greenguard", a2, (IPackageDeleteObserver) null, 0, 4);
                Settings.Secure.putInt(context.getContentResolver(), "green_guard_uninstalled", 1);
            } catch (Exception e) {
                Log.e("GreenGuardManager", "uninstall GreenGuard error. ", e);
            }
        }
    }

    private static boolean d(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "green_kid_active", 0) == 1;
    }
}
