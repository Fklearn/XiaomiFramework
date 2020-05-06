package com.android.server.wm;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Trace;
import android.util.Slog;
import android.view.SurfaceControl;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.server.wm.IGestureStrategy;
import com.android.server.wm.MiuiGestureStrategy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class MiuiGestureRecentsStrategy extends MiuiGestureStrategy {
    private static final String BOTTOM = "bottom";
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator(1.5f);
    private static final int DELAYED_NOTIFY_ANIMATION_END = 200;
    private static final int DELAYED_RESET_ALL_STATUS = 500;
    private static final int DELAYED_RESET_ANIMATION = 100;
    private static final int DELAYED_RESET_INPUT = 300;
    private static final int MSG_CANCEL_ANIMATION = 3;
    private static final int MSG_RESET_ALL_STATUS = 5;
    private static final int MSG_RESET_GO_RECENTS_ANIMATION = 1;
    private static final int MSG_RESET_INPUT = 2;
    private static final int MSG_RESTART_FROM_RECENTS_ANIMATION = 4;
    private static final int MSG_START_GO_RECENTS_ANIMATION = 0;
    private static final int NORMAL_ANIMATION_DURATION = 300;
    private static final String SCALE = "scale";
    private static final String TAG = "MiuiGesture";
    private static final String TOP = "top";
    private static final String TRANSLATE_X = "translate_x";
    private static final String TRANSLATE_Y = "translate_y";
    /* access modifiers changed from: private */
    public final Rect mAnimationClipRect = new Rect();
    /* access modifiers changed from: private */
    public final Handler mAnimationHandler;
    /* access modifiers changed from: private */
    public float mAnimationScale;
    /* access modifiers changed from: private */
    public AnimationStatus mAnimationStatus = AnimationStatus.STATUS_FINISH;
    /* access modifiers changed from: private */
    public float mAnimationTransX;
    /* access modifiers changed from: private */
    public float mAnimationTransY;
    /* access modifiers changed from: private */
    public AppWindowToken mAppTokenStartFromRecents;
    @SuppressLint({"NewApi"})
    private AnimatorSet mGoRecentsAnimation = new AnimatorSet();
    private Animator.AnimatorListener mGoRecentsListener = new Animator.AnimatorListener() {
        public void onAnimationStart(Animator animation) {
            if (MiuiGestureController.DEBUG_RECENTS) {
                Slog.w("MiuiGesture", "start go RecentsAnimation");
            }
            AnimationStatus unused = MiuiGestureRecentsStrategy.this.mAnimationStatus = AnimationStatus.STATUS_GO_RECENTS_ANIMATING;
            MiuiGestureRecentsStrategy.this.mGestureController.notifyGestureAnimationStart();
        }

        public void onAnimationRepeat(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            if (MiuiGestureController.DEBUG_RECENTS) {
                Slog.w("MiuiGesture", "go recents animation end");
            }
            FullScreenEventReporter.recordJankyFrames(MiuiGestureRecentsStrategy.this.getAnimationString(), MiuiGestureRecentsStrategy.this.getTopWindow().mAttrs.packageName);
            if (MiuiGestureRecentsStrategy.this.mAppTokenStartFromRecents == null) {
                MiuiGestureRecentsStrategy.this.mGestureController.setSkipAppTransition();
            }
            MiuiGestureRecentsStrategy.this.mAnimationHandler.removeMessages(1);
            MiuiGestureRecentsStrategy.this.mAnimationHandler.sendEmptyMessageDelayed(1, 100);
            Handler access$200 = MiuiGestureRecentsStrategy.this.mAnimationHandler;
            MiuiGestureController miuiGestureController = MiuiGestureRecentsStrategy.this.mGestureController;
            Objects.requireNonNull(miuiGestureController);
            access$200.postDelayed(new Runnable() {
                public final void run() {
                    MiuiGestureController.this.notifyGestureAnimationEnd();
                }
            }, 200);
            MiuiGestureRecentsStrategy.this.mGestureController.notifyGestureFinish(false);
            MiuiGestureRecentsStrategy.this.mAnimationHandler.removeMessages(2);
            MiuiGestureRecentsStrategy.this.mAnimationHandler.sendEmptyMessageDelayed(2, 300);
        }

        public void onAnimationCancel(Animator animation) {
            Slog.w("MiuiGesture", "go recents animation cancel");
            onAnimationEnd(animation);
        }
    };
    private boolean mIsRestartAppTokenVisible;
    /* access modifiers changed from: private */
    public MiuiGestureStrategy.GestureStrategyCallback mResetCallback;
    private Rect mTargetRect = new Rect();

    private enum AnimationStatus {
        STATUS_FINISH,
        STATUS_GO_RECENTS_ANIMATING,
        STATUS_RESTART_ANIMATING
    }

    interface SurfaceOperator {
        void action(SurfaceControl surfaceControl);
    }

    MiuiGestureRecentsStrategy(MiuiGestureAnimator gestureAnimator, WindowManagerService mService, MiuiGesturePointerEventListener pointerEventListener, MiuiGestureController gestureController, MiuiGestureStrategy.GestureStrategyCallback callback) {
        super(gestureAnimator, mService, pointerEventListener, gestureController, (MiuiGestureStrategy.GestureStrategyCallback) null);
        this.mResetCallback = callback;
        this.mAnimationHandler = new GestureRecentsHandler(gestureController.mHandler.getLooper());
    }

    /* access modifiers changed from: package-private */
    public boolean onCreateAnimation(Rect appFrame, Rect curRect) {
        Task task;
        for (Map.Entry<WindowState, IGestureStrategy.WindowStateInfo> winEntry : this.mScalingWindows.entrySet()) {
            WindowState win = winEntry.getKey();
            IGestureStrategy.WindowStateInfo wInfo = winEntry.getValue();
            if (!(win == null || wInfo == null)) {
                int targetX = this.mTargetRect.left;
                int targetY = this.mTargetRect.top;
                float targetScale = ((float) this.mTargetRect.width()) / ((float) MiuiGestureDetector.sScreenWidth);
                if (win.inMultiWindowMode() && (task = win.getTask()) != null) {
                    Rect taskBounds = new Rect();
                    task.getBounds(taskBounds);
                    if (taskBounds.width() > 0) {
                        targetScale = ((float) this.mTargetRect.width()) / ((float) taskBounds.width());
                    }
                }
                int targetBottom = wInfo.mOriFrame.bottom;
                if (this.mTargetRect.width() > 0 && wInfo.mNowFrame.width() > 0) {
                    targetBottom = (this.mTargetRect.height() * wInfo.mNowFrame.width()) / this.mTargetRect.width();
                }
                wInfo.mTargetPosX = targetX;
                wInfo.mTargetPosY = targetY;
                wInfo.mTargetScale = targetScale;
                wInfo.mTargetBottom = targetBottom;
            }
        }
        return true;
    }

    public int getAnimationType() {
        return 1;
    }

    public void onAnimationStart() {
        super.onAnimationStart();
        startAnimation();
    }

    public void finishAnimation() {
    }

    /* access modifiers changed from: package-private */
    public boolean onAnimationUpdate(long currentTimeMs) {
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setRecentsItemCoordinates(int startX, int startY, int targetWidth, int targetHeight) {
        this.mTargetRect = new Rect(startX, startY, startX + targetWidth, startY + targetHeight);
    }

    private void startAnimation() {
        this.mAnimationHandler.removeMessages(5);
        this.mAppTokenStartFromRecents = null;
        this.mAnimationHandler.sendEmptyMessage(0);
    }

    public void cancelAnimation(AppWindowToken aToken) {
        this.mAnimationHandler.removeMessages(5);
        Message msg = Message.obtain();
        msg.what = 3;
        msg.obj = aToken;
        this.mAnimationHandler.sendMessage(msg);
    }

    public boolean isForbidGesture() {
        return this.mAnimationStatus != AnimationStatus.STATUS_FINISH;
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void startGoRecentsAnimation() {
        if (this.mGoRecentsAnimation.isRunning()) {
            this.mGoRecentsAnimation.cancel();
        }
        this.mGoRecentsAnimation = new AnimatorSet();
        if (this.mGestureController != null) {
            Iterator it = this.mClosingAppTokens.iterator();
            while (it.hasNext()) {
                this.mGestureController.notifyIgnoreInput((AppWindowToken) it.next(), true, true);
            }
        }
        Set<Animator> animatorSet = loadGoRecentsAnimationLocked(this.mScalingWindows);
        this.mGoRecentsAnimation.addListener(this.mGoRecentsListener);
        this.mGoRecentsAnimation.playTogether(animatorSet);
        this.mGoRecentsAnimation.start();
    }

    private Set<Animator> loadGoRecentsAnimationLocked(ConcurrentHashMap<WindowState, IGestureStrategy.WindowStateInfo> scalingWindows) {
        if (scalingWindows == null) {
            Slog.w("MiuiGesture", "Failed to load go recents animation: no app is closing.");
            return null;
        }
        Set<Animator> result = new HashSet<>();
        for (Map.Entry<WindowState, IGestureStrategy.WindowStateInfo> winEntry : scalingWindows.entrySet()) {
            ValueAnimator animator = applyAnimatorLocked(winEntry.getKey(), winEntry.getValue());
            if (animator != null) {
                result.add(animator);
            }
        }
        return result;
    }

    @SuppressLint({"NewApi"})
    private ValueAnimator applyAnimatorLocked(WindowState w, IGestureStrategy.WindowStateInfo wInfo) {
        WindowState windowState = w;
        IGestureStrategy.WindowStateInfo windowStateInfo = wInfo;
        if (windowState == null) {
            return null;
        } else if (windowStateInfo == null) {
            return null;
        } else {
            int nowX = windowStateInfo.mNowPosX;
            int nowY = windowStateInfo.mNowPosY;
            float nowScale = windowStateInfo.mNowScale;
            int startBottom = w.inMultiWindowMode() ? windowStateInfo.mNowFrame.height() : windowStateInfo.mNowFrame.bottom;
            ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATE_X, new float[]{(float) nowX, (float) windowStateInfo.mTargetPosX}), PropertyValuesHolder.ofFloat(TRANSLATE_Y, new float[]{(float) nowY, (float) windowStateInfo.mTargetPosY}), PropertyValuesHolder.ofFloat(SCALE, new float[]{nowScale, windowStateInfo.mTargetScale}), PropertyValuesHolder.ofInt(BOTTOM, new int[]{startBottom, windowStateInfo.mTargetBottom})});
            animator.setInterpolator(DECELERATE_INTERPOLATOR);
            int i = nowX;
            int i2 = nowY;
            animator.setDuration(300);
            animator.addUpdateListener(new GestureRecentsAnimatorUpdateListener(windowState, windowStateInfo));
            return animator;
        }
    }

    /* access modifiers changed from: private */
    public void handleResetGoRecentsAnimation() {
        if (this.mAnimationStatus != AnimationStatus.STATUS_RESTART_ANIMATING) {
            synchronized (this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    Iterator it = this.mClosingAppTokens.iterator();
                    while (it.hasNext()) {
                        AppWindowToken appWindowToken = (AppWindowToken) it.next();
                        if (!appWindowToken.inPinnedWindowingMode()) {
                            this.mGestureAnimator.hideTaskDimmerLayer(appWindowToken);
                            this.mGestureAnimator.hideWindow(appWindowToken);
                            this.mGestureAnimator.applyTransaction();
                        }
                    }
                    for (Map.Entry<WindowState, IGestureStrategy.WindowStateInfo> winEntry : this.mScalingWindows.entrySet()) {
                        WindowState w = winEntry.getKey();
                        resetClipWindow(w, winEntry.getValue(), this.mGestureAnimator, this.mGestureController);
                        this.mIsRestartAppTokenVisible = false;
                        resetWindowSurfaceLocked(w, this.mScalingWindows);
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            MiuiGestureStrategy.GestureStrategyCallback gestureStrategyCallback = this.mResetCallback;
            if (gestureStrategyCallback != null) {
                gestureStrategyCallback.onStrategyFinish();
            }
            this.mAnimationStatus = AnimationStatus.STATUS_FINISH;
            this.mAnimationHandler.sendEmptyMessageDelayed(5, 500);
        }
    }

    /* access modifiers changed from: private */
    public void handleResetInput() {
        Iterator it = this.mClosingAppTokens.iterator();
        while (it.hasNext()) {
            this.mGestureController.notifyIgnoreInput((AppWindowToken) it.next(), false, false);
        }
    }

    /* access modifiers changed from: private */
    public void handleCancelAnimation(AppWindowToken aToken) {
        if (aToken != null) {
            this.mAppTokenStartFromRecents = aToken;
            try {
                restartFromRecentsChecked();
                if (this.mClosingAppTokens.contains(this.mAppTokenStartFromRecents) && this.mGoRecentsAnimation.isRunning()) {
                    Slog.d("MiuiGesture", "cancel recents animation because app restarting ");
                    this.mGoRecentsAnimation.cancel();
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private void restartFromRecentsChecked() {
        if (this.mClosingAppTokens.contains(this.mAppTokenStartFromRecents)) {
            this.mAnimationStatus = AnimationStatus.STATUS_RESTART_ANIMATING;
        } else {
            synchronized (this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    Iterator it = this.mClosingAppTokens.iterator();
                    while (it.hasNext()) {
                        AppWindowToken appWindowToken = (AppWindowToken) it.next();
                        if (!appWindowToken.inPinnedWindowingMode()) {
                            this.mGestureAnimator.hideTaskDimmerLayer(appWindowToken);
                            this.mGestureAnimator.hideWindow(appWindowToken);
                            this.mGestureAnimator.applyTransaction();
                        }
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
        if (this.mAnimationStatus == AnimationStatus.STATUS_RESTART_ANIMATING) {
            this.mAnimationHandler.removeMessages(5);
            this.mAnimationHandler.sendEmptyMessage(4);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0083, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
        r1 = android.animation.ValueAnimator.ofPropertyValuesHolder(new android.animation.PropertyValuesHolder[]{android.animation.PropertyValuesHolder.ofFloat(TRANSLATE_X, new float[]{r15.mAnimationTransX, r5}), android.animation.PropertyValuesHolder.ofFloat(TRANSLATE_Y, new float[]{r15.mAnimationTransY, r6}), android.animation.PropertyValuesHolder.ofFloat(SCALE, new float[]{r15.mAnimationScale, 1.0f}), android.animation.PropertyValuesHolder.ofInt(TOP, new int[]{r15.mAnimationClipRect.top, 0}), android.animation.PropertyValuesHolder.ofInt(BOTTOM, new int[]{r15.mAnimationClipRect.bottom, r0.bottom})});
        r1.setDuration(300);
        r1.addUpdateListener(new com.android.server.wm.$$Lambda$MiuiGestureRecentsStrategy$SJVTYb2J2EgAVoHYmRvgsleDZ48(r15, r0));
        r1.addListener(new com.android.server.wm.MiuiGestureRecentsStrategy.AnonymousClass2(r15));
        r1.start();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0100, code lost:
        return;
     */
    @android.annotation.SuppressLint({"NewApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleRestartFromRecentsAnimation() {
        /*
            r15 = this;
            android.os.Handler r0 = r15.mAnimationHandler
            r1 = 2
            r0.removeMessages(r1)
            r15.handleResetInput()
            com.android.server.wm.MiuiGestureController r0 = r15.mGestureController
            r0.setSkipAppTransition()
            com.android.server.wm.MiuiGestureController r0 = r15.mGestureController
            r2 = 1
            r0.setKeepWallpaperShowing(r2)
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r3 = 0
            java.util.concurrent.ConcurrentHashMap r4 = r15.mScalingWindows
            java.util.Set r4 = r4.entrySet()
            java.util.Iterator r5 = r4.iterator()
        L_0x0024:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L_0x0046
            java.lang.Object r6 = r5.next()
            java.util.Map$Entry r6 = (java.util.Map.Entry) r6
            java.lang.Object r7 = r6.getKey()
            com.android.server.wm.WindowState r7 = (com.android.server.wm.WindowState) r7
            android.view.WindowManager$LayoutParams r8 = r7.mAttrs
            int r8 = r8.type
            if (r8 != r2) goto L_0x0045
            r3 = r7
            com.android.server.wm.WindowFrames r5 = r7.mWindowFrames
            android.graphics.Rect r5 = r5.mContainingFrame
            r0.set(r5)
            goto L_0x0046
        L_0x0045:
            goto L_0x0024
        L_0x0046:
            boolean r5 = com.android.server.wm.MiuiGestureController.DEBUG_RECENTS
            if (r5 == 0) goto L_0x0051
            java.lang.String r5 = "MiuiGesture"
            java.lang.String r6 = "restartFromRecentsAnimation"
            android.util.Slog.d(r5, r6)
        L_0x0051:
            r5 = 0
            r6 = 0
            com.android.server.wm.WindowManagerService r7 = r15.mService
            com.android.server.wm.WindowManagerGlobalLock r7 = r7.mGlobalLock
            monitor-enter(r7)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0101 }
            if (r3 == 0) goto L_0x0082
            boolean r8 = r3.inMultiWindowMode()     // Catch:{ all -> 0x0101 }
            if (r8 == 0) goto L_0x0082
            com.android.server.wm.AppWindowToken r8 = r3.mAppToken     // Catch:{ all -> 0x0101 }
            if (r8 == 0) goto L_0x0082
            com.android.server.wm.AppWindowToken r8 = r3.mAppToken     // Catch:{ all -> 0x0101 }
            com.android.server.wm.Task r8 = r8.getTask()     // Catch:{ all -> 0x0101 }
            if (r8 != 0) goto L_0x0074
            monitor-exit(r7)     // Catch:{ all -> 0x0101 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0074:
            android.graphics.Rect r9 = new android.graphics.Rect     // Catch:{ all -> 0x0101 }
            r9.<init>()     // Catch:{ all -> 0x0101 }
            r8.getBounds(r9)     // Catch:{ all -> 0x0101 }
            int r10 = r9.left     // Catch:{ all -> 0x0101 }
            float r5 = (float) r10     // Catch:{ all -> 0x0101 }
            int r10 = r9.top     // Catch:{ all -> 0x0101 }
            float r6 = (float) r10     // Catch:{ all -> 0x0101 }
        L_0x0082:
            monitor-exit(r7)     // Catch:{ all -> 0x0101 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            float[] r7 = new float[r1]
            float r8 = r15.mAnimationTransX
            r9 = 0
            r7[r9] = r8
            r7[r2] = r5
            java.lang.String r8 = "translate_x"
            android.animation.PropertyValuesHolder r7 = android.animation.PropertyValuesHolder.ofFloat(r8, r7)
            float[] r8 = new float[r1]
            float r10 = r15.mAnimationTransY
            r8[r9] = r10
            r8[r2] = r6
            java.lang.String r10 = "translate_y"
            android.animation.PropertyValuesHolder r8 = android.animation.PropertyValuesHolder.ofFloat(r10, r8)
            float[] r10 = new float[r1]
            float r11 = r15.mAnimationScale
            r10[r9] = r11
            r11 = 1065353216(0x3f800000, float:1.0)
            r10[r2] = r11
            java.lang.String r11 = "scale"
            android.animation.PropertyValuesHolder r10 = android.animation.PropertyValuesHolder.ofFloat(r11, r10)
            int[] r11 = new int[r1]
            android.graphics.Rect r12 = r15.mAnimationClipRect
            int r12 = r12.top
            r11[r9] = r12
            r11[r2] = r9
            java.lang.String r12 = "top"
            android.animation.PropertyValuesHolder r11 = android.animation.PropertyValuesHolder.ofInt(r12, r11)
            int[] r12 = new int[r1]
            android.graphics.Rect r13 = r15.mAnimationClipRect
            int r13 = r13.bottom
            r12[r9] = r13
            int r13 = r0.bottom
            r12[r2] = r13
            java.lang.String r13 = "bottom"
            android.animation.PropertyValuesHolder r12 = android.animation.PropertyValuesHolder.ofInt(r13, r12)
            r13 = 5
            android.animation.PropertyValuesHolder[] r13 = new android.animation.PropertyValuesHolder[r13]
            r13[r9] = r7
            r13[r2] = r8
            r13[r1] = r10
            r1 = 3
            r13[r1] = r11
            r1 = 4
            r13[r1] = r12
            android.animation.ValueAnimator r1 = android.animation.ValueAnimator.ofPropertyValuesHolder(r13)
            r13 = 300(0x12c, double:1.48E-321)
            r1.setDuration(r13)
            com.android.server.wm.-$$Lambda$MiuiGestureRecentsStrategy$SJVTYb2J2EgAVoHYmRvgsleDZ48 r2 = new com.android.server.wm.-$$Lambda$MiuiGestureRecentsStrategy$SJVTYb2J2EgAVoHYmRvgsleDZ48
            r2.<init>(r0)
            r1.addUpdateListener(r2)
            com.android.server.wm.MiuiGestureRecentsStrategy$2 r2 = new com.android.server.wm.MiuiGestureRecentsStrategy$2
            r2.<init>()
            r1.addListener(r2)
            r1.start()
            return
        L_0x0101:
            r1 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x0101 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGestureRecentsStrategy.handleRestartFromRecentsAnimation():void");
    }

    public /* synthetic */ void lambda$handleRestartFromRecentsAnimation$0$MiuiGestureRecentsStrategy(Rect winFrame, ValueAnimator animation) {
        ValueAnimator valueAnimator = animation;
        float transX = ((Float) valueAnimator.getAnimatedValue(TRANSLATE_X)).floatValue();
        float transY = ((Float) valueAnimator.getAnimatedValue(TRANSLATE_Y)).floatValue();
        float scale1 = ((Float) valueAnimator.getAnimatedValue(SCALE)).floatValue();
        int top1 = ((Integer) valueAnimator.getAnimatedValue(TOP)).intValue();
        int bottom1 = ((Integer) valueAnimator.getAnimatedValue(BOTTOM)).intValue();
        Rect tmp = new Rect(winFrame);
        tmp.top = top1;
        tmp.bottom = bottom1;
        Iterator it = this.mClosingAppTokens.iterator();
        while (it.hasNext()) {
            AppWindowToken aToken = (AppWindowToken) it.next();
            this.mGestureAnimator.recreateLeashIfNeeded(aToken);
            this.mGestureAnimator.setWindowCropInTransaction(aToken, tmp);
            this.mGestureAnimator.setPositionInTransaction(aToken, transX, transY);
            int displayRoundCorner = AppTransitionInjector.DISPLAY_ROUND_CORNER_RADIUS;
            float cornerRadius = ((float) displayRoundCorner) - ((1.0f - animation.getAnimatedFraction()) * ((float) (displayRoundCorner - 60)));
            this.mGestureAnimator.setRoundCorner(aToken, cornerRadius);
            float f = cornerRadius;
            int i = displayRoundCorner;
            AppWindowToken appWindowToken = aToken;
            this.mGestureAnimator.setMatrixInTransaction(aToken, scale1, 0.0f, 0.0f, scale1);
        }
        this.mGestureAnimator.applyTransaction();
        if (!this.mIsRestartAppTokenVisible) {
            synchronized (this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    Iterator it2 = this.mClosingAppTokens.iterator();
                    while (it2.hasNext()) {
                        this.mGestureAnimator.showWindow((AppWindowToken) it2.next());
                        this.mGestureAnimator.applyTransaction();
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            this.mIsRestartAppTokenVisible = true;
        }
    }

    /* access modifiers changed from: private */
    public void resetAllStatus() {
        setAnimating(false);
        super.finishAnimation();
        this.mAnimationStatus = AnimationStatus.STATUS_FINISH;
    }

    @SuppressLint({"NewApi"})
    private class GestureRecentsAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private IGestureStrategy.WindowStateInfo mInfo;
        private WindowState mWin;

        GestureRecentsAnimatorUpdateListener(WindowState w, IGestureStrategy.WindowStateInfo info) {
            this.mWin = w;
            this.mInfo = info;
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            onAnimationUpdateLocked(animation);
        }

        private void onAnimationUpdateLocked(ValueAnimator animation) {
            ValueAnimator valueAnimator = animation;
            if (this.mWin != null) {
                Trace.traceBegin(32, "gesture recents animation");
                FullScreenEventReporter.caculateAnimationFrameInterval(MiuiGestureRecentsStrategy.this.getAnimationString());
                Rect clipRect = new Rect();
                int curBottom = ((Integer) valueAnimator.getAnimatedValue(MiuiGestureRecentsStrategy.BOTTOM)).intValue();
                float fraction = animation.getAnimatedFraction();
                boolean z = false;
                int offsetY = this.mWin.mAttrs.type != 2 ? (int) (((float) this.mWin.getContentInsets().top) * fraction) : 0;
                clipRect.set(this.mInfo.mNowFrame);
                if (this.mWin.inMultiWindowMode()) {
                    clipRect.offsetTo(0, offsetY);
                    clipRect.bottom = curBottom;
                } else {
                    clipRect.bottom = curBottom;
                    clipRect.offsetTo(0, offsetY);
                }
                if (this.mWin.mAttrs.isFullscreen()) {
                    MiuiGestureRecentsStrategy.this.mAnimationClipRect.set(clipRect);
                }
                if (this.mWin.getActivityType() == 2) {
                    z = true;
                }
                boolean isHome = z;
                int type = this.mWin.mAttrs.type;
                if (type == 1 || type == 3) {
                    float unused = MiuiGestureRecentsStrategy.this.mAnimationTransX = ((Float) valueAnimator.getAnimatedValue(MiuiGestureRecentsStrategy.TRANSLATE_X)).floatValue();
                    float unused2 = MiuiGestureRecentsStrategy.this.mAnimationTransY = ((Float) valueAnimator.getAnimatedValue(MiuiGestureRecentsStrategy.TRANSLATE_Y)).floatValue() - (((float) offsetY) * MiuiGestureRecentsStrategy.this.mAnimationScale);
                    float unused3 = MiuiGestureRecentsStrategy.this.mAnimationScale = ((Float) valueAnimator.getAnimatedValue(MiuiGestureRecentsStrategy.SCALE)).floatValue();
                    AppWindowToken aToken = this.mWin.mAppToken;
                    if (aToken != null) {
                        if (!isHome) {
                            MiuiGestureRecentsStrategy.this.mGestureAnimator.setWindowCropInTransaction(aToken, clipRect);
                        }
                        MiuiGestureRecentsStrategy.this.mGestureAnimator.setPositionInTransaction(aToken, MiuiGestureRecentsStrategy.this.mAnimationTransX, MiuiGestureRecentsStrategy.this.mAnimationTransY);
                        MiuiGestureRecentsStrategy.this.mGestureAnimator.setMatrixInTransaction(aToken, MiuiGestureRecentsStrategy.this.mAnimationScale, 0.0f, 0.0f, MiuiGestureRecentsStrategy.this.mAnimationScale);
                        int displayRoundCorner = AppTransitionInjector.DISPLAY_ROUND_CORNER_RADIUS;
                        MiuiGestureRecentsStrategy.this.mGestureAnimator.setRoundCorner(aToken, ((float) displayRoundCorner) - (((float) (displayRoundCorner - 60)) * fraction));
                        MiuiGestureRecentsStrategy.this.mGestureAnimator.applyTransaction();
                    } else {
                        return;
                    }
                }
                Trace.traceBegin(32, "gesture recents animation");
            }
        }
    }

    private class GestureRecentsHandler extends Handler {
        GestureRecentsHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 0) {
                MiuiGestureRecentsStrategy.this.startGoRecentsAnimation();
            } else if (i == 1) {
                MiuiGestureRecentsStrategy.this.handleResetGoRecentsAnimation();
            } else if (i == 2) {
                MiuiGestureRecentsStrategy.this.handleResetInput();
            } else if (i == 3) {
                MiuiGestureRecentsStrategy.this.handleCancelAnimation((AppWindowToken) msg.obj);
            } else if (i == 4) {
                MiuiGestureRecentsStrategy.this.handleRestartFromRecentsAnimation();
            } else if (i == 5) {
                MiuiGestureRecentsStrategy.this.resetAllStatus();
            }
        }
    }

    private void resetClipWindow(WindowState w, IGestureStrategy.WindowStateInfo wInfo, MiuiGestureAnimator animator, MiuiGestureController controller) {
        if (w != null && wInfo != null && animator != null && controller != null && wInfo.mNeedClip) {
            Rect clipRect = new Rect(w.mWindowFrames.mFrame);
            boolean isMainWin = true;
            if (!(w.mAttrs.type == 1 || w.mAttrs.type == 3)) {
                isMainWin = false;
            }
            if (isMainWin || wInfo.mHasShowStartingWindow) {
                animator.setWindowCropInTransaction(w.mAppToken, clipRect);
                animator.applyTransaction();
            }
        }
    }

    private void resetWindowSurfaceLocked(WindowState w, ConcurrentHashMap<WindowState, IGestureStrategy.WindowStateInfo> scalingWindows) {
        AppWindowToken aToken;
        WindowState mainWin;
        if (!(w.mAttrs.type != 3 || (aToken = w.mAppToken) == null || (mainWin = aToken.findMainWindow(false)) == null)) {
            try {
                if (!scalingWindows.containsKey(mainWin)) {
                    hideSurfaceLocked(mainWin);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        operateSurfaceChecked(w, $$Lambda$MiuiGestureRecentsStrategy$8XjPZHoXwDdShdLxmbH5oHAKW9A.INSTANCE);
    }

    private void hideSurfaceLocked(WindowState w) {
        operateSurfaceChecked(w, $$Lambda$ZKqNttTn1Vm8vMbULzHFh9E8o24.INSTANCE);
    }

    private void operateSurfaceChecked(WindowState w, SurfaceOperator operator) {
        if (w != null && w.mWinAnimator != null && w.mWinAnimator.mSurfaceController != null) {
            SurfaceControl sc = w.mWinAnimator.mSurfaceController.mSurfaceControl;
            SurfaceControl.openTransaction();
            if (!(sc == null || operator == null)) {
                try {
                    operator.action(sc);
                } catch (Throwable th) {
                    SurfaceControl.closeTransaction();
                    throw th;
                }
            }
            SurfaceControl.closeTransaction();
        }
    }
}
