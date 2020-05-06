package com.miui.gamebooster.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import com.miui.securitycenter.i;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HorizontalListView extends AdapterView<ListAdapter> {
    /* access modifiers changed from: private */
    public boolean A = false;
    private boolean B = false;
    /* access modifiers changed from: private */
    public View.OnClickListener C;
    private DataSetObserver D = new c(this);
    private Runnable E = new d(this);

    /* renamed from: a  reason: collision with root package name */
    protected Scroller f5368a = new Scroller(getContext());

    /* renamed from: b  reason: collision with root package name */
    private final a f5369b = new a(this, (b) null);
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public GestureDetector f5370c;

    /* renamed from: d  reason: collision with root package name */
    private int f5371d;
    protected ListAdapter e;
    private List<Queue<View>> f = new ArrayList();
    /* access modifiers changed from: private */
    public boolean g = false;
    private Rect h = new Rect();
    private View i = null;
    private int j = 0;
    private Drawable k = null;
    protected int l;
    protected int m;
    private Integer n = null;
    private int o = Integer.MAX_VALUE;
    /* access modifiers changed from: private */
    public int p;
    private int q;
    private int r;
    private e s = null;
    private int t = 0;
    /* access modifiers changed from: private */
    public boolean u = false;
    private d v = null;
    private d.a w = d.a.SCROLL_STATE_IDLE;
    private EdgeEffectCompat x;
    private EdgeEffectCompat y;
    private int z;

    private class a extends GestureDetector.SimpleOnGestureListener {
        private a() {
        }

        /* synthetic */ a(HorizontalListView horizontalListView, b bVar) {
            this();
        }

        public boolean onDown(MotionEvent motionEvent) {
            return HorizontalListView.this.a(motionEvent);
        }

        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            return HorizontalListView.this.a(motionEvent, motionEvent2, f, f2);
        }

        public void onLongPress(MotionEvent motionEvent) {
            HorizontalListView.this.h();
            int a2 = HorizontalListView.this.c((int) motionEvent.getX(), (int) motionEvent.getY());
            if (a2 >= 0 && !HorizontalListView.this.A) {
                View childAt = HorizontalListView.this.getChildAt(a2);
                AdapterView.OnItemLongClickListener onItemLongClickListener = HorizontalListView.this.getOnItemLongClickListener();
                if (onItemLongClickListener != null) {
                    int c2 = HorizontalListView.this.p + a2;
                    HorizontalListView horizontalListView = HorizontalListView.this;
                    if (onItemLongClickListener.onItemLongClick(horizontalListView, childAt, c2, horizontalListView.e.getItemId(c2))) {
                        HorizontalListView.this.performHapticFeedback(0);
                    }
                }
            }
        }

        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            HorizontalListView.this.a((Boolean) true);
            HorizontalListView.this.setCurrentScrollState(d.a.SCROLL_STATE_TOUCH_SCROLL);
            HorizontalListView.this.h();
            HorizontalListView horizontalListView = HorizontalListView.this;
            horizontalListView.m += (int) f;
            horizontalListView.i(Math.round(f));
            HorizontalListView.this.requestLayout();
            return true;
        }

        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            HorizontalListView.this.h();
            AdapterView.OnItemClickListener onItemClickListener = HorizontalListView.this.getOnItemClickListener();
            int a2 = HorizontalListView.this.c((int) motionEvent.getX(), (int) motionEvent.getY());
            if (a2 >= 0 && !HorizontalListView.this.A) {
                View childAt = HorizontalListView.this.getChildAt(a2);
                int c2 = HorizontalListView.this.p + a2;
                if (onItemClickListener != null) {
                    HorizontalListView horizontalListView = HorizontalListView.this;
                    onItemClickListener.onItemClick(horizontalListView, childAt, c2, horizontalListView.e.getItemId(c2));
                    return true;
                }
            }
            if (HorizontalListView.this.C == null || HorizontalListView.this.A) {
                return false;
            }
            HorizontalListView.this.C.onClick(HorizontalListView.this);
            return false;
        }
    }

    @TargetApi(11)
    private static final class b {
        static {
            if (Build.VERSION.SDK_INT < 11) {
                throw new RuntimeException("Should not get to HoneycombPlus class unless sdk is >= 11!");
            }
        }

        public static void a(Scroller scroller, float f) {
            if (scroller != null) {
                scroller.setFriction(f);
            }
        }
    }

    @TargetApi(14)
    private static final class c {
        static {
            if (Build.VERSION.SDK_INT < 14) {
                throw new RuntimeException("Should not get to IceCreamSandwichPlus class unless sdk is >= 14!");
            }
        }

        public static float a(Scroller scroller) {
            return scroller.getCurrVelocity();
        }
    }

    public interface d {

        public enum a {
            SCROLL_STATE_IDLE,
            SCROLL_STATE_TOUCH_SCROLL,
            SCROLL_STATE_FLING
        }

        void a(a aVar);
    }

    public interface e {
        void a();
    }

    public HorizontalListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.x = new EdgeEffectCompat(context);
        this.y = new EdgeEffectCompat(context);
        this.f5370c = new GestureDetector(context, this.f5369b);
        a();
        e();
        a(context, attributeSet);
        setWillNotDraw(false);
        if (Build.VERSION.SDK_INT >= 11) {
            b.a(this.f5368a, 0.009f);
        }
    }

    private ViewGroup.LayoutParams a(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        return layoutParams == null ? new ViewGroup.LayoutParams(-2, -1) : layoutParams;
    }

    private void a() {
        setOnTouchListener(new b(this));
    }

    private void a(int i2) {
        View rightmostChild = getRightmostChild();
        int i3 = 0;
        b(rightmostChild != null ? rightmostChild.getRight() : 0, i2);
        View leftmostChild = getLeftmostChild();
        if (leftmostChild != null) {
            i3 = leftmostChild.getLeft();
        }
        a(i3, i2);
    }

    private void a(int i2, int i3) {
        int i4;
        while ((i2 + i3) - this.j > 0 && (i4 = this.p) >= 1) {
            this.p = i4 - 1;
            ListAdapter listAdapter = this.e;
            int i5 = this.p;
            View view = listAdapter.getView(i5, c(i5), this);
            a(view, 0);
            i2 -= this.p == 0 ? view.getMeasuredWidth() : this.j + view.getMeasuredWidth();
            this.f5371d -= i2 + i3 == 0 ? view.getMeasuredWidth() : view.getMeasuredWidth() + this.j;
        }
    }

    private void a(int i2, View view) {
        int itemViewType = this.e.getItemViewType(i2);
        if (e(itemViewType)) {
            this.f.get(itemViewType).offer(view);
        }
    }

    private void a(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.HorizontalListView);
            Drawable drawable = obtainStyledAttributes.getDrawable(1);
            if (drawable != null) {
                setDivider(drawable);
            }
            int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(3, 0);
            if (dimensionPixelSize != 0) {
                setDividerWidth(dimensionPixelSize);
            }
            obtainStyledAttributes.recycle();
        }
    }

    private void a(Canvas canvas) {
        int childCount = getChildCount();
        Rect rect = this.h;
        rect.top = getPaddingTop();
        Rect rect2 = this.h;
        rect2.bottom = rect2.top + getRenderHeight();
        for (int i2 = 0; i2 < childCount; i2++) {
            if (i2 != childCount - 1 || !f(this.q)) {
                View childAt = getChildAt(i2);
                rect.left = childAt.getRight();
                rect.right = childAt.getRight() + this.j;
                if (rect.left < getPaddingLeft()) {
                    rect.left = getPaddingLeft();
                }
                if (rect.right > getWidth() - getPaddingRight()) {
                    rect.right = getWidth() - getPaddingRight();
                }
                a(canvas, rect);
                if (i2 == 0 && childAt.getLeft() > getPaddingLeft()) {
                    rect.left = getPaddingLeft();
                    rect.right = childAt.getLeft();
                    a(canvas, rect);
                }
            }
        }
    }

    private void a(Canvas canvas, Rect rect) {
        Drawable drawable = this.k;
        if (drawable != null) {
            drawable.setBounds(rect);
            this.k.draw(canvas);
        }
    }

    private void a(View view, int i2) {
        addViewInLayout(view, i2, a(view), true);
        b(view);
    }

    /* access modifiers changed from: private */
    public void a(Boolean bool) {
        if (this.B != bool.booleanValue()) {
            for (View view = this; view.getParent() instanceof View; view = (View) view.getParent()) {
                if ((view.getParent() instanceof ListView) || (view.getParent() instanceof ScrollView)) {
                    view.getParent().requestDisallowInterceptTouchEvent(bool.booleanValue());
                    this.B = bool.booleanValue();
                    return;
                }
            }
        }
    }

    private float b() {
        if (Build.VERSION.SDK_INT >= 14) {
            return c.a(this.f5368a);
        }
        return 30.0f;
    }

    private View b(int i2) {
        int i3 = this.p;
        if (i2 < i3 || i2 > this.q) {
            return null;
        }
        return getChildAt(i2 - i3);
    }

    private void b(int i2, int i3) {
        while (i2 + i3 + this.j < getWidth() && this.q + 1 < this.e.getCount()) {
            this.q++;
            if (this.p < 0) {
                this.p = this.q;
            }
            ListAdapter listAdapter = this.e;
            int i4 = this.q;
            View view = listAdapter.getView(i4, c(i4), this);
            a(view, -1);
            i2 += (this.q == 0 ? 0 : this.j) + view.getMeasuredWidth();
            c();
        }
    }

    private void b(View view) {
        ViewGroup.LayoutParams a2 = a(view);
        int childMeasureSpec = ViewGroup.getChildMeasureSpec(this.z, getPaddingTop() + getPaddingBottom(), a2.height);
        int i2 = a2.width;
        view.measure(i2 > 0 ? View.MeasureSpec.makeMeasureSpec(i2, 1073741824) : View.MeasureSpec.makeMeasureSpec(0, 0), childMeasureSpec);
    }

    /* access modifiers changed from: private */
    public int c(int i2, int i3) {
        int childCount = getChildCount();
        for (int i4 = 0; i4 < childCount; i4++) {
            getChildAt(i4).getHitRect(this.h);
            if (this.h.contains(i2, i3)) {
                return i4;
            }
        }
        return -1;
    }

    private View c(int i2) {
        int itemViewType = this.e.getItemViewType(i2);
        if (e(itemViewType)) {
            return (View) this.f.get(itemViewType).poll();
        }
        return null;
    }

    private void c() {
        ListAdapter listAdapter;
        if (this.s != null && (listAdapter = this.e) != null && listAdapter.getCount() - (this.q + 1) < this.t && !this.u) {
            this.u = true;
            this.s.a();
        }
    }

    private void d(int i2) {
        this.f.clear();
        for (int i3 = 0; i3 < i2; i3++) {
            this.f.add(new LinkedList());
        }
    }

    private boolean d() {
        View rightmostChild;
        if (f(this.q) && (rightmostChild = getRightmostChild()) != null) {
            int i2 = this.o;
            this.o = (this.l + (rightmostChild.getRight() - getPaddingLeft())) - getRenderWidth();
            if (this.o < 0) {
                this.o = 0;
            }
            if (this.o != i2) {
                return true;
            }
        }
        return false;
    }

    private void e() {
        this.p = -1;
        this.q = -1;
        this.f5371d = 0;
        this.l = 0;
        this.m = 0;
        this.o = Integer.MAX_VALUE;
        setCurrentScrollState(d.a.SCROLL_STATE_IDLE);
    }

    private boolean e(int i2) {
        return i2 < this.f.size();
    }

    private void f() {
        EdgeEffectCompat edgeEffectCompat = this.x;
        if (edgeEffectCompat != null) {
            edgeEffectCompat.onRelease();
        }
        EdgeEffectCompat edgeEffectCompat2 = this.y;
        if (edgeEffectCompat2 != null) {
            edgeEffectCompat2.onRelease();
        }
    }

    private boolean f(int i2) {
        return i2 == this.e.getCount() - 1;
    }

    /* access modifiers changed from: private */
    public void g() {
        e();
        removeAllViewsInLayout();
        requestLayout();
    }

    private void g(int i2) {
        int childCount = getChildCount();
        if (childCount > 0) {
            this.f5371d += i2;
            int i3 = this.f5371d;
            for (int i4 = 0; i4 < childCount; i4++) {
                View childAt = getChildAt(i4);
                int paddingLeft = getPaddingLeft() + i3;
                int paddingTop = getPaddingTop();
                childAt.layout(paddingLeft, paddingTop, childAt.getMeasuredWidth() + paddingLeft, childAt.getMeasuredHeight() + paddingTop);
                i3 += childAt.getMeasuredWidth() + this.j;
            }
        }
    }

    private View getLeftmostChild() {
        return getChildAt(0);
    }

    private int getRenderHeight() {
        return (getHeight() - getPaddingTop()) - getPaddingBottom();
    }

    private int getRenderWidth() {
        return (getWidth() - getPaddingLeft()) - getPaddingRight();
    }

    private View getRightmostChild() {
        return getChildAt(getChildCount() - 1);
    }

    /* access modifiers changed from: private */
    public void h() {
        View view = this.i;
        if (view != null) {
            view.setPressed(false);
            refreshDrawableState();
            this.i = null;
        }
    }

    private void h(int i2) {
        while (true) {
            View leftmostChild = getLeftmostChild();
            if (leftmostChild != null && leftmostChild.getRight() + i2 <= 0) {
                this.f5371d += f(this.p) ? leftmostChild.getMeasuredWidth() : this.j + leftmostChild.getMeasuredWidth();
                a(this.p, leftmostChild);
                removeViewInLayout(leftmostChild);
                this.p++;
            }
        }
        while (true) {
            View rightmostChild = getRightmostChild();
            if (rightmostChild != null && rightmostChild.getLeft() + i2 >= getWidth()) {
                a(this.q, rightmostChild);
                removeViewInLayout(rightmostChild);
                this.q--;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void i(int i2) {
        EdgeEffectCompat edgeEffectCompat;
        if (this.x != null && this.y != null) {
            int i3 = this.l + i2;
            Scroller scroller = this.f5368a;
            if (scroller == null || scroller.isFinished()) {
                if (i3 < 0) {
                    this.x.onPull(((float) Math.abs(i2)) / ((float) getRenderWidth()));
                    if (!this.y.isFinished()) {
                        edgeEffectCompat = this.y;
                    } else {
                        return;
                    }
                } else if (i3 > this.o) {
                    this.y.onPull(((float) Math.abs(i2)) / ((float) getRenderWidth()));
                    if (!this.x.isFinished()) {
                        edgeEffectCompat = this.x;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
                edgeEffectCompat.onRelease();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentScrollState(d.a aVar) {
        d dVar;
        if (!(this.w == aVar || (dVar = this.v) == null)) {
            dVar.a(aVar);
        }
        this.w = aVar;
    }

    /* access modifiers changed from: protected */
    public boolean a(MotionEvent motionEvent) {
        int c2;
        this.A = !this.f5368a.isFinished();
        this.f5368a.forceFinished(true);
        setCurrentScrollState(d.a.SCROLL_STATE_IDLE);
        h();
        if (!this.A && (c2 = c((int) motionEvent.getX(), (int) motionEvent.getY())) >= 0) {
            this.i = getChildAt(c2);
            View view = this.i;
            if (view != null) {
                view.setPressed(true);
                refreshDrawableState();
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean a(MotionEvent motionEvent, MotionEvent motionEvent2, float f2, float f3) {
        this.f5368a.fling(this.m, 0, (int) (-f2), 0, 0, this.o, 0, 0);
        setCurrentScrollState(d.a.SCROLL_STATE_FLING);
        requestLayout();
        return true;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void dispatchSetPressed(boolean z2) {
    }

    public ListAdapter getAdapter() {
        return this.e;
    }

    public int getFirstVisiblePosition() {
        return this.p;
    }

    public int getLastVisiblePosition() {
        return this.q;
    }

    /* access modifiers changed from: protected */
    public float getLeftFadingEdgeStrength() {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
        int i2 = this.l;
        if (i2 == 0) {
            return 0.0f;
        }
        if (i2 < horizontalFadingEdgeLength) {
            return ((float) i2) / ((float) horizontalFadingEdgeLength);
        }
        return 1.0f;
    }

    /* access modifiers changed from: protected */
    public float getRightFadingEdgeStrength() {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
        int i2 = this.l;
        int i3 = this.o;
        if (i2 == i3) {
            return 0.0f;
        }
        if (i3 - i2 < horizontalFadingEdgeLength) {
            return ((float) (i3 - i2)) / ((float) horizontalFadingEdgeLength);
        }
        return 1.0f;
    }

    public View getSelectedView() {
        return b(this.r);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        a(canvas);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x008a  */
    @android.annotation.SuppressLint({"WrongCall"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLayout(boolean r4, int r5, int r6, int r7, int r8) {
        /*
            r3 = this;
            super.onLayout(r4, r5, r6, r7, r8)
            android.widget.ListAdapter r0 = r3.e
            if (r0 != 0) goto L_0x0008
            return
        L_0x0008:
            r3.invalidate()
            boolean r0 = r3.g
            r1 = 0
            if (r0 == 0) goto L_0x001c
            int r0 = r3.l
            r3.e()
            r3.removeAllViewsInLayout()
            r3.m = r0
            r3.g = r1
        L_0x001c:
            java.lang.Integer r0 = r3.n
            if (r0 == 0) goto L_0x0029
            int r0 = r0.intValue()
            r3.m = r0
            r0 = 0
            r3.n = r0
        L_0x0029:
            android.widget.Scroller r0 = r3.f5368a
            boolean r0 = r0.computeScrollOffset()
            if (r0 == 0) goto L_0x0039
            android.widget.Scroller r0 = r3.f5368a
            int r0 = r0.getCurrX()
            r3.m = r0
        L_0x0039:
            int r0 = r3.m
            r2 = 1
            if (r0 >= 0) goto L_0x005d
            r3.m = r1
            android.support.v4.widget.EdgeEffectCompat r0 = r3.x
            boolean r0 = r0.isFinished()
            if (r0 == 0) goto L_0x0052
            android.support.v4.widget.EdgeEffectCompat r0 = r3.x
        L_0x004a:
            float r1 = r3.b()
            int r1 = (int) r1
            r0.onAbsorb(r1)
        L_0x0052:
            android.widget.Scroller r0 = r3.f5368a
            r0.forceFinished(r2)
            com.miui.gamebooster.widget.HorizontalListView$d$a r0 = com.miui.gamebooster.widget.HorizontalListView.d.a.SCROLL_STATE_IDLE
            r3.setCurrentScrollState(r0)
            goto L_0x006e
        L_0x005d:
            int r1 = r3.o
            if (r0 <= r1) goto L_0x006e
            r3.m = r1
            android.support.v4.widget.EdgeEffectCompat r0 = r3.y
            boolean r0 = r0.isFinished()
            if (r0 == 0) goto L_0x0052
            android.support.v4.widget.EdgeEffectCompat r0 = r3.y
            goto L_0x004a
        L_0x006e:
            int r0 = r3.l
            int r1 = r3.m
            int r0 = r0 - r1
            r3.h(r0)
            r3.a((int) r0)
            r3.g(r0)
            int r0 = r3.m
            r3.l = r0
            boolean r0 = r3.d()
            if (r0 == 0) goto L_0x008a
            r3.onLayout(r4, r5, r6, r7, r8)
            return
        L_0x008a:
            android.widget.Scroller r4 = r3.f5368a
            boolean r4 = r4.isFinished()
            if (r4 == 0) goto L_0x009e
            com.miui.gamebooster.widget.HorizontalListView$d$a r4 = r3.w
            com.miui.gamebooster.widget.HorizontalListView$d$a r5 = com.miui.gamebooster.widget.HorizontalListView.d.a.SCROLL_STATE_FLING
            if (r4 != r5) goto L_0x00a3
            com.miui.gamebooster.widget.HorizontalListView$d$a r4 = com.miui.gamebooster.widget.HorizontalListView.d.a.SCROLL_STATE_IDLE
            r3.setCurrentScrollState(r4)
            goto L_0x00a3
        L_0x009e:
            java.lang.Runnable r4 = r3.E
            android.support.v4.view.ViewCompat.postOnAnimation(r3, r4)
        L_0x00a3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.widget.HorizontalListView.onLayout(boolean, int, int, int, int):void");
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        super.onMeasure(i2, i3);
        int mode = View.MeasureSpec.getMode(i3);
        ListAdapter listAdapter = this.e;
        if (listAdapter == null || listAdapter.getCount() <= 0) {
            this.z = View.MeasureSpec.makeMeasureSpec(getMinimumHeight(), View.MeasureSpec.getMode(i3));
        } else {
            View view = this.e.getView(0, (View) null, this);
            measureChild(view, i2, i3);
            int measuredHeight = view.getMeasuredHeight();
            if (measuredHeight == 0) {
                measuredHeight = getMinimumHeight();
            }
            this.z = View.MeasureSpec.makeMeasureSpec(measuredHeight + getPaddingTop() + getPaddingBottom(), mode);
            setMeasuredDimension(i2, this.z);
        }
        for (int i4 = 0; i4 < getChildCount(); i4++) {
            View b2 = b(i4);
            if (b2 != null) {
                b2.measure(i2, this.z);
            }
        }
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle) parcelable;
            this.n = Integer.valueOf(bundle.getInt("BUNDLE_ID_CURRENT_X"));
            super.onRestoreInstanceState(bundle.getParcelable("BUNDLE_ID_PARENT_STATE"));
        }
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("BUNDLE_ID_PARENT_STATE", super.onSaveInstanceState());
        bundle.putInt("BUNDLE_ID_CURRENT_X", this.l);
        return bundle;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            Scroller scroller = this.f5368a;
            if (scroller == null || scroller.isFinished()) {
                setCurrentScrollState(d.a.SCROLL_STATE_IDLE);
            }
            a((Boolean) false);
            f();
        } else if (motionEvent.getAction() == 3) {
            h();
            f();
            a((Boolean) false);
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setAdapter(ListAdapter listAdapter) {
        ListAdapter listAdapter2 = this.e;
        if (listAdapter2 != null) {
            listAdapter2.unregisterDataSetObserver(this.D);
        }
        if (listAdapter != null) {
            this.u = false;
            this.e = listAdapter;
            this.e.registerDataSetObserver(this.D);
        }
        d(this.e.getViewTypeCount());
        g();
    }

    public void setDivider(Drawable drawable) {
        this.k = drawable;
        setDividerWidth(drawable != null ? drawable.getIntrinsicWidth() : 0);
    }

    public void setDividerWidth(int i2) {
        this.j = i2;
        requestLayout();
        invalidate();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.C = onClickListener;
    }

    public void setOnScrollStateChangedListener(d dVar) {
        this.v = dVar;
    }

    public void setSelection(int i2) {
        this.r = i2;
    }
}
