package com.miui.securityscan.i;

import android.view.View;
import android.view.animation.Animation;
import b.b.c.a.a;
import com.miui.common.customview.OverScrollLayout;

class v extends a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f7750a;

    v(View view) {
        this.f7750a = view;
    }

    public void onAnimationEnd(Animation animation) {
        View view = this.f7750a;
        if (view != null) {
            if (view instanceof OverScrollLayout) {
                ((OverScrollLayout) view).setIntercept(false);
            }
            this.f7750a.setVisibility(8);
        }
    }
}
