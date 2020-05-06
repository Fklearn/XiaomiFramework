package com.miui.maml.animation;

import com.miui.maml.elements.ScreenElement;
import com.miui.maml.folme.AnimatedProperty;
import org.w3c.dom.Element;

public class PositionAnimation extends BaseAnimation {
    public static final String INNER_TAG_NAME = "Position";
    public static final String TAG_NAME = "PositionAnimation";

    public PositionAnimation(Element element, ScreenElement screenElement) {
        this(element, "Position", screenElement);
    }

    public PositionAnimation(Element element, String str, ScreenElement screenElement) {
        super(element, str, new String[]{AnimatedProperty.PROPERTY_NAME_X, AnimatedProperty.PROPERTY_NAME_Y}, screenElement);
    }

    public final double getX() {
        return getCurValue(0);
    }

    public final double getY() {
        return getCurValue(1);
    }
}
