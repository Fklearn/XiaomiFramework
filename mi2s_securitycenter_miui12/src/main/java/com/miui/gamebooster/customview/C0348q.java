package com.miui.gamebooster.customview;

import android.widget.TextView;

/* renamed from: com.miui.gamebooster.customview.q  reason: case insensitive filesystem */
class C0348q implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoxView f4222a;

    C0348q(GameBoxView gameBoxView) {
        this.f4222a = gameBoxView;
    }

    public void run() {
        TextView d2 = this.f4222a.h;
        d2.setText(this.f4222a.A + this.f4222a.v + " %");
        TextView g = this.f4222a.i;
        g.setText(this.f4222a.B + this.f4222a.w + " %");
        TextView j = this.f4222a.j;
        j.setText(this.f4222a.x + this.f4222a.C);
        this.f4222a.k.setText(this.f4222a.y);
        this.f4222a.l.setImageDrawable(this.f4222a.e.getResources().getDrawable(this.f4222a.aa[this.f4222a.E / 5]));
    }
}
