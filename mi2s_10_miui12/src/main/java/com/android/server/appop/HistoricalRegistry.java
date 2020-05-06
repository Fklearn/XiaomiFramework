package com.android.server.appop;

import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.RemoteCallback;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.ArraySet;
import android.util.LongSparseArray;
import android.util.Slog;
import android.util.TimeUtils;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.AtomicDirectory;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.XmlUtils;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.FgThread;
import com.android.server.job.controllers.JobStatus;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

final class HistoricalRegistry {
    private static final boolean DEBUG = false;
    private static final long DEFAULT_COMPRESSION_STEP = 10;
    private static final int DEFAULT_MODE = 1;
    private static final long DEFAULT_SNAPSHOT_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(15);
    private static final String HISTORY_FILE_SUFFIX = ".xml";
    private static final boolean KEEP_WTF_LOG = Build.IS_DEBUGGABLE;
    private static final String LOG_TAG = HistoricalRegistry.class.getSimpleName();
    private static final int MSG_WRITE_PENDING_HISTORY = 1;
    private static final String PARAMETER_ASSIGNMENT = "=";
    private static final String PARAMETER_DELIMITER = ",";
    @GuardedBy({"mInMemoryLock"})
    private long mBaseSnapshotInterval = DEFAULT_SNAPSHOT_INTERVAL_MILLIS;
    @GuardedBy({"mInMemoryLock"})
    private AppOpsManager.HistoricalOps mCurrentHistoricalOps;
    private final Object mInMemoryLock;
    @GuardedBy({"mInMemoryLock"})
    private long mIntervalCompressionMultiplier = DEFAULT_COMPRESSION_STEP;
    @GuardedBy({"mInMemoryLock"})
    private final int mMode = 0;
    @GuardedBy({"mInMemoryLock"})
    private long mNextPersistDueTimeMillis;
    private final Object mOnDiskLock = new Object();
    @GuardedBy({"mInMemoryLock"})
    private long mPendingHistoryOffsetMillis;
    @GuardedBy({"mLock"})
    private LinkedList<AppOpsManager.HistoricalOps> mPendingWrites = new LinkedList<>();
    @GuardedBy({"mOnDiskLock"})
    private Persistence mPersistence;

