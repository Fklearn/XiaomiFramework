package com.android.server.wm;

import android.content.Context;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.view.Choreographer;
import android.view.SurfaceControl;
import com.android.server.AnimationThread;
import com.android.server.policy.WindowManagerPolicy;
import java.io.PrintWriter;
import java.util.ArrayList;

public class WindowAnimator {
    private static final String TAG = "WindowManager";
    private final ArrayList<Runnable> mAfterPrepareSurfacesRunnables = new ArrayList<>();
    private boolean mAnimating;
    final Choreographer.FrameCallback mAnimationFrameCallback;
    private boolean mAnimationFrameCallbackScheduled;
    int mBulkUpdateParams = 0;
    private Choreographer mChoreographer;
    final Context mContext;
    long mCurrentTime;
    SparseArray<DisplayContentsAnimator> mDisplayContentsAnimators = new SparseArray<>(2);
    private boolean mHasNotifyDummyVisibleApp;
    private boolean mInExecuteAfterPrepareSurfacesRunnables;
    private boolean mInitialized = false;
    private boolean mLastRootAnimating;
    Object mLastWindowFreezeSource;
    final WindowManagerPolicy mPolicy;
    private boolean mRemoveReplacedWindows = false;
    final WindowManagerService mService;
    private final SurfaceControl.Transaction mTransaction = new SurfaceControl.Transaction();

    WindowAnimator(WindowManagerService service) {
        this.mService = service;
        this.mContext = service.mContext;
        this.mPolicy = service.mPolicy;
        AnimationThread.getHandler().runWithScissors(new Runnable() {
            public final void run() {
                WindowAnimator.this.lambda$new$0$WindowAnimator();
            }
        }, 0);
        this.mAnimationFrameCallback = new Choreographer.FrameCallback() {
            public final void doFrame(long j) {
                WindowAnimator.this.lambda$new$1$WindowAnimator(j);
            }
        };
    }

    public /* synthetic */ void lambda$new$0$WindowAnimator() {
        this.mChoreographer = Choreographer.getSfInstance();
    }

