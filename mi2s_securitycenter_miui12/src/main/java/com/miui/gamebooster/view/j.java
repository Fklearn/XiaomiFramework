package com.miui.gamebooster.view;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

class j implements View.OnTouchListener {

    /* renamed from: a  reason: collision with root package name */
    float f5293a;

    /* renamed from: b  reason: collision with root package name */
    float f5294b;

    /* renamed from: c  reason: collision with root package name */
    float f5295c;

    /* renamed from: d  reason: collision with root package name */
    float f5296d;
    int e;
    int f;
    final /* synthetic */ k g;

    j(k kVar) {
        this.g = kVar;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 0) {
            boolean z = true;
            if (action == 1) {
                float unused = this.g.f = motionEvent.getRawX();
                float unused2 = this.g.g = motionEvent.getRawY();
                k kVar = this.g;
                if (Math.abs(kVar.f - this.g.f5300d) <= ((float) this.g.i) && Math.abs(this.g.g - this.g.e) <= ((float) this.g.i)) {
                    z = false;
                }
                boolean unused3 = kVar.h = z;
            } else if (action == 2) {
                this.f5295c = motionEvent.getRawX() - this.f5293a;
                this.f5296d = motionEvent.getRawY() - this.f5294b;
                this.e = (int) (((float) this.g.f5299c.b()) + this.f5295c);
                this.f = (int) (((float) this.g.f5299c.c()) + this.f5296d);
                this.g.f5299c.b(this.e, this.f);
            }
            return this.g.h;
        }
        float unused4 = this.g.f5300d = motionEvent.getRawX();
        float unused5 = this.g.e = motionEvent.getRawY();
        this.f5293a = motionEvent.getRawX();
        this.f5294b = motionEvent.getRawY();
        return this.g.h;
    }
}
