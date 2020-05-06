package com.miui.gamebooster.globalgame.util;

import android.view.View;
import android.view.ViewTreeObserver;

class f implements ViewTreeObserver.OnPreDrawListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f4422a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f4423b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Runnable f4424c;

    f(boolean z, View view, Runnable runnable) {
        this.f4422a = z;
        this.f4423b = view;
        this.f4424c = runnable;
    }

    public boolean onPreDraw() {
        if (!this.f4422a) {
            this.f4423b.getViewTreeObserver().removeOnPreDrawListener(this);
        }
        this.f4424c.run();
        return false;
    }
}
