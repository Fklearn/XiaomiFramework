package com.miui.powercenter.quickoptimize;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import b.b.o.g.c;
import b.b.o.g.e;
import com.miui.powercenter.quickoptimize.C0530i;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class z {

    public interface a {
        void a(List<String> list);
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

    public static List<String> a(Context context, List<String> list) {
        ArrayList arrayList = new ArrayList();
        for (String next : list) {
            boolean z = false;
            try {
                z = ((Boolean) e.a(Class.forName("miui.securityspace.XSpaceUserHandle"), Boolean.TYPE, "isAppInXSpace", (Class<?>[]) new Class[]{Context.class, String.class}, context, next)).booleanValue();
            } catch (Exception e) {
                Log.e("RunningAppsHelper", "getRunningXSpaceAppList exception: ", e);
            }
            if (!TextUtils.isEmpty(next) && z) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public static void a(Context context, a aVar) {
        C0530i.a(context).a((C0530i.b) new y(context.getApplicationContext(), aVar));
    }

    public static void a(List<String> list, int i) {
        try {
            ArrayMap arrayMap = new ArrayMap();
            arrayMap.put(Integer.valueOf(((Integer) e.a(Class.forName("miui.process.ProcessConfig"), "KILL_LEVEL_UNKNOWN", Integer.TYPE)).intValue()), list);
            int intValue = ((Integer) e.a(Class.forName("miui.process.ProcessConfig"), "POLICY_LOCK_SCREEN_CLEAN", Integer.TYPE)).intValue();
            Object a2 = c.a(Class.forName("miui.process.ProcessConfig"), (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE, ArrayMap.class}, Integer.valueOf(intValue), Integer.valueOf(i), arrayMap);
            e.a(a2, "setRemoveTaskNeeded", (Class<?>[]) new Class[]{Boolean.TYPE}, true);
            Class<?> cls = Class.forName("miui.process.ProcessManager");
            Class cls2 = Boolean.TYPE;
            Class[] clsArr = new Class[1];
            clsArr[0] = Class.forName("miui.process.ProcessConfig");
            e.a(cls, cls2, "kill", (Class<?>[]) clsArr, a2);
        } catch (Exception e) {
            Log.e("RunningAppsHelper", "killRunningAppList exception", e);
        }
    }

    public static String b(Context context) {
        try {
            List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
            return (runningTasks == null || runningTasks.isEmpty()) ? "" : runningTasks.get(0).topActivity.getPackageName();
        } catch (Exception unused) {
            Log.e("RunningAppsHelper", "get foreground app error");
            return "";
        }
    }

    /* access modifiers changed from: private */
    public static List<String> b() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("com.mfashiongallery.emag");
        return arrayList;
    }

    public static void b(Context context, a aVar) {
        C0530i.a(context).a((C0530i.a) new x(aVar));
    }

    public static void b(List<String> list, int i) {
        try {
            ArrayMap arrayMap = new ArrayMap();
            arrayMap.put(Integer.valueOf(((Integer) e.a(Class.forName("miui.process.ProcessConfig"), "KILL_LEVEL_FORCE_STOP", Integer.TYPE)).intValue()), list);
            int intValue = ((Integer) e.a(Class.forName("miui.process.ProcessConfig"), "POLICY_LOCK_SCREEN_CLEAN", Integer.TYPE)).intValue();
            Object a2 = c.a(Class.forName("miui.process.ProcessConfig"), (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE, ArrayMap.class}, Integer.valueOf(intValue), Integer.valueOf(i), arrayMap);
            e.a(a2, "setRemoveTaskNeeded", (Class<?>[]) new Class[]{Boolean.TYPE}, true);
            Class<?> cls = Class.forName("miui.process.ProcessManager");
            Class cls2 = Boolean.TYPE;
            Class[] clsArr = new Class[1];
            clsArr[0] = Class.forName("miui.process.ProcessConfig");
            e.a(cls, cls2, "kill", (Class<?>[]) clsArr, a2);
        } catch (Exception e) {
            Log.e("RunningAppsHelper", "killRunningAppList exception", e);
        }
    }

    /* access modifiers changed from: private */
    public static List<String> c(Context context) {
        ActivityInfo activityInfo;
        HashSet hashSet = new HashSet();
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        List<ActivityManager.RecentTaskInfo> recentTasks = activityManager.getRecentTasks(1001, 6);
        for (int i = 1; i < recentTasks.size(); i++) {
            ResolveInfo a2 = a(context, recentTasks.get(i));
            if (!(a2 == null || (activityInfo = a2.activityInfo) == null || activityInfo.packageName == null)) {
                String str = a2.activityInfo.packageName;
                if (!TextUtils.isEmpty(str)) {
                    hashSet.add(str);
                }
            }
        }
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        ArrayList arrayList = new ArrayList();
        if (Build.VERSION.SDK_INT >= 28) {
            String b2 = b(context);
            if (!TextUtils.isEmpty(b2)) {
                Log.i("RunningAppsHelper", "topPackageName" + b2);
                arrayList.add(b2);
            }
            arrayList.add(context.getPackageName());
        }
        for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
            String[] strArr = next.pkgList;
            String str2 = strArr != null ? strArr[0] : null;
            if (!TextUtils.isEmpty(str2)) {
                try {
                    ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(str2, 0);
                    if (next.importance <= 200) {
                        arrayList.add(str2);
                    } else if (b.b.c.j.e.a(context, str2, 0)) {
                        hashSet.remove(str2);
                    } else if ((applicationInfo.flags & 1) == 0) {
                        hashSet.add(str2);
                    }
                } catch (Exception e) {
                    Log.e("RunningAppsHelper", "RunningAppProcess", e);
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
}
