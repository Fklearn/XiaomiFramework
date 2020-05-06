package com.miui.common.stickydecoration;

import android.view.MotionEvent;
import android.view.View;

class a implements View.OnTouchListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f3836a;

    a(c cVar) {
        this.f3836a = cVar;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f3836a.k.onTouchEvent(motionEvent);
    }
}
