package com.miui.common.customview;

import android.os.Handler;
import android.os.Message;

class b extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AutoPasteListView f3797a;

    b(AutoPasteListView autoPasteListView) {
        this.f3797a = autoPasteListView;
    }

    public void handleMessage(Message message) {
        if (message.what == 104) {
            this.f3797a.a(message.arg1);
        }
    }
}
