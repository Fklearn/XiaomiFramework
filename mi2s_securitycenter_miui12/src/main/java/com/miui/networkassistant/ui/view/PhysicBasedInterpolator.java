package com.miui.networkassistant.ui.view;

import android.view.animation.Interpolator;

public class PhysicBasedInterpolator implements Interpolator {

    /* renamed from: c  reason: collision with root package name */
    private float f5661c;
    private float c1 = this.mInitial;
    private float c2;
    private float k;
    private float m = 1.0f;
    private float mInitial = -1.0f;
    private float r;
    private float w;

    public static final class Builder {
        private float mDamping = 0.95f;
        private float mResponse = 0.6f;

        public PhysicBasedInterpolator build() {
            return new PhysicBasedInterpolator(this.mDamping, this.mResponse);
        }

        public Builder setDamping(float f) {
            this.mDamping = f;
            return this;
        }

        public Builder setResponse(float f) {
            this.mResponse = f;
            return this;
        }
    }

    public PhysicBasedInterpolator(float f, float f2) {
        double d2 = (double) f2;
        double pow = Math.pow(6.283185307179586d / d2, 2.0d);
        float f3 = this.m;
        this.k = (float) (pow * ((double) f3));
        this.f5661c = (float) (((((double) f) * 12.566370614359172d) * ((double) f3)) / d2);
        float f4 = f3 * 4.0f * this.k;
        float f5 = this.f5661c;
        float f6 = this.m;
        this.w = ((float) Math.sqrt((double) (f4 - (f5 * f5)))) / (f6 * 2.0f);
        this.r = -((this.f5661c / 2.0f) * f6);
        this.c2 = (0.0f - (this.r * this.mInitial)) / this.w;
    }

    public float getInterpolation(float f) {
        return (float) ((Math.pow(2.718281828459045d, (double) (this.r * f)) * ((((double) this.c1) * Math.cos((double) (this.w * f))) + (((double) this.c2) * Math.sin((double) (this.w * f))))) + 1.0d);
    }
}
