package com.miui.gamebooster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import miui.widget.SlidingButton;

public class QRSlidingButton extends SlidingButton {
    public QRSlidingButton(Context context) {
        super(context);
    }

    public QRSlidingButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public QRSlidingButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }
}
