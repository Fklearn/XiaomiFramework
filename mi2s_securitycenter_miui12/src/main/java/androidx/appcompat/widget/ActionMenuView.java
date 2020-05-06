package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.view.menu.j;
import androidx.appcompat.view.menu.n;
import androidx.appcompat.view.menu.s;
import androidx.appcompat.view.menu.t;
import androidx.appcompat.widget.LinearLayoutCompat;

public class ActionMenuView extends LinearLayoutCompat implements j.b, t {
    e A;
    private j p;
    private Context q;
    private int r;
    private boolean s;
    private ActionMenuPresenter t;
    private s.a u;
    j.a v;
    private boolean w;
    private int x;
    private int y;
    private int z;

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public interface a {
        boolean a();

        boolean b();
    }

    private static class b implements s.a {
        b() {
        }

        public void a(j jVar, boolean z) {
        }

        public boolean a(j jVar) {
            return false;
        }
    }

    public static class c extends LinearLayoutCompat.a {
        @ViewDebug.ExportedProperty

        /* renamed from: c  reason: collision with root package name */
        public boolean f436c;
        @ViewDebug.ExportedProperty

        /* renamed from: d  reason: collision with root package name */
        public int f437d;
        @ViewDebug.ExportedProperty
        public int e;
        @ViewDebug.ExportedProperty
        public boolean f;
        @ViewDebug.ExportedProperty
        public boolean g;
        boolean h;

        public c(int i, int i2) {
            super(i, i2);
            this.f436c = false;
        }

        public c(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public c(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public c(c cVar) {
            super(cVar);
            this.f436c = cVar.f436c;
        }
    }

    private class d implements j.a {
        d() {
        }

        public void a(j jVar) {
            j.a aVar = ActionMenuView.this.v;
            if (aVar != null) {
                aVar.a(jVar);
            }
        }

        public boolean a(j jVar, MenuItem menuItem) {
            e eVar = ActionMenuView.this.A;
            return eVar != null && eVar.onMenuItemClick(menuItem);
        }
    }

    public interface e {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public ActionMenuView(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public ActionMenuView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        setBaselineAligned(false);
        float f = context.getResources().getDisplayMetrics().density;
        this.y = (int) (56.0f * f);
        this.z = (int) (f * 4.0f);
        this.q = context;
        this.r = 0;
    }

    static int a(View view, int i, int i2, int i3, int i4) {
        c cVar = (c) view.getLayoutParams();
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i3) - i4, View.MeasureSpec.getMode(i3));
        ActionMenuItemView actionMenuItemView = view instanceof ActionMenuItemView ? (ActionMenuItemView) view : null;
        boolean z2 = true;
        boolean z3 = actionMenuItemView != null && actionMenuItemView.d();
        int i5 = 2;
        if (i2 <= 0 || (z3 && i2 < 2)) {
            i5 = 0;
        } else {
            view.measure(View.MeasureSpec.makeMeasureSpec(i2 * i, Integer.MIN_VALUE), makeMeasureSpec);
            int measuredWidth = view.getMeasuredWidth();
            int i6 = measuredWidth / i;
            if (measuredWidth % i != 0) {
                i6++;
            }
            if (!z3 || i6 >= 2) {
                i5 = i6;
            }
        }
        if (cVar.f436c || !z3) {
            z2 = false;
        }
        cVar.f = z2;
        cVar.f437d = i5;
        view.measure(View.MeasureSpec.makeMeasureSpec(i * i5, 1073741824), makeMeasureSpec);
        return i5;
    }

