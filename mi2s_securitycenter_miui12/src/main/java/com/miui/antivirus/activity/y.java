package com.miui.antivirus.activity;

import android.view.View;
import miui.widget.SlidingButton;

class y implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SlidingButton f2741a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ MainActivity f2742b;

    y(MainActivity mainActivity, SlidingButton slidingButton) {
        this.f2742b = mainActivity;
        this.f2741a = slidingButton;
    }

    public void onClick(View view) {
        SlidingButton slidingButton = this.f2741a;
        slidingButton.setChecked(!slidingButton.isChecked());
    }
}
