package com.miui.optimizemanage.c;

import com.miui.optimizemanage.OptimizemanageMainActivity;
import com.miui.optimizemanage.a.a;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ OptimizemanageMainActivity f5884a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ c f5885b;

    b(c cVar, OptimizemanageMainActivity optimizemanageMainActivity) {
        this.f5885b = cVar;
        this.f5884a = optimizemanageMainActivity;
    }

    public void run() {
        this.f5884a.a(this.f5885b);
        a.d("activity_dislike");
    }
}
