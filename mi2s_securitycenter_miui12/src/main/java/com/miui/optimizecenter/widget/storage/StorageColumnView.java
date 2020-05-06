package com.miui.optimizecenter.widget.storage;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import b.b.i.b.g;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.miui.securitycenter.R;
import java.util.HashMap;

public class StorageColumnView extends View {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public int f5821a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public int f5822b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f5823c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public int f5824d;
    /* access modifiers changed from: private */
    public int e;
    private int f;
    private int g;
    private int h;
    private boolean i;
    private long j;
    /* access modifiers changed from: private */
    public a k;
    /* access modifiers changed from: private */
    public a l;
    /* access modifiers changed from: private */
    public PorterDuffXfermode m;
    private c n;
    private d o;
    private c p;
    private c q;
    private boolean r;
    private Point s;
    private HashMap<b, c> t;
    private b u;
    private boolean v;
    public boolean w;
    private boolean x;

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        private Bitmap f5825a;

        /* renamed from: b  reason: collision with root package name */
        private long f5826b;

        /* renamed from: c  reason: collision with root package name */
        private long f5827c = 250;

        /* renamed from: d  reason: collision with root package name */
        private int f5828d;
        private int e;
        private int f;
        private DecelerateInterpolator g = new DecelerateInterpolator(2.0f);

        public a(Bitmap bitmap) {
            this.f5825a = bitmap;
            this.f = 255;
            this.f5828d = 255;
            this.e = 255;
        }

        public int a() {
            return this.f;
        }

        public void a(boolean z) {
            if (z) {
                this.f5828d = 0;
                this.e = 255;
            } else {
                this.f5828d = 255;
                this.e = 0;
            }
            this.f5826b = 0;
        }

        public boolean a(long j) {
            long j2 = this.f5826b;
            long j3 = this.f5827c;
            if (j2 > j3) {
                return false;
            }
            this.f5826b = j2 + j;
            long j4 = this.f5826b;
            if (j4 < 0) {
                return false;
            }
            float interpolation = this.g.getInterpolation(Math.min(1.0f, ((float) j4) / ((float) j3)));
            int i = this.f5828d;
            this.f = (int) (((float) i) + (interpolation * ((float) (this.e - i))));
            return true;
        }

        public Bitmap b() {
            return this.f5825a;
        }

