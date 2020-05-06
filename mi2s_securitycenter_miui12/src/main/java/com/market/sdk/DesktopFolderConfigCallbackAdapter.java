package com.market.sdk;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class DesktopFolderConfigCallbackAdapter extends ResultReceiver {
    private static final String KEY_DATA = "key_data";
    private static final int RESULT_CODE_FAILED = 2;
    private static final int RESULT_CODE_SUCCESS = 1;
    private final m mAdaptee;

    public DesktopFolderConfigCallbackAdapter(m mVar) {
        super((Handler) null);
        this.mAdaptee = mVar;
    }

    /* access modifiers changed from: protected */
    public void onReceiveResult(int i, Bundle bundle) {
        if (i == 1) {
            this.mAdaptee.a(bundle.getString(KEY_DATA));
        } else if (i == 2) {
            this.mAdaptee.b(bundle.getString(KEY_DATA));
        }
    }
}
