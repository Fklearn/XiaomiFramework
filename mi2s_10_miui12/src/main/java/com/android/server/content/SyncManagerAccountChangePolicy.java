package com.android.server.content;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SyncManagerAccountChangePolicy {
    private static final int ALLOW_FIRST_NUM_SYNCS = 3;
    private static final int ALLOW_FIRST_NUM_SYNCS_FOR_BROWSER = 8;
    private static final String AUTHORITY_BROWSER = "com.miui.browser";
    private static final String AUTHORITY_CALENDAR = "com.android.calendar";
    private static final String AUTHORITY_CONTACTS = "com.android.contacts";
    protected static final String AUTHORITY_GALLERY = "com.miui.gallery.cloud.provider";
    private static final String AUTHORITY_NOTES = "notes";
    private static final long DEFAULT_SCREEN_OFF_PENDING_TIME = 120000;
    static final String EXTRA_KEY_BATTERY_CHARGING = "battery_charging";
    static final String EXTRA_KEY_BATTERY_LOW = "battery_low";
    static final String EXTRA_KEY_INTERACTIVE = "interactive";
    static final String EXTRA_KEY_LAST_SCREEN_OFF_TIME = "last_screen_off_time";
    static final String EXTRA_KEY_NUM_SYNCS = "num_syncs";
    private static final int LOW_BATTERY_LEVEL_LIMIT = 20;
    protected static final String PACKAGE_NAME_GALLERY = "com.miui.gallery";
    private static final Set<String> REAL_TIME_STRATEGY_AUTHORITY_SET = new HashSet();
    private static final String TAG = "SyncManager";

    public interface SyncForbiddenStrategy {
        boolean isSyncForbidden(Context context, String str, Bundle bundle);
    }

    protected static boolean isPackageNameForeground(Context context, String packageName) {
        ComponentName topActivity;
        List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
        if (runningTasks == null || runningTasks.isEmpty() || (topActivity = runningTasks.get(0).topActivity) == null) {
            return false;
        }
        return packageName.equals(topActivity.getPackageName());
    }

    public static boolean isBatteryCharging(int status) {
        return status == 2 || status == 5;
    }

    public static boolean isBatteryCharging(Context context) {
        return isBatteryCharging(context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("status", -1));
    }

    public static boolean isBatteryLow(int status, int level) {
        return status != 2 && level <= 20;
    }

    public static boolean isBatteryLow(Context context) {
        Intent batteryStatus = context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        return isBatteryLow(batteryStatus.getIntExtra("status", -1), batteryStatus.getIntExtra("level", 0));
    }

    static {
        REAL_TIME_STRATEGY_AUTHORITY_SET.add(AUTHORITY_CALENDAR);
        REAL_TIME_STRATEGY_AUTHORITY_SET.add(AUTHORITY_NOTES);
        REAL_TIME_STRATEGY_AUTHORITY_SET.add(AUTHORITY_CONTACTS);
    }

    public static SyncForbiddenStrategy getSyncForbiddenStrategy(String authority) {
        if (REAL_TIME_STRATEGY_AUTHORITY_SET.contains(authority)) {
            return new RealTimeStrategy();
        }
        return new DefaultSyncDataStrategy();
    }

    private static class RealTimeStrategy implements SyncForbiddenStrategy {
        private RealTimeStrategy() {
        }

        public boolean isSyncForbidden(Context context, String authority, Bundle extras) {
            return false;
        }
    }

    private static class DefaultSyncDataStrategy implements SyncForbiddenStrategy {
        private DefaultSyncDataStrategy() {
        }

        public boolean isSyncForbidden(Context context, String authority, Bundle extras) {
            int num = extras.getInt(SyncManagerAccountChangePolicy.EXTRA_KEY_NUM_SYNCS, -1);
            if (SyncManagerAccountChangePolicy.AUTHORITY_BROWSER.equals(authority)) {
                if (num >= 0 && num < 8) {
                    return false;
                }
            } else if (num >= 0 && num < 3) {
                return false;
            }
            if (extras.getBoolean(SyncManagerAccountChangePolicy.EXTRA_KEY_INTERACTIVE, false)) {
                return true;
            }
            if (System.currentTimeMillis() - extras.getLong(SyncManagerAccountChangePolicy.EXTRA_KEY_LAST_SCREEN_OFF_TIME, 0) >= 120000 && extras.getBoolean(SyncManagerAccountChangePolicy.EXTRA_KEY_BATTERY_CHARGING, false)) {
                return false;
            }
            return true;
        }
    }

    public static void rebuildSyncOperation(SyncOperation syncOperation) {
        if (!REAL_TIME_STRATEGY_AUTHORITY_SET.contains(syncOperation.target.provider)) {
            syncOperation.extras.putBoolean("require_charging", true);
        }
    }
}
