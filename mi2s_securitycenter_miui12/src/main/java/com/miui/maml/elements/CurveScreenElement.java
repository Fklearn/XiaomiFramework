package com.miui.maml.elements;

import android.graphics.Canvas;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.elements.GeometryScreenElement;
import org.w3c.dom.Element;

public class CurveScreenElement extends GeometryScreenElement {
    public static final String TAG_NAME = "Curve";

    public CurveScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas, GeometryScreenElement.DrawMode drawMode) {
    }
}
