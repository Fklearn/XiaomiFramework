package com.miui.gamebooster.ui;

import android.view.View;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.model.t;
import com.miui.securityscan.i.i;

/* renamed from: com.miui.gamebooster.ui.pa  reason: case insensitive filesystem */
class C0444pa implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ t f5096a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GameVideoActivity f5097b;

    C0444pa(GameVideoActivity gameVideoActivity, t tVar) {
        this.f5097b = gameVideoActivity;
        this.f5096a = tVar;
    }

    /* JADX WARNING: type inference failed for: r4v3, types: [android.content.Context, com.miui.gamebooster.ui.GameVideoActivity] */
    public void onClick(View view) {
        C0373d.t(this.f5097b.f4906c, GameVideoActivity.class.getSimpleName());
        ? r4 = this.f5097b;
        i.a(r4, r4.a(this.f5096a), "video/*", true);
    }
}
