package com.miui.gamebooster.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import java.util.LinkedList;

public class DragGridView extends GridView {
    private boolean A;
    private int B;
    private int C;
    private int D;
    private Handler E;
    private Runnable F;
    /* access modifiers changed from: private */
    public int G;
    /* access modifiers changed from: private */
    public int H;
    private int I;
    private int J;
    Runnable K;

    /* renamed from: a  reason: collision with root package name */
    private long f5250a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public boolean f5251b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f5252c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public int f5253d;
    private int e;
    private int f;
    /* access modifiers changed from: private */
    public int g;
    private int h;
    /* access modifiers changed from: private */
    public View i;
    private ImageView j;
    private ImageView k;
    /* access modifiers changed from: private */
    public Vibrator l;
    private WindowManager m;
    private WindowManager.LayoutParams n;
    /* access modifiers changed from: private */
    public Bitmap o;
    private Bitmap p;
    private int q;
    private int r;
    private int s;
    private int t;
    private int u;
    /* access modifiers changed from: private */
    public boolean v;
    private int w;
    private d x;
    private int y;
    private int z;

    public DragGridView(Context context) {
        this(context, (AttributeSet) null);
    }

    public DragGridView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DragGridView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f5250a = 500;
        this.f5251b = false;
        this.g = -1;
        this.h = -1;
        this.i = null;
        this.v = true;
        this.C = 1;
        this.E = new Handler();
        this.F = new e(this);
        this.G = 0;
        this.H = 0;
        this.I = 2;
        this.J = -2;
        this.K = new f(this);
        this.l = (Vibrator) context.getSystemService("vibrator");
        this.m = (WindowManager) context.getSystemService("window");
        this.u = a(context);
        this.w = ViewConfiguration.get(context).getScaledTouchSlop();
        if (!this.A) {
            this.y = -1;
        }
    }

    private static int a(int i2, int i3, int i4) {
        return i2 < i3 ? i3 : i2 >= i4 ? i4 - 1 : i2;
    }

    private static int a(Context context) {
        Rect rect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int i2 = rect.top;
        if (i2 != 0) {
            return i2;
        }
        try {
            Class<?> cls = Class.forName("com.android.internal.R$dimen");
            return context.getResources().getDimensionPixelSize(Integer.parseInt(cls.getField("status_bar_height").get(cls.newInstance()).toString()));
        } catch (Exception e2) {
            e2.printStackTrace();
            return i2;
        }
    }

    private AnimatorSet a(View view, float f2, float f3, float f4, float f5) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "translationX", new float[]{f2, f3});
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view, "translationY", new float[]{f4, f5});
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2});
        return animatorSet;
    }

    private static Bitmap a(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(1.1f, 1.1f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void a() {
        View childAt = getChildAt(this.g - getFirstVisiblePosition());
        if (childAt != null) {
            childAt.setVisibility(0);
        }
        this.G = this.H;
        b();
        this.x.a(-1);
        this.x.a();
        c();
        d();
        this.h = -1;
        this.g = -1;
    }

    /* access modifiers changed from: private */
    public void a(int i2, int i3) {
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        float f7;
        boolean z2 = i3 > i2;
        LinkedList linkedList = new LinkedList();
        if (z2) {
            while (i2 < i3) {
                View childAt = getChildAt(i2 - getFirstVisiblePosition());
                if (childAt != null) {
                    if ((i2 + 1) % this.y == 0) {
                        f7 = (float) ((-childAt.getWidth()) * (this.y - 1));
                        f6 = 0.0f;
                        f5 = (float) childAt.getHeight();
                    } else {
                        f7 = (float) childAt.getWidth();
                        f6 = 0.0f;
                        f5 = 0.0f;
                    }
                    linkedList.add(a(childAt, f7, f6, f5, 0.0f));
                }
                i2++;
            }
        } else {
            while (i2 > i3) {
                View childAt2 = getChildAt(i2 - getFirstVisiblePosition());
                if (childAt2 != null) {
                    int i4 = this.y;
                    if ((i2 + i4) % i4 == 0) {
                        f4 = (float) (childAt2.getWidth() * (this.y - 1));
                        f3 = 0.0f;
                        f2 = (float) (-childAt2.getHeight());
                    } else {
                        f4 = (float) (-childAt2.getWidth());
                        f3 = 0.0f;
                        f2 = 0.0f;
                    }
                    linkedList.add(a(childAt2, f4, f3, f2, 0.0f));
                }
                i2--;
            }
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(linkedList);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new h(this));
        animatorSet.start();
    }

    /* access modifiers changed from: private */
    public void a(Bitmap bitmap, int i2, int i3) {
        this.n = new WindowManager.LayoutParams();
        WindowManager.LayoutParams layoutParams = this.n;
        layoutParams.format = -3;
        layoutParams.gravity = 51;
        layoutParams.alpha = 0.9f;
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.flags = 24;
        this.j = new ImageView(getContext());
        Bitmap a2 = a(bitmap);
        WindowManager.LayoutParams layoutParams2 = this.n;
        layoutParams2.x = ((i2 - this.r) + this.t) - ((a2.getWidth() - bitmap.getWidth()) / 2);
        layoutParams2.y = (((i3 - this.q) + this.s) - this.u) - ((a2.getHeight() - bitmap.getHeight()) / 2);
        this.j.setImageBitmap(a2);
        this.m.addView(this.j, this.n);
    }

    private boolean a(View view, int i2, int i3) {
        if (view == null) {
            return false;
        }
        int left = view.getLeft();
        int top = view.getTop();
        return i2 >= left && i2 <= left + view.getWidth() && i3 >= top && i3 <= top + view.getHeight();
    }

    private void b() {
        this.E.removeCallbacks(this.K);
    }

    private void b(int i2, int i3) {
        int i4;
        WindowManager.LayoutParams layoutParams = this.n;
        layoutParams.x = (i2 - this.r) + this.t;
        layoutParams.y = ((i3 - this.q) + this.s) - this.u;
        this.m.updateViewLayout(this.j, layoutParams);
        c(i2, i3);
        if ((i3 - this.q) + this.i.getHeight() > getHeight() - (this.i.getHeight() / 2)) {
            if (this.G != this.I) {
                b();
                i4 = this.I;
            } else {
                return;
            }
        } else if (i3 - this.q >= this.i.getHeight() / 2) {
            this.G = this.H;
            b();
            return;
        } else if (this.G != this.J) {
            b();
            i4 = this.J;
        } else {
            return;
        }
        this.G = i4;
        post(this.K);
    }

    private void b(Bitmap bitmap, int i2, int i3) {
        this.n = new WindowManager.LayoutParams();
        WindowManager.LayoutParams layoutParams = this.n;
        layoutParams.format = -3;
        layoutParams.gravity = 51;
        layoutParams.x = (i2 - this.r) + this.t;
        layoutParams.y = ((i3 - this.q) + this.s) - this.u;
        layoutParams.alpha = 0.9f;
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.flags = 24;
        this.k = new ImageView(getContext());
        this.k.setImageBitmap(bitmap);
        this.m.addView(this.k, this.n);
    }

    private void c() {
        ImageView imageView = this.j;
        if (imageView != null) {
            this.m.removeView(imageView);
            this.j = null;
        }
    }

    private void c(int i2, int i3) {
        int pointToPosition = pointToPosition(i2, i3);
        int i4 = this.g;
        if (pointToPosition != i4 && pointToPosition != -1 && this.v && pointToPosition != this.D) {
            this.x.a(i4, pointToPosition);
            this.x.a(pointToPosition);
            ViewTreeObserver viewTreeObserver = getViewTreeObserver();
            viewTreeObserver.addOnPreDrawListener(new g(this, viewTreeObserver, pointToPosition));
        }
    }

    /* access modifiers changed from: private */
    public void d() {
        ImageView imageView = this.k;
        if (imageView != null) {
            this.m.removeView(imageView);
            this.k = null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000d, code lost:
        if (r0 != 3) goto L_0x00fd;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dispatchTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            int r0 = r6.getAction()
            r1 = 1
            if (r0 == 0) goto L_0x0052
            if (r0 == r1) goto L_0x003f
            r1 = 2
            if (r0 == r1) goto L_0x0011
            r1 = 3
            if (r0 == r1) goto L_0x003f
            goto L_0x00fd
        L_0x0011:
            float r0 = r6.getX()
            int r0 = (int) r0
            float r1 = r6.getY()
            int r1 = (int) r1
            android.view.View r2 = r5.i
            boolean r0 = r5.a((android.view.View) r2, (int) r0, (int) r1)
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x003a
            int r0 = r5.f5253d
            int r1 = r1 - r0
            int r0 = java.lang.Math.abs(r1)
            int r1 = r5.w
            if (r0 <= r1) goto L_0x00fd
            boolean r0 = r5.f5251b
            if (r0 != 0) goto L_0x00fd
        L_0x003a:
            android.os.Handler r0 = r5.E
            java.lang.Runnable r1 = r5.F
            goto L_0x004a
        L_0x003f:
            android.os.Handler r0 = r5.E
            java.lang.Runnable r1 = r5.F
            r0.removeCallbacks(r1)
            android.os.Handler r0 = r5.E
            java.lang.Runnable r1 = r5.K
        L_0x004a:
            r0.removeCallbacks(r1)
            r5.d()
            goto L_0x00fd
        L_0x0052:
            float r0 = r6.getX()
            int r0 = (int) r0
            r5.f5252c = r0
            float r0 = r6.getY()
            int r0 = (int) r0
            r5.f5253d = r0
            int r0 = r5.f5252c
            int r2 = r5.f5253d
            int r0 = r5.pointToPosition(r0, r2)
            r5.h = r0
            int r0 = r5.h
            r5.g = r0
            int r2 = r5.g
            r3 = -1
            if (r2 == r3) goto L_0x0102
            int r2 = r5.D
            if (r0 != r2) goto L_0x0079
            goto L_0x0102
        L_0x0079:
            android.os.Handler r0 = r5.E
            java.lang.Runnable r2 = r5.F
            long r3 = r5.f5250a
            r0.postDelayed(r2, r3)
            int r0 = r5.g
            int r2 = r5.getFirstVisiblePosition()
            int r0 = r0 - r2
            android.view.View r0 = r5.getChildAt(r0)
            r5.i = r0
            int r0 = r5.f5253d
            android.view.View r2 = r5.i
            int r2 = r2.getTop()
            int r0 = r0 - r2
            r5.q = r0
            int r0 = r5.f5252c
            android.view.View r2 = r5.i
            int r2 = r2.getLeft()
            int r0 = r0 - r2
            r5.r = r0
            float r0 = r6.getRawY()
            int r2 = r5.f5253d
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            r5.s = r0
            float r0 = r6.getRawX()
            int r2 = r5.f5252c
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            r5.t = r0
            android.view.View r0 = r5.i
            r0.setDrawingCacheEnabled(r1)
            android.view.View r0 = r5.i
            android.graphics.Bitmap r0 = r0.getDrawingCache()
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0)
            r5.o = r0
            android.view.View r0 = r5.i
            android.graphics.Bitmap r0 = r0.getDrawingCache()
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0)
            r5.p = r0
            android.content.res.Resources r0 = r5.getResources()
            r1 = 2131099976(0x7f060148, float:1.781232E38)
            int r0 = r0.getColor(r1)
            android.graphics.Canvas r1 = new android.graphics.Canvas
            android.graphics.Bitmap r2 = r5.p
            r1.<init>(r2)
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.SRC_ATOP
            r1.drawColor(r0, r2)
            android.graphics.Bitmap r0 = r5.p
            int r1 = r5.f5252c
            int r2 = r5.f5253d
            r5.b(r0, r1, r2)
            android.view.View r0 = r5.i
            r0.destroyDrawingCache()
        L_0x00fd:
            boolean r6 = super.dispatchTouchEvent(r6)
            return r6
        L_0x0102:
            boolean r6 = super.dispatchTouchEvent(r6)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.view.DragGridView.dispatchTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        int i4;
        if (this.y == -1) {
            if (this.z > 0) {
                int max = Math.max((View.MeasureSpec.getSize(i2) - getPaddingLeft()) - getPaddingRight(), 0);
                i4 = max / this.z;
                if (i4 > 0) {
                    while (i4 != 1 && (this.z * i4) + ((i4 - 1) * this.B) > max) {
                        i4--;
                    }
                } else {
                    i4 = 1;
                }
            } else {
                i4 = 2;
            }
            this.y = i4;
        }
        super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(536870911, Integer.MIN_VALUE));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0013, code lost:
        if (r0 != 3) goto L_0x005a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            boolean r0 = r5.f5251b
            if (r0 == 0) goto L_0x005b
            android.widget.ImageView r0 = r5.j
            if (r0 == 0) goto L_0x005b
            int r0 = r6.getAction()
            r1 = 1
            if (r0 == r1) goto L_0x0054
            r2 = 2
            if (r0 == r2) goto L_0x0016
            r6 = 3
            if (r0 == r6) goto L_0x0054
            goto L_0x005a
        L_0x0016:
            float r0 = r6.getX()
            int r0 = (int) r0
            r5.e = r0
            float r6 = r6.getY()
            int r6 = (int) r6
            r5.f = r6
            int r6 = r5.e
            int r0 = r5.r
            int r2 = r5.getWidth()
            android.view.View r3 = r5.i
            int r3 = r3.getWidth()
            int r2 = r2 - r3
            int r3 = r5.r
            int r2 = r2 + r3
            int r6 = a((int) r6, (int) r0, (int) r2)
            int r0 = r5.f
            int r2 = r5.q
            int r3 = r5.getHeight()
            android.view.View r4 = r5.i
            int r4 = r4.getHeight()
            int r3 = r3 - r4
            int r4 = r5.q
            int r3 = r3 + r4
            int r0 = a((int) r0, (int) r2, (int) r3)
            r5.b((int) r6, (int) r0)
            goto L_0x005a
        L_0x0054:
            r5.a()
            r6 = 0
            r5.f5251b = r6
        L_0x005a:
            return r1
        L_0x005b:
            boolean r6 = super.onTouchEvent(r6)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.view.DragGridView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setAdapter(ListAdapter listAdapter) {
        super.setAdapter(listAdapter);
        if (listAdapter instanceof d) {
            this.x = (d) listAdapter;
            return;
        }
        throw new IllegalStateException("the adapter must be implements DragGridAdapter");
    }

    public void setColumnWidth(int i2) {
        super.setColumnWidth(i2);
        this.z = i2;
    }

    public void setDragResponseMS(long j2) {
        this.f5250a = j2;
    }

    public void setHorizontalSpacing(int i2) {
        super.setHorizontalSpacing(i2);
        this.B = i2;
    }

    public void setInvalidPosition(int i2) {
        Log.i("DragGridView", "position = " + i2);
        this.D = i2;
    }

    public void setNumColumns(int i2) {
        super.setNumColumns(i2);
        this.A = true;
        this.y = i2;
    }
}
