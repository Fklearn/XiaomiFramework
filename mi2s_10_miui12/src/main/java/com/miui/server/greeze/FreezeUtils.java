package com.miui.server.greeze;

import android.os.SystemProperties;
import android.util.Slog;
import java.io.File;
import java.util.List;

public class FreezeUtils {
    static boolean DEBUG = SystemProperties.getBoolean("persist.sys.gz.debug", false);
    static boolean DEBUG_CHECK_FREEZE = true;
    static boolean DEBUG_CHECK_THAW = true;
    private static final String FREEZER_CGROUP_FROZEN = "/sys/fs/cgroup/freezer/perf/frozen";
    private static final String FREEZER_CGROUP_THAWED = "/sys/fs/cgroup/freezer/perf/thawed";
    private static final String FREEZER_FROZEN_PORCS = "/sys/fs/cgroup/freezer/perf/frozen/cgroup.procs";
    private static final String FREEZER_FROZEN_TASKS = "/sys/fs/cgroup/freezer/perf/frozen/tasks";
    private static final String FREEZER_ROOT_PATH = "/sys/fs/cgroup/freezer";
    private static final String FREEZER_THAWED_PORCS = "/sys/fs/cgroup/freezer/perf/thawed/cgroup.procs";
    private static final String FREEZER_THAWED_TASKS = "/sys/fs/cgroup/freezer/perf/thawed/tasks";
    private static final String TAG = "FreezeUtils";

