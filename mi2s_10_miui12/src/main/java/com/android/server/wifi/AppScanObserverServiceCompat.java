package com.android.server.wifi;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;

public class AppScanObserverServiceCompat {
    private static final String TAG = "AppScanObserverService";

    protected static void startServiceAsUser(Context context, Intent intent, UserHandle userHandle) {
        try {
            context.startForegroundServiceAsUser(intent, userHandle);
        } catch (Exception e) {
            Log.e(TAG, "Exception while start service: ", e);
        }
    }
}
