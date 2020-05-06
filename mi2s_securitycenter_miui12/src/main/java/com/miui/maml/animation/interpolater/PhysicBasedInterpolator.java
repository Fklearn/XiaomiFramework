package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;
import com.miui.maml.data.Expression;

public class PhysicBasedInterpolator implements Interpolator {

    /* renamed from: c  reason: collision with root package name */
    private float f5612c;
    private float c1 = this.mInitial;
    private float c2;
    private float k;
    private float m = 1.0f;
    private float mDamping = 0.9f;
    private Expression mDampingExp;
    private float mInitial = -1.0f;
    private boolean mNeedEvaluate = true;
    private float mResponse = 0.3f;
    private Expression mResponseExp;
    private float r;
    private float w;

    public PhysicBasedInterpolator() {
    }

    public PhysicBasedInterpolator(float f, float f2) {
        this.mDamping = f;
        this.mResponse = f2;
    }

    public PhysicBasedInterpolator(Expression[] expressionArr) {
        if (expressionArr != null) {
            if (expressionArr.length > 0) {
                this.mDampingExp = expressionArr[0];
            }
            if (expressionArr.length > 1) {
                this.mResponseExp = expressionArr[1];
            }
        }
    }

    private void evaluate() {
        if (this.mNeedEvaluate) {
            double pow = Math.pow(6.283185307179586d / ((double) this.mResponse), 2.0d);
            float f = this.m;
            this.k = (float) (pow * ((double) f));
            this.f5612c = (float) (((((double) this.mDamping) * 12.566370614359172d) * ((double) f)) / ((double) this.mResponse));
            float f2 = f * 4.0f * this.k;
            float f3 = this.f5612c;
            float f4 = this.m;
            this.w = ((float) Math.sqrt((double) (f2 - (f3 * f3)))) / (f4 * 2.0f);
            this.r = -((this.f5612c / 2.0f) * f4);
            this.c2 = (0.0f - (this.r * this.mInitial)) / this.w;
            this.mNeedEvaluate = false;
        }
    }

    public float getInterpolation(float f) {
        Expression expression = this.mDampingExp;
        if (expression != null) {
            float evaluate = (float) expression.evaluate();
            if (this.mDamping != evaluate) {
                this.mDamping = evaluate;
                this.mNeedEvaluate = true;
            }
        }
        Expression expression2 = this.mResponseExp;
        if (expression2 != null) {
            float evaluate2 = (float) expression2.evaluate();
            if (this.mResponse != evaluate2) {
                this.mResponse = evaluate2;
                this.mNeedEvaluate = true;
            }
        }
        evaluate();
        return (float) ((Math.pow(2.718281828459045d, (double) (this.r * f)) * ((((double) this.c1) * Math.cos((double) (this.w * f))) + (((double) this.c2) * Math.sin((double) (this.w * f))))) + 1.0d);
    }
}
