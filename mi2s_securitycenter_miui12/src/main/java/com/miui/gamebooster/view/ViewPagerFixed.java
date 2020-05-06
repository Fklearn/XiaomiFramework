package com.miui.gamebooster.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import miui.util.Log;

public class ViewPagerFixed extends ViewPager {
    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        try {
            return ViewPagerFixed.super.onInterceptTouchEvent(motionEvent);
        } catch (IllegalArgumentException e) {
            Log.e("ViewPagerFixed", e.toString());
            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        try {
            return ViewPagerFixed.super.onTouchEvent(motionEvent);
        } catch (IllegalArgumentException e) {
            Log.e("ViewPagerFixed", e.toString());
            return false;
        }
    }
}
