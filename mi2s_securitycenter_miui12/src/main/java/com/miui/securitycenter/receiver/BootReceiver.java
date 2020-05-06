package com.miui.securitycenter.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.o.g.e;
import com.miui.antispam.service.AntiSpamService;
import com.miui.appcompatibility.d;
import com.miui.applicationlock.c.C0267k;
import com.miui.applicationlock.c.o;
import com.miui.earthquakewarning.service.EarthquakeWarningService;
import com.miui.earthquakewarning.utils.Utils;
import com.miui.gamebooster.m.W;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.service.tm.TrafficManageService;
import com.miui.powercenter.autotask.C0489s;
import com.miui.powercenter.bootshutdown.a;
import com.miui.powercenter.provider.PowerSaveService;
import com.miui.powercenter.quickoptimize.C0533l;
import com.miui.securitycenter.h;
import com.miui.securitycenter.utils.f;
import java.util.Calendar;
import miui.security.SecurityManager;

public class BootReceiver extends BroadcastReceiver {
    private void a() {
        if (h.g() == -1 && Calendar.getInstance().get(1) > 2014) {
            h.e(System.currentTimeMillis());
        }
    }

    private void a(Context context) {
        a.a(context);
        a.b(context);
    }

    private void b(Context context) {
        C0489s.d(context);
    }

    private void c(Context context) {
        Intent intent = new Intent(context, PowerSaveService.class);
        intent.setAction("com.miui.powercenter.action.TRY_CLOSE_SAVE_MODE");
        context.startService(intent);
    }

    private void d(Context context) {
        try {
            e.a((Object) (SecurityManager) context.getSystemService("security"), "updateLauncherPackageNames", (Class<?>[]) null, new Object[0]);
        } catch (Exception e) {
            Log.e("BootReceiver", "updateLauncherPackageNames exception: ", e);
        }
    }

    public void onReceive(Context context, Intent intent) {
        W.a(context);
        f.b(context);
        o.a(0, context);
        o.e(context, g.a(context));
        if (Constants.System.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            d.b(context).c();
        }
        Class<ComponentName> cls = ComponentName.class;
        try {
            e.b(context, cls, "startServiceAsUser", new Class[]{Intent.class, UserHandle.class}, new Intent(context, AntiSpamService.class), UserHandle.CURRENT_OR_SELF);
        } catch (Exception e) {
            Log.e("BootReceiver", "startServiceAsUser exception: ", e);
        }
        g.c(context, new Intent(context, TrafficManageService.class), B.k());
        C0533l.a(0);
        a(context);
        c(context);
        b(context);
        a();
        C0267k.f(context);
        if (Utils.isEarthquakeWarningOpen()) {
            context.startService(new Intent(context, EarthquakeWarningService.class));
        }
        d(context);
    }
}
