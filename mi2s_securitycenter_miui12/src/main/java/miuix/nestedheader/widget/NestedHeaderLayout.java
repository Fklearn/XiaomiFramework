package miuix.nestedheader.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.RequiresApi;
import d.a.h;
import d.e.b;
import java.util.ArrayList;
import java.util.List;
import miuix.nestedheader.widget.c;

public class NestedHeaderLayout extends c {
    private float A;
    private View B;
    private View C;
    private View D;
    private View E;
    /* access modifiers changed from: private */
    public int F;
    /* access modifiers changed from: private */
    public int G;
    private int H;
    private int I;
    private int J;
    private int K;
    private int L;
    private int M;
    private int N;
    private int O;
    /* access modifiers changed from: private */
    public boolean P;
    /* access modifiers changed from: private */
    public boolean Q;
    /* access modifiers changed from: private */
    public boolean R;
    private a S;
    private c.a T;
    private int u;
    private int v;
    private int w;
    private int x;
    private float y;
    private float z;

    public interface a {
        void a(View view);

        void b(View view);

        void c(View view);

        void d(View view);
    }

    public NestedHeaderLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public NestedHeaderLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NestedHeaderLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.H = 0;
        this.I = 0;
        this.J = 0;
        this.K = 0;
        this.L = 0;
        this.M = 0;
        this.N = 0;
        this.O = 0;
        this.P = false;
        this.Q = false;
        this.R = true;
        this.T = new a(this);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, d.e.c.NestedHeaderLayout);
        this.u = obtainStyledAttributes.getResourceId(d.e.c.NestedHeaderLayout_headerView, b.header_view);
        this.v = obtainStyledAttributes.getResourceId(d.e.c.NestedHeaderLayout_triggerView, b.trigger_view);
        this.w = obtainStyledAttributes.getResourceId(d.e.c.NestedHeaderLayout_headerContentId, b.header_content_view);
        this.x = obtainStyledAttributes.getResourceId(d.e.c.NestedHeaderLayout_triggerContentId, b.trigger_content_view);
        this.z = obtainStyledAttributes.getDimension(d.e.c.NestedHeaderLayout_headerContentMinHeight, context.getResources().getDimension(d.e.a.miuix_nested_header_layout_content_min_height));
        this.A = obtainStyledAttributes.getDimension(d.e.c.NestedHeaderLayout_triggerContentMinHeight, context.getResources().getDimension(d.e.a.miuix_nested_header_layout_content_min_height));
        this.y = obtainStyledAttributes.getDimension(d.e.c.NestedHeaderLayout_rangeOffset, 0.0f);
        obtainStyledAttributes.recycle();
        a(this.T);
    }

    private List<View> a(View view) {
        return a(view, this.w == b.header_content_view);
    }

    private List<View> a(View view, boolean z2) {
        if (view == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        if (z2) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    arrayList.add(viewGroup.getChildAt(i));
                }
            } else if (view != null) {
                arrayList.add(view);
            }
            return arrayList;
        }
        arrayList.add(view);
        return arrayList;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0064, code lost:
        if (getHeaderViewVisible() == false) goto L_0x0042;
     */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x006f  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0073 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:45:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(int r2, int r3, boolean r4) {
        /*
            r1 = this;
            miuix.nestedheader.widget.NestedHeaderLayout$a r0 = r1.S
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            if (r4 == 0) goto L_0x003a
            if (r3 != 0) goto L_0x0017
            boolean r4 = r1.getHeaderViewVisible()
            if (r4 == 0) goto L_0x0017
            miuix.nestedheader.widget.NestedHeaderLayout$a r4 = r1.S
            android.view.View r0 = r1.B
            r4.d(r0)
            goto L_0x0028
        L_0x0017:
            int r4 = r1.G
            if (r3 != r4) goto L_0x0028
            boolean r4 = r1.getTriggerViewVisible()
            if (r4 == 0) goto L_0x0028
            miuix.nestedheader.widget.NestedHeaderLayout$a r4 = r1.S
            android.view.View r0 = r1.C
            r4.b(r0)
        L_0x0028:
            if (r2 >= 0) goto L_0x0082
            if (r3 <= 0) goto L_0x0082
            boolean r2 = r1.getHeaderViewVisible()
            if (r2 == 0) goto L_0x0082
            miuix.nestedheader.widget.NestedHeaderLayout$a r2 = r1.S
            android.view.View r3 = r1.B
            r2.d(r3)
            goto L_0x0082
        L_0x003a:
            if (r3 != 0) goto L_0x004a
            boolean r4 = r1.getTriggerViewVisible()
            if (r4 == 0) goto L_0x004a
        L_0x0042:
            miuix.nestedheader.widget.NestedHeaderLayout$a r4 = r1.S
            android.view.View r0 = r1.C
            r4.c(r0)
            goto L_0x0067
        L_0x004a:
            int r4 = r1.F
            if (r3 != r4) goto L_0x005c
            boolean r4 = r1.getHeaderViewVisible()
            if (r4 == 0) goto L_0x005c
            miuix.nestedheader.widget.NestedHeaderLayout$a r4 = r1.S
            android.view.View r0 = r1.B
            r4.a(r0)
            goto L_0x0067
        L_0x005c:
            int r4 = r1.F
            if (r3 != r4) goto L_0x0067
            boolean r4 = r1.getHeaderViewVisible()
            if (r4 != 0) goto L_0x0067
            goto L_0x0042
        L_0x0067:
            boolean r4 = r1.getHeaderViewVisible()
            if (r4 == 0) goto L_0x006f
            r4 = 0
            goto L_0x0071
        L_0x006f:
            int r4 = r1.F
        L_0x0071:
            if (r2 <= r4) goto L_0x0082
            if (r3 >= r4) goto L_0x0082
            boolean r2 = r1.getTriggerViewVisible()
            if (r2 == 0) goto L_0x0082
            miuix.nestedheader.widget.NestedHeaderLayout$a r2 = r1.S
            android.view.View r3 = r1.C
            r2.c(r3)
        L_0x0082:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.nestedheader.widget.NestedHeaderLayout.a(int, int, boolean):void");
    }

    private void a(View view, View view2, int i, int i2) {
        view.layout(view.getLeft(), i, view.getRight(), Math.max(i, view.getMeasuredHeight() + i + i2));
        if (view != view2) {
            view2.layout(view2.getLeft(), view2.getTop(), view2.getRight(), Math.max(view2.getTop(), view2.getTop() + view2.getMeasuredHeight() + i2));
        }
    }

    private void a(List<View> list, float f) {
        if (list != null) {
            float max = Math.max(0.0f, Math.min(1.0f, f));
            for (View alpha : list) {
                alpha.setAlpha(max);
            }
        }
    }

    private void a(boolean z2, boolean z3, boolean z4) {
        boolean z5;
        int i;
        boolean z6;
        int i2;
        int i3;
        View view = this.B;
        if (view == null || view.getVisibility() == 8) {
            i = 0;
            z5 = false;
        } else {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.B.getLayoutParams();
            this.H = marginLayoutParams.bottomMargin;
            this.I = marginLayoutParams.topMargin;
            this.M = this.B.getMeasuredHeight();
            i = ((int) (((((float) (-this.M)) + this.y) - ((float) this.I)) - ((float) this.H))) + 0;
            z5 = true;
        }
        View view2 = this.C;
        if (view2 == null || view2.getVisibility() == 8) {
            i3 = i;
            i2 = 0;
            z6 = false;
        } else {
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) this.C.getLayoutParams();
            this.J = marginLayoutParams2.bottomMargin;
            this.K = marginLayoutParams2.topMargin;
            this.N = this.C.getMeasuredHeight();
            int i4 = this.N + this.K + this.J + 0;
            if (!z5) {
                i3 = -i4;
                z6 = true;
                i2 = 0;
            } else {
                i3 = i;
                z6 = true;
                i2 = i4;
            }
        }
        this.F = i3;
        this.G = i2;
        a(i3, i2, z5, z6, z2, z3, z4);
    }

    private List<View> b(View view) {
        return a(view, this.x == b.trigger_content_view);
    }

    /* access modifiers changed from: private */
    public void d(int i) {
        h c2 = d.a.b.c(new Object[0]);
        c2.setTo("targe", Integer.valueOf(getScrollingProgress()));
        d.a.a.a aVar = new d.a.a.a();
        aVar.a(new b(this));
        c2.to("targe", Integer.valueOf(i), aVar);
    }

    /* access modifiers changed from: private */
    public void e(int i) {
        c(i);
        a(i);
    }

    /* access modifiers changed from: protected */
    public void a(int i) {
        int i2;
        int i3;
        super.a(i);
        View view = this.C;
        if (view == null || view.getVisibility() == 8) {
            i3 = i;
            i2 = 0;
        } else {
            i3 = i - Math.max(0, Math.min(this.G, i));
            int max = Math.max(this.F, Math.min(this.G, i));
            int i4 = this.K;
            View view2 = this.B;
            if (view2 == null || view2.getVisibility() == 8) {
                i2 = this.K + this.J + this.N;
                max += i2;
            } else {
                i4 = this.I + this.M + this.H + this.K;
                i2 = 0;
            }
            View view3 = this.E;
            if (view3 == null) {
                view3 = this.C;
            }
            a(this.C, view3, i4, ((max - this.J) - this.K) - this.N);
            float f = ((float) (max - this.J)) / this.A;
            this.C.setAlpha(Math.max(0.0f, Math.min(1.0f, f)));
            a(b(view3), f - 1.0f);
        }
        View view4 = this.B;
        if (!(view4 == null || view4.getVisibility() == 8)) {
            int i5 = this.L + this.I;
            View view5 = this.D;
            if (view5 == null) {
                view5 = this.B;
            }
            a(this.B, view5, i5, i3);
            float f2 = this.z;
            float f3 = (((float) i3) + f2) / f2;
            this.B.setAlpha(Math.max(0.0f, Math.min(1.0f, f3 + 1.0f)));
            a(a(view5), f3);
            i2 = this.M + this.I + this.H;
        }
        View view6 = this.f8874d;
        view6.offsetTopAndBottom((i2 + i) - view6.getTop());
        int i6 = this.O;
        if (i - i6 > 0) {
            a(i6, i, true);
        } else if (i - i6 < 0) {
            a(i6, i, false);
        }
        this.O = i;
        a(a());
    }

    public boolean a() {
        return getHeaderViewVisible() && getScrollingProgress() >= 0;
    }

    public boolean getHeaderViewVisible() {
        View view = this.B;
        return view != null && view.getVisibility() == 0;
    }

    public boolean getTriggerViewVisible() {
        View view = this.C;
        return view != null && view.getVisibility() == 0;
    }

    /* access modifiers changed from: protected */
    @RequiresApi(api = 21)
    public void onFinishInflate() {
        super.onFinishInflate();
        this.B = findViewById(this.u);
        this.C = findViewById(this.v);
        if (this.B == null && this.C == null) {
            throw new IllegalArgumentException("The headerView or triggerView attribute is required and must refer to a valid child.");
        }
        View view = this.B;
        if (view != null) {
            this.D = view.findViewById(this.w);
            if (this.D == null) {
                this.D = this.B.findViewById(16908318);
            }
        }
        View view2 = this.C;
        if (view2 != null) {
            this.E = view2.findViewById(this.x);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z2, int i, int i2, int i3, int i4) {
        super.onLayout(z2, i, i2, i3, i4);
        a(true, false, false);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        View view = this.B;
        if (view != null) {
            this.L = view.getTop();
        }
    }

    public void setAutoAllClose(boolean z2) {
        int i;
        if (!z2 || getScrollingProgress() <= (i = this.F)) {
            e(this.F);
        } else {
            d(i);
        }
    }

    public void setAutoAllOpen(boolean z2) {
        int i;
        if (!z2 || getScrollingProgress() >= (i = this.G)) {
            e(this.G);
        } else {
            d(i);
        }
    }

    public void setAutoAnim(boolean z2) {
        this.R = z2;
    }

    public void setAutoHeaderClose(boolean z2) {
        int i;
        if (getHeaderViewVisible() && getScrollingProgress() > (i = this.F)) {
            if (z2) {
                d(i);
            } else if (getHeaderViewVisible()) {
                e(this.F);
            }
        }
    }

    public void setAutoHeaderOpen(boolean z2) {
        if (getHeaderViewVisible() && getScrollingProgress() < 0) {
            if (z2) {
                d(0);
            } else {
                e(0);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0021, code lost:
        r0 = getScrollingProgress();
        r2 = r3.F;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setAutoTriggerClose(boolean r4) {
        /*
            r3 = this;
            boolean r0 = r3.getTriggerViewVisible()
            r1 = -1
            if (r0 == 0) goto L_0x0015
            boolean r0 = r3.getHeaderViewVisible()
            if (r0 == 0) goto L_0x0015
            int r0 = r3.getScrollingProgress()
            if (r0 <= 0) goto L_0x0015
            r0 = 0
            goto L_0x002c
        L_0x0015:
            boolean r0 = r3.getTriggerViewVisible()
            if (r0 == 0) goto L_0x002b
            boolean r0 = r3.getHeaderViewVisible()
            if (r0 != 0) goto L_0x002b
            int r0 = r3.getScrollingProgress()
            int r2 = r3.F
            if (r0 <= r2) goto L_0x002b
            r0 = r2
            goto L_0x002c
        L_0x002b:
            r0 = r1
        L_0x002c:
            if (r0 == r1) goto L_0x0034
            if (r4 == 0) goto L_0x0034
            r3.d((int) r0)
            goto L_0x0039
        L_0x0034:
            if (r0 == r1) goto L_0x0039
            r3.e((int) r0)
        L_0x0039:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.nestedheader.widget.NestedHeaderLayout.setAutoTriggerClose(boolean):void");
    }

    public void setAutoTriggerOpen(boolean z2) {
        int i;
        if (getTriggerViewVisible() && getScrollingProgress() < (i = this.G)) {
            if (z2) {
                d(i);
            } else {
                e(i);
            }
        }
    }

    public void setHeaderViewVisible(boolean z2) {
        View view = this.B;
        if (view != null) {
            view.setVisibility(z2 ? 0 : 8);
            a(false, false, z2);
        }
    }

    public void setNestedHeaderChangedListener(a aVar) {
        this.S = aVar;
    }

    public void setTriggerViewVisible(boolean z2) {
        View view = this.C;
        if (view != null) {
            view.setVisibility(z2 ? 0 : 8);
            a(false, z2, false);
        }
    }
}
