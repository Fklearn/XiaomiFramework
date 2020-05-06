package com.miui.securitycenter.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import b.b.c.j.e;
import b.b.c.j.s;
import com.miui.powercenter.utils.o;
import com.xiaomi.stat.MiStat;

class f extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ NotificationService f7533a;

    f(NotificationService notificationService) {
        this.f7533a = notificationService;
    }

    public void onReceive(Context context, Intent intent) {
        int i;
        NotificationService notificationService;
        String action = intent.getAction();
        if (action.equals("com.miui.securitycenter.action.UPDATE_NOTIFICATION")) {
            s.a("update_notification");
            boolean unused = this.f7533a.f7521d = intent.getBooleanExtra("notify", false);
            notificationService = this.f7533a;
            i = o.e(context);
        } else if (action.equals("com.miui.securitycenter.action.CLEAR_MEMORY")) {
            s.a("clear_memory");
            context.sendBroadcast(new Intent("com.android.systemui.taskmanager.Clear"));
            long unused2 = this.f7533a.k = e.a();
            long unused3 = this.f7533a.g = System.currentTimeMillis();
            this.f7533a.a(1000);
            return;
        } else if (action.equals("android.intent.action.BATTERY_CHANGED")) {
            s.a("update_battery");
            int intExtra = intent.getIntExtra(MiStat.Param.LEVEL, 0);
            int intExtra2 = intent.getIntExtra("scale", 0);
            if (intExtra2 != 0 && this.f7533a.f7520c != (i = (intExtra * 100) / intExtra2)) {
                notificationService = this.f7533a;
            } else {
                return;
            }
        } else {
            return;
        }
        int unused4 = notificationService.f7520c = i;
        this.f7533a.a(0);
    }
}
