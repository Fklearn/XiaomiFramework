package com.market.sdk.homeguide;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

class AppstoreUserGuideService$1$1 extends ResultReceiver {
    final /* synthetic */ a this$1;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    AppstoreUserGuideService$1$1(a aVar, Handler handler) {
        super(handler);
        this.this$1 = aVar;
    }

    /* access modifiers changed from: protected */
    public void onReceiveResult(int i, Bundle bundle) {
        this.this$1.a(i != 0);
        this.this$1.a();
    }
}
