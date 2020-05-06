package com.miui.gamebooster.globalgame.global;

import android.view.ViewGroup;
import com.miui.gamebooster.globalgame.util.Utils;

class a implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f4393a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ CoverRatioFixedVH f4394b;

    a(CoverRatioFixedVH coverRatioFixedVH, int i) {
        this.f4394b = coverRatioFixedVH;
        this.f4393a = i;
    }

    public void run() {
        if (!this.f4394b.coverHeightAdjusted) {
            boolean unused = this.f4394b.coverHeightAdjusted = true;
            ViewGroup.LayoutParams layoutParams = this.f4394b.cover.getLayoutParams();
            layoutParams.height = (int) (((float) this.f4393a) * this.f4394b.parseRatio());
            if (layoutParams.height != 0) {
                Utils.a(this.f4394b.keyForStore(), (float) layoutParams.height);
            }
            this.f4394b.cover.requestLayout();
        }
    }
}
