package com.miui.common.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.miui.securitycenter.R;
import com.miui.securitycenter.i;

public class MovableLayout extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private View f3781a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public View f3782b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f3783c;

    /* renamed from: d  reason: collision with root package name */
    private int f3784d;
    /* access modifiers changed from: private */
    public int e;
    private int f;
    /* access modifiers changed from: private */
    public int g;
    private int h;
    /* access modifiers changed from: private */
    public ViewDragHelper i;
    /* access modifiers changed from: private */
    public int j;
    private b k;

    private class a extends ViewDragHelper.Callback {
        private a() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:5:0x002f, code lost:
            if (java.lang.Math.abs(r2) > (com.miui.common.customview.MovableLayout.g(r1.f3785a) / 2)) goto L_0x000b;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private int a(int r2) {
            /*
                r1 = this;
                if (r2 <= 0) goto L_0x0009
            L_0x0002:
                com.miui.common.customview.MovableLayout r2 = com.miui.common.customview.MovableLayout.this
                int r2 = r2.getContentMaximumTop()
                goto L_0x0032
            L_0x0009:
                if (r2 >= 0) goto L_0x0012
            L_0x000b:
                com.miui.common.customview.MovableLayout r2 = com.miui.common.customview.MovableLayout.this
                int r2 = r2.getContentMinimumTop()
                goto L_0x0032
            L_0x0012:
                com.miui.common.customview.MovableLayout r2 = com.miui.common.customview.MovableLayout.this
                int r2 = r2.g
                com.miui.common.customview.MovableLayout r0 = com.miui.common.customview.MovableLayout.this
                android.view.View r0 = r0.f3782b
                int r0 = r0.getTop()
                int r2 = r2 - r0
                com.miui.common.customview.MovableLayout r0 = com.miui.common.customview.MovableLayout.this
                int r0 = r0.e
                int r0 = r0 / 2
                int r2 = java.lang.Math.abs(r2)
                if (r2 <= r0) goto L_0x0002
                goto L_0x000b
            L_0x0032:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.common.customview.MovableLayout.a.a(int):int");
        }

        public int clampViewPositionVertical(View view, int i, int i2) {
            int e = MovableLayout.this.getContentMinimumTop();
            return Math.min(Math.max(i, e), MovableLayout.this.g);
        }

        public int getViewVerticalDragRange(View view) {
            return MovableLayout.this.e;
        }

        public void onViewDragStateChanged(int i) {
            if (MovableLayout.this.j != i) {
                if (i == 0) {
                    MovableLayout.this.b();
                    int unused = MovableLayout.this.j = i;
                } else if (1 == i || 2 == i) {
                    if (MovableLayout.this.j == 0) {
                        MovableLayout.this.a();
                    }
                    int unused2 = MovableLayout.this.j = i;
                }
            }
        }

        public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
            MovableLayout.this.a(i2);
            MovableLayout.this.postInvalidate();
        }

        public void onViewReleased(View view, float f, float f2) {
            MovableLayout.this.i.smoothSlideViewTo(MovableLayout.this.f3782b, view.getLeft(), a((int) f2));
            MovableLayout.this.postInvalidate();
        }

        public boolean tryCaptureView(View view, int i) {
            return view == MovableLayout.this.f3782b;
        }
    }

    public interface b {
        boolean onContentScrolled();

        void onScroll(int i, float f);

        void onStartScroll();

        void onStopScroll();
    }

    public MovableLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public MovableLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MovableLayout(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f3784d = 0;
        this.e = 0;
        this.f = 0;
        this.j = 0;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.MovableLayout);
        this.g = obtainStyledAttributes.getDimensionPixelSize(0, 0);
        this.f = obtainStyledAttributes.getDimensionPixelSize(1, 0);
        this.h = this.g - this.f;
        obtainStyledAttributes.recycle();
        this.i = ViewDragHelper.create(this, 1.0f, new a());
    }

    /* access modifiers changed from: private */
    public void a(int i2) {
        b bVar = this.k;
        if (bVar != null) {
            int i3 = this.e;
            bVar.onScroll(this.i.getViewDragState(), ((float) (i3 - (this.g - i2))) / ((float) i3));
        }
    }

    private void c() {
        this.f3781a = findViewById(R.id.header_container);
        this.f3782b = findViewById(R.id.content_container);
    }

    private boolean d() {
        int i2 = this.j;
        return i2 == 1 || i2 == 2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000c, code lost:
        r0 = r2.k;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean e() {
        /*
            r2 = this;
            android.view.View r0 = r2.f3782b
            int r0 = r0.getTop()
            int r1 = r2.getContentMinimumTop()
            if (r0 < r1) goto L_0x0018
            com.miui.common.customview.MovableLayout$b r0 = r2.k
            if (r0 == 0) goto L_0x0018
            boolean r0 = r0.onContentScrolled()
            if (r0 == 0) goto L_0x0018
            r0 = 1
            return r0
        L_0x0018:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.customview.MovableLayout.e():boolean");
    }

    /* access modifiers changed from: private */
    public int getContentMaximumTop() {
        return this.g;
    }

    /* access modifiers changed from: private */
    public int getContentMinimumTop() {
        return this.h;
    }

    /* access modifiers changed from: protected */
    public void a() {
        b bVar = this.k;
        if (bVar != null) {
            bVar.onStartScroll();
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x008c  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0091  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(int r11, int r12, int r13, int r14, boolean r15) {
        /*
            r10 = this;
            int r0 = r10.getChildCount()
            int r1 = r10.getPaddingLeft()
            int r13 = r13 - r11
            int r11 = r10.getPaddingRight()
            int r13 = r13 - r11
            int r11 = r10.getPaddingTop()
            int r14 = r14 - r12
            int r12 = r10.getPaddingBottom()
            int r14 = r14 - r12
            r12 = 0
        L_0x0019:
            if (r12 >= r0) goto L_0x009e
            android.view.View r2 = r10.getChildAt(r12)
            int r3 = r2.getVisibility()
            r4 = 8
            if (r3 == r4) goto L_0x009a
            android.view.ViewGroup$LayoutParams r3 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            int r4 = r2.getMeasuredWidth()
            int r5 = r2.getMeasuredHeight()
            int r6 = r3.gravity
            int r7 = r10.getLayoutDirection()
            int r7 = android.view.Gravity.getAbsoluteGravity(r6, r7)
            r6 = r6 & 112(0x70, float:1.57E-43)
            r7 = r7 & 7
            r8 = 1
            if (r7 == r8) goto L_0x0053
            r9 = 5
            if (r7 == r9) goto L_0x004a
            goto L_0x004f
        L_0x004a:
            if (r15 != 0) goto L_0x004f
            int r7 = r13 - r4
            goto L_0x005c
        L_0x004f:
            int r7 = r3.leftMargin
            int r7 = r7 + r1
            goto L_0x005f
        L_0x0053:
            int r7 = r13 - r1
            int r7 = r7 - r4
            int r7 = r7 / 2
            int r7 = r7 + r1
            int r9 = r3.leftMargin
            int r7 = r7 + r9
        L_0x005c:
            int r9 = r3.rightMargin
            int r7 = r7 - r9
        L_0x005f:
            r9 = 16
            if (r6 == r9) goto L_0x0072
            r9 = 48
            if (r6 == r9) goto L_0x006b
            r9 = 80
            if (r6 == r9) goto L_0x006f
        L_0x006b:
            int r3 = r3.topMargin
            int r3 = r3 + r11
            goto L_0x007f
        L_0x006f:
            int r6 = r14 - r5
            goto L_0x007b
        L_0x0072:
            int r6 = r14 - r11
            int r6 = r6 - r5
            int r6 = r6 / 2
            int r6 = r6 + r11
            int r9 = r3.topMargin
            int r6 = r6 + r9
        L_0x007b:
            int r3 = r3.bottomMargin
            int r3 = r6 - r3
        L_0x007f:
            android.view.View r6 = r10.f3782b
            if (r2 != r6) goto L_0x0095
            int r3 = r10.f3784d
            if (r3 == 0) goto L_0x0088
            goto L_0x0095
        L_0x0088:
            boolean r3 = r10.f3783c
            if (r3 == 0) goto L_0x0091
            int r3 = r6.getTop()
            goto L_0x0095
        L_0x0091:
            int r3 = r10.g
            r10.f3783c = r8
        L_0x0095:
            int r4 = r4 + r7
            int r5 = r5 + r3
            r2.layout(r7, r3, r4, r5)
        L_0x009a:
            int r12 = r12 + 1
            goto L_0x0019
        L_0x009e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.customview.MovableLayout.a(int, int, int, int, boolean):void");
    }

    /* access modifiers changed from: protected */
    public void b() {
        b bVar = this.k;
        if (bVar != null) {
            bVar.onStopScroll();
        }
    }

    public void computeScroll() {
        if (this.i.continueSettling(true)) {
            postInvalidateOnAnimation();
        }
    }

    public int getContentContainerMinTop() {
        return getContentMinimumTop();
    }

    /* access modifiers changed from: protected */
    public void measureChildWithMargins(View view, int i2, int i3, int i4, int i5) {
        int childMeasureSpec;
        int paddingTop;
        int i6;
        if (view == this.f3781a && this.g > 0) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            childMeasureSpec = FrameLayout.getChildMeasureSpec(i2, getPaddingLeft() + getPaddingRight() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin + i3, marginLayoutParams.width);
            paddingTop = getPaddingTop() + getPaddingBottom() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin + i5;
            i6 = this.g;
        } else if (view == this.f3782b) {
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            childMeasureSpec = FrameLayout.getChildMeasureSpec(i2, getPaddingLeft() + getPaddingRight() + marginLayoutParams2.leftMargin + marginLayoutParams2.rightMargin + i3, marginLayoutParams2.width);
            paddingTop = getPaddingTop() + getPaddingBottom() + marginLayoutParams2.bottomMargin + this.h;
            i6 = marginLayoutParams2.height;
        } else {
            super.measureChildWithMargins(view, i2, i3, i4, i5);
            return;
        }
        view.measure(childMeasureSpec, FrameLayout.getChildMeasureSpec(i4, paddingTop, i6));
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        c();
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (e() || !this.i.shouldInterceptTouchEvent(motionEvent)) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i2, int i3, int i4, int i5) {
        a(i2, i3, i4, i5, false);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!d() && motionEvent.getActionMasked() != 0) {
            return super.onTouchEvent(motionEvent);
        }
        this.i.processTouchEvent(motionEvent);
        return true;
    }

    public void setContentContainerTop(int i2) {
        this.f3784d = i2;
        requestLayout();
    }

    public void setOffsetForNotch(int i2) {
        this.g += i2;
        this.h = this.g - this.f;
        invalidate();
    }

    public void setScrollListener(b bVar) {
        this.k = bVar;
    }

    public void setScrollable(boolean z) {
        this.e = z ? this.f : 0;
    }
}
