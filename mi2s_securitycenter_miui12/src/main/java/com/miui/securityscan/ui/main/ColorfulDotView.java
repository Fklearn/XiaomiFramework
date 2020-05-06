package com.miui.securityscan.ui.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ColorfulDotView extends View {

    /* renamed from: a  reason: collision with root package name */
    private Paint f7984a;

    public ColorfulDotView(Context context) {
        super(context);
        a();
    }

    public ColorfulDotView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a();
    }

    private void a() {
        this.f7984a = new Paint();
        this.f7984a.setAntiAlias(true);
        this.f7984a.setStyle(Paint.Style.FILL);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (width * height > 0) {
            canvas.drawCircle((float) (width / 2), (float) (height / 2), (float) (width < height ? width / 2 : height / 2), this.f7984a);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        invalidate();
    }

    public void setColor(int i) {
        this.f7984a.setColor(i);
        invalidate();
    }
}
