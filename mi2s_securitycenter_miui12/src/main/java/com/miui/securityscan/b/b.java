package com.miui.securityscan.b;

import com.miui.securityscan.i.w;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Integer f7603a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ c f7604b;

    b(c cVar, Integer num) {
        this.f7604b = cVar;
        this.f7603a = num;
    }

    public void run() {
        this.f7604b.f7606b.L.setScore(this.f7603a.intValue());
        this.f7604b.f7606b.M.setScore(this.f7603a.intValue());
        w.a(this.f7604b.f7607c, this.f7603a.intValue(), this.f7604b.f7608d);
        w.b(this.f7604b.f7607c, this.f7603a.intValue(), this.f7604b.e);
        this.f7604b.f7606b.b(this.f7603a.intValue());
        this.f7604b.f7606b.c(this.f7603a.intValue());
    }
}
