package com.miui.phonemanage.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TabViewPager extends ViewPager {

    /* renamed from: a  reason: collision with root package name */
    private boolean f6623a = true;

    public TabViewPager(Context context) {
        super(context);
    }

    public TabViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.f6623a) {
            return false;
        }
        return TabViewPager.super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.f6623a) {
            return false;
        }
        return TabViewPager.super.onTouchEvent(motionEvent);
    }

    public void setScrollEnable(boolean z) {
        this.f6623a = z;
    }
}
