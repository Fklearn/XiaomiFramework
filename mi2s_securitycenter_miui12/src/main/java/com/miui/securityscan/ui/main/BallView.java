package com.miui.securityscan.ui.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.miui.securitycenter.i;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BallView extends View {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Point f7967a;

    /* renamed from: b  reason: collision with root package name */
    private int f7968b;

    /* renamed from: c  reason: collision with root package name */
    private int f7969c;

    /* renamed from: d  reason: collision with root package name */
    private Paint f7970d;
    private int e;
    private boolean f;
    private List<a> g;
    private List<Point> h;
    private Random i;
    /* access modifiers changed from: private */
    public c j;
    private LinearGradient k;
    private LinearGradient l;

    public class a {

        /* renamed from: a  reason: collision with root package name */
        private float f7971a;

        /* renamed from: b  reason: collision with root package name */
        private long f7972b;

        /* renamed from: c  reason: collision with root package name */
        private long f7973c;

        /* renamed from: d  reason: collision with root package name */
        private b f7974d;
        private b e;
        private Point f;
        private int g;
        private int h;
        private int i = 0;
        private boolean j = false;
        private LinearGradient k;
        private LinearGradient l;

        public a() {
            this.g = BallView.this.f7967a.x;
            this.h = BallView.this.f7967a.y;
        }

        public b a() {
            return this.f7974d;
        }

        public void a(float f2) {
            this.f7971a = f2;
        }

        public void a(int i2) {
            this.g = i2;
        }

        public void a(long j2) {
            this.f7973c = j2;
        }

        public void a(Canvas canvas, Paint paint, int i2) {
            LinearGradient linearGradient;
            b bVar;
            if (i2 >= ((int) (((((float) f()) * 1.0f) / 1000.0f) * 60.0f))) {
                this.i++;
                int c2 = (int) (((((float) c()) * 1.0f) / 1000.0f) * 60.0f);
                if (this.i <= c2) {
                    this.j = false;
                    if (BallView.this.j == c.BLUE) {
                        bVar = a();
                        linearGradient = b();
                    } else {
                        bVar = g();
                        linearGradient = h();
                    }
                    paint.setShader(linearGradient);
                    float f2 = (float) c2;
                    paint.setAlpha(bVar.a() - ((int) (((float) bVar.a()) * BallView.this.a((((float) this.i) * 1.0f) / f2))));
                    Point d2 = d();
                    canvas.drawCircle(((float) this.g) + (((float) (d2.x - BallView.this.f7967a.x)) * BallView.this.b((((float) this.i) * 1.0f) / f2)), ((float) this.h) + (((float) (d2.y - BallView.this.f7967a.y)) * BallView.this.b((((float) this.i) * 1.0f) / f2)), e(), paint);
                    return;
                }
                this.j = true;
            }
        }

        public void a(LinearGradient linearGradient) {
            this.k = linearGradient;
        }

        public void a(Point point) {
            this.f = point;
        }

        public void a(b bVar) {
            this.f7974d = bVar;
        }

        public LinearGradient b() {
            return this.k;
        }

        public void b(int i2) {
            this.h = i2;
        }

        public void b(long j2) {
            this.f7972b = j2;
        }

        public void b(LinearGradient linearGradient) {
            this.l = linearGradient;
        }

        public void b(b bVar) {
            this.e = bVar;
        }

        public long c() {
            return this.f7973c;
        }

        public Point d() {
            return this.f;
        }

        public float e() {
            return this.f7971a;
        }

        public long f() {
            return this.f7972b;
        }

        public b g() {
            return this.e;
        }

        public LinearGradient h() {
            return this.l;
        }

        public boolean i() {
            return this.j;
        }
    }

    public static class b {

        /* renamed from: a  reason: collision with root package name */
        private int f7975a;

        /* renamed from: b  reason: collision with root package name */
        private int f7976b;

        /* renamed from: c  reason: collision with root package name */
        private int f7977c;

        /* renamed from: d  reason: collision with root package name */
        private int f7978d;

        public b(int i, int i2, int i3, int i4) {
            this.f7975a = i;
            this.f7976b = i2;
            this.f7977c = i3;
            this.f7978d = i4;
        }

        public int a() {
            return this.f7975a;
        }
    }

    public enum c {
        BLUE,
        YELLOW
    }

    public class d implements Interpolator {
        public d() {
        }

        public float getInterpolation(float f) {
            float f2 = f - 1.0f;
            return (f2 * f2 * f2) + 1.0f;
        }
    }

    public class e implements Interpolator {
        public e() {
        }

        public float getInterpolation(float f) {
            float f2 = f - 1.0f;
            return -((((f2 * f2) * f2) * f2) - 1.0f);
        }
    }

    public BallView(Context context) {
        this(context, (AttributeSet) null);
    }

    public BallView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BallView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f7967a = new Point(0, 0);
        this.g = new ArrayList();
        this.h = new ArrayList();
        this.i = new Random(System.currentTimeMillis());
        this.f7970d = new Paint(1);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, i.BallView);
        this.f7968b = (int) obtainStyledAttributes.getDimension(1, 0.0f);
        this.f7969c = (int) obtainStyledAttributes.getDimension(0, 0.0f);
        obtainStyledAttributes.recycle();
        Point point = this.f7967a;
        point.x = this.f7968b / 2;
        point.y = this.f7969c / 2;
        this.j = c.BLUE;
        d();
        c();
        b();
    }

    /* access modifiers changed from: private */
    public float a(float f2) {
        return new d().getInterpolation(f2);
    }

    /* access modifiers changed from: private */
    public float b(float f2) {
        return new e().getInterpolation(f2);
    }

    private void b() {
        b bVar;
        this.g.clear();
        for (int i2 = 0; i2 < this.h.size(); i2++) {
            a aVar = new a();
            aVar.a((float) (this.i.nextInt(8) + 8));
            aVar.b((long) this.i.nextInt(TsExtractor.TS_STREAM_TYPE_HDMV_DTS));
            aVar.a((long) (this.i.nextInt(300) + 1600));
            if (i2 % 4 == 0) {
                aVar.a(new b(255, 0, 115, 245));
                bVar = new b(255, 254, 117, 67);
            } else {
                aVar.a(new b(this.i.nextInt(56) + 200, 0, 115, 245));
                bVar = new b(this.i.nextInt(56) + 200, 254, 117, 67);
            }
            aVar.b(bVar);
            aVar.a(this.k);
            aVar.b(this.l);
            aVar.a(this.h.get(i2));
            aVar.a(this.f7967a.x);
            aVar.b(this.f7967a.y);
            this.g.add(aVar);
        }
    }

    private void c() {
        Log.d("initEndPointList", "mWidth = " + this.f7968b + "   mHeight = " + this.f7969c);
        this.h.add(new Point((int) (((float) this.f7968b) * 0.331f), (int) (((float) this.f7969c) * 0.0f)));
        this.h.add(new Point((int) (((float) this.f7968b) * 0.711f), (int) (((float) this.f7969c) * 0.042f)));
        this.h.add(new Point((int) (((float) this.f7968b) * 0.224f), (int) (((float) this.f7969c) * 0.045f)));
        this.h.add(new Point((int) (((float) this.f7968b) * 0.92f), (int) (((float) this.f7969c) * 0.156f)));
        this.h.add(new Point((int) (((float) this.f7968b) * 0.031f), (int) (((float) this.f7969c) * 0.233f)));
        this.h.add(new Point((int) (((float) this.f7968b) * 0.011f), (int) (((float) this.f7969c) * 0.481f)));
        this.h.add(new Point((int) (((float) this.f7968b) * 0.83f), (int) (((float) this.f7969c) * 0.495f)));
        this.h.add(new Point((int) (((float) this.f7968b) * 0.12f), (int) (((float) this.f7969c) * 0.706f)));
        this.h.add(new Point((int) (((float) this.f7968b) * 0.944f), (int) (((float) this.f7969c) * 0.806f)));
        this.h.add(new Point((int) (((float) this.f7968b) * 0.725f), (int) (((float) this.f7969c) * 0.86f)));
        this.h.add(new Point((int) (((float) this.f7968b) * 0.061f), (int) (((float) this.f7969c) * 0.94f)));
        this.h.add(new Point((int) (((float) this.f7968b) * 0.331f), (int) (((float) this.f7969c) * 0.966f)));
    }

    private void d() {
        this.k = new LinearGradient(0.0f, 0.0f, 16.0f, 16.0f, new int[]{Color.rgb(101, 242, 249), Color.rgb(111, 243, 251), Color.rgb(27, 145, 239)}, (float[]) null, Shader.TileMode.CLAMP);
        this.l = new LinearGradient(0.0f, 0.0f, 16.0f, 16.0f, new int[]{Color.rgb(255, 180, 52), Color.rgb(255, 123, 43), Color.rgb(255, 83, 43)}, (float[]) null, Shader.TileMode.CLAMP);
    }

    private boolean e() {
        for (a i2 : this.g) {
            if (!i2.i()) {
                return true;
            }
        }
        return false;
    }

    public void a() {
        this.e = 0;
        this.f = true;
        b();
        invalidate();
    }

    public void a(int i2) {
        this.j = i2 >= 80 ? c.BLUE : c.YELLOW;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.f) {
            this.e++;
            for (int i2 = 0; i2 < this.g.size(); i2++) {
                this.g.get(i2).a(canvas, this.f7970d, this.e);
            }
            if (e()) {
                invalidate();
            } else {
                this.f = false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i2, int i3, int i4, int i5) {
        super.onLayout(z, i2, i3, i4, i5);
    }
}
