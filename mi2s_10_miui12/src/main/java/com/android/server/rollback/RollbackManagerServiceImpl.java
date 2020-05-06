package com.android.server.rollback;

import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.PackageParser;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.pm.VersionedPackage;
import android.content.rollback.IRollbackManager;
import android.content.rollback.PackageRollbackInfo;
import android.content.rollback.RollbackInfo;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseLongArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.LocalServices;
import com.android.server.Watchdog;
import com.android.server.pm.DumpState;
import com.android.server.pm.Installer;
import com.android.server.pm.Settings;
import com.android.server.rollback.RollbackManagerServiceImpl;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

class RollbackManagerServiceImpl extends IRollbackManager.Stub {
    private static final long DEFAULT_ROLLBACK_LIFETIME_DURATION_MILLIS = TimeUnit.DAYS.toMillis(14);
    private static final long HANDLER_THREAD_TIMEOUT_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(10);
    private static final String TAG = "RollbackManager";
    @GuardedBy({"mLock"})
    private final SparseBooleanArray mAllocatedRollbackIds = new SparseBooleanArray();
    private final AppDataRollbackHelper mAppDataRollbackHelper;
    private final Context mContext;
    private final HandlerThread mHandlerThread;
    private final Installer mInstaller;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final Set<NewRollback> mNewRollbacks = new ArraySet();
    private final RollbackPackageHealthObserver mPackageHealthObserver;
    private final Random mRandom = new SecureRandom();
    /* access modifiers changed from: private */
    public long mRelativeBootTime = calculateRelativeBootTime();
    private long mRollbackLifetimeDurationInMillis = DEFAULT_ROLLBACK_LIFETIME_DURATION_MILLIS;
    private final RollbackStore mRollbackStore;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public List<RollbackData> mRollbacks;

    RollbackManagerServiceImpl(Context context) {
        this.mContext = context;
        this.mInstaller = new Installer(this.mContext);
        this.mInstaller.onStart();
        this.mHandlerThread = new HandlerThread("RollbackManagerServiceHandler");
        this.mHandlerThread.start();
        Watchdog.getInstance().addThread(getHandler(), HANDLER_THREAD_TIMEOUT_DURATION_MILLIS);
        this.mRollbackStore = new RollbackStore(new File(Environment.getDataDirectory(), "rollback"));
        this.mPackageHealthObserver = new RollbackPackageHealthObserver(this.mContext);
        this.mAppDataRollbackHelper = new AppDataRollbackHelper(this.mInstaller);
        getHandler().post(new Runnable() {
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$new$0$RollbackManagerServiceImpl();
            }
        });
        new SessionCallback();
        for (UserInfo userInfo : UserManager.get(this.mContext).getUsers(true)) {
            registerUserCallbacks(userInfo.getUserHandle());
        }
        IntentFilter enableRollbackFilter = new IntentFilter();
        enableRollbackFilter.addAction("android.intent.action.PACKAGE_ENABLE_ROLLBACK");
        try {
            enableRollbackFilter.addDataType("application/vnd.android.package-archive");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e(TAG, "addDataType", e);
        }
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.PACKAGE_ENABLE_ROLLBACK".equals(intent.getAction())) {
                    int token = intent.getIntExtra("android.content.pm.extra.ENABLE_ROLLBACK_TOKEN", -1);
                    int installFlags = intent.getIntExtra("android.content.pm.extra.ENABLE_ROLLBACK_INSTALL_FLAGS", 0);
                    int[] installedUsers = intent.getIntArrayExtra("android.content.pm.extra.ENABLE_ROLLBACK_INSTALLED_USERS");
                    int user = intent.getIntExtra("android.content.pm.extra.ENABLE_ROLLBACK_USER", 0);
                    RollbackManagerServiceImpl.this.getHandler().post(new Runnable(installFlags, new File(intent.getData().getPath()), installedUsers, user, token) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ File f$2;
                        private final /* synthetic */ int[] f$3;
                        private final /* synthetic */ int f$4;
                        private final /* synthetic */ int f$5;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                        }

