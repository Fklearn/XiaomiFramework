package com.miui.gamebooster.customview;

import android.os.Handler;
import b.b.c.j.z;
import com.miui.gamebooster.m.C0380k;
import com.miui.gamebooster.m.C0385p;
import com.miui.gamebooster.m.da;
import com.miui.powercenter.utils.o;
import com.miui.warningcenter.WarningCenterAlertAdapter;

class r implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f4223a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Handler f4224b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ GameBoxView f4225c;

    r(GameBoxView gameBoxView, boolean z, Handler handler) {
        this.f4225c = gameBoxView;
        this.f4223a = z;
        this.f4224b = handler;
    }

    public void run() {
        while (this.f4225c.F) {
            if (this.f4223a) {
                String unused = this.f4225c.v = String.valueOf(C0385p.a());
            }
            String unused2 = this.f4225c.w = da.a();
            int r = this.f4225c.getMaxFps();
            int ceil = (int) Math.ceil((double) C0380k.a());
            if (ceil <= r) {
                r = ceil;
            }
            String unused3 = this.f4225c.x = String.valueOf(r);
            String unused4 = this.f4225c.y = z.a(System.currentTimeMillis(), WarningCenterAlertAdapter.FORMAT_TIME);
            GameBoxView gameBoxView = this.f4225c;
            int unused5 = gameBoxView.E = o.e(gameBoxView.e);
            if (this.f4225c.E == 100) {
                GameBoxView gameBoxView2 = this.f4225c;
                int unused6 = gameBoxView2.E = gameBoxView2.E - 1;
            }
            this.f4224b.post(this.f4225c.ea);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
