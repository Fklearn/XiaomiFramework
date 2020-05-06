package com.miui.gamebooster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

public class SeekBarLinearLayout extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private float f5259a;

    /* renamed from: b  reason: collision with root package name */
    private float f5260b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f5261c;

    /* renamed from: d  reason: collision with root package name */
    private int f5262d;
    private float e;
    private a f;

    public interface a {
        void a(SeekBarLinearLayout seekBarLinearLayout, float f);
    }

    public SeekBarLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f5262d = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private boolean a(MotionEvent motionEvent) {
        if (Math.abs(this.f5260b - this.f5259a) >= ((float) this.f5262d)) {
            return false;
        }
        int width = getWidth();
        int x = (int) motionEvent.getX();
        this.e = x < 0 ? 0.0f : x > width ? 1.0f : ((float) x) / ((float) width);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void a() {
        this.f5261c = true;
    }

    /* access modifiers changed from: package-private */
    public void b() {
        this.f5261c = false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        a aVar;
        int action = motionEvent.getAction();
        if (action == 0) {
            this.f5259a = motionEvent.getX();
            a();
        } else if (action == 1 && this.f5261c) {
            this.f5260b = motionEvent.getX();
            b();
            if (a(motionEvent) && (aVar = this.f) != null) {
                aVar.a(this, this.e);
            }
        }
        return true;
    }

    public void setOnLinearLayoutClickListener(a aVar) {
        this.f = aVar;
    }
}
