package com.miui.securitycenter;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import b.b.c.j.B;
import b.b.c.j.e;
import com.miui.activityutil.ActivityUtil;
import com.miui.analytics.AnalyticsUtil;
import com.miui.analytics.DeviceInfoManager;
import com.miui.antivirus.service.VirusAutoUpdateJobService;
import com.miui.appcompatibility.d;
import com.miui.applicationlock.c.C0259c;
import com.miui.appmanager.d.c;
import com.miui.cleanmaster.h;
import com.miui.googlebase.GoogleBaseClientProxy;
import com.miui.luckymoney.upgrade.LuckyMoneyHelper;
import com.miui.luckymoney.utils.FloatWindowHelper;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.service.FirewallService;
import com.miui.networkassistant.service.tm.TrafficManageService;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.vpn.miui.MiuiVpnManageService;
import com.miui.permcenter.compact.AppOpsUtilsCompat;
import com.miui.permcenter.j;
import com.miui.securitycenter.dynamic.DynamicServiceManager;
import com.miui.securitycenter.receiver.CleanMasterReceiver;
import com.miui.securitycenter.service.ConnectivityChangeJobService2;
import com.miui.securitycenter.utils.a;
import com.miui.securitycenter.utils.g;
import com.miui.securitycenter.utils.i;
import com.miui.securityscan.model.ModelUpdater;
import miui.R;
import miui.external.ApplicationDelegate;
import miui.os.Build;

class b extends ApplicationDelegate {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Application f7469a;

    b(Application application) {
        this.f7469a = application;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = configuration.orientation;
        if (i == 2) {
            FloatWindowHelper.removeFloatWindow(getApplicationContext());
        } else if (i == 1) {
            FloatWindowHelper.showFloatWindow(getApplicationContext());
        }
    }

    public void onCreate() {
        super.onCreate();
        AnalyticsUtil.initMiStats(getApplicationContext());
        if (getPackageName().equals(this.f7469a.f7463d)) {
            this.f7469a.f();
            this.f7469a.k(this);
            this.f7469a.h();
        } else if ("com.miui.securitycenter:ui".equals(this.f7469a.f7463d)) {
            setTheme(R.style.Theme_Light);
        } else if ("com.miui.securitycenter.remote".equals(this.f7469a.f7463d)) {
            boolean unused = this.f7469a.f7462c = true;
            this.f7469a.h();
            g.a(this);
            o.a(this);
            Application.p(this);
            a.b();
            VirusAutoUpdateJobService.a((Context) this);
            ModelUpdater.getInstance().init();
            CleanMasterReceiver.a();
            Application.j(this);
            Application.m(this);
            this.f7469a.n(this);
            Application.l(this);
            Application.q(this);
            this.f7469a.i(this);
            if (!Build.IS_INTERNATIONAL_BUILD && B.f()) {
                GoogleBaseClientProxy.a(this);
            }
            Application.o(this);
            ConnectivityChangeJobService2.d(this);
            C0259c.a((Context) this);
            DynamicServiceManager.main(this);
            ActivityUtil.setAllowNetworking(this, h.i());
            ActivityUtil.delayedUpload(this, 13000);
            LuckyMoneyHelper.init(this);
            if (Build.VERSION.SDK_INT > 23) {
                registerReceiver(new com.miui.securitycenter.receiver.a(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            }
            SimCardHelper.init(this);
            b.b.c.j.g.c(this, new Intent(this, FirewallService.class), B.k());
            b.b.c.j.g.c(this, new Intent(this, TrafficManageService.class), B.k());
            if (!DeviceUtil.IS_INTERNATIONAL_BUILD && p.a() >= 7 && DeviceUtil.IS_L_OR_LATER) {
                b.b.c.j.g.c(this, new Intent(this, MiuiVpnManageService.class), B.k());
            }
            if (Build.VERSION.SDK_INT >= 23) {
                d.b((Context) this).a();
            }
            if (Build.VERSION.SDK_INT >= 24) {
                j.a((Context) this);
            }
            if (e.b() > 7) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Constants.System.ACTION_USER_PRESENT);
                registerReceiver(new c(), intentFilter);
            }
            DeviceInfoManager.handleTask(this, false);
            this.f7469a.r(this);
            i.a(this);
            AppOpsUtilsCompat.autoOptmize(this);
            this.f7469a.g();
            h.a().a(this);
            Application.h(this);
        }
    }
}
