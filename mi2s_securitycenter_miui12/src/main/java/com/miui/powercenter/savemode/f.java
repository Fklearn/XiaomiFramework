package com.miui.powercenter.savemode;

import android.content.Context;
import com.miui.powercenter.savemode.e;
import com.miui.powercenter.utils.o;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f7285a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f7286b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ e.b f7287c;

    f(e.b bVar, e eVar, boolean z) {
        this.f7287c = bVar;
        this.f7285a = eVar;
        this.f7286b = z;
    }

    public void run() {
        o.a((Context) this.f7285a.getActivity(), this.f7286b);
    }
}
