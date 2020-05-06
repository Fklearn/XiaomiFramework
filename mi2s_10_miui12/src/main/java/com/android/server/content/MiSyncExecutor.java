package com.android.server.content;

import android.accounts.Account;

class MiSyncExecutor {
    private MiSyncExecutor() {
    }

    public static void sync(SyncManager syncManager, int sendingUserId, Account account) {
        throw new UnsupportedOperationException("Android sdk >= Q, unsupport call MiSyncExecutor sync method");
    }
}