    /* JADX WARNING: Removed duplicated region for block: B:133:0x023a A[LOOP:5: B:133:0x023a->B:137:0x0258, LOOP_START, PHI: r13 
      PHI: (r13v3 int) = (r13v2 int), (r13v4 int) binds: [B:132:0x0238, B:137:0x0258] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x025d  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0260  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void e(int r30, int r31) {
        /*
            r29 = this;
            r0 = r29
            int r1 = android.view.View.MeasureSpec.getMode(r31)
            int r2 = android.view.View.MeasureSpec.getSize(r30)
            int r3 = android.view.View.MeasureSpec.getSize(r31)
            int r4 = r29.getPaddingLeft()
            int r5 = r29.getPaddingRight()
            int r4 = r4 + r5
            int r5 = r29.getPaddingTop()
            int r6 = r29.getPaddingBottom()
            int r5 = r5 + r6
            r6 = -2
            r7 = r31
            int r6 = android.view.ViewGroup.getChildMeasureSpec(r7, r5, r6)
            int r2 = r2 - r4
            int r4 = r0.y
            int r7 = r2 / r4
            int r8 = r2 % r4
            r9 = 0
            if (r7 != 0) goto L_0x0035
            r0.setMeasuredDimension(r2, r9)
            return
        L_0x0035:
            int r8 = r8 / r7
            int r4 = r4 + r8
            int r8 = r29.getChildCount()
            r14 = r7
            r7 = r9
            r10 = r7
            r12 = r10
            r13 = r12
            r15 = r13
            r16 = r15
            r17 = 0
        L_0x0045:
            if (r7 >= r8) goto L_0x00c5
            android.view.View r11 = r0.getChildAt(r7)
            int r9 = r11.getVisibility()
            r19 = r3
            r3 = 8
            if (r9 != r3) goto L_0x0057
            goto L_0x00bf
        L_0x0057:
            boolean r3 = r11 instanceof androidx.appcompat.view.menu.ActionMenuItemView
            int r13 = r13 + 1
            if (r3 == 0) goto L_0x0066
            int r9 = r0.z
            r20 = r13
            r13 = 0
            r11.setPadding(r9, r13, r9, r13)
            goto L_0x0069
        L_0x0066:
            r20 = r13
            r13 = 0
        L_0x0069:
            android.view.ViewGroup$LayoutParams r9 = r11.getLayoutParams()
            androidx.appcompat.widget.ActionMenuView$c r9 = (androidx.appcompat.widget.ActionMenuView.c) r9
            r9.h = r13
            r9.e = r13
            r9.f437d = r13
            r9.f = r13
            r9.leftMargin = r13
            r9.rightMargin = r13
            if (r3 == 0) goto L_0x0088
            r3 = r11
            androidx.appcompat.view.menu.ActionMenuItemView r3 = (androidx.appcompat.view.menu.ActionMenuItemView) r3
            boolean r3 = r3.d()
            if (r3 == 0) goto L_0x0088
            r3 = 1
            goto L_0x0089
        L_0x0088:
            r3 = 0
        L_0x0089:
            r9.g = r3
            boolean r3 = r9.f436c
            if (r3 == 0) goto L_0x0091
            r3 = 1
            goto L_0x0092
        L_0x0091:
            r3 = r14
        L_0x0092:
            int r3 = a(r11, r4, r3, r6, r5)
            int r13 = java.lang.Math.max(r15, r3)
            boolean r15 = r9.f
            if (r15 == 0) goto L_0x00a0
            int r16 = r16 + 1
        L_0x00a0:
            boolean r9 = r9.f436c
            if (r9 == 0) goto L_0x00a5
            r12 = 1
        L_0x00a5:
            int r14 = r14 - r3
            int r9 = r11.getMeasuredHeight()
            int r10 = java.lang.Math.max(r10, r9)
            r9 = 1
            if (r3 != r9) goto L_0x00bb
            int r3 = r9 << r7
            r11 = r10
            long r9 = (long) r3
            long r9 = r17 | r9
            r17 = r9
            r10 = r11
            goto L_0x00bc
        L_0x00bb:
            r11 = r10
        L_0x00bc:
            r15 = r13
            r13 = r20
        L_0x00bf:
            int r7 = r7 + 1
            r3 = r19
            r9 = 0
            goto L_0x0045
        L_0x00c5:
            r19 = r3
            r3 = 2
            if (r12 == 0) goto L_0x00ce
            if (r13 != r3) goto L_0x00ce
            r5 = 1
            goto L_0x00cf
        L_0x00ce:
            r5 = 0
        L_0x00cf:
            r7 = 0
        L_0x00d0:
            r20 = 1
            if (r16 <= 0) goto L_0x0170
            if (r14 <= 0) goto L_0x0170
            r9 = 2147483647(0x7fffffff, float:NaN)
            r3 = r9
            r9 = 0
            r11 = 0
            r22 = 0
        L_0x00de:
            if (r9 >= r8) goto L_0x010f
            android.view.View r24 = r0.getChildAt(r9)
            android.view.ViewGroup$LayoutParams r24 = r24.getLayoutParams()
            r25 = r7
            r7 = r24
            androidx.appcompat.widget.ActionMenuView$c r7 = (androidx.appcompat.widget.ActionMenuView.c) r7
            r24 = r10
            boolean r10 = r7.f
            if (r10 != 0) goto L_0x00f5
            goto L_0x0108
        L_0x00f5:
            int r7 = r7.f437d
            if (r7 >= r3) goto L_0x0100
            long r10 = r20 << r9
            r3 = r7
            r22 = r10
            r11 = 1
            goto L_0x0108
        L_0x0100:
            if (r7 != r3) goto L_0x0108
            long r26 = r20 << r9
            long r22 = r22 | r26
            int r11 = r11 + 1
        L_0x0108:
            int r9 = r9 + 1
            r10 = r24
            r7 = r25
            goto L_0x00de
        L_0x010f:
            r25 = r7
            r24 = r10
            long r17 = r17 | r22
            if (r11 <= r14) goto L_0x011b
            r11 = r1
            r26 = r2
            goto L_0x0177
        L_0x011b:
            int r3 = r3 + 1
            r7 = 0
        L_0x011e:
            if (r7 >= r8) goto L_0x016a
            android.view.View r9 = r0.getChildAt(r7)
            android.view.ViewGroup$LayoutParams r10 = r9.getLayoutParams()
            androidx.appcompat.widget.ActionMenuView$c r10 = (androidx.appcompat.widget.ActionMenuView.c) r10
            r26 = r2
            r11 = 1
            int r2 = r11 << r7
            r11 = r1
            long r1 = (long) r2
            long r20 = r22 & r1
            r27 = 0
            int r20 = (r20 > r27 ? 1 : (r20 == r27 ? 0 : -1))
            if (r20 != 0) goto L_0x0142
            int r9 = r10.f437d
            if (r9 != r3) goto L_0x013f
            long r17 = r17 | r1
        L_0x013f:
            r20 = r3
            goto L_0x0162
        L_0x0142:
            if (r5 == 0) goto L_0x0156
            boolean r1 = r10.g
            if (r1 == 0) goto L_0x0156
            r1 = 1
            if (r14 != r1) goto L_0x0156
            int r2 = r0.z
            int r1 = r2 + r4
            r20 = r3
            r3 = 0
            r9.setPadding(r1, r3, r2, r3)
            goto L_0x0158
        L_0x0156:
            r20 = r3
        L_0x0158:
            int r1 = r10.f437d
            r2 = 1
            int r1 = r1 + r2
            r10.f437d = r1
            r10.h = r2
            int r14 = r14 + -1
        L_0x0162:
            int r7 = r7 + 1
            r1 = r11
            r3 = r20
            r2 = r26
            goto L_0x011e
        L_0x016a:
            r10 = r24
            r3 = 2
            r7 = 1
            goto L_0x00d0
        L_0x0170:
            r11 = r1
            r26 = r2
            r25 = r7
            r24 = r10
        L_0x0177:
            if (r12 != 0) goto L_0x017e
            r1 = 1
            if (r13 != r1) goto L_0x017f
            r2 = r1
            goto L_0x0180
        L_0x017e:
            r1 = 1
        L_0x017f:
            r2 = 0
        L_0x0180:
            if (r14 <= 0) goto L_0x0235
            r9 = 0
            int r3 = (r17 > r9 ? 1 : (r17 == r9 ? 0 : -1))
            if (r3 == 0) goto L_0x0235
            int r13 = r13 - r1
            if (r14 < r13) goto L_0x018f
            if (r2 != 0) goto L_0x018f
            if (r15 <= r1) goto L_0x0235
        L_0x018f:
            int r1 = java.lang.Long.bitCount(r17)
            float r1 = (float) r1
            if (r2 != 0) goto L_0x01d0
            long r2 = r17 & r20
            r9 = 0
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            r3 = 1056964608(0x3f000000, float:0.5)
            if (r2 == 0) goto L_0x01b1
            r13 = 0
            android.view.View r2 = r0.getChildAt(r13)
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            androidx.appcompat.widget.ActionMenuView$c r2 = (androidx.appcompat.widget.ActionMenuView.c) r2
            boolean r2 = r2.g
            if (r2 != 0) goto L_0x01b2
            float r1 = r1 - r3
            goto L_0x01b2
        L_0x01b1:
            r13 = 0
        L_0x01b2:
            int r2 = r8 + -1
            r5 = 1
            int r7 = r5 << r2
            long r9 = (long) r7
            long r9 = r17 & r9
            r15 = 0
            int r5 = (r9 > r15 ? 1 : (r9 == r15 ? 0 : -1))
            if (r5 == 0) goto L_0x01d1
            android.view.View r2 = r0.getChildAt(r2)
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            androidx.appcompat.widget.ActionMenuView$c r2 = (androidx.appcompat.widget.ActionMenuView.c) r2
            boolean r2 = r2.g
            if (r2 != 0) goto L_0x01d1
            float r1 = r1 - r3
            goto L_0x01d1
        L_0x01d0:
            r13 = 0
        L_0x01d1:
            r2 = 0
            int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x01db
            int r14 = r14 * r4
            float r2 = (float) r14
            float r2 = r2 / r1
            int r9 = (int) r2
            goto L_0x01dc
        L_0x01db:
            r9 = r13
        L_0x01dc:
            r1 = r13
        L_0x01dd:
            if (r1 >= r8) goto L_0x0236
            r2 = 1
            int r3 = r2 << r1
            long r2 = (long) r3
            long r2 = r17 & r2
            r14 = 0
            int r2 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r2 != 0) goto L_0x01ee
            r2 = 1
            r5 = 2
            goto L_0x0232
        L_0x01ee:
            android.view.View r2 = r0.getChildAt(r1)
            android.view.ViewGroup$LayoutParams r3 = r2.getLayoutParams()
            androidx.appcompat.widget.ActionMenuView$c r3 = (androidx.appcompat.widget.ActionMenuView.c) r3
            boolean r2 = r2 instanceof androidx.appcompat.view.menu.ActionMenuItemView
            if (r2 == 0) goto L_0x0212
            r3.e = r9
            r2 = 1
            r3.h = r2
            if (r1 != 0) goto L_0x020d
            boolean r2 = r3.g
            if (r2 != 0) goto L_0x020d
            int r2 = -r9
            r5 = 2
            int r2 = r2 / r5
            r3.leftMargin = r2
            goto L_0x020e
        L_0x020d:
            r5 = 2
        L_0x020e:
            r2 = 1
            r25 = 1
            goto L_0x0232
        L_0x0212:
            r5 = 2
            boolean r2 = r3.f436c
            if (r2 == 0) goto L_0x0223
            r3.e = r9
            r2 = 1
            r3.h = r2
            int r7 = -r9
            int r7 = r7 / r5
            r3.rightMargin = r7
            r25 = r2
            goto L_0x0232
        L_0x0223:
            r2 = 1
            if (r1 == 0) goto L_0x022a
            int r7 = r9 / 2
            r3.leftMargin = r7
        L_0x022a:
            int r7 = r8 + -1
            if (r1 == r7) goto L_0x0232
            int r7 = r9 / 2
            r3.rightMargin = r7
        L_0x0232:
            int r1 = r1 + 1
            goto L_0x01dd
        L_0x0235:
            r13 = 0
        L_0x0236:
            r1 = 1073741824(0x40000000, float:2.0)
            if (r25 == 0) goto L_0x025b
        L_0x023a:
            if (r13 >= r8) goto L_0x025b
            android.view.View r2 = r0.getChildAt(r13)
            android.view.ViewGroup$LayoutParams r3 = r2.getLayoutParams()
            androidx.appcompat.widget.ActionMenuView$c r3 = (androidx.appcompat.widget.ActionMenuView.c) r3
            boolean r5 = r3.h
            if (r5 != 0) goto L_0x024b
            goto L_0x0258
        L_0x024b:
            int r5 = r3.f437d
            int r5 = r5 * r4
            int r3 = r3.e
            int r5 = r5 + r3
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r1)
            r2.measure(r3, r6)
        L_0x0258:
            int r13 = r13 + 1
            goto L_0x023a
        L_0x025b:
            if (r11 == r1) goto L_0x0260
            r1 = r24
            goto L_0x0262
        L_0x0260:
            r1 = r19
        L_0x0262:
            r2 = r26
            r0.setMeasuredDimension(r2, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.widget.ActionMenuView.e(int, int):void");
    }

    public void a() {
        ActionMenuPresenter actionMenuPresenter = this.t;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.c();
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void a(j jVar) {
        this.p = jVar;
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void a(s.a aVar, j.a aVar2) {
        this.u = aVar;
        this.v = aVar2;
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public boolean a(n nVar) {
        return this.p.a((MenuItem) nVar, 0);
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public c b() {
        c generateDefaultLayoutParams = generateDefaultLayoutParams();
        generateDefaultLayoutParams.f436c = true;
        return generateDefaultLayoutParams;
    }

    public boolean c() {
        ActionMenuPresenter actionMenuPresenter = this.t;
        return actionMenuPresenter != null && actionMenuPresenter.e();
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof c;
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public boolean d() {
        ActionMenuPresenter actionMenuPresenter = this.t;
        return actionMenuPresenter != null && actionMenuPresenter.g();
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public boolean d(int i) {
        boolean z2 = false;
        if (i == 0) {
            return false;
        }
        View childAt = getChildAt(i - 1);
        View childAt2 = getChildAt(i);
        if (i < getChildCount() && (childAt instanceof a)) {
            z2 = false | ((a) childAt).a();
        }
        return (i <= 0 || !(childAt2 instanceof a)) ? z2 : z2 | ((a) childAt2).b();
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return false;
    }

    public boolean e() {
        ActionMenuPresenter actionMenuPresenter = this.t;
        return actionMenuPresenter != null && actionMenuPresenter.h();
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public boolean f() {
        return this.s;
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public j g() {
        return this.p;
    }

    /* access modifiers changed from: protected */
    public c generateDefaultLayoutParams() {
        c cVar = new c(-2, -2);
        cVar.f515b = 16;
        return cVar;
    }

