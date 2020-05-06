package com.miui.maml.elements;

import android.graphics.Canvas;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.elements.GeometryScreenElement;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.maml.folme.PropertyWrapper;
import org.w3c.dom.Element;

public class CircleScreenElement extends GeometryScreenElement {
    private static final String PROPERTY_NAME_R = "r";
    public static final AnimatedProperty R = new AnimatedProperty(PROPERTY_NAME_R) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            if (animatedScreenElement instanceof CircleScreenElement) {
                return (float) ((CircleScreenElement) animatedScreenElement).mRadiusProperty.getValue();
            }
            return 0.0f;
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof CircleScreenElement) {
                ((CircleScreenElement) animatedScreenElement).mRadiusProperty.setValue((double) f);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof CircleScreenElement) {
                ((CircleScreenElement) animatedScreenElement).mRadiusProperty.setVelocity((double) f);
            }
        }
    };
    public static final String TAG_NAME = "Circle";
    /* access modifiers changed from: private */
    public PropertyWrapper mRadiusProperty;

    static {
        AnimatedProperty.sPropertyNameMap.put(PROPERTY_NAME_R, R);
        AnimatedTarget.sPropertyMap.put(1001, R);
        AnimatedTarget.sPropertyTypeMap.put(R, 1001);
    }

    public CircleScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        Expression build = Expression.build(screenElementRoot.getVariables(), getAttr(element, PROPERTY_NAME_R));
        this.mAlign = ScreenElement.Align.CENTER;
        this.mAlignV = ScreenElement.AlignV.CENTER;
        this.mRadiusProperty = new PropertyWrapper(this.mName + ".r", screenElementRoot.getVariables(), build, isInFolmeMode(), 0.0d);
    }

    private final float getRadius() {
        return scale(this.mRadiusProperty.getValue());
    }

    /* access modifiers changed from: protected */
    public void initProperties() {
        super.initProperties();
        this.mRadiusProperty.init();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas, GeometryScreenElement.DrawMode drawMode) {
        float radius = getRadius();
        if (drawMode == GeometryScreenElement.DrawMode.STROKE_OUTER) {
            radius += this.mWeight / 2.0f;
        } else if (drawMode == GeometryScreenElement.DrawMode.STROKE_INNER) {
            radius -= this.mWeight / 2.0f;
        }
        canvas.drawCircle(0.0f, 0.0f, radius, this.mPaint);
    }
}
