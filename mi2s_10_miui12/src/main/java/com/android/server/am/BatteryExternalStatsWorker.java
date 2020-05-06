package com.android.server.am;

import android.content.Context;
import android.net.wifi.IWifiManager;
import android.net.wifi.WifiActivityEnergyInfo;
import android.os.Parcelable;
import android.os.Process;
import android.os.SynchronousResultReceiver;
import android.os.SystemClock;
import android.os.ThreadLocalWorkSource;
import android.telephony.TelephonyManager;
import android.util.IntArray;
import android.util.Slog;
import android.util.StatsLog;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BatteryStatsImpl;
import com.android.server.stats.StatsCompanionService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import libcore.util.EmptyArray;

class BatteryExternalStatsWorker implements BatteryStatsImpl.ExternalStatsSync {
    private static final boolean DEBUG = false;
    private static final long EXTERNAL_STATS_SYNC_TIMEOUT_MILLIS = 2000;
    private static final long MAX_WIFI_STATS_SAMPLE_ERROR_MILLIS = 750;
    private static final String TAG = "BatteryExternalStatsWorker";
    @GuardedBy({"this"})
    private Future<?> mBatteryLevelSync;
    private final Context mContext;
    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public Future<?> mCurrentFuture = null;
    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public String mCurrentReason = null;
    private final ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor($$Lambda$BatteryExternalStatsWorker$ML8sXrbYk0MflPvsY2cfCYlcU0w.INSTANCE);
    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public long mLastCollectionTimeStamp;
    @GuardedBy({"mWorkerLock"})
    private WifiActivityEnergyInfo mLastInfo = new WifiActivityEnergyInfo(0, 0, 0, new long[]{0}, 0, 0, 0, 0);
    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public boolean mOnBattery;
    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public boolean mOnBatteryScreenOff;
    /* access modifiers changed from: private */
    public final BatteryStatsImpl mStats;
    private final Runnable mSyncTask = new Runnable() {
        /* Debug info: failed to restart local var, previous not found, register: 13 */
        public void run() {
            int updateFlags;
            String reason;
            int[] uidsToRemove;
            boolean onBattery;
            boolean onBatteryScreenOff;
            boolean useLatestStates;
            synchronized (BatteryExternalStatsWorker.this) {
                updateFlags = BatteryExternalStatsWorker.this.mUpdateFlags;
                reason = BatteryExternalStatsWorker.this.mCurrentReason;
                uidsToRemove = BatteryExternalStatsWorker.this.mUidsToRemove.size() > 0 ? BatteryExternalStatsWorker.this.mUidsToRemove.toArray() : EmptyArray.INT;
                onBattery = BatteryExternalStatsWorker.this.mOnBattery;
                onBatteryScreenOff = BatteryExternalStatsWorker.this.mOnBatteryScreenOff;
                useLatestStates = BatteryExternalStatsWorker.this.mUseLatestStates;
                int unused = BatteryExternalStatsWorker.this.mUpdateFlags = 0;
                String unused2 = BatteryExternalStatsWorker.this.mCurrentReason = null;
                BatteryExternalStatsWorker.this.mUidsToRemove.clear();
                Future unused3 = BatteryExternalStatsWorker.this.mCurrentFuture = null;
                boolean unused4 = BatteryExternalStatsWorker.this.mUseLatestStates = true;
                if ((updateFlags & 31) != 0) {
                    BatteryExternalStatsWorker.this.cancelSyncDueToBatteryLevelChangeLocked();
                }
                if ((updateFlags & 1) != 0) {
                    BatteryExternalStatsWorker.this.cancelCpuSyncDueToWakelockChange();
                }
            }
            try {
                synchronized (BatteryExternalStatsWorker.this.mWorkerLock) {
                    BatteryExternalStatsWorker.this.updateExternalStatsLocked(reason, updateFlags, onBattery, onBatteryScreenOff, useLatestStates);
                }
                if ((updateFlags & 1) != 0) {
                    BatteryExternalStatsWorker.this.mStats.copyFromAllUidsCpuTimes();
                }
                synchronized (BatteryExternalStatsWorker.this.mStats) {
                    for (int uid : uidsToRemove) {
                        StatsLog.write(43, -1, uid, 0);
                        BatteryExternalStatsWorker.this.mStats.removeIsolatedUidLocked(uid);
                    }
                    BatteryExternalStatsWorker.this.mStats.clearPendingRemovedUids();
                }
            } catch (Exception e) {
                Slog.wtf(BatteryExternalStatsWorker.TAG, "Error updating external stats: ", e);
            }
            synchronized (BatteryExternalStatsWorker.this) {
                long unused5 = BatteryExternalStatsWorker.this.mLastCollectionTimeStamp = SystemClock.elapsedRealtime();
            }
        }
    };
    @GuardedBy({"mWorkerLock"})
    private TelephonyManager mTelephony = null;
    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public final IntArray mUidsToRemove = new IntArray();
    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public int mUpdateFlags = 0;
    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public boolean mUseLatestStates = true;
    @GuardedBy({"this"})
    private Future<?> mWakelockChangesUpdate;
    @GuardedBy({"mWorkerLock"})
    private IWifiManager mWifiManager = null;
    /* access modifiers changed from: private */
    public final Object mWorkerLock = new Object();
    private final Runnable mWriteTask = new Runnable() {
        public void run() {
            synchronized (BatteryExternalStatsWorker.this.mStats) {
                BatteryExternalStatsWorker.this.mStats.writeAsyncLocked();
            }
        }
    };

