package com.miui.activityutil;

import android.util.Log;
import java.io.File;

final class ab implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f2258a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ aa f2259b;

    ab(aa aaVar, int i) {
        this.f2259b = aaVar;
        this.f2258a = i;
    }

    public final void run() {
        w wVar = new w();
        h hVar = new h(this.f2259b.r);
        File a2 = hVar.a(this.f2258a);
        if (a2 == null) {
            Log.e(aa.f2255b, "getInfo error");
            this.f2259b.t = false;
            return;
        }
        wVar.a(this.f2259b.r, aa.f2256c, a2, (x) new ac(this, hVar, a2));
    }
}
