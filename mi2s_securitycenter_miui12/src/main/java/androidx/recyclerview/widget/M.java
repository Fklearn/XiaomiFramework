package androidx.recyclerview.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import d.a.i.f;
import d.g.b.d;

abstract class M extends RecyclerView {
    private int Ha;
    /* access modifiers changed from: private */
    public f Ia;

    class a extends RecyclerView.t {
        private int h;
        private int i;
        d j;
        Interpolator k = RecyclerView.i;
        private boolean l = false;
        private boolean m = false;

        a() {
            super();
            this.j = new d(M.this.getContext(), RecyclerView.i);
        }

        private int a(int i2, int i3, int i4, int i5) {
            int i6;
            int abs = Math.abs(i2);
            int abs2 = Math.abs(i3);
            boolean z = abs > abs2;
            int sqrt = (int) Math.sqrt((double) ((i4 * i4) + (i5 * i5)));
            int sqrt2 = (int) Math.sqrt((double) ((i2 * i2) + (i3 * i3)));
            int width = z ? M.this.getWidth() : M.this.getHeight();
            int i7 = width / 2;
            float f = (float) width;
            float f2 = (float) i7;
            float b2 = f2 + (b(Math.min(1.0f, (((float) sqrt2) * 1.0f) / f)) * f2);
            if (sqrt > 0) {
                i6 = Math.round(Math.abs(b2 / ((float) sqrt)) * 1000.0f) * 4;
            } else {
                if (!z) {
                    abs = abs2;
                }
                i6 = (int) (((((float) abs) / f) + 1.0f) * 300.0f);
            }
            return Math.min(i6, 2000);
        }

        private float b(float f) {
            return (float) Math.sin((double) ((f - 0.5f) * 0.47123894f));
        }

        private void d() {
            M.this.removeCallbacks(this);
            ViewCompat.a((View) M.this, (Runnable) this);
        }

        /* access modifiers changed from: package-private */
        public void a() {
            if (this.l) {
                this.m = true;
            } else {
                d();
            }
        }

