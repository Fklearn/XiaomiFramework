package com.miui.securitycenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import b.b.a.e.c;
import b.b.b.d.n;
import b.b.b.p;
import b.b.c.c.a.a;
import b.b.c.j.x;
import com.miui.antispam.db.d;
import com.miui.antivirus.service.GuardService;
import com.miui.gamebooster.m.C0375f;
import com.miui.gamebooster.m.C0381l;
import com.miui.gamebooster.m.C0384o;
import com.miui.gamebooster.m.C0389u;
import com.miui.gamebooster.m.Z;
import com.miui.gamebooster.m.ba;
import com.miui.gamebooster.mutiwindow.FreeformWindowService;
import com.miui.gamebooster.service.GameBoosterService;
import com.miui.gamebooster.service.VideoToolBoxService;
import com.miui.monthreport.l;
import com.miui.networkassistant.config.SharedPreferenceHelper;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.service.wrapper.TmBinderCacher;
import com.miui.networkassistant.traffic.statistic.PreSetGroup;
import com.miui.powercenter.utils.o;
import com.miui.push.b;
import com.miui.securityscan.shortcut.e;
import com.miui.superpower.b.k;
import com.miui.superpower.notification.SuperPowerTileService;
import java.util.ArrayList;
import miui.external.ApplicationDelegate;

public class Application extends miui.external.Application {

    /* renamed from: a  reason: collision with root package name */
    private static int f7460a;

    /* renamed from: b  reason: collision with root package name */
    private static Application f7461b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public boolean f7462c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public String f7463d;

    static /* synthetic */ int a() {
        int i = f7460a;
        f7460a = i + 1;
        return i;
    }

    /* access modifiers changed from: private */
    public void a(boolean z) {
        if (f7460a == 0 && h.a(getContentResolver())) {
            sendBroadcast(new Intent("com.miui.securitycenter.action.UPDATE_NOTIFICATION").putExtra("notify", z));
        }
    }

    static /* synthetic */ int b() {
        int i = f7460a;
        f7460a = i - 1;
        return i;
    }

    public static Resources c() {
        return f7461b.getResources();
    }

    public static Application d() {
        return f7461b;
    }

    /* access modifiers changed from: private */
    public void g() {
        if (!x.h(this, "com.miui.cleanmaster")) {
            e.c(this, e.a.CLEANMASTER);
        }
    }

    /* access modifiers changed from: private */
    public void h() {
        if (Build.VERSION.SDK_INT >= 24) {
            ComponentName componentName = new ComponentName(getPackageName(), SuperPowerTileService.class.getName());
            if (k.o(this) && getPackageManager().getComponentEnabledSetting(componentName) != 1) {
                getPackageManager().setComponentEnabledSetting(componentName, 1, 1);
                Settings.Secure.putInt(getContentResolver(), "power_supersave_tile_enabled", 1);
            }
        }
    }

    /* access modifiers changed from: private */
    public static void h(Context context) {
        a.a(new e(context));
    }

    /* access modifiers changed from: private */
    public void i(Context context) {
        registerReceiver(new b.b.a.c.a(), new IntentFilter("miui.intent.action.FIREWALL_UPDATED"));
        if (!miui.os.Build.IS_STABLE_VERSION && !d.f()) {
            c.h(context);
            d.c(true);
        }
    }

    /* access modifiers changed from: private */
    public static void j(Context context) {
        com.miui.securityscan.d.c a2 = com.miui.securityscan.d.c.a(context);
        a2.c();
        a2.d();
    }

    /* access modifiers changed from: private */
    public void k(Context context) {
        TmBinderCacher.initForUIProcess(context);
        SharedPreferenceHelper.initForUIProcess();
        PreSetGroup.initGroupMap(context);
        SimCardHelper.asyncInit(context);
    }

    /* access modifiers changed from: private */
    public static void l(Context context) {
        FreeformWindowService.a(context);
        if (!C0389u.a(context) && !C0375f.a()) {
            ba.b(context);
            if (C0381l.b(context) && com.miui.gamebooster.c.a.a(context).k(true)) {
                context.startService(new Intent(context, GameBoosterService.class));
            }
        } else if (Z.b(context, (String) null)) {
            ba.a(context, (String) null);
        }
    }

    /* access modifiers changed from: private */
    public static void m(Context context) {
        l.a(context);
        b.a(context).a();
    }

    /* access modifiers changed from: private */
    public void n(Context context) {
        registerReceiver(new com.miui.antivirus.receiver.b(), new IntentFilter(C0384o.a("ACTION_USER_SWITCHED")));
        ArrayList arrayList = new ArrayList(p.b(context));
        ArrayList<String> h = n.h(context);
        if (!h.containsAll(arrayList)) {
            arrayList.retainAll(h);
            p.b((ArrayList<String>) arrayList);
        }
        if (p.j()) {
            Intent intent = new Intent(context, GuardService.class);
            intent.setAction("action_register_foreground_notification");
            context.startService(intent);
        }
    }

    /* access modifiers changed from: private */
    public static void o(Context context) {
        a.a(new d(context));
    }

    /* access modifiers changed from: private */
    public static void p(Context context) {
        o.p(context);
    }

    /* access modifiers changed from: private */
    public static void q(Context context) {
        if (com.miui.gamebooster.videobox.utils.e.a()) {
            try {
                context.startService(new Intent(context, VideoToolBoxService.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public void r(Context context) {
        b.b.k.b.a aVar = new b.b.k.b.a(context);
        if (aVar.d()) {
            aVar.d(false);
            aVar.c(false);
        }
    }

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        f7461b = this;
        b.b.c.c.d.a(context);
        this.f7463d = x.a(context, Process.myPid());
    }

    public boolean e() {
        return this.f7462c;
    }

    /* access modifiers changed from: protected */
    public void f() {
        registerActivityLifecycleCallbacks(new c(this));
    }

    public ApplicationDelegate onCreateApplicationDelegate() {
        new a(this).execute(new Void[0]);
        return new b(this);
    }
}
