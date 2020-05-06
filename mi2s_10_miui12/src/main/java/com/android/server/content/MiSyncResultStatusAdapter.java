package com.android.server.content;

import android.content.SyncResult;
import android.content.SyncStatusInfo;
import android.text.TextUtils;
import android.util.Log;

public class MiSyncResultStatusAdapter {
    private static final String TAG = "SyncManager";

    public static void updateResultStatus(SyncStatusInfo syncStatusInfo, String lastSyncMessage, SyncResult syncResult) {
        if (!TextUtils.equals(lastSyncMessage, SyncStorageEngine.MESG_CANCELED)) {
            syncStatusInfo.miSyncStatusInfo.lastResultMessage = lastSyncMessage;
            if (syncResult != null && syncResult.miSyncResult != null && !TextUtils.isEmpty(syncResult.miSyncResult.resultMessage)) {
                syncStatusInfo.miSyncStatusInfo.lastResultMessage = syncResult.miSyncResult.resultMessage;
            } else if (Log.isLoggable("SyncManager", 3)) {
                Log.d("SyncManager", "updateResultStatus: sync result message is null");
            }
        } else if (Log.isLoggable("SyncManager", 3)) {
            Log.d("SyncManager", "updateResultStatus: lastSyncMessage is canceled, do nothing");
        }
    }
}
