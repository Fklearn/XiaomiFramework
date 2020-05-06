package com.miui.gamebooster.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.miui.gamebooster.d.a;
import com.miui.securitycenter.h;

/* renamed from: com.miui.gamebooster.ui.y  reason: case insensitive filesystem */
class C0460y extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f5128a;

    C0460y(N n) {
        this.f5128a = n;
    }

    public void onReceive(Context context, Intent intent) {
        boolean z = false;
        if (this.f5128a.B != intent.getBooleanExtra("gb_intent_xunyouuser", false)) {
            N n = this.f5128a;
            boolean unused = n.B = !n.B;
            N n2 = this.f5128a;
            if (a.a() && this.f5128a.B) {
                z = true;
            }
            n2.d(z);
            com.miui.gamebooster.c.a.ea(this.f5128a.B);
            if (this.f5128a.B) {
                if (this.f5128a.D != null && this.f5128a.f4942d != null) {
                    this.f5128a.f4942d.setBusinessText(this.f5128a.D);
                } else if (h.i()) {
                    N n3 = this.f5128a;
                    n3.W(n3);
                }
                this.f5128a.q.a(112, new Object());
            }
        }
    }
}
