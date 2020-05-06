package com.android.server.content;

import android.accounts.Account;
import android.content.Context;

public class ContentServiceInjector {
    public static void setMiSyncPauseToTime(Context context, ContentService contentService, Account account, long pauseTimeMillis, int uid) {
        contentService.enforceCrossUserPermissionForInjector(uid, "no permission to set the sync status for user " + uid);
        context.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
        long identityToken = ContentService.clearCallingIdentity();
        try {
            SyncManager syncManager = contentService.getSyncManagerForInjector();
            if (syncManager != null) {
                syncManager.getSyncStorageEngine().setMiSyncPauseToTime(account, pauseTimeMillis, uid);
                SyncManagerInjector.handleSyncPauseChanged(context, syncManager, pauseTimeMillis);
            }
        } finally {
            ContentService.restoreCallingIdentity(identityToken);
        }
    }

    public static long getMiSyncPauseToTime(Context context, ContentService contentService, Account account, int uid) {
        contentService.enforceCrossUserPermissionForInjector(uid, "no permission to read the sync settings for user " + uid);
        context.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
        long identityToken = ContentService.clearCallingIdentity();
        try {
            SyncManager syncManager = contentService.getSyncManagerForInjector();
            if (syncManager != null) {
                return syncManager.getSyncStorageEngine().getMiSyncPauseToTime(account, uid);
            }
            ContentService.restoreCallingIdentity(identityToken);
            return 0;
        } finally {
            ContentService.restoreCallingIdentity(identityToken);
        }
    }

    public static void setMiSyncStrategy(Context context, ContentService contentService, Account account, int strategy, int uid) {
        contentService.enforceCrossUserPermissionForInjector(uid, "no permission to set the sync status for user " + uid);
        context.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
        long identityToken = ContentService.clearCallingIdentity();
        try {
            SyncManager syncManager = contentService.getSyncManagerForInjector();
            if (syncManager != null) {
                syncManager.getSyncStorageEngine().setMiSyncStrategy(account, strategy, uid);
                SyncManagerInjector.handleSyncStrategyChanged(context, syncManager);
            }
        } finally {
            ContentService.restoreCallingIdentity(identityToken);
        }
    }

    public static int getMiSyncStrategy(Context context, ContentService contentService, Account account, int uid) {
        contentService.enforceCrossUserPermissionForInjector(uid, "no permission to read the sync settings for user " + uid);
        context.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
        long identityToken = ContentService.clearCallingIdentity();
        try {
            SyncManager syncManager = contentService.getSyncManagerForInjector();
            if (syncManager != null) {
                return syncManager.getSyncStorageEngine().getMiSyncStrategy(account, uid);
            }
            ContentService.restoreCallingIdentity(identityToken);
            return 0;
        } finally {
            ContentService.restoreCallingIdentity(identityToken);
        }
    }
}
