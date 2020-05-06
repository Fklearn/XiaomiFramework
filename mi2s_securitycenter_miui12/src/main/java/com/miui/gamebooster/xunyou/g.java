package com.miui.gamebooster.xunyou;

import com.miui.gamebooster.m.C0382m;
import java.util.ArrayList;
import java.util.List;

class g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f5410a;

    g(h hVar) {
        this.f5410a = hVar;
    }

    public void run() {
        h hVar = this.f5410a;
        List unused = hVar.f5412b = C0382m.a("xunyou_support", hVar.f5411a);
        ArrayList<String> c2 = C0382m.c("gamebooster", "xunyousupportlist", this.f5410a.f5411a);
        if (c2 != null && c2.size() > 5) {
            List unused2 = this.f5410a.f5412b = c2;
        }
        h hVar2 = this.f5410a;
        List unused3 = hVar2.f5412b = hVar2.f5412b == null ? new ArrayList() : this.f5410a.f5412b;
        this.f5410a.f5413c.notifyObservers(this.f5410a.f5412b);
    }
}
