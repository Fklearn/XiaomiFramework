package com.miui.gamebooster.model;

import android.view.View;
import com.miui.gamebooster.a.I;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.model.C;
import com.miui.securityscan.i.i;

class w implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f4603a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ t f4604b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ I.a f4605c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ int f4606d;
    final /* synthetic */ C.a e;

    w(C.a aVar, boolean z, t tVar, I.a aVar2, int i) {
        this.e = aVar;
        this.f4603a = z;
        this.f4604b = tVar;
        this.f4605c = aVar2;
        this.f4606d = i;
    }

    public void onClick(View view) {
        if (this.f4603a) {
            t tVar = this.f4604b;
            tVar.a(true ^ tVar.i());
            I.a aVar = this.f4605c;
            if (aVar != null) {
                aVar.a(this.f4606d);
                return;
            }
            return;
        }
        C0373d.t(this.f4604b.d(), "WonderfulMomentActivity");
        i.a(this.e.f4539a, this.e.a(this.f4604b), "video/*", true);
    }
}
