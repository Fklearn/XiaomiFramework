package com.miui.maml.animation;

import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.elements.ListScreenElement;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.folme.AnimatedProperty;
import com.xiaomi.stat.MiStat;
import org.w3c.dom.Element;

public class ScaleAnimation extends BaseAnimation {
    public static final String TAG_NAME = "ScaleAnimation";
    private double mDelayValueX;
    private double mDelayValueY;

    public ScaleAnimation(Element element, ScreenElement screenElement) {
        super(element, ListScreenElement.ListItemElement.TAG_NAME, new String[]{MiStat.Param.VALUE, AnimatedProperty.PROPERTY_NAME_X, AnimatedProperty.PROPERTY_NAME_Y}, screenElement);
        BaseAnimation.AnimationItem item = getItem(0);
        this.mDelayValueX = getItemX(item);
        this.mDelayValueY = getItemY(item);
    }

    private double getItemX(BaseAnimation.AnimationItem animationItem) {
        if (animationItem == null) {
            return 1.0d;
        }
        int i = 0;
        if (!animationItem.attrExists(0)) {
            i = 1;
        }
        return animationItem.get(i);
    }

    private double getItemY(BaseAnimation.AnimationItem animationItem) {
        if (animationItem == null) {
            return 1.0d;
        }
        int i = 0;
        if (!animationItem.attrExists(0)) {
            i = 2;
        }
        return animationItem.get(i);
    }

    /* access modifiers changed from: protected */
    public double getDelayValue(int i) {
        return (i == 0 || i == 1) ? this.mDelayValueX : this.mDelayValueY;
    }

    public final double getScaleX() {
        return getCurValue(1);
    }

    public final double getScaleY() {
        return getCurValue(2);
    }

    /* access modifiers changed from: protected */
    public void onTick(BaseAnimation.AnimationItem animationItem, BaseAnimation.AnimationItem animationItem2, float f) {
        if (animationItem != null || animationItem2 != null) {
            double itemX = getItemX(animationItem);
            double d2 = (double) f;
            setCurValue(1, itemX + ((getItemX(animationItem2) - itemX) * d2));
            double itemY = getItemY(animationItem);
            setCurValue(2, itemY + ((getItemY(animationItem2) - itemY) * d2));
        }
    }
}
