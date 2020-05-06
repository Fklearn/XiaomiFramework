package com.miui.superpower.statusbar.slider;

import android.view.MotionEvent;
import android.widget.SeekBar;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private SeekBar f8226a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f8227b;

    /* renamed from: c  reason: collision with root package name */
    private float f8228c;

    public a(SeekBar seekBar, boolean z) {
        this.f8226a = seekBar;
        this.f8227b = z;
    }

    private void b(MotionEvent motionEvent) {
        float width;
        float rawX;
        int[] iArr = new int[2];
        this.f8226a.getLocationOnScreen(iArr);
        float progress = ((float) this.f8226a.getProgress()) / ((float) this.f8226a.getMax());
        if (this.f8227b) {
            width = ((float) (iArr[1] + this.f8226a.getPaddingTop())) + ((1.0f - progress) * ((float) ((this.f8226a.getHeight() - this.f8226a.getPaddingTop()) - this.f8226a.getPaddingBottom())));
            rawX = motionEvent.getRawY();
        } else {
            int width2 = (this.f8226a.getWidth() - this.f8226a.getPaddingStart()) - this.f8226a.getPaddingEnd();
            width = this.f8226a.getLayoutDirection() == 1 ? ((float) ((iArr[0] + this.f8226a.getWidth()) - this.f8226a.getPaddingEnd())) - (progress * ((float) width2)) : ((float) (iArr[0] + this.f8226a.getPaddingStart())) + (progress * ((float) width2));
            rawX = motionEvent.getRawX();
        }
        this.f8228c = width - rawX;
    }

    public void a(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            b(motionEvent);
        }
        float f = 0.0f;
        float f2 = this.f8227b ? 0.0f : this.f8228c;
        if (this.f8227b) {
            f = this.f8228c;
        }
        motionEvent.offsetLocation(f2, f);
    }
}
