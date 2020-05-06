package com.android.server.wm;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.os.RemoteException;
import android.os.Trace;
import android.util.Slog;
import android.view.IRecentsAnimationRunner;
import com.android.server.wm.ActivityDisplay;
import com.android.server.wm.RecentsAnimationController;

class RecentsAnimation implements RecentsAnimationController.RecentsAnimationCallbacks, ActivityDisplay.OnStackOrderChangedListener {
    private static final boolean DEBUG = false;
    private static final String TAG = RecentsAnimation.class.getSimpleName();
    private final ActivityStartController mActivityStartController;
    private final int mCallingPid;
    private final ActivityDisplay mDefaultDisplay = this.mService.mRootActivityContainer.getDefaultDisplay();
    private ActivityRecord mLaunchedTargetActivity;
    private ActivityStack mRestoreTargetBehindStack;
    private final ActivityTaskManagerService mService;
    private final ActivityStackSupervisor mStackSupervisor;
    private int mTargetActivityType;
    private final WindowManagerService mWindowManager;

    RecentsAnimation(ActivityTaskManagerService atm, ActivityStackSupervisor stackSupervisor, ActivityStartController activityStartController, WindowManagerService wm, int callingPid) {
        this.mService = atm;
        this.mStackSupervisor = stackSupervisor;
        this.mActivityStartController = activityStartController;
        this.mWindowManager = wm;
        this.mCallingPid = callingPid;
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0059  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x005b  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x009d A[SYNTHETIC, Splitter:B:27:0x009d] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00b9 A[Catch:{ Exception -> 0x016d, all -> 0x0169 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startRecentsActivity(android.content.Intent r20, android.view.IRecentsAnimationRunner r21, android.content.ComponentName r22, int r23, @java.lang.Deprecated android.app.IAssistDataReceiver r24) {
        /*
            r19 = this;
            r7 = r19
            r8 = r20
            r9 = r21
            r10 = 64
            java.lang.String r0 = "RecentsAnimation#startRecentsActivity"
            android.os.Trace.traceBegin(r10, r0)
            com.android.server.wm.ActivityTaskManagerService r0 = r7.mService
            com.android.server.wm.RootActivityContainer r0 = r0.mRootActivityContainer
            com.android.server.wm.ActivityDisplay r0 = r0.getDefaultDisplay()
            com.android.server.wm.DisplayContent r12 = r0.mDisplayContent
            com.android.server.wm.WindowManagerService r0 = r7.mWindowManager
            boolean r0 = r0.canStartRecentsAnimation()
            if (r0 != 0) goto L_0x0023
            r7.notifyAnimationCancelBeforeStart(r9)
            return
        L_0x0023:
            com.android.server.wm.ActivityTaskManagerService r0 = r7.mService
            int r13 = r0.getCurrentUserId()
            android.content.ComponentName r0 = r20.getComponent()
            r14 = 2
            if (r0 == 0) goto L_0x0040
            android.content.ComponentName r0 = r20.getComponent()
            r15 = r22
            boolean r0 = r15.equals(r0)
            if (r0 == 0) goto L_0x0042
            r0 = 3
            goto L_0x0043
        L_0x0040:
            r15 = r22
        L_0x0042:
            r0 = r14
        L_0x0043:
            r7.mTargetActivityType = r0
            com.android.server.wm.ActivityDisplay r0 = r7.mDefaultDisplay
            int r1 = r7.mTargetActivityType
            r6 = 0
            com.android.server.wm.ActivityStack r1 = r0.getStack(r6, r1)
            android.content.ComponentName r0 = r20.getComponent()
            com.android.server.wm.ActivityRecord r2 = r7.getTargetActivity(r1, r0, r13)
            r0 = 1
            if (r2 == 0) goto L_0x005b
            r3 = r0
            goto L_0x005c
        L_0x005b:
            r3 = r6
        L_0x005c:
            r16 = r3
            if (r16 == 0) goto L_0x0072
            com.android.server.wm.ActivityDisplay r3 = r2.getDisplay()
            com.android.server.wm.ActivityStack r4 = r3.getStackAbove(r1)
            r7.mRestoreTargetBehindStack = r4
            com.android.server.wm.ActivityStack r4 = r7.mRestoreTargetBehindStack
            if (r4 != 0) goto L_0x0072
            r7.notifyAnimationCancelBeforeStart(r9)
            return
        L_0x0072:
            if (r2 == 0) goto L_0x0078
            boolean r3 = r2.visible
            if (r3 != 0) goto L_0x007f
        L_0x0078:
            com.android.server.wm.ActivityTaskManagerService r3 = r7.mService
            com.android.server.wm.RootActivityContainer r3 = r3.mRootActivityContainer
            r3.sendPowerHintForLaunchStartIfNeeded(r0, r2)
        L_0x007f:
            com.android.server.wm.ActivityStackSupervisor r3 = r7.mStackSupervisor
            com.android.server.wm.ActivityMetricsLogger r3 = r3.getActivityMetricsLogger()
            r3.notifyActivityLaunching(r8)
            com.android.server.wm.ActivityTaskManagerService r3 = r7.mService
            com.android.server.wm.ActivityTaskManagerService$H r3 = r3.mH
            com.android.server.wm.-$$Lambda$RecentsAnimation$e3kosml-870P6Bh_K_Z_6yyLHZk r4 = new com.android.server.wm.-$$Lambda$RecentsAnimation$e3kosml-870P6Bh_K_Z_6yyLHZk
            r4.<init>()
            r3.post(r4)
            com.android.server.wm.WindowManagerService r3 = r7.mWindowManager
            r3.deferSurfaceLayout()
            java.lang.String r3 = "startRecentsActivity"
            if (r16 == 0) goto L_0x00b9
            com.android.server.wm.ActivityDisplay r4 = r7.mDefaultDisplay     // Catch:{ Exception -> 0x016d, all -> 0x0169 }
            r4.moveStackBehindBottomMostVisibleStack(r1)     // Catch:{ Exception -> 0x016d, all -> 0x0169 }
            com.android.server.wm.TaskRecord r4 = r1.topTask()     // Catch:{ Exception -> 0x016d, all -> 0x0169 }
            com.android.server.wm.TaskRecord r5 = r2.getTaskRecord()     // Catch:{ Exception -> 0x016d, all -> 0x0169 }
            if (r4 == r5) goto L_0x00b3
            com.android.server.wm.TaskRecord r4 = r2.getTaskRecord()     // Catch:{ Exception -> 0x016d, all -> 0x0169 }
            r1.addTask(r4, r0, r3)     // Catch:{ Exception -> 0x016d, all -> 0x0169 }
        L_0x00b3:
            r10 = r23
            r17 = r1
            r11 = r2
            goto L_0x0117
        L_0x00b9:
            android.app.ActivityOptions r4 = android.app.ActivityOptions.makeBasic()     // Catch:{ Exception -> 0x016d, all -> 0x0169 }
            int r5 = r7.mTargetActivityType     // Catch:{ Exception -> 0x016d, all -> 0x0169 }
            r4.setLaunchActivityType(r5)     // Catch:{ Exception -> 0x016d, all -> 0x0169 }
            r4.setAvoidMoveToFront()     // Catch:{ Exception -> 0x016d, all -> 0x0169 }
            r5 = 268500992(0x10010000, float:2.5440764E-29)
            r8.addFlags(r5)     // Catch:{ Exception -> 0x016d, all -> 0x0169 }
            com.android.server.wm.ActivityStartController r5 = r7.mActivityStartController     // Catch:{ Exception -> 0x016d, all -> 0x0169 }
            java.lang.String r10 = "startRecentsActivity_noTargetActivity"
            com.android.server.wm.ActivityStarter r5 = r5.obtainStarter(r8, r10)     // Catch:{ Exception -> 0x016d, all -> 0x0169 }
            r10 = r23
            com.android.server.wm.ActivityStarter r5 = r5.setCallingUid(r10)     // Catch:{ Exception -> 0x0167 }
            java.lang.String r11 = r22.getPackageName()     // Catch:{ Exception -> 0x0167 }
            com.android.server.wm.ActivityStarter r5 = r5.setCallingPackage(r11)     // Catch:{ Exception -> 0x0167 }
            android.os.Bundle r11 = r4.toBundle()     // Catch:{ Exception -> 0x0167 }
            com.android.server.wm.SafeActivityOptions r11 = com.android.server.wm.SafeActivityOptions.fromBundle(r11)     // Catch:{ Exception -> 0x0167 }
            com.android.server.wm.ActivityStarter r5 = r5.setActivityOptions((com.android.server.wm.SafeActivityOptions) r11)     // Catch:{ Exception -> 0x0167 }
            com.android.server.wm.ActivityStarter r5 = r5.setMayWait(r13)     // Catch:{ Exception -> 0x0167 }
            r5.execute()     // Catch:{ Exception -> 0x0167 }
            com.android.server.wm.ActivityDisplay r5 = r7.mDefaultDisplay     // Catch:{ Exception -> 0x0167 }
            int r11 = r7.mTargetActivityType     // Catch:{ Exception -> 0x0167 }
            com.android.server.wm.ActivityStack r5 = r5.getStack(r6, r11)     // Catch:{ Exception -> 0x0167 }
            r1 = r5
            android.content.ComponentName r5 = r20.getComponent()     // Catch:{ Exception -> 0x0167 }
            com.android.server.wm.ActivityRecord r5 = r7.getTargetActivity(r1, r5, r13)     // Catch:{ Exception -> 0x0167 }
            r2 = r5
            com.android.server.wm.ActivityDisplay r5 = r7.mDefaultDisplay     // Catch:{ Exception -> 0x0167 }
            r5.moveStackBehindBottomMostVisibleStack(r1)     // Catch:{ Exception -> 0x0167 }
            com.android.server.wm.WindowManagerService r5 = r7.mWindowManager     // Catch:{ Exception -> 0x0167 }
            r5.prepareAppTransition(r6, r6)     // Catch:{ Exception -> 0x0167 }
            com.android.server.wm.WindowManagerService r5 = r7.mWindowManager     // Catch:{ Exception -> 0x0167 }
            r5.executeAppTransition()     // Catch:{ Exception -> 0x0167 }
            r17 = r1
            r11 = r2
        L_0x0117:
            r11.mLaunchTaskBehind = r0     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            r7.mLaunchedTargetActivity = r11     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            com.android.server.wm.WindowManagerService r1 = r7.mWindowManager     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            r1.cancelRecentsAnimationSynchronously(r14, r3)     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            com.android.server.wm.WindowManagerService r1 = r7.mWindowManager     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            int r2 = r7.mTargetActivityType     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            com.android.server.wm.ActivityDisplay r3 = r7.mDefaultDisplay     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            int r5 = r3.mDisplayId     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            com.android.server.wm.ActivityStackSupervisor r3 = r7.mStackSupervisor     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            com.android.server.wm.RecentTasks r3 = r3.mRecentTasks     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            android.util.SparseBooleanArray r18 = r3.getRecentTaskIds()     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            r3 = r21
            r4 = r19
            r14 = r6
            r6 = r18
            r1.initializeRecentsAnimation(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            com.android.server.wm.ActivityTaskManagerService r1 = r7.mService     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            com.android.server.wm.RootActivityContainer r1 = r1.mRootActivityContainer     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            r2 = 0
            r1.ensureActivitiesVisible(r2, r14, r0)     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            com.android.server.wm.ActivityStackSupervisor r0 = r7.mStackSupervisor     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            com.android.server.wm.ActivityMetricsLogger r0 = r0.getActivityMetricsLogger()     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            r1 = 2
            r0.notifyActivityLaunched(r1, r11)     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            com.android.server.wm.ActivityDisplay r0 = r7.mDefaultDisplay     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            r0.registerStackOrderChangedListener(r7)     // Catch:{ Exception -> 0x0162, all -> 0x015d }
            com.android.server.wm.WindowManagerService r0 = r7.mWindowManager
            r0.continueSurfaceLayout()
            r1 = 64
            android.os.Trace.traceEnd(r1)
            return
        L_0x015d:
            r0 = move-exception
            r2 = r11
            r1 = r17
            goto L_0x017a
        L_0x0162:
            r0 = move-exception
            r2 = r11
            r1 = r17
            goto L_0x0170
        L_0x0167:
            r0 = move-exception
            goto L_0x0170
        L_0x0169:
            r0 = move-exception
            r10 = r23
            goto L_0x017a
        L_0x016d:
            r0 = move-exception
            r10 = r23
        L_0x0170:
            java.lang.String r3 = TAG     // Catch:{ all -> 0x0179 }
            java.lang.String r4 = "Failed to start recents activity"
            android.util.Slog.e(r3, r4, r0)     // Catch:{ all -> 0x0179 }
            throw r0     // Catch:{ all -> 0x0179 }
        L_0x0179:
            r0 = move-exception
        L_0x017a:
            com.android.server.wm.WindowManagerService r3 = r7.mWindowManager
            r3.continueSurfaceLayout()
            r3 = 64
            android.os.Trace.traceEnd(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.RecentsAnimation.startRecentsActivity(android.content.Intent, android.view.IRecentsAnimationRunner, android.content.ComponentName, int, android.app.IAssistDataReceiver):void");
    }

    public /* synthetic */ void lambda$startRecentsActivity$0$RecentsAnimation() {
        this.mService.mAmInternal.setRunningRemoteAnimation(this.mCallingPid, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: finishAnimation */
    public void lambda$onAnimationFinished$3$RecentsAnimation(@RecentsAnimationController.ReorderMode int reorderMode, boolean sendUserLeaveHint) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mDefaultDisplay.unregisterStackOrderChangedListener(this);
                RecentsAnimationController controller = this.mWindowManager.getRecentsAnimationController();
                if (controller == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                if (reorderMode != 0) {
                    this.mService.mRootActivityContainer.sendPowerHintForLaunchEndIfNeeded();
                }
                if (reorderMode == 1) {
                    this.mService.stopAppSwitches();
                }
                this.mService.mH.post(new Runnable() {
                    public final void run() {
                        RecentsAnimation.this.lambda$finishAnimation$1$RecentsAnimation();
                    }
                });
                this.mWindowManager.inSurfaceTransaction(new Runnable(reorderMode, sendUserLeaveHint, controller) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ boolean f$2;
                    private final /* synthetic */ RecentsAnimationController f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        RecentsAnimation.this.lambda$finishAnimation$2$RecentsAnimation(this.f$1, this.f$2, this.f$3);
                    }
                });
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public /* synthetic */ void lambda$finishAnimation$1$RecentsAnimation() {
        this.mService.mAmInternal.setRunningRemoteAnimation(this.mCallingPid, false);
    }

    /* Debug info: failed to restart local var, previous not found, register: 15 */
    public /* synthetic */ void lambda$finishAnimation$2$RecentsAnimation(int reorderMode, boolean sendUserLeaveHint, RecentsAnimationController controller) {
        ActivityRecord activityRecord;
        int i = reorderMode;
        Trace.traceBegin(64, "RecentsAnimation#onAnimationFinished_inSurfaceTransaction");
        this.mWindowManager.deferSurfaceLayout();
        try {
            this.mWindowManager.cleanupRecentsAnimation(i);
            ActivityStack targetStack = this.mDefaultDisplay.getStack(0, this.mTargetActivityType);
            if (targetStack != null) {
                activityRecord = targetStack.isInStackLocked(this.mLaunchedTargetActivity);
            } else {
                activityRecord = null;
            }
            ActivityRecord targetActivity = activityRecord;
            if (targetActivity == null) {
                this.mWindowManager.continueSurfaceLayout();
                Trace.traceEnd(64);
                return;
            }
            targetActivity.mLaunchTaskBehind = false;
            if (i == 1) {
                this.mStackSupervisor.mNoAnimActivities.add(targetActivity);
                if (sendUserLeaveHint) {
                    this.mStackSupervisor.mUserLeaving = true;
                    targetStack.moveTaskToFrontLocked(targetActivity.getTaskRecord(), true, (ActivityOptions) null, targetActivity.appTimeTracker, "RecentsAnimation.onAnimationFinished()");
                } else {
                    targetStack.moveToFront("RecentsAnimation.onAnimationFinished()");
                }
            } else if (i == 2) {
                targetActivity.getDisplay().moveStackBehindStack(targetStack, this.mRestoreTargetBehindStack);
            } else {
                if (!controller.shouldCancelWithDeferredScreenshot() && !targetStack.isFocusedStackOnDisplay()) {
                    targetStack.ensureActivitiesVisibleLocked((ActivityRecord) null, 0, false);
                }
                this.mWindowManager.continueSurfaceLayout();
                Trace.traceEnd(64);
                return;
            }
            this.mWindowManager.prepareAppTransition(0, false);
            this.mService.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
            this.mService.mRootActivityContainer.resumeFocusedStacksTopActivities();
            this.mWindowManager.executeAppTransition();
            this.mWindowManager.checkSplitScreenMinimizedChanged(true);
            this.mWindowManager.continueSurfaceLayout();
            Trace.traceEnd(64);
        } catch (Exception e) {
            Slog.e(TAG, "Failed to clean up recents activity", e);
            throw e;
        } catch (Throwable th) {
            this.mWindowManager.continueSurfaceLayout();
            Trace.traceEnd(64);
            throw th;
        }
    }

    public void onAnimationFinished(@RecentsAnimationController.ReorderMode int reorderMode, boolean runSychronously, boolean sendUserLeaveHint) {
        if (runSychronously) {
            lambda$onAnimationFinished$3$RecentsAnimation(reorderMode, sendUserLeaveHint);
        } else {
            this.mService.mH.post(new Runnable(reorderMode, sendUserLeaveHint) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    RecentsAnimation.this.lambda$onAnimationFinished$3$RecentsAnimation(this.f$1, this.f$2);
                }
            });
        }
    }

    public void onStackOrderChanged(ActivityStack stack) {
        RecentsAnimationController controller;
        if (this.mDefaultDisplay.getIndexOf(stack) != -1 && stack.shouldBeVisible((ActivityRecord) null) && (controller = this.mWindowManager.getRecentsAnimationController()) != null) {
            this.mService.mRootActivityContainer.getDefaultDisplay().mDisplayContent.mBoundsAnimationController.setAnimationType(controller.shouldCancelWithDeferredScreenshot() ? 1 : 0);
            if ((!controller.isAnimatingTask((Task) stack.getTaskStack().getTopChild()) || controller.isTargetApp(stack.getTopActivity().mAppWindowToken)) && controller.shouldCancelWithDeferredScreenshot()) {
                controller.cancelOnNextTransitionStart();
            } else {
                this.mWindowManager.cancelRecentsAnimationSynchronously(0, "stackOrderChanged");
            }
        }
    }

    private void notifyAnimationCancelBeforeStart(IRecentsAnimationRunner recentsAnimationRunner) {
        try {
            recentsAnimationRunner.onAnimationCanceled(false);
        } catch (RemoteException e) {
            Slog.e(TAG, "Failed to cancel recents animation before start", e);
        }
    }

    private ActivityStack getTopNonAlwaysOnTopStack() {
        for (int i = this.mDefaultDisplay.getChildCount() - 1; i >= 0; i--) {
            ActivityStack s = this.mDefaultDisplay.getChildAt(i);
            if (!s.getWindowConfiguration().isAlwaysOnTop()) {
                return s;
            }
        }
        return null;
    }

    private ActivityRecord getTargetActivity(ActivityStack targetStack, ComponentName component, int userId) {
        if (targetStack == null) {
            return null;
        }
        for (int i = targetStack.getChildCount() - 1; i >= 0; i--) {
            TaskRecord task = targetStack.getChildAt(i);
            if (task.userId == userId && task.getBaseIntent().getComponent().equals(component)) {
                return task.getTopActivity();
            }
        }
        return null;
    }
}
