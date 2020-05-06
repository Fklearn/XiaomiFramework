package com.android.server.wm;

import android.app.ActivityOptions;
import android.app.IApplicationThread;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.service.voice.IVoiceInteractionSession;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.RemoteAnimationAdapter;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IVoiceInteractor;
import com.android.server.am.PendingIntentRecord;
import com.android.server.wm.ActivityStackSupervisor;
import com.android.server.wm.ActivityStarter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActivityStartController {
    private static final int DO_PENDING_ACTIVITY_LAUNCHES_MSG = 1;
    private static final String TAG = "ActivityTaskManager";
    boolean mCheckedForSetup;
    private final ActivityStarter.Factory mFactory;
    private final Handler mHandler;
    private ActivityRecord mLastHomeActivityStartRecord;
    private int mLastHomeActivityStartResult;
    private ActivityStarter mLastStarter;
    private final ArrayList<ActivityStackSupervisor.PendingActivityLaunch> mPendingActivityLaunches;
    private final PendingRemoteAnimationRegistry mPendingRemoteAnimationRegistry;
    /* access modifiers changed from: private */
    public final ActivityTaskManagerService mService;
    private final ActivityStackSupervisor mSupervisor;
    private ActivityRecord[] tmpOutRecord;

    private final class StartHandler extends Handler {
        public StartHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                synchronized (ActivityStartController.this.mService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        ActivityStartController.this.doPendingActivityLaunches(true);
                    } catch (Throwable th) {
                        while (true) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    ActivityStartController(ActivityTaskManagerService service) {
        this(service, service.mStackSupervisor, new ActivityStarter.DefaultFactory(service, service.mStackSupervisor, new ActivityStartInterceptor(service, service.mStackSupervisor)));
    }

    @VisibleForTesting
    ActivityStartController(ActivityTaskManagerService service, ActivityStackSupervisor supervisor, ActivityStarter.Factory factory) {
        this.tmpOutRecord = new ActivityRecord[1];
        this.mPendingActivityLaunches = new ArrayList<>();
        this.mCheckedForSetup = false;
        this.mService = service;
        this.mSupervisor = supervisor;
        this.mHandler = new StartHandler(this.mService.mH.getLooper());
        this.mFactory = factory;
        this.mFactory.setController(this);
        this.mPendingRemoteAnimationRegistry = new PendingRemoteAnimationRegistry(service, service.mH);
    }

    /* access modifiers changed from: package-private */
    public ActivityStarter obtainStarter(Intent intent, String reason) {
        return this.mFactory.obtain().setIntent(intent).setReason(reason);
    }

    /* access modifiers changed from: package-private */
    public void onExecutionComplete(ActivityStarter starter) {
        if (this.mLastStarter == null) {
            this.mLastStarter = this.mFactory.obtain();
        }
        this.mLastStarter.set(starter);
        this.mFactory.recycle(starter);
    }

    /* access modifiers changed from: package-private */
    public void postStartActivityProcessingForLastStarter(ActivityRecord r, int result, ActivityStack targetStack) {
        ActivityStarter activityStarter = this.mLastStarter;
        if (activityStarter != null) {
            activityStarter.postStartActivityProcessing(r, result, targetStack);
        }
    }

    /* access modifiers changed from: package-private */
    public void startHomeActivity(Intent intent, ActivityInfo aInfo, String reason, int displayId) {
        ActivityOptions options = ActivityOptions.makeBasic();
        options.setLaunchWindowingMode(1);
        if (!ActivityRecord.isResolverActivity(aInfo.name)) {
            options.setLaunchActivityType(2);
        }
        options.setLaunchDisplayId(displayId);
        this.mLastHomeActivityStartResult = obtainStarter(intent, "startHomeActivity: " + reason).setOutActivity(this.tmpOutRecord).setCallingUid(0).setActivityInfo(aInfo).setActivityOptions(options.toBundle()).execute();
        this.mLastHomeActivityStartRecord = this.tmpOutRecord[0];
        ActivityDisplay display = this.mService.mRootActivityContainer.getActivityDisplay(displayId);
        ActivityStack homeStack = display != null ? display.getHomeStack() : null;
        if (homeStack != null && homeStack.mInResumeTopActivity) {
            this.mSupervisor.scheduleResumeTopActivities();
        }
    }

    /* access modifiers changed from: package-private */
    public void startSetupActivity() {
        String vers;
        if (!this.mCheckedForSetup) {
            ContentResolver resolver = this.mService.mContext.getContentResolver();
            if (this.mService.mFactoryTest != 1 && Settings.Global.getInt(resolver, "device_provisioned", 0) != 0) {
                this.mCheckedForSetup = true;
                Intent intent = new Intent("android.intent.action.UPGRADE_SETUP");
                List<ResolveInfo> ris = this.mService.mContext.getPackageManager().queryIntentActivities(intent, 1049728);
                if (!ris.isEmpty()) {
                    ResolveInfo ri = ris.get(0);
                    if (ri.activityInfo.metaData != null) {
                        vers = ri.activityInfo.metaData.getString("android.SETUP_VERSION");
                    } else {
                        vers = null;
                    }
                    if (vers == null && ri.activityInfo.applicationInfo.metaData != null) {
                        vers = ri.activityInfo.applicationInfo.metaData.getString("android.SETUP_VERSION");
                    }
                    String lastVers = Settings.Secure.getString(resolver, "last_setup_shown");
                    if (vers != null && !vers.equals(lastVers)) {
                        intent.setFlags(268435456);
                        intent.setComponent(new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name));
                        obtainStarter(intent, "startSetupActivity").setCallingUid(0).setActivityInfo(ri.activityInfo).execute();
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int checkTargetUser(int targetUserId, boolean validateIncomingUser, int realCallingPid, int realCallingUid, String reason) {
        if (validateIncomingUser) {
            return this.mService.handleIncomingUser(realCallingPid, realCallingUid, targetUserId, reason);
        }
        this.mService.mAmInternal.ensureNotSpecialUser(targetUserId);
        return targetUserId;
    }

    /* access modifiers changed from: package-private */
    public final int startActivityInPackage(int uid, int realCallingPid, int realCallingUid, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, SafeActivityOptions options, int userId, TaskRecord inTask, String reason, boolean validateIncomingUser, PendingIntentRecord originatingPendingIntent, boolean allowBackgroundActivityStart) {
        int userId2 = checkTargetUser(userId, validateIncomingUser, realCallingPid, realCallingUid, reason);
        int i = userId2;
        return obtainStarter(intent, reason).setCallingUid(uid).setRealCallingPid(realCallingPid).setRealCallingUid(realCallingUid).setCallingPackage(callingPackage).setResolvedType(resolvedType).setResultTo(resultTo).setResultWho(resultWho).setRequestCode(requestCode).setStartFlags(startFlags).setActivityOptions(options).setMayWait(userId2).setInTask(inTask).setOriginatingPendingIntent(originatingPendingIntent).setAllowBackgroundActivityStart(allowBackgroundActivityStart).execute();
    }

    /* access modifiers changed from: package-private */
    public final int startActivitiesInPackage(int uid, String callingPackage, Intent[] intents, String[] resolvedTypes, IBinder resultTo, SafeActivityOptions options, int userId, boolean validateIncomingUser, PendingIntentRecord originatingPendingIntent, boolean allowBackgroundActivityStart) {
        return startActivitiesInPackage(uid, 0, -1, callingPackage, intents, resolvedTypes, resultTo, options, userId, validateIncomingUser, originatingPendingIntent, allowBackgroundActivityStart);
    }

    /* access modifiers changed from: package-private */
    public final int startActivitiesInPackage(int uid, int realCallingPid, int realCallingUid, String callingPackage, Intent[] intents, String[] resolvedTypes, IBinder resultTo, SafeActivityOptions options, int userId, boolean validateIncomingUser, PendingIntentRecord originatingPendingIntent, boolean allowBackgroundActivityStart) {
        return startActivities((IApplicationThread) null, uid, realCallingPid, realCallingUid, callingPackage, intents, resolvedTypes, resultTo, options, checkTargetUser(userId, validateIncomingUser, Binder.getCallingPid(), Binder.getCallingUid(), "startActivityInPackage"), "startActivityInPackage", originatingPendingIntent, allowBackgroundActivityStart);
    }

    /* Debug info: failed to restart local var, previous not found, register: 26 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x019b, code lost:
        monitor-exit(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:102:?, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x019f, code lost:
        android.os.Binder.restoreCallingIdentity(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x01a4, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x01a5, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:107:?, code lost:
        monitor-exit(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:?, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x01aa, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x01ab, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x01ad, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0143, code lost:
        r2 = r39;
        r18 = r3;
        r19 = r10;
        r11 = r34;
        r3 = r37;
        r10 = r38;
        r6 = new com.android.server.wm.ActivityRecord[1];
        r13 = r1.mService.mGlobalLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0157, code lost:
        monitor-enter(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:?, code lost:
        com.android.server.wm.WindowManagerService.boostPriorityForLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x015b, code lost:
        r14 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x015e, code lost:
        if (r14 >= r12.length) goto L_0x019b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0160, code lost:
        r0 = r12[r14].setOutActivity(r6).execute();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x016a, code lost:
        if (r0 >= 0) goto L_0x0187;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x016c, code lost:
        r15 = r14 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x016f, code lost:
        if (r15 >= r12.length) goto L_0x017f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0171, code lost:
        r1.mFactory.recycle(r12[r15]);
        r15 = r15 + 1;
        r1 = r26;
        r2 = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x017f, code lost:
        monitor-exit(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x0180, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
        android.os.Binder.restoreCallingIdentity(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x0186, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x018a, code lost:
        if (r6[0] == null) goto L_0x0191;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x018c, code lost:
        r1 = r6[0].appToken;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x0191, code lost:
        r1 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x0193, code lost:
        r11 = r1;
        r14 = r14 + 1;
        r1 = r26;
        r2 = r39;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int startActivities(android.app.IApplicationThread r27, int r28, int r29, int r30, java.lang.String r31, android.content.Intent[] r32, java.lang.String[] r33, android.os.IBinder r34, com.android.server.wm.SafeActivityOptions r35, int r36, java.lang.String r37, com.android.server.am.PendingIntentRecord r38, boolean r39) {
        /*
            r26 = this;
            r1 = r26
            r2 = r27
            r3 = r32
            r4 = r33
            if (r3 == 0) goto L_0x01e8
            if (r4 == 0) goto L_0x01da
            int r0 = r3.length
            int r5 = r4.length
            if (r0 != r5) goto L_0x01cc
            if (r29 == 0) goto L_0x0015
            r0 = r29
            goto L_0x0019
        L_0x0015:
            int r0 = android.os.Binder.getCallingPid()
        L_0x0019:
            r5 = r0
            r0 = -1
            r6 = r30
            if (r6 == r0) goto L_0x0021
            r7 = r6
            goto L_0x0025
        L_0x0021:
            int r7 = android.os.Binder.getCallingUid()
        L_0x0025:
            if (r28 < 0) goto L_0x002c
            r8 = -1
            r9 = r28
            goto L_0x0038
        L_0x002c:
            if (r2 != 0) goto L_0x0031
            r8 = r5
            r9 = r7
            goto L_0x0038
        L_0x0031:
            r8 = r0
            r9 = r0
            r25 = r9
            r9 = r8
            r8 = r25
        L_0x0038:
            long r10 = android.os.Binder.clearCallingIdentity()
            com.android.server.wm.-$$Lambda$ActivityStartController$6bTAPCVeDq_D4Y53Y5WNfMK4xBE r12 = com.android.server.wm.$$Lambda$ActivityStartController$6bTAPCVeDq_D4Y53Y5WNfMK4xBE.INSTANCE     // Catch:{ all -> 0x01bd }
            java.lang.Object[] r12 = com.android.internal.util.ArrayUtils.filterNotNull(r3, r12)     // Catch:{ all -> 0x01bd }
            android.content.Intent[] r12 = (android.content.Intent[]) r12     // Catch:{ all -> 0x01bd }
            r3 = r12
            int r12 = r3.length     // Catch:{ all -> 0x01b1 }
            com.android.server.wm.ActivityStarter[] r12 = new com.android.server.wm.ActivityStarter[r12]     // Catch:{ all -> 0x01b1 }
            r13 = 0
            r14 = r13
        L_0x004a:
            int r15 = r3.length     // Catch:{ all -> 0x01b1 }
            r16 = 0
            if (r14 >= r15) goto L_0x0143
            r15 = r3[r14]     // Catch:{ all -> 0x013e }
            boolean r17 = r15.hasFileDescriptors()     // Catch:{ all -> 0x013e }
            if (r17 != 0) goto L_0x012a
            android.content.Intent r0 = new android.content.Intent     // Catch:{ all -> 0x013e }
            r0.<init>(r15)     // Catch:{ all -> 0x013e }
            com.android.server.wm.ActivityStackSupervisor r15 = r1.mSupervisor     // Catch:{ all -> 0x013e }
            r20 = r4[r14]     // Catch:{ all -> 0x013e }
            r21 = 0
            r22 = 0
            r13 = -10000(0xffffffffffffd8f0, float:NaN)
            int r24 = com.android.server.wm.ActivityStarter.computeResolveFilterUid(r9, r7, r13)     // Catch:{ all -> 0x013e }
            r18 = r15
            r19 = r0
            r23 = r36
            android.content.pm.ActivityInfo r13 = r18.resolveActivity(r19, r20, r21, r22, r23, r24)     // Catch:{ all -> 0x013e }
            com.android.server.wm.ActivityTaskManagerService r15 = r1.mService     // Catch:{ all -> 0x013e }
            android.app.ActivityManagerInternal r15 = r15.mAmInternal     // Catch:{ all -> 0x013e }
            r6 = r36
            android.content.pm.ActivityInfo r15 = r15.getActivityInfoForUser(r13, r6)     // Catch:{ all -> 0x013e }
            r13 = r15
            if (r13 == 0) goto L_0x009f
            android.content.pm.ApplicationInfo r15 = r13.applicationInfo     // Catch:{ all -> 0x0092 }
            int r15 = r15.privateFlags     // Catch:{ all -> 0x0092 }
            r15 = r15 & 2
            if (r15 != 0) goto L_0x008a
            goto L_0x009f
        L_0x008a:
            java.lang.IllegalArgumentException r15 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0092 }
            java.lang.String r6 = "FLAG_CANT_SAVE_STATE not supported here"
            r15.<init>(r6)     // Catch:{ all -> 0x0092 }
            throw r15     // Catch:{ all -> 0x0092 }
        L_0x0092:
            r0 = move-exception
            r18 = r3
            r19 = r10
            r11 = r34
            r3 = r37
            r10 = r38
            goto L_0x01c8
        L_0x009f:
            int r6 = r3.length     // Catch:{ all -> 0x013e }
            r15 = 1
            int r6 = r6 - r15
            if (r14 != r6) goto L_0x00a6
            r6 = 1
            goto L_0x00a7
        L_0x00a6:
            r6 = 0
        L_0x00a7:
            if (r6 == 0) goto L_0x00ac
            r16 = r35
            goto L_0x00ad
        L_0x00ac:
        L_0x00ad:
            r15 = r16
            r18 = r3
            r19 = r10
            r3 = r37
            com.android.server.wm.ActivityStarter r10 = r1.obtainStarter(r0, r3)     // Catch:{ all -> 0x0121 }
            com.android.server.wm.ActivityStarter r10 = r10.setCaller(r2)     // Catch:{ all -> 0x0121 }
            r11 = r4[r14]     // Catch:{ all -> 0x0121 }
            com.android.server.wm.ActivityStarter r10 = r10.setResolvedType(r11)     // Catch:{ all -> 0x0121 }
            com.android.server.wm.ActivityStarter r10 = r10.setActivityInfo(r13)     // Catch:{ all -> 0x0121 }
            r11 = r34
            com.android.server.wm.ActivityStarter r10 = r10.setResultTo(r11)     // Catch:{ all -> 0x011f }
            r2 = -1
            com.android.server.wm.ActivityStarter r10 = r10.setRequestCode(r2)     // Catch:{ all -> 0x011f }
            com.android.server.wm.ActivityStarter r10 = r10.setCallingPid(r8)     // Catch:{ all -> 0x011f }
            com.android.server.wm.ActivityStarter r10 = r10.setCallingUid(r9)     // Catch:{ all -> 0x011f }
            r2 = r31
            com.android.server.wm.ActivityStarter r10 = r10.setCallingPackage(r2)     // Catch:{ all -> 0x011f }
            com.android.server.wm.ActivityStarter r10 = r10.setRealCallingPid(r5)     // Catch:{ all -> 0x011f }
            com.android.server.wm.ActivityStarter r10 = r10.setRealCallingUid(r7)     // Catch:{ all -> 0x011f }
            com.android.server.wm.ActivityStarter r10 = r10.setActivityOptions((com.android.server.wm.SafeActivityOptions) r15)     // Catch:{ all -> 0x011f }
            android.content.ComponentName r16 = r0.getComponent()     // Catch:{ all -> 0x011f }
            if (r16 == 0) goto L_0x00f6
            r21 = r0
            r0 = 1
            goto L_0x00f9
        L_0x00f6:
            r21 = r0
            r0 = 0
        L_0x00f9:
            com.android.server.wm.ActivityStarter r0 = r10.setComponentSpecified(r0)     // Catch:{ all -> 0x011f }
            com.android.server.wm.ActivityStarter r0 = r0.setAllowPendingRemoteAnimationRegistryLookup(r6)     // Catch:{ all -> 0x011f }
            r10 = r38
            com.android.server.wm.ActivityStarter r0 = r0.setOriginatingPendingIntent(r10)     // Catch:{ all -> 0x011d }
            r2 = r39
            com.android.server.wm.ActivityStarter r0 = r0.setAllowBackgroundActivityStart(r2)     // Catch:{ all -> 0x01af }
            r12[r14] = r0     // Catch:{ all -> 0x01af }
            int r14 = r14 + 1
            r2 = r27
            r6 = r30
            r3 = r18
            r10 = r19
            r0 = -1
            r13 = 0
            goto L_0x004a
        L_0x011d:
            r0 = move-exception
            goto L_0x0126
        L_0x011f:
            r0 = move-exception
            goto L_0x0124
        L_0x0121:
            r0 = move-exception
            r11 = r34
        L_0x0124:
            r10 = r38
        L_0x0126:
            r2 = r39
            goto L_0x01c8
        L_0x012a:
            r2 = r39
            r18 = r3
            r19 = r10
            r11 = r34
            r3 = r37
            r10 = r38
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x01af }
            java.lang.String r6 = "File descriptors passed in Intent"
            r0.<init>(r6)     // Catch:{ all -> 0x01af }
            throw r0     // Catch:{ all -> 0x01af }
        L_0x013e:
            r0 = move-exception
            r2 = r39
            goto L_0x01b2
        L_0x0143:
            r2 = r39
            r18 = r3
            r19 = r10
            r11 = r34
            r3 = r37
            r10 = r38
            r0 = 1
            com.android.server.wm.ActivityRecord[] r0 = new com.android.server.wm.ActivityRecord[r0]     // Catch:{ all -> 0x01af }
            r6 = r0
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService     // Catch:{ all -> 0x01af }
            com.android.server.wm.WindowManagerGlobalLock r13 = r0.mGlobalLock     // Catch:{ all -> 0x01af }
            monitor-enter(r13)     // Catch:{ all -> 0x01af }
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x01a5 }
            r0 = 0
            r14 = r0
        L_0x015d:
            int r0 = r12.length     // Catch:{ all -> 0x01ad }
            if (r14 >= r0) goto L_0x019b
            r0 = r12[r14]     // Catch:{ all -> 0x01ad }
            com.android.server.wm.ActivityStarter r0 = r0.setOutActivity(r6)     // Catch:{ all -> 0x01ad }
            int r0 = r0.execute()     // Catch:{ all -> 0x01ad }
            if (r0 >= 0) goto L_0x0187
            int r15 = r14 + 1
        L_0x016e:
            int r2 = r12.length     // Catch:{ all -> 0x01ad }
            if (r15 >= r2) goto L_0x017f
            com.android.server.wm.ActivityStarter$Factory r2 = r1.mFactory     // Catch:{ all -> 0x01ad }
            r1 = r12[r15]     // Catch:{ all -> 0x01ad }
            r2.recycle(r1)     // Catch:{ all -> 0x01ad }
            int r15 = r15 + 1
            r1 = r26
            r2 = r39
            goto L_0x016e
        L_0x017f:
            monitor-exit(r13)     // Catch:{ all -> 0x01ad }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            android.os.Binder.restoreCallingIdentity(r19)
            return r0
        L_0x0187:
            r1 = 0
            r2 = r6[r1]     // Catch:{ all -> 0x01ad }
            if (r2 == 0) goto L_0x0191
            r2 = r6[r1]     // Catch:{ all -> 0x01ad }
            android.view.IApplicationToken$Stub r1 = r2.appToken     // Catch:{ all -> 0x01ad }
            goto L_0x0193
        L_0x0191:
            r1 = r16
        L_0x0193:
            r11 = r1
            int r14 = r14 + 1
            r1 = r26
            r2 = r39
            goto L_0x015d
        L_0x019b:
            monitor-exit(r13)     // Catch:{ all -> 0x01ad }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x01ab }
            android.os.Binder.restoreCallingIdentity(r19)
            r0 = 0
            return r0
        L_0x01a5:
            r0 = move-exception
        L_0x01a6:
            monitor-exit(r13)     // Catch:{ all -> 0x01ad }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x01ab }
            throw r0     // Catch:{ all -> 0x01ab }
        L_0x01ab:
            r0 = move-exception
            goto L_0x01c8
        L_0x01ad:
            r0 = move-exception
            goto L_0x01a6
        L_0x01af:
            r0 = move-exception
            goto L_0x01c8
        L_0x01b1:
            r0 = move-exception
        L_0x01b2:
            r18 = r3
            r19 = r10
            r11 = r34
            r3 = r37
            r10 = r38
            goto L_0x01c8
        L_0x01bd:
            r0 = move-exception
            r3 = r37
            r19 = r10
            r11 = r34
            r10 = r38
            r18 = r32
        L_0x01c8:
            android.os.Binder.restoreCallingIdentity(r19)
            throw r0
        L_0x01cc:
            r11 = r34
            r3 = r37
            r10 = r38
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "intents are length different than resolvedTypes"
            r0.<init>(r1)
            throw r0
        L_0x01da:
            r11 = r34
            r3 = r37
            r10 = r38
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            java.lang.String r1 = "resolvedTypes is null"
            r0.<init>(r1)
            throw r0
        L_0x01e8:
            r11 = r34
            r3 = r37
            r10 = r38
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            java.lang.String r1 = "intents is null"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStartController.startActivities(android.app.IApplicationThread, int, int, int, java.lang.String, android.content.Intent[], java.lang.String[], android.os.IBinder, com.android.server.wm.SafeActivityOptions, int, java.lang.String, com.android.server.am.PendingIntentRecord, boolean):int");
    }

    static /* synthetic */ Intent[] lambda$startActivities$0(int x$0) {
        return new Intent[x$0];
    }

    /* access modifiers changed from: package-private */
    public void schedulePendingActivityLaunches(long delayMs) {
        this.mHandler.removeMessages(1);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), delayMs);
    }

    /* access modifiers changed from: package-private */
    public void doPendingActivityLaunches(boolean doResume) {
        while (!this.mPendingActivityLaunches.isEmpty()) {
            boolean z = false;
            ActivityStackSupervisor.PendingActivityLaunch pal = this.mPendingActivityLaunches.remove(0);
            if (doResume && this.mPendingActivityLaunches.isEmpty()) {
                z = true;
            }
            boolean resume = z;
            ActivityStarter starter = obtainStarter((Intent) null, "pendingActivityLaunch");
            try {
                starter.startResolvedActivity(pal.r, pal.sourceRecord, (IVoiceInteractionSession) null, (IVoiceInteractor) null, pal.startFlags, resume, pal.r.pendingOptions, (TaskRecord) null);
            } catch (Exception e) {
                Slog.e(TAG, "Exception during pending activity launch pal=" + pal, e);
                pal.sendErrorResult(e.getMessage());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void addPendingActivityLaunch(ActivityStackSupervisor.PendingActivityLaunch launch) {
        this.mPendingActivityLaunches.add(launch);
    }

    /* access modifiers changed from: package-private */
    public boolean clearPendingActivityLaunches(String packageName) {
        int pendingLaunches = this.mPendingActivityLaunches.size();
        for (int palNdx = pendingLaunches - 1; palNdx >= 0; palNdx--) {
            ActivityRecord r = this.mPendingActivityLaunches.get(palNdx).r;
            if (r != null && r.packageName.equals(packageName)) {
                this.mPendingActivityLaunches.remove(palNdx);
            }
        }
        return this.mPendingActivityLaunches.size() < pendingLaunches;
    }

    /* access modifiers changed from: package-private */
    public void registerRemoteAnimationForNextActivityStart(String packageName, RemoteAnimationAdapter adapter) {
        this.mPendingRemoteAnimationRegistry.addPendingAnimation(packageName, adapter);
    }

    /* access modifiers changed from: package-private */
    public PendingRemoteAnimationRegistry getPendingRemoteAnimationRegistry() {
        return this.mPendingRemoteAnimationRegistry;
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix, String dumpPackage) {
        ActivityRecord activityRecord;
        pw.print(prefix);
        pw.print("mLastHomeActivityStartResult=");
        pw.println(this.mLastHomeActivityStartResult);
        if (this.mLastHomeActivityStartRecord != null) {
            pw.print(prefix);
            pw.println("mLastHomeActivityStartRecord:");
            this.mLastHomeActivityStartRecord.dump(pw, prefix + "  ");
        }
        boolean dump = true;
        boolean dumpPackagePresent = dumpPackage != null;
        ActivityStarter activityStarter = this.mLastStarter;
        if (activityStarter != null) {
            if (dumpPackagePresent && !activityStarter.relatedToPackage(dumpPackage) && ((activityRecord = this.mLastHomeActivityStartRecord) == null || !dumpPackage.equals(activityRecord.packageName))) {
                dump = false;
            }
            if (dump) {
                pw.print(prefix);
                this.mLastStarter.dump(pw, prefix + "  ");
                if (dumpPackagePresent) {
                    return;
                }
            }
        }
        if (dumpPackagePresent) {
            pw.print(prefix);
            pw.println("(nothing)");
        }
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        Iterator<ActivityStackSupervisor.PendingActivityLaunch> it = this.mPendingActivityLaunches.iterator();
        while (it.hasNext()) {
            it.next().r.writeIdentifierToProto(proto, fieldId);
        }
    }
}
