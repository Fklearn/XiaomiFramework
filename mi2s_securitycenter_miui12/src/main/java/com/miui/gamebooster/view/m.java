package com.miui.gamebooster.view;

import android.view.View;

class m implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f5309a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ float[] f5310b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ View f5311c;

    m(int i, float[] fArr, View view) {
        this.f5309a = i;
        this.f5310b = fArr;
        this.f5311c = view;
    }

    public void run() {
        this.f5311c.getLayoutParams().width = (int) (((float) this.f5309a) / this.f5310b[1]);
        this.f5311c.requestLayout();
    }
}
