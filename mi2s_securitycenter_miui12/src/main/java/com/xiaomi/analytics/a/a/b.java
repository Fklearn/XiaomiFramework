package com.xiaomi.analytics.a.a;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.Signature;
import java.io.File;
import java.util.List;

public class b {
    public static Context a(Context context) {
        return (context == null || context.getApplicationContext() == null) ? context : context.getApplicationContext();
    }

    public static Signature[] a(Context context, File file) {
        try {
            return context.getPackageManager().getPackageArchiveInfo(file.getPath(), 64).signatures;
        } catch (Exception unused) {
            return null;
        }
    }

    public static boolean b(Context context) {
        try {
            List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
            return runningTasks != null && !runningTasks.isEmpty() && runningTasks.get(0).topActivity.getPackageName().equals(context.getPackageName());
        } catch (Exception unused) {
        }
    }
}
