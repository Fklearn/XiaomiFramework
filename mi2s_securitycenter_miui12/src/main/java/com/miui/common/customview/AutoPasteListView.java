package com.miui.common.customview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;
import b.b.c.j.A;
import java.util.ArrayList;
import java.util.List;

public class AutoPasteListView extends ListView {

    /* renamed from: a  reason: collision with root package name */
    private Context f3768a;

    /* renamed from: b  reason: collision with root package name */
    private VelocityTracker f3769b;

    /* renamed from: c  reason: collision with root package name */
    private int f3770c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public int f3771d;
    private float e;
    /* access modifiers changed from: private */
    public int f;
    private int g;
    private int h;
    private boolean i;
    private int j;
    /* access modifiers changed from: private */
    public int k;
    private float l;
    private float m;
    private boolean n;
    private int o;
    /* access modifiers changed from: private */
    public ArrayList<Integer> p;
    private a q;
    /* access modifiers changed from: private */
    public AbsListView.OnScrollListener r;
    /* access modifiers changed from: private */
    public b s;
    /* access modifiers changed from: private */
    public boolean t;
    private boolean u;
    /* access modifiers changed from: private */
    public Handler v;

    private class a implements AbsListView.OnScrollListener {

        /* renamed from: a  reason: collision with root package name */
        private int f3772a;

        /* renamed from: b  reason: collision with root package name */
        private float f3773b;

        /* renamed from: c  reason: collision with root package name */
        private int f3774c;

        private a() {
            this.f3772a = 0;
            this.f3773b = -1.0f;
        }

        /* synthetic */ a(AutoPasteListView autoPasteListView, b bVar) {
            this();
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            if (AutoPasteListView.this.getChildCount() >= 1) {
                AutoPasteListView autoPasteListView = AutoPasteListView.this;
                View childAt = autoPasteListView.getChildAt(autoPasteListView.getChildCount() - 1);
                if (i2 + i == i3 && childAt != null && childAt.getBottom() == AutoPasteListView.this.getHeight()) {
                    boolean unused = AutoPasteListView.this.t = true;
                } else {
                    boolean unused2 = AutoPasteListView.this.t = false;
                }
                this.f3774c = absListView.getFirstVisiblePosition();
                this.f3772a = 0;
                if (this.f3774c <= AutoPasteListView.this.f3771d && AutoPasteListView.this.k != 0) {
                    int i4 = 0;
                    while (i4 < this.f3774c && i4 < AutoPasteListView.this.p.size()) {
                        this.f3772a -= ((Integer) AutoPasteListView.this.p.get(i4)).intValue() + AutoPasteListView.this.getDividerHeight();
                        i4++;
                    }
                    this.f3772a += AutoPasteListView.this.getChildAt(0).getTop() + AutoPasteListView.this.f;
                    float round = ((float) Math.round(((((float) Math.abs(this.f3772a)) * 1.0f) / ((float) AutoPasteListView.this.k)) * 100.0f)) / 100.0f;
                    if (round != this.f3773b) {
                        this.f3773b = round;
                        if (AutoPasteListView.this.s != null) {
                            AutoPasteListView.this.s.a(round);
                        }
                    }
                } else if (this.f3774c > AutoPasteListView.this.f3771d && this.f3773b < 1.0f) {
                    this.f3773b = 1.0f;
                    if (AutoPasteListView.this.s != null) {
                        AutoPasteListView.this.s.a(this.f3773b);
                    }
                }
                if (AutoPasteListView.this.r != null) {
                    AutoPasteListView.this.r.onScroll(absListView, i, i2, i3);
                }
            }
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            int firstVisiblePosition;
            if (AutoPasteListView.this.getChildCount() != 0) {
                if (i == 0 && (firstVisiblePosition = absListView.getFirstVisiblePosition()) <= AutoPasteListView.this.f3771d && AutoPasteListView.this.k != 0) {
                    int i2 = 0;
                    int i3 = 0;
                    while (i2 < firstVisiblePosition && i2 < AutoPasteListView.this.p.size()) {
                        i3 -= ((Integer) AutoPasteListView.this.p.get(i2)).intValue() + AutoPasteListView.this.getDividerHeight();
                        i2++;
                    }
                    int top = i3 + AutoPasteListView.this.getChildAt(0).getTop() + AutoPasteListView.this.f;
                    int b2 = AutoPasteListView.this.k;
                    Message obtainMessage = AutoPasteListView.this.v.obtainMessage();
                    obtainMessage.what = 104;
                    obtainMessage.arg1 = b2 - Math.abs(top);
                    obtainMessage.arg1 = (((float) Math.abs(top)) > ((float) b2) * 0.5f || AutoPasteListView.this.t) ? b2 - Math.abs(top) : -Math.abs(top);
                    AutoPasteListView.this.v.sendMessageAtFrontOfQueue(obtainMessage);
                }
                if (AutoPasteListView.this.r != null) {
                    AutoPasteListView.this.r.onScrollStateChanged(absListView, i);
                }
            }
        }
    }