    HistoricalRegistry(Object lock) {
        this.mInMemoryLock = lock;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: package-private */
    public void systemReady(final ContentResolver resolver) {
        resolver.registerContentObserver(Settings.Global.getUriFor("appop_history_parameters"), false, new ContentObserver(FgThread.getHandler()) {
            public void onChange(boolean selfChange) {
                HistoricalRegistry.this.updateParametersFromSetting(resolver);
            }
        });
        updateParametersFromSetting(resolver);
        synchronized (this.mOnDiskLock) {
            synchronized (this.mInMemoryLock) {
            }
        }
    }

    private boolean isPersistenceInitializedMLocked() {
        return this.mPersistence != null;
    }

    /* access modifiers changed from: private */
    public void updateParametersFromSetting(ContentResolver resolver) {
        String setting = Settings.Global.getString(resolver, "appop_history_parameters");
        if (setting != null) {
            String[] parameters = setting.split(PARAMETER_DELIMITER);
            int length = parameters.length;
            char c = 0;
            String intervalMultiplierValue = null;
            String baseSnapshotIntervalValue = null;
            String modeValue = null;
            int i = 0;
            while (i < length) {
                String parameter = parameters[i];
                String[] parts = parameter.split(PARAMETER_ASSIGNMENT);
                if (parts.length == 2) {
                    String key = parts[c].trim();
                    char c2 = 65535;
                    int hashCode = key.hashCode();
                    if (hashCode != -190198682) {
                        if (hashCode != 3357091) {
                            if (hashCode == 245634204 && key.equals("baseIntervalMillis")) {
                                c2 = 1;
                            }
                        } else if (key.equals("mode")) {
                            c2 = 0;
                        }
                    } else if (key.equals("intervalMultiplier")) {
                        c2 = 2;
                    }
                    if (c2 == 0) {
                        modeValue = parts[1].trim();
                    } else if (c2 == 1) {
                        baseSnapshotIntervalValue = parts[1].trim();
                    } else if (c2 != 2) {
                        Slog.w(LOG_TAG, "Unknown parameter: " + parameter);
                    } else {
                        intervalMultiplierValue = parts[1].trim();
                    }
                }
                i++;
                c = 0;
            }
            if (!(modeValue == null || baseSnapshotIntervalValue == null || intervalMultiplierValue == null)) {
                try {
                    setHistoryParameters(AppOpsManager.parseHistoricalMode(modeValue), Long.parseLong(baseSnapshotIntervalValue), (long) Integer.parseInt(intervalMultiplierValue));
                    return;
                } catch (NumberFormatException e) {
                }
            }
            Slog.w(LOG_TAG, "Bad value forappop_history_parameters=" + setting + " resetting!");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 15 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0090, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dump(java.lang.String r16, java.io.PrintWriter r17, int r18, java.lang.String r19, int r20) {
        /*
            r15 = this;
            r8 = r15
            r9 = r16
            r10 = r17
            java.lang.Object r11 = r8.mOnDiskLock
            monitor-enter(r11)
            java.lang.Object r12 = r8.mInMemoryLock     // Catch:{ all -> 0x0094 }
            monitor-enter(r12)     // Catch:{ all -> 0x0094 }
            r17.println()     // Catch:{ all -> 0x0091 }
            r10.print(r9)     // Catch:{ all -> 0x0091 }
            java.lang.String r0 = "History:"
            r10.print(r0)     // Catch:{ all -> 0x0091 }
            java.lang.String r0 = "  mode="
            r10.print(r0)     // Catch:{ all -> 0x0091 }
            r0 = 0
            java.lang.String r0 = android.app.AppOpsManager.historicalModeToString(r0)     // Catch:{ all -> 0x0091 }
            r10.println(r0)     // Catch:{ all -> 0x0091 }
            com.android.server.appop.HistoricalRegistry$StringDumpVisitor r0 = new com.android.server.appop.HistoricalRegistry$StringDumpVisitor     // Catch:{ all -> 0x0091 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0091 }
            r1.<init>()     // Catch:{ all -> 0x0091 }
            r1.append(r9)     // Catch:{ all -> 0x0091 }
            java.lang.String r2 = "  "
            r1.append(r2)     // Catch:{ all -> 0x0091 }
            java.lang.String r3 = r1.toString()     // Catch:{ all -> 0x0091 }
            r1 = r0
            r2 = r15
            r4 = r17
            r5 = r18
            r6 = r19
            r7 = r20
            r1.<init>(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0091 }
            long r1 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0091 }
            android.app.AppOpsManager$HistoricalOps r3 = r15.getUpdatedPendingHistoricalOpsMLocked(r1)     // Catch:{ all -> 0x0091 }
            makeRelativeToEpochStart(r3, r1)     // Catch:{ all -> 0x0091 }
            r3.accept(r0)     // Catch:{ all -> 0x0091 }
            boolean r4 = r15.isPersistenceInitializedMLocked()     // Catch:{ all -> 0x0091 }
            if (r4 == 0) goto L_0x0061
            java.lang.String r4 = LOG_TAG     // Catch:{ all -> 0x0091 }
            java.lang.String r5 = "Interaction before persistence initialized"
            android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x0091 }
            monitor-exit(r12)     // Catch:{ all -> 0x0091 }
            monitor-exit(r11)     // Catch:{ all -> 0x0094 }
            return
        L_0x0061:
            com.android.server.appop.HistoricalRegistry$Persistence r4 = r8.mPersistence     // Catch:{ all -> 0x0091 }
            java.util.List r4 = r4.readHistoryDLocked()     // Catch:{ all -> 0x0091 }
            if (r4 == 0) goto L_0x0089
            long r5 = r8.mNextPersistDueTimeMillis     // Catch:{ all -> 0x0091 }
            long r5 = r5 - r1
            long r13 = r8.mBaseSnapshotInterval     // Catch:{ all -> 0x0091 }
            long r5 = r5 - r13
            int r7 = r4.size()     // Catch:{ all -> 0x0091 }
            r13 = 0
        L_0x0074:
            if (r13 >= r7) goto L_0x0088
            java.lang.Object r14 = r4.get(r13)     // Catch:{ all -> 0x0091 }
            android.app.AppOpsManager$HistoricalOps r14 = (android.app.AppOpsManager.HistoricalOps) r14     // Catch:{ all -> 0x0091 }
            r14.offsetBeginAndEndTime(r5)     // Catch:{ all -> 0x0091 }
            makeRelativeToEpochStart(r14, r1)     // Catch:{ all -> 0x0091 }
            r14.accept(r0)     // Catch:{ all -> 0x0091 }
            int r13 = r13 + 1
            goto L_0x0074
        L_0x0088:
            goto L_0x008e
        L_0x0089:
            java.lang.String r5 = "  Empty"
            r10.println(r5)     // Catch:{ all -> 0x0091 }
        L_0x008e:
            monitor-exit(r12)     // Catch:{ all -> 0x0091 }
            monitor-exit(r11)     // Catch:{ all -> 0x0094 }
            return
        L_0x0091:
            r0 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x0091 }
            throw r0     // Catch:{ all -> 0x0094 }
        L_0x0094:
            r0 = move-exception
            monitor-exit(r11)     // Catch:{ all -> 0x0094 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.HistoricalRegistry.dump(java.lang.String, java.io.PrintWriter, int, java.lang.String, int):void");
    }

    /* access modifiers changed from: package-private */
    public int getMode() {
        synchronized (this.mInMemoryLock) {
        }
        return 0;
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    /* access modifiers changed from: package-private */
    public void getHistoricalOpsFromDiskRaw(int uid, String packageName, String[] opNames, long beginTimeMillis, long endTimeMillis, int flags, RemoteCallback callback) {
        RemoteCallback remoteCallback = callback;
        synchronized (this.mOnDiskLock) {
            synchronized (this.mInMemoryLock) {
                if (!isPersistenceInitializedMLocked()) {
                    Slog.e(LOG_TAG, "Interaction before persistence initialized");
                    remoteCallback.sendResult(new Bundle());
                    return;
                }
                AppOpsManager.HistoricalOps result = new AppOpsManager.HistoricalOps(beginTimeMillis, endTimeMillis);
                this.mPersistence.collectHistoricalOpsDLocked(result, uid, packageName, opNames, beginTimeMillis, endTimeMillis, flags);
                Bundle payload = new Bundle();
                payload.putParcelable("historical_ops", result);
                remoteCallback.sendResult(payload);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 32 */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    void getHistoricalOps(int r33, java.lang.String r34, java.lang.String[] r35, long r36, long r38, int r40, android.os.RemoteCallback r41) {
        /*
            r32 = this;
            r1 = r32
            r2 = r36
            r4 = r41
            long r5 = java.lang.System.currentTimeMillis()
            r7 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r0 = (r38 > r7 ? 1 : (r38 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x0015
            r7 = r5
            goto L_0x0017
        L_0x0015:
            r7 = r38
        L_0x0017:
            long r9 = r5 - r7
            r11 = 0
            long r9 = java.lang.Math.max(r9, r11)
            long r13 = r5 - r2
            long r14 = java.lang.Math.max(r13, r11)
            android.app.AppOpsManager$HistoricalOps r0 = new android.app.AppOpsManager$HistoricalOps
            r0.<init>(r9, r14)
            r13 = r0
            java.lang.Object r11 = r1.mOnDiskLock
            monitor-enter(r11)
            java.lang.Object r12 = r1.mInMemoryLock     // Catch:{ all -> 0x015d }
            monitor-enter(r12)     // Catch:{ all -> 0x015d }
            boolean r0 = r32.isPersistenceInitializedMLocked()     // Catch:{ all -> 0x0151 }
            if (r0 != 0) goto L_0x006b
            java.lang.String r0 = LOG_TAG     // Catch:{ all -> 0x0061 }
            r16 = r13
            java.lang.String r13 = "Interaction before persistence initialized"
            android.util.Slog.e(r0, r13)     // Catch:{ all -> 0x0056 }
            android.os.Bundle r0 = new android.os.Bundle     // Catch:{ all -> 0x0056 }
            r0.<init>()     // Catch:{ all -> 0x0056 }
            r4.sendResult(r0)     // Catch:{ all -> 0x0056 }
            monitor-exit(r12)     // Catch:{ all -> 0x0056 }
            monitor-exit(r11)     // Catch:{ all -> 0x004b }
            return
        L_0x004b:
            r0 = move-exception
            r28 = r5
            r30 = r9
            r26 = r14
            r6 = r16
            goto L_0x0165
        L_0x0056:
            r0 = move-exception
            r28 = r5
            r30 = r9
            r26 = r14
            r6 = r16
            goto L_0x0159
        L_0x0061:
            r0 = move-exception
            r28 = r5
            r30 = r9
            r6 = r13
            r26 = r14
            goto L_0x0159
        L_0x006b:
            r16 = r13
            android.app.AppOpsManager$HistoricalOps r0 = r1.getUpdatedPendingHistoricalOpsMLocked(r5)     // Catch:{ all -> 0x0147 }
            long r17 = r0.getEndTimeMillis()     // Catch:{ all -> 0x0147 }
            int r13 = (r9 > r17 ? 1 : (r9 == r17 ? 0 : -1))
            if (r13 >= 0) goto L_0x00bb
            long r17 = r0.getBeginTimeMillis()     // Catch:{ all -> 0x00ae }
            int r13 = (r14 > r17 ? 1 : (r14 == r17 ? 0 : -1))
            if (r13 <= 0) goto L_0x00a9
            android.app.AppOpsManager$HistoricalOps r13 = new android.app.AppOpsManager$HistoricalOps     // Catch:{ all -> 0x00ae }
            r13.<init>(r0)     // Catch:{ all -> 0x00ae }
            r21 = r13
            r4 = r16
            r26 = r14
            r14 = r33
            r15 = r34
            r16 = r35
            r17 = r9
            r19 = r26
            r13.filter(r14, r15, r16, r17, r19)     // Catch:{ all -> 0x009f }
            r13 = r21
            r4.merge(r13)     // Catch:{ all -> 0x009f }
            goto L_0x00bf
        L_0x009f:
            r0 = move-exception
            r28 = r5
            r30 = r9
            r6 = r4
            r4 = r41
            goto L_0x0159
        L_0x00a9:
            r26 = r14
            r4 = r16
            goto L_0x00bf
        L_0x00ae:
            r0 = move-exception
            r26 = r14
            r4 = r41
            r28 = r5
            r30 = r9
            r6 = r16
            goto L_0x0159
        L_0x00bb:
            r26 = r14
            r4 = r16
        L_0x00bf:
            java.util.ArrayList r13 = new java.util.ArrayList     // Catch:{ all -> 0x013e }
            java.util.LinkedList<android.app.AppOpsManager$HistoricalOps> r14 = r1.mPendingWrites     // Catch:{ all -> 0x013e }
            r13.<init>(r14)     // Catch:{ all -> 0x013e }
            java.util.LinkedList<android.app.AppOpsManager$HistoricalOps> r14 = r1.mPendingWrites     // Catch:{ all -> 0x013e }
            r14.clear()     // Catch:{ all -> 0x013e }
            long r14 = r0.getEndTimeMillis()     // Catch:{ all -> 0x013e }
            int r14 = (r26 > r14 ? 1 : (r26 == r14 ? 0 : -1))
            if (r14 <= 0) goto L_0x00d5
            r14 = 1
            goto L_0x00d6
        L_0x00d5:
            r14 = 0
        L_0x00d6:
            monitor-exit(r12)     // Catch:{ all -> 0x013e }
            if (r14 == 0) goto L_0x011e
            r1.persistPendingHistory(r13)     // Catch:{ all -> 0x0115 }
            r15 = r13
            long r12 = r1.mNextPersistDueTimeMillis     // Catch:{ all -> 0x0115 }
            long r12 = r5 - r12
            r28 = r5
            long r5 = r1.mBaseSnapshotInterval     // Catch:{ all -> 0x010e }
            long r12 = r12 + r5
            long r5 = r9 - r12
            r30 = r9
            r9 = 0
            long r21 = java.lang.Math.max(r5, r9)     // Catch:{ all -> 0x0108 }
            long r5 = r26 - r12
            long r23 = java.lang.Math.max(r5, r9)     // Catch:{ all -> 0x0108 }
            com.android.server.appop.HistoricalRegistry$Persistence r5 = r1.mPersistence     // Catch:{ all -> 0x0108 }
            r16 = r5
            r17 = r4
            r18 = r33
            r19 = r34
            r20 = r35
            r25 = r40
            r16.collectHistoricalOpsDLocked(r17, r18, r19, r20, r21, r23, r25)     // Catch:{ all -> 0x0108 }
            goto L_0x0123
        L_0x0108:
            r0 = move-exception
            r6 = r4
            r4 = r41
            goto L_0x0165
        L_0x010e:
            r0 = move-exception
            r30 = r9
            r6 = r4
            r4 = r41
            goto L_0x0165
        L_0x0115:
            r0 = move-exception
            r28 = r5
            r30 = r9
            r6 = r4
            r4 = r41
            goto L_0x0165
        L_0x011e:
            r28 = r5
            r30 = r9
            r15 = r13
        L_0x0123:
            r4.setBeginAndEndTime(r2, r7)     // Catch:{ all -> 0x0139 }
            android.os.Bundle r5 = new android.os.Bundle     // Catch:{ all -> 0x0139 }
            r5.<init>()     // Catch:{ all -> 0x0139 }
            java.lang.String r6 = "historical_ops"
            r5.putParcelable(r6, r4)     // Catch:{ all -> 0x0139 }
            r6 = r4
            r4 = r41
            r4.sendResult(r5)     // Catch:{ all -> 0x0167 }
            monitor-exit(r11)     // Catch:{ all -> 0x0167 }
            return
        L_0x0139:
            r0 = move-exception
            r6 = r4
            r4 = r41
            goto L_0x0165
        L_0x013e:
            r0 = move-exception
            r28 = r5
            r30 = r9
            r6 = r4
            r4 = r41
            goto L_0x0159
        L_0x0147:
            r0 = move-exception
            r28 = r5
            r30 = r9
            r26 = r14
            r6 = r16
            goto L_0x0159
        L_0x0151:
            r0 = move-exception
            r28 = r5
            r30 = r9
            r6 = r13
            r26 = r14
        L_0x0159:
            monitor-exit(r12)     // Catch:{ all -> 0x015b }
            throw r0     // Catch:{ all -> 0x0167 }
        L_0x015b:
            r0 = move-exception
            goto L_0x0159
        L_0x015d:
            r0 = move-exception
            r28 = r5
            r30 = r9
            r6 = r13
            r26 = r14
        L_0x0165:
            monitor-exit(r11)     // Catch:{ all -> 0x0167 }
            throw r0
        L_0x0167:
            r0 = move-exception
            goto L_0x0165
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.HistoricalRegistry.getHistoricalOps(int, java.lang.String, java.lang.String[], long, long, int, android.os.RemoteCallback):void");
    }

    /* access modifiers changed from: package-private */
    public void incrementOpAccessedCount(int op, int uid, String packageName, int uidState, int flags) {
        synchronized (this.mInMemoryLock) {
        }
    }

    /* access modifiers changed from: package-private */
    public void incrementOpRejected(int op, int uid, String packageName, int uidState, int flags) {
        synchronized (this.mInMemoryLock) {
        }
    }

    /* access modifiers changed from: package-private */
    public void increaseOpAccessDuration(int op, int uid, String packageName, int uidState, int flags, long increment) {
        synchronized (this.mInMemoryLock) {
        }
    }

    /* access modifiers changed from: package-private */
    public void setHistoryParameters(int mode, long baseSnapshotInterval, long intervalCompressionMultiplier) {
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0044, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void offsetHistory(long r7) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mOnDiskLock
            monitor-enter(r0)
            java.lang.Object r1 = r6.mInMemoryLock     // Catch:{ all -> 0x0048 }
            monitor-enter(r1)     // Catch:{ all -> 0x0048 }
            boolean r2 = r6.isPersistenceInitializedMLocked()     // Catch:{ all -> 0x0045 }
            if (r2 != 0) goto L_0x0016
            java.lang.String r2 = LOG_TAG     // Catch:{ all -> 0x0045 }
            java.lang.String r3 = "Interaction before persistence initialized"
            android.util.Slog.e(r2, r3)     // Catch:{ all -> 0x0045 }
            monitor-exit(r1)     // Catch:{ all -> 0x0045 }
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            return
        L_0x0016:
            com.android.server.appop.HistoricalRegistry$Persistence r2 = r6.mPersistence     // Catch:{ all -> 0x0045 }
            java.util.List r2 = r2.readHistoryDLocked()     // Catch:{ all -> 0x0045 }
            r6.clearHistory()     // Catch:{ all -> 0x0045 }
            if (r2 == 0) goto L_0x0042
            int r3 = r2.size()     // Catch:{ all -> 0x0045 }
            r4 = 0
        L_0x0026:
            if (r4 >= r3) goto L_0x0034
            java.lang.Object r5 = r2.get(r4)     // Catch:{ all -> 0x0045 }
            android.app.AppOpsManager$HistoricalOps r5 = (android.app.AppOpsManager.HistoricalOps) r5     // Catch:{ all -> 0x0045 }
            r5.offsetBeginAndEndTime(r7)     // Catch:{ all -> 0x0045 }
            int r4 = r4 + 1
            goto L_0x0026
        L_0x0034:
            r4 = 0
            int r4 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x003d
            r6.pruneFutureOps(r2)     // Catch:{ all -> 0x0045 }
        L_0x003d:
            com.android.server.appop.HistoricalRegistry$Persistence r4 = r6.mPersistence     // Catch:{ all -> 0x0045 }
            r4.persistHistoricalOpsDLocked(r2)     // Catch:{ all -> 0x0045 }
        L_0x0042:
            monitor-exit(r1)     // Catch:{ all -> 0x0045 }
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            return
        L_0x0045:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0045 }
            throw r2     // Catch:{ all -> 0x0048 }
        L_0x0048:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.HistoricalRegistry.offsetHistory(long):void");
    }

    /* access modifiers changed from: package-private */
    public void addHistoricalOps(AppOpsManager.HistoricalOps ops) {
        synchronized (this.mInMemoryLock) {
            if (!isPersistenceInitializedMLocked()) {
                Slog.e(LOG_TAG, "Interaction before persistence initialized");
                return;
            }
            ops.offsetBeginAndEndTime(this.mBaseSnapshotInterval);
            this.mPendingWrites.offerFirst(ops);
            List<AppOpsManager.HistoricalOps> pendingWrites = new ArrayList<>(this.mPendingWrites);
            this.mPendingWrites.clear();
            persistPendingHistory(pendingWrites);
        }
    }

    private void resampleHistoryOnDiskInMemoryDMLocked(long offsetMillis) {
        this.mPersistence = new Persistence(this.mBaseSnapshotInterval, this.mIntervalCompressionMultiplier);
        offsetHistory(offsetMillis);
    }

    /* access modifiers changed from: package-private */
    public void resetHistoryParameters() {
        if (!isPersistenceInitializedMLocked()) {
            Slog.e(LOG_TAG, "Interaction before persistence initialized");
            return;
        }
        setHistoryParameters(1, DEFAULT_SNAPSHOT_INTERVAL_MILLIS, DEFAULT_COMPRESSION_STEP);
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: package-private */
    public void clearHistory(int uid, String packageName) {
        synchronized (this.mOnDiskLock) {
            synchronized (this.mInMemoryLock) {
                if (!isPersistenceInitializedMLocked()) {
                    Slog.e(LOG_TAG, "Interaction before persistence initialized");
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: package-private */
    public void clearHistory() {
        synchronized (this.mOnDiskLock) {
            synchronized (this.mInMemoryLock) {
                if (!isPersistenceInitializedMLocked()) {
                    Slog.e(LOG_TAG, "Interaction before persistence initialized");
                } else {
                    clearHistoryOnDiskDLocked();
                }
            }
        }
    }

    private void clearHistoryOnDiskDLocked() {
        BackgroundThread.getHandler().removeMessages(1);
        synchronized (this.mInMemoryLock) {
            this.mCurrentHistoricalOps = null;
            this.mNextPersistDueTimeMillis = System.currentTimeMillis();
            this.mPendingWrites.clear();
        }
        Persistence.clearHistoryDLocked();
    }

    private AppOpsManager.HistoricalOps getUpdatedPendingHistoricalOpsMLocked(long now) {
        if (this.mCurrentHistoricalOps != null) {
            long remainingTimeMillis = this.mNextPersistDueTimeMillis - now;
            long j = this.mBaseSnapshotInterval;
            if (remainingTimeMillis > j) {
                this.mPendingHistoryOffsetMillis = remainingTimeMillis - j;
            }
            this.mCurrentHistoricalOps.setEndTime(this.mBaseSnapshotInterval - remainingTimeMillis);
            if (remainingTimeMillis > 0) {
                return this.mCurrentHistoricalOps;
            }
            if (this.mCurrentHistoricalOps.isEmpty()) {
                this.mCurrentHistoricalOps.setBeginAndEndTime(0, 0);
                this.mNextPersistDueTimeMillis = this.mBaseSnapshotInterval + now;
                return this.mCurrentHistoricalOps;
            }
            this.mCurrentHistoricalOps.offsetBeginAndEndTime(this.mBaseSnapshotInterval);
            AppOpsManager.HistoricalOps historicalOps = this.mCurrentHistoricalOps;
            historicalOps.setBeginTime(historicalOps.getEndTimeMillis() - this.mBaseSnapshotInterval);
            this.mCurrentHistoricalOps.offsetBeginAndEndTime(Math.abs(remainingTimeMillis));
            schedulePersistHistoricalOpsMLocked(this.mCurrentHistoricalOps);
        }
        this.mCurrentHistoricalOps = new AppOpsManager.HistoricalOps(0, 0);
        this.mNextPersistDueTimeMillis = this.mBaseSnapshotInterval + now;
        return this.mCurrentHistoricalOps;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* access modifiers changed from: private */
    public void persistPendingHistory() {
        List<AppOpsManager.HistoricalOps> pendingWrites;
        synchronized (this.mOnDiskLock) {
            synchronized (this.mInMemoryLock) {
                pendingWrites = new ArrayList<>(this.mPendingWrites);
                this.mPendingWrites.clear();
                if (this.mPendingHistoryOffsetMillis != 0) {
                    resampleHistoryOnDiskInMemoryDMLocked(this.mPendingHistoryOffsetMillis);
                    this.mPendingHistoryOffsetMillis = 0;
                }
            }
            persistPendingHistory(pendingWrites);
        }
    }

    private void persistPendingHistory(List<AppOpsManager.HistoricalOps> pendingWrites) {
        synchronized (this.mOnDiskLock) {
            BackgroundThread.getHandler().removeMessages(1);
            if (!pendingWrites.isEmpty()) {
                int opCount = pendingWrites.size();
                for (int i = 0; i < opCount; i++) {
                    AppOpsManager.HistoricalOps current = pendingWrites.get(i);
                    if (i > 0) {
                        current.offsetBeginAndEndTime(pendingWrites.get(i - 1).getBeginTimeMillis());
                    }
                }
                this.mPersistence.persistHistoricalOpsDLocked(pendingWrites);
            }
        }
    }

    private void schedulePersistHistoricalOpsMLocked(AppOpsManager.HistoricalOps ops) {
        Message message = PooledLambda.obtainMessage($$Lambda$HistoricalRegistry$dJrtb4M71TzV6sx9vPEImQG_akU.INSTANCE, this);
        message.what = 1;
        BackgroundThread.getHandler().sendMessage(message);
        this.mPendingWrites.offerFirst(ops);
    }

    private static void makeRelativeToEpochStart(AppOpsManager.HistoricalOps ops, long nowMillis) {
        ops.setBeginAndEndTime(nowMillis - ops.getEndTimeMillis(), nowMillis - ops.getBeginTimeMillis());
    }

    private void pruneFutureOps(List<AppOpsManager.HistoricalOps> ops) {
        for (int i = ops.size() - 1; i >= 0; i--) {
            AppOpsManager.HistoricalOps op = ops.get(i);
            if (op.getEndTimeMillis() <= this.mBaseSnapshotInterval) {
                ops.remove(i);
            } else if (op.getBeginTimeMillis() < this.mBaseSnapshotInterval) {
                AppOpsManager.HistoricalOps unused = Persistence.spliceFromBeginning(op, ((double) (op.getEndTimeMillis() - this.mBaseSnapshotInterval)) / ((double) op.getDurationMillis()));
            }
        }
    }

    private static final class Persistence {
        private static final String ATTR_ACCESS_COUNT = "ac";
        private static final String ATTR_ACCESS_DURATION = "du";
        private static final String ATTR_BEGIN_TIME = "beg";
        private static final String ATTR_END_TIME = "end";
        private static final String ATTR_NAME = "na";
        private static final String ATTR_OVERFLOW = "ov";
        private static final String ATTR_REJECT_COUNT = "rc";
        private static final String ATTR_VERSION = "ver";
        private static final int CURRENT_VERSION = 2;
        private static final boolean DEBUG = false;
        private static final String LOG_TAG = Persistence.class.getSimpleName();
        private static final String TAG_HISTORY = "history";
        private static final String TAG_OP = "op";
        private static final String TAG_OPS = "ops";
        private static final String TAG_PACKAGE = "pkg";
        private static final String TAG_STATE = "st";
        private static final String TAG_UID = "uid";
        private static final AtomicDirectory sHistoricalAppOpsDir = new AtomicDirectory(new File(new File(Environment.getDataSystemDirectory(), "appops"), TAG_HISTORY));
        private final long mBaseSnapshotInterval;
        private final long mIntervalCompressionMultiplier;

        Persistence(long baseSnapshotInterval, long intervalCompressionMultiplier) {
            this.mBaseSnapshotInterval = baseSnapshotInterval;
            this.mIntervalCompressionMultiplier = intervalCompressionMultiplier;
        }

        private File generateFile(File baseDir, int depth) {
            long globalBeginMillis = computeGlobalIntervalBeginMillis(depth);
            return new File(baseDir, Long.toString(globalBeginMillis) + HistoricalRegistry.HISTORY_FILE_SUFFIX);
        }

        /* access modifiers changed from: package-private */
        public void clearHistoryDLocked(int uid, String packageName) {
            List<AppOpsManager.HistoricalOps> historicalOps = readHistoryDLocked();
            if (historicalOps != null) {
                for (int index = 0; index < historicalOps.size(); index++) {
                    historicalOps.get(index).clearHistory(uid, packageName);
                }
                clearHistoryDLocked();
                persistHistoricalOpsDLocked(historicalOps);
            }
        }

        static void clearHistoryDLocked() {
            sHistoricalAppOpsDir.delete();
        }

        /* access modifiers changed from: package-private */
        public void persistHistoricalOpsDLocked(List<AppOpsManager.HistoricalOps> ops) {
            try {
                File newBaseDir = sHistoricalAppOpsDir.startWrite();
                File oldBaseDir = sHistoricalAppOpsDir.getBackupDirectory();
                handlePersistHistoricalOpsRecursiveDLocked(newBaseDir, oldBaseDir, ops, getHistoricalFileNames(oldBaseDir), 0);
                sHistoricalAppOpsDir.finishWrite();
            } catch (Throwable t) {
                HistoricalRegistry.wtf("Failed to write historical app ops, restoring backup", t, (File) null);
                sHistoricalAppOpsDir.failWrite();
            }
        }

        /* access modifiers changed from: package-private */
        public List<AppOpsManager.HistoricalOps> readHistoryRawDLocked() {
            return collectHistoricalOpsBaseDLocked(-1, (String) null, (String[]) null, 0, JobStatus.NO_LATEST_RUNTIME, 31);
        }

        /* access modifiers changed from: package-private */
        public List<AppOpsManager.HistoricalOps> readHistoryDLocked() {
            List<AppOpsManager.HistoricalOps> result = readHistoryRawDLocked();
            if (result != null) {
                int opCount = result.size();
                for (int i = 0; i < opCount; i++) {
                    result.get(i).offsetBeginAndEndTime(this.mBaseSnapshotInterval);
                }
            }
            return result;
        }

        /* access modifiers changed from: package-private */
        public long getLastPersistTimeMillisDLocked() {
            File baseDir = null;
            try {
                baseDir = sHistoricalAppOpsDir.startRead();
                File[] files = baseDir.listFiles();
                if (files == null || files.length <= 0) {
                    sHistoricalAppOpsDir.finishRead();
                    return 0;
                }
                File shortestFile = null;
                for (File candidate : files) {
                    String candidateName = candidate.getName();
                    if (candidateName.endsWith(HistoricalRegistry.HISTORY_FILE_SUFFIX)) {
                        if (shortestFile == null) {
                            shortestFile = candidate;
                        } else if (candidateName.length() < shortestFile.getName().length()) {
                            shortestFile = candidate;
                        }
                    }
                }
                if (shortestFile == null) {
                    return 0;
                }
                try {
                    return Long.parseLong(shortestFile.getName().replace(HistoricalRegistry.HISTORY_FILE_SUFFIX, ""));
                } catch (NumberFormatException e) {
                    return 0;
                }
            } catch (Throwable e2) {
                HistoricalRegistry.wtf("Error reading historical app ops. Deleting history.", e2, baseDir);
                sHistoricalAppOpsDir.delete();
            }
        }

        /* access modifiers changed from: private */
        public void collectHistoricalOpsDLocked(AppOpsManager.HistoricalOps currentOps, int filterUid, String filterPackageName, String[] filterOpNames, long filterBeingMillis, long filterEndMillis, int filterFlags) {
            List<AppOpsManager.HistoricalOps> readOps = collectHistoricalOpsBaseDLocked(filterUid, filterPackageName, filterOpNames, filterBeingMillis, filterEndMillis, filterFlags);
            if (readOps != null) {
                int readCount = readOps.size();
                for (int i = 0; i < readCount; i++) {
                    AppOpsManager.HistoricalOps historicalOps = currentOps;
                    currentOps.merge(readOps.get(i));
                }
                AppOpsManager.HistoricalOps historicalOps2 = currentOps;
                return;
            }
            AppOpsManager.HistoricalOps historicalOps3 = currentOps;
        }

        private LinkedList<AppOpsManager.HistoricalOps> collectHistoricalOpsBaseDLocked(int filterUid, String filterPackageName, String[] filterOpNames, long filterBeginTimeMillis, long filterEndTimeMillis, int filterFlags) {
            File baseDir;
            try {
                baseDir = sHistoricalAppOpsDir.startRead();
                try {
                    LinkedList<AppOpsManager.HistoricalOps> ops = collectHistoricalOpsRecursiveDLocked(baseDir, filterUid, filterPackageName, filterOpNames, filterBeginTimeMillis, filterEndTimeMillis, filterFlags, new long[]{0}, (LinkedList<AppOpsManager.HistoricalOps>) null, 0, getHistoricalFileNames(baseDir));
                    sHistoricalAppOpsDir.finishRead();
                    return ops;
                } catch (Throwable th) {
                    t = th;
                    HistoricalRegistry.wtf("Error reading historical app ops. Deleting history.", t, baseDir);
                    sHistoricalAppOpsDir.delete();
                    return null;
                }
            } catch (Throwable th2) {
                t = th2;
                baseDir = null;
                HistoricalRegistry.wtf("Error reading historical app ops. Deleting history.", t, baseDir);
                sHistoricalAppOpsDir.delete();
                return null;
            }
        }

        private LinkedList<AppOpsManager.HistoricalOps> collectHistoricalOpsRecursiveDLocked(File baseDir, int filterUid, String filterPackageName, String[] filterOpNames, long filterBeginTimeMillis, long filterEndTimeMillis, int filterFlags, long[] globalContentOffsetMillis, LinkedList<AppOpsManager.HistoricalOps> outOps, int depth, Set<String> historyFiles) throws IOException, XmlPullParserException {
            int i = depth;
            long previousIntervalEndMillis = ((long) Math.pow((double) this.mIntervalCompressionMultiplier, (double) i)) * this.mBaseSnapshotInterval;
            long currentIntervalEndMillis = this.mBaseSnapshotInterval * ((long) Math.pow((double) this.mIntervalCompressionMultiplier, (double) (i + 1)));
            long filterBeginTimeMillis2 = Math.max(filterBeginTimeMillis - previousIntervalEndMillis, 0);
            long filterEndTimeMillis2 = filterEndTimeMillis - previousIntervalEndMillis;
            long currentIntervalEndMillis2 = currentIntervalEndMillis;
            List<AppOpsManager.HistoricalOps> readOps = readHistoricalOpsLocked(baseDir, previousIntervalEndMillis, currentIntervalEndMillis, filterUid, filterPackageName, filterOpNames, filterBeginTimeMillis2, filterEndTimeMillis2, filterFlags, globalContentOffsetMillis, depth, historyFiles);
            if (readOps != null && readOps.isEmpty()) {
                return outOps;
            }
            LinkedList<AppOpsManager.HistoricalOps> outOps2 = collectHistoricalOpsRecursiveDLocked(baseDir, filterUid, filterPackageName, filterOpNames, filterBeginTimeMillis2, filterEndTimeMillis2, filterFlags, globalContentOffsetMillis, outOps, depth + 1, historyFiles);
            if (outOps2 != null) {
                int opCount = outOps2.size();
                for (int i2 = 0; i2 < opCount; i2++) {
                    outOps2.get(i2).offsetBeginAndEndTime(currentIntervalEndMillis2);
                }
            }
            if (readOps != null) {
                if (outOps2 == null) {
                    outOps2 = new LinkedList<>();
                }
                for (int i3 = readOps.size() - 1; i3 >= 0; i3--) {
                    outOps2.offerFirst(readOps.get(i3));
                }
            }
            return outOps2;
        }

        private void handlePersistHistoricalOpsRecursiveDLocked(File newBaseDir, File oldBaseDir, List<AppOpsManager.HistoricalOps> passedOps, Set<String> oldFileNames, int depth) throws IOException, XmlPullParserException {
            Persistence persistence;
            File file;
            int i;
            Set<String> set;
            List<AppOpsManager.HistoricalOps> list;
            AppOpsManager.HistoricalOps overflowedOp;
            AppOpsManager.HistoricalOps persistedOp;
            File file2 = newBaseDir;
            List<AppOpsManager.HistoricalOps> list2 = passedOps;
            Set<String> set2 = oldFileNames;
            int i2 = depth;
            long previousIntervalEndMillis = ((long) Math.pow((double) this.mIntervalCompressionMultiplier, (double) i2)) * this.mBaseSnapshotInterval;
            long currentIntervalEndMillis = ((long) Math.pow((double) this.mIntervalCompressionMultiplier, (double) (i2 + 1))) * this.mBaseSnapshotInterval;
            if (list2 == null) {
                set = set2;
                i = i2;
                file = file2;
                persistence = this;
            } else if (passedOps.isEmpty()) {
                long j = previousIntervalEndMillis;
                set = set2;
                i = i2;
                file = file2;
                persistence = this;
            } else {
                int passedOpCount = passedOps.size();
                for (int i3 = 0; i3 < passedOpCount; i3++) {
                    list2.get(i3).offsetBeginAndEndTime(-previousIntervalEndMillis);
                }
                int i4 = passedOpCount;
                long previousIntervalEndMillis2 = previousIntervalEndMillis;
                List<AppOpsManager.HistoricalOps> existingOps = readHistoricalOpsLocked(oldBaseDir, previousIntervalEndMillis2, currentIntervalEndMillis, -1, (String) null, (String[]) null, Long.MIN_VALUE, JobStatus.NO_LATEST_RUNTIME, 31, (long[]) null, depth, (Set<String>) null);
                if (existingOps != null) {
                    int existingOpCount = existingOps.size();
                    if (existingOpCount > 0) {
                        list = passedOps;
                        long elapsedTimeMillis = list.get(passedOps.size() - 1).getEndTimeMillis();
                        for (int i5 = 0; i5 < existingOpCount; i5++) {
                            existingOps.get(i5).offsetBeginAndEndTime(elapsedTimeMillis);
                        }
                    } else {
                        list = passedOps;
                    }
                } else {
                    list = passedOps;
                }
                long slotDurationMillis = previousIntervalEndMillis2;
                List<AppOpsManager.HistoricalOps> allOps = new LinkedList<>(list);
                if (existingOps != null) {
                    allOps.addAll(existingOps);
                }
                int opCount = allOps.size();
                List<AppOpsManager.HistoricalOps> persistedOps = null;
                List<AppOpsManager.HistoricalOps> overflowedOps = null;
                long intervalOverflowMillis = 0;
                for (int i6 = 0; i6 < opCount; i6++) {
                    AppOpsManager.HistoricalOps op = allOps.get(i6);
                    if (op.getEndTimeMillis() <= currentIntervalEndMillis) {
                        persistedOp = op;
                        overflowedOp = null;
                    } else if (op.getBeginTimeMillis() < currentIntervalEndMillis) {
                        persistedOp = op;
                        long intervalOverflowMillis2 = op.getEndTimeMillis() - currentIntervalEndMillis;
                        if (intervalOverflowMillis2 > previousIntervalEndMillis2) {
                            long j2 = intervalOverflowMillis2;
                            overflowedOp = spliceFromEnd(op, ((double) intervalOverflowMillis2) / ((double) op.getDurationMillis()));
                            persistedOp = persistedOp;
                            intervalOverflowMillis = op.getEndTimeMillis() - currentIntervalEndMillis;
                        } else {
                            long intervalOverflowMillis3 = intervalOverflowMillis2;
                            overflowedOp = null;
                            intervalOverflowMillis = intervalOverflowMillis3;
                        }
                    } else {
                        persistedOp = null;
                        overflowedOp = op;
                    }
                    if (persistedOp != null) {
                        if (persistedOps == null) {
                            persistedOps = new ArrayList<>();
                        }
                        persistedOps.add(persistedOp);
                    }
                    if (overflowedOp != null) {
                        if (overflowedOps == null) {
                            overflowedOps = new ArrayList<>();
                        }
                        overflowedOps.add(overflowedOp);
                    }
                }
                File file3 = newBaseDir;
                int i7 = depth;
                File newFile = generateFile(file3, i7);
                Set<String> set3 = oldFileNames;
                set3.remove(newFile.getName());
                if (persistedOps != null) {
                    normalizeSnapshotForSlotDuration(persistedOps, slotDurationMillis);
                    writeHistoricalOpsDLocked(persistedOps, intervalOverflowMillis, newFile);
                }
                List<AppOpsManager.HistoricalOps> list3 = existingOps;
                Set<String> set4 = set3;
                File file4 = newFile;
                int i8 = i7;
                long j3 = slotDurationMillis;
                File file5 = file3;
                handlePersistHistoricalOpsRecursiveDLocked(newBaseDir, oldBaseDir, overflowedOps, oldFileNames, i7 + 1);
                return;
            }
            if (!oldFileNames.isEmpty()) {
                File oldFile = persistence.generateFile(oldBaseDir, i);
                if (set.remove(oldFile.getName())) {
                    Files.createLink(persistence.generateFile(file, i).toPath(), oldFile.toPath());
                }
                handlePersistHistoricalOpsRecursiveDLocked(newBaseDir, oldBaseDir, passedOps, oldFileNames, i + 1);
                return;
            }
            File file6 = oldBaseDir;
        }

        private List<AppOpsManager.HistoricalOps> readHistoricalOpsLocked(File baseDir, long intervalBeginMillis, long intervalEndMillis, int filterUid, String filterPackageName, String[] filterOpNames, long filterBeginTimeMillis, long filterEndTimeMillis, int filterFlags, long[] cumulativeOverflowMillis, int depth, Set<String> historyFiles) throws IOException, XmlPullParserException {
            Set<String> set = historyFiles;
            File file = generateFile(baseDir, depth);
            if (set != null) {
                set.remove(file.getName());
            }
            if (filterBeginTimeMillis >= filterEndTimeMillis || filterEndTimeMillis < intervalBeginMillis) {
                return Collections.emptyList();
            }
            if (filterBeginTimeMillis < intervalEndMillis + ((intervalEndMillis - intervalBeginMillis) / this.mIntervalCompressionMultiplier) + (cumulativeOverflowMillis != null ? cumulativeOverflowMillis[0] : 0) && file.exists()) {
                return readHistoricalOpsLocked(file, filterUid, filterPackageName, filterOpNames, filterBeginTimeMillis, filterEndTimeMillis, filterFlags, cumulativeOverflowMillis);
            }
            if (set == null || historyFiles.isEmpty()) {
                return Collections.emptyList();
            }
            return null;
        }

        /* Debug info: failed to restart local var, previous not found, register: 19 */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x00bb, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x00bc, code lost:
            r3 = r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:51:?, code lost:
            r13.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:52:0x00c1, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:?, code lost:
            r2.addSuppressed(r0);
         */
        /* JADX WARNING: Exception block dominator not found, dom blocks: [B:36:0x0097, B:46:0x00ba, B:50:0x00bd] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private java.util.List<android.app.AppOpsManager.HistoricalOps> readHistoricalOpsLocked(java.io.File r20, int r21, java.lang.String r22, java.lang.String[] r23, long r24, long r26, int r28, long[] r29) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
            /*
                r19 = this;
                r1 = r20
                r2 = 0
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x00c9 }
                r0.<init>(r1)     // Catch:{ FileNotFoundException -> 0x00c9 }
                r13 = r0
                org.xmlpull.v1.XmlPullParser r0 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x00b6 }
                java.nio.charset.Charset r3 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ all -> 0x00b6 }
                java.lang.String r3 = r3.name()     // Catch:{ all -> 0x00b6 }
                r0.setInput(r13, r3)     // Catch:{ all -> 0x00b6 }
                java.lang.String r3 = "history"
                com.android.internal.util.XmlUtils.beginDocument(r0, r3)     // Catch:{ all -> 0x00b6 }
                java.lang.String r3 = "ver"
                int r3 = com.android.internal.util.XmlUtils.readIntAttribute(r0, r3)     // Catch:{ all -> 0x00b6 }
                r14 = r3
                r3 = 2
                if (r14 < r3) goto L_0x009f
                java.lang.String r3 = "ov"
                r4 = 0
                long r3 = com.android.internal.util.XmlUtils.readLongAttribute(r0, r3, r4)     // Catch:{ all -> 0x00b6 }
                r15 = r3
                int r3 = r0.getDepth()     // Catch:{ all -> 0x00b6 }
                r12 = r3
                r17 = r2
            L_0x0038:
                boolean r2 = com.android.internal.util.XmlUtils.nextElementWithin(r0, r12)     // Catch:{ all -> 0x009c }
                if (r2 == 0) goto L_0x008d
                java.lang.String r2 = "ops"
                java.lang.String r3 = r0.getName()     // Catch:{ all -> 0x009c }
                boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x009c }
                if (r2 == 0) goto L_0x0088
                r2 = r19
                r3 = r0
                r4 = r21
                r5 = r22
                r6 = r23
                r7 = r24
                r9 = r26
                r11 = r28
                r18 = r12
                r12 = r29
                android.app.AppOpsManager$HistoricalOps r2 = r2.readeHistoricalOpsDLocked(r3, r4, r5, r6, r7, r9, r11, r12)     // Catch:{ all -> 0x009c }
                if (r2 != 0) goto L_0x0065
                goto L_0x008a
            L_0x0065:
                boolean r3 = r2.isEmpty()     // Catch:{ all -> 0x009c }
                if (r3 == 0) goto L_0x006f
                com.android.internal.util.XmlUtils.skipCurrentTag(r0)     // Catch:{ all -> 0x009c }
                goto L_0x008a
            L_0x006f:
                if (r17 != 0) goto L_0x0079
                java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ all -> 0x009c }
                r3.<init>()     // Catch:{ all -> 0x009c }
                r17 = r3
                goto L_0x007b
            L_0x0079:
                r3 = r17
            L_0x007b:
                r3.add(r2)     // Catch:{ all -> 0x0083 }
                r17 = r3
                r12 = r18
                goto L_0x0038
            L_0x0083:
                r0 = move-exception
                r2 = r0
                r17 = r3
                goto L_0x00ba
            L_0x0088:
                r18 = r12
            L_0x008a:
                r12 = r18
                goto L_0x0038
            L_0x008d:
                r18 = r12
                if (r29 == 0) goto L_0x0097
                r2 = 0
                r3 = r29[r2]     // Catch:{ all -> 0x009c }
                long r3 = r3 + r15
                r29[r2] = r3     // Catch:{ all -> 0x009c }
            L_0x0097:
                r13.close()     // Catch:{ FileNotFoundException -> 0x00c7 }
                return r17
            L_0x009c:
                r0 = move-exception
                r2 = r0
                goto L_0x00ba
            L_0x009f:
                java.lang.IllegalStateException r3 = new java.lang.IllegalStateException     // Catch:{ all -> 0x00b6 }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b6 }
                r4.<init>()     // Catch:{ all -> 0x00b6 }
                java.lang.String r5 = "Dropping unsupported history version 1 for file:"
                r4.append(r5)     // Catch:{ all -> 0x00b6 }
                r4.append(r1)     // Catch:{ all -> 0x00b6 }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00b6 }
                r3.<init>(r4)     // Catch:{ all -> 0x00b6 }
                throw r3     // Catch:{ all -> 0x00b6 }
            L_0x00b6:
                r0 = move-exception
                r17 = r2
                r2 = r0
            L_0x00ba:
                throw r2     // Catch:{ all -> 0x00bb }
            L_0x00bb:
                r0 = move-exception
                r3 = r0
                r13.close()     // Catch:{ all -> 0x00c1 }
                goto L_0x00c6
            L_0x00c1:
                r0 = move-exception
                r4 = r0
                r2.addSuppressed(r4)     // Catch:{ FileNotFoundException -> 0x00c7 }
            L_0x00c6:
                throw r3     // Catch:{ FileNotFoundException -> 0x00c7 }
            L_0x00c7:
                r0 = move-exception
                goto L_0x00cc
            L_0x00c9:
                r0 = move-exception
                r17 = r2
            L_0x00cc:
                java.lang.String r2 = LOG_TAG
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "No history file: "
                r3.append(r4)
                java.lang.String r4 = r20.getName()
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                android.util.Slog.i(r2, r3)
                java.util.List r2 = java.util.Collections.emptyList()
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.HistoricalRegistry.Persistence.readHistoricalOpsLocked(java.io.File, int, java.lang.String, java.lang.String[], long, long, int, long[]):java.util.List");
        }

        private AppOpsManager.HistoricalOps readeHistoricalOpsDLocked(XmlPullParser parser, int filterUid, String filterPackageName, String[] filterOpNames, long filterBeginTimeMillis, long filterEndTimeMillis, int filterFlags, long[] cumulativeOverflowMillis) throws IOException, XmlPullParserException {
            XmlPullParser xmlPullParser = parser;
            long j = filterBeginTimeMillis;
            long j2 = filterEndTimeMillis;
            long beginTimeMillis = XmlUtils.readLongAttribute(xmlPullParser, ATTR_BEGIN_TIME, 0) + (cumulativeOverflowMillis != null ? cumulativeOverflowMillis[0] : 0);
            long endTimeMillis = XmlUtils.readLongAttribute(xmlPullParser, ATTR_END_TIME, 0) + (cumulativeOverflowMillis != null ? cumulativeOverflowMillis[0] : 0);
            if (j2 < beginTimeMillis) {
                return null;
            }
            if (j > endTimeMillis) {
                return new AppOpsManager.HistoricalOps(0, 0);
            }
            long filteredBeginTimeMillis = Math.max(beginTimeMillis, j);
            long filteredEndTimeMillis = Math.min(endTimeMillis, j2);
            long filteredEndTimeMillis2 = filteredEndTimeMillis;
            double filterScale = ((double) (filteredEndTimeMillis - filteredBeginTimeMillis)) / ((double) (endTimeMillis - beginTimeMillis));
            int depth = parser.getDepth();
            AppOpsManager.HistoricalOps ops = null;
            while (XmlUtils.nextElementWithin(xmlPullParser, depth)) {
                if ("uid".equals(parser.getName())) {
                    AppOpsManager.HistoricalOps ops2 = ops;
                    long filteredEndTimeMillis3 = filteredEndTimeMillis2;
                    int depth2 = depth;
                    long filteredBeginTimeMillis2 = filteredBeginTimeMillis;
                    long endTimeMillis2 = endTimeMillis;
                    AppOpsManager.HistoricalOps returnedOps = readHistoricalUidOpsDLocked(ops, parser, filterUid, filterPackageName, filterOpNames, filterFlags, filterScale);
                    if (ops2 == null) {
                        ops = returnedOps;
                    } else {
                        ops = ops2;
                    }
                    filteredBeginTimeMillis = filteredBeginTimeMillis2;
                    depth = depth2;
                    endTimeMillis = endTimeMillis2;
                    long filteredBeginTimeMillis3 = filterEndTimeMillis;
                    filteredEndTimeMillis2 = filteredEndTimeMillis3;
                    xmlPullParser = parser;
                    long j3 = filterBeginTimeMillis;
                } else {
                    AppOpsManager.HistoricalOps historicalOps = ops;
                    long j4 = filteredBeginTimeMillis;
                    long j5 = endTimeMillis;
                    long filteredEndTimeMillis4 = filteredEndTimeMillis2;
                    int i = depth;
                    long filteredBeginTimeMillis4 = filterEndTimeMillis;
                    filteredEndTimeMillis2 = filteredEndTimeMillis4;
                    xmlPullParser = parser;
                    long j6 = filterBeginTimeMillis;
                }
            }
            AppOpsManager.HistoricalOps ops3 = ops;
            long filteredBeginTimeMillis5 = filteredBeginTimeMillis;
            long j7 = endTimeMillis;
            long filteredEndTimeMillis5 = filteredEndTimeMillis2;
            int i2 = depth;
            if (ops3 != null) {
                ops3.setBeginAndEndTime(filteredBeginTimeMillis5, filteredEndTimeMillis5);
            }
            return ops3;
        }

        private AppOpsManager.HistoricalOps readHistoricalUidOpsDLocked(AppOpsManager.HistoricalOps ops, XmlPullParser parser, int filterUid, String filterPackageName, String[] filterOpNames, int filterFlags, double filterScale) throws IOException, XmlPullParserException {
            XmlPullParser xmlPullParser = parser;
            int i = filterUid;
            int uid = XmlUtils.readIntAttribute(xmlPullParser, ATTR_NAME);
            if (i == -1 || i == uid) {
                int depth = parser.getDepth();
                AppOpsManager.HistoricalOps ops2 = ops;
                while (XmlUtils.nextElementWithin(xmlPullParser, depth)) {
                    if ("pkg".equals(parser.getName())) {
                        AppOpsManager.HistoricalOps returnedOps = readHistoricalPackageOpsDLocked(ops2, uid, parser, filterPackageName, filterOpNames, filterFlags, filterScale);
                        if (ops2 == null) {
                            ops2 = returnedOps;
                        }
                    }
                }
                return ops2;
            }
            XmlUtils.skipCurrentTag(parser);
            return null;
        }

        private AppOpsManager.HistoricalOps readHistoricalPackageOpsDLocked(AppOpsManager.HistoricalOps ops, int uid, XmlPullParser parser, String filterPackageName, String[] filterOpNames, int filterFlags, double filterScale) throws IOException, XmlPullParserException {
            XmlPullParser xmlPullParser = parser;
            String str = filterPackageName;
            String packageName = XmlUtils.readStringAttribute(xmlPullParser, ATTR_NAME);
            if (str == null || str.equals(packageName)) {
                int depth = parser.getDepth();
                AppOpsManager.HistoricalOps ops2 = ops;
                while (XmlUtils.nextElementWithin(xmlPullParser, depth)) {
                    if (TAG_OP.equals(parser.getName())) {
                        AppOpsManager.HistoricalOps returnedOps = readHistoricalOpDLocked(ops2, uid, packageName, parser, filterOpNames, filterFlags, filterScale);
                        if (ops2 == null) {
                            ops2 = returnedOps;
                        }
                    }
                }
                return ops2;
            }
            XmlUtils.skipCurrentTag(parser);
            return null;
        }

        private AppOpsManager.HistoricalOps readHistoricalOpDLocked(AppOpsManager.HistoricalOps ops, int uid, String packageName, XmlPullParser parser, String[] filterOpNames, int filterFlags, double filterScale) throws IOException, XmlPullParserException {
            XmlPullParser xmlPullParser = parser;
            String[] strArr = filterOpNames;
            int op = XmlUtils.readIntAttribute(xmlPullParser, ATTR_NAME);
            if (strArr == null || ArrayUtils.contains(strArr, AppOpsManager.opToPublicName(op))) {
                int depth = parser.getDepth();
                AppOpsManager.HistoricalOps ops2 = ops;
                while (XmlUtils.nextElementWithin(xmlPullParser, depth)) {
                    if (TAG_STATE.equals(parser.getName())) {
                        AppOpsManager.HistoricalOps returnedOps = readStateDLocked(ops2, uid, packageName, op, parser, filterFlags, filterScale);
                        if (ops2 == null) {
                            ops2 = returnedOps;
                        }
                    }
                }
                return ops2;
            }
            XmlUtils.skipCurrentTag(parser);
            return null;
        }

        private AppOpsManager.HistoricalOps readStateDLocked(AppOpsManager.HistoricalOps ops, int uid, String packageName, int op, XmlPullParser parser, int filterFlags, double filterScale) throws IOException {
            AppOpsManager.HistoricalOps ops2;
            long accessDuration;
            long rejectCount;
            long accessCount;
            XmlPullParser xmlPullParser = parser;
            long key = XmlUtils.readLongAttribute(xmlPullParser, ATTR_NAME);
            int flags = AppOpsManager.extractFlagsFromKey(key) & filterFlags;
            if (flags == 0) {
                return null;
            }
            int uidState = AppOpsManager.extractUidStateFromKey(key);
            long accessCount2 = XmlUtils.readLongAttribute(xmlPullParser, ATTR_ACCESS_COUNT, 0);
            if (accessCount2 > 0) {
                if (!Double.isNaN(filterScale)) {
                    accessCount = (long) AppOpsManager.HistoricalOps.round(((double) accessCount2) * filterScale);
                } else {
                    accessCount = accessCount2;
                }
                if (ops == null) {
                    ops2 = new AppOpsManager.HistoricalOps(0, 0);
                } else {
                    ops2 = ops;
                }
                ops2.increaseAccessCount(op, uid, packageName, uidState, flags, accessCount);
            } else {
                ops2 = ops;
                long j = accessCount2;
            }
            long rejectCount2 = XmlUtils.readLongAttribute(xmlPullParser, ATTR_REJECT_COUNT, 0);
            if (rejectCount2 > 0) {
                if (!Double.isNaN(filterScale)) {
                    rejectCount = (long) AppOpsManager.HistoricalOps.round(((double) rejectCount2) * filterScale);
                } else {
                    rejectCount = rejectCount2;
                }
                if (ops2 == null) {
                    ops2 = new AppOpsManager.HistoricalOps(0, 0);
                }
                ops2.increaseRejectCount(op, uid, packageName, uidState, flags, rejectCount);
            }
            long accessDuration2 = XmlUtils.readLongAttribute(xmlPullParser, ATTR_ACCESS_DURATION, 0);
            if (accessDuration2 > 0) {
                if (!Double.isNaN(filterScale)) {
                    accessDuration = (long) AppOpsManager.HistoricalOps.round(((double) accessDuration2) * filterScale);
                } else {
                    accessDuration = accessDuration2;
                }
                if (ops2 == null) {
                    ops2 = new AppOpsManager.HistoricalOps(0, 0);
                }
                ops2.increaseAccessDuration(op, uid, packageName, uidState, flags, accessDuration);
                long j2 = accessDuration;
            }
            return ops2;
        }

        private void writeHistoricalOpsDLocked(List<AppOpsManager.HistoricalOps> allOps, long intervalOverflowMillis, File file) throws IOException {
            FileOutputStream output = sHistoricalAppOpsDir.openWrite(file);
            try {
                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(output, StandardCharsets.UTF_8.name());
                serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                serializer.startDocument((String) null, true);
                serializer.startTag((String) null, TAG_HISTORY);
                serializer.attribute((String) null, ATTR_VERSION, String.valueOf(2));
                if (intervalOverflowMillis != 0) {
                    serializer.attribute((String) null, ATTR_OVERFLOW, Long.toString(intervalOverflowMillis));
                }
                if (allOps != null) {
                    int opsCount = allOps.size();
                    for (int i = 0; i < opsCount; i++) {
                        writeHistoricalOpDLocked(allOps.get(i), serializer);
                    }
                }
                serializer.endTag((String) null, TAG_HISTORY);
                serializer.endDocument();
                sHistoricalAppOpsDir.closeWrite(output);
            } catch (IOException e) {
                sHistoricalAppOpsDir.failWrite(output);
                throw e;
            }
        }

        private void writeHistoricalOpDLocked(AppOpsManager.HistoricalOps ops, XmlSerializer serializer) throws IOException {
            serializer.startTag((String) null, TAG_OPS);
            serializer.attribute((String) null, ATTR_BEGIN_TIME, Long.toString(ops.getBeginTimeMillis()));
            serializer.attribute((String) null, ATTR_END_TIME, Long.toString(ops.getEndTimeMillis()));
            int uidCount = ops.getUidCount();
            for (int i = 0; i < uidCount; i++) {
                writeHistoricalUidOpsDLocked(ops.getUidOpsAt(i), serializer);
            }
            serializer.endTag((String) null, TAG_OPS);
        }

        private void writeHistoricalUidOpsDLocked(AppOpsManager.HistoricalUidOps uidOps, XmlSerializer serializer) throws IOException {
            serializer.startTag((String) null, "uid");
            serializer.attribute((String) null, ATTR_NAME, Integer.toString(uidOps.getUid()));
            int packageCount = uidOps.getPackageCount();
            for (int i = 0; i < packageCount; i++) {
                writeHistoricalPackageOpsDLocked(uidOps.getPackageOpsAt(i), serializer);
            }
            serializer.endTag((String) null, "uid");
        }

        private void writeHistoricalPackageOpsDLocked(AppOpsManager.HistoricalPackageOps packageOps, XmlSerializer serializer) throws IOException {
            serializer.startTag((String) null, "pkg");
            serializer.attribute((String) null, ATTR_NAME, packageOps.getPackageName());
            int opCount = packageOps.getOpCount();
            for (int i = 0; i < opCount; i++) {
                writeHistoricalOpDLocked(packageOps.getOpAt(i), serializer);
            }
            serializer.endTag((String) null, "pkg");
        }

        private void writeHistoricalOpDLocked(AppOpsManager.HistoricalOp op, XmlSerializer serializer) throws IOException {
            LongSparseArray keys = op.collectKeys();
            if (keys != null && keys.size() > 0) {
                serializer.startTag((String) null, TAG_OP);
                serializer.attribute((String) null, ATTR_NAME, Integer.toString(op.getOpCode()));
                int keyCount = keys.size();
                for (int i = 0; i < keyCount; i++) {
                    writeStateOnLocked(op, keys.keyAt(i), serializer);
                }
                serializer.endTag((String) null, TAG_OP);
            }
        }

        private void writeStateOnLocked(AppOpsManager.HistoricalOp op, long key, XmlSerializer serializer) throws IOException {
            AppOpsManager.HistoricalOp historicalOp = op;
            XmlSerializer xmlSerializer = serializer;
            int uidState = AppOpsManager.extractUidStateFromKey(key);
            int flags = AppOpsManager.extractFlagsFromKey(key);
            long accessCount = historicalOp.getAccessCount(uidState, uidState, flags);
            long rejectCount = historicalOp.getRejectCount(uidState, uidState, flags);
            long accessDuration = historicalOp.getAccessDuration(uidState, uidState, flags);
            if (accessCount > 0 || rejectCount > 0 || accessDuration > 0) {
                xmlSerializer.startTag((String) null, TAG_STATE);
                xmlSerializer.attribute((String) null, ATTR_NAME, Long.toString(key));
                if (accessCount > 0) {
                    xmlSerializer.attribute((String) null, ATTR_ACCESS_COUNT, Long.toString(accessCount));
                }
                if (rejectCount > 0) {
                    xmlSerializer.attribute((String) null, ATTR_REJECT_COUNT, Long.toString(rejectCount));
                }
                if (accessDuration > 0) {
                    xmlSerializer.attribute((String) null, ATTR_ACCESS_DURATION, Long.toString(accessDuration));
                }
                xmlSerializer.endTag((String) null, TAG_STATE);
            }
        }

        private static void enforceOpsWellFormed(List<AppOpsManager.HistoricalOps> ops) {
            if (ops != null) {
                AppOpsManager.HistoricalOps current = null;
                int opsCount = ops.size();
                int i = 0;
                while (i < opsCount) {
                    AppOpsManager.HistoricalOps previous = current;
                    current = ops.get(i);
                    if (current.isEmpty()) {
                        throw new IllegalStateException("Empty ops:\n" + opsToDebugString(ops));
                    } else if (current.getEndTimeMillis() >= current.getBeginTimeMillis()) {
                        if (previous != null) {
                            if (previous.getEndTimeMillis() > current.getBeginTimeMillis()) {
                                throw new IllegalStateException("Intersecting ops:\n" + opsToDebugString(ops));
                            } else if (previous.getBeginTimeMillis() > current.getBeginTimeMillis()) {
                                throw new IllegalStateException("Non increasing ops:\n" + opsToDebugString(ops));
                            }
                        }
                        i++;
                    } else {
                        throw new IllegalStateException("Begin after end:\n" + opsToDebugString(ops));
                    }
                }
            }
        }

        private long computeGlobalIntervalBeginMillis(int depth) {
            long beginTimeMillis = 0;
            for (int i = 0; i < depth + 1; i++) {
                beginTimeMillis = (long) (((double) beginTimeMillis) + Math.pow((double) this.mIntervalCompressionMultiplier, (double) i));
            }
            return this.mBaseSnapshotInterval * beginTimeMillis;
        }

        private static AppOpsManager.HistoricalOps spliceFromEnd(AppOpsManager.HistoricalOps ops, double spliceRatio) {
            return ops.spliceFromEnd(spliceRatio);
        }

        /* access modifiers changed from: private */
        public static AppOpsManager.HistoricalOps spliceFromBeginning(AppOpsManager.HistoricalOps ops, double spliceRatio) {
            return ops.spliceFromBeginning(spliceRatio);
        }

        private static void normalizeSnapshotForSlotDuration(List<AppOpsManager.HistoricalOps> ops, long slotDurationMillis) {
            List<AppOpsManager.HistoricalOps> list = ops;
            int processedIdx = ops.size() - 1;
            while (processedIdx >= 0) {
                AppOpsManager.HistoricalOps processedOp = ops.get(processedIdx);
                long slotBeginTimeMillis = Math.max(processedOp.getEndTimeMillis() - slotDurationMillis, 0);
                for (int candidateIdx = processedIdx - 1; candidateIdx >= 0; candidateIdx--) {
                    AppOpsManager.HistoricalOps candidateOp = ops.get(candidateIdx);
                    long candidateSlotIntersectionMillis = candidateOp.getEndTimeMillis() - Math.min(slotBeginTimeMillis, processedOp.getBeginTimeMillis());
                    if (candidateSlotIntersectionMillis <= 0) {
                        break;
                    }
                    float candidateSplitRatio = ((float) candidateSlotIntersectionMillis) / ((float) candidateOp.getDurationMillis());
                    if (Float.compare(candidateSplitRatio, 1.0f) >= 0) {
                        ops.remove(candidateIdx);
                        processedIdx--;
                        processedOp.merge(candidateOp);
                    } else {
                        AppOpsManager.HistoricalOps endSplice = spliceFromEnd(candidateOp, (double) candidateSplitRatio);
                        if (endSplice != null) {
                            processedOp.merge(endSplice);
                        }
                        if (candidateOp.isEmpty()) {
                            ops.remove(candidateIdx);
                            processedIdx--;
                        }
                    }
                }
                processedIdx--;
            }
        }

        private static String opsToDebugString(List<AppOpsManager.HistoricalOps> ops) {
            StringBuilder builder = new StringBuilder();
            int opCount = ops.size();
            for (int i = 0; i < opCount; i++) {
                builder.append("  ");
                builder.append(ops.get(i));
                if (i < opCount - 1) {
                    builder.append(10);
                }
            }
            return builder.toString();
        }

        private static Set<String> getHistoricalFileNames(File historyDir) {
            File[] files = historyDir.listFiles();
            if (files == null) {
                return Collections.emptySet();
            }
            ArraySet<String> fileNames = new ArraySet<>(files.length);
            for (File file : files) {
                fileNames.add(file.getName());
            }
            return fileNames;
        }
    }

    private static class HistoricalFilesInvariant {
        private final List<File> mBeginFiles = new ArrayList();

        private HistoricalFilesInvariant() {
        }

        public void startTracking(File folder) {
            File[] files = folder.listFiles();
            if (files != null) {
                Collections.addAll(this.mBeginFiles, files);
            }
        }

        public void stopTracking(File folder) {
            List<File> endFiles = new ArrayList<>();
            File[] files = folder.listFiles();
            if (files != null) {
                Collections.addAll(endFiles, files);
            }
            if (getOldestFileOffsetMillis(endFiles) < getOldestFileOffsetMillis(this.mBeginFiles)) {
                String message = "History loss detected!\nold files: " + this.mBeginFiles;
                HistoricalRegistry.wtf(message, (Throwable) null, folder);
                throw new IllegalStateException(message);
            }
        }

        private static long getOldestFileOffsetMillis(List<File> files) {
            if (files.isEmpty()) {
                return 0;
            }
            String longestName = files.get(0).getName();
            int fileCount = files.size();
            for (int i = 1; i < fileCount; i++) {
                File file = files.get(i);
                if (file.getName().length() > longestName.length()) {
                    longestName = file.getName();
                }
            }
            return Long.parseLong(longestName.replace(HistoricalRegistry.HISTORY_FILE_SUFFIX, ""));
        }
    }

    private final class StringDumpVisitor implements AppOpsManager.HistoricalOpsVisitor {
        private final Date mDate = new Date();
        private final SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        private final String mEntryPrefix;
        private final int mFilterOp;
        private final String mFilterPackage;
        private final int mFilterUid;
        private final long mNow = System.currentTimeMillis();
        private final String mOpsPrefix;
        private final String mPackagePrefix;
        private final String mUidPrefix;
        private final String mUidStatePrefix;
        private final PrintWriter mWriter;

        StringDumpVisitor(String prefix, PrintWriter writer, int filterUid, String filterPackage, int filterOp) {
            this.mOpsPrefix = prefix + "  ";
            this.mUidPrefix = this.mOpsPrefix + "  ";
            this.mPackagePrefix = this.mUidPrefix + "  ";
            this.mEntryPrefix = this.mPackagePrefix + "  ";
            this.mUidStatePrefix = this.mEntryPrefix + "  ";
            this.mWriter = writer;
            this.mFilterUid = filterUid;
            this.mFilterPackage = filterPackage;
            this.mFilterOp = filterOp;
        }

        public void visitHistoricalOps(AppOpsManager.HistoricalOps ops) {
            this.mWriter.println();
            this.mWriter.print(this.mOpsPrefix);
            this.mWriter.println("snapshot:");
            this.mWriter.print(this.mUidPrefix);
            this.mWriter.print("begin = ");
            this.mDate.setTime(ops.getBeginTimeMillis());
            this.mWriter.print(this.mDateFormatter.format(this.mDate));
            this.mWriter.print("  (");
            TimeUtils.formatDuration(ops.getBeginTimeMillis() - this.mNow, this.mWriter);
            this.mWriter.println(")");
            this.mWriter.print(this.mUidPrefix);
            this.mWriter.print("end = ");
            this.mDate.setTime(ops.getEndTimeMillis());
            this.mWriter.print(this.mDateFormatter.format(this.mDate));
            this.mWriter.print("  (");
            TimeUtils.formatDuration(ops.getEndTimeMillis() - this.mNow, this.mWriter);
            this.mWriter.println(")");
        }

        public void visitHistoricalUidOps(AppOpsManager.HistoricalUidOps ops) {
            int i = this.mFilterUid;
            if (i == -1 || i == ops.getUid()) {
                this.mWriter.println();
                this.mWriter.print(this.mUidPrefix);
                this.mWriter.print("Uid ");
                UserHandle.formatUid(this.mWriter, ops.getUid());
                this.mWriter.println(":");
            }
        }

        public void visitHistoricalPackageOps(AppOpsManager.HistoricalPackageOps ops) {
            String str = this.mFilterPackage;
            if (str == null || str.equals(ops.getPackageName())) {
                this.mWriter.print(this.mPackagePrefix);
                this.mWriter.print("Package ");
                this.mWriter.print(ops.getPackageName());
                this.mWriter.println(":");
            }
        }

        public void visitHistoricalOp(AppOpsManager.HistoricalOp ops) {
            int keyCount;
            AppOpsManager.HistoricalOp historicalOp = ops;
            int i = this.mFilterOp;
            if (i == -1 || i == ops.getOpCode()) {
                this.mWriter.print(this.mEntryPrefix);
                this.mWriter.print(AppOpsManager.opToName(ops.getOpCode()));
                this.mWriter.println(":");
                LongSparseArray keys = ops.collectKeys();
                int keyCount2 = keys.size();
                int i2 = 0;
                while (i2 < keyCount2) {
                    long key = keys.keyAt(i2);
                    int uidState = AppOpsManager.extractUidStateFromKey(key);
                    int flags = AppOpsManager.extractFlagsFromKey(key);
                    boolean printedUidState = false;
                    long accessCount = historicalOp.getAccessCount(uidState, uidState, flags);
                    if (accessCount > 0) {
                        if (0 == 0) {
                            this.mWriter.print(this.mUidStatePrefix);
                            this.mWriter.print(AppOpsManager.keyToString(key));
                            this.mWriter.print(" = ");
                            printedUidState = true;
                        }
                        this.mWriter.print("access=");
                        this.mWriter.print(accessCount);
                    }
                    long rejectCount = historicalOp.getRejectCount(uidState, uidState, flags);
                    LongSparseArray keys2 = keys;
                    if (rejectCount > 0) {
                        if (!printedUidState) {
                            keyCount = keyCount2;
                            this.mWriter.print(this.mUidStatePrefix);
                            this.mWriter.print(AppOpsManager.keyToString(key));
                            this.mWriter.print(" = ");
                            printedUidState = true;
                        } else {
                            keyCount = keyCount2;
                            this.mWriter.print(", ");
                        }
                        this.mWriter.print("reject=");
                        this.mWriter.print(rejectCount);
                    } else {
                        keyCount = keyCount2;
                    }
                    long j = accessCount;
                    long accessCount2 = historicalOp.getAccessDuration(uidState, uidState, flags);
                    if (accessCount2 > 0) {
                        if (!printedUidState) {
                            this.mWriter.print(this.mUidStatePrefix);
                            this.mWriter.print(AppOpsManager.keyToString(key));
                            this.mWriter.print(" = ");
                            printedUidState = true;
                        } else {
                            this.mWriter.print(", ");
                        }
                        this.mWriter.print("duration=");
                        TimeUtils.formatDuration(accessCount2, this.mWriter);
                    }
                    if (printedUidState) {
                        this.mWriter.println("");
                    }
                    i2++;
                    keys = keys2;
                    keyCount2 = keyCount;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0082, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x008b, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void wtf(java.lang.String r5, java.lang.Throwable r6, java.io.File r7) {
        /*
            java.lang.String r0 = LOG_TAG
            android.util.Slog.wtf(r0, r5, r6)
            boolean r0 = KEEP_WTF_LOG
            if (r0 == 0) goto L_0x008e
            java.io.File r0 = new java.io.File     // Catch:{ IOException -> 0x008d }
            java.io.File r1 = new java.io.File     // Catch:{ IOException -> 0x008d }
            java.io.File r2 = android.os.Environment.getDataSystemDirectory()     // Catch:{ IOException -> 0x008d }
            java.lang.String r3 = "appops"
            r1.<init>(r2, r3)     // Catch:{ IOException -> 0x008d }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x008d }
            r2.<init>()     // Catch:{ IOException -> 0x008d }
            java.lang.String r3 = "wtf"
            r2.append(r3)     // Catch:{ IOException -> 0x008d }
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ IOException -> 0x008d }
            java.lang.String r3 = android.util.TimeUtils.formatForLogging(r3)     // Catch:{ IOException -> 0x008d }
            r2.append(r3)     // Catch:{ IOException -> 0x008d }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x008d }
            r0.<init>(r1, r2)     // Catch:{ IOException -> 0x008d }
            boolean r1 = r0.createNewFile()     // Catch:{ IOException -> 0x008d }
            if (r1 == 0) goto L_0x008c
            java.io.PrintWriter r1 = new java.io.PrintWriter     // Catch:{ IOException -> 0x008d }
            r1.<init>(r0)     // Catch:{ IOException -> 0x008d }
            r2 = 10
            if (r6 == 0) goto L_0x004d
            java.io.PrintWriter r3 = r1.append(r2)     // Catch:{ all -> 0x0080 }
            java.lang.String r4 = r6.toString()     // Catch:{ all -> 0x0080 }
            r3.append(r4)     // Catch:{ all -> 0x0080 }
        L_0x004d:
            java.io.PrintWriter r3 = r1.append(r2)     // Catch:{ all -> 0x0080 }
            java.lang.String r2 = android.os.Debug.getCallers(r2)     // Catch:{ all -> 0x0080 }
            r3.append(r2)     // Catch:{ all -> 0x0080 }
            if (r7 == 0) goto L_0x0077
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0080 }
            r2.<init>()     // Catch:{ all -> 0x0080 }
            java.lang.String r3 = "\nfiles: "
            r2.append(r3)     // Catch:{ all -> 0x0080 }
            java.io.File[] r3 = r7.listFiles()     // Catch:{ all -> 0x0080 }
            java.lang.String r3 = java.util.Arrays.toString(r3)     // Catch:{ all -> 0x0080 }
            r2.append(r3)     // Catch:{ all -> 0x0080 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0080 }
            r1.append(r2)     // Catch:{ all -> 0x0080 }
            goto L_0x007c
        L_0x0077:
            java.lang.String r2 = "\nfiles: none"
            r1.append(r2)     // Catch:{ all -> 0x0080 }
        L_0x007c:
            r1.close()     // Catch:{ IOException -> 0x008d }
            goto L_0x008c
        L_0x0080:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0082 }
        L_0x0082:
            r3 = move-exception
            r1.close()     // Catch:{ all -> 0x0087 }
            goto L_0x008b
        L_0x0087:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ IOException -> 0x008d }
        L_0x008b:
            throw r3     // Catch:{ IOException -> 0x008d }
        L_0x008c:
            goto L_0x008e
        L_0x008d:
            r0 = move-exception
        L_0x008e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.HistoricalRegistry.wtf(java.lang.String, java.lang.Throwable, java.io.File):void");
    }
}
