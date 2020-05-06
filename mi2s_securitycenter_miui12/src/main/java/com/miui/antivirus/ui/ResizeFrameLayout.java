package com.miui.antivirus.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.miui.securitycenter.R;

public class ResizeFrameLayout extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private float f2945a;

    /* renamed from: b  reason: collision with root package name */
    private int[] f2946b;

    public ResizeFrameLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public ResizeFrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ResizeFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f2946b = new int[]{R.attr.ratioXY};
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, this.f2946b, i, 0);
        this.f2945a = obtainStyledAttributes.getFloat(0, -1.0f);
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        float f = this.f2945a;
        if (f < 0.0f) {
            super.onMeasure(i, i2);
        } else if (f > 0.0f) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((int) (((float) FrameLayout.getDefaultSize(getSuggestedMinimumWidth(), i)) * this.f2945a), 1073741824));
        }
    }
}
