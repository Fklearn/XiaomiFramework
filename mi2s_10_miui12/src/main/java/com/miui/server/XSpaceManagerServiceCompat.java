package com.miui.server;

import android.os.BaseBundle;

class XSpaceManagerServiceCompat {
    XSpaceManagerServiceCompat() {
    }

    static void setBundleDefusable(boolean defusable) {
        BaseBundle.setShouldDefuse(defusable);
    }
}
