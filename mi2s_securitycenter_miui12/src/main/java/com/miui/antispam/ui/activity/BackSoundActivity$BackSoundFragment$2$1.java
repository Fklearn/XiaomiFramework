package com.miui.antispam.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;

class BackSoundActivity$BackSoundFragment$2$1 extends ResultReceiver {
    final /* synthetic */ C0220n this$1;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    BackSoundActivity$BackSoundFragment$2$1(C0220n nVar, Handler handler) {
        super(handler);
        this.this$1 = nVar;
    }

    /* access modifiers changed from: protected */
    public void onReceiveResult(int i, Bundle bundle) {
        Log.d("TelephonyDebugTool", "setCallForwardingOption: resultCode=" + i);
        Message message = new Message();
        C0220n nVar = this.this$1;
        message.arg1 = nVar.f2602a;
        message.what = i;
        message.obj = nVar.f2603b;
        nVar.f2604c.i.sendMessage(message);
    }
}
