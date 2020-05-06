package com.miui.antivirus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import b.b.b.g;
import com.miui.activityutil.o;
import com.miui.appcompatibility.m;
import com.miui.applicationlock.c.C0267k;
import com.miui.appmanager.C0322e;
import com.miui.cleanmaster.d;
import com.miui.cleanmaster.h;
import com.miui.gamebooster.mutiwindow.f;
import com.miui.networkassistant.config.Constants;
import com.miui.permcenter.compact.AppOpsUtilsCompat;
import com.miui.securitycenter.utils.i;
import com.miui.securityscan.shortcut.e;
import miui.os.SystemProperties;

public class a extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("AppInstalledReceiver", "receive broadcast");
        if (intent != null && intent.getAction() != null) {
            if (Constants.System.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
                Uri data = intent.getData();
                if (data != null) {
                    String schemeSpecificPart = data.getSchemeSpecificPart();
                    Log.d("AppInstalledReceiver", "packageName: " + schemeSpecificPart);
                    boolean booleanExtra = intent.getBooleanExtra("android.intent.extra.REPLACING", false);
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (b.b.b.d.a.a(context) && miui.os.Build.IS_INTERNATIONAL_BUILD && SystemProperties.getBoolean("persist.sys.miui_optimization", !o.f2310b.equals(SystemProperties.get("ro.miui.cts")))) {
                            m.a(context, schemeSpecificPart, booleanExtra);
                        }
                        Intent intent2 = new Intent("com.miui.appcompatibility.receiver.AppCompatStateReceiver");
                        intent2.putExtra("package_name", data.getSchemeSpecificPart());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
                    }
                    g.a(context).a(schemeSpecificPart, true);
                    C0322e.a(context, schemeSpecificPart, (String) null, intent.hasExtra("android.intent.extra.REPLACING"));
                    C0267k.a(schemeSpecificPart, context, intent.hasExtra("android.intent.extra.REPLACING"));
                    C0267k.b(schemeSpecificPart, context, intent.hasExtra("android.intent.extra.REPLACING"));
                    f.a(context, schemeSpecificPart, intent.getIntExtra("android.intent.extra.UID", 0));
                    i.a(context, schemeSpecificPart);
                    if ("com.miui.packageinstaller".equals(schemeSpecificPart)) {
                        AppOpsUtilsCompat.autoOptmize(context);
                    }
                    if ("com.miui.cleanmaster".equals(schemeSpecificPart) && !h.a().b()) {
                        h.a().a(context);
                        return;
                    }
                    return;
                }
                return;
            }
            if (intent.getAction().equals(Constants.System.ACTION_PACKAGE_REMOVED)) {
                String schemeSpecificPart2 = intent.getData().getSchemeSpecificPart();
                if (!intent.hasExtra("android.intent.extra.REPLACING")) {
                    C0322e.c(context, schemeSpecificPart2);
                    g.a(context).a(schemeSpecificPart2, false);
                }
                i.b(context, schemeSpecificPart2);
                if ("com.miui.cleanmaster".equals(schemeSpecificPart2)) {
                    e.c(context, e.a.CLEANMASTER);
                }
                if (!"com.miui.cleanmaster".equals(schemeSpecificPart2)) {
                    return;
                }
            } else if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED") && "com.miui.cleanmaster".equals(intent.getData().getSchemeSpecificPart())) {
                if (!h.a().b()) {
                    h.a().a(context);
                }
            } else {
                return;
            }
            d.a(context).c(0);
            com.miui.securitycenter.h.b(context, false, 0);
            com.miui.securitycenter.h.a(context, false, 0);
            com.miui.securitycenter.h.c(context, false, 0);
        }
    }
}
