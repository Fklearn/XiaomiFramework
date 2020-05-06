package com.android.server.wm;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AppOpsManager;
import android.app.ProfilerInfo;
import android.app.WaitResult;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserManager;
import android.os.WorkSource;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.BoostFramework;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.TransferPipe;
import com.android.internal.os.logging.MetricsLoggerWrapper;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.am.UserState;
import com.android.server.job.controllers.JobStatus;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.wm.ActivityStack;
import com.android.server.wm.ActivityStackSupervisorInjector;
import com.android.server.wm.RecentTasks;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ActivityStackSupervisor implements RecentTasks.Callbacks {
    private static final ArrayMap<String, String> ACTION_TO_RUNTIME_PERMISSION = new ArrayMap<>();
    private static final int ACTIVITY_RESTRICTION_APPOP = 2;
    private static final int ACTIVITY_RESTRICTION_NONE = 0;
    private static final int ACTIVITY_RESTRICTION_PERMISSION = 1;
    static final boolean DEFER_RESUME = true;
    static final int IDLE_NOW_MSG = 201;
    static final int IDLE_TIMEOUT = 10000;
    static final int IDLE_TIMEOUT_MSG = 200;
    static final int LAUNCH_TASK_BEHIND_COMPLETE = 212;
    static final int LAUNCH_TIMEOUT = 10000;
    static final int LAUNCH_TIMEOUT_MSG = 204;
    private static final int MAX_TASK_IDS_PER_USER = 100000;
    static final boolean ON_TOP = true;
    static final boolean PAUSE_IMMEDIATELY = true;
    static final boolean PRESERVE_WINDOWS = true;
    static final boolean REMOVE_FROM_RECENTS = true;
    static final int REPORT_HOME_CHANGED_MSG = 216;
    static final int REPORT_MULTI_WINDOW_MODE_CHANGED_MSG = 214;
    static final int REPORT_PIP_MODE_CHANGED_MSG = 215;
    static final int RESTART_ACTIVITY_PROCESS_TIMEOUT_MSG = 213;
    static final int RESUME_TOP_ACTIVITY_MSG = 202;
    static final int SLEEP_TIMEOUT = 5000;
    static final int SLEEP_TIMEOUT_MSG = 203;
    private static final String TAG = "ActivityTaskManager";
    private static final String TAG_IDLE = "ActivityTaskManager";
    private static final String TAG_PAUSE = "ActivityTaskManager";
    private static final String TAG_RECENTS = "ActivityTaskManager";
    private static final String TAG_STACK = "ActivityTaskManager";
    private static final String TAG_SWITCH = "ActivityTaskManager";
    static final String TAG_TASKS = "ActivityTaskManager";
    static final int TOP_RESUMED_STATE_LOSS_TIMEOUT = 500;
    static final int TOP_RESUMED_STATE_LOSS_TIMEOUT_MSG = 217;
    static final boolean VALIDATE_WAKE_LOCK_CALLER = false;
    public static boolean mIsPerfBoostAcquired = false;
    public static int mPerfHandle = -1;
    public static boolean mPerfSendTapHint = false;
    private ActivityMetricsLogger mActivityMetricsLogger;
    private boolean mAllowDockedStackResize = true;
    boolean mAppVisibilitiesChangedSinceLastPause;
    private final SparseIntArray mCurTaskIdForUser = new SparseIntArray(20);
    private int mDeferResumeCount;
    private boolean mDockedStackResizing;
    final ArrayList<ActivityRecord> mFinishingActivities = new ArrayList<>();
    final ArrayList<ActivityRecord> mGoingToSleepActivities = new ArrayList<>();
    PowerManager.WakeLock mGoingToSleepWakeLock;
    final ActivityStackSupervisorHandler mHandler;
    private boolean mHasPendingDockedBounds;
    private boolean mInitialized;
    private KeyguardController mKeyguardController;
    private LaunchParamsController mLaunchParamsController;
    LaunchParamsPersister mLaunchParamsPersister;
    PowerManager.WakeLock mLaunchingActivityWakeLock;
    final Looper mLooper;
    final ArrayList<ActivityRecord> mMultiWindowModeChangedActivities = new ArrayList<>();
    final ArrayList<ActivityRecord> mNoAnimActivities = new ArrayList<>();
    private Rect mPendingDockedBounds;
    private Rect mPendingTempDockedTaskBounds;
    private Rect mPendingTempDockedTaskInsetBounds;
    private Rect mPendingTempOtherTaskBounds;
    private Rect mPendingTempOtherTaskInsetBounds;
    public BoostFramework mPerfBoost = new BoostFramework();
    PersisterQueue mPersisterQueue;
    final ArrayList<ActivityRecord> mPipModeChangedActivities = new ArrayList<>();
    Rect mPipModeChangedTargetStackBounds;
    private PowerManager mPowerManager;
    RecentTasks mRecentTasks;
    private final ArraySet<Integer> mResizingTasksDuringAnimation = new ArraySet<>();
    public RootActivityContainer mRootActivityContainer;
    RunningTasks mRunningTasks;
    final ActivityTaskManagerService mService;
    final ArrayList<UserState> mStartingUsers = new ArrayList<>();
    final ArrayList<ActivityRecord> mStoppingActivities = new ArrayList<>();
    private final ActivityOptions mTmpOptions = ActivityOptions.makeBasic();
    private ActivityRecord mTopResumedActivity;
    private boolean mTopResumedActivityWaitingForPrev;
    boolean mUserLeaving = false;
    public BoostFramework mUxPerf = new BoostFramework();
    final ArrayList<WaitResult> mWaitingActivityLaunched = new ArrayList<>();
    private final ArrayList<WaitInfo> mWaitingForActivityVisible = new ArrayList<>();
    WindowManagerService mWindowManager;
    private final Rect tempRect = new Rect();

    static {
        ACTION_TO_RUNTIME_PERMISSION.put("android.media.action.IMAGE_CAPTURE", "android.permission.CAMERA");
        ACTION_TO_RUNTIME_PERMISSION.put("android.media.action.VIDEO_CAPTURE", "android.permission.CAMERA");
        ACTION_TO_RUNTIME_PERMISSION.put("android.intent.action.CALL", "android.permission.CALL_PHONE");
    }

    /* access modifiers changed from: package-private */
    public boolean canPlaceEntityOnDisplay(int displayId, int callingPid, int callingUid, ActivityInfo activityInfo) {
        if (displayId == 0) {
            return true;
        }
        if (this.mService.mSupportsMultiDisplay && isCallerAllowedToLaunchOnDisplay(callingPid, callingUid, displayId, activityInfo)) {
            return true;
        }
        return false;
    }

    static class PendingActivityLaunch {
        final WindowProcessController callerApp;
        final ActivityRecord r;
        final ActivityRecord sourceRecord;
        final ActivityStack stack;
        final int startFlags;

        PendingActivityLaunch(ActivityRecord _r, ActivityRecord _sourceRecord, int _startFlags, ActivityStack _stack, WindowProcessController app) {
            this.r = _r;
            this.sourceRecord = _sourceRecord;
            this.startFlags = _startFlags;
            this.stack = _stack;
            this.callerApp = app;
        }

        /* access modifiers changed from: package-private */
        public void sendErrorResult(String message) {
            try {
                if (this.callerApp != null && this.callerApp.hasThread()) {
                    this.callerApp.getThread().scheduleCrash(message);
                }
            } catch (RemoteException e) {
                Slog.e("ActivityTaskManager", "Exception scheduling crash of failed activity launcher sourceRecord=" + this.sourceRecord, e);
            }
        }
    }

    public ActivityStackSupervisor(ActivityTaskManagerService service, Looper looper) {
        this.mService = service;
        this.mLooper = looper;
        this.mHandler = new ActivityStackSupervisorHandler(looper);
    }

    public void initialize() {
        if (!this.mInitialized) {
            this.mInitialized = true;
            this.mRunningTasks = createRunningTasks();
            this.mActivityMetricsLogger = new ActivityMetricsLogger(this, this.mService.mContext, this.mHandler.getLooper());
            this.mKeyguardController = new KeyguardController(this.mService, this);
            this.mPersisterQueue = new PersisterQueue();
            this.mLaunchParamsPersister = new LaunchParamsPersister(this.mPersisterQueue, this);
            this.mLaunchParamsController = new LaunchParamsController(this.mService, this.mLaunchParamsPersister);
            this.mLaunchParamsController.registerDefaultModifiers(this);
        }
    }

    /* access modifiers changed from: package-private */
    public void onSystemReady() {
        this.mLaunchParamsPersister.onSystemReady();
    }

    /* access modifiers changed from: package-private */
    public void onUserUnlocked(int userId) {
        this.mPersisterQueue.startPersisting();
        this.mLaunchParamsPersister.onUnlockUser(userId);
    }

    public ActivityMetricsLogger getActivityMetricsLogger() {
        return this.mActivityMetricsLogger;
    }

    public KeyguardController getKeyguardController() {
        return this.mKeyguardController;
    }

    /* access modifiers changed from: package-private */
    public void setRecentTasks(RecentTasks recentTasks) {
        this.mRecentTasks = recentTasks;
        this.mRecentTasks.registerCallback(this);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public RunningTasks createRunningTasks() {
        return new RunningTasks();
    }

    /* access modifiers changed from: package-private */
    public void initPowerManagement() {
        this.mPowerManager = (PowerManager) this.mService.mContext.getSystemService(PowerManager.class);
        this.mGoingToSleepWakeLock = this.mPowerManager.newWakeLock(1, "ActivityManager-Sleep");
        this.mLaunchingActivityWakeLock = this.mPowerManager.newWakeLock(1, "*launch*");
        this.mLaunchingActivityWakeLock.setReferenceCounted(false);
    }

    /* access modifiers changed from: package-private */
    public void setWindowManager(WindowManagerService wm) {
        this.mWindowManager = wm;
        getKeyguardController().setWindowManager(wm);
    }

    /* access modifiers changed from: package-private */
    public void moveRecentsStackToFront(String reason) {
        ActivityStack recentsStack = this.mRootActivityContainer.getDefaultDisplay().getStack(0, 3);
        if (recentsStack != null) {
            recentsStack.moveToFront(reason);
        }
    }

    /* access modifiers changed from: package-private */
    public void setNextTaskIdForUserLocked(int taskId, int userId) {
        if (taskId > this.mCurTaskIdForUser.get(userId, -1)) {
            this.mCurTaskIdForUser.put(userId, taskId);
        }
    }

    static int nextTaskIdForUser(int taskId, int userId) {
        int nextTaskId = taskId + 1;
        if (nextTaskId == (userId + 1) * MAX_TASK_IDS_PER_USER) {
            return nextTaskId - MAX_TASK_IDS_PER_USER;
        }
        return nextTaskId;
    }

    /* access modifiers changed from: package-private */
    public int getNextTaskIdForUserLocked(int userId) {
        int currentTaskId = this.mCurTaskIdForUser.get(userId, MAX_TASK_IDS_PER_USER * userId);
        int candidateTaskId = nextTaskIdForUser(currentTaskId, userId);
        do {
            if (this.mRecentTasks.containsTaskId(candidateTaskId, userId) || this.mRootActivityContainer.anyTaskForId(candidateTaskId, 1) != null) {
                candidateTaskId = nextTaskIdForUser(candidateTaskId, userId);
            } else {
                this.mCurTaskIdForUser.put(userId, candidateTaskId);
                return candidateTaskId;
            }
        } while (candidateTaskId != currentTaskId);
        throw new IllegalStateException("Cannot get an available task id. Reached limit of 100000 running tasks per user.");
    }

    /* access modifiers changed from: package-private */
    public void waitActivityVisible(ComponentName name, WaitResult result, long startTimeMs) {
        this.mWaitingForActivityVisible.add(new WaitInfo(name, result, startTimeMs));
    }

    /* access modifiers changed from: package-private */
    public void cleanupActivity(ActivityRecord r) {
        this.mFinishingActivities.remove(r);
        stopWaitingForActivityVisible(r);
    }

    /* access modifiers changed from: package-private */
    public void stopWaitingForActivityVisible(ActivityRecord r) {
        boolean changed = false;
        for (int i = this.mWaitingForActivityVisible.size() - 1; i >= 0; i--) {
            WaitInfo w = this.mWaitingForActivityVisible.get(i);
            if (w.matches(r.mActivityComponent)) {
                WaitResult result = w.getResult();
                changed = true;
                result.timeout = false;
                result.who = w.getComponent();
                result.totalTime = SystemClock.uptimeMillis() - w.getStartTime();
                this.mWaitingForActivityVisible.remove(w);
            }
        }
        if (changed) {
            this.mService.mGlobalLock.notifyAll();
        }
    }

    /* access modifiers changed from: package-private */
    public void reportWaitingActivityLaunchedIfNeeded(ActivityRecord r, int result) {
        if (!this.mWaitingActivityLaunched.isEmpty()) {
            if (result == 3 || result == 2) {
                boolean changed = false;
                for (int i = this.mWaitingActivityLaunched.size() - 1; i >= 0; i--) {
                    WaitResult w = this.mWaitingActivityLaunched.remove(i);
                    if (w.who == null) {
                        changed = true;
                        w.result = result;
                        if (result == 3) {
                            w.who = r.mActivityComponent;
                        }
                    }
                }
                if (changed) {
                    this.mService.mGlobalLock.notifyAll();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void reportActivityLaunchedLocked(boolean timeout, ActivityRecord r, long totalTime, int launchState) {
        boolean changed = false;
        if (!(totalTime <= 0 || this.mPerfBoost == null || r.app == null)) {
            this.mPerfBoost.perfHint(4162, r.packageName, r.app.getPid(), 1);
        }
        for (int i = this.mWaitingActivityLaunched.size() - 1; i >= 0; i--) {
            WaitResult w = this.mWaitingActivityLaunched.remove(i);
            if (w.who == null) {
                changed = true;
                w.timeout = timeout;
                if (r != null) {
                    w.who = new ComponentName(r.info.packageName, r.info.name);
                }
                w.totalTime = totalTime;
                w.launchState = launchState;
            }
        }
        if (changed) {
            this.mService.mGlobalLock.notifyAll();
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityInfo resolveActivity(Intent intent, ResolveInfo rInfo, int startFlags, ProfilerInfo profilerInfo) {
        ActivityInfo aInfo = rInfo != null ? rInfo.activityInfo : null;
        if (aInfo != null) {
            intent.setComponent(new ComponentName(aInfo.applicationInfo.packageName, aInfo.name));
            if (!aInfo.processName.equals("system") && !((startFlags & 14) == 0 && profilerInfo == null)) {
                synchronized (this.mService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        this.mService.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$8ew6SY_v_7ex9pwFGDswbkGWuXc.INSTANCE, this.mService.mAmInternal, aInfo, Integer.valueOf(startFlags), profilerInfo, this.mService.mGlobalLock));
                        try {
                            this.mService.mGlobalLock.wait();
                        } catch (InterruptedException e) {
                        }
                    } catch (Throwable th) {
                        while (true) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
            String intentLaunchToken = intent.getLaunchToken();
            if (aInfo.launchToken == null && intentLaunchToken != null) {
                aInfo.launchToken = intentLaunchToken;
            }
        }
        return aInfo;
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0039 A[Catch:{ all -> 0x006c }] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0043 A[Catch:{ all -> 0x0065, all -> 0x006a }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.pm.ResolveInfo resolveIntent(android.content.Intent r17, java.lang.String r18, int r19, int r20, int r21) {
        /*
            r16 = this;
            r1 = r16
            r2 = 64
            java.lang.String r0 = "resolveIntent"
            android.os.Trace.traceBegin(r2, r0)     // Catch:{ all -> 0x006c }
            r0 = 65536(0x10000, float:9.18355E-41)
            r0 = r20 | r0
            r0 = r0 | 1024(0x400, float:1.435E-42)
            boolean r4 = r17.isWebIntent()     // Catch:{ all -> 0x006c }
            if (r4 != 0) goto L_0x0020
            int r4 = r17.getFlags()     // Catch:{ all -> 0x006c }
            r4 = r4 & 2048(0x800, float:2.87E-42)
            if (r4 == 0) goto L_0x001e
            goto L_0x0020
        L_0x001e:
            r11 = r0
            goto L_0x0024
        L_0x0020:
            r4 = 8388608(0x800000, float:1.17549435E-38)
            r0 = r0 | r4
            r11 = r0
        L_0x0024:
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService     // Catch:{ all -> 0x006c }
            int r4 = android.os.Binder.getCallingPid()     // Catch:{ all -> 0x006c }
            int r5 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x006c }
            com.android.server.wm.WindowProcessController r0 = r0.getProcessController((int) r4, (int) r5)     // Catch:{ all -> 0x006c }
            r12 = r0
            if (r12 == 0) goto L_0x0043
            android.content.pm.ApplicationInfo r0 = r12.mInfo     // Catch:{ all -> 0x006c }
            if (r0 == 0) goto L_0x0043
            android.content.pm.ApplicationInfo r0 = r12.mInfo     // Catch:{ all -> 0x006c }
            java.lang.String r0 = r0.packageName     // Catch:{ all -> 0x006c }
            r13 = r17
            r13.setSender(r0)     // Catch:{ all -> 0x006a }
            goto L_0x0045
        L_0x0043:
            r13 = r17
        L_0x0045:
            long r4 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x006a }
            r14 = r4
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService     // Catch:{ all -> 0x0065 }
            android.content.pm.PackageManagerInternal r4 = r0.getPackageManagerInternalLocked()     // Catch:{ all -> 0x0065 }
            r9 = 1
            r5 = r17
            r6 = r18
            r7 = r11
            r8 = r19
            r10 = r21
            android.content.pm.ResolveInfo r0 = r4.resolveIntent(r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0065 }
            android.os.Binder.restoreCallingIdentity(r14)     // Catch:{ all -> 0x006a }
            android.os.Trace.traceEnd(r2)
            return r0
        L_0x0065:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r14)     // Catch:{ all -> 0x006a }
            throw r0     // Catch:{ all -> 0x006a }
        L_0x006a:
            r0 = move-exception
            goto L_0x006f
        L_0x006c:
            r0 = move-exception
            r13 = r17
        L_0x006f:
            android.os.Trace.traceEnd(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStackSupervisor.resolveIntent(android.content.Intent, java.lang.String, int, int, int):android.content.pm.ResolveInfo");
    }

    public ActivityInfo resolveActivity(Intent intent, String resolvedType, int startFlags, ProfilerInfo profilerInfo, int userId, int filterCallingUid) {
        return resolveActivity(intent, resolveIntent(intent, resolvedType, userId, 0, filterCallingUid), startFlags, profilerInfo);
    }

    /* Debug info: failed to restart local var, previous not found, register: 38 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x020d, code lost:
        r4 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x0279, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x027e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x027f, code lost:
        r6 = r37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x0281, code lost:
        r4 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x02e8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x02e9, code lost:
        r4 = r36;
        r6 = r37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x02ee, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x02ef, code lost:
        r4 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0081, code lost:
        if (r2.appInfo.uid != r14) goto L_0x0083;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00e5, code lost:
        if (r15.getLockTaskModeState() != 1) goto L_0x00ea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x020c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0297  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x02b8  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x02bc  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02e1  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x02ee A[ExcHandler: all (th java.lang.Throwable), Splitter:B:91:0x01d8] */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x030e  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x032b A[Catch:{ all -> 0x036a }] */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0362  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0043 A[SYNTHETIC, Splitter:B:17:0x0043] */
    /* JADX WARNING: Removed duplicated region for block: B:182:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0050 A[Catch:{ all -> 0x0047 }] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0063 A[SYNTHETIC, Splitter:B:27:0x0063] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x006c A[SYNTHETIC, Splitter:B:32:0x006c] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x007d A[SYNTHETIC, Splitter:B:40:0x007d] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00d9 A[SYNTHETIC, Splitter:B:48:0x00d9] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x020c A[ExcHandler: all (th java.lang.Throwable), Splitter:B:97:0x0203] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean realStartActivityLocked(com.android.server.wm.ActivityRecord r39, com.android.server.wm.WindowProcessController r40, boolean r41, boolean r42) throws android.os.RemoteException {
        /*
            r38 = this;
            r1 = r38
            r2 = r39
            r3 = r40
            com.android.server.wm.RootActivityContainer r0 = r1.mRootActivityContainer
            boolean r0 = r0.allPausedActivitiesComplete()
            r4 = 0
            if (r0 != 0) goto L_0x0010
            return r4
        L_0x0010:
            com.android.server.wm.TaskRecord r5 = r39.getTaskRecord()
            com.android.server.wm.ActivityStack r12 = r5.getStack()
            r38.beginDeferResume()
            r2.startFreezingScreenLocked(r3, r4)     // Catch:{ all -> 0x0371 }
            r39.startLaunchTickingLocked()     // Catch:{ all -> 0x0371 }
            r39.setProcess(r40)     // Catch:{ all -> 0x0371 }
            if (r41 == 0) goto L_0x0037
            boolean r0 = r39.canResumeByCompat()     // Catch:{ all -> 0x002f }
            if (r0 != 0) goto L_0x0037
            r0 = 0
            r13 = r0
            goto L_0x0039
        L_0x002f:
            r0 = move-exception
            r13 = r41
            r35 = r5
            r4 = r12
            goto L_0x0377
        L_0x0037:
            r13 = r41
        L_0x0039:
            com.android.server.wm.KeyguardController r0 = r38.getKeyguardController()     // Catch:{ all -> 0x036c }
            boolean r0 = r0.isKeyguardLocked()     // Catch:{ all -> 0x036c }
            if (r0 == 0) goto L_0x004d
            r39.notifyUnknownVisibilityLaunched()     // Catch:{ all -> 0x0047 }
            goto L_0x004d
        L_0x0047:
            r0 = move-exception
            r35 = r5
            r4 = r12
            goto L_0x0377
        L_0x004d:
            r6 = 1
            if (r42 == 0) goto L_0x0059
            com.android.server.wm.RootActivityContainer r0 = r1.mRootActivityContainer     // Catch:{ all -> 0x0047 }
            int r7 = r39.getDisplayId()     // Catch:{ all -> 0x0047 }
            r0.ensureVisibilityAndConfig(r2, r7, r4, r6)     // Catch:{ all -> 0x0047 }
        L_0x0059:
            com.android.server.wm.ActivityStack r0 = r39.getActivityStack()     // Catch:{ all -> 0x036c }
            boolean r0 = r0.checkKeyguardVisibility(r2, r6, r6)     // Catch:{ all -> 0x036c }
            if (r0 == 0) goto L_0x0066
            r2.setVisibility(r6)     // Catch:{ all -> 0x0047 }
        L_0x0066:
            android.content.pm.ActivityInfo r0 = r2.info     // Catch:{ all -> 0x036c }
            android.content.pm.ApplicationInfo r0 = r0.applicationInfo     // Catch:{ all -> 0x036c }
            if (r0 == 0) goto L_0x0073
            android.content.pm.ActivityInfo r0 = r2.info     // Catch:{ all -> 0x0047 }
            android.content.pm.ApplicationInfo r0 = r0.applicationInfo     // Catch:{ all -> 0x0047 }
            int r0 = r0.uid     // Catch:{ all -> 0x0047 }
            goto L_0x0074
        L_0x0073:
            r0 = -1
        L_0x0074:
            r14 = r0
            int r0 = r2.mUserId     // Catch:{ all -> 0x036c }
            int r7 = r3.mUserId     // Catch:{ all -> 0x036c }
            java.lang.String r8 = "ActivityTaskManager"
            if (r0 != r7) goto L_0x0083
            android.content.pm.ApplicationInfo r0 = r2.appInfo     // Catch:{ all -> 0x0047 }
            int r0 = r0.uid     // Catch:{ all -> 0x0047 }
            if (r0 == r14) goto L_0x00bd
        L_0x0083:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x036c }
            r0.<init>()     // Catch:{ all -> 0x036c }
            java.lang.String r7 = "User ID for activity changing for "
            r0.append(r7)     // Catch:{ all -> 0x036c }
            r0.append(r2)     // Catch:{ all -> 0x036c }
            java.lang.String r7 = " appInfo.uid="
            r0.append(r7)     // Catch:{ all -> 0x036c }
            android.content.pm.ApplicationInfo r7 = r2.appInfo     // Catch:{ all -> 0x036c }
            int r7 = r7.uid     // Catch:{ all -> 0x036c }
            r0.append(r7)     // Catch:{ all -> 0x036c }
            java.lang.String r7 = " info.ai.uid="
            r0.append(r7)     // Catch:{ all -> 0x036c }
            r0.append(r14)     // Catch:{ all -> 0x036c }
            java.lang.String r7 = " old="
            r0.append(r7)     // Catch:{ all -> 0x036c }
            com.android.server.wm.WindowProcessController r7 = r2.app     // Catch:{ all -> 0x036c }
            r0.append(r7)     // Catch:{ all -> 0x036c }
            java.lang.String r7 = " new="
            r0.append(r7)     // Catch:{ all -> 0x036c }
            r0.append(r3)     // Catch:{ all -> 0x036c }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x036c }
            android.util.Slog.wtf(r8, r0)     // Catch:{ all -> 0x036c }
        L_0x00bd:
            int r0 = r2.launchCount     // Catch:{ all -> 0x036c }
            int r0 = r0 + r6
            r2.launchCount = r0     // Catch:{ all -> 0x036c }
            long r9 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x036c }
            r2.lastLaunchTime = r9     // Catch:{ all -> 0x036c }
            r3.addActivityIfNeeded(r2)     // Catch:{ all -> 0x036c }
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService     // Catch:{ all -> 0x036c }
            com.android.server.wm.LockTaskController r0 = r0.getLockTaskController()     // Catch:{ all -> 0x036c }
            r15 = r0
            int r0 = r5.mLockTaskAuth     // Catch:{ all -> 0x036c }
            r7 = 3
            r9 = 4
            r10 = 2
            if (r0 == r10) goto L_0x00e7
            int r0 = r5.mLockTaskAuth     // Catch:{ all -> 0x0047 }
            if (r0 == r9) goto L_0x00e7
            int r0 = r5.mLockTaskAuth     // Catch:{ all -> 0x0047 }
            if (r0 != r7) goto L_0x00ea
            int r0 = r15.getLockTaskModeState()     // Catch:{ all -> 0x0047 }
            if (r0 != r6) goto L_0x00ea
        L_0x00e7:
            r15.startLockTaskMode(r5, r4, r4)     // Catch:{ all -> 0x036c }
        L_0x00ea:
            boolean r0 = r40.hasThread()     // Catch:{ RemoteException -> 0x031e }
            if (r0 == 0) goto L_0x030e
            r0 = 0
            r11 = 0
            if (r13 == 0) goto L_0x0106
            java.util.ArrayList<android.app.ResultInfo> r9 = r2.results     // Catch:{ RemoteException -> 0x00fb }
            r0 = r9
            java.util.ArrayList<com.android.internal.content.ReferrerIntent> r9 = r2.newIntents     // Catch:{ RemoteException -> 0x00fb }
            r11 = r9
            goto L_0x0106
        L_0x00fb:
            r0 = move-exception
            r35 = r5
            r6 = r8
            r4 = r12
            r33 = r14
            r34 = r15
            goto L_0x0327
        L_0x0106:
            r9 = 5
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ RemoteException -> 0x031e }
            int r7 = r2.mUserId     // Catch:{ RemoteException -> 0x031e }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ RemoteException -> 0x031e }
            r9[r4] = r7     // Catch:{ RemoteException -> 0x031e }
            int r7 = java.lang.System.identityHashCode(r39)     // Catch:{ RemoteException -> 0x031e }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ RemoteException -> 0x031e }
            r9[r6] = r7     // Catch:{ RemoteException -> 0x031e }
            int r7 = r5.taskId     // Catch:{ RemoteException -> 0x031e }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ RemoteException -> 0x031e }
            r9[r10] = r7     // Catch:{ RemoteException -> 0x031e }
            java.lang.String r7 = r2.shortComponentName     // Catch:{ RemoteException -> 0x031e }
            r17 = 3
            r9[r17] = r7     // Catch:{ RemoteException -> 0x031e }
            com.android.server.wm.WindowProcessController r7 = r2.app     // Catch:{ RemoteException -> 0x031e }
            if (r7 == 0) goto L_0x0134
            com.android.server.wm.WindowProcessController r7 = r2.app     // Catch:{ RemoteException -> 0x00fb }
            int r7 = r7.getPid()     // Catch:{ RemoteException -> 0x00fb }
            goto L_0x0135
        L_0x0134:
            r7 = r4
        L_0x0135:
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ RemoteException -> 0x031e }
            r17 = 4
            r9[r17] = r7     // Catch:{ RemoteException -> 0x031e }
            r7 = 30006(0x7536, float:4.2047E-41)
            android.util.EventLog.writeEvent(r7, r9)     // Catch:{ RemoteException -> 0x031e }
            boolean r7 = r39.isActivityTypeHome()     // Catch:{ RemoteException -> 0x031e }
            if (r7 == 0) goto L_0x0155
            java.util.ArrayList<com.android.server.wm.ActivityRecord> r7 = r5.mActivities     // Catch:{ RemoteException -> 0x00fb }
            java.lang.Object r7 = r7.get(r4)     // Catch:{ RemoteException -> 0x00fb }
            com.android.server.wm.ActivityRecord r7 = (com.android.server.wm.ActivityRecord) r7     // Catch:{ RemoteException -> 0x00fb }
            com.android.server.wm.WindowProcessController r7 = r7.app     // Catch:{ RemoteException -> 0x00fb }
            r1.updateHomeProcess(r7)     // Catch:{ RemoteException -> 0x00fb }
        L_0x0155:
            com.android.server.wm.ActivityTaskManagerService r7 = r1.mService     // Catch:{ RemoteException -> 0x031e }
            android.content.pm.PackageManagerInternal r7 = r7.getPackageManagerInternalLocked()     // Catch:{ RemoteException -> 0x031e }
            android.content.Intent r9 = r2.intent     // Catch:{ RemoteException -> 0x031e }
            android.content.ComponentName r9 = r9.getComponent()     // Catch:{ RemoteException -> 0x031e }
            java.lang.String r9 = r9.getPackageName()     // Catch:{ RemoteException -> 0x031e }
            r7.notifyPackageUse(r9, r4)     // Catch:{ RemoteException -> 0x031e }
            r2.sleeping = r4     // Catch:{ RemoteException -> 0x031e }
            r2.forceNewConfig = r4     // Catch:{ RemoteException -> 0x031e }
            com.android.server.wm.ActivityTaskManagerService r7 = r1.mService     // Catch:{ RemoteException -> 0x031e }
            com.android.server.wm.AppWarnings r7 = r7.getAppWarningsLocked()     // Catch:{ RemoteException -> 0x031e }
            r7.onStartActivity(r2)     // Catch:{ RemoteException -> 0x031e }
            com.android.server.wm.ActivityTaskManagerService r7 = r1.mService     // Catch:{ RemoteException -> 0x031e }
            android.content.pm.ActivityInfo r9 = r2.info     // Catch:{ RemoteException -> 0x031e }
            android.content.pm.ApplicationInfo r9 = r9.applicationInfo     // Catch:{ RemoteException -> 0x031e }
            android.content.res.CompatibilityInfo r7 = r7.compatibilityInfoForPackageLocked(r9)     // Catch:{ RemoteException -> 0x031e }
            r2.compat = r7     // Catch:{ RemoteException -> 0x031e }
            android.util.MergedConfiguration r7 = new android.util.MergedConfiguration     // Catch:{ RemoteException -> 0x031e }
            android.content.res.Configuration r9 = r40.getConfiguration()     // Catch:{ RemoteException -> 0x031e }
            android.content.res.Configuration r6 = r39.getMergedOverrideConfiguration()     // Catch:{ RemoteException -> 0x031e }
            r7.<init>(r9, r6)     // Catch:{ RemoteException -> 0x031e }
            r6 = r7
            r2.setLastReportedConfiguration(r6)     // Catch:{ RemoteException -> 0x031e }
            com.android.server.wm.ActivityTaskManagerServiceInjector.onForegroundActivityChangedLocked(r39)     // Catch:{ RemoteException -> 0x031e }
            android.content.Intent r7 = r2.intent     // Catch:{ RemoteException -> 0x031e }
            android.os.Bundle r9 = r2.icicle     // Catch:{ RemoteException -> 0x031e }
            r1.logIfTransactionTooLarge(r7, r9)     // Catch:{ RemoteException -> 0x031e }
            android.app.IApplicationThread r7 = r40.getThread()     // Catch:{ RemoteException -> 0x031e }
            android.view.IApplicationToken$Stub r9 = r2.appToken     // Catch:{ RemoteException -> 0x031e }
            android.app.servertransaction.ClientTransaction r7 = android.app.servertransaction.ClientTransaction.obtain(r7, r9)     // Catch:{ RemoteException -> 0x031e }
            com.android.server.wm.ActivityDisplay r9 = r39.getDisplay()     // Catch:{ RemoteException -> 0x031e }
            com.android.server.wm.DisplayContent r9 = r9.mDisplayContent     // Catch:{ RemoteException -> 0x031e }
            android.content.Intent r4 = new android.content.Intent     // Catch:{ RemoteException -> 0x031e }
            android.content.Intent r10 = r2.intent     // Catch:{ RemoteException -> 0x031e }
            r4.<init>(r10)     // Catch:{ RemoteException -> 0x031e }
            int r17 = java.lang.System.identityHashCode(r39)     // Catch:{ RemoteException -> 0x031e }
            android.content.pm.ActivityInfo r10 = r2.info     // Catch:{ RemoteException -> 0x031e }
            android.content.res.Configuration r19 = r6.getGlobalConfiguration()     // Catch:{ RemoteException -> 0x031e }
            android.content.res.Configuration r20 = r6.getOverrideConfiguration()     // Catch:{ RemoteException -> 0x031e }
            r32 = r6
            android.content.res.CompatibilityInfo r6 = r2.compat     // Catch:{ RemoteException -> 0x031e }
            r33 = r14
            java.lang.String r14 = r2.launchedFromPackage     // Catch:{ RemoteException -> 0x0306 }
            r34 = r15
            com.android.internal.app.IVoiceInteractor r15 = r5.voiceInteractor     // Catch:{ RemoteException -> 0x0300 }
            int r24 = r40.getReportedProcState()     // Catch:{ RemoteException -> 0x0300 }
            r35 = r5
            android.os.Bundle r5 = r2.icicle     // Catch:{ RemoteException -> 0x02fc, all -> 0x02f8 }
            r36 = r12
            android.os.PersistableBundle r12 = r2.persistentState     // Catch:{ RemoteException -> 0x02f3, all -> 0x02ee }
            boolean r29 = r9.isNextTransitionForward()     // Catch:{ RemoteException -> 0x02f3, all -> 0x02ee }
            android.app.ProfilerInfo r30 = r40.createProfilerInfoIfNeeded()     // Catch:{ RemoteException -> 0x02f3, all -> 0x02ee }
            r37 = r8
            android.os.Binder r8 = r2.assistToken     // Catch:{ RemoteException -> 0x02e8, all -> 0x02ee }
            r16 = r4
            r18 = r10
            r21 = r6
            r22 = r14
            r23 = r15
            r25 = r5
            r26 = r12
            r27 = r0
            r28 = r11
            r31 = r8
            android.app.servertransaction.LaunchActivityItem r4 = android.app.servertransaction.LaunchActivityItem.obtain(r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31)     // Catch:{ RemoteException -> 0x02e8, all -> 0x02ee }
            r7.addCallback(r4)     // Catch:{ RemoteException -> 0x02e8, all -> 0x02ee }
            if (r13 == 0) goto L_0x0218
            boolean r4 = r9.isNextTransitionForward()     // Catch:{ RemoteException -> 0x0211, all -> 0x020c }
            android.app.servertransaction.ResumeActivityItem r4 = android.app.servertransaction.ResumeActivityItem.obtain(r4)     // Catch:{ RemoteException -> 0x0211, all -> 0x020c }
            goto L_0x021c
        L_0x020c:
            r0 = move-exception
            r4 = r36
            goto L_0x0377
        L_0x0211:
            r0 = move-exception
            r4 = r36
            r6 = r37
            goto L_0x0327
        L_0x0218:
            android.app.servertransaction.PauseActivityItem r4 = android.app.servertransaction.PauseActivityItem.obtain()     // Catch:{ RemoteException -> 0x02e8, all -> 0x02ee }
        L_0x021c:
            r7.setLifecycleStateRequest(r4)     // Catch:{ RemoteException -> 0x02e8, all -> 0x02ee }
            com.android.server.wm.ActivityTaskManagerService r5 = r1.mService     // Catch:{ RemoteException -> 0x02e8, all -> 0x02ee }
            com.android.server.wm.ClientLifecycleManager r5 = r5.getLifecycleManager()     // Catch:{ RemoteException -> 0x02e8, all -> 0x02ee }
            r5.scheduleTransaction(r7)     // Catch:{ RemoteException -> 0x02e8, all -> 0x02ee }
            android.content.pm.ApplicationInfo r5 = r3.mInfo     // Catch:{ RemoteException -> 0x02e8, all -> 0x02ee }
            int r5 = r5.privateFlags     // Catch:{ RemoteException -> 0x02e8, all -> 0x02ee }
            r6 = 2
            r5 = r5 & r6
            if (r5 == 0) goto L_0x0285
            com.android.server.wm.ActivityTaskManagerService r5 = r1.mService     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            boolean r5 = r5.mHasHeavyWeightFeature     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            if (r5 == 0) goto L_0x0285
            java.lang.String r5 = r3.mName     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            android.content.pm.ApplicationInfo r6 = r3.mInfo     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            java.lang.String r6 = r6.packageName     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            boolean r5 = r5.equals(r6)     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            if (r5 == 0) goto L_0x027b
            com.android.server.wm.ActivityTaskManagerService r5 = r1.mService     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            com.android.server.wm.WindowProcessController r5 = r5.mHeavyWeightProcess     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            if (r5 == 0) goto L_0x0271
            com.android.server.wm.ActivityTaskManagerService r5 = r1.mService     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            com.android.server.wm.WindowProcessController r5 = r5.mHeavyWeightProcess     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            if (r5 == r3) goto L_0x0271
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            r5.<init>()     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            java.lang.String r6 = "Starting new heavy weight process "
            r5.append(r6)     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            r5.append(r3)     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            java.lang.String r6 = " when already running "
            r5.append(r6)     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            com.android.server.wm.ActivityTaskManagerService r6 = r1.mService     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            com.android.server.wm.WindowProcessController r6 = r6.mHeavyWeightProcess     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            r5.append(r6)     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            java.lang.String r5 = r5.toString()     // Catch:{ RemoteException -> 0x027e, all -> 0x020c }
            r6 = r37
            android.util.Slog.w(r6, r5)     // Catch:{ RemoteException -> 0x0279, all -> 0x020c }
            goto L_0x0273
        L_0x0271:
            r6 = r37
        L_0x0273:
            com.android.server.wm.ActivityTaskManagerService r5 = r1.mService     // Catch:{ RemoteException -> 0x0279, all -> 0x020c }
            r5.setHeavyWeightProcess(r2)     // Catch:{ RemoteException -> 0x0279, all -> 0x020c }
            goto L_0x0287
        L_0x0279:
            r0 = move-exception
            goto L_0x0281
        L_0x027b:
            r6 = r37
            goto L_0x0287
        L_0x027e:
            r0 = move-exception
            r6 = r37
        L_0x0281:
            r4 = r36
            goto L_0x0327
        L_0x0285:
            r6 = r37
        L_0x0287:
            r38.endDeferResume()
            r4 = 0
            r2.launchFailed = r4
            r4 = r36
            boolean r0 = r4.updateLRUListLocked(r2)
            if (r0 == 0) goto L_0x02b0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "Activity "
            r0.append(r5)
            r0.append(r2)
            java.lang.String r5 = " being launched, but already in LRU list"
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            android.util.Slog.w(r6, r0)
        L_0x02b0:
            if (r13 == 0) goto L_0x02bc
            boolean r0 = r38.readyToResume()
            if (r0 == 0) goto L_0x02bc
            r4.minimalResumeActivityLocked(r2)
            goto L_0x02c3
        L_0x02bc:
            com.android.server.wm.ActivityStack$ActivityState r0 = com.android.server.wm.ActivityStack.ActivityState.PAUSED
            java.lang.String r5 = "realStartActivityLocked"
            r2.setState(r0, r5)
        L_0x02c3:
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService
            int r0 = r0.mTopProcessState
            android.content.pm.ActivityInfo r5 = r2.info
            r3.onStartActivity(r0, r5)
            com.android.server.wm.RootActivityContainer r0 = r1.mRootActivityContainer
            boolean r0 = r0.isTopDisplayFocusedStack(r4)
            if (r0 == 0) goto L_0x02dd
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService
            com.android.server.wm.ActivityStartController r0 = r0.getActivityStartController()
            r0.startSetupActivity()
        L_0x02dd:
            com.android.server.wm.WindowProcessController r0 = r2.app
            if (r0 == 0) goto L_0x02e6
            com.android.server.wm.WindowProcessController r0 = r2.app
            r0.updateServiceConnectionActivities()
        L_0x02e6:
            r5 = 1
            return r5
        L_0x02e8:
            r0 = move-exception
            r4 = r36
            r6 = r37
            goto L_0x02f7
        L_0x02ee:
            r0 = move-exception
            r4 = r36
            goto L_0x0377
        L_0x02f3:
            r0 = move-exception
            r6 = r8
            r4 = r36
        L_0x02f7:
            goto L_0x0327
        L_0x02f8:
            r0 = move-exception
            r4 = r12
            goto L_0x0377
        L_0x02fc:
            r0 = move-exception
            r6 = r8
            r4 = r12
            goto L_0x0327
        L_0x0300:
            r0 = move-exception
            r35 = r5
            r6 = r8
            r4 = r12
            goto L_0x0327
        L_0x0306:
            r0 = move-exception
            r35 = r5
            r6 = r8
            r4 = r12
            r34 = r15
            goto L_0x0327
        L_0x030e:
            r35 = r5
            r6 = r8
            r4 = r12
            r33 = r14
            r34 = r15
            android.os.RemoteException r0 = new android.os.RemoteException     // Catch:{ RemoteException -> 0x031c }
            r0.<init>()     // Catch:{ RemoteException -> 0x031c }
            throw r0     // Catch:{ RemoteException -> 0x031c }
        L_0x031c:
            r0 = move-exception
            goto L_0x0327
        L_0x031e:
            r0 = move-exception
            r35 = r5
            r6 = r8
            r4 = r12
            r33 = r14
            r34 = r15
        L_0x0327:
            boolean r5 = r2.launchFailed     // Catch:{ all -> 0x036a }
            if (r5 == 0) goto L_0x0362
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x036a }
            r5.<init>()     // Catch:{ all -> 0x036a }
            java.lang.String r7 = "Second failure launching "
            r5.append(r7)     // Catch:{ all -> 0x036a }
            android.content.Intent r7 = r2.intent     // Catch:{ all -> 0x036a }
            android.content.ComponentName r7 = r7.getComponent()     // Catch:{ all -> 0x036a }
            java.lang.String r7 = r7.flattenToShortString()     // Catch:{ all -> 0x036a }
            r5.append(r7)     // Catch:{ all -> 0x036a }
            java.lang.String r7 = ", giving up"
            r5.append(r7)     // Catch:{ all -> 0x036a }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x036a }
            android.util.Slog.e(r6, r5, r0)     // Catch:{ all -> 0x036a }
            r40.appDied()     // Catch:{ all -> 0x036a }
            android.view.IApplicationToken$Stub r7 = r2.appToken     // Catch:{ all -> 0x036a }
            r8 = 0
            r9 = 0
            java.lang.String r10 = "2nd-crash"
            r11 = 0
            r6 = r4
            r6.requestFinishActivityLocked(r7, r8, r9, r10, r11)     // Catch:{ all -> 0x036a }
            r38.endDeferResume()
            r5 = 0
            return r5
        L_0x0362:
            r5 = 1
            r2.launchFailed = r5     // Catch:{ all -> 0x036a }
            r3.removeActivity(r2)     // Catch:{ all -> 0x036a }
            throw r0     // Catch:{ all -> 0x036a }
        L_0x036a:
            r0 = move-exception
            goto L_0x0377
        L_0x036c:
            r0 = move-exception
            r35 = r5
            r4 = r12
            goto L_0x0377
        L_0x0371:
            r0 = move-exception
            r35 = r5
            r4 = r12
            r13 = r41
        L_0x0377:
            r38.endDeferResume()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStackSupervisor.realStartActivityLocked(com.android.server.wm.ActivityRecord, com.android.server.wm.WindowProcessController, boolean, boolean):boolean");
    }

    /* access modifiers changed from: package-private */
    public void updateHomeProcess(WindowProcessController app) {
        if (app != null && this.mService.mHomeProcess != app) {
            if (!this.mHandler.hasMessages(REPORT_HOME_CHANGED_MSG)) {
                this.mHandler.sendEmptyMessage(REPORT_HOME_CHANGED_MSG);
            }
            this.mService.mHomeProcess = app;
        }
    }

    private void logIfTransactionTooLarge(Intent intent, Bundle icicle) {
        Bundle extras;
        int extrasSize = 0;
        if (!(intent == null || (extras = intent.getExtras()) == null)) {
            extrasSize = extras.getSize();
        }
        int icicleSize = icicle == null ? 0 : icicle.getSize();
        if (extrasSize + icicleSize > 200000) {
            Slog.e("ActivityTaskManager", "Transaction too large, intent: " + intent + ", extras size: " + extrasSize + ", icicle size: " + icicleSize);
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x009d A[Catch:{ all -> 0x00e0 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startSpecificActivityLocked(com.android.server.wm.ActivityRecord r19, boolean r20, boolean r21) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            java.lang.String r3 = "ActivityTaskManager"
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService
            java.lang.String r4 = r2.processName
            android.content.pm.ActivityInfo r5 = r2.info
            android.content.pm.ApplicationInfo r5 = r5.applicationInfo
            int r5 = r5.uid
            com.android.server.wm.WindowProcessController r4 = r0.getProcessController((java.lang.String) r4, (int) r5)
            r5 = 0
            if (r4 == 0) goto L_0x0081
            boolean r0 = r4.hasThread()
            if (r0 == 0) goto L_0x0081
            android.util.BoostFramework r0 = r1.mPerfBoost     // Catch:{ RemoteException -> 0x005c }
            if (r0 == 0) goto L_0x0052
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x005c }
            r0.<init>()     // Catch:{ RemoteException -> 0x005c }
            java.lang.String r6 = "The Process "
            r0.append(r6)     // Catch:{ RemoteException -> 0x005c }
            java.lang.String r6 = r2.processName     // Catch:{ RemoteException -> 0x005c }
            r0.append(r6)     // Catch:{ RemoteException -> 0x005c }
            java.lang.String r6 = " Already Exists in BG. So sending its PID: "
            r0.append(r6)     // Catch:{ RemoteException -> 0x005c }
            int r6 = r4.getPid()     // Catch:{ RemoteException -> 0x005c }
            r0.append(r6)     // Catch:{ RemoteException -> 0x005c }
            java.lang.String r0 = r0.toString()     // Catch:{ RemoteException -> 0x005c }
            android.util.Slog.i(r3, r0)     // Catch:{ RemoteException -> 0x005c }
            android.util.BoostFramework r0 = r1.mPerfBoost     // Catch:{ RemoteException -> 0x005c }
            r6 = 4225(0x1081, float:5.92E-42)
            java.lang.String r7 = r2.processName     // Catch:{ RemoteException -> 0x005c }
            int r8 = r4.getPid()     // Catch:{ RemoteException -> 0x005c }
            r9 = 101(0x65, float:1.42E-43)
            r0.perfHint(r6, r7, r8, r9)     // Catch:{ RemoteException -> 0x005c }
        L_0x0052:
            r6 = r20
            r7 = r21
            r1.realStartActivityLocked(r2, r4, r6, r7)     // Catch:{ RemoteException -> 0x005a }
            return
        L_0x005a:
            r0 = move-exception
            goto L_0x0061
        L_0x005c:
            r0 = move-exception
            r6 = r20
            r7 = r21
        L_0x0061:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Exception when starting activity "
            r8.append(r9)
            android.content.Intent r9 = r2.intent
            android.content.ComponentName r9 = r9.getComponent()
            java.lang.String r9 = r9.flattenToShortString()
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            android.util.Slog.w(r3, r8, r0)
            r5 = 1
            goto L_0x0085
        L_0x0081:
            r6 = r20
            r7 = r21
        L_0x0085:
            r0 = 1
            r2.isColdStart = r0
            com.android.server.wm.KeyguardController r0 = r18.getKeyguardController()
            boolean r0 = r0.isKeyguardLocked()
            if (r0 == 0) goto L_0x0095
            r19.notifyUnknownVisibilityLaunched()
        L_0x0095:
            r8 = 64
            boolean r0 = android.os.Trace.isTagEnabled(r8)     // Catch:{ all -> 0x00e0 }
            if (r0 == 0) goto L_0x00b3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x00e0 }
            r0.<init>()     // Catch:{ all -> 0x00e0 }
            java.lang.String r3 = "dispatchingStartProcess:"
            r0.append(r3)     // Catch:{ all -> 0x00e0 }
            java.lang.String r3 = r2.processName     // Catch:{ all -> 0x00e0 }
            r0.append(r3)     // Catch:{ all -> 0x00e0 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x00e0 }
            android.os.Trace.traceBegin(r8, r0)     // Catch:{ all -> 0x00e0 }
        L_0x00b3:
            com.android.server.wm.-$$Lambda$YnRx7DG_XWEmLQfCe6Y23Ju81c0 r10 = com.android.server.wm.$$Lambda$YnRx7DG_XWEmLQfCe6Y23Ju81c0.INSTANCE     // Catch:{ all -> 0x00e0 }
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService     // Catch:{ all -> 0x00e0 }
            android.app.ActivityManagerInternal r11 = r0.mAmInternal     // Catch:{ all -> 0x00e0 }
            java.lang.String r12 = r2.processName     // Catch:{ all -> 0x00e0 }
            android.content.pm.ActivityInfo r0 = r2.info     // Catch:{ all -> 0x00e0 }
            android.content.pm.ApplicationInfo r13 = r0.applicationInfo     // Catch:{ all -> 0x00e0 }
            java.lang.Boolean r14 = java.lang.Boolean.valueOf(r5)     // Catch:{ all -> 0x00e0 }
            java.lang.String r15 = "activity"
            android.content.Intent r0 = r2.intent     // Catch:{ all -> 0x00e0 }
            android.content.ComponentName r16 = r0.getComponent()     // Catch:{ all -> 0x00e0 }
            java.lang.String r0 = r2.launchedFromPackage     // Catch:{ all -> 0x00e0 }
            r17 = r0
            android.os.Message r0 = com.android.internal.util.function.pooled.PooledLambda.obtainMessage(r10, r11, r12, r13, r14, r15, r16, r17)     // Catch:{ all -> 0x00e0 }
            com.android.server.wm.ActivityTaskManagerService r3 = r1.mService     // Catch:{ all -> 0x00e0 }
            com.android.server.wm.ActivityTaskManagerService$H r3 = r3.mH     // Catch:{ all -> 0x00e0 }
            r3.sendMessage(r0)     // Catch:{ all -> 0x00e0 }
            android.os.Trace.traceEnd(r8)
            return
        L_0x00e0:
            r0 = move-exception
            android.os.Trace.traceEnd(r8)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStackSupervisor.startSpecificActivityLocked(com.android.server.wm.ActivityRecord, boolean, boolean):void");
    }

    /* access modifiers changed from: package-private */
    public boolean checkStartAnyActivityPermission(Intent intent, ActivityInfo aInfo, String resultWho, int requestCode, int callingPid, int callingUid, String callingPackage, boolean ignoreTargetSecurity, boolean launchingInTask, WindowProcessController callerApp, ActivityRecord resultRecord, ActivityStack resultStack, ActivityStackSupervisorInjector.OpCheckData checker) {
        String msg;
        ActivityInfo activityInfo = aInfo;
        int i = callingPid;
        int i2 = callingUid;
        WindowProcessController windowProcessController = callerApp;
        boolean isCallerRecents = this.mService.getRecentTasks() != null && this.mService.getRecentTasks().isCallerRecents(i2);
        ActivityTaskManagerService activityTaskManagerService = this.mService;
        if (ActivityTaskManagerService.checkPermission("android.permission.START_ANY_ACTIVITY", i, i2) == 0) {
            ActivityStackSupervisorInjector.OpCheckData opCheckData = checker;
            return true;
        } else if (!isCallerRecents || !launchingInTask) {
            String str = callingPackage;
            int i3 = callingPid;
            int i4 = callingUid;
            int componentRestriction = getComponentRestrictionForCallingPackage(aInfo, str, i3, i4, ignoreTargetSecurity);
            int actionRestriction = getActionRestrictionForCallingPackage(intent.getAction(), str, i3, i4, checker);
            if (componentRestriction == 1) {
                ActivityStackSupervisorInjector.OpCheckData opCheckData2 = checker;
            } else if (actionRestriction == 1) {
                ActivityStackSupervisorInjector.OpCheckData opCheckData3 = checker;
            } else if (actionRestriction == 2) {
                Slog.w("ActivityTaskManager", "Appop Denial: starting " + intent.toString() + " from " + windowProcessController + " (pid=" + i + ", uid=" + i2 + ") requires " + AppOpsManager.permissionToOp(ACTION_TO_RUNTIME_PERMISSION.get(intent.getAction())));
                return false;
            } else if (componentRestriction == 2) {
                Slog.w("ActivityTaskManager", "Appop Denial: starting " + intent.toString() + " from " + windowProcessController + " (pid=" + i + ", uid=" + i2 + ") requires appop " + AppOpsManager.permissionToOp(activityInfo.permission));
                if (checker.newAInfo == null) {
                    return false;
                }
                return true;
            } else {
                ActivityStackSupervisorInjector.OpCheckData opCheckData4 = checker;
                return true;
            }
            if (resultRecord != null) {
                resultStack.sendActivityResultLocked(-1, resultRecord, resultWho, requestCode, 0, (Intent) null);
            }
            if (actionRestriction == 1) {
                msg = "Permission Denial: starting " + intent.toString() + " from " + windowProcessController + " (pid=" + i + ", uid=" + i2 + ") with revoked permission " + ACTION_TO_RUNTIME_PERMISSION.get(intent.getAction());
            } else if (!activityInfo.exported) {
                msg = "Permission Denial: starting " + intent.toString() + " from " + windowProcessController + " (pid=" + i + ", uid=" + i2 + ") not exported from uid " + activityInfo.applicationInfo.uid;
            } else {
                msg = "Permission Denial: starting " + intent.toString() + " from " + windowProcessController + " (pid=" + i + ", uid=" + i2 + ") requires " + activityInfo.permission;
            }
            Slog.w("ActivityTaskManager", msg);
            throw new SecurityException(msg);
        } else {
            ActivityStackSupervisorInjector.OpCheckData opCheckData5 = checker;
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isCallerAllowedToLaunchOnDisplay(int callingPid, int callingUid, int launchDisplayId, ActivityInfo aInfo) {
        if (callingPid == -1 && callingUid == -1) {
            return true;
        }
        ActivityDisplay activityDisplay = this.mRootActivityContainer.getActivityDisplayOrCreate(launchDisplayId);
        if (activityDisplay == null || activityDisplay.isRemoved()) {
            Slog.w("ActivityTaskManager", "Launch on display check: display not found");
            return false;
        }
        ActivityTaskManagerService activityTaskManagerService = this.mService;
        if (ActivityTaskManagerService.checkPermission("android.permission.INTERNAL_SYSTEM_WINDOW", callingPid, callingUid) == 0) {
            return true;
        }
        boolean uidPresentOnDisplay = activityDisplay.isUidPresent(callingUid);
        int displayOwnerUid = activityDisplay.mDisplay.getOwnerUid();
        if (!(activityDisplay.mDisplay.getType() != 5 || displayOwnerUid == 1000 || displayOwnerUid == aInfo.applicationInfo.uid)) {
            if ((aInfo.flags & Integer.MIN_VALUE) == 0) {
                return false;
            }
            ActivityTaskManagerService activityTaskManagerService2 = this.mService;
            if (ActivityTaskManagerService.checkPermission("android.permission.ACTIVITY_EMBEDDING", callingPid, callingUid) == -1 && !uidPresentOnDisplay) {
                return false;
            }
        }
        if (!activityDisplay.isPrivate() || displayOwnerUid == callingUid || uidPresentOnDisplay) {
            return true;
        }
        Slog.w("ActivityTaskManager", "Launch on display check: denied");
        return false;
    }

    /* access modifiers changed from: package-private */
    public UserInfo getUserInfo(int userId) {
        long identity = Binder.clearCallingIdentity();
        try {
            return UserManager.get(this.mService.mContext).getUserInfo(userId);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    private int getComponentRestrictionForCallingPackage(ActivityInfo activityInfo, String callingPackage, int callingPid, int callingUid, boolean ignoreTargetSecurity) {
        int opCode;
        if (!ignoreTargetSecurity) {
            ActivityTaskManagerService activityTaskManagerService = this.mService;
            if (ActivityTaskManagerService.checkComponentPermission(activityInfo.permission, callingPid, callingUid, activityInfo.applicationInfo.uid, activityInfo.exported) == -1) {
                return 1;
            }
        }
        if (activityInfo.permission == null || (opCode = AppOpsManager.permissionToOpCode(activityInfo.permission)) == -1 || this.mService.getAppOpsService().checkOperation(opCode, callingUid, callingPackage) == 0 || ignoreTargetSecurity) {
            return 0;
        }
        return 2;
    }

    private int getActionRestrictionForCallingPackage(String action, String callingPackage, int callingPid, int callingUid, ActivityStackSupervisorInjector.OpCheckData checker) {
        String permission;
        if (action == null || (permission = ACTION_TO_RUNTIME_PERMISSION.get(action)) == null) {
            return 0;
        }
        try {
            if (!ArrayUtils.contains(this.mService.mContext.getPackageManager().getPackageInfo(callingPackage, 4096).requestedPermissions, permission)) {
                return 0;
            }
            ActivityTaskManagerService activityTaskManagerService = this.mService;
            if (ActivityTaskManagerService.checkPermission(permission, callingPid, callingUid) == -1) {
                return 1;
            }
            int opCode = AppOpsManager.permissionToOpCode(permission);
            if (opCode == -1 || ActivityStackSupervisorInjector.noteOperationLocked(opCode, callingUid, callingPackage, this.mHandler, checker) == 0) {
                return 0;
            }
            return 2;
        } catch (PackageManager.NameNotFoundException e) {
            Slog.i("ActivityTaskManager", "Cannot find package info for " + callingPackage);
            return 0;
        }
    }

    /* access modifiers changed from: package-private */
    public void setLaunchSource(int uid) {
        this.mLaunchingActivityWakeLock.setWorkSource(new WorkSource(uid));
    }

    /* access modifiers changed from: package-private */
    public void setLaunchSource(int uid, String pkgName) {
        this.mLaunchingActivityWakeLock.setWorkSource(new WorkSource(uid, pkgName));
    }

    /* access modifiers changed from: package-private */
    public void acquireLaunchWakelock() {
        this.mLaunchingActivityWakeLock.acquire();
        if (!this.mHandler.hasMessages(LAUNCH_TIMEOUT_MSG)) {
            this.mHandler.sendEmptyMessageDelayed(LAUNCH_TIMEOUT_MSG, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
        }
    }

    @GuardedBy({"mService"})
    private boolean checkFinishBootingLocked() {
        boolean booting = this.mService.isBooting();
        boolean enableScreen = false;
        this.mService.setBooting(false);
        if (!this.mService.isBooted()) {
            this.mService.setBooted(true);
            enableScreen = true;
        }
        if (booting || enableScreen) {
            this.mService.postFinishBooting(booting, enableScreen);
        }
        return booting;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: com.android.server.wm.ActivityRecord} */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    @com.android.internal.annotations.GuardedBy({"mService"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final com.android.server.wm.ActivityRecord activityIdleInternalLocked(android.os.IBinder r18, boolean r19, boolean r20, android.content.res.Configuration r21) {
        /*
            r17 = this;
            r6 = r17
            r7 = r21
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            com.android.server.wm.ActivityRecord r14 = com.android.server.wm.ActivityRecord.forTokenLocked(r18)
            r5 = 1
            if (r14 == 0) goto L_0x0054
            com.android.server.wm.ActivityStackSupervisor$ActivityStackSupervisorHandler r0 = r6.mHandler
            r1 = 200(0xc8, float:2.8E-43)
            r0.removeMessages(r1, r14)
            r14.finishLaunchTickingLocked()
            if (r19 == 0) goto L_0x002d
            r3 = -1
            r16 = -1
            r0 = r17
            r1 = r19
            r2 = r14
            r15 = r5
            r5 = r16
            r0.reportActivityLaunchedLocked(r1, r2, r3, r5)
            goto L_0x002e
        L_0x002d:
            r15 = r5
        L_0x002e:
            if (r7 == 0) goto L_0x0033
            r14.setLastReportedGlobalConfiguration(r7)
        L_0x0033:
            r14.idle = r15
            com.android.server.wm.ActivityTaskManagerService r0 = r6.mService
            boolean r0 = r0.isBooting()
            if (r0 == 0) goto L_0x0045
            com.android.server.wm.RootActivityContainer r0 = r6.mRootActivityContainer
            boolean r0 = r0.allResumedActivitiesIdle()
            if (r0 != 0) goto L_0x0047
        L_0x0045:
            if (r19 == 0) goto L_0x004b
        L_0x0047:
            boolean r12 = r17.checkFinishBootingLocked()
        L_0x004b:
            android.content.pm.ActivityInfo r0 = r14.info
            com.android.server.wm.ActivityTaskManagerServiceInjector.activityIdle(r0)
            r0 = 0
            r14.mRelaunchReason = r0
            goto L_0x0055
        L_0x0054:
            r15 = r5
        L_0x0055:
            com.android.server.wm.RootActivityContainer r0 = r6.mRootActivityContainer
            boolean r0 = r0.allResumedActivitiesIdle()
            if (r0 == 0) goto L_0x007f
            if (r14 == 0) goto L_0x0064
            com.android.server.wm.ActivityTaskManagerService r0 = r6.mService
            r0.scheduleAppGcsLocked()
        L_0x0064:
            android.os.PowerManager$WakeLock r0 = r6.mLaunchingActivityWakeLock
            boolean r0 = r0.isHeld()
            if (r0 == 0) goto L_0x0078
            com.android.server.wm.ActivityStackSupervisor$ActivityStackSupervisorHandler r0 = r6.mHandler
            r1 = 204(0xcc, float:2.86E-43)
            r0.removeMessages(r1)
            android.os.PowerManager$WakeLock r0 = r6.mLaunchingActivityWakeLock
            r0.release()
        L_0x0078:
            com.android.server.wm.RootActivityContainer r0 = r6.mRootActivityContainer
            r1 = 0
            r2 = 0
            r0.ensureActivitiesVisible(r1, r2, r2)
        L_0x007f:
            r0 = r20
            java.util.ArrayList r1 = r6.processStoppingActivitiesLocked(r14, r15, r0)
            if (r1 == 0) goto L_0x008c
            int r2 = r1.size()
            goto L_0x008d
        L_0x008c:
            r2 = 0
        L_0x008d:
            java.util.ArrayList<com.android.server.wm.ActivityRecord> r3 = r6.mFinishingActivities
            int r3 = r3.size()
            r4 = r3
            if (r3 <= 0) goto L_0x00a3
            java.util.ArrayList r3 = new java.util.ArrayList
            java.util.ArrayList<com.android.server.wm.ActivityRecord> r5 = r6.mFinishingActivities
            r3.<init>(r5)
            r8 = r3
            java.util.ArrayList<com.android.server.wm.ActivityRecord> r3 = r6.mFinishingActivities
            r3.clear()
        L_0x00a3:
            java.util.ArrayList<com.android.server.am.UserState> r3 = r6.mStartingUsers
            int r3 = r3.size()
            if (r3 <= 0) goto L_0x00b8
            java.util.ArrayList r3 = new java.util.ArrayList
            java.util.ArrayList<com.android.server.am.UserState> r5 = r6.mStartingUsers
            r3.<init>(r5)
            r9 = r3
            java.util.ArrayList<com.android.server.am.UserState> r3 = r6.mStartingUsers
            r3.clear()
        L_0x00b8:
            r3 = 0
        L_0x00b9:
            if (r3 >= r2) goto L_0x00f7
            java.lang.Object r5 = r1.get(r3)
            r14 = r5
            com.android.server.wm.ActivityRecord r14 = (com.android.server.wm.ActivityRecord) r14
            com.android.server.wm.ActivityStack r5 = r14.getActivityStack()
            if (r5 == 0) goto L_0x00f4
            boolean r10 = r14.finishing
            if (r10 == 0) goto L_0x00d3
            java.lang.String r10 = "activityIdleInternalLocked"
            r11 = 0
            r5.finishCurrentActivityLocked(r14, r11, r11, r10)
            goto L_0x00f4
        L_0x00d3:
            r11 = 0
            boolean r10 = r14.mIsCastMode
            if (r10 == 0) goto L_0x00f1
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "current activity should not stop, activity:"
            r10.append(r11)
            java.lang.String r11 = r14.shortComponentName
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            java.lang.String r11 = "ActivityTaskManager"
            android.util.Slog.i(r11, r10)
            goto L_0x00f4
        L_0x00f1:
            r5.stopActivityLocked(r14)
        L_0x00f4:
            int r3 = r3 + 1
            goto L_0x00b9
        L_0x00f7:
            r3 = 0
        L_0x00f8:
            if (r3 >= r4) goto L_0x0112
            java.lang.Object r5 = r8.get(r3)
            r14 = r5
            com.android.server.wm.ActivityRecord r14 = (com.android.server.wm.ActivityRecord) r14
            com.android.server.wm.ActivityStack r5 = r14.getActivityStack()
            if (r5 == 0) goto L_0x010f
            java.lang.String r10 = "finish-idle"
            boolean r10 = r5.destroyActivityLocked(r14, r15, r10)
            r10 = r10 | r13
            r13 = r10
        L_0x010f:
            int r3 = r3 + 1
            goto L_0x00f8
        L_0x0112:
            if (r12 != 0) goto L_0x012b
            if (r9 == 0) goto L_0x012b
            r3 = 0
        L_0x0117:
            int r5 = r9.size()
            if (r3 >= r5) goto L_0x012b
            com.android.server.wm.ActivityTaskManagerService r5 = r6.mService
            android.app.ActivityManagerInternal r5 = r5.mAmInternal
            java.lang.Object r10 = r9.get(r3)
            r5.finishUserSwitch(r10)
            int r3 = r3 + 1
            goto L_0x0117
        L_0x012b:
            com.android.server.wm.ActivityTaskManagerService r3 = r6.mService
            com.android.server.wm.ActivityTaskManagerService$H r3 = r3.mH
            com.android.server.wm.-$$Lambda$ActivityStackSupervisor$28Zuzbi6usdgbDcOi8hrJg6nZO0 r5 = new com.android.server.wm.-$$Lambda$ActivityStackSupervisor$28Zuzbi6usdgbDcOi8hrJg6nZO0
            r5.<init>()
            r3.post(r5)
            if (r13 == 0) goto L_0x013e
            com.android.server.wm.RootActivityContainer r3 = r6.mRootActivityContainer
            r3.resumeFocusedStacksTopActivities()
        L_0x013e:
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStackSupervisor.activityIdleInternalLocked(android.os.IBinder, boolean, boolean, android.content.res.Configuration):com.android.server.wm.ActivityRecord");
    }

    public /* synthetic */ void lambda$activityIdleInternalLocked$0$ActivityStackSupervisor() {
        this.mService.mAmInternal.trimApplications();
    }

    /* access modifiers changed from: package-private */
    public void findTaskToMoveToFront(TaskRecord task, int flags, ActivityOptions options, String reason, boolean forceNonResizeable) {
        Rect bounds;
        TaskRecord taskRecord = task;
        int i = flags;
        ActivityOptions activityOptions = options;
        ActivityStack currentStack = task.getStack();
        ActivityStack focusedStack = this.mRootActivityContainer.getTopDisplayFocusedStack();
        ActivityRecord top_activity = focusedStack != null ? focusedStack.getTopActivity() : null;
        if (top_activity != null && top_activity.getState() == ActivityStack.ActivityState.DESTROYED) {
            acquireAppLaunchPerfLock(top_activity);
        }
        if (currentStack == null) {
            Slog.e("ActivityTaskManager", "findTaskToMoveToFront: can't move task=" + taskRecord + " to front. Stack is null");
            return;
        }
        if ((i & 2) == 0) {
            this.mUserLeaving = true;
        }
        String reason2 = reason + " findTaskToMoveToFront";
        boolean reparented = false;
        if (task.isResizeable() && canUseActivityOptionsLaunchBounds(activityOptions)) {
            Rect bounds2 = options.getLaunchBounds();
            taskRecord.updateOverrideConfiguration(bounds2);
            ActivityStack stack = this.mRootActivityContainer.getLaunchStack((ActivityRecord) null, activityOptions, taskRecord, true);
            if (stack != currentStack) {
                moveHomeStackToFrontIfNeeded(i, stack.getDisplay(), reason2);
                bounds = bounds2;
                task.reparent(stack, true, 1, false, true, reason2);
                currentStack = stack;
                reparented = true;
            } else {
                bounds = bounds2;
            }
            if (stack.resizeStackWithLaunchBounds()) {
                this.mRootActivityContainer.resizeStack(stack, bounds, (Rect) null, (Rect) null, false, true, false);
            } else {
                task.resizeWindowContainer();
            }
        }
        ActivityStack currentStack2 = currentStack;
        if (!reparented && task.getWindowingMode() != 2) {
            moveHomeStackToFrontIfNeeded(i, currentStack2.getDisplay(), reason2);
        }
        ActivityRecord r = task.getTopActivity();
        TaskRecord taskRecord2 = task;
        ActivityRecord activityRecord = top_activity;
        currentStack2.moveTaskToFrontLocked(taskRecord2, false, options, r == null ? null : r.appTimeTracker, reason2);
        handleNonResizableTaskIfNeeded(taskRecord2, 0, 0, currentStack2, forceNonResizeable);
    }

    private void moveHomeStackToFrontIfNeeded(int flags, ActivityDisplay display, String reason) {
        ActivityStack focusedStack = display.getFocusedStack();
        if ((display.getWindowingMode() == 1 && (flags & 1) != 0) || (focusedStack != null && focusedStack.isActivityTypeRecents())) {
            display.moveHomeStackToFront(reason);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean canUseActivityOptionsLaunchBounds(ActivityOptions options) {
        if (options == null || options.getLaunchBounds() == null) {
            return false;
        }
        if ((!this.mService.mSupportsPictureInPicture || options.getLaunchWindowingMode() != 2) && !this.mService.mSupportsFreeformWindowManagement) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public LaunchParamsController getLaunchParamsController() {
        return this.mLaunchParamsController;
    }

    private void deferUpdateRecentsHomeStackBounds() {
        this.mRootActivityContainer.deferUpdateBounds(3);
        this.mRootActivityContainer.deferUpdateBounds(2);
    }

    private void continueUpdateRecentsHomeStackBounds() {
        this.mRootActivityContainer.continueUpdateBounds(3);
        this.mRootActivityContainer.continueUpdateBounds(2);
    }

    /* access modifiers changed from: package-private */
    public void notifyAppTransitionDone() {
        continueUpdateRecentsHomeStackBounds();
        for (int i = this.mResizingTasksDuringAnimation.size() - 1; i >= 0; i--) {
            TaskRecord task = this.mRootActivityContainer.anyTaskForId(this.mResizingTasksDuringAnimation.valueAt(i).intValue(), 0);
            if (task != null) {
                task.setTaskDockedResizing(false);
            }
        }
        this.mResizingTasksDuringAnimation.clear();
    }

    /* access modifiers changed from: private */
    /* renamed from: moveTasksToFullscreenStackInSurfaceTransaction */
    public void lambda$moveTasksToFullscreenStackLocked$1$ActivityStackSupervisor(ActivityStack fromStack, int toDisplayId, boolean onTop) {
        ActivityRecord activityRecord;
        ActivityDisplay toDisplay;
        ArrayList<TaskRecord> tasks;
        int size;
        int i;
        ActivityRecord activityRecord2;
        this.mWindowManager.deferSurfaceLayout();
        try {
            int windowingMode = fromStack.getWindowingMode();
            boolean inPinnedWindowingMode = windowingMode == 2;
            try {
                ActivityDisplay toDisplay2 = this.mRootActivityContainer.getActivityDisplay(toDisplayId);
                ActivityRecord activityRecord3 = null;
                if (windowingMode == 3) {
                    toDisplay2.onExitingSplitScreenMode();
                    for (int i2 = toDisplay2.getChildCount() - 1; i2 >= 0; i2--) {
                        ActivityStack otherStack = toDisplay2.getChildAt(i2);
                        if (otherStack.inSplitScreenSecondaryWindowingMode()) {
                            if (otherStack.getTaskStack() != null && otherStack.getTaskStack().isAdjustedForIme()) {
                                otherStack.getTaskStack().resetAdjustedForIme(true);
                                otherStack.setTaskDisplayedBounds((Rect) null);
                            }
                            otherStack.setWindowingMode(0);
                        }
                    }
                    this.mAllowDockedStackResize = false;
                }
                boolean schedulePictureInPictureModeChange = inPinnedWindowingMode;
                ArrayList<TaskRecord> tasks2 = fromStack.getAllTasks();
                if (!tasks2.isEmpty()) {
                    this.mTmpOptions.setLaunchWindowingMode(1);
                    int size2 = tasks2.size();
                    int i3 = 0;
                    while (i3 < size2) {
                        TaskRecord task = tasks2.get(i3);
                        ActivityStack toStack = toDisplay2.getOrCreateStack((ActivityRecord) null, this.mTmpOptions, task, task.getActivityType(), onTop);
                        if (onTop) {
                            TaskRecord task2 = task;
                            i = i3;
                            size = size2;
                            tasks = tasks2;
                            toDisplay = toDisplay2;
                            task2.reparent(toStack, true, 0, i3 == size2 + -1, true, schedulePictureInPictureModeChange, "moveTasksToFullscreenStack - onTop");
                            MetricsLoggerWrapper.logPictureInPictureFullScreen(this.mService.mContext, task2.effectiveUid, task2.realActivity.flattenToString());
                            activityRecord2 = activityRecord3;
                        } else {
                            i = i3;
                            size = size2;
                            tasks = tasks2;
                            TaskRecord task3 = task;
                            toDisplay = toDisplay2;
                            activityRecord2 = activityRecord3;
                            TaskRecord taskRecord = task3;
                            task3.reparent(toStack, true, 2, false, true, schedulePictureInPictureModeChange, "moveTasksToFullscreenStack - NOT_onTop");
                        }
                        activityRecord3 = activityRecord2;
                        i3 = i + 1;
                        size2 = size;
                        tasks2 = tasks;
                        toDisplay2 = toDisplay;
                    }
                    int i4 = i3;
                    int i5 = size2;
                    ArrayList<TaskRecord> arrayList = tasks2;
                    activityRecord = activityRecord3;
                    ActivityDisplay activityDisplay = toDisplay2;
                } else {
                    activityRecord = null;
                    ActivityDisplay activityDisplay2 = toDisplay2;
                }
                this.mRootActivityContainer.ensureActivitiesVisible(activityRecord, 0, true);
                this.mRootActivityContainer.resumeFocusedStacksTopActivities();
                this.mAllowDockedStackResize = true;
                this.mWindowManager.continueSurfaceLayout();
            } catch (Throwable th) {
                th = th;
                this.mAllowDockedStackResize = true;
                this.mWindowManager.continueSurfaceLayout();
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            int i6 = toDisplayId;
            this.mAllowDockedStackResize = true;
            this.mWindowManager.continueSurfaceLayout();
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public void moveTasksToFullscreenStackLocked(ActivityStack fromStack, boolean onTop) {
        moveTasksToFullscreenStackLocked(fromStack, 0, onTop);
    }

    /* access modifiers changed from: package-private */
    public void moveTasksToFullscreenStackLocked(ActivityStack fromStack, int toDisplayId, boolean onTop) {
        this.mWindowManager.inSurfaceTransaction(new Runnable(fromStack, toDisplayId, onTop) {
            private final /* synthetic */ ActivityStack f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ActivityStackSupervisor.this.lambda$moveTasksToFullscreenStackLocked$1$ActivityStackSupervisor(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void setSplitScreenResizing(boolean resizing) {
        if (resizing != this.mDockedStackResizing) {
            this.mDockedStackResizing = resizing;
            this.mWindowManager.setDockedStackResizing(resizing);
            if (!resizing && this.mHasPendingDockedBounds) {
                resizeDockedStackLocked(this.mPendingDockedBounds, this.mPendingTempDockedTaskBounds, this.mPendingTempDockedTaskInsetBounds, this.mPendingTempOtherTaskBounds, this.mPendingTempOtherTaskInsetBounds, true);
                this.mHasPendingDockedBounds = false;
                this.mPendingDockedBounds = null;
                this.mPendingTempDockedTaskBounds = null;
                this.mPendingTempDockedTaskInsetBounds = null;
                this.mPendingTempOtherTaskBounds = null;
                this.mPendingTempOtherTaskInsetBounds = null;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void resizeDockedStackLocked(Rect dockedBounds, Rect tempDockedTaskBounds, Rect tempDockedTaskInsetBounds, Rect tempOtherTaskBounds, Rect tempOtherTaskInsetBounds, boolean preserveWindows) {
        resizeDockedStackLocked(dockedBounds, tempDockedTaskBounds, tempDockedTaskInsetBounds, tempOtherTaskBounds, tempOtherTaskInsetBounds, preserveWindows, false);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00de  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void resizeDockedStackLocked(android.graphics.Rect r22, android.graphics.Rect r23, android.graphics.Rect r24, android.graphics.Rect r25, android.graphics.Rect r26, boolean r27, boolean r28) {
        /*
            r21 = this;
            r1 = r21
            r2 = r22
            boolean r0 = r1.mAllowDockedStackResize
            if (r0 != 0) goto L_0x0009
            return
        L_0x0009:
            com.android.server.wm.RootActivityContainer r0 = r1.mRootActivityContainer
            com.android.server.wm.ActivityDisplay r0 = r0.getDefaultDisplay()
            com.android.server.wm.ActivityStack r3 = r0.getSplitScreenPrimaryStack()
            if (r3 != 0) goto L_0x001d
            java.lang.String r0 = "ActivityTaskManager"
            java.lang.String r4 = "resizeDockedStackLocked: docked stack not found"
            android.util.Slog.w(r0, r4)
            return
        L_0x001d:
            boolean r0 = r1.mDockedStackResizing
            r4 = 1
            if (r0 == 0) goto L_0x0042
            r1.mHasPendingDockedBounds = r4
            android.graphics.Rect r0 = android.graphics.Rect.copyOrNull(r22)
            r1.mPendingDockedBounds = r0
            android.graphics.Rect r0 = android.graphics.Rect.copyOrNull(r23)
            r1.mPendingTempDockedTaskBounds = r0
            android.graphics.Rect r0 = android.graphics.Rect.copyOrNull(r24)
            r1.mPendingTempDockedTaskInsetBounds = r0
            android.graphics.Rect r0 = android.graphics.Rect.copyOrNull(r25)
            r1.mPendingTempOtherTaskBounds = r0
            android.graphics.Rect r0 = android.graphics.Rect.copyOrNull(r26)
            r1.mPendingTempOtherTaskInsetBounds = r0
        L_0x0042:
            r5 = 64
            java.lang.String r0 = "am.resizeDockedStack"
            android.os.Trace.traceBegin(r5, r0)
            com.android.server.wm.WindowManagerService r0 = r1.mWindowManager
            r0.deferSurfaceLayout()
            r0 = 0
            r1.mAllowDockedStackResize = r0     // Catch:{ all -> 0x00ee }
            com.android.server.wm.ActivityRecord r0 = r3.topRunningActivityLocked()     // Catch:{ all -> 0x00ee }
            r7 = r23
            r8 = r24
            r3.resize(r2, r7, r8)     // Catch:{ all -> 0x00ec }
            int r9 = r3.getWindowingMode()     // Catch:{ all -> 0x00ec }
            if (r9 == r4) goto L_0x00d0
            if (r2 != 0) goto L_0x006c
            boolean r9 = r3.isAttached()     // Catch:{ all -> 0x00ec }
            if (r9 != 0) goto L_0x006c
            goto L_0x00d0
        L_0x006c:
            com.android.server.wm.RootActivityContainer r9 = r1.mRootActivityContainer     // Catch:{ all -> 0x00ec }
            com.android.server.wm.ActivityDisplay r9 = r9.getDefaultDisplay()     // Catch:{ all -> 0x00ec }
            android.graphics.Rect r10 = new android.graphics.Rect     // Catch:{ all -> 0x00ec }
            r10.<init>()     // Catch:{ all -> 0x00ec }
            int r11 = r9.getChildCount()     // Catch:{ all -> 0x00ec }
            int r11 = r11 - r4
        L_0x007c:
            if (r11 < 0) goto L_0x00d4
            com.android.server.wm.ActivityStack r12 = r9.getChildAt((int) r11)     // Catch:{ all -> 0x00ec }
            boolean r13 = r12.inSplitScreenSecondaryWindowingMode()     // Catch:{ all -> 0x00ec }
            if (r13 != 0) goto L_0x0089
            goto L_0x00cd
        L_0x0089:
            boolean r13 = r12.affectedBySplitScreenResize()     // Catch:{ all -> 0x00ec }
            if (r13 != 0) goto L_0x0090
            goto L_0x00cd
        L_0x0090:
            boolean r13 = r1.mDockedStackResizing     // Catch:{ all -> 0x00ec }
            if (r13 == 0) goto L_0x009b
            boolean r13 = r12.isTopActivityVisible()     // Catch:{ all -> 0x00ec }
            if (r13 != 0) goto L_0x009b
            goto L_0x00cd
        L_0x009b:
            android.graphics.Rect r13 = r1.tempRect     // Catch:{ all -> 0x00ec }
            r15 = r25
            r12.getStackDockedModeBounds(r2, r15, r13, r10)     // Catch:{ all -> 0x00ec }
            com.android.server.wm.RootActivityContainer r13 = r1.mRootActivityContainer     // Catch:{ all -> 0x00ec }
            android.graphics.Rect r14 = r1.tempRect     // Catch:{ all -> 0x00ec }
            boolean r14 = r14.isEmpty()     // Catch:{ all -> 0x00ec }
            if (r14 != 0) goto L_0x00af
            android.graphics.Rect r14 = r1.tempRect     // Catch:{ all -> 0x00ec }
            goto L_0x00b0
        L_0x00af:
            r14 = 0
        L_0x00b0:
            r16 = r14
            boolean r14 = r10.isEmpty()     // Catch:{ all -> 0x00ec }
            if (r14 != 0) goto L_0x00bb
            r17 = r10
            goto L_0x00bd
        L_0x00bb:
            r17 = r15
        L_0x00bd:
            r19 = 1
            r14 = r12
            r15 = r16
            r16 = r17
            r17 = r26
            r18 = r27
            r20 = r28
            r13.resizeStack(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x00ec }
        L_0x00cd:
            int r11 = r11 + -1
            goto L_0x007c
        L_0x00d0:
            r1.moveTasksToFullscreenStackLocked(r3, r4)     // Catch:{ all -> 0x00ec }
            r0 = 0
        L_0x00d4:
            if (r28 != 0) goto L_0x00de
            r9 = r27
            r3.ensureVisibleActivitiesConfigurationLocked(r0, r9)     // Catch:{ all -> 0x00dc }
            goto L_0x00e0
        L_0x00dc:
            r0 = move-exception
            goto L_0x00f5
        L_0x00de:
            r9 = r27
        L_0x00e0:
            r1.mAllowDockedStackResize = r4
            com.android.server.wm.WindowManagerService r0 = r1.mWindowManager
            r0.continueSurfaceLayout()
            android.os.Trace.traceEnd(r5)
            return
        L_0x00ec:
            r0 = move-exception
            goto L_0x00f3
        L_0x00ee:
            r0 = move-exception
            r7 = r23
            r8 = r24
        L_0x00f3:
            r9 = r27
        L_0x00f5:
            r1.mAllowDockedStackResize = r4
            com.android.server.wm.WindowManagerService r4 = r1.mWindowManager
            r4.continueSurfaceLayout()
            android.os.Trace.traceEnd(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStackSupervisor.resizeDockedStackLocked(android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, boolean, boolean):void");
    }

    /* access modifiers changed from: package-private */
    public void resizePinnedStackLocked(Rect pinnedBounds, Rect tempPinnedTaskBounds) {
        ActivityStack stack = this.mRootActivityContainer.getDefaultDisplay().getPinnedStack();
        if (stack == null) {
            Slog.w("ActivityTaskManager", "resizePinnedStackLocked: pinned stack not found");
        } else if (!stack.getTaskStack().pinnedStackResizeDisallowed()) {
            Trace.traceBegin(64, "am.resizePinnedStack");
            this.mWindowManager.deferSurfaceLayout();
            try {
                ActivityRecord r = stack.topRunningActivityLocked();
                Rect insetBounds = null;
                if (tempPinnedTaskBounds != null && stack.isAnimatingBoundsToFullscreen()) {
                    insetBounds = this.tempRect;
                    insetBounds.top = 0;
                    insetBounds.left = 0;
                    insetBounds.right = tempPinnedTaskBounds.width();
                    insetBounds.bottom = tempPinnedTaskBounds.height();
                }
                if (pinnedBounds != null && tempPinnedTaskBounds == null) {
                    stack.onPipAnimationEndResize();
                }
                stack.resize(pinnedBounds, tempPinnedTaskBounds, insetBounds);
                stack.ensureVisibleActivitiesConfigurationLocked(r, false);
            } finally {
                this.mWindowManager.continueSurfaceLayout();
                Trace.traceEnd(64);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: removeStackInSurfaceTransaction */
    public void lambda$removeStack$2$ActivityStackSupervisor(ActivityStack stack) {
        ArrayList<TaskRecord> tasks = stack.getAllTasks();
        if (stack.getWindowingMode() == 2) {
            stack.mForceHidden = true;
            stack.ensureActivitiesVisibleLocked((ActivityRecord) null, 0, true);
            stack.mForceHidden = false;
            activityIdleInternalLocked((IBinder) null, false, true, (Configuration) null);
            moveTasksToFullscreenStackLocked(stack, false);
            return;
        }
        for (int i = tasks.size() - 1; i >= 0; i--) {
            removeTaskByIdLocked(tasks.get(i).taskId, true, true, "remove-stack");
        }
    }

    /* access modifiers changed from: package-private */
    public void removeStack(ActivityStack stack) {
        this.mWindowManager.inSurfaceTransaction(new Runnable(stack) {
            private final /* synthetic */ ActivityStack f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ActivityStackSupervisor.this.lambda$removeStack$2$ActivityStackSupervisor(this.f$1);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public boolean removeTaskByIdLocked(int taskId, boolean killProcess, boolean removeFromRecents, String reason) {
        return removeTaskByIdLocked(taskId, killProcess, removeFromRecents, false, reason);
    }

    /* access modifiers changed from: package-private */
    public boolean removeTaskByIdLocked(int taskId, boolean killProcess, boolean removeFromRecents, boolean pauseImmediately, String reason) {
        TaskRecord tr = this.mRootActivityContainer.anyTaskForId(taskId, 1);
        if (tr != null) {
            tr.removeTaskActivitiesLocked(pauseImmediately, reason);
            cleanUpRemovedTaskLocked(tr, killProcess, removeFromRecents);
            this.mService.getLockTaskController().clearLockedTask(tr);
            if (tr.isPersistable) {
                this.mService.notifyTaskPersisterLocked((TaskRecord) null, true);
            }
            return true;
        }
        Slog.w("ActivityTaskManager", "Request to remove task ignored for non-existent task " + taskId);
        return false;
    }

    /* access modifiers changed from: package-private */
    public void cleanUpRemovedTaskLocked(TaskRecord tr, boolean killProcess, boolean removeFromRecents) {
        if (removeFromRecents) {
            this.mRecentTasks.remove(tr);
        }
        ComponentName component = tr.getBaseIntent().getComponent();
        if (component == null) {
            Slog.w("ActivityTaskManager", "No component for base intent of task: " + tr);
            return;
        }
        this.mService.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$z5j5fiv3cZuY5AODkt3H3rhKimk.INSTANCE, this.mService.mAmInternal, Integer.valueOf(tr.userId), component, new Intent(tr.getBaseIntent())));
        if (killProcess) {
            String pkg = component.getPackageName();
            ArrayList<Object> procsToKill = new ArrayList<>();
            ArrayMap<String, SparseArray<WindowProcessController>> pmap = this.mService.mProcessNames.getMap();
            for (int i = 0; i < pmap.size(); i++) {
                SparseArray<WindowProcessController> uids = pmap.valueAt(i);
                for (int j = 0; j < uids.size(); j++) {
                    WindowProcessController proc = uids.valueAt(j);
                    if (proc.mUserId == tr.userId && proc != this.mService.mHomeProcess && proc.mPkgList.contains(pkg)) {
                        if (proc.shouldKillProcessForRemovedTask(tr) && !proc.hasForegroundServices()) {
                            procsToKill.add(proc);
                        } else {
                            return;
                        }
                    }
                }
            }
            this.mService.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$j9nJq2XXOKyN4f0dfDaTjqmQRvg.INSTANCE, this.mService.mAmInternal, procsToKill));
            if (removeFromRecents) {
                try {
                    new PreferredAppsTask().execute(new Void[0]);
                } catch (Exception e) {
                    Slog.v("ActivityTaskManager", "Exception: " + e);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean restoreRecentTaskLocked(TaskRecord task, ActivityOptions aOptions, boolean onTop) {
        ActivityStack stack = this.mRootActivityContainer.getLaunchStack((ActivityRecord) null, aOptions, task, onTop);
        ActivityStack currentStack = task.getStack();
        if (currentStack != null) {
            if (currentStack == stack) {
                return true;
            }
            currentStack.removeTask(task, "restoreRecentTaskLocked", 1);
        }
        stack.addTask(task, onTop, "restoreRecentTask");
        task.createTask(onTop, true);
        ArrayList<ActivityRecord> activities = task.mActivities;
        for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
            activities.get(activityNdx).createAppWindowToken();
        }
        return true;
    }

    public void onRecentTaskAdded(TaskRecord task) {
        task.touchActiveTime();
    }

    public void onRecentTaskRemoved(TaskRecord task, boolean wasTrimmed, boolean killProcess) {
        if (wasTrimmed) {
            removeTaskByIdLocked(task.taskId, killProcess, false, false, "recent-task-trimmed");
        }
        task.removedFromRecents();
    }

    /* access modifiers changed from: package-private */
    public ActivityStack getReparentTargetStack(TaskRecord task, ActivityStack stack, boolean toTop) {
        ActivityStack prevStack = task.getStack();
        int stackId = stack.mStackId;
        boolean inMultiWindowMode = stack.inMultiWindowMode();
        if (prevStack != null && prevStack.mStackId == stackId) {
            Slog.w("ActivityTaskManager", "Can not reparent to same stack, task=" + task + " already in stackId=" + stackId);
            return prevStack;
        } else if (inMultiWindowMode && !this.mService.mSupportsMultiWindow) {
            throw new IllegalArgumentException("Device doesn't support multi-window, can not reparent task=" + task + " to stack=" + stack);
        } else if (stack.mDisplayId != 0 && !this.mService.mSupportsMultiDisplay) {
            throw new IllegalArgumentException("Device doesn't support multi-display, can not reparent task=" + task + " to stackId=" + stackId);
        } else if (stack.getWindowingMode() == 5 && !this.mService.mSupportsFreeformWindowManagement) {
            throw new IllegalArgumentException("Device doesn't support freeform, can not reparent task=" + task);
        } else if (!inMultiWindowMode || task.isResizeable()) {
            return stack;
        } else {
            Slog.w("ActivityTaskManager", "Can not move unresizeable task=" + task + " to multi-window stack=" + stack + " Moving to a fullscreen stack instead.");
            if (prevStack != null) {
                return prevStack;
            }
            return stack.getDisplay().createStack(1, stack.getActivityType(), toTop);
        }
    }

    /* access modifiers changed from: package-private */
    public void goingToSleepLocked() {
        scheduleSleepTimeout();
        if (!this.mGoingToSleepWakeLock.isHeld()) {
            this.mGoingToSleepWakeLock.acquire();
            if (this.mLaunchingActivityWakeLock.isHeld()) {
                this.mLaunchingActivityWakeLock.release();
                this.mHandler.removeMessages(LAUNCH_TIMEOUT_MSG);
            }
        }
        this.mRootActivityContainer.applySleepTokens(false);
        checkReadyForSleepLocked(true);
    }

    /* access modifiers changed from: package-private */
    public boolean shutdownLocked(int timeout) {
        goingToSleepLocked();
        boolean timedout = false;
        long endTime = System.currentTimeMillis() + ((long) timeout);
        while (true) {
            if (this.mRootActivityContainer.putStacksToSleep(true, true)) {
                break;
            }
            long timeRemaining = endTime - System.currentTimeMillis();
            if (timeRemaining <= 0) {
                Slog.w("ActivityTaskManager", "Activity manager shutdown timed out");
                timedout = true;
                break;
            }
            try {
                this.mService.mGlobalLock.wait(timeRemaining);
            } catch (InterruptedException e) {
            }
        }
        checkReadyForSleepLocked(false);
        return timedout;
    }

    /* access modifiers changed from: package-private */
    public void acquireAppLaunchPerfLock(ActivityRecord r) {
        BoostFramework boostFramework = this.mPerfBoost;
        if (boostFramework != null) {
            boostFramework.perfHint(4225, r.packageName, -1, 1);
            mPerfSendTapHint = true;
            this.mPerfBoost.perfHint(4225, r.packageName, -1, 2);
            if (this.mPerfBoost.perfGetFeedback(5633, r.packageName) == 2) {
                mPerfHandle = this.mPerfBoost.perfHint(4225, r.packageName, -1, 4);
            } else {
                mPerfHandle = this.mPerfBoost.perfHint(4225, r.packageName, -1, 3);
            }
            if (mPerfHandle > 0) {
                mIsPerfBoostAcquired = true;
            }
            if (r.appInfo != null && r.appInfo.sourceDir != null) {
                this.mPerfBoost.perfIOPrefetchStart(-1, r.packageName, r.appInfo.sourceDir.substring(0, r.appInfo.sourceDir.lastIndexOf(47)));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void comeOutOfSleepIfNeededLocked() {
        removeSleepTimeouts();
        if (this.mGoingToSleepWakeLock.isHeld()) {
            this.mGoingToSleepWakeLock.release();
        }
    }

    /* access modifiers changed from: package-private */
    public void activitySleptLocked(ActivityRecord r) {
        this.mGoingToSleepActivities.remove(r);
        ActivityStack s = r.getActivityStack();
        if (s != null) {
            s.checkReadyForSleep();
        } else {
            checkReadyForSleepLocked(true);
        }
    }

    /* access modifiers changed from: package-private */
    public void checkReadyForSleepLocked(boolean allowDelay) {
        if (this.mService.isSleepingOrShuttingDownLocked() && this.mRootActivityContainer.putStacksToSleep(allowDelay, false)) {
            this.mRootActivityContainer.sendPowerHintForLaunchEndIfNeeded();
            removeSleepTimeouts();
            if (this.mGoingToSleepWakeLock.isHeld()) {
                this.mGoingToSleepWakeLock.release();
            }
            if (this.mService.mShuttingDown) {
                this.mService.mGlobalLock.notifyAll();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean reportResumedActivityLocked(ActivityRecord r) {
        this.mStoppingActivities.remove(r);
        ActivityStackSupervisorInjector.updateScreenPaperMode(r.packageName);
        if (!r.getActivityStack().getDisplay().allResumedActivitiesComplete()) {
            return false;
        }
        this.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
        this.mRootActivityContainer.executeAppTransitionForAllDisplay();
        return true;
    }

    /* access modifiers changed from: private */
    public void handleLaunchTaskBehindCompleteLocked(ActivityRecord r) {
        TaskRecord task = r.getTaskRecord();
        ActivityStack stack = task.getStack();
        r.mLaunchTaskBehind = false;
        this.mRecentTasks.add(task);
        this.mService.getTaskChangeNotificationController().notifyTaskStackChanged();
        if (!this.mService.mGestureController.mLaunchRecentsFromGesture) {
            r.setVisibility(false);
        }
        ActivityRecord top = stack.getTopActivity();
        if (top != null) {
            top.getTaskRecord().touchActiveTime();
        }
    }

    /* access modifiers changed from: package-private */
    public void scheduleLaunchTaskBehindComplete(IBinder token) {
        this.mHandler.obtainMessage(212, token).sendToTarget();
    }

    /* access modifiers changed from: package-private */
    public boolean isCurrentProfileLocked(int userId) {
        if (userId == this.mRootActivityContainer.mCurrentUser) {
            return true;
        }
        return this.mService.mAmInternal.isCurrentProfile(userId);
    }

    /* access modifiers changed from: package-private */
    public boolean isStoppingNoHistoryActivity() {
        Iterator<ActivityRecord> it = this.mStoppingActivities.iterator();
        while (it.hasNext()) {
            if (it.next().isNoHistory()) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public final ArrayList<ActivityRecord> processStoppingActivitiesLocked(ActivityRecord idleActivity, boolean remove, boolean processPausingActivities) {
        boolean shouldSleepOrShutDown;
        ArrayList<ActivityRecord> stops = null;
        boolean nowVisible = this.mRootActivityContainer.allResumedActivitiesVisible();
        for (int activityNdx = this.mStoppingActivities.size() - 1; activityNdx >= 0; activityNdx--) {
            ActivityRecord s = this.mStoppingActivities.get(activityNdx);
            boolean animating = s.mAppWindowToken.isSelfAnimating();
            if (nowVisible && s.finishing) {
                s.setVisibility(false);
            }
            if (remove) {
                ActivityStack stack = s.getActivityStack();
                if (stack != null) {
                    shouldSleepOrShutDown = stack.shouldSleepOrShutDownActivities();
                } else {
                    shouldSleepOrShutDown = this.mService.isSleepingOrShuttingDownLocked();
                }
                if (!animating || shouldSleepOrShutDown) {
                    if (processPausingActivities || !s.isState(ActivityStack.ActivityState.PAUSING)) {
                        if (stops == null) {
                            stops = new ArrayList<>();
                        }
                        stops.add(s);
                        this.mStoppingActivities.remove(activityNdx);
                    } else {
                        removeTimeoutsForActivityLocked(idleActivity);
                        scheduleIdleTimeoutLocked(idleActivity);
                    }
                }
            }
        }
        return stops;
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.println();
        pw.println("ActivityStackSupervisor state:");
        this.mRootActivityContainer.dump(pw, prefix);
        pw.print(prefix);
        pw.println("mCurTaskIdForUser=" + this.mCurTaskIdForUser);
        pw.println(prefix + "mUserStackInFront=" + this.mRootActivityContainer.mUserStackInFront);
        if (!this.mWaitingForActivityVisible.isEmpty()) {
            pw.println(prefix + "mWaitingForActivityVisible=");
            for (int i = 0; i < this.mWaitingForActivityVisible.size(); i++) {
                pw.print(prefix + prefix);
                this.mWaitingForActivityVisible.get(i).dump(pw, prefix);
            }
        }
        pw.print(prefix);
        pw.print("isHomeRecentsComponent=");
        pw.print(this.mRecentTasks.isRecentsComponentHomeActivity(this.mRootActivityContainer.mCurrentUser));
        getKeyguardController().dump(pw, prefix);
        this.mService.getLockTaskController().dump(pw, prefix);
    }

    static boolean printThisActivity(PrintWriter pw, ActivityRecord activity, String dumpPackage, boolean needSep, String prefix) {
        if (activity == null) {
            return false;
        }
        if (dumpPackage != null && !dumpPackage.equals(activity.packageName)) {
            return false;
        }
        if (needSep) {
            pw.println();
        }
        pw.print(prefix);
        pw.println(activity);
        return true;
    }

    static boolean dumpHistoryList(FileDescriptor fd, PrintWriter pw, List<ActivityRecord> list, String prefix, String label, boolean complete, boolean brief, boolean client, String dumpPackage, boolean needNL, String header, TaskRecord lastTask) {
        String header2;
        TaskRecord lastTask2;
        TaskRecord lastTask3;
        PrintWriter printWriter = pw;
        String str = prefix;
        String str2 = dumpPackage;
        boolean printed = false;
        boolean z = true;
        int i = list.size() - 1;
        boolean needNL2 = needNL;
        String innerPrefix = null;
        String[] args = null;
        String header3 = header;
        TaskRecord lastTask4 = lastTask;
        while (i >= 0) {
            ActivityRecord r = list.get(i);
            if (str2 == null || str2.equals(r.packageName)) {
                boolean full = false;
                if (innerPrefix == null) {
                    innerPrefix = str + "      ";
                    args = new String[0];
                }
                printed = true;
                if (!brief && (complete || !r.isInHistory())) {
                    full = z;
                }
                if (needNL2) {
                    printWriter.println("");
                    needNL2 = false;
                }
                if (header3 != null) {
                    printWriter.println(header3);
                    header2 = null;
                } else {
                    header2 = header3;
                }
                if (lastTask4 != r.getTaskRecord()) {
                    lastTask4 = r.getTaskRecord();
                    printWriter.print(str);
                    printWriter.print(full ? "* " : "  ");
                    printWriter.println(lastTask4);
                    if (full) {
                        lastTask4.dump(printWriter, str + "  ");
                    } else if (complete && lastTask4.intent != null) {
                        printWriter.print(str);
                        printWriter.print("  ");
                        printWriter.println(lastTask4.intent.toInsecureStringWithClip());
                    }
                }
                printWriter.print(str);
                printWriter.print(full ? "  * " : "    ");
                printWriter.print(label);
                printWriter.print(" #");
                printWriter.print(i);
                printWriter.print(": ");
                printWriter.println(r);
                if (full) {
                    r.dump(printWriter, innerPrefix);
                } else if (complete) {
                    printWriter.print(innerPrefix);
                    printWriter.println(r.intent.toInsecureString());
                    if (r.app != null) {
                        printWriter.print(innerPrefix);
                        printWriter.println(r.app);
                    }
                }
                if (!client || !r.attachedToProcess()) {
                    FileDescriptor fileDescriptor = fd;
                    lastTask4 = lastTask4;
                    header3 = header2;
                } else {
                    pw.flush();
                    try {
                        lastTask2 = new TransferPipe();
                        try {
                            r.app.getThread().dumpActivity(lastTask2.getWriteFd(), r.appToken, innerPrefix, args);
                            TaskRecord taskRecord = lastTask4;
                            lastTask3 = lastTask2;
                            lastTask2 = taskRecord;
                            try {
                                lastTask3.go(fd, 2000);
                                lastTask4 = lastTask2;
                                needNL2 = true;
                                header3 = header2;
                            } catch (Throwable th) {
                                th = th;
                                lastTask3.kill();
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            FileDescriptor fileDescriptor2 = fd;
                            TaskRecord taskRecord2 = lastTask4;
                            lastTask3 = lastTask2;
                            TaskRecord taskRecord3 = taskRecord2;
                            lastTask3.kill();
                            throw th;
                        }
                        try {
                            lastTask3.kill();
                        } catch (IOException e) {
                            e = e;
                            printWriter.println(innerPrefix + "Failure while dumping the activity: " + e);
                            lastTask4 = lastTask2;
                            needNL2 = true;
                            header3 = header2;
                            i--;
                            str = prefix;
                            str2 = dumpPackage;
                            z = true;
                        } catch (RemoteException e2) {
                            printWriter.println(innerPrefix + "Got a RemoteException while dumping the activity");
                            lastTask4 = lastTask2;
                            needNL2 = true;
                            header3 = header2;
                            i--;
                            str = prefix;
                            str2 = dumpPackage;
                            z = true;
                        }
                    } catch (IOException e3) {
                        e = e3;
                        FileDescriptor fileDescriptor3 = fd;
                        lastTask2 = lastTask4;
                        printWriter.println(innerPrefix + "Failure while dumping the activity: " + e);
                        lastTask4 = lastTask2;
                        needNL2 = true;
                        header3 = header2;
                        i--;
                        str = prefix;
                        str2 = dumpPackage;
                        z = true;
                    } catch (RemoteException e4) {
                        FileDescriptor fileDescriptor4 = fd;
                        lastTask2 = lastTask4;
                        printWriter.println(innerPrefix + "Got a RemoteException while dumping the activity");
                        lastTask4 = lastTask2;
                        needNL2 = true;
                        header3 = header2;
                        i--;
                        str = prefix;
                        str2 = dumpPackage;
                        z = true;
                    }
                }
            } else {
                FileDescriptor fileDescriptor5 = fd;
                String str3 = label;
            }
            i--;
            str = prefix;
            str2 = dumpPackage;
            z = true;
        }
        FileDescriptor fileDescriptor6 = fd;
        List<ActivityRecord> list2 = list;
        String str4 = label;
        return printed;
    }

    /* access modifiers changed from: package-private */
    public void scheduleIdleTimeoutLocked(ActivityRecord next) {
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(200, next), JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
    }

    /* access modifiers changed from: package-private */
    public final void scheduleIdleLocked() {
        this.mHandler.sendEmptyMessage(201);
    }

    /* access modifiers changed from: package-private */
    public void updateTopResumedActivityIfNeeded() {
        ActivityRecord prevTopActivity = this.mTopResumedActivity;
        ActivityStack topStack = this.mRootActivityContainer.getTopDisplayFocusedStack();
        if (topStack != null && topStack.mResumedActivity != prevTopActivity) {
            if ((prevTopActivity != null && !this.mTopResumedActivityWaitingForPrev) && prevTopActivity.scheduleTopResumedActivityChanged(false)) {
                scheduleTopResumedStateLossTimeout(prevTopActivity);
                this.mTopResumedActivityWaitingForPrev = true;
            }
            this.mTopResumedActivity = topStack.mResumedActivity;
            scheduleTopResumedActivityStateIfNeeded();
        }
    }

    private void scheduleTopResumedActivityStateIfNeeded() {
        ActivityRecord activityRecord = this.mTopResumedActivity;
        if (activityRecord != null && !this.mTopResumedActivityWaitingForPrev) {
            activityRecord.scheduleTopResumedActivityChanged(true);
        }
    }

    private void scheduleTopResumedStateLossTimeout(ActivityRecord r) {
        Message msg = this.mHandler.obtainMessage(TOP_RESUMED_STATE_LOSS_TIMEOUT_MSG);
        msg.obj = r;
        r.topResumedStateLossTime = SystemClock.uptimeMillis();
        this.mHandler.sendMessageDelayed(msg, 500);
    }

    /* access modifiers changed from: package-private */
    public void handleTopResumedStateReleased(boolean timeout) {
        this.mHandler.removeMessages(TOP_RESUMED_STATE_LOSS_TIMEOUT_MSG);
        if (this.mTopResumedActivityWaitingForPrev) {
            this.mTopResumedActivityWaitingForPrev = false;
            scheduleTopResumedActivityStateIfNeeded();
        }
    }

    /* access modifiers changed from: package-private */
    public void removeTimeoutsForActivityLocked(ActivityRecord r) {
        this.mHandler.removeMessages(200, r);
    }

    /* access modifiers changed from: package-private */
    public final void scheduleResumeTopActivities() {
        if (!this.mHandler.hasMessages(202)) {
            this.mHandler.sendEmptyMessage(202);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeSleepTimeouts() {
        this.mHandler.removeMessages(203);
    }

    /* access modifiers changed from: package-private */
    public final void scheduleSleepTimeout() {
        removeSleepTimeouts();
        this.mHandler.sendEmptyMessageDelayed(203, 5000);
    }

    /* access modifiers changed from: package-private */
    public void removeRestartTimeouts(ActivityRecord r) {
        this.mHandler.removeMessages(RESTART_ACTIVITY_PROCESS_TIMEOUT_MSG, r);
    }

    /* access modifiers changed from: package-private */
    public final void scheduleRestartTimeout(ActivityRecord r) {
        removeRestartTimeouts(r);
        ActivityStackSupervisorHandler activityStackSupervisorHandler = this.mHandler;
        activityStackSupervisorHandler.sendMessageDelayed(activityStackSupervisorHandler.obtainMessage(RESTART_ACTIVITY_PROCESS_TIMEOUT_MSG, r), 2000);
    }

    /* access modifiers changed from: package-private */
    public void handleNonResizableTaskIfNeeded(TaskRecord task, int preferredWindowingMode, int preferredDisplayId, ActivityStack actualStack) {
        handleNonResizableTaskIfNeeded(task, preferredWindowingMode, preferredDisplayId, actualStack, false);
    }

    /* access modifiers changed from: package-private */
    public void handleNonResizableTaskIfNeeded(TaskRecord task, int preferredWindowingMode, int preferredDisplayId, ActivityStack actualStack, boolean forceNonResizable) {
        boolean singleTaskInstance = false;
        boolean isSecondaryDisplayPreferred = (preferredDisplayId == 0 || preferredDisplayId == -1) ? false : true;
        if ((!(actualStack != null && actualStack.getDisplay().hasSplitScreenPrimaryStack()) && preferredWindowingMode != 3 && !isSecondaryDisplayPreferred) || !task.isActivityTypeStandardOrUndefined()) {
            return;
        }
        if (isSecondaryDisplayPreferred) {
            int actualDisplayId = task.getStack().mDisplayId;
            if (task.canBeLaunchedOnDisplay(actualDisplayId)) {
                ActivityDisplay preferredDisplay = this.mRootActivityContainer.getActivityDisplay(preferredDisplayId);
                if (preferredDisplay != null && preferredDisplay.isSingleTaskInstance()) {
                    singleTaskInstance = true;
                }
                if (preferredDisplayId != actualDisplayId) {
                    if (singleTaskInstance) {
                        this.mService.getTaskChangeNotificationController().notifyActivityLaunchOnSecondaryDisplayRerouted(task.getTaskInfo(), preferredDisplayId);
                        return;
                    }
                    Slog.w("ActivityTaskManager", "Failed to put " + task + " on display " + preferredDisplayId);
                    this.mService.getTaskChangeNotificationController().notifyActivityLaunchOnSecondaryDisplayFailed(task.getTaskInfo(), preferredDisplayId);
                } else if (!forceNonResizable) {
                    handleForcedResizableTaskIfNeeded(task, 2);
                }
            } else {
                throw new IllegalStateException("Task resolved to incompatible display");
            }
        } else if (task.supportsSplitScreenWindowingMode() == 0 || forceNonResizable) {
            ActivityStack dockedStack = task.getStack().getDisplay().getSplitScreenPrimaryStack();
            if (dockedStack != null) {
                this.mService.getTaskChangeNotificationController().notifyActivityDismissingDockedStack();
                if (actualStack == dockedStack) {
                    singleTaskInstance = true;
                }
                moveTasksToFullscreenStackLocked(dockedStack, singleTaskInstance);
            }
        } else {
            handleForcedResizableTaskIfNeeded(task, 1);
        }
    }

    private void handleForcedResizableTaskIfNeeded(TaskRecord task, int reason) {
        ActivityRecord topActivity = task.getTopActivity();
        if (topActivity != null && !topActivity.noDisplay && topActivity.isNonResizableOrForcedResizable()) {
            this.mService.getTaskChangeNotificationController().notifyActivityForcedResizable(task.taskId, reason, topActivity.appInfo.packageName);
        }
    }

    /* access modifiers changed from: package-private */
    public void activityRelaunchedLocked(IBinder token) {
        this.mWindowManager.notifyAppRelaunchingFinished(token);
        ActivityRecord r = ActivityRecord.isInStackLocked(token);
        if (r != null && r.getActivityStack().shouldSleepOrShutDownActivities()) {
            r.setSleeping(true, true);
        }
    }

    /* access modifiers changed from: package-private */
    public void activityRelaunchingLocked(ActivityRecord r) {
        this.mWindowManager.notifyAppRelaunching(r.appToken);
    }

    /* access modifiers changed from: package-private */
    public void logStackState() {
        this.mActivityMetricsLogger.logWindowState();
    }

    /* access modifiers changed from: package-private */
    public void scheduleUpdateMultiWindowMode(TaskRecord task) {
        if (!task.getStack().deferScheduleMultiWindowModeChanged()) {
            String pkgName = null;
            for (int i = task.mActivities.size() - 1; i >= 0; i--) {
                ActivityRecord r = task.mActivities.get(i);
                if (r.attachedToProcess()) {
                    if (pkgName == null) {
                        pkgName = r.packageName;
                    }
                    this.mMultiWindowModeChangedActivities.add(r);
                }
            }
            Slog.d("ActivityTaskManager", "window mode changed:" + pkgName + ", " + task.userId + ", " + (task.inMultiWindowMode() ? 1 : 0));
            if (!this.mHandler.hasMessages(REPORT_MULTI_WINDOW_MODE_CHANGED_MSG)) {
                this.mHandler.sendEmptyMessage(REPORT_MULTI_WINDOW_MODE_CHANGED_MSG);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void scheduleUpdatePictureInPictureModeIfNeeded(TaskRecord task, ActivityStack prevStack) {
        ActivityStack stack = task.getStack();
        if (prevStack != null && prevStack != stack) {
            if (prevStack.inPinnedWindowingMode() || stack.inPinnedWindowingMode()) {
                scheduleUpdatePictureInPictureModeIfNeeded(task, stack.getRequestedOverrideBounds());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void scheduleUpdatePictureInPictureModeIfNeeded(TaskRecord task, Rect targetStackBounds) {
        for (int i = task.mActivities.size() - 1; i >= 0; i--) {
            ActivityRecord r = task.mActivities.get(i);
            if (r.attachedToProcess()) {
                this.mPipModeChangedActivities.add(r);
                this.mMultiWindowModeChangedActivities.remove(r);
            }
        }
        this.mPipModeChangedTargetStackBounds = targetStackBounds;
        if (!this.mHandler.hasMessages(REPORT_PIP_MODE_CHANGED_MSG)) {
            this.mHandler.sendEmptyMessage(REPORT_PIP_MODE_CHANGED_MSG);
        }
    }

    /* access modifiers changed from: package-private */
    public void updatePictureInPictureMode(TaskRecord task, Rect targetStackBounds, boolean forceUpdate) {
        this.mHandler.removeMessages(REPORT_PIP_MODE_CHANGED_MSG);
        for (int i = task.mActivities.size() - 1; i >= 0; i--) {
            ActivityRecord r = task.mActivities.get(i);
            if (r.attachedToProcess()) {
                r.updatePictureInPictureMode(targetStackBounds, forceUpdate);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void wakeUp(String reason) {
        PowerManager powerManager = this.mPowerManager;
        long uptimeMillis = SystemClock.uptimeMillis();
        powerManager.wakeUp(uptimeMillis, 2, "android.server.am:TURN_ON:" + reason);
    }

    /* access modifiers changed from: package-private */
    public void beginDeferResume() {
        this.mDeferResumeCount++;
    }

    /* access modifiers changed from: package-private */
    public void endDeferResume() {
        this.mDeferResumeCount--;
    }

    /* access modifiers changed from: package-private */
    public boolean readyToResume() {
        return this.mDeferResumeCount == 0;
    }

    final class ActivityStackSupervisorHandler extends Handler {
        public ActivityStackSupervisorHandler(Looper looper) {
            super(looper);
        }

        /* access modifiers changed from: package-private */
        public void activityIdleInternal(ActivityRecord r, boolean processPausingActivities) {
            synchronized (ActivityStackSupervisor.this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityStackSupervisor.this.activityIdleInternalLocked(r != null ? r.appToken : null, true, processPausingActivities, (Configuration) null);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            switch (i) {
                case 200:
                    activityIdleInternal((ActivityRecord) msg.obj, true);
                    return;
                case 201:
                    activityIdleInternal((ActivityRecord) msg.obj, false);
                    return;
                case 202:
                    synchronized (ActivityStackSupervisor.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            ActivityStackSupervisor.this.mRootActivityContainer.resumeFocusedStacksTopActivities();
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                                break;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                case 203:
                    synchronized (ActivityStackSupervisor.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (ActivityStackSupervisor.this.mService.isSleepingOrShuttingDownLocked()) {
                                Slog.w("ActivityTaskManager", "Sleep timeout!  Sleeping now.");
                                ActivityStackSupervisor.this.checkReadyForSleepLocked(false);
                            }
                        } catch (Throwable th2) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th2;
                                break;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                case ActivityStackSupervisor.LAUNCH_TIMEOUT_MSG /*204*/:
                    synchronized (ActivityStackSupervisor.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (ActivityStackSupervisor.this.mLaunchingActivityWakeLock.isHeld()) {
                                Slog.w("ActivityTaskManager", "Launch timeout has expired, giving up wake lock!");
                                ActivityStackSupervisor.this.mLaunchingActivityWakeLock.release();
                            }
                        } catch (Throwable th3) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th3;
                                break;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                default:
                    switch (i) {
                        case 212:
                            synchronized (ActivityStackSupervisor.this.mService.mGlobalLock) {
                                try {
                                    WindowManagerService.boostPriorityForLockedSection();
                                    ActivityRecord r = ActivityRecord.forTokenLocked((IBinder) msg.obj);
                                    if (r != null) {
                                        ActivityStackSupervisor.this.handleLaunchTaskBehindCompleteLocked(r);
                                    }
                                } catch (Throwable th4) {
                                    while (true) {
                                        WindowManagerService.resetPriorityAfterLockedSection();
                                        throw th4;
                                        break;
                                    }
                                }
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        case ActivityStackSupervisor.RESTART_ACTIVITY_PROCESS_TIMEOUT_MSG /*213*/:
                            ActivityRecord r2 = (ActivityRecord) msg.obj;
                            String processName = null;
                            int uid = 0;
                            synchronized (ActivityStackSupervisor.this.mService.mGlobalLock) {
                                try {
                                    WindowManagerService.boostPriorityForLockedSection();
                                    if (r2.attachedToProcess() && r2.isState(ActivityStack.ActivityState.RESTARTING_PROCESS)) {
                                        processName = r2.app.mName;
                                        uid = r2.app.mUid;
                                    }
                                } catch (Throwable th5) {
                                    while (true) {
                                        WindowManagerService.resetPriorityAfterLockedSection();
                                        throw th5;
                                        break;
                                    }
                                }
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            if (processName != null) {
                                ActivityStackSupervisor.this.mService.mAmInternal.killProcess(processName, uid, "restartActivityProcessTimeout");
                                return;
                            }
                            return;
                        case ActivityStackSupervisor.REPORT_MULTI_WINDOW_MODE_CHANGED_MSG /*214*/:
                            synchronized (ActivityStackSupervisor.this.mService.mGlobalLock) {
                                try {
                                    WindowManagerService.boostPriorityForLockedSection();
                                    for (int i2 = ActivityStackSupervisor.this.mMultiWindowModeChangedActivities.size() - 1; i2 >= 0; i2--) {
                                        ActivityStackSupervisor.this.mMultiWindowModeChangedActivities.remove(i2).updateMultiWindowMode();
                                    }
                                } catch (Throwable th6) {
                                    while (true) {
                                        WindowManagerService.resetPriorityAfterLockedSection();
                                        throw th6;
                                        break;
                                    }
                                }
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        case ActivityStackSupervisor.REPORT_PIP_MODE_CHANGED_MSG /*215*/:
                            synchronized (ActivityStackSupervisor.this.mService.mGlobalLock) {
                                try {
                                    WindowManagerService.boostPriorityForLockedSection();
                                    for (int i3 = ActivityStackSupervisor.this.mPipModeChangedActivities.size() - 1; i3 >= 0; i3--) {
                                        ActivityStackSupervisor.this.mPipModeChangedActivities.remove(i3).updatePictureInPictureMode(ActivityStackSupervisor.this.mPipModeChangedTargetStackBounds, false);
                                    }
                                } catch (Throwable th7) {
                                    while (true) {
                                        WindowManagerService.resetPriorityAfterLockedSection();
                                        throw th7;
                                        break;
                                    }
                                }
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        case ActivityStackSupervisor.REPORT_HOME_CHANGED_MSG /*216*/:
                            synchronized (ActivityStackSupervisor.this.mService.mGlobalLock) {
                                try {
                                    WindowManagerService.boostPriorityForLockedSection();
                                    ActivityStackSupervisor.this.mHandler.removeMessages(ActivityStackSupervisor.REPORT_HOME_CHANGED_MSG);
                                    ActivityStackSupervisor.this.mRootActivityContainer.startHomeOnEmptyDisplays("homeChanged");
                                } catch (Throwable th8) {
                                    while (true) {
                                        WindowManagerService.resetPriorityAfterLockedSection();
                                        throw th8;
                                        break;
                                    }
                                }
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        case ActivityStackSupervisor.TOP_RESUMED_STATE_LOSS_TIMEOUT_MSG /*217*/:
                            ActivityRecord r3 = (ActivityRecord) msg.obj;
                            Slog.w("ActivityTaskManager", "Activity top resumed state loss timeout for " + r3);
                            synchronized (ActivityStackSupervisor.this.mService.mGlobalLock) {
                                try {
                                    WindowManagerService.boostPriorityForLockedSection();
                                    if (r3.hasProcess()) {
                                        ActivityStackSupervisor.this.mService.logAppTooSlow(r3.app, r3.topResumedStateLossTime, "top state loss for " + r3);
                                    }
                                } catch (Throwable th9) {
                                    while (true) {
                                        WindowManagerService.resetPriorityAfterLockedSection();
                                        throw th9;
                                        break;
                                    }
                                }
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            ActivityStackSupervisor.this.handleTopResumedStateReleased(true);
                            return;
                        default:
                            return;
                    }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setResizingDuringAnimation(TaskRecord task) {
        this.mResizingTasksDuringAnimation.add(Integer.valueOf(task.taskId));
        task.setTaskDockedResizing(true);
    }

    /* Debug info: failed to restart local var, previous not found, register: 31 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x02f4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int startActivityFromRecents(int r32, int r33, int r34, com.android.server.wm.SafeActivityOptions r35) {
        /*
            r31 = this;
            r1 = r31
            r2 = r34
            r14 = r35
            r3 = 0
            r0 = 0
            r4 = 0
            r5 = 0
            if (r14 == 0) goto L_0x0011
            android.app.ActivityOptions r6 = r14.getOptions((com.android.server.wm.ActivityStackSupervisor) r1)
            goto L_0x0012
        L_0x0011:
            r6 = r5
        L_0x0012:
            r13 = r6
            if (r13 == 0) goto L_0x0038
            int r0 = r13.getLaunchActivityType()
            int r4 = r13.getLaunchWindowingMode()
            boolean r6 = r13.freezeRecentTasksReordering()
            if (r6 == 0) goto L_0x0033
            com.android.server.wm.RecentTasks r6 = r1.mRecentTasks
            r12 = r33
            boolean r6 = r6.isCallerRecents(r12)
            if (r6 == 0) goto L_0x0035
            com.android.server.wm.RecentTasks r6 = r1.mRecentTasks
            r6.setFreezeTaskListReordering()
            goto L_0x0035
        L_0x0033:
            r12 = r33
        L_0x0035:
            r11 = r0
            r10 = r4
            goto L_0x003c
        L_0x0038:
            r12 = r33
            r11 = r0
            r10 = r4
        L_0x003c:
            java.lang.String r0 = "startActivityFromRecents: Task "
            r15 = 2
            if (r11 == r15) goto L_0x0302
            r9 = 3
            if (r11 == r9) goto L_0x0302
            com.android.server.wm.WindowManagerService r4 = r1.mWindowManager
            r4.deferSurfaceLayout()
            java.lang.String r8 = "startActivityFromRecents: homeVisibleInSplitScreen"
            r6 = 0
            if (r10 != r9) goto L_0x006f
            com.android.server.wm.WindowManagerService r4 = r1.mWindowManager     // Catch:{ all -> 0x0062 }
            int r7 = r13.getSplitScreenCreateMode()     // Catch:{ all -> 0x0062 }
            r4.setDockedStackCreateState(r7, r5)     // Catch:{ all -> 0x0062 }
            r31.deferUpdateRecentsHomeStackBounds()     // Catch:{ all -> 0x0062 }
            com.android.server.wm.WindowManagerService r4 = r1.mWindowManager     // Catch:{ all -> 0x0062 }
            r5 = 19
            r4.prepareAppTransition(r5, r6)     // Catch:{ all -> 0x0062 }
            goto L_0x006f
        L_0x0062:
            r0 = move-exception
            r5 = r9
            r4 = r10
            r21 = r11
            r22 = r13
            r10 = r6
            r9 = r8
            r8 = 4
            r6 = r3
            goto L_0x02da
        L_0x006f:
            com.android.server.wm.RootActivityContainer r4 = r1.mRootActivityContainer     // Catch:{ all -> 0x02cf }
            r5 = 1
            com.android.server.wm.TaskRecord r4 = r4.anyTaskForId(r2, r15, r13, r5)     // Catch:{ all -> 0x02cf }
            r7 = r4
            if (r7 == 0) goto L_0x02a1
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService     // Catch:{ all -> 0x0295 }
            int r0 = r0.getCastModeStackId()     // Catch:{ all -> 0x0295 }
            int r3 = r7.getStackId()     // Catch:{ all -> 0x0295 }
            if (r0 != r3) goto L_0x00c8
            com.android.server.wm.ActivityStack r0 = r7.getStack()     // Catch:{ all -> 0x00bb }
            java.lang.String r3 = "exitCastMode"
            r0.moveToFront(r3, r7)     // Catch:{ all -> 0x00bb }
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService     // Catch:{ all -> 0x00bb }
            r0.resumeCastActivity()     // Catch:{ all -> 0x00bb }
            if (r10 != r9) goto L_0x00b5
            r1.setResizingDuringAnimation(r7)
            com.android.server.wm.ActivityStack r0 = r7.getStack()
            com.android.server.wm.ActivityDisplay r0 = r0.getDisplay()
            r4 = 4
            com.android.server.wm.ActivityStack r3 = r0.getTopStackInWindowingMode(r4)
            boolean r4 = r3.isActivityTypeHome()
            if (r4 == 0) goto L_0x00b5
            r0.moveHomeStackToFront(r8)
            com.android.server.wm.WindowManagerService r4 = r1.mWindowManager
            r4.checkSplitScreenMinimizedChanged(r6)
        L_0x00b5:
            com.android.server.wm.WindowManagerService r0 = r1.mWindowManager
            r0.continueSurfaceLayout()
            return r6
        L_0x00bb:
            r0 = move-exception
            r5 = r9
            r4 = r10
            r21 = r11
            r22 = r13
            r10 = r6
            r6 = r7
            r9 = r8
            r8 = 4
            goto L_0x02da
        L_0x00c8:
            r4 = 4
            if (r10 == r9) goto L_0x00e4
            com.android.server.wm.RootActivityContainer r0 = r1.mRootActivityContainer     // Catch:{ all -> 0x00d7 }
            com.android.server.wm.ActivityDisplay r0 = r0.getDefaultDisplay()     // Catch:{ all -> 0x00d7 }
            java.lang.String r3 = "startActivityFromRecents"
            r0.moveHomeStackToFront(r3)     // Catch:{ all -> 0x00d7 }
            goto L_0x00e4
        L_0x00d7:
            r0 = move-exception
            r5 = r9
            r21 = r11
            r22 = r13
            r9 = r8
            r8 = r4
            r4 = r10
            r10 = r6
            r6 = r7
            goto L_0x02da
        L_0x00e4:
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService     // Catch:{ all -> 0x0289 }
            android.app.ActivityManagerInternal r0 = r0.mAmInternal     // Catch:{ all -> 0x0289 }
            int r3 = r7.userId     // Catch:{ all -> 0x0289 }
            boolean r0 = r0.shouldConfirmCredentials(r3)     // Catch:{ all -> 0x0289 }
            if (r0 != 0) goto L_0x01e8
            com.android.server.wm.ActivityRecord r0 = r7.getRootActivity()     // Catch:{ all -> 0x01d4 }
            if (r0 == 0) goto L_0x01ca
            com.android.server.wm.ActivityRecord r0 = r7.getTopActivity()     // Catch:{ all -> 0x01d4 }
            r3 = r0
            com.android.server.wm.RootActivityContainer r0 = r1.mRootActivityContainer     // Catch:{ all -> 0x01d4 }
            r0.sendPowerHintForLaunchStartIfNeeded(r5, r3)     // Catch:{ all -> 0x01d4 }
            com.android.server.wm.ActivityMetricsLogger r0 = r1.mActivityMetricsLogger     // Catch:{ all -> 0x01d4 }
            android.content.Intent r5 = r7.intent     // Catch:{ all -> 0x01d4 }
            r0.notifyActivityLaunching(r5)     // Catch:{ all -> 0x01d4 }
            com.android.server.wm.ActivityRecord r0 = r7.getTopActivity()     // Catch:{ all -> 0x01d4 }
            r5 = r0
            if (r5 == 0) goto L_0x0115
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService     // Catch:{ all -> 0x00d7 }
            com.android.server.wm.MiuiGestureController r0 = r0.mGestureController     // Catch:{ all -> 0x00d7 }
            r0.notifyStartFromRecents(r5)     // Catch:{ all -> 0x00d7 }
        L_0x0115:
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService     // Catch:{ all -> 0x01ab }
            r16 = 0
            r17 = 0
            int r6 = r7.taskId     // Catch:{ all -> 0x019f }
            r19 = 0
            r20 = 1
            r21 = r3
            r3 = r0
            r22 = r4
            r4 = r16
            r16 = r5
            r5 = r17
            r17 = r13
            r13 = 0
            r23 = r7
            r12 = r22
            r7 = r19
            r22 = r11
            r11 = r8
            r8 = r35
            r13 = r9
            r9 = r20
            r3.moveTaskToFrontLocked(r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0198 }
            r21.applyOptionsLocked()     // Catch:{ all -> 0x0198 }
            com.android.server.wm.ActivityMetricsLogger r0 = r1.mActivityMetricsLogger     // Catch:{ all -> 0x0189 }
            r3 = r21
            r0.notifyActivityLaunched(r15, r3)     // Catch:{ all -> 0x0189 }
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService     // Catch:{ all -> 0x0189 }
            com.android.server.wm.ActivityStartController r0 = r0.getActivityStartController()     // Catch:{ all -> 0x0189 }
            com.android.server.wm.ActivityRecord r4 = r23.getTopActivity()     // Catch:{ all -> 0x0189 }
            com.android.server.wm.ActivityStack r5 = r23.getStack()     // Catch:{ all -> 0x0189 }
            r0.postStartActivityProcessingForLastStarter(r4, r15, r5)     // Catch:{ all -> 0x0189 }
            if (r10 != r13) goto L_0x0181
            r9 = r23
            r1.setResizingDuringAnimation(r9)
            com.android.server.wm.ActivityStack r0 = r9.getStack()
            com.android.server.wm.ActivityDisplay r0 = r0.getDisplay()
            com.android.server.wm.ActivityStack r4 = r0.getTopStackInWindowingMode(r12)
            boolean r5 = r4.isActivityTypeHome()
            if (r5 == 0) goto L_0x0183
            r0.moveHomeStackToFront(r11)
            com.android.server.wm.WindowManagerService r5 = r1.mWindowManager
            r8 = 0
            r5.checkSplitScreenMinimizedChanged(r8)
            goto L_0x0183
        L_0x0181:
            r9 = r23
        L_0x0183:
            com.android.server.wm.WindowManagerService r0 = r1.mWindowManager
            r0.continueSurfaceLayout()
            return r15
        L_0x0189:
            r0 = move-exception
            r9 = r23
            r6 = r9
            r4 = r10
            r9 = r11
            r8 = r12
            r5 = r13
            r21 = r22
            r10 = 0
            r22 = r17
            goto L_0x02da
        L_0x0198:
            r0 = move-exception
            r3 = r21
            r9 = r23
            r8 = 0
            goto L_0x01b7
        L_0x019f:
            r0 = move-exception
            r12 = r4
            r16 = r5
            r22 = r11
            r17 = r13
            r11 = r8
            r13 = r9
            r8 = 0
            goto L_0x01b6
        L_0x01ab:
            r0 = move-exception
            r12 = r4
            r16 = r5
            r22 = r11
            r17 = r13
            r11 = r8
            r13 = r9
            r8 = r6
        L_0x01b6:
            r9 = r7
        L_0x01b7:
            com.android.server.wm.ActivityMetricsLogger r4 = r1.mActivityMetricsLogger     // Catch:{ all -> 0x01bd }
            r4.notifyActivityLaunched(r15, r3)     // Catch:{ all -> 0x01bd }
            throw r0     // Catch:{ all -> 0x01bd }
        L_0x01bd:
            r0 = move-exception
            r6 = r9
            r4 = r10
            r9 = r11
            r5 = r13
            r21 = r22
            r10 = r8
            r8 = r12
            r22 = r17
            goto L_0x02da
        L_0x01ca:
            r12 = r4
            r22 = r11
            r17 = r13
            r11 = r8
            r13 = r9
            r8 = r6
            r9 = r7
            goto L_0x01f1
        L_0x01d4:
            r0 = move-exception
            r22 = r11
            r17 = r13
            r11 = r8
            r13 = r9
            r9 = r7
            r8 = r4
            r4 = r10
            r5 = r13
            r21 = r22
            r10 = r6
            r6 = r9
            r9 = r11
            r22 = r17
            goto L_0x02da
        L_0x01e8:
            r12 = r4
            r22 = r11
            r17 = r13
            r11 = r8
            r13 = r9
            r8 = r6
            r9 = r7
        L_0x01f1:
            java.lang.String r7 = r9.mCallingPackage     // Catch:{ all -> 0x027c }
            android.content.Intent r0 = r9.intent     // Catch:{ all -> 0x027c }
            r3 = 1048576(0x100000, float:1.469368E-39)
            r0.addFlags(r3)     // Catch:{ all -> 0x027c }
            r0.addMiuiFlags(r15)     // Catch:{ all -> 0x027c }
            int r15 = r9.userId     // Catch:{ all -> 0x027c }
            com.android.server.wm.ActivityTaskManagerService r3 = r1.mService     // Catch:{ all -> 0x027c }
            com.android.server.wm.ActivityStartController r3 = r3.getActivityStartController()     // Catch:{ all -> 0x027c }
            int r4 = r9.mCallingUid     // Catch:{ all -> 0x027c }
            r16 = 0
            r21 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            java.lang.String r26 = "startActivityFromRecents"
            r18 = 0
            r19 = 0
            r20 = 0
            r5 = r32
            r6 = r33
            r27 = r8
            r8 = r0
            r28 = r9
            r9 = r16
            r29 = r10
            r10 = r21
            r30 = r11
            r21 = r22
            r11 = r23
            r12 = r24
            r22 = r17
            r13 = r25
            r14 = r35
            r16 = r28
            r17 = r26
            int r3 = r3.startActivityInPackage(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x0270 }
            r4 = r29
            r5 = 3
            if (r4 != r5) goto L_0x0268
            r6 = r28
            r1.setResizingDuringAnimation(r6)
            com.android.server.wm.ActivityStack r5 = r6.getStack()
            com.android.server.wm.ActivityDisplay r5 = r5.getDisplay()
            r8 = 4
            com.android.server.wm.ActivityStack r8 = r5.getTopStackInWindowingMode(r8)
            boolean r9 = r8.isActivityTypeHome()
            if (r9 == 0) goto L_0x026a
            r9 = r30
            r5.moveHomeStackToFront(r9)
            com.android.server.wm.WindowManagerService r9 = r1.mWindowManager
            r10 = 0
            r9.checkSplitScreenMinimizedChanged(r10)
            goto L_0x026a
        L_0x0268:
            r6 = r28
        L_0x026a:
            com.android.server.wm.WindowManagerService r5 = r1.mWindowManager
            r5.continueSurfaceLayout()
            return r3
        L_0x0270:
            r0 = move-exception
            r6 = r28
            r4 = r29
            r9 = r30
            r5 = 3
            r8 = 4
            r10 = 0
            goto L_0x02da
        L_0x027c:
            r0 = move-exception
            r6 = r9
            r4 = r10
            r9 = r11
            r5 = r13
            r21 = r22
            r10 = r8
            r8 = r12
            r22 = r17
            goto L_0x02da
        L_0x0289:
            r0 = move-exception
            r5 = r9
            r21 = r11
            r22 = r13
            r9 = r8
            r8 = r4
            r4 = r10
            r10 = r6
            r6 = r7
            goto L_0x02a0
        L_0x0295:
            r0 = move-exception
            r5 = r9
            r4 = r10
            r21 = r11
            r22 = r13
            r10 = r6
            r6 = r7
            r9 = r8
            r8 = 4
        L_0x02a0:
            goto L_0x02da
        L_0x02a1:
            r5 = r9
            r4 = r10
            r21 = r11
            r22 = r13
            r10 = r6
            r6 = r7
            r9 = r8
            r8 = 4
            r31.continueUpdateRecentsHomeStackBounds()     // Catch:{ all -> 0x02cd }
            com.android.server.wm.WindowManagerService r3 = r1.mWindowManager     // Catch:{ all -> 0x02cd }
            r3.executeAppTransition()     // Catch:{ all -> 0x02cd }
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x02cd }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x02cd }
            r7.<init>()     // Catch:{ all -> 0x02cd }
            r7.append(r0)     // Catch:{ all -> 0x02cd }
            r7.append(r2)     // Catch:{ all -> 0x02cd }
            java.lang.String r0 = " not found."
            r7.append(r0)     // Catch:{ all -> 0x02cd }
            java.lang.String r0 = r7.toString()     // Catch:{ all -> 0x02cd }
            r3.<init>(r0)     // Catch:{ all -> 0x02cd }
            throw r3     // Catch:{ all -> 0x02cd }
        L_0x02cd:
            r0 = move-exception
            goto L_0x02da
        L_0x02cf:
            r0 = move-exception
            r5 = r9
            r4 = r10
            r21 = r11
            r22 = r13
            r10 = r6
            r9 = r8
            r8 = 4
            r6 = r3
        L_0x02da:
            if (r4 != r5) goto L_0x02fc
            if (r6 == 0) goto L_0x02fc
            r1.setResizingDuringAnimation(r6)
            com.android.server.wm.ActivityStack r3 = r6.getStack()
            com.android.server.wm.ActivityDisplay r3 = r3.getDisplay()
            com.android.server.wm.ActivityStack r5 = r3.getTopStackInWindowingMode(r8)
            boolean r7 = r5.isActivityTypeHome()
            if (r7 == 0) goto L_0x02fc
            r3.moveHomeStackToFront(r9)
            com.android.server.wm.WindowManagerService r7 = r1.mWindowManager
            r7.checkSplitScreenMinimizedChanged(r10)
        L_0x02fc:
            com.android.server.wm.WindowManagerService r3 = r1.mWindowManager
            r3.continueSurfaceLayout()
            throw r0
        L_0x0302:
            r4 = r10
            r21 = r11
            r22 = r13
            java.lang.IllegalArgumentException r5 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r0)
            r6.append(r2)
            java.lang.String r0 = " can't be launch in the home/recents stack."
            r6.append(r0)
            java.lang.String r0 = r6.toString()
            r5.<init>(r0)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStackSupervisor.startActivityFromRecents(int, int, int, com.android.server.wm.SafeActivityOptions):int");
    }

    static class WaitInfo {
        private final WaitResult mResult;
        private final long mStartTimeMs;
        private final ComponentName mTargetComponent;

        WaitInfo(ComponentName targetComponent, WaitResult result, long startTimeMs) {
            this.mTargetComponent = targetComponent;
            this.mResult = result;
            this.mStartTimeMs = startTimeMs;
        }

        public boolean matches(ComponentName targetComponent) {
            ComponentName componentName = this.mTargetComponent;
            return componentName == null || componentName.equals(targetComponent);
        }

        public WaitResult getResult() {
            return this.mResult;
        }

        public long getStartTime() {
            return this.mStartTimeMs;
        }

        public ComponentName getComponent() {
            return this.mTargetComponent;
        }

        public void dump(PrintWriter pw, String prefix) {
            pw.println(prefix + "WaitInfo:");
            pw.println(prefix + "  mTargetComponent=" + this.mTargetComponent);
            StringBuilder sb = new StringBuilder();
            sb.append(prefix);
            sb.append("  mResult=");
            pw.println(sb.toString());
            this.mResult.dump(pw, prefix);
        }
    }

    class PreferredAppsTask extends AsyncTask<Void, Void, Void> {
        PreferredAppsTask() {
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            String res;
            new Intent("android.intent.action.MAIN");
            try {
                int trimLevel = ActivityManager.getService().getMemoryTrimLevel();
                if (ActivityStackSupervisor.this.mUxPerf == null || trimLevel >= 3 || (res = ActivityStackSupervisor.this.mUxPerf.perfUXEngine_trigger(1)) == null) {
                    return null;
                }
                String[] p_apps = res.split(SliceClientPermissions.SliceAuthority.DELIMITER);
                if (p_apps.length != 0) {
                    ArrayList<String> apps_l = new ArrayList<>(Arrays.asList(p_apps));
                    Bundle bParams = new Bundle();
                    bParams.putStringArrayList("start_empty_apps", apps_l);
                    ActivityStackSupervisor.this.mService.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$gfq3ciA_KEGa4G8MjW1JFVBuAk8.INSTANCE, ActivityStackSupervisor.this.mService.mAmInternal, bParams));
                }
                return null;
            } catch (RemoteException e) {
                return null;
            }
        }
    }

    public void scheduleIdleIfNeedLocked() {
        ActivityStackSupervisorHandler activityStackSupervisorHandler = this.mHandler;
        if (activityStackSupervisorHandler != null && activityStackSupervisorHandler.hasMessages(200)) {
            this.mHandler.removeMessages(200);
            this.mHandler.sendEmptyMessage(201);
        }
    }
}
