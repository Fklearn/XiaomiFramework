package com.miui.gamebooster.ui;

import android.view.View;

class Ka implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SelectGameActivity f4921a;

    Ka(SelectGameActivity selectGameActivity) {
        this.f4921a = selectGameActivity;
    }

    public void onClick(View view) {
        if (view == this.f4921a.f4986d) {
            SelectGameActivity selectGameActivity = this.f4921a;
            selectGameActivity.a(selectGameActivity.v);
        }
    }
}
