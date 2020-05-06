package com.android.server.usage;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.usage.AppStandbyInfo;
import android.app.usage.UsageStatsManagerInternal;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ParceledListSlice;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.NetworkScoreManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IDeviceIdleController;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.ArraySet;
import android.util.KeyValueListParser;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.ConcurrentUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.LocalServices;
import com.android.server.job.JobPackageTracker;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.Settings;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.usage.AppIdleHistory;
import java.io.File;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class AppStandbyController {
    static final boolean COMPRESS_TIME = false;
    static final boolean DEBUG = false;
    private static final long DEFAULT_PREDICTION_TIMEOUT = 43200000;
    static final long[] ELAPSED_TIME_THRESHOLDS = {0, 43200000, 86400000, 172800000};
    static final int MSG_CHECK_IDLE_STATES = 5;
    static final int MSG_CHECK_PACKAGE_IDLE_STATE = 11;
    static final int MSG_CHECK_PAROLE_TIMEOUT = 6;
    static final int MSG_FORCE_IDLE_STATE = 4;
    static final int MSG_INFORM_LISTENERS = 3;
    static final int MSG_ONE_TIME_CHECK_IDLE_STATES = 10;
    static final int MSG_PAROLE_END_TIMEOUT = 7;
    static final int MSG_PAROLE_STATE_CHANGED = 9;
    static final int MSG_REPORT_CONTENT_PROVIDER_USAGE = 8;
    static final int MSG_REPORT_EXEMPTED_SYNC_START = 13;
    static final int MSG_REPORT_SYNC_SCHEDULED = 12;
    static final int MSG_UPDATE_STABLE_CHARGING = 14;
    private static final long ONE_DAY = 86400000;
    private static final long ONE_HOUR = 3600000;
    private static final long ONE_MINUTE = 60000;
    static final long[] SCREEN_TIME_THRESHOLDS = {0, 0, 3600000, 7200000};
    private static final String TAG = "AppStandbyController";
    static final int[] THRESHOLD_BUCKETS = {10, 20, 30, 40};
    private static final long WAIT_FOR_ADMIN_DATA_TIMEOUT_MS = 10000;
    static final ArrayList<StandbyUpdateRecord> sStandbyUpdatePool = new ArrayList<>(4);
    @GuardedBy({"mActiveAdminApps"})
    private final SparseArray<Set<String>> mActiveAdminApps;
    private final CountDownLatch mAdminDataAvailableLatch;
    volatile boolean mAppIdleEnabled;
    /* access modifiers changed from: private */
    @GuardedBy({"mAppIdleLock"})
    public AppIdleHistory mAppIdleHistory;
    /* access modifiers changed from: private */
    public final Object mAppIdleLock;
    long mAppIdleParoleDurationMillis;
    long mAppIdleParoleIntervalMillis;
    long mAppIdleParoleWindowMillis;
    boolean mAppIdleTempParoled;
    long[] mAppStandbyElapsedThresholds;
    long[] mAppStandbyScreenThresholds;
    private AppWidgetManager mAppWidgetManager;
    @GuardedBy({"mAppIdleLock"})
    private List<String> mCarrierPrivilegedApps;
    boolean mCharging;
    boolean mChargingStable;
    long mCheckIdleIntervalMillis;
    /* access modifiers changed from: private */
    public ConnectivityManager mConnectivityManager;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final DeviceStateReceiver mDeviceStateReceiver;
    private final DisplayManager.DisplayListener mDisplayListener;
    long mExemptedSyncScheduledDozeTimeoutMillis;
    long mExemptedSyncScheduledNonDozeTimeoutMillis;
    long mExemptedSyncStartTimeoutMillis;
    /* access modifiers changed from: private */
    public final AppStandbyHandler mHandler;
    @GuardedBy({"mAppIdleLock"})
    private boolean mHaveCarrierPrivilegedApps;
    long mInitialForegroundServiceStartTimeoutMillis;
    Injector mInjector;
    private long mLastAppIdleParoledTime;
    private final ConnectivityManager.NetworkCallback mNetworkCallback;
    private final NetworkRequest mNetworkRequest;
    long mNotificationSeenTimeoutMillis;
    @GuardedBy({"mPackageAccessListeners"})
    private ArrayList<UsageStatsManagerInternal.AppIdleStateChangeListener> mPackageAccessListeners;
    private PackageManager mPackageManager;
    private boolean mPendingInitializeDefaults;
    private volatile boolean mPendingOneTimeCheckIdleStates;
    private PowerManager mPowerManager;
    long mPredictionTimeoutMillis;
    long mStableChargingThresholdMillis;
    long mStrongUsageTimeoutMillis;
    long mSyncAdapterTimeoutMillis;
    long mSystemInteractionTimeoutMillis;
    private boolean mSystemServicesReady;
    long mSystemUpdateUsageTimeoutMillis;
    long mUnexemptedSyncScheduledTimeoutMillis;

    static class Lock {
        Lock() {
        }
    }

    public static class StandbyUpdateRecord {
        int bucket;
        boolean isUserInteraction;
        String packageName;
        int reason;
        int userId;

        StandbyUpdateRecord(String pkgName, int userId2, int bucket2, int reason2, boolean isInteraction) {
            this.packageName = pkgName;
            this.userId = userId2;
            this.bucket = bucket2;
            this.reason = reason2;
            this.isUserInteraction = isInteraction;
        }

        public static StandbyUpdateRecord obtain(String pkgName, int userId2, int bucket2, int reason2, boolean isInteraction) {
            synchronized (AppStandbyController.sStandbyUpdatePool) {
                int size = AppStandbyController.sStandbyUpdatePool.size();
                if (size < 1) {
                    StandbyUpdateRecord standbyUpdateRecord = new StandbyUpdateRecord(pkgName, userId2, bucket2, reason2, isInteraction);
                    return standbyUpdateRecord;
                }
                StandbyUpdateRecord r = AppStandbyController.sStandbyUpdatePool.remove(size - 1);
                r.packageName = pkgName;
                r.userId = userId2;
                r.bucket = bucket2;
                r.reason = reason2;
                r.isUserInteraction = isInteraction;
                return r;
            }
        }

        public void recycle() {
            synchronized (AppStandbyController.sStandbyUpdatePool) {
                AppStandbyController.sStandbyUpdatePool.add(this);
            }
        }
    }

    AppStandbyController(Context context, Looper looper) {
        this(new Injector(context, looper));
    }

    AppStandbyController(Injector injector) {
        this.mAppIdleLock = new Lock();
        this.mPackageAccessListeners = new ArrayList<>();
        this.mActiveAdminApps = new SparseArray<>();
        this.mAdminDataAvailableLatch = new CountDownLatch(1);
        this.mAppStandbyScreenThresholds = SCREEN_TIME_THRESHOLDS;
        this.mAppStandbyElapsedThresholds = ELAPSED_TIME_THRESHOLDS;
        this.mSystemServicesReady = false;
        this.mNetworkRequest = new NetworkRequest.Builder().build();
        this.mNetworkCallback = new ConnectivityManager.NetworkCallback() {
            public void onAvailable(Network network) {
                AppStandbyController.this.mConnectivityManager.unregisterNetworkCallback(this);
                AppStandbyController.this.checkParoleTimeout();
            }
        };
        this.mDisplayListener = new DisplayManager.DisplayListener() {
            public void onDisplayAdded(int displayId) {
            }

            public void onDisplayRemoved(int displayId) {
            }

            public void onDisplayChanged(int displayId) {
                if (displayId == 0) {
                    boolean displayOn = AppStandbyController.this.isDisplayOn();
                    synchronized (AppStandbyController.this.mAppIdleLock) {
                        AppStandbyController.this.mAppIdleHistory.updateDisplay(displayOn, AppStandbyController.this.mInjector.elapsedRealtime());
                    }
                }
            }
        };
        this.mInjector = injector;
        this.mContext = this.mInjector.getContext();
        this.mHandler = new AppStandbyHandler(this.mInjector.getLooper());
        this.mPackageManager = this.mContext.getPackageManager();
        this.mDeviceStateReceiver = new DeviceStateReceiver();
        IntentFilter deviceStates = new IntentFilter("android.os.action.CHARGING");
        deviceStates.addAction("android.os.action.DISCHARGING");
        deviceStates.addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
        this.mContext.registerReceiver(this.mDeviceStateReceiver, deviceStates);
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory = new AppIdleHistory(this.mInjector.getDataSystemDirectory(), this.mInjector.elapsedRealtime());
        }
        IntentFilter packageFilter = new IntentFilter();
        packageFilter.addAction("android.intent.action.PACKAGE_ADDED");
        packageFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        packageFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        packageFilter.addDataScheme(Settings.ATTR_PACKAGE);
        this.mContext.registerReceiverAsUser(new PackageReceiver(), UserHandle.ALL, packageFilter, (String) null, this.mHandler);
    }

    /* access modifiers changed from: package-private */
    public void setAppIdleEnabled(boolean enabled) {
        synchronized (this.mAppIdleLock) {
            if (this.mAppIdleEnabled != enabled) {
                boolean oldParoleState = isParoledOrCharging();
                this.mAppIdleEnabled = enabled;
                if (isParoledOrCharging() != oldParoleState) {
                    postParoleStateChanged();
                }
            }
        }
    }

    public void onBootPhase(int phase) {
        boolean userFileExists;
        this.mInjector.onBootPhase(phase);
        if (phase == 500) {
            Slog.d(TAG, "Setting app idle enabled state");
            SettingsObserver settingsObserver = new SettingsObserver(this.mHandler);
            settingsObserver.registerObserver();
            settingsObserver.updateSettings();
            this.mAppWidgetManager = (AppWidgetManager) this.mContext.getSystemService(AppWidgetManager.class);
            this.mConnectivityManager = (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
            this.mPowerManager = (PowerManager) this.mContext.getSystemService(PowerManager.class);
            this.mInjector.registerDisplayListener(this.mDisplayListener, this.mHandler);
            synchronized (this.mAppIdleLock) {
                this.mAppIdleHistory.updateDisplay(isDisplayOn(), this.mInjector.elapsedRealtime());
            }
            this.mSystemServicesReady = true;
            synchronized (this.mAppIdleLock) {
                userFileExists = this.mAppIdleHistory.userFileExists(0);
            }
            if (this.mPendingInitializeDefaults || !userFileExists) {
                initializeDefaultsForSystemApps(0);
            }
            if (this.mPendingOneTimeCheckIdleStates) {
                postOneTimeCheckIdleStates();
            }
        } else if (phase == 1000) {
            setChargingState(this.mInjector.isCharging());
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 24 */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v5, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v16, resolved type: java.lang.String[]} */
    /* JADX WARNING: type inference failed for: r16v5 */
    /* JADX WARNING: type inference failed for: r15v5 */
    /* JADX WARNING: type inference failed for: r16v8 */
    /* JADX WARNING: type inference failed for: r15v8 */
    /* JADX WARNING: type inference failed for: r16v12 */
    /* JADX WARNING: type inference failed for: r15v12 */
    /* JADX WARNING: type inference failed for: r15v15 */
    /* JADX WARNING: type inference failed for: r16v15 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void reportContentProviderUsage(java.lang.String r25, java.lang.String r26, int r27) {
        /*
            r24 = this;
            r9 = r24
            r8 = r27
            boolean r0 = r9.mAppIdleEnabled
            if (r0 != 0) goto L_0x0009
            return
        L_0x0009:
            r7 = r25
            java.lang.String[] r6 = android.content.ContentResolver.getSyncAdapterPackagesForAuthorityAsUser(r7, r8)
            com.android.server.usage.AppStandbyController$Injector r0 = r9.mInjector
            long r19 = r0.elapsedRealtime()
            int r4 = r6.length
            r0 = 0
            r5 = r0
        L_0x0018:
            if (r5 >= r4) goto L_0x00ae
            r3 = r6[r5]
            android.content.pm.PackageManager r0 = r9.mPackageManager     // Catch:{ NameNotFoundException -> 0x009c }
            r1 = 1048576(0x100000, float:1.469368E-39)
            android.content.pm.PackageInfo r0 = r0.getPackageInfoAsUser(r3, r1, r8)     // Catch:{ NameNotFoundException -> 0x009c }
            r2 = r0
            if (r2 == 0) goto L_0x0094
            android.content.pm.ApplicationInfo r0 = r2.applicationInfo     // Catch:{ NameNotFoundException -> 0x009c }
            if (r0 != 0) goto L_0x0034
            r13 = r2
            r14 = r3
            r15 = r4
            r16 = r5
            r17 = r6
            goto L_0x009b
        L_0x0034:
            r1 = r26
            boolean r0 = r3.equals(r1)     // Catch:{ NameNotFoundException -> 0x009c }
            if (r0 != 0) goto L_0x008c
            java.lang.Object r15 = r9.mAppIdleLock     // Catch:{ NameNotFoundException -> 0x009c }
            monitor-enter(r15)     // Catch:{ NameNotFoundException -> 0x009c }
            com.android.server.usage.AppIdleHistory r10 = r9.mAppIdleHistory     // Catch:{ all -> 0x007c }
            r13 = 10
            r14 = 8
            r16 = 0
            long r11 = r9.mSyncAdapterTimeoutMillis     // Catch:{ all -> 0x007c }
            long r21 = r19 + r11
            r11 = r3
            r12 = r27
            r23 = r15
            r15 = r16
            r17 = r21
            com.android.server.usage.AppIdleHistory$AppUsageHistory r0 = r10.reportUsage((java.lang.String) r11, (int) r12, (int) r13, (int) r14, (long) r15, (long) r17)     // Catch:{ all -> 0x0073 }
            int r10 = r0.currentBucket     // Catch:{ all -> 0x0073 }
            int r11 = r0.bucketingReason     // Catch:{ all -> 0x0073 }
            r12 = 0
            r1 = r24
            r13 = r2
            r2 = r3
            r14 = r3
            r3 = r27
            r15 = r4
            r16 = r5
            r4 = r19
            r17 = r6
            r6 = r10
            r7 = r11
            r8 = r12
            r1.maybeInformListeners(r2, r3, r4, r6, r7, r8)     // Catch:{ all -> 0x008a }
            monitor-exit(r23)     // Catch:{ all -> 0x008a }
            goto L_0x0093
        L_0x0073:
            r0 = move-exception
            r13 = r2
            r14 = r3
            r15 = r4
            r16 = r5
            r17 = r6
            goto L_0x0086
        L_0x007c:
            r0 = move-exception
            r13 = r2
            r14 = r3
            r16 = r5
            r17 = r6
            r23 = r15
            r15 = r4
        L_0x0086:
            monitor-exit(r23)     // Catch:{ all -> 0x008a }
            throw r0     // Catch:{ NameNotFoundException -> 0x0088 }
        L_0x0088:
            r0 = move-exception
            goto L_0x00a3
        L_0x008a:
            r0 = move-exception
            goto L_0x0086
        L_0x008c:
            r13 = r2
            r14 = r3
            r15 = r4
            r16 = r5
            r17 = r6
        L_0x0093:
            goto L_0x00a3
        L_0x0094:
            r13 = r2
            r14 = r3
            r15 = r4
            r16 = r5
            r17 = r6
        L_0x009b:
            goto L_0x00a3
        L_0x009c:
            r0 = move-exception
            r14 = r3
            r15 = r4
            r16 = r5
            r17 = r6
        L_0x00a3:
            int r5 = r16 + 1
            r7 = r25
            r8 = r27
            r4 = r15
            r6 = r17
            goto L_0x0018
        L_0x00ae:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.AppStandbyController.reportContentProviderUsage(java.lang.String, java.lang.String, int):void");
    }

    /* access modifiers changed from: package-private */
    public void reportExemptedSyncScheduled(String packageName, int userId) {
        long durationMillis;
        int usageReason;
        int bucketToPromote;
        Object obj;
        if (this.mAppIdleEnabled) {
            if (!this.mInjector.isDeviceIdleMode()) {
                bucketToPromote = 10;
                usageReason = 11;
                durationMillis = this.mExemptedSyncScheduledNonDozeTimeoutMillis;
            } else {
                bucketToPromote = 20;
                usageReason = 12;
                durationMillis = this.mExemptedSyncScheduledDozeTimeoutMillis;
            }
            long elapsedRealtime = this.mInjector.elapsedRealtime();
            Object obj2 = this.mAppIdleLock;
            synchronized (obj2) {
                try {
                    AppIdleHistory.AppUsageHistory appUsage = this.mAppIdleHistory.reportUsage(packageName, userId, bucketToPromote, usageReason, 0, elapsedRealtime + durationMillis);
                    obj = obj2;
                    maybeInformListeners(packageName, userId, elapsedRealtime, appUsage.currentBucket, appUsage.bucketingReason, false);
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void reportUnexemptedSyncScheduled(String packageName, int userId) {
        if (this.mAppIdleEnabled) {
            long elapsedRealtime = this.mInjector.elapsedRealtime();
            synchronized (this.mAppIdleLock) {
                if (this.mAppIdleHistory.getAppStandbyBucket(packageName, userId, elapsedRealtime) == 50) {
                    AppIdleHistory.AppUsageHistory appUsage = this.mAppIdleHistory.reportUsage(packageName, userId, 20, 14, 0, elapsedRealtime + this.mUnexemptedSyncScheduledTimeoutMillis);
                    maybeInformListeners(packageName, userId, elapsedRealtime, appUsage.currentBucket, appUsage.bucketingReason, false);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void reportExemptedSyncStart(String packageName, int userId) {
        if (this.mAppIdleEnabled) {
            long elapsedRealtime = this.mInjector.elapsedRealtime();
            synchronized (this.mAppIdleLock) {
                AppIdleHistory.AppUsageHistory appUsage = this.mAppIdleHistory.reportUsage(packageName, userId, 10, 13, 0, elapsedRealtime + this.mExemptedSyncStartTimeoutMillis);
                maybeInformListeners(packageName, userId, elapsedRealtime, appUsage.currentBucket, appUsage.bucketingReason, false);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setChargingState(boolean charging) {
        synchronized (this.mAppIdleLock) {
            if (this.mCharging != charging) {
                this.mCharging = charging;
                if (charging) {
                    this.mHandler.sendEmptyMessageDelayed(14, this.mStableChargingThresholdMillis);
                } else {
                    this.mHandler.removeMessages(14);
                    updateChargingStableState();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateChargingStableState() {
        synchronized (this.mAppIdleLock) {
            if (this.mChargingStable != this.mCharging) {
                this.mChargingStable = this.mCharging;
                postParoleStateChanged();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setAppIdleParoled(boolean paroled) {
        synchronized (this.mAppIdleLock) {
            long now = this.mInjector.currentTimeMillis();
            if (this.mAppIdleTempParoled != paroled) {
                this.mAppIdleTempParoled = paroled;
                if (paroled) {
                    postParoleEndTimeout();
                } else {
                    this.mLastAppIdleParoledTime = now;
                    postNextParoleTimeout(now, false);
                }
                postParoleStateChanged();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isParoledOrCharging() {
        boolean z = true;
        if (!this.mAppIdleEnabled) {
            return true;
        }
        synchronized (this.mAppIdleLock) {
            if (!this.mAppIdleTempParoled) {
                if (!this.mChargingStable) {
                    z = false;
                }
            }
        }
        return z;
    }

    private void postNextParoleTimeout(long now, boolean forced) {
        this.mHandler.removeMessages(6);
        long timeLeft = (this.mLastAppIdleParoledTime + this.mAppIdleParoleIntervalMillis) - now;
        if (forced) {
            timeLeft += this.mAppIdleParoleWindowMillis;
        }
        if (timeLeft < 0) {
            timeLeft = 0;
        }
        this.mHandler.sendEmptyMessageDelayed(6, timeLeft);
    }

    private void postParoleEndTimeout() {
        this.mHandler.removeMessages(7);
        this.mHandler.sendEmptyMessageDelayed(7, this.mAppIdleParoleDurationMillis);
    }

    private void postParoleStateChanged() {
        this.mHandler.removeMessages(9);
        this.mHandler.sendEmptyMessage(9);
    }

    /* access modifiers changed from: package-private */
    public void postCheckIdleStates(int userId) {
        AppStandbyHandler appStandbyHandler = this.mHandler;
        appStandbyHandler.sendMessage(appStandbyHandler.obtainMessage(5, userId, 0));
    }

    /* access modifiers changed from: package-private */
    public void postOneTimeCheckIdleStates() {
        if (this.mInjector.getBootPhase() < 500) {
            this.mPendingOneTimeCheckIdleStates = true;
            return;
        }
        this.mHandler.sendEmptyMessage(10);
        this.mPendingOneTimeCheckIdleStates = false;
    }

    /* access modifiers changed from: package-private */
    public boolean checkIdleStates(int checkUserId) {
        int i = checkUserId;
        if (!this.mAppIdleEnabled) {
            return false;
        }
        try {
            int[] runningUserIds = this.mInjector.getRunningUserIds();
            if (i != -1 && !ArrayUtils.contains(runningUserIds, i)) {
                return false;
            }
            long elapsedRealtime = this.mInjector.elapsedRealtime();
            for (int userId : runningUserIds) {
                if (i == -1 || i == userId) {
                    List<PackageInfo> packages = this.mPackageManager.getInstalledPackagesAsUser(512, userId);
                    int packageCount = packages.size();
                    int p = 0;
                    while (p < packageCount) {
                        PackageInfo pi = packages.get(p);
                        String packageName = pi.packageName;
                        String str = packageName;
                        PackageInfo packageInfo = pi;
                        checkAndUpdateStandbyState(packageName, userId, pi.applicationInfo.uid, elapsedRealtime);
                        p++;
                    }
                    int i2 = p;
                }
            }
            return true;
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkAndUpdateStandbyState(java.lang.String r26, int r27, int r28, long r29) {
        /*
            r25 = this;
            r9 = r25
            r10 = r26
            r11 = r27
            r12 = r29
            if (r28 > 0) goto L_0x0014
            android.content.pm.PackageManager r0 = r9.mPackageManager     // Catch:{ NameNotFoundException -> 0x0012 }
            int r0 = r0.getPackageUidAsUser(r10, r11)     // Catch:{ NameNotFoundException -> 0x0012 }
            r14 = r0
            goto L_0x0016
        L_0x0012:
            r0 = move-exception
            return
        L_0x0014:
            r14 = r28
        L_0x0016:
            int r0 = android.os.UserHandle.getAppId(r14)
            boolean r15 = r9.isAppSpecial(r10, r0, r11)
            if (r15 == 0) goto L_0x0047
            java.lang.Object r8 = r9.mAppIdleLock
            monitor-enter(r8)
            com.android.server.usage.AppIdleHistory r1 = r9.mAppIdleHistory     // Catch:{ all -> 0x0044 }
            r6 = 5
            r7 = 256(0x100, float:3.59E-43)
            r2 = r26
            r3 = r27
            r4 = r29
            r1.setAppStandbyBucket(r2, r3, r4, r6, r7)     // Catch:{ all -> 0x0044 }
            monitor-exit(r8)     // Catch:{ all -> 0x0044 }
            r6 = 5
            r7 = 256(0x100, float:3.59E-43)
            r8 = 0
            r1 = r25
            r2 = r26
            r3 = r27
            r4 = r29
            r1.maybeInformListeners(r2, r3, r4, r6, r7, r8)
            goto L_0x00fa
        L_0x0044:
            r0 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x0044 }
            throw r0
        L_0x0047:
            java.lang.Object r8 = r9.mAppIdleLock
            monitor-enter(r8)
            com.android.server.usage.AppIdleHistory r0 = r9.mAppIdleHistory     // Catch:{ all -> 0x00fb }
            com.android.server.usage.AppIdleHistory$AppUsageHistory r0 = r0.getAppUsageHistory(r10, r11, r12)     // Catch:{ all -> 0x00fb }
            int r1 = r0.bucketingReason     // Catch:{ all -> 0x00fb }
            r2 = 65280(0xff00, float:9.1477E-41)
            r7 = r1 & r2
            r2 = 1024(0x400, float:1.435E-42)
            if (r7 != r2) goto L_0x005d
            monitor-exit(r8)     // Catch:{ all -> 0x00fb }
            return
        L_0x005d:
            int r2 = r0.currentBucket     // Catch:{ all -> 0x00fb }
            r6 = r2
            r2 = 10
            int r3 = java.lang.Math.max(r6, r2)     // Catch:{ all -> 0x00fb }
            boolean r4 = r9.predictionTimedOut(r0, r12)     // Catch:{ all -> 0x00fb }
            r16 = r4
            r4 = 256(0x100, float:3.59E-43)
            if (r7 == r4) goto L_0x007a
            r4 = 768(0x300, float:1.076E-42)
            if (r7 == r4) goto L_0x007a
            r4 = 512(0x200, float:7.175E-43)
            if (r7 == r4) goto L_0x007a
            if (r16 == 0) goto L_0x0093
        L_0x007a:
            if (r16 != 0) goto L_0x008c
            int r4 = r0.lastPredictedBucket     // Catch:{ all -> 0x00fb }
            if (r4 < r2) goto L_0x008c
            int r4 = r0.lastPredictedBucket     // Catch:{ all -> 0x00fb }
            r5 = 40
            if (r4 > r5) goto L_0x008c
            int r4 = r0.lastPredictedBucket     // Catch:{ all -> 0x00fb }
            r3 = r4
            r1 = 1281(0x501, float:1.795E-42)
            goto L_0x0093
        L_0x008c:
            int r4 = r9.getBucketForLocked(r10, r11, r12)     // Catch:{ all -> 0x00fb }
            r3 = r4
            r1 = 512(0x200, float:7.175E-43)
        L_0x0093:
            com.android.server.usage.AppIdleHistory r4 = r9.mAppIdleHistory     // Catch:{ all -> 0x00fb }
            long r4 = r4.getElapsedTime(r12)     // Catch:{ all -> 0x00fb }
            r17 = r4
            if (r3 < r2) goto L_0x00ac
            long r4 = r0.bucketActiveTimeoutTime     // Catch:{ all -> 0x00fb }
            int r2 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1))
            if (r2 <= 0) goto L_0x00ac
            r2 = 10
            int r3 = r0.bucketingReason     // Catch:{ all -> 0x00fb }
            r1 = r3
            r19 = r1
            r4 = r2
            goto L_0x00c7
        L_0x00ac:
            r2 = 20
            if (r3 < r2) goto L_0x00c4
            long r4 = r0.bucketWorkingSetTimeoutTime     // Catch:{ all -> 0x00fb }
            int r2 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1))
            if (r2 <= 0) goto L_0x00c4
            r2 = 20
            if (r2 != r6) goto L_0x00bd
            int r3 = r0.bucketingReason     // Catch:{ all -> 0x00fb }
            goto L_0x00bf
        L_0x00bd:
            r3 = 775(0x307, float:1.086E-42)
        L_0x00bf:
            r1 = r3
            r19 = r1
            r4 = r2
            goto L_0x00c7
        L_0x00c4:
            r19 = r1
            r4 = r3
        L_0x00c7:
            if (r6 < r4) goto L_0x00cf
            if (r16 == 0) goto L_0x00cc
            goto L_0x00cf
        L_0x00cc:
            r24 = r8
            goto L_0x00f9
        L_0x00cf:
            com.android.server.usage.AppIdleHistory r1 = r9.mAppIdleHistory     // Catch:{ all -> 0x00fb }
            r2 = r26
            r3 = r27
            r20 = r4
            r4 = r29
            r21 = r6
            r6 = r20
            r22 = r7
            r7 = r19
            r1.setAppStandbyBucket(r2, r3, r4, r6, r7)     // Catch:{ all -> 0x00fb }
            r23 = 0
            r1 = r25
            r2 = r26
            r3 = r27
            r4 = r29
            r6 = r20
            r7 = r19
            r24 = r8
            r8 = r23
            r1.maybeInformListeners(r2, r3, r4, r6, r7, r8)     // Catch:{ all -> 0x0100 }
        L_0x00f9:
            monitor-exit(r24)     // Catch:{ all -> 0x0100 }
        L_0x00fa:
            return
        L_0x00fb:
            r0 = move-exception
            r24 = r8
        L_0x00fe:
            monitor-exit(r24)     // Catch:{ all -> 0x0100 }
            throw r0
        L_0x0100:
            r0 = move-exception
            goto L_0x00fe
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.AppStandbyController.checkAndUpdateStandbyState(java.lang.String, int, int, long):void");
    }

    private boolean predictionTimedOut(AppIdleHistory.AppUsageHistory app, long elapsedRealtime) {
        return app.lastPredictedTime > 0 && this.mAppIdleHistory.getElapsedTime(elapsedRealtime) - app.lastPredictedTime > this.mPredictionTimeoutMillis;
    }

    private void maybeInformListeners(String packageName, int userId, long elapsedRealtime, int bucket, int reason, boolean userStartedInteracting) {
        synchronized (this.mAppIdleLock) {
            if (this.mAppIdleHistory.shouldInformListeners(packageName, userId, elapsedRealtime, bucket)) {
                this.mHandler.sendMessage(this.mHandler.obtainMessage(3, StandbyUpdateRecord.obtain(packageName, userId, bucket, reason, userStartedInteracting)));
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mAppIdleLock"})
    public int getBucketForLocked(String packageName, int userId, long elapsedRealtime) {
        return THRESHOLD_BUCKETS[this.mAppIdleHistory.getThresholdIndex(packageName, userId, elapsedRealtime, this.mAppStandbyScreenThresholds, this.mAppStandbyElapsedThresholds)];
    }

    /* access modifiers changed from: package-private */
    public void checkParoleTimeout() {
        boolean setParoled = false;
        boolean waitForNetwork = false;
        NetworkInfo activeNetwork = this.mConnectivityManager.getActiveNetworkInfo();
        boolean networkActive = activeNetwork != null && activeNetwork.isConnected();
        synchronized (this.mAppIdleLock) {
            long now = this.mInjector.currentTimeMillis();
            if (!this.mAppIdleTempParoled) {
                long timeSinceLastParole = now - this.mLastAppIdleParoledTime;
                if (timeSinceLastParole <= this.mAppIdleParoleIntervalMillis) {
                    postNextParoleTimeout(now, false);
                } else if (networkActive) {
                    setParoled = true;
                } else if (timeSinceLastParole > this.mAppIdleParoleIntervalMillis + this.mAppIdleParoleWindowMillis) {
                    setParoled = true;
                } else {
                    waitForNetwork = true;
                    postNextParoleTimeout(now, true);
                }
            }
        }
        if (waitForNetwork) {
            this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback);
        }
        if (setParoled) {
            setAppIdleParoled(true);
        }
    }

    private void notifyBatteryStats(String packageName, int userId, boolean idle) {
        try {
            int uid = this.mPackageManager.getPackageUidAsUser(packageName, 8192, userId);
            if (idle) {
                this.mInjector.noteEvent(15, packageName, uid);
            } else {
                this.mInjector.noteEvent(16, packageName, uid);
            }
        } catch (PackageManager.NameNotFoundException | RemoteException e) {
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0021, code lost:
        setAppIdleParoled(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0024, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDeviceIdleModeChanged() {
        /*
            r7 = this;
            android.os.PowerManager r0 = r7.mPowerManager
            boolean r0 = r0.isDeviceIdleMode()
            r1 = 0
            java.lang.Object r2 = r7.mAppIdleLock
            monitor-enter(r2)
            com.android.server.usage.AppStandbyController$Injector r3 = r7.mInjector     // Catch:{ all -> 0x0027 }
            long r3 = r3.currentTimeMillis()     // Catch:{ all -> 0x0027 }
            long r5 = r7.mLastAppIdleParoledTime     // Catch:{ all -> 0x0027 }
            long r3 = r3 - r5
            if (r0 != 0) goto L_0x001d
            long r5 = r7.mAppIdleParoleIntervalMillis     // Catch:{ all -> 0x0027 }
            int r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r5 < 0) goto L_0x001d
            r1 = 1
            goto L_0x0020
        L_0x001d:
            if (r0 == 0) goto L_0x0025
            r1 = 0
        L_0x0020:
            monitor-exit(r2)     // Catch:{ all -> 0x0027 }
            r7.setAppIdleParoled(r1)
            return
        L_0x0025:
            monitor-exit(r2)     // Catch:{ all -> 0x0027 }
            return
        L_0x0027:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0027 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.AppStandbyController.onDeviceIdleModeChanged():void");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x012b A[Catch:{ all -> 0x014f, all -> 0x0155 }] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x012d A[Catch:{ all -> 0x014f, all -> 0x0155 }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0147 A[Catch:{ all -> 0x014f, all -> 0x0155 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void reportEvent(android.app.usage.UsageEvents.Event r29, long r30, int r32) {
        /*
            r28 = this;
            r9 = r28
            r10 = r29
            r6 = r30
            r4 = r32
            boolean r0 = r9.mAppIdleEnabled
            if (r0 != 0) goto L_0x000d
            return
        L_0x000d:
            java.lang.Object r5 = r9.mAppIdleLock
            monitor-enter(r5)
            com.android.server.usage.AppIdleHistory r0 = r9.mAppIdleHistory     // Catch:{ all -> 0x014f }
            java.lang.String r1 = r10.mPackage     // Catch:{ all -> 0x014f }
            boolean r0 = r0.isIdle(r1, r4, r6)     // Catch:{ all -> 0x014f }
            int r1 = r10.mEventType     // Catch:{ all -> 0x014f }
            r2 = 19
            r3 = 14
            r8 = 6
            r15 = 1
            r14 = 10
            if (r1 == r15) goto L_0x004a
            int r1 = r10.mEventType     // Catch:{ all -> 0x014f }
            r11 = 2
            if (r1 == r11) goto L_0x004a
            int r1 = r10.mEventType     // Catch:{ all -> 0x014f }
            if (r1 == r8) goto L_0x004a
            int r1 = r10.mEventType     // Catch:{ all -> 0x014f }
            r11 = 7
            if (r1 == r11) goto L_0x004a
            int r1 = r10.mEventType     // Catch:{ all -> 0x014f }
            if (r1 == r14) goto L_0x004a
            int r1 = r10.mEventType     // Catch:{ all -> 0x014f }
            if (r1 == r3) goto L_0x004a
            int r1 = r10.mEventType     // Catch:{ all -> 0x014f }
            r11 = 13
            if (r1 == r11) goto L_0x004a
            int r1 = r10.mEventType     // Catch:{ all -> 0x014f }
            if (r1 != r2) goto L_0x0045
            goto L_0x004a
        L_0x0045:
            r13 = r4
            r18 = r5
            goto L_0x014d
        L_0x004a:
            com.android.server.usage.AppIdleHistory r1 = r9.mAppIdleHistory     // Catch:{ all -> 0x014f }
            java.lang.String r11 = r10.mPackage     // Catch:{ all -> 0x014f }
            com.android.server.usage.AppIdleHistory$AppUsageHistory r1 = r1.getAppUsageHistory(r11, r4, r6)     // Catch:{ all -> 0x014f }
            int r11 = r1.currentBucket     // Catch:{ all -> 0x014f }
            r13 = r11
            int r11 = r1.bucketingReason     // Catch:{ all -> 0x014f }
            r25 = r11
            int r11 = r10.mEventType     // Catch:{ all -> 0x014f }
            int r11 = r9.usageEventToSubReason(r11)     // Catch:{ all -> 0x014f }
            r12 = r11
            r11 = r12 | 768(0x300, float:1.076E-42)
            int r15 = r10.mEventType     // Catch:{ all -> 0x014f }
            if (r15 == r14) goto L_0x00e8
            int r15 = r10.mEventType     // Catch:{ all -> 0x014f }
            if (r15 != r3) goto L_0x0073
            r26 = r11
            r27 = r12
            r2 = r13
            r8 = r14
            r3 = 1
            goto L_0x00ef
        L_0x0073:
            int r3 = r10.mEventType     // Catch:{ all -> 0x014f }
            if (r3 != r8) goto L_0x009b
            com.android.server.usage.AppIdleHistory r2 = r9.mAppIdleHistory     // Catch:{ all -> 0x014f }
            java.lang.String r3 = r10.mPackage     // Catch:{ all -> 0x014f }
            r19 = 10
            r21 = 0
            long r14 = r9.mSystemInteractionTimeoutMillis     // Catch:{ all -> 0x014f }
            long r23 = r6 + r14
            r16 = r2
            r17 = r1
            r18 = r3
            r20 = r12
            r16.reportUsage((com.android.server.usage.AppIdleHistory.AppUsageHistory) r17, (java.lang.String) r18, (int) r19, (int) r20, (long) r21, (long) r23)     // Catch:{ all -> 0x014f }
            long r2 = r9.mSystemInteractionTimeoutMillis     // Catch:{ all -> 0x014f }
            r26 = r11
            r27 = r12
            r8 = 10
            r11 = r2
            r2 = r13
            r3 = 1
            goto L_0x0108
        L_0x009b:
            int r3 = r10.mEventType     // Catch:{ all -> 0x014f }
            if (r3 != r2) goto L_0x00c8
            r2 = 50
            if (r13 == r2) goto L_0x00a5
            monitor-exit(r5)     // Catch:{ all -> 0x014f }
            return
        L_0x00a5:
            com.android.server.usage.AppIdleHistory r2 = r9.mAppIdleHistory     // Catch:{ all -> 0x014f }
            java.lang.String r3 = r10.mPackage     // Catch:{ all -> 0x014f }
            r19 = 10
            r21 = 0
            long r14 = r9.mInitialForegroundServiceStartTimeoutMillis     // Catch:{ all -> 0x014f }
            long r23 = r6 + r14
            r16 = r2
            r17 = r1
            r18 = r3
            r20 = r12
            r16.reportUsage((com.android.server.usage.AppIdleHistory.AppUsageHistory) r17, (java.lang.String) r18, (int) r19, (int) r20, (long) r21, (long) r23)     // Catch:{ all -> 0x014f }
            long r2 = r9.mInitialForegroundServiceStartTimeoutMillis     // Catch:{ all -> 0x014f }
            r26 = r11
            r27 = r12
            r8 = 10
            r11 = r2
            r2 = r13
            r3 = 1
            goto L_0x0108
        L_0x00c8:
            com.android.server.usage.AppIdleHistory r2 = r9.mAppIdleHistory     // Catch:{ all -> 0x014f }
            java.lang.String r3 = r10.mPackage     // Catch:{ all -> 0x014f }
            long r14 = r9.mStrongUsageTimeoutMillis     // Catch:{ all -> 0x014f }
            long r18 = r6 + r14
            r26 = r11
            r11 = r2
            r27 = r12
            r12 = r1
            r2 = r13
            r13 = r3
            r3 = 10
            r8 = 10
            r14 = r3
            r3 = 1
            r15 = r27
            r16 = r30
            r11.reportUsage((com.android.server.usage.AppIdleHistory.AppUsageHistory) r12, (java.lang.String) r13, (int) r14, (int) r15, (long) r16, (long) r18)     // Catch:{ all -> 0x014f }
            long r11 = r9.mStrongUsageTimeoutMillis     // Catch:{ all -> 0x014f }
            goto L_0x0108
        L_0x00e8:
            r26 = r11
            r27 = r12
            r2 = r13
            r8 = r14
            r3 = 1
        L_0x00ef:
            com.android.server.usage.AppIdleHistory r11 = r9.mAppIdleHistory     // Catch:{ all -> 0x014f }
            java.lang.String r12 = r10.mPackage     // Catch:{ all -> 0x014f }
            r19 = 20
            r21 = 0
            long r13 = r9.mNotificationSeenTimeoutMillis     // Catch:{ all -> 0x014f }
            long r23 = r6 + r13
            r16 = r11
            r17 = r1
            r18 = r12
            r20 = r27
            r16.reportUsage((com.android.server.usage.AppIdleHistory.AppUsageHistory) r17, (java.lang.String) r18, (int) r19, (int) r20, (long) r21, (long) r23)     // Catch:{ all -> 0x014f }
            long r11 = r9.mNotificationSeenTimeoutMillis     // Catch:{ all -> 0x014f }
        L_0x0108:
            com.android.server.usage.AppStandbyController$AppStandbyHandler r13 = r9.mHandler     // Catch:{ all -> 0x014f }
            com.android.server.usage.AppStandbyController$AppStandbyHandler r14 = r9.mHandler     // Catch:{ all -> 0x014f }
            r15 = 11
            r3 = -1
            java.lang.String r8 = r10.mPackage     // Catch:{ all -> 0x014f }
            android.os.Message r3 = r14.obtainMessage(r15, r4, r3, r8)     // Catch:{ all -> 0x014f }
            r13.sendMessageDelayed(r3, r11)     // Catch:{ all -> 0x014f }
            int r3 = r1.currentBucket     // Catch:{ all -> 0x014f }
            r8 = 10
            if (r3 != r8) goto L_0x012d
            int r3 = r1.currentBucket     // Catch:{ all -> 0x014f }
            if (r2 == r3) goto L_0x012d
            r3 = 65280(0xff00, float:9.1477E-41)
            r3 = r25 & r3
            r8 = 768(0x300, float:1.076E-42)
            if (r3 == r8) goto L_0x012d
            r8 = 1
            goto L_0x012e
        L_0x012d:
            r8 = 0
        L_0x012e:
            java.lang.String r3 = r10.mPackage     // Catch:{ all -> 0x014f }
            int r14 = r1.currentBucket     // Catch:{ all -> 0x014f }
            r15 = r1
            r1 = r28
            r16 = r2
            r2 = r3
            r3 = r32
            r13 = r4
            r18 = r5
            r4 = r30
            r6 = r14
            r7 = r26
            r1.maybeInformListeners(r2, r3, r4, r6, r7, r8)     // Catch:{ all -> 0x0155 }
            if (r0 == 0) goto L_0x014d
            java.lang.String r1 = r10.mPackage     // Catch:{ all -> 0x0155 }
            r2 = 0
            r9.notifyBatteryStats(r1, r13, r2)     // Catch:{ all -> 0x0155 }
        L_0x014d:
            monitor-exit(r18)     // Catch:{ all -> 0x0155 }
            return
        L_0x014f:
            r0 = move-exception
            r13 = r4
            r18 = r5
        L_0x0153:
            monitor-exit(r18)     // Catch:{ all -> 0x0155 }
            throw r0
        L_0x0155:
            r0 = move-exception
            goto L_0x0153
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.AppStandbyController.reportEvent(android.app.usage.UsageEvents$Event, long, int):void");
    }

    private int usageEventToSubReason(int eventType) {
        if (eventType == 1) {
            return 4;
        }
        if (eventType == 2) {
            return 5;
        }
        if (eventType == 6) {
            return 1;
        }
        if (eventType == 7) {
            return 3;
        }
        if (eventType == 10) {
            return 2;
        }
        if (eventType == 19) {
            return 15;
        }
        if (eventType == 13) {
            return 10;
        }
        if (eventType != 14) {
            return 0;
        }
        return 9;
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0060, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void forceIdleState(java.lang.String r20, int r21, boolean r22) {
        /*
            r19 = this;
            r15 = r19
            boolean r0 = r15.mAppIdleEnabled
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            int r16 = r19.getAppId(r20)
            if (r16 >= 0) goto L_0x000e
            return
        L_0x000e:
            com.android.server.usage.AppStandbyController$Injector r0 = r15.mInjector
            long r17 = r0.elapsedRealtime()
            r1 = r19
            r2 = r20
            r3 = r16
            r4 = r21
            r5 = r17
            boolean r14 = r1.isAppIdleFiltered(r2, r3, r4, r5)
            java.lang.Object r7 = r15.mAppIdleLock
            monitor-enter(r7)
            com.android.server.usage.AppIdleHistory r1 = r15.mAppIdleHistory     // Catch:{ all -> 0x005c }
            r2 = r20
            r3 = r21
            r4 = r22
            r5 = r17
            int r12 = r1.setIdle(r2, r3, r4, r5)     // Catch:{ all -> 0x005c }
            monitor-exit(r7)     // Catch:{ all -> 0x005c }
            r1 = r19
            r2 = r20
            r3 = r16
            r4 = r21
            r5 = r17
            boolean r0 = r1.isAppIdleFiltered(r2, r3, r4, r5)
            if (r14 == r0) goto L_0x005a
            r13 = 1024(0x400, float:1.435E-42)
            r1 = 0
            r7 = r19
            r8 = r20
            r9 = r21
            r10 = r17
            r2 = r14
            r14 = r1
            r7.maybeInformListeners(r8, r9, r10, r12, r13, r14)
            if (r0 != 0) goto L_0x005b
            r19.notifyBatteryStats(r20, r21, r22)
            goto L_0x005b
        L_0x005a:
            r2 = r14
        L_0x005b:
            return
        L_0x005c:
            r0 = move-exception
            r2 = r14
        L_0x005e:
            monitor-exit(r7)     // Catch:{ all -> 0x0060 }
            throw r0
        L_0x0060:
            r0 = move-exception
            goto L_0x005e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.AppStandbyController.forceIdleState(java.lang.String, int, boolean):void");
    }

    public void setLastJobRunTime(String packageName, int userId, long elapsedRealtime) {
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.setLastJobRunTime(packageName, userId, elapsedRealtime);
        }
    }

    public long getTimeSinceLastJobRun(String packageName, int userId) {
        long timeSinceLastJobRun;
        long elapsedRealtime = this.mInjector.elapsedRealtime();
        synchronized (this.mAppIdleLock) {
            timeSinceLastJobRun = this.mAppIdleHistory.getTimeSinceLastJobRun(packageName, userId, elapsedRealtime);
        }
        return timeSinceLastJobRun;
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public void onUserRemoved(int userId) {
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.onUserRemoved(userId);
            synchronized (this.mActiveAdminApps) {
                this.mActiveAdminApps.remove(userId);
            }
        }
    }

    private boolean isAppIdleUnfiltered(String packageName, int userId, long elapsedRealtime) {
        boolean isIdle;
        synchronized (this.mAppIdleLock) {
            isIdle = this.mAppIdleHistory.isIdle(packageName, userId, elapsedRealtime);
        }
        return isIdle;
    }

    /* access modifiers changed from: package-private */
    public void addListener(UsageStatsManagerInternal.AppIdleStateChangeListener listener) {
        synchronized (this.mPackageAccessListeners) {
            if (!this.mPackageAccessListeners.contains(listener)) {
                this.mPackageAccessListeners.add(listener);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void removeListener(UsageStatsManagerInternal.AppIdleStateChangeListener listener) {
        synchronized (this.mPackageAccessListeners) {
            this.mPackageAccessListeners.remove(listener);
        }
    }

    /* access modifiers changed from: package-private */
    public int getAppId(String packageName) {
        try {
            return this.mPackageManager.getApplicationInfo(packageName, 4194816).uid;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAppIdleFilteredOrParoled(String packageName, int userId, long elapsedRealtime, boolean shouldObfuscateInstantApps) {
        if (isParoledOrCharging()) {
            return false;
        }
        if (shouldObfuscateInstantApps && this.mInjector.isPackageEphemeral(userId, packageName)) {
            return false;
        }
        return isAppIdleFiltered(packageName, getAppId(packageName), userId, elapsedRealtime);
    }

    /* access modifiers changed from: package-private */
    public boolean isAppSpecial(String packageName, int appId, int userId) {
        if (packageName == null) {
            return false;
        }
        if (!this.mAppIdleEnabled || appId < 10000 || packageName.equals(PackageManagerService.PLATFORM_PACKAGE_NAME)) {
            return true;
        }
        if (this.mSystemServicesReady) {
            try {
                if (this.mInjector.isPowerSaveWhitelistExceptIdleApp(packageName) || isActiveDeviceAdmin(packageName, userId) || isActiveNetworkScorer(packageName)) {
                    return true;
                }
                AppWidgetManager appWidgetManager = this.mAppWidgetManager;
                if ((appWidgetManager != null && this.mInjector.isBoundWidgetPackage(appWidgetManager, packageName, userId)) || isDeviceProvisioningPackage(packageName)) {
                    return true;
                }
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        if (isCarrierApp(packageName)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isAppIdleFiltered(String packageName, int appId, int userId, long elapsedRealtime) {
        if (isAppSpecial(packageName, appId, userId)) {
            return false;
        }
        return isAppIdleUnfiltered(packageName, userId, elapsedRealtime);
    }

    /* access modifiers changed from: package-private */
    public int[] getIdleUidsForUser(int userId) {
        if (!this.mAppIdleEnabled) {
            return new int[0];
        }
        long elapsedRealtime = this.mInjector.elapsedRealtime();
        try {
            ParceledListSlice<ApplicationInfo> slice = AppGlobals.getPackageManager().getInstalledApplications(0, userId);
            if (slice == null) {
                return new int[0];
            }
            List list = slice.getList();
            SparseIntArray uidStates = new SparseIntArray();
            for (int i = list.size() - 1; i >= 0; i--) {
                ApplicationInfo ai = (ApplicationInfo) list.get(i);
                boolean idle = isAppIdleFiltered(ai.packageName, UserHandle.getAppId(ai.uid), userId, elapsedRealtime);
                int index = uidStates.indexOfKey(ai.uid);
                int i2 = 65536;
                if (index < 0) {
                    int i3 = ai.uid;
                    if (!idle) {
                        i2 = 0;
                    }
                    uidStates.put(i3, i2 + 1);
                } else {
                    int valueAt = uidStates.valueAt(index) + 1;
                    if (!idle) {
                        i2 = 0;
                    }
                    uidStates.setValueAt(index, valueAt + i2);
                }
            }
            int numIdle = 0;
            for (int i4 = uidStates.size() - 1; i4 >= 0; i4--) {
                int value = uidStates.valueAt(i4);
                if ((value & 32767) == (value >> 16)) {
                    numIdle++;
                }
            }
            int[] res = new int[numIdle];
            int numIdle2 = 0;
            for (int i5 = uidStates.size() - 1; i5 >= 0; i5--) {
                int value2 = uidStates.valueAt(i5);
                if ((value2 & 32767) == (value2 >> 16)) {
                    res[numIdle2] = uidStates.keyAt(i5);
                    numIdle2++;
                }
            }
            return res;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* access modifiers changed from: package-private */
    public void setAppIdleAsync(String packageName, boolean idle, int userId) {
        if (packageName != null && this.mAppIdleEnabled) {
            this.mHandler.obtainMessage(4, userId, idle, packageName).sendToTarget();
        }
    }

    public int getAppStandbyBucket(String packageName, int userId, long elapsedRealtime, boolean shouldObfuscateInstantApps) {
        int appStandbyBucket;
        if (!this.mAppIdleEnabled) {
            return 10;
        }
        if (shouldObfuscateInstantApps && this.mInjector.isPackageEphemeral(userId, packageName)) {
            return 10;
        }
        synchronized (this.mAppIdleLock) {
            appStandbyBucket = this.mAppIdleHistory.getAppStandbyBucket(packageName, userId, elapsedRealtime);
        }
        return appStandbyBucket;
    }

    public List<AppStandbyInfo> getAppStandbyBuckets(int userId) {
        ArrayList<AppStandbyInfo> appStandbyBuckets;
        synchronized (this.mAppIdleLock) {
            appStandbyBuckets = this.mAppIdleHistory.getAppStandbyBuckets(userId, this.mAppIdleEnabled);
        }
        return appStandbyBuckets;
    }

    /* access modifiers changed from: package-private */
    public void setAppStandbyBucket(String packageName, int userId, int newBucket, int reason, long elapsedRealtime) {
        setAppStandbyBucket(packageName, userId, newBucket, reason, elapsedRealtime, false);
    }

    /* access modifiers changed from: package-private */
    public void setAppStandbyBucket(String packageName, int userId, int newBucket, int reason, long elapsedRealtime, boolean resetTimeout) {
        int reason2;
        int newBucket2;
        String str = packageName;
        int i = userId;
        int i2 = newBucket;
        long j = elapsedRealtime;
        synchronized (this.mAppIdleLock) {
            try {
                boolean z = false;
                if (this.mInjector.isPackageInstalled(str, 0, i)) {
                    AppIdleHistory.AppUsageHistory app = this.mAppIdleHistory.getAppUsageHistory(str, i, j);
                    if ((reason & JobPackageTracker.EVENT_STOP_REASON_MASK) == 1280) {
                        z = true;
                    }
                    boolean predicted = z;
                    if (app.currentBucket >= 10) {
                        if ((app.currentBucket != 50 && i2 != 50) || !predicted) {
                            if ((app.bucketingReason & JobPackageTracker.EVENT_STOP_REASON_MASK) != 1024 || !predicted) {
                                if (predicted) {
                                    long elapsedTimeAdjusted = this.mAppIdleHistory.getElapsedTime(j);
                                    this.mAppIdleHistory.updateLastPrediction(app, elapsedTimeAdjusted, i2);
                                    if (i2 > 10 && app.bucketActiveTimeoutTime > elapsedTimeAdjusted) {
                                        try {
                                            newBucket2 = 10;
                                            reason2 = app.bucketingReason;
                                            this.mAppIdleHistory.setAppStandbyBucket(packageName, userId, elapsedRealtime, newBucket2, reason2, resetTimeout);
                                            maybeInformListeners(packageName, userId, elapsedRealtime, newBucket2, reason2, false);
                                        } catch (Throwable th) {
                                            th = th;
                                            int i3 = reason;
                                            while (true) {
                                                try {
                                                    break;
                                                } catch (Throwable th2) {
                                                    th = th2;
                                                }
                                            }
                                            throw th;
                                        }
                                    } else if (i2 > 20) {
                                        if (app.bucketWorkingSetTimeoutTime > elapsedTimeAdjusted) {
                                            if (app.currentBucket != 20) {
                                                newBucket2 = 20;
                                                reason2 = 775;
                                            } else {
                                                newBucket2 = 20;
                                                reason2 = app.bucketingReason;
                                            }
                                            this.mAppIdleHistory.setAppStandbyBucket(packageName, userId, elapsedRealtime, newBucket2, reason2, resetTimeout);
                                            maybeInformListeners(packageName, userId, elapsedRealtime, newBucket2, reason2, false);
                                        }
                                    }
                                }
                                reason2 = reason;
                                newBucket2 = i2;
                                try {
                                    this.mAppIdleHistory.setAppStandbyBucket(packageName, userId, elapsedRealtime, newBucket2, reason2, resetTimeout);
                                    maybeInformListeners(packageName, userId, elapsedRealtime, newBucket2, reason2, false);
                                } catch (Throwable th3) {
                                    th = th3;
                                    int i4 = newBucket2;
                                    while (true) {
                                        break;
                                    }
                                    throw th;
                                }
                            }
                        }
                    }
                }
            } catch (Throwable th4) {
                th = th4;
                int i5 = reason;
                while (true) {
                    break;
                }
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isActiveDeviceAdmin(String packageName, int userId) {
        boolean z;
        synchronized (this.mActiveAdminApps) {
            Set<String> adminPkgs = this.mActiveAdminApps.get(userId);
            z = adminPkgs != null && adminPkgs.contains(packageName);
        }
        return z;
    }

    public void addActiveDeviceAdmin(String adminPkg, int userId) {
        synchronized (this.mActiveAdminApps) {
            Set<String> adminPkgs = this.mActiveAdminApps.get(userId);
            if (adminPkgs == null) {
                adminPkgs = new ArraySet<>();
                this.mActiveAdminApps.put(userId, adminPkgs);
            }
            adminPkgs.add(adminPkg);
        }
    }

    public void setActiveAdminApps(Set<String> adminPkgs, int userId) {
        synchronized (this.mActiveAdminApps) {
            if (adminPkgs == null) {
                this.mActiveAdminApps.remove(userId);
            } else {
                this.mActiveAdminApps.put(userId, adminPkgs);
            }
        }
    }

    public void onAdminDataAvailable() {
        this.mAdminDataAvailableLatch.countDown();
    }

    /* access modifiers changed from: private */
    public void waitForAdminData() {
        if (this.mContext.getPackageManager().hasSystemFeature("android.software.device_admin")) {
            ConcurrentUtils.waitForCountDownNoInterrupt(this.mAdminDataAvailableLatch, 10000, "Wait for admin data");
        }
    }

    /* access modifiers changed from: package-private */
    public Set<String> getActiveAdminAppsForTest(int userId) {
        Set<String> set;
        synchronized (this.mActiveAdminApps) {
            set = this.mActiveAdminApps.get(userId);
        }
        return set;
    }

    private boolean isDeviceProvisioningPackage(String packageName) {
        String deviceProvisioningPackage = this.mContext.getResources().getString(17039741);
        return deviceProvisioningPackage != null && deviceProvisioningPackage.equals(packageName);
    }

    private boolean isCarrierApp(String packageName) {
        synchronized (this.mAppIdleLock) {
            if (!this.mHaveCarrierPrivilegedApps) {
                fetchCarrierPrivilegedAppsLocked();
            }
            if (this.mCarrierPrivilegedApps == null) {
                return false;
            }
            boolean contains = this.mCarrierPrivilegedApps.contains(packageName);
            return contains;
        }
    }

    /* access modifiers changed from: package-private */
    public void clearCarrierPrivilegedApps() {
        synchronized (this.mAppIdleLock) {
            this.mHaveCarrierPrivilegedApps = false;
            this.mCarrierPrivilegedApps = null;
        }
    }

    @GuardedBy({"mAppIdleLock"})
    private void fetchCarrierPrivilegedAppsLocked() {
        this.mCarrierPrivilegedApps = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).getPackagesWithCarrierPrivilegesForAllPhones();
        this.mHaveCarrierPrivilegedApps = true;
    }

    private boolean isActiveNetworkScorer(String packageName) {
        return packageName != null && packageName.equals(this.mInjector.getActiveNetworkScorer());
    }

    /* access modifiers changed from: package-private */
    public void informListeners(String packageName, int userId, int bucket, int reason, boolean userInteraction) {
        boolean idle = bucket >= 40;
        synchronized (this.mPackageAccessListeners) {
            Iterator<UsageStatsManagerInternal.AppIdleStateChangeListener> it = this.mPackageAccessListeners.iterator();
            while (it.hasNext()) {
                UsageStatsManagerInternal.AppIdleStateChangeListener next = it.next();
                UsageStatsManagerInternal.AppIdleStateChangeListener listener = next;
                next.onAppIdleStateChanged(packageName, userId, idle, bucket, reason);
                if (userInteraction) {
                    listener.onUserInteractionStarted(packageName, userId);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void informParoleStateChanged() {
        boolean paroled = isParoledOrCharging();
        synchronized (this.mPackageAccessListeners) {
            Iterator<UsageStatsManagerInternal.AppIdleStateChangeListener> it = this.mPackageAccessListeners.iterator();
            while (it.hasNext()) {
                it.next().onParoleStateChanged(paroled);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void flushToDisk(int userId) {
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.writeAppIdleTimes(userId);
        }
    }

    /* access modifiers changed from: package-private */
    public void flushDurationsToDisk() {
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.writeAppIdleDurations();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isDisplayOn() {
        return this.mInjector.isDefaultDisplayOn();
    }

    /* access modifiers changed from: package-private */
    public void clearAppIdleForPackage(String packageName, int userId) {
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.clearUsage(packageName, userId);
        }
    }

    private class PackageReceiver extends BroadcastReceiver {
        private PackageReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.PACKAGE_ADDED".equals(action) || "android.intent.action.PACKAGE_CHANGED".equals(action)) {
                AppStandbyController.this.clearCarrierPrivilegedApps();
            }
            if (("android.intent.action.PACKAGE_REMOVED".equals(action) || "android.intent.action.PACKAGE_ADDED".equals(action)) && !intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                AppStandbyController.this.clearAppIdleForPackage(intent.getData().getSchemeSpecificPart(), getSendingUserId());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void initializeDefaultsForSystemApps(int userId) {
        Object obj;
        int i = userId;
        if (!this.mSystemServicesReady) {
            this.mPendingInitializeDefaults = true;
            return;
        }
        Slog.d(TAG, "Initializing defaults for system apps on user " + i + ", appIdleEnabled=" + this.mAppIdleEnabled);
        long elapsedRealtime = this.mInjector.elapsedRealtime();
        List<PackageInfo> packages = this.mPackageManager.getInstalledPackagesAsUser(512, i);
        int packageCount = packages.size();
        Object obj2 = this.mAppIdleLock;
        synchronized (obj2) {
            int i2 = 0;
            while (i2 < packageCount) {
                try {
                    PackageInfo pi = packages.get(i2);
                    String packageName = pi.packageName;
                    if (pi.applicationInfo == null || !pi.applicationInfo.isSystemApp()) {
                        obj = obj2;
                        PackageInfo packageInfo = pi;
                    } else {
                        obj = obj2;
                        PackageInfo packageInfo2 = pi;
                        this.mAppIdleHistory.reportUsage(packageName, userId, 10, 6, 0, elapsedRealtime + this.mSystemUpdateUsageTimeoutMillis);
                    }
                    i2++;
                    obj2 = obj;
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            }
            Object obj3 = obj2;
            this.mAppIdleHistory.writeAppIdleTimes(i);
        }
    }

    /* access modifiers changed from: package-private */
    public void postReportContentProviderUsage(String name, String packageName, int userId) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = name;
        args.arg2 = packageName;
        args.arg3 = Integer.valueOf(userId);
        this.mHandler.obtainMessage(8, args).sendToTarget();
    }

    /* access modifiers changed from: package-private */
    public void postReportSyncScheduled(String packageName, int userId, boolean exempted) {
        this.mHandler.obtainMessage(12, userId, exempted, packageName).sendToTarget();
    }

    /* access modifiers changed from: package-private */
    public void postReportExemptedSyncStart(String packageName, int userId) {
        this.mHandler.obtainMessage(13, userId, 0, packageName).sendToTarget();
    }

    /* access modifiers changed from: package-private */
    public void dumpUser(IndentingPrintWriter idpw, int userId, String pkg) {
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.dump(idpw, userId, pkg);
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpState(String[] args, PrintWriter pw) {
        synchronized (this.mAppIdleLock) {
            pw.println("Carrier privileged apps (have=" + this.mHaveCarrierPrivilegedApps + "): " + this.mCarrierPrivilegedApps);
        }
        long now = System.currentTimeMillis();
        pw.println();
        pw.println("Settings:");
        pw.print("  mCheckIdleIntervalMillis=");
        TimeUtils.formatDuration(this.mCheckIdleIntervalMillis, pw);
        pw.println();
        pw.print("  mAppIdleParoleIntervalMillis=");
        TimeUtils.formatDuration(this.mAppIdleParoleIntervalMillis, pw);
        pw.println();
        pw.print("  mAppIdleParoleWindowMillis=");
        TimeUtils.formatDuration(this.mAppIdleParoleWindowMillis, pw);
        pw.println();
        pw.print("  mAppIdleParoleDurationMillis=");
        TimeUtils.formatDuration(this.mAppIdleParoleDurationMillis, pw);
        pw.println();
        pw.print("  mStrongUsageTimeoutMillis=");
        TimeUtils.formatDuration(this.mStrongUsageTimeoutMillis, pw);
        pw.println();
        pw.print("  mNotificationSeenTimeoutMillis=");
        TimeUtils.formatDuration(this.mNotificationSeenTimeoutMillis, pw);
        pw.println();
        pw.print("  mSyncAdapterTimeoutMillis=");
        TimeUtils.formatDuration(this.mSyncAdapterTimeoutMillis, pw);
        pw.println();
        pw.print("  mSystemInteractionTimeoutMillis=");
        TimeUtils.formatDuration(this.mSystemInteractionTimeoutMillis, pw);
        pw.println();
        pw.print("  mInitialForegroundServiceStartTimeoutMillis=");
        TimeUtils.formatDuration(this.mInitialForegroundServiceStartTimeoutMillis, pw);
        pw.println();
        pw.print("  mPredictionTimeoutMillis=");
        TimeUtils.formatDuration(this.mPredictionTimeoutMillis, pw);
        pw.println();
        pw.print("  mExemptedSyncScheduledNonDozeTimeoutMillis=");
        TimeUtils.formatDuration(this.mExemptedSyncScheduledNonDozeTimeoutMillis, pw);
        pw.println();
        pw.print("  mExemptedSyncScheduledDozeTimeoutMillis=");
        TimeUtils.formatDuration(this.mExemptedSyncScheduledDozeTimeoutMillis, pw);
        pw.println();
        pw.print("  mExemptedSyncStartTimeoutMillis=");
        TimeUtils.formatDuration(this.mExemptedSyncStartTimeoutMillis, pw);
        pw.println();
        pw.print("  mUnexemptedSyncScheduledTimeoutMillis=");
        TimeUtils.formatDuration(this.mUnexemptedSyncScheduledTimeoutMillis, pw);
        pw.println();
        pw.print("  mSystemUpdateUsageTimeoutMillis=");
        TimeUtils.formatDuration(this.mSystemUpdateUsageTimeoutMillis, pw);
        pw.println();
        pw.print("  mStableChargingThresholdMillis=");
        TimeUtils.formatDuration(this.mStableChargingThresholdMillis, pw);
        pw.println();
        pw.println();
        pw.print("mAppIdleEnabled=");
        pw.print(this.mAppIdleEnabled);
        pw.print(" mAppIdleTempParoled=");
        pw.print(this.mAppIdleTempParoled);
        pw.print(" mCharging=");
        pw.print(this.mCharging);
        pw.print(" mChargingStable=");
        pw.print(this.mChargingStable);
        pw.print(" mLastAppIdleParoledTime=");
        TimeUtils.formatDuration(now - this.mLastAppIdleParoledTime, pw);
        pw.println();
        pw.print("mScreenThresholds=");
        pw.println(Arrays.toString(this.mAppStandbyScreenThresholds));
        pw.print("mElapsedThresholds=");
        pw.println(Arrays.toString(this.mAppStandbyElapsedThresholds));
        pw.print("mStableChargingThresholdMillis=");
        TimeUtils.formatDuration(this.mStableChargingThresholdMillis, pw);
        pw.println();
    }

    static class Injector {
        private IBatteryStats mBatteryStats;
        int mBootPhase;
        private final Context mContext;
        private IDeviceIdleController mDeviceIdleController;
        private DisplayManager mDisplayManager;
        private final Looper mLooper;
        private PackageManagerInternal mPackageManagerInternal;
        private PowerManager mPowerManager;

        Injector(Context context, Looper looper) {
            this.mContext = context;
            this.mLooper = looper;
        }

        /* access modifiers changed from: package-private */
        public Context getContext() {
            return this.mContext;
        }

        /* access modifiers changed from: package-private */
        public Looper getLooper() {
            return this.mLooper;
        }

        /* access modifiers changed from: package-private */
        public void onBootPhase(int phase) {
            if (phase == 500) {
                this.mDeviceIdleController = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
                this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
                this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
                this.mDisplayManager = (DisplayManager) this.mContext.getSystemService("display");
                this.mPowerManager = (PowerManager) this.mContext.getSystemService(PowerManager.class);
            }
            this.mBootPhase = phase;
        }

        /* access modifiers changed from: package-private */
        public int getBootPhase() {
            return this.mBootPhase;
        }

        /* access modifiers changed from: package-private */
        public long elapsedRealtime() {
            return SystemClock.elapsedRealtime();
        }

        /* access modifiers changed from: package-private */
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }

        /* access modifiers changed from: package-private */
        public boolean isAppIdleEnabled() {
            boolean buildFlag = this.mContext.getResources().getBoolean(17891434);
            boolean runtimeFlag = Settings.Global.getInt(this.mContext.getContentResolver(), "app_standby_enabled", 1) == 1 && Settings.Global.getInt(this.mContext.getContentResolver(), "adaptive_battery_management_enabled", 1) == 1;
            if (!buildFlag || !runtimeFlag) {
                return false;
            }
            return true;
        }

        /* access modifiers changed from: package-private */
        public boolean isCharging() {
            return ((BatteryManager) this.mContext.getSystemService(BatteryManager.class)).isCharging();
        }

        /* access modifiers changed from: package-private */
        public boolean isPowerSaveWhitelistExceptIdleApp(String packageName) throws RemoteException {
            return this.mDeviceIdleController.isPowerSaveWhitelistExceptIdleApp(packageName);
        }

        /* access modifiers changed from: package-private */
        public File getDataSystemDirectory() {
            return Environment.getDataSystemDirectory();
        }

        /* access modifiers changed from: package-private */
        public void noteEvent(int event, String packageName, int uid) throws RemoteException {
            this.mBatteryStats.noteEvent(event, packageName, uid);
        }

        /* access modifiers changed from: package-private */
        public boolean isPackageEphemeral(int userId, String packageName) {
            return this.mPackageManagerInternal.isPackageEphemeral(userId, packageName);
        }

        /* access modifiers changed from: package-private */
        public boolean isPackageInstalled(String packageName, int flags, int userId) {
            return this.mPackageManagerInternal.getPackageUid(packageName, flags, userId) >= 0;
        }

        /* access modifiers changed from: package-private */
        public int[] getRunningUserIds() throws RemoteException {
            return ActivityManager.getService().getRunningUserIds();
        }

        /* access modifiers changed from: package-private */
        public boolean isDefaultDisplayOn() {
            return this.mDisplayManager.getDisplay(0).getState() == 2;
        }

        /* access modifiers changed from: package-private */
        public void registerDisplayListener(DisplayManager.DisplayListener listener, Handler handler) {
            this.mDisplayManager.registerDisplayListener(listener, handler);
        }

        /* access modifiers changed from: package-private */
        public String getActiveNetworkScorer() {
            return ((NetworkScoreManager) this.mContext.getSystemService("network_score")).getActiveScorerPackage();
        }

        public boolean isBoundWidgetPackage(AppWidgetManager appWidgetManager, String packageName, int userId) {
            return appWidgetManager.isBoundWidgetPackage(packageName, userId);
        }

        /* access modifiers changed from: package-private */
        public String getAppIdleSettings() {
            return Settings.Global.getString(this.mContext.getContentResolver(), "app_idle_constants");
        }

        public boolean isDeviceIdleMode() {
            return this.mPowerManager.isDeviceIdleMode();
        }
    }

    class AppStandbyHandler extends Handler {
        AppStandbyHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            boolean exempted = true;
            switch (msg.what) {
                case 3:
                    StandbyUpdateRecord r = (StandbyUpdateRecord) msg.obj;
                    AppStandbyController.this.informListeners(r.packageName, r.userId, r.bucket, r.reason, r.isUserInteraction);
                    r.recycle();
                    return;
                case 4:
                    AppStandbyController appStandbyController = AppStandbyController.this;
                    String str = (String) msg.obj;
                    int i = msg.arg1;
                    if (msg.arg2 != 1) {
                        exempted = false;
                    }
                    appStandbyController.forceIdleState(str, i, exempted);
                    return;
                case 5:
                    if (AppStandbyController.this.checkIdleStates(msg.arg1) && AppStandbyController.this.mAppIdleEnabled) {
                        AppStandbyController.this.mHandler.sendMessageDelayed(AppStandbyController.this.mHandler.obtainMessage(5, msg.arg1, 0), AppStandbyController.this.mCheckIdleIntervalMillis);
                        return;
                    }
                    return;
                case 6:
                    AppStandbyController.this.checkParoleTimeout();
                    return;
                case 7:
                    AppStandbyController.this.setAppIdleParoled(false);
                    return;
                case 8:
                    SomeArgs args = (SomeArgs) msg.obj;
                    AppStandbyController.this.reportContentProviderUsage((String) args.arg1, (String) args.arg2, ((Integer) args.arg3).intValue());
                    args.recycle();
                    return;
                case 9:
                    AppStandbyController.this.informParoleStateChanged();
                    return;
                case 10:
                    AppStandbyController.this.mHandler.removeMessages(10);
                    AppStandbyController.this.waitForAdminData();
                    AppStandbyController.this.checkIdleStates(-1);
                    return;
                case 11:
                    AppStandbyController.this.checkAndUpdateStandbyState((String) msg.obj, msg.arg1, msg.arg2, AppStandbyController.this.mInjector.elapsedRealtime());
                    return;
                case 12:
                    if (msg.arg1 <= 0) {
                        exempted = false;
                    }
                    if (exempted) {
                        AppStandbyController.this.reportExemptedSyncScheduled((String) msg.obj, msg.arg1);
                        return;
                    } else {
                        AppStandbyController.this.reportUnexemptedSyncScheduled((String) msg.obj, msg.arg1);
                        return;
                    }
                case 13:
                    AppStandbyController.this.reportExemptedSyncStart((String) msg.obj, msg.arg1);
                    return;
                case 14:
                    AppStandbyController.this.updateChargingStableState();
                    return;
                default:
                    super.handleMessage(msg);
                    return;
            }
        }
    }

    private class DeviceStateReceiver extends BroadcastReceiver {
        private DeviceStateReceiver() {
        }

        /* JADX WARNING: Removed duplicated region for block: B:17:0x003c  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x004d  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r7, android.content.Intent r8) {
            /*
                r6 = this;
                java.lang.String r0 = r8.getAction()
                int r1 = r0.hashCode()
                r2 = -54942926(0xfffffffffcb9a332, float:-7.711079E36)
                r3 = 0
                r4 = 2
                r5 = 1
                if (r1 == r2) goto L_0x002f
                r2 = 870701415(0x33e5d967, float:1.0703189E-7)
                if (r1 == r2) goto L_0x0025
                r2 = 948344062(0x388694fe, float:6.41737E-5)
                if (r1 == r2) goto L_0x001b
            L_0x001a:
                goto L_0x0039
            L_0x001b:
                java.lang.String r1 = "android.os.action.CHARGING"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x001a
                r0 = r3
                goto L_0x003a
            L_0x0025:
                java.lang.String r1 = "android.os.action.DEVICE_IDLE_MODE_CHANGED"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x001a
                r0 = r4
                goto L_0x003a
            L_0x002f:
                java.lang.String r1 = "android.os.action.DISCHARGING"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x001a
                r0 = r5
                goto L_0x003a
            L_0x0039:
                r0 = -1
            L_0x003a:
                if (r0 == 0) goto L_0x004d
                if (r0 == r5) goto L_0x0047
                if (r0 == r4) goto L_0x0041
                goto L_0x0053
            L_0x0041:
                com.android.server.usage.AppStandbyController r0 = com.android.server.usage.AppStandbyController.this
                r0.onDeviceIdleModeChanged()
                goto L_0x0053
            L_0x0047:
                com.android.server.usage.AppStandbyController r0 = com.android.server.usage.AppStandbyController.this
                r0.setChargingState(r3)
                goto L_0x0053
            L_0x004d:
                com.android.server.usage.AppStandbyController r0 = com.android.server.usage.AppStandbyController.this
                r0.setChargingState(r5)
            L_0x0053:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.AppStandbyController.DeviceStateReceiver.onReceive(android.content.Context, android.content.Intent):void");
        }
    }

    private class SettingsObserver extends ContentObserver {
        public static final long DEFAULT_EXEMPTED_SYNC_SCHEDULED_DOZE_TIMEOUT = 14400000;
        public static final long DEFAULT_EXEMPTED_SYNC_SCHEDULED_NON_DOZE_TIMEOUT = 600000;
        public static final long DEFAULT_EXEMPTED_SYNC_START_TIMEOUT = 600000;
        public static final long DEFAULT_INITIAL_FOREGROUND_SERVICE_START_TIMEOUT = 1800000;
        public static final long DEFAULT_NOTIFICATION_TIMEOUT = 43200000;
        public static final long DEFAULT_STABLE_CHARGING_THRESHOLD = 600000;
        public static final long DEFAULT_STRONG_USAGE_TIMEOUT = 3600000;
        public static final long DEFAULT_SYNC_ADAPTER_TIMEOUT = 600000;
        public static final long DEFAULT_SYSTEM_INTERACTION_TIMEOUT = 600000;
        public static final long DEFAULT_SYSTEM_UPDATE_TIMEOUT = 7200000;
        public static final long DEFAULT_UNEXEMPTED_SYNC_SCHEDULED_TIMEOUT = 600000;
        private static final String KEY_ELAPSED_TIME_THRESHOLDS = "elapsed_thresholds";
        private static final String KEY_EXEMPTED_SYNC_SCHEDULED_DOZE_HOLD_DURATION = "exempted_sync_scheduled_d_duration";
        private static final String KEY_EXEMPTED_SYNC_SCHEDULED_NON_DOZE_HOLD_DURATION = "exempted_sync_scheduled_nd_duration";
        private static final String KEY_EXEMPTED_SYNC_START_HOLD_DURATION = "exempted_sync_start_duration";
        @Deprecated
        private static final String KEY_IDLE_DURATION = "idle_duration2";
        @Deprecated
        private static final String KEY_IDLE_DURATION_OLD = "idle_duration";
        private static final String KEY_INITIAL_FOREGROUND_SERVICE_START_HOLD_DURATION = "initial_foreground_service_start_duration";
        private static final String KEY_NOTIFICATION_SEEN_HOLD_DURATION = "notification_seen_duration";
        private static final String KEY_PAROLE_DURATION = "parole_duration";
        private static final String KEY_PAROLE_INTERVAL = "parole_interval";
        private static final String KEY_PAROLE_WINDOW = "parole_window";
        private static final String KEY_PREDICTION_TIMEOUT = "prediction_timeout";
        private static final String KEY_SCREEN_TIME_THRESHOLDS = "screen_thresholds";
        private static final String KEY_STABLE_CHARGING_THRESHOLD = "stable_charging_threshold";
        private static final String KEY_STRONG_USAGE_HOLD_DURATION = "strong_usage_duration";
        private static final String KEY_SYNC_ADAPTER_HOLD_DURATION = "sync_adapter_duration";
        private static final String KEY_SYSTEM_INTERACTION_HOLD_DURATION = "system_interaction_duration";
        private static final String KEY_SYSTEM_UPDATE_HOLD_DURATION = "system_update_usage_duration";
        private static final String KEY_UNEXEMPTED_SYNC_SCHEDULED_HOLD_DURATION = "unexempted_sync_scheduled_duration";
        @Deprecated
        private static final String KEY_WALLCLOCK_THRESHOLD = "wallclock_threshold";
        private final KeyValueListParser mParser = new KeyValueListParser(',');

        SettingsObserver(Handler handler) {
            super(handler);
        }

        /* access modifiers changed from: package-private */
        public void registerObserver() {
            ContentResolver cr = AppStandbyController.this.mContext.getContentResolver();
            cr.registerContentObserver(Settings.Global.getUriFor("app_idle_constants"), false, this);
            cr.registerContentObserver(Settings.Global.getUriFor("app_standby_enabled"), false, this);
            cr.registerContentObserver(Settings.Global.getUriFor("adaptive_battery_management_enabled"), false, this);
        }

        public void onChange(boolean selfChange) {
            updateSettings();
            AppStandbyController.this.postOneTimeCheckIdleStates();
        }

        /* access modifiers changed from: package-private */
        public void updateSettings() {
            try {
                this.mParser.setString(AppStandbyController.this.mInjector.getAppIdleSettings());
            } catch (IllegalArgumentException e) {
                Slog.e(AppStandbyController.TAG, "Bad value for app idle settings: " + e.getMessage());
            }
            synchronized (AppStandbyController.this.mAppIdleLock) {
                AppStandbyController.this.mAppIdleParoleIntervalMillis = this.mParser.getDurationMillis(KEY_PAROLE_INTERVAL, 86400000);
                AppStandbyController.this.mAppIdleParoleWindowMillis = this.mParser.getDurationMillis(KEY_PAROLE_WINDOW, 7200000);
                AppStandbyController.this.mAppIdleParoleDurationMillis = this.mParser.getDurationMillis(KEY_PAROLE_DURATION, 600000);
                String screenThresholdsValue = this.mParser.getString(KEY_SCREEN_TIME_THRESHOLDS, (String) null);
                AppStandbyController.this.mAppStandbyScreenThresholds = parseLongArray(screenThresholdsValue, AppStandbyController.SCREEN_TIME_THRESHOLDS);
                String elapsedThresholdsValue = this.mParser.getString(KEY_ELAPSED_TIME_THRESHOLDS, (String) null);
                AppStandbyController.this.mAppStandbyElapsedThresholds = parseLongArray(elapsedThresholdsValue, AppStandbyController.ELAPSED_TIME_THRESHOLDS);
                AppStandbyController.this.mCheckIdleIntervalMillis = Math.min(AppStandbyController.this.mAppStandbyElapsedThresholds[1] / 4, 14400000);
                AppStandbyController.this.mStrongUsageTimeoutMillis = this.mParser.getDurationMillis(KEY_STRONG_USAGE_HOLD_DURATION, 3600000);
                AppStandbyController.this.mNotificationSeenTimeoutMillis = this.mParser.getDurationMillis(KEY_NOTIFICATION_SEEN_HOLD_DURATION, 43200000);
                AppStandbyController.this.mSystemUpdateUsageTimeoutMillis = this.mParser.getDurationMillis(KEY_SYSTEM_UPDATE_HOLD_DURATION, 7200000);
                AppStandbyController.this.mPredictionTimeoutMillis = this.mParser.getDurationMillis(KEY_PREDICTION_TIMEOUT, 43200000);
                AppStandbyController.this.mSyncAdapterTimeoutMillis = this.mParser.getDurationMillis(KEY_SYNC_ADAPTER_HOLD_DURATION, 600000);
                AppStandbyController.this.mExemptedSyncScheduledNonDozeTimeoutMillis = this.mParser.getDurationMillis(KEY_EXEMPTED_SYNC_SCHEDULED_NON_DOZE_HOLD_DURATION, 600000);
                AppStandbyController.this.mExemptedSyncScheduledDozeTimeoutMillis = this.mParser.getDurationMillis(KEY_EXEMPTED_SYNC_SCHEDULED_DOZE_HOLD_DURATION, 14400000);
                AppStandbyController.this.mExemptedSyncStartTimeoutMillis = this.mParser.getDurationMillis(KEY_EXEMPTED_SYNC_START_HOLD_DURATION, 600000);
                AppStandbyController.this.mUnexemptedSyncScheduledTimeoutMillis = this.mParser.getDurationMillis(KEY_EXEMPTED_SYNC_SCHEDULED_DOZE_HOLD_DURATION, 600000);
                AppStandbyController.this.mSystemInteractionTimeoutMillis = this.mParser.getDurationMillis(KEY_SYSTEM_INTERACTION_HOLD_DURATION, 600000);
                AppStandbyController.this.mInitialForegroundServiceStartTimeoutMillis = this.mParser.getDurationMillis(KEY_INITIAL_FOREGROUND_SERVICE_START_HOLD_DURATION, 1800000);
                AppStandbyController.this.mStableChargingThresholdMillis = this.mParser.getDurationMillis(KEY_STABLE_CHARGING_THRESHOLD, 600000);
            }
            AppStandbyController appStandbyController = AppStandbyController.this;
            appStandbyController.setAppIdleEnabled(appStandbyController.mInjector.isAppIdleEnabled());
        }

        /* access modifiers changed from: package-private */
        public long[] parseLongArray(String values, long[] defaults) {
            if (values == null || values.isEmpty()) {
                return defaults;
            }
            String[] thresholds = values.split(SliceClientPermissions.SliceAuthority.DELIMITER);
            if (thresholds.length != AppStandbyController.THRESHOLD_BUCKETS.length) {
                return defaults;
            }
            long[] array = new long[AppStandbyController.THRESHOLD_BUCKETS.length];
            int i = 0;
            while (i < AppStandbyController.THRESHOLD_BUCKETS.length) {
                try {
                    if (!thresholds[i].startsWith("P")) {
                        if (!thresholds[i].startsWith("p")) {
                            array[i] = Long.parseLong(thresholds[i]);
                            i++;
                        }
                    }
                    array[i] = Duration.parse(thresholds[i]).toMillis();
                    i++;
                } catch (NumberFormatException | DateTimeParseException e) {
                    return defaults;
                }
            }
            return array;
        }
    }
}
