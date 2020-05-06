package com.miui.luckymoney.upgrade;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.gamebooster.service.NotificationListener;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.service.LuckyMoneyAccessibilityService;
import com.miui.luckymoney.service.LuckyMoneyMonitorService;
import com.miui.luckymoney.utils.SettingsUtil;
import miui.os.Build;

public class LuckyMoneyHelper {
    private static final String TAG = "LuckyMoneyHelper";

    public static void init(Context context) {
        if (!Build.IS_INTERNATIONAL_BUILD) {
            Log.i(TAG, "start Lm service");
            startLuckyMoneyService(context);
        }
    }

    public static void startLuckyMoneyService(Context context) {
        CommonConfig instance = CommonConfig.getInstance(context);
        if (instance.getXiaomiLuckyMoneyEnable()) {
            SettingsUtil.enableNotificationListener(context, NotificationListener.class);
            SettingsUtil.enableAccessibility(context, LuckyMoneyAccessibilityService.class);
            context.startService(new Intent(context, LuckyMoneyMonitorService.class));
        } else {
            SettingsUtil.closeNotificationListener(context, NotificationListener.class);
            SettingsUtil.closeAccessibility(context, LuckyMoneyAccessibilityService.class);
        }
        if (!instance.getLuckySoundWarningEnable()) {
            instance.setLuckySoundWarningEnable(true);
            instance.setLuckySoundWarningLevel(0);
        }
    }

    public static void stopLuckyMoneyService(Context context) {
        SettingsUtil.closeNotificationListener(context, NotificationListener.class);
        SettingsUtil.closeAccessibility(context, LuckyMoneyAccessibilityService.class);
        context.stopService(new Intent(context, LuckyMoneyMonitorService.class));
    }
}
