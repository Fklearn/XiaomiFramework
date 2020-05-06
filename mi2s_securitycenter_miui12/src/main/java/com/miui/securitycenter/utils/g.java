package com.miui.securitycenter.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import com.miui.antivirus.receiver.a;
import com.miui.appcompatibility.a.c;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.monitor.GolbalReceiver;
import com.miui.powercenter.bootshutdown.b;
import com.miui.powercenter.utils.o;

public class g {
    public static void a(Context context) {
        f(context);
        b(context);
        e(context);
        c(context);
        d(context);
    }

    private static void b(Context context) {
        a aVar = new a();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Constants.System.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme("package");
        context.registerReceiver(aVar, intentFilter);
    }

    private static void c(Context context) {
        com.miui.appmanager.d.a aVar = new com.miui.appmanager.d.a();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.miui.packageinstaller.ACTION_INSTALL_SUCCESS");
        context.registerReceiver(aVar, intentFilter, "android.permission.INSTALL_PACKAGES", (Handler) null);
    }

    private static void d(Context context) {
        GolbalReceiver golbalReceiver = new GolbalReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_SIM_STATE_CHANGED);
        context.registerReceiver(golbalReceiver, intentFilter);
    }

    private static void e(Context context) {
        com.miui.powercenter.powersaver.a aVar = new com.miui.powercenter.powersaver.a();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("miui.intent.action.CHANGE_POWER_SAVE_MODE");
        context.registerReceiver(aVar, intentFilter, "com.miui.powercenter.permission.POWER_COMMAND", (Handler) null);
        b bVar = new b();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.ACTION_SHUTDOWN");
        context.registerReceiver(bVar, intentFilter2);
        com.miui.powercenter.abnormalscan.a aVar2 = new com.miui.powercenter.abnormalscan.a();
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("com.miui.powerkeeper.action.POWER_NOTIFY");
        context.registerReceiver(aVar2, intentFilter3, "com.miui.powerkeeper.permission.POWER_NOTIFY", (Handler) null);
        if (o.d()) {
            com.miui.powercenter.e.a aVar3 = new com.miui.powercenter.e.a();
            IntentFilter intentFilter4 = new IntentFilter();
            intentFilter4.addAction("miui.intent.action.HANG_UP_CHANGED");
            context.registerReceiver(aVar3, intentFilter4, "com.miui.permission.HANG_UP_CHANGED", (Handler) null);
        }
    }

    private static void f(Context context) {
        com.miui.securityscan.e.a aVar = new com.miui.securityscan.e.a();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.xiaomi.market.action.APP_UPDATE_CHECKED");
        intentFilter.addAction("com.xiaomi.market.action.APP_UPDATE_CHECKED_GLOBAL");
        context.registerReceiver(aVar, intentFilter, "miui.permission.USE_INTERNAL_GENERAL_API", (Handler) null);
        com.miui.securityscan.e.b bVar = new com.miui.securityscan.e.b();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.android.settings.action.DEV_OPEN");
        context.registerReceiver(bVar, intentFilter2, "com.miui.securitycenter.permission.SWITCH_DEV_MODE", (Handler) null);
        com.miui.securitycenter.receiver.b bVar2 = new com.miui.securitycenter.receiver.b();
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("com.miui.securitycenter.action.TRACK_CLOUD_COUNT");
        context.registerReceiver(bVar2, intentFilter3, "com.miui.securitrycenter.permission.EXTERNAL_ANLYTICS", (Handler) null);
        com.miui.securitycenter.receiver.a aVar2 = new com.miui.securitycenter.receiver.a();
        IntentFilter intentFilter4 = new IntentFilter();
        intentFilter4.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(aVar2, intentFilter4);
        if (Build.VERSION.SDK_INT >= 24) {
            c cVar = new c();
            IntentFilter intentFilter5 = new IntentFilter();
            intentFilter5.addAction("com.miui.appcompatibility.receiver.AppCompatStateReceive");
            context.registerReceiver(cVar, intentFilter5);
        }
        b.b.q.a aVar3 = new b.b.q.a();
        IntentFilter intentFilter6 = new IntentFilter();
        intentFilter6.addAction(Constants.System.ACTION_USER_PRESENT);
        context.registerReceiver(aVar3, intentFilter6);
    }
}
