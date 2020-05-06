package com.miui.phonemanage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;
import b.b.c.j.A;
import d.a.b;

public class PhoneManageListView extends ListView {

    /* renamed from: a  reason: collision with root package name */
    private float f6610a;

    public PhoneManageListView(Context context) {
        super(context);
    }

    public PhoneManageListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PhoneManageListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (A.a()) {
            try {
                b.a((AbsListView) this, motionEvent);
            } catch (Throwable unused) {
                Log.e("PhoneManageListView", "no support folme");
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public float getFirstY() {
        return this.f6610a;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.f6610a = motionEvent.getY();
        }
        return super.onInterceptTouchEvent(motionEvent);
    }
}
