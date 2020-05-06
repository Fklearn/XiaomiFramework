package com.miui.antivirus.result;

import com.miui.antivirus.activity.MainActivity;

/* renamed from: com.miui.antivirus.result.e  reason: case insensitive filesystem */
class C0242e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2831a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0243f f2832b;

    C0242e(C0243f fVar, MainActivity mainActivity) {
        this.f2832b = fVar;
        this.f2831a = mainActivity;
    }

    public void run() {
        this.f2831a.a((C0238a) this.f2832b);
        if (this.f2832b.l == 10001 || this.f2832b.l == 30001 || this.f2832b.l == 30002) {
            C0251n.b(this.f2832b.c(), this.f2832b.J);
        }
    }
}
