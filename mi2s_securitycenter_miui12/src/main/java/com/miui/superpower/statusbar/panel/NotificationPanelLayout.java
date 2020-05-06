package com.miui.superpower.statusbar.panel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextClock;
import com.miui.gamebooster.globalgame.view.RoundedDrawable;
import com.miui.securitycenter.R;
import com.miui.superpower.b.j;
import com.miui.superpower.statusbar.button.CellularButton;
import com.miui.superpower.statusbar.panel.d;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationPanelLayout extends ViewGroup {

    /* renamed from: a  reason: collision with root package name */
    private static final e f8198a = e.COLLAPSED;

    /* renamed from: b  reason: collision with root package name */
    private static final int[] f8199b = {16842927};

    /* renamed from: c  reason: collision with root package name */
    private int f8200c;

    /* renamed from: d  reason: collision with root package name */
    private final Paint f8201d;
    private int e;
    /* access modifiers changed from: private */
    public boolean f;
    /* access modifiers changed from: private */
    public View g;
    private e h;
    private e i;
    /* access modifiers changed from: private */
    public float j;
    /* access modifiers changed from: private */
    public int k;
    /* access modifiers changed from: private */
    public boolean l;
    private float m;
    private float n;
    private final List<d> o;
    /* access modifiers changed from: private */
    public final d p;
    private CellularButton q;
    private b r;
    private boolean s;
    private final Rect t;

    private class a extends d.a {
        private a() {
        }

        /* synthetic */ a(NotificationPanelLayout notificationPanelLayout, a aVar) {
            this();
        }

        public int a(View view) {
            return NotificationPanelLayout.this.k;
        }

        public int a(View view, int i, int i2) {
            int a2 = NotificationPanelLayout.this.b(0.0f);
            int a3 = NotificationPanelLayout.this.b(1.0f);
            return NotificationPanelLayout.this.f ? Math.min(Math.max(i, a3), a2) : Math.min(Math.max(i, a2), a3);
        }

        public void a(int i) {
            NotificationPanelLayout notificationPanelLayout;
            e eVar;
            if (NotificationPanelLayout.this.p != null && NotificationPanelLayout.this.p.d() == 0) {
                NotificationPanelLayout notificationPanelLayout2 = NotificationPanelLayout.this;
                float unused = notificationPanelLayout2.j = notificationPanelLayout2.a(notificationPanelLayout2.g.getTop());
                if (NotificationPanelLayout.this.j == 1.0f) {
                    notificationPanelLayout = NotificationPanelLayout.this;
                    eVar = e.EXPANDED;
                } else if (NotificationPanelLayout.this.j < 1.0f) {
                    notificationPanelLayout = NotificationPanelLayout.this;
                    eVar = e.COLLAPSED;
                } else {
                    return;
                }
                notificationPanelLayout.setPanelStateInternal(eVar);
            }
        }

        public void a(View view, float f, float f2) {
            if (NotificationPanelLayout.this.f) {
                f2 = -f2;
            }
            int a2 = (f2 <= 0.0f && (f2 < 0.0f || NotificationPanelLayout.this.j < 0.5f)) ? NotificationPanelLayout.this.b(0.0f) : NotificationPanelLayout.this.b(1.0f);
            if (NotificationPanelLayout.this.p != null) {
                NotificationPanelLayout.this.p.a(view.getLeft(), a2);
            }
            NotificationPanelLayout.this.invalidate();
        }

        public void a(View view, int i) {
            NotificationPanelLayout.this.e();
        }

        public void a(View view, int i, int i2, int i3, int i4) {
            NotificationPanelLayout.this.b(i2);
            NotificationPanelLayout.this.invalidate();
        }

        public boolean b(View view, int i) {
            return !NotificationPanelLayout.this.l && view == NotificationPanelLayout.this.g;
        }
    }

    private class b extends BroadcastReceiver {

        /* renamed from: a  reason: collision with root package name */
        private Context f8203a;

        public b(Context context) {
            this.f8203a = context;
        }

        /* access modifiers changed from: private */
        public void a() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
            this.f8203a.registerReceiver(this, intentFilter);
        }

        /* access modifiers changed from: private */
        public void b() {
            this.f8203a.unregisterReceiver(this);
        }

        public void onReceive(Context context, Intent intent) {
            String stringExtra;
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals("android.intent.action.CLOSE_SYSTEM_DIALOGS") && (stringExtra = intent.getStringExtra("reason")) != null) {
                if (stringExtra.equals("homekey") || stringExtra.equals("recentapps")) {
                    NotificationPanelLayout.this.a();
                }
            }
        }
    }

    public static class c extends ViewGroup.MarginLayoutParams {

        /* renamed from: a  reason: collision with root package name */
        private static final int[] f8205a = {16843137};

        /* renamed from: b  reason: collision with root package name */
        public float f8206b = 0.0f;

        public c() {
            super(-1, -1);
        }

        public c(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, f8205a);
            if (obtainStyledAttributes != null) {
                this.f8206b = obtainStyledAttributes.getFloat(0, 0.0f);
                obtainStyledAttributes.recycle();
            }
        }

        public c(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public c(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }
    }

    public interface d {
        void a(View view, e eVar, e eVar2);

        void onPanelSlide(View view, float f);
    }

    public enum e {
        EXPANDED,
        COLLAPSED,
        HIDDEN,
        DRAGGING
    }

    public static class f implements d {
        public void onPanelSlide(View view, float f) {
        }
    }

    public NotificationPanelLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public NotificationPanelLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NotificationPanelLayout(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        TypedArray obtainStyledAttributes;
        this.f8200c = RoundedDrawable.DEFAULT_BORDER_COLOR;
        this.f8201d = new Paint();
        this.e = -1;
        e eVar = f8198a;
        this.h = eVar;
        this.i = eVar;
        this.o = new CopyOnWriteArrayList();
        this.s = true;
        this.t = new Rect();
        if (isInEditMode()) {
            this.p = null;
            return;
        }
        if (!(attributeSet == null || (obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, f8199b)) == null)) {
            setGravity(obtainStyledAttributes.getInt(0, 0));
            obtainStyledAttributes.recycle();
        }
        this.h = f8198a;
        this.f8200c = RoundedDrawable.DEFAULT_BORDER_COLOR;
        this.e = (int) ((context.getResources().getDisplayMetrics().density * 24.0f) + 0.5f);
        setWillNotDraw(false);
        this.p = d.a((ViewGroup) this, 0.5f, (d.a) new a(this, (a) null));
        this.p.a((float) ViewConfiguration.get(context).getScaledMinimumFlingVelocity());
        this.r = new b(context);
    }

    /* access modifiers changed from: private */
    public float a(int i2) {
        int b2 = b(0.0f);
        return (this.f ? (float) (b2 - i2) : (float) (i2 - b2)) / ((float) this.k);
    }

    /* access modifiers changed from: private */
    public int b(float f2) {
        View view = this.g;
        int measuredHeight = view != null ? view.getMeasuredHeight() : 0;
        int i2 = (int) (f2 * ((float) this.k));
        return this.f ? ((getMeasuredHeight() - getPaddingBottom()) - this.e) - i2 : (getPaddingTop() - measuredHeight) + this.e + i2;
    }

    /* access modifiers changed from: private */
    public void b(int i2) {
        e eVar = this.h;
        if (eVar != e.DRAGGING) {
            this.i = eVar;
        }
        setPanelStateInternal(this.h == e.EXPANDED ? e.COLLAPSED : e.DRAGGING);
        this.j = a(i2);
        a(this.g);
    }

    /* access modifiers changed from: private */
    public void e() {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt.getVisibility() == 4) {
                childAt.setVisibility(0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setPanelStateInternal(e eVar) {
        e eVar2 = this.h;
        if (eVar2 != eVar) {
            this.h = eVar;
            a(this, eVar2, eVar);
        }
    }

    public void a() {
        if (getPanelState() != e.DRAGGING) {
            setPanelState(e.COLLAPSED);
        }
    }

    /* access modifiers changed from: package-private */
    public void a(float f2) {
        if (isEnabled() && this.g != null) {
            int b2 = b(f2);
            d dVar = this.p;
            View view = this.g;
            if (dVar.a(view, view.getLeft(), b2)) {
                e();
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(View view) {
        synchronized (this.o) {
            for (d onPanelSlide : this.o) {
                onPanelSlide.onPanelSlide(view, this.j);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(View view, e eVar, e eVar2) {
        synchronized (this.o) {
            for (d a2 : this.o) {
                a2.a(view, eVar, eVar2);
            }
        }
        sendAccessibilityEvent(32);
    }

    public void a(d dVar) {
        synchronized (this.o) {
            this.o.add(dVar);
        }
    }

    public boolean b() {
        return (this.g == null || this.h == e.HIDDEN) ? false : true;
    }

    /* access modifiers changed from: protected */
    public void c() {
        a(0.0f);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return (layoutParams instanceof c) && super.checkLayoutParams(layoutParams);
    }

    public void computeScroll() {
        d dVar = this.p;
        if (dVar != null && dVar.a(true)) {
            if (!isEnabled()) {
                this.p.a();
            } else {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    public void d() {
        CellularButton cellularButton = this.q;
        if (cellularButton != null) {
            cellularButton.b();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() != 4 || keyEvent.getAction() == 1) {
            return super.dispatchKeyEvent(keyEvent);
        }
        a();
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        if (isEnabled() && b() && (!this.l || actionMasked == 0)) {
            return super.dispatchTouchEvent(motionEvent);
        }
        this.p.a();
        return super.dispatchTouchEvent(motionEvent);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j2) {
        boolean z;
        int save = canvas.save();
        View view2 = this.g;
        if (view2 == null || view2 == view) {
            z = super.drawChild(canvas, view, j2);
        } else {
            canvas.getClipBounds(this.t);
            canvas.clipRect(this.t);
            z = super.drawChild(canvas, view, j2);
            int i2 = this.f8200c;
            if (i2 != 0) {
                float f2 = this.j;
                if (f2 > 0.0f) {
                    this.f8201d.setColor((i2 & 16777215) | (((int) (((float) ((-16777216 & i2) >>> 24)) * f2)) << 24));
                    canvas.drawRect(this.t, this.f8201d);
                }
            }
        }
        canvas.restoreToCount(save);
        return z;
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new c();
    }

    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new c(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof ViewGroup.MarginLayoutParams ? new c((ViewGroup.MarginLayoutParams) layoutParams) : new c(layoutParams);
    }

    public int getPanelHeight() {
        return this.e;
    }

    public e getPanelState() {
        return this.h;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.s = true;
        this.q = (CellularButton) findViewById(R.id.button_cellular);
        ((TextClock) findViewById(R.id.time)).setTypeface(j.b(getContext().getApplicationContext()));
        this.r.a();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.s = true;
        this.r.b();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0038, code lost:
        if (r0 != 3) goto L_0x0060;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            boolean r0 = r7.b()
            r1 = 0
            if (r0 != 0) goto L_0x000d
            com.miui.superpower.statusbar.panel.d r8 = r7.p
            r8.a()
            return r1
        L_0x000d:
            int r0 = android.support.v4.view.MotionEventCompat.getActionMasked(r8)
            float r2 = r8.getX()
            float r3 = r8.getY()
            float r4 = r7.m
            float r4 = r2 - r4
            float r4 = java.lang.Math.abs(r4)
            float r5 = r7.n
            float r5 = r3 - r5
            float r5 = java.lang.Math.abs(r5)
            com.miui.superpower.statusbar.panel.d r6 = r7.p
            int r6 = r6.c()
            if (r0 == 0) goto L_0x005a
            r2 = 1
            if (r0 == r2) goto L_0x004c
            r3 = 2
            if (r0 == r3) goto L_0x003b
            r1 = 3
            if (r0 == r1) goto L_0x004c
            goto L_0x0060
        L_0x003b:
            float r0 = (float) r6
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0060
            int r0 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x0060
            com.miui.superpower.statusbar.panel.d r8 = r7.p
            r8.b()
            r7.l = r2
            return r1
        L_0x004c:
            com.miui.superpower.statusbar.panel.d r0 = r7.p
            boolean r0 = r0.e()
            if (r0 == 0) goto L_0x0060
            com.miui.superpower.statusbar.panel.d r0 = r7.p
            r0.a((android.view.MotionEvent) r8)
            return r2
        L_0x005a:
            r7.l = r1
            r7.m = r2
            r7.n = r3
        L_0x0060:
            com.miui.superpower.statusbar.panel.d r0 = r7.p
            boolean r8 = r0.b((android.view.MotionEvent) r8)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.superpower.statusbar.panel.NotificationPanelLayout.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i2, int i3, int i4, int i5) {
        float f2;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int childCount = getChildCount();
        if (this.s) {
            int i6 = a.f8211a[this.h.ordinal()];
            if (i6 == 1) {
                f2 = 1.0f;
            } else if (i6 != 2) {
                this.j = 0.0f;
            } else {
                f2 = a(b(0.0f) + (this.f ? this.e : -this.e));
            }
            this.j = f2;
        }
        for (int i7 = 0; i7 < childCount; i7++) {
            View childAt = getChildAt(i7);
            c cVar = (c) childAt.getLayoutParams();
            if (childAt.getVisibility() != 8 || (i7 != 0 && !this.s)) {
                int measuredHeight = childAt.getMeasuredHeight();
                int b2 = childAt == this.g ? b(this.j) : paddingTop;
                int i8 = cVar.leftMargin + paddingLeft;
                childAt.layout(i8, b2, childAt.getMeasuredWidth() + i8, measuredHeight + b2);
            }
        }
        this.s = false;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int mode = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i2);
        int mode2 = View.MeasureSpec.getMode(i3);
        int size2 = View.MeasureSpec.getSize(i3);
        if (mode != 1073741824 && mode != Integer.MIN_VALUE) {
            throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
        } else if (mode2 == 1073741824 || mode2 == Integer.MIN_VALUE) {
            int childCount = getChildCount();
            if (childCount == 2) {
                View childAt = getChildAt(0);
                this.g = getChildAt(1);
                int paddingTop = (size2 - getPaddingTop()) - getPaddingBottom();
                int paddingLeft = (size - getPaddingLeft()) - getPaddingRight();
                for (int i9 = 0; i9 < childCount; i9++) {
                    View childAt2 = getChildAt(i9);
                    c cVar = (c) childAt2.getLayoutParams();
                    if (childAt2.getVisibility() != 8 || i9 != 0) {
                        if (childAt2 == childAt) {
                            i5 = paddingLeft - (cVar.leftMargin + cVar.rightMargin);
                            i4 = paddingTop;
                        } else {
                            i4 = childAt2 == this.g ? paddingTop - cVar.topMargin : paddingTop;
                            i5 = paddingLeft;
                        }
                        if (cVar.width == -2) {
                            i6 = View.MeasureSpec.makeMeasureSpec(i5, Integer.MIN_VALUE);
                        } else {
                            if (cVar.width == -1) {
                                i8 = 1073741824;
                            } else {
                                i8 = 1073741824;
                                i5 = cVar.width;
                            }
                            i6 = View.MeasureSpec.makeMeasureSpec(i5, i8);
                        }
                        if (cVar.height == -2) {
                            i7 = View.MeasureSpec.makeMeasureSpec(i4, Integer.MIN_VALUE);
                        } else {
                            float f2 = cVar.f8206b;
                            if (f2 > 0.0f && f2 < 1.0f) {
                                i4 = (int) (((float) i4) * f2);
                            } else if (cVar.height != -1) {
                                i4 = cVar.height;
                            }
                            i7 = View.MeasureSpec.makeMeasureSpec(i4, 1073741824);
                        }
                        childAt2.measure(i6, i7);
                        View view = this.g;
                        if (childAt2 == view) {
                            this.k = view.getMeasuredHeight() - this.e;
                        }
                    }
                }
                setMeasuredDimension(size, size2);
                return;
            }
            throw new IllegalStateException("Sliding up panel layout must have exactly 2 children!");
        } else {
            throw new IllegalStateException("Height must have an exact value or MATCH_PARENT");
        }
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle) parcelable;
            this.h = (e) bundle.getSerializable("sliding_state");
            e eVar = this.h;
            if (eVar == null) {
                eVar = f8198a;
            }
            this.h = eVar;
            parcelable = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(parcelable);
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        e eVar = this.h;
        if (eVar == e.DRAGGING) {
            eVar = this.i;
        }
        bundle.putSerializable("sliding_state", eVar);
        return bundle;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        if (i3 != i5) {
            this.s = true;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled() || !b()) {
            return super.onTouchEvent(motionEvent);
        }
        try {
            this.p.a(motionEvent);
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public void setCoveredFadeColor(int i2) {
        this.f8200c = i2;
        requestLayout();
    }

    public void setGravity(int i2) {
        if (i2 == 48 || i2 == 80) {
            this.f = i2 == 80;
            if (!this.s) {
                requestLayout();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("gravity must be set to either top or bottom");
    }

    public void setPanelHeight(int i2) {
        if (getPanelHeight() != i2) {
            this.e = i2;
            if (!this.s) {
                requestLayout();
            }
            if (getPanelState() == e.COLLAPSED) {
                c();
                invalidate();
            }
        }
    }

    public void setPanelState(e eVar) {
        e eVar2;
        float f2;
        if (this.p.d() == 2) {
            Log.d("SlidingUpPanelLayout", "View is settling. Aborting animation.");
            this.p.a();
        }
        if (eVar == null || eVar == e.DRAGGING) {
            throw new IllegalArgumentException("Panel state cannot be null or DRAGGING.");
        } else if (!isEnabled()) {
        } else {
            if ((!this.s && this.g == null) || eVar == (eVar2 = this.h)) {
                return;
            }
            if (this.s) {
                setPanelStateInternal(eVar);
                return;
            }
            if (eVar2 == e.HIDDEN) {
                this.g.setVisibility(0);
                requestLayout();
            }
            int i2 = a.f8211a[eVar.ordinal()];
            if (i2 == 1) {
                f2 = 1.0f;
            } else if (i2 == 2) {
                f2 = a(b(0.0f) + (this.f ? this.e : -this.e));
            } else if (i2 == 3) {
                a(0.0f);
                return;
            } else {
                return;
            }
            a(f2);
        }
    }
}
