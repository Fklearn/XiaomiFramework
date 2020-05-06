package com.miui.securityscan;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

class H implements View.OnTouchListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7553a;

    H(L l) {
        this.f7553a = l;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action;
        float f;
        L l;
        Activity activity = this.f7553a.getActivity();
        if (this.f7553a.a(activity) && (action = motionEvent.getAction()) != 0) {
            if (action == 1) {
                float unused = this.f7553a.Ta = 0.0f;
                float unused2 = this.f7553a.Ua = 0.0f;
            } else if (action == 2) {
                if (this.f7553a.Ua == 0.0f) {
                    l = this.f7553a;
                    f = l.y.getFirstY();
                } else {
                    l = this.f7553a;
                    f = l.Ua;
                }
                float unused3 = l.Ta = f;
                float unused4 = this.f7553a.Ua = motionEvent.getY();
                MainActivity mainActivity = (MainActivity) activity;
                boolean m = mainActivity.m();
                if (this.f7553a.Ua - this.f7553a.Ta > 5.0f && !m) {
                    mainActivity.a(true, true);
                } else if (this.f7553a.Ta - this.f7553a.Ua > 5.0f && m) {
                    mainActivity.a(false, true);
                }
            }
        }
        return false;
    }
}
