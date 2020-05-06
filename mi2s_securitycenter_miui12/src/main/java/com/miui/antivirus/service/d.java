package com.miui.antivirus.service;

import android.util.Log;
import b.b.b.o;

class d implements o.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GuardService f2892a;

    d(GuardService guardService) {
        this.f2892a = guardService;
    }

    public void a(int i) {
        if (!this.f2892a.n.contains(Integer.valueOf(i))) {
            this.f2892a.n.add(Integer.valueOf(i));
        }
        if (i > this.f2892a.m) {
            int unused = this.f2892a.m = i;
        }
    }

    public void b() {
        Log.i("GuardService", "onStartScan");
        boolean unused = this.f2892a.j = true;
        boolean unused2 = this.f2892a.l = false;
        int unused3 = this.f2892a.m = 0;
        int unused4 = this.f2892a.o = 0;
        this.f2892a.n.clear();
    }

    public void c() {
        boolean unused = this.f2892a.l = true;
    }

    public void d() {
        if (this.f2892a.j) {
            this.f2892a.t.sendEmptyMessage(2);
        }
    }

    public void e() {
        if (this.f2892a.j) {
            GuardService.e(this.f2892a);
            if (this.f2892a.o >= 3) {
                this.f2892a.t.sendEmptyMessage(2);
            }
        }
    }
}
