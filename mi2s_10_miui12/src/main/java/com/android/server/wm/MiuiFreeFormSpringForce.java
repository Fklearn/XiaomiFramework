package com.android.server.wm;

import com.android.server.wm.MiuiFreeFormDynamicAnimation;

public final class MiuiFreeFormSpringForce implements Force {
    public static final float DAMPING_RATIO_HIGH_BOUNCY = 0.2f;
    public static final float DAMPING_RATIO_LOW_BOUNCY = 0.75f;
    public static final float DAMPING_RATIO_MEDIUM_BOUNCY = 0.5f;
    public static final float DAMPING_RATIO_NO_BOUNCY = 1.0f;
    public static final float STIFFNESS_HIGH = 10000.0f;
    public static final float STIFFNESS_LOW = 200.0f;
    public static final float STIFFNESS_MEDIUM = 1500.0f;
    public static final float STIFFNESS_VERY_LOW = 50.0f;
    private static final double UNSET = Double.MAX_VALUE;
    private static final double VELOCITY_THRESHOLD_MULTIPLIER = 62.5d;
    private double mDampedFreq;
    double mDampingRatio = 0.5d;
    private double mFinalPosition = UNSET;
    private double mGammaMinus;
    private double mGammaPlus;
    private boolean mInitialized = false;
    private final MiuiFreeFormDynamicAnimation.MassState mMassState = new MiuiFreeFormDynamicAnimation.MassState();
    double mNaturalFreq = Math.sqrt(1500.0d);
    private double mValueThreshold;
    private double mVelocityThreshold;

    public MiuiFreeFormSpringForce() {
    }

    public MiuiFreeFormSpringForce(float finalPosition) {
        this.mFinalPosition = (double) finalPosition;
    }

    public MiuiFreeFormSpringForce setStiffness(float stiffness) {
        if (stiffness > 0.0f) {
            this.mNaturalFreq = Math.sqrt((double) stiffness);
            this.mInitialized = false;
            return this;
        }
        throw new IllegalArgumentException("Spring stiffness constant must be positive.");
    }

    public float getStiffness() {
        double d = this.mNaturalFreq;
        return (float) (d * d);
    }

    public MiuiFreeFormSpringForce setDampingRatio(float dampingRatio) {
        if (dampingRatio >= 0.0f) {
            this.mDampingRatio = (double) dampingRatio;
            this.mInitialized = false;
            return this;
        }
        throw new IllegalArgumentException("Damping ratio must be non-negative");
    }

    public float getDampingRatio() {
        return (float) this.mDampingRatio;
    }

    public MiuiFreeFormSpringForce setFinalPosition(float finalPosition) {
        this.mFinalPosition = (double) finalPosition;
        return this;
    }

    public float getFinalPosition() {
        return (float) this.mFinalPosition;
    }

    public float getAcceleration(float lastDisplacement, float lastVelocity) {
        float lastDisplacement2 = lastDisplacement - getFinalPosition();
        double d = this.mNaturalFreq;
        return (float) (((-(d * d)) * ((double) lastDisplacement2)) - (((double) lastVelocity) * ((d * 2.0d) * this.mDampingRatio)));
    }

    public boolean isAtEquilibrium(float value, float velocity) {
        if (((double) Math.abs(velocity)) >= this.mVelocityThreshold || ((double) Math.abs(value - getFinalPosition())) >= this.mValueThreshold) {
            return false;
        }
        return true;
    }

    private void init() {
        if (!this.mInitialized) {
            if (this.mFinalPosition != UNSET) {
                double d = this.mDampingRatio;
                if (d > 1.0d) {
                    double d2 = this.mNaturalFreq;
                    this.mGammaPlus = ((-d) * d2) + (d2 * Math.sqrt((d * d) - 1.0d));
                    double d3 = this.mDampingRatio;
                    double d4 = this.mNaturalFreq;
                    this.mGammaMinus = ((-d3) * d4) - (d4 * Math.sqrt((d3 * d3) - 1.0d));
                } else if (d >= 0.0d && d < 1.0d) {
                    this.mDampedFreq = this.mNaturalFreq * Math.sqrt(1.0d - (d * d));
                }
                this.mInitialized = true;
                return;
            }
            throw new IllegalStateException("Error: Final position of the spring must be set before the animation starts");
        }
    }

