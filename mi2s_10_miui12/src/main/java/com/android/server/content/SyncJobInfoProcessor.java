package com.android.server.content;

import android.app.job.JobInfo;
import android.content.Context;
import android.content.SyncStatusInfo;
import android.content.SyncStatusInfoAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import com.android.server.content.MiSyncConstants;

class SyncJobInfoProcessor {
    private static final String TAG = "SyncJobInfoProcessor";

    SyncJobInfoProcessor() {
    }

    static void buildSyncJobInfo(Context context, SyncOperation op, SyncStorageEngine syncStorageEngine, JobInfo.Builder builder, long minDelay) {
        if (op != null) {
            boolean isSyncOperationPass = MiSyncUtils.checkSyncOperationPass(op);
            if (!isSyncOperationPass) {
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "injector: wrapSyncJobInfo: setRequiresBatteryNotLow");
                }
                builder.setRequiresBatteryNotLow(true);
            }
            if (!isSyncOperationPass && isMasterSyncOnWifiOnly(context) && !op.isNotAllowedOnMetered()) {
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "injector: wrapSyncJobInfo: setRequiredNetworkType: Unmetered");
                }
                builder.setRequiredNetworkType(2);
            }
            if (MiSyncUtils.checkSyncOperationAccount(op)) {
                MiSyncPause miSyncPause = syncStorageEngine.getMiSyncPause(op.target.account.name, op.target.userId);
                if (miSyncPause != null) {
                    long syncResumeTimeLeft = miSyncPause.getResumeTimeLeft();
                    if (syncResumeTimeLeft > minDelay && !op.isPeriodic) {
                        if (Log.isLoggable(TAG, 3)) {
                            Log.d(TAG, "injector: wrapSyncJobInfo: setMinimumLatency: " + syncResumeTimeLeft);
                        }
                        builder.setMinimumLatency(syncResumeTimeLeft);
                    }
                    if (!isSyncOperationPass) {
                        MiSyncStrategy miSyncStrategy = syncStorageEngine.getMiSyncStrategy(op.target.account.name, op.target.userId);
                        if (miSyncStrategy != null) {
                            if (Log.isLoggable(TAG, 3)) {
                                Log.d(TAG, "injector: wrapSyncJobInfo: apply mi sync strategy");
                            }
                            miSyncStrategy.apply(op, getExtrasForStrategy(syncStorageEngine, op), builder);
                        } else if (Log.isLoggable(TAG, 3)) {
                            Log.d(TAG, "injector: wrapSyncJobInfo: mi sync strategy is null");
                        }
                    }
                } else if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "injector: wrapSyncJobInfo: mi sync pause is null");
                }
            } else if (Log.isLoggable(TAG, 3)) {
                Log.v(TAG, "injector: wrapSyncJobInfo: not xiaomi account, return");
            }
        } else if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "injector: wrapSyncJobInfo: null parameter, return");
        }
    }

    private static boolean isMasterSyncOnWifiOnly(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "sync_on_wifi_only", 0) == 1;
    }

    private static Bundle getExtrasForStrategy(SyncStorageEngine syncStorageEngine, SyncOperation op) {
        SyncStatusInfo syncStatusInfo;
        Bundle bundle = new Bundle();
        if (!(syncStorageEngine == null || op == null || (syncStatusInfo = syncStorageEngine.getStatusByAuthority(op.target)) == null)) {
            bundle.putInt(MiSyncConstants.Strategy.EXTRA_KEY_NUM_SYNCS, SyncStatusInfoAdapter.getNumSyncs(syncStatusInfo));
        }
        return bundle;
    }
}
