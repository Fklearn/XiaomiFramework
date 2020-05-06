package com.miui.maml.animation;

import android.text.TextUtils;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.elements.ScreenElement;
import org.w3c.dom.Element;

public class AlphaAnimation extends BaseAnimation {
    public static final String INNER_TAG_NAME = "Alpha";
    public static final String TAG_NAME = "AlphaAnimation";
    private int mDelayValue;

    public AlphaAnimation(Element element, ScreenElement screenElement) {
        super(element, INNER_TAG_NAME, "a", screenElement);
        String attribute = element.getAttribute("delayValue");
        if (!TextUtils.isEmpty(attribute)) {
            try {
                this.mDelayValue = Integer.parseInt(attribute);
            } catch (NumberFormatException unused) {
            }
        } else {
            BaseAnimation.AnimationItem item = getItem(0);
            if (item != null) {
                this.mDelayValue = (int) item.get(0);
            }
        }
    }

    public final int getAlpha() {
        return (int) getCurValue(0);
    }

    /* access modifiers changed from: protected */
    public double getDefaultValue() {
        return 255.0d;
    }

    /* access modifiers changed from: protected */
    public double getDelayValue(int i) {
        return (double) this.mDelayValue;
    }
}
