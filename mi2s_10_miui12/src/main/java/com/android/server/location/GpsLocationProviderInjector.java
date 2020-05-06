package com.android.server.location;

import android.content.Intent;
import android.os.WorkSource;

class GpsLocationProviderInjector {
    GpsLocationProviderInjector() {
    }

    static void appendUidExtra(WorkSource clientSource, boolean navigating, Intent intent) {
        if (navigating && clientSource.size() > 0) {
            intent.putExtra("android.intent.extra.UID", clientSource.get(0));
            intent.putExtra("android.intent.extra.PACKAGES", clientSource.getName(0));
        }
    }
}
