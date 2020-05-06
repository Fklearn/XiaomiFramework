package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;
import com.miui.maml.data.Expression;

public class BackEaseInInterpolater implements Interpolator {
    private float mFactor = 1.70158f;
    private Expression mFactorExp;

    public BackEaseInInterpolater() {
    }

    public BackEaseInInterpolater(float f) {
        this.mFactor = f;
    }

    public BackEaseInInterpolater(Expression[] expressionArr) {
        if (expressionArr != null && expressionArr.length > 0) {
            this.mFactorExp = expressionArr[0];
        }
    }

    public float getInterpolation(float f) {
        Expression expression = this.mFactorExp;
        if (expression != null) {
            this.mFactor = (float) expression.evaluate();
        }
        float f2 = this.mFactor;
        return f * f * (((1.0f + f2) * f) - f2);
    }
}
