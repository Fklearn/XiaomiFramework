package com.miui.gamebooster.ui;

import android.app.Activity;
import b.b.c.c.a.b;
import java.util.ArrayList;

class E extends b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ArrayList f4872a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ F f4873b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    E(F f, Activity activity, ArrayList arrayList) {
        super(activity);
        this.f4873b = f;
        this.f4872a = arrayList;
    }

    /* access modifiers changed from: protected */
    public void runOnUiThread() {
        this.f4873b.f4875a.y.clear();
        this.f4873b.f4875a.y.addAll(this.f4872a);
        this.f4873b.f4875a.A();
        N.b(this.f4873b.f4875a.mAppContext, this.f4873b.f4875a.y.size());
    }
}
