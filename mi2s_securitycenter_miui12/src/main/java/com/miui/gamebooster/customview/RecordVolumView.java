package com.miui.gamebooster.customview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import com.miui.securitycenter.R;
import com.miui.securitycenter.i;

public class RecordVolumView extends View {

    /* renamed from: a  reason: collision with root package name */
    private Resources f4148a;

    /* renamed from: b  reason: collision with root package name */
    private Paint f4149b;

    /* renamed from: c  reason: collision with root package name */
    private float f4150c;

    /* renamed from: d  reason: collision with root package name */
    private int f4151d;
    private int e;
    private int f;
    private double g;
    private float h;
    private float i;
    private float j;
    private int k;
    private Path l;
    private Path m;
    private String n;
    private float o;

    public RecordVolumView(Context context) {
        this(context, (AttributeSet) null);
    }

    public RecordVolumView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RecordVolumView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f4148a = context.getResources();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.GBRecordVolumView);
        this.f4150c = obtainStyledAttributes.getDimension(5, 0.0f);
        this.n = obtainStyledAttributes.getString(6);
        this.f4151d = obtainStyledAttributes.getColor(0, -1);
        this.e = obtainStyledAttributes.getColor(1, -1);
        this.f = obtainStyledAttributes.getColor(7, -1);
        this.k = obtainStyledAttributes.getInteger(9, 9);
        this.h = obtainStyledAttributes.getDimension(4, 0.0f);
        this.i = obtainStyledAttributes.getDimension(3, 0.0f);
        this.j = obtainStyledAttributes.getDimension(2, 0.0f);
        this.o = obtainStyledAttributes.getDimension(8, 0.0f);
        this.f4149b = new Paint(1);
        this.f4149b.setStyle(Paint.Style.FILL);
        this.f4149b.setStrokeWidth(this.h);
        this.f4149b.setTextSize(this.o);
        this.l = new Path();
        this.m = new Path();
    }

    public static float a(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float f2 = fontMetrics.descent;
        return ((f2 - fontMetrics.ascent) / 2.0f) - f2;
    }

    private void a(Path path, int i2) {
        if (path != null) {
            path.reset();
            float f2 = this.i;
            float height = (((float) getHeight()) - f2) / 2.0f;
            float f3 = f2 + height;
            float width = (float) (getWidth() / 2);
            float measureText = this.f4149b.measureText(this.n);
            float f4 = this.f4150c;
            float f5 = f4 / 2.0f;
            if (measureText > f4) {
                f5 = (measureText + 10.0f) / 2.0f;
            }
            float f6 = width - f5;
            int i3 = 0;
            for (int i4 = 0; i4 < i2; i4++) {
                float f7 = (float) i3;
                float f8 = f6 - f7;
                path.moveTo(f8, height);
                path.lineTo(f8, f3);
                i3 = (int) (f7 + this.h + this.j);
            }
            float f9 = width + f5;
            int i5 = 0;
            for (int i6 = 0; i6 < i2; i6++) {
                float f10 = (float) i5;
                float f11 = f9 + f10;
                path.moveTo(f11, height);
                path.lineTo(f11, f3);
                i5 = (int) (f10 + this.h + this.j);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.f4149b.setStrokeWidth(this.h);
        this.f4149b.setColor(this.f4151d);
        this.f4149b.setStyle(Paint.Style.STROKE);
        canvas.drawPath(this.l, this.f4149b);
        this.f4149b.setColor(this.e);
        canvas.drawPath(this.m, this.f4149b);
        this.f4149b.setStrokeWidth(0.0f);
        this.f4149b.setStyle(Paint.Style.FILL);
        this.f4149b.setColor(this.f);
        canvas.drawText(this.n, (float) ((int) (((float) (getWidth() / 2)) - (this.f4149b.measureText(this.n) / 2.0f))), a(this.f4149b) + ((float) (getHeight() / 2)), this.f4149b);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        a(this.l, this.k);
        a(this.m, (int) (((double) this.k) * this.g));
    }

    public void setTime(int i2) {
        this.n = this.f4148a.getQuantityString(R.plurals.gb_record_time_title, i2, new Object[]{Integer.valueOf(i2)});
        a(this.l, this.k);
        a(this.m, (int) (((double) this.k) * this.g));
        invalidate();
    }

    public void setVoice(double d2) {
        this.g = d2;
        a(this.l, this.k);
        a(this.m, (int) (((double) this.k) * this.g));
        invalidate();
    }
}
