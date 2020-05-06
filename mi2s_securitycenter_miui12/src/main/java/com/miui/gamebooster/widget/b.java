package com.miui.gamebooster.widget;

import android.view.MotionEvent;
import android.view.View;

class b implements View.OnTouchListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ HorizontalListView f5390a;

    b(HorizontalListView horizontalListView) {
        this.f5390a = horizontalListView;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f5390a.f5370c.onTouchEvent(motionEvent);
    }
}
