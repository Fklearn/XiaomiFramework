package com.android.server.content;

import android.app.job.JobInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import com.android.server.am.AutoStartManagerService;

public class SyncManagerInjector extends SyncManagerAccountChangePolicy {
    public static final long SYNC_DELAY_ON_DISALLOW_METERED = 3600000;
    private static final String TAG = "SyncManager";
    public static final Uri uri = Settings.Secure.getUriFor("sync_on_wifi_only");

    public static boolean canBindService(Context context, Intent service, int userId) {
        return AutoStartManagerService.isAllowStartService(context, service, userId);
    }

    public static boolean isDisallowMeteredBySettings(Context ctx) {
        return Settings.Secure.getInt(ctx.getContentResolver(), "sync_on_wifi_only", 0) == 1;
    }

    public static void registerSyncSettingsObserver(Context context, SyncManager syncManager) {
        MiSyncPolicyManager.registerSyncSettingsObserver(context, syncManager);
    }

    public static long getSyncDelayedH(SyncOperation op, SyncManager syncManager) {
        return MiSyncPolicyManager.getSyncDelayedH(op, syncManager);
    }

    public static void wrapSyncJobInfo(Context context, SyncOperation op, SyncStorageEngine syncStorageEngine, JobInfo.Builder builder, long minDelay) {
        MiSyncPolicyManager.wrapSyncJobInfo(context, op, syncStorageEngine, builder, minDelay);
    }

    public static void handleMasterWifiOnlyChanged(SyncManager syncManager) {
        MiSyncPolicyManager.handleMasterWifiOnlyChanged(syncManager);
    }

    public static void handleSyncPauseChanged(Context context, SyncManager syncManager, long pauseTimeMills) {
        MiSyncPolicyManager.handleSyncPauseChanged(context, syncManager, pauseTimeMills);
    }

    public static void handleSyncPauseChanged(SyncManager syncManager) {
        MiSyncPolicyManager.handleSyncPauseChanged(syncManager);
    }

    public static void handleSyncStrategyChanged(Context context, SyncManager syncManager) {
        MiSyncPolicyManager.handleSyncStrategyChanged(context, syncManager);
    }

    public static void handleSyncStrategyChanged(SyncManager syncManager) {
        MiSyncPolicyManager.handleSyncStrategyChanged(syncManager);
    }
}
