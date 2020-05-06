package com.miui.antivirus.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class AnimationView extends View {

    /* renamed from: a  reason: collision with root package name */
    private Bitmap f2908a;

    /* renamed from: b  reason: collision with root package name */
    private Bitmap f2909b;

    /* renamed from: c  reason: collision with root package name */
    private Paint f2910c;

    /* renamed from: d  reason: collision with root package name */
    private float f2911d;
    private int e;
    private a f;

    private enum a {
        TO_TOP,
        TO_BOTTOM
    }

    private void a(Canvas canvas) {
        canvas.save();
        this.f2910c.setAlpha(this.e);
        Bitmap bitmap = this.f2909b;
        canvas.drawBitmap(bitmap, 0.0f, this.f2911d - ((float) bitmap.getHeight()), this.f2910c);
        canvas.restore();
    }

    private void b(Canvas canvas) {
        canvas.save();
        this.f2910c.setAlpha(this.e);
        canvas.drawBitmap(this.f2908a, 0.0f, this.f2911d, this.f2910c);
        canvas.restore();
    }

    public int getHighLightAlpha() {
        return this.e;
    }

    public float getHighLightViewTop() {
        return this.f2911d;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i = a.f2952a[this.f.ordinal()];
        if (i == 1) {
            b(canvas);
        } else if (i != 2) {
            super.onDraw(canvas);
        } else {
            a(canvas);
        }
    }

    public void setHighLightAlpha(int i) {
        this.e = i;
        invalidate();
    }

    public void setHighLightViewTop(float f2) {
        this.f2911d = f2;
        invalidate();
    }
}
