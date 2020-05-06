package com.miui.networkassistant.utils;

import android.content.Context;
import android.content.Intent;
import com.miui.networkassistant.provider.ProviderConstant;

public class HybirdServiceUtil {
    public static final String ACTION_HYBIRD_PERMISSIONS = "com.miui.hybrid.action.PERMISSION_PREFERENCES";
    public static final String ACTION_MIUI_HYBIRD = "com.miui.hybrid.DataUsage";
    public static final String HYBIRD_PACKAGE_NAME = "com.miui.hybrid";

    public static String getHybirdActivityLabel(Context context) {
        if (isHybirdIntentExist(context)) {
            return PackageUtil.getActivityLabel(context, getHybirdIntent());
        }
        return null;
    }

    private static Intent getHybirdIntent() {
        Intent intent = new Intent(ACTION_MIUI_HYBIRD);
        intent.setPackage(HYBIRD_PACKAGE_NAME);
        intent.setFlags(268435456);
        return intent;
    }

    public static boolean isHybirdIntentExist(Context context) {
        return PackageUtil.isIntentExist(context, getHybirdIntent());
    }

    public static boolean isHybirdService(CharSequence charSequence) {
        return HYBIRD_PACKAGE_NAME.equals(charSequence);
    }

    public static void startHybirdTrafficSortActivity(Context context, int i, long j, int i2, String str) {
        Intent hybirdIntent = getHybirdIntent();
        hybirdIntent.putExtra("date_type", i);
        hybirdIntent.putExtra("data_usage", j);
        hybirdIntent.putExtra("network_type", i2);
        hybirdIntent.putExtra(ProviderConstant.TrafficDistributionColumns.IMSI, str);
        context.startActivity(hybirdIntent);
    }
}
