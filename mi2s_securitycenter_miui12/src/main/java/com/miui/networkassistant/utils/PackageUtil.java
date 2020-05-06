package com.miui.networkassistant.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.C;
import b.b.o.g.c;
import b.b.o.g.e;
import com.miui.appmanager.AppManageUtils;
import com.miui.networkassistant.config.Constants;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import miui.securitycenter.utils.SecurityCenterHelper;

public class PackageUtil {
    private static final String TAG = "PackageUtil";

    private PackageUtil() {
    }

    public static void forceStopPackage(ActivityManager activityManager, String str) {
        SecurityCenterHelper.forceStopPackage(activityManager, str);
    }

    public static void forceStopPackage(Context context, String str) {
        forceStopPackage((ActivityManager) context.getSystemService("activity"), str);
    }

    public static void forceStopPackage(Context context, String str, int i) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
            if (Build.VERSION.SDK_INT > 20) {
                ActivityManager.class.getMethod("forceStopPackageAsUser", new Class[]{String.class, Integer.TYPE}).invoke(activityManager, new Object[]{str, Integer.valueOf(i)});
                return;
            }
            ActivityManager.class.getMethod("forceStopPackage", new Class[]{String.class}).invoke(activityManager, new Object[]{str});
        } catch (Exception e) {
            Log.e(TAG, "forceStopPakage error", e);
        }
    }

    public static Drawable getActivityIcon(Intent intent, String str, String str2, Context context) {
        return queryIntent(intent, str, str2, context).activityInfo.loadIcon(context.getPackageManager());
    }

    public static String getActivityLabel(Context context, Intent intent) {
        try {
            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 32);
            if (queryIntentActivities == null || queryIntentActivities.isEmpty()) {
                return null;
            }
            for (ResolveInfo resolveInfo : queryIntentActivities) {
                CharSequence loadLabel = resolveInfo.activityInfo.loadLabel(packageManager);
                if (loadLabel != null) {
                    return loadLabel.toString();
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean getAppEnable(String str, int i) {
        int i2;
        try {
            i2 = AppManageUtils.a(str, i);
        } catch (Exception e) {
            Log.e(TAG, "getApplicationEnabledSetting error", e);
            i2 = 0;
        }
        return i2 == 0 || i2 == 1;
    }

    public static String getAppVersion(PackageManager packageManager, String str) {
        try {
            return packageManager.getPackageInfo(str, 8768).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getAppVersionCode(PackageManager packageManager, String str) {
        try {
            return packageManager.getPackageInfo(str, 8768).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getCurrentUserId() {
        try {
            return ((Integer) e.a(Class.forName("miui.securityspace.CrossUserUtils"), "getCurrentUserId", (Class<?>[]) null, new Object[0])).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Drawable getIconByPackageName(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
            Drawable iconDrawable = getIconDrawable(context.getApplicationContext(), applicationInfo, packageManager, 60000);
            return iconDrawable == null ? applicationInfo.loadIcon(packageManager) : iconDrawable;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "getIconByPackageName", e);
            return null;
        }
    }

    private static Drawable getIconDrawable(Context context, PackageItemInfo packageItemInfo, PackageManager packageManager, long j) {
        try {
            c.a a2 = c.a.a("miui.maml.util.AppIconsHelper");
            a2.b("getIconDrawable", new Class[]{Context.class, PackageItemInfo.class, PackageManager.class, Long.TYPE}, context, packageItemInfo, packageManager, Long.valueOf(j));
            return (Drawable) a2.d();
        } catch (NullPointerException e) {
            Log.i(TAG, "getIconDrawable", e);
            return null;
        }
    }

    public static String getInstaller(Context context, String str) {
        if (Build.VERSION.SDK_INT <= 22) {
            return null;
        }
        try {
            return context.getPackageManager().getInstallerPackageName(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CharSequence getLableByPackageName(Context context, String str) {
        return getLableByPackageName(context, str, B.c());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000f, code lost:
        r1 = (r3 = r3.applicationInfo).loadLabel(r1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.CharSequence getLableByPackageName(android.content.Context r1, java.lang.String r2, int r3) {
        /*
            android.content.pm.PackageManager r1 = r1.getPackageManager()     // Catch:{ Exception -> 0x0016 }
            r0 = 0
            android.content.pm.PackageInfo r3 = b.b.o.b.a.a.a(r2, r0, r3)     // Catch:{ Exception -> 0x0016 }
            if (r3 == 0) goto L_0x001a
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo     // Catch:{ Exception -> 0x0016 }
            if (r3 == 0) goto L_0x001a
            java.lang.CharSequence r1 = r3.loadLabel(r1)     // Catch:{ Exception -> 0x0016 }
            if (r1 == 0) goto L_0x001a
            return r1
        L_0x0016:
            r1 = move-exception
            r1.printStackTrace()
        L_0x001a:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.utils.PackageUtil.getLableByPackageName(android.content.Context, java.lang.String, int):java.lang.CharSequence");
    }

    public static String getManagedProfilePackageNameFormat(Context context, String str, int i) {
        if (!B.a(context, UserHandle.getUserId(i))) {
            return str;
        }
        return str + Constants.Default.MANAGED_PROFILE_PACKAGE_SPLIT + i;
    }

    public static String getPackageNameFormat(String str, int i) {
        if (!C.a(i)) {
            return str;
        }
        return str + Constants.Default.XSPACE_PACKAGE_SPLIT + i;
    }

    public static String getRealPackageName(String str) {
        String split = getSplit(str);
        return TextUtils.isEmpty(split) ? str : str.split(split)[0];
    }

    private static String getSplit(String str) {
        return str.contains(Constants.Default.XSPACE_PACKAGE_SPLIT) ? Constants.Default.XSPACE_PACKAGE_SPLIT : str.contains(Constants.Default.MANAGED_PROFILE_PACKAGE_SPLIT) ? Constants.Default.MANAGED_PROFILE_PACKAGE_SPLIT : "";
    }

    public static int getUidByPackageName(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 0).uid;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.i(TAG, "not find packageName :" + str);
            return -1;
        }
    }

    public static boolean hasInternetPermission(PackageManager packageManager, String str) {
        return packageManager.checkPermission("android.permission.INTERNET", str) == 0;
    }

    public static boolean isActivityExist(Context context, Intent intent) {
        return context.getPackageManager().resolveActivity(intent, 0) != null;
    }

    public static boolean isInstalledPackage(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isIntentExist(Context context, Intent intent) {
        try {
            List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 32);
            return queryIntentActivities != null && !queryIntentActivities.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isIntentExist(Context context, String str, String str2) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory(Constants.System.CATEGORY_DEFALUT);
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.setData(Uri.parse(str));
            intent.setPackage(str2);
            return isIntentExist(context, intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isManagedProfileApp(String str) {
        return str.split(Constants.Default.MANAGED_PROFILE_PACKAGE_SPLIT).length > 1;
    }

    public static boolean isRunningForeground(Context context, String str) {
        List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
        if (runningTasks != null && !runningTasks.isEmpty()) {
            return TextUtils.equals(runningTasks.get(0).topActivity.getPackageName(), str);
        }
        Log.i(TAG, "get runningTaskInfo exception : " + runningTasks.size());
        return false;
    }

    public static boolean isSpecialApp(String str) {
        return !str.equals(getRealPackageName(str));
    }

    public static boolean isSystemApp(Context context, String str) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            applicationInfo = null;
        }
        return applicationInfo != null && isSystemApp(applicationInfo);
    }

    public static boolean isSystemApp(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & 1) > 0 || B.a(applicationInfo.uid) < 10000;
    }

    public static boolean isXSpaceApp(String str) {
        return str.split(Constants.Default.XSPACE_PACKAGE_SPLIT).length > 1;
    }

    public static void killBackgroundProcesses(Context context, String str) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        if (activityManager != null) {
            activityManager.killBackgroundProcesses(str);
        }
    }

    public static int parseUidByPackageName(String str) {
        String split = getSplit(str);
        if (TextUtils.isEmpty(split)) {
            return 0;
        }
        String[] split2 = str.split(split);
        if (split2.length > 1) {
            return Integer.parseInt(split2[1]);
        }
        return 0;
    }

    public static ResolveInfo queryIntent(Intent intent, String str, String str2, Context context) {
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 0);
        if (queryIntentActivities.isEmpty()) {
            return null;
        }
        Iterator<ResolveInfo> it = queryIntentActivities.iterator();
        while (it.hasNext()) {
            ResolveInfo next = it.next();
            if (TextUtils.equals(next.activityInfo.packageName, str) && (TextUtils.isEmpty(str2) || TextUtils.equals(next.activityInfo.name, str2))) {
                return next;
            }
        }
        return null;
    }

    public static String reflectGetReferrer(Activity activity) {
        try {
            Field declaredField = Class.forName("android.app.Activity").getDeclaredField("mReferrer");
            declaredField.setAccessible(true);
            return (String) declaredField.get(activity);
        } catch (Exception e) {
            Log.e(TAG, "error ", e);
            return activity.getCallingPackage();
        }
    }
}
