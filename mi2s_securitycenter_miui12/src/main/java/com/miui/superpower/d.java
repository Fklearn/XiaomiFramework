package com.miui.superpower;

import android.view.View;

class d implements View.OnSystemUiVisibilityChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f8100a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ SuperPowerProgressActivity f8101b;

    d(SuperPowerProgressActivity superPowerProgressActivity, View view) {
        this.f8101b = superPowerProgressActivity;
        this.f8100a = view;
    }

    public void onSystemUiVisibilityChange(int i) {
        if ((i & 2) == 0) {
            this.f8100a.setSystemUiVisibility(4866);
        }
    }
}
