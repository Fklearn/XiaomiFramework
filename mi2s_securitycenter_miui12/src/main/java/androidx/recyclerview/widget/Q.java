package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EdgeEffect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.l;
import androidx.recyclerview.widget.M;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.reflect.Field;

public abstract class Q extends M {
    private static final Field Ja;
    private static final Field Ka;
    private static final RecyclerView.EdgeEffectFactory La = new b((P) null);
    /* access modifiers changed from: private */
    public c Ma;
    /* access modifiers changed from: private */
    public d Na;
    private boolean Oa;
    /* access modifiers changed from: private */
    public boolean Pa;
    /* access modifiers changed from: private */
    public boolean Qa;
    /* access modifiers changed from: private */
    public int Ra;
    /* access modifiers changed from: private */
    public d.i.a.c Sa;

    private static class a extends EdgeEffect {
        a(Context context) {
            super(context);
        }

        public boolean draw(Canvas canvas) {
            return false;
        }

        public void finish() {
        }

        public int getColor() {
            return 0;
        }

        public int getMaxHeight() {
            return 0;
        }

        public boolean isFinished() {
            return true;
        }

        public void onAbsorb(int i) {
        }

        public void onPull(float f) {
        }

        public void onPull(float f, float f2) {
        }

        public void onRelease() {
        }

        public void setColor(int i) {
        }

        public void setSize(int i, int i2) {
        }
    }

    private static class b extends RecyclerView.EdgeEffectFactory {
        private b() {
        }

        /* synthetic */ b(P p) {
            this();
        }

        /* access modifiers changed from: protected */
        @NonNull
        public EdgeEffect a(@NonNull RecyclerView recyclerView, int i) {
            return new a(recyclerView.getContext());
        }
    }

    private class c extends M.a {
        private c() {
            super();
        }

        /* synthetic */ c(Q q, P p) {
            this();
        }

        /* access modifiers changed from: package-private */
        public void a(int i) {
            boolean unused = Q.this.Pa = true;
            Q.this.setScrollState(2);
            c();
            this.j.a(0, -i, Q.this.getWidth());
        }

        public void a(int i, int i2) {
            int d2 = Q.this.Sa.d();
            int e = Q.this.Sa.e();
            if (!Q.this.Q() || (d2 == 0 && e == 0)) {
                super.a(i, i2);
            } else {
                a(i, i2, d2, e);
            }
        }

        /* access modifiers changed from: package-private */
        public void a(int i, int i2, int i3, int i4) {
            int i5;
            int i6;
            int i7;
            int i8;
            int i9 = i3;
            int i10 = i4;
            boolean z = true;
            boolean unused = Q.this.Pa = i9 != 0;
            Q q = Q.this;
            if (i10 == 0) {
                z = false;
            }
            boolean unused2 = q.Qa = z;
            Q.this.setScrollState(2);
            c();
            int i11 = Integer.MIN_VALUE;
            int i12 = Integer.MAX_VALUE;
            if (Integer.signum(i) * i9 > 0) {
                i6 = -i9;
                i5 = i6;
            } else if (i < 0) {
                i5 = -i9;
                i6 = Integer.MIN_VALUE;
            } else {
                i6 = -i9;
                i5 = Integer.MAX_VALUE;
            }
            if (Integer.signum(i2) * i10 > 0) {
                i8 = -i10;
                i7 = i8;
            } else {
                if (i2 < 0) {
                    i12 = -i10;
                } else {
                    i11 = -i10;
                }
                i8 = i11;
                i7 = i12;
            }
            this.j.a(0, 0, i, i2, i6, i5, i8, i7, Q.this.getWidth(), Q.this.getHeight());
            a();
        }

        /* access modifiers changed from: package-private */
        public void b(int i) {
            boolean unused = Q.this.Qa = true;
            Q.this.setScrollState(2);
            c();
            this.j.b(0, -i, Q.this.getHeight());
        }

