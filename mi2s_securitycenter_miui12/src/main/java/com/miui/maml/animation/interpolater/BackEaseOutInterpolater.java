package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;
import com.miui.maml.data.Expression;

public class BackEaseOutInterpolater implements Interpolator {
    private float mFactor = 1.70158f;
    private Expression mFactorExp;

    public BackEaseOutInterpolater() {
    }

    public BackEaseOutInterpolater(float f) {
        this.mFactor = f;
    }

    public BackEaseOutInterpolater(Expression[] expressionArr) {
        if (expressionArr != null && expressionArr.length > 0) {
            this.mFactorExp = expressionArr[0];
        }
    }

    public float getInterpolation(float f) {
        Expression expression = this.mFactorExp;
        if (expression != null) {
            this.mFactor = (float) expression.evaluate();
        }
        float f2 = f - 1.0f;
        float f3 = this.mFactor;
        return (f2 * f2 * (((f3 + 1.0f) * f2) + f3)) + 1.0f;
    }
}
