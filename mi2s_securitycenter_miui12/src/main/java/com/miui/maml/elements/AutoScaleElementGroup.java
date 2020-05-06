package com.miui.maml.elements;

import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;

public class AutoScaleElementGroup extends ElementGroup {
    public static final String TAG_NAME = "AutoScaleGroup";
    private float mInitRawHeight;
    private float mInitRawWidth;

    public AutoScaleElementGroup(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
    }

    public float getScaleX() {
        float widthRaw = getWidthRaw();
        float f = this.mInitRawWidth;
        return (f <= 0.0f || widthRaw <= 0.0f) ? super.getScaleX() : (widthRaw / f) * super.getScaleX();
    }

    public float getScaleY() {
        float heightRaw = getHeightRaw();
        float f = this.mInitRawHeight;
        return (f <= 0.0f || heightRaw <= 0.0f) ? super.getScaleY() : (heightRaw / f) * super.getScaleY();
    }

    public void init() {
        super.init();
        this.mInitRawWidth = getWidthRaw();
        this.mInitRawHeight = getHeightRaw();
    }
}
