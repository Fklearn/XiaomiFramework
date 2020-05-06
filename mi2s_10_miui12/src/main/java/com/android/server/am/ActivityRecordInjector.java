package com.android.server.am;

import com.android.server.appop.AppOpsService;

public class ActivityRecordInjector {
    public static boolean canShowWhenLocked(AppOpsService appOpsService, int uid, String packageName) {
        return true;
    }
}
