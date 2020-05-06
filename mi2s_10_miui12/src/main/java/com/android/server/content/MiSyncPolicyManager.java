package com.android.server.content;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import com.android.server.content.SyncManager;
import java.util.Iterator;

public class MiSyncPolicyManager extends MiSyncPolicyManagerBase {
    private static final Uri SYNC_ON_WIFI_ONLY_URI = Settings.Secure.getUriFor("sync_on_wifi_only");
    private static final String TAG = "SyncManager";

    public static void registerSyncSettingsObserver(Context context, final SyncManager syncManager) {
        context.getContentResolver().registerContentObserver(SYNC_ON_WIFI_ONLY_URI, false, new ContentObserver(syncManager.mSyncHandler) {
            public void onChange(boolean selfChange) {
                MiSyncPolicyManager.handleMasterWifiOnlyChanged(syncManager);
            }
        });
    }

    public static long getSyncDelayedH(SyncOperation op, SyncManager syncManager) {
        if (!isSyncRoomForbiddenH(op, syncManager)) {
            return 0;
        }
        if (!Log.isLoggable("SyncManager", 3)) {
            return 30000;
        }
        Log.d("SyncManager", "injector: sync is forbidden for no room!");
        return 30000;
    }

    private static boolean isSyncRoomForbiddenH(SyncOperation op, SyncManager syncManager) {
        return MiSyncUtils.isSyncRoomForbiddenH(op, syncManager);
    }

    public static void wrapSyncJobInfo(Context context, SyncOperation op, SyncStorageEngine syncStorageEngine, JobInfo.Builder builder, long minDelay) {
        SyncJobInfoProcessor.buildSyncJobInfo(context, op, syncStorageEngine, builder, minDelay);
    }

    public static void handleMasterWifiOnlyChanged(final SyncManager syncManager) {
        syncManager.mSyncHandler.post(new Runnable() {
            public void run() {
                MiSyncPolicyManager.rescheduleAllSyncsH(SyncManager.this);
            }
        });
    }

    public static void handleSyncPauseChanged(Context context, SyncManager syncManager, long pauseTimeMills) {
        handleSyncPauseChanged(syncManager);
    }

    public static void handleSyncPauseChanged(final SyncManager syncManager) {
        syncManager.mSyncHandler.post(new Runnable() {
            public void run() {
                MiSyncPolicyManager.rescheduleXiaomiSyncsH(SyncManager.this);
            }
        });
    }

    public static void handleSyncStrategyChanged(Context context, SyncManager syncManager) {
        handleSyncStrategyChanged(syncManager);
    }

    public static void handleSyncStrategyChanged(final SyncManager syncManager) {
        syncManager.mSyncHandler.post(new Runnable() {
            public void run() {
                MiSyncPolicyManager.rescheduleXiaomiSyncsH(SyncManager.this);
            }
        });
    }

    /* access modifiers changed from: private */
    public static void rescheduleAllSyncsH(SyncManager syncManager) {
        Iterator<SyncManager.ActiveSyncContext> it = syncManager.mActiveSyncContexts.iterator();
        while (it.hasNext()) {
            syncManager.mSyncHandler.deferActiveSyncH(it.next());
        }
        JobScheduler jobScheduler = syncManager.getJobScheduler();
        int count = 0;
        for (SyncOperation op : syncManager.getAllPendingSyncs()) {
            count++;
            jobScheduler.cancel(op.jobId);
            SyncManagerAdapter.postScheduleSyncMessage(syncManager, op, 0);
        }
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "Rescheduled " + count + " syncs");
        }
    }

    /* access modifiers changed from: private */
    public static void rescheduleXiaomiSyncsH(SyncManager syncManager) {
        Iterator<SyncManager.ActiveSyncContext> it = syncManager.mActiveSyncContexts.iterator();
        while (it.hasNext()) {
            SyncManager.ActiveSyncContext asc = it.next();
            if (MiSyncUtils.checkSyncOperationAccount(asc.mSyncOperation)) {
                syncManager.mSyncHandler.deferActiveSyncH(asc);
            }
        }
        JobScheduler jobScheduler = syncManager.getJobScheduler();
        int count = 0;
        for (SyncOperation op : syncManager.getAllPendingSyncs()) {
            if (MiSyncUtils.checkSyncOperationAccount(op)) {
                count++;
                jobScheduler.cancel(op.jobId);
                SyncManagerAdapter.postScheduleSyncMessage(syncManager, op, 0);
            }
        }
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "Rescheduled " + count + " syncs");
        }
    }
}