        public void a(int i2, int i3) {
            M.this.setScrollState(2);
            this.i = 0;
            this.h = 0;
            Interpolator interpolator = this.k;
            Interpolator interpolator2 = RecyclerView.i;
            if (interpolator != interpolator2) {
                this.k = interpolator2;
                this.j = new d(M.this.getContext(), RecyclerView.i);
            }
            this.j.a(0, 0, -((int) M.this.Ia.a(0)), -((int) M.this.Ia.a(1)), Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            a();
        }

        public void a(int i2, int i3, int i4, @Nullable Interpolator interpolator) {
            if (i4 == Integer.MIN_VALUE) {
                i4 = a(i2, i3, 0, 0);
            }
            int i5 = i4;
            if (interpolator == null) {
                interpolator = RecyclerView.i;
            }
            if (this.k != interpolator) {
                this.k = interpolator;
                this.j = new d(M.this.getContext(), interpolator);
            }
            this.i = 0;
            this.h = 0;
            M.this.setScrollState(2);
            this.j.a(0, 0, i2, i3, i5);
            if (Build.VERSION.SDK_INT < 23) {
                this.j.b();
            }
            a();
        }

        public void b() {
            M.this.removeCallbacks(this);
            this.j.a();
        }

        /* access modifiers changed from: package-private */
        public void c() {
            this.i = 0;
            this.h = 0;
        }

        public void run() {
            int i2;
            int i3;
            M m2 = M.this;
            if (m2.v == null) {
                b();
                return;
            }
            this.m = false;
            this.l = true;
            m2.b();
            d dVar = this.j;
            if (dVar.b()) {
                int f = dVar.f();
                int g = dVar.g();
                int i4 = f - this.h;
                int i5 = g - this.i;
                this.h = f;
                this.i = g;
                M m3 = M.this;
                int[] iArr = m3.Da;
                iArr[0] = 0;
                iArr[1] = 0;
                ViewParent parent = m3.getParent();
                View findViewById = M.this.getRootView().findViewById(16908290);
                while (parent != null && ((!(parent instanceof d.c.b.a) || !((d.c.b.a) parent).a(this.j.d(), this.j.e())) && (!(parent instanceof ViewGroup) || parent != findViewById))) {
                    parent = parent.getParent();
                }
                M m4 = M.this;
                if (m4.a(i4, i5, m4.Da, (int[]) null, 1)) {
                    int[] iArr2 = M.this.Da;
                    i4 -= iArr2[0];
                    i5 -= iArr2[1];
                }
                if (M.this.getOverScrollMode() != 2) {
                    M.this.b(i4, i5);
                }
                M m5 = M.this;
                if (m5.u != null) {
                    int[] iArr3 = m5.Da;
                    iArr3[0] = 0;
                    iArr3[1] = 0;
                    m5.a(i4, i5, iArr3);
                    M m6 = M.this;
                    int[] iArr4 = m6.Da;
                    i2 = iArr4[0];
                    i3 = iArr4[1];
                    i4 -= i2;
                    i5 -= i3;
                    RecyclerView.q qVar = m6.v.g;
                    if (qVar != null && !qVar.b() && qVar.c()) {
                        int a2 = M.this.qa.a();
                        if (a2 == 0) {
                            qVar.d();
                        } else {
                            if (qVar.a() >= a2) {
                                qVar.a(a2 - 1);
                            }
                            qVar.a(i2, i3);
                        }
                    }
                } else {
                    i3 = 0;
                    i2 = 0;
                }
                if (!M.this.x.isEmpty()) {
                    M.this.invalidate();
                }
                M m7 = M.this;
                int[] iArr5 = m7.Da;
                iArr5[0] = 0;
                iArr5[1] = 0;
                m7.a(i2, i3, i4, i5, (int[]) null, 1, iArr5);
                int[] iArr6 = M.this.Da;
                int i6 = i4 - iArr6[0];
                int i7 = i5 - iArr6[1];
                if (!(i2 == 0 && i3 == 0)) {
                    M.this.d(i2, i3);
                }
                if (!M.this.awakenScrollBars()) {
                    M.this.invalidate();
                }
                boolean z = dVar.j() || (((dVar.f() == dVar.h()) || i6 != 0) && ((dVar.g() == dVar.i()) || i7 != 0));
                RecyclerView.q qVar2 = M.this.v.g;
                if ((qVar2 != null && qVar2.b()) || !z) {
                    a();
                    M m8 = M.this;
                    C0178t tVar = m8.oa;
                    if (tVar != null) {
                        tVar.a((RecyclerView) m8, i2, i3);
                    }
                } else {
                    if (M.this.getOverScrollMode() != 2) {
                        int c2 = (int) dVar.c();
                        int i8 = i6 < 0 ? -c2 : i6 > 0 ? c2 : 0;
                        if (i7 < 0) {
                            c2 = -c2;
                        } else if (i7 <= 0) {
                            c2 = 0;
                        }
                        M.this.a(i8, c2);
                    }
                    if (RecyclerView.e) {
                        M.this.pa.a();
                    }
                }
            }
            RecyclerView.q qVar3 = M.this.v.g;
            if (qVar3 != null && qVar3.b()) {
                qVar3.a(0, 0);
            }
            this.l = false;
            if (this.m) {
                d();
                return;
            }
            M.this.setScrollState(0);
            M.this.g(1);
        }
    }

    public M(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public M(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, d.h.a.recyclerViewStyle);
    }

    public M(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.Ha = -1;
    }

    private void a(MotionEvent motionEvent, int i) {
        if (Build.VERSION.SDK_INT >= 29) {
            this.Ia.a((double) motionEvent.getRawX(i), (double) motionEvent.getRawY(i));
            return;
        }
        this.Ia.a((double) motionEvent.getRawX(), (double) motionEvent.getRawY());
    }

    private void d(MotionEvent motionEvent) {
        int i;
        if (this.Ia == null) {
            this.Ia = new f();
        }
        int actionMasked = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        if (actionMasked != 0) {
            int i2 = 1;
            if (actionMasked == 1 || actionMasked == 2) {
                int findPointerIndex = motionEvent.findPointerIndex(this.Ha);
                if (findPointerIndex < 0) {
                    Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.Ha + " not found. Did any MotionEvents get skipped?");
                    return;
                }
                a(motionEvent, findPointerIndex);
                return;
            } else if (actionMasked != 5) {
                if (actionMasked == 6 && motionEvent.getPointerId(actionIndex) == this.Ha) {
                    if (actionIndex != 0) {
                        i2 = 0;
                    }
                    i = motionEvent.getPointerId(i2);
                    this.Ha = i;
                    a(motionEvent, actionIndex);
                }
                return;
            }
        } else {
            this.Ia.a();
        }
        i = motionEvent.getPointerId(actionIndex);
        this.Ha = i;
        a(motionEvent, actionIndex);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        d(motionEvent);
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        d(motionEvent);
        return super.onTouchEvent(motionEvent);
    }
}
