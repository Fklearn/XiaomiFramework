package com.miui.gamebooster.customview;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.miui.securitycenter.R;

/* renamed from: com.miui.gamebooster.customview.f  reason: case insensitive filesystem */
class C0337f implements Animation.AnimationListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AuditionView f4192a;

    C0337f(AuditionView auditionView) {
        this.f4192a = auditionView;
    }

    public void onAnimationEnd(Animation animation) {
        this.f4192a.f4105c.setVisibility(8);
        this.f4192a.f4104b.setVisibility(0);
        this.f4192a.f4104b.startAnimation(AnimationUtils.loadAnimation(this.f4192a.f, R.anim.gb_record_view_enter));
    }

    public void onAnimationRepeat(Animation animation) {
    }

    public void onAnimationStart(Animation animation) {
    }
}
