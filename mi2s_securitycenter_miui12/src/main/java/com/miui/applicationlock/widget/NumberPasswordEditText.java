package com.miui.applicationlock.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;
import com.miui.securitycenter.i;

public class NumberPasswordEditText extends EditText {

    /* renamed from: a  reason: collision with root package name */
    private int f3416a;

    /* renamed from: b  reason: collision with root package name */
    private int f3417b;

    /* renamed from: c  reason: collision with root package name */
    private Paint f3418c;

    /* renamed from: d  reason: collision with root package name */
    private Paint f3419d;
    private Paint e;
    private Paint f;
    private Paint g;
    private Paint h;
    private int i;
    private int j;
    private int k;
    private int l;
    private int m;
    private int n;
    private int o;
    private int p;
    private int q;
    private boolean r;
    private int s;
    private boolean t;
    private int u;
    private int v;
    private a w;

    public interface a {
        void a(String str);
    }

    public NumberPasswordEditText(Context context) {
        this(context, (AttributeSet) null);
    }

    public NumberPasswordEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f3416a = 40;
        this.f3417b = 4;
        this.j = 1;
        this.k = -1;
        this.l = -1;
        this.m = -1;
        this.n = -1;
        this.o = -1;
        this.p = 64;
        this.q = 0;
        this.r = false;
        this.s = 0;
        this.t = false;
        this.u = 10;
        this.v = -1;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.NumberPasswordEditText);
        if (obtainStyledAttributes != null) {
            this.f3416a = obtainStyledAttributes.getDimensionPixelSize(5, this.f3416a);
            this.f3417b = obtainStyledAttributes.getInt(2, this.f3417b);
            this.j = obtainStyledAttributes.getDimensionPixelSize(9, this.j);
            this.k = obtainStyledAttributes.getColor(8, this.k);
            this.n = obtainStyledAttributes.getColor(3, this.n);
            this.m = obtainStyledAttributes.getColor(4, this.m);
            this.l = obtainStyledAttributes.getColor(11, this.l);
            this.o = obtainStyledAttributes.getColor(12, this.o);
            this.s = obtainStyledAttributes.getDimensionPixelSize(10, this.s);
            this.p = obtainStyledAttributes.getDimensionPixelSize(13, this.p);
            this.t = obtainStyledAttributes.getBoolean(6, this.t);
            this.r = obtainStyledAttributes.getBoolean(7, this.r);
            this.u = obtainStyledAttributes.getDimensionPixelSize(1, this.u);
            this.v = obtainStyledAttributes.getColor(0, this.v);
            obtainStyledAttributes.recycle();
        }
        setBackgroundColor(0);
        setCursorVisible(false);
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(this.f3417b)});
        a();
    }

    private void a() {
        b();
    }

    private void a(Canvas canvas) {
        char[] charArray = getText().toString().toCharArray();
        for (int i2 = 0; i2 < charArray.length; i2++) {
            a(canvas, i2);
            int i3 = this.i;
            int i4 = this.f3416a;
            canvas.drawCircle((float) (i3 + (i2 * i4) + (this.s * i2) + (i4 / 2)), (float) (i4 / 2), (float) this.u, this.h);
        }
    }

    private void a(Canvas canvas, int i2) {
        if (i2 <= this.f3417b - 1) {
            if (this.r) {
                int i3 = this.i;
                int i4 = this.f3416a;
                int i5 = this.s;
                canvas.drawRoundRect(new RectF((float) ((i2 * i4) + i3 + (i2 * i5)), 1.0f, (float) (i3 + (i2 * i4) + (i5 * i2) + i4), (float) i4), 18.0f, 18.0f, this.e);
            }
            int i6 = this.i;
            int i7 = this.f3416a;
            int i8 = this.s;
            int i9 = this.j;
            canvas.drawRoundRect(new RectF((float) ((i2 * i7) + i6 + (i2 * i8) + i9), (float) (i9 + 1), (float) ((((i6 + (i2 * i7)) + (i2 * i8)) + i7) - i9), (float) (i7 - i9)), 18.0f, 18.0f, this.f);
        }
    }

    private void b() {
        int i2;
        Paint paint;
        this.f3418c = new Paint(1);
        this.f3418c.setStrokeWidth((float) this.j);
        this.f3418c.setColor(this.k);
        this.f3418c.setAntiAlias(true);
        this.f3418c.setStyle(Paint.Style.STROKE);
        this.f3419d = new Paint(1);
        this.f3419d.setAntiAlias(true);
        this.f3419d.setStyle(Paint.Style.FILL);
        this.f3419d.setColor(this.l);
        this.e = new Paint(1);
        this.e.setStrokeWidth((float) this.j);
        this.e.setColor(this.n);
        this.e.setAntiAlias(true);
        this.e.setStyle(Paint.Style.STROKE);
        this.f = new Paint(1);
        this.f.setAntiAlias(true);
        this.f.setStyle(Paint.Style.FILL);
        this.f.setColor(this.m);
        if (!this.t) {
            this.g = new Paint(1);
            this.g.setTextAlign(Paint.Align.CENTER);
            this.g.setAntiAlias(true);
            this.g.setTextSize((float) this.p);
            paint = this.g;
            i2 = this.o;
        } else {
            this.h = new Paint(1);
            this.h.setAntiAlias(true);
            this.h.setStrokeWidth(2.0f);
            this.h.setStyle(Paint.Style.FILL);
            paint = this.h;
            i2 = this.v;
        }
        paint.setColor(i2);
    }

    private void b(Canvas canvas) {
        for (int i2 = 0; i2 < this.f3417b; i2++) {
            if (this.r) {
                int i3 = this.i;
                int i4 = this.f3416a;
                int i5 = this.s;
                canvas.drawRoundRect(new RectF((float) ((i2 * i4) + i3 + (i2 * i5)), 1.0f, (float) (i3 + (i2 * i4) + (i5 * i2) + i4), (float) i4), 18.0f, 18.0f, this.f3418c);
            }
            int i6 = this.i;
            int i7 = this.f3416a;
            int i8 = this.s;
            int i9 = this.j;
            canvas.drawRoundRect(new RectF((float) ((i2 * i7) + i6 + (i2 * i8) + i9), (float) (i9 + 1), (float) ((((i6 + (i2 * i7)) + (i8 * i2)) + i7) - i9), (float) (i7 - i9)), 18.0f, 18.0f, this.f3419d);
        }
    }

    private void c(Canvas canvas) {
        char[] charArray = getText().toString().toCharArray();
        for (int i2 = 0; i2 < charArray.length; i2++) {
            a(canvas, i2);
            Paint.FontMetrics fontMetrics = this.g.getFontMetrics();
            int i3 = (int) ((((float) (this.f3416a / 2)) - (fontMetrics.top / 2.0f)) - (fontMetrics.bottom / 2.0f));
            String valueOf = String.valueOf(charArray[i2]);
            int i4 = this.i;
            int i5 = this.f3416a;
            canvas.drawText(valueOf, (float) (i4 + (i2 * i5) + (this.s * i2) + (i5 / 2)), (float) i3, this.g);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        b(canvas);
        a(canvas, this.q);
        if (!this.t) {
            c(canvas);
        } else {
            a(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        int i6 = this.f3417b;
        int i7 = this.f3416a;
        if (i6 * i7 <= i2) {
            this.i = ((i2 - (i7 * i6)) - ((i6 - 1) * this.s)) / 2;
            return;
        }
        throw new IllegalArgumentException("View must be less than the width of the screen!");
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
        a aVar;
        super.onTextChanged(charSequence, i2, i3, i4);
        this.q = i2 + i4;
        if (!TextUtils.isEmpty(charSequence) && charSequence.toString().length() == this.f3417b && (aVar = this.w) != null) {
            aVar.a(charSequence.toString());
        }
        invalidate();
    }

    public void setOnFinishListener(a aVar) {
        this.w = aVar;
    }
}
