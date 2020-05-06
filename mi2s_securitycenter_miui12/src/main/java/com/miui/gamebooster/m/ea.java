package com.miui.gamebooster.m;

import android.app.Activity;
import android.view.View;

class ea implements View.OnSystemUiVisibilityChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Activity f4483a;

    ea(Activity activity) {
        this.f4483a = activity;
    }

    public void onSystemUiVisibilityChange(int i) {
        na.a(this.f4483a);
    }
}
