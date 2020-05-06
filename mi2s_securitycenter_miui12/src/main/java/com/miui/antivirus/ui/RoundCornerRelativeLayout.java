package com.miui.antivirus.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.miui.securitycenter.R;

public class RoundCornerRelativeLayout extends RelativeLayout {

    /* renamed from: a  reason: collision with root package name */
    private static final int[] f2947a = {R.attr.roundCornerRadius};

    /* renamed from: b  reason: collision with root package name */
    private float[] f2948b;

    /* renamed from: c  reason: collision with root package name */
    private Path f2949c;

    /* renamed from: d  reason: collision with root package name */
    private RectF f2950d;

    public RoundCornerRelativeLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public RoundCornerRelativeLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RoundCornerRelativeLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f2949c = new Path();
        this.f2950d = new RectF();
        a(context, attributeSet, i);
    }

    private void a(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, f2947a, i, 0);
        float dimension = obtainStyledAttributes.getDimension(0, 0.0f);
        obtainStyledAttributes.recycle();
        this.f2948b = new float[]{dimension, dimension, dimension, dimension, dimension, dimension, dimension, dimension};
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.f2950d.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
        this.f2949c.reset();
        this.f2949c.addRoundRect(this.f2950d, this.f2948b, Path.Direction.CW);
        canvas.clipPath(this.f2949c);
        super.onDraw(canvas);
    }
}
