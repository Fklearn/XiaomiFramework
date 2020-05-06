package com.miui.maml.shader;

import android.graphics.SweepGradient;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.folme.AnimatedProperty;
import org.w3c.dom.Element;

public class SweepGradientElement extends ShaderElement {
    public static final String TAG_NAME = "SweepGradient";
    private float mAngle;
    private Expression mAngleExp;

    public SweepGradientElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mAngleExp = Expression.build(getVariables(), element.getAttribute(AnimatedProperty.PROPERTY_NAME_ROTATION));
        this.mGradientStops.update();
    }

    private final float getAngle() {
        Expression expression = this.mAngleExp;
        if (expression != null) {
            return (float) expression.evaluate();
        }
        return 0.0f;
    }

    public void onGradientStopsChanged() {
        this.mX = 0.0f;
        this.mY = 0.0f;
        this.mAngle = 0.0f;
        this.mShader = new SweepGradient(0.0f, 0.0f, this.mGradientStops.getColors(), this.mGradientStops.getPositions());
    }

    public boolean updateShaderMatrix() {
        float x = getX();
        float y = getY();
        float angle = getAngle();
        if (x == this.mX && y == this.mY && angle == this.mAngle) {
            return false;
        }
        this.mX = x;
        this.mY = y;
        this.mAngle = angle;
        this.mShaderMatrix.reset();
        this.mShaderMatrix.preTranslate(-x, -y);
        this.mShaderMatrix.setRotate(angle);
        this.mShaderMatrix.postTranslate(x, y);
        return true;
    }
}
