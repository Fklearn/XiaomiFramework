package com.miui.networkassistant.utils;

import android.content.Context;
import com.miui.securitycenter.R;

public class LoadConfigUtil {
    public static boolean isDataUsageLimitAlertEnabled(Context context) {
        return context.getResources().getBoolean(R.bool.overlay_config_datausage_limit_alert_enabled);
    }

    public static boolean isDataUsagePurchaseEnabled(Context context) {
        return context.getResources().getBoolean(R.bool.overlay_config_datausage_purchase_enabled);
    }
}
