package com.miui.permcenter.privacymanager.b;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

class f extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private Paint f6357a;

    /* renamed from: b  reason: collision with root package name */
    private RectF f6358b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f6359c;

    /* renamed from: d  reason: collision with root package name */
    private int[] f6360d = new int[2];

    f(Context context) {
        super(context);
        float f = context.getResources().getDisplayMetrics().density;
        setWillNotDraw(false);
        setOrientation(1);
        setGravity(3);
        this.f6358b = new RectF();
        this.f6357a = new Paint(1);
        this.f6357a.setStrokeCap(Paint.Cap.ROUND);
        int i = (int) (f * 20.0f);
        this.f6359c = new TextView(context);
        this.f6359c.setPadding(i, i, i, i);
        this.f6359c.setGravity(16);
        this.f6359c.setTextColor(-1);
        this.f6359c.setTextSize(1, 18.0f);
        this.f6359c.setMaxLines(2);
        this.f6359c.setMinLines(2);
        this.f6359c.setMarqueeRepeatLimit(1);
        this.f6359c.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.f6359c, new LinearLayout.LayoutParams(-2, -2));
    }

    public void a(int i) {
        this.f6357a.setAlpha(255);
        this.f6357a.setColor(i);
        invalidate();
    }

    public void a(String str) {
        if (str == null) {
            removeView(this.f6359c);
        } else {
            this.f6359c.setText(str);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getLocationOnScreen(this.f6360d);
        this.f6358b.set((float) getPaddingLeft(), (float) getPaddingTop(), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - getPaddingBottom()));
        canvas.drawRoundRect(this.f6358b, 30.0f, 30.0f, this.f6357a);
    }
}
