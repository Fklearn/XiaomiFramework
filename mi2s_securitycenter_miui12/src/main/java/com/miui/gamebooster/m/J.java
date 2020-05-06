package com.miui.gamebooster.m;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.miui.gamebooster.m.a.a;

class J extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f4450a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    J(N n, Looper looper) {
        super(looper);
        this.f4450a = n;
    }

    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 129) {
            boolean unused = this.f4450a.f4457d = true;
            Log.i("MiLinkUtils", "handleMessage: toolBoxType=" + this.f4450a.m);
            a.b(this.f4450a.m);
            N n = this.f4450a;
            int unused2 = n.e = C0384o.a(n.f.getContentResolver(), this.f4450a.j, 0, -2);
            C0384o.b(this.f4450a.f.getContentResolver(), this.f4450a.j, 1, -2);
            com.miui.gamebooster.c.a.S(true);
            if (this.f4450a.h != null) {
                this.f4450a.h.b();
            }
            Log.i("MiLinkUtils", "onConnectSuccess mUserPrivateOn:" + this.f4450a.e);
        } else if (i == 130) {
            Log.i("MiLinkUtils", "onConnectFail ");
            a.b(0);
            if (this.f4450a.f4457d) {
                this.f4450a.i();
            } else {
                this.f4450a.a();
            }
        }
    }
}
