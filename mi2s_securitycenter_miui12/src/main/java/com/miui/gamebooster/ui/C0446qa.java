package com.miui.gamebooster.ui;

import android.view.View;
import android.widget.TextView;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.model.t;

/* renamed from: com.miui.gamebooster.ui.qa  reason: case insensitive filesystem */
class C0446qa implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ t f5099a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GameVideoActivity f5100b;

    C0446qa(GameVideoActivity gameVideoActivity, t tVar) {
        this.f5100b = gameVideoActivity;
        this.f5099a = tVar;
    }

    public void onClick(View view) {
        C0373d.z(this.f5100b.f4906c, GameVideoActivity.class.getSimpleName());
        this.f5100b.a((TextView) view, this.f5099a);
    }
}
