package com.miui.powercenter.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.miui.securitycenter.R;

public class ShadowButton extends LinearLayout implements Checkable {

    /* renamed from: a  reason: collision with root package name */
    private d f7352a;

    /* renamed from: b  reason: collision with root package name */
    private Paint f7353b;

    /* renamed from: c  reason: collision with root package name */
    private ValueAnimator f7354c;

    /* renamed from: d  reason: collision with root package name */
    private int f7355d;
    private int e;

    public ShadowButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public ShadowButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ShadowButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayerType(1, (Paint) null);
        this.f7355d = Color.parseColor("#10000000");
        this.e = Color.parseColor("#190099FF");
        this.f7352a = new d(context);
        this.f7352a.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(this.f7352a, new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.pc_power_history_button_width), getResources().getDimensionPixelSize(R.dimen.pc_power_history_button_height)));
        this.f7353b = new Paint();
        this.f7353b.setColor(-1);
        this.f7353b.setStyle(Paint.Style.FILL);
        this.f7353b.setAntiAlias(true);
    }

    private void a() {
        ValueAnimator valueAnimator = this.f7354c;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.f7354c.cancel();
        }
        this.f7354c = ValueAnimator.ofFloat(new float[]{getScaleX(), 0.9f});
        this.f7354c.setDuration(128);
        this.f7354c.setInterpolator(new DecelerateInterpolator());
        this.f7354c.addUpdateListener(new a(this));
        this.f7354c.start();
    }

    private void b() {
        ValueAnimator valueAnimator = this.f7354c;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.f7354c.cancel();
        }
        this.f7354c = ValueAnimator.ofFloat(new float[]{getScaleX(), 1.0f});
        this.f7354c.setDuration(128);
        this.f7354c.setInterpolator(new AccelerateInterpolator());
        this.f7354c.addUpdateListener(new b(this));
        this.f7354c.start();
    }

    public /* synthetic */ void a(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        setScaleX(floatValue);
        setScaleY(floatValue);
    }

    public /* synthetic */ void b(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        setScaleX(floatValue);
        setScaleY(floatValue);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        d dVar = this.f7352a;
        if (dVar != null) {
            canvas.drawRoundRect(new RectF(dVar.getX(), this.f7352a.getY(), this.f7352a.getX() + ((float) this.f7352a.getWidth()), this.f7352a.getY() + ((float) this.f7352a.getHeight())), this.f7352a.getCorner(), this.f7352a.getCorner(), this.f7353b);
            canvas.save();
            super.dispatchDraw(canvas);
        }
    }

    public boolean isChecked() {
        d dVar = this.f7352a;
        if (dVar != null) {
            return dVar.isChecked();
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            a();
            return true;
        } else if (action != 1) {
            return false;
        } else {
            b();
            performClick();
            return true;
        }
    }

    public boolean performClick() {
        return super.performClick();
    }

    public void setChecked(boolean z) {
        this.f7353b.setShadowLayer(10.0f, 0.0f, 3.0f, z ? this.e : this.f7355d);
        d dVar = this.f7352a;
        if (dVar != null) {
            dVar.setChecked(z);
        }
        invalidate();
    }

    public void setImageResources(int[] iArr) {
        d dVar = this.f7352a;
        if (dVar != null) {
            dVar.setImageResources(iArr);
        }
    }

    public void toggle() {
        d dVar = this.f7352a;
        if (dVar != null) {
            dVar.toggle();
        }
    }
}
