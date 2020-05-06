package com.miui.superpower.b;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import b.b.c.j.B;
import b.b.o.g.c;
import b.b.o.g.e;
import com.miui.appmanager.AppManageUtils;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.firewall.UserConfigure;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.networkassistant.utils.BitmapUtil;
import com.miui.powercenter.utils.o;
import com.miui.securitycenter.R;
import com.miui.superpower.a;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import miui.app.Activity;
import miui.os.SystemProperties;
import miui.util.FeatureParser;

public class k {

    /* renamed from: a  reason: collision with root package name */
    private static Boolean f8091a = null;

    /* renamed from: b  reason: collision with root package name */
    private static int f8092b = -1;

    /* renamed from: c  reason: collision with root package name */
    private static int f8093c = -1;

    /* renamed from: d  reason: collision with root package name */
    private static final boolean f8094d = FeatureParser.getBoolean("support_extreme_battery_saver", false);
    private static final boolean e = FeatureParser.getBoolean("support_superpower_replace_extremesaver", false);

    public static int a() {
        try {
            String str = SystemProperties.get("ro.miui.ui.version.code");
            if (!TextUtils.isEmpty(str)) {
                return Integer.parseInt(str);
            }
            return 0;
        } catch (Exception unused) {
            return 0;
        }
    }

    private static ResolveInfo a(Context context, ActivityManager.RecentTaskInfo recentTaskInfo) {
        Intent intent = new Intent(recentTaskInfo.baseIntent);
        ComponentName componentName = recentTaskInfo.origActivity;
        if (componentName != null) {
            intent.setComponent(componentName);
        }
        intent.setFlags((intent.getFlags() & -2097153) | 268435456);
        return context.getPackageManager().resolveActivity(intent, 0);
    }

    public static String a(Context context, int i, String str) {
        File file = new File(context.getCacheDir(), "superpowernoti_files");
        BitmapUtil.saveDrawableResToFile(context, file, "superpower_icon_for_systemui.png", i);
        Uri uriForFile = FileProvider.getUriForFile(context, ProviderConstant.AUTHORITY_FILE, new File(file, "superpower_icon_for_systemui.png"));
        context.grantUriPermission(str, uriForFile, 1);
        return uriForFile.toString();
    }

    public static List<String> a(PackageManager packageManager, int i, HashSet<String> hashSet) {
        ArrayList arrayList = new ArrayList();
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage((String) null);
        for (ResolveInfo next : AppManageUtils.a(packageManager, intent, 0, i)) {
            if (hashSet == null || hashSet.isEmpty() || !hashSet.contains(next.activityInfo.packageName)) {
                arrayList.add(next.activityInfo.packageName);
            }
        }
        return arrayList;
    }

