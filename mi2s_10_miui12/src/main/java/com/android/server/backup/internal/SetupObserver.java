package com.android.server.backup.internal;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Slog;
import com.android.server.backup.BackupManagerService;
import com.android.server.backup.KeyValueBackupJob;
import com.android.server.backup.UserBackupManagerService;

public class SetupObserver extends ContentObserver {
    private final Context mContext;
    private final UserBackupManagerService mUserBackupManagerService;
    private final int mUserId;

    public SetupObserver(UserBackupManagerService userBackupManagerService, Handler handler) {
        super(handler);
        this.mUserBackupManagerService = userBackupManagerService;
        this.mContext = userBackupManagerService.getContext();
        this.mUserId = userBackupManagerService.getUserId();
    }

    public void onChange(boolean selfChange) {
        boolean previousSetupComplete = this.mUserBackupManagerService.isSetupComplete();
        boolean newSetupComplete = UserBackupManagerService.getSetupCompleteSettingForUser(this.mContext, this.mUserId);
        boolean resolvedSetupComplete = previousSetupComplete || newSetupComplete;
        this.mUserBackupManagerService.setSetupComplete(resolvedSetupComplete);
        Slog.d(BackupManagerService.TAG, "Setup complete change: was=" + previousSetupComplete + " new=" + newSetupComplete + " resolved=" + resolvedSetupComplete);
        synchronized (this.mUserBackupManagerService.getQueueLock()) {
            if (resolvedSetupComplete && !previousSetupComplete) {
                if (this.mUserBackupManagerService.isEnabled()) {
                    Slog.d(BackupManagerService.TAG, "Setup complete so starting backups");
                    KeyValueBackupJob.schedule(this.mUserBackupManagerService.getUserId(), this.mContext, this.mUserBackupManagerService.getConstants());
                    this.mUserBackupManagerService.scheduleNextFullBackupJob(0);
                }
            }
        }
    }
}
