package com.miui.gamebooster.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import b.b.c.j.x;
import com.miui.gamebooster.m.C0391w;
import com.miui.gamebooster.model.C0398d;
import java.util.List;

class D implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f4869a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ N f4870b;

    D(N n, List list) {
        this.f4870b = n;
        this.f4869a = list;
    }

    public void run() {
        if (this.f4869a.size() != 0) {
            int i = 0;
            while (true) {
                int i2 = i;
                for (C0398d dVar : this.f4869a) {
                    if (dVar != null && dVar.b() != null) {
                        ApplicationInfo b2 = dVar.b();
                        String charSequence = x.j(this.f4870b.mAppContext, b2.packageName).toString();
                        Context s = this.f4870b.mAppContext;
                        String str = b2.packageName;
                        int i3 = b2.uid;
                        i = i2 + 1;
                        C0391w.a(s, charSequence, str, i3, 0, i2);
                    }
                }
                return;
            }
        }
    }
}
