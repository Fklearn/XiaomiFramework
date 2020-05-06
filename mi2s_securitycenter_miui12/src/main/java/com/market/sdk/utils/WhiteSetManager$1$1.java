package com.market.sdk.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

class WhiteSetManager$1$1 extends ResultReceiver {
    final /* synthetic */ f this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    WhiteSetManager$1$1(f fVar, Handler handler) {
        super(handler);
        this.this$0 = fVar;
    }

    public void onReceiveResult(int i, Bundle bundle) {
        this.this$0.f2249b.set(bundle.getString("whiteSet"));
        throw null;
    }
}
