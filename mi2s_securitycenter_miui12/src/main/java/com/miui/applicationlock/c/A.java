package com.miui.applicationlock.c;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.miui.securitycenter.R;

class A extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C f3270a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    A(C c2, Looper looper) {
        super(looper);
        this.f3270a = c2;
    }

    public void handleMessage(Message message) {
        switch (message.what) {
            case 1001:
                this.f3270a.p.d();
                return;
            case 1002:
                this.f3270a.p.b();
                return;
            case 1003:
                this.f3270a.p.a(this.f3270a.h.getResources().getString(this.f3270a.l));
                return;
            case 1005:
                if (this.f3270a.m) {
                    this.f3270a.p.a(this.f3270a.h.getResources().getString(R.string.face_unlock_check_failed));
                }
                this.f3270a.p.a(this.f3270a.m);
                return;
            case 1006:
                this.f3270a.p.c();
                return;
            case 1007:
                this.f3270a.p.a();
                return;
            default:
                return;
        }
    }
}
