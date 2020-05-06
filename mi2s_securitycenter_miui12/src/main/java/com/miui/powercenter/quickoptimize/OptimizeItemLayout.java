package com.miui.powercenter.quickoptimize;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class OptimizeItemLayout extends ViewGroup {

    /* renamed from: a  reason: collision with root package name */
    private int f7207a;

    public OptimizeItemLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public OptimizeItemLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f7207a = 0;
    }

    private int getFirstChildPos() {
        if (a()) {
            return getChildCount() - 1;
        }
        return 0;
    }

    private int getLastChildPos() {
        if (a()) {
            return 0;
        }
        return getChildCount() - 1;
    }

    public boolean a() {
        return getLayoutDirection() == 1;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        boolean a2 = a();
        int childCount = getChildCount();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int measuredWidth = getMeasuredWidth() - getPaddingRight();
        int i7 = measuredWidth - paddingLeft;
        int measuredHeight = (getMeasuredHeight() - getPaddingBottom()) - paddingTop;
        if (a2) {
            i5 = childCount - 1;
            i6 = -1;
        } else {
            i6 = 1;
            i5 = 0;
        }
        int i8 = 0;
        for (int i9 = 0; i9 < childCount; i9++) {
            int i10 = (i6 * i9) + i5;
            View childAt = getChildAt(i10);
            if (childAt.getVisibility() != 8) {
                if (i10 == 0) {
                    childAt.measure(View.MeasureSpec.makeMeasureSpec(i7 - this.f7207a, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(measuredHeight, Integer.MIN_VALUE));
                }
                int measuredWidth2 = childAt.getMeasuredWidth();
                int measuredHeight2 = childAt.getMeasuredHeight();
                int i11 = (measuredHeight - measuredHeight2) / 2;
                if (i10 == getLastChildPos()) {
                    childAt.layout(Math.max(measuredWidth - measuredWidth2, i8), i11, measuredWidth, measuredHeight2 + i11);
                } else {
                    int i12 = i8 + measuredWidth2;
                    int i13 = i12 > measuredWidth ? measuredWidth : i12;
                    childAt.layout(i8, i11, i13, measuredHeight2 + i11);
                    if (i13 != measuredWidth) {
                        if (a()) {
                            int i14 = 0;
                            for (int i15 = (childCount - i9) - 2; i15 >= 0; i15--) {
                                i14 += getChildAt(i15).getMeasuredWidth();
                            }
                            i8 = measuredWidth - i14;
                        } else {
                            i8 = i12;
                        }
                    } else {
                        return;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int childCount = getChildCount();
        int i3 = 0;
        this.f7207a = 0;
        for (int i4 = childCount - 1; i4 >= 0; i4--) {
            View childAt = getChildAt(i4);
            if (childAt.getVisibility() != 8) {
                if (i4 == 0) {
                    measureChild(childAt, View.MeasureSpec.makeMeasureSpec(size - this.f7207a, Integer.MIN_VALUE), i2);
                } else {
                    measureChild(childAt, View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), i2);
                    this.f7207a += childAt.getMeasuredWidth();
                }
                int measuredHeight = childAt.getMeasuredHeight();
                if (measuredHeight > i3) {
                    i3 = measuredHeight;
                }
            }
        }
        setMeasuredDimension(size, i3);
    }
}
