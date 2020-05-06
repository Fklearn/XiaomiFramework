package com.miui.maml.animation;

import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.elements.ScreenElement;
import org.w3c.dom.Element;

public class SourcesAnimation extends PositionAnimation {
    public static final String TAG_NAME = "SourcesAnimation";
    private String mCurrentSrc;

    public static class Source extends BaseAnimation.AnimationItem {
        public static final String TAG_NAME = "Source";
        public String mSrc;

        public Source(BaseAnimation baseAnimation, Element element) {
            super(baseAnimation, element);
            this.mSrc = element.getAttribute("src");
        }
    }

    public SourcesAnimation(Element element, ScreenElement screenElement) {
        super(element, Source.TAG_NAME, screenElement);
    }

    public final String getSrc() {
        return this.mCurrentSrc;
    }

    /* access modifiers changed from: protected */
    public BaseAnimation.AnimationItem onCreateItem(BaseAnimation baseAnimation, Element element) {
        return new Source(baseAnimation, element);
    }

    /* access modifiers changed from: protected */
    public void onTick(BaseAnimation.AnimationItem animationItem, BaseAnimation.AnimationItem animationItem2, float f) {
        if (animationItem2 == null) {
            setCurValue(0, 0.0d);
            setCurValue(1, 0.0d);
            return;
        }
        setCurValue(0, animationItem2.get(0));
        setCurValue(1, animationItem2.get(1));
        this.mCurrentSrc = ((Source) animationItem2).mSrc;
    }
}