    public c generateLayoutParams(AttributeSet attributeSet) {
        return new c(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public c generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams == null) {
            return generateDefaultLayoutParams();
        }
        c cVar = layoutParams instanceof c ? new c((c) layoutParams) : new c(layoutParams);
        if (cVar.f515b <= 0) {
            cVar.f515b = 16;
        }
        return cVar;
    }

    public Menu getMenu() {
        if (this.p == null) {
            Context context = getContext();
            this.p = new j(context);
            this.p.a((j.a) new d());
            this.t = new ActionMenuPresenter(context);
            this.t.c(true);
            ActionMenuPresenter actionMenuPresenter = this.t;
            s.a aVar = this.u;
            if (aVar == null) {
                aVar = new b();
            }
            actionMenuPresenter.a(aVar);
            this.p.a((s) this.t, this.q);
            this.t.a(this);
        }
        return this.p;
    }

    @Nullable
    public Drawable getOverflowIcon() {
        getMenu();
        return this.t.d();
    }

    public int getPopupTheme() {
        return this.r;
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public int getWindowAnimations() {
        return 0;
    }

    public boolean h() {
        ActionMenuPresenter actionMenuPresenter = this.t;
        return actionMenuPresenter != null && actionMenuPresenter.i();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        ActionMenuPresenter actionMenuPresenter = this.t;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.a(false);
            if (this.t.h()) {
                this.t.e();
                this.t.i();
            }
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        a();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z2, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        int i8;
        if (!this.w) {
            super.onLayout(z2, i, i2, i3, i4);
            return;
        }
        int childCount = getChildCount();
        int i9 = (i4 - i2) / 2;
        int dividerWidth = getDividerWidth();
        int i10 = i3 - i;
        int paddingRight = (i10 - getPaddingRight()) - getPaddingLeft();
        boolean a2 = Ja.a(this);
        int i11 = paddingRight;
        int i12 = 0;
        int i13 = 0;
        for (int i14 = 0; i14 < childCount; i14++) {
            View childAt = getChildAt(i14);
            if (childAt.getVisibility() != 8) {
                c cVar = (c) childAt.getLayoutParams();
                if (cVar.f436c) {
                    int measuredWidth = childAt.getMeasuredWidth();
                    if (d(i14)) {
                        measuredWidth += dividerWidth;
                    }
                    int measuredHeight = childAt.getMeasuredHeight();
                    if (a2) {
                        i7 = getPaddingLeft() + cVar.leftMargin;
                        i8 = i7 + measuredWidth;
                    } else {
                        i8 = (getWidth() - getPaddingRight()) - cVar.rightMargin;
                        i7 = i8 - measuredWidth;
                    }
                    int i15 = i9 - (measuredHeight / 2);
                    childAt.layout(i7, i15, i8, measuredHeight + i15);
                    i11 -= measuredWidth;
                    i12 = 1;
                } else {
                    i11 -= (childAt.getMeasuredWidth() + cVar.leftMargin) + cVar.rightMargin;
                    d(i14);
                    i13++;
                }
            }
        }
        if (childCount == 1 && i12 == 0) {
            View childAt2 = getChildAt(0);
            int measuredWidth2 = childAt2.getMeasuredWidth();
            int measuredHeight2 = childAt2.getMeasuredHeight();
            int i16 = (i10 / 2) - (measuredWidth2 / 2);
            int i17 = i9 - (measuredHeight2 / 2);
            childAt2.layout(i16, i17, measuredWidth2 + i16, measuredHeight2 + i17);
            return;
        }
        int i18 = i13 - (i12 ^ 1);
        if (i18 > 0) {
            i5 = i11 / i18;
            i6 = 0;
        } else {
            i6 = 0;
            i5 = 0;
        }
        int max = Math.max(i6, i5);
        if (a2) {
            int width = getWidth() - getPaddingRight();
            while (i6 < childCount) {
                View childAt3 = getChildAt(i6);
                c cVar2 = (c) childAt3.getLayoutParams();
                if (childAt3.getVisibility() != 8 && !cVar2.f436c) {
                    int i19 = width - cVar2.rightMargin;
                    int measuredWidth3 = childAt3.getMeasuredWidth();
                    int measuredHeight3 = childAt3.getMeasuredHeight();
                    int i20 = i9 - (measuredHeight3 / 2);
                    childAt3.layout(i19 - measuredWidth3, i20, i19, measuredHeight3 + i20);
                    width = i19 - ((measuredWidth3 + cVar2.leftMargin) + max);
                }
                i6++;
            }
            return;
        }
        int paddingLeft = getPaddingLeft();
        while (i6 < childCount) {
            View childAt4 = getChildAt(i6);
            c cVar3 = (c) childAt4.getLayoutParams();
            if (childAt4.getVisibility() != 8 && !cVar3.f436c) {
                int i21 = paddingLeft + cVar3.leftMargin;
                int measuredWidth4 = childAt4.getMeasuredWidth();
                int measuredHeight4 = childAt4.getMeasuredHeight();
                int i22 = i9 - (measuredHeight4 / 2);
                childAt4.layout(i21, i22, i21 + measuredWidth4, measuredHeight4 + i22);
                paddingLeft = i21 + measuredWidth4 + cVar3.rightMargin + max;
            }
            i6++;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        j jVar;
        boolean z2 = this.w;
        this.w = View.MeasureSpec.getMode(i) == 1073741824;
        if (z2 != this.w) {
            this.x = 0;
        }
        int size = View.MeasureSpec.getSize(i);
        if (!(!this.w || (jVar = this.p) == null || size == this.x)) {
            this.x = size;
            jVar.b(true);
        }
        int childCount = getChildCount();
        if (!this.w || childCount <= 0) {
            for (int i3 = 0; i3 < childCount; i3++) {
                c cVar = (c) getChildAt(i3).getLayoutParams();
                cVar.rightMargin = 0;
                cVar.leftMargin = 0;
            }
            super.onMeasure(i, i2);
            return;
        }
        e(i, i2);
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setExpandedActionViewsExclusive(boolean z2) {
        this.t.b(z2);
    }

    public void setOnMenuItemClickListener(e eVar) {
        this.A = eVar;
    }

    public void setOverflowIcon(@Nullable Drawable drawable) {
        getMenu();
        this.t.a(drawable);
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setOverflowReserved(boolean z2) {
        this.s = z2;
    }

    public void setPopupTheme(@StyleRes int i) {
        if (this.r != i) {
            this.r = i;
            if (i == 0) {
                this.q = getContext();
            } else {
                this.q = new ContextThemeWrapper(getContext(), i);
            }
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setPresenter(ActionMenuPresenter actionMenuPresenter) {
        this.t = actionMenuPresenter;
        this.t.a(this);
    }
}
