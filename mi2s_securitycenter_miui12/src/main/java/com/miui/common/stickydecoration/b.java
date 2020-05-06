package com.miui.common.stickydecoration;

import android.view.GestureDetector;
import android.view.MotionEvent;

class b implements GestureDetector.OnGestureListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f3840a;

    b(c cVar) {
        this.f3840a = cVar;
    }

    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    public void onLongPress(MotionEvent motionEvent) {
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return this.f3840a.c(motionEvent);
    }
}
