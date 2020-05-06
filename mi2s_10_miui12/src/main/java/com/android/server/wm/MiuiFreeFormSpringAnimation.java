package com.android.server.wm;

import com.android.server.wm.MiuiFreeFormDynamicAnimation;
import com.miui.internal.dynamicanimation.animation.FloatPropertyCompat;
import com.miui.internal.dynamicanimation.animation.FloatValueHolder;

public final class MiuiFreeFormSpringAnimation extends MiuiFreeFormDynamicAnimation<MiuiFreeFormSpringAnimation> {
    private static final float UNSET = Float.MAX_VALUE;
    private boolean mEndRequested = false;
    private float mPendingPosition = UNSET;
    private MiuiFreeFormSpringForce mSpring = null;

    public MiuiFreeFormSpringAnimation(FloatValueHolder floatValueHolder) {
        super(floatValueHolder);
    }

    public <WindowState> MiuiFreeFormSpringAnimation(WindowState object, FloatPropertyCompat<WindowState> property) {
        super(object, property);
    }

    public <WindowState> MiuiFreeFormSpringAnimation(WindowState object, FloatPropertyCompat<WindowState> property, float finalPosition) {
        super(object, property);
        this.mSpring = new MiuiFreeFormSpringForce(finalPosition);
    }

    public MiuiFreeFormSpringForce getSpring() {
        return this.mSpring;
    }

    public MiuiFreeFormSpringAnimation setSpring(MiuiFreeFormSpringForce force) {
        this.mSpring = force;
        return this;
    }

    public void start(boolean manully) {
        sanityCheck();
        this.mSpring.setValueThreshold((double) getValueThreshold());
        super.start(manully);
    }

    public void animateToFinalPosition(float finalPosition) {
        if (isRunning()) {
            this.mPendingPosition = finalPosition;
            return;
        }
        if (this.mSpring == null) {
            this.mSpring = new MiuiFreeFormSpringForce(finalPosition);
        }
        this.mSpring.setFinalPosition(finalPosition);
        start();
    }

    public void skipToEnd() {
        if (!canSkipToEnd()) {
            throw new UnsupportedOperationException("Spring animations can only come to an end when there is damping");
        } else if (this.mRunning) {
            this.mEndRequested = true;
        }
    }

    public boolean canSkipToEnd() {
        return this.mSpring.mDampingRatio > 0.0d;
    }

    private void sanityCheck() {
        MiuiFreeFormSpringForce miuiFreeFormSpringForce = this.mSpring;
        if (miuiFreeFormSpringForce != null) {
            double finalPosition = (double) miuiFreeFormSpringForce.getFinalPosition();
            if (finalPosition > ((double) this.mMaxValue)) {
                throw new UnsupportedOperationException("Final position of the spring cannot be greater than the max value.");
            } else if (finalPosition < ((double) this.mMinValue)) {
                throw new UnsupportedOperationException("Final position of the spring cannot be less than the min value.");
            }
        } else {
            throw new UnsupportedOperationException("Incomplete SpringAnimation: Either final position or a spring force needs to be set.");
        }
    }

    /* access modifiers changed from: package-private */
    public boolean updateValueAndVelocity(long deltaT) {
        if (this.mEndRequested) {
            float f = this.mPendingPosition;
            if (f != UNSET) {
                this.mSpring.setFinalPosition(f);
                this.mPendingPosition = UNSET;
            }
            this.mValue = this.mSpring.getFinalPosition();
            this.mVelocity = 0.0f;
            this.mEndRequested = false;
            return true;
        }
        if (this.mPendingPosition != UNSET) {
            double finalPosition = (double) this.mSpring.getFinalPosition();
            MiuiFreeFormDynamicAnimation.MassState massState = this.mSpring.updateValues((double) this.mValue, (double) this.mVelocity, deltaT / 2);
            this.mSpring.setFinalPosition(this.mPendingPosition);
            this.mPendingPosition = UNSET;
            MiuiFreeFormDynamicAnimation.MassState massState2 = this.mSpring.updateValues((double) massState.mValue, (double) massState.mVelocity, deltaT / 2);
            this.mValue = massState2.mValue;
            this.mVelocity = massState2.mVelocity;
        } else {
            MiuiFreeFormDynamicAnimation.MassState massState3 = this.mSpring.updateValues((double) this.mValue, (double) this.mVelocity, deltaT);
            this.mValue = massState3.mValue;
            this.mVelocity = massState3.mVelocity;
        }
        this.mValue = Math.max(this.mValue, this.mMinValue);
        this.mValue = Math.min(this.mValue, this.mMaxValue);
        if (!isAtEquilibrium(this.mValue, this.mVelocity)) {
            return false;
        }
        this.mValue = this.mSpring.getFinalPosition();
        this.mVelocity = 0.0f;
        return true;
    }

    /* access modifiers changed from: package-private */
    public float getAcceleration(float value, float velocity) {
        return this.mSpring.getAcceleration(value, velocity);
    }

    /* access modifiers changed from: package-private */
    public boolean isAtEquilibrium(float value, float velocity) {
        return this.mSpring.isAtEquilibrium(value, velocity);
    }

    /* access modifiers changed from: package-private */
    public void setValueThreshold(float threshold) {
    }
}
