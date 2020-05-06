package com.android.server.content;

class SyncManagerAdapter {
    private SyncManagerAdapter() {
    }

    public static void postScheduleSyncMessage(SyncManager manager, SyncOperation op, long delay) {
        manager.postScheduleSyncMessage(op, delay);
    }
}