        public boolean c() {
            return this.f5828d != this.e && this.f5826b <= this.f5827c;
        }
    }

    public interface b {
        void a(MotionEvent motionEvent, b bVar, int i, int i2);

        void a(b bVar, int i, int i2);
    }

    class c {
        private int A;

        /* renamed from: a  reason: collision with root package name */
        private BitmapDrawable f5829a;

        /* renamed from: b  reason: collision with root package name */
        private BitmapDrawable f5830b;

        /* renamed from: c  reason: collision with root package name */
        private BitmapDrawable f5831c;

        /* renamed from: d  reason: collision with root package name */
        private Rect f5832d;
        private Rect e;
        private Rect f;
        /* access modifiers changed from: private */
        public b g;
        private long h;
        private long i;
        private DecelerateInterpolator j = new DecelerateInterpolator(2.0f);
        private int k;
        private boolean l;
        private int m;
        private int n;
        private int o;
        private int p;
        private long q;
        private long r;
        private long s;
        private DecelerateInterpolator t = new DecelerateInterpolator(2.0f);
        private boolean u;
        private DecelerateInterpolator v = new DecelerateInterpolator(3.0f);
        private int w;
        private int x;
        private int y;
        private int z;

        public c(b bVar, Resources resources) {
            this.g = bVar;
            this.f5829a = new BitmapDrawable(resources, BitmapFactory.decodeResource(resources, bVar.l));
            this.f5830b = new BitmapDrawable(resources, BitmapFactory.decodeResource(resources, bVar.m));
            this.f5831c = new BitmapDrawable(resources, BitmapFactory.decodeResource(resources, bVar.n));
            this.k = StorageColumnView.this.f5823c;
            this.n = StorageColumnView.this.f5823c;
            this.o = StorageColumnView.this.f5821a;
            this.p = StorageColumnView.this.f5821a;
            this.h = 700;
            this.f5832d = new Rect(0, 0, StorageColumnView.this.e, StorageColumnView.this.f5821a);
            Rect rect = this.f5832d;
            int i2 = rect.left;
            int b2 = rect.bottom - (StorageColumnView.this.f5821a / 2);
            Rect rect2 = this.f5832d;
            this.e = new Rect(i2, b2, rect2.right, (rect2.bottom - (StorageColumnView.this.f5821a / 2)) + this.n);
            Rect rect3 = this.e;
            int i3 = rect3.left;
            int i4 = rect3.bottom;
            this.f = new Rect(i3, i4, rect3.right, StorageColumnView.this.f5821a + i4);
            this.y = 0;
            this.z = 255;
            this.A = 50;
        }

        private void a(Canvas canvas, BitmapDrawable bitmapDrawable, Rect rect, a aVar) {
            if (this.e.height() != 0) {
                Rect rect2 = new Rect(0, 0, 1, 1);
                Paint paint = bitmapDrawable.getPaint();
                int saveLayer = canvas.saveLayer((float) rect.left, (float) rect.top, (float) rect.right, (float) rect.bottom, paint);
                bitmapDrawable.draw(canvas);
                paint.setXfermode(StorageColumnView.this.m);
                paint.setAlpha(aVar.a());
                canvas.drawBitmap(aVar.b(), rect2, rect, paint);
                paint.setAlpha(255);
                paint.setXfermode((Xfermode) null);
                canvas.restoreToCount(saveLayer);
            }
        }

        private void b(int i2) {
            this.f5829a.setAlpha(i2);
            this.f5830b.setAlpha(i2);
            this.f5831c.setAlpha(i2);
        }

        private void i() {
            this.f5829a.setBounds(this.f5832d);
            this.f5830b.setBounds(this.e);
            this.f5831c.setBounds(this.f);
        }

        public int a() {
            return this.f.bottom;
        }

        public c a(int i2) {
            if (i2 == this.k) {
                return this;
            }
            this.h = 700;
            this.i = 0;
            this.m = this.n;
            this.k = i2;
            this.l = this.m < this.k;
            return this;
        }

        public c a(int i2, int i3) {
            this.w = i3;
            this.x = i2;
            return this;
        }

        public c a(int i2, int i3, int i4) {
            this.z = i3;
            this.y = i2;
            this.A = i4;
            return this;
        }

        public c a(long j2) {
            this.q = j2;
            return this;
        }

        public void a(int i2, int i3, int i4, int i5) {
            this.k = i5;
            Rect rect = this.f;
            rect.bottom = i2;
            rect.top = i2 - i4;
            Rect rect2 = this.e;
            rect2.bottom = rect.top;
            rect2.top = rect2.bottom - i5;
            Rect rect3 = this.f5832d;
            rect3.bottom = rect2.top + (i3 / 2);
            rect3.top = rect3.bottom - i3;
            i();
        }

        public void a(int i2, int i3, long j2) {
            this.i += j2;
            long j3 = this.i;
            if (j3 >= 0) {
                float interpolation = this.j.getInterpolation(Math.min(1.0f, ((float) j3) / ((float) this.h)));
                int i4 = this.m;
                int i5 = this.k;
                this.n = (int) (((float) i4) + (((float) (i5 - i4)) * interpolation));
                this.n = this.l ? Math.min(this.n, i5) : Math.max(this.n, i5);
                StorageColumnView storageColumnView = StorageColumnView.this;
                if (!storageColumnView.w && !this.l) {
                    this.n = Math.max(this.n, storageColumnView.f5823c);
                }
                int max = Math.max(i3, StorageColumnView.this.f5822b);
                if (i3 > StorageColumnView.this.f5821a) {
                    max = StorageColumnView.this.f5821a;
                }
                this.p = max / 2;
                this.o = Math.max(Math.min(StorageColumnView.this.f5822b + ((int) ((((float) (StorageColumnView.this.f5821a - StorageColumnView.this.f5822b)) * ((float) (this.n + i2))) / ((float) StorageColumnView.this.f5824d))), StorageColumnView.this.f5821a), StorageColumnView.this.f5822b);
                Rect rect = this.f;
                rect.bottom = i2;
                rect.top = i2 - this.p;
                Rect rect2 = this.e;
                rect2.bottom = rect.top;
                rect2.top = rect2.bottom - this.n;
                Rect rect3 = this.f5832d;
                int i6 = rect2.top;
                int i7 = this.o;
                rect3.bottom = i6 + (i7 / 2);
                rect3.top = rect3.bottom - i7;
                i();
            }
        }

        public void a(long j2, boolean z2) {
            this.r += j2;
            long j3 = this.r - this.s;
            if (j3 >= 0) {
                float f2 = (float) j3;
                float min = Math.min(1.0f, f2 / ((float) this.A));
                if (z2) {
                    min = this.v.getInterpolation(min);
                }
                int i2 = this.y;
                b((int) (((float) i2) + (((float) (this.z - i2)) * min)));
                float interpolation = (z2 ? this.v : this.t).getInterpolation(Math.min(1.0f, f2 / ((float) this.q)));
                int i3 = this.x;
                int i4 = (int) (((float) i3) + (((float) (this.w - i3)) * interpolation));
                int b2 = StorageColumnView.this.f5821a;
                Rect rect = this.f;
                rect.bottom = i4;
                rect.top = i4 - (StorageColumnView.this.f5821a / 2);
                Rect rect2 = this.e;
                rect2.bottom = rect.top;
                rect2.top = rect2.bottom - (z2 ? StorageColumnView.this.f5824d : StorageColumnView.this.f5823c);
                Rect rect3 = this.f5832d;
                rect3.bottom = this.e.top + (b2 / 2);
                rect3.top = rect3.bottom - b2;
                i();
                this.u = this.r - this.s >= this.q;
            }
        }

        public void a(Canvas canvas) {
            this.f5831c.draw(canvas);
            this.f5830b.draw(canvas);
            this.f5829a.draw(canvas);
        }

        public void a(Canvas canvas, boolean z2, boolean z3, boolean z4) {
            BitmapDrawable bitmapDrawable;
            Rect rect;
            a h2;
            if (this.e.height() != 0) {
                if (!z3) {
                    if (z4) {
                        a(canvas, this.f5831c, this.f, StorageColumnView.this.l);
                        a(canvas, this.f5830b, this.e, StorageColumnView.this.l);
                        bitmapDrawable = this.f5829a;
                        rect = this.f5832d;
                        h2 = StorageColumnView.this.l;
                    } else if (z2) {
                        a(canvas, this.f5831c, this.f, StorageColumnView.this.k);
                        a(canvas, this.f5830b, this.e, StorageColumnView.this.k);
                        bitmapDrawable = this.f5829a;
                        rect = this.f5832d;
                        h2 = StorageColumnView.this.k;
                    }
                    a(canvas, bitmapDrawable, rect, h2);
                    return;
                }
                a(canvas);
            }
        }

        public boolean a(int i2, int i3, boolean z2) {
            if (this.e.height() == 0) {
                return false;
            }
            Rect rect = this.f;
            return (i2 <= rect.right && i2 >= rect.left) && (i3 >= (z2 ? this.f5832d.top : this.f5832d.bottom) && i3 <= this.f.bottom);
        }

        public int b() {
            return this.o;
        }

        public c b(long j2) {
            this.r = j2;
            return this;
        }

        public Point c() {
            if (StorageColumnView.this.a()) {
                Rect rect = this.e;
                return new Point(rect.left, rect.centerY());
            }
            Rect rect2 = this.e;
            return new Point(rect2.right, rect2.centerY());
        }

        public c c(long j2) {
            this.s = j2;
            return this;
        }

        public int d() {
            return this.f5832d.bottom;
        }

        public boolean e() {
            return this.u;
        }

        public boolean f() {
            return this.n == this.k;
        }

        public boolean g() {
            return this.e.height() > 0;
        }

        public void h() {
            b(this.y);
            int b2 = StorageColumnView.this.f5821a;
            Rect rect = this.f;
            int i2 = this.x;
            rect.bottom = i2;
            rect.top = i2 - (StorageColumnView.this.f5821a / 2);
            Rect rect2 = this.e;
            rect2.bottom = rect.top;
            rect2.top = rect2.bottom - StorageColumnView.this.f5823c;
            Rect rect3 = this.f5832d;
            rect3.bottom = this.e.top + (b2 / 2);
            rect3.top = rect3.bottom - b2;
            i();
        }
    }

    private static class d {

        /* renamed from: a  reason: collision with root package name */
        private BitmapDrawable f5833a;

        /* renamed from: b  reason: collision with root package name */
        private int f5834b;

        /* renamed from: c  reason: collision with root package name */
        private int f5835c;

        /* renamed from: d  reason: collision with root package name */
        private Point f5836d;
        private Rect e;
        private float f;
        private long g;
        private long h;
        private float i;
        private float j;
        private int k;
        private int l;
        private DecelerateInterpolator m = new DecelerateInterpolator(1.5f);

        public d(Resources resources, int i2, int i3) {
            this.f5833a = new BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.storage_column_bottom_shadow));
            this.f5834b = resources.getDimensionPixelSize(R.dimen.storage_column_shadow_w);
            this.f5835c = resources.getDimensionPixelSize(R.dimen.storage_column_shadow_h);
            this.f5836d = new Point(i2, (i3 - (this.f5835c / 2)) + resources.getDimensionPixelSize(R.dimen.storage_column_shadow_y_by_pie));
            this.f = 1.5f;
            this.i = 1.5f;
            this.j = 1.0f;
            this.k = 0;
            this.l = 255;
            this.g = 0;
            this.h = AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS;
            float f2 = this.f;
            int i4 = (int) ((((float) this.f5834b) * f2) / 2.0f);
            int i5 = (int) ((f2 * ((float) this.f5835c)) / 2.0f);
            Point point = this.f5836d;
            int i6 = point.x;
            int i7 = point.y;
            this.e = new Rect(i6 - i4, i7 - i5, i6 + i4, i7 + i5);
            this.f5833a.setBounds(this.e);
        }

        public void a(long j2) {
            this.g += j2;
            long j3 = this.g;
            if (j3 >= 0) {
                float interpolation = this.m.getInterpolation(Math.min(1.0f, ((float) j3) / ((float) this.h)));
                int i2 = this.k;
                this.f5833a.setAlpha((int) (((float) i2) + (((float) (this.l - i2)) * interpolation)));
                float f2 = this.i;
                float f3 = f2 + ((this.j - f2) * interpolation);
                int i3 = (int) ((((float) this.f5834b) * f3) / 2.0f);
                int i4 = (int) ((f3 * ((float) this.f5835c)) / 2.0f);
                Point point = this.f5836d;
                int i5 = point.x;
                int i6 = point.y;
                this.e = new Rect(i5 - i3, i6 - i4, i5 + i3, i6 + i4);
                this.f5833a.setBounds(this.e);
            }
        }

        public void a(Canvas canvas) {
            this.f5833a.draw(canvas);
        }
    }

    public StorageColumnView(Context context) {
        this(context, (AttributeSet) null);
    }

    public StorageColumnView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public StorageColumnView(Context context, @Nullable AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f5821a = 80;
        this.f5822b = 60;
        this.f5823c = 50;
        this.f5824d = 1396;
        this.e = 334;
        this.i = false;
        this.p = null;
        this.q = null;
        this.s = new Point();
        this.t = new HashMap<>();
        this.v = false;
        a(context);
        setLayerType(1, (Paint) null);
    }

    private static Bitmap a(int i2) {
        Bitmap createBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        new Paint(1).setColor(i2);
        canvas.drawColor(i2);
        return createBitmap;
    }

    private void a(Context context) {
        Resources resources = context.getResources();
        this.f5821a = resources.getDimensionPixelSize(R.dimen.storage_column_top_bottom_max_h);
        this.f5822b = resources.getDimensionPixelSize(R.dimen.storage_column_top_bottom_min_h);
        this.f5823c = resources.getDimensionPixelSize(R.dimen.storage_column_min_h);
        this.f5824d = resources.getDimensionPixelSize(R.dimen.storage_mid_max_h);
        this.e = resources.getDimensionPixelSize(R.dimen.storage_ui_w);
        this.g = resources.getDimensionPixelSize(g.b() ? R.dimen.storage_column_mt : R.dimen.storage_column_mt_small_title);
        this.f = resources.getDimensionPixelSize(R.dimen.storage_ui_h);
        this.h = this.g + this.f;
        this.k = new a(a(resources.getColor(R.color.storage_pie_mask)));
        this.l = new a(a(resources.getColor(R.color.storage_pie_mask)));
        this.m = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
        int i2 = this.g + (this.f / 2);
        this.n = new c(b.STORAGE_TOTAL, resources);
        this.n.a(this.h, this.f5822b, this.f5821a / 2, this.f5824d);
        int i3 = 0;
        for (b bVar : StorageViewGroup.f5837a) {
            this.t.put(bVar, new c(bVar, resources));
        }
        this.o = new d(resources, this.e / 2, this.h);
        c cVar = this.n;
        cVar.a(this.h - resources.getDimensionPixelSize(R.dimen.storage_column_bg_offset), this.h);
        cVar.b(0);
        cVar.a(650);
        cVar.c(0);
        cVar.a(0, 255, 650);
        a(true, 0);
        int i4 = 0;
        while (true) {
            b[] bVarArr = StorageViewGroup.f5837a;
            if (i4 >= bVarArr.length) {
                break;
            }
            c cVar2 = this.t.get(bVarArr[i4]);
            if (cVar2 != null) {
                cVar2.a((i2 - 100) - (i4 * 60), cVar2.a());
                int i5 = i4 * 50;
                cVar2.a(0, 255, i5);
                cVar2.b(0);
                cVar2.a(650);
                cVar2.c((long) i5);
            }
            i4++;
        }
        while (true) {
            b[] bVarArr2 = StorageViewGroup.f5837a;
            if (i3 < bVarArr2.length) {
                c cVar3 = this.t.get(bVarArr2[i3]);
                if (cVar3 != null) {
                    cVar3.h();
                }
                i3++;
            } else {
                return;
            }
        }
    }

    private void a(MotionEvent motionEvent) {
        c cVar;
        this.s.set((int) motionEvent.getX(), (int) motionEvent.getY());
        this.r = a(motionEvent.getX(), motionEvent.getY());
        if (!(this.u == null || (cVar = this.p) == null)) {
            Point c2 = cVar.c();
            this.u.a(motionEvent, this.p.g, c2.x + getLeft(), c2.y + 7 + getTop());
            this.k.a(true);
        }
        invalidate();
    }

    private void a(boolean z, long j2) {
        if (!z) {
            this.n.a(j2, true);
            this.o.a(j2);
        }
        boolean a2 = this.k.a(j2);
        boolean a3 = this.l.a(j2);
        boolean z2 = false;
        boolean z3 = true;
        boolean z4 = false;
        c cVar = null;
        int i2 = 0;
        while (true) {
            b[] bVarArr = StorageViewGroup.f5837a;
            if (i2 >= bVarArr.length) {
                break;
            }
            c cVar2 = this.t.get(bVarArr[i2]);
            if (cVar2 != null) {
                z4 |= !cVar2.f();
                z3 &= cVar2.e();
                if (z) {
                    cVar2.a(cVar == null ? this.n.a() : cVar.d(), cVar == null ? this.f5821a : cVar.b(), j2);
                } else {
                    cVar2.a(j2, false);
                }
                cVar = cVar2;
            }
            i2++;
        }
        if (this.r) {
            z2 = a(false);
        }
        this.i = z3;
        boolean z5 = this.i;
        this.v = z5;
        if (z4 || !z5 || a2 || z2 || a3) {
            invalidate();
        } else {
            this.j = 0;
        }
    }

    /* access modifiers changed from: private */
    public boolean a() {
        return getLayoutDirection() == 1;
    }

    private boolean a(float f2, float f3) {
        this.p = null;
        boolean z = true;
        for (int length = StorageViewGroup.f5837a.length - 1; length >= 0; length--) {
            c cVar = this.t.get(StorageViewGroup.f5837a[length]);
            if (cVar != null) {
                if (cVar.a((int) f2, (int) f3, z)) {
                    this.p = cVar;
                    return true;
                } else if (cVar.g()) {
                    z = false;
                }
            }
        }
        return false;
    }

    private boolean a(boolean z) {
        c cVar;
        c cVar2 = this.p;
        Point point = this.s;
        this.r = a((float) point.x, (float) point.y);
        if (!this.r) {
            b bVar = this.u;
            if (bVar != null) {
                bVar.a((b) null, 0, 0);
            }
            this.k.a(false);
            return true;
        } else if (cVar2 == null || (cVar = this.p) == null || cVar == cVar2) {
            c cVar3 = this.p;
            if (cVar3 != null) {
                Point c2 = cVar3.c();
                b bVar2 = this.u;
                if (bVar2 != null) {
                    bVar2.a(this.p.g, c2.x + getLeft(), c2.y + 7 + getTop());
                }
            }
            return z;
        } else {
            this.q = cVar2;
            Point c3 = cVar.c();
            b bVar3 = this.u;
            if (bVar3 != null) {
                bVar3.a(this.p.g, c3.x + getLeft(), c3.y + 7 + getTop());
            }
            this.l.a(true);
            return true;
        }
    }

    private boolean b(MotionEvent motionEvent) {
        this.s.set((int) motionEvent.getX(), (int) motionEvent.getY());
        if (!this.r) {
            return true;
        }
        c cVar = this.p;
        this.r = a(motionEvent.getX(), motionEvent.getY());
        if (!this.r) {
            b bVar = this.u;
            if (bVar != null) {
                bVar.a(motionEvent, (b) null, 0, 0);
            }
            this.k.a(false);
            this.q = null;
        } else {
            c cVar2 = this.p;
            if (!(cVar2 == null || cVar == null || cVar2 == cVar)) {
                Point c2 = cVar2.c();
                b bVar2 = this.u;
                if (bVar2 != null) {
                    bVar2.a(motionEvent, this.p.g, c2.x + getLeft(), c2.y + 7 + getTop());
                }
                this.q = cVar;
                this.l.a(true);
            }
        }
        invalidate();
        return false;
    }

    public void a(d dVar) {
        long a2 = dVar.a();
        int i2 = 0;
        while (true) {
            b[] bVarArr = StorageViewGroup.f5837a;
            if (i2 < bVarArr.length) {
                c cVar = this.t.get(bVarArr[i2]);
                if (cVar != null) {
                    float a3 = (float) dVar.a(cVar.g);
                    int i3 = (int) (((((float) this.f5824d) * a3) * 1.0f) / ((float) a2));
                    int i4 = this.f5823c;
                    if (i3 < i4) {
                        i3 = i4;
                    }
                    if (this.w && a3 == 0.0f) {
                        i3 = 0;
                    }
                    cVar.a(i3);
                }
                i2++;
            } else {
                postInvalidate();
                return;
            }
        }
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        boolean z;
        ViewParent viewParent;
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action == 2 && !this.x) {
                viewParent = getParent();
                z = false;
            }
            return super.dispatchTouchEvent(motionEvent);
        }
        this.x = a(motionEvent.getX(), motionEvent.getY());
        viewParent = getParent();
        z = true;
        viewParent.requestDisallowInterceptTouchEvent(z);
        return super.dispatchTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long uptimeMillis = SystemClock.uptimeMillis();
        if (this.j == 0) {
            this.j = uptimeMillis;
        }
        long j2 = uptimeMillis - this.j;
        this.j = uptimeMillis;
        this.o.a(canvas);
        this.n.a(canvas);
        int i2 = 0;
        while (true) {
            b[] bVarArr = StorageViewGroup.f5837a;
            if (i2 < bVarArr.length) {
                c cVar = this.t.get(bVarArr[i2]);
                if (cVar != null) {
                    boolean z = true;
                    boolean z2 = this.r || this.k.c();
                    c cVar2 = this.p;
                    boolean z3 = cVar2 != null && cVar2.g == cVar.g;
                    c cVar3 = this.q;
                    if (cVar3 == null || cVar3.g != cVar.g || !this.l.c()) {
                        z = false;
                    }
                    cVar.a(canvas, z2, z3, z);
                }
                i2++;
            } else {
                a(this.i, j2);
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        super.onMeasure(i2, i3);
        setMeasuredDimension(this.e, this.h + (this.f5821a / 2));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0016, code lost:
        if (r0 != 3) goto L_0x004b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r5) {
        /*
            r4 = this;
            boolean r0 = r4.v
            if (r0 != 0) goto L_0x0009
            boolean r5 = super.onTouchEvent(r5)
            return r5
        L_0x0009:
            int r0 = r5.getActionMasked()
            r1 = 1
            if (r0 == 0) goto L_0x0048
            if (r0 == r1) goto L_0x0024
            r2 = 2
            if (r0 == r2) goto L_0x0019
            r2 = 3
            if (r0 == r2) goto L_0x0024
            goto L_0x004b
        L_0x0019:
            boolean r0 = r4.b((android.view.MotionEvent) r5)
            if (r0 == 0) goto L_0x004b
            boolean r5 = super.onTouchEvent(r5)
            return r5
        L_0x0024:
            android.graphics.Point r0 = r4.s
            r2 = 0
            r0.set(r2, r2)
            boolean r0 = r4.r
            if (r0 != 0) goto L_0x0033
            boolean r5 = super.onTouchEvent(r5)
            return r5
        L_0x0033:
            com.miui.optimizecenter.widget.storage.StorageColumnView$b r0 = r4.u
            r3 = 0
            if (r0 == 0) goto L_0x003b
            r0.a(r5, r3, r2, r2)
        L_0x003b:
            com.miui.optimizecenter.widget.storage.StorageColumnView$a r5 = r4.k
            r5.a((boolean) r2)
            r4.q = r3
            r4.r = r2
            r4.invalidate()
            goto L_0x004b
        L_0x0048:
            r4.a((android.view.MotionEvent) r5)
        L_0x004b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.optimizecenter.widget.storage.StorageColumnView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setCanClick(boolean z) {
        this.v = z;
    }

    public void setOnItemEventListener(b bVar) {
        this.u = bVar;
    }

    public void setScanFinished(boolean z) {
        this.w = z;
    }
}
