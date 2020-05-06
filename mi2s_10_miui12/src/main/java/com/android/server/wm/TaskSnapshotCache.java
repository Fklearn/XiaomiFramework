package com.android.server.wm;

import android.app.ActivityManager;
import android.util.ArrayMap;
import java.io.PrintWriter;

class TaskSnapshotCache {
    private final ArrayMap<AppWindowToken, Integer> mAppTaskMap = new ArrayMap<>();
    private final TaskSnapshotLoader mLoader;
    private final ArrayMap<Integer, CacheEntry> mRunningCache = new ArrayMap<>();
    private final WindowManagerService mService;

    TaskSnapshotCache(WindowManagerService service, TaskSnapshotLoader loader) {
        this.mService = service;
        this.mLoader = loader;
    }

    /* access modifiers changed from: package-private */
    public void putSnapshot(Task task, ActivityManager.TaskSnapshot snapshot) {
        CacheEntry entry = this.mRunningCache.get(Integer.valueOf(task.mTaskId));
        if (entry != null) {
            this.mAppTaskMap.remove(entry.topApp);
        }
        this.mAppTaskMap.put((AppWindowToken) task.getTopChild(), Integer.valueOf(task.mTaskId));
        this.mRunningCache.put(Integer.valueOf(task.mTaskId), new CacheEntry(snapshot, (AppWindowToken) task.getTopChild()));
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001e, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0021, code lost:
        if (r6 != false) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0023, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0029, code lost:
        return tryRestoreFromDisk(r4, r5, r7);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.app.ActivityManager.TaskSnapshot getSnapshot(int r4, int r5, boolean r6, boolean r7) {
        /*
            r3 = this;
            com.android.server.wm.WindowManagerService r0 = r3.mService
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x002a }
            android.util.ArrayMap<java.lang.Integer, com.android.server.wm.TaskSnapshotCache$CacheEntry> r1 = r3.mRunningCache     // Catch:{ all -> 0x002a }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x002a }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x002a }
            com.android.server.wm.TaskSnapshotCache$CacheEntry r1 = (com.android.server.wm.TaskSnapshotCache.CacheEntry) r1     // Catch:{ all -> 0x002a }
            if (r1 == 0) goto L_0x001d
            android.app.ActivityManager$TaskSnapshot r2 = r1.snapshot     // Catch:{ all -> 0x002a }
            monitor-exit(r0)     // Catch:{ all -> 0x002a }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r2
        L_0x001d:
            monitor-exit(r0)     // Catch:{ all -> 0x002a }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            if (r6 != 0) goto L_0x0025
            r0 = 0
            return r0
        L_0x0025:
            android.app.ActivityManager$TaskSnapshot r0 = r3.tryRestoreFromDisk(r4, r5, r7)
            return r0
        L_0x002a:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002a }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.TaskSnapshotCache.getSnapshot(int, int, boolean, boolean):android.app.ActivityManager$TaskSnapshot");
    }

    private ActivityManager.TaskSnapshot tryRestoreFromDisk(int taskId, int userId, boolean reducedResolution) {
        ActivityManager.TaskSnapshot snapshot = this.mLoader.loadTask(taskId, userId, reducedResolution);
        if (snapshot == null) {
            return null;
        }
        return snapshot;
    }

    /* access modifiers changed from: package-private */
    public void onAppRemoved(AppWindowToken wtoken) {
        Integer taskId = this.mAppTaskMap.get(wtoken);
        if (taskId != null) {
            removeRunningEntry(taskId.intValue());
        }
    }

    /* access modifiers changed from: package-private */
    public void onAppDied(AppWindowToken wtoken) {
        Integer taskId = this.mAppTaskMap.get(wtoken);
        if (taskId != null) {
            removeRunningEntry(taskId.intValue());
        }
    }

    /* access modifiers changed from: package-private */
    public void onTaskRemoved(int taskId) {
        removeRunningEntry(taskId);
    }

    private void removeRunningEntry(int taskId) {
        CacheEntry entry = this.mRunningCache.get(Integer.valueOf(taskId));
        if (entry != null) {
            this.mAppTaskMap.remove(entry.topApp);
            this.mRunningCache.remove(Integer.valueOf(taskId));
        }
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        String doublePrefix = prefix + "  ";
        String triplePrefix = doublePrefix + "  ";
        pw.println(prefix + "SnapshotCache");
        for (int i = this.mRunningCache.size() + -1; i >= 0; i += -1) {
            CacheEntry entry = this.mRunningCache.valueAt(i);
            pw.println(doublePrefix + "Entry taskId=" + this.mRunningCache.keyAt(i));
            pw.println(triplePrefix + "topApp=" + entry.topApp);
            pw.println(triplePrefix + "snapshot=" + entry.snapshot);
        }
    }

    private static final class CacheEntry {
        final ActivityManager.TaskSnapshot snapshot;
        final AppWindowToken topApp;

        CacheEntry(ActivityManager.TaskSnapshot snapshot2, AppWindowToken topApp2) {
            this.snapshot = snapshot2;
            this.topApp = topApp2;
        }
    }
}
