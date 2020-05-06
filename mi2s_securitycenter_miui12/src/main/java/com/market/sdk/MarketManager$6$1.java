package com.market.sdk;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

class MarketManager$6$1 extends ResultReceiver {
    final /* synthetic */ o this$1;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    MarketManager$6$1(o oVar, Handler handler) {
        super(handler);
        this.this$1 = oVar;
    }

    /* access modifiers changed from: protected */
    public void onReceiveResult(int i, Bundle bundle) {
        if (bundle != null) {
            this.this$1.f2239b.set(bundle.getString("categoryName"));
            throw null;
        } else {
            this.this$1.f2239b.set(null);
            throw null;
        }
    }
}
