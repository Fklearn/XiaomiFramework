package com.miui.maml.folme;

import android.util.ArrayMap;
import com.miui.maml.elements.AnimatedScreenElement;
import d.a.g.C0574a;
import d.a.g.C0575b;

public abstract class AnimatedProperty extends C0575b<AnimatedScreenElement> implements IAnimatedProperty<AnimatedScreenElement> {
    public static final AnimatedProperty ALPHA = new AnimatedProperty(PROPERTY_NAME_ALPHA) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mAlphaProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mAlphaProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mAlphaProperty.setVelocity((double) f);
        }
    };
    public static final AnimatedProperty HEIGHT = new AnimatedProperty(PROPERTY_NAME_H) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mHeightProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mHeightProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mHeightProperty.setVelocity((double) f);
        }
    };
    public static final AnimatedProperty PIVOT_X = new AnimatedProperty(PROPERTY_NAME_PIVOT_X) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mPivotXProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mPivotXProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mPivotXProperty.setVelocity((double) f);
        }
    };
    public static final AnimatedProperty PIVOT_Y = new AnimatedProperty(PROPERTY_NAME_PIVOT_Y) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mPivotYProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mPivotYProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mPivotYProperty.setVelocity((double) f);
        }
    };
    public static final AnimatedProperty PIVOT_Z = new AnimatedProperty(PROPERTY_NAME_PIVOT_Z) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mPivotZProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mPivotZProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mPivotZProperty.setVelocity((double) f);
        }
    };
    public static final String PROPERTY_NAME_ALPHA = "alpha";
    public static final String PROPERTY_NAME_H = "h";
    public static final String PROPERTY_NAME_PIVOT_X = "pivotX";
    public static final String PROPERTY_NAME_PIVOT_Y = "pivotY";
    public static final String PROPERTY_NAME_PIVOT_Z = "pivotZ";
    public static final String PROPERTY_NAME_ROTATION = "rotation";
    public static final String PROPERTY_NAME_ROTATION_X = "rotationX";
    public static final String PROPERTY_NAME_ROTATION_Y = "rotationY";
    public static final String PROPERTY_NAME_ROTATION_Z = "rotationZ";
    public static final String PROPERTY_NAME_SCALE_X = "scaleX";
    public static final String PROPERTY_NAME_SCALE_Y = "scaleY";
    public static final String PROPERTY_NAME_TINT_COLOR = "tintColor";
    public static final String PROPERTY_NAME_W = "w";
    public static final String PROPERTY_NAME_X = "x";
    public static final String PROPERTY_NAME_Y = "y";
    public static final AnimatedProperty ROTATION = new AnimatedProperty(PROPERTY_NAME_ROTATION) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mRotationProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mRotationProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mRotationProperty.setVelocity((double) f);
        }
    };
    public static final AnimatedProperty ROTATION_X = new AnimatedProperty(PROPERTY_NAME_ROTATION_X) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mRotationXProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mRotationXProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mRotationXProperty.setVelocity((double) f);
        }
    };
    public static final AnimatedProperty ROTATION_Y = new AnimatedProperty(PROPERTY_NAME_ROTATION_Y) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mRotationYProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mRotationYProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mRotationYProperty.setVelocity((double) f);
        }
    };
    public static final AnimatedProperty ROTATION_Z = new AnimatedProperty(PROPERTY_NAME_ROTATION_Z) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mRotationZProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mRotationZProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mRotationZProperty.setVelocity((double) f);
        }
    };
    public static final AnimatedProperty SCALE_X = new AnimatedProperty(PROPERTY_NAME_SCALE_X) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mScaleXProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mScaleXProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mScaleXProperty.setVelocity((double) f);
        }
    };
    public static final AnimatedProperty SCALE_Y = new AnimatedProperty(PROPERTY_NAME_SCALE_Y) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mScaleYProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mScaleYProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mScaleYProperty.setVelocity((double) f);
        }
    };
    public static final AnimatedColorProperty TINT_COLOR = new AnimatedColorProperty(PROPERTY_NAME_TINT_COLOR) {
        public int getIntValue(AnimatedScreenElement animatedScreenElement) {
            return (int) ((long) animatedScreenElement.mTintColorProperty.getValue());
        }

        public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
            animatedScreenElement.mTintColorProperty.setValue((double) i);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mTintColorProperty.setVelocity((double) f);
        }
    };
    public static final AnimatedProperty WIDTH = new AnimatedProperty(PROPERTY_NAME_W) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mWidthProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mWidthProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mHeightProperty.setVelocity((double) f);
        }
    };
    public static final AnimatedProperty X = new AnimatedProperty(PROPERTY_NAME_X) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mXProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mXProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mXProperty.setVelocity((double) f);
        }
    };
    public static final AnimatedProperty Y = new AnimatedProperty(PROPERTY_NAME_Y) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            return (float) animatedScreenElement.mYProperty.getValue();
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mYProperty.setValue((double) f);
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            animatedScreenElement.mYProperty.setVelocity((double) f);
        }
    };
    public static ArrayMap<String, C0575b> sPropertyNameMap = new ArrayMap<>();

    public static abstract class AnimatedColorProperty extends C0574a<AnimatedScreenElement> implements IAnimatedProperty<AnimatedScreenElement> {
        public AnimatedColorProperty(String str) {
            super(str);
        }
    }

    static {
        ArrayMap<String, C0575b> arrayMap = sPropertyNameMap;
        Object obj = PROPERTY_NAME_PIVOT_Z;
        arrayMap.put(PROPERTY_NAME_X, X);
        sPropertyNameMap.put(PROPERTY_NAME_Y, Y);
        sPropertyNameMap.put(PROPERTY_NAME_SCALE_X, SCALE_X);
        sPropertyNameMap.put(PROPERTY_NAME_SCALE_Y, SCALE_Y);
        sPropertyNameMap.put(PROPERTY_NAME_ALPHA, ALPHA);
        sPropertyNameMap.put(PROPERTY_NAME_H, HEIGHT);
        sPropertyNameMap.put(PROPERTY_NAME_W, WIDTH);
        sPropertyNameMap.put(PROPERTY_NAME_ROTATION, ROTATION);
        sPropertyNameMap.put(PROPERTY_NAME_ROTATION_X, ROTATION_X);
        sPropertyNameMap.put(PROPERTY_NAME_ROTATION_Y, ROTATION_Y);
        sPropertyNameMap.put(PROPERTY_NAME_ROTATION_Z, ROTATION_Z);
        sPropertyNameMap.put(PROPERTY_NAME_TINT_COLOR, TINT_COLOR);
        sPropertyNameMap.put(PROPERTY_NAME_PIVOT_X, PIVOT_X);
        sPropertyNameMap.put(PROPERTY_NAME_PIVOT_Y, PIVOT_Y);
        sPropertyNameMap.put(obj, PIVOT_Z);
    }

    public AnimatedProperty(String str) {
        super(str);
    }

    public static C0575b getPropertyByName(String str) {
        return sPropertyNameMap.get(str);
    }
}