    public interface b {
        void a(float f);
    }

    public AutoPasteListView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AutoPasteListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.e = 1.5f;
        this.f = 0;
        this.i = false;
        this.j = -100;
        this.n = false;
        this.p = new ArrayList<>();
        this.q = new a(this, (b) null);
        this.v = new b(this);
        a();
    }

    public AutoPasteListView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.e = 1.5f;
        this.f = 0;
        this.i = false;
        this.j = -100;
        this.n = false;
        this.p = new ArrayList<>();
        this.q = new a(this, (b) null);
        this.v = new b(this);
        a();
    }

    private void a() {
        this.f3768a = getContext();
        this.f3771d = 1;
        this.h = ViewConfiguration.get(this.f3768a).getScaledMaximumFlingVelocity();
        this.g = ViewConfiguration.get(this.f3768a).getScaledMinimumFlingVelocity();
        setSelector(new ColorDrawable(0));
        super.setOnScrollListener(this.q);
    }

    public void a(int i2) {
        if (!this.i) {
            smoothScrollBy(i2, Math.min(1000, Math.max(400, (int) (((float) Math.abs(i2)) * this.e))));
        }
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (A.a()) {
            try {
                d.a.b.a((AbsListView) this, motionEvent);
            } catch (Throwable unused) {
                Log.e("AutoPasteListView", "no support folme");
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public int getAlignHeight() {
        return this.k;
    }

    public float getFirstY() {
        return this.m;
    }

    public ArrayList<Integer> getItemHeightList() {
        return this.p;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (getChildCount() == 0) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        if (motionEvent.getAction() == 0) {
            this.i = true;
            this.l = motionEvent.getRawY();
            this.m = motionEvent.getY();
            int i2 = 0;
            this.f3770c = motionEvent.getPointerId(0);
            getChildAt(0).getTop();
            int i3 = this.f;
            if (getFirstVisiblePosition() == 0) {
                this.p.clear();
                this.k = 0;
                while (i2 < this.f3771d + 1 && i2 < getChildCount()) {
                    int height = getChildAt(i2).getHeight();
                    this.p.add(Integer.valueOf(height));
                    this.k += height + getDividerHeight();
                    i2++;
                }
            }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0126, code lost:
        if (r7.u == false) goto L_0x010a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0161, code lost:
        if (r7.u == false) goto L_0x014c;
     */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x016f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            int r0 = r7.j
            r1 = -100
            if (r0 == r1) goto L_0x0009
            r7.setOverScrollMode(r0)
        L_0x0009:
            int r0 = r7.getChildCount()
            if (r0 != 0) goto L_0x0014
            boolean r8 = super.onTouchEvent(r8)
            return r8
        L_0x0014:
            boolean r0 = r7.n
            r1 = 0
            if (r0 != 0) goto L_0x0044
            android.view.View r0 = r7.getChildAt(r1)
            int r2 = r0.getHeight()
            int r3 = r7.getFirstVisiblePosition()
            if (r3 != 0) goto L_0x0044
            float r3 = r8.getRawY()
            int r4 = r7.o
            float r4 = (float) r4
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x0044
            float r3 = r8.getRawY()
            int r0 = r0.getTop()
            int r2 = r2 + r0
            int r0 = r7.o
            int r2 = r2 + r0
            float r0 = (float) r2
            int r0 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x0044
            return r1
        L_0x0044:
            int r0 = r7.getFirstVisiblePosition()
            android.view.View r2 = r7.getChildAt(r0)
            if (r0 != 0) goto L_0x005b
            if (r2 == 0) goto L_0x005b
            int r0 = r2.getTop()
            if (r0 != 0) goto L_0x005b
            boolean r8 = super.onTouchEvent(r8)
            return r8
        L_0x005b:
            int r0 = r7.getChildCount()
            r2 = 1
            int r0 = r0 - r2
            android.view.View r0 = r7.getChildAt(r0)
            if (r0 == 0) goto L_0x0076
            int r0 = r0.getBottom()
            int r3 = r7.getHeight()
            if (r0 != r3) goto L_0x0076
            boolean r8 = super.onTouchEvent(r8)
            return r8
        L_0x0076:
            android.view.VelocityTracker r0 = r7.f3769b
            if (r0 != 0) goto L_0x0080
            android.view.VelocityTracker r0 = android.view.VelocityTracker.obtain()
            r7.f3769b = r0
        L_0x0080:
            android.view.VelocityTracker r0 = r7.f3769b
            r0.addMovement(r8)
            int r0 = r8.getAction()
            if (r0 == 0) goto L_0x01b7
            if (r0 == r2) goto L_0x0092
            r2 = 3
            if (r0 == r2) goto L_0x0092
            goto L_0x01b7
        L_0x0092:
            r7.i = r1
            int r0 = r7.getOverScrollMode()
            r7.j = r0
            int r0 = r7.j
            if (r0 != 0) goto L_0x00a2
            r0 = 2
            r7.setOverScrollMode(r0)
        L_0x00a2:
            android.view.VelocityTracker r0 = r7.f3769b
            r2 = 1000(0x3e8, float:1.401E-42)
            int r3 = r7.h
            float r3 = (float) r3
            r0.computeCurrentVelocity(r2, r3)
            int r2 = r7.f3770c
            float r0 = r0.getYVelocity(r2)
            int r2 = r7.getFirstVisiblePosition()
            int r3 = r7.f3771d
            if (r2 > r3) goto L_0x01a2
            int r3 = r7.k
            if (r3 == 0) goto L_0x01a2
            r3 = r1
            r4 = r3
        L_0x00c0:
            if (r3 >= r2) goto L_0x00df
            java.util.ArrayList<java.lang.Integer> r5 = r7.p
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x00df
            java.util.ArrayList<java.lang.Integer> r5 = r7.p
            java.lang.Object r5 = r5.get(r3)
            java.lang.Integer r5 = (java.lang.Integer) r5
            int r5 = r5.intValue()
            int r6 = r7.getDividerHeight()
            int r5 = r5 + r6
            int r4 = r4 - r5
            int r3 = r3 + 1
            goto L_0x00c0
        L_0x00df:
            android.view.View r1 = r7.getChildAt(r1)
            int r1 = r1.getTop()
            int r2 = r7.f
            int r1 = r1 + r2
            int r4 = r4 + r1
            int r1 = r7.k
            r2 = -994344960(0xffffffffc4bb8000, float:-1500.0)
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            r5 = 1017370378(0x3ca3d70a, float:0.02)
            if (r3 <= 0) goto L_0x011a
            int r3 = r7.g
            int r3 = -r3
            float r3 = (float) r3
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x011a
            int r2 = java.lang.Math.abs(r4)
            float r2 = (float) r2
            float r3 = (float) r1
            float r3 = r3 * r5
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0111
        L_0x010a:
            int r2 = java.lang.Math.abs(r4)
            int r2 = r1 - r2
            goto L_0x0116
        L_0x0111:
            int r2 = java.lang.Math.abs(r4)
            int r2 = -r2
        L_0x0116:
            r7.a((int) r2)
            goto L_0x0129
        L_0x011a:
            r3 = -822083584(0xffffffffcf000000, float:-2.14748365E9)
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x0129
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x0129
            boolean r2 = r7.u
            if (r2 != 0) goto L_0x0129
            goto L_0x010a
        L_0x0129:
            r2 = 1153138688(0x44bb8000, float:1500.0)
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            r6 = 1065017672(0x3f7ae148, float:0.98)
            if (r3 >= 0) goto L_0x0155
            int r3 = r7.g
            float r3 = (float) r3
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x0155
            int r2 = java.lang.Math.abs(r4)
            float r2 = (float) r2
            float r3 = (float) r1
            float r3 = r3 * r6
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x014c
            int r2 = java.lang.Math.abs(r4)
            int r2 = r1 - r2
            goto L_0x0151
        L_0x014c:
            int r2 = java.lang.Math.abs(r4)
            int r2 = -r2
        L_0x0151:
            r7.a((int) r2)
            goto L_0x0164
        L_0x0155:
            r3 = 1325400064(0x4f000000, float:2.14748365E9)
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x0164
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x0164
            boolean r2 = r7.u
            if (r2 != 0) goto L_0x0164
            goto L_0x014c
        L_0x0164:
            float r0 = java.lang.Math.abs(r0)
            int r2 = r7.g
            float r2 = (float) r2
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x01a8
            float r0 = r8.getRawY()
            float r2 = r7.l
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0185
            int r0 = java.lang.Math.abs(r4)
            float r0 = (float) r0
            float r2 = (float) r1
            float r2 = r2 * r5
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x0199
            goto L_0x0190
        L_0x0185:
            int r0 = java.lang.Math.abs(r4)
            float r0 = (float) r0
            float r2 = (float) r1
            float r2 = r2 * r6
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x0199
        L_0x0190:
            int r0 = java.lang.Math.abs(r4)
            int r1 = r1 - r0
            r7.a((int) r1)
            goto L_0x01a8
        L_0x0199:
            int r0 = java.lang.Math.abs(r4)
            int r0 = -r0
            r7.a((int) r0)
            goto L_0x01a8
        L_0x01a2:
            int r0 = r7.f3771d
            if (r2 > r0) goto L_0x01a8
            int r0 = r7.k
        L_0x01a8:
            android.view.VelocityTracker r0 = r7.f3769b
            if (r0 == 0) goto L_0x01b7
            r0.clear()
            android.view.VelocityTracker r0 = r7.f3769b
            r0.recycle()
            r0 = 0
            r7.f3769b = r0
        L_0x01b7:
            boolean r8 = super.onTouchEvent(r8)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.customview.AutoPasteListView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setAlignHeight(int i2) {
        this.k = i2;
    }

    public void setAlignItem(int i2) {
        this.f3771d = i2;
    }

    public void setHeavySlideNoAnim(boolean z) {
        this.u = z;
    }

    public void setItemHeightList(List<Integer> list) {
        this.p.clear();
        this.p.addAll(list);
    }

    public void setMarginTopPixel(int i2) {
        this.o = i2;
    }

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        this.r = onScrollListener;
    }

    public void setOnScrollPercentChangeListener(b bVar) {
        this.s = bVar;
    }

    public void setTopDraggable(boolean z) {
        this.n = z;
    }
}
