package com.miui.gamebooster.ui;

import android.view.View;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.model.t;
import com.miui.securityscan.i.i;

/* renamed from: com.miui.gamebooster.ui.oa  reason: case insensitive filesystem */
class C0442oa implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ t f5093a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GameVideoActivity f5094b;

    C0442oa(GameVideoActivity gameVideoActivity, t tVar) {
        this.f5094b = gameVideoActivity;
        this.f5093a = tVar;
    }

    /* JADX WARNING: type inference failed for: r4v3, types: [android.content.Context, com.miui.gamebooster.ui.GameVideoActivity] */
    public void onClick(View view) {
        C0373d.t(this.f5094b.f4906c, GameVideoActivity.class.getSimpleName());
        ? r4 = this.f5094b;
        i.a(r4, r4.a(this.f5093a), "video/*", true);
    }
}