    public static boolean isFreezerEnable() {
        return new File(FREEZER_CGROUP_FROZEN).exists() && new File(FREEZER_CGROUP_THAWED).exists();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        $closeResource((java.lang.Throwable) null, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0044, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        $closeResource(r3, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0048, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<java.lang.Integer> getFrozenPids() {
        /*
            java.lang.String r0 = "FreezeUtils"
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0049 }
            java.io.FileReader r3 = new java.io.FileReader     // Catch:{ IOException -> 0x0049 }
            java.lang.String r4 = "/sys/fs/cgroup/freezer/perf/frozen/cgroup.procs"
            r3.<init>(r4)     // Catch:{ IOException -> 0x0049 }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0049 }
            r3 = 0
        L_0x0014:
            java.lang.String r4 = r2.readLine()     // Catch:{ all -> 0x0042 }
            r5 = r4
            if (r4 == 0) goto L_0x003e
            int r4 = java.lang.Integer.parseInt(r5)     // Catch:{ NumberFormatException -> 0x0027 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ NumberFormatException -> 0x0027 }
            r1.add(r4)     // Catch:{ NumberFormatException -> 0x0027 }
            goto L_0x0014
        L_0x0027:
            r4 = move-exception
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0042 }
            r6.<init>()     // Catch:{ all -> 0x0042 }
            java.lang.String r7 = "Failed to parse /sys/fs/cgroup/freezer/perf/frozen/cgroup.procs line: "
            r6.append(r7)     // Catch:{ all -> 0x0042 }
            r6.append(r5)     // Catch:{ all -> 0x0042 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0042 }
            android.util.Slog.w(r0, r6, r4)     // Catch:{ all -> 0x0042 }
            goto L_0x0014
        L_0x003e:
            $closeResource(r3, r2)     // Catch:{ IOException -> 0x0049 }
            goto L_0x004f
        L_0x0042:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0044 }
        L_0x0044:
            r4 = move-exception
            $closeResource(r3, r2)     // Catch:{ IOException -> 0x0049 }
            throw r4     // Catch:{ IOException -> 0x0049 }
        L_0x0049:
            r2 = move-exception
            java.lang.String r3 = "Failed to get frozen pids"
            android.util.Slog.w(r0, r3, r2)
        L_0x004f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.greeze.FreezeUtils.getFrozenPids():java.util.List");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        $closeResource((java.lang.Throwable) null, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0044, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        $closeResource(r3, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0048, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<java.lang.Integer> getFrozonTids() {
        /*
            java.lang.String r0 = "FreezeUtils"
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0049 }
            java.io.FileReader r3 = new java.io.FileReader     // Catch:{ IOException -> 0x0049 }
            java.lang.String r4 = "/sys/fs/cgroup/freezer/perf/frozen/tasks"
            r3.<init>(r4)     // Catch:{ IOException -> 0x0049 }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0049 }
            r3 = 0
        L_0x0014:
            java.lang.String r4 = r2.readLine()     // Catch:{ all -> 0x0042 }
            r5 = r4
            if (r4 == 0) goto L_0x003e
            int r4 = java.lang.Integer.parseInt(r5)     // Catch:{ NumberFormatException -> 0x0027 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ NumberFormatException -> 0x0027 }
            r1.add(r4)     // Catch:{ NumberFormatException -> 0x0027 }
            goto L_0x0014
        L_0x0027:
            r4 = move-exception
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0042 }
            r6.<init>()     // Catch:{ all -> 0x0042 }
            java.lang.String r7 = "Failed to parse /sys/fs/cgroup/freezer/perf/frozen/tasks line: "
            r6.append(r7)     // Catch:{ all -> 0x0042 }
            r6.append(r5)     // Catch:{ all -> 0x0042 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0042 }
            android.util.Slog.w(r0, r6, r4)     // Catch:{ all -> 0x0042 }
            goto L_0x0014
        L_0x003e:
            $closeResource(r3, r2)     // Catch:{ IOException -> 0x0049 }
            goto L_0x004f
        L_0x0042:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0044 }
        L_0x0044:
            r4 = move-exception
            $closeResource(r3, r2)     // Catch:{ IOException -> 0x0049 }
            throw r4     // Catch:{ IOException -> 0x0049 }
        L_0x0049:
            r2 = move-exception
            java.lang.String r3 = "Failed to get frozen tids"
            android.util.Slog.w(r0, r3, r2)
        L_0x004f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.greeze.FreezeUtils.getFrozonTids():java.util.List");
    }

    public static boolean isFrozonPid(int pid) {
        return getFrozenPids().contains(Integer.valueOf(pid));
    }

    public static boolean isAllFrozon(int[] pids) {
        List<Integer> frozen = getFrozenPids();
        for (int pid : pids) {
            if (!frozen.contains(Integer.valueOf(pid))) {
                return false;
            }
        }
        return true;
    }

    static boolean isFrozonTid(int tid) {
        return getFrozonTids().contains(Integer.valueOf(tid));
    }

    public static boolean freezePid(int pid) {
        if (DEBUG) {
            Slog.d(TAG, "Freeze pid " + pid);
        }
        boolean done = writeNode(FREEZER_FROZEN_PORCS, pid);
        if (!DEBUG_CHECK_FREEZE || !done || isFrozonPid(pid)) {
            return done;
        }
        Slog.w(TAG, "Failed to thaw pid " + pid + ", it's still thawed!");
        return false;
    }

    public static boolean freezeTid(int tid) {
        if (DEBUG) {
            Slog.d(TAG, "Freeze tid " + tid);
        }
        return writeNode(FREEZER_FROZEN_TASKS, tid);
    }

    public static boolean thawPid(int pid) {
        if (DEBUG) {
            Slog.d(TAG, "Thaw pid " + pid);
        }
        boolean done = writeNode(FREEZER_THAWED_PORCS, pid);
        if (!DEBUG_CHECK_THAW || !done || !isFrozonPid(pid)) {
            return done;
        }
        Slog.w(TAG, "Failed to thaw pid " + pid + ", it's still frozen!");
        return false;
    }

    public static boolean thawTid(int tid) {
        if (DEBUG) {
            Slog.d(TAG, "Thaw tid " + tid);
        }
        return writeNode(FREEZER_THAWED_TASKS, tid);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0036, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        $closeResource(r3, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003a, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean writeNode(java.lang.String r6, int r7) {
        /*
            java.lang.String r0 = " with value "
            java.lang.String r1 = "FreezeUtils"
            java.io.PrintWriter r2 = new java.io.PrintWriter     // Catch:{ IOException -> 0x003b }
            r2.<init>(r6)     // Catch:{ IOException -> 0x003b }
            r3 = 0
            java.lang.String r4 = java.lang.Integer.toString(r7)     // Catch:{ all -> 0x0034 }
            r2.write(r4)     // Catch:{ all -> 0x0034 }
            boolean r4 = DEBUG     // Catch:{ all -> 0x0034 }
            if (r4 == 0) goto L_0x002f
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0034 }
            r4.<init>()     // Catch:{ all -> 0x0034 }
            java.lang.String r5 = "Wrote to "
            r4.append(r5)     // Catch:{ all -> 0x0034 }
            r4.append(r6)     // Catch:{ all -> 0x0034 }
            r4.append(r0)     // Catch:{ all -> 0x0034 }
            r4.append(r7)     // Catch:{ all -> 0x0034 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0034 }
            android.util.Slog.d(r1, r4)     // Catch:{ all -> 0x0034 }
        L_0x002f:
            r4 = 1
            $closeResource(r3, r2)     // Catch:{ IOException -> 0x003b }
            return r4
        L_0x0034:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0036 }
        L_0x0036:
            r4 = move-exception
            $closeResource(r3, r2)     // Catch:{ IOException -> 0x003b }
            throw r4     // Catch:{ IOException -> 0x003b }
        L_0x003b:
            r2 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Failed to write to "
            r3.append(r4)
            r3.append(r6)
            r3.append(r0)
            r3.append(r7)
            java.lang.String r0 = r3.toString()
            android.util.Slog.w(r1, r0, r2)
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.greeze.FreezeUtils.writeNode(java.lang.String, int):boolean");
    }
}
