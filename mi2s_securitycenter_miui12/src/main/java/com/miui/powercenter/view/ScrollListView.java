package com.miui.powercenter.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class ScrollListView extends ListView {

    /* renamed from: a  reason: collision with root package name */
    private boolean f7350a = true;

    /* renamed from: b  reason: collision with root package name */
    private int f7351b;

    public ScrollListView(Context context) {
        super(context);
    }

    public ScrollListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ScrollListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked() & 255;
        if (actionMasked == 2 && !this.f7350a) {
            return true;
        }
        if (actionMasked == 0) {
            this.f7351b = pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
            return super.dispatchTouchEvent(motionEvent);
        }
        if (actionMasked == 1 || actionMasked == 3) {
            if (pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY()) == this.f7351b) {
                super.dispatchTouchEvent(motionEvent);
            } else {
                setPressed(false);
                invalidate();
                return true;
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void setScrollEnable(boolean z) {
        this.f7350a = z;
    }
}