    public /* synthetic */ void lambda$new$1$WindowAnimator(long frameTimeNs) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mAnimationFrameCallbackScheduled = false;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        animate(frameTimeNs);
    }

    /* access modifiers changed from: package-private */
    public void addDisplayLocked(int displayId) {
        getDisplayContentsAnimatorLocked(displayId);
    }

    /* access modifiers changed from: package-private */
    public void removeDisplayLocked(int displayId) {
        DisplayContentsAnimator displayAnimator = this.mDisplayContentsAnimators.get(displayId);
        if (!(displayAnimator == null || displayAnimator.mScreenRotationAnimation == null)) {
            displayAnimator.mScreenRotationAnimation.kill();
            displayAnimator.mScreenRotationAnimation = null;
        }
        this.mDisplayContentsAnimators.delete(displayId);
    }

    /* access modifiers changed from: package-private */
    public void ready() {
        this.mInitialized = true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0015, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
        r1 = r12.mService.mGlobalLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001c, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        com.android.server.wm.WindowManagerService.boostPriorityForLockedSection();
        r12.mCurrentTime = r13 / 1000000;
        r12.mBulkUpdateParams = 4;
        r12.mAnimating = false;
        r12.mService.openSurfaceTransaction();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r3 = r12.mService.mAccessibilityController;
        r4 = r12.mDisplayContentsAnimators.size();
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003e, code lost:
        if (r5 >= r4) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0040, code lost:
        r7 = r12.mService.mRoot.getDisplayContent(r12.mDisplayContentsAnimators.keyAt(r5));
        r8 = r12.mDisplayContentsAnimators.valueAt(r5);
        r9 = r8.mScreenRotationAnimation;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0058, code lost:
        if (r9 == null) goto L_0x0089;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005e, code lost:
        if (r9.isAnimating() == false) goto L_0x0089;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0062, code lost:
        if (r12.mHasNotifyDummyVisibleApp != false) goto L_0x006d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0064, code lost:
        r12.mHasNotifyDummyVisibleApp = true;
        r12.mService.mMiuiGestureController.notifyAppDummyVisible(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0073, code lost:
        if (r9.stepAnimationLocked(r12.mCurrentTime) == false) goto L_0x0079;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0075, code lost:
        setAnimating(true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0079, code lost:
        r12.mBulkUpdateParams |= 1;
        r9.kill();
        r8.mScreenRotationAnimation = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0084, code lost:
        if (r3 == null) goto L_0x0089;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0086, code lost:
        r3.onRotationChangedLocked(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0089, code lost:
        r7.updateWindowsForAnimator();
        com.android.server.wm.WindowAnimatorInjector.updateLockDeviceWindowLocked(r12.mService, r7);
        r7.updateBackgroundForAnimator();
        r7.prepareSurfaces();
        r5 = r5 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x009a, code lost:
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x009b, code lost:
        if (r5 >= r4) goto L_0x00d4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x009d, code lost:
        r6 = r12.mDisplayContentsAnimators.keyAt(r5);
        r7 = r12.mService.mRoot.getDisplayContent(r6);
        r7.checkAppWindowsReadyToShow();
        r8 = r12.mDisplayContentsAnimators.valueAt(r5).mScreenRotationAnimation;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00b8, code lost:
        if (r8 == null) goto L_0x00bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00ba, code lost:
        r8.updateSurfaces(r12.mTransaction);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00bf, code lost:
        orAnimating(r7.getDockedDividerController().animate(r12.mCurrentTime));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00cc, code lost:
        if (r3 == null) goto L_0x00d1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00ce, code lost:
        r3.drawMagnifiedRegionBorderIfNeededLocked(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00d1, code lost:
        r5 = r5 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00d6, code lost:
        if (r12.mAnimating != 0) goto L_0x00dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00d8, code lost:
        r12.mHasNotifyDummyVisibleApp = false;
        cancelAnimation();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00e1, code lost:
        if (r12.mService.mWatermark == null) goto L_0x00ea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00e3, code lost:
        r12.mService.mWatermark.drawIfNeeded();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00ee, code lost:
        if (r12.mService.mMIUIWatermark == null) goto L_0x00f7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00f0, code lost:
        r12.mService.mMIUIWatermark.drawIfNeeded();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00fb, code lost:
        if (r12.mService.mTalkbackWatermark == null) goto L_0x0116;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0105, code lost:
        if (r12.mService.mPowerManager.isInteractive() == false) goto L_0x010f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0107, code lost:
        r12.mService.mTalkbackWatermark.setVisibility(true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x010f, code lost:
        r12.mService.mTalkbackWatermark.setVisibility(false, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0116, code lost:
        android.view.SurfaceControl.mergeToGlobalTransaction(r12.mTransaction);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:?, code lost:
        r3 = r12.mService;
        r4 = "WindowAnimator";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0120, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0123, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:?, code lost:
        android.util.Slog.wtf("WindowManager", "Unhandled exception in Window Manager", r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:?, code lost:
        r3 = r12.mService;
        r4 = "WindowAnimator";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0130, code lost:
        r3.closeSurfaceTransaction(r4);
        r3 = r12.mService.mRoot.hasPendingLayoutChanges(r12);
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x013f, code lost:
        if (r12.mBulkUpdateParams != 0) goto L_0x0141;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0141, code lost:
        r4 = r12.mService.mRoot.copyAnimToLayoutParams();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x014e, code lost:
        r12.mService.mWindowPlacerLocked.requestTraversal();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0155, code lost:
        r5 = r12.mService.mRoot.isSelfOrChildAnimating();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0165, code lost:
        r12.mService.mTaskSnapshotController.setPersisterPaused(true);
        android.os.Trace.asyncTraceBegin(32, "animating", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0177, code lost:
        r12.mService.mWindowPlacerLocked.requestTraversal();
        r12.mService.mTaskSnapshotController.setPersisterPaused(false);
        android.os.Trace.asyncTraceEnd(32, "animating", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x018a, code lost:
        r12.mLastRootAnimating = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x018e, code lost:
        if (r12.mRemoveReplacedWindows != false) goto L_0x0190;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0190, code lost:
        r12.mService.mRoot.removeReplacedWindows();
        r12.mRemoveReplacedWindows = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x0199, code lost:
        r12.mService.destroyPreservedSurfaceLocked();
        executeAfterPrepareSurfacesRunnables();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x01a1, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x01a2, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x01a5, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:?, code lost:
        r12.mService.closeSurfaceTransaction("WindowAnimator");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x01ad, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x01ae, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x01b0, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x01b3, code lost:
        throw r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void animate(long r13) {
        /*
            r12 = this;
            com.android.server.wm.WindowManagerService r0 = r12.mService
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x01b4 }
            boolean r1 = r12.mInitialized     // Catch:{ all -> 0x01b4 }
            if (r1 != 0) goto L_0x0011
            monitor-exit(r0)     // Catch:{ all -> 0x01b4 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0011:
            r12.scheduleAnimation()     // Catch:{ all -> 0x01b4 }
            monitor-exit(r0)     // Catch:{ all -> 0x01b4 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            com.android.server.wm.WindowManagerService r0 = r12.mService
            com.android.server.wm.WindowManagerGlobalLock r1 = r0.mGlobalLock
            monitor-enter(r1)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x01ae }
            r2 = 1000000(0xf4240, double:4.940656E-318)
            long r2 = r13 / r2
            r12.mCurrentTime = r2     // Catch:{ all -> 0x01ae }
            r0 = 4
            r12.mBulkUpdateParams = r0     // Catch:{ all -> 0x01ae }
            r0 = 0
            r12.mAnimating = r0     // Catch:{ all -> 0x01ae }
            com.android.server.wm.WindowManagerService r2 = r12.mService     // Catch:{ all -> 0x01ae }
            r2.openSurfaceTransaction()     // Catch:{ all -> 0x01ae }
            r2 = 1
            com.android.server.wm.WindowManagerService r3 = r12.mService     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.AccessibilityController r3 = r3.mAccessibilityController     // Catch:{ RuntimeException -> 0x0123 }
            android.util.SparseArray<com.android.server.wm.WindowAnimator$DisplayContentsAnimator> r4 = r12.mDisplayContentsAnimators     // Catch:{ RuntimeException -> 0x0123 }
            int r4 = r4.size()     // Catch:{ RuntimeException -> 0x0123 }
            r5 = 0
        L_0x003e:
            if (r5 >= r4) goto L_0x009a
            android.util.SparseArray<com.android.server.wm.WindowAnimator$DisplayContentsAnimator> r6 = r12.mDisplayContentsAnimators     // Catch:{ RuntimeException -> 0x0123 }
            int r6 = r6.keyAt(r5)     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.WindowManagerService r7 = r12.mService     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.RootWindowContainer r7 = r7.mRoot     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.DisplayContent r7 = r7.getDisplayContent(r6)     // Catch:{ RuntimeException -> 0x0123 }
            android.util.SparseArray<com.android.server.wm.WindowAnimator$DisplayContentsAnimator> r8 = r12.mDisplayContentsAnimators     // Catch:{ RuntimeException -> 0x0123 }
            java.lang.Object r8 = r8.valueAt(r5)     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.WindowAnimator$DisplayContentsAnimator r8 = (com.android.server.wm.WindowAnimator.DisplayContentsAnimator) r8     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.ScreenRotationAnimation r9 = r8.mScreenRotationAnimation     // Catch:{ RuntimeException -> 0x0123 }
            if (r9 == 0) goto L_0x0089
            boolean r10 = r9.isAnimating()     // Catch:{ RuntimeException -> 0x0123 }
            if (r10 == 0) goto L_0x0089
            boolean r10 = r12.mHasNotifyDummyVisibleApp     // Catch:{ RuntimeException -> 0x0123 }
            if (r10 != 0) goto L_0x006d
            r12.mHasNotifyDummyVisibleApp = r2     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.WindowManagerService r10 = r12.mService     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.MiuiGestureController r10 = r10.mMiuiGestureController     // Catch:{ RuntimeException -> 0x0123 }
            r10.notifyAppDummyVisible(r7)     // Catch:{ RuntimeException -> 0x0123 }
        L_0x006d:
            long r10 = r12.mCurrentTime     // Catch:{ RuntimeException -> 0x0123 }
            boolean r10 = r9.stepAnimationLocked(r10)     // Catch:{ RuntimeException -> 0x0123 }
            if (r10 == 0) goto L_0x0079
            r12.setAnimating(r2)     // Catch:{ RuntimeException -> 0x0123 }
            goto L_0x0089
        L_0x0079:
            int r10 = r12.mBulkUpdateParams     // Catch:{ RuntimeException -> 0x0123 }
            r10 = r10 | r2
            r12.mBulkUpdateParams = r10     // Catch:{ RuntimeException -> 0x0123 }
            r9.kill()     // Catch:{ RuntimeException -> 0x0123 }
            r10 = 0
            r8.mScreenRotationAnimation = r10     // Catch:{ RuntimeException -> 0x0123 }
            if (r3 == 0) goto L_0x0089
            r3.onRotationChangedLocked(r7)     // Catch:{ RuntimeException -> 0x0123 }
        L_0x0089:
            r7.updateWindowsForAnimator()     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.WindowManagerService r10 = r12.mService     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.WindowAnimatorInjector.updateLockDeviceWindowLocked(r10, r7)     // Catch:{ RuntimeException -> 0x0123 }
            r7.updateBackgroundForAnimator()     // Catch:{ RuntimeException -> 0x0123 }
            r7.prepareSurfaces()     // Catch:{ RuntimeException -> 0x0123 }
            int r5 = r5 + 1
            goto L_0x003e
        L_0x009a:
            r5 = 0
        L_0x009b:
            if (r5 >= r4) goto L_0x00d4
            android.util.SparseArray<com.android.server.wm.WindowAnimator$DisplayContentsAnimator> r6 = r12.mDisplayContentsAnimators     // Catch:{ RuntimeException -> 0x0123 }
            int r6 = r6.keyAt(r5)     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.WindowManagerService r7 = r12.mService     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.RootWindowContainer r7 = r7.mRoot     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.DisplayContent r7 = r7.getDisplayContent(r6)     // Catch:{ RuntimeException -> 0x0123 }
            r7.checkAppWindowsReadyToShow()     // Catch:{ RuntimeException -> 0x0123 }
            android.util.SparseArray<com.android.server.wm.WindowAnimator$DisplayContentsAnimator> r8 = r12.mDisplayContentsAnimators     // Catch:{ RuntimeException -> 0x0123 }
            java.lang.Object r8 = r8.valueAt(r5)     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.WindowAnimator$DisplayContentsAnimator r8 = (com.android.server.wm.WindowAnimator.DisplayContentsAnimator) r8     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.ScreenRotationAnimation r8 = r8.mScreenRotationAnimation     // Catch:{ RuntimeException -> 0x0123 }
            if (r8 == 0) goto L_0x00bf
            android.view.SurfaceControl$Transaction r9 = r12.mTransaction     // Catch:{ RuntimeException -> 0x0123 }
            r8.updateSurfaces(r9)     // Catch:{ RuntimeException -> 0x0123 }
        L_0x00bf:
            com.android.server.wm.DockedStackDividerController r9 = r7.getDockedDividerController()     // Catch:{ RuntimeException -> 0x0123 }
            long r10 = r12.mCurrentTime     // Catch:{ RuntimeException -> 0x0123 }
            boolean r9 = r9.animate(r10)     // Catch:{ RuntimeException -> 0x0123 }
            r12.orAnimating(r9)     // Catch:{ RuntimeException -> 0x0123 }
            if (r3 == 0) goto L_0x00d1
            r3.drawMagnifiedRegionBorderIfNeededLocked(r6)     // Catch:{ RuntimeException -> 0x0123 }
        L_0x00d1:
            int r5 = r5 + 1
            goto L_0x009b
        L_0x00d4:
            boolean r5 = r12.mAnimating     // Catch:{ RuntimeException -> 0x0123 }
            if (r5 != 0) goto L_0x00dd
            r12.mHasNotifyDummyVisibleApp = r0     // Catch:{ RuntimeException -> 0x0123 }
            r12.cancelAnimation()     // Catch:{ RuntimeException -> 0x0123 }
        L_0x00dd:
            com.android.server.wm.WindowManagerService r5 = r12.mService     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.Watermark r5 = r5.mWatermark     // Catch:{ RuntimeException -> 0x0123 }
            if (r5 == 0) goto L_0x00ea
            com.android.server.wm.WindowManagerService r5 = r12.mService     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.Watermark r5 = r5.mWatermark     // Catch:{ RuntimeException -> 0x0123 }
            r5.drawIfNeeded()     // Catch:{ RuntimeException -> 0x0123 }
        L_0x00ea:
            com.android.server.wm.WindowManagerService r5 = r12.mService     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.MIUIWatermark r5 = r5.mMIUIWatermark     // Catch:{ RuntimeException -> 0x0123 }
            if (r5 == 0) goto L_0x00f7
            com.android.server.wm.WindowManagerService r5 = r12.mService     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.MIUIWatermark r5 = r5.mMIUIWatermark     // Catch:{ RuntimeException -> 0x0123 }
            r5.drawIfNeeded()     // Catch:{ RuntimeException -> 0x0123 }
        L_0x00f7:
            com.android.server.wm.WindowManagerService r5 = r12.mService     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.TalkbackWatermark r5 = r5.mTalkbackWatermark     // Catch:{ RuntimeException -> 0x0123 }
            if (r5 == 0) goto L_0x0116
            com.android.server.wm.WindowManagerService r5 = r12.mService     // Catch:{ RuntimeException -> 0x0123 }
            android.os.PowerManager r5 = r5.mPowerManager     // Catch:{ RuntimeException -> 0x0123 }
            boolean r5 = r5.isInteractive()     // Catch:{ RuntimeException -> 0x0123 }
            if (r5 == 0) goto L_0x010f
            com.android.server.wm.WindowManagerService r5 = r12.mService     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.TalkbackWatermark r5 = r5.mTalkbackWatermark     // Catch:{ RuntimeException -> 0x0123 }
            r5.setVisibility(r2)     // Catch:{ RuntimeException -> 0x0123 }
            goto L_0x0116
        L_0x010f:
            com.android.server.wm.WindowManagerService r5 = r12.mService     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.TalkbackWatermark r5 = r5.mTalkbackWatermark     // Catch:{ RuntimeException -> 0x0123 }
            r5.setVisibility(r0, r0)     // Catch:{ RuntimeException -> 0x0123 }
        L_0x0116:
            android.view.SurfaceControl$Transaction r5 = r12.mTransaction     // Catch:{ RuntimeException -> 0x0123 }
            android.view.SurfaceControl.mergeToGlobalTransaction(r5)     // Catch:{ RuntimeException -> 0x0123 }
            com.android.server.wm.WindowManagerService r3 = r12.mService     // Catch:{ all -> 0x01ae }
            java.lang.String r4 = "WindowAnimator"
            goto L_0x0130
        L_0x0120:
            r0 = move-exception
            goto L_0x01a6
        L_0x0123:
            r3 = move-exception
            java.lang.String r4 = "WindowManager"
            java.lang.String r5 = "Unhandled exception in Window Manager"
            android.util.Slog.wtf(r4, r5, r3)     // Catch:{ all -> 0x0120 }
            com.android.server.wm.WindowManagerService r3 = r12.mService     // Catch:{ all -> 0x01ae }
            java.lang.String r4 = "WindowAnimator"
        L_0x0130:
            r3.closeSurfaceTransaction(r4)     // Catch:{ all -> 0x01ae }
            com.android.server.wm.WindowManagerService r3 = r12.mService     // Catch:{ all -> 0x01ae }
            com.android.server.wm.RootWindowContainer r3 = r3.mRoot     // Catch:{ all -> 0x01ae }
            boolean r3 = r3.hasPendingLayoutChanges(r12)     // Catch:{ all -> 0x01ae }
            r4 = 0
            int r5 = r12.mBulkUpdateParams     // Catch:{ all -> 0x01ae }
            if (r5 == 0) goto L_0x014a
            com.android.server.wm.WindowManagerService r5 = r12.mService     // Catch:{ all -> 0x01ae }
            com.android.server.wm.RootWindowContainer r5 = r5.mRoot     // Catch:{ all -> 0x01ae }
            boolean r5 = r5.copyAnimToLayoutParams()     // Catch:{ all -> 0x01ae }
            r4 = r5
        L_0x014a:
            if (r3 != 0) goto L_0x014e
            if (r4 == 0) goto L_0x0155
        L_0x014e:
            com.android.server.wm.WindowManagerService r5 = r12.mService     // Catch:{ all -> 0x01ae }
            com.android.server.wm.WindowSurfacePlacer r5 = r5.mWindowPlacerLocked     // Catch:{ all -> 0x01ae }
            r5.requestTraversal()     // Catch:{ all -> 0x01ae }
        L_0x0155:
            com.android.server.wm.WindowManagerService r5 = r12.mService     // Catch:{ all -> 0x01ae }
            com.android.server.wm.RootWindowContainer r5 = r5.mRoot     // Catch:{ all -> 0x01ae }
            boolean r5 = r5.isSelfOrChildAnimating()     // Catch:{ all -> 0x01ae }
            r6 = 32
            if (r5 == 0) goto L_0x0171
            boolean r8 = r12.mLastRootAnimating     // Catch:{ all -> 0x01ae }
            if (r8 != 0) goto L_0x0171
            com.android.server.wm.WindowManagerService r8 = r12.mService     // Catch:{ all -> 0x01ae }
            com.android.server.wm.TaskSnapshotController r8 = r8.mTaskSnapshotController     // Catch:{ all -> 0x01ae }
            r8.setPersisterPaused(r2)     // Catch:{ all -> 0x01ae }
            java.lang.String r2 = "animating"
            android.os.Trace.asyncTraceBegin(r6, r2, r0)     // Catch:{ all -> 0x01ae }
        L_0x0171:
            if (r5 != 0) goto L_0x018a
            boolean r2 = r12.mLastRootAnimating     // Catch:{ all -> 0x01ae }
            if (r2 == 0) goto L_0x018a
            com.android.server.wm.WindowManagerService r2 = r12.mService     // Catch:{ all -> 0x01ae }
            com.android.server.wm.WindowSurfacePlacer r2 = r2.mWindowPlacerLocked     // Catch:{ all -> 0x01ae }
            r2.requestTraversal()     // Catch:{ all -> 0x01ae }
            com.android.server.wm.WindowManagerService r2 = r12.mService     // Catch:{ all -> 0x01ae }
            com.android.server.wm.TaskSnapshotController r2 = r2.mTaskSnapshotController     // Catch:{ all -> 0x01ae }
            r2.setPersisterPaused(r0)     // Catch:{ all -> 0x01ae }
            java.lang.String r2 = "animating"
            android.os.Trace.asyncTraceEnd(r6, r2, r0)     // Catch:{ all -> 0x01ae }
        L_0x018a:
            r12.mLastRootAnimating = r5     // Catch:{ all -> 0x01ae }
            boolean r2 = r12.mRemoveReplacedWindows     // Catch:{ all -> 0x01ae }
            if (r2 == 0) goto L_0x0199
            com.android.server.wm.WindowManagerService r2 = r12.mService     // Catch:{ all -> 0x01ae }
            com.android.server.wm.RootWindowContainer r2 = r2.mRoot     // Catch:{ all -> 0x01ae }
            r2.removeReplacedWindows()     // Catch:{ all -> 0x01ae }
            r12.mRemoveReplacedWindows = r0     // Catch:{ all -> 0x01ae }
        L_0x0199:
            com.android.server.wm.WindowManagerService r0 = r12.mService     // Catch:{ all -> 0x01ae }
            r0.destroyPreservedSurfaceLocked()     // Catch:{ all -> 0x01ae }
            r12.executeAfterPrepareSurfacesRunnables()     // Catch:{ all -> 0x01ae }
            monitor-exit(r1)     // Catch:{ all -> 0x01ae }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x01a6:
            com.android.server.wm.WindowManagerService r2 = r12.mService     // Catch:{ all -> 0x01ae }
            java.lang.String r3 = "WindowAnimator"
            r2.closeSurfaceTransaction(r3)     // Catch:{ all -> 0x01ae }
            throw r0     // Catch:{ all -> 0x01ae }
        L_0x01ae:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x01ae }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x01b4:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x01b4 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowAnimator.animate(long):void");
    }

    private static String bulkUpdateParamsToString(int bulkUpdateParams) {
        StringBuilder builder = new StringBuilder(128);
        if ((bulkUpdateParams & 1) != 0) {
            builder.append(" UPDATE_ROTATION");
        }
        if ((bulkUpdateParams & 4) != 0) {
            builder.append(" ORIENTATION_CHANGE_COMPLETE");
        }
        return builder.toString();
    }

    public void dumpLocked(PrintWriter pw, String prefix, boolean dumpAll) {
        String subPrefix = "  " + prefix;
        String subSubPrefix = "  " + subPrefix;
        for (int i = 0; i < this.mDisplayContentsAnimators.size(); i++) {
            pw.print(prefix);
            pw.print("DisplayContentsAnimator #");
            pw.print(this.mDisplayContentsAnimators.keyAt(i));
            pw.println(":");
            DisplayContentsAnimator displayAnimator = this.mDisplayContentsAnimators.valueAt(i);
            this.mService.mRoot.getDisplayContent(this.mDisplayContentsAnimators.keyAt(i)).dumpWindowAnimators(pw, subPrefix);
            if (displayAnimator.mScreenRotationAnimation != null) {
                pw.print(subPrefix);
                pw.println("mScreenRotationAnimation:");
                displayAnimator.mScreenRotationAnimation.printTo(subSubPrefix, pw);
            } else if (dumpAll) {
                pw.print(subPrefix);
                pw.println("no ScreenRotationAnimation ");
            }
            pw.println();
        }
        pw.println();
        if (dumpAll) {
            pw.print(prefix);
            pw.print("mCurrentTime=");
            pw.println(TimeUtils.formatUptime(this.mCurrentTime));
        }
        if (this.mBulkUpdateParams != 0) {
            pw.print(prefix);
            pw.print("mBulkUpdateParams=0x");
            pw.print(Integer.toHexString(this.mBulkUpdateParams));
            pw.println(bulkUpdateParamsToString(this.mBulkUpdateParams));
        }
    }

    /* access modifiers changed from: package-private */
    public int getPendingLayoutChanges(int displayId) {
        DisplayContent displayContent;
        if (displayId >= 0 && (displayContent = this.mService.mRoot.getDisplayContent(displayId)) != null) {
            return displayContent.pendingLayoutChanges;
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void setPendingLayoutChanges(int displayId, int changes) {
        DisplayContent displayContent;
        if (displayId >= 0 && (displayContent = this.mService.mRoot.getDisplayContent(displayId)) != null) {
            displayContent.pendingLayoutChanges |= changes;
        }
    }

    private DisplayContentsAnimator getDisplayContentsAnimatorLocked(int displayId) {
        if (displayId < 0) {
            return null;
        }
        DisplayContentsAnimator displayAnimator = this.mDisplayContentsAnimators.get(displayId);
        if (displayAnimator != null || this.mService.mRoot.getDisplayContent(displayId) == null) {
            return displayAnimator;
        }
        DisplayContentsAnimator displayAnimator2 = new DisplayContentsAnimator();
        this.mDisplayContentsAnimators.put(displayId, displayAnimator2);
        return displayAnimator2;
    }

    /* access modifiers changed from: package-private */
    public void setScreenRotationAnimationLocked(int displayId, ScreenRotationAnimation animation) {
        DisplayContentsAnimator animator = getDisplayContentsAnimatorLocked(displayId);
        if (animator != null) {
            animator.mScreenRotationAnimation = animation;
        }
    }

    /* access modifiers changed from: package-private */
    public ScreenRotationAnimation getScreenRotationAnimationLocked(int displayId) {
        DisplayContentsAnimator animator;
        if (displayId >= 0 && (animator = getDisplayContentsAnimatorLocked(displayId)) != null) {
            return animator.mScreenRotationAnimation;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void requestRemovalOfReplacedWindows(WindowState win) {
        this.mRemoveReplacedWindows = true;
    }

    /* access modifiers changed from: package-private */
    public void scheduleAnimation() {
        if (!this.mAnimationFrameCallbackScheduled) {
            this.mAnimationFrameCallbackScheduled = true;
            this.mChoreographer.postFrameCallback(this.mAnimationFrameCallback);
        }
    }

    private void cancelAnimation() {
        if (this.mAnimationFrameCallbackScheduled) {
            this.mAnimationFrameCallbackScheduled = false;
            this.mChoreographer.removeFrameCallback(this.mAnimationFrameCallback);
        }
    }

    private class DisplayContentsAnimator {
        ScreenRotationAnimation mScreenRotationAnimation;

        private DisplayContentsAnimator() {
            this.mScreenRotationAnimation = null;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAnimating() {
        return this.mAnimating;
    }

    /* access modifiers changed from: package-private */
    public boolean isAnimationScheduled() {
        return this.mAnimationFrameCallbackScheduled;
    }

    /* access modifiers changed from: package-private */
    public Choreographer getChoreographer() {
        return this.mChoreographer;
    }

    /* access modifiers changed from: package-private */
    public void setAnimating(boolean animating) {
        this.mAnimating = animating;
    }

    /* access modifiers changed from: package-private */
    public void orAnimating(boolean animating) {
        this.mAnimating |= animating;
    }

    /* access modifiers changed from: package-private */
    public void addAfterPrepareSurfacesRunnable(Runnable r) {
        if (this.mInExecuteAfterPrepareSurfacesRunnables) {
            r.run();
            return;
        }
        this.mAfterPrepareSurfacesRunnables.add(r);
        scheduleAnimation();
    }

    /* access modifiers changed from: package-private */
    public void executeAfterPrepareSurfacesRunnables() {
        if (!this.mInExecuteAfterPrepareSurfacesRunnables) {
            this.mInExecuteAfterPrepareSurfacesRunnables = true;
            int size = this.mAfterPrepareSurfacesRunnables.size();
            for (int i = 0; i < size; i++) {
                this.mAfterPrepareSurfacesRunnables.get(i).run();
            }
            this.mAfterPrepareSurfacesRunnables.clear();
            this.mInExecuteAfterPrepareSurfacesRunnables = false;
        }
    }
}
