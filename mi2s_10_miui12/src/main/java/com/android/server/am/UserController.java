package com.android.server.am;

import android.app.AppGlobals;
import android.app.IStopUserCallback;
import android.app.IUserSwitchObserver;
import android.app.KeyguardManager;
import android.appwidget.AppWidgetManagerInternal;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.UserInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IProgressListener;
import android.os.IUserManager;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManagerInternal;
import android.os.storage.IStorageManager;
import android.os.storage.StorageManager;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimingsTraceLog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.ProgressReporter;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemServiceManager;
import com.android.server.am.UserController;
import com.android.server.am.UserState;
import com.android.server.pm.UserManagerService;
import com.android.server.wm.ActivityTaskManagerInternal;
import com.android.server.wm.WindowManagerService;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class UserController implements Handler.Callback {
    static final int CONTINUE_USER_SWITCH_MSG = 20;
    static final int FOREGROUND_PROFILE_CHANGED_MSG = 70;
    static final int REPORT_LOCKED_BOOT_COMPLETE_MSG = 110;
    static final int REPORT_USER_SWITCH_COMPLETE_MSG = 80;
    static final int REPORT_USER_SWITCH_MSG = 10;
    static final int START_PROFILES_MSG = 40;
    static final int START_USER_SWITCH_FG_MSG = 120;
    static final int START_USER_SWITCH_UI_MSG = 1000;
    static final int SYSTEM_USER_CURRENT_MSG = 60;
    static final int SYSTEM_USER_START_MSG = 50;
    static final int SYSTEM_USER_UNLOCK_MSG = 100;
    private static final String TAG = "ActivityManager";
    private static final int USER_SWITCH_CALLBACKS_TIMEOUT_MS = 5000;
    static final int USER_SWITCH_CALLBACKS_TIMEOUT_MSG = 90;
    static final int USER_SWITCH_TIMEOUT_MS = 3000;
    static final int USER_SWITCH_TIMEOUT_MSG = 30;
    volatile boolean mBootCompleted;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public volatile ArraySet<String> mCurWaitingUserSwitchCallbacks;
    @GuardedBy({"mLock"})
    private int[] mCurrentProfileIds;
    @GuardedBy({"mLock"})
    private volatile int mCurrentUserId;
    boolean mDelayUserDataLocking;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    /* access modifiers changed from: private */
    public final Injector mInjector;
    @GuardedBy({"mLock"})
    private final ArrayList<Integer> mLastActiveUsers;
    /* access modifiers changed from: private */
    public final Object mLock;
    private final LockPatternUtils mLockPatternUtils;
    int mMaxRunningUsers;
    @GuardedBy({"mLock"})
    private int[] mStartedUserArray;
    @GuardedBy({"mLock"})
    private final SparseArray<UserState> mStartedUsers;
    @GuardedBy({"mLock"})
    private String mSwitchingFromSystemUserMessage;
    @GuardedBy({"mLock"})
    private String mSwitchingToSystemUserMessage;
    @GuardedBy({"mLock"})
    private volatile int mTargetUserId;
    @GuardedBy({"mLock"})
    private ArraySet<String> mTimeoutUserSwitchCallbacks;
    private final Handler mUiHandler;
    @GuardedBy({"mLock"})
    private final ArrayList<Integer> mUserLru;
    @GuardedBy({"mLock"})
    private final SparseIntArray mUserProfileGroupIds;
    private final RemoteCallbackList<IUserSwitchObserver> mUserSwitchObservers;
    boolean mUserSwitchUiEnabled;

    UserController(ActivityManagerService service) {
        this(new Injector(service));
    }

    @VisibleForTesting
    UserController(Injector injector) {
        this.mLock = new Object();
        this.mCurrentUserId = 0;
        this.mTargetUserId = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        this.mStartedUsers = new SparseArray<>();
        this.mUserLru = new ArrayList<>();
        this.mStartedUserArray = new int[]{0};
        this.mCurrentProfileIds = new int[0];
        this.mUserProfileGroupIds = new SparseIntArray();
        this.mUserSwitchObservers = new RemoteCallbackList<>();
        this.mUserSwitchUiEnabled = true;
        this.mLastActiveUsers = new ArrayList<>();
        this.mInjector = injector;
        this.mHandler = this.mInjector.getHandler(this);
        this.mUiHandler = this.mInjector.getUiHandler(this);
        UserState uss = new UserState(UserHandle.SYSTEM);
        uss.mUnlockProgress.addListener(new UserProgressListener());
        this.mStartedUsers.put(0, uss);
        this.mUserLru.add(0);
        this.mLockPatternUtils = this.mInjector.getLockPatternUtils();
        updateStartedUserArrayLU();
    }

    /* access modifiers changed from: package-private */
    public void finishUserSwitch(UserState uss) {
        this.mHandler.post(new Runnable(uss) {
            private final /* synthetic */ UserState f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                UserController.this.lambda$finishUserSwitch$0$UserController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$finishUserSwitch$0$UserController(UserState uss) {
        finishUserBoot(uss);
        startProfiles();
        synchronized (this.mLock) {
            stopRunningUsersLU(this.mMaxRunningUsers);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public List<Integer> getRunningUsersLU() {
        ArrayList<Integer> runningUsers = new ArrayList<>();
        Iterator<Integer> it = this.mUserLru.iterator();
        while (it.hasNext()) {
            Integer userId = it.next();
            UserState uss = this.mStartedUsers.get(userId.intValue());
            if (!(uss == null || uss.state == 4 || uss.state == 5)) {
                if (userId.intValue() != 0 || !UserInfo.isSystemOnly(userId.intValue())) {
                    runningUsers.add(userId);
                }
            }
        }
        return runningUsers;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void stopRunningUsersLU(int maxRunningUsers) {
        List<Integer> currentlyRunning = getRunningUsersLU();
        Iterator<Integer> iterator = currentlyRunning.iterator();
        while (currentlyRunning.size() > maxRunningUsers && iterator.hasNext()) {
            Integer userId = iterator.next();
            if (!(userId.intValue() == 0 || userId.intValue() == this.mCurrentUserId || stopUsersLU(userId.intValue(), false, (IStopUserCallback) null, (UserState.KeyEvictedCallback) null) != 0)) {
                iterator.remove();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean canStartMoreUsers() {
        boolean z;
        synchronized (this.mLock) {
            z = getRunningUsersLU().size() < this.mMaxRunningUsers;
        }
        return z;
    }

    private void finishUserBoot(UserState uss) {
        finishUserBoot(uss, (IIntentReceiver) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0038, code lost:
        if (r2.setState(0, 1) == false) goto L_0x00ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003a, code lost:
        r1.mInjector.getUserManagerInternal().setUserState(r6, r2.state);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0045, code lost:
        if (r6 != 0) goto L_0x0086;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004d, code lost:
        if (r1.mInjector.isRuntimeRestarted() != false) goto L_0x0086;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0055, code lost:
        if (r1.mInjector.isFirstBootOrUpgrade() != false) goto L_0x0086;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0057, code lost:
        r0 = (int) (android.os.SystemClock.elapsedRealtime() / 1000);
        com.android.internal.logging.MetricsLogger.histogram(r1.mInjector.getContext(), "framework_locked_boot_completed", r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x006e, code lost:
        if (r0 <= START_USER_SWITCH_FG_MSG) goto L_0x0086;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0070, code lost:
        android.util.Slog.wtf("SystemServerTiming", "finishUserBoot took too long. uptimeSeconds=" + r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0086, code lost:
        r0 = r1.mHandler;
        r0.sendMessage(r0.obtainMessage(REPORT_LOCKED_BOOT_COMPLETE_MSG, r6, 0));
        r0 = new android.content.Intent("android.intent.action.LOCKED_BOOT_COMPLETED", (android.net.Uri) null);
        r0.putExtra("android.intent.extra.user_handle", r6);
        r0.addFlags(150994944);
        r20 = r6;
        r1.mInjector.broadcastIntent(r0, (java.lang.String) null, r23, 0, (java.lang.String) null, (android.os.Bundle) null, new java.lang.String[]{"android.permission.RECEIVE_BOOT_COMPLETED"}, -1, (android.os.Bundle) null, true, false, com.android.server.am.ActivityManagerService.MY_PID, 1000, android.os.Binder.getCallingUid(), android.os.Binder.getCallingPid(), r20);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00ca, code lost:
        r20 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00cc, code lost:
        r4 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00d8, code lost:
        if (r1.mInjector.getUserManager().isManagedProfile(r4) != false) goto L_0x00e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00de, code lost:
        if (miui.securityspace.XSpaceUserHandle.isXSpaceUserId(r4) == false) goto L_0x00e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00e1, code lost:
        maybeUnlockUser(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00e5, code lost:
        r0 = r1.mInjector.getUserManager().getProfileParent(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00ef, code lost:
        if (r0 == null) goto L_0x0123;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00f8, code lost:
        if (isUserRunning(r0.id, 4) == false) goto L_0x0123;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00fa, code lost:
        android.util.Slog.d(TAG, "User " + r4 + " (parent " + r0.id + "): attempting unlock because parent is unlocked");
        maybeUnlockUser(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0123, code lost:
        if (r0 != null) goto L_0x0128;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0125, code lost:
        r3 = "<null>";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0128, code lost:
        r3 = java.lang.String.valueOf(r0.id);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x012e, code lost:
        android.util.Slog.d(TAG, "User " + r4 + " (parent " + r3 + "): delaying unlock because parent is locked");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void finishUserBoot(com.android.server.am.UserState r22, android.content.IIntentReceiver r23) {
        /*
            r21 = this;
            r1 = r21
            r2 = r22
            android.os.UserHandle r0 = r2.mHandle
            int r6 = r0.getIdentifier()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Finishing user boot "
            r0.append(r3)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            java.lang.String r3 = "ActivityManager"
            android.util.Slog.d(r3, r0)
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            android.util.SparseArray<com.android.server.am.UserState> r0 = r1.mStartedUsers     // Catch:{ all -> 0x0153 }
            java.lang.Object r0 = r0.get(r6)     // Catch:{ all -> 0x0153 }
            if (r0 == r2) goto L_0x0031
            monitor-exit(r3)     // Catch:{ all -> 0x002d }
            return
        L_0x002d:
            r0 = move-exception
            r4 = r6
            goto L_0x0155
        L_0x0031:
            monitor-exit(r3)     // Catch:{ all -> 0x0153 }
            r0 = 1
            r3 = 0
            boolean r0 = r2.setState(r3, r0)
            if (r0 == 0) goto L_0x00ca
            com.android.server.am.UserController$Injector r0 = r1.mInjector
            android.os.UserManagerInternal r0 = r0.getUserManagerInternal()
            int r4 = r2.state
            r0.setUserState(r6, r4)
            if (r6 != 0) goto L_0x0086
            com.android.server.am.UserController$Injector r0 = r1.mInjector
            boolean r0 = r0.isRuntimeRestarted()
            if (r0 != 0) goto L_0x0086
            com.android.server.am.UserController$Injector r0 = r1.mInjector
            boolean r0 = r0.isFirstBootOrUpgrade()
            if (r0 != 0) goto L_0x0086
            long r4 = android.os.SystemClock.elapsedRealtime()
            r7 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 / r7
            int r0 = (int) r4
            com.android.server.am.UserController$Injector r4 = r1.mInjector
            android.content.Context r4 = r4.getContext()
            java.lang.String r5 = "framework_locked_boot_completed"
            com.android.internal.logging.MetricsLogger.histogram(r4, r5, r0)
            r4 = 120(0x78, float:1.68E-43)
            r5 = 120(0x78, float:1.68E-43)
            if (r0 <= r5) goto L_0x0086
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "finishUserBoot took too long. uptimeSeconds="
            r5.append(r7)
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            java.lang.String r7 = "SystemServerTiming"
            android.util.Slog.wtf(r7, r5)
        L_0x0086:
            android.os.Handler r0 = r1.mHandler
            r4 = 110(0x6e, float:1.54E-43)
            android.os.Message r3 = r0.obtainMessage(r4, r6, r3)
            r0.sendMessage(r3)
            android.content.Intent r0 = new android.content.Intent
            r3 = 0
            java.lang.String r4 = "android.intent.action.LOCKED_BOOT_COMPLETED"
            r0.<init>(r4, r3)
            r4 = r0
            java.lang.String r3 = "android.intent.extra.user_handle"
            r0.putExtra(r3, r6)
            r3 = 150994944(0x9000000, float:1.540744E-33)
            r0.addFlags(r3)
            com.android.server.am.UserController$Injector r3 = r1.mInjector
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            java.lang.String r10 = "android.permission.RECEIVE_BOOT_COMPLETED"
            java.lang.String[] r10 = new java.lang.String[]{r10}
            r11 = -1
            r12 = 0
            r13 = 1
            r14 = 0
            int r15 = com.android.server.am.ActivityManagerService.MY_PID
            r16 = 1000(0x3e8, float:1.401E-42)
            int r17 = android.os.Binder.getCallingUid()
            int r18 = android.os.Binder.getCallingPid()
            r20 = r6
            r6 = r23
            r19 = r20
            r3.broadcastIntent(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            goto L_0x00cc
        L_0x00ca:
            r20 = r6
        L_0x00cc:
            com.android.server.am.UserController$Injector r0 = r1.mInjector
            com.android.server.pm.UserManagerService r0 = r0.getUserManager()
            r4 = r20
            boolean r0 = r0.isManagedProfile(r4)
            if (r0 != 0) goto L_0x00e5
            boolean r0 = miui.securityspace.XSpaceUserHandle.isXSpaceUserId(r4)
            if (r0 == 0) goto L_0x00e1
            goto L_0x00e5
        L_0x00e1:
            r1.maybeUnlockUser(r4)
            goto L_0x0152
        L_0x00e5:
            com.android.server.am.UserController$Injector r0 = r1.mInjector
            com.android.server.pm.UserManagerService r0 = r0.getUserManager()
            android.content.pm.UserInfo r0 = r0.getProfileParent(r4)
            if (r0 == 0) goto L_0x0123
            int r3 = r0.id
            r5 = 4
            boolean r3 = r1.isUserRunning(r3, r5)
            if (r3 == 0) goto L_0x0123
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "User "
            r3.append(r5)
            r3.append(r4)
            java.lang.String r5 = " (parent "
            r3.append(r5)
            int r5 = r0.id
            r3.append(r5)
            java.lang.String r5 = "): attempting unlock because parent is unlocked"
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            java.lang.String r5 = "ActivityManager"
            android.util.Slog.d(r5, r3)
            r1.maybeUnlockUser(r4)
            goto L_0x0151
        L_0x0123:
            if (r0 != 0) goto L_0x0128
            java.lang.String r3 = "<null>"
            goto L_0x012e
        L_0x0128:
            int r3 = r0.id
            java.lang.String r3 = java.lang.String.valueOf(r3)
        L_0x012e:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "User "
            r5.append(r6)
            r5.append(r4)
            java.lang.String r6 = " (parent "
            r5.append(r6)
            r5.append(r3)
            java.lang.String r6 = "): delaying unlock because parent is locked"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = "ActivityManager"
            android.util.Slog.d(r6, r5)
        L_0x0151:
        L_0x0152:
            return
        L_0x0153:
            r0 = move-exception
            r4 = r6
        L_0x0155:
            monitor-exit(r3)     // Catch:{ all -> 0x0157 }
            throw r0
        L_0x0157:
            r0 = move-exception
            goto L_0x0155
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.UserController.finishUserBoot(com.android.server.am.UserState, android.content.IIntentReceiver):void");
    }

    private boolean finishUserUnlocking(UserState uss) {
        int userId = uss.mHandle.getIdentifier();
        if (!StorageManager.isUserKeyUnlocked(userId)) {
            return false;
        }
        synchronized (this.mLock) {
            if (this.mStartedUsers.get(userId) == uss) {
                if (uss.state == 1) {
                    uss.mUnlockProgress.start();
                    uss.mUnlockProgress.setProgress(5, this.mInjector.getContext().getString(17039503));
                    FgThread.getHandler().post(new Runnable(userId, uss) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ UserState f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            UserController.this.lambda$finishUserUnlocking$1$UserController(this.f$1, this.f$2);
                        }
                    });
                    return true;
                }
            }
            return false;
        }
    }

    public /* synthetic */ void lambda$finishUserUnlocking$1$UserController(int userId, UserState uss) {
        if (!StorageManager.isUserKeyUnlocked(userId)) {
            Slog.w(TAG, "User key got locked unexpectedly, leaving user locked.");
            return;
        }
        this.mInjector.getUserManager().onBeforeUnlockUser(userId);
        synchronized (this.mLock) {
            if (uss.setState(1, 2)) {
                this.mInjector.getUserManagerInternal().setUserState(userId, uss.state);
                uss.mUnlockProgress.setProgress(20);
                this.mHandler.obtainMessage(100, userId, 0, uss).sendToTarget();
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0033, code lost:
        r1.mInjector.getUserManagerInternal().setUserState(r15, r2.state);
        r2.mUnlockProgress.finish();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0043, code lost:
        if (r15 != 0) goto L_0x0058;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0045, code lost:
        r1.mInjector.startPersistentApps(com.android.server.pm.DumpState.DUMP_DOMAIN_PREFERRED);
        com.android.server.am.ActivityManagerServiceInjector.reportBootEvent();
        com.android.server.am.ActivityManagerServiceInjector.finishBooting(r1.mInjector.getService());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0058, code lost:
        r1.mInjector.installEncryptionUnawareProviders(r15);
        r0 = new android.content.Intent("android.intent.action.USER_UNLOCKED");
        r0.putExtra("android.intent.extra.user_handle", r15);
        r0.addFlags(1342177280);
        r20 = r15;
        r1.mInjector.broadcastIntent(r0, (java.lang.String) null, (android.content.IIntentReceiver) null, 0, (java.lang.String) null, (android.os.Bundle) null, (java.lang.String[]) null, -1, (android.os.Bundle) null, false, false, com.android.server.am.ActivityManagerService.MY_PID, 1000, android.os.Binder.getCallingUid(), android.os.Binder.getCallingPid(), r20);
        r4 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x009d, code lost:
        if (getUserInfo(r4).isManagedProfile() == false) goto L_0x00ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x009f, code lost:
        r3 = r1.mInjector.getUserManager().getProfileParent(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a9, code lost:
        if (r3 == null) goto L_0x00ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00ab, code lost:
        r5 = new android.content.Intent("android.intent.action.MANAGED_PROFILE_UNLOCKED");
        r5.putExtra("android.intent.extra.USER", android.os.UserHandle.of(r4));
        r5.addFlags(1342177280);
        r1.mInjector.broadcastIntent(r5, (java.lang.String) null, (android.content.IIntentReceiver) null, 0, (java.lang.String) null, (android.os.Bundle) null, (java.lang.String[]) null, -1, (android.os.Bundle) null, false, false, com.android.server.am.ActivityManagerService.MY_PID, 1000, android.os.Binder.getCallingUid(), android.os.Binder.getCallingPid(), r3.id);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00ed, code lost:
        r3 = getUserInfo(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00f9, code lost:
        if (java.util.Objects.equals(r3.lastLoggedInFingerprint, android.os.Build.FINGERPRINT) != false) goto L_0x011e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00ff, code lost:
        if (r3.isManagedProfile() == false) goto L_0x0112;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0103, code lost:
        if (r2.tokenProvided == false) goto L_0x0110;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x010b, code lost:
        if (r1.mLockPatternUtils.isSeparateProfileChallengeEnabled(r4) != false) goto L_0x010e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x010e, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0110, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0112, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0113, code lost:
        r1.mInjector.sendPreBootBroadcast(r4, r5, new com.android.server.am.$$Lambda$UserController$K71HFCIuD0iCwrDTKYnIUDyAeWg(r1, r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x011e, code lost:
        finishUserUnlockedCompleted(r38);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void finishUserUnlocked(com.android.server.am.UserState r38) {
        /*
            r37 = this;
            r1 = r37
            r2 = r38
            android.os.UserHandle r0 = r2.mHandle
            int r15 = r0.getIdentifier()
            boolean r0 = android.os.storage.StorageManager.isUserKeyUnlocked(r15)
            if (r0 != 0) goto L_0x0011
            return
        L_0x0011:
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            android.util.SparseArray<com.android.server.am.UserState> r0 = r1.mStartedUsers     // Catch:{ all -> 0x0122 }
            android.os.UserHandle r4 = r2.mHandle     // Catch:{ all -> 0x0122 }
            int r4 = r4.getIdentifier()     // Catch:{ all -> 0x0122 }
            java.lang.Object r0 = r0.get(r4)     // Catch:{ all -> 0x0122 }
            if (r0 == r2) goto L_0x0028
            monitor-exit(r3)     // Catch:{ all -> 0x0024 }
            return
        L_0x0024:
            r0 = move-exception
            r4 = r15
            goto L_0x0124
        L_0x0028:
            r0 = 2
            r4 = 3
            boolean r0 = r2.setState(r0, r4)     // Catch:{ all -> 0x0122 }
            if (r0 != 0) goto L_0x0032
            monitor-exit(r3)     // Catch:{ all -> 0x0024 }
            return
        L_0x0032:
            monitor-exit(r3)     // Catch:{ all -> 0x0122 }
            com.android.server.am.UserController$Injector r0 = r1.mInjector
            android.os.UserManagerInternal r0 = r0.getUserManagerInternal()
            int r3 = r2.state
            r0.setUserState(r15, r3)
            com.android.internal.util.ProgressReporter r0 = r2.mUnlockProgress
            r0.finish()
            if (r15 != 0) goto L_0x0058
            com.android.server.am.UserController$Injector r0 = r1.mInjector
            r3 = 262144(0x40000, float:3.67342E-40)
            r0.startPersistentApps(r3)
            com.android.server.am.ActivityManagerServiceInjector.reportBootEvent()
            com.android.server.am.UserController$Injector r0 = r1.mInjector
            com.android.server.am.ActivityManagerService r0 = r0.getService()
            com.android.server.am.ActivityManagerServiceInjector.finishBooting(r0)
        L_0x0058:
            com.android.server.am.UserController$Injector r0 = r1.mInjector
            r0.installEncryptionUnawareProviders(r15)
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r3 = "android.intent.action.USER_UNLOCKED"
            r0.<init>(r3)
            r4 = r0
            java.lang.String r3 = "android.intent.extra.user_handle"
            r0.putExtra(r3, r15)
            r14 = 1342177280(0x50000000, float:8.5899346E9)
            r0.addFlags(r14)
            com.android.server.am.UserController$Injector r3 = r1.mInjector
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = -1
            r12 = 0
            r13 = 0
            r16 = 0
            r14 = r16
            int r16 = com.android.server.am.ActivityManagerService.MY_PID
            r20 = r15
            r15 = r16
            r16 = 1000(0x3e8, float:1.401E-42)
            int r17 = android.os.Binder.getCallingUid()
            int r18 = android.os.Binder.getCallingPid()
            r19 = r20
            r3.broadcastIntent(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            r4 = r20
            android.content.pm.UserInfo r3 = r1.getUserInfo(r4)
            boolean r3 = r3.isManagedProfile()
            if (r3 == 0) goto L_0x00ed
            com.android.server.am.UserController$Injector r3 = r1.mInjector
            com.android.server.pm.UserManagerService r3 = r3.getUserManager()
            android.content.pm.UserInfo r3 = r3.getProfileParent(r4)
            if (r3 == 0) goto L_0x00ed
            android.content.Intent r5 = new android.content.Intent
            java.lang.String r6 = "android.intent.action.MANAGED_PROFILE_UNLOCKED"
            r5.<init>(r6)
            r21 = r5
            android.os.UserHandle r6 = android.os.UserHandle.of(r4)
            java.lang.String r7 = "android.intent.extra.USER"
            r5.putExtra(r7, r6)
            r6 = 1342177280(0x50000000, float:8.5899346E9)
            r5.addFlags(r6)
            com.android.server.am.UserController$Injector r6 = r1.mInjector
            r20 = r6
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = -1
            r29 = 0
            r30 = 0
            r31 = 0
            int r32 = com.android.server.am.ActivityManagerService.MY_PID
            r33 = 1000(0x3e8, float:1.401E-42)
            int r34 = android.os.Binder.getCallingUid()
            int r35 = android.os.Binder.getCallingPid()
            int r6 = r3.id
            r36 = r6
            r20.broadcastIntent(r21, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31, r32, r33, r34, r35, r36)
        L_0x00ed:
            android.content.pm.UserInfo r3 = r1.getUserInfo(r4)
            java.lang.String r5 = r3.lastLoggedInFingerprint
            java.lang.String r6 = android.os.Build.FINGERPRINT
            boolean r5 = java.util.Objects.equals(r5, r6)
            if (r5 != 0) goto L_0x011e
            boolean r5 = r3.isManagedProfile()
            if (r5 == 0) goto L_0x0112
            boolean r5 = r2.tokenProvided
            if (r5 == 0) goto L_0x0110
            com.android.internal.widget.LockPatternUtils r5 = r1.mLockPatternUtils
            boolean r5 = r5.isSeparateProfileChallengeEnabled(r4)
            if (r5 != 0) goto L_0x010e
            goto L_0x0110
        L_0x010e:
            r5 = 0
            goto L_0x0111
        L_0x0110:
            r5 = 1
        L_0x0111:
            goto L_0x0113
        L_0x0112:
            r5 = 0
        L_0x0113:
            com.android.server.am.UserController$Injector r6 = r1.mInjector
            com.android.server.am.-$$Lambda$UserController$K71HFCIuD0iCwrDTKYnIUDyAeWg r7 = new com.android.server.am.-$$Lambda$UserController$K71HFCIuD0iCwrDTKYnIUDyAeWg
            r7.<init>(r2)
            r6.sendPreBootBroadcast(r4, r5, r7)
            goto L_0x0121
        L_0x011e:
            r37.lambda$finishUserUnlocked$2$UserController(r38)
        L_0x0121:
            return
        L_0x0122:
            r0 = move-exception
            r4 = r15
        L_0x0124:
            monitor-exit(r3)     // Catch:{ all -> 0x0126 }
            throw r0
        L_0x0126:
            r0 = move-exception
            goto L_0x0124
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.UserController.finishUserUnlocked(com.android.server.am.UserState):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0022, code lost:
        r5 = getUserInfo(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0026, code lost:
        if (r5 != null) goto L_0x0029;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0028, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002d, code lost:
        if (android.os.storage.StorageManager.isUserKeyUnlocked(r6) != false) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002f, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0030, code lost:
        r7.mInjector.getUserManager().onUserLoggedIn(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0041, code lost:
        if (android.app.AppGlobals.getPackageManager().isDeviceUpgrading() == false) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0043, code lost:
        r7.mHandler.post(com.android.server.am.$$Lambda$UserController$k8ApLcQKGJQpqZmC58PDXNdaMjM.INSTANCE);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004c, code lost:
        r0.printStackTrace();
     */
    /* renamed from: finishUserUnlockedCompleted */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$finishUserUnlocked$2$UserController(com.android.server.am.UserState r27) {
        /*
            r26 = this;
            r7 = r26
            r8 = r27
            android.os.UserHandle r0 = r8.mHandle
            int r6 = r0.getIdentifier()
            java.lang.Object r1 = r7.mLock
            monitor-enter(r1)
            android.util.SparseArray<com.android.server.am.UserState> r0 = r7.mStartedUsers     // Catch:{ all -> 0x0111 }
            android.os.UserHandle r2 = r8.mHandle     // Catch:{ all -> 0x0111 }
            int r2 = r2.getIdentifier()     // Catch:{ all -> 0x0111 }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ all -> 0x0111 }
            if (r0 == r8) goto L_0x0021
            monitor-exit(r1)     // Catch:{ all -> 0x001d }
            return
        L_0x001d:
            r0 = move-exception
            r14 = r6
            goto L_0x0113
        L_0x0021:
            monitor-exit(r1)     // Catch:{ all -> 0x0111 }
            android.content.pm.UserInfo r5 = r7.getUserInfo(r6)
            if (r5 != 0) goto L_0x0029
            return
        L_0x0029:
            boolean r0 = android.os.storage.StorageManager.isUserKeyUnlocked(r6)
            if (r0 != 0) goto L_0x0030
            return
        L_0x0030:
            com.android.server.am.UserController$Injector r0 = r7.mInjector
            com.android.server.pm.UserManagerService r0 = r0.getUserManager()
            r0.onUserLoggedIn(r6)
            android.content.pm.IPackageManager r0 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x004b }
            boolean r0 = r0.isDeviceUpgrading()     // Catch:{ RemoteException -> 0x004b }
            if (r0 == 0) goto L_0x004a
            android.os.Handler r0 = r7.mHandler     // Catch:{ RemoteException -> 0x004b }
            com.android.server.am.-$$Lambda$UserController$k8ApLcQKGJQpqZmC58PDXNdaMjM r1 = com.android.server.am.$$Lambda$UserController$k8ApLcQKGJQpqZmC58PDXNdaMjM.INSTANCE     // Catch:{ RemoteException -> 0x004b }
            r0.post(r1)     // Catch:{ RemoteException -> 0x004b }
        L_0x004a:
            goto L_0x004f
        L_0x004b:
            r0 = move-exception
            r0.printStackTrace()
        L_0x004f:
            boolean r0 = r5.isInitialized()
            if (r0 != 0) goto L_0x00a1
            if (r6 == 0) goto L_0x00a1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Initializing user #"
            r0.append(r1)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "ActivityManager"
            android.util.Slog.d(r1, r0)
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "android.intent.action.USER_INITIALIZE"
            r0.<init>(r1)
            r10 = r0
            r1 = 285212672(0x11000000, float:1.00974196E-28)
            r0.addFlags(r1)
            com.android.server.am.UserController$Injector r9 = r7.mInjector
            r11 = 0
            com.android.server.am.UserController$1 r1 = new com.android.server.am.UserController$1
            r12 = r1
            r1.<init>(r5)
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = -1
            r18 = 0
            r19 = 1
            r20 = 0
            int r21 = com.android.server.am.ActivityManagerService.MY_PID
            r22 = 1000(0x3e8, float:1.401E-42)
            int r23 = android.os.Binder.getCallingUid()
            int r24 = android.os.Binder.getCallingPid()
            r25 = r6
            r9.broadcastIntent(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25)
        L_0x00a1:
            com.android.server.am.UserController$Injector r0 = r7.mInjector
            r0.startUserWidgets(r6)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Posting BOOT_COMPLETED user #"
            r0.append(r1)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "ActivityManager"
            android.util.Slog.i(r1, r0)
            if (r6 != 0) goto L_0x00e1
            com.android.server.am.UserController$Injector r0 = r7.mInjector
            boolean r0 = r0.isRuntimeRestarted()
            if (r0 != 0) goto L_0x00e1
            com.android.server.am.UserController$Injector r0 = r7.mInjector
            boolean r0 = r0.isFirstBootOrUpgrade()
            if (r0 != 0) goto L_0x00e1
            long r0 = android.os.SystemClock.elapsedRealtime()
            r2 = 1000(0x3e8, double:4.94E-321)
            long r0 = r0 / r2
            int r0 = (int) r0
            com.android.server.am.UserController$Injector r1 = r7.mInjector
            android.content.Context r1 = r1.getContext()
            java.lang.String r2 = "framework_boot_completed"
            com.android.internal.logging.MetricsLogger.histogram(r1, r2, r0)
        L_0x00e1:
            android.content.Intent r0 = new android.content.Intent
            r1 = 0
            java.lang.String r2 = "android.intent.action.BOOT_COMPLETED"
            r0.<init>(r2, r1)
            java.lang.String r1 = "android.intent.extra.user_handle"
            r0.putExtra(r1, r6)
            r1 = -1996488704(0xffffffff89000000, float:-1.540744E-33)
            r0.addFlags(r1)
            int r9 = android.os.Binder.getCallingUid()
            int r10 = android.os.Binder.getCallingPid()
            android.os.Handler r11 = com.android.server.FgThread.getHandler()
            com.android.server.am.-$$Lambda$UserController$iNxcwiechN4VieHO-D0SwsPl6xc r12 = new com.android.server.am.-$$Lambda$UserController$iNxcwiechN4VieHO-D0SwsPl6xc
            r1 = r12
            r2 = r26
            r3 = r0
            r4 = r6
            r13 = r5
            r5 = r9
            r14 = r6
            r6 = r10
            r1.<init>(r3, r4, r5, r6)
            r11.post(r12)
            return
        L_0x0111:
            r0 = move-exception
            r14 = r6
        L_0x0113:
            monitor-exit(r1)     // Catch:{ all -> 0x0115 }
            throw r0
        L_0x0115:
            r0 = move-exception
            goto L_0x0113
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.UserController.lambda$finishUserUnlocked$2$UserController(com.android.server.am.UserState):void");
    }

    public /* synthetic */ void lambda$finishUserUnlockedCompleted$4$UserController(Intent bootIntent, int userId, int callingUid, int callingPid) {
        Injector injector = this.mInjector;
        AnonymousClass2 r4 = r3;
        final int i = userId;
        AnonymousClass2 r3 = new IIntentReceiver.Stub() {
            public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) throws RemoteException {
                Slog.i(UserController.TAG, "Finished processing BOOT_COMPLETED for u" + i);
                UserController.this.mBootCompleted = true;
            }
        };
        String[] strArr = {"android.permission.RECEIVE_BOOT_COMPLETED"};
        int i2 = ActivityManagerService.MY_PID;
        injector.broadcastIntent(bootIntent, (String) null, r4, 0, (String) null, (Bundle) null, strArr, -1, (Bundle) null, true, false, i2, 1000, callingUid, callingPid, userId);
    }

    /* access modifiers changed from: package-private */
    public int restartUser(int userId, final boolean foreground) {
        return stopUser(userId, true, (IStopUserCallback) null, new UserState.KeyEvictedCallback() {
            public void keyEvicted(int userId) {
                UserController.this.mHandler.post(new Runnable(userId, foreground) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        UserController.AnonymousClass3.this.lambda$keyEvicted$0$UserController$3(this.f$1, this.f$2);
                    }
                });
            }

            public /* synthetic */ void lambda$keyEvicted$0$UserController$3(int userId, boolean foreground) {
                UserController.this.startUser(userId, foreground);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public int stopUser(int userId, boolean force, IStopUserCallback stopUserCallback, UserState.KeyEvictedCallback keyEvictedCallback) {
        int stopUsersLU;
        if (this.mInjector.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0) {
            String msg = "Permission Denial: switchUser() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.INTERACT_ACROSS_USERS_FULL";
            Slog.w(TAG, msg);
            throw new SecurityException(msg);
        } else if (userId < 0 || userId == 0) {
            throw new IllegalArgumentException("Can't stop system user " + userId);
        } else {
            enforceShellRestriction("no_debugging_features", userId);
            synchronized (this.mLock) {
                stopUsersLU = stopUsersLU(userId, force, stopUserCallback, keyEvictedCallback);
            }
            return stopUsersLU;
        }
    }

    @GuardedBy({"mLock"})
    private int stopUsersLU(int userId, boolean force, IStopUserCallback stopUserCallback, UserState.KeyEvictedCallback keyEvictedCallback) {
        if (userId == 0) {
            return -3;
        }
        if (isCurrentUserLU(userId)) {
            return -2;
        }
        int[] usersToStop = getUsersToStopLU(userId);
        int i = 0;
        while (i < usersToStop.length) {
            int relatedUserId = usersToStop[i];
            if (relatedUserId != 0 && !isCurrentUserLU(relatedUserId)) {
                i++;
            } else if (!force) {
                return -4;
            } else {
                Slog.i(TAG, "Force stop user " + userId + ". Related users will not be stopped");
                stopSingleUserLU(userId, stopUserCallback, keyEvictedCallback);
                return 0;
            }
        }
        int i2 = usersToStop.length;
        for (int i3 = 0; i3 < i2; i3++) {
            int userIdToStop = usersToStop[i3];
            UserState.KeyEvictedCallback keyEvictedCallback2 = null;
            IStopUserCallback iStopUserCallback = userIdToStop == userId ? stopUserCallback : null;
            if (userIdToStop == userId) {
                keyEvictedCallback2 = keyEvictedCallback;
            }
            stopSingleUserLU(userIdToStop, iStopUserCallback, keyEvictedCallback2);
        }
        return 0;
    }

    @GuardedBy({"mLock"})
    private void stopSingleUserLU(int userId, IStopUserCallback stopUserCallback, UserState.KeyEvictedCallback keyEvictedCallback) {
        UserState uss = this.mStartedUsers.get(userId);
        if (uss != null) {
            if (stopUserCallback != null) {
                uss.mStopCallbacks.add(stopUserCallback);
            }
            if (keyEvictedCallback != null) {
                uss.mKeyEvictedCallbacks.add(keyEvictedCallback);
            }
            if (uss.state != 4 && uss.state != 5) {
                uss.setState(4);
                this.mInjector.getUserManagerInternal().setUserState(userId, uss.state);
                updateStartedUserArrayLU();
                this.mHandler.post(new Runnable(userId, uss) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ UserState f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        UserController.this.lambda$stopSingleUserLU$6$UserController(this.f$1, this.f$2);
                    }
                });
            }
        } else if (stopUserCallback != null) {
            this.mHandler.post(new Runnable(stopUserCallback, userId) {
                private final /* synthetic */ IStopUserCallback f$0;
                private final /* synthetic */ int f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    UserController.lambda$stopSingleUserLU$5(this.f$0, this.f$1);
                }
            });
        }
    }

    static /* synthetic */ void lambda$stopSingleUserLU$5(IStopUserCallback stopUserCallback, int userId) {
        try {
            stopUserCallback.userStopped(userId);
        } catch (RemoteException e) {
        }
    }

    public /* synthetic */ void lambda$stopSingleUserLU$6$UserController(int userId, UserState uss) {
        final int i = userId;
        Intent stoppingIntent = new Intent("android.intent.action.USER_STOPPING");
        stoppingIntent.addFlags(1073741824);
        stoppingIntent.putExtra("android.intent.extra.user_handle", i);
        stoppingIntent.putExtra("android.intent.extra.SHUTDOWN_USERSPACE_ONLY", true);
        final UserState userState = uss;
        IIntentReceiver stoppingReceiver = new IIntentReceiver.Stub() {
            public /* synthetic */ void lambda$performReceive$0$UserController$4(int userId, UserState uss) {
                UserController.this.finishUserStopping(userId, uss);
            }

            public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) {
                UserController.this.mHandler.post(new Runnable(i, userState) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ UserState f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        UserController.AnonymousClass4.this.lambda$performReceive$0$UserController$4(this.f$1, this.f$2);
                    }
                });
            }
        };
        this.mInjector.clearBroadcastQueueForUser(i);
        this.mInjector.broadcastIntent(stoppingIntent, (String) null, stoppingReceiver, 0, (String) null, (Bundle) null, new String[]{"android.permission.INTERACT_ACROSS_USERS"}, -1, (Bundle) null, true, false, ActivityManagerService.MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), -1);
    }

    /* access modifiers changed from: package-private */
    public void finishUserStopping(int userId, UserState uss) {
        int i = userId;
        final UserState userState = uss;
        Intent shutdownIntent = new Intent("android.intent.action.ACTION_SHUTDOWN");
        IIntentReceiver shutdownReceiver = new IIntentReceiver.Stub() {
            public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) {
                UserController.this.mHandler.post(new Runnable() {
                    public void run() {
                        UserController.this.finishUserStopped(userState);
                    }
                });
            }
        };
        synchronized (this.mLock) {
            if (userState.state == 4) {
                userState.setState(5);
                this.mInjector.getUserManagerInternal().setUserState(i, userState.state);
                this.mInjector.batteryStatsServiceNoteEvent(16391, Integer.toString(userId), i);
                this.mInjector.getSystemServiceManager().stopUser(i);
                this.mInjector.broadcastIntent(shutdownIntent, (String) null, shutdownReceiver, 0, (String) null, (Bundle) null, (String[]) null, -1, (Bundle) null, true, false, ActivityManagerService.MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), userId);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void finishUserStopped(UserState uss) {
        ArrayList<IStopUserCallback> stopCallbacks;
        ArrayList<UserState.KeyEvictedCallback> keyEvictedCallbacks;
        boolean stopped;
        int userId = uss.mHandle.getIdentifier();
        boolean lockUser = true;
        int userIdToLock = userId;
        synchronized (this.mLock) {
            stopCallbacks = new ArrayList<>(uss.mStopCallbacks);
            keyEvictedCallbacks = new ArrayList<>(uss.mKeyEvictedCallbacks);
            if (this.mStartedUsers.get(userId) == uss) {
                if (uss.state == 5) {
                    stopped = true;
                    this.mStartedUsers.remove(userId);
                    this.mUserLru.remove(Integer.valueOf(userId));
                    updateStartedUserArrayLU();
                    userIdToLock = updateUserToLockLU(userId);
                    if (userIdToLock == -10000) {
                        lockUser = false;
                    }
                }
            }
            stopped = false;
        }
        if (stopped) {
            this.mInjector.getUserManagerInternal().removeUserState(userId);
            this.mInjector.activityManagerOnUserStopped(userId);
            forceStopUser(userId, ProcessRecordInjector.POLICY_FINISH_USER);
        }
        Iterator<IStopUserCallback> it = stopCallbacks.iterator();
        while (it.hasNext()) {
            IStopUserCallback callback = it.next();
            if (stopped) {
                try {
                    callback.userStopped(userId);
                } catch (RemoteException e) {
                }
            } else {
                callback.userStopAborted(userId);
            }
        }
        if (stopped) {
            this.mInjector.systemServiceManagerCleanupUser(userId);
            this.mInjector.stackSupervisorRemoveUser(userId);
            if (getUserInfo(userId).isEphemeral()) {
                this.mInjector.getUserManager().removeUserEvenWhenDisallowed(userId);
            }
            if (lockUser) {
                FgThread.getHandler().post(new Runnable(userIdToLock, userId, keyEvictedCallbacks) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ ArrayList f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        UserController.this.lambda$finishUserStopped$7$UserController(this.f$1, this.f$2, this.f$3);
                    }
                });
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        r3.mInjector.getStorageManager().lockUserKey(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001f, code lost:
        if (r4 != r5) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0021, code lost:
        r0 = r6.iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0029, code lost:
        if (r0.hasNext() == false) goto L_0x0035;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002b, code lost:
        ((com.android.server.am.UserState.KeyEvictedCallback) r0.next()).keyEvicted(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0036, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003b, code lost:
        throw r0.rethrowAsRuntimeException();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$finishUserStopped$7$UserController(int r4, int r5, java.util.ArrayList r6) {
        /*
            r3 = this;
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.am.UserState> r1 = r3.mStartedUsers     // Catch:{ all -> 0x003c }
            java.lang.Object r1 = r1.get(r4)     // Catch:{ all -> 0x003c }
            if (r1 == 0) goto L_0x0014
            java.lang.String r1 = "ActivityManager"
            java.lang.String r2 = "User was restarted, skipping key eviction"
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x003c }
            monitor-exit(r0)     // Catch:{ all -> 0x003c }
            return
        L_0x0014:
            monitor-exit(r0)     // Catch:{ all -> 0x003c }
            com.android.server.am.UserController$Injector r0 = r3.mInjector     // Catch:{ RemoteException -> 0x0036 }
            android.os.storage.IStorageManager r0 = r0.getStorageManager()     // Catch:{ RemoteException -> 0x0036 }
            r0.lockUserKey(r4)     // Catch:{ RemoteException -> 0x0036 }
            if (r4 != r5) goto L_0x0035
            java.util.Iterator r0 = r6.iterator()
        L_0x0025:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0035
            java.lang.Object r1 = r0.next()
            com.android.server.am.UserState$KeyEvictedCallback r1 = (com.android.server.am.UserState.KeyEvictedCallback) r1
            r1.keyEvicted(r5)
            goto L_0x0025
        L_0x0035:
            return
        L_0x0036:
            r0 = move-exception
            java.lang.RuntimeException r1 = r0.rethrowAsRuntimeException()
            throw r1
        L_0x003c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.UserController.lambda$finishUserStopped$7$UserController(int, int, java.util.ArrayList):void");
    }

    @GuardedBy({"mLock"})
    private int updateUserToLockLU(int userId) {
        int userIdToLock = userId;
        if (!this.mDelayUserDataLocking || getUserInfo(userId).isEphemeral() || hasUserRestriction("no_run_in_background", userId)) {
            return userIdToLock;
        }
        this.mLastActiveUsers.remove(Integer.valueOf(userId));
        this.mLastActiveUsers.add(0, Integer.valueOf(userId));
        if (this.mStartedUsers.size() + this.mLastActiveUsers.size() > this.mMaxRunningUsers) {
            ArrayList<Integer> arrayList = this.mLastActiveUsers;
            int userIdToLock2 = arrayList.get(arrayList.size() - 1).intValue();
            ArrayList<Integer> arrayList2 = this.mLastActiveUsers;
            arrayList2.remove(arrayList2.size() - 1);
            Slog.i(TAG, "finishUserStopped, stopping user:" + userId + " lock user:" + userIdToLock2);
            return userIdToLock2;
        }
        Slog.i(TAG, "finishUserStopped, user:" + userId + ",skip locking");
        return ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
    }

    @GuardedBy({"mLock"})
    private int[] getUsersToStopLU(int userId) {
        int startedUsersSize = this.mStartedUsers.size();
        IntArray userIds = new IntArray();
        userIds.add(userId);
        int userGroupId = this.mUserProfileGroupIds.get(userId, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
        for (int i = 0; i < startedUsersSize; i++) {
            int startedUserId = this.mStartedUsers.valueAt(i).mHandle.getIdentifier();
            boolean sameUserId = false;
            boolean sameGroup = userGroupId != -10000 && userGroupId == this.mUserProfileGroupIds.get(startedUserId, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
            if (startedUserId == userId) {
                sameUserId = true;
            }
            if (sameGroup && !sameUserId) {
                userIds.add(startedUserId);
            }
        }
        return userIds.toArray();
    }

    private void forceStopUser(int userId, String reason) {
        int i = userId;
        this.mInjector.activityManagerForceStopPackage(i, reason);
        Intent intent = new Intent("android.intent.action.USER_STOPPED");
        intent.addFlags(1342177280);
        intent.putExtra("android.intent.extra.user_handle", i);
        this.mInjector.broadcastIntent(intent, (String) null, (IIntentReceiver) null, 0, (String) null, (Bundle) null, (String[]) null, -1, (Bundle) null, false, false, ActivityManagerService.MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), -1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001f, code lost:
        r1 = getUserInfo(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0027, code lost:
        if (r1.isEphemeral() == false) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0029, code lost:
        ((android.os.UserManagerInternal) com.android.server.LocalServices.getService(android.os.UserManagerInternal.class)).onEphemeralUserStop(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0038, code lost:
        if (r1.isGuest() != false) goto L_0x0040;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003e, code lost:
        if (r1.isEphemeral() == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0040, code lost:
        r2 = r4.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0042, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        stopUsersLU(r5, true, (android.app.IStopUserCallback) null, (com.android.server.am.UserState.KeyEvictedCallback) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0048, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void stopGuestOrEphemeralUserIfBackground(int r5) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.am.UserState> r1 = r4.mStartedUsers     // Catch:{ all -> 0x004f }
            java.lang.Object r1 = r1.get(r5)     // Catch:{ all -> 0x004f }
            com.android.server.am.UserState r1 = (com.android.server.am.UserState) r1     // Catch:{ all -> 0x004f }
            if (r5 == 0) goto L_0x004d
            int r2 = r4.mCurrentUserId     // Catch:{ all -> 0x004f }
            if (r5 == r2) goto L_0x004d
            if (r1 == 0) goto L_0x004d
            int r2 = r1.state     // Catch:{ all -> 0x004f }
            r3 = 4
            if (r2 == r3) goto L_0x004d
            int r2 = r1.state     // Catch:{ all -> 0x004f }
            r3 = 5
            if (r2 != r3) goto L_0x001e
            goto L_0x004d
        L_0x001e:
            monitor-exit(r0)     // Catch:{ all -> 0x004f }
            android.content.pm.UserInfo r1 = r4.getUserInfo(r5)
            boolean r0 = r1.isEphemeral()
            if (r0 == 0) goto L_0x0034
            java.lang.Class<android.os.UserManagerInternal> r0 = android.os.UserManagerInternal.class
            java.lang.Object r0 = com.android.server.LocalServices.getService(r0)
            android.os.UserManagerInternal r0 = (android.os.UserManagerInternal) r0
            r0.onEphemeralUserStop(r5)
        L_0x0034:
            boolean r0 = r1.isGuest()
            if (r0 != 0) goto L_0x0040
            boolean r0 = r1.isEphemeral()
            if (r0 == 0) goto L_0x0049
        L_0x0040:
            java.lang.Object r2 = r4.mLock
            monitor-enter(r2)
            r0 = 1
            r3 = 0
            r4.stopUsersLU(r5, r0, r3, r3)     // Catch:{ all -> 0x004a }
            monitor-exit(r2)     // Catch:{ all -> 0x004a }
        L_0x0049:
            return
        L_0x004a:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x004a }
            throw r0
        L_0x004d:
            monitor-exit(r0)     // Catch:{ all -> 0x004f }
            return
        L_0x004f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.UserController.stopGuestOrEphemeralUserIfBackground(int):void");
    }

    /* access modifiers changed from: package-private */
    public void scheduleStartProfiles() {
        FgThread.getHandler().post(new Runnable() {
            public final void run() {
                UserController.this.lambda$scheduleStartProfiles$8$UserController();
            }
        });
    }

    public /* synthetic */ void lambda$scheduleStartProfiles$8$UserController() {
        if (!this.mHandler.hasMessages(40)) {
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(40), 1000);
        }
    }

    /* access modifiers changed from: package-private */
    public void startProfiles() {
        List<UserInfo> profiles = this.mInjector.getUserManager().getProfiles(getCurrentUserId(), false);
        List<UserInfo> profilesToStart = new ArrayList<>(profiles.size());
        for (UserInfo user : profiles) {
            if ((user.flags & 16) == 16 && user.id != this.mCurrentUserId && !user.isQuietModeEnabled()) {
                profilesToStart.add(user);
            }
        }
        int profilesToStartSize = profilesToStart.size();
        int i = 0;
        while (i < profilesToStartSize && i < this.mMaxRunningUsers - 1) {
            startUser(profilesToStart.get(i).id, false);
            i++;
        }
        if (i < profilesToStartSize) {
            Slog.w(TAG, "More profiles than MAX_RUNNING_USERS");
        }
    }

    /* access modifiers changed from: package-private */
    public boolean startUser(int userId, boolean foreground) {
        return lambda$startUser$9$UserController(userId, foreground, (IProgressListener) null);
    }

    /* Debug info: failed to restart local var, previous not found, register: 32 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x0202, code lost:
        if (r11.state != 5) goto L_0x021f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:?, code lost:
        r11.setState(0);
        r1.mInjector.getUserManagerInternal().setUserState(r15, r11.state);
        r2 = r1.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x0214, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:?, code lost:
        updateStartedUserArrayLU();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x0218, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:0x0219, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x021f, code lost:
        r2 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x0222, code lost:
        if (r11.state != 0) goto L_0x023a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:?, code lost:
        r1.mInjector.getUserManager().onBeforeStartUser(r15);
        r1.mHandler.sendMessage(r1.mHandler.obtainMessage(50, r15, 0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x023a, code lost:
        if (r14 == false) goto L_0x026f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x023c, code lost:
        r1.mHandler.sendMessage(r1.mHandler.obtainMessage(60, r15, r12));
        r1.mHandler.removeMessages(10);
        r1.mHandler.removeMessages(30);
        r1.mHandler.sendMessage(r1.mHandler.obtainMessage(10, r12, r15, r11));
        r1.mHandler.sendMessageDelayed(r1.mHandler.obtainMessage(30, r12, r15, r11), 3000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x026f, code lost:
        if (r2 == false) goto L_0x02b5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x0271, code lost:
        r3 = new android.content.Intent("android.intent.action.USER_STARTED");
        r3.addFlags(1342177280);
        r3.putExtra("android.intent.extra.user_handle", r15);
        r30 = r11;
        r31 = r12;
        r1.mInjector.broadcastIntent(r3, (java.lang.String) null, (android.content.IIntentReceiver) null, 0, (java.lang.String) null, (android.os.Bundle) null, (java.lang.String[]) null, -1, (android.os.Bundle) null, false, false, com.android.server.am.ActivityManagerService.MY_PID, 1000, r22, r23, r33);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x02b5, code lost:
        r30 = r11;
        r31 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x02b9, code lost:
        if (r34 == false) goto L_0x02c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x02bb, code lost:
        r3 = r33;
        r14 = r30;
        r15 = r31;
        moveUserToForeground(r14, r15, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x02c5, code lost:
        r3 = r33;
        r14 = r30;
        r15 = r31;
        finishUserBoot(r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x02ce, code lost:
        if (r2 == false) goto L_0x031a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x02d0, code lost:
        r13 = new android.content.Intent("android.intent.action.USER_STARTING");
        r13.addFlags(1073741824);
        r13.putExtra("android.intent.extra.user_handle", r3);
        r29 = r13;
        r30 = r14;
        r31 = r15;
        r1.mInjector.broadcastIntent(r13, (java.lang.String) null, new com.android.server.am.UserController.AnonymousClass6(r1), 0, (java.lang.String) null, (android.os.Bundle) null, new java.lang.String[]{"android.permission.INTERACT_ACROSS_USERS"}, -1, (android.os.Bundle) null, true, false, com.android.server.am.ActivityManagerService.MY_PID, 1000, r22, r23, -1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x031a, code lost:
        r30 = r14;
        r31 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x031e, code lost:
        android.os.Binder.restoreCallingIdentity(r24);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x0322, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x0323, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x0324, code lost:
        r3 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x032d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x033e, code lost:
        android.os.Binder.restoreCallingIdentity(r24);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x0341, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0151, code lost:
        if (r13 == null) goto L_0x0158;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:?, code lost:
        r11.mUnlockProgress.addListener(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0158, code lost:
        if (r27 == false) goto L_0x0165;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x015a, code lost:
        r1.mInjector.getUserManagerInternal().setUserState(r15, r11.state);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0165, code lost:
        if (r14 == false) goto L_0x01ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0167, code lost:
        r1.mInjector.reportGlobalUsageEventLocked(16);
        r2 = r1.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0170, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:?, code lost:
        r1.mCurrentUserId = r15;
        r1.mTargetUserId = com.android.server.wm.ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0177, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:?, code lost:
        r1.mInjector.updateUserConfiguration();
        updateCurrentProfileIds();
        r1.mInjector.reportCurWakefulnessUsageEvent();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0187, code lost:
        if (r1.mUserSwitchUiEnabled == false) goto L_0x01a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0189, code lost:
        r1.mInjector.getWindowManager().setSwitchingUser(true);
        com.android.server.am.ActivityManagerServiceInjector.handleWindowManagerAndUserLru(r1.mInjector.getContext(), r33, r4, r12, r1.mInjector.getWindowManager(), getCurrentProfileIds());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x01a9, code lost:
        r1.mInjector.getWindowManager().setCurrentUser(r15, getCurrentProfileIds());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x01ba, code lost:
        r2 = java.lang.Integer.valueOf(r1.mCurrentUserId);
        updateCurrentProfileIds();
        r1.mInjector.getWindowManager().setCurrentProfileIds(getCurrentProfileIds());
        r3 = r1.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x01d2, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:?, code lost:
        r1.mUserLru.remove(r2);
        r1.mUserLru.add(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x01dd, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x01e1, code lost:
        if (r11.state != 4) goto L_0x0200;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:?, code lost:
        r11.setState(r11.lastState);
        r1.mInjector.getUserManagerInternal().setUserState(r15, r11.state);
        r2 = r1.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x01f5, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:?, code lost:
        updateStartedUserArrayLU();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x01f9, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x01fa, code lost:
        r2 = true;
     */
    /* renamed from: startUser */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean lambda$startUser$9$UserController(int r33, boolean r34, android.os.IProgressListener r35) {
        /*
            r32 = this;
            r1 = r32
            r15 = r33
            r14 = r34
            r13 = r35
            r4 = r33
            com.android.server.am.UserController$Injector r0 = r1.mInjector
            java.lang.String r2 = "android.permission.INTERACT_ACROSS_USERS_FULL"
            int r0 = r0.checkCallingPermission(r2)
            if (r0 != 0) goto L_0x0342
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Starting userid:"
            r0.append(r2)
            r0.append(r15)
            java.lang.String r2 = " fg:"
            r0.append(r2)
            r0.append(r14)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "ActivityManager"
            android.util.Slog.i(r2, r0)
            int r22 = android.os.Binder.getCallingUid()
            int r23 = android.os.Binder.getCallingPid()
            long r24 = android.os.Binder.clearCallingIdentity()
            int r0 = r32.getCurrentUserId()     // Catch:{ all -> 0x033d }
            r12 = r0
            r0 = 1
            if (r12 != r15) goto L_0x0068
            com.android.server.am.UserState r2 = r32.getStartedUserState(r33)     // Catch:{ all -> 0x033d }
            if (r2 != 0) goto L_0x0054
            java.lang.String r3 = "ActivityManager"
            java.lang.String r5 = "Current user has no UserState"
            android.util.Slog.wtf(r3, r5)     // Catch:{ all -> 0x033d }
            goto L_0x0068
        L_0x0054:
            if (r15 != 0) goto L_0x005b
            int r3 = r2.state     // Catch:{ all -> 0x033d }
            if (r3 != 0) goto L_0x005b
            goto L_0x0068
        L_0x005b:
            int r3 = r2.state     // Catch:{ all -> 0x033d }
            r5 = 3
            if (r3 != r5) goto L_0x0063
            notifyFinished(r15, r13)     // Catch:{ all -> 0x033d }
        L_0x0063:
            android.os.Binder.restoreCallingIdentity(r24)
            return r0
        L_0x0068:
            if (r14 == 0) goto L_0x0072
            com.android.server.am.UserController$Injector r2 = r1.mInjector     // Catch:{ all -> 0x033d }
            java.lang.String r3 = "startUser"
            r2.clearAllLockedTasks(r3)     // Catch:{ all -> 0x033d }
        L_0x0072:
            android.content.pm.UserInfo r2 = r32.getUserInfo(r33)     // Catch:{ all -> 0x033d }
            r26 = r2
            r8 = 0
            if (r26 != 0) goto L_0x0096
            java.lang.String r0 = "ActivityManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x033d }
            r2.<init>()     // Catch:{ all -> 0x033d }
            java.lang.String r3 = "No user info for user #"
            r2.append(r3)     // Catch:{ all -> 0x033d }
            r2.append(r15)     // Catch:{ all -> 0x033d }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x033d }
            android.util.Slog.w(r0, r2)     // Catch:{ all -> 0x033d }
            android.os.Binder.restoreCallingIdentity(r24)
            return r8
        L_0x0096:
            if (r14 == 0) goto L_0x00be
            boolean r2 = r26.isManagedProfile()     // Catch:{ all -> 0x033d }
            if (r2 == 0) goto L_0x00be
            java.lang.String r0 = "ActivityManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x033d }
            r2.<init>()     // Catch:{ all -> 0x033d }
            java.lang.String r3 = "Cannot switch to User #"
            r2.append(r3)     // Catch:{ all -> 0x033d }
            r2.append(r15)     // Catch:{ all -> 0x033d }
            java.lang.String r3 = ": not a full user"
            r2.append(r3)     // Catch:{ all -> 0x033d }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x033d }
            android.util.Slog.w(r0, r2)     // Catch:{ all -> 0x033d }
            android.os.Binder.restoreCallingIdentity(r24)
            return r8
        L_0x00be:
            if (r14 == 0) goto L_0x00d0
            boolean r2 = r1.mUserSwitchUiEnabled     // Catch:{ all -> 0x033d }
            if (r2 == 0) goto L_0x00d0
            com.android.server.am.UserController$Injector r2 = r1.mInjector     // Catch:{ all -> 0x033d }
            com.android.server.wm.WindowManagerService r2 = r2.getWindowManager()     // Catch:{ all -> 0x033d }
            r3 = 17432870(0x10a0126, float:2.534742E-38)
            r2.startFreezingScreen(r3, r8)     // Catch:{ all -> 0x033d }
        L_0x00d0:
            r2 = 0
            r3 = 0
            java.lang.Object r5 = r1.mLock     // Catch:{ all -> 0x033d }
            monitor-enter(r5)     // Catch:{ all -> 0x033d }
            android.util.SparseArray<com.android.server.am.UserState> r6 = r1.mStartedUsers     // Catch:{ all -> 0x0336 }
            java.lang.Object r6 = r6.get(r15)     // Catch:{ all -> 0x0336 }
            com.android.server.am.UserState r6 = (com.android.server.am.UserState) r6     // Catch:{ all -> 0x0336 }
            r9 = 5
            if (r6 != 0) goto L_0x0109
            com.android.server.am.UserState r7 = new com.android.server.am.UserState     // Catch:{ all -> 0x0104 }
            android.os.UserHandle r10 = android.os.UserHandle.of(r33)     // Catch:{ all -> 0x0104 }
            r7.<init>(r10)     // Catch:{ all -> 0x0104 }
            r6 = r7
            com.android.internal.util.ProgressReporter r7 = r6.mUnlockProgress     // Catch:{ all -> 0x0104 }
            com.android.server.am.UserController$UserProgressListener r10 = new com.android.server.am.UserController$UserProgressListener     // Catch:{ all -> 0x0104 }
            r11 = 0
            r10.<init>()     // Catch:{ all -> 0x0104 }
            r7.addListener(r10)     // Catch:{ all -> 0x0104 }
            android.util.SparseArray<com.android.server.am.UserState> r7 = r1.mStartedUsers     // Catch:{ all -> 0x0104 }
            r7.put(r15, r6)     // Catch:{ all -> 0x0104 }
            r32.updateStartedUserArrayLU()     // Catch:{ all -> 0x0104 }
            r2 = 1
            r3 = 1
            r10 = r2
            r27 = r3
            r11 = r6
            goto L_0x0141
        L_0x0104:
            r0 = move-exception
            r31 = r12
            goto L_0x0339
        L_0x0109:
            int r7 = r6.state     // Catch:{ all -> 0x0336 }
            if (r7 != r9) goto L_0x013d
            boolean r7 = r32.isCallingOnHandlerThread()     // Catch:{ all -> 0x0104 }
            if (r7 != 0) goto L_0x013d
            java.lang.String r7 = "ActivityManager"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0104 }
            r8.<init>()     // Catch:{ all -> 0x0104 }
            java.lang.String r9 = "User #"
            r8.append(r9)     // Catch:{ all -> 0x0104 }
            r8.append(r15)     // Catch:{ all -> 0x0104 }
            java.lang.String r9 = " is shutting down - will start after full stop"
            r8.append(r9)     // Catch:{ all -> 0x0104 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0104 }
            android.util.Slog.i(r7, r8)     // Catch:{ all -> 0x0104 }
            android.os.Handler r7 = r1.mHandler     // Catch:{ all -> 0x0104 }
            com.android.server.am.-$$Lambda$UserController$iUhPl1IaxAC7-Q7kTU8VNTT3nUc r8 = new com.android.server.am.-$$Lambda$UserController$iUhPl1IaxAC7-Q7kTU8VNTT3nUc     // Catch:{ all -> 0x0104 }
            r8.<init>(r15, r14, r13)     // Catch:{ all -> 0x0104 }
            r7.post(r8)     // Catch:{ all -> 0x0104 }
            monitor-exit(r5)     // Catch:{ all -> 0x0104 }
            android.os.Binder.restoreCallingIdentity(r24)
            return r0
        L_0x013d:
            r10 = r2
            r27 = r3
            r11 = r6
        L_0x0141:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r33)     // Catch:{ all -> 0x032f }
            java.util.ArrayList<java.lang.Integer> r3 = r1.mUserLru     // Catch:{ all -> 0x032f }
            r3.remove(r2)     // Catch:{ all -> 0x032f }
            java.util.ArrayList<java.lang.Integer> r3 = r1.mUserLru     // Catch:{ all -> 0x032f }
            r3.add(r2)     // Catch:{ all -> 0x032f }
            monitor-exit(r5)     // Catch:{ all -> 0x032f }
            if (r13 == 0) goto L_0x0158
            com.android.internal.util.ProgressReporter r2 = r11.mUnlockProgress     // Catch:{ all -> 0x033d }
            r2.addListener(r13)     // Catch:{ all -> 0x033d }
        L_0x0158:
            if (r27 == 0) goto L_0x0165
            com.android.server.am.UserController$Injector r2 = r1.mInjector     // Catch:{ all -> 0x033d }
            android.os.UserManagerInternal r2 = r2.getUserManagerInternal()     // Catch:{ all -> 0x033d }
            int r3 = r11.state     // Catch:{ all -> 0x033d }
            r2.setUserState(r15, r3)     // Catch:{ all -> 0x033d }
        L_0x0165:
            if (r14 == 0) goto L_0x01ba
            com.android.server.am.UserController$Injector r2 = r1.mInjector     // Catch:{ all -> 0x033d }
            r3 = 16
            r2.reportGlobalUsageEventLocked(r3)     // Catch:{ all -> 0x033d }
            java.lang.Object r2 = r1.mLock     // Catch:{ all -> 0x033d }
            monitor-enter(r2)     // Catch:{ all -> 0x033d }
            r1.mCurrentUserId = r15     // Catch:{ all -> 0x01b7 }
            r3 = -10000(0xffffffffffffd8f0, float:NaN)
            r1.mTargetUserId = r3     // Catch:{ all -> 0x01b7 }
            monitor-exit(r2)     // Catch:{ all -> 0x01b7 }
            com.android.server.am.UserController$Injector r2 = r1.mInjector     // Catch:{ all -> 0x033d }
            r2.updateUserConfiguration()     // Catch:{ all -> 0x033d }
            r32.updateCurrentProfileIds()     // Catch:{ all -> 0x033d }
            com.android.server.am.UserController$Injector r2 = r1.mInjector     // Catch:{ all -> 0x033d }
            r2.reportCurWakefulnessUsageEvent()     // Catch:{ all -> 0x033d }
            boolean r2 = r1.mUserSwitchUiEnabled     // Catch:{ all -> 0x033d }
            if (r2 == 0) goto L_0x01a9
            com.android.server.am.UserController$Injector r2 = r1.mInjector     // Catch:{ all -> 0x033d }
            com.android.server.wm.WindowManagerService r2 = r2.getWindowManager()     // Catch:{ all -> 0x033d }
            r2.setSwitchingUser(r0)     // Catch:{ all -> 0x033d }
            com.android.server.am.UserController$Injector r2 = r1.mInjector     // Catch:{ all -> 0x033d }
            android.content.Context r2 = r2.getContext()     // Catch:{ all -> 0x033d }
            com.android.server.am.UserController$Injector r3 = r1.mInjector     // Catch:{ all -> 0x033d }
            com.android.server.wm.WindowManagerService r6 = r3.getWindowManager()     // Catch:{ all -> 0x033d }
            int[] r7 = r32.getCurrentProfileIds()     // Catch:{ all -> 0x033d }
            r3 = r33
            r5 = r12
            com.android.server.am.ActivityManagerServiceInjector.handleWindowManagerAndUserLru(r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x033d }
            goto L_0x01de
        L_0x01a9:
            com.android.server.am.UserController$Injector r2 = r1.mInjector     // Catch:{ all -> 0x033d }
            com.android.server.wm.WindowManagerService r2 = r2.getWindowManager()     // Catch:{ all -> 0x033d }
            int[] r3 = r32.getCurrentProfileIds()     // Catch:{ all -> 0x033d }
            r2.setCurrentUser(r15, r3)     // Catch:{ all -> 0x033d }
            goto L_0x01de
        L_0x01b7:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x01b7 }
            throw r0     // Catch:{ all -> 0x033d }
        L_0x01ba:
            int r2 = r1.mCurrentUserId     // Catch:{ all -> 0x033d }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x033d }
            r32.updateCurrentProfileIds()     // Catch:{ all -> 0x033d }
            com.android.server.am.UserController$Injector r3 = r1.mInjector     // Catch:{ all -> 0x033d }
            com.android.server.wm.WindowManagerService r3 = r3.getWindowManager()     // Catch:{ all -> 0x033d }
            int[] r5 = r32.getCurrentProfileIds()     // Catch:{ all -> 0x033d }
            r3.setCurrentProfileIds(r5)     // Catch:{ all -> 0x033d }
            java.lang.Object r3 = r1.mLock     // Catch:{ all -> 0x033d }
            monitor-enter(r3)     // Catch:{ all -> 0x033d }
            java.util.ArrayList<java.lang.Integer> r5 = r1.mUserLru     // Catch:{ all -> 0x0326 }
            r5.remove(r2)     // Catch:{ all -> 0x0326 }
            java.util.ArrayList<java.lang.Integer> r5 = r1.mUserLru     // Catch:{ all -> 0x0326 }
            r5.add(r2)     // Catch:{ all -> 0x0326 }
            monitor-exit(r3)     // Catch:{ all -> 0x0326 }
        L_0x01de:
            int r2 = r11.state     // Catch:{ all -> 0x0323 }
            r3 = 4
            if (r2 != r3) goto L_0x0200
            int r2 = r11.lastState     // Catch:{ all -> 0x033d }
            r11.setState(r2)     // Catch:{ all -> 0x033d }
            com.android.server.am.UserController$Injector r2 = r1.mInjector     // Catch:{ all -> 0x033d }
            android.os.UserManagerInternal r2 = r2.getUserManagerInternal()     // Catch:{ all -> 0x033d }
            int r3 = r11.state     // Catch:{ all -> 0x033d }
            r2.setUserState(r15, r3)     // Catch:{ all -> 0x033d }
            java.lang.Object r2 = r1.mLock     // Catch:{ all -> 0x033d }
            monitor-enter(r2)     // Catch:{ all -> 0x033d }
            r32.updateStartedUserArrayLU()     // Catch:{ all -> 0x01fd }
            monitor-exit(r2)     // Catch:{ all -> 0x01fd }
            r10 = 1
            r2 = r10
            goto L_0x0220
        L_0x01fd:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x01fd }
            throw r0     // Catch:{ all -> 0x033d }
        L_0x0200:
            int r2 = r11.state     // Catch:{ all -> 0x0323 }
            if (r2 != r9) goto L_0x021f
            r11.setState(r8)     // Catch:{ all -> 0x033d }
            com.android.server.am.UserController$Injector r2 = r1.mInjector     // Catch:{ all -> 0x033d }
            android.os.UserManagerInternal r2 = r2.getUserManagerInternal()     // Catch:{ all -> 0x033d }
            int r3 = r11.state     // Catch:{ all -> 0x033d }
            r2.setUserState(r15, r3)     // Catch:{ all -> 0x033d }
            java.lang.Object r2 = r1.mLock     // Catch:{ all -> 0x033d }
            monitor-enter(r2)     // Catch:{ all -> 0x033d }
            r32.updateStartedUserArrayLU()     // Catch:{ all -> 0x021c }
            monitor-exit(r2)     // Catch:{ all -> 0x021c }
            r10 = 1
            r2 = r10
            goto L_0x0220
        L_0x021c:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x021c }
            throw r0     // Catch:{ all -> 0x033d }
        L_0x021f:
            r2 = r10
        L_0x0220:
            int r3 = r11.state     // Catch:{ all -> 0x0323 }
            if (r3 != 0) goto L_0x023a
            com.android.server.am.UserController$Injector r3 = r1.mInjector     // Catch:{ all -> 0x033d }
            com.android.server.pm.UserManagerService r3 = r3.getUserManager()     // Catch:{ all -> 0x033d }
            r3.onBeforeStartUser(r15)     // Catch:{ all -> 0x033d }
            android.os.Handler r3 = r1.mHandler     // Catch:{ all -> 0x033d }
            android.os.Handler r5 = r1.mHandler     // Catch:{ all -> 0x033d }
            r6 = 50
            android.os.Message r5 = r5.obtainMessage(r6, r15, r8)     // Catch:{ all -> 0x033d }
            r3.sendMessage(r5)     // Catch:{ all -> 0x033d }
        L_0x023a:
            if (r14 == 0) goto L_0x026f
            android.os.Handler r3 = r1.mHandler     // Catch:{ all -> 0x033d }
            android.os.Handler r5 = r1.mHandler     // Catch:{ all -> 0x033d }
            r6 = 60
            android.os.Message r5 = r5.obtainMessage(r6, r15, r12)     // Catch:{ all -> 0x033d }
            r3.sendMessage(r5)     // Catch:{ all -> 0x033d }
            android.os.Handler r3 = r1.mHandler     // Catch:{ all -> 0x033d }
            r5 = 10
            r3.removeMessages(r5)     // Catch:{ all -> 0x033d }
            android.os.Handler r3 = r1.mHandler     // Catch:{ all -> 0x033d }
            r6 = 30
            r3.removeMessages(r6)     // Catch:{ all -> 0x033d }
            android.os.Handler r3 = r1.mHandler     // Catch:{ all -> 0x033d }
            android.os.Handler r7 = r1.mHandler     // Catch:{ all -> 0x033d }
            android.os.Message r5 = r7.obtainMessage(r5, r12, r15, r11)     // Catch:{ all -> 0x033d }
            r3.sendMessage(r5)     // Catch:{ all -> 0x033d }
            android.os.Handler r3 = r1.mHandler     // Catch:{ all -> 0x033d }
            android.os.Handler r5 = r1.mHandler     // Catch:{ all -> 0x033d }
            android.os.Message r5 = r5.obtainMessage(r6, r12, r15, r11)     // Catch:{ all -> 0x033d }
            r6 = 3000(0xbb8, double:1.482E-320)
            r3.sendMessageDelayed(r5, r6)     // Catch:{ all -> 0x033d }
        L_0x026f:
            if (r2 == 0) goto L_0x02b5
            android.content.Intent r3 = new android.content.Intent     // Catch:{ all -> 0x033d }
            java.lang.String r5 = "android.intent.action.USER_STARTED"
            r3.<init>(r5)     // Catch:{ all -> 0x033d }
            r5 = 1342177280(0x50000000, float:8.5899346E9)
            r3.addFlags(r5)     // Catch:{ all -> 0x033d }
            java.lang.String r5 = "android.intent.extra.user_handle"
            r3.putExtra(r5, r15)     // Catch:{ all -> 0x033d }
            com.android.server.am.UserController$Injector r5 = r1.mInjector     // Catch:{ all -> 0x033d }
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r16 = 0
            r17 = 0
            r19 = -1
            r20 = 0
            r21 = 0
            r28 = 0
            int r29 = com.android.server.am.ActivityManagerService.MY_PID     // Catch:{ all -> 0x033d }
            r18 = 1000(0x3e8, float:1.401E-42)
            r6 = r3
            r30 = r11
            r11 = r16
            r31 = r12
            r12 = r17
            r13 = r19
            r14 = r20
            r15 = r21
            r16 = r28
            r17 = r29
            r19 = r22
            r20 = r23
            r21 = r33
            r5.broadcastIntent(r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)     // Catch:{ all -> 0x033d }
            goto L_0x02b9
        L_0x02b5:
            r30 = r11
            r31 = r12
        L_0x02b9:
            if (r34 == 0) goto L_0x02c5
            r3 = r33
            r14 = r30
            r15 = r31
            r1.moveUserToForeground(r14, r15, r3)     // Catch:{ all -> 0x033d }
            goto L_0x02ce
        L_0x02c5:
            r3 = r33
            r14 = r30
            r15 = r31
            r1.finishUserBoot(r14)     // Catch:{ all -> 0x033d }
        L_0x02ce:
            if (r2 == 0) goto L_0x031a
            android.content.Intent r5 = new android.content.Intent     // Catch:{ all -> 0x033d }
            java.lang.String r6 = "android.intent.action.USER_STARTING"
            r5.<init>(r6)     // Catch:{ all -> 0x033d }
            r13 = r5
            r5 = 1073741824(0x40000000, float:2.0)
            r13.addFlags(r5)     // Catch:{ all -> 0x033d }
            java.lang.String r5 = "android.intent.extra.user_handle"
            r13.putExtra(r5, r3)     // Catch:{ all -> 0x033d }
            com.android.server.am.UserController$Injector r5 = r1.mInjector     // Catch:{ all -> 0x033d }
            r7 = 0
            com.android.server.am.UserController$6 r8 = new com.android.server.am.UserController$6     // Catch:{ all -> 0x033d }
            r8.<init>()     // Catch:{ all -> 0x033d }
            r9 = 0
            r10 = 0
            r11 = 0
            java.lang.String r6 = "android.permission.INTERACT_ACROSS_USERS"
            java.lang.String[] r12 = new java.lang.String[]{r6}     // Catch:{ all -> 0x033d }
            r16 = -1
            r17 = 0
            r19 = 1
            r20 = 0
            int r28 = com.android.server.am.ActivityManagerService.MY_PID     // Catch:{ all -> 0x033d }
            r18 = 1000(0x3e8, float:1.401E-42)
            r21 = -1
            r6 = r13
            r29 = r13
            r13 = r16
            r30 = r14
            r14 = r17
            r31 = r15
            r15 = r19
            r16 = r20
            r17 = r28
            r19 = r22
            r20 = r23
            r5.broadcastIntent(r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)     // Catch:{ all -> 0x033d }
            goto L_0x031e
        L_0x031a:
            r30 = r14
            r31 = r15
        L_0x031e:
            android.os.Binder.restoreCallingIdentity(r24)
            return r0
        L_0x0323:
            r0 = move-exception
            r3 = r15
            goto L_0x033e
        L_0x0326:
            r0 = move-exception
            r30 = r11
            r31 = r12
        L_0x032b:
            monitor-exit(r3)     // Catch:{ all -> 0x032d }
            throw r0     // Catch:{ all -> 0x033d }
        L_0x032d:
            r0 = move-exception
            goto L_0x032b
        L_0x032f:
            r0 = move-exception
            r31 = r12
            r2 = r10
            r3 = r27
            goto L_0x0339
        L_0x0336:
            r0 = move-exception
            r31 = r12
        L_0x0339:
            monitor-exit(r5)     // Catch:{ all -> 0x033b }
            throw r0     // Catch:{ all -> 0x033d }
        L_0x033b:
            r0 = move-exception
            goto L_0x0339
        L_0x033d:
            r0 = move-exception
        L_0x033e:
            android.os.Binder.restoreCallingIdentity(r24)
            throw r0
        L_0x0342:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Permission Denial: switchUser() from pid="
            r0.append(r2)
            int r2 = android.os.Binder.getCallingPid()
            r0.append(r2)
            java.lang.String r2 = ", uid="
            r0.append(r2)
            int r2 = android.os.Binder.getCallingUid()
            r0.append(r2)
            java.lang.String r2 = " requires "
            r0.append(r2)
            java.lang.String r2 = "android.permission.INTERACT_ACROSS_USERS_FULL"
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "ActivityManager"
            android.util.Slog.w(r2, r0)
            java.lang.SecurityException r2 = new java.lang.SecurityException
            r2.<init>(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.UserController.lambda$startUser$9$UserController(int, boolean, android.os.IProgressListener):boolean");
    }

    private boolean isCallingOnHandlerThread() {
        return Looper.myLooper() == this.mHandler.getLooper();
    }

    /* access modifiers changed from: package-private */
    public void startUserInForeground(int targetUserId) {
        if (!startUser(targetUserId, true)) {
            this.mInjector.getWindowManager().setSwitchingUser(false);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean unlockUser(int userId, byte[] token, byte[] secret, IProgressListener listener) {
        if (this.mInjector.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0) {
            long binderToken = Binder.clearCallingIdentity();
            try {
                return unlockUserCleared(userId, token, secret, listener);
            } finally {
                Binder.restoreCallingIdentity(binderToken);
            }
        } else {
            String msg = "Permission Denial: unlockUser() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.INTERACT_ACROSS_USERS_FULL";
            Slog.w(TAG, msg);
            throw new SecurityException(msg);
        }
    }

    private boolean maybeUnlockUser(int userId) {
        return unlockUserCleared(userId, (byte[]) null, (byte[]) null, (IProgressListener) null);
    }

    private static void notifyFinished(int userId, IProgressListener listener) {
        if (listener != null) {
            try {
                listener.onFinished(userId, (Bundle) null);
            } catch (RemoteException e) {
            }
        }
    }

    private boolean unlockUserCleared(int userId, byte[] token, byte[] secret, IProgressListener listener) {
        UserState uss;
        int[] userIds;
        if (!StorageManager.isUserKeyUnlocked(userId)) {
            try {
                this.mInjector.getStorageManager().unlockUserKey(userId, getUserInfo(userId).serialNumber, token, secret);
            } catch (RemoteException | RuntimeException e) {
                Slog.w(TAG, "Failed to unlock: " + e.getMessage());
                return false;
            }
        }
        synchronized (this.mLock) {
            uss = this.mStartedUsers.get(userId);
            if (uss != null) {
                uss.mUnlockProgress.addListener(listener);
                uss.tokenProvided = token != null;
            }
        }
        if (uss == null) {
            notifyFinished(userId, listener);
            return false;
        } else if (!finishUserUnlocking(uss)) {
            notifyFinished(userId, listener);
            return false;
        } else {
            synchronized (this.mLock) {
                userIds = new int[this.mStartedUsers.size()];
                for (int i = 0; i < userIds.length; i++) {
                    userIds[i] = this.mStartedUsers.keyAt(i);
                }
            }
            for (int testUserId : userIds) {
                UserInfo parent = this.mInjector.getUserManager().getProfileParent(testUserId);
                if (!(parent == null || parent.id != userId || testUserId == userId)) {
                    Slog.d(TAG, "User " + testUserId + " (parent " + parent.id + "): attempting unlock because parent was just unlocked");
                    maybeUnlockUser(testUserId);
                }
            }
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean switchUser(int targetUserId) {
        enforceShellRestriction("no_debugging_features", targetUserId);
        int currentUserId = getCurrentUserId();
        UserInfo targetUserInfo = getUserInfo(targetUserId);
        if (targetUserId == currentUserId) {
            Slog.i(TAG, "user #" + targetUserId + " is already the current user");
            return true;
        } else if (targetUserInfo == null) {
            Slog.w(TAG, "No user info for user #" + targetUserId);
            return false;
        } else if (!targetUserInfo.supportsSwitchTo()) {
            Slog.w(TAG, "Cannot switch to User #" + targetUserId + ": not supported");
            return false;
        } else if (targetUserInfo.isManagedProfile()) {
            Slog.w(TAG, "Cannot switch to User #" + targetUserId + ": not a full user");
            return false;
        } else {
            synchronized (this.mLock) {
                this.mTargetUserId = targetUserId;
            }
            if (this.mUserSwitchUiEnabled) {
                new Pair(getUserInfo(currentUserId), targetUserInfo);
                this.mUiHandler.removeMessages(1000);
                if (!ActivityManagerServiceInjector.showSwitchingDialog(this.mInjector.getService(), targetUserId, this.mUiHandler)) {
                    this.mInjector.getService().mUserController.startUser(targetUserId, true);
                }
            } else {
                this.mHandler.removeMessages(START_USER_SWITCH_FG_MSG);
                Handler handler = this.mHandler;
                handler.sendMessage(handler.obtainMessage(START_USER_SWITCH_FG_MSG, targetUserId, 0));
            }
            return true;
        }
    }

    private void showUserSwitchDialog(Pair<UserInfo, UserInfo> fromToUserPair) {
        this.mInjector.showUserSwitchingDialog((UserInfo) fromToUserPair.first, (UserInfo) fromToUserPair.second, getSwitchingFromSystemUserMessage(), getSwitchingToSystemUserMessage());
    }

    private void dispatchForegroundProfileChanged(int userId) {
        int observerCount = this.mUserSwitchObservers.beginBroadcast();
        for (int i = 0; i < observerCount; i++) {
            try {
                this.mUserSwitchObservers.getBroadcastItem(i).onForegroundProfileSwitch(userId);
            } catch (RemoteException e) {
            }
        }
        this.mUserSwitchObservers.finishBroadcast();
    }

    /* access modifiers changed from: package-private */
    public void dispatchUserSwitchComplete(int userId) {
        this.mInjector.getWindowManager().setSwitchingUser(false);
        int observerCount = this.mUserSwitchObservers.beginBroadcast();
        for (int i = 0; i < observerCount; i++) {
            try {
                this.mUserSwitchObservers.getBroadcastItem(i).onUserSwitchComplete(userId);
            } catch (RemoteException e) {
            }
        }
        this.mUserSwitchObservers.finishBroadcast();
    }

    private void dispatchLockedBootComplete(int userId) {
        int observerCount = this.mUserSwitchObservers.beginBroadcast();
        for (int i = 0; i < observerCount; i++) {
            try {
                this.mUserSwitchObservers.getBroadcastItem(i).onLockedBootComplete(userId);
            } catch (RemoteException e) {
            }
        }
        this.mUserSwitchObservers.finishBroadcast();
    }

    private void stopBackgroundUsersIfEnforced(int oldUserId) {
        if (oldUserId != 0) {
            if (hasUserRestriction("no_run_in_background", oldUserId) || this.mDelayUserDataLocking) {
                synchronized (this.mLock) {
                    stopUsersLU(oldUserId, false, (IStopUserCallback) null, (UserState.KeyEvictedCallback) null);
                }
            }
        }
    }

    private void timeoutUserSwitch(UserState uss, int oldUserId, int newUserId) {
        synchronized (this.mLock) {
            Slog.e(TAG, "User switch timeout: from " + oldUserId + " to " + newUserId);
            this.mTimeoutUserSwitchCallbacks = this.mCurWaitingUserSwitchCallbacks;
            this.mHandler.removeMessages(USER_SWITCH_CALLBACKS_TIMEOUT_MSG);
            sendContinueUserSwitchLU(uss, oldUserId, newUserId);
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(USER_SWITCH_CALLBACKS_TIMEOUT_MSG, oldUserId, newUserId), 5000);
        }
    }

    private void timeoutUserSwitchCallbacks(int oldUserId, int newUserId) {
        synchronized (this.mLock) {
            if (this.mTimeoutUserSwitchCallbacks != null && !this.mTimeoutUserSwitchCallbacks.isEmpty()) {
                Slog.wtf(TAG, "User switch timeout: from " + oldUserId + " to " + newUserId + ". Observers that didn't respond: " + this.mTimeoutUserSwitchCallbacks);
                this.mTimeoutUserSwitchCallbacks = null;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x009a, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchUserSwitch(com.android.server.am.UserState r20, int r21, int r22) {
        /*
            r19 = this;
            r11 = r19
            r12 = r22
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Dispatch onUserSwitching oldUser #"
            r0.append(r1)
            r13 = r21
            r0.append(r13)
            java.lang.String r1 = " newUser #"
            r0.append(r1)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "ActivityManager"
            android.util.Slog.d(r1, r0)
            android.os.RemoteCallbackList<android.app.IUserSwitchObserver> r0 = r11.mUserSwitchObservers
            int r14 = r0.beginBroadcast()
            if (r14 <= 0) goto L_0x00aa
            android.util.ArraySet r0 = new android.util.ArraySet
            r0.<init>()
            r15 = r0
            java.lang.Object r1 = r11.mLock
            monitor-enter(r1)
            r0 = 1
            r10 = r20
            r10.switching = r0     // Catch:{ all -> 0x00a7 }
            r11.mCurWaitingUserSwitchCallbacks = r15     // Catch:{ all -> 0x00a7 }
            monitor-exit(r1)     // Catch:{ all -> 0x00a7 }
            java.util.concurrent.atomic.AtomicInteger r7 = new java.util.concurrent.atomic.AtomicInteger
            r7.<init>(r14)
            long r16 = android.os.SystemClock.elapsedRealtime()
            r0 = 0
            r9 = r0
        L_0x0048:
            if (r9 >= r14) goto L_0x00a5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x009c }
            r0.<init>()     // Catch:{ RemoteException -> 0x009c }
            java.lang.String r1 = "#"
            r0.append(r1)     // Catch:{ RemoteException -> 0x009c }
            r0.append(r9)     // Catch:{ RemoteException -> 0x009c }
            java.lang.String r1 = " "
            r0.append(r1)     // Catch:{ RemoteException -> 0x009c }
            android.os.RemoteCallbackList<android.app.IUserSwitchObserver> r1 = r11.mUserSwitchObservers     // Catch:{ RemoteException -> 0x009c }
            java.lang.Object r1 = r1.getBroadcastCookie(r9)     // Catch:{ RemoteException -> 0x009c }
            r0.append(r1)     // Catch:{ RemoteException -> 0x009c }
            java.lang.String r0 = r0.toString()     // Catch:{ RemoteException -> 0x009c }
            r8 = r0
            java.lang.Object r1 = r11.mLock     // Catch:{ RemoteException -> 0x009c }
            monitor-enter(r1)     // Catch:{ RemoteException -> 0x009c }
            r15.add(r8)     // Catch:{ all -> 0x0092 }
            monitor-exit(r1)     // Catch:{ all -> 0x0092 }
            com.android.server.am.UserController$7 r0 = new com.android.server.am.UserController$7     // Catch:{ RemoteException -> 0x009c }
            r1 = r0
            r2 = r19
            r3 = r16
            r5 = r8
            r6 = r15
            r18 = r8
            r8 = r20
            r13 = r9
            r9 = r21
            r10 = r22
            r1.<init>(r3, r5, r6, r7, r8, r9, r10)     // Catch:{ RemoteException -> 0x0098 }
            android.os.RemoteCallbackList<android.app.IUserSwitchObserver> r1 = r11.mUserSwitchObservers     // Catch:{ RemoteException -> 0x0098 }
            android.os.IInterface r1 = r1.getBroadcastItem(r13)     // Catch:{ RemoteException -> 0x0098 }
            android.app.IUserSwitchObserver r1 = (android.app.IUserSwitchObserver) r1     // Catch:{ RemoteException -> 0x0098 }
            r1.onUserSwitching(r12, r0)     // Catch:{ RemoteException -> 0x0098 }
            goto L_0x009e
        L_0x0092:
            r0 = move-exception
            r18 = r8
            r13 = r9
        L_0x0096:
            monitor-exit(r1)     // Catch:{ all -> 0x009a }
            throw r0     // Catch:{ RemoteException -> 0x0098 }
        L_0x0098:
            r0 = move-exception
            goto L_0x009e
        L_0x009a:
            r0 = move-exception
            goto L_0x0096
        L_0x009c:
            r0 = move-exception
            r13 = r9
        L_0x009e:
            int r9 = r13 + 1
            r10 = r20
            r13 = r21
            goto L_0x0048
        L_0x00a5:
            r13 = r9
            goto L_0x00b1
        L_0x00a7:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00a7 }
            throw r0
        L_0x00aa:
            java.lang.Object r1 = r11.mLock
            monitor-enter(r1)
            r19.sendContinueUserSwitchLU(r20, r21, r22)     // Catch:{ all -> 0x00b7 }
            monitor-exit(r1)     // Catch:{ all -> 0x00b7 }
        L_0x00b1:
            android.os.RemoteCallbackList<android.app.IUserSwitchObserver> r0 = r11.mUserSwitchObservers
            r0.finishBroadcast()
            return
        L_0x00b7:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00b7 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.UserController.dispatchUserSwitch(com.android.server.am.UserState, int, int):void");
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void sendContinueUserSwitchLU(UserState uss, int oldUserId, int newUserId) {
        this.mCurWaitingUserSwitchCallbacks = null;
        this.mHandler.removeMessages(30);
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(20, oldUserId, newUserId, uss));
    }

    /* access modifiers changed from: package-private */
    public void continueUserSwitch(UserState uss, int oldUserId, int newUserId) {
        Slog.d(TAG, "Continue user switch oldUser #" + oldUserId + ", newUser #" + newUserId);
        if (this.mUserSwitchUiEnabled) {
            this.mInjector.getWindowManager().stopFreezingScreen();
        }
        uss.switching = false;
        this.mHandler.removeMessages(80);
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(80, newUserId, 0));
        stopGuestOrEphemeralUserIfBackground(oldUserId);
        stopBackgroundUsersIfEnforced(oldUserId);
    }

    private void moveUserToForeground(UserState uss, int oldUserId, int newUserId) {
        if (this.mInjector.stackSupervisorSwitchUser(newUserId, uss)) {
            this.mInjector.startHomeActivity(newUserId, "moveUserToForeground");
        } else {
            this.mInjector.stackSupervisorResumeFocusedStackTopActivity();
        }
        EventLogTags.writeAmSwitchUser(newUserId);
        sendUserSwitchBroadcasts(oldUserId, newUserId);
    }

    /* access modifiers changed from: package-private */
    public void sendUserSwitchBroadcasts(int oldUserId, int newUserId) {
        String str;
        int i = oldUserId;
        int i2 = newUserId;
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        long ident = Binder.clearCallingIdentity();
        String str2 = "android.intent.extra.user_handle";
        int i3 = 1342177280;
        if (i >= 0) {
            try {
                List<UserInfo> profiles = this.mInjector.getUserManager().getProfiles(i, false);
                int count = profiles.size();
                int i4 = 0;
                while (i4 < count) {
                    int profileUserId = profiles.get(i4).id;
                    Intent intent = new Intent("android.intent.action.USER_BACKGROUND");
                    intent.addFlags(i3);
                    intent.putExtra(str2, profileUserId);
                    Intent intent2 = intent;
                    this.mInjector.broadcastIntent(intent, (String) null, (IIntentReceiver) null, 0, (String) null, (Bundle) null, (String[]) null, -1, (Bundle) null, false, false, ActivityManagerService.MY_PID, 1000, callingUid, callingPid, profileUserId);
                    i4++;
                    count = count;
                    profiles = profiles;
                    str2 = str2;
                    i3 = 1342177280;
                }
                int i5 = i4;
                int i6 = count;
                List<UserInfo> list = profiles;
                str = str2;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        } else {
            str = str2;
        }
        if (i2 >= 0) {
            List<UserInfo> profiles2 = this.mInjector.getUserManager().getProfiles(i2, false);
            int count2 = profiles2.size();
            int i7 = 0;
            while (i7 < count2) {
                int profileUserId2 = profiles2.get(i7).id;
                Intent intent3 = new Intent("android.intent.action.USER_FOREGROUND");
                intent3.addFlags(1342177280);
                String str3 = str;
                intent3.putExtra(str3, profileUserId2);
                Intent intent4 = intent3;
                this.mInjector.broadcastIntent(intent3, (String) null, (IIntentReceiver) null, 0, (String) null, (Bundle) null, (String[]) null, -1, (Bundle) null, false, false, ActivityManagerService.MY_PID, 1000, callingUid, callingPid, profileUserId2);
                i7++;
                count2 = count2;
                str = str3;
            }
            int i8 = i7;
            int i9 = count2;
            Intent intent5 = new Intent("android.intent.action.USER_SWITCHED");
            intent5.addFlags(1342177280);
            intent5.putExtra(str, i2);
            Intent intent6 = intent5;
            this.mInjector.broadcastIntent(intent5, (String) null, (IIntentReceiver) null, 0, (String) null, (Bundle) null, new String[]{"android.permission.MANAGE_USERS"}, -1, (Bundle) null, false, false, ActivityManagerService.MY_PID, 1000, callingUid, callingPid, -1);
        }
        Binder.restoreCallingIdentity(ident);
    }

    /* access modifiers changed from: package-private */
    public int handleIncomingUser(int callingPid, int callingUid, int userId, boolean allowAll, int allowMode, String name, String callerPackage) {
        boolean allow;
        int i = callingUid;
        int i2 = userId;
        int i3 = allowMode;
        String str = callerPackage;
        int callingUserId = UserHandle.getUserId(callingUid);
        if (callingUserId == i2) {
            return i2;
        }
        int targetUserId = unsafeConvertIncomingUser(i2);
        if (i == 0 || i == 1000) {
            String str2 = name;
        } else {
            if (this.mInjector.isCallerRecents(i) && callingUserId == getCurrentUserId() && isSameProfileGroup(callingUserId, targetUserId)) {
                allow = true;
            } else if (this.mInjector.checkComponentPermission("android.permission.INTERACT_ACROSS_USERS_FULL", callingPid, callingUid, -1, true) == 0) {
                allow = true;
            } else if (i3 == 2) {
                allow = false;
            } else if (this.mInjector.checkComponentPermission("android.permission.INTERACT_ACROSS_USERS", callingPid, callingUid, -1, true) != 0) {
                allow = false;
            } else if (i3 == 0) {
                allow = true;
            } else if (i3 == 1) {
                allow = isSameProfileGroup(callingUserId, targetUserId);
            } else {
                String str3 = name;
                throw new IllegalArgumentException("Unknown mode: " + i3);
            }
            if (allow) {
                String str4 = name;
            } else if (i2 == -3) {
                targetUserId = callingUserId;
                String str5 = name;
            } else {
                StringBuilder builder = new StringBuilder(128);
                builder.append("Permission Denial: ");
                builder.append(name);
                if (str != null) {
                    builder.append(" from ");
                    builder.append(str);
                }
                builder.append(" asks to run as user ");
                builder.append(i2);
                builder.append(" but is calling from uid ");
                UserHandle.formatUid(builder, i);
                builder.append("; this requires ");
                builder.append("android.permission.INTERACT_ACROSS_USERS_FULL");
                if (i3 != 2) {
                    builder.append(" or ");
                    builder.append("android.permission.INTERACT_ACROSS_USERS");
                }
                String msg = builder.toString();
                Slog.w(TAG, msg);
                throw new SecurityException(msg);
            }
        }
        if (!allowAll) {
            ensureNotSpecialUser(targetUserId);
        }
        if (i != 2000 || targetUserId < 0 || !hasUserRestriction("no_debugging_features", targetUserId)) {
            return targetUserId;
        }
        throw new SecurityException("Shell does not have permission to access user " + targetUserId + "\n " + Debug.getCallers(3));
    }

    /* access modifiers changed from: package-private */
    public int unsafeConvertIncomingUser(int userId) {
        return (userId == -2 || userId == -3) ? getCurrentUserId() : userId;
    }

    /* access modifiers changed from: package-private */
    public void ensureNotSpecialUser(int userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("Call does not support special user #" + userId);
        }
    }

    /* access modifiers changed from: package-private */
    public void registerUserSwitchObserver(IUserSwitchObserver observer, String name) {
        Preconditions.checkNotNull(name, "Observer name cannot be null");
        if (this.mInjector.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0) {
            this.mUserSwitchObservers.register(observer, name);
            return;
        }
        String msg = "Permission Denial: registerUserSwitchObserver() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.INTERACT_ACROSS_USERS_FULL";
        Slog.w(TAG, msg);
        throw new SecurityException(msg);
    }

    /* access modifiers changed from: package-private */
    public void sendForegroundProfileChanged(int userId) {
        this.mHandler.removeMessages(70);
        this.mHandler.obtainMessage(70, userId, 0).sendToTarget();
    }

    /* access modifiers changed from: package-private */
    public void unregisterUserSwitchObserver(IUserSwitchObserver observer) {
        this.mUserSwitchObservers.unregister(observer);
    }

    /* access modifiers changed from: package-private */
    public UserState getStartedUserState(int userId) {
        UserState userState;
        synchronized (this.mLock) {
            userState = this.mStartedUsers.get(userId);
        }
        return userState;
    }

    /* access modifiers changed from: package-private */
    public boolean hasStartedUserState(int userId) {
        boolean z;
        synchronized (this.mLock) {
            z = this.mStartedUsers.get(userId) != null;
        }
        return z;
    }

    @GuardedBy({"mLock"})
    private void updateStartedUserArrayLU() {
        int num = 0;
        for (int i = 0; i < this.mStartedUsers.size(); i++) {
            UserState uss = this.mStartedUsers.valueAt(i);
            if (!(uss.state == 4 || uss.state == 5)) {
                num++;
            }
        }
        this.mStartedUserArray = new int[num];
        int num2 = 0;
        for (int i2 = 0; i2 < this.mStartedUsers.size(); i2++) {
            UserState uss2 = this.mStartedUsers.valueAt(i2);
            if (!(uss2.state == 4 || uss2.state == 5)) {
                this.mStartedUserArray[num2] = this.mStartedUsers.keyAt(i2);
                num2++;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void sendBootCompleted(IIntentReceiver resultTo) {
        SparseArray<UserState> startedUsers;
        synchronized (this.mLock) {
            startedUsers = this.mStartedUsers.clone();
        }
        for (int i = 0; i < startedUsers.size(); i++) {
            finishUserBoot(startedUsers.valueAt(i), resultTo);
        }
    }

    /* access modifiers changed from: package-private */
    public void onSystemReady() {
        updateCurrentProfileIds();
        this.mInjector.reportCurWakefulnessUsageEvent();
    }

    private void updateCurrentProfileIds() {
        List<UserInfo> profiles = this.mInjector.getUserManager().getProfiles(getCurrentUserId(), false);
        int[] currentProfileIds = new int[profiles.size()];
        for (int i = 0; i < currentProfileIds.length; i++) {
            currentProfileIds[i] = profiles.get(i).id;
        }
        List<UserInfo> users = this.mInjector.getUserManager().getUsers(false);
        synchronized (this.mLock) {
            this.mCurrentProfileIds = currentProfileIds;
            this.mUserProfileGroupIds.clear();
            for (int i2 = 0; i2 < users.size(); i2++) {
                UserInfo user = users.get(i2);
                if (user.profileGroupId != -10000) {
                    this.mUserProfileGroupIds.put(user.id, user.profileGroupId);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int[] getStartedUserArray() {
        int[] iArr;
        synchronized (this.mLock) {
            iArr = this.mStartedUserArray;
        }
        return iArr;
    }

    /* access modifiers changed from: package-private */
    public boolean isUserRunning(int userId, int flags) {
        UserState state = getStartedUserState(userId);
        if (state == null) {
            return false;
        }
        if ((flags & 1) != 0) {
            return true;
        }
        if ((flags & 2) != 0) {
            int i = state.state;
            if (i == 0 || i == 1) {
                return true;
            }
            return false;
        } else if ((flags & 8) != 0) {
            int i2 = state.state;
            if (i2 == 2 || i2 == 3) {
                return true;
            }
            if (i2 == 4 || i2 == 5) {
                return StorageManager.isUserKeyUnlocked(userId);
            }
            return false;
        } else if ((flags & 4) != 0) {
            int i3 = state.state;
            if (i3 == 3) {
                return true;
            }
            if (i3 == 4 || i3 == 5) {
                return StorageManager.isUserKeyUnlocked(userId);
            }
            return false;
        } else if (state.state == 4 || state.state == 5) {
            return false;
        } else {
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0021, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isSystemUserStarted() {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.am.UserState> r1 = r6.mStartedUsers     // Catch:{ all -> 0x0022 }
            r2 = 0
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x0022 }
            com.android.server.am.UserState r1 = (com.android.server.am.UserState) r1     // Catch:{ all -> 0x0022 }
            if (r1 != 0) goto L_0x0010
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return r2
        L_0x0010:
            int r3 = r1.state     // Catch:{ all -> 0x0022 }
            r4 = 1
            if (r3 == r4) goto L_0x001f
            int r3 = r1.state     // Catch:{ all -> 0x0022 }
            r5 = 2
            if (r3 == r5) goto L_0x001f
            int r3 = r1.state     // Catch:{ all -> 0x0022 }
            r5 = 3
            if (r3 != r5) goto L_0x0020
        L_0x001f:
            r2 = r4
        L_0x0020:
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return r2
        L_0x0022:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.UserController.isSystemUserStarted():boolean");
    }

    /* access modifiers changed from: package-private */
    public UserInfo getCurrentUser() {
        UserInfo currentUserLU;
        if (this.mInjector.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS") != 0 && this.mInjector.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0) {
            String msg = "Permission Denial: getCurrentUser() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.INTERACT_ACROSS_USERS";
            Slog.w(TAG, msg);
            throw new SecurityException(msg);
        } else if (this.mTargetUserId == -10000) {
            return getUserInfo(this.mCurrentUserId);
        } else {
            synchronized (this.mLock) {
                currentUserLU = getCurrentUserLU();
            }
            return currentUserLU;
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public UserInfo getCurrentUserLU() {
        return getUserInfo(this.mTargetUserId != -10000 ? this.mTargetUserId : this.mCurrentUserId);
    }

    /* access modifiers changed from: package-private */
    public int getCurrentOrTargetUserId() {
        int i;
        synchronized (this.mLock) {
            i = this.mTargetUserId != -10000 ? this.mTargetUserId : this.mCurrentUserId;
        }
        return i;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public int getCurrentOrTargetUserIdLU() {
        return this.mTargetUserId != -10000 ? this.mTargetUserId : this.mCurrentUserId;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public int getCurrentUserIdLU() {
        return this.mCurrentUserId;
    }

    /* access modifiers changed from: package-private */
    public int getCurrentUserId() {
        int i;
        synchronized (this.mLock) {
            i = this.mCurrentUserId;
        }
        return i;
    }

    @GuardedBy({"mLock"})
    private boolean isCurrentUserLU(int userId) {
        return userId == getCurrentOrTargetUserIdLU();
    }

    /* access modifiers changed from: package-private */
    public int[] getUsers() {
        UserManagerService ums = this.mInjector.getUserManager();
        if (ums != null) {
            return ums.getUserIds();
        }
        return new int[]{0};
    }

    private UserInfo getUserInfo(int userId) {
        return this.mInjector.getUserManager().getUserInfo(userId);
    }

    /* access modifiers changed from: package-private */
    public int[] getUserIds() {
        return this.mInjector.getUserManager().getUserIds();
    }

    /* access modifiers changed from: package-private */
    public int[] expandUserId(int userId) {
        if (userId == -1) {
            return getUsers();
        }
        return new int[]{userId};
    }

    /* access modifiers changed from: package-private */
    public boolean exists(int userId) {
        return this.mInjector.getUserManager().exists(userId);
    }

    private void enforceShellRestriction(String restriction, int userHandle) {
        if (Binder.getCallingUid() != 2000) {
            return;
        }
        if (userHandle < 0 || hasUserRestriction(restriction, userHandle)) {
            throw new SecurityException("Shell does not have permission to access user " + userHandle);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasUserRestriction(String restriction, int userId) {
        return this.mInjector.getUserManager().hasUserRestriction(restriction, userId);
    }

    /* access modifiers changed from: package-private */
    public boolean isSameProfileGroup(int callingUserId, int targetUserId) {
        boolean z = true;
        if (callingUserId == targetUserId) {
            return true;
        }
        synchronized (this.mLock) {
            int callingProfile = this.mUserProfileGroupIds.get(callingUserId, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
            int targetProfile = this.mUserProfileGroupIds.get(targetUserId, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
            if (callingProfile == -10000 || callingProfile != targetProfile) {
                z = false;
            }
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public boolean isUserOrItsParentRunning(int userId) {
        synchronized (this.mLock) {
            if (isUserRunning(userId, 0)) {
                return true;
            }
            int parentUserId = this.mUserProfileGroupIds.get(userId, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
            if (parentUserId == -10000) {
                return false;
            }
            boolean isUserRunning = isUserRunning(parentUserId, 0);
            return isUserRunning;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isCurrentProfile(int userId) {
        boolean contains;
        synchronized (this.mLock) {
            contains = ArrayUtils.contains(this.mCurrentProfileIds, userId);
        }
        return contains;
    }

    /* access modifiers changed from: package-private */
    public int[] getCurrentProfileIds() {
        int[] iArr;
        synchronized (this.mLock) {
            iArr = this.mCurrentProfileIds;
        }
        return iArr;
    }

    /* access modifiers changed from: package-private */
    public void onUserRemoved(int userId) {
        synchronized (this.mLock) {
            for (int i = this.mUserProfileGroupIds.size() - 1; i >= 0; i--) {
                if (this.mUserProfileGroupIds.keyAt(i) == userId || this.mUserProfileGroupIds.valueAt(i) == userId) {
                    this.mUserProfileGroupIds.removeAt(i);
                }
            }
            this.mCurrentProfileIds = ArrayUtils.removeInt(this.mCurrentProfileIds, userId);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0017, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0018, code lost:
        r0 = r3.mInjector.getKeyguardManager();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0022, code lost:
        if (r0.isDeviceLocked(r4) == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0028, code lost:
        if (r0.isDeviceSecure(r4) == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002a, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0015, code lost:
        if (r3.mLockPatternUtils.isSeparateProfileChallengeEnabled(r4) != false) goto L_0x0018;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean shouldConfirmCredentials(int r4) {
        /*
            r3 = this;
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.am.UserState> r1 = r3.mStartedUsers     // Catch:{ all -> 0x002c }
            java.lang.Object r1 = r1.get(r4)     // Catch:{ all -> 0x002c }
            r2 = 0
            if (r1 != 0) goto L_0x000e
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return r2
        L_0x000e:
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            com.android.internal.widget.LockPatternUtils r0 = r3.mLockPatternUtils
            boolean r0 = r0.isSeparateProfileChallengeEnabled(r4)
            if (r0 != 0) goto L_0x0018
            return r2
        L_0x0018:
            com.android.server.am.UserController$Injector r0 = r3.mInjector
            android.app.KeyguardManager r0 = r0.getKeyguardManager()
            boolean r1 = r0.isDeviceLocked(r4)
            if (r1 == 0) goto L_0x002b
            boolean r1 = r0.isDeviceSecure(r4)
            if (r1 == 0) goto L_0x002b
            r2 = 1
        L_0x002b:
            return r2
        L_0x002c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.UserController.shouldConfirmCredentials(int):boolean");
    }

    /* access modifiers changed from: package-private */
    public boolean isLockScreenDisabled(int userId) {
        return this.mLockPatternUtils.isLockScreenDisabled(userId);
    }

    /* access modifiers changed from: package-private */
    public void setSwitchingFromSystemUserMessage(String switchingFromSystemUserMessage) {
        synchronized (this.mLock) {
            this.mSwitchingFromSystemUserMessage = switchingFromSystemUserMessage;
        }
    }

    /* access modifiers changed from: package-private */
    public void setSwitchingToSystemUserMessage(String switchingToSystemUserMessage) {
        synchronized (this.mLock) {
            this.mSwitchingToSystemUserMessage = switchingToSystemUserMessage;
        }
    }

    private String getSwitchingFromSystemUserMessage() {
        String str;
        synchronized (this.mLock) {
            str = this.mSwitchingFromSystemUserMessage;
        }
        return str;
    }

    private String getSwitchingToSystemUserMessage() {
        String str;
        synchronized (this.mLock) {
            str = this.mSwitchingToSystemUserMessage;
        }
        return str;
    }

    /* access modifiers changed from: package-private */
    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        synchronized (this.mLock) {
            long token = proto.start(fieldId);
            for (int i = 0; i < this.mStartedUsers.size(); i++) {
                UserState uss = this.mStartedUsers.valueAt(i);
                long uToken = proto.start(2246267895809L);
                proto.write(1120986464257L, uss.mHandle.getIdentifier());
                uss.writeToProto(proto, 1146756268034L);
                proto.end(uToken);
            }
            for (int write : this.mStartedUserArray) {
                proto.write(2220498092034L, write);
            }
            for (int i2 = 0; i2 < this.mUserLru.size(); i2++) {
                proto.write(2220498092035L, this.mUserLru.get(i2).intValue());
            }
            if (this.mUserProfileGroupIds.size() > 0) {
                for (int i3 = 0; i3 < this.mUserProfileGroupIds.size(); i3++) {
                    long uToken2 = proto.start(2246267895812L);
                    proto.write(1120986464257L, this.mUserProfileGroupIds.keyAt(i3));
                    proto.write(1120986464258L, this.mUserProfileGroupIds.valueAt(i3));
                    proto.end(uToken2);
                }
            }
            proto.end(token);
        }
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, boolean dumpAll) {
        synchronized (this.mLock) {
            pw.println("  mStartedUsers:");
            for (int i = 0; i < this.mStartedUsers.size(); i++) {
                UserState uss = this.mStartedUsers.valueAt(i);
                pw.print("    User #");
                pw.print(uss.mHandle.getIdentifier());
                pw.print(": ");
                uss.dump("", pw);
            }
            pw.print("  mStartedUserArray: [");
            for (int i2 = 0; i2 < this.mStartedUserArray.length; i2++) {
                if (i2 > 0) {
                    pw.print(", ");
                }
                pw.print(this.mStartedUserArray[i2]);
            }
            pw.println("]");
            pw.print("  mUserLru: [");
            for (int i3 = 0; i3 < this.mUserLru.size(); i3++) {
                if (i3 > 0) {
                    pw.print(", ");
                }
                pw.print(this.mUserLru.get(i3));
            }
            pw.println("]");
            if (this.mUserProfileGroupIds.size() > 0) {
                pw.println("  mUserProfileGroupIds:");
                for (int i4 = 0; i4 < this.mUserProfileGroupIds.size(); i4++) {
                    pw.print("    User #");
                    pw.print(this.mUserProfileGroupIds.keyAt(i4));
                    pw.print(" -> profile #");
                    pw.println(this.mUserProfileGroupIds.valueAt(i4));
                }
            }
            pw.println("  mCurrentUserId:" + this.mCurrentUserId);
            pw.println("  mLastActiveUsers:" + this.mLastActiveUsers);
        }
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 10:
                dispatchUserSwitch((UserState) msg.obj, msg.arg1, msg.arg2);
                return false;
            case 20:
                continueUserSwitch((UserState) msg.obj, msg.arg1, msg.arg2);
                return false;
            case 30:
                timeoutUserSwitch((UserState) msg.obj, msg.arg1, msg.arg2);
                return false;
            case 40:
                startProfiles();
                return false;
            case 50:
                this.mInjector.batteryStatsServiceNoteEvent(32775, Integer.toString(msg.arg1), msg.arg1);
                this.mInjector.getSystemServiceManager().startUser(msg.arg1);
                return false;
            case 60:
                this.mInjector.batteryStatsServiceNoteEvent(16392, Integer.toString(msg.arg2), msg.arg2);
                this.mInjector.batteryStatsServiceNoteEvent(32776, Integer.toString(msg.arg1), msg.arg1);
                this.mInjector.getSystemServiceManager().switchUser(msg.arg1);
                return false;
            case 70:
                dispatchForegroundProfileChanged(msg.arg1);
                return false;
            case 80:
                dispatchUserSwitchComplete(msg.arg1);
                return false;
            case USER_SWITCH_CALLBACKS_TIMEOUT_MSG /*90*/:
                timeoutUserSwitchCallbacks(msg.arg1, msg.arg2);
                return false;
            case 100:
                int userId = msg.arg1;
                this.mInjector.getSystemServiceManager().unlockUser(userId);
                FgThread.getHandler().post(new Runnable(userId) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        UserController.this.lambda$handleMessage$10$UserController(this.f$1);
                    }
                });
                finishUserUnlocked((UserState) msg.obj);
                return false;
            case REPORT_LOCKED_BOOT_COMPLETE_MSG /*110*/:
                dispatchLockedBootComplete(msg.arg1);
                return false;
            case START_USER_SWITCH_FG_MSG /*120*/:
                startUserInForeground(msg.arg1);
                return false;
            case 1000:
                showUserSwitchDialog((Pair) msg.obj);
                return false;
            default:
                return false;
        }
    }

    public /* synthetic */ void lambda$handleMessage$10$UserController(int userId) {
        this.mInjector.loadUserRecents(userId);
    }

    private static class UserProgressListener extends IProgressListener.Stub {
        private volatile long mUnlockStarted;

        private UserProgressListener() {
        }

        public void onStarted(int id, Bundle extras) throws RemoteException {
            Slog.d(UserController.TAG, "Started unlocking user " + id);
            this.mUnlockStarted = SystemClock.uptimeMillis();
        }

        public void onProgress(int id, int progress, Bundle extras) throws RemoteException {
            Slog.d(UserController.TAG, "Unlocking user " + id + " progress " + progress);
        }

        public void onFinished(int id, Bundle extras) throws RemoteException {
            long unlockTime = SystemClock.uptimeMillis() - this.mUnlockStarted;
            if (id == 0) {
                new TimingsTraceLog("SystemServerTiming", 524288).logDuration("SystemUserUnlock", unlockTime);
                return;
            }
            TimingsTraceLog timingsTraceLog = new TimingsTraceLog("SystemServerTiming", 524288);
            timingsTraceLog.logDuration("User" + id + "Unlock", unlockTime);
        }
    }

    @VisibleForTesting
    static class Injector {
        private final ActivityManagerService mService;
        private UserManagerService mUserManager;
        private UserManagerInternal mUserManagerInternal;

        Injector(ActivityManagerService service) {
            this.mService = service;
        }

        /* access modifiers changed from: protected */
        public Handler getHandler(Handler.Callback callback) {
            return new Handler(this.mService.mHandlerThread.getLooper(), callback);
        }

        /* access modifiers changed from: protected */
        public Handler getUiHandler(Handler.Callback callback) {
            return new Handler(this.mService.mUiHandler.getLooper(), callback);
        }

        /* access modifiers changed from: protected */
        public Context getContext() {
            return this.mService.mContext;
        }

        /* access modifiers changed from: protected */
        public LockPatternUtils getLockPatternUtils() {
            return new LockPatternUtils(getContext());
        }

        /* access modifiers changed from: protected */
        public int broadcastIntent(Intent intent, String resolvedType, IIntentReceiver resultTo, int resultCode, String resultData, Bundle resultExtras, String[] requiredPermissions, int appOp, Bundle bOptions, boolean ordered, boolean sticky, int callingPid, int callingUid, int realCallingUid, int realCallingPid, int userId) {
            int broadcastIntentLocked;
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    broadcastIntentLocked = this.mService.broadcastIntentLocked((ProcessRecord) null, (String) null, intent, resolvedType, resultTo, resultCode, resultData, resultExtras, requiredPermissions, appOp, bOptions, ordered, sticky, callingPid, callingUid, realCallingUid, realCallingPid, userId);
                } catch (Throwable th) {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return broadcastIntentLocked;
        }

        /* access modifiers changed from: package-private */
        public int checkCallingPermission(String permission) {
            return this.mService.checkCallingPermission(permission);
        }

        /* access modifiers changed from: package-private */
        public WindowManagerService getWindowManager() {
            return this.mService.mWindowManager;
        }

        /* access modifiers changed from: package-private */
        public void activityManagerOnUserStopped(int userId) {
            ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).onUserStopped(userId);
        }

        /* access modifiers changed from: package-private */
        public void systemServiceManagerCleanupUser(int userId) {
            this.mService.mSystemServiceManager.cleanupUser(userId);
        }

        /* access modifiers changed from: protected */
        public UserManagerService getUserManager() {
            if (this.mUserManager == null) {
                this.mUserManager = IUserManager.Stub.asInterface(ServiceManager.getService("user"));
            }
            return this.mUserManager;
        }

        /* access modifiers changed from: package-private */
        public UserManagerInternal getUserManagerInternal() {
            if (this.mUserManagerInternal == null) {
                this.mUserManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
            }
            return this.mUserManagerInternal;
        }

        /* access modifiers changed from: package-private */
        public KeyguardManager getKeyguardManager() {
            return (KeyguardManager) this.mService.mContext.getSystemService(KeyguardManager.class);
        }

        /* access modifiers changed from: package-private */
        public void batteryStatsServiceNoteEvent(int code, String name, int uid) {
            this.mService.mBatteryStatsService.noteEvent(code, name, uid);
        }

        /* access modifiers changed from: package-private */
        public boolean isRuntimeRestarted() {
            return this.mService.mSystemServiceManager.isRuntimeRestarted();
        }

        /* access modifiers changed from: package-private */
        public SystemServiceManager getSystemServiceManager() {
            return this.mService.mSystemServiceManager;
        }

        /* access modifiers changed from: package-private */
        public boolean isFirstBootOrUpgrade() {
            IPackageManager pm = AppGlobals.getPackageManager();
            try {
                return pm.isFirstBoot() || pm.isDeviceUpgrading();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* access modifiers changed from: package-private */
        public void sendPreBootBroadcast(int userId, boolean quiet, Runnable onFinish) {
            final Runnable runnable = onFinish;
            new PreBootBroadcaster(this.mService, userId, (ProgressReporter) null, quiet) {
                public void onFinished() {
                    runnable.run();
                }
            }.sendNext();
        }

        /* access modifiers changed from: package-private */
        public void activityManagerForceStopPackage(int userId, String reason) {
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    this.mService.forceStopPackageLocked((String) null, -1, false, false, true, false, false, userId, reason);
                } catch (Throwable th) {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        /* access modifiers changed from: package-private */
        public int checkComponentPermission(String permission, int pid, int uid, int owningUid, boolean exported) {
            ActivityManagerService activityManagerService = this.mService;
            return ActivityManagerService.checkComponentPermission(permission, pid, uid, owningUid, exported);
        }

        /* access modifiers changed from: protected */
        public void startHomeActivity(int userId, String reason) {
            this.mService.mAtmInternal.startHomeActivity(userId, reason);
        }

        /* access modifiers changed from: package-private */
        public void startUserWidgets(int userId) {
            AppWidgetManagerInternal awm = (AppWidgetManagerInternal) LocalServices.getService(AppWidgetManagerInternal.class);
            if (awm != null) {
                FgThread.getHandler().post(new Runnable(awm, userId) {
                    private final /* synthetic */ AppWidgetManagerInternal f$0;
                    private final /* synthetic */ int f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void run() {
                        this.f$0.unlockUser(this.f$1);
                    }
                });
            }
        }

        /* access modifiers changed from: package-private */
        public void updateUserConfiguration() {
            this.mService.mAtmInternal.updateUserConfiguration();
        }

        /* access modifiers changed from: package-private */
        public void clearBroadcastQueueForUser(int userId) {
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    this.mService.clearBroadcastQueueForUserLocked(userId);
                } catch (Throwable th) {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        /* access modifiers changed from: package-private */
        public void loadUserRecents(int userId) {
            this.mService.mAtmInternal.loadRecentTasksForUser(userId);
        }

        /* access modifiers changed from: package-private */
        public void startPersistentApps(int matchFlags) {
            this.mService.startPersistentApps(matchFlags);
        }

        /* access modifiers changed from: package-private */
        public void installEncryptionUnawareProviders(int userId) {
            this.mService.installEncryptionUnawareProviders(userId);
        }

        /* JADX WARNING: type inference failed for: r0v4, types: [android.app.Dialog] */
        /* JADX WARNING: type inference failed for: r1v3, types: [com.android.server.am.CarUserSwitchingDialog] */
        /* JADX WARNING: type inference failed for: r1v4, types: [com.android.server.am.UserSwitchingDialog] */
        /* access modifiers changed from: package-private */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void showUserSwitchingDialog(android.content.pm.UserInfo r10, android.content.pm.UserInfo r11, java.lang.String r12, java.lang.String r13) {
            /*
                r9 = this;
                com.android.server.am.ActivityManagerService r0 = r9.mService
                android.content.Context r0 = r0.mContext
                android.content.pm.PackageManager r0 = r0.getPackageManager()
                java.lang.String r1 = "android.hardware.type.automotive"
                boolean r0 = r0.hasSystemFeature(r1)
                if (r0 != 0) goto L_0x0020
                com.android.server.am.UserSwitchingDialog r0 = new com.android.server.am.UserSwitchingDialog
                com.android.server.am.ActivityManagerService r2 = r9.mService
                android.content.Context r3 = r2.mContext
                r6 = 1
                r1 = r0
                r4 = r10
                r5 = r11
                r7 = r12
                r8 = r13
                r1.<init>(r2, r3, r4, r5, r6, r7, r8)
                goto L_0x002f
            L_0x0020:
                com.android.server.am.CarUserSwitchingDialog r0 = new com.android.server.am.CarUserSwitchingDialog
                com.android.server.am.ActivityManagerService r2 = r9.mService
                android.content.Context r3 = r2.mContext
                r6 = 1
                r1 = r0
                r4 = r10
                r5 = r11
                r7 = r12
                r8 = r13
                r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            L_0x002f:
                r0.show()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.UserController.Injector.showUserSwitchingDialog(android.content.pm.UserInfo, android.content.pm.UserInfo, java.lang.String, java.lang.String):void");
        }

        /* access modifiers changed from: package-private */
        public void reportGlobalUsageEventLocked(int event) {
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    this.mService.reportGlobalUsageEventLocked(event);
                } catch (Throwable th) {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        /* access modifiers changed from: package-private */
        public void reportCurWakefulnessUsageEvent() {
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    this.mService.reportCurWakefulnessUsageEventLocked();
                } catch (Throwable th) {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        /* access modifiers changed from: package-private */
        public void stackSupervisorRemoveUser(int userId) {
            this.mService.mAtmInternal.removeUser(userId);
        }

        /* access modifiers changed from: protected */
        public boolean stackSupervisorSwitchUser(int userId, UserState uss) {
            return this.mService.mAtmInternal.switchUser(userId, uss);
        }

        /* access modifiers changed from: protected */
        public void stackSupervisorResumeFocusedStackTopActivity() {
            this.mService.mAtmInternal.resumeTopActivities(false);
        }

        /* access modifiers changed from: protected */
        public void clearAllLockedTasks(String reason) {
            this.mService.mAtmInternal.clearLockedTasks(reason);
        }

        /* access modifiers changed from: protected */
        public boolean isCallerRecents(int callingUid) {
            return this.mService.mAtmInternal.isCallerRecents(callingUid);
        }

        /* access modifiers changed from: protected */
        public ActivityManagerService getService() {
            return this.mService;
        }

        /* access modifiers changed from: protected */
        public IStorageManager getStorageManager() {
            return IStorageManager.Stub.asInterface(ServiceManager.getService("mount"));
        }
    }
}
