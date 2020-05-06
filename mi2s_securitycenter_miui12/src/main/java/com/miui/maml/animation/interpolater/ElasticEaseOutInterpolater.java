package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;
import com.miui.maml.data.Expression;

public class ElasticEaseOutInterpolater implements Interpolator {
    private float mAmplitude = 0.0f;
    private Expression mAmplitudeExp;
    private float mPriod = 0.3f;
    private Expression mPriodExp;

    public ElasticEaseOutInterpolater() {
    }

    public ElasticEaseOutInterpolater(float f, float f2) {
        this.mPriod = f;
        this.mAmplitude = f2;
    }

    public ElasticEaseOutInterpolater(Expression[] expressionArr) {
        if (expressionArr != null) {
            if (expressionArr.length > 0) {
                this.mAmplitudeExp = expressionArr[0];
            }
            if (expressionArr.length > 1) {
                this.mPriodExp = expressionArr[1];
            }
        }
    }

    public float getInterpolation(float f) {
        float f2;
        Expression expression = this.mAmplitudeExp;
        if (expression != null) {
            this.mAmplitude = (float) expression.evaluate();
        }
        Expression expression2 = this.mPriodExp;
        if (expression2 != null) {
            this.mPriod = (float) expression2.evaluate();
        }
        float f3 = this.mAmplitude;
        if (f == 0.0f) {
            return 0.0f;
        }
        if (f == 1.0f) {
            return 1.0f;
        }
        if (f3 < 1.0f) {
            f2 = this.mPriod / 4.0f;
            f3 = 1.0f;
        } else {
            f2 = (float) ((((double) this.mPriod) / 6.283185307179586d) * Math.asin((double) (1.0f / f3)));
        }
        return (float) ((((double) f3) * Math.pow(2.0d, (double) (-10.0f * f)) * Math.sin((((double) (f - f2)) * 6.283185307179586d) / ((double) this.mPriod))) + 1.0d);
    }
}
