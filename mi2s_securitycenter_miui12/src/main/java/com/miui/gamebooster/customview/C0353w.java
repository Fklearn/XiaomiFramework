package com.miui.gamebooster.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.miui.securitycenter.R;

/* renamed from: com.miui.gamebooster.customview.w  reason: case insensitive filesystem */
public class C0353w extends Drawable {

    /* renamed from: a  reason: collision with root package name */
    private Paint f4232a;

    /* renamed from: b  reason: collision with root package name */
    private Context f4233b;

    /* renamed from: c  reason: collision with root package name */
    private float f4234c;

    /* renamed from: d  reason: collision with root package name */
    private float f4235d;
    private float e;
    private float f;
    private float g;
    private int h = 255;
    private int i;
    private int j;
    private boolean k;

    public C0353w(Context context) {
        this.f4233b = context;
        this.f4232a = new Paint();
        this.f4232a.setStyle(Paint.Style.STROKE);
        this.f4232a.setAntiAlias(true);
        this.f4232a.setStrokeCap(Paint.Cap.ROUND);
        this.f4234c = (float) this.f4233b.getResources().getDimensionPixelSize(R.dimen.gb_vc_record_ring_width);
        this.f = (float) this.f4233b.getResources().getDimensionPixelSize(R.dimen.gb_vc_circle_margin);
        this.f4235d = (float) this.f4233b.getResources().getDimensionPixelSize(R.dimen.gb_vc_record_circle_size);
        this.e = this.f4235d;
        this.g = (float) this.f4233b.getResources().getDimensionPixelSize(R.dimen.gb_vc_record_circle_layout_size);
        this.i = this.f4233b.getResources().getColor(R.color.gb_vc_mode_ring_color_selected);
        this.j = this.f4233b.getResources().getColor(R.color.gb_vc_audition_icon_selected_color);
    }

    public void a(float f2) {
        this.f4235d = this.e * f2;
        invalidateSelf();
    }

    public void a(boolean z) {
        this.k = z;
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        canvas.drawColor(0);
        float f2 = this.f4235d;
        float f3 = this.g;
        float min = ((Math.min(f2, f2) - this.f4234c) / 2.0f) * 2.0f;
        float f4 = (f3 - min) / 2.0f;
        float f5 = (f3 - min) / 2.0f;
        this.f4232a.setStyle(Paint.Style.STROKE);
        this.f4232a.setAlpha(this.h);
        this.f4232a.setColor(this.k ? this.j : this.i);
        this.f4232a.setStrokeWidth(this.f4234c);
        canvas.drawArc(new RectF(f4, f5, f4 + min, min + f5), 0.0f, 360.0f, false, this.f4232a);
        this.f4232a.setStyle(Paint.Style.FILL);
        float f6 = this.g / 2.0f;
        canvas.drawCircle(f6, f6, ((this.f4235d - this.f4234c) - (this.f * 2.0f)) / 2.0f, this.f4232a);
    }

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i2) {
        this.h = i2;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }
}
