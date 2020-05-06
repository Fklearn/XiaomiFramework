package com.miui.powercenter.bootshutdown;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

class o extends Handler {
    o(Looper looper) {
        super(looper);
    }

    public void handleMessage(Message message) {
        int i;
        super.handleMessage(message);
        if (message.what == 123 && (i = message.arg1) > 0) {
            p.b((Context) message.obj, 1, i);
            p.b((Context) message.obj, i);
        }
    }
}
