package com.android.server.usage;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.IUidObserver;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManagerInternal;
import android.app.usage.AppStandbyInfo;
import android.app.usage.ConfigurationStats;
import android.app.usage.EventStats;
import android.app.usage.IUsageStatsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IDeviceIdleController;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.pm.DumpState;
import com.android.server.pm.PackageManagerService;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.usage.AppTimeLimitController;
import com.android.server.usage.UserUsageStatsService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class UsageStatsService extends SystemService implements UserUsageStatsService.StatsUpdatedListener {
    static final boolean COMPRESS_TIME = false;
    static final boolean DEBUG = false;
    private static final boolean ENABLE_KERNEL_UPDATES = true;
    public static final boolean ENABLE_TIME_CHANGE_CORRECTION = SystemProperties.getBoolean("persist.debug.time_correction", true);
    private static final long FLUSH_INTERVAL = 1200000;
    /* access modifiers changed from: private */
    public static final File KERNEL_COUNTER_FILE = new File("/proc/uid_procstat/set");
    static final int MSG_FLUSH_TO_DISK = 1;
    static final int MSG_REMOVE_USER = 2;
    static final int MSG_REPORT_EVENT = 0;
    static final int MSG_REPORT_EVENT_TO_ALL_USERID = 4;
    static final int MSG_UID_STATE_CHANGED = 3;
    static final String TAG = "UsageStatsService";
    private static final long TEN_SECONDS = 10000;
    private static final long TIME_CHANGE_THRESHOLD_MILLIS = 2000;
    private static final char TOKEN_DELIMITER = '/';
    private static final long TWENTY_MINUTES = 1200000;
    AppOpsManager mAppOps;
    AppStandbyController mAppStandby;
    AppTimeLimitController mAppTimeLimit;
    IDeviceIdleController mDeviceIdleController;
    DevicePolicyManagerInternal mDpmInternal;
    Handler mHandler;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    PackageManager mPackageManager;
    PackageManagerInternal mPackageManagerInternal;
    PackageMonitor mPackageMonitor;
    long mRealTimeSnapshot;
    private UsageStatsManagerInternal.AppIdleStateChangeListener mStandbyChangeListener = new UsageStatsManagerInternal.AppIdleStateChangeListener() {
        public void onAppIdleStateChanged(String packageName, int userId, boolean idle, int bucket, int reason) {
            UsageEvents.Event event = new UsageEvents.Event(11, SystemClock.elapsedRealtime());
            event.mBucketAndReason = (bucket << 16) | (65535 & reason);
            event.mPackage = packageName;
            UsageStatsService.this.mHandler.obtainMessage(0, userId, 0, event).sendToTarget();
        }

        public void onParoleStateChanged(boolean isParoleOn) {
        }
    };
    long mSystemTimeSnapshot;
    private final IUidObserver mUidObserver = new IUidObserver.Stub() {
        public void onUidStateChanged(int uid, int procState, long procStateSeq) {
            UsageStatsService.this.mHandler.obtainMessage(3, uid, procState).sendToTarget();
        }

        public void onUidIdle(int uid, boolean disabled) {
        }

        public void onUidGone(int uid, boolean disabled) {
            onUidStateChanged(uid, 21, 0);
        }

        public void onUidActive(int uid) {
        }

        public void onUidCachedChanged(int uid, boolean cached) {
        }
    };
    /* access modifiers changed from: private */
    public final SparseIntArray mUidToKernelCounter = new SparseIntArray();
    final SparseArray<ArraySet<String>> mUsageReporters = new SparseArray<>();
    int mUsageSource;
    private File mUsageStatsDir;
    UserManager mUserManager;
    private final SparseArray<UserUsageStatsService> mUserState = new SparseArray<>();
    final SparseArray<ActivityData> mVisibleActivities = new SparseArray<>();

    private static class ActivityData {
        /* access modifiers changed from: private */
        public final String mTaskRootClass;
        /* access modifiers changed from: private */
        public final String mTaskRootPackage;

        private ActivityData(String taskRootPackage, String taskRootClass) {
            this.mTaskRootPackage = taskRootPackage;
            this.mTaskRootClass = taskRootClass;
        }
    }

    public UsageStatsService(Context context) {
        super(context);
    }

    /* JADX WARNING: type inference failed for: r3v8, types: [com.android.server.usage.UsageStatsService$BinderService, android.os.IBinder] */
    public void onStart() {
        this.mAppOps = (AppOpsManager) getContext().getSystemService("appops");
        this.mUserManager = (UserManager) getContext().getSystemService("user");
        this.mPackageManager = getContext().getPackageManager();
        this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mHandler = new H(BackgroundThread.get().getLooper());
        this.mAppStandby = new AppStandbyController(getContext(), BackgroundThread.get().getLooper());
        this.mAppTimeLimit = new AppTimeLimitController(new AppTimeLimitController.TimeLimitCallbackListener() {
            public void onLimitReached(int observerId, int userId, long timeLimit, long timeElapsed, PendingIntent callbackIntent) {
                if (callbackIntent != null) {
                    Intent intent = new Intent();
                    intent.putExtra("android.app.usage.extra.OBSERVER_ID", observerId);
                    intent.putExtra("android.app.usage.extra.TIME_LIMIT", timeLimit);
                    intent.putExtra("android.app.usage.extra.TIME_USED", timeElapsed);
                    try {
                        callbackIntent.send(UsageStatsService.this.getContext(), 0, intent);
                    } catch (PendingIntent.CanceledException e) {
                        Slog.w(UsageStatsService.TAG, "Couldn't deliver callback: " + callbackIntent);
                    }
                }
            }

            public void onSessionEnd(int observerId, int userId, long timeElapsed, PendingIntent callbackIntent) {
                if (callbackIntent != null) {
                    Intent intent = new Intent();
                    intent.putExtra("android.app.usage.extra.OBSERVER_ID", observerId);
                    intent.putExtra("android.app.usage.extra.TIME_USED", timeElapsed);
                    try {
                        callbackIntent.send(UsageStatsService.this.getContext(), 0, intent);
                    } catch (PendingIntent.CanceledException e) {
                        Slog.w(UsageStatsService.TAG, "Couldn't deliver callback: " + callbackIntent);
                    }
                }
            }
        }, this.mHandler.getLooper());
        this.mAppStandby.addListener(this.mStandbyChangeListener);
        this.mUsageStatsDir = new File(new File(Environment.getDataDirectory(), "system"), "usagestats");
        this.mUsageStatsDir.mkdirs();
        if (this.mUsageStatsDir.exists()) {
            IntentFilter filter = new IntentFilter("android.intent.action.USER_REMOVED");
            filter.addAction("android.intent.action.USER_STARTED");
            getContext().registerReceiverAsUser(new UserActionsReceiver(), UserHandle.ALL, filter, (String) null, this.mHandler);
            synchronized (this.mLock) {
                cleanUpRemovedUsersLocked();
            }
            this.mRealTimeSnapshot = SystemClock.elapsedRealtime();
            this.mSystemTimeSnapshot = System.currentTimeMillis();
            publishLocalService(UsageStatsManagerInternal.class, new LocalService());
            publishBinderService("usagestats", new BinderService());
            getUserDataAndInitializeIfNeededLocked(0, this.mSystemTimeSnapshot);
            return;
        }
        throw new IllegalStateException("Usage stats directory does not exist: " + this.mUsageStatsDir.getAbsolutePath());
    }

    public void onBootPhase(int phase) {
        this.mAppStandby.onBootPhase(phase);
        if (phase == 500) {
            getDpmInternal();
            this.mDeviceIdleController = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
            if (KERNEL_COUNTER_FILE.exists()) {
                try {
                    ActivityManager.getService().registerUidObserver(this.mUidObserver, 3, -1, (String) null);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Slog.w(TAG, "Missing procfs interface: " + KERNEL_COUNTER_FILE);
            }
            readUsageSourceSetting();
        }
    }

    /* access modifiers changed from: private */
    public DevicePolicyManagerInternal getDpmInternal() {
        if (this.mDpmInternal == null) {
            this.mDpmInternal = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
        }
        return this.mDpmInternal;
    }

    /* access modifiers changed from: private */
    public void readUsageSourceSetting() {
        synchronized (this.mLock) {
            this.mUsageSource = Settings.Global.getInt(getContext().getContentResolver(), "app_time_limit_usage_source", 1);
        }
    }

    private class UserActionsReceiver extends BroadcastReceiver {
        private UserActionsReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            int userId = intent.getIntExtra("android.intent.extra.user_handle", -1);
            String action = intent.getAction();
            if ("android.intent.action.USER_REMOVED".equals(action)) {
                if (userId >= 0) {
                    UsageStatsService.this.mHandler.obtainMessage(2, userId, 0).sendToTarget();
                }
            } else if ("android.intent.action.USER_STARTED".equals(action) && userId >= 0) {
                UsageStatsService.this.mAppStandby.postCheckIdleStates(userId);
            }
        }
    }

    public void onStatsUpdated() {
        this.mHandler.sendEmptyMessageDelayed(1, 1200000);
    }

    public void onStatsReloaded() {
        this.mAppStandby.postOneTimeCheckIdleStates();
    }

    public void onNewUpdate(int userId) {
        this.mAppStandby.initializeDefaultsForSystemApps(userId);
    }

    /* access modifiers changed from: private */
    public boolean shouldObfuscateInstantAppsForCaller(int callingUid, int userId) {
        return !this.mPackageManagerInternal.canAccessInstantApps(callingUid, userId);
    }

    private void cleanUpRemovedUsersLocked() {
        List<UserInfo> users = this.mUserManager.getUsers(true);
        if (users == null || users.size() == 0) {
            throw new IllegalStateException("There can't be no users");
        }
        ArraySet<String> toDelete = new ArraySet<>();
        String[] fileNames = this.mUsageStatsDir.list();
        if (fileNames != null) {
            toDelete.addAll(Arrays.asList(fileNames));
            int userCount = users.size();
            for (int i = 0; i < userCount; i++) {
                toDelete.remove(Integer.toString(users.get(i).id));
            }
            int i2 = toDelete.size();
            for (int i3 = 0; i3 < i2; i3++) {
                deleteRecursively(new File(this.mUsageStatsDir, toDelete.valueAt(i3)));
            }
        }
    }

    private static void deleteRecursively(File f) {
        File[] files = f.listFiles();
        if (files != null) {
            for (File subFile : files) {
                deleteRecursively(subFile);
            }
        }
        if (!f.delete()) {
            Slog.e(TAG, "Failed to delete " + f);
        }
    }

    /* access modifiers changed from: private */
    public UserUsageStatsService getUserDataAndInitializeIfNeededLocked(int userId, long currentTimeMillis) {
        UserUsageStatsService service = this.mUserState.get(userId);
        if (service != null) {
            return service;
        }
        UserUsageStatsService service2 = new UserUsageStatsService(getContext(), userId, new File(this.mUsageStatsDir, Integer.toString(userId)), this);
        service2.init(currentTimeMillis);
        this.mUserState.put(userId, service2);
        return service2;
    }

    /* access modifiers changed from: private */
    public long checkAndGetTimeLocked() {
        long actualSystemTime = System.currentTimeMillis();
        long actualRealtime = SystemClock.elapsedRealtime();
        long expectedSystemTime = (actualRealtime - this.mRealTimeSnapshot) + this.mSystemTimeSnapshot;
        long diffSystemTime = actualSystemTime - expectedSystemTime;
        if (Math.abs(diffSystemTime) > TIME_CHANGE_THRESHOLD_MILLIS && ENABLE_TIME_CHANGE_CORRECTION) {
            Slog.i(TAG, "Time changed in UsageStats by " + (diffSystemTime / 1000) + " seconds");
            int userCount = this.mUserState.size();
            for (int i = 0; i < userCount; i++) {
                this.mUserState.valueAt(i).onTimeChanged(expectedSystemTime, actualSystemTime);
            }
            this.mRealTimeSnapshot = actualRealtime;
            this.mSystemTimeSnapshot = actualSystemTime;
        }
        return actualSystemTime;
    }

    private void convertToSystemTimeLocked(UsageEvents.Event event) {
        event.mTimeStamp = Math.max(0, event.mTimeStamp - this.mRealTimeSnapshot) + this.mSystemTimeSnapshot;
    }

    /* access modifiers changed from: package-private */
    public void shutdown() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(0);
            UsageEvents.Event event = new UsageEvents.Event(26, SystemClock.elapsedRealtime());
            event.mPackage = PackageManagerService.PLATFORM_PACKAGE_NAME;
            reportEventToAllUserId(event);
            flushToDiskLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void prepareForPossibleShutdown() {
        UsageEvents.Event event = new UsageEvents.Event(26, SystemClock.elapsedRealtime());
        event.mPackage = PackageManagerService.PLATFORM_PACKAGE_NAME;
        this.mHandler.obtainMessage(4, event).sendToTarget();
        this.mHandler.sendEmptyMessage(1);
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
    /* access modifiers changed from: package-private */
    public void reportEvent(UsageEvents.Event event, int userId) {
        ArraySet<String> tokens;
        int size;
        UsageEvents.Event event2 = event;
        int i = userId;
        synchronized (this.mLock) {
            long timeNow = checkAndGetTimeLocked();
            long elapsedRealtime = SystemClock.elapsedRealtime();
            convertToSystemTimeLocked(event);
            if (event2.mPackage != null && this.mPackageManagerInternal.isPackageEphemeral(i, event2.mPackage)) {
                event2.mFlags |= 1;
            }
            int i2 = event2.mEventType;
            if (i2 != 1) {
                if (i2 != 2) {
                    if (i2 != 23) {
                        if (i2 == 24) {
                            event2.mEventType = 23;
                        }
                    }
                    ActivityData prevData = (ActivityData) this.mVisibleActivities.removeReturnOld(event2.mInstanceId);
                    if (prevData != null) {
                        synchronized (this.mUsageReporters) {
                            tokens = (ArraySet) this.mUsageReporters.removeReturnOld(event2.mInstanceId);
                        }
                        if (tokens != null) {
                            synchronized (tokens) {
                                int size2 = tokens.size();
                                int i3 = 0;
                                while (i3 < size2) {
                                    try {
                                        this.mAppTimeLimit.noteUsageStop(buildFullToken(event2.mPackage, tokens.valueAt(i3)), i);
                                        size = size2;
                                    } catch (IllegalArgumentException iae) {
                                        StringBuilder sb = new StringBuilder();
                                        size = size2;
                                        sb.append("Failed to stop usage for during reporter death: ");
                                        sb.append(iae);
                                        Slog.w(TAG, sb.toString());
                                    }
                                    i3++;
                                    size2 = size;
                                }
                            }
                        }
                        if (event2.mTaskRootPackage == null) {
                            event2.mTaskRootPackage = prevData.mTaskRootPackage;
                            event2.mTaskRootClass = prevData.mTaskRootClass;
                        }
                        try {
                            if (this.mUsageSource != 2) {
                                this.mAppTimeLimit.noteUsageStop(event2.mTaskRootPackage, i);
                            } else {
                                this.mAppTimeLimit.noteUsageStop(event2.mPackage, i);
                            }
                        } catch (IllegalArgumentException iae2) {
                            Slog.w(TAG, "Failed to note usage stop", iae2);
                        }
                    } else {
                        return;
                    }
                } else if (event2.mTaskRootPackage == null) {
                    ActivityData prevData2 = this.mVisibleActivities.get(event2.mInstanceId);
                    if (prevData2 == null) {
                        Slog.w(TAG, "Unexpected activity event reported! (" + event2.mPackage + SliceClientPermissions.SliceAuthority.DELIMITER + event2.mClass + " event : " + event2.mEventType + " instanceId : " + event2.mInstanceId + ")");
                    } else {
                        event2.mTaskRootPackage = prevData2.mTaskRootPackage;
                        event2.mTaskRootClass = prevData2.mTaskRootClass;
                    }
                }
            } else if (this.mVisibleActivities.get(event2.mInstanceId) == null) {
                this.mVisibleActivities.put(event2.mInstanceId, new ActivityData(event2.mTaskRootPackage, event2.mTaskRootClass));
                try {
                    if (this.mUsageSource != 2) {
                        this.mAppTimeLimit.noteUsageStart(event2.mTaskRootPackage, i);
                    } else {
                        this.mAppTimeLimit.noteUsageStart(event2.mPackage, i);
                    }
                } catch (IllegalArgumentException iae3) {
                    Slog.e(TAG, "Failed to note usage start", iae3);
                }
            }
            getUserDataAndInitializeIfNeededLocked(i, timeNow).reportEvent(event2);
            this.mAppStandby.reportEvent(event2, elapsedRealtime, i);
            return;
        }
    }

    /* access modifiers changed from: package-private */
    public void reportEventToAllUserId(UsageEvents.Event event) {
        synchronized (this.mLock) {
            int userCount = this.mUserState.size();
            for (int i = 0; i < userCount; i++) {
                reportEvent(new UsageEvents.Event(event), this.mUserState.keyAt(i));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void flushToDisk() {
        synchronized (this.mLock) {
            reportEventToAllUserId(new UsageEvents.Event(25, SystemClock.elapsedRealtime()));
            flushToDiskLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void onUserRemoved(int userId) {
        synchronized (this.mLock) {
            Slog.i(TAG, "Removing user " + userId + " and all data.");
            this.mUserState.remove(userId);
            this.mAppStandby.onUserRemoved(userId);
            this.mAppTimeLimit.onUserRemoved(userId);
            cleanUpRemovedUsersLocked();
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004e, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<android.app.usage.UsageStats> queryUsageStats(int r14, int r15, long r16, long r18, boolean r20) {
        /*
            r13 = this;
            r1 = r13
            r2 = r14
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            long r4 = r13.checkAndGetTimeLocked()     // Catch:{ all -> 0x004f }
            r6 = r4
            r8 = r16
            r10 = r18
            boolean r0 = validRange(r6, r8, r10)     // Catch:{ all -> 0x004f }
            r6 = 0
            if (r0 != 0) goto L_0x0017
            monitor-exit(r3)     // Catch:{ all -> 0x004f }
            return r6
        L_0x0017:
            com.android.server.usage.UserUsageStatsService r7 = r13.getUserDataAndInitializeIfNeededLocked(r14, r4)     // Catch:{ all -> 0x004f }
            r8 = r15
            r9 = r16
            r11 = r18
            java.util.List r0 = r7.queryUsageStats(r8, r9, r11)     // Catch:{ all -> 0x004f }
            if (r0 != 0) goto L_0x0029
            monitor-exit(r3)     // Catch:{ all -> 0x004f }
            return r6
        L_0x0029:
            if (r20 == 0) goto L_0x004d
            int r6 = r0.size()     // Catch:{ all -> 0x004f }
            int r6 = r6 + -1
        L_0x0031:
            if (r6 < 0) goto L_0x004d
            java.lang.Object r8 = r0.get(r6)     // Catch:{ all -> 0x004f }
            android.app.usage.UsageStats r8 = (android.app.usage.UsageStats) r8     // Catch:{ all -> 0x004f }
            android.content.pm.PackageManagerInternal r9 = r1.mPackageManagerInternal     // Catch:{ all -> 0x004f }
            java.lang.String r10 = r8.mPackageName     // Catch:{ all -> 0x004f }
            boolean r9 = r9.isPackageEphemeral(r14, r10)     // Catch:{ all -> 0x004f }
            if (r9 == 0) goto L_0x004a
            android.app.usage.UsageStats r9 = r8.getObfuscatedForInstantApp()     // Catch:{ all -> 0x004f }
            r0.set(r6, r9)     // Catch:{ all -> 0x004f }
        L_0x004a:
            int r6 = r6 + -1
            goto L_0x0031
        L_0x004d:
            monitor-exit(r3)     // Catch:{ all -> 0x004f }
            return r0
        L_0x004f:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x004f }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.UsageStatsService.queryUsageStats(int, int, long, long, boolean):java.util.List");
    }

    /* access modifiers changed from: package-private */
    public List<ConfigurationStats> queryConfigurationStats(int userId, int bucketType, long beginTime, long endTime) {
        synchronized (this.mLock) {
            long timeNow = checkAndGetTimeLocked();
            if (!validRange(timeNow, beginTime, endTime)) {
                return null;
            }
            List<ConfigurationStats> queryConfigurationStats = getUserDataAndInitializeIfNeededLocked(userId, timeNow).queryConfigurationStats(bucketType, beginTime, endTime);
            return queryConfigurationStats;
        }
    }

    /* access modifiers changed from: package-private */
    public List<EventStats> queryEventStats(int userId, int bucketType, long beginTime, long endTime) {
        synchronized (this.mLock) {
            long timeNow = checkAndGetTimeLocked();
            if (!validRange(timeNow, beginTime, endTime)) {
                return null;
            }
            List<EventStats> queryEventStats = getUserDataAndInitializeIfNeededLocked(userId, timeNow).queryEventStats(bucketType, beginTime, endTime);
            return queryEventStats;
        }
    }

    /* access modifiers changed from: package-private */
    public UsageEvents queryEvents(int userId, long beginTime, long endTime, boolean shouldObfuscateInstantApps) {
        synchronized (this.mLock) {
            long timeNow = checkAndGetTimeLocked();
            if (!validRange(timeNow, beginTime, endTime)) {
                return null;
            }
            UsageEvents queryEvents = getUserDataAndInitializeIfNeededLocked(userId, timeNow).queryEvents(beginTime, endTime, shouldObfuscateInstantApps);
            return queryEvents;
        }
    }

    /* access modifiers changed from: package-private */
    public UsageEvents queryEventsForPackage(int userId, long beginTime, long endTime, String packageName, boolean includeTaskRoot) {
        synchronized (this.mLock) {
            try {
                long timeNow = checkAndGetTimeLocked();
                if (!validRange(timeNow, beginTime, endTime)) {
                    return null;
                }
                int i = userId;
                UsageEvents queryEventsForPackage = getUserDataAndInitializeIfNeededLocked(userId, timeNow).queryEventsForPackage(beginTime, endTime, packageName, includeTaskRoot);
                return queryEventsForPackage;
            } catch (Throwable th) {
                th = th;
                throw th;
            }
        }
    }

    private static boolean validRange(long currentTime, long beginTime, long endTime) {
        return beginTime <= currentTime && beginTime < endTime;
    }

    /* access modifiers changed from: private */
    public String buildFullToken(String packageName, String token) {
        StringBuilder sb = new StringBuilder(packageName.length() + token.length() + 1);
        sb.append(packageName);
        sb.append(TOKEN_DELIMITER);
        sb.append(token);
        return sb.toString();
    }

    private void flushToDiskLocked() {
        int userCount = this.mUserState.size();
        for (int i = 0; i < userCount; i++) {
            this.mUserState.valueAt(i).persistActiveStats();
            this.mAppStandby.flushToDisk(this.mUserState.keyAt(i));
        }
        this.mAppStandby.flushDurationsToDisk();
        this.mHandler.removeMessages(1);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0072, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x007b, code lost:
        r8 = new com.android.internal.util.IndentingPrintWriter(r15, "  ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0085, code lost:
        if ((r6 + 1) < r14.length) goto L_0x00c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0087, code lost:
        r9 = r13.mUserState.size();
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x008e, code lost:
        if (r10 >= r9) goto L_0x00f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0090, code lost:
        r8.println("user=" + r13.mUserState.keyAt(r10));
        r8.increaseIndent();
        r13.mUserState.valueAt(r10).dumpFile(r8, (java.lang.String[]) null);
        r8.decreaseIndent();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00bc, code lost:
        r10 = r10 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
        r5 = java.lang.Integer.valueOf(r14[r6 + 1]).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00d3, code lost:
        if (r13.mUserState.indexOfKey(r5) >= 0) goto L_0x00dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00d5, code lost:
        r8.println("the specified user does not exist.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00dc, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00dd, code lost:
        r13.mUserState.get(r5).dumpFile(r8, (java.lang.String[]) java.util.Arrays.copyOfRange(r14, r6 + 2, r14.length));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00f2, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00f4, code lost:
        r8.println("invalid user specified.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00fb, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0104, code lost:
        r5 = new com.android.internal.util.IndentingPrintWriter(r15, "  ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x010e, code lost:
        if ((r6 + 1) < r14.length) goto L_0x0149;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0110, code lost:
        r8 = r13.mUserState.size();
        r9 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0117, code lost:
        if (r9 >= r8) goto L_0x0171;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0119, code lost:
        r5.println("user=" + r13.mUserState.keyAt(r9));
        r5.increaseIndent();
        r13.mUserState.valueAt(r9).dumpDatabaseInfo(r5);
        r5.decreaseIndent();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0145, code lost:
        r9 = r9 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:?, code lost:
        r8 = java.lang.Integer.valueOf(r14[r6 + 1]).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x015c, code lost:
        if (r13.mUserState.indexOfKey(r8) >= 0) goto L_0x0166;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x015e, code lost:
        r5.println("the specified user does not exist.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0165, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0166, code lost:
        r13.mUserState.get(r8).dumpDatabaseInfo(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0172, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0174, code lost:
        r5.println("invalid user specified.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x017b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dump(java.lang.String[] r14, java.io.PrintWriter r15) {
        /*
            r13 = this;
            java.lang.Object r0 = r13.mLock
            monitor-enter(r0)
            com.android.internal.util.IndentingPrintWriter r1 = new com.android.internal.util.IndentingPrintWriter     // Catch:{ all -> 0x01f5 }
            java.lang.String r2 = "  "
            r1.<init>(r15, r2)     // Catch:{ all -> 0x01f5 }
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            if (r14 == 0) goto L_0x018c
            r6 = 0
        L_0x0011:
            int r7 = r14.length     // Catch:{ all -> 0x01f5 }
            if (r6 >= r7) goto L_0x018c
            r7 = r14[r6]     // Catch:{ all -> 0x01f5 }
            java.lang.String r8 = "--checkin"
            boolean r8 = r8.equals(r7)     // Catch:{ all -> 0x01f5 }
            if (r8 == 0) goto L_0x0021
            r2 = 1
            goto L_0x0188
        L_0x0021:
            java.lang.String r8 = "-c"
            boolean r8 = r8.equals(r7)     // Catch:{ all -> 0x01f5 }
            if (r8 == 0) goto L_0x002c
            r3 = 1
            goto L_0x0188
        L_0x002c:
            java.lang.String r8 = "flush"
            boolean r8 = r8.equals(r7)     // Catch:{ all -> 0x01f5 }
            if (r8 == 0) goto L_0x003e
            r13.flushToDiskLocked()     // Catch:{ all -> 0x01f5 }
            java.lang.String r5 = "Flushed stats to disk"
            r15.println(r5)     // Catch:{ all -> 0x01f5 }
            monitor-exit(r0)     // Catch:{ all -> 0x01f5 }
            return
        L_0x003e:
            java.lang.String r8 = "is-app-standby-enabled"
            boolean r8 = r8.equals(r7)     // Catch:{ all -> 0x01f5 }
            if (r8 == 0) goto L_0x0050
            com.android.server.usage.AppStandbyController r5 = r13.mAppStandby     // Catch:{ all -> 0x01f5 }
            boolean r5 = r5.mAppIdleEnabled     // Catch:{ all -> 0x01f5 }
            r15.println(r5)     // Catch:{ all -> 0x01f5 }
            monitor-exit(r0)     // Catch:{ all -> 0x01f5 }
            return
        L_0x0050:
            java.lang.String r8 = "apptimelimit"
            boolean r8 = r8.equals(r7)     // Catch:{ all -> 0x01f5 }
            if (r8 == 0) goto L_0x0073
            int r8 = r6 + 1
            int r9 = r14.length     // Catch:{ all -> 0x01f5 }
            if (r8 < r9) goto L_0x0063
            com.android.server.usage.AppTimeLimitController r8 = r13.mAppTimeLimit     // Catch:{ all -> 0x01f5 }
            r8.dump(r5, r15)     // Catch:{ all -> 0x01f5 }
            goto L_0x0071
        L_0x0063:
            int r5 = r6 + 1
            int r8 = r14.length     // Catch:{ all -> 0x01f5 }
            java.lang.Object[] r5 = java.util.Arrays.copyOfRange(r14, r5, r8)     // Catch:{ all -> 0x01f5 }
            java.lang.String[] r5 = (java.lang.String[]) r5     // Catch:{ all -> 0x01f5 }
            com.android.server.usage.AppTimeLimitController r8 = r13.mAppTimeLimit     // Catch:{ all -> 0x01f5 }
            r8.dump(r5, r15)     // Catch:{ all -> 0x01f5 }
        L_0x0071:
            monitor-exit(r0)     // Catch:{ all -> 0x01f5 }
            return
        L_0x0073:
            java.lang.String r8 = "file"
            boolean r8 = r8.equals(r7)     // Catch:{ all -> 0x01f5 }
            if (r8 == 0) goto L_0x00fc
            com.android.internal.util.IndentingPrintWriter r8 = new com.android.internal.util.IndentingPrintWriter     // Catch:{ all -> 0x01f5 }
            java.lang.String r9 = "  "
            r8.<init>(r15, r9)     // Catch:{ all -> 0x01f5 }
            int r9 = r6 + 1
            int r10 = r14.length     // Catch:{ all -> 0x01f5 }
            if (r9 < r10) goto L_0x00c0
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r9 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            int r9 = r9.size()     // Catch:{ all -> 0x01f5 }
            r10 = 0
        L_0x008e:
            if (r10 >= r9) goto L_0x00bf
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f5 }
            r11.<init>()     // Catch:{ all -> 0x01f5 }
            java.lang.String r12 = "user="
            r11.append(r12)     // Catch:{ all -> 0x01f5 }
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r12 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            int r12 = r12.keyAt(r10)     // Catch:{ all -> 0x01f5 }
            r11.append(r12)     // Catch:{ all -> 0x01f5 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x01f5 }
            r8.println(r11)     // Catch:{ all -> 0x01f5 }
            r8.increaseIndent()     // Catch:{ all -> 0x01f5 }
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r11 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            java.lang.Object r11 = r11.valueAt(r10)     // Catch:{ all -> 0x01f5 }
            com.android.server.usage.UserUsageStatsService r11 = (com.android.server.usage.UserUsageStatsService) r11     // Catch:{ all -> 0x01f5 }
            r11.dumpFile(r8, r5)     // Catch:{ all -> 0x01f5 }
            r8.decreaseIndent()     // Catch:{ all -> 0x01f5 }
            int r10 = r10 + 1
            goto L_0x008e
        L_0x00bf:
            goto L_0x00f1
        L_0x00c0:
            int r5 = r6 + 1
            r5 = r14[r5]     // Catch:{ NumberFormatException -> 0x00f3 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ NumberFormatException -> 0x00f3 }
            int r5 = r5.intValue()     // Catch:{ NumberFormatException -> 0x00f3 }
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r9 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            int r9 = r9.indexOfKey(r5)     // Catch:{ all -> 0x01f5 }
            if (r9 >= 0) goto L_0x00dd
            java.lang.String r9 = "the specified user does not exist."
            r8.println(r9)     // Catch:{ all -> 0x01f5 }
            monitor-exit(r0)     // Catch:{ all -> 0x01f5 }
            return
        L_0x00dd:
            int r9 = r6 + 2
            int r10 = r14.length     // Catch:{ all -> 0x01f5 }
            java.lang.Object[] r9 = java.util.Arrays.copyOfRange(r14, r9, r10)     // Catch:{ all -> 0x01f5 }
            java.lang.String[] r9 = (java.lang.String[]) r9     // Catch:{ all -> 0x01f5 }
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r10 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            java.lang.Object r10 = r10.get(r5)     // Catch:{ all -> 0x01f5 }
            com.android.server.usage.UserUsageStatsService r10 = (com.android.server.usage.UserUsageStatsService) r10     // Catch:{ all -> 0x01f5 }
            r10.dumpFile(r8, r9)     // Catch:{ all -> 0x01f5 }
        L_0x00f1:
            monitor-exit(r0)     // Catch:{ all -> 0x01f5 }
            return
        L_0x00f3:
            r5 = move-exception
            java.lang.String r9 = "invalid user specified."
            r8.println(r9)     // Catch:{ all -> 0x01f5 }
            monitor-exit(r0)     // Catch:{ all -> 0x01f5 }
            return
        L_0x00fc:
            java.lang.String r8 = "database-info"
            boolean r8 = r8.equals(r7)     // Catch:{ all -> 0x01f5 }
            if (r8 == 0) goto L_0x017c
            com.android.internal.util.IndentingPrintWriter r5 = new com.android.internal.util.IndentingPrintWriter     // Catch:{ all -> 0x01f5 }
            java.lang.String r8 = "  "
            r5.<init>(r15, r8)     // Catch:{ all -> 0x01f5 }
            int r8 = r6 + 1
            int r9 = r14.length     // Catch:{ all -> 0x01f5 }
            if (r8 < r9) goto L_0x0149
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r8 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            int r8 = r8.size()     // Catch:{ all -> 0x01f5 }
            r9 = 0
        L_0x0117:
            if (r9 >= r8) goto L_0x0148
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f5 }
            r10.<init>()     // Catch:{ all -> 0x01f5 }
            java.lang.String r11 = "user="
            r10.append(r11)     // Catch:{ all -> 0x01f5 }
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r11 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            int r11 = r11.keyAt(r9)     // Catch:{ all -> 0x01f5 }
            r10.append(r11)     // Catch:{ all -> 0x01f5 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x01f5 }
            r5.println(r10)     // Catch:{ all -> 0x01f5 }
            r5.increaseIndent()     // Catch:{ all -> 0x01f5 }
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r10 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            java.lang.Object r10 = r10.valueAt(r9)     // Catch:{ all -> 0x01f5 }
            com.android.server.usage.UserUsageStatsService r10 = (com.android.server.usage.UserUsageStatsService) r10     // Catch:{ all -> 0x01f5 }
            r10.dumpDatabaseInfo(r5)     // Catch:{ all -> 0x01f5 }
            r5.decreaseIndent()     // Catch:{ all -> 0x01f5 }
            int r9 = r9 + 1
            goto L_0x0117
        L_0x0148:
            goto L_0x0171
        L_0x0149:
            int r8 = r6 + 1
            r8 = r14[r8]     // Catch:{ NumberFormatException -> 0x0173 }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ NumberFormatException -> 0x0173 }
            int r8 = r8.intValue()     // Catch:{ NumberFormatException -> 0x0173 }
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r9 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            int r9 = r9.indexOfKey(r8)     // Catch:{ all -> 0x01f5 }
            if (r9 >= 0) goto L_0x0166
            java.lang.String r9 = "the specified user does not exist."
            r5.println(r9)     // Catch:{ all -> 0x01f5 }
            monitor-exit(r0)     // Catch:{ all -> 0x01f5 }
            return
        L_0x0166:
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r9 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            java.lang.Object r9 = r9.get(r8)     // Catch:{ all -> 0x01f5 }
            com.android.server.usage.UserUsageStatsService r9 = (com.android.server.usage.UserUsageStatsService) r9     // Catch:{ all -> 0x01f5 }
            r9.dumpDatabaseInfo(r5)     // Catch:{ all -> 0x01f5 }
        L_0x0171:
            monitor-exit(r0)     // Catch:{ all -> 0x01f5 }
            return
        L_0x0173:
            r8 = move-exception
            java.lang.String r9 = "invalid user specified."
            r5.println(r9)     // Catch:{ all -> 0x01f5 }
            monitor-exit(r0)     // Catch:{ all -> 0x01f5 }
            return
        L_0x017c:
            if (r7 == 0) goto L_0x0188
            java.lang.String r8 = "-"
            boolean r8 = r7.startsWith(r8)     // Catch:{ all -> 0x01f5 }
            if (r8 != 0) goto L_0x0188
            r4 = r7
            goto L_0x018c
        L_0x0188:
            int r6 = r6 + 1
            goto L_0x0011
        L_0x018c:
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r6 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            int r6 = r6.size()     // Catch:{ all -> 0x01f5 }
            r7 = 0
        L_0x0193:
            if (r7 >= r6) goto L_0x01d3
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r8 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            int r8 = r8.keyAt(r7)     // Catch:{ all -> 0x01f5 }
            java.lang.String r9 = "user"
            java.lang.Integer r10 = java.lang.Integer.valueOf(r8)     // Catch:{ all -> 0x01f5 }
            r1.printPair(r9, r10)     // Catch:{ all -> 0x01f5 }
            r1.println()     // Catch:{ all -> 0x01f5 }
            r1.increaseIndent()     // Catch:{ all -> 0x01f5 }
            if (r2 == 0) goto L_0x01b9
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r9 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            java.lang.Object r9 = r9.valueAt(r7)     // Catch:{ all -> 0x01f5 }
            com.android.server.usage.UserUsageStatsService r9 = (com.android.server.usage.UserUsageStatsService) r9     // Catch:{ all -> 0x01f5 }
            r9.checkin(r1)     // Catch:{ all -> 0x01f5 }
            goto L_0x01c7
        L_0x01b9:
            android.util.SparseArray<com.android.server.usage.UserUsageStatsService> r9 = r13.mUserState     // Catch:{ all -> 0x01f5 }
            java.lang.Object r9 = r9.valueAt(r7)     // Catch:{ all -> 0x01f5 }
            com.android.server.usage.UserUsageStatsService r9 = (com.android.server.usage.UserUsageStatsService) r9     // Catch:{ all -> 0x01f5 }
            r9.dump(r1, r4, r3)     // Catch:{ all -> 0x01f5 }
            r1.println()     // Catch:{ all -> 0x01f5 }
        L_0x01c7:
            com.android.server.usage.AppStandbyController r9 = r13.mAppStandby     // Catch:{ all -> 0x01f5 }
            r9.dumpUser(r1, r8, r4)     // Catch:{ all -> 0x01f5 }
            r1.decreaseIndent()     // Catch:{ all -> 0x01f5 }
            int r7 = r7 + 1
            goto L_0x0193
        L_0x01d3:
            if (r4 != 0) goto L_0x01dd
            r15.println()     // Catch:{ all -> 0x01f5 }
            com.android.server.usage.AppStandbyController r7 = r13.mAppStandby     // Catch:{ all -> 0x01f5 }
            r7.dumpState(r14, r15)     // Catch:{ all -> 0x01f5 }
        L_0x01dd:
            r1.println()     // Catch:{ all -> 0x01f5 }
            java.lang.String r7 = "Usage Source"
            int r8 = r13.mUsageSource     // Catch:{ all -> 0x01f5 }
            java.lang.String r8 = android.app.usage.UsageStatsManager.usageSourceToString(r8)     // Catch:{ all -> 0x01f5 }
            r1.printPair(r7, r8)     // Catch:{ all -> 0x01f5 }
            r1.println()     // Catch:{ all -> 0x01f5 }
            com.android.server.usage.AppTimeLimitController r7 = r13.mAppTimeLimit     // Catch:{ all -> 0x01f5 }
            r7.dump(r5, r15)     // Catch:{ all -> 0x01f5 }
            monitor-exit(r0)     // Catch:{ all -> 0x01f5 }
            return
        L_0x01f5:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x01f5 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.UsageStatsService.dump(java.lang.String[], java.io.PrintWriter):void");
    }

    class H extends Handler {
        public H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i != 0) {
                int newCounter = 1;
                if (i == 1) {
                    UsageStatsService.this.flushToDisk();
                } else if (i == 2) {
                    UsageStatsService.this.onUserRemoved(msg.arg1);
                } else if (i == 3) {
                    int uid = msg.arg1;
                    if (msg.arg2 <= 2) {
                        newCounter = 0;
                    }
                    synchronized (UsageStatsService.this.mUidToKernelCounter) {
                        if (newCounter != UsageStatsService.this.mUidToKernelCounter.get(uid, 0)) {
                            UsageStatsService.this.mUidToKernelCounter.put(uid, newCounter);
                            try {
                                FileUtils.stringToFile(UsageStatsService.KERNEL_COUNTER_FILE, uid + " " + newCounter);
                            } catch (IOException e) {
                                Slog.w(UsageStatsService.TAG, "Failed to update counter set: " + e);
                            }
                        }
                    }
                } else if (i != 4) {
                    super.handleMessage(msg);
                } else {
                    UsageStatsService.this.reportEventToAllUserId((UsageEvents.Event) msg.obj);
                }
            } else {
                UsageStatsService.this.reportEvent((UsageEvents.Event) msg.obj, msg.arg1);
            }
        }
    }

    private final class BinderService extends IUsageStatsManager.Stub {
        private BinderService() {
        }

        private boolean hasPermission(String callingPackage) {
            int callingUid = Binder.getCallingUid();
            if (callingUid == 1000) {
                return true;
            }
            int mode = UsageStatsService.this.mAppOps.noteOp(43, callingUid, callingPackage);
            if (mode == 3) {
                if (UsageStatsService.this.getContext().checkCallingPermission("android.permission.PACKAGE_USAGE_STATS") == 0) {
                    return true;
                }
                return false;
            } else if (mode == 0) {
                return true;
            } else {
                return false;
            }
        }

        private boolean hasObserverPermission() {
            int callingUid = Binder.getCallingUid();
            DevicePolicyManagerInternal dpmInternal = UsageStatsService.this.getDpmInternal();
            if (callingUid == 1000 || ((dpmInternal != null && dpmInternal.isActiveAdminWithPolicy(callingUid, -1)) || UsageStatsService.this.getContext().checkCallingPermission("android.permission.OBSERVE_APP_USAGE") == 0)) {
                return true;
            }
            return false;
        }

        private boolean hasPermissions(String callingPackage, String... permissions) {
            if (Binder.getCallingUid() == 1000) {
                return true;
            }
            boolean hasPermissions = true;
            Context context = UsageStatsService.this.getContext();
            for (int i = 0; i < permissions.length; i++) {
                hasPermissions = hasPermissions && context.checkCallingPermission(permissions[i]) == 0;
            }
            return hasPermissions;
        }

        private void checkCallerIsSystemOrSameApp(String pkg) {
            if (!isCallingUidSystem()) {
                checkCallerIsSameApp(pkg);
            }
        }

        private void checkCallerIsSameApp(String pkg) {
            int callingUid = Binder.getCallingUid();
            if (UsageStatsService.this.mPackageManagerInternal.getPackageUid(pkg, 0, UserHandle.getUserId(callingUid)) != callingUid) {
                throw new SecurityException("Calling uid " + callingUid + " cannot query eventsfor package " + pkg);
            }
        }

        private boolean isCallingUidSystem() {
            return UserHandle.getAppId(Binder.getCallingUid()) == 1000;
        }

        public ParceledListSlice<UsageStats> queryUsageStats(int bucketType, long beginTime, long endTime, String callingPackage) {
            if (!hasPermission(callingPackage)) {
                return null;
            }
            boolean obfuscateInstantApps = UsageStatsService.this.shouldObfuscateInstantAppsForCaller(Binder.getCallingUid(), UserHandle.getCallingUserId());
            int userId = UserHandle.getCallingUserId();
            long token = Binder.clearCallingIdentity();
            try {
                List<UsageStats> results = UsageStatsService.this.queryUsageStats(userId, bucketType, beginTime, endTime, obfuscateInstantApps);
                if (results != null) {
                    return new ParceledListSlice<>(results);
                }
                Binder.restoreCallingIdentity(token);
                return null;
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public ParceledListSlice<UsageStats> queryUsageStatsAsUser(int bucketType, long beginTime, long endTime, String callingPackage, int userId) {
            int i = userId;
            if (i < 0) {
                String str = callingPackage;
                throw new IllegalArgumentException("Invalid userId " + i);
            } else if (!hasPermission(callingPackage)) {
                return null;
            } else {
                int callingUid = Binder.getCallingUid();
                if (i != UserHandle.getUserId(callingUid)) {
                    UsageStatsService.this.getContext().enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "get usagestats");
                }
                boolean obfuscateInstantApps = UsageStatsService.this.shouldObfuscateInstantAppsForCaller(callingUid, UserHandle.getCallingUserId());
                long token = Binder.clearCallingIdentity();
                try {
                    List<UsageStats> results = UsageStatsService.this.queryUsageStats(userId, bucketType, beginTime, endTime, obfuscateInstantApps);
                    if (results != null) {
                        return new ParceledListSlice<>(results);
                    }
                    Binder.restoreCallingIdentity(token);
                    return null;
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
        }

        public ParceledListSlice<ConfigurationStats> queryConfigurationStats(int bucketType, long beginTime, long endTime, String callingPackage) throws RemoteException {
            if (!hasPermission(callingPackage)) {
                return null;
            }
            int userId = UserHandle.getCallingUserId();
            long token = Binder.clearCallingIdentity();
            try {
                List<ConfigurationStats> results = UsageStatsService.this.queryConfigurationStats(userId, bucketType, beginTime, endTime);
                if (results != null) {
                    return new ParceledListSlice<>(results);
                }
                Binder.restoreCallingIdentity(token);
                return null;
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public ParceledListSlice<EventStats> queryEventStats(int bucketType, long beginTime, long endTime, String callingPackage) throws RemoteException {
            if (!hasPermission(callingPackage)) {
                return null;
            }
            int userId = UserHandle.getCallingUserId();
            long token = Binder.clearCallingIdentity();
            try {
                List<EventStats> results = UsageStatsService.this.queryEventStats(userId, bucketType, beginTime, endTime);
                if (results != null) {
                    return new ParceledListSlice<>(results);
                }
                Binder.restoreCallingIdentity(token);
                return null;
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public UsageEvents queryEvents(long beginTime, long endTime, String callingPackage) {
            if (!hasPermission(callingPackage)) {
                return null;
            }
            boolean obfuscateInstantApps = UsageStatsService.this.shouldObfuscateInstantAppsForCaller(Binder.getCallingUid(), UserHandle.getCallingUserId());
            int userId = UserHandle.getCallingUserId();
            long token = Binder.clearCallingIdentity();
            try {
                return UsageStatsService.this.queryEvents(userId, beginTime, endTime, obfuscateInstantApps);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public UsageEvents queryEventsForPackage(long beginTime, long endTime, String callingPackage) {
            String str = callingPackage;
            int callingUserId = UserHandle.getUserId(Binder.getCallingUid());
            checkCallerIsSameApp(str);
            boolean includeTaskRoot = hasPermission(str);
            long token = Binder.clearCallingIdentity();
            try {
                return UsageStatsService.this.queryEventsForPackage(callingUserId, beginTime, endTime, callingPackage, includeTaskRoot);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public UsageEvents queryEventsForUser(long beginTime, long endTime, int userId, String callingPackage) {
            if (!hasPermission(callingPackage)) {
                return null;
            }
            if (userId != UserHandle.getCallingUserId()) {
                UsageStatsService.this.getContext().enforceCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "No permission to query usage stats for this user");
            }
            boolean obfuscateInstantApps = UsageStatsService.this.shouldObfuscateInstantAppsForCaller(Binder.getCallingUid(), UserHandle.getCallingUserId());
            long token = Binder.clearCallingIdentity();
            try {
                return UsageStatsService.this.queryEvents(userId, beginTime, endTime, obfuscateInstantApps);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public UsageEvents queryEventsForPackageForUser(long beginTime, long endTime, int userId, String pkg, String callingPackage) {
            if (!hasPermission(callingPackage)) {
                return null;
            }
            if (userId != UserHandle.getCallingUserId()) {
                UsageStatsService.this.getContext().enforceCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "No permission to query usage stats for this user");
            }
            checkCallerIsSystemOrSameApp(pkg);
            long token = Binder.clearCallingIdentity();
            try {
                return UsageStatsService.this.queryEventsForPackage(userId, beginTime, endTime, pkg, true);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean isAppInactive(String packageName, int userId) {
            try {
                int userId2 = ActivityManager.getService().handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, false, "isAppInactive", (String) null);
                boolean obfuscateInstantApps = UsageStatsService.this.shouldObfuscateInstantAppsForCaller(Binder.getCallingUid(), userId2);
                long token = Binder.clearCallingIdentity();
                try {
                    return UsageStatsService.this.mAppStandby.isAppIdleFilteredOrParoled(packageName, userId2, SystemClock.elapsedRealtime(), obfuscateInstantApps);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }

        public void setAppInactive(String packageName, boolean idle, int userId) {
            try {
                int userId2 = ActivityManager.getService().handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, true, "setAppInactive", (String) null);
                UsageStatsService.this.getContext().enforceCallingPermission("android.permission.CHANGE_APP_IDLE_STATE", "No permission to change app idle state");
                long token = Binder.clearCallingIdentity();
                try {
                    if (UsageStatsService.this.mAppStandby.getAppId(packageName) >= 0) {
                        UsageStatsService.this.mAppStandby.setAppIdleAsync(packageName, idle, userId2);
                        Binder.restoreCallingIdentity(token);
                    }
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }

        public int getAppStandbyBucket(String packageName, String callingPackage, int userId) {
            int callingUid = Binder.getCallingUid();
            try {
                int userId2 = ActivityManager.getService().handleIncomingUser(Binder.getCallingPid(), callingUid, userId, false, false, "getAppStandbyBucket", (String) null);
                int packageUid = UsageStatsService.this.mPackageManagerInternal.getPackageUid(packageName, 0, userId2);
                if (packageUid != callingUid && !hasPermission(callingPackage)) {
                    throw new SecurityException("Don't have permission to query app standby bucket");
                } else if (packageUid >= 0) {
                    boolean obfuscateInstantApps = UsageStatsService.this.shouldObfuscateInstantAppsForCaller(callingUid, userId2);
                    long token = Binder.clearCallingIdentity();
                    try {
                        return UsageStatsService.this.mAppStandby.getAppStandbyBucket(packageName, userId2, SystemClock.elapsedRealtime(), obfuscateInstantApps);
                    } finally {
                        Binder.restoreCallingIdentity(token);
                    }
                } else {
                    throw new IllegalArgumentException("Cannot get standby bucket for non existent package (" + packageName + ")");
                }
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 17 */
        public void setAppStandbyBucket(String packageName, int bucket, int userId) {
            int reason;
            String str = packageName;
            int i = bucket;
            UsageStatsService.this.getContext().enforceCallingPermission("android.permission.CHANGE_APP_IDLE_STATE", "No permission to change app standby state");
            if (i < 10 || i > 50) {
                throw new IllegalArgumentException("Cannot set the standby bucket to " + i);
            }
            int callingUid = Binder.getCallingUid();
            try {
                int userId2 = ActivityManager.getService().handleIncomingUser(Binder.getCallingPid(), callingUid, userId, false, true, "setAppStandbyBucket", (String) null);
                boolean shellCaller = callingUid == 0 || callingUid == 2000;
                if (UserHandle.isCore(callingUid)) {
                    reason = 1024;
                } else {
                    reason = 1280;
                }
                long token = Binder.clearCallingIdentity();
                try {
                    int packageUid = UsageStatsService.this.mPackageManagerInternal.getPackageUid(str, 4980736, userId2);
                    if (packageUid == callingUid) {
                        throw new IllegalArgumentException("Cannot set your own standby bucket");
                    } else if (packageUid >= 0) {
                        UsageStatsService.this.mAppStandby.setAppStandbyBucket(packageName, userId2, bucket, reason, SystemClock.elapsedRealtime(), shellCaller);
                    } else {
                        throw new IllegalArgumentException("Cannot set standby bucket for non existent package (" + str + ")");
                    }
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }

        public ParceledListSlice<AppStandbyInfo> getAppStandbyBuckets(String callingPackageName, int userId) {
            ParceledListSlice<AppStandbyInfo> parceledListSlice;
            try {
                int userId2 = ActivityManager.getService().handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, false, "getAppStandbyBucket", (String) null);
                if (hasPermission(callingPackageName)) {
                    long token = Binder.clearCallingIdentity();
                    try {
                        List<AppStandbyInfo> standbyBucketList = UsageStatsService.this.mAppStandby.getAppStandbyBuckets(userId2);
                        if (standbyBucketList == null) {
                            parceledListSlice = ParceledListSlice.emptyList();
                        } else {
                            parceledListSlice = new ParceledListSlice<>(standbyBucketList);
                        }
                        return parceledListSlice;
                    } finally {
                        Binder.restoreCallingIdentity(token);
                    }
                } else {
                    throw new SecurityException("Don't have permission to query app standby bucket");
                }
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 18 */
        public void setAppStandbyBuckets(ParceledListSlice appBuckets, int userId) {
            int reason;
            UsageStatsService.this.getContext().enforceCallingPermission("android.permission.CHANGE_APP_IDLE_STATE", "No permission to change app standby state");
            int callingUid = Binder.getCallingUid();
            try {
                int userId2 = ActivityManager.getService().handleIncomingUser(Binder.getCallingPid(), callingUid, userId, false, true, "setAppStandbyBucket", (String) null);
                boolean shellCaller = callingUid == 0 || callingUid == 2000;
                if (shellCaller) {
                    reason = 1024;
                } else {
                    reason = 1280;
                }
                long token = Binder.clearCallingIdentity();
                try {
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    for (AppStandbyInfo bucketInfo : appBuckets.getList()) {
                        String packageName = bucketInfo.mPackageName;
                        int bucket = bucketInfo.mStandbyBucket;
                        if (bucket < 10 || bucket > 50) {
                            String str = packageName;
                            AppStandbyInfo appStandbyInfo = bucketInfo;
                            throw new IllegalArgumentException("Cannot set the standby bucket to " + bucket);
                        } else if (UsageStatsService.this.mPackageManagerInternal.getPackageUid(packageName, DumpState.DUMP_CHANGES, userId2) != callingUid) {
                            String str2 = packageName;
                            AppStandbyInfo appStandbyInfo2 = bucketInfo;
                            UsageStatsService.this.mAppStandby.setAppStandbyBucket(packageName, userId2, bucket, reason, elapsedRealtime, shellCaller);
                        } else {
                            int i = bucket;
                            String str3 = packageName;
                            AppStandbyInfo appStandbyInfo3 = bucketInfo;
                            throw new IllegalArgumentException("Cannot set your own standby bucket");
                        }
                    }
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }

        public void whitelistAppTemporarily(String packageName, long duration, int userId) throws RemoteException {
            StringBuilder reason = new StringBuilder(32);
            reason.append("from:");
            UserHandle.formatUid(reason, Binder.getCallingUid());
            UsageStatsService.this.mDeviceIdleController.addPowerSaveTempWhitelistApp(packageName, duration, userId, reason.toString());
        }

        public void onCarrierPrivilegedAppsChanged() {
            UsageStatsService.this.getContext().enforceCallingOrSelfPermission("android.permission.BIND_CARRIER_SERVICES", "onCarrierPrivilegedAppsChanged can only be called by privileged apps.");
            UsageStatsService.this.mAppStandby.clearCarrierPrivilegedApps();
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpAndUsageStatsPermission(UsageStatsService.this.getContext(), UsageStatsService.TAG, pw)) {
                UsageStatsService.this.dump(args, pw);
            }
        }

        public void reportChooserSelection(String packageName, int userId, String contentType, String[] annotations, String action) {
            if (packageName == null) {
                Slog.w(UsageStatsService.TAG, "Event report user selecting a null package");
                return;
            }
            UsageEvents.Event event = new UsageEvents.Event(9, SystemClock.elapsedRealtime());
            event.mPackage = packageName;
            event.mAction = action;
            event.mContentType = contentType;
            event.mContentAnnotations = annotations;
            UsageStatsService.this.mHandler.obtainMessage(0, userId, 0, event).sendToTarget();
        }

        public void registerAppUsageObserver(int observerId, String[] packages, long timeLimitMs, PendingIntent callbackIntent, String callingPackage) {
            String[] strArr = packages;
            if (!hasObserverPermission()) {
                throw new SecurityException("Caller doesn't have OBSERVE_APP_USAGE permission");
            } else if (strArr == null || strArr.length == 0) {
                throw new IllegalArgumentException("Must specify at least one package");
            } else if (callbackIntent != null) {
                int callingUid = Binder.getCallingUid();
                int userId = UserHandle.getUserId(callingUid);
                long token = Binder.clearCallingIdentity();
                try {
                    UsageStatsService.this.registerAppUsageObserver(callingUid, observerId, packages, timeLimitMs, callbackIntent, userId);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } else {
                throw new NullPointerException("callbackIntent can't be null");
            }
        }

        public void unregisterAppUsageObserver(int observerId, String callingPackage) {
            if (hasObserverPermission()) {
                int callingUid = Binder.getCallingUid();
                int userId = UserHandle.getUserId(callingUid);
                long token = Binder.clearCallingIdentity();
                try {
                    UsageStatsService.this.unregisterAppUsageObserver(callingUid, observerId, userId);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } else {
                throw new SecurityException("Caller doesn't have OBSERVE_APP_USAGE permission");
            }
        }

        public void registerUsageSessionObserver(int sessionObserverId, String[] observed, long timeLimitMs, long sessionThresholdTimeMs, PendingIntent limitReachedCallbackIntent, PendingIntent sessionEndCallbackIntent, String callingPackage) {
            String[] strArr = observed;
            if (!hasObserverPermission()) {
                throw new SecurityException("Caller doesn't have OBSERVE_APP_USAGE permission");
            } else if (strArr == null || strArr.length == 0) {
                throw new IllegalArgumentException("Must specify at least one observed entity");
            } else if (limitReachedCallbackIntent != null) {
                int callingUid = Binder.getCallingUid();
                int userId = UserHandle.getUserId(callingUid);
                long token = Binder.clearCallingIdentity();
                try {
                    UsageStatsService.this.registerUsageSessionObserver(callingUid, sessionObserverId, observed, timeLimitMs, sessionThresholdTimeMs, limitReachedCallbackIntent, sessionEndCallbackIntent, userId);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } else {
                throw new NullPointerException("limitReachedCallbackIntent can't be null");
            }
        }

        public void unregisterUsageSessionObserver(int sessionObserverId, String callingPackage) {
            if (hasObserverPermission()) {
                int callingUid = Binder.getCallingUid();
                int userId = UserHandle.getUserId(callingUid);
                long token = Binder.clearCallingIdentity();
                try {
                    UsageStatsService.this.unregisterUsageSessionObserver(callingUid, sessionObserverId, userId);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } else {
                throw new SecurityException("Caller doesn't have OBSERVE_APP_USAGE permission");
            }
        }

        public void registerAppUsageLimitObserver(int observerId, String[] packages, long timeLimitMs, long timeUsedMs, PendingIntent callbackIntent, String callingPackage) {
            String[] strArr = packages;
            if (!hasPermissions(callingPackage, "android.permission.SUSPEND_APPS", "android.permission.OBSERVE_APP_USAGE")) {
                throw new SecurityException("Caller doesn't have both SUSPEND_APPS and OBSERVE_APP_USAGE permissions");
            } else if (strArr == null || strArr.length == 0) {
                throw new IllegalArgumentException("Must specify at least one package");
            } else if (callbackIntent != null || timeUsedMs >= timeLimitMs) {
                int callingUid = Binder.getCallingUid();
                int userId = UserHandle.getUserId(callingUid);
                long token = Binder.clearCallingIdentity();
                try {
                    UsageStatsService.this.registerAppUsageLimitObserver(callingUid, observerId, packages, timeLimitMs, timeUsedMs, callbackIntent, userId);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } else {
                throw new NullPointerException("callbackIntent can't be null");
            }
        }

        public void unregisterAppUsageLimitObserver(int observerId, String callingPackage) {
            if (hasPermissions(callingPackage, "android.permission.SUSPEND_APPS", "android.permission.OBSERVE_APP_USAGE")) {
                int callingUid = Binder.getCallingUid();
                int userId = UserHandle.getUserId(callingUid);
                long token = Binder.clearCallingIdentity();
                try {
                    UsageStatsService.this.unregisterAppUsageLimitObserver(callingUid, observerId, userId);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } else {
                throw new SecurityException("Caller doesn't have both SUSPEND_APPS and OBSERVE_APP_USAGE permissions");
            }
        }

        public void reportUsageStart(IBinder activity, String token, String callingPackage) {
            reportPastUsageStart(activity, token, 0, callingPackage);
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void reportPastUsageStart(IBinder activity, String token, long timeAgoMs, String callingPackage) {
            ArraySet<String> tokens;
            int userId = UserHandle.getUserId(Binder.getCallingUid());
            long binderToken = Binder.clearCallingIdentity();
            try {
                synchronized (UsageStatsService.this.mUsageReporters) {
                    tokens = UsageStatsService.this.mUsageReporters.get(activity.hashCode());
                    if (tokens == null) {
                        tokens = new ArraySet<>();
                        UsageStatsService.this.mUsageReporters.put(activity.hashCode(), tokens);
                    }
                }
                synchronized (tokens) {
                    if (!tokens.add(token)) {
                        throw new IllegalArgumentException(token + " for " + callingPackage + " is already reported as started for this activity");
                    }
                }
                UsageStatsService.this.mAppTimeLimit.noteUsageStart(UsageStatsService.this.buildFullToken(callingPackage, token), userId, timeAgoMs);
                Binder.restoreCallingIdentity(binderToken);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(binderToken);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public void reportUsageStop(IBinder activity, String token, String callingPackage) {
            ArraySet<String> tokens;
            int userId = UserHandle.getUserId(Binder.getCallingUid());
            long binderToken = Binder.clearCallingIdentity();
            try {
                synchronized (UsageStatsService.this.mUsageReporters) {
                    tokens = UsageStatsService.this.mUsageReporters.get(activity.hashCode());
                    if (tokens == null) {
                        throw new IllegalArgumentException("Unknown reporter trying to stop token " + token + " for " + callingPackage);
                    }
                }
                synchronized (tokens) {
                    if (!tokens.remove(token)) {
                        throw new IllegalArgumentException(token + " for " + callingPackage + " is already reported as stopped for this activity");
                    }
                }
                UsageStatsService.this.mAppTimeLimit.noteUsageStop(UsageStatsService.this.buildFullToken(callingPackage, token), userId);
                Binder.restoreCallingIdentity(binderToken);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(binderToken);
                throw th;
            }
        }

        public int getUsageSource() {
            int i;
            if (hasObserverPermission()) {
                synchronized (UsageStatsService.this.mLock) {
                    i = UsageStatsService.this.mUsageSource;
                }
                return i;
            }
            throw new SecurityException("Caller doesn't have OBSERVE_APP_USAGE permission");
        }

        public void forceUsageSourceSettingRead() {
            UsageStatsService.this.readUsageSourceSetting();
        }
    }

    /* access modifiers changed from: package-private */
    public void registerAppUsageObserver(int callingUid, int observerId, String[] packages, long timeLimitMs, PendingIntent callbackIntent, int userId) {
        this.mAppTimeLimit.addAppUsageObserver(callingUid, observerId, packages, timeLimitMs, callbackIntent, userId);
    }

    /* access modifiers changed from: package-private */
    public void unregisterAppUsageObserver(int callingUid, int observerId, int userId) {
        this.mAppTimeLimit.removeAppUsageObserver(callingUid, observerId, userId);
    }

    /* access modifiers changed from: package-private */
    public void registerUsageSessionObserver(int callingUid, int observerId, String[] observed, long timeLimitMs, long sessionThresholdTime, PendingIntent limitReachedCallbackIntent, PendingIntent sessionEndCallbackIntent, int userId) {
        this.mAppTimeLimit.addUsageSessionObserver(callingUid, observerId, observed, timeLimitMs, sessionThresholdTime, limitReachedCallbackIntent, sessionEndCallbackIntent, userId);
    }

    /* access modifiers changed from: package-private */
    public void unregisterUsageSessionObserver(int callingUid, int sessionObserverId, int userId) {
        this.mAppTimeLimit.removeUsageSessionObserver(callingUid, sessionObserverId, userId);
    }

    /* access modifiers changed from: package-private */
    public void registerAppUsageLimitObserver(int callingUid, int observerId, String[] packages, long timeLimitMs, long timeUsedMs, PendingIntent callbackIntent, int userId) {
        this.mAppTimeLimit.addAppUsageLimitObserver(callingUid, observerId, packages, timeLimitMs, timeUsedMs, callbackIntent, userId);
    }

    /* access modifiers changed from: package-private */
    public void unregisterAppUsageLimitObserver(int callingUid, int observerId, int userId) {
        this.mAppTimeLimit.removeAppUsageLimitObserver(callingUid, observerId, userId);
    }

    private final class LocalService extends UsageStatsManagerInternal {
        private LocalService() {
        }

        public void reportEvent(ComponentName component, int userId, int eventType, int instanceId, ComponentName taskRoot) {
            if (component == null) {
                Slog.w(UsageStatsService.TAG, "Event reported without a component name");
                return;
            }
            UsageEvents.Event event = new UsageEvents.Event(eventType, SystemClock.elapsedRealtime());
            event.mPackage = component.getPackageName();
            event.mClass = component.getClassName();
            event.mInstanceId = instanceId;
            if (taskRoot == null) {
                event.mTaskRootPackage = null;
                event.mTaskRootClass = null;
            } else {
                event.mTaskRootPackage = taskRoot.getPackageName();
                event.mTaskRootClass = taskRoot.getClassName();
            }
            UsageStatsService.this.mHandler.obtainMessage(0, userId, 0, event).sendToTarget();
        }

        public void reportEvent(String packageName, int userId, int eventType) {
            if (packageName == null) {
                Slog.w(UsageStatsService.TAG, "Event reported without a package name, eventType:" + eventType);
                return;
            }
            UsageEvents.Event event = new UsageEvents.Event(eventType, SystemClock.elapsedRealtime());
            event.mPackage = packageName;
            UsageStatsService.this.mHandler.obtainMessage(0, userId, 0, event).sendToTarget();
        }

        public void reportConfigurationChange(Configuration config, int userId) {
            if (config == null) {
                Slog.w(UsageStatsService.TAG, "Configuration event reported with a null config");
                return;
            }
            UsageEvents.Event event = new UsageEvents.Event(5, SystemClock.elapsedRealtime());
            event.mPackage = PackageManagerService.PLATFORM_PACKAGE_NAME;
            event.mConfiguration = new Configuration(config);
            UsageStatsService.this.mHandler.obtainMessage(0, userId, 0, event).sendToTarget();
        }

        public void reportInterruptiveNotification(String packageName, String channelId, int userId) {
            if (packageName == null || channelId == null) {
                Slog.w(UsageStatsService.TAG, "Event reported without a package name or a channel ID");
                return;
            }
            UsageEvents.Event event = new UsageEvents.Event(12, SystemClock.elapsedRealtime());
            event.mPackage = packageName.intern();
            event.mNotificationChannelId = channelId.intern();
            UsageStatsService.this.mHandler.obtainMessage(0, userId, 0, event).sendToTarget();
        }

        public void reportShortcutUsage(String packageName, String shortcutId, int userId) {
            if (packageName == null || shortcutId == null) {
                Slog.w(UsageStatsService.TAG, "Event reported without a package name or a shortcut ID");
                return;
            }
            UsageEvents.Event event = new UsageEvents.Event(8, SystemClock.elapsedRealtime());
            event.mPackage = packageName.intern();
            event.mShortcutId = shortcutId.intern();
            UsageStatsService.this.mHandler.obtainMessage(0, userId, 0, event).sendToTarget();
        }

        public void reportContentProviderUsage(String name, String packageName, int userId) {
            UsageStatsService.this.mAppStandby.postReportContentProviderUsage(name, packageName, userId);
        }

        public boolean isAppIdle(String packageName, int uidForAppId, int userId) {
            return UsageStatsService.this.mAppStandby.isAppIdleFiltered(packageName, uidForAppId, userId, SystemClock.elapsedRealtime());
        }

        public int getAppStandbyBucket(String packageName, int userId, long nowElapsed) {
            return UsageStatsService.this.mAppStandby.getAppStandbyBucket(packageName, userId, nowElapsed, false);
        }

        public int[] getIdleUidsForUser(int userId) {
            return UsageStatsService.this.mAppStandby.getIdleUidsForUser(userId);
        }

        public boolean isAppIdleParoleOn() {
            return UsageStatsService.this.mAppStandby.isParoledOrCharging();
        }

        public void prepareShutdown() {
            UsageStatsService.this.shutdown();
        }

        public void prepareForPossibleShutdown() {
            UsageStatsService.this.prepareForPossibleShutdown();
        }

        public void addAppIdleStateChangeListener(UsageStatsManagerInternal.AppIdleStateChangeListener listener) {
            UsageStatsService.this.mAppStandby.addListener(listener);
            listener.onParoleStateChanged(isAppIdleParoleOn());
        }

        public void removeAppIdleStateChangeListener(UsageStatsManagerInternal.AppIdleStateChangeListener listener) {
            UsageStatsService.this.mAppStandby.removeListener(listener);
        }

        public byte[] getBackupPayload(int user, String key) {
            synchronized (UsageStatsService.this.mLock) {
                if (user != 0) {
                    return null;
                }
                byte[] backupPayload = UsageStatsService.this.getUserDataAndInitializeIfNeededLocked(user, UsageStatsService.this.checkAndGetTimeLocked()).getBackupPayload(key);
                return backupPayload;
            }
        }

        public void applyRestoredPayload(int user, String key, byte[] payload) {
            synchronized (UsageStatsService.this.mLock) {
                if (user == 0) {
                    UsageStatsService.this.getUserDataAndInitializeIfNeededLocked(user, UsageStatsService.this.checkAndGetTimeLocked()).applyRestoredPayload(key, payload);
                }
            }
        }

        public List<UsageStats> queryUsageStatsForUser(int userId, int intervalType, long beginTime, long endTime, boolean obfuscateInstantApps) {
            return UsageStatsService.this.queryUsageStats(userId, intervalType, beginTime, endTime, obfuscateInstantApps);
        }

        public void setLastJobRunTime(String packageName, int userId, long elapsedRealtime) {
            UsageStatsService.this.mAppStandby.setLastJobRunTime(packageName, userId, elapsedRealtime);
        }

        public long getTimeSinceLastJobRun(String packageName, int userId) {
            return UsageStatsService.this.mAppStandby.getTimeSinceLastJobRun(packageName, userId);
        }

        public void reportAppJobState(String packageName, int userId, int numDeferredJobs, long timeSinceLastJobRun) {
        }

        public void onActiveAdminAdded(String packageName, int userId) {
            UsageStatsService.this.mAppStandby.addActiveDeviceAdmin(packageName, userId);
        }

        public void setActiveAdminApps(Set<String> packageNames, int userId) {
            UsageStatsService.this.mAppStandby.setActiveAdminApps(packageNames, userId);
        }

        public void onAdminDataAvailable() {
            UsageStatsService.this.mAppStandby.onAdminDataAvailable();
        }

        public void reportSyncScheduled(String packageName, int userId, boolean exempted) {
            UsageStatsService.this.mAppStandby.postReportSyncScheduled(packageName, userId, exempted);
        }

        public void reportExemptedSyncStart(String packageName, int userId) {
            UsageStatsService.this.mAppStandby.postReportExemptedSyncStart(packageName, userId);
        }

        public UsageStatsManagerInternal.AppUsageLimitData getAppUsageLimit(String packageName, UserHandle user) {
            return UsageStatsService.this.mAppTimeLimit.getAppUsageLimit(packageName, user);
        }
    }
}
