package com.miui.networkassistant.xman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class XmanShareReceiver extends BroadcastReceiver {
    private static final String ACTION_SHARED = "com.miui.securitycenter.intent.action.SHARED";
    private static final String ACTION_XMAN_SETTINGS = "com.miui.securitycenter.intent.action.XMAN.SECURITY_SHARE_SETTINGS_SHOW";

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            char c2 = 65535;
            int hashCode = action.hashCode();
            if (hashCode != -694269512) {
                if (hashCode == 1527677971 && action.equals(ACTION_SHARED)) {
                    c2 = 0;
                }
            } else if (action.equals(ACTION_XMAN_SETTINGS)) {
                c2 = 1;
            }
            if (c2 == 0) {
                XmanHelper.trackXmanSharedEvent(intent.getStringExtra("android.intent.extra.PACKAGE_NAME"), intent.getIntExtra("type", 0));
            } else if (c2 == 1) {
                XmanHelper.trackXmanSettingsEvent(intent.getStringExtra("android.intent.extra.PACKAGE_NAME"));
            }
        }
    }
}
