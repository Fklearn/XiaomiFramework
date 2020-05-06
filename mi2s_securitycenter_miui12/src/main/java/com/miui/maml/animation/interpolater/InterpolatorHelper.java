package com.miui.maml.animation.interpolater;

import android.text.TextUtils;
import android.view.animation.Interpolator;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import org.w3c.dom.Element;

public class InterpolatorHelper {
    private static final String VAR_RATIO = "__ratio";
    private Expression mEaseFunExp;
    private Interpolator mInterpolator;
    private IndexedVariable mRatioVar;

    public InterpolatorHelper(Variables variables, String str, String str2, String str3) {
        this.mInterpolator = InterpolatorFactory.create(str, Expression.buildMultiple(variables, str3));
        this.mEaseFunExp = Expression.build(variables, str2);
        if (this.mEaseFunExp != null) {
            this.mRatioVar = new IndexedVariable(VAR_RATIO, variables, true);
        }
    }

    public static InterpolatorHelper create(Variables variables, Element element) {
        String attribute = element.getAttribute("easeType");
        String attribute2 = element.getAttribute("easeExp");
        String attribute3 = element.getAttribute("easeParamsExp");
        if (!TextUtils.isEmpty(attribute) || !TextUtils.isEmpty(attribute2)) {
            return new InterpolatorHelper(variables, attribute, attribute2, attribute3);
        }
        return null;
    }

    public float get(float f) {
        if (this.mEaseFunExp != null) {
            this.mRatioVar.set((double) f);
            return (float) this.mEaseFunExp.evaluate();
        }
        Interpolator interpolator = this.mInterpolator;
        return interpolator != null ? interpolator.getInterpolation(f) : f;
    }

    public boolean isValid() {
        return (this.mEaseFunExp == null && this.mInterpolator == null) ? false : true;
    }
}
