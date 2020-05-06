package com.miui.superpower.statusbar.panel;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.widget.ScrollerCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import java.util.Arrays;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static final Interpolator f8213a = new b();

    /* renamed from: b  reason: collision with root package name */
    private int f8214b;

    /* renamed from: c  reason: collision with root package name */
    private int f8215c;

    /* renamed from: d  reason: collision with root package name */
    private int f8216d = -1;
    private float[] e;
    private float[] f;
    private float[] g;
    private float[] h;
    private int[] i;
    private int[] j;
    private int[] k;
    private VelocityTracker l;
    private float m;
    private float n;
    private int o;
    private ScrollerCompat p;
    private final a q;
    private View r;
    private boolean s;
    private final ViewGroup t;
    private final Runnable u = new c(this);

    public static abstract class a {
        /* access modifiers changed from: package-private */
        public abstract int a(View view);

        /* access modifiers changed from: package-private */
        public abstract int a(View view, int i, int i2);

        /* access modifiers changed from: package-private */
        public abstract void a(int i);

        /* access modifiers changed from: package-private */
        public abstract void a(View view, float f, float f2);

        /* access modifiers changed from: package-private */
        public abstract void a(View view, int i);

        /* access modifiers changed from: package-private */
        public abstract void a(View view, int i, int i2, int i3, int i4);

        /* access modifiers changed from: package-private */
        public abstract boolean b(View view, int i);
    }

    private d(Context context, ViewGroup viewGroup, a aVar) {
        if (viewGroup == null) {
            throw new IllegalArgumentException("Parent view may not be null");
        } else if (aVar != null) {
            this.t = viewGroup;
            this.q = aVar;
            ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
            this.o = (int) ((context.getResources().getDisplayMetrics().density * 20.0f) + 0.5f);
            this.f8215c = viewConfiguration.getScaledTouchSlop();
            this.m = (float) viewConfiguration.getScaledMaximumFlingVelocity();
            this.n = (float) viewConfiguration.getScaledMinimumFlingVelocity();
            this.p = ScrollerCompat.create(context, f8213a);
        } else {
            throw new IllegalArgumentException("Callback may not be null");
        }
    }

    private float a(float f2, float f3, float f4) {
        float abs = Math.abs(f2);
        if (abs < f3) {
            return 0.0f;
        }
        return abs > f4 ? f2 > 0.0f ? f4 : -f4 : f2;
    }

    private int a(int i2, int i3, int i4) {
        int abs = Math.abs(i2);
        if (abs < i3) {
            return 0;
        }
        return abs > i4 ? i2 > 0 ? i4 : -i4 : i2;
    }

    private int a(View view, int i2, int i3, int i4, int i5) {
        float f2;
        float f3;
        float f4;
        float f5;
        int a2 = a(i4, (int) this.n, (int) this.m);
        int a3 = a(i5, (int) this.n, (int) this.m);
        int abs = Math.abs(i2);
        int abs2 = Math.abs(i3);
        int abs3 = Math.abs(a2);
        int abs4 = Math.abs(a3);
        int i6 = abs3 + abs4;
        int i7 = abs + abs2;
        if (a2 != 0) {
            f3 = (float) abs3;
            f2 = (float) i6;
        } else {
            f3 = (float) abs;
            f2 = (float) i7;
        }
        float f6 = f3 / f2;
        if (a3 != 0) {
            f5 = (float) abs4;
            f4 = (float) i6;
        } else {
            f5 = (float) abs2;
            f4 = (float) i7;
        }
        return (int) ((((float) b(i2, a2, 0)) * f6) + (((float) b(i3, a3, this.q.a(view))) * (f5 / f4)));
    }

    public static d a(ViewGroup viewGroup, float f2, a aVar) {
        d a2 = a(viewGroup, aVar);
        a2.f8215c = (int) (((float) a2.f8215c) * (1.0f / f2));
        return a2;
    }

    public static d a(ViewGroup viewGroup, a aVar) {
        return new d(viewGroup.getContext(), viewGroup, aVar);
    }

    private void a(float f2, float f3) {
        this.s = true;
        this.q.a(this.r, f2, f3);
        this.s = false;
        if (this.f8214b == 1) {
            c(0);
        }
    }

    private void a(float f2, float f3, int i2) {
        int i3 = 1;
        if (!a(f2, f3, i2, 1)) {
            i3 = 0;
        }
        if (a(f3, f2, i2, 4)) {
            i3 |= 4;
        }
        if (a(f2, f3, i2, 2)) {
            i3 |= 2;
        }
        if (a(f3, f2, i2, 8)) {
            i3 |= 8;
        }
        if (i3 != 0) {
            int[] iArr = this.j;
            iArr[i2] = iArr[i2] | i3;
        }
    }

    private void a(int i2) {
        float[] fArr = this.e;
        if (fArr != null && fArr.length > i2) {
            fArr[i2] = 0.0f;
            this.f[i2] = 0.0f;
            this.g[i2] = 0.0f;
            this.h[i2] = 0.0f;
            this.i[i2] = 0;
            this.j[i2] = 0;
            this.k[i2] = 0;
        }
    }

    private void a(int i2, int i3, int i4, int i5) {
        int left = this.r.getLeft();
        int top = this.r.getTop();
        if (i4 != 0) {
            i2 = 0;
            this.r.offsetLeftAndRight(0 - left);
        }
        int i6 = i2;
        if (i5 != 0) {
            i3 = this.q.a(this.r, i3, i5);
            this.r.offsetTopAndBottom(i3 - top);
        }
        int i7 = i3;
        if (i4 != 0 || i5 != 0) {
            this.q.a(this.r, i6, i7, i6 - left, i7 - top);
        }
    }

    private void a(View view, int i2) {
        if (view.getParent() == this.t) {
            this.r = view;
            this.f8216d = i2;
            this.q.a(view, i2);
            c(1);
            return;
        }
        throw new IllegalArgumentException("captureChildView: parameter must be a descendant of the PanelDragHelper's tracked parent view (" + this.t + ")");
    }

    private boolean a(float f2, float f3, int i2, int i3) {
        float abs = Math.abs(f2);
        float abs2 = Math.abs(f3);
        if ((this.i[i2] & i3) != i3 || (this.k[i2] & i3) == i3 || (this.j[i2] & i3) == i3) {
            return false;
        }
        int i4 = this.f8215c;
        return (abs > ((float) i4) || abs2 > ((float) i4)) && (this.j[i2] & i3) == 0 && abs > ((float) this.f8215c);
    }

    private boolean a(View view, float f2) {
        if (view == null) {
            return false;
        }
        return (this.q.a(view) > 0) && Math.abs(f2) > ((float) this.f8215c);
    }

    private float b(float f2) {
        return (float) Math.sin((double) ((float) (((double) (f2 - 0.5f)) * 0.4712389167638204d)));
    }

    private int b(int i2, int i3, int i4) {
        if (i2 == 0) {
            return 0;
        }
        int width = this.t.getWidth();
        float f2 = (float) (width / 2);
        float b2 = f2 + (b(Math.min(1.0f, ((float) Math.abs(i2)) / ((float) width))) * f2);
        int abs = Math.abs(i3);
        return Math.min(abs > 0 ? Math.round(Math.abs(b2 / ((float) abs)) * 1000.0f) * 4 : (int) (((((float) Math.abs(i2)) / ((float) i4)) + 1.0f) * 256.0f), 600);
    }

    private View b(int i2, int i3) {
        for (int childCount = this.t.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = this.t.getChildAt(childCount);
            if (i2 >= childAt.getLeft() && i2 < childAt.getRight() && i3 >= childAt.getTop() && i3 < childAt.getBottom()) {
                return this.t.getChildAt(1);
            }
        }
        return null;
    }

    private void b(float f2, float f3, int i2) {
        b(i2);
        float[] fArr = this.e;
        this.g[i2] = f2;
        fArr[i2] = f2;
        float[] fArr2 = this.f;
        this.h[i2] = f3;
        fArr2[i2] = f3;
        this.i[i2] = c((int) f2, (int) f3);
    }

    private void b(int i2) {
        float[] fArr = this.e;
        if (fArr == null || fArr.length <= i2) {
            int i3 = i2 + 1;
            float[] fArr2 = new float[i3];
            float[] fArr3 = new float[i3];
            float[] fArr4 = new float[i3];
            float[] fArr5 = new float[i3];
            int[] iArr = new int[i3];
            int[] iArr2 = new int[i3];
            int[] iArr3 = new int[i3];
            float[] fArr6 = this.e;
            if (fArr6 != null) {
                System.arraycopy(fArr6, 0, fArr2, 0, fArr6.length);
                float[] fArr7 = this.f;
                System.arraycopy(fArr7, 0, fArr3, 0, fArr7.length);
                float[] fArr8 = this.g;
                System.arraycopy(fArr8, 0, fArr4, 0, fArr8.length);
                float[] fArr9 = this.h;
                System.arraycopy(fArr9, 0, fArr5, 0, fArr9.length);
                int[] iArr4 = this.i;
                System.arraycopy(iArr4, 0, iArr, 0, iArr4.length);
                int[] iArr5 = this.j;
                System.arraycopy(iArr5, 0, iArr2, 0, iArr5.length);
                int[] iArr6 = this.k;
                System.arraycopy(iArr6, 0, iArr3, 0, iArr6.length);
            }
            this.e = fArr2;
            this.f = fArr3;
            this.g = fArr4;
            this.h = fArr5;
            this.i = iArr;
            this.j = iArr2;
            this.k = iArr3;
        }
    }

    private boolean b(int i2, int i3, int i4, int i5) {
        int left = this.r.getLeft();
        int top = this.r.getTop();
        int i6 = i2 - left;
        int i7 = i3 - top;
        if (i6 == 0 && i7 == 0) {
            this.p.abortAnimation();
            c(0);
            return false;
        }
        this.p.startScroll(left, top, i6, i7, a(this.r, i6, i7, i4, i5));
        c(2);
        return true;
    }

    private boolean b(View view, int i2) {
        if (view == this.r && this.f8216d == i2) {
            return true;
        }
        if (view == null || !this.q.b(view, i2)) {
            return false;
        }
        this.f8216d = i2;
        a(view, i2);
        return true;
    }

    private boolean b(View view, int i2, int i3) {
        return view != null && i2 >= view.getLeft() && i2 < view.getRight() && i3 >= view.getTop() && i3 < view.getBottom();
    }

    private int c(int i2, int i3) {
        int i4 = i2 < this.t.getLeft() + this.o ? 1 : 0;
        if (i3 < this.t.getTop() + this.o) {
            i4 |= 4;
        }
        if (i2 > this.t.getRight() - this.o) {
            i4 |= 2;
        }
        return i3 > this.t.getBottom() - this.o ? i4 | 8 : i4;
    }

    /* access modifiers changed from: private */
    public void c(int i2) {
        if (this.f8214b != i2) {
            this.f8214b = i2;
            this.q.a(i2);
            if (this.f8214b == 0) {
                this.r = null;
            }
        }
    }

    private void c(MotionEvent motionEvent) {
        float[] fArr;
        int pointerCount = MotionEventCompat.getPointerCount(motionEvent);
        for (int i2 = 0; i2 < pointerCount; i2++) {
            int pointerId = MotionEventCompat.getPointerId(motionEvent, i2);
            float x = MotionEventCompat.getX(motionEvent, i2);
            float y = MotionEventCompat.getY(motionEvent, i2);
            float[] fArr2 = this.g;
            if (fArr2 != null && (fArr = this.h) != null && fArr2.length > pointerId && fArr.length > pointerId) {
                fArr2[pointerId] = x;
                fArr[pointerId] = y;
            }
        }
    }

    private boolean d(int i2, int i3) {
        return b(this.r, i2, i3);
    }

    private void f() {
        float[] fArr = this.e;
        if (fArr != null) {
            Arrays.fill(fArr, 0.0f);
            Arrays.fill(this.f, 0.0f);
            Arrays.fill(this.g, 0.0f);
            Arrays.fill(this.h, 0.0f);
            Arrays.fill(this.i, 0);
            Arrays.fill(this.j, 0);
            Arrays.fill(this.k, 0);
        }
    }

    private void g() {
        this.l.computeCurrentVelocity(1000, this.m);
        a(a(VelocityTrackerCompat.getXVelocity(this.l, this.f8216d), this.n, this.m), a(VelocityTrackerCompat.getYVelocity(this.l, this.f8216d), this.n, this.m));
    }

    /* access modifiers changed from: package-private */
    public void a() {
        b();
        if (this.f8214b == 2) {
            int currX = this.p.getCurrX();
            int currY = this.p.getCurrY();
            this.p.abortAnimation();
            int currX2 = this.p.getCurrX();
            int currY2 = this.p.getCurrY();
            this.q.a(this.r, currX2, currY2, currX2 - currX, currY2 - currY);
        }
        c(0);
    }

    /* access modifiers changed from: package-private */
    public void a(float f2) {
        this.n = f2;
    }

    /* access modifiers changed from: package-private */
    public void a(int i2, int i3) {
        if (this.s) {
            b(i2, i3, (int) VelocityTrackerCompat.getXVelocity(this.l, this.f8216d), (int) VelocityTrackerCompat.getYVelocity(this.l, this.f8216d));
            return;
        }
        throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased");
    }

    /* access modifiers changed from: package-private */
    public void a(MotionEvent motionEvent) {
        View view;
        int i2;
        int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
        if (actionMasked == 0) {
            b();
        }
        if (this.l == null) {
            this.l = VelocityTracker.obtain();
        }
        this.l.addMovement(motionEvent);
        int i3 = 0;
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    if (this.f8214b != 1) {
                        int pointerCount = MotionEventCompat.getPointerCount(motionEvent);
                        while (i3 < pointerCount) {
                            int pointerId = MotionEventCompat.getPointerId(motionEvent, i3);
                            float x = MotionEventCompat.getX(motionEvent, i3);
                            float y = MotionEventCompat.getY(motionEvent, i3);
                            float f2 = x - this.e[pointerId];
                            float f3 = y - this.f[pointerId];
                            a(f2, f3, pointerId);
                            if (this.f8214b != 1) {
                                View b2 = b((int) this.e[pointerId], (int) this.f[pointerId]);
                                if (a(b2, f3) && b(b2, pointerId)) {
                                    break;
                                }
                                i3++;
                            } else {
                                break;
                            }
                        }
                    } else {
                        int findPointerIndex = MotionEventCompat.findPointerIndex(motionEvent, this.f8216d);
                        float x2 = MotionEventCompat.getX(motionEvent, findPointerIndex);
                        float y2 = MotionEventCompat.getY(motionEvent, findPointerIndex);
                        float[] fArr = this.g;
                        int i4 = this.f8216d;
                        int i5 = (int) (x2 - fArr[i4]);
                        int i6 = (int) (y2 - this.h[i4]);
                        a(this.r.getLeft() + i5, this.r.getTop() + i6, i5, i6);
                    }
                    c(motionEvent);
                    return;
                } else if (actionMasked != 3) {
                    if (actionMasked == 5) {
                        int pointerId2 = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                        float x3 = MotionEventCompat.getX(motionEvent, actionIndex);
                        float y3 = MotionEventCompat.getY(motionEvent, actionIndex);
                        b(x3, y3, pointerId2);
                        if (this.f8214b == 0) {
                            view = b((int) x3, (int) y3);
                        } else if (d((int) x3, (int) y3)) {
                            view = this.r;
                        } else {
                            return;
                        }
                        b(view, pointerId2);
                        return;
                    } else if (actionMasked == 6) {
                        int pointerId3 = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                        if (this.f8214b == 1 && pointerId3 == this.f8216d) {
                            int pointerCount2 = MotionEventCompat.getPointerCount(motionEvent);
                            while (true) {
                                if (i3 >= pointerCount2) {
                                    i2 = -1;
                                    break;
                                }
                                int pointerId4 = MotionEventCompat.getPointerId(motionEvent, i3);
                                if (pointerId4 != this.f8216d) {
                                    View b3 = b((int) MotionEventCompat.getX(motionEvent, i3), (int) MotionEventCompat.getY(motionEvent, i3));
                                    View view2 = this.r;
                                    if (b3 == view2 && b(view2, pointerId4)) {
                                        i2 = this.f8216d;
                                        break;
                                    }
                                }
                                i3++;
                            }
                            if (i2 == -1) {
                                g();
                            }
                        }
                        a(pointerId3);
                        return;
                    } else {
                        return;
                    }
                } else if (this.f8214b == 1) {
                    a(0.0f, 0.0f);
                }
            } else if (this.f8214b == 1) {
                g();
            }
            b();
            return;
        }
        float x4 = motionEvent.getX();
        float y4 = motionEvent.getY();
        int pointerId5 = MotionEventCompat.getPointerId(motionEvent, 0);
        View b4 = b((int) x4, (int) y4);
        b(x4, y4, pointerId5);
        b(b4, pointerId5);
    }

    /* access modifiers changed from: package-private */
    public boolean a(View view, int i2, int i3) {
        this.r = view;
        this.f8216d = -1;
        return b(i2, i3, 0, 0);
    }

    public boolean a(boolean z) {
        if (this.r == null) {
            return false;
        }
        if (this.f8214b == 2) {
            boolean computeScrollOffset = this.p.computeScrollOffset();
            int currX = this.p.getCurrX();
            int currY = this.p.getCurrY();
            int left = currX - this.r.getLeft();
            int top = currY - this.r.getTop();
            if (computeScrollOffset || top == 0) {
                if (left != 0) {
                    this.r.offsetLeftAndRight(left);
                }
                if (top != 0) {
                    this.r.offsetTopAndBottom(top);
                }
                if (!(left == 0 && top == 0)) {
                    this.q.a(this.r, currX, currY, left, top);
                }
                if (computeScrollOffset && currX == this.p.getFinalX() && currY == this.p.getFinalY()) {
                    this.p.abortAnimation();
                    computeScrollOffset = this.p.isFinished();
                }
                if (!computeScrollOffset) {
                    if (z) {
                        this.t.post(this.u);
                    } else {
                        c(0);
                    }
                }
            } else {
                this.r.setTop(0);
                return true;
            }
        }
        return this.f8214b == 2;
    }

    public void b() {
        this.f8216d = -1;
        f();
        VelocityTracker velocityTracker = this.l;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.l = null;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean b(MotionEvent motionEvent) {
        View b2;
        int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
        if (actionMasked == 0) {
            b();
        }
        if (this.l == null) {
            this.l = VelocityTracker.obtain();
        }
        this.l.addMovement(motionEvent);
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    int pointerCount = MotionEventCompat.getPointerCount(motionEvent);
                    for (int i2 = 0; i2 < pointerCount && this.e != null && this.f != null; i2++) {
                        int pointerId = MotionEventCompat.getPointerId(motionEvent, i2);
                        if (pointerId < this.e.length && pointerId < this.f.length) {
                            float x = MotionEventCompat.getX(motionEvent, i2);
                            float y = MotionEventCompat.getY(motionEvent, i2);
                            float f2 = x - this.e[pointerId];
                            float f3 = y - this.f[pointerId];
                            a(f2, f3, pointerId);
                            if (this.f8214b != 1) {
                                View b3 = b((int) this.e[pointerId], (int) this.f[pointerId]);
                                if (a(b3, f3) && b(b3, pointerId)) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                    c(motionEvent);
                } else if (actionMasked != 3) {
                    if (actionMasked == 5) {
                        int pointerId2 = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                        float x2 = MotionEventCompat.getX(motionEvent, actionIndex);
                        float y2 = MotionEventCompat.getY(motionEvent, actionIndex);
                        b(x2, y2, pointerId2);
                        if (this.f8214b == 2 && (b2 = b((int) x2, (int) y2)) == this.r) {
                            b(b2, pointerId2);
                        }
                    } else if (actionMasked == 6) {
                        a(MotionEventCompat.getPointerId(motionEvent, actionIndex));
                    }
                }
            }
            b();
        } else {
            float x3 = motionEvent.getX();
            float y3 = motionEvent.getY();
            int pointerId3 = MotionEventCompat.getPointerId(motionEvent, 0);
            b(x3, y3, pointerId3);
            View b4 = b((int) x3, (int) y3);
            if (b4 == this.r && this.f8214b == 2) {
                b(b4, pointerId3);
            }
        }
        return this.f8214b == 1;
    }

    /* access modifiers changed from: package-private */
    public int c() {
        return this.f8215c;
    }

    /* access modifiers changed from: package-private */
    public int d() {
        return this.f8214b;
    }

    /* access modifiers changed from: package-private */
    public boolean e() {
        return this.f8214b == 1;
    }
}
