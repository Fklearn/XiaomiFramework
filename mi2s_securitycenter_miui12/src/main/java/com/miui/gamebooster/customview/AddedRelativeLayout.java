package com.miui.gamebooster.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class AddedRelativeLayout extends RelativeLayout {

    /* renamed from: a  reason: collision with root package name */
    private boolean f4102a;

    public AddedRelativeLayout(Context context) {
        super(context);
    }

    public AddedRelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AddedRelativeLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public AddedRelativeLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public boolean a() {
        return this.f4102a;
    }

    public void setAdded(boolean z) {
        this.f4102a = z;
    }
}
