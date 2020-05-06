package com.miui.optimizemanage;

import android.app.Activity;
import android.app.FragmentTransaction;

class k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ m f5939a;

    k(m mVar) {
        this.f5939a = mVar;
    }

    public void run() {
        Activity activity = this.f5939a.getActivity();
        if (activity != null && !activity.isFinishing()) {
            FragmentTransaction beginTransaction = activity.getFragmentManager().beginTransaction();
            beginTransaction.remove(this.f5939a);
            beginTransaction.commitAllowingStateLoss();
        }
    }
}
