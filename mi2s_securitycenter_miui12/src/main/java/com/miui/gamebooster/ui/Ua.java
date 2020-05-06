package com.miui.gamebooster.ui;

import android.util.Log;

class Ua implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WelcomActivity f5012a;

    Ua(WelcomActivity welcomActivity) {
        this.f5012a = welcomActivity;
    }

    public void run() {
        if (!this.f5012a.isFinishing()) {
            Log.i("WelcomActivity", "onActivityResult:not finish");
            this.f5012a.finish();
        }
    }
}
