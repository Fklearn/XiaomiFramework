package com.miui.powercenter.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class EmptyView extends View {

    /* renamed from: a  reason: collision with root package name */
    private Activity f7328a;

    public EmptyView(Context context) {
        this(context, (AttributeSet) null);
    }

    public EmptyView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public EmptyView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        if (context instanceof Activity) {
            this.f7328a = (Activity) context;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int measuredWidth;
        int i3;
        super.onMeasure(i, i2);
        if (this.f7328a != null) {
            Point point = new Point();
            this.f7328a.getWindowManager().getDefaultDisplay().getRealSize(point);
            if (point.y >= 2160) {
                measuredWidth = getMeasuredWidth();
                i3 = 86;
                setMeasuredDimension(measuredWidth, i3);
            }
        }
        measuredWidth = getMeasuredWidth();
        i3 = 0;
        setMeasuredDimension(measuredWidth, i3);
    }
}
