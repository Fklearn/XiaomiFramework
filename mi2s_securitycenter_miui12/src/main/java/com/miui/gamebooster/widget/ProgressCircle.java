package com.miui.gamebooster.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.miui.securitycenter.i;

public class ProgressCircle extends View {

    /* renamed from: a  reason: collision with root package name */
    private static final String f5377a = "ProgressCircle";

    /* renamed from: b  reason: collision with root package name */
    private static final int f5378b = Color.parseColor("#a6000000");

    /* renamed from: c  reason: collision with root package name */
    private static final int f5379c = Color.parseColor("#FF389BFF");

    /* renamed from: d  reason: collision with root package name */
    private Paint f5380d;
    private Paint e;
    private Paint f;
    private float g;
    private RectF h;
    private float i;
    private int j;
    private int k;
    private int l;
    private int m;
    private float n;
    private int o;
    private Bitmap p;
    private boolean q;

    public ProgressCircle(Context context) {
        this(context, (AttributeSet) null);
    }

    public ProgressCircle(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ProgressCircle(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        a(context, attributeSet, i2);
    }

    private void a() {
        this.f5380d = new Paint(1);
        this.f5380d.setColor(this.j);
        this.f5380d.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    private void a(Context context, AttributeSet attributeSet, int i2) {
        b(context, attributeSet, i2);
        c();
        e();
    }

    private void a(Canvas canvas) {
        int i2 = this.o;
        canvas.drawCircle((float) i2, (float) i2, this.i, this.f5380d);
    }

    private void b() {
        this.e = new Paint(1);
        this.e.setColor(this.k);
        this.e.setStyle(Paint.Style.FILL);
    }

    private void b(Context context, AttributeSet attributeSet, int i2) {
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, i.ProgressCircle, i2, 0);
            int indexCount = obtainStyledAttributes.getIndexCount();
            for (int i3 = 0; i3 < indexCount; i3++) {
                int index = obtainStyledAttributes.getIndex(i3);
                if (index == 1) {
                    this.g = (float) obtainStyledAttributes.getDimensionPixelSize(index, 50);
                }
                if (index == 0) {
                    this.j = obtainStyledAttributes.getColor(index, f5378b);
                }
                if (index == 3) {
                    this.k = obtainStyledAttributes.getColor(index, f5379c);
                }
                if (index == 4) {
                    this.l = obtainStyledAttributes.getInt(index, 100);
                }
                if (index == 5) {
                    this.m = obtainStyledAttributes.getInt(index, 0);
                }
                if (index == 6) {
                    this.n = obtainStyledAttributes.getFloat(index, -90.0f);
                }
                if (index == 2) {
                    this.p = BitmapFactory.decodeResource(getResources(), obtainStyledAttributes.getResourceId(index, 0));
                }
            }
            obtainStyledAttributes.recycle();
        }
    }

    private void b(Canvas canvas) {
        Bitmap bitmap = this.p;
        if (bitmap != null) {
            canvas.drawBitmap(this.p, (float) (this.o - (bitmap.getWidth() / 2)), (float) (this.o - (this.p.getHeight() / 2)), this.f);
        }
    }

    private void c() {
        if (this.j == 0) {
            this.j = f5378b;
        }
        this.i = this.g / 2.0f;
        if (this.k == 0) {
            this.k = f5379c;
        }
        if (this.l == 0) {
            this.l = 100;
        }
        if (this.m == 0) {
            this.m = 0;
        }
        if (this.n == 0.0f) {
            this.n = -90.0f;
        }
    }

    private void c(Canvas canvas) {
        if (this.h == null) {
            float f2 = ((float) this.o) - this.i;
            this.h = new RectF(f2, f2, ((float) getWidth()) - f2, ((float) getHeight()) - f2);
        }
        Canvas canvas2 = canvas;
        canvas2.drawArc(this.h, this.n, (((float) this.m) * 360.0f) / ((float) this.l), true, this.e);
    }

    private void d() {
        this.f = new Paint(1);
    }

    private void e() {
        a();
        b();
        d();
    }

    private boolean f() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    private void g() {
        this.o = getWidth() >> 1;
        this.i = Math.min((float) this.o, this.i);
    }

    public void a(int i2, boolean z) {
        this.q = z;
        setProgress(i2);
    }

    public int getMaxProgress() {
        return this.l;
    }

    public int getProgress() {
        return this.m;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        g();
        a(canvas);
        if (!this.q) {
            c(canvas);
        } else if (this.p != null) {
            b(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        int size = View.MeasureSpec.getSize(i2);
        int mode = View.MeasureSpec.getMode(i2);
        int size2 = View.MeasureSpec.getSize(i3);
        int mode2 = View.MeasureSpec.getMode(i3);
        if (mode == Integer.MIN_VALUE) {
            size = (int) (this.g + 0.5f);
        }
        if (mode2 == Integer.MIN_VALUE) {
            size2 = (int) (this.g + 0.5f);
        }
        setMeasuredDimension(size, size2);
    }

    public void setProgress(int i2) {
        if (i2 >= 0 && i2 <= this.l) {
            this.m = i2;
            if (f()) {
                invalidate();
            } else {
                postInvalidate();
            }
        } else {
            Log.w(f5377a, "Max progress can't be less than zero");
        }
    }
}
