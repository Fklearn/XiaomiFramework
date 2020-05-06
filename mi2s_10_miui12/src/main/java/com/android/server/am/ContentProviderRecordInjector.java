package com.android.server.am;

import android.content.pm.ApplicationInfo;

public class ContentProviderRecordInjector {
    public static boolean isReleaseNeeded(ApplicationInfo ai) {
        if (ai.uid != 1000 || !"com.android.settings".equals(ai.packageName)) {
            return false;
        }
        return true;
    }
}
