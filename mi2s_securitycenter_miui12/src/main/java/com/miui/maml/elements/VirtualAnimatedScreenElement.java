package com.miui.maml.elements;

import android.graphics.Canvas;
import android.util.ArrayMap;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.folme.AnimatedPropertyType;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.maml.folme.PropertyWrapper;
import d.a.g.C0575b;
import org.w3c.dom.Element;

public class VirtualAnimatedScreenElement extends AnimatedScreenElement {
    public static final AnimatedProperty.AnimatedColorProperty COLOR_1 = new AnimatedProperty.AnimatedColorProperty("color1") {
        public int getIntValue(AnimatedScreenElement animatedScreenElement) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                return (int) ((long) ((VirtualAnimatedScreenElement) animatedScreenElement).mColor1Property.getValue());
            }
            return 0;
        }

        public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                ((VirtualAnimatedScreenElement) animatedScreenElement).mColor1Property.setValue((double) i);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                ((VirtualAnimatedScreenElement) animatedScreenElement).mColor1Property.setVelocity((double) f);
            }
        }
    };
    public static final AnimatedProperty.AnimatedColorProperty COLOR_2 = new AnimatedProperty.AnimatedColorProperty("color1") {
        public int getIntValue(AnimatedScreenElement animatedScreenElement) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                return (int) ((long) ((VirtualAnimatedScreenElement) animatedScreenElement).mColor2Property.getValue());
            }
            return 0;
        }

        public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                ((VirtualAnimatedScreenElement) animatedScreenElement).mColor2Property.setValue((double) i);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                ((VirtualAnimatedScreenElement) animatedScreenElement).mColor2Property.setVelocity((double) f);
            }
        }
    };
    public static final AnimatedProperty FLOAT_1 = new AnimatedProperty(PROPERTY_NAME_RESERVE_FLOAT_1) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                return (float) ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat1Property.getValue();
            }
            return 0.0f;
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat1Property.setValue((double) f);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat1Property.setVelocity((double) f);
            }
        }
    };
    public static final AnimatedProperty FLOAT_2 = new AnimatedProperty(PROPERTY_NAME_RESERVE_FLOAT_2) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                return (float) ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat2Property.getValue();
            }
            return 0.0f;
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat2Property.setValue((double) f);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat2Property.setVelocity((double) f);
            }
        }
    };
    public static final AnimatedProperty FLOAT_3 = new AnimatedProperty(PROPERTY_NAME_RESERVE_FLOAT_3) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                return (float) ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat3Property.getValue();
            }
            return 0.0f;
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat3Property.setValue((double) f);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat3Property.setVelocity((double) f);
            }
        }
    };
    public static final AnimatedProperty FLOAT_4 = new AnimatedProperty(PROPERTY_NAME_RESERVE_FLOAT_4) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                return (float) ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat4Property.getValue();
            }
            return 0.0f;
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat4Property.setValue((double) f);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat4Property.setVelocity((double) f);
            }
        }
    };
    private static final String PROPERTY_NAME_RESERVE_COLOR_1 = "color1";
    private static final String PROPERTY_NAME_RESERVE_COLOR_2 = "color1";
    private static final String PROPERTY_NAME_RESERVE_FLOAT_1 = "float1";
    private static final String PROPERTY_NAME_RESERVE_FLOAT_2 = "float2";
    private static final String PROPERTY_NAME_RESERVE_FLOAT_3 = "float3";
    private static final String PROPERTY_NAME_RESERVE_FLOAT_4 = "float4";
    public static final String TAG_NAME = "VirtualElement";
    /* access modifiers changed from: private */
    public PropertyWrapper mColor1Property = new PropertyWrapper(this.mName + ".color1", getVariables(), (Expression) null, isInFolmeMode(), 0.0d);
    /* access modifiers changed from: private */
    public PropertyWrapper mColor2Property = new PropertyWrapper(this.mName + ".color2", getVariables(), (Expression) null, isInFolmeMode(), 0.0d);
    /* access modifiers changed from: private */
    public PropertyWrapper mFloat1Property = new PropertyWrapper(this.mName + ".float1", getVariables(), (Expression) null, isInFolmeMode(), 0.0d);
    /* access modifiers changed from: private */
    public PropertyWrapper mFloat2Property = new PropertyWrapper(this.mName + ".float2", getVariables(), (Expression) null, isInFolmeMode(), 0.0d);
    /* access modifiers changed from: private */
    public PropertyWrapper mFloat3Property = new PropertyWrapper(this.mName + ".float3", getVariables(), (Expression) null, isInFolmeMode(), 0.0d);
    /* access modifiers changed from: private */
    public PropertyWrapper mFloat4Property = new PropertyWrapper(this.mName + ".float4", getVariables(), (Expression) null, isInFolmeMode(), 0.0d);

    static {
        AnimatedProperty.sPropertyNameMap.put("color1", COLOR_1);
        AnimatedProperty.sPropertyNameMap.put("color1", COLOR_2);
        AnimatedProperty.sPropertyNameMap.put(PROPERTY_NAME_RESERVE_FLOAT_1, FLOAT_1);
        AnimatedProperty.sPropertyNameMap.put(PROPERTY_NAME_RESERVE_FLOAT_2, FLOAT_2);
        AnimatedProperty.sPropertyNameMap.put(PROPERTY_NAME_RESERVE_FLOAT_3, FLOAT_3);
        AnimatedProperty.sPropertyNameMap.put(PROPERTY_NAME_RESERVE_FLOAT_4, FLOAT_4);
        ArrayMap<Integer, C0575b> arrayMap = AnimatedTarget.sPropertyMap;
        Integer valueOf = Integer.valueOf(AnimatedPropertyType.RESERVE_COLOR_1);
        arrayMap.put(valueOf, COLOR_1);
        ArrayMap<Integer, C0575b> arrayMap2 = AnimatedTarget.sPropertyMap;
        Integer valueOf2 = Integer.valueOf(AnimatedPropertyType.RESERVE_COLOR_2);
        arrayMap2.put(valueOf2, COLOR_2);
        ArrayMap<Integer, C0575b> arrayMap3 = AnimatedTarget.sPropertyMap;
        Integer valueOf3 = Integer.valueOf(AnimatedPropertyType.RESERVE_FLOAT_1);
        arrayMap3.put(valueOf3, FLOAT_1);
        ArrayMap<Integer, C0575b> arrayMap4 = AnimatedTarget.sPropertyMap;
        Integer valueOf4 = Integer.valueOf(AnimatedPropertyType.RESERVE_FLOAT_2);
        arrayMap4.put(valueOf4, FLOAT_2);
        ArrayMap<Integer, C0575b> arrayMap5 = AnimatedTarget.sPropertyMap;
        Integer valueOf5 = Integer.valueOf(AnimatedPropertyType.RESERVE_FLOAT_3);
        arrayMap5.put(valueOf5, FLOAT_3);
        ArrayMap<Integer, C0575b> arrayMap6 = AnimatedTarget.sPropertyMap;
        Integer valueOf6 = Integer.valueOf(AnimatedPropertyType.RESERVE_FLOAT_4);
        arrayMap6.put(valueOf6, FLOAT_4);
        AnimatedTarget.sPropertyTypeMap.put(COLOR_1, valueOf);
        AnimatedTarget.sPropertyTypeMap.put(COLOR_2, valueOf2);
        AnimatedTarget.sPropertyTypeMap.put(FLOAT_1, valueOf3);
        AnimatedTarget.sPropertyTypeMap.put(FLOAT_2, valueOf4);
        AnimatedTarget.sPropertyTypeMap.put(FLOAT_3, valueOf5);
        AnimatedTarget.sPropertyTypeMap.put(FLOAT_4, valueOf6);
    }

    public VirtualAnimatedScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public boolean isInFolmeMode() {
        return this.mHasName;
    }

    public boolean isVisible() {
        return false;
    }

    public void tick(long j) {
    }
}
