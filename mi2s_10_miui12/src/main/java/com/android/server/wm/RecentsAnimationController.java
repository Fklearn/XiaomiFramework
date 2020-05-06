package com.android.server.wm;

import android.app.ActivityManager;
import android.app.WindowConfiguration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.proto.ProtoOutputStream;
import android.view.IRecentsAnimationController;
import android.view.IRecentsAnimationRunner;
import android.view.InputWindowHandle;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.android.server.wm.SurfaceAnimator;
import com.android.server.wm.WindowManagerInternal;
import com.android.server.wm.utils.InsetUtils;
import com.google.android.collect.Sets;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RecentsAnimationController implements IBinder.DeathRecipient {
    private static final long FAILSAFE_DELAY = 1000;
    public static final int REORDER_KEEP_IN_PLACE = 0;
    public static final int REORDER_MOVE_TO_ORIGINAL_POSITION = 2;
    public static final int REORDER_MOVE_TO_TOP = 1;
    private static final String TAG = RecentsAnimationController.class.getSimpleName();
    final WindowManagerInternal.AppTransitionListener mAppTransitionListener = new WindowManagerInternal.AppTransitionListener() {
        public int onAppTransitionStartingLocked(int transit, long duration, long statusBarAnimationStartTime, long statusBarAnimationDuration) {
            RecentsAnimationController.this.onTransitionStart();
            RecentsAnimationController.this.mService.mRoot.getDisplayContent(RecentsAnimationController.this.mDisplayId).mAppTransition.unregisterListener(this);
            return 0;
        }
    };
    /* access modifiers changed from: private */
    public final RecentsAnimationCallbacks mCallbacks;
    private boolean mCancelOnNextTransitionStart;
    private boolean mCancelWithDeferredScreenshot;
    /* access modifiers changed from: private */
    public boolean mCanceled;
    private final IRecentsAnimationController mController = new IRecentsAnimationController.Stub() {
        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public ActivityManager.TaskSnapshot screenshotTask(int taskId) {
            long token = Binder.clearCallingIdentity();
            try {
                synchronized (RecentsAnimationController.this.mService.getWindowManagerLock()) {
                    if (RecentsAnimationController.this.mCanceled) {
                        Binder.restoreCallingIdentity(token);
                        return null;
                    }
                    for (int i = RecentsAnimationController.this.mPendingAnimations.size() - 1; i >= 0; i--) {
                        Task task = ((TaskAnimationAdapter) RecentsAnimationController.this.mPendingAnimations.get(i)).mTask;
                        if (task.mTaskId == taskId) {
                            TaskSnapshotController snapshotController = RecentsAnimationController.this.mService.mTaskSnapshotController;
                            ArraySet<Task> tasks = Sets.newArraySet(new Task[]{task});
                            snapshotController.snapshotTasks(tasks);
                            snapshotController.addSkipClosingAppSnapshotTasks(tasks);
                            ActivityManager.TaskSnapshot snapshot = snapshotController.getSnapshot(taskId, 0, false, false);
                            Binder.restoreCallingIdentity(token);
                            return snapshot;
                        }
                    }
                    Binder.restoreCallingIdentity(token);
                    return null;
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
            r2 = com.android.server.wm.RecentsAnimationController.access$500(r5.this$0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0024, code lost:
            if (r6 == false) goto L_0x0028;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0026, code lost:
            r4 = 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0028, code lost:
            r4 = 2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0029, code lost:
            r2.onAnimationFinished(r4, true, r7);
            com.android.server.wm.RecentsAnimationController.access$100(r5.this$0).mRoot.getDisplayContent(com.android.server.wm.RecentsAnimationController.access$000(r5.this$0)).mBoundsAnimationController.setAnimationType(1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0044, code lost:
            android.os.Binder.restoreCallingIdentity(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0048, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void finish(boolean r6, boolean r7) {
            /*
                r5 = this;
                long r0 = android.os.Binder.clearCallingIdentity()
                com.android.server.wm.RecentsAnimationController r2 = com.android.server.wm.RecentsAnimationController.this     // Catch:{ all -> 0x004c }
                com.android.server.wm.WindowManagerService r2 = r2.mService     // Catch:{ all -> 0x004c }
                java.lang.Object r2 = r2.getWindowManagerLock()     // Catch:{ all -> 0x004c }
                monitor-enter(r2)     // Catch:{ all -> 0x004c }
                com.android.server.wm.RecentsAnimationController r3 = com.android.server.wm.RecentsAnimationController.this     // Catch:{ all -> 0x0049 }
                boolean r3 = r3.mCanceled     // Catch:{ all -> 0x0049 }
                if (r3 == 0) goto L_0x001c
                monitor-exit(r2)     // Catch:{ all -> 0x0049 }
                android.os.Binder.restoreCallingIdentity(r0)
                return
            L_0x001c:
                monitor-exit(r2)     // Catch:{ all -> 0x0049 }
                com.android.server.wm.RecentsAnimationController r2 = com.android.server.wm.RecentsAnimationController.this     // Catch:{ all -> 0x004c }
                com.android.server.wm.RecentsAnimationController$RecentsAnimationCallbacks r2 = r2.mCallbacks     // Catch:{ all -> 0x004c }
                r3 = 1
                if (r6 == 0) goto L_0x0028
                r4 = r3
                goto L_0x0029
            L_0x0028:
                r4 = 2
            L_0x0029:
                r2.onAnimationFinished(r4, r3, r7)     // Catch:{ all -> 0x004c }
                com.android.server.wm.RecentsAnimationController r2 = com.android.server.wm.RecentsAnimationController.this     // Catch:{ all -> 0x004c }
                com.android.server.wm.WindowManagerService r2 = r2.mService     // Catch:{ all -> 0x004c }
                com.android.server.wm.RootWindowContainer r2 = r2.mRoot     // Catch:{ all -> 0x004c }
                com.android.server.wm.RecentsAnimationController r4 = com.android.server.wm.RecentsAnimationController.this     // Catch:{ all -> 0x004c }
                int r4 = r4.mDisplayId     // Catch:{ all -> 0x004c }
                com.android.server.wm.DisplayContent r2 = r2.getDisplayContent(r4)     // Catch:{ all -> 0x004c }
                com.android.server.wm.BoundsAnimationController r4 = r2.mBoundsAnimationController     // Catch:{ all -> 0x004c }
                r4.setAnimationType(r3)     // Catch:{ all -> 0x004c }
                android.os.Binder.restoreCallingIdentity(r0)
                return
            L_0x0049:
                r3 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0049 }
                throw r3     // Catch:{ all -> 0x004c }
            L_0x004c:
                r2 = move-exception
                android.os.Binder.restoreCallingIdentity(r0)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.RecentsAnimationController.AnonymousClass2.finish(boolean, boolean):void");
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public void setAnimationTargetsBehindSystemBars(boolean behindSystemBars) throws RemoteException {
            long token = Binder.clearCallingIdentity();
            try {
                synchronized (RecentsAnimationController.this.mService.getWindowManagerLock()) {
                    for (int i = RecentsAnimationController.this.mPendingAnimations.size() - 1; i >= 0; i--) {
                        Task task = ((TaskAnimationAdapter) RecentsAnimationController.this.mPendingAnimations.get(i)).mTask;
                        if (task.getActivityType() != RecentsAnimationController.this.mTargetActivityType) {
                            task.setCanAffectSystemUiFlags(behindSystemBars);
                        }
                    }
                    RecentsAnimationController.this.mService.mWindowPlacerLocked.requestTraversal();
                }
                Binder.restoreCallingIdentity(token);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public void setInputConsumerEnabled(boolean enabled) {
            long token = Binder.clearCallingIdentity();
            try {
                synchronized (RecentsAnimationController.this.mService.getWindowManagerLock()) {
                    if (RecentsAnimationController.this.mCanceled) {
                        Binder.restoreCallingIdentity(token);
                        return;
                    }
                    boolean unused = RecentsAnimationController.this.mInputConsumerEnabled = enabled;
                    RecentsAnimationController.this.mService.mRoot.getDisplayContent(RecentsAnimationController.this.mDisplayId).getInputMonitor().updateInputWindowsLw(true);
                    RecentsAnimationController.this.mService.scheduleAnimationLocked();
                    Binder.restoreCallingIdentity(token);
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public void setSplitScreenMinimized(boolean minimized) {
            long token = Binder.clearCallingIdentity();
            try {
                synchronized (RecentsAnimationController.this.mService.getWindowManagerLock()) {
                    if (RecentsAnimationController.this.mCanceled) {
                        Binder.restoreCallingIdentity(token);
                        return;
                    }
                    boolean unused = RecentsAnimationController.this.mSplitScreenMinimized = minimized;
                    RecentsAnimationController.this.mService.checkSplitScreenMinimizedChanged(true);
                    Binder.restoreCallingIdentity(token);
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }

        public void hideCurrentInputMethod() {
            long token = Binder.clearCallingIdentity();
            try {
                InputMethodManagerInternal inputMethodManagerInternal = (InputMethodManagerInternal) LocalServices.getService(InputMethodManagerInternal.class);
                if (inputMethodManagerInternal != null) {
                    inputMethodManagerInternal.hideCurrentInputMethod();
                }
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void setCancelWithDeferredScreenshot(boolean screenshot) {
            synchronized (RecentsAnimationController.this.mLock) {
                RecentsAnimationController.this.setCancelWithDeferredScreenshotLocked(screenshot);
            }
        }

        public void cleanupScreenshot() {
            synchronized (RecentsAnimationController.this.mLock) {
                if (RecentsAnimationController.this.mRecentScreenshotAnimator != null) {
                    RecentsAnimationController.this.mRecentScreenshotAnimator.cancelAnimation();
                    RecentsAnimationController.this.mRecentScreenshotAnimator = null;
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public final int mDisplayId;
    private final Runnable mFailsafeRunnable = new Runnable() {
        public final void run() {
            RecentsAnimationController.this.lambda$new$0$RecentsAnimationController();
        }
    };
    /* access modifiers changed from: private */
    public boolean mInputConsumerEnabled;
    private boolean mLinkedToDeathOfRunner;
    final Object mLock = new Object();
    private Rect mMinimizedHomeBounds = new Rect();
    /* access modifiers changed from: private */
    public final ArrayList<TaskAnimationAdapter> mPendingAnimations = new ArrayList<>();
    private boolean mPendingStart = true;
    SurfaceAnimator mRecentScreenshotAnimator;
    private IRecentsAnimationRunner mRunner;
    /* access modifiers changed from: private */
    public final WindowManagerService mService;
    /* access modifiers changed from: private */
    public boolean mSplitScreenMinimized;
    private final StatusBarManagerInternal mStatusBar;
    /* access modifiers changed from: private */
    public int mTargetActivityType;
    private AppWindowToken mTargetAppToken;
    /* access modifiers changed from: private */
    public final Rect mTmpRect = new Rect();

    public interface RecentsAnimationCallbacks {
        void onAnimationFinished(@ReorderMode int i, boolean z, boolean z2);
    }

    public @interface ReorderMode {
    }

    public /* synthetic */ void lambda$new$0$RecentsAnimationController() {
        cancelAnimation(2, "failSafeRunnable");
    }

    RecentsAnimationController(WindowManagerService service, IRecentsAnimationRunner remoteAnimationRunner, RecentsAnimationCallbacks callbacks, int displayId) {
        this.mService = service;
        this.mRunner = remoteAnimationRunner;
        this.mCallbacks = callbacks;
        this.mDisplayId = displayId;
        this.mStatusBar = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
    }

    public void initialize(int targetActivityType, SparseBooleanArray recentTaskIds) {
        initialize(this.mService.mRoot.getDisplayContent(this.mDisplayId), targetActivityType, recentTaskIds);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void initialize(DisplayContent dc, int targetActivityType, SparseBooleanArray recentTaskIds) {
        this.mTargetActivityType = targetActivityType;
        dc.mAppTransition.registerListenerLocked(this.mAppTransitionListener);
        ArrayList<Task> visibleTasks = dc.getVisibleTasks();
        TaskStack targetStack = dc.getStack(0, targetActivityType);
        if (targetStack != null) {
            for (int i = targetStack.getChildCount() - 1; i >= 0; i--) {
                Task t = (Task) targetStack.getChildAt(i);
                if (!visibleTasks.contains(t)) {
                    visibleTasks.add(t);
                }
            }
        }
        int i2 = visibleTasks.size();
        for (int i3 = 0; i3 < i2; i3++) {
            Task task = visibleTasks.get(i3);
            WindowConfiguration config = task.getWindowConfiguration();
            if (!config.tasksAreFloating() && config.getWindowingMode() != 3) {
                addAnimation(task, !recentTaskIds.get(task.mTaskId));
            }
        }
        if (this.mPendingAnimations.isEmpty()) {
            cancelAnimation(2, "initialize-noVisibleTasks");
            return;
        }
        try {
            linkToDeathOfRunner();
            AppWindowToken recentsComponentAppToken = ((Task) dc.getStack(0, targetActivityType).getTopChild()).getTopFullscreenAppToken();
            if (recentsComponentAppToken != null) {
                this.mTargetAppToken = recentsComponentAppToken;
                if (recentsComponentAppToken.windowsCanBeWallpaperTarget()) {
                    dc.pendingLayoutChanges |= 4;
                    dc.setLayoutNeeded();
                }
            }
            TaskStack dockedStack = dc.getSplitScreenPrimaryStackIgnoringVisibility();
            dc.getDockedDividerController().getHomeStackBoundsInDockedMode(dc.getConfiguration(), dockedStack == null ? -1 : dockedStack.getDockSide(), this.mMinimizedHomeBounds);
            this.mService.mWindowPlacerLocked.performSurfacePlacement();
            StatusBarManagerInternal statusBarManagerInternal = this.mStatusBar;
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.onRecentsAnimationStateChanged(true);
            }
        } catch (RemoteException e) {
            cancelAnimation(2, "initialize-failedToLinkToDeath");
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public AnimationAdapter addAnimation(Task task, boolean isRecentTaskInvisible) {
        TaskAnimationAdapter taskAdapter = new TaskAnimationAdapter(task, isRecentTaskInvisible);
        task.startAnimation(task.getPendingTransaction(), taskAdapter, false);
        task.commitPendingTransaction();
        this.mPendingAnimations.add(taskAdapter);
        return taskAdapter;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void removeAnimation(TaskAnimationAdapter taskAdapter) {
        taskAdapter.mTask.setCanAffectSystemUiFlags(true);
        taskAdapter.mCapturedFinishCallback.onAnimationFinished(taskAdapter);
        this.mPendingAnimations.remove(taskAdapter);
    }

    /* access modifiers changed from: package-private */
    public void startAnimation() {
        Rect minimizedHomeBounds;
        Rect contentInsets;
        if (this.mPendingStart && !this.mCanceled) {
            try {
                ArrayList<RemoteAnimationTarget> appAnimations = new ArrayList<>();
                for (int i = this.mPendingAnimations.size() - 1; i >= 0; i--) {
                    TaskAnimationAdapter taskAdapter = this.mPendingAnimations.get(i);
                    RemoteAnimationTarget target = taskAdapter.createRemoteAnimationApp();
                    if (target != null) {
                        appAnimations.add(target);
                    } else {
                        removeAnimation(taskAdapter);
                    }
                }
                if (appAnimations.isEmpty() != 0) {
                    cancelAnimation(2, "startAnimation-noAppWindows");
                    return;
                }
                RemoteAnimationTarget[] appTargets = (RemoteAnimationTarget[]) appAnimations.toArray(new RemoteAnimationTarget[appAnimations.size()]);
                this.mPendingStart = false;
                this.mService.mRoot.getDisplayContent(this.mDisplayId).performLayout(false, false);
                if (this.mTargetAppToken == null || !this.mTargetAppToken.inSplitScreenSecondaryWindowingMode()) {
                    minimizedHomeBounds = null;
                } else {
                    minimizedHomeBounds = this.mMinimizedHomeBounds;
                }
                if (this.mTargetAppToken == null || this.mTargetAppToken.findMainWindow() == null) {
                    this.mService.getStableInsets(this.mDisplayId, this.mTmpRect);
                    contentInsets = this.mTmpRect;
                } else {
                    contentInsets = this.mTargetAppToken.findMainWindow().getContentInsets();
                }
                this.mRunner.onAnimationStart(this.mController, appTargets, contentInsets, minimizedHomeBounds);
                SparseIntArray reasons = new SparseIntArray();
                reasons.put(1, 5);
                this.mService.mAtmInternal.notifyAppTransitionStarting(reasons, SystemClock.uptimeMillis());
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to start recents animation", e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelAnimation(@ReorderMode int reorderMode, String reason) {
        cancelAnimation(reorderMode, false, false, reason);
    }

    /* access modifiers changed from: package-private */
    public void cancelAnimationSynchronously(@ReorderMode int reorderMode, String reason) {
        cancelAnimation(reorderMode, true, false, reason);
    }

    /* access modifiers changed from: package-private */
    public void cancelAnimationWithScreenShot() {
        cancelAnimation(0, true, true, "stackOrderChanged");
    }

    private void cancelAnimation(@ReorderMode int reorderMode, boolean runSynchronously, boolean screenshot, String reason) {
        synchronized (this.mService.getWindowManagerLock()) {
            if (!this.mCanceled) {
                this.mService.mH.removeCallbacks(this.mFailsafeRunnable);
                this.mCanceled = true;
                if (screenshot) {
                    try {
                        screenshotRecentTask(this.mPendingAnimations.get(0).mTask, reorderMode, runSynchronously);
                        this.mRunner.onAnimationCanceled(true);
                    } catch (RemoteException e) {
                        Slog.e(TAG, "Failed to cancel recents animation", e);
                    }
                } else {
                    this.mRunner.onAnimationCanceled(false);
                    this.mCallbacks.onAnimationFinished(reorderMode, runSynchronously, false);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelOnNextTransitionStart() {
        this.mCancelOnNextTransitionStart = true;
    }

    /* access modifiers changed from: package-private */
    public void setCancelWithDeferredScreenshotLocked(boolean screenshot) {
        this.mCancelWithDeferredScreenshot = screenshot;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldCancelWithDeferredScreenshot() {
        return this.mCancelWithDeferredScreenshot;
    }

    /* access modifiers changed from: package-private */
    public void onTransitionStart() {
        if (!this.mCanceled && this.mCancelOnNextTransitionStart) {
            this.mCancelOnNextTransitionStart = false;
            cancelAnimationWithScreenShot();
        }
    }

    /* access modifiers changed from: package-private */
    public void screenshotRecentTask(Task task, @ReorderMode int reorderMode, boolean runSynchronously) {
        TaskScreenshotAnimatable animatable = TaskScreenshotAnimatable.create(task);
        if (animatable != null) {
            this.mRecentScreenshotAnimator = new SurfaceAnimator(animatable, new Runnable(reorderMode, runSynchronously) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    RecentsAnimationController.this.lambda$screenshotRecentTask$1$RecentsAnimationController(this.f$1, this.f$2);
                }
            }, this.mService);
            this.mRecentScreenshotAnimator.transferAnimation(task.mSurfaceAnimator);
        }
    }

    public /* synthetic */ void lambda$screenshotRecentTask$1$RecentsAnimationController(int reorderMode, boolean runSynchronously) {
        this.mCallbacks.onAnimationFinished(reorderMode, runSynchronously, false);
    }

    /* access modifiers changed from: package-private */
    public void cleanupAnimation(@ReorderMode int reorderMode) {
        for (int i = this.mPendingAnimations.size() - 1; i >= 0; i--) {
            TaskAnimationAdapter taskAdapter = this.mPendingAnimations.get(i);
            if (reorderMode == 1 || reorderMode == 0) {
                taskAdapter.mTask.dontAnimateDimExit();
            }
            removeAnimation(taskAdapter);
        }
        this.mService.mH.removeCallbacks(this.mFailsafeRunnable);
        unlinkToDeathOfRunner();
        this.mRunner = null;
        this.mCanceled = true;
        SurfaceAnimator surfaceAnimator = this.mRecentScreenshotAnimator;
        if (surfaceAnimator != null) {
            surfaceAnimator.cancelAnimation();
            this.mRecentScreenshotAnimator = null;
        }
        this.mService.mRoot.getDisplayContent(this.mDisplayId).getInputMonitor().updateInputWindowsLw(true);
        if (this.mTargetAppToken != null && (reorderMode == 1 || reorderMode == 0)) {
            this.mService.mRoot.getDisplayContent(this.mDisplayId).mAppTransition.notifyAppTransitionFinishedLocked(this.mTargetAppToken.token);
        }
        StatusBarManagerInternal statusBarManagerInternal = this.mStatusBar;
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.onRecentsAnimationStateChanged(false);
        }
    }

    /* access modifiers changed from: package-private */
    public void scheduleFailsafe() {
        this.mService.mH.postDelayed(this.mFailsafeRunnable, 1000);
    }

    private void linkToDeathOfRunner() throws RemoteException {
        if (!this.mLinkedToDeathOfRunner) {
            this.mRunner.asBinder().linkToDeath(this, 0);
            this.mLinkedToDeathOfRunner = true;
        }
    }

    private void unlinkToDeathOfRunner() {
        if (this.mLinkedToDeathOfRunner) {
            this.mRunner.asBinder().unlinkToDeath(this, 0);
            this.mLinkedToDeathOfRunner = false;
        }
    }

    public void binderDied() {
        cancelAnimation(2, "binderDied");
        synchronized (this.mService.getWindowManagerLock()) {
            this.mService.mRoot.getDisplayContent(this.mDisplayId).getInputMonitor().destroyInputConsumer("recents_animation_input_consumer");
        }
    }

    /* access modifiers changed from: package-private */
    public void checkAnimationReady(WallpaperController wallpaperController) {
        if (this.mPendingStart) {
            if (!isTargetOverWallpaper() || (wallpaperController.getWallpaperTarget() != null && wallpaperController.wallpaperTransitionReady())) {
                this.mService.getRecentsAnimationController().startAnimation();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isSplitScreenMinimized() {
        return this.mSplitScreenMinimized;
    }

    /* access modifiers changed from: package-private */
    public boolean isWallpaperVisible(WindowState w) {
        return w != null && w.mAppToken != null && this.mTargetAppToken == w.mAppToken && isTargetOverWallpaper();
    }

    /* access modifiers changed from: package-private */
    public boolean shouldApplyInputConsumer(AppWindowToken appToken) {
        return this.mInputConsumerEnabled && this.mTargetAppToken != appToken && isAnimatingApp(appToken);
    }

    /* access modifiers changed from: package-private */
    public boolean updateInputConsumerForApp(InputWindowHandle inputWindowHandle, boolean hasFocus) {
        WindowState targetAppMainWindow;
        AppWindowToken appWindowToken = this.mTargetAppToken;
        if (appWindowToken != null) {
            targetAppMainWindow = appWindowToken.findMainWindow();
        } else {
            targetAppMainWindow = null;
        }
        if (targetAppMainWindow == null) {
            return false;
        }
        targetAppMainWindow.getBounds(this.mTmpRect);
        inputWindowHandle.hasFocus = hasFocus;
        inputWindowHandle.touchableRegion.set(this.mTmpRect);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isTargetApp(AppWindowToken token) {
        AppWindowToken appWindowToken = this.mTargetAppToken;
        return appWindowToken != null && token == appWindowToken;
    }

    private boolean isTargetOverWallpaper() {
        AppWindowToken appWindowToken = this.mTargetAppToken;
        if (appWindowToken == null) {
            return false;
        }
        return appWindowToken.windowsCanBeWallpaperTarget();
    }

    /* access modifiers changed from: package-private */
    public boolean isAnimatingTask(Task task) {
        for (int i = this.mPendingAnimations.size() - 1; i >= 0; i--) {
            if (task == this.mPendingAnimations.get(i).mTask) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnimatingApp(AppWindowToken appToken) {
        for (int i = this.mPendingAnimations.size() - 1; i >= 0; i--) {
            Task task = this.mPendingAnimations.get(i).mTask;
            for (int j = task.getChildCount() - 1; j >= 0; j--) {
                if (((AppWindowToken) task.getChildAt(j)) == appToken) {
                    return true;
                }
            }
        }
        return false;
    }

    @VisibleForTesting
    class TaskAnimationAdapter implements AnimationAdapter {
        private final Rect mBounds = new Rect();
        /* access modifiers changed from: private */
        public SurfaceAnimator.OnAnimationFinishedCallback mCapturedFinishCallback;
        private SurfaceControl mCapturedLeash;
        private final boolean mIsRecentTaskInvisible;
        private final Point mPosition = new Point();
        private RemoteAnimationTarget mTarget;
        /* access modifiers changed from: private */
        public final Task mTask;

        TaskAnimationAdapter(Task task, boolean isRecentTaskInvisible) {
            this.mTask = task;
            this.mIsRecentTaskInvisible = isRecentTaskInvisible;
            WindowContainer container = this.mTask.getParent();
            container.getRelativeDisplayedPosition(this.mPosition);
            this.mBounds.set(container.getDisplayedBounds());
        }

        /* access modifiers changed from: package-private */
        public RemoteAnimationTarget createRemoteAnimationApp() {
            WindowState mainWindow;
            int mode;
            AppWindowToken topApp = this.mTask.getTopVisibleAppToken();
            if (topApp != null) {
                mainWindow = topApp.findMainWindow();
            } else {
                mainWindow = null;
            }
            if (mainWindow == null) {
                return null;
            }
            Rect insets = new Rect();
            mainWindow.getContentInsets(insets);
            InsetUtils.addInsets(insets, mainWindow.mAppToken.getLetterboxInsets());
            if (topApp.getActivityType() == RecentsAnimationController.this.mTargetActivityType) {
                mode = 0;
            } else {
                mode = 1;
            }
            AppWindowToken appWindowToken = topApp;
            AppWindowToken topApp2 = r4;
            AppWindowToken remoteAnimationTarget = new RemoteAnimationTarget(this.mTask.mTaskId, mode, this.mCapturedLeash, !topApp.fillsParent(), mainWindow.mWinAnimator.mLastClipRect, insets, this.mTask.getPrefixOrderIndex(), this.mPosition, this.mBounds, this.mTask.getWindowConfiguration(), this.mIsRecentTaskInvisible, (SurfaceControl) null, (Rect) null);
            this.mTarget = topApp2;
            return this.mTarget;
        }

        public boolean getShowWallpaper() {
            return false;
        }

        public int getBackgroundColor() {
            return 0;
        }

        public void startAnimation(SurfaceControl animationLeash, SurfaceControl.Transaction t, SurfaceAnimator.OnAnimationFinishedCallback finishCallback) {
            t.setLayer(animationLeash, this.mTask.getPrefixOrderIndex());
            t.setPosition(animationLeash, (float) this.mPosition.x, (float) this.mPosition.y);
            RecentsAnimationController.this.mTmpRect.set(this.mBounds);
            RecentsAnimationController.this.mTmpRect.offsetTo(0, 0);
            t.setWindowCrop(animationLeash, RecentsAnimationController.this.mTmpRect);
            this.mCapturedLeash = animationLeash;
            this.mCapturedFinishCallback = finishCallback;
        }

        public void onAnimationCancelled(SurfaceControl animationLeash) {
            RecentsAnimationController.this.cancelAnimation(2, "taskAnimationAdapterCanceled");
        }

        public long getDurationHint() {
            return 0;
        }

        public long getStatusBarTransitionsStartTime() {
            return SystemClock.uptimeMillis();
        }

        public void dump(PrintWriter pw, String prefix) {
            pw.print(prefix);
            pw.println("task=" + this.mTask);
            if (this.mTarget != null) {
                pw.print(prefix);
                pw.println("Target:");
                RemoteAnimationTarget remoteAnimationTarget = this.mTarget;
                remoteAnimationTarget.dump(pw, prefix + "  ");
            } else {
                pw.print(prefix);
                pw.println("Target: null");
            }
            pw.println("mIsRecentTaskInvisible=" + this.mIsRecentTaskInvisible);
            pw.println("mPosition=" + this.mPosition);
            pw.println("mBounds=" + this.mBounds);
            pw.println("mIsRecentTaskInvisible=" + this.mIsRecentTaskInvisible);
        }

        public void writeToProto(ProtoOutputStream proto) {
            long token = proto.start(1146756268034L);
            RemoteAnimationTarget remoteAnimationTarget = this.mTarget;
            if (remoteAnimationTarget != null) {
                remoteAnimationTarget.writeToProto(proto, 1146756268033L);
            }
            proto.end(token);
        }
    }

    public void dump(PrintWriter pw, String prefix) {
        String innerPrefix = prefix + "  ";
        pw.print(prefix);
        pw.println(RecentsAnimationController.class.getSimpleName() + ":");
        pw.print(innerPrefix);
        pw.println("mPendingStart=" + this.mPendingStart);
        pw.print(innerPrefix);
        pw.println("mPendingAnimations=" + this.mPendingAnimations.size());
        pw.print(innerPrefix);
        pw.println("mCanceled=" + this.mCanceled);
        pw.print(innerPrefix);
        pw.println("mInputConsumerEnabled=" + this.mInputConsumerEnabled);
        pw.print(innerPrefix);
        pw.println("mSplitScreenMinimized=" + this.mSplitScreenMinimized);
        pw.print(innerPrefix);
        pw.println("mTargetAppToken=" + this.mTargetAppToken);
        pw.print(innerPrefix);
        pw.println("isTargetOverWallpaper=" + isTargetOverWallpaper());
    }
}
