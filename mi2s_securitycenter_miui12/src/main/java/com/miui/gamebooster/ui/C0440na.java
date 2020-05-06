package com.miui.gamebooster.ui;

import com.miui.gamebooster.m.C0378i;
import com.miui.gamebooster.model.t;
import java.util.List;

/* renamed from: com.miui.gamebooster.ui.na  reason: case insensitive filesystem */
class C0440na implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameVideoActivity f5090a;

    C0440na(GameVideoActivity gameVideoActivity) {
        this.f5090a = gameVideoActivity;
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.gamebooster.ui.GameVideoActivity] */
    public void run() {
        ? r0 = this.f5090a;
        List<t> c2 = C0378i.c(r0, r0.f4907d);
        this.f5090a.e.clear();
        if (c2 != null && c2.size() > 0) {
            this.f5090a.e.addAll(c2);
        }
        this.f5090a.runOnUiThread(new C0438ma(this));
    }
}
