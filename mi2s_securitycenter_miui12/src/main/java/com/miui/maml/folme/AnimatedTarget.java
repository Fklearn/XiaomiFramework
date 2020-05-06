package com.miui.maml.folme;

import android.util.ArrayMap;
import com.miui.maml.elements.AnimatedScreenElement;
import d.a.d;
import d.a.g.C0575b;
import d.a.i;
import java.lang.ref.WeakReference;

public class AnimatedTarget extends d<AnimatedScreenElement> {
    public static final String STATE_TAG_FROM = "from";
    public static final String STATE_TAG_SET_TO = "setTo";
    public static final String STATE_TAG_TO = "to";
    public static i<AnimatedScreenElement> sCreator = new i<AnimatedScreenElement>() {
        public d createTarget(AnimatedScreenElement animatedScreenElement) {
            return new AnimatedTarget(animatedScreenElement);
        }
    };
    public static ArrayMap<Integer, C0575b> sPropertyMap = new ArrayMap<>();
    public static ArrayMap<C0575b, Integer> sPropertyTypeMap = new ArrayMap<>();
    private WeakReference<AnimatedScreenElement> mElementRef;

    static {
        sPropertyTypeMap.put(AnimatedProperty.X, 0);
        sPropertyTypeMap.put(AnimatedProperty.Y, 1);
        sPropertyTypeMap.put(AnimatedProperty.SCALE_X, 2);
        sPropertyTypeMap.put(AnimatedProperty.SCALE_Y, 3);
        sPropertyTypeMap.put(AnimatedProperty.ALPHA, 4);
        sPropertyTypeMap.put(AnimatedProperty.HEIGHT, 5);
        sPropertyTypeMap.put(AnimatedProperty.WIDTH, 6);
        sPropertyTypeMap.put(AnimatedProperty.ROTATION, 9);
        sPropertyTypeMap.put(AnimatedProperty.ROTATION_X, 10);
        sPropertyTypeMap.put(AnimatedProperty.ROTATION_Y, 11);
        sPropertyTypeMap.put(AnimatedProperty.TINT_COLOR, 1008);
        sPropertyTypeMap.put(AnimatedProperty.PIVOT_X, 1009);
        sPropertyTypeMap.put(AnimatedProperty.PIVOT_Y, 1010);
        sPropertyTypeMap.put(AnimatedProperty.PIVOT_Z, 1011);
        sPropertyMap.put(0, AnimatedProperty.X);
        sPropertyMap.put(1, AnimatedProperty.Y);
        sPropertyMap.put(2, AnimatedProperty.SCALE_X);
        sPropertyMap.put(3, AnimatedProperty.SCALE_Y);
        sPropertyMap.put(4, AnimatedProperty.ALPHA);
        sPropertyMap.put(5, AnimatedProperty.HEIGHT);
        sPropertyMap.put(6, AnimatedProperty.WIDTH);
        sPropertyMap.put(9, AnimatedProperty.ROTATION);
        sPropertyMap.put(10, AnimatedProperty.ROTATION_X);
        sPropertyMap.put(11, AnimatedProperty.ROTATION_Y);
        sPropertyMap.put(1008, AnimatedProperty.TINT_COLOR);
        sPropertyMap.put(1009, AnimatedProperty.PIVOT_X);
        sPropertyMap.put(1010, AnimatedProperty.PIVOT_Y);
        sPropertyMap.put(1011, AnimatedProperty.PIVOT_Z);
    }

    public AnimatedTarget(AnimatedScreenElement animatedScreenElement) {
        this.mElementRef = new WeakReference<>(animatedScreenElement);
    }

    public void executeOnInitialized(Runnable runnable) {
        if (((AnimatedScreenElement) this.mElementRef.get()) != null) {
            runnable.run();
        }
    }

    public float getDefaultMinVisible() {
        return 1.0f;
    }

    public void getLocationOnScreen(int[] iArr) {
        AnimatedScreenElement animatedScreenElement = (AnimatedScreenElement) this.mElementRef.get();
        if (animatedScreenElement != null && iArr != null && iArr.length > 1) {
            iArr[0] = (int) animatedScreenElement.getAbsoluteLeft();
            iArr[1] = (int) animatedScreenElement.getAbsoluteTop();
        }
    }

    public C0575b getProperty(int i) {
        return sPropertyMap.get(Integer.valueOf(i));
    }

    public AnimatedScreenElement getTargetObject() {
        return (AnimatedScreenElement) this.mElementRef.get();
    }

    public int getType(C0575b bVar) {
        if (sPropertyTypeMap.containsKey(bVar)) {
            return sPropertyTypeMap.get(bVar).intValue();
        }
        return -1;
    }

    public boolean isValid() {
        return ((AnimatedScreenElement) this.mElementRef.get()) != null;
    }
}
