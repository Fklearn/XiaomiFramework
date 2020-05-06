package com.android.server.lights.interpolater;

import android.view.animation.Interpolator;

public class PhysicBasedInterpolator implements Interpolator {
    private float c;
    private float c1 = this.mInitial;
    private float c2;
    private float k;
    private float m = 1.0f;
    private float mInitial = -1.0f;
    private float r;
    private float w;

    public PhysicBasedInterpolator(float damping, float response) {
        double pow = Math.pow(6.283185307179586d / ((double) response), 2.0d);
        float f = this.m;
        this.k = (float) (pow * ((double) f));
        this.c = (float) (((((double) damping) * 12.566370614359172d) * ((double) f)) / ((double) response));
        float f2 = f * 4.0f * this.k;
        float f3 = this.c;
        float f4 = this.m;
        this.w = ((float) Math.sqrt((double) (f2 - (f3 * f3)))) / (f4 * 2.0f);
        this.r = -((this.c / 2.0f) * f4);
        this.c2 = (0.0f - (this.r * this.mInitial)) / this.w;
    }

    public float getInterpolation(float input) {
        return (float) ((Math.pow(2.718281828459045d, (double) (this.r * input)) * ((((double) this.c1) * Math.cos((double) (this.w * input))) + (((double) this.c2) * Math.sin((double) (this.w * input))))) + 1.0d);
    }

    public static final class Builder {
        private float mDamping = 0.95f;
        private float mResponse = 0.6f;

        public Builder setDamping(float damping) {
            this.mDamping = damping;
            return this;
        }

        public Builder setResponse(float response) {
            this.mResponse = response;
            return this;
        }

        public PhysicBasedInterpolator build() {
            return new PhysicBasedInterpolator(this.mDamping, this.mResponse);
        }
    }
}
