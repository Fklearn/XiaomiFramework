package com.miui.optimizecenter.storage.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.miui.securitycenter.R;

public class DeepCleanChartView extends View {

    /* renamed from: a  reason: collision with root package name */
    private RectF f5787a;

    /* renamed from: b  reason: collision with root package name */
    private RectF f5788b;

    /* renamed from: c  reason: collision with root package name */
    private RectF f5789c;

    /* renamed from: d  reason: collision with root package name */
    private int f5790d;
    private int e;
    private int f;
    float g;
    float h;
    private int i;
    private int j;
    private int k;
    private int l;
    private final int m;
    private Paint n;

    public DeepCleanChartView(Context context) {
        this(context, (AttributeSet) null);
    }

    public DeepCleanChartView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DeepCleanChartView(Context context, @Nullable AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.m = getResources().getDimensionPixelSize(R.dimen.dc_header_percent_bar_radius);
        a(context);
    }

    private void a() {
        int i2 = this.i;
        int i3 = this.m;
        this.k = ((int) (((float) i2) * this.g)) + i3;
        this.l = ((int) (((float) i2) * this.h)) + i3;
        this.f5787a.set(0.0f, 0.0f, (float) Math.min(this.l, i2), (float) this.j);
        this.f5788b.set(0.0f, 0.0f, (float) Math.min(this.k, this.i), (float) this.j);
    }

    private void a(Context context) {
        Resources resources = context.getResources();
        this.f5790d = resources.getColor(R.color.color_dc_storage_used_not_trash);
        this.e = resources.getColor(R.color.color_dc_storage_used);
        this.f = resources.getColor(R.color.color_dc_storage_total);
        this.f5787a = new RectF();
        this.f5788b = new RectF();
        this.f5789c = new RectF();
        this.n = new Paint(1);
        this.n.setStyle(Paint.Style.FILL);
    }

    public void a(long j2, long j3, long j4) {
        double d2 = (double) j2;
        this.g = 1.0f - ((float) ((((double) j3) * 1.0d) / d2));
        this.h = (float) ((((double) ((j2 - j3) - j4)) * 1.0d) / d2);
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        a();
        this.n.setColor(this.f5790d);
        RectF rectF = this.f5789c;
        int i2 = this.m;
        canvas.drawRoundRect(rectF, (float) i2, (float) i2, this.n);
        this.n.setColor(this.e);
        canvas.clipRect(0, 0, this.k - this.m, this.j);
        RectF rectF2 = this.f5788b;
        int i3 = this.m;
        canvas.drawRoundRect(rectF2, (float) i3, (float) i3, this.n);
        this.n.setColor(this.f);
        canvas.clipRect(0, 0, this.l - this.m, this.j);
        RectF rectF3 = this.f5787a;
        int i4 = this.m;
        canvas.drawRoundRect(rectF3, (float) i4, (float) i4, this.n);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        this.i = i2;
        this.j = i3;
        this.f5789c.set(0.0f, 0.0f, (float) this.i, (float) this.j);
    }
}
