package com.miui.securityscan;

import android.app.Activity;

class w implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f8041a;

    w(L l) {
        this.f8041a = l;
    }

    public void run() {
        Activity activity = this.f8041a.getActivity();
        if (this.f8041a.a(activity)) {
            this.f8041a.Qa.a(activity);
        }
    }
}
