package com.miui.appmanager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import b.b.c.j.x;
import b.b.c.j.y;
import b.b.o.g.c;
import b.b.o.g.d;
import b.b.o.g.e;
import com.miui.common.persistence.b;
import com.miui.networkassistant.utils.HybirdServiceUtil;
import com.miui.permcenter.a;
import com.miui.permcenter.n;
import com.miui.permcenter.s;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.app.AlertDialog;
import miui.cloud.Constants;
import miui.os.Build;
import miui.security.SecurityManager;
import miui.theme.ThemeManagerHelper;

public class AppManageUtils {

    /* renamed from: a  reason: collision with root package name */
    private static final String f3483a = "com.miui.appmanager.AppManageUtils";

    /* renamed from: b  reason: collision with root package name */
    public static final ArrayList<String> f3484b = new ArrayList<>();

    /* renamed from: c  reason: collision with root package name */
    public static final ArrayList<String> f3485c = new ArrayList<>();

    /* renamed from: d  reason: collision with root package name */
    public static final List<String> f3486d = new ArrayList();
    public static final List<String> e = new ArrayList();
    public static final List<String> f = new ArrayList();
    public static final List<String> g = new ArrayList();
    public static final List<String> h = new ArrayList();

    public static class ClearCacheObserver extends IPackageDataObserver.Stub {

        /* renamed from: a  reason: collision with root package name */
        Handler f3487a;

        public ClearCacheObserver(Handler handler) {
            this.f3487a = handler;
        }

        public void onRemoveCompleted(String str, boolean z) {
            int i = 2;
            Message obtainMessage = this.f3487a.obtainMessage(2);
            if (z) {
                i = 1;
            }
            obtainMessage.arg1 = i;
            this.f3487a.sendMessage(obtainMessage);
        }
    }

    public static class ClearUserDataObserver extends IPackageDataObserver.Stub {

        /* renamed from: a  reason: collision with root package name */
        Handler f3488a;

        public ClearUserDataObserver(Handler handler) {
            this.f3488a = handler;
        }

        public void onRemoveCompleted(String str, boolean z) {
            Message obtainMessage = this.f3488a.obtainMessage(3);
            obtainMessage.arg1 = z ? 1 : 2;
            this.f3488a.sendMessage(obtainMessage);
        }
    }

    static {
        f3484b.add("com.miui.notes");
        f3484b.add("com.xiaomi.gamecenter");
        f3484b.add("com.miui.compass");
        f3484b.add("com.android.email");
        f3484b.add("com.miui.screenrecorder");
        f3484b.add("com.miui.calculator");
        f3484b.add("com.xiaomi.scanner");
        f3484b.add("com.miui.weather2");
        f3485c.add("com.facebook.appmanager");
        f3485c.add("com.facebook.services");
        f3485c.add("com.facebook.system");
        if (Build.IS_INTERNATIONAL_BUILD && "fr_sfr".equals(y.a("ro.miui.customized.region", ""))) {
            f3485c.add("com.altice.android.myapps");
            f3485c.add("com.sfr.android.sfrjeux");
        }
        if (Build.IS_INTERNATIONAL_BUILD && "fr_orange".equals(y.a("ro.miui.customized.region", ""))) {
            f3485c.add("com.google.android.youtube");
        }
        f3486d.add("com.sohu.inputmethod.sogou.xiaomi");
        f3486d.add("com.baidu.input_mi");
        f3486d.add("com.miui.securitycore");
        f3486d.add("com.xiaomi.finddevice");
        f3486d.add("com.android.phone");
        f3486d.add("com.android.bluetooth");
        f3486d.add("com.android.systemui");
        f3486d.add("com.miui.home");
        f3486d.add("com.mi.android.globallauncher");
        f3486d.add("com.miui.tsmclient");
        f3486d.add(HybirdServiceUtil.HYBIRD_PACKAGE_NAME);
        f3486d.add("com.xiaomi.payment");
        f3486d.add("com.miui.yellowpage");
        f3486d.add("com.android.quicksearchbox");
        f3486d.add("com.xiaomi.miplay");
        f3486d.add("com.xiaomi.xmsf");
        f3486d.add("com.xiaomi.midrop");
        f3486d.add("com.xiaomi.account");
        f3486d.add("com.android.midrive");
        f3486d.add("com.miui.cloudbackup");
        f3486d.add(Constants.CLOUDSERVICE_PACKAGE_NAME);
        f3486d.add("com.xiaomi.simactivate.service");
        f3486d.add("com.miui.personalassistant");
        e.add("com.xiaomi.market");
        e.add("com.xiaomi.gamecenter");
        if (!Build.IS_INTERNATIONAL_BUILD) {
            f.add("com.miui.greenguard");
        }
        if ("lm_cr".equals(y.a("ro.miui.customized.region", ""))) {
            g.add("co.sitic.pp");
        }
        h.add("com.jeejen.family.miui");
        h.add("com.xiaomi.mipicks");
        h.add("com.miui.android.fashiongallery");
        if (Build.IS_GLOBAL_BUILD) {
            h.add("com.amazon.appmanager");
        }
    }

