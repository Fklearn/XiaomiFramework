package com.android.server;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.util.Slog;

class MountServiceIdlerInjector {
    private static final long FINISH_INTERVAL_TIME = 7200000;
    private static final int MINIMUM_BATTERY_LEVEL = 10;
    private static final long MINIMUM_INTERVAL_TIME = 1800000;
    private static final String TAG = "MountServiceIdlerInjector";
    private static long sNextTrimDuration = 7200000;

    private MountServiceIdlerInjector() {
    }

    public static void internalScheduleIdlePass(Context context, int jobId, ComponentName componentName) {
        Slog.i(TAG, "sNextTrimDuration :  " + sNextTrimDuration);
        JobScheduler tm = (JobScheduler) context.getSystemService("jobscheduler");
        if (sNextTrimDuration < 1800000) {
            sNextTrimDuration = 1800000;
        }
        JobInfo.Builder builder = new JobInfo.Builder(jobId, componentName);
        builder.setMinimumLatency(sNextTrimDuration);
        tm.schedule(builder.build());
    }

    public static boolean canExecuteAsyncDiscard(Context context) {
        int batteryLevel = ((BatteryManager) context.getSystemService("batterymanager")).getIntProperty(4);
        if (((PowerManager) context.getSystemService("power")).isInteractive() || batteryLevel < 10) {
            return false;
        }
        return true;
    }

    public static boolean canExecuteIdleMaintenance(Context context) {
        BatteryManager bm = (BatteryManager) context.getSystemService("batterymanager");
        PowerManager pm = (PowerManager) context.getSystemService("power");
        return (pm.isDeviceIdleMode() || pm.isLightDeviceIdleMode()) && bm.isCharging();
    }

    public static void resetNextTrimDuration() {
        sNextTrimDuration = 7200000;
    }

    public static void halveNextTrimDuration() {
        sNextTrimDuration >>= 1;
    }
}
