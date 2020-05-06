package com.android.server;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import java.util.List;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.mqsas.sdk.event.FeatureEvent;

public class FeatureInjector {
    public static final String BRIGHTNESS_FEATURES = "updateBrightness";
    public static final String BRIGHTNESS_FLICK_FEATURES = "flickBrightness";

    public static void onBrightnessFeature(String processName, String packageName, String message, String details, String type) {
        MQSEventManagerDelegate.getInstance().reportFeatureEvent(new FeatureEvent(processName, packageName, message, details, type));
    }

    public static String getForegroundAppPackageName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
                if (processInfos.size() == 0) {
                    return "";
                }
                for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                    if (processInfo.importance == 100) {
                        return processInfo.processName;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
