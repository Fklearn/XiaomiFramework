package com.miui.applicationlock;

import android.graphics.drawable.Drawable;

class D implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Drawable f3152a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ E f3153b;

    D(E e, Drawable drawable) {
        this.f3153b = e;
        this.f3152a = drawable;
    }

    public void run() {
        boolean unused = this.f3153b.f3162d = true;
        this.f3152a.clearColorFilter();
    }
}
