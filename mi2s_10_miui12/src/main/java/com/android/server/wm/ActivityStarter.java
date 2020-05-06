package com.android.server.wm;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.IApplicationThread;
import android.app.ProfilerInfo;
import android.app.WaitResult;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.AuxiliaryResolveInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.voice.IVoiceInteractionSession;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.BoostFramework;
import android.util.EventLog;
import android.util.Pools;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IVoiceInteractor;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.am.EventLogTags;
import com.android.server.am.PendingIntentRecord;
import com.android.server.pm.DumpState;
import com.android.server.pm.InstantAppResolver;
import com.android.server.wm.ActivityStack;
import com.android.server.wm.LaunchParamsController;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

class ActivityStarter {
    private static final int INVALID_LAUNCH_MODE = -1;
    private static final String TAG = "ActivityTaskManager";
    private static final String TAG_CONFIGURATION = "ActivityTaskManager";
    private static final String TAG_FOCUS = "ActivityTaskManager";
    private static final String TAG_RESULTS = "ActivityTaskManager";
    private static final String TAG_USER_LEAVING = "ActivityTaskManager";
    private boolean mAddingToTask;
    private boolean mAvoidMoveToFront;
    private int mCallingUid;
    private final ActivityStartController mController;
    private boolean mDoResume;
    private TaskRecord mInTask;
    private Intent mIntent;
    private boolean mIntentDelivered;
    private final ActivityStartInterceptor mInterceptor;
    private boolean mKeepCurTransition;
    private final ActivityRecord[] mLastStartActivityRecord = new ActivityRecord[1];
    private int mLastStartActivityResult;
    private long mLastStartActivityTimeMs;
    private String mLastStartReason;
    private int mLaunchFlags;
    private int mLaunchMode;
    private LaunchParamsController.LaunchParams mLaunchParams = new LaunchParamsController.LaunchParams();
    private boolean mLaunchTaskBehind;
    private boolean mMovedToFront;
    private ActivityInfo mNewTaskInfo;
    private Intent mNewTaskIntent;
    private boolean mNoAnimation;
    private ActivityRecord mNotTop;
    private ActivityOptions mOptions;
    public BoostFramework mPerf = null;
    private int mPreferredDisplayId;
    private Request mRequest = new Request();
    private boolean mRestrictedBgActivity;
    private TaskRecord mReuseTask;
    private final RootActivityContainer mRootActivityContainer;
    private final ActivityTaskManagerService mService;
    private ActivityRecord mSourceRecord;
    private ActivityStack mSourceStack;
    private ActivityRecord mStartActivity;
    private int mStartFlags;
    private final ActivityStackSupervisor mSupervisor;
    private ActivityStack mTargetStack;
    private IVoiceInteractor mVoiceInteractor;
    private IVoiceInteractionSession mVoiceSession;

    @VisibleForTesting
    interface Factory {
        ActivityStarter obtain();

        void recycle(ActivityStarter activityStarter);

        void setController(ActivityStartController activityStartController);
    }

    static class DefaultFactory implements Factory {
        private final int MAX_STARTER_COUNT = 3;
        private ActivityStartController mController;
        private ActivityStartInterceptor mInterceptor;
        private ActivityTaskManagerService mService;
        private Pools.SynchronizedPool<ActivityStarter> mStarterPool = new Pools.SynchronizedPool<>(3);
        private ActivityStackSupervisor mSupervisor;

        DefaultFactory(ActivityTaskManagerService service, ActivityStackSupervisor supervisor, ActivityStartInterceptor interceptor) {
            this.mService = service;
            this.mSupervisor = supervisor;
            this.mInterceptor = interceptor;
        }

        public void setController(ActivityStartController controller) {
            this.mController = controller;
        }

        public ActivityStarter obtain() {
            ActivityStarter starter = (ActivityStarter) this.mStarterPool.acquire();
            if (starter == null) {
                return new ActivityStarter(this.mController, this.mService, this.mSupervisor, this.mInterceptor);
            }
            return starter;
        }

        public void recycle(ActivityStarter starter) {
            starter.reset(true);
            this.mStarterPool.release(starter);
        }
    }

    private static class Request {
        private static final int DEFAULT_CALLING_PID = 0;
        private static final int DEFAULT_CALLING_UID = -1;
        static final int DEFAULT_REAL_CALLING_PID = 0;
        static final int DEFAULT_REAL_CALLING_UID = -1;
        ActivityInfo activityInfo;
        SafeActivityOptions activityOptions;
        boolean allowBackgroundActivityStart;
        boolean allowPendingRemoteAnimationRegistryLookup;
        boolean avoidMoveToFront;
        IApplicationThread caller;
        String callingPackage;
        int callingPid = 0;
        int callingUid = -1;
        boolean componentSpecified;
        Intent ephemeralIntent;
        int filterCallingUid;
        Configuration globalConfig;
        boolean ignoreTargetSecurity;
        TaskRecord inTask;
        Intent intent;
        boolean mayWait;
        PendingIntentRecord originatingPendingIntent;
        ActivityRecord[] outActivity;
        ProfilerInfo profilerInfo;
        int realCallingPid = 0;
        int realCallingUid = -1;
        String reason;
        int requestCode;
        ResolveInfo resolveInfo;
        String resolvedType;
        IBinder resultTo;
        String resultWho;
        int startFlags;
        int userId;
        IVoiceInteractor voiceInteractor;
        IVoiceInteractionSession voiceSession;
        WaitResult waitResult;

        Request() {
            reset();
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.caller = null;
            this.intent = null;
            this.ephemeralIntent = null;
            this.resolvedType = null;
            this.activityInfo = null;
            this.resolveInfo = null;
            this.voiceSession = null;
            this.voiceInteractor = null;
            this.resultTo = null;
            this.resultWho = null;
            this.requestCode = 0;
            this.callingPid = 0;
            this.callingUid = -1;
            this.callingPackage = null;
            this.realCallingPid = 0;
            this.realCallingUid = -1;
            this.startFlags = 0;
            this.activityOptions = null;
            this.ignoreTargetSecurity = false;
            this.componentSpecified = false;
            this.outActivity = null;
            this.inTask = null;
            this.reason = null;
            this.profilerInfo = null;
            this.globalConfig = null;
            this.userId = 0;
            this.waitResult = null;
            this.mayWait = false;
            this.avoidMoveToFront = false;
            this.allowPendingRemoteAnimationRegistryLookup = true;
            this.filterCallingUid = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
            this.originatingPendingIntent = null;
            this.allowBackgroundActivityStart = false;
        }

        /* access modifiers changed from: package-private */
        public void set(Request request) {
            this.caller = request.caller;
            this.intent = request.intent;
            this.ephemeralIntent = request.ephemeralIntent;
            this.resolvedType = request.resolvedType;
            this.activityInfo = request.activityInfo;
            this.resolveInfo = request.resolveInfo;
            this.voiceSession = request.voiceSession;
            this.voiceInteractor = request.voiceInteractor;
            this.resultTo = request.resultTo;
            this.resultWho = request.resultWho;
            this.requestCode = request.requestCode;
            this.callingPid = request.callingPid;
            this.callingUid = request.callingUid;
            this.callingPackage = request.callingPackage;
            this.realCallingPid = request.realCallingPid;
            this.realCallingUid = request.realCallingUid;
            this.startFlags = request.startFlags;
            this.activityOptions = request.activityOptions;
            this.ignoreTargetSecurity = request.ignoreTargetSecurity;
            this.componentSpecified = request.componentSpecified;
            this.outActivity = request.outActivity;
            this.inTask = request.inTask;
            this.reason = request.reason;
            this.profilerInfo = request.profilerInfo;
            this.globalConfig = request.globalConfig;
            this.userId = request.userId;
            this.waitResult = request.waitResult;
            this.mayWait = request.mayWait;
            this.avoidMoveToFront = request.avoidMoveToFront;
            this.allowPendingRemoteAnimationRegistryLookup = request.allowPendingRemoteAnimationRegistryLookup;
            this.filterCallingUid = request.filterCallingUid;
            this.originatingPendingIntent = request.originatingPendingIntent;
            this.allowBackgroundActivityStart = request.allowBackgroundActivityStart;
        }
    }

    ActivityStarter(ActivityStartController controller, ActivityTaskManagerService service, ActivityStackSupervisor supervisor, ActivityStartInterceptor interceptor) {
        this.mController = controller;
        this.mService = service;
        this.mRootActivityContainer = service.mRootActivityContainer;
        this.mSupervisor = supervisor;
        this.mInterceptor = interceptor;
        reset(true);
        this.mPerf = new BoostFramework();
    }

