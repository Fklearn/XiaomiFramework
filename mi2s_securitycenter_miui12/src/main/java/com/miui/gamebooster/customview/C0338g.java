package com.miui.gamebooster.customview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.miui.securitycenter.R;

/* renamed from: com.miui.gamebooster.customview.g  reason: case insensitive filesystem */
public class C0338g extends Drawable {

    /* renamed from: a  reason: collision with root package name */
    private Paint f4193a;

    /* renamed from: b  reason: collision with root package name */
    private Path f4194b;

    /* renamed from: c  reason: collision with root package name */
    private Resources f4195c;

    /* renamed from: d  reason: collision with root package name */
    private int f4196d = 0;
    private int e;
    private int f;
    private int g;
    private float h;
    private float i;
    private float j;
    private float k;
    private float l;
    private float m;

    public C0338g(Context context) {
        this.f4195c = context.getResources();
        this.e = this.f4195c.getColor(R.color.gb_vc_mode_ring_color_selected);
        this.h = (float) this.f4195c.getDimensionPixelSize(R.dimen.gb_vc_mode_ring_width);
        this.j = (float) this.f4195c.getDimensionPixelSize(R.dimen.gb_vc_middle_line_height);
        this.k = (float) this.f4195c.getDimensionPixelSize(R.dimen.gb_vc_middle_left_line_height);
        this.l = (float) this.f4195c.getDimensionPixelSize(R.dimen.gb_vc_middle_leftest_line_height);
        this.m = (float) this.f4195c.getDimensionPixelSize(R.dimen.gb_vc_line_interval);
        this.f4193a = new Paint();
        this.f4193a.setStyle(Paint.Style.STROKE);
        this.f4193a.setAntiAlias(true);
        this.f4193a.setStrokeCap(Paint.Cap.ROUND);
        this.f4194b = new Path();
    }

    private void a() {
        this.f4194b.reset();
        float height = (float) getBounds().height();
        float f2 = this.j;
        float f3 = (height - f2) / 2.0f;
        float width = (float) (getBounds().width() / 2);
        this.f4194b.moveTo(width, f3);
        this.f4194b.lineTo(width, f2 + f3);
        float f4 = this.m + this.h + 0.0f;
        float f5 = this.k;
        float f6 = (height - f5) / 2.0f;
        float f7 = f5 + f6;
        float f8 = width - f4;
        this.f4194b.moveTo(f8, f6);
        this.f4194b.lineTo(f8, f7);
        float f9 = width + f4;
        this.f4194b.moveTo(f9, f6);
        this.f4194b.lineTo(f9, f7);
        float f10 = f4 + this.m + this.h;
        float f11 = this.l;
        float f12 = (height - f11) / 2.0f;
        float f13 = f11 + f12;
        float f14 = width - f10;
        this.f4194b.moveTo(f14, f12);
        this.f4194b.lineTo(f14, f13);
        float f15 = width + f10;
        this.f4194b.moveTo(f15, f12);
        this.f4194b.lineTo(f15, f13);
    }

    private void a(Canvas canvas) {
        canvas.drawColor(0);
        Rect bounds = getBounds();
        int i2 = this.f;
        if (i2 != 0) {
            Bitmap decodeResource = BitmapFactory.decodeResource(this.f4195c, i2);
            canvas.drawBitmap(decodeResource, (float) ((bounds.width() - decodeResource.getWidth()) / 2), (float) ((bounds.height() - decodeResource.getHeight()) / 2), this.f4193a);
        }
    }

    private void b(Canvas canvas) {
        canvas.drawColor(this.f4195c.getColor(R.color.gb_circle_progress_canvas_color));
        Rect bounds = getBounds();
        float min = ((((float) Math.min(bounds.width(), bounds.height())) - this.h) / 2.0f) * 2.0f;
        float width = (((float) bounds.width()) - min) / 2.0f;
        float height = (((float) bounds.height()) - min) / 2.0f;
        RectF rectF = new RectF(width, height, width + min, min + height);
        this.f4193a.setColor(this.f4195c.getColor(R.color.gb_vc_playing_bg_color));
        this.f4193a.setStrokeWidth(this.h);
        canvas.drawArc(rectF, 0.0f, 360.0f, false, this.f4193a);
        int i2 = this.g;
        if (i2 != 0) {
            Bitmap decodeResource = BitmapFactory.decodeResource(this.f4195c, i2);
            canvas.drawBitmap(decodeResource, (float) ((bounds.width() - decodeResource.getWidth()) / 2), (float) ((bounds.height() - decodeResource.getHeight()) / 2), this.f4193a);
        }
        this.f4193a.setColor(this.e);
        this.f4193a.setStrokeWidth(this.h);
        canvas.drawArc(rectF, -90.0f, (float) ((int) (this.i * 360.0f)), false, this.f4193a);
        a();
        this.f4193a.setStrokeWidth(this.h);
        this.f4193a.setColor(this.e);
        this.f4193a.setStyle(Paint.Style.STROKE);
        canvas.drawPath(this.f4194b, this.f4193a);
    }

    private void c(Canvas canvas) {
        canvas.drawColor(0);
        Rect bounds = getBounds();
        int i2 = this.g;
        if (i2 != 0) {
            Bitmap decodeResource = BitmapFactory.decodeResource(this.f4195c, i2);
            canvas.drawBitmap(decodeResource, (float) ((bounds.width() - decodeResource.getWidth()) / 2), (float) ((bounds.height() - decodeResource.getHeight()) / 2), this.f4193a);
        }
    }

    public void a(float f2) {
        this.i = f2;
        invalidateSelf();
    }

    public void a(int i2) {
        this.f = i2;
    }

    public void b(int i2) {
        int dimensionPixelSize;
        if (i2 == 0) {
            this.j = (float) this.f4195c.getDimensionPixelSize(R.dimen.gb_vc_middle_line_height);
            this.k = (float) this.f4195c.getDimensionPixelSize(R.dimen.gb_vc_middle_left_line_height);
            dimensionPixelSize = this.f4195c.getDimensionPixelSize(R.dimen.gb_vc_middle_leftest_line_height);
        } else {
            if (i2 == 1) {
                this.j = (float) this.f4195c.getDimensionPixelSize(R.dimen.gb_vc_middle_leftest_line_height);
                this.k = (float) this.f4195c.getDimensionPixelSize(R.dimen.gb_vc_middle_left_line_height);
                dimensionPixelSize = this.f4195c.getDimensionPixelSize(R.dimen.gb_vc_middle_line_height);
            }
            invalidateSelf();
        }
        this.l = (float) dimensionPixelSize;
        invalidateSelf();
    }

    public void c(int i2) {
        this.g = i2;
    }

    public void d(int i2) {
        this.f4196d = i2;
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        int i2 = this.f4196d;
        if (i2 == 0) {
            a(canvas);
        } else if (i2 == 1) {
            c(canvas);
        } else if (i2 == 2) {
            b(canvas);
        }
    }

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i2) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }
}
