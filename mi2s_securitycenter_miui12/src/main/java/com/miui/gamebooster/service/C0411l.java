package com.miui.gamebooster.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.miui.analytics.AnalyticsUtil;
import com.miui.gamebooster.gbservices.m;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;
import com.miui.securitycenter.p;
import com.miui.securityscan.c.a;

/* renamed from: com.miui.gamebooster.service.l  reason: case insensitive filesystem */
class C0411l implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f4824a;

    C0411l(r rVar) {
        this.f4824a = rVar;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IFeedbackControl unused = this.f4824a.r = IFeedbackControl.Stub.a(iBinder);
        if (this.f4824a.r != null) {
            if (p.a() < 12) {
                try {
                    int unused2 = this.f4824a.h = this.f4824a.r.O() ? 1 : 0;
                } catch (Exception e) {
                    Log.i("GameBoosterService", e.toString());
                    AnalyticsUtil.trackException(e);
                }
            } else {
                int unused3 = this.f4824a.h = this.f4824a.r.x();
                if (this.f4824a.h == 2) {
                    this.f4824a.r.b(this.f4824a.v);
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("mThermalMode:");
            sb.append(this.f4824a.h);
            sb.append(a.f7625a ? this.f4824a.v : "");
            Log.i("GameBoosterService", sb.toString());
            if (!this.f4824a.f4831b.contains(this.f4824a.s)) {
                r rVar = this.f4824a;
                rVar.a((m) rVar.s);
                Log.i("GameBoosterService", "addThermal:" + this.f4824a.h);
            }
            this.f4824a.f4832c.unbindService(this.f4824a.x);
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IFeedbackControl unused = this.f4824a.r = null;
    }
}
