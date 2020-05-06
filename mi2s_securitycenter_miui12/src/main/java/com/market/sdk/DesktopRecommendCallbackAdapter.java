package com.market.sdk;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class DesktopRecommendCallbackAdapter extends ResultReceiver {
    private static final int CODE_LOAD_FAILED = 1;
    private static final int CODE_LOAD_SUCCESS = 0;
    private static final String KEY_JSON = "json";
    private final e mAdaptee;

    public DesktopRecommendCallbackAdapter(e eVar) {
        super((Handler) null);
        this.mAdaptee = eVar;
    }

    /* access modifiers changed from: protected */
    public void onReceiveResult(int i, Bundle bundle) {
        super.onReceiveResult(i, bundle);
        if (i == 0) {
            this.mAdaptee.a(DesktopRecommendInfo.restore(bundle.getString(KEY_JSON)));
        } else if (i == 1) {
            this.mAdaptee.h();
        }
    }
}
