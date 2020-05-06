package com.miui.gamebooster.ui;

class B implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f4853a;

    B(N n) {
        this.f4853a = n;
    }

    public void run() {
        N n = this.f4853a;
        n.postOnUiThread(new A(this, n.mActivity));
    }
}
