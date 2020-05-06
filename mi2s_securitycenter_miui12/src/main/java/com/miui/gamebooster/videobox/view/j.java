package com.miui.gamebooster.videobox.view;

class j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f5248a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ SlidingButton f5249b;

    j(SlidingButton slidingButton, boolean z) {
        this.f5249b = slidingButton;
        this.f5248a = z;
    }

    public void run() {
        if (this.f5249b.r != null) {
            this.f5249b.r.onCheckedChanged(this.f5249b, this.f5248a);
        }
    }
}
