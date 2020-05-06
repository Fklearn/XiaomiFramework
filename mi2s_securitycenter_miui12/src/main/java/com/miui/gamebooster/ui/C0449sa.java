package com.miui.gamebooster.ui;

import android.content.Context;
import android.widget.TextView;
import com.miui.gamebooster.m.C0378i;
import com.miui.gamebooster.m.C0382m;
import com.miui.gamebooster.m.I;
import com.miui.gamebooster.model.t;

/* renamed from: com.miui.gamebooster.ui.sa  reason: case insensitive filesystem */
class C0449sa implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ t f5104a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ TextView f5105b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ GameVideoActivity f5106c;

    C0449sa(GameVideoActivity gameVideoActivity, t tVar, TextView textView) {
        this.f5106c = gameVideoActivity;
        this.f5104a = tVar;
        this.f5105b = textView;
    }

    /* JADX WARNING: type inference failed for: r0v3, types: [android.content.Context, com.miui.gamebooster.ui.GameVideoActivity] */
    public void run() {
        String b2 = C0382m.b(this.f5104a.c());
        I.a(b2);
        this.f5104a.f(b2);
        if (C0378i.b((Context) this.f5106c, this.f5104a)) {
            this.f5106c.runOnUiThread(new C0447ra(this));
        }
    }
}
