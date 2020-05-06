package com.miui.gamebooster.videobox.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.miui.securitycenter.R;

public class VBIndicatorView extends View {

    /* renamed from: a  reason: collision with root package name */
    private int f5231a;

    /* renamed from: b  reason: collision with root package name */
    private int f5232b;

    /* renamed from: c  reason: collision with root package name */
    private Paint f5233c;

    /* renamed from: d  reason: collision with root package name */
    private float f5234d;
    private int e;
    private int f;
    private int g;
    private int h;

    public VBIndicatorView(Context context) {
        this(context, (AttributeSet) null);
    }

    public VBIndicatorView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VBIndicatorView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.e = 2;
        this.f = 0;
        Resources resources = context.getResources();
        this.f5231a = resources.getColor(R.color.color_vb_indicator_fg);
        this.f5232b = resources.getColor(R.color.color_vb_indicator_bg);
        this.f5233c = new Paint(1);
        this.f5233c.setStrokeWidth(2.0f);
        this.f5233c.setStyle(Paint.Style.FILL);
        this.f5233c.setStrokeJoin(Paint.Join.ROUND);
        this.f5233c.setStrokeCap(Paint.Cap.ROUND);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0012, code lost:
        if (r2 < 0.0f) goto L_0x000c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(int r2, float r3) {
        /*
            r1 = this;
            r1.f = r2
            r1.f5234d = r3
            float r2 = r1.f5234d
            r3 = 1065353216(0x3f800000, float:1.0)
            int r0 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x000f
        L_0x000c:
            r1.f5234d = r3
            goto L_0x0015
        L_0x000f:
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 >= 0) goto L_0x0015
            goto L_0x000c
        L_0x0015:
            int r2 = r1.getVisibility()
            if (r2 != 0) goto L_0x001e
            r1.invalidate()
        L_0x001e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.videobox.view.VBIndicatorView.a(int, float):void");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.f5233c.setColor(this.f5232b);
        int i = this.h / 2;
        float f2 = (float) i;
        canvas.drawLine(f2, f2, (float) (this.g - i), f2, this.f5233c);
        this.f5233c.setColor(this.f5231a);
        int i2 = this.g;
        int i3 = i2 / this.e;
        int i4 = i3 - (i * 2);
        int i5 = ((int) (((float) (this.f * i3)) + (((float) i3) * this.f5234d))) + i;
        if (i5 + i4 > i2 - i) {
            i5 = (i2 - i) - i4;
        }
        canvas.drawLine((float) i5, f2, (float) (i5 + i4), f2, this.f5233c);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.g = i;
        this.h = i2;
        this.f5233c.setStrokeWidth((float) i2);
    }

    public void setTotalCount(int i) {
        this.e = i;
        if (getVisibility() == 0) {
            invalidate();
        }
    }
}
