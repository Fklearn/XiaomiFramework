package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.RectF;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.elements.GeometryScreenElement;
import com.miui.maml.elements.ScreenElement;
import org.w3c.dom.Element;

public class EllipseScreenElement extends GeometryScreenElement {
    public static final String TAG_NAME = "Ellipse";

    public EllipseScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mAlign = ScreenElement.Align.CENTER;
        this.mAlignV = ScreenElement.AlignV.CENTER;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas, GeometryScreenElement.DrawMode drawMode) {
        float width = getWidth();
        float height = getHeight();
        if (width >= 0.0f && height >= 0.0f) {
            if (drawMode == GeometryScreenElement.DrawMode.STROKE_OUTER) {
                float f = this.mWeight;
                width += f;
                height += f;
            } else if (drawMode == GeometryScreenElement.DrawMode.STROKE_INNER) {
                float f2 = this.mWeight;
                width -= f2;
                height -= f2;
                if (width < 0.0f || height < 0.0f) {
                    return;
                }
            }
            float f3 = 0.0f - (width / 2.0f);
            float f4 = 0.0f - (height / 2.0f);
            canvas.drawOval(new RectF(f3, f4, width + f3, height + f4), this.mPaint);
        }
    }
}
