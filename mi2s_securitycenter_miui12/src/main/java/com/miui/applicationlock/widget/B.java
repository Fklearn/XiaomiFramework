package com.miui.applicationlock.widget;

import android.app.Activity;

class B implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Activity f3394a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PercentLayout f3395b;

    B(PercentLayout percentLayout, Activity activity) {
        this.f3395b = percentLayout;
        this.f3394a = activity;
    }

    public void run() {
        int unused = this.f3395b.f3423a = this.f3394a.getWindow().findViewById(16908290).getMeasuredHeight();
        this.f3395b.requestLayout();
    }
}
