package com.android.server.wm;

import android.util.Slog;
import com.android.server.appop.AppOpsService;

public class ActivityRecordInjector {
    private static final String TAG = "ActivityRecordInjector";

    public static boolean canShowWhenLocked(AppOpsService appOpsService, int uid, String packageName) {
        if (appOpsService.checkOperation(10020, uid, packageName) == 0) {
            return true;
        }
        Slog.i(TAG, "MIUILOG- Show when locked PermissionDenied pkg : " + packageName + " uid : " + uid);
        return false;
    }
}
