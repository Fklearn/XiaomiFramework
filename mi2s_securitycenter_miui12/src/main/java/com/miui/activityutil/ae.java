package com.miui.activityutil;

import android.text.TextUtils;

final class ae implements x {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ad f2264a;

    ae(ad adVar) {
        this.f2264a = adVar;
    }

    public final void a(int i) {
        this.f2264a.f2263a.t = false;
    }

    public final void a(String str) {
        if (TextUtils.isEmpty(str)) {
            this.f2264a.f2263a.t = false;
            return;
        }
        int b2 = this.f2264a.f2263a.a(str.getBytes());
        if (b2 != -1) {
            h.a(aj.a(this.f2264a.f2263a.r, aa.e), str.getBytes());
        }
        if (b2 == 0) {
            int unused = this.f2264a.f2263a.s = 3;
            aj.a((Runnable) new ab(this.f2264a.f2263a, 6));
            return;
        }
        if (b2 == 1) {
            int unused2 = this.f2264a.f2263a.s = 4;
        }
        this.f2264a.f2263a.t = false;
    }
}