    public static void a(List<String> list, int i) {
        try {
            Object invoke = Class.forName("android.app.ActivityThread").getMethod("getPackageManager", new Class[0]).invoke((Object) null, new Object[0]);
            for (String str : list) {
                invoke.getClass().getMethod("setPackageStoppedState", new Class[]{String.class, Boolean.TYPE, Integer.TYPE}).invoke(invoke, new Object[]{str, false, Integer.valueOf(i)});
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void a(Activity activity) {
        String str;
        try {
            boolean i = i(activity);
            boolean l = l(activity);
            boolean n = n(activity);
            boolean k = k(activity);
            if (!i || !l || !n || !k) {
                activity.getWindow().clearFlags(134217728);
                str = "clearFlags";
            } else {
                activity.getWindow().addFlags(134217728);
                str = "addFlags";
            }
            Log.i("GestureLine", str);
        } catch (Exception e2) {
            Log.e("GestureLine", "error:", e2);
        }
    }

    public static boolean a(ContentResolver contentResolver) {
        try {
            return Settings.Global.getInt(contentResolver, "airplane_mode_on") == 1;
        } catch (Settings.SettingNotFoundException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static boolean a(Context context) {
        int i;
        if (Build.VERSION.SDK_INT < 26) {
            return false;
        }
        try {
            i = Settings.Secure.getInt(context.getContentResolver(), "shield_super_save_bar", 0);
        } catch (Exception e2) {
            e2.printStackTrace();
            i = 0;
        }
        return i == 1;
    }

    public static String b(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "default_input_method");
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService("input_method");
        if (inputMethodManager == null) {
            return null;
        }
        for (InputMethodInfo next : inputMethodManager.getEnabledInputMethodList()) {
            if (string.contains(next.getPackageName())) {
                return next.getPackageName();
            }
        }
        return null;
    }

    public static boolean b() {
        int i = Build.VERSION.SDK_INT;
        return i >= 24 && i <= 28;
    }

    public static int c(Context context) {
        if (f8092b == -1) {
            f8092b = 0;
            Resources resources = context.getResources();
            int identifier = resources.getIdentifier("navigation_bar_height", "dimen", Constants.System.ANDROID_PACKAGE_NAME);
            if (identifier > 0) {
                f8092b = resources.getDimensionPixelSize(identifier);
            }
        }
        return f8092b;
    }

    public static int d(Context context) {
        if (f8093c == -1) {
            f8093c = 0;
            Resources resources = context.getResources();
            int identifier = resources.getIdentifier("status_bar_height", "dimen", Constants.System.ANDROID_PACKAGE_NAME);
            if (identifier > 0) {
                f8093c = resources.getDimensionPixelSize(identifier);
            }
        }
        return f8093c;
    }

    public static String e(Context context) {
        int a2 = i.a(context, 0, 0) / 60;
        return (a2 < 1 || o.e(context) <= 3) ? "-" : String.valueOf(a2);
    }

    public static String f(Context context) {
        return (i.a(context, 0, 0) / 60 < 1 || o.e(context) <= 3) ? " " : context.getResources().getString(R.string.power_center_list_item_battery_status_time);
    }

    public static String g(Context context) {
        Resources resources;
        int i;
        if (i.a(context, 0, 0) / 60 < 1 || o.e(context) <= 3) {
            resources = context.getResources();
            i = R.string.power_superpower_title_systemui_lowpower;
        } else {
            resources = context.getResources();
            i = R.string.power_superpower_title_systemui_highpower;
        }
        return resources.getString(i);
    }

    public static List<String> h(Context context) {
        ActivityInfo activityInfo;
        HashSet hashSet = new HashSet();
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        if (activityManager == null) {
            return new ArrayList();
        }
        List<ActivityManager.RecentTaskInfo> recentTasks = activityManager.getRecentTasks(1001, 6);
        int i = 1;
        while (true) {
            int i2 = 0;
            if (i >= recentTasks.size()) {
                break;
            }
            try {
                i2 = ((Integer) e.a((Object) recentTasks.get(i), UserConfigure.Columns.USER_ID)).intValue();
            } catch (NoSuchFieldException e2) {
                e2.printStackTrace();
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
            }
            ResolveInfo a2 = a(context, recentTasks.get(i));
            if (!(a2 == null || (activityInfo = a2.activityInfo) == null || activityInfo.packageName == null)) {
                String str = a2.activityInfo.packageName;
                if (!TextUtils.isEmpty(str)) {
                    if (String.valueOf(i2).startsWith("999")) {
                        str = str.concat(":999");
                    }
                    hashSet.add(str);
                }
            }
            i++;
        }
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        ArrayList arrayList = new ArrayList();
        List<String> a3 = a(context.getPackageManager(), B.j(), (HashSet<String>) null);
        for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
            String[] strArr = next.pkgList;
            String str2 = strArr != null ? strArr[0] : null;
            String concat = (!String.valueOf(next.uid).startsWith("999") || str2 == null) ? str2 : str2.concat(":999");
            if (!TextUtils.isEmpty(concat)) {
                try {
                    if (next.importance <= 125) {
                        arrayList.add(concat);
                    } else {
                        ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(str2, 0);
                        if ((applicationInfo.flags & 1) == 0 || a3.contains(applicationInfo.packageName)) {
                            hashSet.add(concat);
                        }
                    }
                } catch (Exception e4) {
                    Log.e("SuperPowerUtils", "ThirdRunningAppProcess", e4);
                }
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            hashSet.remove((String) it.next());
        }
        ArrayList arrayList2 = new ArrayList();
        arrayList2.addAll(hashSet);
        return arrayList2;
    }

    public static boolean i(Context context) {
        Boolean bool;
        Boolean bool2 = f8091a;
        if (bool2 != null) {
            return bool2.booleanValue();
        }
        try {
            IBinder iBinder = (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "window");
            Object a2 = e.a(Class.forName("android.view.IWindowManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder);
            if (Build.VERSION.SDK_INT < 29) {
                bool = (Boolean) e.a(a2, Boolean.TYPE, "hasNavigationBar", (Class<?>[]) null, new Object[0]);
            } else {
                int intValue = ((Integer) e.b(context, "getDisplayId", (Class<?>[]) null, new Object[0])).intValue();
                bool = (Boolean) e.a(a2, Boolean.TYPE, "hasNavigationBar", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(intValue));
            }
            f8091a = bool;
            return f8091a.booleanValue();
        } catch (Exception e2) {
            Log.e("SuperPowerUtils", "reflect error while get navigationbar", e2);
            return true;
        }
    }

    public static boolean j(Context context) {
        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }
        try {
            for (UserHandle identifier : ((UserManager) context.getSystemService("user")).getUserProfiles()) {
                if (identifier.getIdentifier() == 999) {
                    return true;
                }
            }
        } catch (Exception e2) {
            Log.e("SuperPowerSaveManager", "hasXSpace exception : " + e2);
        }
        return false;
    }

    public static boolean k(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "hide_gesture_line", 0) == 0;
    }

    public static boolean l(Context context) {
        c.a a2 = c.a.a("android.provider.MiuiSettings$Global");
        a2.b("getBoolean", new Class[]{ContentResolver.class, String.class}, context.getContentResolver(), "force_fsg_nav_bar");
        return a2.a();
    }

    public static boolean m(Context context) {
        return !i(context) && a() > 8;
    }

    public static boolean n(Context context) {
        try {
            return ((Boolean) e.a(Class.forName("android.provider.MiuiSettings$Global"), "getBoolean", (Class<?>[]) new Class[]{ContentResolver.class, String.class}, context.getContentResolver(), "use_gesture_version_three")).booleanValue();
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static boolean o(Context context) {
        boolean z;
        try {
            z = context.getPackageManager().getApplicationInfo(com.miui.earthquakewarning.Constants.SECURITY_ADD_PACKAGE, 128).metaData.getBoolean("is_support_superpower");
        } catch (Exception e2) {
            e2.printStackTrace();
            z = false;
        }
        if (!B.f() || !a.a() || !z) {
            return false;
        }
        if (i(context) || m(context)) {
            return (!f8094d || e) && Build.VERSION.SDK_INT >= 24;
        }
        return false;
    }
}