    static /* synthetic */ Thread lambda$new$1(Runnable r) {
        Thread t = new Thread(new Runnable(r) {
            private final /* synthetic */ Runnable f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                BatteryExternalStatsWorker.lambda$new$0(this.f$0);
            }
        }, "batterystats-worker");
        t.setPriority(5);
        return t;
    }

    static /* synthetic */ void lambda$new$0(Runnable r) {
        ThreadLocalWorkSource.setUid(Process.myUid());
        r.run();
    }

    BatteryExternalStatsWorker(Context context, BatteryStatsImpl stats) {
        this.mContext = context;
        this.mStats = stats;
    }

    public synchronized Future<?> scheduleSync(String reason, int flags) {
        return scheduleSyncLocked(reason, flags);
    }

    public synchronized Future<?> scheduleCpuSyncDueToRemovedUid(int uid) {
        this.mUidsToRemove.add(uid);
        return scheduleSyncLocked("remove-uid", 1);
    }

    public synchronized Future<?> scheduleCpuSyncDueToSettingChange() {
        return scheduleSyncLocked("setting-change", 1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0016, code lost:
        if (r5.mExecutorService.isShutdown() != false) goto L_0x0036;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0018, code lost:
        r0 = r5.mExecutorService.schedule(com.android.internal.util.function.pooled.PooledLambda.obtainRunnable(com.android.server.am.$$Lambda$cC4f0pNQX9_D9f8AXLmKk2sArGY.INSTANCE, r5.mStats, java.lang.Boolean.valueOf(r6), java.lang.Boolean.valueOf(r7)).recycleOnUse(), r8, java.util.concurrent.TimeUnit.MILLISECONDS);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0034, code lost:
        monitor-exit(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0035, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0036, code lost:
        monitor-exit(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0037, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000f, code lost:
        monitor-enter(r5);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.concurrent.Future<?> scheduleReadProcStateCpuTimes(boolean r6, boolean r7, long r8) {
        /*
            r5 = this;
            com.android.internal.os.BatteryStatsImpl r0 = r5.mStats
            monitor-enter(r0)
            com.android.internal.os.BatteryStatsImpl r1 = r5.mStats     // Catch:{ all -> 0x003b }
            boolean r1 = r1.trackPerProcStateCpuTimes()     // Catch:{ all -> 0x003b }
            r2 = 0
            if (r1 != 0) goto L_0x000e
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            return r2
        L_0x000e:
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            monitor-enter(r5)
            java.util.concurrent.ScheduledExecutorService r0 = r5.mExecutorService     // Catch:{ all -> 0x0038 }
            boolean r0 = r0.isShutdown()     // Catch:{ all -> 0x0038 }
            if (r0 != 0) goto L_0x0036
            java.util.concurrent.ScheduledExecutorService r0 = r5.mExecutorService     // Catch:{ all -> 0x0038 }
            com.android.server.am.-$$Lambda$cC4f0pNQX9_D9f8AXLmKk2sArGY r1 = com.android.server.am.$$Lambda$cC4f0pNQX9_D9f8AXLmKk2sArGY.INSTANCE     // Catch:{ all -> 0x0038 }
            com.android.internal.os.BatteryStatsImpl r2 = r5.mStats     // Catch:{ all -> 0x0038 }
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r6)     // Catch:{ all -> 0x0038 }
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r7)     // Catch:{ all -> 0x0038 }
            com.android.internal.util.function.pooled.PooledRunnable r1 = com.android.internal.util.function.pooled.PooledLambda.obtainRunnable(r1, r2, r3, r4)     // Catch:{ all -> 0x0038 }
            com.android.internal.util.function.pooled.PooledRunnable r1 = r1.recycleOnUse()     // Catch:{ all -> 0x0038 }
            java.util.concurrent.TimeUnit r2 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ all -> 0x0038 }
            java.util.concurrent.ScheduledFuture r0 = r0.schedule(r1, r8, r2)     // Catch:{ all -> 0x0038 }
            monitor-exit(r5)     // Catch:{ all -> 0x0038 }
            return r0
        L_0x0036:
            monitor-exit(r5)     // Catch:{ all -> 0x0038 }
            return r2
        L_0x0038:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0038 }
            throw r0
        L_0x003b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.BatteryExternalStatsWorker.scheduleReadProcStateCpuTimes(boolean, boolean, long):java.util.concurrent.Future");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0016, code lost:
        if (r5.mExecutorService.isShutdown() != false) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0018, code lost:
        r0 = r5.mExecutorService.submit(com.android.internal.util.function.pooled.PooledLambda.obtainRunnable(com.android.server.am.$$Lambda$7toxTvZDSEytL0rCkoEfGilPDWM.INSTANCE, r5.mStats, java.lang.Boolean.valueOf(r6), java.lang.Boolean.valueOf(r7)).recycleOnUse());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0032, code lost:
        monitor-exit(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0033, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0034, code lost:
        monitor-exit(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0035, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000f, code lost:
        monitor-enter(r5);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.concurrent.Future<?> scheduleCopyFromAllUidsCpuTimes(boolean r6, boolean r7) {
        /*
            r5 = this;
            com.android.internal.os.BatteryStatsImpl r0 = r5.mStats
            monitor-enter(r0)
            com.android.internal.os.BatteryStatsImpl r1 = r5.mStats     // Catch:{ all -> 0x0039 }
            boolean r1 = r1.trackPerProcStateCpuTimes()     // Catch:{ all -> 0x0039 }
            r2 = 0
            if (r1 != 0) goto L_0x000e
            monitor-exit(r0)     // Catch:{ all -> 0x0039 }
            return r2
        L_0x000e:
            monitor-exit(r0)     // Catch:{ all -> 0x0039 }
            monitor-enter(r5)
            java.util.concurrent.ScheduledExecutorService r0 = r5.mExecutorService     // Catch:{ all -> 0x0036 }
            boolean r0 = r0.isShutdown()     // Catch:{ all -> 0x0036 }
            if (r0 != 0) goto L_0x0034
            java.util.concurrent.ScheduledExecutorService r0 = r5.mExecutorService     // Catch:{ all -> 0x0036 }
            com.android.server.am.-$$Lambda$7toxTvZDSEytL0rCkoEfGilPDWM r1 = com.android.server.am.$$Lambda$7toxTvZDSEytL0rCkoEfGilPDWM.INSTANCE     // Catch:{ all -> 0x0036 }
            com.android.internal.os.BatteryStatsImpl r2 = r5.mStats     // Catch:{ all -> 0x0036 }
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r6)     // Catch:{ all -> 0x0036 }
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r7)     // Catch:{ all -> 0x0036 }
            com.android.internal.util.function.pooled.PooledRunnable r1 = com.android.internal.util.function.pooled.PooledLambda.obtainRunnable(r1, r2, r3, r4)     // Catch:{ all -> 0x0036 }
            com.android.internal.util.function.pooled.PooledRunnable r1 = r1.recycleOnUse()     // Catch:{ all -> 0x0036 }
            java.util.concurrent.Future r0 = r0.submit(r1)     // Catch:{ all -> 0x0036 }
            monitor-exit(r5)     // Catch:{ all -> 0x0036 }
            return r0
        L_0x0034:
            monitor-exit(r5)     // Catch:{ all -> 0x0036 }
            return r2
        L_0x0036:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0036 }
            throw r0
        L_0x0039:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0039 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.BatteryExternalStatsWorker.scheduleCopyFromAllUidsCpuTimes(boolean, boolean):java.util.concurrent.Future");
    }

    public Future<?> scheduleCpuSyncDueToScreenStateChange(boolean onBattery, boolean onBatteryScreenOff) {
        Future<?> scheduleSyncLocked;
        synchronized (this) {
            if (this.mCurrentFuture == null || (this.mUpdateFlags & 1) == 0) {
                this.mOnBattery = onBattery;
                this.mOnBatteryScreenOff = onBatteryScreenOff;
                this.mUseLatestStates = false;
            }
            scheduleSyncLocked = scheduleSyncLocked("screen-state", 1);
        }
        return scheduleSyncLocked;
    }

    public Future<?> scheduleCpuSyncDueToWakelockChange(long delayMillis) {
        Future<?> future;
        synchronized (this) {
            this.mWakelockChangesUpdate = scheduleDelayedSyncLocked(this.mWakelockChangesUpdate, new Runnable() {
                public final void run() {
                    BatteryExternalStatsWorker.this.lambda$scheduleCpuSyncDueToWakelockChange$3$BatteryExternalStatsWorker();
                }
            }, delayMillis);
            future = this.mWakelockChangesUpdate;
        }
        return future;
    }

    public /* synthetic */ void lambda$scheduleCpuSyncDueToWakelockChange$3$BatteryExternalStatsWorker() {
        scheduleSync("wakelock-change", 1);
        scheduleRunnable(new Runnable() {
            public final void run() {
                BatteryExternalStatsWorker.this.lambda$scheduleCpuSyncDueToWakelockChange$2$BatteryExternalStatsWorker();
            }
        });
    }

    public /* synthetic */ void lambda$scheduleCpuSyncDueToWakelockChange$2$BatteryExternalStatsWorker() {
        this.mStats.postBatteryNeedsCpuUpdateMsg();
    }

    public void cancelCpuSyncDueToWakelockChange() {
        synchronized (this) {
            if (this.mWakelockChangesUpdate != null) {
                this.mWakelockChangesUpdate.cancel(false);
                this.mWakelockChangesUpdate = null;
            }
        }
    }

    public Future<?> scheduleSyncDueToBatteryLevelChange(long delayMillis) {
        Future<?> future;
        synchronized (this) {
            this.mBatteryLevelSync = scheduleDelayedSyncLocked(this.mBatteryLevelSync, new Runnable() {
                public final void run() {
                    BatteryExternalStatsWorker.this.lambda$scheduleSyncDueToBatteryLevelChange$4$BatteryExternalStatsWorker();
                }
            }, delayMillis);
            future = this.mBatteryLevelSync;
        }
        return future;
    }

    public /* synthetic */ void lambda$scheduleSyncDueToBatteryLevelChange$4$BatteryExternalStatsWorker() {
        scheduleSync("battery-level", 31);
    }

    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public void cancelSyncDueToBatteryLevelChangeLocked() {
        Future<?> future = this.mBatteryLevelSync;
        if (future != null) {
            future.cancel(false);
            this.mBatteryLevelSync = null;
        }
    }

    @GuardedBy({"this"})
    private Future<?> scheduleDelayedSyncLocked(Future<?> lastScheduledSync, Runnable syncRunnable, long delayMillis) {
        if (this.mExecutorService.isShutdown()) {
            return CompletableFuture.failedFuture(new IllegalStateException("worker shutdown"));
        }
        if (lastScheduledSync != null) {
            if (delayMillis != 0) {
                return lastScheduledSync;
            }
            lastScheduledSync.cancel(false);
        }
        return this.mExecutorService.schedule(syncRunnable, delayMillis, TimeUnit.MILLISECONDS);
    }

    public synchronized Future<?> scheduleWrite() {
        if (this.mExecutorService.isShutdown()) {
            return CompletableFuture.failedFuture(new IllegalStateException("worker shutdown"));
        }
        scheduleSyncLocked("write", 31);
        return this.mExecutorService.submit(this.mWriteTask);
    }

    public synchronized void scheduleRunnable(Runnable runnable) {
        if (!this.mExecutorService.isShutdown()) {
            this.mExecutorService.submit(runnable);
        }
    }

    public void shutdown() {
        this.mExecutorService.shutdownNow();
    }

    @GuardedBy({"this"})
    private Future<?> scheduleSyncLocked(String reason, int flags) {
        if (this.mExecutorService.isShutdown()) {
            return CompletableFuture.failedFuture(new IllegalStateException("worker shutdown"));
        }
        if (this.mCurrentFuture == null) {
            this.mUpdateFlags = flags;
            this.mCurrentReason = reason;
            this.mCurrentFuture = this.mExecutorService.submit(this.mSyncTask);
        }
        this.mUpdateFlags |= flags;
        return this.mCurrentFuture;
    }

    /* access modifiers changed from: package-private */
    public long getLastCollectionTimeStamp() {
        long j;
        synchronized (this) {
            j = this.mLastCollectionTimeStamp;
        }
        return j;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0125, code lost:
        if (r6 == null) goto L_0x014e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x012b, code lost:
        if (r6.isValid() == false) goto L_0x0137;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x012d, code lost:
        r1.mStats.updateWifiState(extractDeltaLocked(r6));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0137, code lost:
        android.util.Slog.w(TAG, "wifi info is invalid: " + r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x014e, code lost:
        if (r8 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x0154, code lost:
        if (r8.isValid() == false) goto L_0x015c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0156, code lost:
        r1.mStats.updateMobileRadioState(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x015c, code lost:
        android.util.Slog.w(TAG, "modem info is invalid: " + r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:?, code lost:
        return;
     */
    @com.android.internal.annotations.GuardedBy({"mWorkerLock"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateExternalStatsLocked(java.lang.String r19, int r20, boolean r21, boolean r22, boolean r23) {
        /*
            r18 = this;
            r1 = r18
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r0 = r20 & 2
            if (r0 == 0) goto L_0x0050
            android.net.wifi.IWifiManager r0 = r1.mWifiManager
            if (r0 != 0) goto L_0x001b
            java.lang.String r0 = "wifi"
            android.os.IBinder r0 = android.os.ServiceManager.getService(r0)
            android.net.wifi.IWifiManager r0 = android.net.wifi.IWifiManager.Stub.asInterface(r0)
            r1.mWifiManager = r0
        L_0x001b:
            android.net.wifi.IWifiManager r0 = r1.mWifiManager
            if (r0 == 0) goto L_0x0040
            long r6 = r0.getSupportedFeatures()     // Catch:{ RemoteException -> 0x003d }
            r8 = 65536(0x10000, double:3.2379E-319)
            long r6 = r6 & r8
            r8 = 0
            int r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r0 == 0) goto L_0x003b
            android.os.SynchronousResultReceiver r0 = new android.os.SynchronousResultReceiver     // Catch:{ RemoteException -> 0x003d }
            java.lang.String r6 = "wifi"
            r0.<init>(r6)     // Catch:{ RemoteException -> 0x003d }
            r2 = r0
            android.net.wifi.IWifiManager r0 = r1.mWifiManager     // Catch:{ RemoteException -> 0x003d }
            r0.requestActivityInfo(r2)     // Catch:{ RemoteException -> 0x003d }
        L_0x003b:
            r6 = r2
            goto L_0x0041
        L_0x003d:
            r0 = move-exception
            r6 = r2
            goto L_0x0041
        L_0x0040:
            r6 = r2
        L_0x0041:
            com.android.internal.os.BatteryStatsImpl r7 = r1.mStats
            monitor-enter(r7)
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x004d }
            r0.updateRailStatsLocked()     // Catch:{ all -> 0x004d }
            monitor-exit(r7)     // Catch:{ all -> 0x004d }
            r5 = 1
            r2 = r6
            goto L_0x0050
        L_0x004d:
            r0 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x004d }
            throw r0
        L_0x0050:
            r0 = r20 & 8
            if (r0 == 0) goto L_0x0065
            android.bluetooth.BluetoothAdapter r0 = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
            if (r0 == 0) goto L_0x0065
            android.os.SynchronousResultReceiver r6 = new android.os.SynchronousResultReceiver
            java.lang.String r7 = "bluetooth"
            r6.<init>(r7)
            r3 = r6
            r0.requestControllerActivityEnergyInfo(r3)
        L_0x0065:
            r0 = r20 & 4
            if (r0 == 0) goto L_0x0096
            android.telephony.TelephonyManager r0 = r1.mTelephony
            if (r0 != 0) goto L_0x0075
            android.content.Context r0 = r1.mContext
            android.telephony.TelephonyManager r0 = android.telephony.TelephonyManager.from(r0)
            r1.mTelephony = r0
        L_0x0075:
            android.telephony.TelephonyManager r0 = r1.mTelephony
            if (r0 == 0) goto L_0x0087
            android.os.SynchronousResultReceiver r0 = new android.os.SynchronousResultReceiver
            java.lang.String r6 = "telephony"
            r0.<init>(r6)
            r4 = r0
            android.telephony.TelephonyManager r0 = r1.mTelephony
            r0.requestModemActivityInfo(r4)
        L_0x0087:
            if (r5 != 0) goto L_0x0096
            com.android.internal.os.BatteryStatsImpl r6 = r1.mStats
            monitor-enter(r6)
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x0093 }
            r0.updateRailStatsLocked()     // Catch:{ all -> 0x0093 }
            monitor-exit(r6)     // Catch:{ all -> 0x0093 }
            goto L_0x0096
        L_0x0093:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0093 }
            throw r0
        L_0x0096:
            android.os.Parcelable r0 = awaitControllerInfo(r2)
            r6 = r0
            android.net.wifi.WifiActivityEnergyInfo r6 = (android.net.wifi.WifiActivityEnergyInfo) r6
            android.os.Parcelable r0 = awaitControllerInfo(r3)
            r7 = r0
            android.bluetooth.BluetoothActivityEnergyInfo r7 = (android.bluetooth.BluetoothActivityEnergyInfo) r7
            android.os.Parcelable r0 = awaitControllerInfo(r4)
            r8 = r0
            android.telephony.ModemActivityInfo r8 = (android.telephony.ModemActivityInfo) r8
            com.android.internal.os.BatteryStatsImpl r9 = r1.mStats
            monitor-enter(r9)
            com.android.internal.os.BatteryStatsImpl r10 = r1.mStats     // Catch:{ all -> 0x0174 }
            long r11 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0174 }
            long r13 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x0174 }
            r15 = 14
            r17 = 0
            r16 = r19
            r10.addHistoryEventLocked(r11, r13, r15, r16, r17)     // Catch:{ all -> 0x0174 }
            r0 = r20 & 1
            if (r0 == 0) goto L_0x00e5
            if (r23 == 0) goto L_0x00db
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x0174 }
            boolean r0 = r0.isOnBatteryLocked()     // Catch:{ all -> 0x0174 }
            r10 = r0
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x00d6 }
            boolean r0 = r0.isOnBatteryScreenOffLocked()     // Catch:{ all -> 0x00d6 }
            r11 = r0
            goto L_0x00df
        L_0x00d6:
            r0 = move-exception
            r11 = r22
            goto L_0x0179
        L_0x00db:
            r10 = r21
            r11 = r22
        L_0x00df:
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x017b }
            r0.updateCpuTimeLocked(r10, r11)     // Catch:{ all -> 0x017b }
            goto L_0x00e9
        L_0x00e5:
            r10 = r21
            r11 = r22
        L_0x00e9:
            r0 = r20 & 31
            if (r0 == 0) goto L_0x00f7
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x017b }
            r0.updateKernelWakelocksLocked()     // Catch:{ all -> 0x017b }
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x017b }
            r0.updateKernelMemoryBandwidthLocked()     // Catch:{ all -> 0x017b }
        L_0x00f7:
            r0 = r20 & 16
            if (r0 == 0) goto L_0x0100
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x017b }
            r0.updateRpmStatsLocked()     // Catch:{ all -> 0x017b }
        L_0x0100:
            if (r7 == 0) goto L_0x0124
            boolean r0 = r7.isValid()     // Catch:{ all -> 0x017b }
            if (r0 == 0) goto L_0x010e
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x017b }
            r0.updateBluetoothStateLocked(r7)     // Catch:{ all -> 0x017b }
            goto L_0x0124
        L_0x010e:
            java.lang.String r0 = "BatteryExternalStatsWorker"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x017b }
            r12.<init>()     // Catch:{ all -> 0x017b }
            java.lang.String r13 = "bluetooth info is invalid: "
            r12.append(r13)     // Catch:{ all -> 0x017b }
            r12.append(r7)     // Catch:{ all -> 0x017b }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x017b }
            android.util.Slog.w(r0, r12)     // Catch:{ all -> 0x017b }
        L_0x0124:
            monitor-exit(r9)     // Catch:{ all -> 0x017b }
            if (r6 == 0) goto L_0x014e
            boolean r0 = r6.isValid()
            if (r0 == 0) goto L_0x0137
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats
            android.net.wifi.WifiActivityEnergyInfo r9 = r1.extractDeltaLocked(r6)
            r0.updateWifiState(r9)
            goto L_0x014e
        L_0x0137:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r9 = "wifi info is invalid: "
            r0.append(r9)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            java.lang.String r9 = "BatteryExternalStatsWorker"
            android.util.Slog.w(r9, r0)
        L_0x014e:
            if (r8 == 0) goto L_0x0173
            boolean r0 = r8.isValid()
            if (r0 == 0) goto L_0x015c
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats
            r0.updateMobileRadioState(r8)
            goto L_0x0173
        L_0x015c:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r9 = "modem info is invalid: "
            r0.append(r9)
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            java.lang.String r9 = "BatteryExternalStatsWorker"
            android.util.Slog.w(r9, r0)
        L_0x0173:
            return
        L_0x0174:
            r0 = move-exception
            r10 = r21
            r11 = r22
        L_0x0179:
            monitor-exit(r9)     // Catch:{ all -> 0x017b }
            throw r0
        L_0x017b:
            r0 = move-exception
            goto L_0x0179
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.BatteryExternalStatsWorker.updateExternalStatsLocked(java.lang.String, int, boolean, boolean, boolean):void");
    }

    private static <T extends Parcelable> T awaitControllerInfo(SynchronousResultReceiver receiver) {
        if (receiver == null) {
            return null;
        }
        try {
            SynchronousResultReceiver.Result result = receiver.awaitResult(EXTERNAL_STATS_SYNC_TIMEOUT_MILLIS);
            if (result.bundle != null) {
                result.bundle.setDefusable(true);
                T data = result.bundle.getParcelable(StatsCompanionService.RESULT_RECEIVER_CONTROLLER_KEY);
                if (data != null) {
                    return data;
                }
            }
            Slog.e(TAG, "no controller energy info supplied for " + receiver.getName());
        } catch (TimeoutException e) {
            Slog.w(TAG, "timeout reading " + receiver.getName() + " stats");
        }
        return null;
    }

    @GuardedBy({"mWorkerLock"})
    private WifiActivityEnergyInfo extractDeltaLocked(WifiActivityEnergyInfo latest) {
        WifiActivityEnergyInfo delta;
        long rxTimeMs;
        long scanTimeMs;
        long maxExpectedIdleTimeMs;
        WifiActivityEnergyInfo wifiActivityEnergyInfo = latest;
        long timePeriodMs = wifiActivityEnergyInfo.mTimestamp - this.mLastInfo.mTimestamp;
        long lastScanMs = this.mLastInfo.mControllerScanTimeMs;
        long lastIdleMs = this.mLastInfo.mControllerIdleTimeMs;
        long lastTxMs = this.mLastInfo.mControllerTxTimeMs;
        long lastRxMs = this.mLastInfo.mControllerRxTimeMs;
        long lastEnergy = this.mLastInfo.mControllerEnergyUsed;
        WifiActivityEnergyInfo delta2 = this.mLastInfo;
        long lastEnergy2 = lastEnergy;
        delta2.mTimestamp = latest.getTimeStamp();
        delta2.mStackState = latest.getStackState();
        long txTimeMs = wifiActivityEnergyInfo.mControllerTxTimeMs - lastTxMs;
        WifiActivityEnergyInfo delta3 = delta2;
        long lastEnergy3 = lastEnergy2;
        long rxTimeMs2 = wifiActivityEnergyInfo.mControllerRxTimeMs - lastRxMs;
        long lastTxMs2 = lastTxMs;
        long idleTimeMs = wifiActivityEnergyInfo.mControllerIdleTimeMs - lastIdleMs;
        long lastRxMs2 = lastRxMs;
        long scanTimeMs2 = wifiActivityEnergyInfo.mControllerScanTimeMs - lastScanMs;
        long j = lastScanMs;
        if (txTimeMs < 0 || rxTimeMs2 < 0 || scanTimeMs2 < 0) {
            long lastIdleMs2 = idleTimeMs;
            long j2 = scanTimeMs2;
            delta = delta3;
        } else if (idleTimeMs < 0) {
            long j3 = lastIdleMs;
            long lastIdleMs3 = idleTimeMs;
            long j4 = scanTimeMs2;
            delta = delta3;
        } else {
            long idleTimeMs2 = idleTimeMs;
            long idleTimeMs3 = txTimeMs + rxTimeMs2;
            if (idleTimeMs3 > timePeriodMs) {
                if (idleTimeMs3 > timePeriodMs + MAX_WIFI_STATS_SAMPLE_ERROR_MILLIS) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Total Active time ");
                    TimeUtils.formatDuration(idleTimeMs3, sb);
                    sb.append(" is longer than sample period ");
                    TimeUtils.formatDuration(timePeriodMs, sb);
                    sb.append(".\n");
                    sb.append("Previous WiFi snapshot: ");
                    sb.append("idle=");
                    TimeUtils.formatDuration(lastIdleMs, sb);
                    long j5 = lastIdleMs;
                    sb.append(" rx=");
                    scanTimeMs = scanTimeMs2;
                    TimeUtils.formatDuration(lastRxMs2, sb);
                    sb.append(" tx=");
                    TimeUtils.formatDuration(lastTxMs2, sb);
                    sb.append(" e=");
                    rxTimeMs = rxTimeMs2;
                    sb.append(lastEnergy3);
                    sb.append("\n");
                    sb.append("Current WiFi snapshot: ");
                    sb.append("idle=");
                    TimeUtils.formatDuration(wifiActivityEnergyInfo.mControllerIdleTimeMs, sb);
                    sb.append(" rx=");
                    TimeUtils.formatDuration(wifiActivityEnergyInfo.mControllerRxTimeMs, sb);
                    sb.append(" tx=");
                    TimeUtils.formatDuration(wifiActivityEnergyInfo.mControllerTxTimeMs, sb);
                    sb.append(" e=");
                    sb.append(wifiActivityEnergyInfo.mControllerEnergyUsed);
                    Slog.wtf(TAG, sb.toString());
                } else {
                    scanTimeMs = scanTimeMs2;
                    rxTimeMs = rxTimeMs2;
                }
                maxExpectedIdleTimeMs = 0;
            } else {
                scanTimeMs = scanTimeMs2;
                rxTimeMs = rxTimeMs2;
                maxExpectedIdleTimeMs = timePeriodMs - idleTimeMs3;
            }
            delta = delta3;
            delta.mControllerTxTimeMs = txTimeMs;
            delta.mControllerRxTimeMs = rxTimeMs;
            delta.mControllerScanTimeMs = scanTimeMs;
            long j6 = idleTimeMs3;
            delta.mControllerIdleTimeMs = Math.min(maxExpectedIdleTimeMs, Math.max(0, idleTimeMs2));
            delta.mControllerEnergyUsed = Math.max(0, wifiActivityEnergyInfo.mControllerEnergyUsed - lastEnergy3);
            this.mLastInfo = wifiActivityEnergyInfo;
            return delta;
        }
        if (wifiActivityEnergyInfo.mControllerTxTimeMs + wifiActivityEnergyInfo.mControllerRxTimeMs + wifiActivityEnergyInfo.mControllerIdleTimeMs <= timePeriodMs + MAX_WIFI_STATS_SAMPLE_ERROR_MILLIS) {
            delta.mControllerEnergyUsed = wifiActivityEnergyInfo.mControllerEnergyUsed;
            delta.mControllerRxTimeMs = wifiActivityEnergyInfo.mControllerRxTimeMs;
            delta.mControllerTxTimeMs = wifiActivityEnergyInfo.mControllerTxTimeMs;
            delta.mControllerIdleTimeMs = wifiActivityEnergyInfo.mControllerIdleTimeMs;
            delta.mControllerScanTimeMs = wifiActivityEnergyInfo.mControllerScanTimeMs;
        } else {
            delta.mControllerEnergyUsed = 0;
            delta.mControllerRxTimeMs = 0;
            delta.mControllerTxTimeMs = 0;
            delta.mControllerIdleTimeMs = 0;
            delta.mControllerScanTimeMs = 0;
        }
        Slog.v(TAG, "WiFi energy data was reset, new WiFi energy data is " + delta);
        this.mLastInfo = wifiActivityEnergyInfo;
        return delta;
    }
}
