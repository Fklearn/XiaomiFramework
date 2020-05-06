package com.miui.applicationlock.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class LinearLayoutWithDefaultTouchRecepient extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private final Rect f3396a = new Rect();

    /* renamed from: b  reason: collision with root package name */
    private View f3397b;

    public LinearLayoutWithDefaultTouchRecepient(Context context) {
        super(context);
    }

    public LinearLayoutWithDefaultTouchRecepient(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.f3397b == null) {
            return super.dispatchTouchEvent(motionEvent);
        }
        if (super.dispatchTouchEvent(motionEvent)) {
            return true;
        }
        this.f3396a.set(0, 0, 0, 0);
        offsetRectIntoDescendantCoords(this.f3397b, this.f3396a);
        motionEvent.setLocation(motionEvent.getX() + ((float) this.f3396a.left), motionEvent.getY() + ((float) this.f3396a.top));
        return this.f3397b.dispatchTouchEvent(motionEvent);
    }

    public void setDefaultTouchRecepient(View view) {
        this.f3397b = view;
    }
}