                        public final void run() {
                            RollbackManagerServiceImpl.AnonymousClass1.this.lambda$onReceive$0$RollbackManagerServiceImpl$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                        }
                    });
                    abortBroadcast();
                }
            }

            public /* synthetic */ void lambda$onReceive$0$RollbackManagerServiceImpl$1(int installFlags, File newPackageCodePath, int[] installedUsers, int user, int token) {
                int ret = 1;
                if (!RollbackManagerServiceImpl.this.enableRollback(installFlags, newPackageCodePath, installedUsers, user, token)) {
                    ret = -1;
                }
                ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).setEnableRollbackCode(token, ret);
            }
        }, enableRollbackFilter, (String) null, getHandler());
        IntentFilter enableRollbackTimedOutFilter = new IntentFilter();
        enableRollbackTimedOutFilter.addAction("android.intent.action.CANCEL_ENABLE_ROLLBACK");
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.CANCEL_ENABLE_ROLLBACK".equals(intent.getAction())) {
                    int token = intent.getIntExtra("android.content.pm.extra.ENABLE_ROLLBACK_TOKEN", -1);
                    synchronized (RollbackManagerServiceImpl.this.mLock) {
                        for (NewRollback rollback : RollbackManagerServiceImpl.this.mNewRollbacks) {
                            if (rollback.hasToken(token)) {
                                rollback.isCancelled = true;
                                return;
                            }
                        }
                    }
                }
            }
        }, enableRollbackTimedOutFilter, (String) null, getHandler());
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int newUserId;
                if ("android.intent.action.USER_ADDED".equals(intent.getAction()) && (newUserId = intent.getIntExtra("android.intent.extra.user_handle", -1)) != -1) {
                    RollbackManagerServiceImpl.this.registerUserCallbacks(UserHandle.of(newUserId));
                }
            }
        }, new IntentFilter("android.intent.action.USER_ADDED"), (String) null, getHandler());
        registerTimeChangeReceiver();
    }

    /* access modifiers changed from: private */
    public void registerUserCallbacks(UserHandle user) {
        Context context = getContextAsUser(user);
        if (context == null) {
            Log.e(TAG, "Unable to register user callbacks for user " + user);
            return;
        }
        context.getPackageManager().getPackageInstaller().registerSessionCallback(new SessionCallback(), getHandler());
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
        filter.addDataScheme(Settings.ATTR_PACKAGE);
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.intent.action.PACKAGE_REPLACED".equals(action)) {
                    RollbackManagerServiceImpl.this.onPackageReplaced(intent.getData().getSchemeSpecificPart());
                }
                if ("android.intent.action.PACKAGE_FULLY_REMOVED".equals(action)) {
                    RollbackManagerServiceImpl.this.onPackageFullyRemoved(intent.getData().getSchemeSpecificPart());
                }
            }
        }, filter, (String) null, getHandler());
    }

    public ParceledListSlice getAvailableRollbacks() {
        ParceledListSlice parceledListSlice;
        enforceManageRollbacks("getAvailableRollbacks");
        if (!Thread.currentThread().equals(this.mHandlerThread)) {
            LinkedBlockingQueue<Boolean> result = new LinkedBlockingQueue<>();
            getHandler().post(new Runnable(result) {
                private final /* synthetic */ LinkedBlockingQueue f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    this.f$0.offer(true);
                }
            });
            try {
                result.take();
            } catch (InterruptedException e) {
                Log.w(TAG, "Interrupted while waiting for handler thread in getAvailableRollbacks");
            }
            synchronized (this.mLock) {
                ensureRollbackDataLoadedLocked();
                List<RollbackInfo> rollbacks = new ArrayList<>();
                for (int i = 0; i < this.mRollbacks.size(); i++) {
                    RollbackData data = this.mRollbacks.get(i);
                    if (data.state == 1) {
                        rollbacks.add(data.info);
                    }
                }
                parceledListSlice = new ParceledListSlice(rollbacks);
            }
            return parceledListSlice;
        }
        Log.wtf(TAG, "Calling getAvailableRollbacks from mHandlerThread causes a deadlock");
        throw new IllegalStateException("Cannot call RollbackManager#getAvailableRollbacks from the handler thread!");
    }

    public ParceledListSlice<RollbackInfo> getRecentlyExecutedRollbacks() {
        ParceledListSlice<RollbackInfo> parceledListSlice;
        enforceManageRollbacks("getRecentlyCommittedRollbacks");
        synchronized (this.mLock) {
            ensureRollbackDataLoadedLocked();
            List<RollbackInfo> rollbacks = new ArrayList<>();
            for (int i = 0; i < this.mRollbacks.size(); i++) {
                RollbackData data = this.mRollbacks.get(i);
                if (data.state == 3) {
                    rollbacks.add(data.info);
                }
            }
            parceledListSlice = new ParceledListSlice<>(rollbacks);
        }
        return parceledListSlice;
    }

    public void commitRollback(int rollbackId, ParceledListSlice causePackages, String callerPackageName, IntentSender statusReceiver) {
        enforceManageRollbacks("executeRollback");
        ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).checkPackage(Binder.getCallingUid(), callerPackageName);
        getHandler().post(new Runnable(rollbackId, causePackages, callerPackageName, statusReceiver) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ ParceledListSlice f$2;
            private final /* synthetic */ String f$3;
            private final /* synthetic */ IntentSender f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                RollbackManagerServiceImpl.this.lambda$commitRollback$2$RollbackManagerServiceImpl(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$commitRollback$2$RollbackManagerServiceImpl(int rollbackId, ParceledListSlice causePackages, String callerPackageName, IntentSender statusReceiver) {
        commitRollbackInternal(rollbackId, causePackages.getList(), callerPackageName, statusReceiver);
    }

    private void registerTimeChangeReceiver() {
        BroadcastReceiver timeChangeIntentReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                long oldRelativeBootTime = RollbackManagerServiceImpl.this.mRelativeBootTime;
                long unused = RollbackManagerServiceImpl.this.mRelativeBootTime = RollbackManagerServiceImpl.calculateRelativeBootTime();
                long timeDifference = RollbackManagerServiceImpl.this.mRelativeBootTime - oldRelativeBootTime;
                synchronized (RollbackManagerServiceImpl.this.mLock) {
                    RollbackManagerServiceImpl.this.ensureRollbackDataLoadedLocked();
                    for (RollbackData data : RollbackManagerServiceImpl.this.mRollbacks) {
                        data.timestamp = data.timestamp.plusMillis(timeDifference);
                        RollbackManagerServiceImpl.this.saveRollbackData(data);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_SET");
        this.mContext.registerReceiver(timeChangeIntentReceiver, filter, (String) null, getHandler());
    }

    /* access modifiers changed from: private */
    public static long calculateRelativeBootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }

    /* Debug info: failed to restart local var, previous not found, register: 28 */
    private void commitRollbackInternal(int rollbackId, List<VersionedPackage> causePackages, String callerPackageName, IntentSender statusReceiver) {
        File packageCodePath;
        Throwable th;
        long token;
        String installerPackageName;
        IntentSender intentSender = statusReceiver;
        Log.i(TAG, "Initiating rollback");
        RollbackData data = getRollbackForId(rollbackId);
        if (data != null) {
            boolean z = true;
            if (data.state == 1) {
                try {
                    int i = 0;
                    PackageManager pm = this.mContext.createPackageContext(callerPackageName, 0).getPackageManager();
                    try {
                        PackageInstaller packageInstaller = pm.getPackageInstaller();
                        PackageInstaller.SessionParams parentParams = new PackageInstaller.SessionParams(1);
                        parentParams.setRequestDowngrade(true);
                        parentParams.setMultiPackage();
                        if (data.isStaged()) {
                            parentParams.setStaged();
                        }
                        int parentSessionId = packageInstaller.createSession(parentParams);
                        PackageInstaller.Session parentSession = packageInstaller.openSession(parentSessionId);
                        Iterator it = data.info.getPackages().iterator();
                        while (it.hasNext()) {
                            PackageRollbackInfo info = (PackageRollbackInfo) it.next();
                            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(z ? 1 : 0);
                            if (!info.isApex() && (installerPackageName = pm.getInstallerPackageName(info.getPackageName())) != null) {
                                params.setInstallerPackageName(installerPackageName);
                            }
                            params.setRequestDowngrade(z);
                            params.setRequiredInstalledVersionCode(info.getVersionRolledBackFrom().getLongVersionCode());
                            if (data.isStaged()) {
                                params.setStaged();
                            }
                            if (info.isApex()) {
                                params.setInstallAsApex();
                            }
                            int sessionId = packageInstaller.createSession(params);
                            PackageInstaller.Session session = packageInstaller.openSession(sessionId);
                            File[] packageCodePaths = RollbackStore.getPackageCodePaths(data, info.getPackageName());
                            if (packageCodePaths == null) {
                                sendFailure(intentSender, 1, "Backup copy of package inaccessible");
                                return;
                            }
                            int length = packageCodePaths.length;
                            while (i < length) {
                                Iterator it2 = it;
                                PackageRollbackInfo info2 = info;
                                packageCodePath = packageCodePaths[i];
                                ParcelFileDescriptor fd = ParcelFileDescriptor.open(packageCodePath, 268435456);
                                try {
                                    token = Binder.clearCallingIdentity();
                                    session.write(packageCodePath.getName(), 0, packageCodePath.length(), fd);
                                    Binder.restoreCallingIdentity(token);
                                    if (fd != null) {
                                        fd.close();
                                    }
                                    i++;
                                    it = it2;
                                    info = info2;
                                } catch (Throwable th2) {
                                    th = th2;
                                    try {
                                        throw th;
                                    } catch (Throwable th3) {
                                        Throwable th4 = th3;
                                        if (fd != null) {
                                            fd.close();
                                            File file = packageCodePath;
                                        }
                                        throw th4;
                                    }
                                }
                            }
                            PackageRollbackInfo packageRollbackInfo = info;
                            parentSession.addChildSessionId(sessionId);
                            String str = callerPackageName;
                            it = it;
                            i = 0;
                            z = true;
                        }
                        PackageInstaller.Session parentSession2 = parentSession;
                        int i2 = parentSessionId;
                        LocalIntentReceiver receiver = new LocalIntentReceiver(new Consumer(data, statusReceiver, parentSessionId, causePackages) {
                            private final /* synthetic */ RollbackData f$1;
                            private final /* synthetic */ IntentSender f$2;
                            private final /* synthetic */ int f$3;
                            private final /* synthetic */ List f$4;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                            }

                            public final void accept(Object obj) {
                                RollbackManagerServiceImpl.this.lambda$commitRollbackInternal$4$RollbackManagerServiceImpl(this.f$1, this.f$2, this.f$3, this.f$4, (Intent) obj);
                            }
                        });
                        synchronized (this.mLock) {
                            data.state = 3;
                            data.restoreUserDataInProgress = true;
                        }
                        parentSession2.commit(receiver.getIntentSender());
                        return;
                    } catch (IOException e) {
                        Log.e(TAG, "Rollback failed", e);
                        sendFailure(intentSender, 1, "IOException: " + e.toString());
                        return;
                    } catch (Throwable th5) {
                        File file2 = packageCodePath;
                        th.addSuppressed(th5);
                    }
                } catch (PackageManager.NameNotFoundException e2) {
                    sendFailure(intentSender, 1, "Invalid callerPackageName");
                    return;
                }
            }
        }
        sendFailure(intentSender, 2, "Rollback unavailable");
    }

    public /* synthetic */ void lambda$commitRollbackInternal$4$RollbackManagerServiceImpl(RollbackData data, IntentSender statusReceiver, int parentSessionId, List causePackages, Intent result) {
        getHandler().post(new Runnable(result, data, statusReceiver, parentSessionId, causePackages) {
            private final /* synthetic */ Intent f$1;
            private final /* synthetic */ RollbackData f$2;
            private final /* synthetic */ IntentSender f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ List f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                RollbackManagerServiceImpl.this.lambda$commitRollbackInternal$3$RollbackManagerServiceImpl(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$commitRollbackInternal$3$RollbackManagerServiceImpl(Intent result, RollbackData data, IntentSender statusReceiver, int parentSessionId, List causePackages) {
        if (result.getIntExtra("android.content.pm.extra.STATUS", 1) != 0) {
            synchronized (this.mLock) {
                data.state = 1;
                data.restoreUserDataInProgress = false;
            }
            sendFailure(statusReceiver, 3, "Rollback downgrade install failed: " + result.getStringExtra("android.content.pm.extra.STATUS_MESSAGE"));
            return;
        }
        synchronized (this.mLock) {
            if (!data.isStaged()) {
                data.restoreUserDataInProgress = false;
            }
            data.info.setCommittedSessionId(parentSessionId);
            data.info.getCausePackages().addAll(causePackages);
        }
        RollbackStore rollbackStore = this.mRollbackStore;
        RollbackStore.deletePackageCodePaths(data);
        saveRollbackData(data);
        sendSuccess(statusReceiver);
        this.mContext.sendBroadcastAsUser(new Intent("android.intent.action.ROLLBACK_COMMITTED"), UserHandle.SYSTEM, "android.permission.MANAGE_ROLLBACKS");
    }

    public void reloadPersistedData() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.TEST_MANAGE_ROLLBACKS", "reloadPersistedData");
        synchronized (this.mLock) {
            this.mRollbacks = null;
        }
        getHandler().post(new Runnable() {
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$reloadPersistedData$5$RollbackManagerServiceImpl();
            }
        });
    }

    public /* synthetic */ void lambda$reloadPersistedData$5$RollbackManagerServiceImpl() {
        lambda$onBootCompleted$7$RollbackManagerServiceImpl();
        lambda$new$0$RollbackManagerServiceImpl();
    }

    public void expireRollbackForPackage(String packageName) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.TEST_MANAGE_ROLLBACKS", "expireRollbackForPackage");
        synchronized (this.mLock) {
            ensureRollbackDataLoadedLocked();
            Iterator<RollbackData> iter = this.mRollbacks.iterator();
            while (iter.hasNext()) {
                RollbackData data = iter.next();
                Iterator it = data.info.getPackages().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    } else if (((PackageRollbackInfo) it.next()).getPackageName().equals(packageName)) {
                        iter.remove();
                        deleteRollback(data);
                        break;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onUnlockUser(int userId) {
        getHandler().post(new Runnable(userId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RollbackManagerServiceImpl.this.lambda$onUnlockUser$6$RollbackManagerServiceImpl(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onUnlockUser$6$RollbackManagerServiceImpl(int userId) {
        List<RollbackData> rollbacks;
        synchronized (this.mLock) {
            rollbacks = new ArrayList<>(this.mRollbacks);
        }
        for (RollbackData rd : this.mAppDataRollbackHelper.commitPendingBackupAndRestoreForUser(userId, rollbacks)) {
            saveRollbackData(rd);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: updateRollbackLifetimeDurationInMillis */
    public void lambda$onBootCompleted$7$RollbackManagerServiceImpl() {
        this.mRollbackLifetimeDurationInMillis = DeviceConfig.getLong("rollback_boot", "rollback_lifetime_in_millis", DEFAULT_ROLLBACK_LIFETIME_DURATION_MILLIS);
        if (this.mRollbackLifetimeDurationInMillis < 0) {
            this.mRollbackLifetimeDurationInMillis = DEFAULT_ROLLBACK_LIFETIME_DURATION_MILLIS;
        }
    }

    /* access modifiers changed from: package-private */
    public void onBootCompleted() {
        getHandler().post(new Runnable() {
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$onBootCompleted$7$RollbackManagerServiceImpl();
            }
        });
        scheduleExpiration(0);
        getHandler().post(new Runnable() {
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$onBootCompleted$8$RollbackManagerServiceImpl();
            }
        });
    }

    public /* synthetic */ void lambda$onBootCompleted$8$RollbackManagerServiceImpl() {
        List<RollbackData> enabling = new ArrayList<>();
        List<RollbackData> restoreInProgress = new ArrayList<>();
        Set<String> apexPackageNames = new HashSet<>();
        synchronized (this.mLock) {
            ensureRollbackDataLoadedLocked();
            for (RollbackData data : this.mRollbacks) {
                if (data.isStaged()) {
                    if (data.state == 0) {
                        enabling.add(data);
                    } else if (data.restoreUserDataInProgress) {
                        restoreInProgress.add(data);
                    }
                    for (PackageRollbackInfo info : data.info.getPackages()) {
                        if (info.isApex()) {
                            apexPackageNames.add(info.getPackageName());
                        }
                    }
                }
            }
        }
        for (RollbackData data2 : enabling) {
            PackageInstaller.SessionInfo session = this.mContext.getPackageManager().getPackageInstaller().getSessionInfo(data2.stagedSessionId);
            if (session != null) {
                if (session.isStagedSessionApplied()) {
                    makeRollbackAvailable(data2);
                } else if (session.isStagedSessionFailed()) {
                    deleteRollback(data2);
                }
            }
        }
        for (RollbackData data3 : restoreInProgress) {
            PackageInstaller.SessionInfo session2 = this.mContext.getPackageManager().getPackageInstaller().getSessionInfo(data3.stagedSessionId);
            if (session2 != null && (session2.isStagedSessionApplied() || session2.isStagedSessionFailed())) {
                synchronized (this.mLock) {
                    data3.restoreUserDataInProgress = false;
                }
                saveRollbackData(data3);
            }
        }
        for (String apexPackageName : apexPackageNames) {
            onPackageReplaced(apexPackageName);
        }
        this.mPackageHealthObserver.onBootCompletedAsync();
    }

    /* access modifiers changed from: private */
    /* renamed from: ensureRollbackDataLoaded */
    public void lambda$new$0$RollbackManagerServiceImpl() {
        synchronized (this.mLock) {
            ensureRollbackDataLoadedLocked();
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void ensureRollbackDataLoadedLocked() {
        if (this.mRollbacks == null) {
            loadAllRollbackDataLocked();
        }
    }

    @GuardedBy({"mLock"})
    private void loadAllRollbackDataLocked() {
        this.mRollbacks = this.mRollbackStore.loadAllRollbackData();
        for (RollbackData data : this.mRollbacks) {
            this.mAllocatedRollbackIds.put(data.info.getRollbackId(), true);
        }
    }

    /* access modifiers changed from: private */
    public void onPackageReplaced(String packageName) {
        VersionedPackage installedVersion = getInstalledPackageVersion(packageName);
        synchronized (this.mLock) {
            ensureRollbackDataLoadedLocked();
            Iterator<RollbackData> iter = this.mRollbacks.iterator();
            while (iter.hasNext()) {
                RollbackData data = iter.next();
                if (data.state == 1 || data.state == 0) {
                    Iterator it = data.info.getPackages().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        PackageRollbackInfo info = (PackageRollbackInfo) it.next();
                        if (info.getPackageName().equals(packageName) && !packageVersionsEqual(info.getVersionRolledBackFrom(), installedVersion)) {
                            iter.remove();
                            deleteRollback(data);
                            break;
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void onPackageFullyRemoved(String packageName) {
        expireRollbackForPackage(packageName);
    }

    private void sendFailure(IntentSender statusReceiver, int status, String message) {
        Log.e(TAG, message);
        try {
            Intent fillIn = new Intent();
            fillIn.putExtra("android.content.rollback.extra.STATUS", status);
            fillIn.putExtra("android.content.rollback.extra.STATUS_MESSAGE", message);
            statusReceiver.sendIntent(this.mContext, 0, fillIn, (IntentSender.OnFinished) null, (Handler) null);
        } catch (IntentSender.SendIntentException e) {
        }
    }

    private void sendSuccess(IntentSender statusReceiver) {
        try {
            Intent fillIn = new Intent();
            fillIn.putExtra("android.content.rollback.extra.STATUS", 0);
            statusReceiver.sendIntent(this.mContext, 0, fillIn, (IntentSender.OnFinished) null, (Handler) null);
        } catch (IntentSender.SendIntentException e) {
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: runExpiration */
    public void lambda$scheduleExpiration$9$RollbackManagerServiceImpl() {
        Instant now = Instant.now();
        Instant oldest = null;
        synchronized (this.mLock) {
            ensureRollbackDataLoadedLocked();
            Iterator<RollbackData> iter = this.mRollbacks.iterator();
            while (iter.hasNext()) {
                RollbackData data = iter.next();
                if (data.state == 1) {
                    if (!now.isBefore(data.timestamp.plusMillis(this.mRollbackLifetimeDurationInMillis))) {
                        iter.remove();
                        deleteRollback(data);
                    } else if (oldest == null || oldest.isAfter(data.timestamp)) {
                        oldest = data.timestamp;
                    }
                }
            }
        }
        if (oldest != null) {
            scheduleExpiration(now.until(oldest.plusMillis(this.mRollbackLifetimeDurationInMillis), ChronoUnit.MILLIS));
        }
    }

    private void scheduleExpiration(long duration) {
        getHandler().postDelayed(new Runnable() {
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$scheduleExpiration$9$RollbackManagerServiceImpl();
            }
        }, duration);
    }

    /* access modifiers changed from: private */
    public Handler getHandler() {
        return this.mHandlerThread.getThreadHandler();
    }

    private boolean sessionMatchesForEnableRollback(PackageInstaller.SessionInfo session, int installFlags, File newPackageCodePath) {
        if (session == null || session.resolvedBaseCodePath == null || !newPackageCodePath.equals(new File(session.resolvedBaseCodePath).getParentFile()) || installFlags != session.installFlags) {
            return false;
        }
        return true;
    }

    private Context getContextAsUser(UserHandle user) {
        try {
            return this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, user);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0113, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x011c, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean enableRollback(int r17, java.io.File r18, int[] r19, int r20, int r21) {
        /*
            r16 = this;
            r1 = r16
            r2 = r17
            r3 = r18
            android.os.UserHandle r0 = android.os.UserHandle.of(r20)
            android.content.Context r4 = r1.getContextAsUser(r0)
            r5 = 0
            if (r4 != 0) goto L_0x0019
            java.lang.String r0 = "RollbackManager"
            java.lang.String r6 = "Unable to create context for install session user."
            android.util.Log.e(r0, r6)
            return r5
        L_0x0019:
            r0 = 0
            r6 = 0
            android.content.pm.PackageManager r7 = r4.getPackageManager()
            android.content.pm.PackageInstaller r7 = r7.getPackageInstaller()
            java.util.List r8 = r7.getAllSessions()
            java.util.Iterator r8 = r8.iterator()
        L_0x002b:
            boolean r9 = r8.hasNext()
            if (r9 == 0) goto L_0x0064
            java.lang.Object r9 = r8.next()
            android.content.pm.PackageInstaller$SessionInfo r9 = (android.content.pm.PackageInstaller.SessionInfo) r9
            boolean r10 = r9.isMultiPackage()
            if (r10 == 0) goto L_0x0058
            int[] r10 = r9.getChildSessionIds()
            int r11 = r10.length
            r12 = r5
        L_0x0043:
            if (r12 >= r11) goto L_0x0057
            r13 = r10[r12]
            android.content.pm.PackageInstaller$SessionInfo r14 = r7.getSessionInfo(r13)
            boolean r15 = r1.sessionMatchesForEnableRollback(r14, r2, r3)
            if (r15 == 0) goto L_0x0054
            r0 = r9
            r6 = r14
            goto L_0x0057
        L_0x0054:
            int r12 = r12 + 1
            goto L_0x0043
        L_0x0057:
            goto L_0x0063
        L_0x0058:
            boolean r10 = r1.sessionMatchesForEnableRollback(r9, r2, r3)
            if (r10 == 0) goto L_0x0063
            r0 = r9
            r6 = r9
            r8 = r6
            r6 = r0
            goto L_0x0066
        L_0x0063:
            goto L_0x002b
        L_0x0064:
            r8 = r6
            r6 = r0
        L_0x0066:
            if (r6 == 0) goto L_0x011e
            if (r8 != 0) goto L_0x0070
            r12 = r19
            r11 = r21
            goto L_0x0122
        L_0x0070:
            r9 = 0
            java.lang.Object r10 = r1.mLock
            monitor-enter(r10)
            r16.ensureRollbackDataLoadedLocked()     // Catch:{ all -> 0x0115 }
            r0 = r5
        L_0x0078:
            java.util.List<com.android.server.rollback.RollbackData> r11 = r1.mRollbacks     // Catch:{ all -> 0x0115 }
            int r11 = r11.size()     // Catch:{ all -> 0x0115 }
            if (r0 >= r11) goto L_0x0095
            java.util.List<com.android.server.rollback.RollbackData> r11 = r1.mRollbacks     // Catch:{ all -> 0x0115 }
            java.lang.Object r11 = r11.get(r0)     // Catch:{ all -> 0x0115 }
            com.android.server.rollback.RollbackData r11 = (com.android.server.rollback.RollbackData) r11     // Catch:{ all -> 0x0115 }
            int r12 = r11.apkSessionId     // Catch:{ all -> 0x0115 }
            int r13 = r6.getSessionId()     // Catch:{ all -> 0x0115 }
            if (r12 != r13) goto L_0x0092
            r9 = r11
            goto L_0x0095
        L_0x0092:
            int r0 = r0 + 1
            goto L_0x0078
        L_0x0095:
            monitor-exit(r10)     // Catch:{ all -> 0x0115 }
            if (r9 == 0) goto L_0x00e6
            r10 = 0
            java.io.File r0 = new java.io.File     // Catch:{ PackageParserException -> 0x00dd }
            java.lang.String r11 = r8.resolvedBaseCodePath     // Catch:{ PackageParserException -> 0x00dd }
            r0.<init>(r11)     // Catch:{ PackageParserException -> 0x00dd }
            android.content.pm.PackageParser$PackageLite r0 = android.content.pm.PackageParser.parsePackageLite(r0, r5)     // Catch:{ PackageParserException -> 0x00dd }
            java.lang.String r10 = r0.packageName
            android.content.rollback.RollbackInfo r11 = r9.info
            java.util.List r11 = r11.getPackages()
            java.util.Iterator r11 = r11.iterator()
        L_0x00b1:
            boolean r12 = r11.hasNext()
            if (r12 == 0) goto L_0x00d5
            java.lang.Object r12 = r11.next()
            android.content.rollback.PackageRollbackInfo r12 = (android.content.rollback.PackageRollbackInfo) r12
            java.lang.String r13 = r12.getPackageName()
            boolean r13 = r13.equals(r10)
            if (r13 == 0) goto L_0x00d4
            android.util.IntArray r5 = r12.getInstalledUsers()
            android.util.IntArray r11 = android.util.IntArray.wrap(r19)
            r5.addAll(r11)
            r5 = 1
            return r5
        L_0x00d4:
            goto L_0x00b1
        L_0x00d5:
            java.lang.String r11 = "RollbackManager"
            java.lang.String r12 = "Unable to find package in apk session"
            android.util.Log.e(r11, r12)
            return r5
        L_0x00dd:
            r0 = move-exception
            java.lang.String r11 = "RollbackManager"
            java.lang.String r12 = "Unable to parse new package"
            android.util.Log.e(r11, r12, r0)
            return r5
        L_0x00e6:
            java.lang.Object r5 = r1.mLock
            monitor-enter(r5)
            int r0 = r8.getSessionId()     // Catch:{ all -> 0x010c }
            com.android.server.rollback.RollbackManagerServiceImpl$NewRollback r0 = r1.getNewRollbackForPackageSessionLocked(r0)     // Catch:{ all -> 0x010c }
            if (r0 != 0) goto L_0x00fd
            com.android.server.rollback.RollbackManagerServiceImpl$NewRollback r10 = r1.createNewRollbackLocked(r6)     // Catch:{ all -> 0x010c }
            r0 = r10
            java.util.Set<com.android.server.rollback.RollbackManagerServiceImpl$NewRollback> r10 = r1.mNewRollbacks     // Catch:{ all -> 0x010c }
            r10.add(r0)     // Catch:{ all -> 0x010c }
        L_0x00fd:
            monitor-exit(r5)     // Catch:{ all -> 0x010c }
            r11 = r21
            r0.addToken(r11)
            com.android.server.rollback.RollbackData r5 = r0.data
            r12 = r19
            boolean r5 = r1.enableRollbackForPackageSession(r5, r8, r12)
            return r5
        L_0x010c:
            r0 = move-exception
            r12 = r19
            r11 = r21
        L_0x0111:
            monitor-exit(r5)     // Catch:{ all -> 0x0113 }
            throw r0
        L_0x0113:
            r0 = move-exception
            goto L_0x0111
        L_0x0115:
            r0 = move-exception
            r12 = r19
            r11 = r21
        L_0x011a:
            monitor-exit(r10)     // Catch:{ all -> 0x011c }
            throw r0
        L_0x011c:
            r0 = move-exception
            goto L_0x011a
        L_0x011e:
            r12 = r19
            r11 = r21
        L_0x0122:
            java.lang.String r0 = "RollbackManager"
            java.lang.String r9 = "Unable to find session for enabled rollback."
            android.util.Log.e(r0, r9)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.rollback.RollbackManagerServiceImpl.enableRollback(int, java.io.File, int[], int, int):boolean");
    }

    private boolean enableRollbackForPackageSession(RollbackData data, PackageInstaller.SessionInfo session, int[] installedUsers) {
        RollbackData rollbackData = data;
        PackageInstaller.SessionInfo sessionInfo = session;
        int installFlags = sessionInfo.installFlags;
        if ((262144 & installFlags) == 0) {
            Log.e(TAG, "Rollback is not enabled.");
            return false;
        } else if ((installFlags & 2048) != 0) {
            Log.e(TAG, "Rollbacks not supported for instant app install");
            return false;
        } else if (sessionInfo.resolvedBaseCodePath == null) {
            Log.e(TAG, "Session code path has not been resolved.");
            return false;
        } else {
            try {
                PackageParser.PackageLite newPackage = PackageParser.parsePackageLite(new File(sessionInfo.resolvedBaseCodePath), 0);
                String packageName = newPackage.packageName;
                Log.i(TAG, "Enabling rollback for install of " + packageName + ", session:" + sessionInfo.sessionId);
                String installerPackageName = session.getInstallerPackageName();
                if (!enableRollbackAllowed(installerPackageName, packageName)) {
                    Log.e(TAG, "Installer " + installerPackageName + " is not allowed to enable rollback on " + packageName);
                    return false;
                }
                VersionedPackage newVersion = new VersionedPackage(packageName, newPackage.versionCode);
                boolean isApex = (131072 & installFlags) != 0;
                PackageManager packageManager = this.mContext.getPackageManager();
                try {
                    PackageInfo pkgInfo = getPackageInfo(packageName);
                    PackageParser.PackageLite packageLite = newPackage;
                    PackageInfo pkgInfo2 = pkgInfo;
                    PackageRollbackInfo packageRollbackInfo = new PackageRollbackInfo(newVersion, new VersionedPackage(packageName, pkgInfo.getLongVersionCode()), new IntArray(), new ArrayList(), isApex, IntArray.wrap(installedUsers), new SparseLongArray());
                    try {
                        ApplicationInfo appInfo = pkgInfo2.applicationInfo;
                        RollbackStore.backupPackageCodePath(rollbackData, packageName, appInfo.sourceDir);
                        if (!ArrayUtils.isEmpty(appInfo.splitSourceDirs)) {
                            for (String sourceDir : appInfo.splitSourceDirs) {
                                RollbackStore.backupPackageCodePath(rollbackData, packageName, sourceDir);
                            }
                        }
                        synchronized (this.mLock) {
                            rollbackData.info.getPackages().add(packageRollbackInfo);
                        }
                        return true;
                    } catch (IOException e) {
                        Log.e(TAG, "Unable to copy package for rollback for " + packageName, e);
                        return false;
                    }
                } catch (PackageManager.NameNotFoundException e2) {
                    PackageParser.PackageLite packageLite2 = newPackage;
                    PackageManager.NameNotFoundException nameNotFoundException = e2;
                    Log.e(TAG, packageName + " is not installed");
                    return false;
                }
            } catch (PackageParser.PackageParserException e3) {
                Log.e(TAG, "Unable to parse new package", e3);
                return false;
            }
        }
    }

    public void snapshotAndRestoreUserData(String packageName, int[] userIds, int appId, long ceDataInode, String seInfo, int token) {
        if (Binder.getCallingUid() == 1000) {
            getHandler().post(new Runnable(packageName, userIds, appId, ceDataInode, seInfo, token) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ int[] f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ long f$4;
                private final /* synthetic */ String f$5;
                private final /* synthetic */ int f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r7;
                    this.f$6 = r8;
                }

                public final void run() {
                    RollbackManagerServiceImpl.this.lambda$snapshotAndRestoreUserData$10$RollbackManagerServiceImpl(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            });
            return;
        }
        throw new SecurityException("snapshotAndRestoreUserData may only be called by the system.");
    }

    public /* synthetic */ void lambda$snapshotAndRestoreUserData$10$RollbackManagerServiceImpl(String packageName, int[] userIds, int appId, long ceDataInode, String seInfo, int token) {
        snapshotUserDataInternal(packageName);
        restoreUserDataInternal(packageName, userIds, appId, ceDataInode, seInfo, token);
        ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).finishPackageInstall(token, false);
    }

    private void snapshotUserDataInternal(String packageName) {
        synchronized (this.mLock) {
            ensureRollbackDataLoadedLocked();
            for (int i = 0; i < this.mRollbacks.size(); i++) {
                RollbackData data = this.mRollbacks.get(i);
                if (data.state == 0) {
                    Iterator it = data.info.getPackages().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        PackageRollbackInfo info = (PackageRollbackInfo) it.next();
                        if (info.getPackageName().equals(packageName)) {
                            this.mAppDataRollbackHelper.snapshotAppData(data.info.getRollbackId(), info);
                            saveRollbackData(data);
                            break;
                        }
                    }
                }
            }
            for (NewRollback rollback : this.mNewRollbacks) {
                PackageRollbackInfo info2 = getPackageRollbackInfo(rollback.data, packageName);
                if (info2 != null) {
                    this.mAppDataRollbackHelper.snapshotAppData(rollback.data.info.getRollbackId(), info2);
                    saveRollbackData(rollback.data);
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0034, code lost:
        if (r4 != null) goto L_0x0037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0036, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0037, code lost:
        r5 = r2.length;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0038, code lost:
        if (r0 >= r5) goto L_0x0056;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x004e, code lost:
        if (r1.mAppDataRollbackHelper.restoreAppData(r4.info.getRollbackId(), r3, r2[r0], r18, r21) == false) goto L_0x0053;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0050, code lost:
        saveRollbackData(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0053, code lost:
        r0 = r0 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0056, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void restoreUserDataInternal(java.lang.String r16, int[] r17, int r18, long r19, java.lang.String r21, int r22) {
        /*
            r15 = this;
            r1 = r15
            r2 = r17
            r3 = 0
            r4 = 0
            java.lang.Object r5 = r1.mLock
            monitor-enter(r5)
            r15.ensureRollbackDataLoadedLocked()     // Catch:{ all -> 0x0057 }
            r0 = 0
            r6 = r0
        L_0x000d:
            java.util.List<com.android.server.rollback.RollbackData> r7 = r1.mRollbacks     // Catch:{ all -> 0x0057 }
            int r7 = r7.size()     // Catch:{ all -> 0x0057 }
            if (r6 >= r7) goto L_0x0031
            java.util.List<com.android.server.rollback.RollbackData> r7 = r1.mRollbacks     // Catch:{ all -> 0x0057 }
            java.lang.Object r7 = r7.get(r6)     // Catch:{ all -> 0x0057 }
            com.android.server.rollback.RollbackData r7 = (com.android.server.rollback.RollbackData) r7     // Catch:{ all -> 0x0057 }
            boolean r8 = r7.restoreUserDataInProgress     // Catch:{ all -> 0x0057 }
            if (r8 == 0) goto L_0x002c
            r8 = r16
            android.content.rollback.PackageRollbackInfo r9 = getPackageRollbackInfo(r7, r8)     // Catch:{ all -> 0x005c }
            r3 = r9
            if (r3 == 0) goto L_0x002e
            r4 = r7
            goto L_0x0033
        L_0x002c:
            r8 = r16
        L_0x002e:
            int r6 = r6 + 1
            goto L_0x000d
        L_0x0031:
            r8 = r16
        L_0x0033:
            monitor-exit(r5)     // Catch:{ all -> 0x005c }
            if (r4 != 0) goto L_0x0037
            return
        L_0x0037:
            int r5 = r2.length
        L_0x0038:
            if (r0 >= r5) goto L_0x0056
            r6 = r2[r0]
            com.android.server.rollback.AppDataRollbackHelper r9 = r1.mAppDataRollbackHelper
            android.content.rollback.RollbackInfo r7 = r4.info
            int r10 = r7.getRollbackId()
            r11 = r3
            r12 = r6
            r13 = r18
            r14 = r21
            boolean r7 = r9.restoreAppData(r10, r11, r12, r13, r14)
            if (r7 == 0) goto L_0x0053
            r15.saveRollbackData(r4)
        L_0x0053:
            int r0 = r0 + 1
            goto L_0x0038
        L_0x0056:
            return
        L_0x0057:
            r0 = move-exception
            r8 = r16
        L_0x005a:
            monitor-exit(r5)     // Catch:{ all -> 0x005c }
            throw r0
        L_0x005c:
            r0 = move-exception
            goto L_0x005a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.rollback.RollbackManagerServiceImpl.restoreUserDataInternal(java.lang.String, int[], int, long, java.lang.String, int):void");
    }

    public boolean notifyStagedSession(int sessionId) {
        if (Binder.getCallingUid() == 1000) {
            LinkedBlockingQueue<Boolean> result = new LinkedBlockingQueue<>();
            getHandler().post(new Runnable(sessionId, result) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ LinkedBlockingQueue f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    RollbackManagerServiceImpl.this.lambda$notifyStagedSession$11$RollbackManagerServiceImpl(this.f$1, this.f$2);
                }
            });
            try {
                return result.take().booleanValue();
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted while waiting for notifyStagedSession response");
                return false;
            }
        } else {
            throw new SecurityException("notifyStagedSession may only be called by the system.");
        }
    }

    public /* synthetic */ void lambda$notifyStagedSession$11$RollbackManagerServiceImpl(int sessionId, LinkedBlockingQueue result) {
        NewRollback newRollback;
        PackageInstaller installer = this.mContext.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionInfo session = installer.getSessionInfo(sessionId);
        boolean z = false;
        if (session == null) {
            Log.e(TAG, "No matching install session for: " + sessionId);
            result.offer(false);
            return;
        }
        synchronized (this.mLock) {
            newRollback = createNewRollbackLocked(session);
        }
        if (session.isMultiPackage()) {
            int[] childSessionIds = session.getChildSessionIds();
            int length = childSessionIds.length;
            int i = 0;
            while (i < length) {
                int childSessionId = childSessionIds[i];
                PackageInstaller.SessionInfo childSession = installer.getSessionInfo(childSessionId);
                if (childSession == null) {
                    Log.e(TAG, "No matching child install session for: " + childSessionId);
                    result.offer(false);
                    return;
                } else if (!enableRollbackForPackageSession(newRollback.data, childSession, new int[0])) {
                    Log.e(TAG, "Unable to enable rollback for session: " + sessionId);
                    result.offer(false);
                    return;
                } else {
                    i++;
                }
            }
        } else if (!enableRollbackForPackageSession(newRollback.data, session, new int[0])) {
            Log.e(TAG, "Unable to enable rollback for session: " + sessionId);
            result.offer(false);
            return;
        }
        if (completeEnableRollback(newRollback, true) != null) {
            z = true;
        }
        result.offer(Boolean.valueOf(z));
    }

    public void notifyStagedApkSession(int originalSessionId, int apkSessionId) {
        if (Binder.getCallingUid() == 1000) {
            getHandler().post(new Runnable(originalSessionId, apkSessionId) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    RollbackManagerServiceImpl.this.lambda$notifyStagedApkSession$12$RollbackManagerServiceImpl(this.f$1, this.f$2);
                }
            });
            return;
        }
        throw new SecurityException("notifyStagedApkSession may only be called by the system.");
    }

    public /* synthetic */ void lambda$notifyStagedApkSession$12$RollbackManagerServiceImpl(int originalSessionId, int apkSessionId) {
        RollbackData rd = null;
        synchronized (this.mLock) {
            ensureRollbackDataLoadedLocked();
            int i = 0;
            while (true) {
                if (i >= this.mRollbacks.size()) {
                    break;
                }
                RollbackData data = this.mRollbacks.get(i);
                if (data.stagedSessionId == originalSessionId) {
                    data.apkSessionId = apkSessionId;
                    rd = data;
                    break;
                }
                i++;
            }
        }
        if (rd != null) {
            saveRollbackData(rd);
        }
    }

    private boolean enableRollbackAllowed(String installerPackageName, String packageName) {
        if (installerPackageName == null) {
            return false;
        }
        PackageManager pm = this.mContext.getPackageManager();
        boolean manageRollbacksGranted = pm.checkPermission("android.permission.MANAGE_ROLLBACKS", installerPackageName) == 0;
        boolean testManageRollbacksGranted = pm.checkPermission("android.permission.TEST_MANAGE_ROLLBACKS", installerPackageName) == 0;
        if ((!isModule(packageName) || !manageRollbacksGranted) && !testManageRollbacksGranted) {
            return false;
        }
        return true;
    }

    private boolean isModule(String packageName) {
        try {
            if (this.mContext.getPackageManager().getModuleInfo(packageName, 0) != null) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private VersionedPackage getInstalledPackageVersion(String packageName) {
        PackageManager packageManager = this.mContext.getPackageManager();
        try {
            return new VersionedPackage(packageName, getPackageInfo(packageName).getLongVersionCode());
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private PackageInfo getPackageInfo(String packageName) throws PackageManager.NameNotFoundException {
        PackageManager pm = this.mContext.getPackageManager();
        try {
            return pm.getPackageInfo(packageName, DumpState.DUMP_CHANGES);
        } catch (PackageManager.NameNotFoundException e) {
            return pm.getPackageInfo(packageName, 1073741824);
        }
    }

    private boolean packageVersionsEqual(VersionedPackage a, VersionedPackage b) {
        return a != null && b != null && a.getPackageName().equals(b.getPackageName()) && a.getLongVersionCode() == b.getLongVersionCode();
    }

    private class SessionCallback extends PackageInstaller.SessionCallback {
        private SessionCallback() {
        }

        public void onCreated(int sessionId) {
        }

        public void onBadgingChanged(int sessionId) {
        }

        public void onActiveChanged(int sessionId, boolean active) {
        }

        public void onProgressChanged(int sessionId, float progress) {
        }

        public void onFinished(int sessionId, boolean success) {
            NewRollback newRollback;
            RollbackData rollback;
            synchronized (RollbackManagerServiceImpl.this.mLock) {
                newRollback = RollbackManagerServiceImpl.this.getNewRollbackForPackageSessionLocked(sessionId);
                if (newRollback != null) {
                    RollbackManagerServiceImpl.this.mNewRollbacks.remove(newRollback);
                }
            }
            if (newRollback != null && (rollback = RollbackManagerServiceImpl.this.completeEnableRollback(newRollback, success)) != null && !rollback.isStaged()) {
                RollbackManagerServiceImpl.this.makeRollbackAvailable(rollback);
            }
        }
    }

    /* access modifiers changed from: private */
    public RollbackData completeEnableRollback(NewRollback newRollback, boolean success) {
        RollbackData data = newRollback.data;
        if (!success) {
            deleteRollback(data);
            return null;
        } else if (newRollback.isCancelled) {
            Log.e(TAG, "Rollback has been cancelled by PackageManager");
            deleteRollback(data);
            return null;
        } else if (data.info.getPackages().size() != newRollback.packageSessionIds.length) {
            Log.e(TAG, "Failed to enable rollback for all packages in session.");
            deleteRollback(data);
            return null;
        } else {
            saveRollbackData(data);
            synchronized (this.mLock) {
                ensureRollbackDataLoadedLocked();
                this.mRollbacks.add(data);
            }
            return data;
        }
    }

    /* access modifiers changed from: private */
    public void makeRollbackAvailable(RollbackData data) {
        synchronized (this.mLock) {
            data.state = 1;
            data.timestamp = Instant.now();
        }
        saveRollbackData(data);
        List<String> packages = new ArrayList<>();
        for (int i = 0; i < data.info.getPackages().size(); i++) {
            packages.add(((PackageRollbackInfo) data.info.getPackages().get(i)).getPackageName());
        }
        this.mPackageHealthObserver.startObservingHealth(packages, this.mRollbackLifetimeDurationInMillis);
        scheduleExpiration(this.mRollbackLifetimeDurationInMillis);
    }

    private RollbackData getRollbackForId(int rollbackId) {
        synchronized (this.mLock) {
            ensureRollbackDataLoadedLocked();
            for (int i = 0; i < this.mRollbacks.size(); i++) {
                RollbackData data = this.mRollbacks.get(i);
                if (data.info.getRollbackId() == rollbackId) {
                    return data;
                }
            }
            return null;
        }
    }

    private static PackageRollbackInfo getPackageRollbackInfo(RollbackData data, String packageName) {
        for (PackageRollbackInfo info : data.info.getPackages()) {
            if (info.getPackageName().equals(packageName)) {
                return info;
            }
        }
        return null;
    }

    @GuardedBy({"mLock"})
    private int allocateRollbackIdLocked() {
        int n = 0;
        while (true) {
            int rollbackId = this.mRandom.nextInt(2147483646) + 1;
            if (!this.mAllocatedRollbackIds.get(rollbackId, false)) {
                this.mAllocatedRollbackIds.put(rollbackId, true);
                return rollbackId;
            }
            int n2 = n + 1;
            if (n < 32) {
                n = n2;
            } else {
                throw new IllegalStateException("Failed to allocate rollback ID");
            }
        }
    }

    private void deleteRollback(RollbackData rollbackData) {
        for (PackageRollbackInfo info : rollbackData.info.getPackages()) {
            IntArray installedUsers = info.getInstalledUsers();
            for (int i = 0; i < installedUsers.size(); i++) {
                this.mAppDataRollbackHelper.destroyAppDataSnapshot(rollbackData.info.getRollbackId(), info, installedUsers.get(i));
            }
        }
        this.mRollbackStore.deleteRollbackData(rollbackData);
    }

    /* access modifiers changed from: private */
    public void saveRollbackData(RollbackData rollbackData) {
        try {
            this.mRollbackStore.saveRollbackData(rollbackData);
        } catch (IOException ioe) {
            Log.e(TAG, "Unable to save rollback info for: " + rollbackData.info.getRollbackId(), ioe);
        }
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        IndentingPrintWriter ipw = new IndentingPrintWriter(pw, "  ");
        synchronized (this.mLock) {
            for (RollbackData data : this.mRollbacks) {
                RollbackInfo info = data.info;
                ipw.println(info.getRollbackId() + ":");
                ipw.increaseIndent();
                ipw.println("-state: " + data.getStateAsString());
                ipw.println("-timestamp: " + data.timestamp);
                if (data.stagedSessionId != -1) {
                    ipw.println("-stagedSessionId: " + data.stagedSessionId);
                }
                ipw.println("-packages:");
                ipw.increaseIndent();
                for (PackageRollbackInfo pkg : info.getPackages()) {
                    ipw.println(pkg.getPackageName() + " " + pkg.getVersionRolledBackFrom().getLongVersionCode() + " -> " + pkg.getVersionRolledBackTo().getLongVersionCode());
                }
                ipw.decreaseIndent();
                if (data.state == 3) {
                    ipw.println("-causePackages:");
                    ipw.increaseIndent();
                    for (VersionedPackage cPkg : info.getCausePackages()) {
                        ipw.println(cPkg.getPackageName() + " " + cPkg.getLongVersionCode());
                    }
                    ipw.decreaseIndent();
                    ipw.println("-committedSessionId: " + info.getCommittedSessionId());
                }
                ipw.decreaseIndent();
            }
        }
    }

    private void enforceManageRollbacks(String message) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MANAGE_ROLLBACKS") != 0 && this.mContext.checkCallingOrSelfPermission("android.permission.TEST_MANAGE_ROLLBACKS") != 0) {
            throw new SecurityException(message + " requires " + "android.permission.MANAGE_ROLLBACKS" + " or " + "android.permission.TEST_MANAGE_ROLLBACKS");
        }
    }

    private static class NewRollback {
        public final RollbackData data;
        public boolean isCancelled = false;
        private final IntArray mTokens = new IntArray();
        public final int[] packageSessionIds;

        NewRollback(RollbackData data2, int[] packageSessionIds2) {
            this.data = data2;
            this.packageSessionIds = packageSessionIds2;
        }

        public void addToken(int token) {
            this.mTokens.add(token);
        }

        public boolean hasToken(int token) {
            return this.mTokens.indexOf(token) != -1;
        }
    }

    /* access modifiers changed from: package-private */
    public NewRollback createNewRollbackLocked(PackageInstaller.SessionInfo parentSession) {
        RollbackData data;
        int rollbackId = allocateRollbackIdLocked();
        int parentSessionId = parentSession.getSessionId();
        if (parentSession.isStaged()) {
            data = this.mRollbackStore.createStagedRollback(rollbackId, parentSessionId);
        } else {
            data = this.mRollbackStore.createNonStagedRollback(rollbackId);
        }
        return new NewRollback(data, parentSession.isMultiPackage() ? parentSession.getChildSessionIds() : new int[]{parentSessionId});
    }

    /* access modifiers changed from: package-private */
    public NewRollback getNewRollbackForPackageSessionLocked(int packageSessionId) {
        for (NewRollback newRollbackData : this.mNewRollbacks) {
            int[] iArr = newRollbackData.packageSessionIds;
            int length = iArr.length;
            int i = 0;
            while (true) {
                if (i < length) {
                    if (iArr[i] == packageSessionId) {
                        return newRollbackData;
                    }
                    i++;
                }
            }
        }
        return null;
    }
}
