package com.miui.maml.shader;

import android.graphics.LinearGradient;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import org.w3c.dom.Element;

public class LinearGradientElement extends ShaderElement {
    public static final String TAG_NAME = "LinearGradient";
    private float mEndX;
    private Expression mEndXExp;
    private float mEndY;
    private Expression mEndYExp;

    public LinearGradientElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mEndXExp = Expression.build(screenElementRoot.getVariables(), element.getAttribute("x1"));
        this.mEndYExp = Expression.build(screenElementRoot.getVariables(), element.getAttribute("y1"));
        this.mGradientStops.update();
    }

    private final float getEndX() {
        Expression expression = this.mEndXExp;
        return (float) ((expression != null ? expression.evaluate() : 0.0d) * ((double) this.mRoot.getScale()));
    }

    private final float getEndY() {
        Expression expression = this.mEndYExp;
        return (float) ((expression != null ? expression.evaluate() : 0.0d) * ((double) this.mRoot.getScale()));
    }

    public void onGradientStopsChanged() {
        this.mX = 0.0f;
        this.mY = 0.0f;
        this.mEndX = 1.0f;
        this.mEndY = 1.0f;
        this.mShader = new LinearGradient(0.0f, 0.0f, 1.0f, 1.0f, this.mGradientStops.getColors(), this.mGradientStops.getPositions(), this.mTileMode);
    }

    public boolean updateShaderMatrix() {
        float x = getX();
        float y = getY();
        float endX = getEndX();
        float endY = getEndY();
        if (x == this.mX && y == this.mY && endX == this.mEndX && endY == this.mEndY) {
            return false;
        }
        this.mX = x;
        this.mY = y;
        this.mEndX = endX;
        this.mEndY = endY;
        this.mShaderMatrix.reset();
        this.mShaderMatrix.setPolyToPoly(new float[]{0.0f, 0.0f, 1.0f, 1.0f}, 0, new float[]{x, y, endX, endY}, 0, 2);
        return true;
    }
}