    /* access modifiers changed from: package-private */
    public MiuiFreeFormDynamicAnimation.MassState updateValues(double lastDisplacement, double lastVelocity, long timeElapsed) {
        double sinCoeff;
        double cosCoeff;
        init();
        double deltaT = ((double) timeElapsed) / 1000.0d;
        double lastDisplacement2 = lastDisplacement - this.mFinalPosition;
        double displacement = this.mDampingRatio;
        if (displacement > 1.0d) {
            double d = this.mGammaMinus;
            double d2 = this.mGammaPlus;
            double coeffA = lastDisplacement2 - (((d * lastDisplacement2) - lastVelocity) / (d - d2));
            double coeffB = ((d * lastDisplacement2) - lastVelocity) / (d - d2);
            double displacement2 = (Math.pow(2.718281828459045d, d * deltaT) * coeffA) + (Math.pow(2.718281828459045d, this.mGammaPlus * deltaT) * coeffB);
            double d3 = this.mGammaMinus;
            double pow = coeffA * d3 * Math.pow(2.718281828459045d, d3 * deltaT);
            double d4 = this.mGammaPlus;
            double d5 = lastDisplacement2;
            sinCoeff = displacement2;
            cosCoeff = pow + (coeffB * d4 * Math.pow(2.718281828459045d, d4 * deltaT));
        } else if (displacement == 1.0d) {
            double coeffA2 = lastDisplacement2;
            double d6 = this.mNaturalFreq;
            double coeffB2 = lastVelocity + (d6 * lastDisplacement2);
            sinCoeff = Math.pow(2.718281828459045d, (-d6) * deltaT) * (coeffA2 + (coeffB2 * deltaT));
            double pow2 = (coeffA2 + (coeffB2 * deltaT)) * Math.pow(2.718281828459045d, (-this.mNaturalFreq) * deltaT);
            double d7 = this.mNaturalFreq;
            double d8 = lastDisplacement2;
            cosCoeff = (pow2 * (-d7)) + (Math.pow(2.718281828459045d, (-d7) * deltaT) * coeffB2);
        } else {
            double cosCoeff2 = lastDisplacement2;
            double d9 = 1.0d / this.mDampedFreq;
            double d10 = this.mNaturalFreq;
            double sinCoeff2 = d9 * ((displacement * d10 * lastDisplacement2) + lastVelocity);
            double displacement3 = Math.pow(2.718281828459045d, (-displacement) * d10 * deltaT) * ((Math.cos(this.mDampedFreq * deltaT) * cosCoeff2) + (Math.sin(this.mDampedFreq * deltaT) * sinCoeff2));
            double d11 = this.mNaturalFreq;
            double d12 = lastDisplacement2;
            double lastDisplacement3 = this.mDampingRatio;
            double d13 = (-d11) * displacement3 * lastDisplacement3;
            double pow3 = Math.pow(2.718281828459045d, (-lastDisplacement3) * d11 * deltaT);
            double d14 = this.mDampedFreq;
            double displacement4 = displacement3;
            double sin = (-d14) * cosCoeff2 * Math.sin(d14 * deltaT);
            double d15 = this.mDampedFreq;
            sinCoeff = displacement4;
            cosCoeff = d13 + (pow3 * (sin + (d15 * sinCoeff2 * Math.cos(d15 * deltaT))));
        }
        MiuiFreeFormDynamicAnimation.MassState massState = this.mMassState;
        massState.mValue = (float) (this.mFinalPosition + sinCoeff);
        massState.mVelocity = (float) cosCoeff;
        return massState;
    }

    /* access modifiers changed from: package-private */
    public void setValueThreshold(double threshold) {
        this.mValueThreshold = Math.abs(threshold);
        this.mVelocityThreshold = this.mValueThreshold * VELOCITY_THRESHOLD_MULTIPLIER;
    }
}
