package com.miui.powercenter.powerui;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.io.File;

class a extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f7142a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    a(h hVar, Looper looper) {
        super(looper);
        this.f7142a = hVar;
    }

    public void handleMessage(Message message) {
        h hVar;
        File file;
        super.handleMessage(message);
        int i = message.what;
        if (i != 1) {
            if (i == 2) {
                hVar = this.f7142a;
                file = h.f7149a;
            } else if (i == 3) {
                hVar = this.f7142a;
                file = hVar.l == 4 ? h.f7150b : h.f7151c;
            } else {
                return;
            }
            hVar.a(Uri.fromFile(file));
            return;
        }
        this.f7142a.q();
    }
}
