package com.miui.gamebooster.g;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class a extends Drawable {

    /* renamed from: a  reason: collision with root package name */
    private Paint f4290a;

    /* renamed from: b  reason: collision with root package name */
    private int f4291b;

    /* renamed from: c  reason: collision with root package name */
    private int f4292c;

    /* renamed from: d  reason: collision with root package name */
    private int f4293d;
    private boolean e;
    private int f;
    private int g;
    private int h;
    private int i;
    private Path j;
    private final int k;
    private boolean l;
    private boolean m = true;
    private int n;
    private boolean o;

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0052  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0076  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public a(android.content.Context r3, int r4, boolean r5) {
        /*
            r2 = this;
            r2.<init>()
            r0 = 1
            r2.m = r0
            r2.l = r5
            r5 = 0
            r1 = 90
            if (r4 != r1) goto L_0x001c
            int r4 = com.miui.gamebooster.m.X.b(r3)
            r2.f4291b = r4
            int r4 = com.miui.gamebooster.m.X.a(r3)
        L_0x0017:
            r2.f4292c = r4
            r2.o = r0
            goto L_0x0031
        L_0x001c:
            r1 = 270(0x10e, float:3.78E-43)
            if (r4 != r1) goto L_0x002b
            int r4 = com.miui.gamebooster.m.X.a(r3)
            r2.f4291b = r4
            int r4 = com.miui.gamebooster.m.X.b(r3)
            goto L_0x0017
        L_0x002b:
            r2.f4291b = r5
            r2.f4292c = r5
            r2.o = r5
        L_0x0031:
            int r4 = r2.f4291b
            if (r4 <= 0) goto L_0x003a
            int r4 = r2.f4292c
            if (r4 <= 0) goto L_0x003a
            r5 = r0
        L_0x003a:
            r2.e = r5
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2131165954(0x7f070302, float:1.794614E38)
            int r4 = r3.getDimensionPixelSize(r4)
            r2.k = r4
            boolean r4 = r2.e
            if (r4 != 0) goto L_0x0056
            boolean r4 = r2.o
            if (r4 != 0) goto L_0x0052
            goto L_0x0056
        L_0x0052:
            r4 = 2131165952(0x7f070300, float:1.7946136E38)
            goto L_0x0059
        L_0x0056:
            r4 = 2131165951(0x7f0702ff, float:1.7946134E38)
        L_0x0059:
            int r4 = r3.getDimensionPixelSize(r4)
            int r5 = r2.k
            int r5 = r5 / 2
            int r5 = r5 + r4
            r2.f = r5
            boolean r4 = r2.e
            if (r4 == 0) goto L_0x0076
            r4 = 2131165948(0x7f0702fc, float:1.7946128E38)
            int r4 = r3.getDimensionPixelSize(r4)
            r2.f4293d = r4
            int r4 = r2.f4291b
        L_0x0073:
            r2.g = r4
            goto L_0x008f
        L_0x0076:
            boolean r4 = r2.o
            if (r4 == 0) goto L_0x007e
            r4 = 2131165946(0x7f0702fa, float:1.7946123E38)
            goto L_0x0081
        L_0x007e:
            r4 = 2131165945(0x7f0702f9, float:1.7946121E38)
        L_0x0081:
            int r4 = r3.getDimensionPixelSize(r4)
            r2.f4293d = r4
            r4 = 2131165953(0x7f070301, float:1.7946138E38)
            int r4 = r3.getDimensionPixelSize(r4)
            goto L_0x0073
        L_0x008f:
            r4 = 2131165949(0x7f0702fd, float:1.794613E38)
            int r4 = r3.getDimensionPixelSize(r4)
            r2.h = r4
            r4 = 2131165950(0x7f0702fe, float:1.7946132E38)
            int r4 = r3.getDimensionPixelSize(r4)
            r2.i = r4
            android.graphics.Path r4 = new android.graphics.Path
            r4.<init>()
            r2.j = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r0)
            r2.f4290a = r4
            r4 = 2131100254(0x7f06025e, float:1.7812884E38)
            int r3 = r3.getColor(r4)
            r2.n = r3
            android.graphics.Paint r3 = r2.f4290a
            int r4 = r2.n
            r3.setColor(r4)
            android.graphics.Paint r3 = r2.f4290a
            android.graphics.Paint$Cap r4 = android.graphics.Paint.Cap.ROUND
            r3.setStrokeCap(r4)
            android.graphics.Paint r3 = r2.f4290a
            android.graphics.Paint$Style r4 = android.graphics.Paint.Style.STROKE
            r3.setStyle(r4)
            android.graphics.Paint r3 = r2.f4290a
            android.graphics.Paint$Join r4 = android.graphics.Paint.Join.ROUND
            r3.setStrokeJoin(r4)
            android.graphics.Paint r3 = r2.f4290a
            int r4 = r2.k
            float r4 = (float) r4
            r3.setStrokeWidth(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.g.a.<init>(android.content.Context, int, boolean):void");
    }

    private void a() {
        Path path;
        this.j.reset();
        float f2 = 0.0f;
        if (!this.e) {
            int i2 = this.g;
            if (this.l) {
                path = this.j;
            } else {
                int intrinsicWidth = getIntrinsicWidth();
                path = this.j;
                f2 = (float) intrinsicWidth;
            }
            path.moveTo(f2, (float) ((this.f4293d + i2) - (this.k / 2)));
            this.j.lineTo(f2, (float) (i2 + (this.k / 2)));
        } else if (this.l) {
            this.j.moveTo(0.0f, (float) (this.f4293d + this.f4291b));
            this.j.lineTo(0.0f, (float) this.f4291b);
            int i3 = this.f4291b;
            this.j.addArc(new RectF(0.0f, 4.0f, (float) (i3 * 2), (float) (i3 * 2)), 180.0f, 30.0f);
        } else {
            int intrinsicWidth2 = getIntrinsicWidth();
            float f3 = (float) intrinsicWidth2;
            this.j.moveTo(f3, (float) (this.f4293d + this.f4292c));
            this.j.lineTo(f3, (float) this.f4292c);
            int i4 = this.f4292c;
            this.j.addArc(new RectF((float) (intrinsicWidth2 - (i4 * 2)), 4.0f, f3, (float) (i4 * 2)), 0.0f, -30.0f);
        }
    }

    public void a(boolean z) {
        Paint paint;
        int i2;
        this.m = z;
        if (z) {
            paint = this.f4290a;
            i2 = this.n;
        } else {
            paint = this.f4290a;
            i2 = 0;
        }
        paint.setColor(i2);
    }

    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.translate((float) (this.l ? this.f : -this.f), 0.0f);
        canvas.drawPath(this.j, this.f4290a);
        canvas.restore();
    }

    public int getIntrinsicHeight() {
        return this.f4293d + this.g + this.h;
    }

    public int getIntrinsicWidth() {
        return (this.e ? (int) (((double) this.f) + ((((double) this.f4291b) * Math.tan(0.5235987755982988d)) / 2.0d)) : this.f * 2) + this.i;
    }

    public int getOpacity() {
        return -3;
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        a();
    }

    public void setAlpha(int i2) {
        this.f4290a.setAlpha(i2);
    }

    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.f4290a.setColorFilter(colorFilter);
    }
}
