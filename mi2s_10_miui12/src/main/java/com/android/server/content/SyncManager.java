package com.android.server.content;

import android.accounts.Account;
import android.accounts.AccountAndUser;
import android.accounts.AccountManager;
import android.accounts.AccountManagerInternal;
import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.usage.UsageStatsManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ISyncAdapter;
import android.content.ISyncAdapterUnsyncableAccountCallback;
import android.content.ISyncContext;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.PeriodicSync;
import android.content.ServiceConnection;
import android.content.SyncActivityTooManyDeletes;
import android.content.SyncAdapterType;
import android.content.SyncAdaptersCache;
import android.content.SyncInfo;
import android.content.SyncResult;
import android.content.SyncStatusInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ProviderInfo;
import android.content.pm.RegisteredServicesCache;
import android.content.pm.RegisteredServicesCacheListener;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.provider.Settings;
import android.text.format.Time;
import android.util.EventLog;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IBatteryStats;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.DeviceIdleController;
import com.android.server.LocalServices;
import com.android.server.accounts.AccountManagerService;
import com.android.server.am.ProcessPolicy;
import com.android.server.backup.AccountSyncSettingsBackupHelper;
import com.android.server.content.SyncManager;
import com.android.server.content.SyncStorageEngine;
import com.android.server.job.JobSchedulerInternal;
import com.android.server.pm.DumpState;
import com.android.server.slice.SliceClientPermissions;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

