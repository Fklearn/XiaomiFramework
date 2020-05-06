package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.elements.GeometryScreenElement;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.maml.folme.PropertyWrapper;
import org.w3c.dom.Element;

public class RectangleScreenElement extends GeometryScreenElement {
    public static final AnimatedProperty CORNER_RADIUS_X = new AnimatedProperty(PROPERTY_NAME_CORNER_RADIUS_X) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            if (animatedScreenElement instanceof RectangleScreenElement) {
                return (float) ((RectangleScreenElement) animatedScreenElement).mRXProperty.getValue();
            }
            return 0.0f;
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof RectangleScreenElement) {
                ((RectangleScreenElement) animatedScreenElement).mRXProperty.setValue((double) f);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof RectangleScreenElement) {
                ((RectangleScreenElement) animatedScreenElement).mRXProperty.setVelocity((double) f);
            }
        }
    };
    public static final AnimatedProperty CORNER_RADIUS_Y = new AnimatedProperty(PROPERTY_NAME_CORNER_RADIUS_Y) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            if (animatedScreenElement instanceof RectangleScreenElement) {
                return (float) ((RectangleScreenElement) animatedScreenElement).mRYProperty.getValue();
            }
            return 0.0f;
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof RectangleScreenElement) {
                ((RectangleScreenElement) animatedScreenElement).mRYProperty.setValue((double) f);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof RectangleScreenElement) {
                ((RectangleScreenElement) animatedScreenElement).mRYProperty.setVelocity((double) f);
            }
        }
    };
    private static final String LOG_TAG = "RectangleScreenElement";
    private static final String PROPERTY_NAME_CORNER_RADIUS_X = "cornerRadiusX";
    private static final String PROPERTY_NAME_CORNER_RADIUS_Y = "cornerRadiusY";
    public static final String TAG_NAME = "Rectangle";
    private float mCornerRadiusX;
    private float mCornerRadiusY;
    /* access modifiers changed from: private */
    public PropertyWrapper mRXProperty;
    /* access modifiers changed from: private */
    public PropertyWrapper mRYProperty;

    static {
        AnimatedProperty.sPropertyNameMap.put(PROPERTY_NAME_CORNER_RADIUS_X, CORNER_RADIUS_X);
        AnimatedTarget.sPropertyMap.put(1004, CORNER_RADIUS_X);
        AnimatedTarget.sPropertyTypeMap.put(CORNER_RADIUS_X, 1006);
        AnimatedProperty.sPropertyNameMap.put(PROPERTY_NAME_CORNER_RADIUS_Y, CORNER_RADIUS_Y);
        AnimatedTarget.sPropertyMap.put(1005, CORNER_RADIUS_Y);
        AnimatedTarget.sPropertyTypeMap.put(CORNER_RADIUS_Y, 1007);
    }

    public RectangleScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        resolveCornerRadius(element);
    }

    private void resolveCornerRadius(Element element) {
        Expression[] buildMultiple = Expression.buildMultiple(getVariables(), element.getAttribute("cornerRadiusExp"));
        if (buildMultiple == null) {
            String[] split = getAttr(element, "cornerRadius").split(",");
            try {
                if (split.length >= 1) {
                    if (split.length == 1) {
                        float scale = scale((double) Float.parseFloat(split[0]));
                        this.mCornerRadiusY = scale;
                        this.mCornerRadiusX = scale;
                    } else {
                        this.mCornerRadiusX = scale((double) Float.parseFloat(split[0]));
                        this.mCornerRadiusY = scale((double) Float.parseFloat(split[1]));
                    }
                } else {
                    return;
                }
            } catch (NumberFormatException unused) {
                Log.w(LOG_TAG, "illegal number format of cornerRadius.");
            }
        }
        Expression expression = null;
        Expression expression2 = (buildMultiple == null || buildMultiple.length <= 0) ? null : buildMultiple[0];
        if (buildMultiple != null && buildMultiple.length > 1) {
            expression = buildMultiple[1];
        }
        this.mRXProperty = new PropertyWrapper(this.mName + ".cornerRadiusX", getVariables(), expression2, isInFolmeMode(), descale((double) this.mCornerRadiusX));
        this.mRYProperty = new PropertyWrapper(this.mName + ".cornerRadiusY", getVariables(), expression, isInFolmeMode(), descale((double) this.mCornerRadiusY));
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        super.doTick(j);
        this.mCornerRadiusX = scale(this.mRXProperty.getValue());
        this.mCornerRadiusY = scale(this.mRYProperty.getValue());
    }

    /* access modifiers changed from: protected */
    public void initProperties() {
        super.initProperties();
        this.mRXProperty.init();
        this.mRYProperty.init();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas, GeometryScreenElement.DrawMode drawMode) {
        float width = getWidth();
        float height = getHeight();
        float left = getLeft(0.0f, width);
        float top = getTop(0.0f, height);
        if (width <= 0.0f) {
            width = 0.0f;
        }
        float f = width + left;
        if (height <= 0.0f) {
            height = 0.0f;
        }
        float f2 = height + top;
        if (drawMode == GeometryScreenElement.DrawMode.STROKE_OUTER) {
            float f3 = this.mWeight;
            left -= f3 / 2.0f;
            top -= f3 / 2.0f;
            f += f3 / 2.0f;
            f2 += f3 / 2.0f;
        } else if (drawMode == GeometryScreenElement.DrawMode.STROKE_INNER) {
            float f4 = this.mWeight;
            left += f4 / 2.0f;
            top += f4 / 2.0f;
            f -= f4 / 2.0f;
            f2 -= f4 / 2.0f;
        }
        float f5 = f;
        float f6 = f2;
        float f7 = left;
        float f8 = top;
        if (this.mCornerRadiusX <= 0.0f || this.mCornerRadiusY <= 0.0f) {
            canvas.drawRect(f7, f8, f5, f6, this.mPaint);
            return;
        }
        canvas.drawRoundRect(new RectF(f7, f8, f5, f6), this.mCornerRadiusX, this.mCornerRadiusY, this.mPaint);
    }
}
