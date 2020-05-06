package com.android.server.wm;

import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Slog;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.server.wm.MiuiGestureStrategy;
import java.util.Iterator;

abstract class MiuiGestureSimpleStrategy extends MiuiGestureStrategy {
    private static final int NORMAL_ANIMATION_DURATION = 300;
    private static String TAG = "MiuiGesture";
    private ValueHolder clipHolder;
    private long mAnimationStartTime;
    private Rect mAppFrame;
    private float mDuration;
    private float mFraction;
    private final Interpolator mInterpolator = new DecelerateInterpolator(1.5f);
    private Rect mTargetRect;
    private ValueHolder scaleHolder;
    private ValueHolder xHolder;
    private ValueHolder yHolder;

    public MiuiGestureSimpleStrategy(MiuiGestureAnimator mSurfaceController, WindowManagerService mService, MiuiGesturePointerEventListener pointerEventListener, MiuiGestureController gestureController, MiuiGestureStrategy.GestureStrategyCallback callback) {
        super(mSurfaceController, mService, pointerEventListener, gestureController, callback);
    }

    /* access modifiers changed from: package-private */
    public boolean onCreateAnimation(Rect appFrame, Rect curRect) {
        Rect rect;
        setDuration(this.mService.getTransitionAnimationScaleLocked() * 300.0f);
        if (appFrame == null || (rect = this.mTargetRect) == null || rect.isEmpty()) {
            Slog.d(TAG, "mTargetRect is null");
            return false;
        }
        this.mAppFrame = appFrame;
        this.mFraction = 0.0f;
        float startScale = (((float) curRect.width()) * 1.0f) / ((float) appFrame.width());
        this.xHolder = new ValueHolder((float) curRect.left, (float) this.mTargetRect.left);
        this.yHolder = new ValueHolder((float) curRect.top, (float) this.mTargetRect.top);
        this.scaleHolder = new ValueHolder(startScale, (((float) this.mTargetRect.width()) * 1.0f) / ((float) appFrame.width()));
        this.clipHolder = new ValueHolder((float) curRect.bottom, (float) this.mTargetRect.bottom);
        if (!MiuiGestureController.DEBUG_RECENTS && !MiuiGestureController.DEBUG_CANCEL) {
            return true;
        }
        String str = TAG;
        Slog.d(str, "startX=" + this.xHolder.startValue + ", targetX = " + this.xHolder.targetValue + ", startY=" + this.yHolder.startValue + ",targetY=" + this.yHolder.targetValue + ", startScale=" + this.scaleHolder.startValue + ", targetScale=" + this.scaleHolder.targetValue + ", startClip=" + this.clipHolder.startValue + ", targetClip=" + this.clipHolder.targetValue);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean onAnimationUpdate(long currentTimeMs) {
        if (this.mAnimationStartTime == 0) {
            this.mAnimationStartTime = SystemClock.uptimeMillis();
        }
        this.mFraction = this.mInterpolator.getInterpolation((((float) (currentTimeMs - this.mAnimationStartTime)) * 1.0f) / getDuration());
        float f = this.mFraction;
        if (f > 1.0f) {
            return false;
        }
        float x = this.xHolder.getCurValue(f);
        float y = this.yHolder.getCurValue(this.mFraction);
        float scale = this.scaleHolder.getCurValue(this.mFraction);
        float clip = this.clipHolder.getCurValue(this.mFraction);
        Rect clipRect = new Rect();
        clipRect.set(this.mAppFrame);
        clipRect.bottom = (int) clip;
        if (MiuiGestureController.DEBUG_STEP) {
            String str = TAG;
            Slog.d(str, "tx=" + x + ",ty=" + y + ",scale=" + scale + ",clipRect=" + clipRect);
        }
        Iterator it = this.mClosingAppTokens.iterator();
        while (it.hasNext()) {
            AppWindowToken token = (AppWindowToken) it.next();
            setAppTokenTransformation(token, 1.0f, scale, scale, x, y);
            this.mGestureAnimator.setWindowCropInTransaction(token, clipRect);
        }
        return true;
    }

    public void finishAnimation() {
        if (!isAnimating()) {
            super.finishAnimation();
            return;
        }
        this.mTargetRect = null;
        this.xHolder.reset();
        this.yHolder.reset();
        this.scaleHolder.reset();
        this.clipHolder.reset();
        this.mAnimationStartTime = 0;
        this.mGestureController.notifyGestureFinish(true);
        super.finishAnimation();
    }

    /* access modifiers changed from: protected */
    public void setTargetRect(Rect targetRect) {
        this.mTargetRect = targetRect;
    }

    /* access modifiers changed from: protected */
    public float getDuration() {
        return this.mDuration;
    }

    /* access modifiers changed from: protected */
    public void setDuration(float duration) {
        this.mDuration = duration;
    }

    class ValueHolder {
        float curValue;
        float startValue;
        float targetValue;

        ValueHolder(float startValue2, float targetValue2) {
            this.startValue = startValue2;
            this.targetValue = targetValue2;
        }

        /* access modifiers changed from: package-private */
        public float getCurValue(float fraction) {
            float f = this.targetValue;
            float f2 = this.startValue;
            this.curValue = ((f - f2) * fraction) + f2;
            return this.curValue;
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.startValue = 0.0f;
            this.curValue = 0.0f;
            this.targetValue = 0.0f;
        }
    }
}
