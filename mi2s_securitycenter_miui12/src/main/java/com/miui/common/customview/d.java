package com.miui.common.customview;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class d extends Scroller {

    /* renamed from: a  reason: collision with root package name */
    private double f3798a = 1.0d;

    /* renamed from: b  reason: collision with root package name */
    private int f3799b = 1000;

    public d(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public void a(double d2) {
        this.f3798a = d2;
    }

    public void a(int i) {
        this.f3799b = i;
    }

    public void startScroll(int i, int i2, int i3, int i4, int i5) {
        super.startScroll(i, i2, i3, i4, (int) (((double) this.f3799b) * this.f3798a));
    }
}
