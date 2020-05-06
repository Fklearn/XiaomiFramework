package com.miui.applicationlock.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.miui.securitycenter.i;

public class PercentLayout extends RelativeLayout {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public int f3423a;

    /* renamed from: b  reason: collision with root package name */
    private Activity f3424b;

    public static class a extends RelativeLayout.LayoutParams {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public float f3425a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public float f3426b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public float f3427c;
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public float f3428d;
        /* access modifiers changed from: private */
        public float e;
        /* access modifiers changed from: private */
        public float f;

        public a(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.PercentLayout);
            this.f3425a = obtainStyledAttributes.getFloat(5, 0.0f);
            this.f3426b = obtainStyledAttributes.getFloat(0, 0.0f);
            this.f3427c = obtainStyledAttributes.getFloat(2, 0.0f);
            this.f3428d = obtainStyledAttributes.getFloat(3, 0.0f);
            this.e = obtainStyledAttributes.getFloat(4, 0.0f);
            this.f = obtainStyledAttributes.getFloat(1, 0.0f);
            obtainStyledAttributes.recycle();
        }

        public void a(float f2) {
            this.e = f2;
        }
    }

    public PercentLayout(Context context) {
        super(context);
        a((Activity) context);
    }

    public PercentLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a((Activity) context);
    }

    public PercentLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a((Activity) context);
    }

    private void a(Activity activity) {
        this.f3424b = activity;
        post(new B(this, activity));
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof a;
    }

    public a generateLayoutParams(AttributeSet attributeSet) {
        return new a(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int i3 = this.f3423a;
        int childCount = getChildCount();
        for (int i4 = 0; i4 < childCount; i4++) {
            ViewGroup.LayoutParams layoutParams = getChildAt(i4).getLayoutParams();
            if (checkLayoutParams(layoutParams)) {
                a aVar = (a) layoutParams;
                float a2 = aVar.f3425a;
                float b2 = aVar.f3426b;
                float c2 = aVar.f3427c;
                float d2 = aVar.f3428d;
                float e = aVar.e;
                float f = aVar.f;
                if (a2 > 0.0f) {
                    layoutParams.width = (int) (((float) size) * a2);
                }
                if (b2 > 0.0f) {
                    layoutParams.height = (int) (((float) i3) * b2);
                }
                if (c2 > 0.0f) {
                    aVar.leftMargin = (int) (((float) size) * c2);
                }
                if (d2 > 0.0f) {
                    aVar.rightMargin = (int) (((float) size) * d2);
                }
                if (e > 0.0f) {
                    aVar.topMargin = (int) (((float) i3) * e);
                }
                if (f > 0.0f) {
                    aVar.bottomMargin = (int) (((float) i3) * f);
                }
            }
        }
        super.onMeasure(i, i2);
    }
}
