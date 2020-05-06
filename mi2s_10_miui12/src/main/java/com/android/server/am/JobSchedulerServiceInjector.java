package com.android.server.am;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.util.Slog;

public class JobSchedulerServiceInjector {
    private static final int BATTERY_TEMPERATURE_THRESHOLD = 400;
    private static final String TAG = "JobSchedulerServiceInjector";
    private static final IntentFilter filter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
    private static boolean isDelayState = false;
    private static int sBatteryStatus = 1;
    private static int sBatteryTemperature = 0;
    private static boolean sDelayEnable = SystemProperties.getBoolean("persist.sys.job_delay", false);

    public static boolean isDelayState(Context context) {
        if (!sDelayEnable || context == null) {
            return false;
        }
        getBatteryTemperature(context);
        if (MiuiSysUserServiceHelper.isLowMemory() || (sBatteryStatus != 2 && sBatteryTemperature > BATTERY_TEMPERATURE_THRESHOLD)) {
            isDelayState = true;
        } else {
            isDelayState = false;
        }
        if (isDelayState) {
            Slog.d(TAG, "Jobs should be delayed because of low memory or high temperature");
        }
        return isDelayState;
    }

    private static void getBatteryTemperature(Context context) {
        Intent intent = context.registerReceiver((BroadcastReceiver) null, filter);
        if (intent != null) {
            sBatteryStatus = intent.getIntExtra("status", 1);
            sBatteryTemperature = intent.getIntExtra("temperature", 0);
        }
    }
}
