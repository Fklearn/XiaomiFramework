package com.miui.gamebooster.service;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0393y;
import java.util.List;

/* renamed from: com.miui.gamebooster.service.h  reason: case insensitive filesystem */
class C0407h extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f4816a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f4817b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ GameBoosterService f4818c;

    C0407h(GameBoosterService gameBoosterService, Context context, boolean z) {
        this.f4818c = gameBoosterService;
        this.f4816a = context;
        this.f4817b = z;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        ComponentName componentName;
        String a2 = b.a("key_hang_up_pkg", (String) null);
        int i = 0;
        if (a2 != null) {
            C0393y.a(this.f4816a, a2, false);
            b.b("key_hang_up_pkg", (String) null);
        }
        ActivityManager activityManager = (ActivityManager) this.f4816a.getSystemService("activity");
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        if (!(runningTasks == null || runningTasks.size() <= 0 || (componentName = runningTasks.get(0).topActivity) == null || componentName.getPackageName() == null)) {
            String packageName = componentName.getPackageName();
            for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
                if (next.processName.equals(packageName) && next.importance == 100) {
                    i = next.uid;
                }
            }
            synchronized (this.f4818c.z) {
                if (this.f4818c.v.booleanValue() && this.f4818c.r.contains(packageName)) {
                    r.a((Context) this.f4818c, this.f4818c.i).a(com.miui.gamebooster.d.b.GAME);
                    r.a((Context) this.f4818c, this.f4818c.i).a(packageName);
                    r.a((Context) this.f4818c, this.f4818c.i).c(i);
                    this.f4818c.g();
                } else if (this.f4817b) {
                    this.f4818c.h();
                }
            }
        }
        return null;
    }
}
