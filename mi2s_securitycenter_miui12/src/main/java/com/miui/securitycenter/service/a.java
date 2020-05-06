package com.miui.securitycenter.service;

import android.content.Context;
import android.content.Intent;
import b.b.b.a.b;
import b.b.c.j.B;
import b.b.c.j.z;
import com.miui.appcompatibility.d;
import com.miui.applicationlock.a.j;
import com.miui.earthquakewarning.EarthquakeWarningManager;
import com.miui.earthquakewarning.analytics.AnalyticHelper;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.U;
import com.miui.networkassistant.xman.XmanHelper;
import com.miui.networkassistant.zman.ZmanHelper;
import com.miui.securitycenter.dynamic.DynamicServiceManager;
import com.miui.securitycenter.h;
import com.miui.securityscan.a.G;

class a implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7528a;

    a(Context context) {
        this.f7528a = context;
    }

    public void run() {
        if (z.a(h.e()) >= 7) {
            G.c(this.f7528a);
            b.a(this.f7528a);
            com.miui.permcenter.a.a.b(this.f7528a);
            com.miui.applicationlock.a.h.a(this.f7528a);
            j.a(this.f7528a);
            com.miui.powercenter.a.a.a(this.f7528a);
            com.miui.optimizemanage.a.a.b();
            b.b.a.a.a.a(this.f7528a);
            b.b.e.a.a.a(this.f7528a);
            com.miui.googlebase.a.a.a(this.f7528a);
            com.miui.idprovider.a.a.a(this.f7528a);
            h.c(System.currentTimeMillis());
            if (B.h()) {
                this.f7528a.sendBroadcast(new Intent("com.miui.securitycenter.action.TRACK_EVERY_WEEK"), "com.miui.securitycenter.permission.RECEIVE_TRACK_EVERYWEEK");
            }
            DynamicServiceManager.getInstance(this.f7528a).track();
            b.b.m.a.e(this.f7528a);
            com.miui.appmanager.a.a.d(this.f7528a);
            com.miui.appmanager.a.a.c(this.f7528a);
            com.miui.appmanager.a.a.a(this.f7528a);
            b.b.k.a.a.a(this.f7528a);
            AnalyticHelper.trackUpdateToggleStat();
            com.miui.warningcenter.analytics.AnalyticHelper.trackUpdateToggleStat();
            XmanHelper.uploadXmanData(this.f7528a);
            ZmanHelper.trackSecurityShareStateEvent(this.f7528a);
            com.miui.appmanager.a.a.b(this.f7528a);
            com.miui.permcenter.a.a.a(this.f7528a);
        }
        ConnectivityChangeJobService2.e(this.f7528a);
        C0373d.b(this.f7528a);
        EarthquakeWarningManager.getInstance().requestSignature();
        U.c(this.f7528a);
        ConnectivityChangeJobService2.g(this.f7528a);
        ConnectivityChangeJobService2.f(this.f7528a);
        d.a(this.f7528a);
    }
}
