package com.miui.monthreport;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

class i extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ l f5646a;

    i(l lVar) {
        this.f5646a = lVar;
    }

    public void handleMessage(Message message) {
        String a2 = l.f5649a;
        Log.i(a2, "Handle task message : " + message.what);
        switch (message.what) {
            case 101:
                Object obj = message.obj;
                if (obj != null && (obj instanceof String)) {
                    this.f5646a.a((String) obj, (h) null);
                    return;
                }
                return;
            case 102:
                Object obj2 = message.obj;
                if (obj2 != null && (obj2 instanceof h)) {
                    h hVar = (h) obj2;
                    this.f5646a.a(hVar.c(), hVar);
                    return;
                }
                return;
            case 103:
                this.f5646a.e();
                return;
            default:
                return;
        }
    }
}
