package com.miui.optimizemanage.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.miui.securitycenter.R;

public class OMRoundResizeFrameLayout extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private static final int[] f6012a = {R.attr.roundCornerRadius, R.attr.ratioXY};

    /* renamed from: b  reason: collision with root package name */
    private float f6013b;

    /* renamed from: c  reason: collision with root package name */
    private float[] f6014c;

    /* renamed from: d  reason: collision with root package name */
    private Path f6015d;
    private RectF e;

    public OMRoundResizeFrameLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public OMRoundResizeFrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OMRoundResizeFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f6015d = new Path();
        this.e = new RectF();
        a(context, attributeSet, i);
    }

    private void a(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, f6012a, i, 0);
        float dimension = obtainStyledAttributes.getDimension(0, 0.0f);
        this.f6014c = new float[]{dimension, dimension, dimension, dimension, dimension, dimension, dimension, dimension};
        this.f6013b = obtainStyledAttributes.getFloat(1, -1.0f);
        obtainStyledAttributes.recycle();
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.e.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
        this.f6015d.reset();
        this.f6015d.addRoundRect(this.e, this.f6014c, Path.Direction.CW);
        canvas.clipPath(this.f6015d);
        super.onDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        float f = this.f6013b;
        if (f < 0.0f) {
            super.onMeasure(i, i2);
        } else if (f > 0.0f) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((int) (((float) FrameLayout.getDefaultSize(getSuggestedMinimumWidth(), i)) * this.f6013b), 1073741824));
        }
    }
}
