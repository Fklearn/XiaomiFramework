package com.miui.securityscan.ui.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;

public class ColorfulRingView extends View {

    /* renamed from: a  reason: collision with root package name */
    private List<a> f7985a;

    /* renamed from: b  reason: collision with root package name */
    private int f7986b;

    /* renamed from: c  reason: collision with root package name */
    private RectF f7987c;

    /* renamed from: d  reason: collision with root package name */
    private Paint f7988d;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public int f7989a = 0;

        /* renamed from: b  reason: collision with root package name */
        public float f7990b = 0.0f;
    }

    public ColorfulRingView(Context context) {
        super(context);
        a();
    }

    public ColorfulRingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a();
    }

    private void a() {
        this.f7987c = new RectF();
        this.f7988d = new Paint();
        this.f7988d.setAntiAlias(true);
        this.f7988d.setStyle(Paint.Style.STROKE);
        this.f7988d.setStrokeWidth(16.0f);
    }

    private boolean a(List<a> list) {
        return list != null;
    }

    public void a(List<a> list, int i) {
        if (a(list)) {
            this.f7985a = list;
            this.f7986b = i;
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() * getHeight() > 0) {
            this.f7987c.set(16.0f, 16.0f, (float) (getWidth() - 16), (float) (getHeight() - 16));
            if (a(this.f7985a)) {
                float f = -90.0f;
                for (a next : this.f7985a) {
                    this.f7988d.setColor(next.f7989a);
                    float f2 = next.f7990b * 360.0f;
                    if (f + f2 > 270.0f) {
                        f2 = 270.0f - f;
                    }
                    float f3 = f2;
                    canvas.drawArc(this.f7987c, f, f3, false, this.f7988d);
                    f += f3;
                    if (f > 270.0f) {
                        break;
                    }
                }
                float f4 = f;
                if (f4 <= 270.0f) {
                    this.f7988d.setColor(this.f7986b);
                    canvas.drawArc(this.f7987c, f4, 270.0f - f4, false, this.f7988d);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        invalidate();
    }
}
