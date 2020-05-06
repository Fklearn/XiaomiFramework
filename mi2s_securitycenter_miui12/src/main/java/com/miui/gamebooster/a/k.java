package com.miui.gamebooster.a;

import android.view.MotionEvent;
import android.view.View;

class k implements View.OnTouchListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ v f4049a;

    k(v vVar) {
        this.f4049a = vVar;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        v vVar = this.f4049a;
        return vVar.a(vVar.e, motionEvent);
    }
}
