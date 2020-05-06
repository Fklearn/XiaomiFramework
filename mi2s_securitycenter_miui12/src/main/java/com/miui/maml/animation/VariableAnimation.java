package com.miui.maml.animation;

import com.miui.maml.elements.ScreenElement;
import org.w3c.dom.Element;

public class VariableAnimation extends BaseAnimation {
    public static final String INNER_TAG_NAME = "AniFrame";
    public static final String TAG_NAME = "VariableAnimation";

    public VariableAnimation(Element element, ScreenElement screenElement) {
        super(element, INNER_TAG_NAME, screenElement);
    }

    public final double getValue() {
        return getCurValue(0);
    }
}