public class SyncManager {
    private static final boolean DEBUG_ACCOUNT_ACCESS = false;
    private static final int DELAY_RETRY_SYNC_IN_PROGRESS_IN_SECONDS = 10;
    private static final boolean ENABLE_SUSPICIOUS_CHECK = Build.IS_DEBUGGABLE;
    private static final String HANDLE_SYNC_ALARM_WAKE_LOCK = "SyncManagerHandleSyncAlarm";
    private static final AccountAndUser[] INITIAL_ACCOUNTS_ARRAY = new AccountAndUser[0];
    private static final long LOCAL_SYNC_DELAY = SystemProperties.getLong("sync.local_sync_delay", 30000);
    private static final int MAX_SYNC_JOB_ID = 110000;
    private static final int MIN_SYNC_JOB_ID = 100000;
    private static final int SYNC_ADAPTER_CONNECTION_FLAGS = 21;
    private static final long SYNC_DELAY_ON_CONFLICT = 10000;
    private static final long SYNC_DELAY_ON_LOW_STORAGE = 3600000;
    private static final String SYNC_LOOP_WAKE_LOCK = "SyncLoopWakeLock";
    private static final int SYNC_MONITOR_PROGRESS_THRESHOLD_BYTES = 10;
    private static final long SYNC_MONITOR_WINDOW_LENGTH_MILLIS = 60000;
    private static final int SYNC_OP_STATE_INVALID = 1;
    private static final int SYNC_OP_STATE_INVALID_NO_ACCOUNT_ACCESS = 2;
    private static final int SYNC_OP_STATE_VALID = 0;
    private static final String SYNC_WAKE_LOCK_PREFIX = "*sync*/";
    static final String TAG = "SyncManager";
    @GuardedBy({"SyncManager.class"})
    private static SyncManager sInstance;
    private static final Comparator<SyncOperation> sOpDumpComparator = $$Lambda$SyncManager$bVs0A6OYdmGkOiq_lbp5MiBwelw.INSTANCE;
    private static final Comparator<SyncOperation> sOpRuntimeComparator = $$Lambda$SyncManager$68MEyNkTh36YmYoFlURJoRa_cY.INSTANCE;
    private final AccountManager mAccountManager;
    /* access modifiers changed from: private */
    public final AccountManagerInternal mAccountManagerInternal;
    private final BroadcastReceiver mAccountsUpdatedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            SyncManager.this.updateRunningAccounts(new SyncStorageEngine.EndPoint((Account) null, (String) null, getSendingUserId()));
        }
    };
    protected final CopyOnWriteArrayList<ActiveSyncContext> mActiveSyncContexts = new CopyOnWriteArrayList<>();
    /* access modifiers changed from: private */
    public final IBatteryStats mBatteryStats;
    private ConnectivityManager mConnManagerDoNotUseDirectly;
    private BroadcastReceiver mConnectivityIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean wasConnected = SyncManager.this.mDataConnectionIsConnected;
            SyncManager syncManager = SyncManager.this;
            boolean unused = syncManager.mDataConnectionIsConnected = syncManager.readDataConnectionState();
            if (SyncManager.this.mDataConnectionIsConnected && !wasConnected) {
                if (Log.isLoggable("SyncManager", 2)) {
                    Slog.v("SyncManager", "Reconnection detected: clearing all backoffs");
                }
                SyncManager.this.clearAllBackoffs("network reconnect");
            }
        }
    };
    /* access modifiers changed from: private */
    public final SyncManagerConstants mConstants;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public volatile boolean mDataConnectionIsConnected = false;
    private volatile boolean mDeviceIsIdle = false;
    private JobScheduler mJobScheduler;
    private JobSchedulerInternal mJobSchedulerInternal;
    /* access modifiers changed from: private */
    public final SyncLogger mLogger;
    /* access modifiers changed from: private */
    public final NotificationManager mNotificationMgr;
    private final BroadcastReceiver mOtherIntentsReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.TIME_SET".equals(intent.getAction())) {
                SyncManager.this.mSyncStorageEngine.setClockValid();
            }
        }
    };
    /* access modifiers changed from: private */
    public final PackageManagerInternal mPackageManagerInternal;
    /* access modifiers changed from: private */
    public final PowerManager mPowerManager;
    /* access modifiers changed from: private */
    public volatile boolean mProvisioned;
    private final Random mRand;
    private volatile boolean mReportedSyncActive = false;
    /* access modifiers changed from: private */
    public volatile AccountAndUser[] mRunningAccounts = INITIAL_ACCOUNTS_ARRAY;
    private BroadcastReceiver mShutdownIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.w("SyncManager", "Writing sync state before shutdown...");
            SyncManager.this.getSyncStorageEngine().writeAllState();
            SyncManager.this.mLogger.log(SyncManager.this.getJobStats());
            SyncManager.this.mLogger.log("Shutting down.");
        }
    };
    private final BroadcastReceiver mStorageIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.DEVICE_STORAGE_LOW".equals(action)) {
                if (Log.isLoggable("SyncManager", 2)) {
                    Slog.v("SyncManager", "Internal storage is low.");
                }
                boolean unused = SyncManager.this.mStorageIsLow = true;
                SyncManager.this.cancelActiveSync(SyncStorageEngine.EndPoint.USER_ALL_PROVIDER_ALL_ACCOUNTS_ALL, (Bundle) null, "storage low");
            } else if ("android.intent.action.DEVICE_STORAGE_OK".equals(action)) {
                if (Log.isLoggable("SyncManager", 2)) {
                    Slog.v("SyncManager", "Internal storage is ok.");
                }
                boolean unused2 = SyncManager.this.mStorageIsLow = false;
                SyncManager.this.rescheduleSyncs(SyncStorageEngine.EndPoint.USER_ALL_PROVIDER_ALL_ACCOUNTS_ALL, "storage ok");
            }
        }
    };
    /* access modifiers changed from: private */
    public volatile boolean mStorageIsLow = false;
    protected final SyncAdaptersCache mSyncAdapters;
    final SyncHandler mSyncHandler;
    /* access modifiers changed from: private */
    public volatile PowerManager.WakeLock mSyncManagerWakeLock;
    /* access modifiers changed from: private */
    public SyncStorageEngine mSyncStorageEngine;
    private final HandlerThread mThread;
    @GuardedBy({"mUnlockedUsers"})
    private final SparseBooleanArray mUnlockedUsers = new SparseBooleanArray();
    private BroadcastReceiver mUserIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int userId = intent.getIntExtra("android.intent.extra.user_handle", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
            if (userId != -10000) {
                if ("android.intent.action.USER_REMOVED".equals(action)) {
                    SyncManager.this.onUserRemoved(userId);
                } else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                    SyncManager.this.onUserUnlocked(userId);
                } else if ("android.intent.action.USER_STOPPED".equals(action)) {
                    SyncManager.this.onUserStopped(userId);
                }
            }
        }
    };
    private final UserManager mUserManager;

    interface OnReadyCallback {
        void onReady();
    }

    /* JADX WARNING: type inference failed for: r0v2, types: [boolean, byte] */
    static /* synthetic */ boolean access$1476(SyncManager x0, int x1) {
        ? r0 = (byte) (x0.mProvisioned | x1);
        x0.mProvisioned = r0;
        return r0;
    }

    private boolean isJobIdInUseLockedH(int jobId, List<JobInfo> pendingJobs) {
        for (JobInfo job : pendingJobs) {
            if (job.getId() == jobId) {
                return true;
            }
        }
        Iterator<ActiveSyncContext> it = this.mActiveSyncContexts.iterator();
        while (it.hasNext()) {
            if (it.next().mSyncOperation.jobId == jobId) {
                return true;
            }
        }
        return false;
    }

    private int getUnusedJobIdH() {
        int newJobId;
        do {
            newJobId = this.mRand.nextInt(10000) + MIN_SYNC_JOB_ID;
        } while (isJobIdInUseLockedH(newJobId, this.mJobScheduler.getAllPendingJobs()));
        return newJobId;
    }

    /* access modifiers changed from: package-private */
    public List<SyncOperation> getAllPendingSyncs() {
        verifyJobScheduler();
        List<JobInfo> pendingJobs = this.mJobSchedulerInternal.getSystemScheduledPendingJobs();
        List<SyncOperation> pendingSyncs = new ArrayList<>(pendingJobs.size());
        for (JobInfo job : pendingJobs) {
            SyncOperation op = SyncOperation.maybeCreateFromJobExtras(job.getExtras());
            if (op != null) {
                pendingSyncs.add(op);
            }
        }
        return pendingSyncs;
    }

    private List<UserInfo> getAllUsers() {
        return this.mUserManager.getUsers();
    }

    /* access modifiers changed from: private */
    public boolean containsAccountAndUser(AccountAndUser[] accounts, Account account, int userId) {
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].userId == userId && accounts[i].account.equals(account)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void updateRunningAccounts(SyncStorageEngine.EndPoint target) {
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "sending MESSAGE_ACCOUNTS_UPDATED");
        }
        Message m = this.mSyncHandler.obtainMessage(9);
        m.obj = target;
        m.sendToTarget();
    }

    /* access modifiers changed from: private */
    public void removeStaleAccounts() {
        for (UserInfo user : this.mUserManager.getUsers(true)) {
            if (!user.partial) {
                this.mSyncStorageEngine.removeStaleAccounts(AccountManagerService.getSingleton().getAccounts(user.id, this.mContext.getOpPackageName()), user.id);
            }
        }
    }

    /* access modifiers changed from: private */
    public void clearAllBackoffs(String why) {
        this.mSyncStorageEngine.clearAllBackoffsLocked();
        rescheduleSyncs(SyncStorageEngine.EndPoint.USER_ALL_PROVIDER_ALL_ACCOUNTS_ALL, why);
    }

    /* access modifiers changed from: private */
    public boolean readDataConnectionState() {
        NetworkInfo networkInfo = getConnectivityManager().getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /* access modifiers changed from: private */
    public String getJobStats() {
        String str;
        JobSchedulerInternal js = (JobSchedulerInternal) LocalServices.getService(JobSchedulerInternal.class);
        StringBuilder sb = new StringBuilder();
        sb.append("JobStats: ");
        if (js == null) {
            str = "(JobSchedulerInternal==null)";
        } else {
            str = js.getPersistStats().toString();
        }
        sb.append(str);
        return sb.toString();
    }

    private ConnectivityManager getConnectivityManager() {
        ConnectivityManager connectivityManager;
        synchronized (this) {
            if (this.mConnManagerDoNotUseDirectly == null) {
                this.mConnManagerDoNotUseDirectly = (ConnectivityManager) this.mContext.getSystemService("connectivity");
            }
            connectivityManager = this.mConnManagerDoNotUseDirectly;
        }
        return connectivityManager;
    }

    private void cleanupJobs() {
        this.mSyncHandler.postAtFrontOfQueue(new Runnable() {
            public void run() {
                List<SyncOperation> ops = SyncManager.this.getAllPendingSyncs();
                Set<String> cleanedKeys = new HashSet<>();
                for (SyncOperation opx : ops) {
                    if (!cleanedKeys.contains(opx.key)) {
                        cleanedKeys.add(opx.key);
                        for (SyncOperation opy : ops) {
                            if (opx != opy && opx.key.equals(opy.key)) {
                                SyncManager.this.mLogger.log("Removing duplicate sync: ", opy);
                                SyncManager syncManager = SyncManager.this;
                                syncManager.cancelJob(opy, "cleanupJobs() x=" + opx + " y=" + opy);
                            }
                        }
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:36:0x00d0=Splitter:B:36:0x00d0, B:41:0x00d7=Splitter:B:41:0x00d7} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void verifyJobScheduler() {
        /*
            r11 = this;
            monitor-enter(r11)
            android.app.job.JobScheduler r0 = r11.mJobScheduler     // Catch:{ all -> 0x00db }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r11)
            return
        L_0x0007:
            long r0 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x00db }
            java.lang.String r2 = "SyncManager"
            r3 = 2
            boolean r2 = android.util.Log.isLoggable(r2, r3)     // Catch:{ all -> 0x00d6 }
            if (r2 == 0) goto L_0x0020
            java.lang.String r2 = "SyncManager"
            java.lang.String r3 = "initializing JobScheduler object."
            android.util.Log.d(r2, r3)     // Catch:{ all -> 0x001d }
            goto L_0x0020
        L_0x001d:
            r2 = move-exception
            goto L_0x00d7
        L_0x0020:
            android.content.Context r2 = r11.mContext     // Catch:{ all -> 0x00d6 }
            java.lang.String r3 = "jobscheduler"
            java.lang.Object r2 = r2.getSystemService(r3)     // Catch:{ all -> 0x00d6 }
            android.app.job.JobScheduler r2 = (android.app.job.JobScheduler) r2     // Catch:{ all -> 0x00d6 }
            r11.mJobScheduler = r2     // Catch:{ all -> 0x00d6 }
            java.lang.Class<com.android.server.job.JobSchedulerInternal> r2 = com.android.server.job.JobSchedulerInternal.class
            java.lang.Object r2 = com.android.server.LocalServices.getService(r2)     // Catch:{ all -> 0x00d6 }
            com.android.server.job.JobSchedulerInternal r2 = (com.android.server.job.JobSchedulerInternal) r2     // Catch:{ all -> 0x00d6 }
            r11.mJobSchedulerInternal = r2     // Catch:{ all -> 0x00d6 }
            android.app.job.JobScheduler r2 = r11.mJobScheduler     // Catch:{ all -> 0x00d6 }
            java.util.List r2 = r2.getAllPendingJobs()     // Catch:{ all -> 0x00d6 }
            r3 = 0
            r4 = 0
            java.util.Iterator r5 = r2.iterator()     // Catch:{ all -> 0x00d6 }
        L_0x0043:
            boolean r6 = r5.hasNext()     // Catch:{ all -> 0x00d6 }
            r7 = 1
            if (r6 == 0) goto L_0x006b
            java.lang.Object r6 = r5.next()     // Catch:{ all -> 0x001d }
            android.app.job.JobInfo r6 = (android.app.job.JobInfo) r6     // Catch:{ all -> 0x001d }
            android.os.PersistableBundle r8 = r6.getExtras()     // Catch:{ all -> 0x001d }
            com.android.server.content.SyncOperation r8 = com.android.server.content.SyncOperation.maybeCreateFromJobExtras(r8)     // Catch:{ all -> 0x001d }
            if (r8 == 0) goto L_0x006a
            boolean r9 = r8.isPeriodic     // Catch:{ all -> 0x001d }
            if (r9 == 0) goto L_0x0061
            int r3 = r3 + 1
            goto L_0x006a
        L_0x0061:
            int r4 = r4 + 1
            com.android.server.content.SyncStorageEngine r9 = r11.mSyncStorageEngine     // Catch:{ all -> 0x001d }
            com.android.server.content.SyncStorageEngine$EndPoint r10 = r8.target     // Catch:{ all -> 0x001d }
            r9.markPending(r10, r7)     // Catch:{ all -> 0x001d }
        L_0x006a:
            goto L_0x0043
        L_0x006b:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d6 }
            r5.<init>()     // Catch:{ all -> 0x00d6 }
            java.lang.String r6 = "Loaded persisted syncs: "
            r5.append(r6)     // Catch:{ all -> 0x00d6 }
            r5.append(r3)     // Catch:{ all -> 0x00d6 }
            java.lang.String r6 = " periodic syncs, "
            r5.append(r6)     // Catch:{ all -> 0x00d6 }
            r5.append(r4)     // Catch:{ all -> 0x00d6 }
            java.lang.String r6 = " oneshot syncs, "
            r5.append(r6)     // Catch:{ all -> 0x00d6 }
            int r6 = r2.size()     // Catch:{ all -> 0x00d6 }
            r5.append(r6)     // Catch:{ all -> 0x00d6 }
            java.lang.String r6 = " total system server jobs, "
            r5.append(r6)     // Catch:{ all -> 0x00d6 }
            java.lang.String r6 = r11.getJobStats()     // Catch:{ all -> 0x00d6 }
            r5.append(r6)     // Catch:{ all -> 0x00d6 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00d6 }
            java.lang.String r6 = "SyncManager"
            android.util.Slog.i(r6, r5)     // Catch:{ all -> 0x00d6 }
            com.android.server.content.SyncLogger r6 = r11.mLogger     // Catch:{ all -> 0x00d6 }
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x00d6 }
            r8 = 0
            r7[r8] = r5     // Catch:{ all -> 0x00d6 }
            r6.log(r7)     // Catch:{ all -> 0x00d6 }
            r11.cleanupJobs()     // Catch:{ all -> 0x00d6 }
            boolean r6 = ENABLE_SUSPICIOUS_CHECK     // Catch:{ all -> 0x00d6 }
            if (r6 == 0) goto L_0x00d0
            if (r3 != 0) goto L_0x00d0
            boolean r6 = r11.likelyHasPeriodicSyncs()     // Catch:{ all -> 0x001d }
            if (r6 == 0) goto L_0x00d0
            java.lang.String r6 = "SyncManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x001d }
            r7.<init>()     // Catch:{ all -> 0x001d }
            java.lang.String r8 = "Device booted with no persisted periodic syncs: "
            r7.append(r8)     // Catch:{ all -> 0x001d }
            r7.append(r5)     // Catch:{ all -> 0x001d }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x001d }
            android.util.Slog.wtf(r6, r7)     // Catch:{ all -> 0x001d }
        L_0x00d0:
            android.os.Binder.restoreCallingIdentity(r0)     // Catch:{ all -> 0x00db }
            monitor-exit(r11)
            return
        L_0x00d6:
            r2 = move-exception
        L_0x00d7:
            android.os.Binder.restoreCallingIdentity(r0)     // Catch:{ all -> 0x00db }
            throw r2     // Catch:{ all -> 0x00db }
        L_0x00db:
            r0 = move-exception
            monitor-exit(r11)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncManager.verifyJobScheduler():void");
    }

    private boolean likelyHasPeriodicSyncs() {
        try {
            return this.mSyncStorageEngine.getAuthorityCount() >= 6;
        } catch (Throwable th) {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public JobScheduler getJobScheduler() {
        verifyJobScheduler();
        return this.mJobScheduler;
    }

    public SyncManager(Context context, boolean factoryTest) {
        synchronized (SyncManager.class) {
            if (sInstance == null) {
                sInstance = this;
            } else {
                Slog.wtf("SyncManager", "SyncManager instantiated multiple times");
            }
        }
        this.mContext = context;
        this.mLogger = SyncLogger.getInstance();
        SyncStorageEngine.init(context, BackgroundThread.get().getLooper());
        this.mSyncStorageEngine = SyncStorageEngine.getSingleton();
        this.mSyncStorageEngine.setOnSyncRequestListener(new SyncStorageEngine.OnSyncRequestListener() {
            public void onSyncRequest(SyncStorageEngine.EndPoint info, int reason, Bundle extras, int syncExemptionFlag, int callingUid, int callingPid) {
                SyncStorageEngine.EndPoint endPoint = info;
                SyncManager.this.scheduleSync(endPoint.account, endPoint.userId, reason, endPoint.provider, extras, -2, syncExemptionFlag, callingUid, callingPid, (String) null);
            }
        });
        this.mSyncStorageEngine.setPeriodicSyncAddedListener(new SyncStorageEngine.PeriodicSyncAddedListener() {
            public void onPeriodicSyncAdded(SyncStorageEngine.EndPoint target, Bundle extras, long pollFrequency, long flex) {
                SyncManager.this.updateOrAddPeriodicSync(target, pollFrequency, flex, extras);
            }
        });
        this.mSyncStorageEngine.setOnAuthorityRemovedListener(new SyncStorageEngine.OnAuthorityRemovedListener() {
            public void onAuthorityRemoved(SyncStorageEngine.EndPoint removedAuthority) {
                SyncManager.this.removeSyncsForAuthority(removedAuthority, "onAuthorityRemoved");
            }
        });
        this.mSyncAdapters = new SyncAdaptersCache(this.mContext);
        this.mThread = new HandlerThread("SyncManager", 10);
        this.mThread.start();
        this.mSyncHandler = new SyncHandler(this.mThread.getLooper());
        this.mSyncAdapters.setListener(new RegisteredServicesCacheListener<SyncAdapterType>() {
            public void onServiceChanged(SyncAdapterType type, int userId, boolean removed) {
                if (!removed) {
                    SyncManager.this.scheduleSync((Account) null, -1, -3, type.authority, (Bundle) null, -2, 0, Process.myUid(), -1, (String) null);
                }
            }
        }, this.mSyncHandler);
        this.mRand = new Random(System.currentTimeMillis());
        this.mConstants = new SyncManagerConstants(context);
        context.registerReceiver(this.mConnectivityIntentReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        IntentFilter intentFilter = new IntentFilter("android.intent.action.DEVICE_STORAGE_LOW");
        intentFilter.addAction("android.intent.action.DEVICE_STORAGE_OK");
        context.registerReceiver(this.mStorageIntentReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter("android.intent.action.ACTION_SHUTDOWN");
        intentFilter2.setPriority(100);
        context.registerReceiver(this.mShutdownIntentReceiver, intentFilter2);
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("android.intent.action.USER_REMOVED");
        intentFilter3.addAction("android.intent.action.USER_UNLOCKED");
        intentFilter3.addAction("android.intent.action.USER_STOPPED");
        this.mContext.registerReceiverAsUser(this.mUserIntentReceiver, UserHandle.ALL, intentFilter3, (String) null, (Handler) null);
        context.registerReceiver(this.mOtherIntentsReceiver, new IntentFilter("android.intent.action.TIME_SET"));
        if (!factoryTest) {
            this.mNotificationMgr = (NotificationManager) context.getSystemService("notification");
        } else {
            this.mNotificationMgr = null;
        }
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        this.mAccountManager = (AccountManager) this.mContext.getSystemService("account");
        this.mAccountManagerInternal = (AccountManagerInternal) LocalServices.getService(AccountManagerInternal.class);
        this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mAccountManagerInternal.addOnAppPermissionChangeListener(new AccountManagerInternal.OnAppPermissionChangeListener() {
            public final void onAppPermissionChanged(Account account, int i) {
                SyncManager.this.lambda$new$0$SyncManager(account, i);
            }
        });
        this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
        this.mSyncManagerWakeLock = this.mPowerManager.newWakeLock(1, SYNC_LOOP_WAKE_LOCK);
        this.mSyncManagerWakeLock.setReferenceCounted(false);
        this.mProvisioned = isDeviceProvisioned();
        if (!this.mProvisioned) {
            final ContentResolver resolver = context.getContentResolver();
            ContentObserver provisionedObserver = new ContentObserver((Handler) null) {
                public void onChange(boolean selfChange) {
                    SyncManager syncManager = SyncManager.this;
                    SyncManager.access$1476(syncManager, syncManager.isDeviceProvisioned() ? 1 : 0);
                    if (SyncManager.this.mProvisioned) {
                        resolver.unregisterContentObserver(this);
                    }
                }
            };
            synchronized (this.mSyncHandler) {
                resolver.registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, provisionedObserver);
                this.mProvisioned |= isDeviceProvisioned();
                if (this.mProvisioned) {
                    resolver.unregisterContentObserver(provisionedObserver);
                }
            }
        }
        SyncManagerInjector.registerSyncSettingsObserver(this.mContext, this);
        if (!factoryTest) {
            this.mContext.registerReceiverAsUser(this.mAccountsUpdatedReceiver, UserHandle.ALL, new IntentFilter("android.accounts.LOGIN_ACCOUNTS_CHANGED"), (String) null, (Handler) null);
        }
        whiteListExistingSyncAdaptersIfNeeded();
        this.mLogger.log("Sync manager initialized: " + Build.FINGERPRINT);
    }

    public /* synthetic */ void lambda$new$0$SyncManager(Account account, int uid) {
        if (this.mAccountManagerInternal.hasAccountAccess(account, uid)) {
            scheduleSync(account, UserHandle.getUserId(uid), -2, (String) null, (Bundle) null, 3, 0, Process.myUid(), -2, (String) null);
        }
    }

    public /* synthetic */ void lambda$onStartUser$1$SyncManager(int userId) {
        this.mLogger.log("onStartUser: user=", Integer.valueOf(userId));
    }

    public void onStartUser(int userId) {
        this.mSyncHandler.post(new Runnable(userId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SyncManager.this.lambda$onStartUser$1$SyncManager(this.f$1);
            }
        });
    }

    public void onUnlockUser(int userId) {
        synchronized (this.mUnlockedUsers) {
            this.mUnlockedUsers.put(userId, true);
        }
        this.mSyncHandler.post(new Runnable(userId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SyncManager.this.lambda$onUnlockUser$2$SyncManager(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onUnlockUser$2$SyncManager(int userId) {
        this.mLogger.log("onUnlockUser: user=", Integer.valueOf(userId));
    }

    public void onStopUser(int userId) {
        synchronized (this.mUnlockedUsers) {
            this.mUnlockedUsers.put(userId, false);
        }
        this.mSyncHandler.post(new Runnable(userId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SyncManager.this.lambda$onStopUser$3$SyncManager(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onStopUser$3$SyncManager(int userId) {
        this.mLogger.log("onStopUser: user=", Integer.valueOf(userId));
    }

    private boolean isUserUnlocked(int userId) {
        boolean z;
        synchronized (this.mUnlockedUsers) {
            z = this.mUnlockedUsers.get(userId);
        }
        return z;
    }

    public void onBootPhase(int phase) {
        if (phase == 550) {
            this.mConstants.start();
        }
    }

    private void whiteListExistingSyncAdaptersIfNeeded() {
        SyncManager syncManager = this;
        if (syncManager.mSyncStorageEngine.shouldGrantSyncAdaptersAccountAccess()) {
            List<UserInfo> users = syncManager.mUserManager.getUsers(true);
            int userCount = users.size();
            int i = 0;
            while (i < userCount) {
                UserHandle userHandle = users.get(i).getUserHandle();
                int userId = userHandle.getIdentifier();
                for (RegisteredServicesCache.ServiceInfo<SyncAdapterType> service : syncManager.mSyncAdapters.getAllServices(userId)) {
                    String packageName = service.componentName.getPackageName();
                    Account[] accountsByTypeAsUser = syncManager.mAccountManager.getAccountsByTypeAsUser(((SyncAdapterType) service.type).accountType, userHandle);
                    int length = accountsByTypeAsUser.length;
                    int i2 = 0;
                    while (i2 < length) {
                        Account account = accountsByTypeAsUser[i2];
                        if (!syncManager.canAccessAccount(account, packageName, userId)) {
                            syncManager.mAccountManager.updateAppPermission(account, "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", service.uid, true);
                        }
                        i2++;
                        syncManager = this;
                    }
                    syncManager = this;
                }
                i++;
                syncManager = this;
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isDeviceProvisioned() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0;
    }

    private long jitterize(long minValue, long maxValue) {
        Random random = new Random(SystemClock.elapsedRealtime());
        long spread = maxValue - minValue;
        if (spread <= 2147483647L) {
            return ((long) random.nextInt((int) spread)) + minValue;
        }
        throw new IllegalArgumentException("the difference between the maxValue and the minValue must be less than 2147483647");
    }

    public SyncStorageEngine getSyncStorageEngine() {
        return this.mSyncStorageEngine;
    }

    private int getIsSyncable(Account account, int userId, String providerName) {
        int isSyncable = this.mSyncStorageEngine.getIsSyncable(account, userId, providerName);
        UserInfo userInfo = UserManager.get(this.mContext).getUserInfo(userId);
        if (userInfo == null || !userInfo.isRestricted()) {
            return isSyncable;
        }
        RegisteredServicesCache.ServiceInfo<SyncAdapterType> syncAdapterInfo = this.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey(providerName, account.type), userId);
        if (syncAdapterInfo == null) {
            return 0;
        }
        try {
            PackageInfo pInfo = AppGlobals.getPackageManager().getPackageInfo(syncAdapterInfo.componentName.getPackageName(), 0, userId);
            if (pInfo == null || pInfo.restrictedAccountType == null || !pInfo.restrictedAccountType.equals(account.type)) {
                return 0;
            }
            return isSyncable;
        } catch (RemoteException e) {
            return 0;
        }
    }

    /* access modifiers changed from: private */
    public void setAuthorityPendingState(SyncStorageEngine.EndPoint info) {
        for (SyncOperation op : getAllPendingSyncs()) {
            if (!op.isPeriodic && op.target.matchesSpec(info)) {
                getSyncStorageEngine().markPending(info, true);
                return;
            }
        }
        getSyncStorageEngine().markPending(info, false);
    }

    public void scheduleSync(Account requestedAccount, int userId, int reason, String requestedAuthority, Bundle extras, int targetSyncState, int syncExemptionFlag, int callingUid, int callingPid, String callingPackage) {
        scheduleSync(requestedAccount, userId, reason, requestedAuthority, extras, targetSyncState, 0, true, syncExemptionFlag, callingUid, callingPid, callingPackage);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:88:0x0330, code lost:
        if (r12.mSyncStorageEngine.getSyncAutomatically(r11.account, r11.userId, r7) != false) goto L_0x033a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0341  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0371  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void scheduleSync(android.accounts.Account r53, int r54, int r55, java.lang.String r56, android.os.Bundle r57, int r58, long r59, boolean r61, int r62, int r63, int r64, java.lang.String r65) {
        /*
            r52 = this;
            r14 = r52
            r15 = r53
            r13 = r54
            r12 = r56
            r11 = r58
            r8 = r59
            if (r57 != 0) goto L_0x0015
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            r10 = r0
            goto L_0x0017
        L_0x0015:
            r10 = r57
        L_0x0017:
            r10.size()
            r7 = 2
            java.lang.String r0 = "SyncManager"
            boolean r0 = android.util.Log.isLoggable(r0, r7)
            r5 = 5
            r4 = 4
            r3 = 3
            r2 = 0
            r1 = 1
            if (r0 == 0) goto L_0x00b0
            com.android.server.content.SyncLogger r0 = r14.mLogger
            r6 = 22
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r16 = "scheduleSync: account="
            r6[r2] = r16
            r6[r1] = r15
            java.lang.String r16 = " u"
            r6[r7] = r16
            java.lang.Integer r16 = java.lang.Integer.valueOf(r54)
            r6[r3] = r16
            java.lang.String r16 = " authority="
            r6[r4] = r16
            r6[r5] = r12
            r16 = 6
            java.lang.String r17 = " reason="
            r6[r16] = r17
            r16 = 7
            java.lang.Integer r17 = java.lang.Integer.valueOf(r55)
            r6[r16] = r17
            r16 = 8
            java.lang.String r17 = " extras="
            r6[r16] = r17
            r16 = 9
            r6[r16] = r10
            r16 = 10
            java.lang.String r17 = " cuid="
            r6[r16] = r17
            r16 = 11
            java.lang.Integer r17 = java.lang.Integer.valueOf(r63)
            r6[r16] = r17
            r16 = 12
            java.lang.String r17 = " cpid="
            r6[r16] = r17
            r16 = 13
            java.lang.Integer r17 = java.lang.Integer.valueOf(r64)
            r6[r16] = r17
            r16 = 14
            java.lang.String r17 = " cpkg="
            r6[r16] = r17
            r16 = 15
            r6[r16] = r65
            r16 = 16
            java.lang.String r17 = " mdm="
            r6[r16] = r17
            r16 = 17
            java.lang.Long r17 = java.lang.Long.valueOf(r59)
            r6[r16] = r17
            r16 = 18
            java.lang.String r17 = " ciar="
            r6[r16] = r17
            r16 = 19
            java.lang.Boolean r17 = java.lang.Boolean.valueOf(r61)
            r6[r16] = r17
            r16 = 20
            java.lang.String r17 = " sef="
            r6[r16] = r17
            r16 = 21
            java.lang.Integer r17 = java.lang.Integer.valueOf(r62)
            r6[r16] = r17
            r0.log(r6)
        L_0x00b0:
            r0 = 0
            r6 = -1
            if (r15 == 0) goto L_0x00e7
            if (r13 == r6) goto L_0x00c4
            android.accounts.AccountAndUser[] r4 = new android.accounts.AccountAndUser[r1]
            android.accounts.AccountAndUser r5 = new android.accounts.AccountAndUser
            r5.<init>(r15, r13)
            r4[r2] = r5
            r0 = r4
            r7 = r55
            r5 = r0
            goto L_0x00f0
        L_0x00c4:
            android.accounts.AccountAndUser[] r4 = r14.mRunningAccounts
            int r5 = r4.length
            r6 = r0
            r0 = r2
        L_0x00c9:
            if (r0 >= r5) goto L_0x00e3
            r7 = r4[r0]
            android.accounts.Account r3 = r7.account
            boolean r3 = r15.equals(r3)
            if (r3 == 0) goto L_0x00de
            java.lang.Class<android.accounts.AccountAndUser> r3 = android.accounts.AccountAndUser.class
            java.lang.Object[] r3 = com.android.internal.util.ArrayUtils.appendElement(r3, r6, r7)
            android.accounts.AccountAndUser[] r3 = (android.accounts.AccountAndUser[]) r3
            r6 = r3
        L_0x00de:
            int r0 = r0 + 1
            r3 = 3
            r7 = 2
            goto L_0x00c9
        L_0x00e3:
            r7 = r55
            r5 = r6
            goto L_0x00f0
        L_0x00e7:
            android.accounts.AccountAndUser[] r0 = r14.mRunningAccounts
            r7 = r55
            android.accounts.AccountAndUser[] r0 = com.android.server.content.SyncSecurityInjector.filterOutXiaomiAccount(r0, r7)
            r5 = r0
        L_0x00f0:
            boolean r0 = com.android.internal.util.ArrayUtils.isEmpty(r5)
            if (r0 == 0) goto L_0x0103
            com.android.server.content.SyncLogger r0 = r14.mLogger
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r3 = "scheduleSync: no accounts configured, dropping"
            r1[r2] = r3
            r0.log(r1)
            return
        L_0x0103:
            java.lang.String r0 = "upload"
            boolean r31 = r10.getBoolean(r0, r2)
            java.lang.String r0 = "force"
            boolean r32 = r10.getBoolean(r0, r2)
            java.lang.String r0 = "ignore_settings"
            if (r32 == 0) goto L_0x011e
            java.lang.String r3 = "ignore_backoff"
            r10.putBoolean(r3, r1)
            r10.putBoolean(r0, r1)
        L_0x011e:
            boolean r33 = r10.getBoolean(r0, r2)
            if (r31 == 0) goto L_0x0129
            r0 = 1
            r34 = r0
            goto L_0x0144
        L_0x0129:
            if (r32 == 0) goto L_0x012f
            r0 = 3
            r34 = r0
            goto L_0x0144
        L_0x012f:
            if (r12 != 0) goto L_0x0135
            r0 = 2
            r34 = r0
            goto L_0x0144
        L_0x0135:
            java.lang.String r0 = "feed"
            boolean r0 = r10.containsKey(r0)
            if (r0 == 0) goto L_0x0141
            r0 = 5
            r34 = r0
            goto L_0x0144
        L_0x0141:
            r0 = 0
            r34 = r0
        L_0x0144:
            int r4 = r5.length
            r3 = r2
        L_0x0146:
            if (r3 >= r4) goto L_0x04e0
            r0 = r5[r3]
            if (r13 < 0) goto L_0x016a
            int r6 = r0.userId
            if (r6 < 0) goto L_0x016a
            int r6 = r0.userId
            if (r13 == r6) goto L_0x016a
            r41 = r2
            r19 = r3
            r44 = r4
            r16 = r5
            r45 = r10
            r13 = r14
            r18 = 2
            r43 = 3
            r46 = -1
            r49 = 5
            r14 = r8
            goto L_0x04c7
        L_0x016a:
            java.util.HashSet r6 = new java.util.HashSet
            r6.<init>()
            android.content.SyncAdaptersCache r2 = r14.mSyncAdapters
            int r1 = r0.userId
            java.util.Collection r1 = r2.getAllServices(r1)
            java.util.Iterator r1 = r1.iterator()
        L_0x017b:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0195
            java.lang.Object r2 = r1.next()
            android.content.pm.RegisteredServicesCache$ServiceInfo r2 = (android.content.pm.RegisteredServicesCache.ServiceInfo) r2
            r22 = r1
            java.lang.Object r1 = r2.type
            android.content.SyncAdapterType r1 = (android.content.SyncAdapterType) r1
            java.lang.String r1 = r1.authority
            r6.add(r1)
            r1 = r22
            goto L_0x017b
        L_0x0195:
            if (r12 == 0) goto L_0x01a3
            boolean r1 = r6.contains(r12)
            r6.clear()
            if (r1 == 0) goto L_0x01a3
            r6.add(r12)
        L_0x01a3:
            java.util.Iterator r35 = r6.iterator()
        L_0x01a7:
            boolean r1 = r35.hasNext()
            if (r1 == 0) goto L_0x04af
            java.lang.Object r1 = r35.next()
            r2 = r1
            java.lang.String r2 = (java.lang.String) r2
            android.accounts.Account r1 = r0.account
            r22 = r3
            int r3 = r0.userId
            r23 = r4
            r4 = r61 ^ 1
            int r4 = r14.computeSyncable(r1, r3, r2, r4)
            if (r4 != 0) goto L_0x01c9
            r3 = r22
            r4 = r23
            goto L_0x01a7
        L_0x01c9:
            android.content.SyncAdaptersCache r1 = r14.mSyncAdapters
            android.accounts.Account r3 = r0.account
            java.lang.String r3 = r3.type
            android.content.SyncAdapterType r3 = android.content.SyncAdapterType.newKey(r2, r3)
            r24 = r2
            int r2 = r0.userId
            android.content.pm.RegisteredServicesCache$ServiceInfo r3 = r1.getServiceInfo(r3, r2)
            if (r3 != 0) goto L_0x01e2
            r3 = r22
            r4 = r23
            goto L_0x01a7
        L_0x01e2:
            int r2 = r3.uid
            r1 = 3
            if (r4 != r1) goto L_0x027f
            com.android.server.content.SyncLogger r1 = r14.mLogger
            r25 = r2
            r21 = r4
            r2 = 1
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r26 = "scheduleSync: Not scheduling sync operation: isSyncable == SYNCABLE_NO_ACCOUNT_ACCESS"
            r20 = 0
            r4[r20] = r26
            r1.log(r4)
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>(r10)
            r17 = r6
            r4 = -1
            r6 = r1
            android.content.ComponentName r1 = r3.componentName
            java.lang.String r1 = r1.getPackageName()
            boolean r26 = r14.wasPackageEverLaunched(r1, r13)
            if (r26 != 0) goto L_0x0216
            r6 = r17
            r3 = r22
            r4 = r23
            goto L_0x01a7
        L_0x0216:
            android.accounts.AccountManagerInternal r15 = r14.mAccountManagerInternal
            android.accounts.Account r14 = r0.account
            r26 = r14
            android.os.RemoteCallback r14 = new android.os.RemoteCallback
            r27 = r15
            com.android.server.content.-$$Lambda$SyncManager$BRG-YMU-C9QC6JWVXAvsoEZC6Zc r15 = new com.android.server.content.-$$Lambda$SyncManager$BRG-YMU-C9QC6JWVXAvsoEZC6Zc
            r36 = r0
            r0 = r15
            r2 = r1
            r19 = 3
            r1 = r52
            r40 = r2
            r41 = r20
            r38 = r24
            r39 = r25
            r2 = r36
            r42 = r3
            r43 = r19
            r19 = r22
            r3 = r54
            r20 = r21
            r44 = r23
            r4 = r55
            r16 = r5
            r5 = r38
            r18 = 2
            r7 = r58
            r8 = r59
            r48 = r10
            r10 = r62
            r11 = r63
            r12 = r64
            r13 = r65
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r10, r11, r12, r13)
            r14.<init>(r15)
            r13 = r54
            r2 = r26
            r1 = r27
            r0 = r40
            r1.requestAccountAccess(r2, r0, r13, r14)
            r14 = r52
            r15 = r53
            r7 = r55
            r12 = r56
            r11 = r58
            r5 = r16
            r6 = r17
            r3 = r19
            r0 = r36
            r4 = r44
            r10 = r48
            goto L_0x01a7
        L_0x027f:
            r36 = r0
            r43 = r1
            r39 = r2
            r42 = r3
            r20 = r4
            r16 = r5
            r17 = r6
            r48 = r10
            r19 = r22
            r44 = r23
            r38 = r24
            r18 = 2
            r41 = 0
            r14 = r42
            java.lang.Object r0 = r14.type
            android.content.SyncAdapterType r0 = (android.content.SyncAdapterType) r0
            boolean r15 = r0.allowParallelSyncs()
            java.lang.Object r0 = r14.type
            android.content.SyncAdapterType r0 = (android.content.SyncAdapterType) r0
            boolean r40 = r0.isAlwaysSyncable()
            if (r61 != 0) goto L_0x02cf
            if (r20 >= 0) goto L_0x02cf
            if (r40 == 0) goto L_0x02cf
            r12 = r52
            com.android.server.content.SyncStorageEngine r0 = r12.mSyncStorageEngine
            r11 = r36
            android.accounts.Account r1 = r11.account
            int r2 = r11.userId
            r26 = 1
            r22 = r0
            r23 = r1
            r24 = r2
            r25 = r38
            r27 = r63
            r28 = r64
            r22.setIsSyncable(r23, r24, r25, r26, r27, r28)
            r4 = 1
            r10 = r4
            goto L_0x02d5
        L_0x02cf:
            r12 = r52
            r11 = r36
            r10 = r20
        L_0x02d5:
            r0 = -2
            r9 = r58
            if (r9 == r0) goto L_0x02f3
            if (r9 == r10) goto L_0x02f3
            r15 = r53
            r7 = r55
            r0 = r11
            r14 = r12
            r5 = r16
            r6 = r17
            r3 = r19
            r4 = r44
            r10 = r48
            r12 = r56
            r11 = r9
            r8 = r59
            goto L_0x01a7
        L_0x02f3:
            java.lang.Object r1 = r14.type
            android.content.SyncAdapterType r1 = (android.content.SyncAdapterType) r1
            boolean r1 = r1.supportsUploading()
            if (r1 != 0) goto L_0x0316
            if (r31 == 0) goto L_0x0316
            r15 = r53
            r7 = r55
            r0 = r11
            r14 = r12
            r5 = r16
            r6 = r17
            r3 = r19
            r4 = r44
            r10 = r48
            r12 = r56
            r11 = r9
            r8 = r59
            goto L_0x01a7
        L_0x0316:
            if (r10 < 0) goto L_0x0338
            if (r33 != 0) goto L_0x0338
            com.android.server.content.SyncStorageEngine r1 = r12.mSyncStorageEngine
            int r2 = r11.userId
            boolean r1 = r1.getMasterSyncAutomatically(r2)
            if (r1 == 0) goto L_0x0333
            com.android.server.content.SyncStorageEngine r1 = r12.mSyncStorageEngine
            android.accounts.Account r2 = r11.account
            int r3 = r11.userId
            r7 = r38
            boolean r1 = r1.getSyncAutomatically(r2, r3, r7)
            if (r1 == 0) goto L_0x0335
            goto L_0x033a
        L_0x0333:
            r7 = r38
        L_0x0335:
            r1 = r41
            goto L_0x033b
        L_0x0338:
            r7 = r38
        L_0x033a:
            r1 = 1
        L_0x033b:
            r36 = r1
            java.lang.String r1 = " "
            if (r36 != 0) goto L_0x0371
            com.android.server.content.SyncLogger r0 = r12.mLogger
            r8 = 5
            java.lang.Object[] r2 = new java.lang.Object[r8]
            java.lang.String r3 = "scheduleSync: sync of "
            r2[r41] = r3
            r6 = 1
            r2[r6] = r11
            r2[r18] = r1
            r2[r43] = r7
            java.lang.String r1 = " is not allowed, dropping request"
            r4 = 4
            r2[r4] = r1
            r0.log(r2)
            r15 = r53
            r7 = r55
            r0 = r11
            r14 = r12
            r5 = r16
            r6 = r17
            r3 = r19
            r4 = r44
            r10 = r48
            r12 = r56
            r11 = r9
            r8 = r59
            goto L_0x01a7
        L_0x0371:
            r4 = 4
            r6 = 1
            r8 = 5
            com.android.server.content.SyncStorageEngine$EndPoint r2 = new com.android.server.content.SyncStorageEngine$EndPoint
            android.accounts.Account r3 = r11.account
            int r5 = r11.userId
            r2.<init>(r3, r7, r5)
            r3 = r2
            com.android.server.content.SyncStorageEngine r2 = r12.mSyncStorageEngine
            long r37 = r2.getDelayUntilTime(r3)
            android.content.ComponentName r2 = r14.componentName
            java.lang.String r42 = r2.getPackageName()
            r2 = -1
            if (r10 != r2) goto L_0x042c
            if (r61 == 0) goto L_0x03d2
            android.os.Bundle r5 = new android.os.Bundle
            r1 = r48
            r5.<init>(r1)
            android.content.Context r0 = r12.mContext
            int r13 = r11.userId
            r57 = r15
            com.android.server.content.-$$Lambda$SyncManager$XKEiBZ17uDgUCTwf_kh9_pH7usQ r15 = new com.android.server.content.-$$Lambda$SyncManager$XKEiBZ17uDgUCTwf_kh9_pH7usQ
            r20 = r13
            r13 = r0
            r0 = r15
            r45 = r1
            r1 = r52
            r46 = r2
            r2 = r11
            r47 = r3
            r3 = r55
            r4 = r7
            r6 = r58
            r48 = r7
            r49 = r8
            r7 = r59
            r9 = r62
            r50 = r10
            r10 = r63
            r51 = r11
            r11 = r64
            r12 = r65
            r0.<init>(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12)
            r0 = r20
            sendOnUnsyncableAccount(r13, r14, r0, r15)
            r13 = r52
            r14 = r59
            r22 = r51
            goto L_0x0495
        L_0x03d2:
            r46 = r2
            r47 = r3
            r49 = r8
            r50 = r10
            r51 = r11
            r57 = r15
            r45 = r48
            r48 = r7
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r2 = "initialize"
            r12 = 1
            r0.putBoolean(r2, r12)
            r13 = r52
            com.android.server.content.SyncLogger r2 = r13.mLogger
            r15 = 4
            java.lang.Object[] r3 = new java.lang.Object[r15]
            java.lang.String r4 = "scheduleSync: schedule initialisation sync "
            r3[r41] = r4
            r3[r12] = r11
            r3[r18] = r1
            r3[r43] = r48
            r2.log(r3)
            com.android.server.content.SyncOperation r1 = new com.android.server.content.SyncOperation
            android.accounts.Account r2 = r11.account
            int r3 = r11.userId
            r20 = r1
            r21 = r2
            r22 = r3
            r23 = r39
            r24 = r42
            r25 = r55
            r26 = r34
            r27 = r48
            r28 = r0
            r29 = r57
            r30 = r62
            r20.<init>(r21, r22, r23, r24, r25, r26, r27, r28, r29, r30)
            r9 = r59
            r13.postScheduleSyncMessage(r1, r9)
            r14 = r9
            r22 = r11
            goto L_0x0495
        L_0x042c:
            r46 = r2
            r47 = r3
            r49 = r8
            r50 = r10
            r13 = r12
            r57 = r15
            r45 = r48
            r9 = r59
            r15 = r4
            r12 = r6
            r48 = r7
            r8 = r58
            if (r8 == r0) goto L_0x045e
            r0 = r50
            if (r8 != r0) goto L_0x0448
            goto L_0x0460
        L_0x0448:
            com.android.server.content.SyncLogger r2 = r13.mLogger
            java.lang.Object[] r3 = new java.lang.Object[r15]
            java.lang.String r4 = "scheduleSync: not handling "
            r3[r41] = r4
            r3[r12] = r11
            r3[r18] = r1
            r3[r43] = r48
            r2.log(r3)
            r14 = r9
            r22 = r11
            goto L_0x0495
        L_0x045e:
            r0 = r50
        L_0x0460:
            com.android.server.content.SyncLogger r2 = r13.mLogger
            java.lang.Object[] r3 = new java.lang.Object[r15]
            java.lang.String r4 = "scheduleSync: scheduling sync "
            r3[r41] = r4
            r3[r12] = r11
            r3[r18] = r1
            r3[r43] = r48
            r2.log(r3)
            com.android.server.content.SyncOperation r7 = new com.android.server.content.SyncOperation
            android.accounts.Account r2 = r11.account
            int r3 = r11.userId
            r1 = r7
            r4 = r39
            r5 = r42
            r6 = r55
            r12 = r7
            r7 = r34
            r8 = r48
            r20 = r14
            r14 = r9
            r9 = r45
            r10 = r57
            r22 = r11
            r11 = r62
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r13.postScheduleSyncMessage(r12, r14)
        L_0x0495:
            r7 = r55
            r12 = r56
            r11 = r58
            r8 = r14
            r5 = r16
            r6 = r17
            r3 = r19
            r0 = r22
            r4 = r44
            r10 = r45
            r15 = r53
            r14 = r13
            r13 = r54
            goto L_0x01a7
        L_0x04af:
            r22 = r0
            r19 = r3
            r44 = r4
            r16 = r5
            r17 = r6
            r45 = r10
            r13 = r14
            r18 = 2
            r41 = 0
            r43 = 3
            r46 = -1
            r49 = 5
            r14 = r8
        L_0x04c7:
            int r3 = r19 + 1
            r7 = r55
            r12 = r56
            r11 = r58
            r8 = r14
            r5 = r16
            r2 = r41
            r4 = r44
            r10 = r45
            r1 = 1
            r15 = r53
            r14 = r13
            r13 = r54
            goto L_0x0146
        L_0x04e0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncManager.scheduleSync(android.accounts.Account, int, int, java.lang.String, android.os.Bundle, int, long, boolean, int, int, int, java.lang.String):void");
    }

    public /* synthetic */ void lambda$scheduleSync$4$SyncManager(AccountAndUser account, int userId, int reason, String authority, Bundle finalExtras, int targetSyncState, long minDelayMillis, int syncExemptionFlag, int callingUid, int callingPid, String callingPackage, Bundle result) {
        Bundle bundle = result;
        if (bundle == null) {
            AccountAndUser accountAndUser = account;
        } else if (bundle.getBoolean("booleanResult")) {
            scheduleSync(account.account, userId, reason, authority, finalExtras, targetSyncState, minDelayMillis, true, syncExemptionFlag, callingUid, callingPid, callingPackage);
        } else {
            AccountAndUser accountAndUser2 = account;
        }
    }

    public /* synthetic */ void lambda$scheduleSync$5$SyncManager(AccountAndUser account, int reason, String authority, Bundle finalExtras, int targetSyncState, long minDelayMillis, int syncExemptionFlag, int callingUid, int callingPid, String callingPackage) {
        AccountAndUser accountAndUser = account;
        scheduleSync(accountAndUser.account, accountAndUser.userId, reason, authority, finalExtras, targetSyncState, minDelayMillis, false, syncExemptionFlag, callingUid, callingPid, callingPackage);
    }

    public int computeSyncable(Account account, int userId, String authority, boolean checkAccountAccess) {
        RegisteredServicesCache.ServiceInfo<SyncAdapterType> syncAdapterInfo;
        int status = getIsSyncable(account, userId, authority);
        if (status == 0 || (syncAdapterInfo = this.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey(authority, account.type), userId)) == null) {
            return 0;
        }
        int owningUid = syncAdapterInfo.uid;
        String owningPackage = syncAdapterInfo.componentName.getPackageName();
        try {
            if (ActivityManager.getService().isAppStartModeDisabled(owningUid, owningPackage)) {
                Slog.w("SyncManager", "Not scheduling job " + syncAdapterInfo.uid + ":" + syncAdapterInfo.componentName + " -- package not allowed to start");
                return 0;
            }
        } catch (RemoteException e) {
        }
        if (!checkAccountAccess || canAccessAccount(account, owningPackage, owningUid)) {
            return status;
        }
        Log.w("SyncManager", "Access to " + SyncLogger.logSafe(account) + " denied for package " + owningPackage + " in UID " + syncAdapterInfo.uid);
        return 3;
    }

    private boolean canAccessAccount(Account account, String packageName, int uid) {
        if (this.mAccountManager.hasAccountAccess(account, packageName, UserHandle.getUserHandleForUid(uid))) {
            return true;
        }
        try {
            this.mContext.getPackageManager().getApplicationInfoAsUser(packageName, DumpState.DUMP_DEXOPT, UserHandle.getUserId(uid));
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void removeSyncsForAuthority(SyncStorageEngine.EndPoint info, String why) {
        this.mLogger.log("removeSyncsForAuthority: ", info, why);
        verifyJobScheduler();
        for (SyncOperation op : getAllPendingSyncs()) {
            if (op.target.matchesSpec(info)) {
                this.mLogger.log("canceling: ", op);
                cancelJob(op, why);
            }
        }
    }

    public void removePeriodicSync(SyncStorageEngine.EndPoint target, Bundle extras, String why) {
        Message m = this.mSyncHandler.obtainMessage(14, Pair.create(target, why));
        m.setData(extras);
        m.sendToTarget();
    }

    public void updateOrAddPeriodicSync(SyncStorageEngine.EndPoint target, long pollFrequency, long flex, Bundle extras) {
        this.mSyncHandler.obtainMessage(13, new UpdatePeriodicSyncMessagePayload(target, pollFrequency, flex, extras)).sendToTarget();
    }

    public List<PeriodicSync> getPeriodicSyncs(SyncStorageEngine.EndPoint target) {
        List<SyncOperation> ops = getAllPendingSyncs();
        List<PeriodicSync> periodicSyncs = new ArrayList<>();
        for (SyncOperation op : ops) {
            if (!op.isPeriodic) {
                SyncStorageEngine.EndPoint endPoint = target;
            } else if (op.target.matchesSpec(target)) {
                periodicSyncs.add(new PeriodicSync(op.target.account, op.target.provider, op.extras, op.periodMillis / 1000, op.flexMillis / 1000));
            }
        }
        SyncStorageEngine.EndPoint endPoint2 = target;
        return periodicSyncs;
    }

    public void scheduleLocalSync(Account account, int userId, int reason, String authority, int syncExemptionFlag, int callingUid, int callingPid, String callingPackage) {
        Bundle extras = new Bundle();
        extras.putBoolean("upload", true);
        scheduleSync(account, userId, reason, authority, extras, -2, LOCAL_SYNC_DELAY, true, syncExemptionFlag, callingUid, callingPid, callingPackage);
    }

    public SyncAdapterType[] getSyncAdapterTypes(int userId) {
        Collection<RegisteredServicesCache.ServiceInfo<SyncAdapterType>> serviceInfos = this.mSyncAdapters.getAllServices(userId);
        SyncAdapterType[] types = new SyncAdapterType[serviceInfos.size()];
        int i = 0;
        for (RegisteredServicesCache.ServiceInfo<SyncAdapterType> serviceInfo : serviceInfos) {
            types[i] = (SyncAdapterType) serviceInfo.type;
            i++;
        }
        return types;
    }

    public String[] getSyncAdapterPackagesForAuthorityAsUser(String authority, int userId) {
        return this.mSyncAdapters.getSyncAdapterPackagesForAuthority(authority, userId);
    }

    /* access modifiers changed from: private */
    public void sendSyncFinishedOrCanceledMessage(ActiveSyncContext syncContext, SyncResult syncResult) {
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "sending MESSAGE_SYNC_FINISHED");
        }
        Message msg = this.mSyncHandler.obtainMessage();
        msg.what = 1;
        msg.obj = new SyncFinishedOrCancelledMessagePayload(syncContext, syncResult);
        this.mSyncHandler.sendMessage(msg);
    }

    private void sendCancelSyncsMessage(SyncStorageEngine.EndPoint info, Bundle extras, String why) {
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "sending MESSAGE_CANCEL");
        }
        this.mLogger.log("sendCancelSyncsMessage() ep=", info, " why=", why);
        Message msg = this.mSyncHandler.obtainMessage();
        msg.what = 6;
        msg.setData(extras);
        msg.obj = info;
        this.mSyncHandler.sendMessage(msg);
    }

    /* access modifiers changed from: private */
    public void postMonitorSyncProgressMessage(ActiveSyncContext activeSyncContext) {
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "posting MESSAGE_SYNC_MONITOR in 60s");
        }
        activeSyncContext.mBytesTransferredAtLastPoll = getTotalBytesTransferredByUid(activeSyncContext.mSyncAdapterUid);
        activeSyncContext.mLastPolledTimeElapsed = SystemClock.elapsedRealtime();
        this.mSyncHandler.sendMessageDelayed(this.mSyncHandler.obtainMessage(8, activeSyncContext), 60000);
    }

    public void postScheduleSyncMessage(SyncOperation syncOperation, long minDelayMillis) {
        this.mSyncHandler.obtainMessage(12, new ScheduleSyncMessagePayload(syncOperation, minDelayMillis)).sendToTarget();
    }

    /* access modifiers changed from: private */
    public long getTotalBytesTransferredByUid(int uid) {
        return TrafficStats.getUidRxBytes(uid) + TrafficStats.getUidTxBytes(uid);
    }

    private class SyncFinishedOrCancelledMessagePayload {
        public final ActiveSyncContext activeSyncContext;
        public final SyncResult syncResult;

        SyncFinishedOrCancelledMessagePayload(ActiveSyncContext syncContext, SyncResult syncResult2) {
            this.activeSyncContext = syncContext;
            this.syncResult = syncResult2;
        }
    }

    private class UpdatePeriodicSyncMessagePayload {
        public final Bundle extras;
        public final long flex;
        public final long pollFrequency;
        public final SyncStorageEngine.EndPoint target;

        UpdatePeriodicSyncMessagePayload(SyncStorageEngine.EndPoint target2, long pollFrequency2, long flex2, Bundle extras2) {
            this.target = target2;
            this.pollFrequency = pollFrequency2;
            this.flex = flex2;
            this.extras = extras2;
        }
    }

    private static class ScheduleSyncMessagePayload {
        final long minDelayMillis;
        final SyncOperation syncOperation;

        ScheduleSyncMessagePayload(SyncOperation syncOperation2, long minDelayMillis2) {
            this.syncOperation = syncOperation2;
            this.minDelayMillis = minDelayMillis2;
        }
    }

    /* access modifiers changed from: private */
    public void clearBackoffSetting(SyncStorageEngine.EndPoint target, String why) {
        Pair<Long, Long> backoff = this.mSyncStorageEngine.getBackoff(target);
        if (backoff == null || ((Long) backoff.first).longValue() != -1 || ((Long) backoff.second).longValue() != -1) {
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "Clearing backoffs for " + target);
            }
            this.mSyncStorageEngine.setBackoff(target, -1, -1);
            rescheduleSyncs(target, why);
        }
    }

    /* access modifiers changed from: private */
    public void increaseBackoffSetting(SyncStorageEngine.EndPoint target) {
        long newDelayInMs;
        SyncStorageEngine.EndPoint endPoint = target;
        long now = SystemClock.elapsedRealtime();
        Pair<Long, Long> previousSettings = this.mSyncStorageEngine.getBackoff(endPoint);
        long newDelayInMs2 = -1;
        if (previousSettings != null) {
            if (now >= ((Long) previousSettings.first).longValue()) {
                newDelayInMs2 = (long) (((float) ((Long) previousSettings.second).longValue()) * this.mConstants.getRetryTimeIncreaseFactor());
            } else if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "Still in backoff, do not increase it. Remaining: " + ((((Long) previousSettings.first).longValue() - now) / 1000) + " seconds.");
                return;
            } else {
                return;
            }
        }
        if (newDelayInMs2 <= 0) {
            long initialRetryMs = (long) (this.mConstants.getInitialSyncRetryTimeInSeconds() * 1000);
            newDelayInMs2 = jitterize(initialRetryMs, (long) (((double) initialRetryMs) * 1.1d));
        }
        long maxSyncRetryTimeInSeconds = (long) this.mConstants.getMaxSyncRetryTimeInSeconds();
        if (newDelayInMs2 > maxSyncRetryTimeInSeconds * 1000) {
            newDelayInMs = 1000 * maxSyncRetryTimeInSeconds;
        } else {
            newDelayInMs = newDelayInMs2;
        }
        long backoff = now + newDelayInMs;
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "Backoff until: " + backoff + ", delayTime: " + newDelayInMs);
        }
        this.mSyncStorageEngine.setBackoff(target, backoff, newDelayInMs);
        rescheduleSyncs(endPoint, "increaseBackoffSetting");
    }

    /* access modifiers changed from: private */
    public void rescheduleSyncs(SyncStorageEngine.EndPoint target, String why) {
        this.mLogger.log("rescheduleSyncs() ep=", target, " why=", why);
        int count = 0;
        for (SyncOperation op : getAllPendingSyncs()) {
            if (!op.isPeriodic && op.target.matchesSpec(target)) {
                count++;
                cancelJob(op, why);
                postScheduleSyncMessage(op, 0);
            }
        }
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "Rescheduled " + count + " syncs for " + target);
        }
    }

    /* access modifiers changed from: private */
    public void setDelayUntilTime(SyncStorageEngine.EndPoint target, long delayUntilSeconds) {
        long newDelayUntilTime;
        long delayUntil = 1000 * delayUntilSeconds;
        long absoluteNow = System.currentTimeMillis();
        if (delayUntil > absoluteNow) {
            newDelayUntilTime = SystemClock.elapsedRealtime() + (delayUntil - absoluteNow);
        } else {
            newDelayUntilTime = 0;
        }
        this.mSyncStorageEngine.setDelayUntilTime(target, newDelayUntilTime);
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "Delay Until time set to " + newDelayUntilTime + " for " + target);
        }
        rescheduleSyncs(target, "delayUntil newDelayUntilTime: " + newDelayUntilTime);
    }

    /* access modifiers changed from: private */
    public boolean isAdapterDelayed(SyncStorageEngine.EndPoint target) {
        long now = SystemClock.elapsedRealtime();
        Pair<Long, Long> backoff = this.mSyncStorageEngine.getBackoff(target);
        if ((backoff == null || ((Long) backoff.first).longValue() == -1 || ((Long) backoff.first).longValue() <= now) && this.mSyncStorageEngine.getDelayUntilTime(target) <= now) {
            return false;
        }
        return true;
    }

    public void cancelActiveSync(SyncStorageEngine.EndPoint info, Bundle extras, String why) {
        sendCancelSyncsMessage(info, extras, why);
    }

    /* access modifiers changed from: private */
    public void scheduleSyncOperationH(SyncOperation syncOperation) {
        scheduleSyncOperationH(syncOperation, 0);
    }

    /* access modifiers changed from: private */
    public void scheduleSyncOperationH(SyncOperation syncOperation, long minDelay) {
        String str;
        long minDelay2;
        long minDelay3;
        SyncManager syncManager;
        boolean z;
        DeviceIdleController.LocalService dic;
        int inheritedSyncExemptionFlag;
        long now;
        int inheritedSyncExemptionFlag2;
        long backoffDelay;
        long delayUntilDelay;
        SyncOperation syncOperation2 = syncOperation;
        boolean isLoggable = Log.isLoggable("SyncManager", 2);
        if (syncOperation2 == null) {
            Slog.e("SyncManager", "Can't schedule null sync operation.");
            return;
        }
        if (!syncOperation.ignoreBackoff()) {
            Pair<Long, Long> backoff = this.mSyncStorageEngine.getBackoff(syncOperation2.target);
            if (backoff == null) {
                Slog.e("SyncManager", "Couldn't find backoff values for " + SyncLogger.logSafe(syncOperation2.target));
                backoff = new Pair<>(-1L, -1L);
            }
            long now2 = SystemClock.elapsedRealtime();
            if (((Long) backoff.first).longValue() == -1) {
                backoffDelay = 0;
            } else {
                backoffDelay = ((Long) backoff.first).longValue() - now2;
            }
            long delayUntil = this.mSyncStorageEngine.getDelayUntilTime(syncOperation2.target);
            long delayUntilDelay2 = delayUntil > now2 ? delayUntil - now2 : 0;
            if (isLoggable) {
                StringBuilder sb = new StringBuilder();
                sb.append("backoff delay:");
                sb.append(backoffDelay);
                sb.append(" delayUntil delay:");
                delayUntilDelay = delayUntilDelay2;
                sb.append(delayUntilDelay);
                Slog.v("SyncManager", sb.toString());
            } else {
                delayUntilDelay = delayUntilDelay2;
            }
            str = "SyncManager";
            Pair<Long, Long> pair = backoff;
            long j = delayUntilDelay;
            minDelay2 = Math.max(minDelay, Math.max(backoffDelay, delayUntilDelay));
        } else {
            str = "SyncManager";
            minDelay2 = minDelay;
        }
        if (minDelay2 < 0) {
            minDelay3 = 0;
        } else {
            minDelay3 = minDelay2;
        }
        if (!syncOperation2.isPeriodic) {
            int inheritedSyncExemptionFlag3 = 0;
            Iterator<ActiveSyncContext> it = this.mActiveSyncContexts.iterator();
            while (it.hasNext()) {
                if (it.next().mSyncOperation.key.equals(syncOperation2.key)) {
                    if (isLoggable) {
                        Log.v(str, "Duplicate sync is already running. Not scheduling " + syncOperation2);
                        return;
                    }
                    return;
                }
            }
            int duplicatesCount = 0;
            long now3 = SystemClock.elapsedRealtime();
            syncOperation2.expectedRuntime = now3 + minDelay3;
            List<SyncOperation> pending = getAllPendingSyncs();
            SyncOperation syncToRun = syncOperation;
            for (SyncOperation op : pending) {
                if (!op.isPeriodic) {
                    if (op.key.equals(syncOperation2.key)) {
                        now = now3;
                        inheritedSyncExemptionFlag2 = inheritedSyncExemptionFlag3;
                        if (syncToRun.expectedRuntime > op.expectedRuntime) {
                            syncToRun = op;
                        }
                        duplicatesCount++;
                    } else {
                        inheritedSyncExemptionFlag2 = inheritedSyncExemptionFlag3;
                        now = now3;
                    }
                    inheritedSyncExemptionFlag3 = inheritedSyncExemptionFlag2;
                    now3 = now;
                }
            }
            int inheritedSyncExemptionFlag4 = inheritedSyncExemptionFlag3;
            long j2 = now3;
            if (duplicatesCount > 1) {
                Slog.e(str, "FATAL ERROR! File a bug if you see this.");
            }
            if (syncOperation2 == syncToRun || minDelay3 != 0 || syncToRun.syncExemptionFlag >= syncOperation2.syncExemptionFlag) {
                inheritedSyncExemptionFlag = inheritedSyncExemptionFlag4;
            } else {
                syncToRun = syncOperation;
                inheritedSyncExemptionFlag = Math.max(inheritedSyncExemptionFlag4, syncToRun.syncExemptionFlag);
            }
            for (SyncOperation op2 : pending) {
                if (!op2.isPeriodic) {
                    if (op2.key.equals(syncOperation2.key)) {
                        if (op2 != syncToRun) {
                            if (isLoggable) {
                                Slog.v(str, "Cancelling duplicate sync " + op2);
                            }
                            inheritedSyncExemptionFlag = Math.max(inheritedSyncExemptionFlag, op2.syncExemptionFlag);
                            cancelJob(op2, "scheduleSyncOperationH-duplicate");
                        }
                    }
                }
            }
            syncManager = this;
            if (syncToRun != syncOperation2) {
                if (isLoggable) {
                    Slog.v(str, "Not scheduling because a duplicate exists.");
                    return;
                }
                return;
            } else if (inheritedSyncExemptionFlag > 0) {
                syncOperation2.syncExemptionFlag = inheritedSyncExemptionFlag;
            }
        } else {
            syncManager = this;
        }
        if (syncOperation2.jobId == -1) {
            syncOperation2.jobId = getUnusedJobIdH();
        }
        if (isLoggable) {
            Slog.v(str, "scheduling sync operation " + syncOperation.toString());
        }
        JobInfo.Builder b = new JobInfo.Builder(syncOperation2.jobId, new ComponentName(syncManager.mContext, SyncJobService.class)).setExtras(syncOperation.toJobInfoExtras()).setRequiredNetworkType(syncOperation.isNotAllowedOnMetered() ? 2 : 1).setPersisted(true).setPriority(syncOperation.findPriority()).setFlags(syncOperation.isAppStandbyExempted() ? 8 : 0);
        if (syncOperation2.isPeriodic) {
            b.setPeriodic(syncOperation2.periodMillis, syncOperation2.flexMillis);
            z = true;
        } else {
            if (minDelay3 > 0) {
                b.setMinimumLatency(minDelay3);
            }
            z = true;
            getSyncStorageEngine().markPending(syncOperation2.target, true);
        }
        if (syncOperation2.extras.getBoolean("require_charging")) {
            b.setRequiresCharging(z);
        }
        if (syncOperation2.syncExemptionFlag == 2 && (dic = (DeviceIdleController.LocalService) LocalServices.getService(DeviceIdleController.LocalService.class)) != null) {
            dic.addPowerSaveTempWhitelistApp(1000, syncOperation2.owningPackage, (long) (syncManager.mConstants.getKeyExemptionTempWhitelistDurationInSeconds() * 1000), UserHandle.getUserId(syncOperation2.owningUid), false, "sync by top app");
        }
        UsageStatsManagerInternal usmi = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
        if (usmi != null) {
            usmi.reportSyncScheduled(syncOperation2.owningPackage, UserHandle.getUserId(syncOperation2.owningUid), syncOperation.isAppStandbyExempted());
        }
        SyncManagerInjector.wrapSyncJobInfo(syncManager.mContext, syncOperation, syncManager.mSyncStorageEngine, b, minDelay3);
        getJobScheduler().scheduleAsPackage(b.build(), syncOperation2.owningPackage, syncOperation2.target.userId, syncOperation.wakeLockName());
    }

    public void clearScheduledSyncOperations(SyncStorageEngine.EndPoint info) {
        for (SyncOperation op : getAllPendingSyncs()) {
            if (!op.isPeriodic && op.target.matchesSpec(info)) {
                cancelJob(op, "clearScheduledSyncOperations");
                getSyncStorageEngine().markPending(op.target, false);
            }
        }
        this.mSyncStorageEngine.setBackoff(info, -1, -1);
    }

    public void cancelScheduledSyncOperation(SyncStorageEngine.EndPoint info, Bundle extras) {
        for (SyncOperation op : getAllPendingSyncs()) {
            if (!op.isPeriodic && op.target.matchesSpec(info) && syncExtrasEquals(extras, op.extras, false)) {
                cancelJob(op, "cancelScheduledSyncOperation");
            }
        }
        setAuthorityPendingState(info);
        if (!this.mSyncStorageEngine.isSyncPending(info)) {
            this.mSyncStorageEngine.setBackoff(info, -1, -1);
        }
    }

    /* access modifiers changed from: private */
    public void maybeRescheduleSync(SyncResult syncResult, SyncOperation operation) {
        boolean isLoggable = Log.isLoggable("SyncManager", 3);
        if (isLoggable) {
            Log.d("SyncManager", "encountered error(s) during the sync: " + syncResult + ", " + operation);
        }
        if (operation.extras.getBoolean("ignore_backoff", false)) {
            operation.extras.remove("ignore_backoff");
        }
        if (!operation.extras.getBoolean("do_not_retry", false) || syncResult.syncAlreadyInProgress) {
            if (operation.extras.getBoolean("upload", false) && !syncResult.syncAlreadyInProgress) {
                operation.extras.remove("upload");
                if (isLoggable) {
                    Log.d("SyncManager", "retrying sync operation as a two-way sync because an upload-only sync encountered an error: " + operation);
                }
                scheduleSyncOperationH(operation);
            } else if (syncResult.tooManyRetries) {
                if (isLoggable) {
                    Log.d("SyncManager", "not retrying sync operation because it retried too many times: " + operation);
                }
            } else if (syncResult.madeSomeProgress()) {
                if (isLoggable) {
                    Log.d("SyncManager", "retrying sync operation because even though it had an error it achieved some success");
                }
                scheduleSyncOperationH(operation);
            } else if (syncResult.syncAlreadyInProgress) {
                if (isLoggable) {
                    Log.d("SyncManager", "retrying sync operation that failed because there was already a sync in progress: " + operation);
                }
                scheduleSyncOperationH(operation, 10000);
            } else if (syncResult.hasSoftError()) {
                if (isLoggable) {
                    Log.d("SyncManager", "retrying sync operation because it encountered a soft error: " + operation);
                }
                scheduleSyncOperationH(operation);
            } else {
                Log.e("SyncManager", "not retrying sync operation because the error is a hard error: " + SyncLogger.logSafe(operation));
            }
        } else if (isLoggable) {
            Log.d("SyncManager", "not retrying sync operation because SYNC_EXTRAS_DO_NOT_RETRY was specified " + operation);
        }
    }

    /* access modifiers changed from: private */
    public void onUserUnlocked(int userId) {
        int i = userId;
        try {
            AccountManagerService.getSingleton().validateAccounts(i);
            this.mSyncAdapters.invalidateCache(i);
            updateRunningAccounts(new SyncStorageEngine.EndPoint((Account) null, (String) null, i));
            for (Account account : AccountManagerService.getSingleton().getAccounts(i, this.mContext.getOpPackageName())) {
                scheduleSync(account, userId, -8, (String) null, (Bundle) null, -1, 0, Process.myUid(), -3, (String) null);
            }
        } catch (SQLiteException e) {
            Slog.w("SyncManager", "failed to validate accounts for user " + i, e);
        }
    }

    /* access modifiers changed from: private */
    public void onUserStopped(int userId) {
        updateRunningAccounts((SyncStorageEngine.EndPoint) null);
        cancelActiveSync(new SyncStorageEngine.EndPoint((Account) null, (String) null, userId), (Bundle) null, "onUserStopped");
    }

    /* access modifiers changed from: private */
    public void onUserRemoved(int userId) {
        this.mLogger.log("onUserRemoved: u", Integer.valueOf(userId));
        updateRunningAccounts((SyncStorageEngine.EndPoint) null);
        this.mSyncStorageEngine.removeStaleAccounts((Account[]) null, userId);
        for (SyncOperation op : getAllPendingSyncs()) {
            if (op.target.userId == userId) {
                cancelJob(op, "user removed u" + userId);
            }
        }
    }

    static Intent getAdapterBindIntent(Context context, ComponentName syncAdapterComponent, int userId) {
        Intent intent = new Intent();
        intent.setAction("android.content.SyncAdapter");
        intent.setComponent(syncAdapterComponent);
        intent.putExtra("android.intent.extra.client_label", 17041210);
        intent.putExtra("android.intent.extra.client_intent", PendingIntent.getActivityAsUser(context, 0, new Intent("android.settings.SYNC_SETTINGS"), 0, (Bundle) null, UserHandle.of(userId)));
        return intent;
    }

    class ActiveSyncContext extends ISyncContext.Stub implements ServiceConnection, IBinder.DeathRecipient {
        boolean mBound;
        long mBytesTransferredAtLastPoll;
        String mEventName;
        final long mHistoryRowId;
        boolean mIsLinkedToDeath = false;
        long mLastPolledTimeElapsed;
        final long mStartTime;
        ISyncAdapter mSyncAdapter;
        final int mSyncAdapterUid;
        SyncInfo mSyncInfo;
        final SyncOperation mSyncOperation;
        final PowerManager.WakeLock mSyncWakeLock;
        long mTimeoutStartTime;

        public ActiveSyncContext(SyncOperation syncOperation, long historyRowId, int syncAdapterUid) {
            this.mSyncAdapterUid = syncAdapterUid;
            this.mSyncOperation = syncOperation;
            this.mHistoryRowId = historyRowId;
            this.mSyncAdapter = null;
            this.mStartTime = SystemClock.elapsedRealtime();
            this.mTimeoutStartTime = this.mStartTime;
            this.mSyncWakeLock = SyncManager.this.mSyncHandler.getSyncWakeLock(this.mSyncOperation);
            this.mSyncWakeLock.setWorkSource(new WorkSource(syncAdapterUid, syncOperation.owningPackage));
            this.mSyncWakeLock.acquire();
        }

        public void sendHeartbeat() {
        }

        public void onFinished(SyncResult result) {
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "onFinished: " + this);
            }
            SyncLogger access$700 = SyncManager.this.mLogger;
            Object[] objArr = new Object[4];
            objArr[0] = "onFinished result=";
            objArr[1] = result;
            objArr[2] = " endpoint=";
            SyncOperation syncOperation = this.mSyncOperation;
            objArr[3] = syncOperation == null ? "null" : syncOperation.target;
            access$700.log(objArr);
            SyncManager.this.sendSyncFinishedOrCanceledMessage(this, result);
        }

        public void toString(StringBuilder sb, boolean logSafe) {
            sb.append("startTime ");
            sb.append(this.mStartTime);
            sb.append(", mTimeoutStartTime ");
            sb.append(this.mTimeoutStartTime);
            sb.append(", mHistoryRowId ");
            sb.append(this.mHistoryRowId);
            sb.append(", syncOperation ");
            SyncOperation syncOperation = this.mSyncOperation;
            Object obj = syncOperation;
            if (logSafe) {
                obj = SyncLogger.logSafe(syncOperation);
            }
            sb.append(obj);
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Message msg = SyncManager.this.mSyncHandler.obtainMessage();
            msg.what = 4;
            msg.obj = new ServiceConnectionData(this, service);
            SyncManager.this.mSyncHandler.sendMessage(msg);
        }

        public void onServiceDisconnected(ComponentName name) {
            Message msg = SyncManager.this.mSyncHandler.obtainMessage();
            msg.what = 5;
            msg.obj = new ServiceConnectionData(this, (IBinder) null);
            SyncManager.this.mSyncHandler.sendMessage(msg);
        }

        /* access modifiers changed from: package-private */
        public boolean bindToSyncAdapter(ComponentName serviceComponent, int userId) {
            if (Log.isLoggable("SyncManager", 2)) {
                Log.d("SyncManager", "bindToSyncAdapter: " + serviceComponent + ", connection " + this);
            }
            Intent intent = SyncManager.getAdapterBindIntent(SyncManager.this.mContext, serviceComponent, userId);
            this.mBound = true;
            if (!SyncManagerInjector.canBindService(SyncManager.this.mContext, intent, this.mSyncOperation.target.userId)) {
                this.mBound = false;
                return false;
            }
            boolean bindResult = SyncManager.this.mContext.bindServiceAsUser(intent, this, 21, new UserHandle(this.mSyncOperation.target.userId));
            SyncManager.this.mLogger.log("bindService() returned=", Boolean.valueOf(this.mBound), " for ", this);
            if (!bindResult) {
                this.mBound = false;
            } else {
                try {
                    this.mEventName = this.mSyncOperation.wakeLockName();
                    SyncManager.this.mBatteryStats.noteSyncStart(this.mEventName, this.mSyncAdapterUid);
                } catch (RemoteException e) {
                }
            }
            return bindResult;
        }

        /* access modifiers changed from: protected */
        public void close() {
            if (Log.isLoggable("SyncManager", 2)) {
                Log.d("SyncManager", "unBindFromSyncAdapter: connection " + this);
            }
            if (this.mBound) {
                this.mBound = false;
                SyncManager.this.mLogger.log("unbindService for ", this);
                SyncManager.this.mContext.unbindService(this);
                try {
                    SyncManager.this.mBatteryStats.noteSyncFinish(this.mEventName, this.mSyncAdapterUid);
                } catch (RemoteException e) {
                }
            }
            this.mSyncWakeLock.release();
            this.mSyncWakeLock.setWorkSource((WorkSource) null);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            toString(sb, false);
            return sb.toString();
        }

        public String toSafeString() {
            StringBuilder sb = new StringBuilder();
            toString(sb, true);
            return sb.toString();
        }

        public void binderDied() {
            SyncManager.this.sendSyncFinishedOrCanceledMessage(this, (SyncResult) null);
        }
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, boolean dumpAll) {
        IndentingPrintWriter ipw = new IndentingPrintWriter(pw, "  ");
        dumpSyncState(ipw, new SyncAdapterStateFetcher());
        this.mConstants.dump(pw, "");
        dumpSyncAdapters(ipw);
        if (dumpAll) {
            ipw.println("Detailed Sync History");
            this.mLogger.dumpAll(pw);
        }
    }

    static String formatTime(long time) {
        if (time == 0) {
            return "N/A";
        }
        Time tobj = new Time();
        tobj.set(time);
        return tobj.format("%Y-%m-%d %H:%M:%S");
    }

    static /* synthetic */ int lambda$static$6(SyncOperation op1, SyncOperation op2) {
        int res = Integer.compare(op1.target.userId, op2.target.userId);
        if (res != 0) {
            return res;
        }
        Comparator<String> stringComparator = String.CASE_INSENSITIVE_ORDER;
        int res2 = stringComparator.compare(op1.target.account.type, op2.target.account.type);
        if (res2 != 0) {
            return res2;
        }
        int res3 = stringComparator.compare(op1.target.account.name, op2.target.account.name);
        if (res3 != 0) {
            return res3;
        }
        int res4 = stringComparator.compare(op1.target.provider, op2.target.provider);
        if (res4 != 0) {
            return res4;
        }
        int res5 = Integer.compare(op1.reason, op2.reason);
        if (res5 != 0) {
            return res5;
        }
        int res6 = Long.compare(op1.periodMillis, op2.periodMillis);
        if (res6 != 0) {
            return res6;
        }
        int res7 = Long.compare(op1.expectedRuntime, op2.expectedRuntime);
        if (res7 != 0) {
            return res7;
        }
        int res8 = Long.compare((long) op1.jobId, (long) op2.jobId);
        if (res8 != 0) {
            return res8;
        }
        return 0;
    }

    static /* synthetic */ int lambda$static$7(SyncOperation op1, SyncOperation op2) {
        int res = Long.compare(op1.expectedRuntime, op2.expectedRuntime);
        if (res != 0) {
            return res;
        }
        return sOpDumpComparator.compare(op1, op2);
    }

    private static <T> int countIf(Collection<T> col, Predicate<T> p) {
        int ret = 0;
        for (T item : col) {
            if (p.test(item)) {
                ret++;
            }
        }
        return ret;
    }

    /* access modifiers changed from: protected */
    public void dumpPendingSyncs(PrintWriter pw, SyncAdapterStateFetcher buckets) {
        List<SyncOperation> pendingSyncs = getAllPendingSyncs();
        pw.print("Pending Syncs: ");
        pw.println(countIf(pendingSyncs, $$Lambda$SyncManager$rDUHWai3SU0BXk1TE0bLDap9gVc.INSTANCE));
        Collections.sort(pendingSyncs, sOpRuntimeComparator);
        int count = 0;
        for (SyncOperation op : pendingSyncs) {
            if (!op.isPeriodic) {
                pw.println(op.dump((PackageManager) null, false, buckets, false));
                count++;
            }
        }
        pw.println();
    }

    static /* synthetic */ boolean lambda$dumpPendingSyncs$8(SyncOperation op) {
        return !op.isPeriodic;
    }

    /* access modifiers changed from: protected */
    public void dumpPeriodicSyncs(PrintWriter pw, SyncAdapterStateFetcher buckets) {
        List<SyncOperation> pendingSyncs = getAllPendingSyncs();
        pw.print("Periodic Syncs: ");
        pw.println(countIf(pendingSyncs, $$Lambda$SyncManager$ag0YGuZ1oL06fytmNlyErbNyYcw.INSTANCE));
        Collections.sort(pendingSyncs, sOpDumpComparator);
        int count = 0;
        for (SyncOperation op : pendingSyncs) {
            if (op.isPeriodic) {
                pw.println(op.dump((PackageManager) null, false, buckets, false));
                count++;
            }
        }
        pw.println();
    }

    public static StringBuilder formatDurationHMS(StringBuilder sb, long duration) {
        long duration2 = duration / 1000;
        if (duration2 < 0) {
            sb.append('-');
            duration2 = -duration2;
        }
        long seconds = duration2 % 60;
        long duration3 = duration2 / 60;
        long minutes = duration3 % 60;
        long duration4 = duration3 / 60;
        long hours = duration4 % 24;
        long days = duration4 / 24;
        boolean print = false;
        if (days > 0) {
            sb.append(days);
            sb.append('d');
            print = true;
        }
        if (!printTwoDigitNumber(sb, seconds, 's', printTwoDigitNumber(sb, minutes, 'm', printTwoDigitNumber(sb, hours, 'h', print)))) {
            sb.append("0s");
        }
        return sb;
    }

    private static boolean printTwoDigitNumber(StringBuilder sb, long value, char unit, boolean always) {
        if (!always && value == 0) {
            return false;
        }
        if (always && value < 10) {
            sb.append('0');
        }
        sb.append(value);
        sb.append(unit);
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x04d4, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dumpSyncState(java.io.PrintWriter r39, com.android.server.content.SyncAdapterStateFetcher r40) {
        /*
            r38 = this;
            r1 = r38
            r2 = r39
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r3 = r0
            java.lang.String r0 = "Data connected: "
            r2.print(r0)
            boolean r0 = r1.mDataConnectionIsConnected
            r2.println(r0)
            java.lang.String r0 = "Battery saver: "
            r2.print(r0)
            android.os.PowerManager r0 = r1.mPowerManager
            r4 = 0
            r5 = 1
            if (r0 == 0) goto L_0x0027
            boolean r0 = r0.isPowerSaveMode()
            if (r0 == 0) goto L_0x0027
            r0 = r5
            goto L_0x0028
        L_0x0027:
            r0 = r4
        L_0x0028:
            r2.println(r0)
            java.lang.String r0 = "Background network restriction: "
            r2.print(r0)
            android.net.ConnectivityManager r0 = r38.getConnectivityManager()
            if (r0 != 0) goto L_0x0038
            r6 = -1
            goto L_0x003c
        L_0x0038:
            int r6 = r0.getRestrictBackgroundStatus()
        L_0x003c:
            r7 = 3
            r8 = 2
            if (r6 == r5) goto L_0x005e
            if (r6 == r8) goto L_0x0058
            if (r6 == r7) goto L_0x0052
            java.lang.String r9 = "Unknown("
            r2.print(r9)
            r2.print(r6)
            java.lang.String r9 = ")"
            r2.println(r9)
            goto L_0x0064
        L_0x0052:
            java.lang.String r9 = " enabled"
            r2.println(r9)
            goto L_0x0064
        L_0x0058:
            java.lang.String r9 = " whitelisted"
            r2.println(r9)
            goto L_0x0064
        L_0x005e:
            java.lang.String r9 = " disabled"
            r2.println(r9)
        L_0x0064:
            java.lang.String r0 = "Auto sync: "
            r2.print(r0)
            java.util.List r6 = r38.getAllUsers()
            if (r6 == 0) goto L_0x00af
            java.util.Iterator r0 = r6.iterator()
        L_0x0073:
            boolean r9 = r0.hasNext()
            if (r9 == 0) goto L_0x00ac
            java.lang.Object r9 = r0.next()
            android.content.pm.UserInfo r9 = (android.content.pm.UserInfo) r9
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "u"
            r10.append(r11)
            int r11 = r9.id
            r10.append(r11)
            java.lang.String r11 = "="
            r10.append(r11)
            com.android.server.content.SyncStorageEngine r11 = r1.mSyncStorageEngine
            int r12 = r9.id
            boolean r11 = r11.getMasterSyncAutomatically(r12)
            r10.append(r11)
            java.lang.String r11 = " "
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r2.print(r10)
            goto L_0x0073
        L_0x00ac:
            r39.println()
        L_0x00af:
            java.lang.String r0 = "Memory low: "
            r2.print(r0)
            boolean r0 = r1.mStorageIsLow
            r2.println(r0)
            java.lang.String r0 = "Device idle: "
            r2.print(r0)
            boolean r0 = r1.mDeviceIsIdle
            r2.println(r0)
            java.lang.String r0 = "Reported active: "
            r2.print(r0)
            boolean r0 = r1.mReportedSyncActive
            r2.println(r0)
            java.lang.String r0 = "Clock valid: "
            r2.print(r0)
            com.android.server.content.SyncStorageEngine r0 = r1.mSyncStorageEngine
            boolean r0 = r0.isClockValid()
            r2.println(r0)
            com.android.server.accounts.AccountManagerService r0 = com.android.server.accounts.AccountManagerService.getSingleton()
            android.accounts.AccountAndUser[] r9 = r0.getAllAccounts()
            java.lang.String r0 = "Accounts: "
            r2.print(r0)
            android.accounts.AccountAndUser[] r0 = INITIAL_ACCOUNTS_ARRAY
            if (r9 == r0) goto L_0x00f1
            int r0 = r9.length
            r2.println(r0)
            goto L_0x00f7
        L_0x00f1:
            java.lang.String r0 = "not known yet"
            r2.println(r0)
        L_0x00f7:
            long r10 = android.os.SystemClock.elapsedRealtime()
            java.lang.String r0 = "Now: "
            r2.print(r0)
            r2.print(r10)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r12 = " ("
            r0.append(r12)
            long r12 = java.lang.System.currentTimeMillis()
            java.lang.String r12 = formatTime(r12)
            r0.append(r12)
            java.lang.String r12 = ")"
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            r2.println(r0)
            r3.setLength(r4)
            java.lang.String r0 = "Uptime: "
            r2.print(r0)
            java.lang.StringBuilder r0 = formatDurationHMS(r3, r10)
            r2.print(r0)
            r39.println()
            java.lang.String r0 = "Time spent syncing: "
            r2.print(r0)
            r3.setLength(r4)
            com.android.server.content.SyncManager$SyncHandler r0 = r1.mSyncHandler
            com.android.server.content.SyncManager$SyncTimeTracker r0 = r0.mSyncTimeTracker
            long r12 = r0.timeSpentSyncing()
            java.lang.StringBuilder r0 = formatDurationHMS(r3, r12)
            r2.print(r0)
            java.lang.String r0 = ", sync "
            r2.print(r0)
            com.android.server.content.SyncManager$SyncHandler r0 = r1.mSyncHandler
            com.android.server.content.SyncManager$SyncTimeTracker r0 = r0.mSyncTimeTracker
            boolean r0 = r0.mLastWasSyncing
            if (r0 == 0) goto L_0x015d
            java.lang.String r0 = ""
            goto L_0x0160
        L_0x015d:
            java.lang.String r0 = "not "
        L_0x0160:
            r2.print(r0)
            java.lang.String r0 = "in progress"
            r2.println(r0)
            r39.println()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r12 = "Active Syncs: "
            r0.append(r12)
            java.util.concurrent.CopyOnWriteArrayList<com.android.server.content.SyncManager$ActiveSyncContext> r12 = r1.mActiveSyncContexts
            int r12 = r12.size()
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            r2.println(r0)
            android.content.Context r0 = r1.mContext
            android.content.pm.PackageManager r12 = r0.getPackageManager()
            java.util.concurrent.CopyOnWriteArrayList<com.android.server.content.SyncManager$ActiveSyncContext> r0 = r1.mActiveSyncContexts
            java.util.Iterator r0 = r0.iterator()
        L_0x0192:
            boolean r13 = r0.hasNext()
            if (r13 == 0) goto L_0x01c7
            java.lang.Object r13 = r0.next()
            com.android.server.content.SyncManager$ActiveSyncContext r13 = (com.android.server.content.SyncManager.ActiveSyncContext) r13
            long r14 = r13.mStartTime
            long r14 = r10 - r14
            java.lang.String r7 = "  "
            r2.print(r7)
            r3.setLength(r4)
            java.lang.StringBuilder r7 = formatDurationHMS(r3, r14)
            r2.print(r7)
            java.lang.String r7 = " - "
            r2.print(r7)
            com.android.server.content.SyncOperation r7 = r13.mSyncOperation
            r8 = r40
            java.lang.String r7 = r7.dump(r12, r4, r8, r4)
            r2.print(r7)
            r39.println()
            r7 = 3
            r8 = 2
            goto L_0x0192
        L_0x01c7:
            r8 = r40
            r39.println()
            r38.dumpPendingSyncs(r39, r40)
            r38.dumpPeriodicSyncs(r39, r40)
            java.lang.String r0 = "Sync Status"
            r2.println(r0)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7 = r0
            com.android.server.content.SyncStorageEngine r0 = r1.mSyncStorageEngine
            r0.resetTodayStats(r4)
            int r0 = r9.length
            r13 = r4
        L_0x01e4:
            if (r13 >= r0) goto L_0x04d6
            r14 = r9[r13]
            android.util.SparseBooleanArray r15 = r1.mUnlockedUsers
            monitor-enter(r15)
            android.util.SparseBooleanArray r5 = r1.mUnlockedUsers     // Catch:{ all -> 0x04c5 }
            int r4 = r14.userId     // Catch:{ all -> 0x04c5 }
            boolean r4 = r5.get(r4)     // Catch:{ all -> 0x04c5 }
            monitor-exit(r15)     // Catch:{ all -> 0x04c5 }
            r5 = 4
            java.lang.Object[] r15 = new java.lang.Object[r5]
            android.accounts.Account r5 = r14.account
            java.lang.String r5 = r5.name
            r19 = 0
            r15[r19] = r5
            int r5 = r14.userId
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r18 = 1
            r15[r18] = r5
            android.accounts.Account r5 = r14.account
            java.lang.String r5 = r5.type
            r17 = 2
            r15[r17] = r5
            if (r4 == 0) goto L_0x0216
            java.lang.String r5 = ""
            goto L_0x0218
        L_0x0216:
            java.lang.String r5 = " (locked)"
        L_0x0218:
            r16 = 3
            r15[r16] = r5
            java.lang.String r5 = "Account %s u%d %s%s\n"
            r2.printf(r5, r15)
            java.lang.String r5 = "======================================================================="
            r2.println(r5)
            com.android.server.content.SyncManager$PrintTable r5 = new com.android.server.content.SyncManager$PrintTable
            r15 = 16
            r5.<init>(r15)
            java.lang.Object[] r15 = new java.lang.Object[r15]
            java.lang.String r21 = "Authority"
            r19 = 0
            r15[r19] = r21
            java.lang.String r21 = "Syncable"
            r18 = 1
            r15[r18] = r21
            java.lang.String r21 = "Enabled"
            r17 = 2
            r15[r17] = r21
            java.lang.String r21 = "Stats"
            r16 = 3
            r15[r16] = r21
            java.lang.String r21 = "Loc"
            r20 = 4
            r15[r20] = r21
            r20 = 5
            java.lang.String r21 = "Poll"
            r15[r20] = r21
            r20 = 6
            java.lang.String r21 = "Per"
            r15[r20] = r21
            r20 = 7
            java.lang.String r21 = "Feed"
            r15[r20] = r21
            r20 = 8
            java.lang.String r21 = "User"
            r15[r20] = r21
            r20 = 9
            java.lang.String r21 = "Othr"
            r15[r20] = r21
            r20 = 10
            java.lang.String r21 = "Tot"
            r15[r20] = r21
            r20 = 11
            java.lang.String r21 = "Fail"
            r15[r20] = r21
            r20 = 12
            java.lang.String r21 = "Can"
            r15[r20] = r21
            r20 = 13
            java.lang.String r21 = "Time"
            r15[r20] = r21
            r20 = r0
            r0 = 14
            java.lang.String r21 = "Last Sync"
            r15[r0] = r21
            r0 = 15
            java.lang.String r22 = "Backoff"
            r15[r0] = r22
            r0 = 0
            r5.set(r0, r0, r15)
            java.util.ArrayList r0 = com.google.android.collect.Lists.newArrayList()
            android.content.SyncAdaptersCache r15 = r1.mSyncAdapters
            r23 = r4
            int r4 = r14.userId
            java.util.Collection r4 = r15.getAllServices(r4)
            r0.addAll(r4)
            com.android.server.content.SyncManager$13 r4 = new com.android.server.content.SyncManager$13
            r4.<init>()
            java.util.Collections.sort(r0, r4)
            java.util.Iterator r4 = r0.iterator()
        L_0x02b2:
            boolean r15 = r4.hasNext()
            if (r15 == 0) goto L_0x04a5
            java.lang.Object r15 = r4.next()
            android.content.pm.RegisteredServicesCache$ServiceInfo r15 = (android.content.pm.RegisteredServicesCache.ServiceInfo) r15
            r24 = r0
            java.lang.Object r0 = r15.type
            android.content.SyncAdapterType r0 = (android.content.SyncAdapterType) r0
            java.lang.String r0 = r0.accountType
            r25 = r4
            android.accounts.Account r4 = r14.account
            java.lang.String r4 = r4.type
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x02d7
            r0 = r24
            r4 = r25
            goto L_0x02b2
        L_0x02d7:
            int r0 = r5.getNumRows()
            com.android.server.content.SyncStorageEngine r4 = r1.mSyncStorageEngine
            r26 = r6
            com.android.server.content.SyncStorageEngine$EndPoint r6 = new com.android.server.content.SyncStorageEngine$EndPoint
            android.accounts.Account r8 = r14.account
            r27 = r9
            java.lang.Object r9 = r15.type
            android.content.SyncAdapterType r9 = (android.content.SyncAdapterType) r9
            java.lang.String r9 = r9.authority
            r28 = r12
            int r12 = r14.userId
            r6.<init>(r8, r9, r12)
            android.util.Pair r4 = r4.getCopyOfAuthorityWithSyncStatus(r6)
            java.lang.Object r6 = r4.first
            com.android.server.content.SyncStorageEngine$AuthorityInfo r6 = (com.android.server.content.SyncStorageEngine.AuthorityInfo) r6
            java.lang.Object r8 = r4.second
            android.content.SyncStatusInfo r8 = (android.content.SyncStatusInfo) r8
            com.android.server.content.SyncStorageEngine$EndPoint r9 = r6.target
            android.util.Pair r9 = android.util.Pair.create(r9, r8)
            r7.add(r9)
            com.android.server.content.SyncStorageEngine$EndPoint r9 = r6.target
            java.lang.String r9 = r9.provider
            int r12 = r9.length()
            r29 = r4
            r4 = 50
            if (r12 <= r4) goto L_0x031e
            int r12 = r9.length()
            int r12 = r12 - r4
            java.lang.String r9 = r9.substring(r12)
        L_0x031e:
            r4 = 3
            java.lang.Object[] r12 = new java.lang.Object[r4]
            r4 = 0
            r12[r4] = r9
            int r4 = r6.syncable
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r18 = 1
            r12[r18] = r4
            boolean r4 = r6.enabled
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r4)
            r17 = 2
            r12[r17] = r4
            r4 = 0
            r5.set(r0, r4, r12)
            com.android.server.content.-$$Lambda$SyncManager$9EoLpTk5JrHZn9R-uS0lqCVrpRw r4 = new com.android.server.content.-$$Lambda$SyncManager$9EoLpTk5JrHZn9R-uS0lqCVrpRw
            r4.<init>(r3, r5)
            android.content.SyncStatusInfo$Stats r12 = r8.totalStats
            r30 = r3
            com.android.server.content.-$$Lambda$SyncManager$pdoEVnuSkmOrvULQ9M7Ic-lU5vw r3 = com.android.server.content.$$Lambda$SyncManager$pdoEVnuSkmOrvULQ9M7IclU5vw.INSTANCE
            r31 = r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)
            r32 = r14
            java.lang.String r14 = "Total"
            r4.accept(r14, r12, r3, r9)
            android.content.SyncStatusInfo$Stats r3 = r8.todayStats
            com.android.server.content.-$$Lambda$SyncManager$EMXCZP9LDjgUTYbLsEoVu9Ccntw r9 = new com.android.server.content.-$$Lambda$SyncManager$EMXCZP9LDjgUTYbLsEoVu9Ccntw
            r9.<init>()
            int r12 = r0 + 1
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r14 = "Today"
            r4.accept(r14, r3, r9, r12)
            android.content.SyncStatusInfo$Stats r3 = r8.yesterdayStats
            com.android.server.content.-$$Lambda$SyncManager$EMXCZP9LDjgUTYbLsEoVu9Ccntw r9 = new com.android.server.content.-$$Lambda$SyncManager$EMXCZP9LDjgUTYbLsEoVu9Ccntw
            r9.<init>()
            int r12 = r0 + 2
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r14 = "Yestr"
            r4.accept(r14, r3, r9, r12)
            r3 = 14
            r9 = 15
            r12 = r0
            r33 = r3
            r14 = r4
            long r3 = r6.delayUntil
            int r3 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r3 <= 0) goto L_0x03f7
            int r3 = r12 + 1
            r4 = 1
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r34 = r9
            java.lang.String r9 = "D: "
            r4.append(r9)
            r35 = r14
            r9 = r15
            long r14 = r6.delayUntil
            long r14 = r14 - r10
            r36 = 1000(0x3e8, double:4.94E-321)
            long r14 = r14 / r36
            r4.append(r14)
            java.lang.String r4 = r4.toString()
            r14 = 0
            r1[r14] = r4
            r4 = 15
            r5.set(r12, r4, r1)
            long r14 = r6.backoffTime
            int r1 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1))
            if (r1 <= 0) goto L_0x03f2
            int r1 = r3 + 1
            r4 = 1
            java.lang.Object[] r12 = new java.lang.Object[r4]
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r14 = "B: "
            r4.append(r14)
            long r14 = r6.backoffTime
            long r14 = r14 - r10
            long r14 = r14 / r36
            r4.append(r14)
            java.lang.String r4 = r4.toString()
            r14 = 0
            r12[r14] = r4
            r4 = 15
            r5.set(r3, r4, r12)
            int r12 = r1 + 1
            r3 = 1
            java.lang.Object[] r15 = new java.lang.Object[r3]
            r22 = r5
            long r4 = r6.backoffDelay
            long r4 = r4 / r36
            java.lang.Long r3 = java.lang.Long.valueOf(r4)
            r15[r14] = r3
            r4 = r22
            r5 = 15
            r4.set(r1, r5, r15)
            goto L_0x03ff
        L_0x03f2:
            r4 = r5
            r5 = 15
            r12 = r3
            goto L_0x03ff
        L_0x03f7:
            r4 = r5
            r34 = r9
            r35 = r14
            r9 = r15
            r5 = 15
        L_0x03ff:
            r1 = r0
            long r14 = r8.lastSuccessTime
            r36 = 0
            int r3 = (r14 > r36 ? 1 : (r14 == r36 ? 0 : -1))
            if (r3 == 0) goto L_0x0440
            int r3 = r1 + 1
            r12 = 1
            java.lang.Object[] r14 = new java.lang.Object[r12]
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String[] r15 = com.android.server.content.SyncStorageEngine.SOURCES
            int r5 = r8.lastSuccessSource
            r5 = r15[r5]
            r12.append(r5)
            java.lang.String r5 = " SUCCESS"
            r12.append(r5)
            java.lang.String r5 = r12.toString()
            r12 = 0
            r14[r12] = r5
            r5 = 14
            r4.set(r1, r5, r14)
            int r1 = r3 + 1
            r14 = 1
            java.lang.Object[] r15 = new java.lang.Object[r14]
            r14 = r6
            long r5 = r8.lastSuccessTime
            java.lang.String r5 = formatTime(r5)
            r15[r12] = r5
            r5 = 14
            r4.set(r3, r5, r15)
            goto L_0x0441
        L_0x0440:
            r14 = r6
        L_0x0441:
            long r5 = r8.lastFailureTime
            int r3 = (r5 > r36 ? 1 : (r5 == r36 ? 0 : -1))
            if (r3 == 0) goto L_0x048a
            int r3 = r1 + 1
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String[] r12 = com.android.server.content.SyncStorageEngine.SOURCES
            int r15 = r8.lastFailureSource
            r12 = r12[r15]
            r5.append(r12)
            java.lang.String r12 = " FAILURE"
            r5.append(r12)
            java.lang.String r5 = r5.toString()
            r12 = 0
            r6[r12] = r5
            r5 = 14
            r4.set(r1, r5, r6)
            int r1 = r3 + 1
            r6 = 1
            java.lang.Object[] r15 = new java.lang.Object[r6]
            r21 = r7
            long r6 = r8.lastFailureTime
            java.lang.String r6 = formatTime(r6)
            r15[r12] = r6
            r4.set(r3, r5, r15)
            int r3 = r1 + 1
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]
            java.lang.String r6 = r8.lastFailureMesg
            r7[r12] = r6
            r4.set(r1, r5, r7)
            goto L_0x048e
        L_0x048a:
            r21 = r7
            r5 = 14
        L_0x048e:
            r1 = r38
            r8 = r40
            r5 = r4
            r7 = r21
            r0 = r24
            r4 = r25
            r6 = r26
            r9 = r27
            r12 = r28
            r3 = r30
            r14 = r32
            goto L_0x02b2
        L_0x04a5:
            r24 = r0
            r30 = r3
            r4 = r5
            r26 = r6
            r21 = r7
            r27 = r9
            r28 = r12
            r32 = r14
            r17 = 2
            r4.writeTo(r2)
            int r13 = r13 + 1
            r1 = r38
            r8 = r40
            r0 = r20
            r4 = 0
            r5 = 1
            goto L_0x01e4
        L_0x04c5:
            r0 = move-exception
            r30 = r3
            r26 = r6
            r21 = r7
            r27 = r9
            r28 = r12
            r32 = r14
        L_0x04d2:
            monitor-exit(r15)     // Catch:{ all -> 0x04d4 }
            throw r0
        L_0x04d4:
            r0 = move-exception
            goto L_0x04d2
        L_0x04d6:
            r30 = r3
            r26 = r6
            r21 = r7
            r27 = r9
            r28 = r12
            r38.dumpSyncHistory(r39)
            r39.println()
            java.lang.String r0 = "Per Adapter History"
            r2.println(r0)
            java.lang.String r0 = "(SERVER is now split up to FEED and OTHER)"
            r2.println(r0)
            r0 = 0
        L_0x04f1:
            int r1 = r21.size()
            if (r0 >= r1) goto L_0x05e2
            r1 = r21
            java.lang.Object r3 = r1.get(r0)
            android.util.Pair r3 = (android.util.Pair) r3
            java.lang.String r4 = "  "
            r2.print(r4)
            java.lang.Object r4 = r3.first
            com.android.server.content.SyncStorageEngine$EndPoint r4 = (com.android.server.content.SyncStorageEngine.EndPoint) r4
            android.accounts.Account r4 = r4.account
            java.lang.String r4 = r4.name
            r2.print(r4)
            r4 = 47
            r2.print(r4)
            java.lang.Object r4 = r3.first
            com.android.server.content.SyncStorageEngine$EndPoint r4 = (com.android.server.content.SyncStorageEngine.EndPoint) r4
            android.accounts.Account r4 = r4.account
            java.lang.String r4 = r4.type
            r2.print(r4)
            java.lang.String r4 = " u"
            r2.print(r4)
            java.lang.Object r4 = r3.first
            com.android.server.content.SyncStorageEngine$EndPoint r4 = (com.android.server.content.SyncStorageEngine.EndPoint) r4
            int r4 = r4.userId
            r2.print(r4)
            java.lang.String r4 = " ["
            r2.print(r4)
            java.lang.Object r4 = r3.first
            com.android.server.content.SyncStorageEngine$EndPoint r4 = (com.android.server.content.SyncStorageEngine.EndPoint) r4
            java.lang.String r4 = r4.provider
            r2.print(r4)
            java.lang.String r4 = "]"
            r2.print(r4)
            r39.println()
            java.lang.String r4 = "    Per source last syncs:"
            r2.println(r4)
            r4 = 0
        L_0x0549:
            java.lang.String[] r5 = com.android.server.content.SyncStorageEngine.SOURCES
            int r5 = r5.length
            if (r4 >= r5) goto L_0x0591
            java.lang.String r5 = "      "
            r2.print(r5)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String[] r7 = com.android.server.content.SyncStorageEngine.SOURCES
            r7 = r7[r4]
            r8 = 0
            r6[r8] = r7
            java.lang.String r7 = "%8s"
            java.lang.String r6 = java.lang.String.format(r7, r6)
            r2.print(r6)
            java.lang.String r6 = "  Success: "
            r2.print(r6)
            java.lang.Object r6 = r3.second
            android.content.SyncStatusInfo r6 = (android.content.SyncStatusInfo) r6
            long[] r6 = r6.perSourceLastSuccessTimes
            r6 = r6[r4]
            java.lang.String r6 = formatTime(r6)
            r2.print(r6)
            java.lang.String r6 = "  Failure: "
            r2.print(r6)
            java.lang.Object r6 = r3.second
            android.content.SyncStatusInfo r6 = (android.content.SyncStatusInfo) r6
            long[] r6 = r6.perSourceLastFailureTimes
            r6 = r6[r4]
            java.lang.String r6 = formatTime(r6)
            r2.println(r6)
            int r4 = r4 + 1
            goto L_0x0549
        L_0x0591:
            r5 = 1
            r8 = 0
            java.lang.String r4 = "    Last syncs:"
            r2.println(r4)
            r4 = 0
        L_0x0599:
            java.lang.Object r6 = r3.second
            android.content.SyncStatusInfo r6 = (android.content.SyncStatusInfo) r6
            int r6 = r6.getEventCount()
            if (r4 >= r6) goto L_0x05cd
            java.lang.String r6 = "      "
            r2.print(r6)
            java.lang.Object r6 = r3.second
            android.content.SyncStatusInfo r6 = (android.content.SyncStatusInfo) r6
            long r6 = r6.getEventTime(r4)
            java.lang.String r6 = formatTime(r6)
            r2.print(r6)
            r6 = 32
            r2.print(r6)
            java.lang.Object r6 = r3.second
            android.content.SyncStatusInfo r6 = (android.content.SyncStatusInfo) r6
            java.lang.String r6 = r6.getEvent(r4)
            r2.print(r6)
            r39.println()
            int r4 = r4 + 1
            goto L_0x0599
        L_0x05cd:
            java.lang.Object r4 = r3.second
            android.content.SyncStatusInfo r4 = (android.content.SyncStatusInfo) r4
            int r4 = r4.getEventCount()
            if (r4 != 0) goto L_0x05dc
            java.lang.String r4 = "      N/A"
            r2.println(r4)
        L_0x05dc:
            int r0 = r0 + 1
            r21 = r1
            goto L_0x04f1
        L_0x05e2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncManager.dumpSyncState(java.io.PrintWriter, com.android.server.content.SyncAdapterStateFetcher):void");
    }

    static /* synthetic */ void lambda$dumpSyncState$10(StringBuilder sb, PrintTable table, String label, SyncStatusInfo.Stats stats, Function filter, Integer r) {
        sb.setLength(0);
        table.set(r.intValue(), 3, label, filter.apply(Integer.valueOf(stats.numSourceLocal)), filter.apply(Integer.valueOf(stats.numSourcePoll)), filter.apply(Integer.valueOf(stats.numSourcePeriodic)), filter.apply(Integer.valueOf(stats.numSourceFeed)), filter.apply(Integer.valueOf(stats.numSourceUser)), filter.apply(Integer.valueOf(stats.numSourceOther)), filter.apply(Integer.valueOf(stats.numSyncs)), filter.apply(Integer.valueOf(stats.numFailures)), filter.apply(Integer.valueOf(stats.numCancels)), formatDurationHMS(sb, stats.totalElapsedTime));
    }

    /* access modifiers changed from: private */
    public String zeroToEmpty(int value) {
        return value != 0 ? Integer.toString(value) : "";
    }

    private void dumpTimeSec(PrintWriter pw, long time) {
        pw.print(time / 1000);
        pw.print('.');
        pw.print((time / 100) % 10);
        pw.print('s');
    }

    private void dumpDayStatistic(PrintWriter pw, SyncStorageEngine.DayStats ds) {
        pw.print("Success (");
        pw.print(ds.successCount);
        if (ds.successCount > 0) {
            pw.print(" for ");
            dumpTimeSec(pw, ds.successTime);
            pw.print(" avg=");
            dumpTimeSec(pw, ds.successTime / ((long) ds.successCount));
        }
        pw.print(") Failure (");
        pw.print(ds.failureCount);
        if (ds.failureCount > 0) {
            pw.print(" for ");
            dumpTimeSec(pw, ds.failureTime);
            pw.print(" avg=");
            dumpTimeSec(pw, ds.failureTime / ((long) ds.failureCount));
        }
        pw.println(")");
    }

    /* access modifiers changed from: protected */
    public void dumpSyncHistory(PrintWriter pw) {
        dumpRecentHistory(pw);
        dumpDayStatistics(pw);
    }

    private void dumpRecentHistory(PrintWriter pw) {
        String str;
        int maxAccount;
        int maxAuthority;
        int N;
        String str2;
        String str3;
        int N2;
        int N3;
        ArrayList<SyncStorageEngine.SyncHistoryItem> items;
        Map<String, Long> lastTimeMap;
        String str4;
        String str5;
        String accountKey;
        String authorityName;
        int maxAccount2;
        int maxAuthority2;
        long totalTimes;
        String authorityName2;
        String str6;
        String diffString;
        String accountKey2;
        int N4;
        String str7;
        String diffString2;
        PackageManager pm;
        Map<String, Long> lastTimeMap2;
        PackageManager pm2;
        ArrayList<SyncStorageEngine.SyncHistoryItem> items2;
        String authorityName3;
        String accountKey3;
        long totalElapsedTime;
        AuthoritySyncStats authoritySyncStats;
        SyncManager syncManager = this;
        PrintWriter printWriter = pw;
        ArrayList<SyncStorageEngine.SyncHistoryItem> items3 = syncManager.mSyncStorageEngine.getSyncHistory();
        if (items3 == null || items3.size() <= 0) {
            return;
        }
        Map<String, AuthoritySyncStats> newHashMap = Maps.newHashMap();
        long totalElapsedTime2 = 0;
        long totalTimes2 = 0;
        int N5 = items3.size();
        int maxAuthority3 = 0;
        int maxAccount3 = 0;
        Iterator<SyncStorageEngine.SyncHistoryItem> it = items3.iterator();
        while (true) {
            boolean hasNext = it.hasNext();
            str = SliceClientPermissions.SliceAuthority.DELIMITER;
            if (!hasNext) {
                break;
            }
            SyncStorageEngine.SyncHistoryItem item = it.next();
            Iterator<SyncStorageEngine.SyncHistoryItem> it2 = it;
            SyncStorageEngine.AuthorityInfo authorityInfo = syncManager.mSyncStorageEngine.getAuthority(item.authorityId);
            if (authorityInfo != null) {
                String authorityName4 = authorityInfo.target.provider;
                StringBuilder sb = new StringBuilder();
                items2 = items3;
                sb.append(authorityInfo.target.account.name);
                sb.append(str);
                sb.append(authorityInfo.target.account.type);
                sb.append(" u");
                sb.append(authorityInfo.target.userId);
                accountKey3 = sb.toString();
                authorityName3 = authorityName4;
            } else {
                items2 = items3;
                authorityName3 = ProcessPolicy.REASON_UNKNOWN;
                accountKey3 = ProcessPolicy.REASON_UNKNOWN;
            }
            int length = authorityName3.length();
            if (length > maxAuthority3) {
                maxAuthority3 = length;
            }
            int length2 = accountKey3.length();
            if (length2 > maxAccount3) {
                maxAccount3 = length2;
            }
            int maxAuthority4 = maxAuthority3;
            int maxAccount4 = maxAccount3;
            long elapsedTime = item.elapsedTime;
            long totalElapsedTime3 = totalElapsedTime2 + elapsedTime;
            long totalTimes3 = totalTimes2 + 1;
            AuthoritySyncStats authoritySyncStats2 = (AuthoritySyncStats) newHashMap.get(authorityName3);
            if (authoritySyncStats2 == null) {
                totalElapsedTime = totalElapsedTime3;
                authoritySyncStats = new AuthoritySyncStats(authorityName3);
                newHashMap.put(authorityName3, authoritySyncStats);
            } else {
                totalElapsedTime = totalElapsedTime3;
                authoritySyncStats = authoritySyncStats2;
            }
            long totalTimes4 = totalTimes3;
            authoritySyncStats.elapsedTime += elapsedTime;
            authoritySyncStats.times++;
            Map<String, AccountSyncStats> accountMap = authoritySyncStats.accountMap;
            AccountSyncStats accountSyncStats = accountMap.get(accountKey3);
            if (accountSyncStats == null) {
                AuthoritySyncStats authoritySyncStats3 = authoritySyncStats;
                accountSyncStats = new AccountSyncStats(accountKey3);
                accountMap.put(accountKey3, accountSyncStats);
            }
            Map<String, AccountSyncStats> map = accountMap;
            accountSyncStats.elapsedTime += elapsedTime;
            accountSyncStats.times++;
            maxAuthority3 = maxAuthority4;
            it = it2;
            maxAccount3 = maxAccount4;
            items3 = items2;
            totalElapsedTime2 = totalElapsedTime;
            totalTimes2 = totalTimes4;
        }
        ArrayList<SyncStorageEngine.SyncHistoryItem> items4 = items3;
        if (totalElapsedTime2 > 0) {
            pw.println();
            printWriter.printf("Detailed Statistics (Recent history):  %d (# of times) %ds (sync time)\n", new Object[]{Long.valueOf(totalTimes2), Long.valueOf(totalElapsedTime2 / 1000)});
            List<AuthoritySyncStats> arrayList = new ArrayList<>(newHashMap.values());
            Collections.sort(arrayList, new Comparator<AuthoritySyncStats>() {
                public int compare(AuthoritySyncStats lhs, AuthoritySyncStats rhs) {
                    int compare = Integer.compare(rhs.times, lhs.times);
                    if (compare == 0) {
                        return Long.compare(rhs.elapsedTime, lhs.elapsedTime);
                    }
                    return compare;
                }
            });
            int maxLength = Math.max(maxAuthority3, maxAccount3 + 3);
            int padLength = maxLength + 4 + 2 + 10 + 11;
            char[] chars = new char[padLength];
            HashMap hashMap = newHashMap;
            Arrays.fill(chars, '-');
            String separator = new String(chars);
            int i = padLength;
            char[] cArr = chars;
            String timeStr = String.format("  %%-%ds: %%-9s  %%-11s\n", new Object[]{Integer.valueOf(maxLength + 2)});
            str3 = " u";
            String accountFormat = String.format("    %%-%ds:   %%-9s  %%-11s\n", new Object[]{Integer.valueOf(maxLength)});
            printWriter.println(separator);
            Iterator<AuthoritySyncStats> it3 = arrayList.iterator();
            while (it3.hasNext()) {
                List<AuthoritySyncStats> sortedAuthorities = arrayList;
                AuthoritySyncStats authoritySyncStats4 = it3.next();
                Iterator<AuthoritySyncStats> it4 = it3;
                String name = authoritySyncStats4.name;
                String str8 = str;
                int maxLength2 = maxLength;
                long elapsedTime2 = authoritySyncStats4.elapsedTime;
                int N6 = N5;
                int times = authoritySyncStats4.times;
                int maxAuthority5 = maxAuthority3;
                int maxAccount5 = maxAccount3;
                String timeStr2 = String.format("%ds/%d%%", new Object[]{Long.valueOf(elapsedTime2 / 1000), Long.valueOf((elapsedTime2 * 100) / totalElapsedTime2)});
                long j = elapsedTime2;
                int i2 = times;
                String str9 = "%ds/%d%%";
                String separator2 = separator;
                printWriter.printf(timeStr, new Object[]{name, String.format("%d/%d%%", new Object[]{Integer.valueOf(times), Long.valueOf(((long) (times * 100)) / totalTimes2)}), timeStr2});
                List<AccountSyncStats> sortedAccounts = new ArrayList<>(authoritySyncStats4.accountMap.values());
                Collections.sort(sortedAccounts, new Comparator<AccountSyncStats>() {
                    public int compare(AccountSyncStats lhs, AccountSyncStats rhs) {
                        int compare = Integer.compare(rhs.times, lhs.times);
                        if (compare == 0) {
                            return Long.compare(rhs.elapsedTime, lhs.elapsedTime);
                        }
                        return compare;
                    }
                });
                Iterator<AccountSyncStats> it5 = sortedAccounts.iterator();
                while (it5.hasNext()) {
                    AccountSyncStats stats = it5.next();
                    AuthoritySyncStats authoritySyncStats5 = authoritySyncStats4;
                    long elapsedTime3 = stats.elapsedTime;
                    Iterator<AccountSyncStats> it6 = it5;
                    int times2 = stats.times;
                    String str10 = timeStr2;
                    String authorityFormat = timeStr;
                    String timeStr3 = String.format(str9, new Object[]{Long.valueOf(elapsedTime3 / 1000), Long.valueOf((elapsedTime3 * 100) / totalElapsedTime2)});
                    long j2 = elapsedTime3;
                    printWriter.printf(accountFormat, new Object[]{stats.name, String.format("%d/%d%%", new Object[]{Integer.valueOf(times2), Long.valueOf(((long) (times2 * 100)) / totalTimes2)}), timeStr3});
                    int i3 = times2;
                    timeStr2 = timeStr3;
                    authoritySyncStats4 = authoritySyncStats5;
                    sortedAccounts = sortedAccounts;
                    it5 = it6;
                    timeStr = authorityFormat;
                    totalElapsedTime2 = totalElapsedTime2;
                }
                List<AccountSyncStats> list = sortedAccounts;
                long j3 = totalElapsedTime2;
                String str11 = timeStr2;
                String str12 = timeStr;
                String separator3 = separator2;
                printWriter.println(separator3);
                separator = separator3;
                it3 = it4;
                arrayList = sortedAuthorities;
                str = str8;
                maxLength = maxLength2;
                N5 = N6;
                maxAuthority3 = maxAuthority5;
                maxAccount3 = maxAccount5;
            }
            List<AuthoritySyncStats> sortedAuthorities2 = arrayList;
            String str13 = separator;
            long j4 = totalElapsedTime2;
            N = N5;
            maxAuthority = maxAuthority3;
            maxAccount = maxAccount3;
            String str14 = timeStr;
            str2 = str;
            int i4 = maxLength;
        } else {
            Map<String, AuthoritySyncStats> authorityMap = newHashMap;
            long j5 = totalElapsedTime2;
            N = N5;
            maxAuthority = maxAuthority3;
            maxAccount = maxAccount3;
            str3 = " u";
            str2 = str;
        }
        pw.println();
        printWriter.println("Recent Sync History");
        printWriter.println("(SERVER is now split up to FEED and OTHER)");
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  %-");
        int maxAccount6 = maxAccount;
        sb2.append(maxAccount6);
        sb2.append("s  %-");
        int maxAuthority6 = maxAuthority;
        sb2.append(maxAuthority6);
        sb2.append("s %s\n");
        String format = sb2.toString();
        Map<String, Long> lastTimeMap3 = Maps.newHashMap();
        PackageManager pm3 = syncManager.mContext.getPackageManager();
        int i5 = 0;
        while (true) {
            N2 = N;
            if (i5 >= N2) {
                break;
            }
            ArrayList<SyncStorageEngine.SyncHistoryItem> items5 = items4;
            SyncStorageEngine.SyncHistoryItem item2 = items5.get(i5);
            SyncStorageEngine.AuthorityInfo authorityInfo2 = syncManager.mSyncStorageEngine.getAuthority(item2.authorityId);
            if (authorityInfo2 != null) {
                authorityName2 = authorityInfo2.target.provider;
                totalTimes = totalTimes2;
                StringBuilder sb3 = new StringBuilder();
                sb3.append(authorityInfo2.target.account.name);
                diffString = str2;
                sb3.append(diffString);
                maxAccount2 = maxAccount6;
                sb3.append(authorityInfo2.target.account.type);
                str6 = str3;
                sb3.append(str6);
                maxAuthority2 = maxAuthority6;
                sb3.append(authorityInfo2.target.userId);
                accountKey2 = sb3.toString();
            } else {
                totalTimes = totalTimes2;
                maxAccount2 = maxAccount6;
                maxAuthority2 = maxAuthority6;
                str6 = str3;
                diffString = str2;
                authorityName2 = ProcessPolicy.REASON_UNKNOWN;
                accountKey2 = ProcessPolicy.REASON_UNKNOWN;
            }
            SyncStorageEngine.AuthorityInfo authorityInfo3 = authorityInfo2;
            long elapsedTime4 = item2.elapsedTime;
            Time time = new Time();
            String str15 = str6;
            ArrayList<SyncStorageEngine.SyncHistoryItem> items6 = items5;
            long eventTime = item2.eventTime;
            time.set(eventTime);
            String key = authorityName2 + diffString + accountKey2;
            Long lastEventTime = lastTimeMap3.get(key);
            if (lastEventTime == null) {
                diffString2 = "";
                str7 = diffString;
                N4 = N2;
            } else {
                long diff = (lastEventTime.longValue() - eventTime) / 1000;
                if (diff < 60) {
                    str7 = diffString;
                    N4 = N2;
                    diffString2 = String.valueOf(diff);
                } else if (diff < 3600) {
                    str7 = diffString;
                    N4 = N2;
                    diffString2 = String.format("%02d:%02d", new Object[]{Long.valueOf(diff / 60), Long.valueOf(diff % 60)});
                } else {
                    str7 = diffString;
                    N4 = N2;
                    long sec = diff % 3600;
                    diffString2 = String.format("%02d:%02d:%02d", new Object[]{Long.valueOf(diff / 3600), Long.valueOf(sec / 60), Long.valueOf(sec % 60)});
                }
            }
            lastTimeMap3.put(key, Long.valueOf(eventTime));
            String str16 = key;
            printWriter.printf("  #%-3d: %s %8s  %5.1fs  %8s", new Object[]{Integer.valueOf(i5 + 1), formatTime(eventTime), SyncStorageEngine.SOURCES[item2.source], Float.valueOf(((float) elapsedTime4) / 1000.0f), diffString2});
            printWriter.printf(format, new Object[]{accountKey2, authorityName2, SyncOperation.reasonToString(pm3, item2.reason)});
            if (item2.event == 1) {
                lastTimeMap2 = lastTimeMap3;
                pm2 = pm3;
                if (item2.upstreamActivity == 0 && item2.downstreamActivity == 0) {
                    String str17 = accountKey2;
                    pm = pm2;
                    if (item2.mesg != null && !SyncStorageEngine.MESG_SUCCESS.equals(item2.mesg)) {
                        printWriter.printf("    mesg=%s\n", new Object[]{item2.mesg});
                    }
                    i5++;
                    lastTimeMap3 = lastTimeMap2;
                    pm3 = pm;
                    totalTimes2 = totalTimes;
                    items4 = items6;
                    str3 = str15;
                    maxAuthority6 = maxAuthority2;
                    maxAccount6 = maxAccount2;
                    str2 = str7;
                    N = N4;
                    syncManager = this;
                }
            } else {
                lastTimeMap2 = lastTimeMap3;
                pm2 = pm3;
            }
            String str18 = accountKey2;
            pm = pm2;
            printWriter.printf("    event=%d upstreamActivity=%d downstreamActivity=%d\n", new Object[]{Integer.valueOf(item2.event), Long.valueOf(item2.upstreamActivity), Long.valueOf(item2.downstreamActivity)});
            printWriter.printf("    mesg=%s\n", new Object[]{item2.mesg});
            i5++;
            lastTimeMap3 = lastTimeMap2;
            pm3 = pm;
            totalTimes2 = totalTimes;
            items4 = items6;
            str3 = str15;
            maxAuthority6 = maxAuthority2;
            maxAccount6 = maxAccount2;
            str2 = str7;
            N = N4;
            syncManager = this;
        }
        Map<String, Long> lastTimeMap4 = lastTimeMap3;
        long j6 = totalTimes2;
        int N7 = N2;
        int i6 = maxAccount6;
        int i7 = maxAuthority6;
        String str19 = str3;
        String str20 = str2;
        PackageManager packageManager = pm3;
        ArrayList<SyncStorageEngine.SyncHistoryItem> items7 = items4;
        pw.println();
        printWriter.println("Recent Sync History Extras");
        printWriter.println("(SERVER is now split up to FEED and OTHER)");
        int i8 = 0;
        while (true) {
            int N8 = N7;
            if (i8 < N8) {
                ArrayList<SyncStorageEngine.SyncHistoryItem> items8 = items7;
                SyncStorageEngine.SyncHistoryItem item3 = items8.get(i8);
                Bundle extras = item3.extras;
                if (extras == null) {
                    lastTimeMap = lastTimeMap4;
                    N3 = N8;
                    items = items8;
                    str4 = str19;
                    str5 = str20;
                } else if (extras.size() == 0) {
                    lastTimeMap = lastTimeMap4;
                    N3 = N8;
                    items = items8;
                    str4 = str19;
                    str5 = str20;
                } else {
                    SyncStorageEngine.AuthorityInfo authorityInfo4 = this.mSyncStorageEngine.getAuthority(item3.authorityId);
                    if (authorityInfo4 != null) {
                        authorityName = authorityInfo4.target.provider;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append(authorityInfo4.target.account.name);
                        str5 = str20;
                        sb4.append(str5);
                        sb4.append(authorityInfo4.target.account.type);
                        str4 = str19;
                        sb4.append(str4);
                        sb4.append(authorityInfo4.target.userId);
                        accountKey = sb4.toString();
                    } else {
                        str4 = str19;
                        str5 = str20;
                        authorityName = ProcessPolicy.REASON_UNKNOWN;
                        accountKey = ProcessPolicy.REASON_UNKNOWN;
                    }
                    Time time2 = new Time();
                    N3 = N8;
                    items = items8;
                    long eventTime2 = item3.eventTime;
                    time2.set(eventTime2);
                    lastTimeMap = lastTimeMap4;
                    long j7 = eventTime2;
                    printWriter.printf("  #%-3d: %s %8s ", new Object[]{Integer.valueOf(i8 + 1), formatTime(eventTime2), SyncStorageEngine.SOURCES[item3.source]});
                    printWriter.printf(format, new Object[]{accountKey, authorityName, extras});
                }
                i8++;
                str20 = str5;
                str19 = str4;
                lastTimeMap4 = lastTimeMap;
                items7 = items;
                N7 = N3;
            } else {
                int i9 = N8;
                ArrayList<SyncStorageEngine.SyncHistoryItem> arrayList2 = items7;
                return;
            }
        }
    }

    private void dumpDayStatistics(PrintWriter pw) {
        SyncStorageEngine.DayStats ds;
        int delta;
        SyncStorageEngine.DayStats[] dses = this.mSyncStorageEngine.getDayStatistics();
        if (dses != null && dses[0] != null) {
            pw.println();
            pw.println("Sync Statistics");
            pw.print("  Today:  ");
            dumpDayStatistic(pw, dses[0]);
            int today = dses[0].day;
            int i = 1;
            while (i <= 6 && i < dses.length && (ds = dses[i]) != null && (delta = today - ds.day) <= 6) {
                pw.print("  Day-");
                pw.print(delta);
                pw.print(":  ");
                dumpDayStatistic(pw, ds);
                i++;
            }
            int weekDay = today;
            while (i < dses.length) {
                SyncStorageEngine.DayStats aggr = null;
                weekDay -= 7;
                while (true) {
                    if (i >= dses.length) {
                        break;
                    }
                    SyncStorageEngine.DayStats ds2 = dses[i];
                    if (ds2 == null) {
                        i = dses.length;
                        break;
                    } else if (weekDay - ds2.day > 6) {
                        break;
                    } else {
                        i++;
                        if (aggr == null) {
                            aggr = new SyncStorageEngine.DayStats(weekDay);
                        }
                        aggr.successCount += ds2.successCount;
                        aggr.successTime += ds2.successTime;
                        aggr.failureCount += ds2.failureCount;
                        aggr.failureTime += ds2.failureTime;
                    }
                }
                if (aggr != null) {
                    pw.print("  Week-");
                    pw.print((today - weekDay) / 7);
                    pw.print(": ");
                    dumpDayStatistic(pw, aggr);
                }
            }
        }
    }

    private void dumpSyncAdapters(IndentingPrintWriter pw) {
        pw.println();
        List<UserInfo> users = getAllUsers();
        if (users != null) {
            for (UserInfo user : users) {
                pw.println("Sync adapters for " + user + ":");
                pw.increaseIndent();
                for (RegisteredServicesCache.ServiceInfo<?> info : this.mSyncAdapters.getAllServices(user.id)) {
                    pw.println(info);
                }
                pw.decreaseIndent();
                pw.println();
            }
        }
    }

    private static class AuthoritySyncStats {
        Map<String, AccountSyncStats> accountMap;
        long elapsedTime;
        String name;
        int times;

        private AuthoritySyncStats(String name2) {
            this.accountMap = Maps.newHashMap();
            this.name = name2;
        }
    }

    private static class AccountSyncStats {
        long elapsedTime;
        String name;
        int times;

        private AccountSyncStats(String name2) {
            this.name = name2;
        }
    }

    static void sendOnUnsyncableAccount(Context context, RegisteredServicesCache.ServiceInfo<SyncAdapterType> syncAdapterInfo, int userId, OnReadyCallback onReadyCallback) {
        OnUnsyncableAccountCheck connection = new OnUnsyncableAccountCheck(syncAdapterInfo, onReadyCallback);
        boolean isBound = false;
        if (SyncManagerInjector.canBindService(context, getAdapterBindIntent(context, syncAdapterInfo.componentName, userId), userId)) {
            isBound = context.bindServiceAsUser(getAdapterBindIntent(context, syncAdapterInfo.componentName, userId), connection, 21, UserHandle.of(userId));
        }
        if (isBound) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable(context, connection) {
                private final /* synthetic */ Context f$0;
                private final /* synthetic */ SyncManager.OnUnsyncableAccountCheck f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    this.f$0.unbindService(this.f$1);
                }
            }, 5000);
        } else {
            connection.onReady();
        }
    }

    private static class OnUnsyncableAccountCheck implements ServiceConnection {
        static final long SERVICE_BOUND_TIME_MILLIS = 5000;
        private final OnReadyCallback mOnReadyCallback;
        private final RegisteredServicesCache.ServiceInfo<SyncAdapterType> mSyncAdapterInfo;

        OnUnsyncableAccountCheck(RegisteredServicesCache.ServiceInfo<SyncAdapterType> syncAdapterInfo, OnReadyCallback onReadyCallback) {
            this.mSyncAdapterInfo = syncAdapterInfo;
            this.mOnReadyCallback = onReadyCallback;
        }

        /* access modifiers changed from: private */
        public void onReady() {
            long identity = Binder.clearCallingIdentity();
            try {
                this.mOnReadyCallback.onReady();
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                ISyncAdapter.Stub.asInterface(service).onUnsyncableAccount(new ISyncAdapterUnsyncableAccountCallback.Stub() {
                    public void onUnsyncableAccountDone(boolean isReady) {
                        if (isReady) {
                            OnUnsyncableAccountCheck.this.onReady();
                        }
                    }
                });
            } catch (RemoteException e) {
                Slog.e("SyncManager", "Could not call onUnsyncableAccountDone " + this.mSyncAdapterInfo, e);
                onReady();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    }

    private class SyncTimeTracker {
        boolean mLastWasSyncing;
        private long mTimeSpentSyncing;
        long mWhenSyncStarted;

        private SyncTimeTracker() {
            this.mLastWasSyncing = false;
            this.mWhenSyncStarted = 0;
        }

        public synchronized void update() {
            boolean isSyncInProgress = !SyncManager.this.mActiveSyncContexts.isEmpty();
            if (isSyncInProgress != this.mLastWasSyncing) {
                long now = SystemClock.elapsedRealtime();
                if (isSyncInProgress) {
                    this.mWhenSyncStarted = now;
                } else {
                    this.mTimeSpentSyncing += now - this.mWhenSyncStarted;
                }
                this.mLastWasSyncing = isSyncInProgress;
            }
        }

        public synchronized long timeSpentSyncing() {
            if (!this.mLastWasSyncing) {
                return this.mTimeSpentSyncing;
            }
            long now = SystemClock.elapsedRealtime();
            return this.mTimeSpentSyncing + (now - this.mWhenSyncStarted);
        }
    }

    class ServiceConnectionData {
        public final ActiveSyncContext activeSyncContext;
        public final IBinder adapter;

        ServiceConnectionData(ActiveSyncContext activeSyncContext2, IBinder adapter2) {
            this.activeSyncContext = activeSyncContext2;
            this.adapter = adapter2;
        }
    }

    private static SyncManager getInstance() {
        SyncManager syncManager;
        synchronized (SyncManager.class) {
            if (sInstance == null) {
                Slog.wtf("SyncManager", "sInstance == null");
            }
            syncManager = sInstance;
        }
        return syncManager;
    }

    public static boolean readyToSync(int userId) {
        SyncManager instance = getInstance();
        return instance != null && SyncJobService.isReady() && instance.mProvisioned && instance.isUserUnlocked(userId);
    }

    public static void sendMessage(Message message) {
        SyncManager instance = getInstance();
        if (instance != null) {
            instance.mSyncHandler.sendMessage(message);
        }
    }

    class SyncHandler extends Handler {
        private static final int MESSAGE_ACCOUNTS_UPDATED = 9;
        private static final int MESSAGE_CANCEL = 6;
        private static final int MESSAGE_MONITOR_SYNC = 8;
        static final int MESSAGE_REMOVE_PERIODIC_SYNC = 14;
        static final int MESSAGE_SCHEDULE_SYNC = 12;
        private static final int MESSAGE_SERVICE_CONNECTED = 4;
        private static final int MESSAGE_SERVICE_DISCONNECTED = 5;
        static final int MESSAGE_START_SYNC = 10;
        static final int MESSAGE_STOP_SYNC = 11;
        private static final int MESSAGE_SYNC_FINISHED = 1;
        static final int MESSAGE_UPDATE_PERIODIC_SYNC = 13;
        public final SyncTimeTracker mSyncTimeTracker = new SyncTimeTracker();
        private final HashMap<String, PowerManager.WakeLock> mWakeLocks = Maps.newHashMap();

        public SyncHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            SyncManager.this.mSyncManagerWakeLock.acquire();
            try {
                handleSyncMessage(msg);
            } finally {
                SyncManager.this.mSyncManagerWakeLock.release();
            }
        }

        private void handleSyncMessage(Message msg) {
            boolean isLoggable = Log.isLoggable("SyncManager", 2);
            try {
                boolean unused = SyncManager.this.mDataConnectionIsConnected = SyncManager.this.readDataConnectionState();
                int i = msg.what;
                boolean applyBackoff = true;
                if (i == 1) {
                    SyncFinishedOrCancelledMessagePayload payload = (SyncFinishedOrCancelledMessagePayload) msg.obj;
                    if (SyncManager.this.isSyncStillActiveH(payload.activeSyncContext)) {
                        if (isLoggable) {
                            Slog.v("SyncManager", "syncFinished" + payload.activeSyncContext.mSyncOperation);
                        }
                        SyncJobService.callJobFinished(payload.activeSyncContext.mSyncOperation.jobId, false, "sync finished");
                        runSyncFinishedOrCanceledH(payload.syncResult, payload.activeSyncContext);
                    } else if (isLoggable) {
                        Log.d("SyncManager", "handleSyncHandlerMessage: dropping since the sync is no longer active: " + payload.activeSyncContext);
                    }
                } else if (i == 4) {
                    ServiceConnectionData msgData = (ServiceConnectionData) msg.obj;
                    if (isLoggable) {
                        Log.d("SyncManager", "handleSyncHandlerMessage: MESSAGE_SERVICE_CONNECTED: " + msgData.activeSyncContext);
                    }
                    if (SyncManager.this.isSyncStillActiveH(msgData.activeSyncContext)) {
                        runBoundToAdapterH(msgData.activeSyncContext, msgData.adapter);
                    }
                } else if (i == 5) {
                    ActiveSyncContext currentSyncContext = ((ServiceConnectionData) msg.obj).activeSyncContext;
                    if (isLoggable) {
                        Log.d("SyncManager", "handleSyncHandlerMessage: MESSAGE_SERVICE_DISCONNECTED: " + currentSyncContext);
                    }
                    if (SyncManager.this.isSyncStillActiveH(currentSyncContext)) {
                        if (currentSyncContext.mSyncAdapter != null) {
                            SyncManager.this.mLogger.log("Calling cancelSync for SERVICE_DISCONNECTED ", currentSyncContext, " adapter=", currentSyncContext.mSyncAdapter);
                            currentSyncContext.mSyncAdapter.cancelSync(currentSyncContext);
                            SyncManager.this.mLogger.log("Canceled");
                        }
                        SyncResult syncResult = new SyncResult();
                        syncResult.stats.numIoExceptions++;
                        SyncJobService.callJobFinished(currentSyncContext.mSyncOperation.jobId, false, "service disconnected");
                        runSyncFinishedOrCanceledH(syncResult, currentSyncContext);
                    }
                } else if (i != 6) {
                    switch (i) {
                        case 8:
                            ActiveSyncContext monitoredSyncContext = (ActiveSyncContext) msg.obj;
                            if (isLoggable) {
                                Log.d("SyncManager", "handleSyncHandlerMessage: MESSAGE_MONITOR_SYNC: " + monitoredSyncContext.mSyncOperation.target);
                            }
                            if (!isSyncNotUsingNetworkH(monitoredSyncContext)) {
                                SyncManager.this.postMonitorSyncProgressMessage(monitoredSyncContext);
                                break;
                            } else {
                                Log.w("SyncManager", String.format("Detected sync making no progress for %s. cancelling.", new Object[]{SyncLogger.logSafe(monitoredSyncContext)}));
                                SyncJobService.callJobFinished(monitoredSyncContext.mSyncOperation.jobId, false, "no network activity");
                                runSyncFinishedOrCanceledH((SyncResult) null, monitoredSyncContext);
                                break;
                            }
                        case 9:
                            if (Log.isLoggable("SyncManager", 2)) {
                                Slog.v("SyncManager", "handleSyncHandlerMessage: MESSAGE_ACCOUNTS_UPDATED");
                            }
                            updateRunningAccountsH((SyncStorageEngine.EndPoint) msg.obj);
                            break;
                        case 10:
                            startSyncH((SyncOperation) msg.obj);
                            break;
                        case 11:
                            SyncOperation op = (SyncOperation) msg.obj;
                            if (isLoggable) {
                                Slog.v("SyncManager", "Stop sync received.");
                            }
                            ActiveSyncContext asc = findActiveSyncContextH(op.jobId);
                            if (asc != null) {
                                runSyncFinishedOrCanceledH((SyncResult) null, asc);
                                boolean reschedule = msg.arg1 != 0;
                                if (msg.arg2 == 0) {
                                    applyBackoff = false;
                                }
                                if (isLoggable) {
                                    Slog.v("SyncManager", "Stopping sync. Reschedule: " + reschedule + "Backoff: " + applyBackoff);
                                }
                                if (applyBackoff) {
                                    SyncManager.this.increaseBackoffSetting(op.target);
                                }
                                if (reschedule) {
                                    deferStoppedSyncH(op, 0);
                                }
                                break;
                            }
                            break;
                        case 12:
                            ScheduleSyncMessagePayload syncPayload = (ScheduleSyncMessagePayload) msg.obj;
                            SyncManager.this.scheduleSyncOperationH(syncPayload.syncOperation, syncPayload.minDelayMillis);
                            break;
                        case 13:
                            UpdatePeriodicSyncMessagePayload data = (UpdatePeriodicSyncMessagePayload) msg.obj;
                            updateOrAddPeriodicSyncH(data.target, data.pollFrequency, data.flex, data.extras);
                            break;
                        case 14:
                            Pair<SyncStorageEngine.EndPoint, String> args = (Pair) msg.obj;
                            removePeriodicSyncH((SyncStorageEngine.EndPoint) args.first, msg.getData(), (String) args.second);
                            break;
                    }
                } else {
                    SyncStorageEngine.EndPoint endpoint = (SyncStorageEngine.EndPoint) msg.obj;
                    Bundle extras = msg.peekData();
                    if (isLoggable) {
                        Log.d("SyncManager", "handleSyncHandlerMessage: MESSAGE_CANCEL: " + endpoint + " bundle: " + extras);
                    }
                    cancelActiveSyncH(endpoint, extras, "MESSAGE_CANCEL");
                }
            } catch (RemoteException e) {
                SyncManager.this.mLogger.log("RemoteException ", Log.getStackTraceString(e));
            } catch (Throwable th) {
                this.mSyncTimeTracker.update();
                throw th;
            }
            this.mSyncTimeTracker.update();
        }

        /* access modifiers changed from: private */
        public PowerManager.WakeLock getSyncWakeLock(SyncOperation operation) {
            String wakeLockKey = operation.wakeLockName();
            PowerManager.WakeLock wakeLock = this.mWakeLocks.get(wakeLockKey);
            if (wakeLock != null) {
                return wakeLock;
            }
            PowerManager.WakeLock wakeLock2 = SyncManager.this.mPowerManager.newWakeLock(1, SyncManager.SYNC_WAKE_LOCK_PREFIX + wakeLockKey);
            wakeLock2.setReferenceCounted(false);
            this.mWakeLocks.put(wakeLockKey, wakeLock2);
            return wakeLock2;
        }

        private void deferSyncH(SyncOperation op, long delay, String why) {
            SyncLogger access$700 = SyncManager.this.mLogger;
            Object[] objArr = new Object[8];
            objArr[0] = "deferSyncH() ";
            objArr[1] = op.isPeriodic ? "periodic " : "";
            objArr[2] = "sync.  op=";
            objArr[3] = op;
            objArr[4] = " delay=";
            objArr[5] = Long.valueOf(delay);
            objArr[6] = " why=";
            objArr[7] = why;
            access$700.log(objArr);
            SyncJobService.callJobFinished(op.jobId, false, why);
            if (op.isPeriodic) {
                SyncManager.this.scheduleSyncOperationH(op.createOneTimeSyncOperation(), delay);
                return;
            }
            SyncManager.this.cancelJob(op, "deferSyncH()");
            SyncManager.this.scheduleSyncOperationH(op, delay);
        }

        private void deferStoppedSyncH(SyncOperation op, long delay) {
            if (op.isPeriodic) {
                SyncManager.this.scheduleSyncOperationH(op.createOneTimeSyncOperation(), delay);
            } else {
                SyncManager.this.scheduleSyncOperationH(op, delay);
            }
        }

        private void deferActiveSyncH(ActiveSyncContext asc, String why) {
            SyncOperation op = asc.mSyncOperation;
            runSyncFinishedOrCanceledH((SyncResult) null, asc);
            deferSyncH(op, 10000, why);
        }

        /* access modifiers changed from: package-private */
        public void deferActiveSyncH(ActiveSyncContext asc) {
            deferActiveSyncH(asc, "injector defer");
        }

        private void startSyncH(SyncOperation op) {
            boolean isLoggable = Log.isLoggable("SyncManager", 2);
            if (isLoggable) {
                Slog.v("SyncManager", op.toString());
            }
            SyncManager.this.mSyncStorageEngine.setClockValid();
            SyncJobService.markSyncStarted(op.jobId);
            if (SyncManager.this.mStorageIsLow) {
                deferSyncH(op, 3600000, "storage low");
                return;
            }
            long injectorDelay = SyncManagerInjector.getSyncDelayedH(op, SyncManager.this);
            if (injectorDelay > 0) {
                if (Log.isLoggable("SyncManager", 2)) {
                    Log.v("SyncManager", "Sync is delayed by injector for " + injectorDelay + "ms");
                }
                deferSyncH(op, injectorDelay, "injector delay");
                return;
            }
            if (op.isPeriodic) {
                for (SyncOperation syncOperation : SyncManager.this.getAllPendingSyncs()) {
                    if (syncOperation.sourcePeriodicId == op.jobId) {
                        SyncJobService.callJobFinished(op.jobId, false, "periodic sync, pending");
                        return;
                    }
                }
                Iterator<ActiveSyncContext> it = SyncManager.this.mActiveSyncContexts.iterator();
                while (it.hasNext()) {
                    if (it.next().mSyncOperation.sourcePeriodicId == op.jobId) {
                        SyncJobService.callJobFinished(op.jobId, false, "periodic sync, already running");
                        return;
                    }
                }
                if (SyncManager.this.isAdapterDelayed(op.target)) {
                    deferSyncH(op, 0, "backing off");
                    return;
                }
            }
            Iterator<ActiveSyncContext> it2 = SyncManager.this.mActiveSyncContexts.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                ActiveSyncContext asc = it2.next();
                if (asc.mSyncOperation.isConflict(op)) {
                    if (asc.mSyncOperation.findPriority() >= op.findPriority()) {
                        if (isLoggable) {
                            Slog.v("SyncManager", "Rescheduling sync due to conflict " + op.toString());
                        }
                        deferSyncH(op, 10000, "delay on conflict");
                        return;
                    }
                    if (isLoggable) {
                        Slog.v("SyncManager", "Pushing back running sync due to a higher priority sync");
                    }
                    deferActiveSyncH(asc, "preempted");
                }
            }
            int syncOpState = computeSyncOpState(op);
            if (syncOpState == 1 || syncOpState == 2) {
                int i = op.jobId;
                SyncJobService.callJobFinished(i, false, "invalid op state: " + syncOpState);
                return;
            }
            if (!dispatchSyncOperation(op)) {
                SyncJobService.callJobFinished(op.jobId, false, "dispatchSyncOperation() failed");
            }
            SyncManager.this.setAuthorityPendingState(op.target);
        }

        private ActiveSyncContext findActiveSyncContextH(int jobId) {
            Iterator<ActiveSyncContext> it = SyncManager.this.mActiveSyncContexts.iterator();
            while (it.hasNext()) {
                ActiveSyncContext asc = it.next();
                SyncOperation op = asc.mSyncOperation;
                if (op != null && op.jobId == jobId) {
                    return asc;
                }
            }
            return null;
        }

        private void updateRunningAccountsH(SyncStorageEngine.EndPoint syncTargets) {
            SyncStorageEngine.EndPoint endPoint = syncTargets;
            AccountAndUser[] oldAccounts = SyncManager.this.mRunningAccounts;
            AccountAndUser[] unused = SyncManager.this.mRunningAccounts = AccountManagerService.getSingleton().getRunningAccounts();
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "Accounts list: ");
                for (AccountAndUser acc : SyncManager.this.mRunningAccounts) {
                    Slog.v("SyncManager", acc.toString());
                }
            }
            if (SyncManager.this.mLogger.enabled()) {
                SyncManager.this.mLogger.log("updateRunningAccountsH: ", Arrays.toString(SyncManager.this.mRunningAccounts));
            }
            SyncManager.this.removeStaleAccounts();
            AccountAndUser[] accounts = SyncManager.this.mRunningAccounts;
            Iterator<ActiveSyncContext> it = SyncManager.this.mActiveSyncContexts.iterator();
            while (it.hasNext()) {
                ActiveSyncContext currentSyncContext = it.next();
                if (!SyncManager.this.containsAccountAndUser(accounts, currentSyncContext.mSyncOperation.target.account, currentSyncContext.mSyncOperation.target.userId)) {
                    Log.d("SyncManager", "canceling sync since the account is no longer running");
                    SyncManager.this.sendSyncFinishedOrCanceledMessage(currentSyncContext, (SyncResult) null);
                }
            }
            if (endPoint != null) {
                AccountAndUser[] access$3200 = SyncManager.this.mRunningAccounts;
                int length = access$3200.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    AccountAndUser aau = access$3200[i];
                    if (!SyncManager.this.containsAccountAndUser(oldAccounts, aau.account, aau.userId)) {
                        if (Log.isLoggable("SyncManager", 3)) {
                            Log.d("SyncManager", "Account " + aau.account + " added, checking sync restore data");
                        }
                        AccountSyncSettingsBackupHelper.accountAdded(SyncManager.this.mContext, endPoint.userId);
                    } else {
                        i++;
                    }
                }
            }
            AccountAndUser[] allAccounts = AccountManagerService.getSingleton().getAllAccounts();
            for (SyncOperation op : SyncManager.this.getAllPendingSyncs()) {
                if (!SyncManager.this.containsAccountAndUser(allAccounts, op.target.account, op.target.userId)) {
                    SyncManager.this.mLogger.log("canceling: ", op);
                    SyncManager.this.cancelJob(op, "updateRunningAccountsH()");
                }
            }
            if (endPoint != null) {
                SyncManager.this.scheduleSync(endPoint.account, endPoint.userId, -2, endPoint.provider, (Bundle) null, -1, 0, Process.myUid(), -4, (String) null);
            }
        }

        private void maybeUpdateSyncPeriodH(SyncOperation syncOperation, long pollFrequencyMillis, long flexMillis) {
            if (pollFrequencyMillis != syncOperation.periodMillis || flexMillis != syncOperation.flexMillis) {
                if (Log.isLoggable("SyncManager", 2)) {
                    Slog.v("SyncManager", "updating period " + syncOperation + " to " + pollFrequencyMillis + " and flex to " + flexMillis);
                }
                SyncOperation syncOperation2 = new SyncOperation(syncOperation, pollFrequencyMillis, flexMillis);
                syncOperation2.jobId = syncOperation.jobId;
                SyncManager.this.scheduleSyncOperationH(syncOperation2);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:21:0x00cd, code lost:
            r2 = r6.uid;
            r10 = new com.android.server.content.SyncOperation(r32, r2, r6.componentName.getPackageName(), -4, 4, r37, ((android.content.SyncAdapterType) r6.type).allowParallelSyncs(), true, -1, r26, r28, 0);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void updateOrAddPeriodicSyncH(com.android.server.content.SyncStorageEngine.EndPoint r32, long r33, long r35, android.os.Bundle r37) {
            /*
                r31 = this;
                r9 = r31
                r8 = r32
                r14 = r33
                r12 = r35
                r0 = 2
                java.lang.String r1 = "SyncManager"
                boolean r25 = android.util.Log.isLoggable(r1, r0)
                com.android.server.content.SyncManager r2 = com.android.server.content.SyncManager.this
                r2.verifyJobScheduler()
                r2 = 1000(0x3e8, double:4.94E-321)
                long r26 = r14 * r2
                long r28 = r12 * r2
                java.lang.String r2 = " extras: "
                java.lang.String r3 = " flexMillis: "
                java.lang.String r4 = " period: "
                if (r25 == 0) goto L_0x004c
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "Addition to periodic syncs requested: "
                r5.append(r6)
                r5.append(r8)
                r5.append(r4)
                r5.append(r14)
                r5.append(r3)
                r5.append(r12)
                r5.append(r2)
                java.lang.String r6 = r37.toString()
                r5.append(r6)
                java.lang.String r5 = r5.toString()
                android.util.Slog.v(r1, r5)
            L_0x004c:
                com.android.server.content.SyncManager r5 = com.android.server.content.SyncManager.this
                java.util.List r30 = r5.getAllPendingSyncs()
                java.util.Iterator r5 = r30.iterator()
            L_0x0056:
                boolean r6 = r5.hasNext()
                r7 = 1
                if (r6 == 0) goto L_0x0088
                java.lang.Object r6 = r5.next()
                r10 = r6
                com.android.server.content.SyncOperation r10 = (com.android.server.content.SyncOperation) r10
                boolean r6 = r10.isPeriodic
                if (r6 == 0) goto L_0x0085
                com.android.server.content.SyncStorageEngine$EndPoint r6 = r10.target
                boolean r6 = r6.matchesSpec(r8)
                if (r6 == 0) goto L_0x0085
                android.os.Bundle r6 = r10.extras
                r11 = r37
                boolean r6 = com.android.server.content.SyncManager.syncExtrasEquals(r6, r11, r7)
                if (r6 == 0) goto L_0x0087
                r1 = r31
                r2 = r10
                r3 = r26
                r5 = r28
                r1.maybeUpdateSyncPeriodH(r2, r3, r5)
                return
            L_0x0085:
                r11 = r37
            L_0x0087:
                goto L_0x0056
            L_0x0088:
                r11 = r37
                if (r25 == 0) goto L_0x00b6
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "Adding new periodic sync: "
                r5.append(r6)
                r5.append(r8)
                r5.append(r4)
                r5.append(r14)
                r5.append(r3)
                r5.append(r12)
                r5.append(r2)
                java.lang.String r2 = r37.toString()
                r5.append(r2)
                java.lang.String r2 = r5.toString()
                android.util.Slog.v(r1, r2)
            L_0x00b6:
                com.android.server.content.SyncManager r1 = com.android.server.content.SyncManager.this
                android.content.SyncAdaptersCache r1 = r1.mSyncAdapters
                java.lang.String r2 = r8.provider
                android.accounts.Account r3 = r8.account
                java.lang.String r3 = r3.type
                android.content.SyncAdapterType r2 = android.content.SyncAdapterType.newKey(r2, r3)
                int r3 = r8.userId
                android.content.pm.RegisteredServicesCache$ServiceInfo r6 = r1.getServiceInfo(r2, r3)
                if (r6 != 0) goto L_0x00cd
                return
            L_0x00cd:
                com.android.server.content.SyncOperation r1 = new com.android.server.content.SyncOperation
                int r2 = r6.uid
                android.content.ComponentName r3 = r6.componentName
                java.lang.String r3 = r3.getPackageName()
                r4 = -4
                r5 = 4
                java.lang.Object r10 = r6.type
                android.content.SyncAdapterType r10 = (android.content.SyncAdapterType) r10
                boolean r17 = r10.allowParallelSyncs()
                r18 = 1
                r19 = -1
                r24 = 0
                r10 = r1
                r11 = r32
                r12 = r2
                r13 = r3
                r14 = r4
                r15 = r5
                r16 = r37
                r20 = r26
                r22 = r28
                r10.<init>(r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r22, r24)
                int r11 = r9.computeSyncOpState(r10)
                if (r11 == r7) goto L_0x0150
                if (r11 == r0) goto L_0x0110
                com.android.server.content.SyncManager r0 = com.android.server.content.SyncManager.this
                r0.scheduleSyncOperationH(r10)
                com.android.server.content.SyncManager r0 = com.android.server.content.SyncManager.this
                com.android.server.content.SyncStorageEngine r0 = r0.mSyncStorageEngine
                int r1 = r8.userId
                r0.reportChange(r7, r1)
                return
            L_0x0110:
                java.lang.String r12 = r10.owningPackage
                int r0 = r10.owningUid
                int r13 = android.os.UserHandle.getUserId(r0)
                com.android.server.content.SyncManager r0 = com.android.server.content.SyncManager.this     // Catch:{ IllegalArgumentException -> 0x014c }
                android.content.pm.PackageManagerInternal r0 = r0.mPackageManagerInternal     // Catch:{ IllegalArgumentException -> 0x014c }
                boolean r0 = r0.wasPackageEverLaunched(r12, r13)     // Catch:{ IllegalArgumentException -> 0x014c }
                if (r0 != 0) goto L_0x0125
                return
            L_0x0125:
                com.android.server.content.SyncManager r0 = com.android.server.content.SyncManager.this
                android.accounts.AccountManagerInternal r0 = r0.mAccountManagerInternal
                com.android.server.content.SyncStorageEngine$EndPoint r1 = r10.target
                android.accounts.Account r14 = r1.account
                android.os.RemoteCallback r15 = new android.os.RemoteCallback
                com.android.server.content.-$$Lambda$SyncManager$SyncHandler$7-vThHsPImW4qB6AnVEnnD3dGhM r7 = new com.android.server.content.-$$Lambda$SyncManager$SyncHandler$7-vThHsPImW4qB6AnVEnnD3dGhM
                r1 = r7
                r2 = r31
                r3 = r32
                r4 = r33
                r16 = r6
                r9 = r7
                r6 = r35
                r8 = r37
                r1.<init>(r3, r4, r6, r8)
                r15.<init>(r9)
                r0.requestAccountAccess(r14, r12, r13, r15)
                return
            L_0x014c:
                r0 = move-exception
                r16 = r6
                return
            L_0x0150:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncManager.SyncHandler.updateOrAddPeriodicSyncH(com.android.server.content.SyncStorageEngine$EndPoint, long, long, android.os.Bundle):void");
        }

        public /* synthetic */ void lambda$updateOrAddPeriodicSyncH$0$SyncManager$SyncHandler(SyncStorageEngine.EndPoint target, long pollFrequency, long flex, Bundle extras, Bundle result) {
            if (result != null && result.getBoolean("booleanResult")) {
                SyncManager.this.updateOrAddPeriodicSync(target, pollFrequency, flex, extras);
            }
        }

        private void removePeriodicSyncInternalH(SyncOperation syncOperation, String why) {
            for (SyncOperation op : SyncManager.this.getAllPendingSyncs()) {
                if (op.sourcePeriodicId == syncOperation.jobId || op.jobId == syncOperation.jobId) {
                    ActiveSyncContext asc = findActiveSyncContextH(syncOperation.jobId);
                    if (asc != null) {
                        SyncJobService.callJobFinished(syncOperation.jobId, false, "removePeriodicSyncInternalH");
                        runSyncFinishedOrCanceledH((SyncResult) null, asc);
                    }
                    SyncManager.this.mLogger.log("removePeriodicSyncInternalH-canceling: ", op);
                    SyncManager.this.cancelJob(op, why);
                }
            }
        }

        private void removePeriodicSyncH(SyncStorageEngine.EndPoint target, Bundle extras, String why) {
            SyncManager.this.verifyJobScheduler();
            for (SyncOperation op : SyncManager.this.getAllPendingSyncs()) {
                if (op.isPeriodic && op.target.matchesSpec(target) && SyncManager.syncExtrasEquals(op.extras, extras, true)) {
                    removePeriodicSyncInternalH(op, why);
                }
            }
        }

        private boolean isSyncNotUsingNetworkH(ActiveSyncContext activeSyncContext) {
            ActiveSyncContext activeSyncContext2 = activeSyncContext;
            long deltaBytesTransferred = SyncManager.this.getTotalBytesTransferredByUid(activeSyncContext2.mSyncAdapterUid) - activeSyncContext2.mBytesTransferredAtLastPoll;
            if (Log.isLoggable("SyncManager", 3)) {
                long remainder = deltaBytesTransferred;
                long mb = remainder / 1048576;
                long remainder2 = remainder % 1048576;
                String str = "SyncManager";
                Log.d(str, String.format("Time since last update: %ds. Delta transferred: %dMBs,%dKBs,%dBs", new Object[]{Long.valueOf((SystemClock.elapsedRealtime() - activeSyncContext2.mLastPolledTimeElapsed) / 1000), Long.valueOf(mb), Long.valueOf(remainder2 / 1024), Long.valueOf(remainder2 % 1024)}));
            }
            if (deltaBytesTransferred <= 10) {
                return true;
            }
            return false;
        }

        private int computeSyncOpState(SyncOperation op) {
            boolean isLoggable = Log.isLoggable("SyncManager", 2);
            SyncStorageEngine.EndPoint target = op.target;
            if (!SyncManager.this.containsAccountAndUser(SyncManager.this.mRunningAccounts, target.account, target.userId)) {
                if (isLoggable) {
                    Slog.v("SyncManager", "    Dropping sync operation: account doesn't exist.");
                }
                return 1;
            }
            int state = SyncManager.this.computeSyncable(target.account, target.userId, target.provider, true);
            if (state == 3) {
                if (isLoggable) {
                    Slog.v("SyncManager", "    Dropping sync operation: isSyncable == SYNCABLE_NO_ACCOUNT_ACCESS");
                }
                return 2;
            } else if (state == 0) {
                if (isLoggable) {
                    Slog.v("SyncManager", "    Dropping sync operation: isSyncable == NOT_SYNCABLE");
                }
                return 1;
            } else {
                boolean syncEnabled = SyncManager.this.mSyncStorageEngine.getMasterSyncAutomatically(target.userId) && SyncManager.this.mSyncStorageEngine.getSyncAutomatically(target.account, target.userId, target.provider);
                boolean ignoreSystemConfiguration = op.isIgnoreSettings() || state < 0;
                if (syncEnabled || ignoreSystemConfiguration) {
                    return 0;
                }
                if (isLoggable) {
                    Slog.v("SyncManager", "    Dropping sync operation: disallowed by settings/network.");
                }
                return 1;
            }
        }

        private boolean dispatchSyncOperation(SyncOperation op) {
            UsageStatsManagerInternal usmi;
            SyncOperation syncOperation = op;
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "dispatchSyncOperation: we are going to sync " + syncOperation);
                Slog.v("SyncManager", "num active syncs: " + SyncManager.this.mActiveSyncContexts.size());
                Iterator<ActiveSyncContext> it = SyncManager.this.mActiveSyncContexts.iterator();
                while (it.hasNext()) {
                    Slog.v("SyncManager", it.next().toString());
                }
            }
            if (op.isAppStandbyExempted() && (usmi = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class)) != null) {
                usmi.reportExemptedSyncStart(syncOperation.owningPackage, UserHandle.getUserId(syncOperation.owningUid));
            }
            SyncStorageEngine.EndPoint info = syncOperation.target;
            SyncAdapterType syncAdapterType = SyncAdapterType.newKey(info.provider, info.account.type);
            RegisteredServicesCache.ServiceInfo<SyncAdapterType> syncAdapterInfo = SyncManager.this.mSyncAdapters.getServiceInfo(syncAdapterType, info.userId);
            if (syncAdapterInfo == null) {
                SyncManager.this.mLogger.log("dispatchSyncOperation() failed: no sync adapter info for ", syncAdapterType);
                Log.d("SyncManager", "can't find a sync adapter for " + syncAdapterType + ", removing settings for it");
                SyncManager.this.mSyncStorageEngine.removeAuthority(info);
                return false;
            }
            int targetUid = syncAdapterInfo.uid;
            ComponentName targetComponent = syncAdapterInfo.componentName;
            ActiveSyncContext activeSyncContext = new ActiveSyncContext(op, insertStartSyncEvent(op), targetUid);
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "dispatchSyncOperation: starting " + activeSyncContext);
            }
            activeSyncContext.mSyncInfo = SyncManager.this.mSyncStorageEngine.addActiveSync(activeSyncContext);
            SyncManager.this.mActiveSyncContexts.add(activeSyncContext);
            SyncManager.this.postMonitorSyncProgressMessage(activeSyncContext);
            if (activeSyncContext.bindToSyncAdapter(targetComponent, info.userId)) {
                return true;
            }
            SyncManager.this.mLogger.log("dispatchSyncOperation() failed: bind failed. target: ", targetComponent);
            Slog.e("SyncManager", "Bind attempt failed - target: " + targetComponent);
            closeActiveSyncContext(activeSyncContext);
            return false;
        }

        private void runBoundToAdapterH(ActiveSyncContext activeSyncContext, IBinder syncAdapter) {
            SyncOperation syncOperation = activeSyncContext.mSyncOperation;
            try {
                activeSyncContext.mIsLinkedToDeath = true;
                syncAdapter.linkToDeath(activeSyncContext, 0);
                SyncLogger access$700 = SyncManager.this.mLogger;
                access$700.log("Sync start: account=" + syncOperation.target.account, " authority=", syncOperation.target.provider, " reason=", SyncOperation.reasonToString((PackageManager) null, syncOperation.reason), " extras=", SyncOperation.extrasToString(syncOperation.extras), " adapter=", activeSyncContext.mSyncAdapter);
                activeSyncContext.mSyncAdapter = ISyncAdapter.Stub.asInterface(syncAdapter);
                activeSyncContext.mSyncAdapter.startSync(activeSyncContext, syncOperation.target.provider, syncOperation.target.account, syncOperation.extras);
                SyncManager.this.mLogger.log("Sync is running now...");
            } catch (RemoteException remoteExc) {
                SyncManager.this.mLogger.log("Sync failed with RemoteException: ", remoteExc.toString());
                Log.d("SyncManager", "maybeStartNextSync: caught a RemoteException, rescheduling", remoteExc);
                closeActiveSyncContext(activeSyncContext);
                SyncManager.this.increaseBackoffSetting(syncOperation.target);
                SyncManager.this.scheduleSyncOperationH(syncOperation);
            } catch (RuntimeException exc) {
                SyncManager.this.mLogger.log("Sync failed with RuntimeException: ", exc.toString());
                closeActiveSyncContext(activeSyncContext);
                Slog.e("SyncManager", "Caught RuntimeException while starting the sync " + SyncLogger.logSafe(syncOperation), exc);
            }
        }

        private void cancelActiveSyncH(SyncStorageEngine.EndPoint info, Bundle extras, String why) {
            Iterator<ActiveSyncContext> it = new ArrayList<>(SyncManager.this.mActiveSyncContexts).iterator();
            while (it.hasNext()) {
                ActiveSyncContext activeSyncContext = it.next();
                if (activeSyncContext != null && activeSyncContext.mSyncOperation.target.matchesSpec(info)) {
                    if (extras == null || SyncManager.syncExtrasEquals(activeSyncContext.mSyncOperation.extras, extras, false)) {
                        SyncJobService.callJobFinished(activeSyncContext.mSyncOperation.jobId, false, why);
                        runSyncFinishedOrCanceledH((SyncResult) null, activeSyncContext);
                    }
                }
            }
        }

        private void reschedulePeriodicSyncH(SyncOperation syncOperation) {
            SyncOperation periodicSync = null;
            Iterator<SyncOperation> it = SyncManager.this.getAllPendingSyncs().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                SyncOperation op = it.next();
                if (op.isPeriodic && syncOperation.matchesPeriodicOperation(op)) {
                    periodicSync = op;
                    break;
                }
            }
            if (periodicSync != null) {
                SyncManager.this.scheduleSyncOperationH(periodicSync);
            }
        }

        private void runSyncFinishedOrCanceledH(SyncResult syncResult, ActiveSyncContext activeSyncContext) {
            int downstreamActivity;
            int upstreamActivity;
            String historyMessage;
            int upstreamActivity2;
            int downstreamActivity2;
            SyncResult syncResult2 = syncResult;
            ActiveSyncContext activeSyncContext2 = activeSyncContext;
            boolean isLoggable = Log.isLoggable("SyncManager", 2);
            SyncOperation syncOperation = activeSyncContext2.mSyncOperation;
            SyncStorageEngine.EndPoint info = syncOperation.target;
            if (activeSyncContext2.mIsLinkedToDeath) {
                activeSyncContext2.mSyncAdapter.asBinder().unlinkToDeath(activeSyncContext2, 0);
                activeSyncContext2.mIsLinkedToDeath = false;
            }
            long elapsedTime = SystemClock.elapsedRealtime() - activeSyncContext2.mStartTime;
            SyncManager.this.mLogger.log("runSyncFinishedOrCanceledH() op=", syncOperation, " result=", syncResult2);
            if (syncResult2 != null) {
                if (isLoggable) {
                    Slog.v("SyncManager", "runSyncFinishedOrCanceled [finished]: " + syncOperation + ", result " + syncResult2);
                }
                closeActiveSyncContext(activeSyncContext2);
                if (!syncOperation.isPeriodic) {
                    SyncManager.this.cancelJob(syncOperation, "runSyncFinishedOrCanceledH()-finished");
                }
                if (!syncResult.hasError()) {
                    historyMessage = SyncStorageEngine.MESG_SUCCESS;
                    downstreamActivity2 = 0;
                    upstreamActivity2 = 0;
                    SyncManager.this.clearBackoffSetting(syncOperation.target, "sync success");
                    if (syncOperation.isDerivedFromFailedPeriodicSync()) {
                        reschedulePeriodicSyncH(syncOperation);
                    }
                } else {
                    Log.w("SyncManager", "failed sync operation " + SyncLogger.logSafe(syncOperation) + ", " + syncResult2);
                    syncOperation.retries = syncOperation.retries + 1;
                    if (syncOperation.retries > SyncManager.this.mConstants.getMaxRetriesWithAppStandbyExemption()) {
                        syncOperation.syncExemptionFlag = 0;
                    }
                    SyncManager.this.increaseBackoffSetting(syncOperation.target);
                    if (!syncOperation.isPeriodic) {
                        SyncManager.this.maybeRescheduleSync(syncResult2, syncOperation);
                    } else {
                        SyncManager.this.postScheduleSyncMessage(syncOperation.createOneTimeSyncOperation(), 0);
                    }
                    historyMessage = ContentResolver.syncErrorToString(syncResultToErrorNumber(syncResult));
                    downstreamActivity2 = 0;
                    upstreamActivity2 = 0;
                }
                SyncManager.this.setDelayUntilTime(syncOperation.target, syncResult2.delayUntil);
                downstreamActivity = downstreamActivity2;
                upstreamActivity = upstreamActivity2;
            } else {
                if (isLoggable) {
                    Slog.v("SyncManager", "runSyncFinishedOrCanceled [canceled]: " + syncOperation);
                }
                if (!syncOperation.isPeriodic) {
                    SyncManager.this.cancelJob(syncOperation, "runSyncFinishedOrCanceledH()-canceled");
                }
                if (activeSyncContext2.mSyncAdapter != null) {
                    try {
                        SyncManager.this.mLogger.log("Calling cancelSync for runSyncFinishedOrCanceled ", activeSyncContext2, "  adapter=", activeSyncContext2.mSyncAdapter);
                        activeSyncContext2.mSyncAdapter.cancelSync(activeSyncContext2);
                        SyncManager.this.mLogger.log("Canceled");
                    } catch (RemoteException e) {
                        SyncManager.this.mLogger.log("RemoteException ", Log.getStackTraceString(e));
                    }
                }
                historyMessage = SyncStorageEngine.MESG_CANCELED;
                closeActiveSyncContext(activeSyncContext2);
                downstreamActivity = 0;
                upstreamActivity = 0;
            }
            SyncStorageEngine.EndPoint info2 = info;
            stopSyncEvent(activeSyncContext2.mHistoryRowId, syncOperation, historyMessage, upstreamActivity, downstreamActivity, elapsedTime, syncResult);
            if (syncResult2 == null || !syncResult2.tooManyDeletions) {
                SyncManager.this.mNotificationMgr.cancelAsUser(Integer.toString(info2.account.hashCode() ^ info2.provider.hashCode()), 18, new UserHandle(info2.userId));
            } else {
                installHandleTooManyDeletesNotification(info2.account, info2.provider, syncResult2.stats.numDeletes, info2.userId);
            }
            if (syncResult2 == null || !syncResult2.fullSyncRequested) {
                return;
            }
            String str = historyMessage;
            SyncManager.this.scheduleSyncOperationH(new SyncOperation(info2.account, info2.userId, syncOperation.owningUid, syncOperation.owningPackage, syncOperation.reason, syncOperation.syncSource, info2.provider, new Bundle(), syncOperation.allowParallelSyncs, syncOperation.syncExemptionFlag));
        }

        private void closeActiveSyncContext(ActiveSyncContext activeSyncContext) {
            activeSyncContext.close();
            SyncManager.this.mActiveSyncContexts.remove(activeSyncContext);
            SyncManager.this.mSyncStorageEngine.removeActiveSync(activeSyncContext.mSyncInfo, activeSyncContext.mSyncOperation.target.userId);
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "removing all MESSAGE_MONITOR_SYNC & MESSAGE_SYNC_EXPIRED for " + activeSyncContext.toString());
            }
            SyncManager.this.mSyncHandler.removeMessages(8, activeSyncContext);
            SyncManager.this.mLogger.log("closeActiveSyncContext: ", activeSyncContext);
        }

        private int syncResultToErrorNumber(SyncResult syncResult) {
            if (syncResult.syncAlreadyInProgress) {
                return 1;
            }
            if (syncResult.stats.numAuthExceptions > 0) {
                return 2;
            }
            if (syncResult.stats.numIoExceptions > 0) {
                return 3;
            }
            if (syncResult.stats.numParseExceptions > 0) {
                return 4;
            }
            if (syncResult.stats.numConflictDetectedExceptions > 0) {
                return 5;
            }
            if (syncResult.tooManyDeletions) {
                return 6;
            }
            if (syncResult.tooManyRetries) {
                return 7;
            }
            if (syncResult.databaseError) {
                return 8;
            }
            throw new IllegalStateException("we are not in an error state, " + syncResult);
        }

        private void installHandleTooManyDeletesNotification(Account account, String authority, long numDeletes, int userId) {
            ProviderInfo providerInfo;
            String str = authority;
            if (SyncManager.this.mNotificationMgr != null && (providerInfo = SyncManager.this.mContext.getPackageManager().resolveContentProvider(str, 0)) != null) {
                CharSequence authorityName = providerInfo.loadLabel(SyncManager.this.mContext.getPackageManager());
                Intent clickIntent = new Intent(SyncManager.this.mContext, SyncActivityTooManyDeletes.class);
                clickIntent.putExtra("account", account);
                clickIntent.putExtra("authority", str);
                clickIntent.putExtra("provider", authorityName.toString());
                clickIntent.putExtra("numDeletes", numDeletes);
                if (!isActivityAvailable(clickIntent)) {
                    Log.w("SyncManager", "No activity found to handle too many deletes.");
                    return;
                }
                UserHandle user = new UserHandle(userId);
                PendingIntent pendingIntent = PendingIntent.getActivityAsUser(SyncManager.this.mContext, 0, clickIntent, 268435456, (Bundle) null, user);
                CharSequence tooManyDeletesDescFormat = SyncManager.this.mContext.getResources().getText(17039820);
                Context contextForUser = SyncManager.this.getContextForUser(user);
                Notification notification = new Notification.Builder(contextForUser, SystemNotificationChannels.ACCOUNT).setSmallIcon(17303568).setTicker(SyncManager.this.mContext.getString(17039818)).setWhen(System.currentTimeMillis()).setColor(contextForUser.getColor(17170460)).setContentTitle(contextForUser.getString(17039819)).setContentText(String.format(tooManyDeletesDescFormat.toString(), new Object[]{authorityName})).setContentIntent(pendingIntent).build();
                notification.flags |= 2;
                SyncManager.this.mNotificationMgr.notifyAsUser(Integer.toString(account.hashCode() ^ authority.hashCode()), 18, notification, user);
            }
        }

        private boolean isActivityAvailable(Intent intent) {
            List<ResolveInfo> list = SyncManager.this.mContext.getPackageManager().queryIntentActivities(intent, 0);
            int listSize = list.size();
            for (int i = 0; i < listSize; i++) {
                if ((list.get(i).activityInfo.applicationInfo.flags & 1) != 0) {
                    return true;
                }
            }
            return false;
        }

        public long insertStartSyncEvent(SyncOperation syncOperation) {
            long now = System.currentTimeMillis();
            EventLog.writeEvent(2720, syncOperation.toEventLog(0));
            return SyncManager.this.mSyncStorageEngine.insertStartSyncEvent(syncOperation, now);
        }

        public void stopSyncEvent(long rowId, SyncOperation syncOperation, String resultMessage, int upstreamActivity, int downstreamActivity, long elapsedTime, SyncResult syncResult) {
            SyncOperation syncOperation2 = syncOperation;
            EventLog.writeEvent(2720, syncOperation2.toEventLog(1));
            String resultMessage2 = MiSyncPolicyManager.maybeConvertToMiCanceledMsg(syncOperation, resultMessage);
            long j = rowId;
            long j2 = elapsedTime;
            String str = resultMessage2;
            SyncManager.this.mSyncStorageEngine.stopSyncEvent(j, j2, str, (long) downstreamActivity, (long) upstreamActivity, syncOperation2.target.userId, syncResult);
        }
    }

    /* access modifiers changed from: private */
    public boolean isSyncStillActiveH(ActiveSyncContext activeSyncContext) {
        Iterator<ActiveSyncContext> it = this.mActiveSyncContexts.iterator();
        while (it.hasNext()) {
            if (it.next() == activeSyncContext) {
                return true;
            }
        }
        return false;
    }

    public static boolean syncExtrasEquals(Bundle b1, Bundle b2, boolean includeSyncSettings) {
        if (b1 == b2) {
            return true;
        }
        if (includeSyncSettings && b1.size() != b2.size()) {
            return false;
        }
        Bundle bigger = b1.size() > b2.size() ? b1 : b2;
        Bundle smaller = b1.size() > b2.size() ? b2 : b1;
        for (String key : bigger.keySet()) {
            if ((includeSyncSettings || !isSyncSetting(key)) && (!smaller.containsKey(key) || !Objects.equals(bigger.get(key), smaller.get(key)))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSyncSetting(String key) {
        if (!key.equals("expedited") && !key.equals("ignore_settings") && !key.equals("ignore_backoff") && !key.equals("do_not_retry") && !key.equals("force") && !key.equals("upload") && !key.equals("deletions_override") && !key.equals("discard_deletions") && !key.equals("expected_upload") && !key.equals("expected_download") && !key.equals("sync_priority") && !key.equals("allow_metered") && !key.equals("initialize")) {
            return false;
        }
        return true;
    }

    static class PrintTable {
        private final int mCols;
        private ArrayList<String[]> mTable = Lists.newArrayList();

        PrintTable(int cols) {
            this.mCols = cols;
        }

        /* access modifiers changed from: package-private */
        public void set(int row, int col, Object... values) {
            String str;
            if (values.length + col <= this.mCols) {
                for (int i = this.mTable.size(); i <= row; i++) {
                    String[] list = new String[this.mCols];
                    this.mTable.add(list);
                    for (int j = 0; j < this.mCols; j++) {
                        list[j] = "";
                    }
                }
                String[] rowArray = this.mTable.get(row);
                for (int i2 = 0; i2 < values.length; i2++) {
                    Object value = values[i2];
                    int i3 = col + i2;
                    if (value == null) {
                        str = "";
                    } else {
                        str = value.toString();
                    }
                    rowArray[i3] = str;
                }
                return;
            }
            throw new IndexOutOfBoundsException("Table only has " + this.mCols + " columns. can't set " + values.length + " at column " + col);
        }

        /* access modifiers changed from: package-private */
        public void writeTo(PrintWriter out) {
            int i;
            String[] formats = new String[this.mCols];
            int totalLength = 0;
            int col = 0;
            while (true) {
                i = this.mCols;
                if (col >= i) {
                    break;
                }
                int maxLength = 0;
                Iterator<String[]> it = this.mTable.iterator();
                while (it.hasNext()) {
                    int length = ((Object[]) it.next())[col].toString().length();
                    if (length > maxLength) {
                        maxLength = length;
                    }
                }
                totalLength += maxLength;
                formats[col] = String.format("%%-%ds", new Object[]{Integer.valueOf(maxLength)});
                col++;
            }
            formats[i - 1] = "%s";
            printRow(out, formats, (Object[]) this.mTable.get(0));
            int totalLength2 = totalLength + ((this.mCols - 1) * 2);
            for (int i2 = 0; i2 < totalLength2; i2++) {
                out.print("-");
            }
            out.println();
            int mTableSize = this.mTable.size();
            for (int i3 = 1; i3 < mTableSize; i3++) {
                printRow(out, formats, (Object[]) this.mTable.get(i3));
            }
        }

        private void printRow(PrintWriter out, String[] formats, Object[] row) {
            int rowLength = row.length;
            for (int j = 0; j < rowLength; j++) {
                out.printf(String.format(formats[j], new Object[]{row[j].toString()}), new Object[0]);
                out.print("  ");
            }
            out.println();
        }

        public int getNumRows() {
            return this.mTable.size();
        }
    }

    /* access modifiers changed from: private */
    public Context getContextForUser(UserHandle user) {
        try {
            return this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, user);
        } catch (PackageManager.NameNotFoundException e) {
            return this.mContext;
        }
    }

    /* access modifiers changed from: private */
    public void cancelJob(SyncOperation op, String why) {
        if (op == null) {
            Slog.wtf("SyncManager", "Null sync operation detected.");
            return;
        }
        if (op.isPeriodic) {
            this.mLogger.log("Removing periodic sync ", op, " for ", why);
        }
        getJobScheduler().cancel(op.jobId);
    }

    public void resetTodayStats() {
        this.mSyncStorageEngine.resetTodayStats(true);
    }

    private boolean wasPackageEverLaunched(String packageName, int userId) {
        try {
            return this.mPackageManagerInternal.wasPackageEverLaunched(packageName, userId);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