        /* access modifiers changed from: package-private */
        public void b(int i, int i2) {
            if (i != 0) {
                boolean unused = Q.this.Pa = true;
            }
            if (i2 != 0) {
                boolean unused2 = Q.this.Qa = true;
            }
            Q.this.setScrollState(2);
            c();
            int i3 = -i;
            int i4 = -i2;
            this.j.a(0, 0, i3, i3, i4, i4);
            a();
        }
    }

    private class d extends l {
        d(@NonNull View view) {
            super(view);
        }

        public void a(int i, int i2, int i3, int i4, @Nullable int[] iArr, int i5, @Nullable int[] iArr2) {
            Q.this.Sa.b(i, i2, i3, i4, iArr, i5, iArr2);
        }

        public boolean a(int i, int i2, @Nullable int[] iArr, @Nullable int[] iArr2, int i3) {
            return Q.this.Sa.b(i, i2, iArr, iArr2, i3);
        }

        /* access modifiers changed from: package-private */
        public void b(int i, int i2, int i3, int i4, @Nullable int[] iArr, int i5, @Nullable int[] iArr2) {
            if (!Q.this.Pa && !Q.this.Qa) {
                super.a(i, i2, i3, i4, iArr, i5, iArr2);
            }
        }

        /* access modifiers changed from: package-private */
        public boolean b(int i, int i2, @Nullable int[] iArr, @Nullable int[] iArr2, int i3) {
            if (Q.this.Pa || Q.this.Qa) {
                return false;
            }
            if (i == 0 && i2 == 0) {
                return false;
            }
            return super.a(i, i2, iArr, iArr2, i3);
        }
    }

    static {
        try {
            Ja = RecyclerView.class.getDeclaredField("na");
            Ja.setAccessible(true);
            try {
                Ka = RecyclerView.class.getDeclaredField("Aa");
                Ka.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchFieldException e2) {
            throw new RuntimeException(e2);
        }
    }

    public Q(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public Q(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, a.i.a.recyclerViewStyle);
    }

    public Q(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.Oa = true;
        this.Ra = 0;
        this.Sa = new P(this);
        this.Ma = new c(this, (P) null);
        this.Na = new d(this);
        a((M.a) this.Ma);
        a((l) this.Na);
        super.setEdgeEffectFactory(La);
        if (d.f.a.f8810a) {
            this.Oa = false;
        }
    }

    /* access modifiers changed from: private */
    public boolean Q() {
        return getOverScrollMode() != 2 && this.Oa;
    }

    private void a(l lVar) {
        try {
            Ka.set(this, lVar);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void a(M.a aVar) {
        try {
            Ja.set(this, aVar);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Canvas canvas) {
        int d2 = this.Sa.d();
        int e = this.Sa.e();
        if (d2 == 0 && e == 0) {
            super.draw(canvas);
            return;
        }
        int save = canvas.save();
        canvas.translate((float) (-d2), (float) (-e));
        super.draw(canvas);
        canvas.restoreToCount(save);
    }

    public void e(int i) {
        super.e(i);
        this.Ra = i;
        if (!Q() || i == 2) {
            return;
        }
        if (this.Pa || this.Qa) {
            this.Ma.b();
            this.Pa = false;
            this.Qa = false;
        }
    }

    /* access modifiers changed from: protected */
    public boolean getSpringEnabled() {
        return this.Oa;
    }

    public /* bridge */ /* synthetic */ boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return super.onInterceptTouchEvent(motionEvent);
    }

    public /* bridge */ /* synthetic */ boolean onTouchEvent(MotionEvent motionEvent) {
        return super.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: package-private */
    public void setScrollState(int i) {
        if (this.Ra == 1 && i == 0) {
            int d2 = this.Sa.d();
            int e = this.Sa.e();
            if (!(d2 == 0 && e == 0)) {
                this.Ma.b(d2, e);
                return;
            }
        }
        super.setScrollState(i);
    }

    /* access modifiers changed from: protected */
    public void setSpringEnabled(boolean z) {
        this.Oa = z;
    }
}
