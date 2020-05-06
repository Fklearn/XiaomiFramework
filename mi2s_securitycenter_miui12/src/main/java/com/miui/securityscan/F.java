package com.miui.securityscan;

import android.os.Build;
import android.view.ViewTreeObserver;

class F implements ViewTreeObserver.OnGlobalLayoutListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7548a;

    F(L l) {
        this.f7548a = l;
    }

    public void onGlobalLayout() {
        L l = this.f7548a;
        int unused = l.qa = l.y.getHeight();
        if (Build.VERSION.SDK_INT > 16) {
            this.f7548a.y.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            this.f7548a.y.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }
}
