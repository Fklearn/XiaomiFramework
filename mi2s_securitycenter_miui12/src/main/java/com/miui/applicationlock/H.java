package com.miui.applicationlock;

class H implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f3177a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ I f3178b;

    H(I i, int i2) {
        this.f3178b = i;
        this.f3177a = i2;
    }

    public void run() {
        this.f3178b.f3180a.p.removeAllViews();
        this.f3178b.f3180a.o();
        int i = this.f3177a;
        if (i != 0 && i == 1) {
            ChooseAccessControl.showKeyboard(this.f3178b.f3180a.k.f());
        } else {
            ChooseAccessControl.hideKeyboard(this.f3178b.f3180a.k);
        }
    }
}
