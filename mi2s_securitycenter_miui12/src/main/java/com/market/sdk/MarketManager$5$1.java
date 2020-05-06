package com.market.sdk;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

class MarketManager$5$1 extends ResultReceiver {
    final /* synthetic */ n this$1;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    MarketManager$5$1(n nVar, Handler handler) {
        super(handler);
        this.this$1 = nVar;
    }

    /* access modifiers changed from: protected */
    public void onReceiveResult(int i, Bundle bundle) {
        this.this$1.f2238b.set(Integer.valueOf(i));
        throw null;
    }
}
