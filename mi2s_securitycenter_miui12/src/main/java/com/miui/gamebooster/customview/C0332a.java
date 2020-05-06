package com.miui.gamebooster.customview;

import com.miui.gamebooster.customview.AuditionView;

/* renamed from: com.miui.gamebooster.customview.a  reason: case insensitive filesystem */
class C0332a implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AuditionView f4168a;

    C0332a(AuditionView auditionView) {
        this.f4168a = auditionView;
    }

    public void run() {
        int i;
        AuditionView.c cVar;
        AuditionView auditionView = this.f4168a;
        int unused = auditionView.w = auditionView.w + 1000;
        if (this.f4168a.w >= 10000) {
            cVar = this.f4168a.B;
            i = 5;
        } else {
            cVar = this.f4168a.B;
            i = 3;
        }
        cVar.sendEmptyMessage(i);
    }
}