    /* access modifiers changed from: package-private */
    public void set(ActivityStarter starter) {
        this.mStartActivity = starter.mStartActivity;
        this.mIntent = starter.mIntent;
        this.mCallingUid = starter.mCallingUid;
        this.mOptions = starter.mOptions;
        this.mRestrictedBgActivity = starter.mRestrictedBgActivity;
        this.mLaunchTaskBehind = starter.mLaunchTaskBehind;
        this.mLaunchFlags = starter.mLaunchFlags;
        this.mLaunchMode = starter.mLaunchMode;
        this.mLaunchParams.set(starter.mLaunchParams);
        this.mNotTop = starter.mNotTop;
        this.mDoResume = starter.mDoResume;
        this.mStartFlags = starter.mStartFlags;
        this.mSourceRecord = starter.mSourceRecord;
        this.mPreferredDisplayId = starter.mPreferredDisplayId;
        this.mInTask = starter.mInTask;
        this.mAddingToTask = starter.mAddingToTask;
        this.mReuseTask = starter.mReuseTask;
        this.mNewTaskInfo = starter.mNewTaskInfo;
        this.mNewTaskIntent = starter.mNewTaskIntent;
        this.mSourceStack = starter.mSourceStack;
        this.mTargetStack = starter.mTargetStack;
        this.mMovedToFront = starter.mMovedToFront;
        this.mNoAnimation = starter.mNoAnimation;
        this.mKeepCurTransition = starter.mKeepCurTransition;
        this.mAvoidMoveToFront = starter.mAvoidMoveToFront;
        this.mVoiceSession = starter.mVoiceSession;
        this.mVoiceInteractor = starter.mVoiceInteractor;
        this.mIntentDelivered = starter.mIntentDelivered;
        this.mRequest.set(starter.mRequest);
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord getStartActivity() {
        return this.mStartActivity;
    }

    /* access modifiers changed from: package-private */
    public boolean relatedToPackage(String packageName) {
        ActivityRecord activityRecord;
        ActivityRecord[] activityRecordArr = this.mLastStartActivityRecord;
        if ((activityRecordArr[0] == null || !packageName.equals(activityRecordArr[0].packageName)) && ((activityRecord = this.mStartActivity) == null || !packageName.equals(activityRecord.packageName))) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public int execute() {
        try {
            if (this.mRequest.mayWait) {
                return startActivityMayWait(this.mRequest.caller, this.mRequest.callingUid, this.mRequest.callingPackage, this.mRequest.realCallingPid, this.mRequest.realCallingUid, this.mRequest.intent, this.mRequest.resolvedType, this.mRequest.voiceSession, this.mRequest.voiceInteractor, this.mRequest.resultTo, this.mRequest.resultWho, this.mRequest.requestCode, this.mRequest.startFlags, this.mRequest.profilerInfo, this.mRequest.waitResult, this.mRequest.globalConfig, this.mRequest.activityOptions, this.mRequest.ignoreTargetSecurity, this.mRequest.userId, this.mRequest.inTask, this.mRequest.reason, this.mRequest.allowPendingRemoteAnimationRegistryLookup, this.mRequest.originatingPendingIntent, this.mRequest.allowBackgroundActivityStart);
            }
            int startActivity = startActivity(this.mRequest.caller, this.mRequest.intent, this.mRequest.ephemeralIntent, this.mRequest.resolvedType, this.mRequest.activityInfo, this.mRequest.resolveInfo, this.mRequest.voiceSession, this.mRequest.voiceInteractor, this.mRequest.resultTo, this.mRequest.resultWho, this.mRequest.requestCode, this.mRequest.callingPid, this.mRequest.callingUid, this.mRequest.callingPackage, this.mRequest.realCallingPid, this.mRequest.realCallingUid, this.mRequest.startFlags, this.mRequest.activityOptions, this.mRequest.ignoreTargetSecurity, this.mRequest.componentSpecified, this.mRequest.outActivity, this.mRequest.inTask, this.mRequest.reason, this.mRequest.allowPendingRemoteAnimationRegistryLookup, this.mRequest.originatingPendingIntent, this.mRequest.allowBackgroundActivityStart);
            onExecutionComplete();
            return startActivity;
        } finally {
            onExecutionComplete();
        }
    }

    /* access modifiers changed from: package-private */
    public int startResolvedActivity(ActivityRecord r, ActivityRecord sourceRecord, IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor, int startFlags, boolean doResume, ActivityOptions options, TaskRecord inTask) {
        ActivityRecord activityRecord = r;
        try {
            this.mSupervisor.getActivityMetricsLogger().notifyActivityLaunching(activityRecord.intent);
            this.mLastStartReason = "startResolvedActivity";
            this.mLastStartActivityTimeMs = System.currentTimeMillis();
            this.mLastStartActivityRecord[0] = activityRecord;
            this.mLastStartActivityResult = startActivity(r, sourceRecord, voiceSession, voiceInteractor, startFlags, doResume, options, inTask, this.mLastStartActivityRecord, false);
            this.mSupervisor.getActivityMetricsLogger().notifyActivityLaunched(this.mLastStartActivityResult, this.mLastStartActivityRecord[0]);
            return this.mLastStartActivityResult;
        } finally {
            onExecutionComplete();
        }
    }

    private int startActivity(IApplicationThread caller, Intent intent, Intent ephemeralIntent, String resolvedType, ActivityInfo aInfo, ResolveInfo rInfo, IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor, IBinder resultTo, String resultWho, int requestCode, int callingPid, int callingUid, String callingPackage, int realCallingPid, int realCallingUid, int startFlags, SafeActivityOptions options, boolean ignoreTargetSecurity, boolean componentSpecified, ActivityRecord[] outActivity, TaskRecord inTask, String reason, boolean allowPendingRemoteAnimationRegistryLookup, PendingIntentRecord originatingPendingIntent, boolean allowBackgroundActivityStart) {
        if (!TextUtils.isEmpty(reason)) {
            this.mLastStartReason = reason;
            this.mLastStartActivityTimeMs = System.currentTimeMillis();
            ActivityRecord[] activityRecordArr = this.mLastStartActivityRecord;
            activityRecordArr[0] = null;
            this.mLastStartActivityResult = startActivity(caller, intent, ephemeralIntent, resolvedType, aInfo, rInfo, voiceSession, voiceInteractor, resultTo, resultWho, requestCode, callingPid, callingUid, callingPackage, realCallingPid, realCallingUid, startFlags, options, ignoreTargetSecurity, componentSpecified, activityRecordArr, inTask, allowPendingRemoteAnimationRegistryLookup, originatingPendingIntent, allowBackgroundActivityStart);
            if (outActivity != null) {
                outActivity[0] = this.mLastStartActivityRecord[0];
            }
            return getExternalResult(this.mLastStartActivityResult);
        }
        throw new IllegalArgumentException("Need to specify a reason.");
    }

    static int getExternalResult(int result) {
        if (result != 102) {
            return result;
        }
        return 0;
    }

    private void onExecutionComplete() {
        this.mController.onExecutionComplete(this);
    }

    /* JADX WARNING: Removed duplicated region for block: B:109:0x01f5  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x01f8  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0200  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0215  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x04a2  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x04ce  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x04e8  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x04eb  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x0515  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0519  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x052c  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0585  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int startActivity(android.app.IApplicationThread r64, android.content.Intent r65, android.content.Intent r66, java.lang.String r67, android.content.pm.ActivityInfo r68, android.content.pm.ResolveInfo r69, android.service.voice.IVoiceInteractionSession r70, com.android.internal.app.IVoiceInteractor r71, android.os.IBinder r72, java.lang.String r73, int r74, int r75, int r76, java.lang.String r77, int r78, int r79, int r80, com.android.server.wm.SafeActivityOptions r81, boolean r82, boolean r83, com.android.server.wm.ActivityRecord[] r84, com.android.server.wm.TaskRecord r85, boolean r86, com.android.server.am.PendingIntentRecord r87, boolean r88) {
        /*
            r63 = this;
            r14 = r63
            r13 = r64
            r11 = r65
            r12 = r67
            r10 = r68
            r9 = r72
            r8 = r79
            r7 = r80
            r6 = r81
            com.android.server.wm.ActivityStackSupervisor r0 = r14.mSupervisor
            com.android.server.wm.ActivityMetricsLogger r0 = r0.getActivityMetricsLogger()
            r0.notifyActivityLaunching(r11)
            r0 = 0
            if (r6 == 0) goto L_0x0024
            android.os.Bundle r1 = r81.popAppVerificationBundle()
            r15 = r1
            goto L_0x0025
        L_0x0024:
            r15 = 0
        L_0x0025:
            r1 = 0
            java.lang.String r2 = "ActivityTaskManager"
            if (r13 == 0) goto L_0x0071
            com.android.server.wm.ActivityTaskManagerService r3 = r14.mService
            com.android.server.wm.WindowProcessController r1 = r3.getProcessController(r13)
            if (r1 == 0) goto L_0x003f
            int r3 = r1.getPid()
            android.content.pm.ApplicationInfo r4 = r1.mInfo
            int r4 = r4.uid
            r31 = r3
            r5 = r4
            r4 = r1
            goto L_0x0078
        L_0x003f:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Unable to find app for caller "
            r3.append(r4)
            r3.append(r13)
            java.lang.String r4 = " (pid="
            r3.append(r4)
            r4 = r75
            r3.append(r4)
            java.lang.String r5 = ") when starting: "
            r3.append(r5)
            java.lang.String r5 = r65.toString()
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            android.util.Slog.w(r2, r3)
            r0 = -94
            r5 = r76
            r31 = r4
            r4 = r1
            goto L_0x0078
        L_0x0071:
            r4 = r75
            r5 = r76
            r31 = r4
            r4 = r1
        L_0x0078:
            if (r10 == 0) goto L_0x0087
            android.content.pm.ApplicationInfo r1 = r10.applicationInfo
            if (r1 == 0) goto L_0x0087
            android.content.pm.ApplicationInfo r1 = r10.applicationInfo
            int r1 = r1.uid
            int r1 = android.os.UserHandle.getUserId(r1)
            goto L_0x0088
        L_0x0087:
            r1 = 0
        L_0x0088:
            if (r0 != 0) goto L_0x00b5
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r6 = "START u"
            r3.append(r6)
            r3.append(r1)
            java.lang.String r6 = " {"
            r3.append(r6)
            r6 = 0
            r8 = 1
            java.lang.String r13 = r11.toShortString(r8, r8, r8, r6)
            r3.append(r13)
            java.lang.String r8 = "} from uid "
            r3.append(r8)
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            android.util.Slog.i(r2, r3)
            goto L_0x00b6
        L_0x00b5:
            r6 = 0
        L_0x00b6:
            r3 = 0
            r8 = 0
            if (r9 == 0) goto L_0x00f1
            com.android.server.wm.RootActivityContainer r13 = r14.mRootActivityContainer
            com.android.server.wm.ActivityRecord r3 = r13.isInAnyStack(r9)
            if (r3 == 0) goto L_0x00ed
            com.android.server.wm.TaskRecord r13 = r3.getTaskRecord()
            if (r13 == 0) goto L_0x00dc
            boolean r13 = r3.inMultiWindowMode()
            if (r13 == 0) goto L_0x00dc
            r13 = -1
            r6 = r74
            if (r6 != r13) goto L_0x00de
            boolean r13 = com.android.server.wm.ActivityStackSupervisorInjector.isAppLockActivity(r11, r10)
            if (r13 == 0) goto L_0x00de
            r6 = -1001(0xfffffffffffffc17, float:NaN)
            goto L_0x00de
        L_0x00dc:
            r6 = r74
        L_0x00de:
            if (r6 >= 0) goto L_0x00e4
            r13 = -1001(0xfffffffffffffc17, float:NaN)
            if (r6 != r13) goto L_0x00eb
        L_0x00e4:
            boolean r13 = r3.finishing
            if (r13 != 0) goto L_0x00eb
            r8 = r3
            r13 = r3
            goto L_0x00f4
        L_0x00eb:
            r13 = r3
            goto L_0x00f4
        L_0x00ed:
            r6 = r74
            r13 = r3
            goto L_0x00f4
        L_0x00f1:
            r6 = r74
            r13 = r3
        L_0x00f4:
            int r49 = r65.getFlags()
            r3 = 33554432(0x2000000, float:9.403955E-38)
            r3 = r49 & r3
            if (r3 == 0) goto L_0x0133
            if (r13 == 0) goto L_0x0133
            if (r6 < 0) goto L_0x0108
            com.android.server.wm.SafeActivityOptions.abort(r81)
            r2 = -93
            return r2
        L_0x0108:
            com.android.server.wm.ActivityRecord r3 = r13.resultTo
            if (r3 == 0) goto L_0x0113
            boolean r8 = r3.isInStackLocked()
            if (r8 != 0) goto L_0x0113
            r3 = 0
        L_0x0113:
            java.lang.String r8 = r13.resultWho
            int r6 = r13.requestCode
            r9 = 0
            r13.resultTo = r9
            if (r3 == 0) goto L_0x011f
            r3.removeResultsLocked(r13, r8, r6)
        L_0x011f:
            int r9 = r13.launchedFromUid
            if (r9 != r5) goto L_0x012b
            java.lang.String r9 = r13.launchedFromPackage
            r58 = r6
            r57 = r8
            r8 = r3
            goto L_0x0139
        L_0x012b:
            r9 = r77
            r58 = r6
            r57 = r8
            r8 = r3
            goto L_0x0139
        L_0x0133:
            r57 = r73
            r9 = r77
            r58 = r6
        L_0x0139:
            if (r0 != 0) goto L_0x0143
            android.content.ComponentName r3 = r65.getComponent()
            if (r3 != 0) goto L_0x0143
            r0 = -91
        L_0x0143:
            if (r0 != 0) goto L_0x014b
            if (r10 != 0) goto L_0x014b
            r0 = -92
            r3 = r0
            goto L_0x014c
        L_0x014b:
            r3 = r0
        L_0x014c:
            java.lang.String r6 = "Failure checking voice capabilities"
            if (r3 != 0) goto L_0x01ab
            if (r13 == 0) goto L_0x01ab
            com.android.server.wm.TaskRecord r0 = r13.getTaskRecord()
            android.service.voice.IVoiceInteractionSession r0 = r0.voiceSession
            if (r0 == 0) goto L_0x01a8
            r0 = 268435456(0x10000000, float:2.5243549E-29)
            r0 = r49 & r0
            if (r0 != 0) goto L_0x01a5
            android.content.pm.ActivityInfo r0 = r13.info
            android.content.pm.ApplicationInfo r0 = r0.applicationInfo
            int r0 = r0.uid
            r73 = r3
            android.content.pm.ApplicationInfo r3 = r10.applicationInfo
            int r3 = r3.uid
            if (r0 == r3) goto L_0x01ad
            java.lang.String r0 = "android.intent.category.VOICE"
            r11.addCategory(r0)     // Catch:{ RemoteException -> 0x019e }
            com.android.server.wm.ActivityTaskManagerService r0 = r14.mService     // Catch:{ RemoteException -> 0x019e }
            android.content.pm.IPackageManager r0 = r0.getPackageManager()     // Catch:{ RemoteException -> 0x019e }
            android.content.ComponentName r3 = r65.getComponent()     // Catch:{ RemoteException -> 0x019e }
            boolean r0 = r0.activitySupportsIntent(r3, r11, r12)     // Catch:{ RemoteException -> 0x019e }
            if (r0 != 0) goto L_0x019b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x019e }
            r0.<init>()     // Catch:{ RemoteException -> 0x019e }
            java.lang.String r3 = "Activity being started in current voice task does not support voice: "
            r0.append(r3)     // Catch:{ RemoteException -> 0x019e }
            r0.append(r11)     // Catch:{ RemoteException -> 0x019e }
            java.lang.String r0 = r0.toString()     // Catch:{ RemoteException -> 0x019e }
            android.util.Slog.w(r2, r0)     // Catch:{ RemoteException -> 0x019e }
            r0 = -97
            r3 = r0
            goto L_0x019d
        L_0x019b:
            r3 = r73
        L_0x019d:
            goto L_0x01af
        L_0x019e:
            r0 = move-exception
            android.util.Slog.w(r2, r6, r0)
            r3 = -97
            goto L_0x01af
        L_0x01a5:
            r73 = r3
            goto L_0x01ad
        L_0x01a8:
            r73 = r3
            goto L_0x01ad
        L_0x01ab:
            r73 = r3
        L_0x01ad:
            r3 = r73
        L_0x01af:
            if (r3 != 0) goto L_0x01ef
            if (r70 == 0) goto L_0x01ef
            com.android.server.wm.ActivityTaskManagerService r0 = r14.mService     // Catch:{ RemoteException -> 0x01e4 }
            android.content.pm.IPackageManager r0 = r0.getPackageManager()     // Catch:{ RemoteException -> 0x01e4 }
            r73 = r3
            android.content.ComponentName r3 = r65.getComponent()     // Catch:{ RemoteException -> 0x01e2 }
            boolean r0 = r0.activitySupportsIntent(r3, r11, r12)     // Catch:{ RemoteException -> 0x01e2 }
            if (r0 != 0) goto L_0x01dd
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x01e2 }
            r0.<init>()     // Catch:{ RemoteException -> 0x01e2 }
            java.lang.String r3 = "Activity being started in new voice task does not support: "
            r0.append(r3)     // Catch:{ RemoteException -> 0x01e2 }
            r0.append(r11)     // Catch:{ RemoteException -> 0x01e2 }
            java.lang.String r0 = r0.toString()     // Catch:{ RemoteException -> 0x01e2 }
            android.util.Slog.w(r2, r0)     // Catch:{ RemoteException -> 0x01e2 }
            r0 = -97
            r3 = r0
            goto L_0x01df
        L_0x01dd:
            r3 = r73
        L_0x01df:
            r59 = r3
            goto L_0x01f3
        L_0x01e2:
            r0 = move-exception
            goto L_0x01e7
        L_0x01e4:
            r0 = move-exception
            r73 = r3
        L_0x01e7:
            android.util.Slog.w(r2, r6, r0)
            r3 = -97
            r59 = r3
            goto L_0x01f3
        L_0x01ef:
            r73 = r3
            r59 = r73
        L_0x01f3:
            if (r8 != 0) goto L_0x01f8
            r50 = 0
            goto L_0x01fe
        L_0x01f8:
            com.android.server.wm.ActivityStack r0 = r8.getActivityStack()
            r50 = r0
        L_0x01fe:
            if (r59 == 0) goto L_0x0215
            if (r8 == 0) goto L_0x0211
            r51 = -1
            r55 = 0
            r56 = 0
            r52 = r8
            r53 = r57
            r54 = r58
            r50.sendActivityResultLocked(r51, r52, r53, r54, r55, r56)
        L_0x0211:
            com.android.server.wm.SafeActivityOptions.abort(r81)
            return r59
        L_0x0215:
            com.android.server.wm.ActivityStackSupervisorInjector$OpCheckData r0 = new com.android.server.wm.ActivityStackSupervisorInjector$OpCheckData
            r0.<init>()
            r6 = r0
            r6.orginalintent = r11
            r6.resolvedType = r12
            r6.startFlags = r7
            r6.resultRecord = r8
            com.android.server.wm.ActivityStackSupervisor r0 = r14.mSupervisor
            r6.stackSupervisor = r0
            r6.userId = r1
            if (r85 == 0) goto L_0x022e
            r25 = 1
            goto L_0x0230
        L_0x022e:
            r25 = 0
        L_0x0230:
            r16 = r0
            r17 = r65
            r18 = r68
            r19 = r57
            r20 = r58
            r21 = r31
            r22 = r5
            r23 = r9
            r24 = r82
            r26 = r4
            r27 = r8
            r28 = r50
            r29 = r6
            boolean r0 = r16.checkStartAnyActivityPermission(r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29)
            r3 = 1
            r0 = r0 ^ r3
            com.android.server.wm.ActivityTaskManagerService r2 = r14.mService
            com.android.server.firewall.IntentFirewall r2 = r2.mIntentFirewall
            r73 = r6
            android.content.pm.ApplicationInfo r6 = r10.applicationInfo
            r74 = r1
            r1 = r2
            r2 = r65
            r28 = r15
            r15 = r3
            r3 = r5
            r76 = r4
            r4 = r31
            r60 = r5
            r5 = r67
            r61 = r73
            boolean r1 = r1.checkStartActivity(r2, r3, r4, r5, r6)
            r1 = r1 ^ r15
            r0 = r0 | r1
            com.android.server.wm.ActivityTaskManagerService r1 = r14.mService
            com.android.server.policy.PermissionPolicyInternal r1 = r1.getPermissionPolicyInternal()
            r6 = r60
            boolean r1 = r1.checkStartActivity(r11, r6, r9)
            r1 = r1 ^ r15
            r16 = r0 | r1
            r17 = 0
            if (r16 != 0) goto L_0x02c0
            r4 = 64
            java.lang.String r0 = "shouldAbortBackgroundActivityStart"
            android.os.Trace.traceBegin(r4, r0)     // Catch:{ all -> 0x02b2 }
            r1 = r63
            r2 = r6
            r3 = r31
            r18 = r4
            r4 = r9
            r5 = r79
            r20 = r6
            r6 = r78
            r7 = r76
            r29 = r8
            r8 = r87
            r73 = r9
            r9 = r88
            r15 = r10
            r10 = r65
            boolean r0 = r1.shouldAbortBackgroundActivityStart(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x02b0 }
            android.os.Trace.traceEnd(r18)
            r30 = r0
            goto L_0x02c9
        L_0x02b0:
            r0 = move-exception
            goto L_0x02bc
        L_0x02b2:
            r0 = move-exception
            r18 = r4
            r20 = r6
            r29 = r8
            r73 = r9
            r15 = r10
        L_0x02bc:
            android.os.Trace.traceEnd(r18)
            throw r0
        L_0x02c0:
            r20 = r6
            r29 = r8
            r73 = r9
            r15 = r10
            r30 = r17
        L_0x02c9:
            if (r16 != 0) goto L_0x02df
            com.android.server.wm.ActivityTaskManagerService r1 = r14.mService
            com.android.server.wm.ActivityStackSupervisor r2 = r14.mSupervisor
            r3 = r65
            r4 = r73
            r5 = r20
            r6 = r68
            boolean r0 = com.android.server.wm.ActivityTaskManagerServiceInjector.isAllowedStartActivity(r1, r2, r3, r4, r5, r6)
            if (r0 != 0) goto L_0x02df
            r16 = 1
        L_0x02df:
            r10 = r81
            if (r10 == 0) goto L_0x02ec
            com.android.server.wm.ActivityStackSupervisor r0 = r14.mSupervisor
            r9 = r76
            android.app.ActivityOptions r5 = r10.getOptions(r11, r15, r9, r0)
            goto L_0x02ef
        L_0x02ec:
            r9 = r76
            r5 = 0
        L_0x02ef:
            r0 = r5
            com.android.server.wm.ActivityTaskManagerService r1 = r14.mService
            com.android.server.wm.RootActivityContainer r2 = r14.mRootActivityContainer
            r8 = r73
            android.app.ActivityOptions r0 = com.android.server.wm.ActivityStarterInjector.modifyLaunchActivityOptionIfNeed(r1, r2, r8, r0, r9)
            com.android.server.wm.ActivityTaskManagerService r1 = r14.mService
            com.android.server.wm.ActivityStarterInjector.checkFreeformSupport(r1, r0)
            com.android.server.wm.RootActivityContainer r1 = r14.mRootActivityContainer
            com.android.server.wm.ActivityStarterInjector.initFreefomLaunchParameters(r0, r1)
            if (r86 == 0) goto L_0x0317
            com.android.server.wm.ActivityTaskManagerService r1 = r14.mService
            com.android.server.wm.ActivityStartController r1 = r1.getActivityStartController()
            com.android.server.wm.PendingRemoteAnimationRegistry r1 = r1.getPendingRemoteAnimationRegistry()
            android.app.ActivityOptions r0 = r1.overrideOptionsIfNeeded(r8, r0)
            r17 = r0
            goto L_0x0319
        L_0x0317:
            r17 = r0
        L_0x0319:
            com.android.server.wm.ActivityTaskManagerService r0 = r14.mService
            android.app.IActivityController r0 = r0.mController
            if (r0 == 0) goto L_0x033e
            android.content.Intent r0 = r65.cloneFilter()     // Catch:{ RemoteException -> 0x0337 }
            com.android.server.wm.ActivityTaskManagerService r1 = r14.mService     // Catch:{ RemoteException -> 0x0337 }
            android.app.IActivityController r1 = r1.mController     // Catch:{ RemoteException -> 0x0337 }
            android.content.pm.ApplicationInfo r2 = r15.applicationInfo     // Catch:{ RemoteException -> 0x0337 }
            java.lang.String r2 = r2.packageName     // Catch:{ RemoteException -> 0x0337 }
            boolean r1 = r1.activityStarting(r0, r2)     // Catch:{ RemoteException -> 0x0337 }
            r2 = 1
            r1 = r1 ^ r2
            r16 = r16 | r1
            r0 = r16
            r7 = 0
            goto L_0x0341
        L_0x0337:
            r0 = move-exception
            com.android.server.wm.ActivityTaskManagerService r1 = r14.mService
            r7 = 0
            r1.mController = r7
            goto L_0x033f
        L_0x033e:
            r7 = 0
        L_0x033f:
            r0 = r16
        L_0x0341:
            com.android.server.wm.ActivityStartInterceptor r1 = r14.mInterceptor
            r2 = r74
            r3 = r78
            r4 = r79
            r5 = r80
            r6 = r8
            r1.setStates(r2, r3, r4, r5, r6)
            com.android.server.wm.ActivityStartInterceptor r1 = r14.mInterceptor
            r2 = r65
            r3 = r69
            r4 = r68
            r5 = r67
            r6 = r85
            r7 = r31
            r51 = r8
            r8 = r20
            r52 = r9
            r9 = r17
            boolean r1 = r1.intercept(r2, r3, r4, r5, r6, r7, r8, r9)
            if (r1 == 0) goto L_0x0393
            com.android.server.wm.ActivityStartInterceptor r1 = r14.mInterceptor
            android.content.Intent r1 = r1.mIntent
            com.android.server.wm.ActivityStartInterceptor r2 = r14.mInterceptor
            android.content.pm.ResolveInfo r2 = r2.mRInfo
            com.android.server.wm.ActivityStartInterceptor r3 = r14.mInterceptor
            android.content.pm.ActivityInfo r3 = r3.mAInfo
            com.android.server.wm.ActivityStartInterceptor r4 = r14.mInterceptor
            java.lang.String r4 = r4.mResolvedType
            com.android.server.wm.ActivityStartInterceptor r5 = r14.mInterceptor
            com.android.server.wm.TaskRecord r5 = r5.mInTask
            com.android.server.wm.ActivityStartInterceptor r6 = r14.mInterceptor
            int r6 = r6.mCallingPid
            com.android.server.wm.ActivityStartInterceptor r7 = r14.mInterceptor
            int r7 = r7.mCallingUid
            com.android.server.wm.ActivityStartInterceptor r8 = r14.mInterceptor
            android.app.ActivityOptions r8 = r8.mActivityOptions
            r11 = r1
            r15 = r3
            r12 = r4
            r53 = r5
            r54 = r8
            goto L_0x039d
        L_0x0393:
            r2 = r69
            r53 = r85
            r54 = r17
            r7 = r20
            r6 = r31
        L_0x039d:
            if (r0 == 0) goto L_0x03b8
            if (r29 == 0) goto L_0x03b2
            r17 = -1
            r21 = 0
            r22 = 0
            r16 = r50
            r18 = r29
            r19 = r57
            r20 = r58
            r16.sendActivityResultLocked(r17, r18, r19, r20, r21, r22)
        L_0x03b2:
            android.app.ActivityOptions.abort(r54)
            r1 = 102(0x66, float:1.43E-43)
            return r1
        L_0x03b8:
            r5 = r61
            android.content.pm.ActivityInfo r1 = r5.newAInfo
            if (r1 == 0) goto L_0x03ca
            android.content.pm.ResolveInfo r2 = r5.newRInfo
            android.content.pm.ActivityInfo r15 = r5.newAInfo
            android.content.Intent r11 = r5.newIntent
            r12 = 0
            r1 = r79
            r6 = r78
            r7 = r1
        L_0x03ca:
            if (r15 == 0) goto L_0x0489
            com.android.server.wm.ActivityTaskManagerService r1 = r14.mService
            android.content.pm.PackageManagerInternal r1 = r1.getPackageManagerInternalLocked()
            java.lang.String r3 = r15.packageName
            r4 = r74
            boolean r1 = r1.isPermissionsReviewRequired(r3, r4)
            if (r1 == 0) goto L_0x047b
            com.android.server.wm.ActivityTaskManagerService r1 = r14.mService
            r17 = 2
            r21 = 0
            r22 = 0
            r23 = 0
            r3 = 1
            android.content.Intent[] r8 = new android.content.Intent[r3]
            r31 = 0
            r8[r31] = r11
            java.lang.String[] r9 = new java.lang.String[r3]
            r9[r31] = r12
            r26 = 1342177280(0x50000000, float:8.5899346E9)
            r27 = 0
            r16 = r1
            r18 = r51
            r19 = r7
            r20 = r4
            r24 = r8
            r25 = r9
            android.content.IIntentSender r1 = r16.getIntentSenderLocked(r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27)
            android.content.Intent r3 = new android.content.Intent
            java.lang.String r8 = "android.intent.action.REVIEW_PERMISSIONS"
            r3.<init>(r8)
            int r8 = r11.getFlags()
            r9 = 8388608(0x800000, float:1.17549435E-38)
            r8 = r8 | r9
            r9 = 268959744(0x10080000, float:2.682127E-29)
            r9 = r9 & r8
            if (r9 == 0) goto L_0x041d
            r9 = 134217728(0x8000000, float:3.85186E-34)
            r8 = r8 | r9
            r9 = r8
            goto L_0x041e
        L_0x041d:
            r9 = r8
        L_0x041e:
            r3.setFlags(r9)
            java.lang.String r8 = r15.packageName
            r73 = r0
            java.lang.String r0 = "android.intent.extra.PACKAGE_NAME"
            r3.putExtra(r0, r8)
            android.content.IntentSender r0 = new android.content.IntentSender
            r0.<init>(r1)
            java.lang.String r8 = "android.intent.extra.INTENT"
            r3.putExtra(r8, r0)
            if (r29 == 0) goto L_0x043d
            java.lang.String r0 = "android.intent.extra.RESULT_NEEDED"
            r8 = 1
            r3.putExtra(r0, r8)
            goto L_0x043e
        L_0x043d:
            r8 = 1
        L_0x043e:
            r0 = r3
            r12 = 0
            r11 = r79
            r16 = r78
            com.android.server.wm.ActivityStackSupervisor r6 = r14.mSupervisor
            r17 = 0
            com.android.server.wm.ActivityStarter$Request r7 = r14.mRequest
            int r7 = r7.filterCallingUid
            r10 = r79
            int r18 = computeResolveFilterUid(r11, r10, r7)
            r7 = r0
            r19 = r8
            r8 = r12
            r20 = r9
            r9 = r4
            r61 = r5
            r5 = r10
            r10 = r17
            r17 = r11
            r11 = r18
            android.content.pm.ResolveInfo r2 = r6.resolveIntent(r7, r8, r9, r10, r11)
            com.android.server.wm.ActivityStackSupervisor r6 = r14.mSupervisor
            r10 = r80
            r8 = 0
            android.content.pm.ActivityInfo r15 = r6.resolveActivity(r0, r2, r10, r8)
            r1 = r12
            r3 = r15
            r6 = r16
            r7 = r17
            r62 = r2
            r2 = r0
            r0 = r62
            goto L_0x049c
        L_0x047b:
            r10 = r80
            r73 = r0
            r61 = r5
            r8 = 0
            r19 = 1
            r31 = 0
            r5 = r79
            goto L_0x0498
        L_0x0489:
            r4 = r74
            r10 = r80
            r73 = r0
            r61 = r5
            r8 = 0
            r19 = 1
            r31 = 0
            r5 = r79
        L_0x0498:
            r0 = r2
            r2 = r11
            r1 = r12
            r3 = r15
        L_0x049c:
            if (r0 == 0) goto L_0x04ce
            android.content.pm.AuxiliaryResolveInfo r9 = r0.auxiliaryInfo
            if (r9 == 0) goto L_0x04ce
            android.content.pm.AuxiliaryResolveInfo r12 = r0.auxiliaryInfo
            r11 = r63
            r9 = r13
            r13 = r66
            r15 = r14
            r14 = r51
            r16 = r1
            r17 = r4
            r65 = r7
            r7 = r15
            r18 = r19
            r15 = r28
            android.content.Intent r2 = r11.createLaunchIntent(r12, r13, r14, r15, r16, r17)
            r1 = 0
            r11 = r79
            r6 = r78
            com.android.server.wm.ActivityStackSupervisor r12 = r7.mSupervisor
            android.content.pm.ActivityInfo r3 = r12.resolveActivity(r2, r0, r10, r8)
            r16 = r1
            r14 = r2
            r17 = r3
            r12 = r6
            r13 = r11
            goto L_0x04de
        L_0x04ce:
            r65 = r7
            r9 = r13
            r7 = r14
            r18 = r19
            r15 = r28
            r13 = r65
            r16 = r1
            r14 = r2
            r17 = r3
            r12 = r6
        L_0x04de:
            com.android.server.wm.ActivityRecord r1 = new com.android.server.wm.ActivityRecord
            com.android.server.wm.ActivityTaskManagerService r2 = r7.mService
            android.content.res.Configuration r40 = r2.getGlobalConfiguration()
            if (r70 == 0) goto L_0x04eb
            r45 = r18
            goto L_0x04ed
        L_0x04eb:
            r45 = 0
        L_0x04ed:
            com.android.server.wm.ActivityStackSupervisor r3 = r7.mSupervisor
            r46 = r3
            r31 = r1
            r32 = r2
            r33 = r52
            r34 = r12
            r35 = r13
            r36 = r51
            r37 = r14
            r38 = r16
            r39 = r17
            r41 = r29
            r42 = r57
            r43 = r58
            r44 = r83
            r47 = r54
            r48 = r9
            r31.<init>(r32, r33, r34, r35, r36, r37, r38, r39, r40, r41, r42, r43, r44, r45, r46, r47, r48)
            r11 = r1
            if (r84 == 0) goto L_0x0519
            r8 = 0
            r84[r8] = r11
            goto L_0x051a
        L_0x0519:
            r8 = 0
        L_0x051a:
            com.android.server.am.AppTimeTracker r1 = r11.appTimeTracker
            if (r1 != 0) goto L_0x0524
            if (r9 == 0) goto L_0x0524
            com.android.server.am.AppTimeTracker r1 = r9.appTimeTracker
            r11.appTimeTracker = r1
        L_0x0524:
            com.android.server.wm.RootActivityContainer r1 = r7.mRootActivityContainer
            com.android.server.wm.ActivityStack r18 = r1.getTopDisplayFocusedStack()
            if (r70 != 0) goto L_0x0585
            com.android.server.wm.ActivityRecord r1 = r18.getResumedActivity()
            if (r1 == 0) goto L_0x0546
            com.android.server.wm.ActivityRecord r1 = r18.getResumedActivity()
            android.content.pm.ActivityInfo r1 = r1.info
            android.content.pm.ApplicationInfo r1 = r1.applicationInfo
            int r1 = r1.uid
            if (r1 == r5) goto L_0x053f
            goto L_0x0546
        L_0x053f:
            r65 = r0
            r19 = r4
            r20 = r61
            goto L_0x058b
        L_0x0546:
            com.android.server.wm.ActivityTaskManagerService r1 = r7.mService
            java.lang.String r6 = "Activity start"
            r2 = r12
            r3 = r13
            r19 = r4
            r4 = r78
            r20 = r61
            r5 = r79
            boolean r1 = r1.checkAppSwitchAllowedLocked(r2, r3, r4, r5, r6)
            if (r1 != 0) goto L_0x0582
            if (r30 == 0) goto L_0x0566
            boolean r1 = r7.handleBackgroundActivityAbort(r11)
            if (r1 != 0) goto L_0x0563
            goto L_0x0566
        L_0x0563:
            r65 = r0
            goto L_0x057c
        L_0x0566:
            com.android.server.wm.ActivityStartController r8 = r7.mController
            com.android.server.wm.ActivityStackSupervisor$PendingActivityLaunch r6 = new com.android.server.wm.ActivityStackSupervisor$PendingActivityLaunch
            r1 = r6
            r2 = r11
            r3 = r9
            r4 = r80
            r5 = r18
            r65 = r0
            r0 = r6
            r6 = r52
            r1.<init>(r2, r3, r4, r5, r6)
            r8.addPendingActivityLaunch(r0)
        L_0x057c:
            android.app.ActivityOptions.abort(r54)
            r0 = 100
            return r0
        L_0x0582:
            r65 = r0
            goto L_0x058b
        L_0x0585:
            r65 = r0
            r19 = r4
            r20 = r61
        L_0x058b:
            com.android.server.wm.ActivityTaskManagerService r0 = r7.mService
            r0.onStartActivitySetDidAppSwitch()
            com.android.server.wm.ActivityStartController r0 = r7.mController
            r0.doPendingActivityLaunches(r8)
            r0 = 1
            r1 = r63
            r2 = r11
            r3 = r9
            r4 = r70
            r5 = r71
            r6 = r80
            r67 = r12
            r12 = r7
            r7 = r0
            r21 = r8
            r8 = r54
            r22 = r9
            r9 = r53
            r10 = r84
            r0 = r11
            r11 = r30
            int r1 = r1.startActivity(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            com.android.server.wm.ActivityStackSupervisor r2 = r12.mSupervisor
            com.android.server.wm.ActivityMetricsLogger r2 = r2.getActivityMetricsLogger()
            r3 = r84[r21]
            r2.notifyActivityLaunched(r1, r3)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStarter.startActivity(android.app.IApplicationThread, android.content.Intent, android.content.Intent, java.lang.String, android.content.pm.ActivityInfo, android.content.pm.ResolveInfo, android.service.voice.IVoiceInteractionSession, com.android.internal.app.IVoiceInteractor, android.os.IBinder, java.lang.String, int, int, int, java.lang.String, int, int, int, com.android.server.wm.SafeActivityOptions, boolean, boolean, com.android.server.wm.ActivityRecord[], com.android.server.wm.TaskRecord, boolean, com.android.server.am.PendingIntentRecord, boolean):int");
    }

    /* access modifiers changed from: package-private */
    public boolean shouldAbortBackgroundActivityStart(int callingUid, int callingPid, String callingPackage, int realCallingUid, int realCallingPid, WindowProcessController callerApp, PendingIntentRecord originatingPendingIntent, boolean allowBackgroundActivityStart, Intent intent) {
        int realCallingUidProcState;
        boolean z;
        boolean z2;
        boolean isRealCallingUidPersistentSystemProcess;
        int realCallingUidProcState2;
        boolean z3;
        int callerAppUid;
        WindowProcessController callerApp2;
        int callerAppUid2;
        boolean z4;
        int i = callingUid;
        int i2 = callingPid;
        String str = callingPackage;
        int i3 = realCallingUid;
        PendingIntentRecord pendingIntentRecord = originatingPendingIntent;
        boolean z5 = allowBackgroundActivityStart;
        Intent intent2 = intent;
        int callingAppId = UserHandle.getAppId(callingUid);
        if (i == 0 || callingAppId == 1000) {
            return false;
        } else if (callingAppId == 1027) {
            int i4 = callingAppId;
            return false;
        } else {
            int callingUidProcState = this.mService.getUidState(i);
            boolean callingUidHasAnyVisibleWindow = this.mService.mWindowManager.mRoot.isAnyNonToastWindowVisibleForUid(i);
            boolean isCallingUidForeground = callingUidHasAnyVisibleWindow || callingUidProcState == 2 || callingUidProcState == 4;
            boolean isCallingUidPersistentSystemProcess = callingUidProcState <= 1;
            if (callingUidHasAnyVisibleWindow) {
                boolean z6 = isCallingUidForeground;
                int i5 = callingUidProcState;
                int i6 = callingAppId;
                return false;
            } else if (isCallingUidPersistentSystemProcess) {
                boolean z7 = isCallingUidPersistentSystemProcess;
                boolean z8 = isCallingUidForeground;
                int i7 = callingUidProcState;
                int i8 = callingAppId;
                return false;
            } else {
                if (i == i3) {
                    realCallingUidProcState = callingUidProcState;
                } else {
                    realCallingUidProcState = this.mService.getUidState(i3);
                }
                if (i == i3) {
                    z = callingUidHasAnyVisibleWindow;
                } else {
                    z = this.mService.mWindowManager.mRoot.isAnyNonToastWindowVisibleForUid(i3);
                }
                boolean realCallingUidHasAnyVisibleWindow = z;
                if (i == i3) {
                    z2 = isCallingUidForeground;
                } else {
                    z2 = realCallingUidHasAnyVisibleWindow || realCallingUidProcState == 2;
                }
                boolean isRealCallingUidForeground = z2;
                int realCallingAppId = UserHandle.getAppId(realCallingUid);
                if (i == i3) {
                    isRealCallingUidPersistentSystemProcess = isCallingUidPersistentSystemProcess;
                } else {
                    isRealCallingUidPersistentSystemProcess = realCallingAppId == 1000 || realCallingUidProcState <= 1;
                }
                if (i3 == i) {
                    realCallingUidProcState2 = realCallingUidProcState;
                    int i9 = realCallingAppId;
                    z3 = false;
                } else if (realCallingUidHasAnyVisibleWindow) {
                    return false;
                } else {
                    z3 = false;
                    if (isRealCallingUidPersistentSystemProcess && z5) {
                        return false;
                    }
                    realCallingUidProcState2 = realCallingUidProcState;
                    int i10 = realCallingAppId;
                    if (this.mService.isAssociatedCompanionApp(UserHandle.getUserId(realCallingUid), i3)) {
                        return false;
                    }
                }
                ActivityTaskManagerService activityTaskManagerService = this.mService;
                if (ActivityTaskManagerService.checkPermission("android.permission.START_ACTIVITIES_FROM_BACKGROUND", i2, i) == 0 || this.mSupervisor.mRecentTasks.isCallerRecents(i) || this.mService.isDeviceOwner(i)) {
                    return z3;
                }
                int callingUserId = UserHandle.getUserId(callingUid);
                if (this.mService.isAssociatedCompanionApp(callingUserId, i)) {
                    return z3;
                }
                int callerAppUid3 = callingUid;
                if (callerApp == null) {
                    int i11 = callerAppUid3;
                    int i12 = callingAppId;
                    callerApp2 = this.mService.getProcessController(realCallingPid, i3);
                    callerAppUid = realCallingUid;
                } else {
                    int callerAppUid4 = callerAppUid3;
                    int i13 = callingAppId;
                    int callingAppId2 = realCallingPid;
                    callerApp2 = callerApp;
                    callerAppUid = callerAppUid4;
                }
                if (callerApp2 == null) {
                    callerAppUid2 = callerAppUid;
                    z4 = true;
                } else if (callerApp2.areBackgroundActivityStartsAllowed()) {
                    return false;
                } else {
                    int i14 = callingUserId;
                    ArraySet<WindowProcessController> uidProcesses = this.mService.mProcessMap.getProcesses(callerAppUid);
                    if (uidProcesses != null) {
                        z4 = true;
                        callerAppUid2 = callerAppUid;
                        int i15 = uidProcesses.size() - 1;
                        while (i15 >= 0) {
                            ArraySet<WindowProcessController> uidProcesses2 = uidProcesses;
                            WindowProcessController proc = uidProcesses.valueAt(i15);
                            if (proc != callerApp2 && proc.areBackgroundActivityStartsAllowed()) {
                                return false;
                            }
                            i15--;
                            uidProcesses = uidProcesses2;
                        }
                    } else {
                        callerAppUid2 = callerAppUid;
                        z4 = true;
                    }
                }
                if (this.mService.hasSystemAlertWindowPermission(i, i2, str)) {
                    Slog.w("ActivityTaskManager", "Background activity start for " + str + " allowed because SYSTEM_ALERT_WINDOW permission is granted.");
                    return false;
                } else if (ActivityTaskManagerServiceInjector.isAllowedBackgroundStart(this.mService, intent2, str, i)) {
                    return false;
                } else {
                    Slog.w("ActivityTaskManager", "Background activity start [callingPackage: " + str + "; callingUid: " + i + "; isCallingUidForeground: " + isCallingUidForeground + "; isCallingUidPersistentSystemProcess: " + isCallingUidPersistentSystemProcess + "; realCallingUid: " + i3 + "; isRealCallingUidForeground: " + isRealCallingUidForeground + "; isRealCallingUidPersistentSystemProcess: " + isRealCallingUidPersistentSystemProcess + "; originatingPendingIntent: " + pendingIntentRecord + "; isBgStartWhitelisted: " + z5 + "; intent: " + intent2 + "; callerApp: " + callerApp2 + "]");
                    if (this.mService.isActivityStartsLoggingEnabled()) {
                        ActivityMetricsLogger activityMetricsLogger = this.mSupervisor.getActivityMetricsLogger();
                        boolean z9 = pendingIntentRecord != null ? z4 : false;
                        boolean z10 = isRealCallingUidPersistentSystemProcess;
                        boolean z11 = isCallingUidPersistentSystemProcess;
                        boolean z12 = isCallingUidForeground;
                        boolean z13 = z4;
                        boolean z14 = isRealCallingUidForeground;
                        int i16 = callingUidProcState;
                        int i17 = callerAppUid2;
                        activityMetricsLogger.logAbortedBgActivityStart(intent, callerApp2, callingUid, callingPackage, callingUidProcState, callingUidHasAnyVisibleWindow, realCallingUid, realCallingUidProcState2, realCallingUidHasAnyVisibleWindow, z9);
                        return z13;
                    }
                    boolean z15 = isRealCallingUidPersistentSystemProcess;
                    boolean z16 = isCallingUidPersistentSystemProcess;
                    boolean z17 = isCallingUidForeground;
                    int i18 = callingUidProcState;
                    boolean z18 = z4;
                    int i19 = realCallingUidProcState2;
                    WindowProcessController windowProcessController = callerApp2;
                    boolean z19 = isRealCallingUidForeground;
                    return z18;
                }
            }
        }
    }

    private Intent createLaunchIntent(AuxiliaryResolveInfo auxiliaryResponse, Intent originalIntent, String callingPackage, Bundle verificationBundle, String resolvedType, int userId) {
        AuxiliaryResolveInfo auxiliaryResolveInfo = auxiliaryResponse;
        if (auxiliaryResolveInfo != null && auxiliaryResolveInfo.needsPhaseTwo) {
            this.mService.getPackageManagerInternalLocked().requestInstantAppResolutionPhaseTwo(auxiliaryResponse, originalIntent, resolvedType, callingPackage, verificationBundle, userId);
        }
        Intent sanitizeIntent = InstantAppResolver.sanitizeIntent(originalIntent);
        List list = null;
        Intent intent = auxiliaryResolveInfo == null ? null : auxiliaryResolveInfo.failureIntent;
        ComponentName componentName = auxiliaryResolveInfo == null ? null : auxiliaryResolveInfo.installFailureActivity;
        String str = auxiliaryResolveInfo == null ? null : auxiliaryResolveInfo.token;
        boolean z = auxiliaryResolveInfo != null && auxiliaryResolveInfo.needsPhaseTwo;
        if (auxiliaryResolveInfo != null) {
            list = auxiliaryResolveInfo.filters;
        }
        return InstantAppResolver.buildEphemeralInstallerIntent(originalIntent, sanitizeIntent, intent, callingPackage, verificationBundle, resolvedType, userId, componentName, str, z, list);
    }

    /* access modifiers changed from: package-private */
    public void postStartActivityProcessing(ActivityRecord r, int result, ActivityStack startedActivityStack) {
        ActivityStack homeStack;
        if (!ActivityManager.isStartResultFatalError(result)) {
            this.mSupervisor.reportWaitingActivityLaunchedIfNeeded(r, result);
            if (startedActivityStack != null) {
                boolean clearedTask = (this.mLaunchFlags & 268468224) == 268468224 && this.mReuseTask != null;
                if (result == 2 || result == 3 || clearedTask) {
                    int windowingMode = startedActivityStack.getWindowingMode();
                    if (windowingMode == 2) {
                        this.mService.getTaskChangeNotificationController().notifyPinnedActivityRestartAttempt(clearedTask);
                    } else if (windowingMode == 3 && (homeStack = startedActivityStack.getDisplay().getHomeStack()) != null && homeStack.shouldBeVisible((ActivityRecord) null) && !MiuiGestureController.isActivityDummyVisible(homeStack.getTopActivity())) {
                        this.mService.mWindowManager.showRecentApps();
                    }
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v0, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v11, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v52, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v57, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v4, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r3v7, types: [boolean] */
    /* JADX WARNING: type inference failed for: r3v9 */
    /* JADX WARNING: type inference failed for: r3v12 */
    /* JADX WARNING: type inference failed for: r0v60 */
    /* JADX WARNING: type inference failed for: r0v61 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r0v5, types: [boolean, int] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x024e A[SYNTHETIC, Splitter:B:103:0x024e] */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x04cb  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x04e9 A[Catch:{ all -> 0x04e4, all -> 0x057c }] */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x04fd A[SYNTHETIC, Splitter:B:199:0x04fd] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0127  */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:230:0x0564=Splitter:B:230:0x0564, B:220:0x054a=Splitter:B:220:0x054a} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int startActivityMayWait(android.app.IApplicationThread r44, int r45, java.lang.String r46, int r47, int r48, android.content.Intent r49, java.lang.String r50, android.service.voice.IVoiceInteractionSession r51, com.android.internal.app.IVoiceInteractor r52, android.os.IBinder r53, java.lang.String r54, int r55, int r56, android.app.ProfilerInfo r57, android.app.WaitResult r58, android.content.res.Configuration r59, com.android.server.wm.SafeActivityOptions r60, boolean r61, int r62, com.android.server.wm.TaskRecord r63, java.lang.String r64, boolean r65, com.android.server.am.PendingIntentRecord r66, boolean r67) {
        /*
            r43 = this;
            r15 = r43
            r1 = r44
            r0 = r49
            r14 = r57
            r3 = r58
            r2 = r59
            r13 = r62
            if (r0 == 0) goto L_0x001f
            boolean r4 = r49.hasFileDescriptors()
            if (r4 != 0) goto L_0x0017
            goto L_0x001f
        L_0x0017:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.String r5 = "File descriptors passed in Intent"
            r4.<init>(r5)
            throw r4
        L_0x001f:
            com.android.server.wm.ActivityStackSupervisor r4 = r15.mSupervisor
            com.android.server.wm.ActivityMetricsLogger r4 = r4.getActivityMetricsLogger()
            r4.notifyActivityLaunching(r0)
            android.content.ComponentName r4 = r49.getComponent()
            if (r4 == 0) goto L_0x0030
            r4 = 1
            goto L_0x0031
        L_0x0030:
            r4 = 0
        L_0x0031:
            r5 = r4
            if (r47 == 0) goto L_0x0037
            r16 = r47
            goto L_0x003d
        L_0x0037:
            int r4 = android.os.Binder.getCallingPid()
            r16 = r4
        L_0x003d:
            r4 = -1
            r10 = r48
            if (r10 == r4) goto L_0x0045
            r6 = r10
            goto L_0x0049
        L_0x0045:
            int r6 = android.os.Binder.getCallingUid()
        L_0x0049:
            r9 = r6
            if (r45 < 0) goto L_0x0051
            r4 = -1
            r8 = r45
            r7 = r4
            goto L_0x005c
        L_0x0051:
            if (r1 != 0) goto L_0x0059
            r4 = r16
            r6 = r9
            r7 = r4
            r8 = r6
            goto L_0x005c
        L_0x0059:
            r6 = r4
            r7 = r4
            r8 = r6
        L_0x005c:
            r6 = r46
            android.content.Intent r0 = com.android.server.wm.ActivityTaskManagerServiceInjector.hookStartActivity(r0, r6)
            android.content.Intent r4 = new android.content.Intent
            r4.<init>(r0)
            android.content.Intent r11 = new android.content.Intent
            r11.<init>(r0)
            if (r5 == 0) goto L_0x00b0
            java.lang.String r12 = r11.getAction()
            java.lang.String r0 = "android.intent.action.VIEW"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x0080
            android.net.Uri r0 = r11.getData()
            if (r0 == 0) goto L_0x00b0
        L_0x0080:
            java.lang.String r0 = r11.getAction()
            java.lang.String r12 = "android.intent.action.INSTALL_INSTANT_APP_PACKAGE"
            boolean r0 = r12.equals(r0)
            if (r0 != 0) goto L_0x00b0
            java.lang.String r0 = r11.getAction()
            java.lang.String r12 = "android.intent.action.RESOLVE_INSTANT_APP_PACKAGE"
            boolean r0 = r12.equals(r0)
            if (r0 != 0) goto L_0x00b0
            com.android.server.wm.ActivityTaskManagerService r0 = r15.mService
            android.content.pm.PackageManagerInternal r0 = r0.getPackageManagerInternalLocked()
            android.content.ComponentName r12 = r11.getComponent()
            boolean r0 = r0.isInstantAppInstallerComponent(r12)
            if (r0 == 0) goto L_0x00b0
            r0 = 0
            r11.setComponent(r0)
            r5 = 0
            r30 = r5
            goto L_0x00b2
        L_0x00b0:
            r30 = r5
        L_0x00b2:
            com.android.server.wm.ActivityStackSupervisor r5 = r15.mSupervisor
            r0 = 0
            com.android.server.wm.ActivityStarter$Request r12 = r15.mRequest
            int r12 = r12.filterCallingUid
            int r12 = computeResolveFilterUid(r8, r9, r12)
            r6 = r11
            r31 = r7
            r7 = r50
            r3 = r8
            r8 = r62
            r49 = r4
            r4 = r9
            r9 = r0
            r10 = r12
            android.content.pm.ResolveInfo r12 = r5.resolveIntent(r6, r7, r8, r9, r10)
            if (r12 != 0) goto L_0x0131
            com.android.server.wm.ActivityStackSupervisor r0 = r15.mSupervisor
            android.content.pm.UserInfo r19 = r0.getUserInfo(r13)
            if (r19 == 0) goto L_0x0131
            boolean r0 = r19.isManagedProfile()
            if (r0 == 0) goto L_0x0131
            com.android.server.wm.ActivityTaskManagerService r0 = r15.mService
            android.content.Context r0 = r0.mContext
            android.os.UserManager r10 = android.os.UserManager.get(r0)
            r5 = 0
            long r20 = android.os.Binder.clearCallingIdentity()
            android.content.pm.UserInfo r0 = r10.getProfileParent(r13)     // Catch:{ all -> 0x012a }
            if (r0 == 0) goto L_0x0105
            int r6 = r0.id     // Catch:{ all -> 0x0101 }
            boolean r6 = r10.isUserUnlockingOrUnlocked(r6)     // Catch:{ all -> 0x0101 }
            if (r6 == 0) goto L_0x0105
            boolean r6 = r10.isUserUnlockingOrUnlocked(r13)     // Catch:{ all -> 0x0101 }
            if (r6 != 0) goto L_0x0105
            r6 = 1
            goto L_0x0106
        L_0x0101:
            r0 = move-exception
            r23 = r10
            goto L_0x012d
        L_0x0105:
            r6 = 0
        L_0x0106:
            r0 = r6
            android.os.Binder.restoreCallingIdentity(r20)
            if (r0 == 0) goto L_0x0127
            com.android.server.wm.ActivityStackSupervisor r5 = r15.mSupervisor
            r9 = 786432(0xc0000, float:1.102026E-39)
            com.android.server.wm.ActivityStarter$Request r6 = r15.mRequest
            int r6 = r6.filterCallingUid
            int r22 = computeResolveFilterUid(r3, r4, r6)
            r6 = r11
            r7 = r50
            r8 = r62
            r23 = r10
            r10 = r22
            android.content.pm.ResolveInfo r12 = r5.resolveIntent(r6, r7, r8, r9, r10)
            goto L_0x0131
        L_0x0127:
            r23 = r10
            goto L_0x0131
        L_0x012a:
            r0 = move-exception
            r23 = r10
        L_0x012d:
            android.os.Binder.restoreCallingIdentity(r20)
            throw r0
        L_0x0131:
            com.android.server.wm.ActivityStackSupervisor r0 = r15.mSupervisor
            r10 = r56
            android.content.pm.ActivityInfo r0 = r0.resolveActivity(r11, r12, r10, r14)
            if (r0 != 0) goto L_0x0151
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "aInfo is null for resolve intent: "
            r5.append(r6)
            r5.append(r11)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = "ActivityTaskManager"
            android.util.Slog.w(r6, r5)
        L_0x0151:
            int r9 = r11.getMiuiFlags()
            boolean r5 = com.android.server.wm.ActivityStackSupervisorInjector.isXSpaceActive()
            if (r5 == 0) goto L_0x019c
            com.android.server.wm.ActivityTaskManagerService r5 = r15.mService
            android.content.Context r5 = r5.mContext
            if (r53 == 0) goto L_0x0163
            r8 = 1
            goto L_0x0164
        L_0x0163:
            r8 = 0
        L_0x0164:
            r6 = r0
            r7 = r11
            r1 = r9
            r9 = r55
            r10 = r62
            r17 = r11
            r11 = r46
            android.content.Intent r17 = com.android.server.wm.ActivityStackSupervisorInjector.checkXSpaceControl(r5, r6, r7, r8, r9, r10, r11)
            com.android.server.wm.ActivityStackSupervisor r7 = r15.mSupervisor
            com.android.server.wm.ActivityStarter$Request r5 = r15.mRequest
            int r5 = r5.filterCallingUid
            int r19 = computeResolveFilterUid(r3, r4, r5)
            r5 = r0
            r6 = r17
            r8 = r57
            r9 = r50
            r10 = r56
            r11 = r62
            r18 = r0
            r33 = r12
            r0 = 1
            r12 = r46
            r34 = r4
            r4 = r13
            r13 = r19
            android.content.pm.ActivityInfo r5 = com.android.server.wm.ActivityStackSupervisorInjector.resolveXSpaceIntent(r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r11 = r5
            r12 = r17
            goto L_0x01ab
        L_0x019c:
            r18 = r0
            r34 = r4
            r1 = r9
            r17 = r11
            r33 = r12
            r4 = r13
            r0 = 1
            r12 = r17
            r11 = r18
        L_0x01ab:
            com.android.server.wm.ActivityTaskManagerService r5 = r15.mService
            r6 = r44
            r7 = r11
            r8 = r12
            r9 = r62
            r10 = r46
            boolean r5 = com.android.server.wm.ActivityTaskManagerServiceInjector.checkRunningCompatibility(r5, r6, r7, r8, r9, r10)
            if (r5 != 0) goto L_0x01bd
            r0 = 5
            return r0
        L_0x01bd:
            com.android.server.wm.ActivityTaskManagerService r5 = r15.mService
            android.content.Context r5 = r5.mContext
            com.android.server.wm.ActivityTaskManagerService r6 = r15.mService
            if (r53 == 0) goto L_0x01c8
            r23 = r0
            goto L_0x01ca
        L_0x01c8:
            r23 = 0
        L_0x01ca:
            if (r60 != 0) goto L_0x01cf
            r29 = 0
            goto L_0x01d5
        L_0x01cf:
            android.os.Bundle r7 = r60.getActivityOptionsBundle()
            r29 = r7
        L_0x01d5:
            r17 = r5
            r18 = r6
            r19 = r44
            r20 = r11
            r21 = r12
            r22 = r50
            r24 = r55
            r25 = r61
            r26 = r62
            r27 = r3
            r28 = r46
            android.content.Intent r12 = com.android.server.wm.ActivityTaskManagerServiceInjector.checkStartActivityPermission(r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29)
            com.android.server.wm.ActivityStackSupervisor r5 = r15.mSupervisor
            android.content.pm.ActivityInfo r11 = com.android.server.wm.ActivityTaskManagerServiceInjector.resolveCheckIntent(r11, r12, r5, r14, r4)
            com.android.server.wm.ActivityStackSupervisor r9 = r15.mSupervisor
            com.android.server.wm.ActivityTaskManagerService r10 = r15.mService
            r5 = r12
            r6 = r11
            r7 = r60
            r8 = r44
            android.content.Intent r5 = com.android.server.wm.ActivityTaskManagerServiceInjector.requestSplashScreen(r5, r6, r7, r8, r9, r10)
            com.android.server.wm.ActivityStackSupervisor r6 = r15.mSupervisor
            android.content.pm.ActivityInfo r11 = com.android.server.wm.ActivityTaskManagerServiceInjector.resolveSplashIntent(r11, r5, r6, r14, r4)
            r5.setMiuiFlags(r1)
            com.android.server.wm.ActivityTaskManagerService r6 = r15.mService
            com.android.server.wm.WindowManagerGlobalLock r13 = r6.mGlobalLock
            monitor-enter(r13)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x05a4 }
            com.android.server.wm.RootActivityContainer r6 = r15.mRootActivityContainer     // Catch:{ all -> 0x05a4 }
            com.android.server.wm.ActivityStack r6 = r6.getTopDisplayFocusedStack()     // Catch:{ all -> 0x05a4 }
            r12 = r6
            if (r2 == 0) goto L_0x0242
            com.android.server.wm.ActivityTaskManagerService r6 = r15.mService     // Catch:{ all -> 0x022b }
            android.content.res.Configuration r6 = r6.getGlobalConfiguration()     // Catch:{ all -> 0x022b }
            int r6 = r6.diff(r2)     // Catch:{ all -> 0x022b }
            if (r6 == 0) goto L_0x0242
            r6 = r0
            goto L_0x0243
        L_0x022b:
            r0 = move-exception
            r4 = r49
            r37 = r50
            r8 = r58
            r29 = r1
            r7 = r2
            r41 = r13
            r6 = r15
            r40 = r30
            r1 = r31
            r12 = r33
            r30 = r44
            goto L_0x05b9
        L_0x0242:
            r6 = 0
        L_0x0243:
            r12.mConfigWillChange = r6     // Catch:{ all -> 0x05a4 }
            long r6 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x05a4 }
            r35 = r6
            r10 = 2
            if (r11 == 0) goto L_0x0454
            android.content.pm.ApplicationInfo r6 = r11.applicationInfo     // Catch:{ all -> 0x043d }
            int r6 = r6.privateFlags     // Catch:{ all -> 0x043d }
            r6 = r6 & r10
            if (r6 == 0) goto L_0x0454
            com.android.server.wm.ActivityTaskManagerService r6 = r15.mService     // Catch:{ all -> 0x043d }
            boolean r6 = r6.mHasHeavyWeightFeature     // Catch:{ all -> 0x043d }
            if (r6 == 0) goto L_0x0454
            java.lang.String r6 = r11.processName     // Catch:{ all -> 0x043d }
            android.content.pm.ApplicationInfo r7 = r11.applicationInfo     // Catch:{ all -> 0x043d }
            java.lang.String r7 = r7.packageName     // Catch:{ all -> 0x043d }
            boolean r6 = r6.equals(r7)     // Catch:{ all -> 0x043d }
            if (r6 == 0) goto L_0x0438
            com.android.server.wm.ActivityTaskManagerService r6 = r15.mService     // Catch:{ all -> 0x043d }
            com.android.server.wm.WindowProcessController r6 = r6.mHeavyWeightProcess     // Catch:{ all -> 0x043d }
            r9 = r6
            if (r9 == 0) goto L_0x0431
            android.content.pm.ApplicationInfo r6 = r9.mInfo     // Catch:{ all -> 0x043d }
            int r6 = r6.uid     // Catch:{ all -> 0x043d }
            android.content.pm.ApplicationInfo r7 = r11.applicationInfo     // Catch:{ all -> 0x043d }
            int r7 = r7.uid     // Catch:{ all -> 0x043d }
            if (r6 != r7) goto L_0x0289
            java.lang.String r6 = r9.mName     // Catch:{ all -> 0x022b }
            java.lang.String r7 = r11.processName     // Catch:{ all -> 0x022b }
            boolean r6 = r6.equals(r7)     // Catch:{ all -> 0x022b }
            if (r6 != 0) goto L_0x0283
            goto L_0x0289
        L_0x0283:
            r29 = r1
            r1 = r31
            goto L_0x0458
        L_0x0289:
            r6 = r3
            r29 = r1
            r1 = r44
            if (r1 == 0) goto L_0x0318
            com.android.server.wm.ActivityTaskManagerService r7 = r15.mService     // Catch:{ all -> 0x0302 }
            com.android.server.wm.WindowProcessController r7 = r7.getProcessController(r1)     // Catch:{ all -> 0x0302 }
            if (r7 == 0) goto L_0x02b8
            android.content.pm.ApplicationInfo r8 = r7.mInfo     // Catch:{ all -> 0x02a3 }
            int r8 = r8.uid     // Catch:{ all -> 0x02a3 }
            r6 = r8
            r1 = r31
            r31 = r6
            goto L_0x031c
        L_0x02a3:
            r0 = move-exception
            r4 = r49
            r37 = r50
            r8 = r58
            r7 = r2
            r41 = r13
            r6 = r15
            r40 = r30
            r12 = r33
            r30 = r1
            r1 = r31
            goto L_0x05b9
        L_0x02b8:
            java.lang.String r0 = "ActivityTaskManager"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0302 }
            r8.<init>()     // Catch:{ all -> 0x0302 }
            java.lang.String r10 = "Unable to find app for caller "
            r8.append(r10)     // Catch:{ all -> 0x0302 }
            r8.append(r1)     // Catch:{ all -> 0x0302 }
            java.lang.String r10 = " (pid="
            r8.append(r10)     // Catch:{ all -> 0x0302 }
            r10 = r31
            r8.append(r10)     // Catch:{ all -> 0x02ee }
            java.lang.String r1 = ") when starting: "
            r8.append(r1)     // Catch:{ all -> 0x02ee }
            java.lang.String r1 = r5.toString()     // Catch:{ all -> 0x02ee }
            r8.append(r1)     // Catch:{ all -> 0x02ee }
            java.lang.String r1 = r8.toString()     // Catch:{ all -> 0x02ee }
            android.util.Slog.w(r0, r1)     // Catch:{ all -> 0x02ee }
            com.android.server.wm.SafeActivityOptions.abort(r60)     // Catch:{ all -> 0x02ee }
            r0 = -94
            monitor-exit(r13)     // Catch:{ all -> 0x02ee }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r0
        L_0x02ee:
            r0 = move-exception
            r4 = r49
            r37 = r50
            r8 = r58
            r7 = r2
            r1 = r10
            r41 = r13
            r6 = r15
            r40 = r30
            r12 = r33
            r30 = r44
            goto L_0x05b9
        L_0x0302:
            r0 = move-exception
            r10 = r31
            r4 = r49
            r37 = r50
            r8 = r58
            r7 = r2
            r1 = r10
            r41 = r13
            r6 = r15
            r40 = r30
            r12 = r33
            r30 = r44
            goto L_0x05b9
        L_0x0318:
            r1 = r31
            r31 = r6
        L_0x031c:
            com.android.server.wm.ActivityTaskManagerService r6 = r15.mService     // Catch:{ all -> 0x041e }
            r18 = 2
            java.lang.String r19 = "android"
            r22 = 0
            r23 = 0
            r24 = 0
            android.content.Intent[] r7 = new android.content.Intent[r0]     // Catch:{ all -> 0x041e }
            r8 = 0
            r7[r8] = r5     // Catch:{ all -> 0x041e }
            java.lang.String[] r10 = new java.lang.String[r0]     // Catch:{ all -> 0x041e }
            r10[r8] = r50     // Catch:{ all -> 0x041e }
            r27 = 1342177280(0x50000000, float:8.5899346E9)
            r28 = 0
            r17 = r6
            r20 = r31
            r21 = r62
            r25 = r7
            r26 = r10
            android.content.IIntentSender r6 = r17.getIntentSenderLocked(r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28)     // Catch:{ all -> 0x041e }
            r10 = r6
            android.content.Intent r6 = new android.content.Intent     // Catch:{ all -> 0x041e }
            r6.<init>()     // Catch:{ all -> 0x041e }
            r7 = r6
            if (r55 < 0) goto L_0x0351
            java.lang.String r6 = "has_result"
            r7.putExtra(r6, r0)     // Catch:{ all -> 0x041e }
        L_0x0351:
            java.lang.String r6 = "intent"
            android.content.IntentSender r8 = new android.content.IntentSender     // Catch:{ all -> 0x041e }
            r8.<init>(r10)     // Catch:{ all -> 0x041e }
            r7.putExtra(r6, r8)     // Catch:{ all -> 0x041e }
            r9.updateIntentForHeavyWeightActivity(r7)     // Catch:{ all -> 0x041e }
            java.lang.String r6 = "new_app"
            java.lang.String r8 = r11.packageName     // Catch:{ all -> 0x041e }
            r7.putExtra(r6, r8)     // Catch:{ all -> 0x041e }
            int r6 = r5.getFlags()     // Catch:{ all -> 0x041e }
            r7.setFlags(r6)     // Catch:{ all -> 0x041e }
            java.lang.String r6 = "android"
            java.lang.Class<com.android.internal.app.HeavyWeightSwitcherActivity> r8 = com.android.internal.app.HeavyWeightSwitcherActivity.class
            java.lang.String r8 = r8.getName()     // Catch:{ all -> 0x041e }
            r7.setClassName(r6, r8)     // Catch:{ all -> 0x041e }
            r6 = r7
            r18 = 0
            r19 = 0
            int r5 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x040a }
            r3 = r5
            int r5 = android.os.Binder.getCallingPid()     // Catch:{ all -> 0x040a }
            r1 = r5
            r30 = 1
            com.android.server.wm.ActivityStackSupervisor r5 = r15.mSupervisor     // Catch:{ all -> 0x040a }
            r8 = 0
            r20 = 0
            com.android.server.wm.ActivityStarter$Request r0 = r15.mRequest     // Catch:{ all -> 0x040a }
            int r0 = r0.filterCallingUid     // Catch:{ all -> 0x040a }
            r21 = r9
            r9 = r34
            int r0 = computeResolveFilterUid(r3, r9, r0)     // Catch:{ all -> 0x03f4 }
            r22 = r7
            r7 = r8
            r8 = r62
            r34 = r9
            r17 = r21
            r9 = r20
            r20 = r10
            r10 = r0
            android.content.pm.ResolveInfo r0 = r5.resolveIntent(r6, r7, r8, r9, r10)     // Catch:{ all -> 0x040a }
            r5 = r0
            if (r5 == 0) goto L_0x03c4
            android.content.pm.ActivityInfo r0 = r5.activityInfo     // Catch:{ all -> 0x03b1 }
            goto L_0x03c5
        L_0x03b1:
            r0 = move-exception
            r4 = r49
            r8 = r58
            r7 = r2
            r12 = r5
            r5 = r6
            r41 = r13
            r6 = r15
            r37 = r18
            r40 = r30
            r30 = r19
            goto L_0x05b9
        L_0x03c4:
            r0 = 0
        L_0x03c5:
            r11 = r0
            if (r11 == 0) goto L_0x03e2
            com.android.server.wm.ActivityTaskManagerService r0 = r15.mService     // Catch:{ all -> 0x03b1 }
            android.app.ActivityManagerInternal r0 = r0.mAmInternal     // Catch:{ all -> 0x03b1 }
            android.content.pm.ActivityInfo r0 = r0.getActivityInfoForUser(r11, r4)     // Catch:{ all -> 0x03b1 }
            r33 = r0
            r39 = r1
            r31 = r3
            r38 = r5
            r32 = r6
            r37 = r18
            r40 = r30
            r30 = r19
            goto L_0x0468
        L_0x03e2:
            r39 = r1
            r31 = r3
            r38 = r5
            r32 = r6
            r33 = r11
            r37 = r18
            r40 = r30
            r30 = r19
            goto L_0x0468
        L_0x03f4:
            r0 = move-exception
            r4 = r49
            r8 = r58
            r7 = r2
            r5 = r6
            r34 = r9
            r41 = r13
            r6 = r15
            r37 = r18
            r40 = r30
            r12 = r33
            r30 = r19
            goto L_0x05b9
        L_0x040a:
            r0 = move-exception
            r4 = r49
            r8 = r58
            r7 = r2
            r5 = r6
            r41 = r13
            r6 = r15
            r37 = r18
            r40 = r30
            r12 = r33
            r30 = r19
            goto L_0x05b9
        L_0x041e:
            r0 = move-exception
            r4 = r49
            r37 = r50
            r8 = r58
            r7 = r2
            r41 = r13
            r6 = r15
            r40 = r30
            r12 = r33
            r30 = r44
            goto L_0x05b9
        L_0x0431:
            r29 = r1
            r17 = r9
            r1 = r31
            goto L_0x0458
        L_0x0438:
            r29 = r1
            r1 = r31
            goto L_0x0458
        L_0x043d:
            r0 = move-exception
            r29 = r1
            r1 = r31
            r4 = r49
            r37 = r50
            r8 = r58
            r7 = r2
            r41 = r13
            r6 = r15
            r40 = r30
            r12 = r33
            r30 = r44
            goto L_0x05b9
        L_0x0454:
            r29 = r1
            r1 = r31
        L_0x0458:
            r37 = r50
            r39 = r1
            r31 = r3
            r32 = r5
            r40 = r30
            r38 = r33
            r30 = r44
            r33 = r11
        L_0x0468:
            r5 = r12
            r6 = r30
            r7 = r31
            r8 = r46
            r9 = r32
            r10 = r33
            r11 = r53
            r3 = r12
            r12 = r55
            r41 = r13
            r13 = r62
            com.android.server.wm.ActivityStackSupervisorInjector.updateInfoBeforeRealStartActivity(r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ all -> 0x0592 }
            r0 = 1
            com.android.server.wm.ActivityRecord[] r1 = new com.android.server.wm.ActivityRecord[r0]     // Catch:{ all -> 0x0592 }
            r28 = r1
            r22 = r28
            r1 = r43
            r13 = r2
            r2 = r30
            r12 = r58
            r11 = r3
            r3 = r32
            r5 = r37
            r6 = r33
            r7 = r38
            r8 = r51
            r9 = r52
            r10 = r53
            r42 = r11
            r11 = r54
            r12 = r55
            r13 = r39
            r14 = r31
            r15 = r46
            r17 = r34
            r18 = r56
            r19 = r60
            r20 = r61
            r21 = r40
            r23 = r63
            r24 = r64
            r25 = r65
            r26 = r66
            r27 = r67
            r4 = r49
            int r1 = r1.startActivity(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27)     // Catch:{ all -> 0x0580 }
            android.os.Binder.restoreCallingIdentity(r35)     // Catch:{ all -> 0x0580 }
            r2 = r42
            boolean r3 = r2.mConfigWillChange     // Catch:{ all -> 0x0580 }
            if (r3 == 0) goto L_0x04e9
            r6 = r43
            com.android.server.wm.ActivityTaskManagerService r3 = r6.mService     // Catch:{ all -> 0x04e4 }
            android.app.ActivityManagerInternal r3 = r3.mAmInternal     // Catch:{ all -> 0x04e4 }
            java.lang.String r5 = "android.permission.CHANGE_CONFIGURATION"
            java.lang.String r7 = "updateConfiguration()"
            r3.enforceCallingPermission(r5, r7)     // Catch:{ all -> 0x04e4 }
            r3 = 0
            r2.mConfigWillChange = r3     // Catch:{ all -> 0x04e4 }
            com.android.server.wm.ActivityTaskManagerService r5 = r6.mService     // Catch:{ all -> 0x04e4 }
            r7 = r59
            r8 = 0
            r5.updateConfigurationLocked(r7, r8, r3)     // Catch:{ all -> 0x057c }
            goto L_0x04ee
        L_0x04e4:
            r0 = move-exception
            r7 = r59
            goto L_0x057d
        L_0x04e9:
            r3 = 0
            r6 = r43
            r7 = r59
        L_0x04ee:
            com.android.server.wm.ActivityStackSupervisor r5 = r6.mSupervisor     // Catch:{ all -> 0x057c }
            com.android.server.wm.ActivityMetricsLogger r5 = r5.getActivityMetricsLogger()     // Catch:{ all -> 0x057c }
            r8 = r28[r3]     // Catch:{ all -> 0x057c }
            r5.notifyActivityLaunched(r1, r8)     // Catch:{ all -> 0x057c }
            r8 = r58
            if (r8 == 0) goto L_0x0575
            r8.result = r1     // Catch:{ all -> 0x057a }
            r5 = r28[r3]     // Catch:{ all -> 0x057a }
            if (r1 == 0) goto L_0x0553
            r9 = 0
            r12 = 3
            r11 = 2
            if (r1 == r11) goto L_0x0516
            if (r1 == r12) goto L_0x050d
            goto L_0x0575
        L_0x050d:
            r8.timeout = r3     // Catch:{ all -> 0x057a }
            android.content.ComponentName r0 = r5.mActivityComponent     // Catch:{ all -> 0x057a }
            r8.who = r0     // Catch:{ all -> 0x057a }
            r8.totalTime = r9     // Catch:{ all -> 0x057a }
            goto L_0x0575
        L_0x0516:
            boolean r11 = r5.attachedToProcess()     // Catch:{ all -> 0x057a }
            if (r11 == 0) goto L_0x051e
            goto L_0x051f
        L_0x051e:
            r12 = r0
        L_0x051f:
            r8.launchState = r12     // Catch:{ all -> 0x057a }
            boolean r0 = r5.nowVisible     // Catch:{ all -> 0x057a }
            if (r0 == 0) goto L_0x0536
            com.android.server.wm.ActivityStack$ActivityState r0 = com.android.server.wm.ActivityStack.ActivityState.RESUMED     // Catch:{ all -> 0x057a }
            boolean r0 = r5.isState(r0)     // Catch:{ all -> 0x057a }
            if (r0 == 0) goto L_0x0536
            r8.timeout = r3     // Catch:{ all -> 0x057a }
            android.content.ComponentName r0 = r5.mActivityComponent     // Catch:{ all -> 0x057a }
            r8.who = r0     // Catch:{ all -> 0x057a }
            r8.totalTime = r9     // Catch:{ all -> 0x057a }
            goto L_0x0575
        L_0x0536:
            long r9 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x057a }
            com.android.server.wm.ActivityStackSupervisor r0 = r6.mSupervisor     // Catch:{ all -> 0x057a }
            android.content.ComponentName r3 = r5.mActivityComponent     // Catch:{ all -> 0x057a }
            r0.waitActivityVisible(r3, r8, r9)     // Catch:{ all -> 0x057a }
        L_0x0541:
            com.android.server.wm.ActivityTaskManagerService r0 = r6.mService     // Catch:{ InterruptedException -> 0x0549 }
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock     // Catch:{ InterruptedException -> 0x0549 }
            r0.wait()     // Catch:{ InterruptedException -> 0x0549 }
            goto L_0x054a
        L_0x0549:
            r0 = move-exception
        L_0x054a:
            boolean r0 = r8.timeout     // Catch:{ all -> 0x057a }
            if (r0 != 0) goto L_0x0575
            android.content.ComponentName r0 = r8.who     // Catch:{ all -> 0x057a }
            if (r0 == 0) goto L_0x0541
            goto L_0x0575
        L_0x0553:
            r11 = 2
            com.android.server.wm.ActivityStackSupervisor r0 = r6.mSupervisor     // Catch:{ all -> 0x057a }
            java.util.ArrayList<android.app.WaitResult> r0 = r0.mWaitingActivityLaunched     // Catch:{ all -> 0x057a }
            r0.add(r8)     // Catch:{ all -> 0x057a }
        L_0x055b:
            com.android.server.wm.ActivityTaskManagerService r0 = r6.mService     // Catch:{ InterruptedException -> 0x0563 }
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock     // Catch:{ InterruptedException -> 0x0563 }
            r0.wait()     // Catch:{ InterruptedException -> 0x0563 }
            goto L_0x0564
        L_0x0563:
            r0 = move-exception
        L_0x0564:
            int r0 = r8.result     // Catch:{ all -> 0x057a }
            if (r0 == r11) goto L_0x0570
            boolean r0 = r8.timeout     // Catch:{ all -> 0x057a }
            if (r0 != 0) goto L_0x0570
            android.content.ComponentName r0 = r8.who     // Catch:{ all -> 0x057a }
            if (r0 == 0) goto L_0x055b
        L_0x0570:
            int r0 = r8.result     // Catch:{ all -> 0x057a }
            if (r0 != r11) goto L_0x0575
            r1 = 2
        L_0x0575:
            monitor-exit(r41)     // Catch:{ all -> 0x057a }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r1
        L_0x057a:
            r0 = move-exception
            goto L_0x0587
        L_0x057c:
            r0 = move-exception
        L_0x057d:
            r8 = r58
            goto L_0x0587
        L_0x0580:
            r0 = move-exception
            r6 = r43
            r8 = r58
            r7 = r59
        L_0x0587:
            r3 = r31
            r5 = r32
            r11 = r33
            r12 = r38
            r1 = r39
            goto L_0x05b9
        L_0x0592:
            r0 = move-exception
            r4 = r49
            r8 = r58
            r7 = r2
            r6 = r15
            r3 = r31
            r5 = r32
            r11 = r33
            r12 = r38
            r1 = r39
            goto L_0x05b9
        L_0x05a4:
            r0 = move-exception
            r4 = r49
            r8 = r58
            r29 = r1
            r7 = r2
            r41 = r13
            r6 = r15
            r1 = r31
            r37 = r50
            r40 = r30
            r12 = r33
            r30 = r44
        L_0x05b9:
            monitor-exit(r41)     // Catch:{ all -> 0x05be }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x05be:
            r0 = move-exception
            goto L_0x05b9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStarter.startActivityMayWait(android.app.IApplicationThread, int, java.lang.String, int, int, android.content.Intent, java.lang.String, android.service.voice.IVoiceInteractionSession, com.android.internal.app.IVoiceInteractor, android.os.IBinder, java.lang.String, int, int, android.app.ProfilerInfo, android.app.WaitResult, android.content.res.Configuration, com.android.server.wm.SafeActivityOptions, boolean, int, com.android.server.wm.TaskRecord, java.lang.String, boolean, com.android.server.am.PendingIntentRecord, boolean):int");
    }

    static int computeResolveFilterUid(int customCallingUid, int actualCallingUid, int filterCallingUid) {
        if (filterCallingUid != -10000) {
            return filterCallingUid;
        }
        return customCallingUid >= 0 ? customCallingUid : actualCallingUid;
    }

    /* JADX INFO: finally extract failed */
    private int startActivity(ActivityRecord r, ActivityRecord sourceRecord, IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor, int startFlags, boolean doResume, ActivityOptions options, TaskRecord inTask, ActivityRecord[] outActivity, boolean restrictedBgActivity) {
        ActivityRecord currentTop;
        ActivityRecord currentTop2;
        ActivityRecord activityRecord = r;
        if (this.mService.mCastActivity == null || activityRecord.packageName == null || !activityRecord.packageName.equals(this.mService.mCastActivity.packageName)) {
            try {
                this.mService.mWindowManager.deferSurfaceLayout();
                ActivityStack topStack = this.mRootActivityContainer.getTopDisplayFocusedStack();
                ActivityRecord lastActivity = topStack != null ? topStack.topRunningActivityLocked() : null;
                boolean isGoHome = r.isActivityTypeHome();
                boolean isFromHome = lastActivity != null && lastActivity.isActivityTypeHome();
                int result = startActivityUnchecked(r, sourceRecord, voiceSession, voiceInteractor, startFlags, doResume, options, inTask, outActivity, restrictedBgActivity);
                if (isGoHome && !isFromHome) {
                    if (result >= 0) {
                        this.mService.updateMiuiAnimationInfo(lastActivity);
                    }
                    this.mService.setIsMultiWindowMode(lastActivity);
                } else if (isFromHome && !isGoHome) {
                    this.mService.setIsMultiWindowMode(r);
                }
                ActivityStack topStack2 = r.getActivityStack();
                ActivityStack startedActivityStack = topStack2 != null ? topStack2 : this.mTargetStack;
                if (!ActivityManager.isStartResultSuccessful(result)) {
                    ActivityStack stack = this.mStartActivity.getActivityStack();
                    if (stack != null) {
                        stack.finishActivityLocked(this.mStartActivity, 0, (Intent) null, "startActivity", true);
                    }
                    if (startedActivityStack != null && startedActivityStack.isAttached() && startedActivityStack.numActivities() == 0 && !startedActivityStack.isActivityTypeHome()) {
                        startedActivityStack.remove();
                    }
                } else if (!(startedActivityStack == null || (currentTop2 = startedActivityStack.topRunningActivityLocked()) == null || !currentTop2.shouldUpdateConfigForDisplayChanged())) {
                    this.mRootActivityContainer.ensureVisibilityAndConfig(currentTop2, currentTop2.getDisplayId(), true, false);
                }
                this.mService.mWindowManager.continueSurfaceLayout();
                postStartActivityProcessing(r, result, startedActivityStack);
                return result;
            } catch (Throwable th) {
                ActivityStack currentStack = r.getActivityStack();
                ActivityStack startedActivityStack2 = currentStack != null ? currentStack : this.mTargetStack;
                if (!ActivityManager.isStartResultSuccessful(-96)) {
                    ActivityStack stack2 = this.mStartActivity.getActivityStack();
                    if (stack2 != null) {
                        stack2.finishActivityLocked(this.mStartActivity, 0, (Intent) null, "startActivity", true);
                    }
                    if (startedActivityStack2 != null && startedActivityStack2.isAttached() && startedActivityStack2.numActivities() == 0 && !startedActivityStack2.isActivityTypeHome()) {
                        startedActivityStack2.remove();
                    }
                } else if (!(startedActivityStack2 == null || (currentTop = startedActivityStack2.topRunningActivityLocked()) == null || !currentTop.shouldUpdateConfigForDisplayChanged())) {
                    this.mRootActivityContainer.ensureVisibilityAndConfig(currentTop, currentTop.getDisplayId(), true, false);
                }
                this.mService.mWindowManager.continueSurfaceLayout();
                throw th;
            }
        } else {
            TaskRecord task = this.mService.mCastActivity.getTaskRecord();
            task.getStack().moveToFront("exitCastMode", task);
            this.mService.resumeCastActivity();
            return 0;
        }
    }

    private boolean handleBackgroundActivityAbort(ActivityRecord r) {
        if (!(!this.mService.isBackgroundActivityStartsEnabled())) {
            return false;
        }
        ActivityRecord resultRecord = r.resultTo;
        String resultWho = r.resultWho;
        int requestCode = r.requestCode;
        if (resultRecord != null) {
            resultRecord.getActivityStack().sendActivityResultLocked(-1, resultRecord, resultWho, requestCode, 0, (Intent) null);
        }
        ActivityOptions.abort(r.pendingOptions);
        return true;
    }

    private int startActivityUnchecked(ActivityRecord r, ActivityRecord sourceRecord, IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor, int startFlags, boolean doResume, ActivityOptions options, TaskRecord inTask, ActivityRecord[] outActivity, boolean restrictedBgActivity) {
        int i;
        int result;
        boolean newTask;
        ActivityRecord activityRecord;
        ActivityRecord activityRecord2 = r;
        ActivityRecord[] activityRecordArr = outActivity;
        setInitialState(r, options, inTask, doResume, startFlags, sourceRecord, voiceSession, voiceInteractor, restrictedBgActivity);
        int preferredWindowingMode = this.mLaunchParams.mWindowingMode;
        computeLaunchingTaskFlags();
        computeSourceStack();
        this.mIntent.setFlags(this.mLaunchFlags);
        ActivityRecord reusedActivity = getReusableIntentActivity();
        this.mSupervisor.getLaunchParamsController().calculate(reusedActivity != null ? reusedActivity.getTaskRecord() : this.mInTask, activityRecord2.info.windowLayout, r, sourceRecord, options, 2, this.mLaunchParams);
        if (this.mLaunchParams.hasPreferredDisplay()) {
            i = this.mLaunchParams.mPreferredDisplayId;
        } else {
            i = 0;
        }
        this.mPreferredDisplayId = i;
        if (!r.isActivityTypeHome() || this.mRootActivityContainer.canStartHomeOnDisplay(activityRecord2.info, this.mPreferredDisplayId, true)) {
            ActivityStack activityStack = null;
            if (reusedActivity != null) {
                if (this.mService.getLockTaskController().isLockTaskModeViolation(reusedActivity.getTaskRecord(), (this.mLaunchFlags & 268468224) == 268468224)) {
                    Slog.e("ActivityTaskManager", "startActivityUnchecked: Attempt to violate Lock Task Mode");
                    return 101;
                }
                boolean clearTopAndResetStandardLaunchMode = (this.mLaunchFlags & 69206016) == 69206016 && this.mLaunchMode == 0;
                if (this.mStartActivity.getTaskRecord() == null && !clearTopAndResetStandardLaunchMode) {
                    this.mStartActivity.setTask(reusedActivity.getTaskRecord());
                }
                if (reusedActivity.getTaskRecord().intent == null) {
                    reusedActivity.getTaskRecord().setIntent(this.mStartActivity);
                } else {
                    if ((this.mStartActivity.intent.getFlags() & 16384) != 0) {
                        reusedActivity.getTaskRecord().intent.addFlags(16384);
                    } else {
                        reusedActivity.getTaskRecord().intent.removeFlags(16384);
                    }
                }
                int i2 = this.mLaunchFlags;
                if ((67108864 & i2) != 0 || isDocumentLaunchesIntoExisting(i2) || isLaunchModeOneOf(3, 2)) {
                    TaskRecord task = reusedActivity.getTaskRecord();
                    ActivityRecord top = task.performClearTaskForReuseLocked(this.mStartActivity, this.mLaunchFlags);
                    if (reusedActivity.getTaskRecord() == null) {
                        reusedActivity.setTask(task);
                    }
                    if (ActivityStarterInjector.getLastFrame(reusedActivity.toString())) {
                        reusedActivity.setLastFrame(true);
                    }
                    if (top != null) {
                        if (top.frontOfTask) {
                            top.getTaskRecord().setIntent(this.mStartActivity);
                        }
                        deliverNewIntent(top);
                    }
                }
                this.mRootActivityContainer.sendPowerHintForLaunchStartIfNeeded(false, reusedActivity);
                ActivityRecord reusedActivity2 = setTargetStackAndMoveToFrontIfNeeded(reusedActivity);
                if (reusedActivity2 != null) {
                    reusedActivity2.realComponentName = activityRecord2.realComponentName;
                }
                ActivityRecord outResult = (activityRecordArr == null || activityRecordArr.length <= 0) ? null : activityRecordArr[0];
                if (outResult != null && (outResult.finishing || outResult.noDisplay)) {
                    activityRecordArr[0] = reusedActivity2;
                }
                if ((this.mStartFlags & 1) != 0) {
                    resumeTargetStackIfNeeded();
                    return 1;
                } else if (reusedActivity2 != null) {
                    setTaskFromIntentActivity(reusedActivity2);
                    if (!this.mAddingToTask && this.mReuseTask == null) {
                        resumeTargetStackIfNeeded();
                        if (activityRecordArr != null && activityRecordArr.length > 0) {
                            activityRecordArr[0] = reusedActivity2.finishing ? reusedActivity2.getTaskRecord().getTopActivity() : reusedActivity2;
                        }
                        if (this.mMovedToFront) {
                            return 2;
                        }
                        return 3;
                    }
                }
            }
            if (this.mStartActivity.packageName == null) {
                if (this.mStartActivity.resultTo != null) {
                    activityStack = this.mStartActivity.resultTo.getActivityStack();
                }
                ActivityStack sourceStack = activityStack;
                if (sourceStack != null) {
                    sourceStack.sendActivityResultLocked(-1, this.mStartActivity.resultTo, this.mStartActivity.resultWho, this.mStartActivity.requestCode, 0, (Intent) null);
                }
                ActivityOptions.abort(this.mOptions);
                return -92;
            }
            ActivityStack topStack = this.mRootActivityContainer.getTopDisplayFocusedStack();
            ActivityRecord topFocused = topStack.getTopActivity();
            ActivityRecord top2 = topStack.topRunningNonDelayedActivityLocked(this.mNotTop);
            if (top2 != null && this.mStartActivity.resultTo == null && top2.mActivityComponent.equals(this.mStartActivity.mActivityComponent) && top2.mUserId == this.mStartActivity.mUserId && top2.attachedToProcess() && ((this.mLaunchFlags & 536870912) != 0 || isLaunchModeOneOf(1, 2)) && (!top2.isActivityTypeHome() || top2.getDisplayId() == this.mPreferredDisplayId)) {
                topStack.mLastPausedActivity = null;
                if (this.mDoResume) {
                    this.mRootActivityContainer.resumeFocusedStacksTopActivities();
                }
                ActivityOptions.abort(this.mOptions);
                if ((this.mStartFlags & 1) != 0) {
                    return 1;
                }
                deliverNewIntent(top2);
                this.mSupervisor.handleNonResizableTaskIfNeeded(top2.getTaskRecord(), preferredWindowingMode, this.mPreferredDisplayId, topStack);
                return 3;
            }
            boolean newTask2 = false;
            TaskRecord taskToAffiliate = (!this.mLaunchTaskBehind || (activityRecord = this.mSourceRecord) == null) ? null : activityRecord.getTaskRecord();
            if (ActivityStarterInjector.getLastFrame(this.mStartActivity.toString())) {
                this.mStartActivity.setLastFrame(true);
            }
            if (this.mStartActivity.resultTo == null && this.mInTask == null && !this.mAddingToTask && (this.mLaunchFlags & 268435456) != 0) {
                String packageName = this.mService.mContext.getPackageName();
                BoostFramework boostFramework = this.mPerf;
                if (boostFramework != null) {
                    newTask = true;
                    this.mStartActivity.perfActivityBoostHandler = boostFramework.perfHint(4225, packageName, -1, 1);
                } else {
                    newTask = true;
                }
                result = setTaskFromReuseOrCreateNewTask(taskToAffiliate);
                newTask2 = newTask;
            } else if (this.mSourceRecord != null) {
                result = setTaskFromSourceRecord();
            } else if (this.mInTask != null) {
                result = setTaskFromInTask();
            } else {
                result = setTaskToCurrentTopOrCreateNewTask();
            }
            if (result != 0) {
                return result;
            }
            this.mService.mUgmInternal.grantUriPermissionFromIntent(this.mCallingUid, this.mStartActivity.packageName, this.mIntent, this.mStartActivity.getUriPermissionsLocked(), this.mStartActivity.mUserId);
            this.mService.getPackageManagerInternalLocked().grantEphemeralAccess(this.mStartActivity.mUserId, this.mIntent, UserHandle.getAppId(this.mStartActivity.appInfo.uid), UserHandle.getAppId(this.mCallingUid));
            if (newTask2) {
                EventLog.writeEvent(EventLogTags.AM_CREATE_TASK, new Object[]{Integer.valueOf(this.mStartActivity.mUserId), Integer.valueOf(this.mStartActivity.getTaskRecord().taskId)});
            }
            ActivityRecord activityRecord3 = this.mStartActivity;
            ActivityStack.logStartActivity(EventLogTags.AM_CREATE_ACTIVITY, activityRecord3, activityRecord3.getTaskRecord());
            this.mTargetStack.mLastPausedActivity = null;
            this.mRootActivityContainer.sendPowerHintForLaunchStartIfNeeded(false, this.mStartActivity);
            TaskRecord taskRecord = taskToAffiliate;
            this.mTargetStack.startActivityLocked(this.mStartActivity, topFocused, newTask2, this.mKeepCurTransition, this.mOptions);
            if (this.mDoResume) {
                ActivityRecord topTaskActivity = this.mStartActivity.getTaskRecord().topRunningActivityLocked();
                if (!this.mTargetStack.isFocusable() || !(topTaskActivity == null || !topTaskActivity.mTaskOverlay || this.mStartActivity == topTaskActivity)) {
                    this.mTargetStack.ensureActivitiesVisibleLocked(this.mStartActivity, 0, false);
                    this.mTargetStack.getDisplay().mDisplayContent.executeAppTransition();
                } else {
                    if (this.mTargetStack.isFocusable() && !this.mRootActivityContainer.isTopDisplayFocusedStack(this.mTargetStack)) {
                        this.mTargetStack.moveToFront("startActivityUnchecked");
                    }
                    this.mRootActivityContainer.resumeFocusedStacksTopActivities(this.mTargetStack, this.mStartActivity, this.mOptions);
                }
            } else if (this.mStartActivity != null) {
                this.mSupervisor.mRecentTasks.add(this.mStartActivity.getTaskRecord());
            }
            this.mRootActivityContainer.updateUserStack(this.mStartActivity.mUserId, this.mTargetStack);
            this.mSupervisor.handleNonResizableTaskIfNeeded(this.mStartActivity.getTaskRecord(), preferredWindowingMode, this.mPreferredDisplayId, this.mTargetStack);
            return 0;
        }
        Slog.w("ActivityTaskManager", "Cannot launch home on display " + this.mPreferredDisplayId);
        return -96;
    }

    /* access modifiers changed from: package-private */
    public void reset(boolean clearRequest) {
        this.mStartActivity = null;
        this.mIntent = null;
        this.mCallingUid = -1;
        this.mOptions = null;
        this.mRestrictedBgActivity = false;
        this.mLaunchTaskBehind = false;
        this.mLaunchFlags = 0;
        this.mLaunchMode = -1;
        this.mLaunchParams.reset();
        this.mNotTop = null;
        this.mDoResume = false;
        this.mStartFlags = 0;
        this.mSourceRecord = null;
        this.mPreferredDisplayId = -1;
        this.mInTask = null;
        this.mAddingToTask = false;
        this.mReuseTask = null;
        this.mNewTaskInfo = null;
        this.mNewTaskIntent = null;
        this.mSourceStack = null;
        this.mTargetStack = null;
        this.mMovedToFront = false;
        this.mNoAnimation = false;
        this.mKeepCurTransition = false;
        this.mAvoidMoveToFront = false;
        this.mVoiceSession = null;
        this.mVoiceInteractor = null;
        this.mIntentDelivered = false;
        if (clearRequest) {
            this.mRequest.reset();
        }
    }

    private void setInitialState(ActivityRecord r, ActivityOptions options, TaskRecord inTask, boolean doResume, int startFlags, ActivityRecord sourceRecord, IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor, boolean restrictedBgActivity) {
        int i;
        ActivityRecord activityRecord = r;
        TaskRecord taskRecord = inTask;
        boolean z = doResume;
        int i2 = startFlags;
        reset(false);
        this.mStartActivity = activityRecord;
        this.mIntent = activityRecord.intent;
        this.mOptions = options;
        this.mCallingUid = activityRecord.launchedFromUid;
        ActivityRecord activityRecord2 = sourceRecord;
        this.mSourceRecord = activityRecord2;
        this.mVoiceSession = voiceSession;
        this.mVoiceInteractor = voiceInteractor;
        this.mRestrictedBgActivity = restrictedBgActivity;
        this.mLaunchParams.reset();
        this.mSupervisor.getLaunchParamsController().calculate(inTask, activityRecord.info.windowLayout, r, sourceRecord, options, 0, this.mLaunchParams);
        if (this.mLaunchParams.hasPreferredDisplay()) {
            i = this.mLaunchParams.mPreferredDisplayId;
        } else {
            i = 0;
        }
        this.mPreferredDisplayId = i;
        this.mLaunchMode = activityRecord.launchMode;
        this.mLaunchFlags = adjustLaunchFlagsToDocumentMode(activityRecord, 3 == this.mLaunchMode, 2 == this.mLaunchMode, this.mIntent.getFlags());
        this.mLaunchTaskBehind = activityRecord.mLaunchTaskBehind && !isLaunchModeOneOf(2, 3) && (this.mLaunchFlags & DumpState.DUMP_FROZEN) != 0;
        sendNewTaskResultRequestIfNeeded();
        if ((this.mLaunchFlags & DumpState.DUMP_FROZEN) != 0 && activityRecord.resultTo == null) {
            this.mLaunchFlags |= 268435456;
        }
        if ((this.mLaunchFlags & 268435456) != 0 && (this.mLaunchTaskBehind || activityRecord.info.documentLaunchMode == 2)) {
            this.mLaunchFlags |= 134217728;
        }
        this.mSupervisor.mUserLeaving = (this.mLaunchFlags & DumpState.DUMP_DOMAIN_PREFERRED) == 0;
        this.mDoResume = z;
        if (!z || !r.okToShowLocked()) {
            activityRecord.delayedResume = true;
            this.mDoResume = false;
        }
        ActivityOptions activityOptions = this.mOptions;
        if (activityOptions != null) {
            if (activityOptions.getLaunchTaskId() != -1 && this.mOptions.getTaskOverlay()) {
                activityRecord.mTaskOverlay = true;
                if (!this.mOptions.canTaskOverlayResume()) {
                    TaskRecord task = this.mRootActivityContainer.anyTaskForId(this.mOptions.getLaunchTaskId());
                    ActivityRecord top = task != null ? task.getTopActivity() : null;
                    if (top != null && !top.isState(ActivityStack.ActivityState.RESUMED)) {
                        this.mDoResume = false;
                        this.mAvoidMoveToFront = true;
                    }
                }
            } else if (this.mOptions.getAvoidMoveToFront()) {
                this.mDoResume = false;
                this.mAvoidMoveToFront = true;
            }
        }
        this.mNotTop = (this.mLaunchFlags & DumpState.DUMP_SERVICE_PERMISSIONS) != 0 ? activityRecord2 : null;
        this.mInTask = taskRecord;
        if (taskRecord != null && !taskRecord.inRecents) {
            Slog.w("ActivityTaskManager", "Starting activity in task not in recents: " + taskRecord);
            this.mInTask = null;
        }
        this.mStartFlags = i2;
        if ((i2 & 1) != 0) {
            ActivityRecord checkedCaller = sourceRecord;
            if (checkedCaller == null) {
                checkedCaller = this.mRootActivityContainer.getTopDisplayFocusedStack().topRunningNonDelayedActivityLocked(this.mNotTop);
            }
            if (!checkedCaller.mActivityComponent.equals(activityRecord.mActivityComponent)) {
                this.mStartFlags &= -2;
            }
        }
        this.mNoAnimation = (this.mLaunchFlags & 65536) != 0;
        if (this.mRestrictedBgActivity && !this.mService.isBackgroundActivityStartsEnabled()) {
            this.mAvoidMoveToFront = true;
            this.mDoResume = false;
        }
    }

    private void sendNewTaskResultRequestIfNeeded() {
        ActivityStack sourceStack = this.mStartActivity.resultTo != null ? this.mStartActivity.resultTo.getActivityStack() : null;
        if (sourceStack != null && (this.mLaunchFlags & 268435456) != 0) {
            Slog.w("ActivityTaskManager", "Activity is launching as a new task, so cancelling activity result.");
            sourceStack.sendActivityResultLocked(-1, this.mStartActivity.resultTo, this.mStartActivity.resultWho, this.mStartActivity.requestCode, 0, (Intent) null);
            this.mStartActivity.resultTo = null;
        }
    }

    private void computeLaunchingTaskFlags() {
        ActivityRecord activityRecord;
        TaskRecord taskRecord;
        if (this.mSourceRecord != null || (taskRecord = this.mInTask) == null || taskRecord.getStack() == null) {
            this.mInTask = null;
            if ((this.mStartActivity.isResolverActivity() || this.mStartActivity.noDisplay) && (activityRecord = this.mSourceRecord) != null && activityRecord.inFreeformWindowingMode()) {
                this.mAddingToTask = true;
            }
        } else {
            Intent baseIntent = this.mInTask.getBaseIntent();
            ActivityRecord root = this.mInTask.getRootActivity();
            if (baseIntent != null) {
                if (isLaunchModeOneOf(3, 2)) {
                    if (!baseIntent.getComponent().equals(this.mStartActivity.intent.getComponent())) {
                        ActivityOptions.abort(this.mOptions);
                        throw new IllegalArgumentException("Trying to launch singleInstance/Task " + this.mStartActivity + " into different task " + this.mInTask);
                    } else if (root != null) {
                        ActivityOptions.abort(this.mOptions);
                        throw new IllegalArgumentException("Caller with mInTask " + this.mInTask + " has root " + root + " but target is singleInstance/Task");
                    }
                }
                if (root == null) {
                    this.mLaunchFlags = (this.mLaunchFlags & -403185665) | (baseIntent.getFlags() & 403185664);
                    this.mIntent.setFlags(this.mLaunchFlags);
                    this.mInTask.setIntent(this.mStartActivity);
                    this.mAddingToTask = true;
                } else if ((this.mLaunchFlags & 268435456) != 0) {
                    this.mAddingToTask = false;
                } else {
                    this.mAddingToTask = true;
                }
                this.mReuseTask = this.mInTask;
            } else {
                ActivityOptions.abort(this.mOptions);
                throw new IllegalArgumentException("Launching into task without base intent: " + this.mInTask);
            }
        }
        TaskRecord taskRecord2 = this.mInTask;
        if (taskRecord2 == null) {
            ActivityRecord activityRecord2 = this.mSourceRecord;
            if (activityRecord2 == null) {
                if ((this.mLaunchFlags & 268435456) == 0 && taskRecord2 == null) {
                    Slog.w("ActivityTaskManager", "startActivity called from non-Activity context; forcing Intent.FLAG_ACTIVITY_NEW_TASK for: " + this.mIntent);
                    this.mLaunchFlags = this.mLaunchFlags | 268435456;
                }
            } else if (activityRecord2.launchMode == 3) {
                this.mLaunchFlags |= 268435456;
            } else if (isLaunchModeOneOf(3, 2)) {
                this.mLaunchFlags |= 268435456;
            }
        }
    }

    private void computeSourceStack() {
        ActivityRecord activityRecord = this.mSourceRecord;
        if (activityRecord == null) {
            this.mSourceStack = null;
        } else if (!activityRecord.finishing) {
            this.mSourceStack = this.mSourceRecord.getActivityStack();
        } else {
            if ((this.mLaunchFlags & 268435456) == 0) {
                Slog.w("ActivityTaskManager", "startActivity called from finishing " + this.mSourceRecord + "; forcing Intent.FLAG_ACTIVITY_NEW_TASK for: " + this.mIntent);
                this.mLaunchFlags = this.mLaunchFlags | 268435456;
                this.mNewTaskInfo = this.mSourceRecord.info;
                TaskRecord sourceTask = this.mSourceRecord.getTaskRecord();
                this.mNewTaskIntent = sourceTask != null ? sourceTask.intent : null;
            }
            this.mSourceRecord = null;
            this.mSourceStack = null;
            ActivityRecord activityRecord2 = this.mStartActivity;
            if (activityRecord2 != null) {
                activityRecord2.resultTo = null;
            }
        }
    }

    private ActivityRecord getReusableIntentActivity() {
        int i = this.mLaunchFlags;
        boolean z = false;
        boolean putIntoExistingTask = (((268435456 & i) != 0 && (i & 134217728) == 0) || isLaunchModeOneOf(3, 2)) & (this.mInTask == null && this.mStartActivity.resultTo == null);
        ActivityRecord intentActivity = null;
        ActivityOptions activityOptions = this.mOptions;
        if (activityOptions != null && activityOptions.getLaunchTaskId() != -1) {
            TaskRecord task = this.mRootActivityContainer.anyTaskForId(this.mOptions.getLaunchTaskId());
            intentActivity = task != null ? task.getTopActivity() : null;
        } else if (putIntoExistingTask) {
            if (3 == this.mLaunchMode) {
                intentActivity = this.mRootActivityContainer.findActivity(this.mIntent, this.mStartActivity.info, this.mStartActivity.isActivityTypeHome());
            } else if ((this.mLaunchFlags & 4096) != 0) {
                RootActivityContainer rootActivityContainer = this.mRootActivityContainer;
                Intent intent = this.mIntent;
                ActivityInfo activityInfo = this.mStartActivity.info;
                if (2 != this.mLaunchMode) {
                    z = true;
                }
                intentActivity = rootActivityContainer.findActivity(intent, activityInfo, z);
            } else {
                intentActivity = this.mRootActivityContainer.findTask(this.mStartActivity, this.mPreferredDisplayId);
            }
        }
        if (intentActivity == null) {
            return intentActivity;
        }
        if ((this.mStartActivity.isActivityTypeHome() || intentActivity.isActivityTypeHome()) && intentActivity.getDisplayId() != this.mPreferredDisplayId) {
            return null;
        }
        return intentActivity;
    }

    private ActivityRecord setTargetStackAndMoveToFrontIfNeeded(ActivityRecord intentActivity) {
        boolean differentTopTask;
        ActivityRecord activityRecord;
        ActivityRecord activityRecord2 = intentActivity;
        this.mTargetStack = intentActivity.getActivityStack();
        ActivityStack activityStack = this.mTargetStack;
        activityStack.mLastPausedActivity = null;
        if (this.mPreferredDisplayId == activityStack.mDisplayId) {
            ActivityStack focusStack = this.mTargetStack.getDisplay().getFocusedStack();
            ActivityRecord curTop = focusStack == null ? null : focusStack.topRunningNonDelayedActivityLocked(this.mNotTop);
            TaskRecord topTask = curTop != null ? curTop.getTaskRecord() : null;
            differentTopTask = (topTask == intentActivity.getTaskRecord() && (focusStack == null || topTask == focusStack.topTask())) ? false : true;
        } else {
            differentTopTask = true;
        }
        if (differentTopTask && !this.mAvoidMoveToFront) {
            this.mStartActivity.intent.addFlags(DumpState.DUMP_CHANGES);
            if (this.mSourceRecord == null || (this.mSourceStack.getTopActivity() != null && this.mSourceStack.getTopActivity().getTaskRecord() == this.mSourceRecord.getTaskRecord())) {
                if (this.mLaunchTaskBehind && (activityRecord = this.mSourceRecord) != null) {
                    activityRecord2.setTaskToAffiliateWith(activityRecord.getTaskRecord());
                }
                if (!((this.mLaunchFlags & 268468224) == 268468224)) {
                    ActivityRecord activityRecord3 = this.mStartActivity;
                    ActivityStack launchStack = getLaunchStack(activityRecord3, this.mLaunchFlags, activityRecord3.getTaskRecord(), this.mOptions);
                    TaskRecord intentTask = intentActivity.getTaskRecord();
                    if (launchStack == null || launchStack == this.mTargetStack) {
                        this.mTargetStack.moveTaskToFrontLocked(intentTask, this.mNoAnimation, this.mOptions, this.mStartActivity.appTimeTracker, "bringingFoundTaskToFront");
                        this.mMovedToFront = true;
                    } else if (launchStack.inSplitScreenWindowingMode()) {
                        if ((this.mLaunchFlags & 4096) != 0) {
                            intentTask.reparent(launchStack, true, 0, true, true, "launchToSide");
                        } else {
                            this.mTargetStack.moveTaskToFrontLocked(intentTask, this.mNoAnimation, this.mOptions, this.mStartActivity.appTimeTracker, "bringToFrontInsteadOfAdjacentLaunch");
                        }
                        this.mMovedToFront = launchStack != launchStack.getDisplay().getTopStackInWindowingMode(launchStack.getWindowingMode());
                    } else if (launchStack.mDisplayId != this.mTargetStack.mDisplayId) {
                        intentActivity.getTaskRecord().reparent(launchStack, true, 0, true, true, "reparentToDisplay");
                        this.mMovedToFront = true;
                    } else if (launchStack.isActivityTypeHome() && !this.mTargetStack.isActivityTypeHome()) {
                        intentActivity.getTaskRecord().reparent(launchStack, true, 0, true, true, "reparentingHome");
                        this.mMovedToFront = true;
                    } else if (launchStack.getWindowingMode() == 5 && this.mTargetStack.getWindowingMode() != 5) {
                        intentActivity.getTaskRecord().reparent(launchStack, true, 0, true, true, "reparentingFreeform");
                        this.mMovedToFront = true;
                    }
                    this.mOptions = null;
                    activityRecord2.showStartingWindow((ActivityRecord) null, false, true);
                }
            }
        }
        this.mTargetStack = intentActivity.getActivityStack();
        if (!this.mMovedToFront && this.mDoResume) {
            this.mTargetStack.moveToFront("intentActivityFound");
        }
        this.mSupervisor.handleNonResizableTaskIfNeeded(intentActivity.getTaskRecord(), 0, 0, this.mTargetStack);
        if ((this.mLaunchFlags & DumpState.DUMP_COMPILER_STATS) != 0) {
            return this.mTargetStack.resetTaskIfNeededLocked(activityRecord2, this.mStartActivity);
        }
        return activityRecord2;
    }

    private void setTaskFromIntentActivity(ActivityRecord intentActivity) {
        int i = this.mLaunchFlags;
        if ((i & 268468224) == 268468224) {
            TaskRecord task = intentActivity.getTaskRecord();
            task.performClearTaskLocked();
            this.mReuseTask = task;
            this.mReuseTask.setIntent(this.mStartActivity);
        } else if ((i & BroadcastQueueInjector.FLAG_IMMUTABLE) != 0 || isLaunchModeOneOf(3, 2)) {
            if (intentActivity.getTaskRecord().performClearTaskLocked(this.mStartActivity, this.mLaunchFlags) == null) {
                this.mAddingToTask = true;
                this.mStartActivity.setTask((TaskRecord) null);
                this.mSourceRecord = intentActivity;
                TaskRecord task2 = this.mSourceRecord.getTaskRecord();
                if (task2 != null && task2.getStack() == null) {
                    this.mTargetStack = computeStackFocus(this.mSourceRecord, false, this.mLaunchFlags, this.mOptions);
                    this.mTargetStack.addTask(task2, true ^ this.mLaunchTaskBehind, "startActivityUnchecked");
                }
            }
        } else if (this.mStartActivity.mActivityComponent.equals(intentActivity.getTaskRecord().realActivity)) {
            if (((this.mLaunchFlags & 536870912) != 0 || 1 == this.mLaunchMode) && intentActivity.mActivityComponent.equals(this.mStartActivity.mActivityComponent)) {
                if (intentActivity.frontOfTask) {
                    intentActivity.getTaskRecord().setIntent(this.mStartActivity);
                }
                deliverNewIntent(intentActivity);
            } else if (!intentActivity.getTaskRecord().isSameIntentFilter(this.mStartActivity)) {
                this.mAddingToTask = true;
                this.mSourceRecord = intentActivity;
            }
        } else if ((this.mLaunchFlags & DumpState.DUMP_COMPILER_STATS) == 0) {
            this.mAddingToTask = true;
            this.mSourceRecord = intentActivity;
        } else if (!intentActivity.getTaskRecord().rootWasReset) {
            intentActivity.getTaskRecord().setIntent(this.mStartActivity);
        }
    }

    private void resumeTargetStackIfNeeded() {
        if (this.mDoResume) {
            this.mRootActivityContainer.resumeFocusedStacksTopActivities(this.mTargetStack, (ActivityRecord) null, this.mOptions);
        } else {
            ActivityOptions.abort(this.mOptions);
        }
        this.mRootActivityContainer.updateUserStack(this.mStartActivity.mUserId, this.mTargetStack);
    }

    private int setTaskFromReuseOrCreateNewTask(TaskRecord taskToAffiliate) {
        TaskRecord taskRecord;
        if (this.mRestrictedBgActivity && (((taskRecord = this.mReuseTask) == null || !taskRecord.containsAppUid(this.mCallingUid)) && handleBackgroundActivityAbort(this.mStartActivity))) {
            return 102;
        }
        this.mTargetStack = computeStackFocus(this.mStartActivity, true, this.mLaunchFlags, this.mOptions);
        TaskRecord taskRecord2 = this.mReuseTask;
        if (taskRecord2 == null) {
            ActivityStack activityStack = this.mTargetStack;
            int nextTaskIdForUserLocked = this.mSupervisor.getNextTaskIdForUserLocked(this.mStartActivity.mUserId);
            ActivityInfo activityInfo = this.mNewTaskInfo;
            if (activityInfo == null) {
                activityInfo = this.mStartActivity.info;
            }
            ActivityInfo activityInfo2 = activityInfo;
            Intent intent = this.mNewTaskIntent;
            if (intent == null) {
                intent = this.mIntent;
            }
            addOrReparentStartingActivity(activityStack.createTaskRecord(nextTaskIdForUserLocked, activityInfo2, intent, this.mVoiceSession, this.mVoiceInteractor, !this.mLaunchTaskBehind, this.mStartActivity, this.mSourceRecord, this.mOptions), "setTaskFromReuseOrCreateNewTask - mReuseTask");
            updateBounds(this.mStartActivity.getTaskRecord(), this.mLaunchParams.mBounds);
        } else {
            addOrReparentStartingActivity(taskRecord2, "setTaskFromReuseOrCreateNewTask");
        }
        if (taskToAffiliate != null) {
            this.mStartActivity.setTaskToAffiliateWith(taskToAffiliate);
        }
        if (this.mService.getLockTaskController().isLockTaskModeViolation(this.mStartActivity.getTaskRecord())) {
            Slog.e("ActivityTaskManager", "Attempted Lock Task Mode violation mStartActivity=" + this.mStartActivity);
            return 101;
        } else if (!this.mDoResume) {
            return 0;
        } else {
            this.mTargetStack.moveToFront("reuseOrNewTask");
            return 0;
        }
    }

    private void deliverNewIntent(ActivityRecord activity) {
        if (!this.mIntentDelivered) {
            ActivityStack.logStartActivity(EventLogTags.AM_NEW_INTENT, activity, activity.getTaskRecord());
            activity.deliverNewIntentLocked(this.mCallingUid, this.mStartActivity.intent, this.mStartActivity.launchedFromPackage);
            this.mIntentDelivered = true;
        }
    }

    private int setTaskFromSourceRecord() {
        int i;
        ActivityRecord top;
        if (this.mService.getLockTaskController().isLockTaskModeViolation(this.mSourceRecord.getTaskRecord())) {
            Slog.e("ActivityTaskManager", "Attempted Lock Task Mode violation mStartActivity=" + this.mStartActivity);
            return 101;
        }
        String packageName = this.mService.mContext.getPackageName();
        BoostFramework boostFramework = this.mPerf;
        if (boostFramework != null) {
            this.mStartActivity.perfActivityBoostHandler = boostFramework.perfHint(4225, packageName, -1, 1);
        }
        TaskRecord sourceTask = this.mSourceRecord.getTaskRecord();
        ActivityStack sourceStack = this.mSourceRecord.getActivityStack();
        if (this.mRestrictedBgActivity && !sourceTask.containsAppUid(this.mCallingUid) && handleBackgroundActivityAbort(this.mStartActivity)) {
            return 102;
        }
        ActivityStack activityStack = this.mTargetStack;
        if (activityStack != null) {
            i = activityStack.mDisplayId;
        } else {
            i = sourceStack.mDisplayId;
        }
        int targetDisplayId = i;
        if (sourceStack.topTask() != sourceTask || !this.mStartActivity.canBeLaunchedOnDisplay(targetDisplayId)) {
            ActivityRecord activityRecord = this.mStartActivity;
            this.mTargetStack = getLaunchStack(activityRecord, this.mLaunchFlags, activityRecord.getTaskRecord(), this.mOptions);
            if (this.mTargetStack == null && targetDisplayId != sourceStack.mDisplayId) {
                this.mTargetStack = this.mRootActivityContainer.getValidLaunchStackOnDisplay(sourceStack.mDisplayId, this.mStartActivity, this.mOptions, this.mLaunchParams);
            }
            if (this.mTargetStack == null) {
                this.mTargetStack = this.mRootActivityContainer.getNextValidLaunchStack(this.mStartActivity, -1);
            }
        }
        ActivityStack activityStack2 = this.mTargetStack;
        if (activityStack2 == null) {
            this.mTargetStack = sourceStack;
        } else if (activityStack2 != sourceStack) {
            sourceTask.reparent(activityStack2, true, 0, false, true, "launchToSide");
        }
        if (this.mTargetStack.topTask() != sourceTask && !this.mAvoidMoveToFront) {
            this.mTargetStack.moveTaskToFrontLocked(sourceTask, this.mNoAnimation, this.mOptions, this.mStartActivity.appTimeTracker, "sourceTaskToFront");
        } else if (this.mDoResume) {
            this.mTargetStack.moveToFront("sourceStackToFront");
        }
        if (!this.mAddingToTask) {
            int i2 = this.mLaunchFlags;
            if ((67108864 & i2) != 0) {
                ActivityRecord top2 = sourceTask.performClearTaskLocked(this.mStartActivity, i2);
                this.mKeepCurTransition = true;
                if (top2 != null) {
                    ActivityStack.logStartActivity(EventLogTags.AM_NEW_INTENT, this.mStartActivity, top2.getTaskRecord());
                    deliverNewIntent(top2);
                    this.mTargetStack.mLastPausedActivity = null;
                    if (this.mDoResume) {
                        this.mRootActivityContainer.resumeFocusedStacksTopActivities();
                    }
                    ActivityOptions.abort(this.mOptions);
                    return 3;
                }
                addOrReparentStartingActivity(sourceTask, "setTaskFromSourceRecord");
                return 0;
            }
        }
        if (!(this.mAddingToTask || (this.mLaunchFlags & 131072) == 0 || (top = sourceTask.findActivityInHistoryLocked(this.mStartActivity)) == null)) {
            TaskRecord task = top.getTaskRecord();
            task.moveActivityToFrontLocked(top);
            top.updateOptionsLocked(this.mOptions);
            ActivityStack.logStartActivity(EventLogTags.AM_NEW_INTENT, this.mStartActivity, task);
            deliverNewIntent(top);
            this.mTargetStack.mLastPausedActivity = null;
            if (this.mDoResume) {
                this.mRootActivityContainer.resumeFocusedStacksTopActivities();
            }
            return 3;
        }
        addOrReparentStartingActivity(sourceTask, "setTaskFromSourceRecord");
        return 0;
    }

    private int setTaskFromInTask() {
        if (this.mService.getLockTaskController().isLockTaskModeViolation(this.mInTask)) {
            Slog.e("ActivityTaskManager", "Attempted Lock Task Mode violation mStartActivity=" + this.mStartActivity);
            return 101;
        }
        this.mTargetStack = this.mInTask.getStack();
        ActivityRecord top = this.mInTask.getTopActivity();
        if (top != null && top.mActivityComponent.equals(this.mStartActivity.mActivityComponent) && top.mUserId == this.mStartActivity.mUserId && ((this.mLaunchFlags & 536870912) != 0 || isLaunchModeOneOf(1, 2))) {
            this.mTargetStack.moveTaskToFrontLocked(this.mInTask, this.mNoAnimation, this.mOptions, this.mStartActivity.appTimeTracker, "inTaskToFront");
            if ((this.mStartFlags & 1) != 0) {
                return 1;
            }
            deliverNewIntent(top);
            return 3;
        } else if (!this.mAddingToTask) {
            this.mTargetStack.moveTaskToFrontLocked(this.mInTask, this.mNoAnimation, this.mOptions, this.mStartActivity.appTimeTracker, "inTaskToFront");
            ActivityOptions.abort(this.mOptions);
            return 2;
        } else {
            if (!this.mLaunchParams.mBounds.isEmpty()) {
                ActivityStack stack = this.mRootActivityContainer.getLaunchStack((ActivityRecord) null, (ActivityOptions) null, this.mInTask, true);
                if (stack != this.mInTask.getStack()) {
                    this.mInTask.reparent(stack, true, 1, false, true, "inTaskToFront");
                    this.mTargetStack = this.mInTask.getStack();
                }
                updateBounds(this.mInTask, this.mLaunchParams.mBounds);
            }
            this.mTargetStack.moveTaskToFrontLocked(this.mInTask, this.mNoAnimation, this.mOptions, this.mStartActivity.appTimeTracker, "inTaskToFront");
            addOrReparentStartingActivity(this.mInTask, "setTaskFromInTask");
            return 0;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateBounds(TaskRecord task, Rect bounds) {
        if (!bounds.isEmpty()) {
            ActivityStack stack = task.getStack();
            if (stack == null || !stack.resizeStackWithLaunchBounds()) {
                task.updateOverrideConfiguration(bounds);
            } else {
                this.mService.resizeStack(stack.mStackId, bounds, true, false, true, -1);
            }
        }
    }

    private int setTaskToCurrentTopOrCreateNewTask() {
        this.mTargetStack = computeStackFocus(this.mStartActivity, false, this.mLaunchFlags, this.mOptions);
        if (this.mDoResume) {
            this.mTargetStack.moveToFront("addingToTopTask");
        }
        ActivityRecord prev = this.mTargetStack.getTopActivity();
        if (this.mRestrictedBgActivity && prev == null && handleBackgroundActivityAbort(this.mStartActivity)) {
            return 102;
        }
        TaskRecord task = prev != null ? prev.getTaskRecord() : this.mTargetStack.createTaskRecord(this.mSupervisor.getNextTaskIdForUserLocked(this.mStartActivity.mUserId), this.mStartActivity.info, this.mIntent, (IVoiceInteractionSession) null, (IVoiceInteractor) null, true, this.mStartActivity, this.mSourceRecord, this.mOptions);
        if (this.mRestrictedBgActivity && prev != null && !task.containsAppUid(this.mCallingUid) && handleBackgroundActivityAbort(this.mStartActivity)) {
            return 102;
        }
        addOrReparentStartingActivity(task, "setTaskToCurrentTopOrCreateNewTask");
        this.mTargetStack.positionChildWindowContainerAtTop(task);
        Slog.v("ActivityTaskManager", "Starting new activity " + this.mStartActivity + " in new guessed " + this.mStartActivity.getTaskRecord());
        return 0;
    }

    private void addOrReparentStartingActivity(TaskRecord parent, String reason) {
        if (this.mStartActivity.getTaskRecord() == null || this.mStartActivity.getTaskRecord() == parent) {
            parent.addActivityToTop(this.mStartActivity);
        } else {
            this.mStartActivity.reparent(parent, parent.mActivities.size(), reason);
        }
    }

    private int adjustLaunchFlagsToDocumentMode(ActivityRecord r, boolean launchSingleInstance, boolean launchSingleTask, int launchFlags) {
        if ((launchFlags & DumpState.DUMP_FROZEN) == 0 || (!launchSingleInstance && !launchSingleTask)) {
            int i = r.info.documentLaunchMode;
            if (i == 0) {
                return launchFlags;
            }
            if (i == 1) {
                return launchFlags | DumpState.DUMP_FROZEN;
            }
            if (i == 2) {
                return launchFlags | DumpState.DUMP_FROZEN;
            }
            if (i != 3) {
                return launchFlags;
            }
            return launchFlags & -134217729;
        }
        Slog.i("ActivityTaskManager", "Ignoring FLAG_ACTIVITY_NEW_DOCUMENT, launchMode is \"singleInstance\" or \"singleTask\"");
        return launchFlags & -134742017;
    }

    private ActivityStack computeStackFocus(ActivityRecord r, boolean newTask, int launchFlags, ActivityOptions aOptions) {
        TaskRecord task = r.getTaskRecord();
        ActivityStack stack = getLaunchStack(r, launchFlags, task, aOptions);
        if (stack != null) {
            return stack;
        }
        ActivityStack currentStack = task != null ? task.getStack() : null;
        ActivityStack focusedStack = this.mRootActivityContainer.getTopDisplayFocusedStack();
        if (currentStack != null) {
            return currentStack;
        }
        if (canLaunchIntoFocusedStack(r, newTask)) {
            return focusedStack;
        }
        int i = this.mPreferredDisplayId;
        if (i != 0 && (stack = this.mRootActivityContainer.getValidLaunchStackOnDisplay(i, r, aOptions, this.mLaunchParams)) == null) {
            stack = this.mRootActivityContainer.getNextValidLaunchStack(r, this.mPreferredDisplayId);
        }
        if (stack == null) {
            return this.mRootActivityContainer.getLaunchStack(r, aOptions, task, true);
        }
        return stack;
    }

    private boolean canLaunchIntoFocusedStack(ActivityRecord r, boolean newTask) {
        boolean canUseFocusedStack;
        ActivityStack focusedStack = this.mRootActivityContainer.getTopDisplayFocusedStack();
        if (focusedStack.isActivityTypeAssistant()) {
            canUseFocusedStack = r.isActivityTypeAssistant();
        } else {
            int windowingMode = focusedStack.getWindowingMode();
            if (windowingMode == 1) {
                canUseFocusedStack = true;
            } else if (windowingMode == 3 || windowingMode == 4) {
                canUseFocusedStack = r.supportsSplitScreenWindowingMode();
            } else if (windowingMode != 5) {
                canUseFocusedStack = !focusedStack.isOnHomeDisplay() && r.canBeLaunchedOnDisplay(focusedStack.mDisplayId);
            } else {
                canUseFocusedStack = r.supportsFreeform();
            }
        }
        if (!canUseFocusedStack || newTask || this.mPreferredDisplayId != focusedStack.mDisplayId) {
            return false;
        }
        return true;
    }

    private ActivityStack getLaunchStack(ActivityRecord r, int launchFlags, TaskRecord task, ActivityOptions aOptions) {
        TaskRecord taskRecord = this.mReuseTask;
        if (taskRecord != null) {
            return taskRecord.getStack();
        }
        if (!(r == null || aOptions == null || aOptions.getLaunchWindowingMode() != 5)) {
            r.setMiuiConfigFlag(2);
        }
        boolean onTop = true;
        if ((launchFlags & 4096) == 0 || this.mPreferredDisplayId != 0) {
            if (aOptions != null && aOptions.getAvoidMoveToFront()) {
                onTop = false;
            }
            return this.mRootActivityContainer.getLaunchStack(r, aOptions, task, onTop, this.mLaunchParams);
        }
        ActivityStack focusedStack = this.mRootActivityContainer.getTopDisplayFocusedStack();
        ActivityStack parentStack = task != null ? task.getStack() : focusedStack;
        if (parentStack != focusedStack) {
            return parentStack;
        }
        if (focusedStack != null && task == focusedStack.topTask()) {
            return focusedStack;
        }
        if (parentStack == null || !parentStack.inSplitScreenPrimaryWindowingMode()) {
            ActivityStack dockedStack = this.mRootActivityContainer.getDefaultDisplay().getSplitScreenPrimaryStack();
            if (dockedStack == null || dockedStack.shouldBeVisible(r)) {
                return dockedStack;
            }
            return this.mRootActivityContainer.getLaunchStack(r, aOptions, task, true);
        }
        return parentStack.getDisplay().getOrCreateStack(4, this.mRootActivityContainer.resolveActivityType(r, this.mOptions, task), true);
    }

    private boolean isLaunchModeOneOf(int mode1, int mode2) {
        int i = this.mLaunchMode;
        return mode1 == i || mode2 == i;
    }

    static boolean isDocumentLaunchesIntoExisting(int flags) {
        return (524288 & flags) != 0 && (134217728 & flags) == 0;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setIntent(Intent intent) {
        this.mRequest.intent = intent;
        return this;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Intent getIntent() {
        return this.mRequest.intent;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setReason(String reason) {
        this.mRequest.reason = reason;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setCaller(IApplicationThread caller) {
        this.mRequest.caller = caller;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setEphemeralIntent(Intent intent) {
        this.mRequest.ephemeralIntent = intent;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setResolvedType(String type) {
        this.mRequest.resolvedType = type;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setActivityInfo(ActivityInfo info) {
        this.mRequest.activityInfo = info;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setResolveInfo(ResolveInfo info) {
        this.mRequest.resolveInfo = info;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setVoiceSession(IVoiceInteractionSession voiceSession) {
        this.mRequest.voiceSession = voiceSession;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setVoiceInteractor(IVoiceInteractor voiceInteractor) {
        this.mRequest.voiceInteractor = voiceInteractor;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setResultTo(IBinder resultTo) {
        this.mRequest.resultTo = resultTo;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setResultWho(String resultWho) {
        this.mRequest.resultWho = resultWho;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setRequestCode(int requestCode) {
        this.mRequest.requestCode = requestCode;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setCallingPid(int pid) {
        this.mRequest.callingPid = pid;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setCallingUid(int uid) {
        this.mRequest.callingUid = uid;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setCallingPackage(String callingPackage) {
        this.mRequest.callingPackage = callingPackage;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setRealCallingPid(int pid) {
        this.mRequest.realCallingPid = pid;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setRealCallingUid(int uid) {
        this.mRequest.realCallingUid = uid;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setStartFlags(int startFlags) {
        this.mRequest.startFlags = startFlags;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setActivityOptions(SafeActivityOptions options) {
        this.mRequest.activityOptions = options;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setActivityOptions(Bundle bOptions) {
        return setActivityOptions(SafeActivityOptions.fromBundle(bOptions));
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setIgnoreTargetSecurity(boolean ignoreTargetSecurity) {
        this.mRequest.ignoreTargetSecurity = ignoreTargetSecurity;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setFilterCallingUid(int filterCallingUid) {
        this.mRequest.filterCallingUid = filterCallingUid;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setComponentSpecified(boolean componentSpecified) {
        this.mRequest.componentSpecified = componentSpecified;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setOutActivity(ActivityRecord[] outActivity) {
        this.mRequest.outActivity = outActivity;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setInTask(TaskRecord inTask) {
        this.mRequest.inTask = inTask;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setWaitResult(WaitResult result) {
        this.mRequest.waitResult = result;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setProfilerInfo(ProfilerInfo info) {
        this.mRequest.profilerInfo = info;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setGlobalConfiguration(Configuration config) {
        this.mRequest.globalConfig = config;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setUserId(int userId) {
        this.mRequest.userId = userId;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setMayWait(int userId) {
        Request request = this.mRequest;
        request.mayWait = true;
        request.userId = userId;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setAllowPendingRemoteAnimationRegistryLookup(boolean allowLookup) {
        this.mRequest.allowPendingRemoteAnimationRegistryLookup = allowLookup;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setOriginatingPendingIntent(PendingIntentRecord originatingPendingIntent) {
        this.mRequest.originatingPendingIntent = originatingPendingIntent;
        return this;
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter setAllowBackgroundActivityStart(boolean allowBackgroundActivityStart) {
        this.mRequest.allowBackgroundActivityStart = allowBackgroundActivityStart;
        return this;
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        String prefix2 = prefix + "  ";
        pw.print(prefix2);
        pw.print("mCurrentUser=");
        pw.println(this.mRootActivityContainer.mCurrentUser);
        pw.print(prefix2);
        pw.print("mLastStartReason=");
        pw.println(this.mLastStartReason);
        pw.print(prefix2);
        pw.print("mLastStartActivityTimeMs=");
        pw.println(DateFormat.getDateTimeInstance().format(new Date(this.mLastStartActivityTimeMs)));
        pw.print(prefix2);
        pw.print("mLastStartActivityResult=");
        pw.println(this.mLastStartActivityResult);
        boolean z = false;
        ActivityRecord r = this.mLastStartActivityRecord[0];
        if (r != null) {
            pw.print(prefix2);
            pw.println("mLastStartActivityRecord:");
            r.dump(pw, prefix2 + "  ");
        }
        if (this.mStartActivity != null) {
            pw.print(prefix2);
            pw.println("mStartActivity:");
            this.mStartActivity.dump(pw, prefix2 + "  ");
        }
        if (this.mIntent != null) {
            pw.print(prefix2);
            pw.print("mIntent=");
            pw.println(this.mIntent);
        }
        if (this.mOptions != null) {
            pw.print(prefix2);
            pw.print("mOptions=");
            pw.println(this.mOptions);
        }
        pw.print(prefix2);
        pw.print("mLaunchSingleTop=");
        pw.print(1 == this.mLaunchMode);
        pw.print(" mLaunchSingleInstance=");
        pw.print(3 == this.mLaunchMode);
        pw.print(" mLaunchSingleTask=");
        if (2 == this.mLaunchMode) {
            z = true;
        }
        pw.println(z);
        pw.print(prefix2);
        pw.print("mLaunchFlags=0x");
        pw.print(Integer.toHexString(this.mLaunchFlags));
        pw.print(" mDoResume=");
        pw.print(this.mDoResume);
        pw.print(" mAddingToTask=");
        pw.println(this.mAddingToTask);
    }
}
