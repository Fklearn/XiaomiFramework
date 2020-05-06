package com.miui.maml.animation;

import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.folme.AnimatedProperty;
import java.util.Iterator;
import org.w3c.dom.Element;

public class SizeAnimation extends BaseAnimation {
    public static final String INNER_TAG_NAME = "Size";
    public static final String TAG_NAME = "SizeAnimation";
    private double mMaxH;
    private double mMaxW;

    public SizeAnimation(Element element, ScreenElement screenElement) {
        super(element, INNER_TAG_NAME, new String[]{AnimatedProperty.PROPERTY_NAME_W, AnimatedProperty.PROPERTY_NAME_H}, screenElement);
        Iterator<BaseAnimation.AnimationItem> it = this.mItems.iterator();
        while (it.hasNext()) {
            BaseAnimation.AnimationItem next = it.next();
            if (next.get(0) > this.mMaxW) {
                this.mMaxW = next.get(0);
            }
            if (next.get(1) > this.mMaxH) {
                this.mMaxH = next.get(1);
            }
        }
    }

    public final double getHeight() {
        return getCurValue(1);
    }

    public final double getMaxHeight() {
        return this.mMaxH;
    }

    public final double getMaxWidth() {
        return this.mMaxW;
    }

    public final double getWidth() {
        return getCurValue(0);
    }
}
