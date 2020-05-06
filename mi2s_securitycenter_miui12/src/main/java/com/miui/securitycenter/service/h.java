package com.miui.securitycenter.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.x;
import com.miui.cleanmaster.f;
import com.miui.cleanmaster.g;
import com.miui.securityscan.MainActivity;
import com.miui.securityscan.a.G;
import miui.os.Build;
import miui.securitycenter.utils.SecurityCenterHelper;

class h extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ NotificationService f7535a;

    h(NotificationService notificationService) {
        this.f7535a = notificationService;
    }

    public void onReceive(Context context, Intent intent) {
        String str;
        Intent action;
        if (intent.getAction().equals("com.miui.securitycenter.action.TRACK_NOTIFICATION_CLICK")) {
            String stringExtra = intent.getStringExtra("track_module");
            if (!TextUtils.isEmpty(stringExtra)) {
                G.z(stringExtra);
                Intent addFlags = new Intent().addFlags(268468224);
                if (stringExtra.equals("securitycenter")) {
                    action = new Intent(addFlags).setComponent(new ComponentName(context, MainActivity.class));
                } else if (stringExtra.equals("memory_clean")) {
                    if (Build.IS_INTERNATIONAL_BUILD) {
                        try {
                            Intent intent2 = new Intent(addFlags);
                            intent2.setAction("miui.intent.action.GARBAGE_MEMORY_CLEAN");
                            intent2.putExtra("enter_homepage_way", "securityscan_notification");
                            context.startActivity(intent2);
                            SecurityCenterHelper.collapseStatusPanels(this.f7535a.getApplicationContext());
                            return;
                        } catch (Exception e) {
                            e = e;
                            str = "garbage memory clean";
                        }
                    } else {
                        context.sendBroadcast(new Intent(intent).setAction("com.miui.securitycenter.action.CLEAR_MEMORY"));
                        return;
                    }
                } else if (stringExtra.equals("powercenter")) {
                    action = new Intent(addFlags).setAction("miui.intent.action.POWER_MANAGER");
                } else if (stringExtra.equals("garbage_clean")) {
                    try {
                        Intent intent3 = new Intent(addFlags);
                        intent3.setAction("miui.intent.action.GARBAGE_CLEANUP");
                        intent3.putExtra("enter_homepage_way", "securityscan_notification");
                        if (f.a(context)) {
                            x.c(context, intent3);
                        } else {
                            g.b(context, intent3);
                        }
                        SecurityCenterHelper.collapseStatusPanels(this.f7535a.getApplicationContext());
                        return;
                    } catch (Exception e2) {
                        e = e2;
                        str = "garbage clean";
                        Log.e("NotificationService", str, e);
                        return;
                    }
                } else {
                    return;
                }
                context.startActivity(action);
                SecurityCenterHelper.collapseStatusPanels(this.f7535a.getApplicationContext());
            }
        }
    }
}
