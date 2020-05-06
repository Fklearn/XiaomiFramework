package com.miui.securityscan.ui.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import com.miui.common.customview.AdImageView;
import com.miui.securitycenter.i;

public class RoundImageView extends AdImageView {

    /* renamed from: d  reason: collision with root package name */
    private float[] f8009d;
    private boolean e;

    public RoundImageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public RoundImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RoundImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, i.RoundImageView);
        float dimension = obtainStyledAttributes.getDimension(2, 0.0f);
        float dimension2 = obtainStyledAttributes.getDimension(4, 0.0f);
        float dimension3 = obtainStyledAttributes.getDimension(3, 0.0f);
        float dimension4 = obtainStyledAttributes.getDimension(1, 0.0f);
        this.e = obtainStyledAttributes.getBoolean(0, false);
        obtainStyledAttributes.recycle();
        this.f8009d = new float[]{dimension, dimension, dimension2, dimension2, dimension3, dimension3, dimension4, dimension4};
    }

    public boolean a() {
        return this.e;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Path path = new Path();
        path.addRoundRect(new RectF(0.0f, 0.0f, (float) getWidth(), (float) getHeight()), this.f8009d, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
