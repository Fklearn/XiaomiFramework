package com.android.server.display;

import android.animation.ValueAnimator;
import android.util.IntProperty;
import android.view.Choreographer;

final class MiuiRampAnimator<T> {
    /* access modifiers changed from: private */
    public float mAnimatedValue;
    /* access modifiers changed from: private */
    public boolean mAnimating;
    private final Runnable mAnimationCallback = new Runnable() {
        public void run() {
            long frameTimeNanos = MiuiRampAnimator.this.mChoreographer.getFrameTimeNanos();
            float timeDelta = ((float) (frameTimeNanos - MiuiRampAnimator.this.mLastFrameTimeNanos)) * 1.0E-9f;
            long unused = MiuiRampAnimator.this.mLastFrameTimeNanos = frameTimeNanos;
            float scale = ValueAnimator.getDurationScale();
            if (scale == 0.0f) {
                MiuiRampAnimator miuiRampAnimator = MiuiRampAnimator.this;
                float unused2 = miuiRampAnimator.mAnimatedValue = (float) miuiRampAnimator.mTargetValue;
            } else {
                float amount = (((float) MiuiRampAnimator.this.mRate) * timeDelta) / scale;
                if (MiuiRampAnimator.this.mTargetValue > MiuiRampAnimator.this.mCurrentValue) {
                    MiuiRampAnimator miuiRampAnimator2 = MiuiRampAnimator.this;
                    float unused3 = miuiRampAnimator2.mAnimatedValue = Math.min(miuiRampAnimator2.mAnimatedValue + amount, (float) MiuiRampAnimator.this.mTargetValue);
                } else {
                    MiuiRampAnimator miuiRampAnimator3 = MiuiRampAnimator.this;
                    float unused4 = miuiRampAnimator3.mAnimatedValue = Math.max(miuiRampAnimator3.mAnimatedValue - amount, (float) MiuiRampAnimator.this.mTargetValue);
                }
            }
            int oldCurrentValue = MiuiRampAnimator.this.mCurrentValue;
            MiuiRampAnimator miuiRampAnimator4 = MiuiRampAnimator.this;
            int unused5 = miuiRampAnimator4.mCurrentValue = Math.round(miuiRampAnimator4.mAnimatedValue);
            if (oldCurrentValue != MiuiRampAnimator.this.mCurrentValue) {
                MiuiRampAnimator.this.mProperty.setValue(MiuiRampAnimator.this.mObject, MiuiRampAnimator.this.mCurrentValue);
            }
            if (MiuiRampAnimator.this.mTargetValue != MiuiRampAnimator.this.mCurrentValue) {
                MiuiRampAnimator.this.postAnimationCallback();
                return;
            }
            boolean unused6 = MiuiRampAnimator.this.mAnimating = false;
            if (MiuiRampAnimator.this.mListener != null) {
                MiuiRampAnimator.this.mListener.onAnimationEnd();
            }
        }
    };
    /* access modifiers changed from: private */
    public final Choreographer mChoreographer;
    /* access modifiers changed from: private */
    public int mCurrentValue;
    private boolean mFirstTime = true;
    /* access modifiers changed from: private */
    public long mLastFrameTimeNanos;
    /* access modifiers changed from: private */
    public Listener mListener;
    /* access modifiers changed from: private */
    public final T mObject;
    /* access modifiers changed from: private */
    public final IntProperty<T> mProperty;
    /* access modifiers changed from: private */
    public int mRate;
    /* access modifiers changed from: private */
    public int mTargetValue;

    public interface Listener {
        void onAnimationEnd();
    }

    public MiuiRampAnimator(T object, IntProperty<T> property) {
        this.mObject = object;
        this.mProperty = property;
        this.mChoreographer = Choreographer.getInstance();
    }

    public boolean animateTo(int target, int rate) {
        int i;
        int i2;
        int i3;
        boolean z = false;
        if (!this.mFirstTime && rate > 0) {
            if (!this.mAnimating || rate > this.mRate || ((target <= (i2 = this.mCurrentValue) && i2 <= this.mTargetValue) || (this.mTargetValue <= (i3 = this.mCurrentValue) && i3 <= target))) {
                this.mRate = rate;
            }
            if (this.mTargetValue != target) {
                z = true;
            }
            boolean changed = z;
            this.mTargetValue = target;
            if (!this.mAnimating && target != (i = this.mCurrentValue)) {
                this.mAnimating = true;
                this.mAnimatedValue = (float) i;
                this.mLastFrameTimeNanos = System.nanoTime();
                postAnimationCallback();
            }
            return changed;
        } else if (!this.mFirstTime && target == this.mCurrentValue) {
            return false;
        } else {
            this.mFirstTime = false;
            this.mRate = 0;
            this.mTargetValue = target;
            this.mCurrentValue = target;
            this.mProperty.setValue(this.mObject, target);
            if (this.mAnimating) {
                this.mAnimating = false;
                cancelAnimationCallback();
            }
            Listener listener = this.mListener;
            if (listener != null) {
                listener.onAnimationEnd();
            }
            return true;
        }
    }

    public boolean isAnimating() {
        return this.mAnimating;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    /* access modifiers changed from: private */
    public void postAnimationCallback() {
        this.mChoreographer.postCallback(1, this.mAnimationCallback, (Object) null);
    }

    private void cancelAnimationCallback() {
        this.mChoreographer.removeCallbacks(1, this.mAnimationCallback, (Object) null);
    }
}
