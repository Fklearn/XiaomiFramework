package com.android.server.rollback;

import android.content.Context;
import com.android.server.SystemService;

public final class RollbackManagerService extends SystemService {
    private RollbackManagerServiceImpl mService;

    public RollbackManagerService(Context context) {
        super(context);
    }

    /* JADX WARNING: type inference failed for: r0v1, types: [com.android.server.rollback.RollbackManagerServiceImpl, android.os.IBinder] */
    public void onStart() {
        this.mService = new RollbackManagerServiceImpl(getContext());
        publishBinderService("rollback", this.mService);
    }

    public void onUnlockUser(int user) {
        this.mService.onUnlockUser(user);
    }

    public void onBootPhase(int phase) {
        if (phase == 1000) {
            this.mService.onBootCompleted();
        }
    }
}
