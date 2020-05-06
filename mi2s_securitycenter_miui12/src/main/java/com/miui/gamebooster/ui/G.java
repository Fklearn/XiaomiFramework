package com.miui.gamebooster.ui;

import android.app.Activity;
import b.b.c.c.a.b;
import java.util.List;

class G extends b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f4882a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ H f4883b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    G(H h, Activity activity, List list) {
        super(activity);
        this.f4883b = h;
        this.f4882a = list;
    }

    /* access modifiers changed from: protected */
    public void runOnUiThread() {
        this.f4883b.b(this.f4882a);
    }
}
