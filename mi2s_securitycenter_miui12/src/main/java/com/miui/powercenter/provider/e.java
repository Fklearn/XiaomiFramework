package com.miui.powercenter.provider;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.util.Log;
import com.miui.powercenter.utils.m;
import java.util.ArrayList;
import java.util.List;

public class e {

    /* renamed from: a  reason: collision with root package name */
    private ArrayList<String> f7178a = new ArrayList<>();

    public e() {
        this.f7178a.add("com.mi.dlabs.vr.thor");
        this.f7178a.add("com.mi.dlabs.vr.hulk");
    }

    private ResolveInfo a(Context context, ActivityManager.RecentTaskInfo recentTaskInfo) {
        Intent intent = new Intent(recentTaskInfo.baseIntent);
        ComponentName componentName = recentTaskInfo.origActivity;
        if (componentName != null) {
            intent.setComponent(componentName);
        }
        intent.setFlags((intent.getFlags() & -2097153) | 268435456);
        return context.getPackageManager().resolveActivity(intent, 0);
    }

    private boolean a(Context context) {
        ResolveInfo a2;
        ActivityInfo activityInfo;
        List<ActivityManager.RecentTaskInfo> recentTasks = ((ActivityManager) context.getSystemService("activity")).getRecentTasks(2, 2);
        if (recentTasks == null || recentTasks.isEmpty() || (a2 = a(context, recentTasks.get(0))) == null || (activityInfo = a2.activityInfo) == null || activityInfo.packageName == null) {
            return true;
        }
        Log.i("BatteryNotifyManager", "package on the top, " + a2.activityInfo.packageName);
        return !this.f7178a.contains(a2.activityInfo.packageName);
    }

    public void a(Context context, String str) {
        if (a(context)) {
            m.a(context, str);
        }
    }
}
