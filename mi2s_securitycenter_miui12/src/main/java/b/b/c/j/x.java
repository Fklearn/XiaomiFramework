package b.b.c.j;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.b;
import b.b.o.b.a.a;
import b.b.o.g.c;
import com.google.android.exoplayer2.C;
import com.market.sdk.p;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import miui.securitycenter.utils.SecurityCenterHelper;
import org.json.JSONObject;

public class x {
    public static ResolveInfo a(Context context, String str) {
        if (context == null || str == null) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(str);
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        if (queryIntentActivities == null || queryIntentActivities.isEmpty()) {
            return null;
        }
        return queryIntentActivities.get(0);
    }

    public static String a(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("PackageUtils", "getPackageVersionName", e);
            return "1.5.160106";
        }
    }

    public static String a(Context context, int i) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
            if (next.pid == i) {
                return next.processName;
            }
        }
        return null;
    }

    public static String a(Context context, ApplicationInfo applicationInfo) {
        String str = applicationInfo.packageName;
        return "root".equals(str) ? b.f1606c.toString() : "com.android.shell".equals(str) ? b.f1607d.toString() : b.b.c.b.b.a(context).a(applicationInfo).a();
    }

    public static void a(ActivityManager activityManager, String str) {
        SecurityCenterHelper.forceStopPackage(activityManager, str);
    }

    private static void a(Context context, String str, String str2, String str3, String str4) {
        try {
            Intent intent = new Intent("com.xiaomi.market.service.AppDownloadInstallService");
            intent.setPackage("com.xiaomi.market");
            JSONObject jSONObject = new JSONObject();
            if (!TextUtils.isEmpty(str4)) {
                jSONObject.put("ext_apkChannel", str4);
            }
            jSONObject.put("ext_passback", str3);
            intent.putExtra("extra_query_params", jSONObject.toString());
            intent.putExtra("packageName", str);
            intent.putExtra("ref", str2);
            intent.putExtra("show_cta", "true");
            intent.putExtra("senderPackageName", context.getPackageName());
            context.startService(intent);
        } catch (Exception e) {
            Log.e("PackageUtils", "startAppDownloadOld exception", e);
        }
    }

    private static void a(Context context, String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        Intent intent = new Intent("com.xiaomi.market.service.AppDownloadService");
        intent.setPackage("com.xiaomi.market");
        Context context2 = context;
        context.bindService(intent, new w(str, str2, str7, str3, context, str4, str5, str6), 1);
    }

    public static void a(Context context, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8) {
        int e = e(context, "com.xiaomi.market");
        Log.d("PackageUtils", "marketVersionCode :" + e);
        if (e >= 1914651 && !TextUtils.isEmpty(str8)) {
            a(str8);
        } else if (e >= 1914111) {
            a(context, str, str2, str3, str4, str5, str6, str7);
        } else {
            a(context, str, str2, str3, str7);
        }
    }

    private static void a(String str) {
        p.b().a().a(str);
    }

    public static boolean a(Context context, Intent intent) {
        return a(context, intent, true);
    }

    public static boolean a(Context context, Intent intent, int i) {
        List<ResolveInfo> queryIntentActivities;
        if (intent == null || context == null || !(context instanceof Activity) || (queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 1)) == null || queryIntentActivities.isEmpty()) {
            return false;
        }
        ((Activity) context).startActivityForResult(intent, i);
        return true;
    }

    public static boolean a(Context context, Intent intent, boolean z) {
        int i;
        String str = "com.xiaomi.vipaccount";
        String dataString = intent.getDataString();
        if (Build.IS_INTERNATIONAL_BUILD || TextUtils.isEmpty(dataString) || !dataString.startsWith("mio:") || !dataString.contains("fallback=")) {
            return false;
        }
        try {
            int indexOf = dataString.indexOf("fallback=");
            if (indexOf <= 0 || (i = indexOf + 9) >= dataString.length()) {
                return false;
            }
            intent.setData(Uri.parse(URLDecoder.decode(dataString.substring(i, dataString.length()), C.UTF8_NAME)));
            if (!h(context, str)) {
                str = "com.android.browser";
            }
            intent.setPackage(str);
            List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 1);
            if (queryIntentActivities == null || queryIntentActivities.isEmpty()) {
                return false;
            }
            if (z) {
                context.startActivity(intent);
            }
            return true;
        } catch (Exception e) {
            Log.e("PackageUtils", "adapteVipAccount", e);
            return false;
        }
    }

    public static boolean a(Context context, String str, int i) {
        return a.a(str, 0, i) != null;
    }

    public static boolean a(ApplicationInfo applicationInfo) {
        return applicationInfo.uid >= 10000 && (applicationInfo.flags & 1) == 0;
    }

    public static boolean a(int[] iArr, String str) {
        if (iArr.length == 0) {
            return false;
        }
        try {
            c.a a2 = c.a.a("android.app.ActivityManagerNative");
            a2.b("getDefault", (Class<?>[]) null, new Object[0]);
            a2.e();
            a2.a("killPids", new Class[]{int[].class, String.class, Boolean.TYPE}, iArr, str, true);
            return a2.a();
        } catch (Exception e) {
            Log.e("PackageUtils", "killPids", e);
            return false;
        }
    }

    public static List<String> b(Context context) {
        if (context == null) {
            return null;
        }
        int j = B.j();
        ArrayList arrayList = new ArrayList();
        for (ActivityManager.RunningAppProcessInfo next : ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses()) {
            if (B.c(next.uid) == j || (j == 0 && B.c(next.uid) == 999)) {
                String[] strArr = next.pkgList;
                for (String add : strArr) {
                    arrayList.add(add);
                }
            }
        }
        return arrayList;
    }

    public static void b(Context context, String str) {
        a((ActivityManager) context.getSystemService("activity"), str);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0006, code lost:
        r2 = r2.getPackageManager().queryIntentActivities(r3, 1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean b(android.content.Context r2, android.content.Intent r3) {
        /*
            r0 = 0
            if (r3 == 0) goto L_0x0018
            if (r2 != 0) goto L_0x0006
            goto L_0x0018
        L_0x0006:
            android.content.pm.PackageManager r2 = r2.getPackageManager()
            r1 = 1
            java.util.List r2 = r2.queryIntentActivities(r3, r1)
            if (r2 == 0) goto L_0x0018
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0018
            return r1
        L_0x0018:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.j.x.b(android.content.Context, android.content.Intent):boolean");
    }

    public static PackageInfo c(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("PackageUtils", "getPackageVersionName", e);
            return null;
        }
    }

    public static ArrayList<PackageInfo> c(Context context) {
        List<PackageInfo> a2 = b.b.c.b.b.a(context).a();
        ArrayList<PackageInfo> arrayList = new ArrayList<>();
        for (PackageInfo next : a2) {
            if (a(next.applicationInfo)) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public static boolean c(Context context, Intent intent) {
        if (!(intent == null || context == null)) {
            if (a(context, intent)) {
                return true;
            }
            List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 1);
            if (queryIntentActivities != null && !queryIntentActivities.isEmpty()) {
                context.startActivity(intent);
                return true;
            }
        }
        return false;
    }

    public static PackageInfo d(Context context, String str) {
        PackageInfo packageArchiveInfo;
        if (str == null || (packageArchiveInfo = context.getPackageManager().getPackageArchiveInfo(str, 1)) == null) {
            return null;
        }
        ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
        applicationInfo.sourceDir = str;
        applicationInfo.publicSourceDir = str;
        return packageArchiveInfo;
    }

    public static int e(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        try {
            return context.getPackageManager().getPackageInfo(str, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("PackageUtils", "getPackageVersionName", e);
            return 0;
        }
    }

    public static String f(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return "1.5.160106";
        }
        try {
            return context.getPackageManager().getPackageInfo(str, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("PackageUtils", "getPackageVersionName", e);
            return "1.5.160106";
        }
    }

    public static boolean g(Context context, String str) {
        Intent intent = new Intent();
        intent.setPackage(str);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 32);
        return queryIntentActivities != null && !queryIntentActivities.isEmpty();
    }

    public static boolean h(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 0) != null;
        } catch (Exception e) {
            Log.e("PackageUtils", "isInstalledPackage ", e);
            return false;
        }
    }

    public static boolean i(Context context, String str) {
        if (context == null) {
            return false;
        }
        try {
            return (context.getPackageManager().getApplicationInfo(str, 0).flags & 1) != 0;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static CharSequence j(Context context, String str) {
        if ("root".equals(str)) {
            return b.f1606c;
        }
        if ("com.android.shell".equals(str)) {
            return b.f1607d;
        }
        try {
            return b.b.c.b.b.a(context).a(str).a();
        } catch (PackageManager.NameNotFoundException unused) {
            return str;
        }
    }
}
