package com.market.pm.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class MarketInstallObserver extends ResultReceiver implements a {
    private static final String KEY_PACKAGE_NAME = "packageName";
    private static final String KEY_RETURN_CODE = "returnCode";
    private static final int RESULT_CODE_INSTALLED = 0;
    private static final int RESULT_CODE_REFUSE = 1;
    private static final int RESULT_CODE_SERVICE_DEAD = 2;
    private final c mListener;

    public MarketInstallObserver(c cVar) {
        super((Handler) null);
        this.mListener = cVar;
    }

    private static int getCode(Bundle bundle) {
        return bundle.getInt(KEY_RETURN_CODE);
    }

    private static String getPackageName(Bundle bundle) {
        return bundle.getString(KEY_PACKAGE_NAME);
    }

    /* access modifiers changed from: private */
    public static Bundle obtainBundle(String str, int i) {
        Bundle bundle = new Bundle(2);
        bundle.putString(KEY_PACKAGE_NAME, str);
        bundle.putInt(KEY_RETURN_CODE, i);
        return bundle;
    }

    /* access modifiers changed from: protected */
    public void onReceiveResult(int i, Bundle bundle) {
        super.onReceiveResult(i, bundle);
        c cVar = this.mListener;
        if (cVar == null) {
            return;
        }
        if (i == 0) {
            cVar.packageInstalled(getPackageName(bundle), getCode(bundle));
        } else if (i == 1) {
            cVar.a(getPackageName(bundle), getCode(bundle));
        } else if (i == 2) {
            cVar.a();
        }
    }
}
