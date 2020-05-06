package com.miui.earthquakewarning.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ControlledViewPager extends ViewPager {
    private boolean mSlide = false;

    public ControlledViewPager(Context context) {
        super(context);
    }

    public ControlledViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.mSlide) {
            return ControlledViewPager.super.onInterceptTouchEvent(motionEvent);
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mSlide) {
            return ControlledViewPager.super.onTouchEvent(motionEvent);
        }
        return false;
    }

    public void toggleSlide(boolean z) {
        this.mSlide = z;
    }
}