    public static int a(int i) {
        return i % DefaultOggSeeker.MATCH_BYTE_RANGE;
    }

    public static int a(AppOpsManager appOpsManager, int i, String str) {
        if (Build.VERSION.SDK_INT > 25) {
            try {
                return ((Integer) e.a((Object) appOpsManager, "checkOpNoThrow", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE, String.class}, 66, Integer.valueOf(i), str)).intValue();
            } catch (Exception e2) {
                Log.e(f3483a, "checkOpNoThrow error", e2);
            }
        }
        return R.string.app_manager_not_allow;
    }

    public static int a(Context context, Object obj, int i, int i2) {
        if (Build.VERSION.SDK_INT > 25) {
            try {
                int intValue = ((Integer) e.a((Object) (UserManager) context.getSystemService("user"), "getUserRestrictionSource", (Class<?>[]) new Class[]{String.class, UserHandle.class}, "no_install_unknown_sources", UserHandle.getUserHandleForUid(i))).intValue();
                return intValue != 1 ? (intValue == 2 || intValue == 4) ? R.string.app_manager_disabled_by_admin : a(obj, i, i2) ? R.string.app_manager_allow : R.string.app_manager_not_allow : R.string.app_manager_disabled;
            } catch (Exception e2) {
                Log.e(f3483a, "GETPotentialAppSummary error", e2);
            }
        }
        return 1;
    }

    public static int a(String str, int i) {
        try {
            IBinder iBinder = (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "package");
            return ((Integer) e.a(e.a(Class.forName("android.content.pm.IPackageManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder), "getApplicationEnabledSetting", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, str, Integer.valueOf(i))).intValue();
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while get application enable setting", e2);
            return 0;
        }
    }

    public static ApplicationInfo a(Object obj, String str, int i, int i2) {
        if (obj == null) {
            return null;
        }
        try {
            return (ApplicationInfo) e.a(obj, "getApplicationInfo", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE}, str, Integer.valueOf(i), Integer.valueOf(i2));
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while get applicaiton info", e2);
            return null;
        }
    }

    public static SparseArray<List<String>> a(Context context, PackageManager packageManager, List<UserHandle> list, HashSet<ComponentName> hashSet) {
        SparseArray<List<String>> sparseArray = new SparseArray<>();
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage((String) null);
        for (UserHandle identifier : list) {
            int identifier2 = identifier.getIdentifier();
            sparseArray.put(identifier2, new ArrayList());
            for (ResolveInfo next : a(packageManager, intent, 0, identifier2)) {
                if (!hashSet.contains(new ComponentName(next.activityInfo.packageName, next.activityInfo.name))) {
                    sparseArray.get(identifier2).add(next.activityInfo.packageName);
                }
            }
        }
        return sparseArray;
    }

    public static F a(Context context, ApplicationInfo applicationInfo, int i) {
        try {
            Object a2 = e.a(context.getSystemService("storagestats"), "queryStatsForPackage", (Class<?>[]) new Class[]{String.class, String.class, UserHandle.class}, (String) e.a((Object) applicationInfo, "volumeUuid"), applicationInfo.packageName, UserHandle.getUserHandleForUid(i));
            return new F(((Long) e.a(a2, "getDataBytes", (Class<?>[]) null, new Object[0])).longValue(), ((Long) e.a(a2, "getCacheBytes", (Class<?>[]) null, new Object[0])).longValue(), ((Long) e.a(a2, "getCodeBytes", (Class<?>[]) null, new Object[0])).longValue());
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while query stats for pacakge", e2);
            return new F(0, 0, 0);
        }
    }

    public static String a() {
        return b.a("am_click_update_time", a(86400000));
    }

    public static String a(long j) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Timestamp(System.currentTimeMillis() - j));
    }

    public static String a(Context context) {
        String str;
        if (Build.VERSION.SDK_INT < 24) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            Method declaredMethod = PackageManager.class.getDeclaredMethod("getDefaultBrowserPackageNameAsUser", new Class[]{Integer.TYPE});
            declaredMethod.setAccessible(true);
            Method declaredMethod2 = UserHandle.class.getDeclaredMethod("getCallingUserId", new Class[0]);
            declaredMethod2.setAccessible(true);
            str = (String) declaredMethod.invoke(packageManager, new Object[]{Integer.valueOf(((Integer) declaredMethod2.invoke((Object) null, new Object[0])).intValue())});
        } catch (Exception e2) {
            e2.printStackTrace();
            str = null;
        }
        return TextUtils.isEmpty(str) ? "com.android.default" : str;
    }

    public static String a(PackageManager packageManager, int i) {
        String str;
        Class[] clsArr;
        Class<?> cls = packageManager.getClass();
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                str = "getDefaultBrowserPackageNameAsUser";
                clsArr = new Class[]{Integer.TYPE};
            } else {
                str = "getDefaultBrowserPackageName";
                clsArr = new Class[]{Integer.TYPE};
            }
            Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
            if (declaredMethod == null) {
                return null;
            }
            return (String) declaredMethod.invoke(packageManager, new Object[]{Integer.valueOf(i)});
        } catch (Exception e2) {
            Log.e(f3483a, "getDefaultBrowserPackage error", e2);
            return null;
        }
    }

    public static String a(HashSet<String> hashSet) {
        if (hashSet == null || hashSet.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = hashSet.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (!TextUtils.isEmpty(next)) {
                sb.append(next + ",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static HashSet<String> a(String str) {
        if (TextUtils.isEmpty(str)) {
            return new HashSet<>();
        }
        String[] split = str.split(",");
        HashSet<String> hashSet = new HashSet<>();
        for (String str2 : split) {
            if (!hashSet.contains(str2)) {
                hashSet.add(str2);
            }
        }
        return hashSet;
    }

    public static List<PackageInfo> a(PackageManager packageManager, int i, int i2) {
        try {
            if (Build.VERSION.SDK_INT > 23) {
                return (List) e.a((Object) packageManager, "getInstalledPackagesAsUser", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE}, Integer.valueOf(i), Integer.valueOf(i2));
            }
            return (List) e.a((Object) packageManager, "getInstalledPackages", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE}, Integer.valueOf(i), Integer.valueOf(i2));
        } catch (Exception e2) {
            Log.e(f3483a, "getInstalledPacakge error", e2);
            return null;
        }
    }

    public static List<ResolveInfo> a(PackageManager packageManager, Intent intent, int i, int i2) {
        String str = f3483a;
        Class cls = Integer.TYPE;
        return (List) d.a(str, (Object) packageManager, "queryIntentActivitiesAsUser", (Class<?>[]) new Class[]{Intent.class, cls, cls}, intent, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static Map<String, Long> a(UsageStatsManager usageStatsManager, int i) {
        List<UsageStats> list;
        HashMap hashMap = new HashMap();
        Calendar instance = Calendar.getInstance();
        instance.add(2, -1);
        try {
            list = (List) e.a((Object) usageStatsManager, "queryUsageStatsAsUser", (Class<?>[]) new Class[]{Integer.TYPE, Long.TYPE, Long.TYPE, Integer.TYPE}, 4, Long.valueOf(instance.getTimeInMillis()), Long.valueOf(System.currentTimeMillis()), Integer.valueOf(i));
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while query usage stats", e2);
            list = null;
        }
        if (list != null && list.size() > 0) {
            for (UsageStats usageStats : list) {
                String packageName = usageStats.getPackageName();
                Long l = (Long) hashMap.get(packageName);
                Long valueOf = Long.valueOf(usageStats.getLastTimeUsed());
                if (l == null || l.longValue() < valueOf.longValue()) {
                    hashMap.put(packageName, valueOf);
                }
            }
        }
        return hashMap;
    }

    private static void a(Activity activity, Intent intent, int i, UserHandle userHandle) {
        d.b(f3483a, activity, "startActivityForResultAsUser", new Class[]{Intent.class, Integer.TYPE, UserHandle.class}, intent, Integer.valueOf(i), userHandle);
    }

    public static void a(ActivityManager activityManager, String str, int i) {
        try {
            e.a((Object) activityManager, "forceStopPackageAsUser", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, str, Integer.valueOf(i));
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while forceStopPackage", e2);
        }
    }

    public static void a(AppWidgetManager appWidgetManager, String str, boolean z) {
        try {
            e.a((Object) appWidgetManager, "setBindAppWidgetPermission", (Class<?>[]) new Class[]{String.class, Boolean.TYPE}, str, Boolean.valueOf(z));
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error set app bind widget permission", e2);
        }
    }

    public static void a(Context context, int i, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder;
        AlertDialog.Builder builder2;
        if (i == 1) {
            builder2 = new AlertDialog.Builder(context).setTitle(R.string.app_manager_clear_data_dlg_title).setMessage(R.string.app_manager_clear_data_dlg_message);
        } else if (i == 2) {
            builder = new AlertDialog.Builder(context).setTitle(R.string.app_manager_clear_dlg_title).setMessage(R.string.app_manager_clear_failed_dlg_message).setNeutralButton(R.string.app_manager_dlg_ok, onClickListener);
            builder.create().show();
        } else if (i == 3) {
            builder2 = new AlertDialog.Builder(context).setTitle(R.string.app_manager_dlg_clear_cache_title);
        } else {
            return;
        }
        builder = builder2.setNegativeButton(R.string.app_manager_dlg_cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.app_manager_dlg_ok, onClickListener);
        builder.create().show();
    }

    public static void a(Context context, String str, long j, long j2, boolean z, DialogInterface.OnClickListener onClickListener) {
        String str2;
        if (j <= 0 || !z) {
            str2 = null;
        } else {
            str2 = context.getString(str != null ? R.string.app_manager_manage_space : R.string.app_manager_clear_all_data);
        }
        String string = j2 > 0 ? context.getString(R.string.app_manager_clear_cache) : null;
        ArrayList arrayList = new ArrayList();
        if (str2 != null) {
            arrayList.add(str2);
        }
        if (string != null) {
            arrayList.add(string);
        }
        if (arrayList.size() > 0) {
            String[] strArr = new String[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                strArr[i] = (String) arrayList.get(i);
            }
            new AlertDialog.Builder(context).setTitle(R.string.app_manager_clear_dlg_title).setItems(strArr, onClickListener).setNeutralButton(R.string.app_manager_dlg_cancel, (DialogInterface.OnClickListener) null).create().show();
        }
    }

    public static void a(Context context, String str, String str2, int i, int i2) {
        if (!ActivityManager.isUserAMonkey()) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setClassName(str, str2);
                a((Activity) context, intent, i2, new UserHandle(i));
            } catch (Exception e2) {
                Log.e(f3483a, "startActivityForResultAsUser error", e2);
            }
        }
    }

    public static void a(Context context, String str, boolean z) {
        HashSet<String> a2 = a(c(context));
        if (z && !a2.contains(str)) {
            a2.add(str);
        }
        if (!z && a2.contains(str)) {
            a2.remove(str);
        }
        Settings.System.putString(context.getContentResolver(), "miui_recents_privacy_thumbnail_blur", a(a2));
    }

    public static void a(PackageManager packageManager, String str, int i) {
        String str2;
        Class[] clsArr;
        Class<?> cls = packageManager.getClass();
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                str2 = "setDefaultBrowserPackageNameAsUser";
                clsArr = new Class[]{String.class, Integer.TYPE};
            } else {
                str2 = "setDefaultBrowserPackageName";
                clsArr = new Class[]{String.class, Integer.TYPE};
            }
            Method declaredMethod = cls.getDeclaredMethod(str2, clsArr);
            if (declaredMethod != null) {
                declaredMethod.invoke(packageManager, new Object[]{str, Integer.valueOf(i)});
            }
        } catch (Exception e2) {
            Log.e(f3483a, "setDefaultBrowserPackage error", e2);
        }
    }

    public static void a(PackageManager packageManager, String str, int i, IPackageStatsObserver iPackageStatsObserver) {
        String str2;
        Class[] clsArr;
        Object[] objArr;
        try {
            if (Build.VERSION.SDK_INT > 23) {
                str2 = "getPackageSizeInfoAsUser";
                clsArr = new Class[]{String.class, Integer.TYPE, IPackageStatsObserver.class};
                objArr = new Object[]{str, Integer.valueOf(i), iPackageStatsObserver};
            } else {
                str2 = "getPackageSizeInfo";
                clsArr = new Class[]{String.class, Integer.TYPE, IPackageStatsObserver.class};
                objArr = new Object[]{str, Integer.valueOf(i), iPackageStatsObserver};
            }
            e.a((Object) packageManager, str2, (Class<?>[]) clsArr, objArr);
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while get package size info", e2);
        }
    }

    public static void a(Object obj, String str, int i) {
        try {
            e.a(obj, "clearDefaults", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, str, Integer.valueOf(i));
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while clear usb default", e2);
        }
    }

    public static void a(Object obj, String str, int i, IPackageDeleteObserver iPackageDeleteObserver, int i2, int i3) {
        try {
            if (Build.VERSION.SDK_INT > 25) {
                e.a(obj, "deletePackageAsUser", (Class<?>[]) new Class[]{String.class, Integer.TYPE, IPackageDeleteObserver.class, Integer.TYPE, Integer.TYPE}, str, Integer.valueOf(i), iPackageDeleteObserver, Integer.valueOf(i2), Integer.valueOf(i3));
                return;
            }
            e.a(obj, "deletePackageAsUser", (Class<?>[]) new Class[]{String.class, IPackageDeleteObserver.class, Integer.TYPE, Integer.TYPE}, str, iPackageDeleteObserver, Integer.valueOf(i2), Integer.valueOf(i3));
        } catch (Exception e2) {
            Log.e(f3483a, "deletePackage error", e2);
        }
    }

    public static void a(Object obj, String str, int i, ClearCacheObserver clearCacheObserver) {
        if (Build.VERSION.SDK_INT > 23) {
            try {
                e.a(obj, "deleteApplicationCacheFilesAsUser", (Class<?>[]) new Class[]{String.class, Integer.TYPE, IPackageDataObserver.class}, str, Integer.valueOf(i), clearCacheObserver);
            } catch (Exception e2) {
                Log.e(f3483a, "deleteApplicationCache error", e2);
            }
        } else {
            e.a(obj, "deleteApplicationCacheFilesForUser", (Class<?>[]) new Class[]{String.class, Integer.TYPE, IPackageDataObserver.class}, str, Integer.valueOf(i), clearCacheObserver);
        }
    }

    public static void a(String str, int i, boolean z) {
        try {
            IBinder iBinder = (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "notification");
            e.a(e.a(Class.forName("android.app.INotificationManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder), "setNotificationsEnabledForPackage", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Boolean.TYPE}, str, Integer.valueOf(i), Boolean.valueOf(z));
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while set notification enable", e2);
        }
    }

    public static void a(String str, boolean z) {
        try {
            e.a(Class.forName("miui.os.MiuiInit"), "setRestrictAspect", (Class<?>[]) new Class[]{String.class, Boolean.TYPE}, str, Boolean.valueOf(z));
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error set restric aspect", e2);
        }
    }

    public static void a(boolean z) {
        b.b("am_update_clicked", z);
    }

    public static boolean a(DevicePolicyManager devicePolicyManager, String str) {
        try {
            return ((Boolean) e.a((Object) devicePolicyManager, "packageHasActiveAdmins", (Class<?>[]) new Class[]{String.class}, str)).booleanValue();
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while package has active admin", e2);
            return false;
        }
    }

    public static boolean a(AppWidgetManager appWidgetManager, String str) {
        try {
            return ((Boolean) e.a((Object) appWidgetManager, "hasBindAppWidgetPermission", (Class<?>[]) new Class[]{String.class}, str)).booleanValue();
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error app has bind widget permission", e2);
            return false;
        }
    }

    public static boolean a(Context context, int i) {
        boolean z;
        try {
            e.a((Object) (SecurityManager) context.getSystemService("security"), "getAllPrivacyApps", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i));
            z = true;
        } catch (Exception unused) {
            Log.e(f3483a, "reflect error when get app isPrivacy");
            z = false;
        }
        return z && j(context);
    }

    public static boolean a(Context context, String str) {
        a a2 = n.a(context, (long) PermissionManager.PERM_ID_AUTOSTART, str);
        if (a2 != null) {
            List<String> a3 = s.a(context);
            if (a2.f().get(Long.valueOf(PermissionManager.PERM_ID_AUTOSTART)).intValue() == 3 || (a3 != null && Collections.binarySearch(a3, str) >= 0)) {
                return true;
            }
        }
        return false;
    }

    public static boolean a(Context context, String str, int i) {
        try {
            return ((Boolean) e.a(Class.forName("com.miui.enterprise.ApplicationHelper"), "allowAutoStart", (Class<?>[]) new Class[]{Context.class, String.class, Integer.TYPE}, context, str, Integer.valueOf(i))).booleanValue();
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error shoueKeeAlive", e2);
            return false;
        }
    }

    public static boolean a(ContextWrapper contextWrapper) {
        try {
            IBinder iBinder = (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "window");
            Object a2 = e.a(Class.forName("android.view.IWindowManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder);
            if (Build.VERSION.SDK_INT <= 28) {
                return ((Boolean) e.a(a2, "hasNavigationBar", (Class<?>[]) null, new Object[0])).booleanValue();
            }
            int intValue = ((Integer) e.b(contextWrapper, "getDisplayId", (Class<?>[]) null, new Object[0])).intValue();
            return ((Boolean) e.a(a2, "hasNavigationBar", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(intValue))).booleanValue();
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while get navigationbar", e2);
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0035 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0036  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean a(java.lang.Object r6, int r7, int r8) {
        /*
            r0 = 1
            r1 = 0
            java.lang.String r2 = "checkUidPermission"
            r3 = 2
            java.lang.Class[] r4 = new java.lang.Class[r3]     // Catch:{ Exception -> 0x0029 }
            java.lang.Class<java.lang.String> r5 = java.lang.String.class
            r4[r1] = r5     // Catch:{ Exception -> 0x0029 }
            java.lang.Class r5 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x0029 }
            r4[r0] = r5     // Catch:{ Exception -> 0x0029 }
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x0029 }
            java.lang.String r5 = "android.permission.REQUEST_INSTALL_PACKAGES"
            r3[r1] = r5     // Catch:{ Exception -> 0x0029 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x0029 }
            r3[r0] = r7     // Catch:{ Exception -> 0x0029 }
            java.lang.Object r6 = b.b.o.g.e.a((java.lang.Object) r6, (java.lang.String) r2, (java.lang.Class<?>[]) r4, (java.lang.Object[]) r3)     // Catch:{ Exception -> 0x0029 }
            java.lang.Integer r6 = (java.lang.Integer) r6     // Catch:{ Exception -> 0x0029 }
            int r6 = r6.intValue()     // Catch:{ Exception -> 0x0029 }
            if (r6 != 0) goto L_0x0031
            r6 = r0
            goto L_0x0032
        L_0x0029:
            r6 = move-exception
            java.lang.String r7 = f3483a
            java.lang.String r2 = "checkUidPermission error"
            android.util.Log.e(r7, r2, r6)
        L_0x0031:
            r6 = r1
        L_0x0032:
            r7 = 3
            if (r8 != r7) goto L_0x0036
            return r6
        L_0x0036:
            if (r8 != 0) goto L_0x0039
            goto L_0x003a
        L_0x0039:
            r0 = r1
        L_0x003a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.AppManageUtils.a(java.lang.Object, int, int):boolean");
    }

    public static boolean a(String str, int i, ClearUserDataObserver clearUserDataObserver) {
        String str2;
        StringBuilder sb;
        if (Build.VERSION.SDK_INT > 27) {
            try {
                c.a a2 = c.a.a("android.app.ActivityManagerNative");
                a2.b("getDefault", (Class<?>[]) null, new Object[0]);
                a2.e();
                a2.a("clearApplicationUserData", new Class[]{String.class, Boolean.TYPE, IPackageDataObserver.class, Integer.TYPE}, str, false, clearUserDataObserver, Integer.valueOf(i));
                return a2.a();
            } catch (Exception e2) {
                e = e2;
                str2 = f3483a;
                sb = new StringBuilder();
                sb.append("Couldnt clear application user data for package:");
                sb.append(str);
                Log.e(str2, sb.toString(), e);
                return false;
            }
        } else {
            try {
                c.a a3 = c.a.a("android.app.ActivityManagerNative");
                a3.b("getDefault", (Class<?>[]) null, new Object[0]);
                a3.e();
                a3.a("clearApplicationUserData", new Class[]{String.class, IPackageDataObserver.class, Integer.TYPE}, str, clearUserDataObserver, Integer.valueOf(i));
                return a3.a();
            } catch (Exception e3) {
                e = e3;
                str2 = f3483a;
                sb = new StringBuilder();
                sb.append("Couldnt clear application user data for package:");
                sb.append(str);
                Log.e(str2, sb.toString(), e);
                return false;
            }
        }
    }

    public static boolean a(SecurityManager securityManager, String str, int i) {
        try {
            return ((Boolean) e.a((Object) securityManager, "isPrivacyApp", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, str, Integer.valueOf(i))).booleanValue();
        } catch (Exception unused) {
            Log.e(f3483a, "reflect error when get app isPrivacy");
            return false;
        }
    }

    public static int b() {
        return b.a("icon_discription_times", 0);
    }

    public static int b(Context context, String str, boolean z) {
        try {
            return ((Integer) e.a(Class.forName("miui.util.NotificationFilterHelper"), "getAppFlag", (Class<?>[]) new Class[]{Context.class, String.class, Boolean.TYPE}, context, str, Boolean.valueOf(z))).intValue();
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while get app flag", e2);
            return 3;
        }
    }

    public static int b(String str) {
        try {
            return ((Integer) e.a(Class.forName("miui.os.MiuiInit"), "getDefaultAspectType", (Class<?>[]) new Class[]{String.class}, str)).intValue();
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error get default aspect type", e2);
            return 0;
        }
    }

    public static void b(int i) {
        try {
            c.a a2 = c.a.a("android.app.ActivityManagerNative");
            a2.b("getDefault", (Class<?>[]) null, new Object[0]);
            a2.e();
            a2.a("removeTask", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while remove task", e2);
        }
    }

    public static boolean b(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "enable_miui_security_ime", 1) == 1;
    }

    public static boolean b(Context context, String str) {
        String c2 = c(context);
        return c2 != null && c2.contains(str);
    }

    public static boolean b(Object obj, String str, int i) {
        try {
            return ((Boolean) e.a(obj, "hasDefaults", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, str, Integer.valueOf(i))).booleanValue();
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while get usb default", e2);
            return false;
        }
    }

    public static String c(Context context) {
        return Settings.System.getString(context.getContentResolver(), "miui_recents_privacy_thumbnail_blur");
    }

    public static void c(int i) {
        b.b("icon_discription_times", i);
    }

    public static void c(Context context, String str, boolean z) {
        PermissionManager.getInstance(context).setApplicationPermission(PermissionManager.PERM_ID_AUTOSTART, z ? 3 : 1, str);
        if (!z) {
            x.a((ActivityManager) context.getSystemService("activity"), str);
        }
        s.a(context, str, z);
    }

    public static boolean c() {
        return b.a("am_update_clicked", false);
    }

    public static boolean c(Object obj, String str, int i) {
        boolean z;
        if (Build.VERSION.SDK_INT > 25) {
            try {
                z = Arrays.asList((String[]) e.a(obj, "getAppOpPermissionPackages", (Class<?>[]) new Class[]{String.class}, "android.permission.REQUEST_INSTALL_PACKAGES")).contains(str);
            } catch (Exception e2) {
                Log.e(f3483a, "isPotentialAppSource error", e2);
            }
            return i != 3 || z;
        }
        z = false;
        if (i != 3) {
            return true;
        }
    }

    public static boolean c(String str) {
        try {
            return ((Boolean) e.a(Class.forName("android.app.AppOpsManagerInjector"), "isAutoStartRestriction", (Class<?>[]) new Class[]{String.class}, str)).booleanValue();
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while autostart restriction", e2);
            return false;
        }
    }

    public static List<UserHandle> d(Context context) {
        return context == null ? new ArrayList() : ((UserManager) context.getSystemService("user")).getUserProfiles();
    }

    public static boolean d(String str) {
        try {
            return ((Boolean) e.a(Class.forName("miui.os.MiuiInit"), "isRestrictAspect", (Class<?>[]) new Class[]{String.class}, str)).booleanValue();
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error get restric aspect", e2);
            return false;
        }
    }

    public static void e(String str) {
        b.b("am_click_update_time", str);
    }

    public static boolean e(Context context) {
        if (context == null) {
            return false;
        }
        return x.h(context, "com.miui.thirdappassistant");
    }

    public static String f(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        int length = str.length();
        int i = 0;
        while (i < length && !TextUtils.isGraphic(str.charAt(i))) {
            i++;
        }
        return str.substring(i);
    }

    public static boolean f(Context context) {
        return context.getPackageManager().resolveActivity(new Intent("com.android.settings.action.PRIVACY_THUMBNAIL_BLUR_SETTING"), 131072) != null;
    }

    public static boolean g(Context context) {
        try {
            return ((Boolean) e.a(Class.forName("android.provider.MiuiSettings$Secure"), "getBoolean", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, context.getContentResolver(), "xspace_enabled", false)).booleanValue();
        } catch (Exception e2) {
            Log.e(f3483a, "reflect error while get Miuisettings secure boolean", e2);
            return false;
        }
    }

    public static HashSet<ComponentName> h(Context context) {
        HashSet<ComponentName> hashSet = new HashSet<>();
        if (!miui.os.Build.IS_INTERNATIONAL_BUILD) {
            hashSet.add(new ComponentName("com.google.android.gms", "com.google.android.gms.app.settings.GoogleSettingsActivity"));
            hashSet.add(new ComponentName("com.google.android.gms", "com.google.android.gms.common.settings.GoogleSettingsActivity"));
        }
        hashSet.add(new ComponentName("com.qualcomm.qti.modemtestmode", "com.qualcomm.qti.modemtestmode.MbnFileActivate"));
        hashSet.add(new ComponentName("com.google.android.googlequicksearchbox", "com.google.android.googlequicksearchbox.SearchActivity"));
        hashSet.add(new ComponentName("com.google.android.googlequicksearchbox", "com.google.android.handsfree.HandsFreeLauncherActivity"));
        hashSet.add(new ComponentName("com.google.android.inputmethod.pinyin", "com.google.android.apps.inputmethod.libs.framework.core.LauncherActivity"));
        hashSet.add(new ComponentName("com.opera.max.oem.xiaomi", "com.opera.max.ui.v2.MainActivity"));
        hashSet.add(new ComponentName("com.google.android.inputmethod.latin", "com.android.inputmethod.latin.setup.SetupActivity"));
        if (ThemeManagerHelper.needDisableTheme(context)) {
            hashSet.add(new ComponentName("com.android.thememanager", "com.android.thememanager.ThemeResourceTabActivity"));
        }
        return hashSet;
    }

    public static void i(Context context) {
        Context context2 = context;
        Log.i(f3483a, "startScan");
        int i = Build.VERSION.SDK_INT;
        if (i > 27) {
            try {
                IBinder iBinder = (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "wifi");
                e.a(e.a(Class.forName("android.net.wifi.IWifiManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder), "startScan", (Class<?>[]) new Class[]{String.class}, (String) e.b(context2, "getOpPackageName", (Class<?>[]) null, new Object[0]));
            } catch (Exception e2) {
                Log.e(f3483a, "startScan error", e2);
            }
        } else if (i > 25) {
            try {
                Object newInstance = Class.forName("android.net.wifi.ScanSettings").getConstructor(new Class[0]).newInstance(new Object[0]);
                Object newInstance2 = Class.forName("android.net.wifi.WifiChannel").getConstructor(new Class[0]).newInstance(new Object[0]);
                ArrayList arrayList = new ArrayList();
                arrayList.add(newInstance2);
                e.a((Object) newInstance2, "freqMHz", (Object) 2437);
                e.a((Object) newInstance2, "channelNum", (Object) 6);
                e.a((Object) newInstance, "channelSet", (Object) arrayList);
                IBinder iBinder2 = (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "wifi");
                Object a2 = e.a(Class.forName("android.net.wifi.IWifiManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder2);
                String str = (String) e.b(context2, "getOpPackageName", (Class<?>[]) null, new Object[0]);
                Class[] clsArr = new Class[3];
                clsArr[0] = Class.forName("android.net.wifi.ScanSettings");
                clsArr[1] = WorkSource.class;
                clsArr[2] = String.class;
                e.a(a2, "startScan", (Class<?>[]) clsArr, newInstance, null, str);
            } catch (Exception e3) {
                Log.d(f3483a, "reflect exception while scan!", e3);
            }
        } else {
            Object newInstance3 = Class.forName("android.net.wifi.ScanSettings").getConstructor(new Class[0]).newInstance(new Object[0]);
            Object newInstance4 = Class.forName("android.net.wifi.WifiChannel").getConstructor(new Class[0]).newInstance(new Object[0]);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(newInstance4);
            e.a((Object) newInstance4, "freqMHz", (Object) 2437);
            e.a((Object) newInstance4, "channelNum", (Object) 6);
            e.a((Object) newInstance3, "channelSet", (Object) arrayList2);
            IBinder iBinder3 = (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "wifi");
            Object a3 = e.a(Class.forName("android.net.wifi.IWifiManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder3);
            Class[] clsArr2 = new Class[2];
            clsArr2[0] = Class.forName("android.net.wifi.ScanSettings");
            clsArr2[1] = WorkSource.class;
            e.a(a3, "startScan", (Class<?>[]) clsArr2, newInstance3, null);
        }
    }

    private static boolean j(Context context) {
        ComponentName componentName;
        ArrayList arrayList = new ArrayList();
        ComponentName componentName2 = new ComponentName(context, "com.miui.home");
        try {
            componentName = (ComponentName) e.a((Object) context.getPackageManager(), ComponentName.class, "getHomeActivities", (Class<?>[]) new Class[]{List.class}, arrayList);
        } catch (Exception e2) {
            Log.e(f3483a, "isSupportPrivacyApp exception: ", e2);
            componentName = componentName2;
        }
        if (componentName != null) {
            return "com.miui.home".equals(componentName.getPackageName());
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            if ("com.miui.home".equals(((ResolveInfo) it.next()).activityInfo.packageName)) {
                return true;
            }
        }
        return false;
    }
}
