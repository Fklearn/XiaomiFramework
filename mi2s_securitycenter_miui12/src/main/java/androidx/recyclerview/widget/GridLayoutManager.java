package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.a.c;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;

public class GridLayoutManager extends LinearLayoutManager {
    boolean I = false;
    int J = -1;
    int[] K;
    View[] L;
    final SparseIntArray M = new SparseIntArray();
    final SparseIntArray N = new SparseIntArray();
    c O = new a();
    final Rect P = new Rect();
    private boolean Q;

    public static final class a extends c {
        public int a(int i) {
            return 1;
        }

        public int d(int i, int i2) {
            return i % i2;
        }
    }

    public static class b extends RecyclerView.h {
        int e = -1;
        int f = 0;

        public b(int i, int i2) {
            super(i, i2);
        }

        public b(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public b(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public b(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        public int e() {
            return this.e;
        }

        public int f() {
            return this.f;
        }
    }

    public static abstract class c {

        /* renamed from: a  reason: collision with root package name */
        final SparseIntArray f1086a = new SparseIntArray();

        /* renamed from: b  reason: collision with root package name */
        final SparseIntArray f1087b = new SparseIntArray();

        /* renamed from: c  reason: collision with root package name */
        private boolean f1088c = false;

        /* renamed from: d  reason: collision with root package name */
        private boolean f1089d = false;

        static int a(SparseIntArray sparseIntArray, int i) {
            int size = sparseIntArray.size() - 1;
            int i2 = 0;
            while (i2 <= size) {
                int i3 = (i2 + size) >>> 1;
                if (sparseIntArray.keyAt(i3) < i) {
                    i2 = i3 + 1;
                } else {
                    size = i3 - 1;
                }
            }
            int i4 = i2 - 1;
            if (i4 < 0 || i4 >= sparseIntArray.size()) {
                return -1;
            }
            return sparseIntArray.keyAt(i4);
        }

        public abstract int a(int i);

        /* access modifiers changed from: package-private */
        public int a(int i, int i2) {
            if (!this.f1089d) {
                return c(i, i2);
            }
            int i3 = this.f1087b.get(i, -1);
            if (i3 != -1) {
                return i3;
            }
            int c2 = c(i, i2);
            this.f1087b.put(i, c2);
            return c2;
        }

        public void a() {
            this.f1087b.clear();
        }

        /* access modifiers changed from: package-private */
        public int b(int i, int i2) {
            if (!this.f1088c) {
                return d(i, i2);
            }
            int i3 = this.f1086a.get(i, -1);
            if (i3 != -1) {
                return i3;
            }
            int d2 = d(i, i2);
            this.f1086a.put(i, d2);
            return d2;
        }

        public void b() {
            this.f1086a.clear();
        }

        public int c(int i, int i2) {
            int i3;
            int i4;
            int i5;
            int a2;
            if (!this.f1089d || (a2 = a(this.f1087b, i)) == -1) {
                i5 = 0;
                i4 = 0;
                i3 = 0;
            } else {
                i4 = this.f1087b.get(a2);
                i3 = a2 + 1;
                i5 = a(a2) + b(a2, i2);
                if (i5 == i2) {
                    i4++;
                    i5 = 0;
                }
            }
            int a3 = a(i);
            while (i3 < i) {
                int a4 = a(i3);
                int i6 = i5 + a4;
                if (i6 == i2) {
                    i4++;
                    i6 = 0;
                } else if (i6 > i2) {
                    i4++;
                    i6 = a4;
                }
                i3++;
            }
            return i5 + a3 > i2 ? i4 + 1 : i4;
        }

        public abstract int d(int i, int i2);
    }

    public GridLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        k(RecyclerView.g.a(context, attributeSet, i, i2).f1128b);
    }

    private void N() {
        int e = e();
        for (int i = 0; i < e; i++) {
            b bVar = (b) c(i).getLayoutParams();
            int a2 = bVar.a();
            this.M.put(a2, bVar.f());
            this.N.put(a2, bVar.e());
        }
    }

    private void O() {
        this.M.clear();
        this.N.clear();
    }

    private void P() {
        View[] viewArr = this.L;
        if (viewArr == null || viewArr.length != this.J) {
            this.L = new View[this.J];
        }
    }

    private void Q() {
        int i;
        int i2;
        if (I() == 1) {
            i2 = r() - p();
            i = o();
        } else {
            i2 = h() - n();
            i = q();
        }
        l(i2 - i);
    }

    private int a(RecyclerView.n nVar, RecyclerView.r rVar, int i) {
        if (!rVar.d()) {
            return this.O.a(i, this.J);
        }
        int a2 = nVar.a(i);
        if (a2 != -1) {
            return this.O.a(a2, this.J);
        }
        Log.w("GridLayoutManager", "Cannot find span size for pre layout position. " + i);
        return 0;
    }

    private void a(float f, int i) {
        l(Math.max(Math.round(f * ((float) this.J)), i));
    }

    private void a(View view, int i, int i2, boolean z) {
        RecyclerView.h hVar = (RecyclerView.h) view.getLayoutParams();
        if (z ? b(view, i, i2, hVar) : a(view, i, i2, hVar)) {
            view.measure(i, i2);
        }
    }

    private void a(RecyclerView.n nVar, RecyclerView.r rVar, int i, boolean z) {
        int i2;
        int i3;
        int i4 = 0;
        int i5 = -1;
        if (z) {
            i2 = 1;
            i5 = i;
            i3 = 0;
        } else {
            i3 = i - 1;
            i2 = -1;
        }
        while (i3 != i5) {
            View view = this.L[i3];
            b bVar = (b) view.getLayoutParams();
            bVar.f = c(nVar, rVar, l(view));
            bVar.e = i4;
            i4 += bVar.f;
            i3 += i2;
        }
    }

    static int[] a(int[] iArr, int i, int i2) {
        int i3;
        if (!(iArr != null && iArr.length == i + 1 && iArr[iArr.length - 1] == i2)) {
            iArr = new int[(i + 1)];
        }
        int i4 = 0;
        iArr[0] = 0;
        int i5 = i2 / i;
        int i6 = i2 % i;
        int i7 = 0;
        for (int i8 = 1; i8 <= i; i8++) {
            i4 += i6;
            if (i4 <= 0 || i - i4 >= i6) {
                i3 = i5;
            } else {
                i3 = i5 + 1;
                i4 -= i;
            }
            i7 += i3;
            iArr[i8] = i7;
        }
        return iArr;
    }

    private int b(RecyclerView.n nVar, RecyclerView.r rVar, int i) {
        if (!rVar.d()) {
            return this.O.b(i, this.J);
        }
        int i2 = this.N.get(i, -1);
        if (i2 != -1) {
            return i2;
        }
        int a2 = nVar.a(i);
        if (a2 != -1) {
            return this.O.b(a2, this.J);
        }
        Log.w("GridLayoutManager", "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + i);
        return 0;
    }

    private void b(View view, int i, boolean z) {
        int i2;
        int i3;
        b bVar = (b) view.getLayoutParams();
        Rect rect = bVar.f1132b;
        int i4 = rect.top + rect.bottom + bVar.topMargin + bVar.bottomMargin;
        int i5 = rect.left + rect.right + bVar.leftMargin + bVar.rightMargin;
        int f = f(bVar.e, bVar.f);
        if (this.s == 1) {
            i2 = RecyclerView.g.a(f, i, i5, bVar.width, false);
            i3 = RecyclerView.g.a(this.u.g(), i(), i4, bVar.height, true);
        } else {
            int a2 = RecyclerView.g.a(f, i, i4, bVar.height, false);
            int a3 = RecyclerView.g.a(this.u.g(), s(), i5, bVar.width, true);
            i3 = a2;
            i2 = a3;
        }
        a(view, i2, i3, z);
    }

    private void b(RecyclerView.n nVar, RecyclerView.r rVar, LinearLayoutManager.a aVar, int i) {
        boolean z = i == 1;
        int b2 = b(nVar, rVar, aVar.f1098b);
        if (z) {
            while (b2 > 0) {
                int i2 = aVar.f1098b;
                if (i2 > 0) {
                    aVar.f1098b = i2 - 1;
                    b2 = b(nVar, rVar, aVar.f1098b);
                } else {
                    return;
                }
            }
            return;
        }
        int a2 = rVar.a() - 1;
        int i3 = aVar.f1098b;
        while (i3 < a2) {
            int i4 = i3 + 1;
            int b3 = b(nVar, rVar, i4);
            if (b3 <= b2) {
                break;
            }
            i3 = i4;
            b2 = b3;
        }
        aVar.f1098b = i3;
    }

    private int c(RecyclerView.n nVar, RecyclerView.r rVar, int i) {
        if (!rVar.d()) {
            return this.O.a(i);
        }
        int i2 = this.M.get(i, -1);
        if (i2 != -1) {
            return i2;
        }
        int a2 = nVar.a(i);
        if (a2 != -1) {
            return this.O.a(a2);
        }
        Log.w("GridLayoutManager", "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + i);
        return 1;
    }

    private int i(RecyclerView.r rVar) {
        if (!(e() == 0 || rVar.a() == 0)) {
            F();
            boolean K2 = K();
            View b2 = b(!K2, true);
            View a2 = a(!K2, true);
            if (!(b2 == null || a2 == null)) {
                int a3 = this.O.a(l(b2), this.J);
                int a4 = this.O.a(l(a2), this.J);
                int min = Math.min(a3, a4);
                int max = this.x ? Math.max(0, ((this.O.a(rVar.a() - 1, this.J) + 1) - Math.max(a3, a4)) - 1) : Math.max(0, min);
                if (!K2) {
                    return max;
                }
                return Math.round((((float) max) * (((float) Math.abs(this.u.a(a2) - this.u.d(b2))) / ((float) ((this.O.a(l(a2), this.J) - this.O.a(l(b2), this.J)) + 1)))) + ((float) (this.u.f() - this.u.d(b2))));
            }
        }
        return 0;
    }

    private int j(RecyclerView.r rVar) {
        if (!(e() == 0 || rVar.a() == 0)) {
            F();
            View b2 = b(!K(), true);
            View a2 = a(!K(), true);
            if (!(b2 == null || a2 == null)) {
                if (!K()) {
                    return this.O.a(rVar.a() - 1, this.J) + 1;
                }
                return (int) ((((float) (this.u.a(a2) - this.u.d(b2))) / ((float) ((this.O.a(l(a2), this.J) - this.O.a(l(b2), this.J)) + 1))) * ((float) (this.O.a(rVar.a() - 1, this.J) + 1)));
            }
        }
        return 0;
    }

    private void l(int i) {
        this.K = a(this.K, this.J, i);
    }

    public boolean D() {
        return this.D == null && !this.I;
    }

    public int M() {
        return this.J;
    }

    public int a(int i, RecyclerView.n nVar, RecyclerView.r rVar) {
        Q();
        P();
        return super.a(i, nVar, rVar);
    }

    public int a(RecyclerView.n nVar, RecyclerView.r rVar) {
        if (this.s == 1) {
            return this.J;
        }
        if (rVar.a() < 1) {
            return 0;
        }
        return a(nVar, rVar, rVar.a() - 1) + 1;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00d7, code lost:
        if (r13 == (r2 > r8)) goto L_0x00cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00f7, code lost:
        if (r13 == r11) goto L_0x00b7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0105  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View a(android.view.View r24, int r25, androidx.recyclerview.widget.RecyclerView.n r26, androidx.recyclerview.widget.RecyclerView.r r27) {
        /*
            r23 = this;
            r0 = r23
            r1 = r26
            r2 = r27
            android.view.View r3 = r23.c((android.view.View) r24)
            r4 = 0
            if (r3 != 0) goto L_0x000e
            return r4
        L_0x000e:
            android.view.ViewGroup$LayoutParams r5 = r3.getLayoutParams()
            androidx.recyclerview.widget.GridLayoutManager$b r5 = (androidx.recyclerview.widget.GridLayoutManager.b) r5
            int r6 = r5.e
            int r5 = r5.f
            int r5 = r5 + r6
            android.view.View r7 = super.a((android.view.View) r24, (int) r25, (androidx.recyclerview.widget.RecyclerView.n) r26, (androidx.recyclerview.widget.RecyclerView.r) r27)
            if (r7 != 0) goto L_0x0020
            return r4
        L_0x0020:
            r7 = r25
            int r7 = r0.i((int) r7)
            r9 = 1
            if (r7 != r9) goto L_0x002b
            r7 = r9
            goto L_0x002c
        L_0x002b:
            r7 = 0
        L_0x002c:
            boolean r10 = r0.x
            if (r7 == r10) goto L_0x0032
            r7 = r9
            goto L_0x0033
        L_0x0032:
            r7 = 0
        L_0x0033:
            r10 = -1
            if (r7 == 0) goto L_0x003e
            int r7 = r23.e()
            int r7 = r7 - r9
            r11 = r10
            r12 = r11
            goto L_0x0045
        L_0x003e:
            int r7 = r23.e()
            r11 = r7
            r12 = r9
            r7 = 0
        L_0x0045:
            int r13 = r0.s
            if (r13 != r9) goto L_0x0051
            boolean r13 = r23.J()
            if (r13 == 0) goto L_0x0051
            r13 = r9
            goto L_0x0052
        L_0x0051:
            r13 = 0
        L_0x0052:
            int r14 = r0.a((androidx.recyclerview.widget.RecyclerView.n) r1, (androidx.recyclerview.widget.RecyclerView.r) r2, (int) r7)
            r8 = r10
            r17 = r8
            r15 = 0
            r16 = 0
            r10 = r4
        L_0x005d:
            if (r7 == r11) goto L_0x0147
            int r9 = r0.a((androidx.recyclerview.widget.RecyclerView.n) r1, (androidx.recyclerview.widget.RecyclerView.r) r2, (int) r7)
            android.view.View r1 = r0.c((int) r7)
            if (r1 != r3) goto L_0x006b
            goto L_0x0147
        L_0x006b:
            boolean r18 = r1.hasFocusable()
            if (r18 == 0) goto L_0x0085
            if (r9 == r14) goto L_0x0085
            if (r4 == 0) goto L_0x0077
            goto L_0x0147
        L_0x0077:
            r18 = r3
            r19 = r8
            r21 = r10
            r20 = r11
            r8 = r16
            r10 = r17
            goto L_0x0133
        L_0x0085:
            android.view.ViewGroup$LayoutParams r9 = r1.getLayoutParams()
            androidx.recyclerview.widget.GridLayoutManager$b r9 = (androidx.recyclerview.widget.GridLayoutManager.b) r9
            int r2 = r9.e
            r18 = r3
            int r3 = r9.f
            int r3 = r3 + r2
            boolean r19 = r1.hasFocusable()
            if (r19 == 0) goto L_0x009d
            if (r2 != r6) goto L_0x009d
            if (r3 != r5) goto L_0x009d
            return r1
        L_0x009d:
            boolean r19 = r1.hasFocusable()
            if (r19 == 0) goto L_0x00a5
            if (r4 == 0) goto L_0x00ad
        L_0x00a5:
            boolean r19 = r1.hasFocusable()
            if (r19 != 0) goto L_0x00b9
            if (r10 != 0) goto L_0x00b9
        L_0x00ad:
            r19 = r8
            r21 = r10
        L_0x00b1:
            r20 = r11
            r8 = r16
            r10 = r17
        L_0x00b7:
            r11 = 1
            goto L_0x0103
        L_0x00b9:
            int r19 = java.lang.Math.max(r2, r6)
            int r20 = java.lang.Math.min(r3, r5)
            r21 = r10
            int r10 = r20 - r19
            boolean r19 = r1.hasFocusable()
            if (r19 == 0) goto L_0x00da
            if (r10 <= r15) goto L_0x00d0
        L_0x00cd:
            r19 = r8
            goto L_0x00b1
        L_0x00d0:
            if (r10 != r15) goto L_0x00fa
            if (r2 <= r8) goto L_0x00d6
            r10 = 1
            goto L_0x00d7
        L_0x00d6:
            r10 = 0
        L_0x00d7:
            if (r13 != r10) goto L_0x00fa
            goto L_0x00cd
        L_0x00da:
            if (r4 != 0) goto L_0x00fa
            r19 = r8
            r20 = r11
            r8 = 0
            r11 = 1
            boolean r22 = r0.a((android.view.View) r1, (boolean) r8, (boolean) r11)
            if (r22 == 0) goto L_0x00fe
            r8 = r16
            if (r10 <= r8) goto L_0x00ef
            r10 = r17
            goto L_0x0103
        L_0x00ef:
            if (r10 != r8) goto L_0x0100
            r10 = r17
            if (r2 <= r10) goto L_0x00f6
            goto L_0x00f7
        L_0x00f6:
            r11 = 0
        L_0x00f7:
            if (r13 != r11) goto L_0x0102
            goto L_0x00b7
        L_0x00fa:
            r19 = r8
            r20 = r11
        L_0x00fe:
            r8 = r16
        L_0x0100:
            r10 = r17
        L_0x0102:
            r11 = 0
        L_0x0103:
            if (r11 == 0) goto L_0x0133
            boolean r11 = r1.hasFocusable()
            if (r11 == 0) goto L_0x0120
            int r4 = r9.e
            int r3 = java.lang.Math.min(r3, r5)
            int r2 = java.lang.Math.max(r2, r6)
            int r3 = r3 - r2
            r15 = r3
            r16 = r8
            r17 = r10
            r10 = r21
            r8 = r4
            r4 = r1
            goto L_0x013b
        L_0x0120:
            int r8 = r9.e
            int r3 = java.lang.Math.min(r3, r5)
            int r2 = java.lang.Math.max(r2, r6)
            int r3 = r3 - r2
            r10 = r1
            r16 = r3
            r17 = r8
            r8 = r19
            goto L_0x013b
        L_0x0133:
            r16 = r8
            r17 = r10
            r8 = r19
            r10 = r21
        L_0x013b:
            int r7 = r7 + r12
            r1 = r26
            r2 = r27
            r3 = r18
            r11 = r20
            r9 = 1
            goto L_0x005d
        L_0x0147:
            r21 = r10
            if (r4 == 0) goto L_0x014c
            goto L_0x014e
        L_0x014c:
            r4 = r21
        L_0x014e:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.GridLayoutManager.a(android.view.View, int, androidx.recyclerview.widget.RecyclerView$n, androidx.recyclerview.widget.RecyclerView$r):android.view.View");
    }

    /* access modifiers changed from: package-private */
    public View a(RecyclerView.n nVar, RecyclerView.r rVar, int i, int i2, int i3) {
        F();
        int f = this.u.f();
        int b2 = this.u.b();
        int i4 = i2 > i ? 1 : -1;
        View view = null;
        View view2 = null;
        while (i != i2) {
            View c2 = c(i);
            int l = l(c2);
            if (l >= 0 && l < i3 && b(nVar, rVar, l) == 0) {
                if (((RecyclerView.h) c2.getLayoutParams()).c()) {
                    if (view2 == null) {
                        view2 = c2;
                    }
                } else if (this.u.d(c2) < b2 && this.u.a(c2) >= f) {
                    return c2;
                } else {
                    if (view == null) {
                        view = c2;
                    }
                }
            }
            i += i4;
        }
        return view != null ? view : view2;
    }

    public RecyclerView.h a(Context context, AttributeSet attributeSet) {
        return new b(context, attributeSet);
    }

    public RecyclerView.h a(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof ViewGroup.MarginLayoutParams ? new b((ViewGroup.MarginLayoutParams) layoutParams) : new b(layoutParams);
    }

    public void a(Rect rect, int i, int i2) {
        int i3;
        int i4;
        if (this.K == null) {
            super.a(rect, i, i2);
        }
        int o = o() + p();
        int q = q() + n();
        if (this.s == 1) {
            i4 = RecyclerView.g.a(i2, rect.height() + q, l());
            int[] iArr = this.K;
            i3 = RecyclerView.g.a(i, iArr[iArr.length - 1] + o, m());
        } else {
            i3 = RecyclerView.g.a(i, rect.width() + o, m());
            int[] iArr2 = this.K;
            i4 = RecyclerView.g.a(i2, iArr2[iArr2.length - 1] + q, l());
        }
        c(i3, i4);
    }

    public void a(RecyclerView.n nVar, RecyclerView.r rVar, View view, androidx.core.view.a.c cVar) {
        boolean z;
        boolean z2;
        int i;
        int i2;
        int i3;
        int i4;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (!(layoutParams instanceof b)) {
            super.a(view, cVar);
            return;
        }
        b bVar = (b) layoutParams;
        int a2 = a(nVar, rVar, bVar.a());
        if (this.s == 0) {
            i4 = bVar.e();
            i3 = bVar.f();
            i = 1;
            z2 = false;
            z = false;
            i2 = a2;
        } else {
            i3 = 1;
            i2 = bVar.e();
            i = bVar.f();
            z2 = false;
            z = false;
            i4 = a2;
        }
        cVar.b((Object) c.C0013c.a(i4, i3, i2, i, z2, z));
    }

    /* access modifiers changed from: package-private */
    public void a(RecyclerView.n nVar, RecyclerView.r rVar, LinearLayoutManager.a aVar, int i) {
        super.a(nVar, rVar, aVar, i);
        Q();
        if (rVar.a() > 0 && !rVar.d()) {
            b(nVar, rVar, aVar, i);
        }
        P();
    }

    /* access modifiers changed from: package-private */
    public void a(RecyclerView.n nVar, RecyclerView.r rVar, LinearLayoutManager.c cVar, LinearLayoutManager.b bVar) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        View a2;
        RecyclerView.n nVar2 = nVar;
        RecyclerView.r rVar2 = rVar;
        LinearLayoutManager.c cVar2 = cVar;
        LinearLayoutManager.b bVar2 = bVar;
        int e = this.u.e();
        boolean z = false;
        boolean z2 = e != 1073741824;
        int i11 = e() > 0 ? this.K[this.J] : 0;
        if (z2) {
            Q();
        }
        boolean z3 = cVar2.e == 1;
        int i12 = this.J;
        if (!z3) {
            i12 = b(nVar2, rVar2, cVar2.f1108d) + c(nVar2, rVar2, cVar2.f1108d);
        }
        int i13 = i12;
        int i14 = 0;
        while (i14 < this.J && cVar2.a(rVar2) && i13 > 0) {
            int i15 = cVar2.f1108d;
            int c2 = c(nVar2, rVar2, i15);
            if (c2 <= this.J) {
                i13 -= c2;
                if (i13 < 0 || (a2 = cVar2.a(nVar2)) == null) {
                    break;
                }
                this.L[i14] = a2;
                i14++;
            } else {
                throw new IllegalArgumentException("Item at position " + i15 + " requires " + c2 + " spans but GridLayoutManager has only " + this.J + " spans.");
            }
        }
        if (i14 == 0) {
            bVar2.f1102b = true;
            return;
        }
        float f = 0.0f;
        a(nVar2, rVar2, i14, z3);
        int i16 = 0;
        int i17 = 0;
        while (i16 < i14) {
            View view = this.L[i16];
            if (cVar2.l == null) {
                if (z3) {
                    b(view);
                } else {
                    b(view, (int) z);
                }
            } else if (z3) {
                a(view);
            } else {
                a(view, z ? 1 : 0);
            }
            a(view, this.P);
            b(view, e, z);
            int b2 = this.u.b(view);
            if (b2 > i17) {
                i17 = b2;
            }
            float c3 = (((float) this.u.c(view)) * 1.0f) / ((float) ((b) view.getLayoutParams()).f);
            if (c3 > f) {
                f = c3;
            }
            i16++;
            z = false;
        }
        if (z2) {
            a(f, i11);
            i17 = 0;
            for (int i18 = 0; i18 < i14; i18++) {
                View view2 = this.L[i18];
                b(view2, 1073741824, true);
                int b3 = this.u.b(view2);
                if (b3 > i17) {
                    i17 = b3;
                }
            }
        }
        for (int i19 = 0; i19 < i14; i19++) {
            View view3 = this.L[i19];
            if (this.u.b(view3) != i17) {
                b bVar3 = (b) view3.getLayoutParams();
                Rect rect = bVar3.f1132b;
                int i20 = rect.top + rect.bottom + bVar3.topMargin + bVar3.bottomMargin;
                int i21 = rect.left + rect.right + bVar3.leftMargin + bVar3.rightMargin;
                int f2 = f(bVar3.e, bVar3.f);
                if (this.s == 1) {
                    i10 = RecyclerView.g.a(f2, 1073741824, i21, bVar3.width, false);
                    i9 = View.MeasureSpec.makeMeasureSpec(i17 - i20, 1073741824);
                } else {
                    int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i17 - i21, 1073741824);
                    i9 = RecyclerView.g.a(f2, 1073741824, i20, bVar3.height, false);
                    i10 = makeMeasureSpec;
                }
                a(view3, i10, i9, true);
            }
        }
        int i22 = 0;
        bVar2.f1101a = i17;
        if (this.s == 1) {
            if (cVar2.f == -1) {
                int i23 = cVar2.f1106b;
                i3 = i23 - i17;
                i2 = i23;
            } else {
                int i24 = cVar2.f1106b;
                i2 = i24 + i17;
                i3 = i24;
            }
            i4 = 0;
            i = 0;
        } else if (cVar2.f == -1) {
            i = cVar2.f1106b;
            i4 = i - i17;
            i3 = 0;
            i2 = 0;
        } else {
            int i25 = cVar2.f1106b;
            int i26 = i25 + i17;
            i3 = 0;
            i2 = 0;
            int i27 = i25;
            i = i26;
            i4 = i27;
        }
        while (i22 < i14) {
            View view4 = this.L[i22];
            b bVar4 = (b) view4.getLayoutParams();
            if (this.s != 1) {
                int q = q() + this.K[bVar4.e];
                i8 = i4;
                i7 = q;
                i5 = this.u.c(view4) + q;
                i6 = i;
            } else if (J()) {
                int o = o() + this.K[this.J - bVar4.e];
                i6 = o;
                i7 = i3;
                i5 = i2;
                i8 = o - this.u.c(view4);
            } else {
                int o2 = o() + this.K[bVar4.e];
                i8 = o2;
                i7 = i3;
                i5 = i2;
                i6 = this.u.c(view4) + o2;
            }
            a(view4, i8, i7, i6, i5);
            if (bVar4.c() || bVar4.b()) {
                bVar2.f1103c = true;
            }
            bVar2.f1104d |= view4.hasFocusable();
            i22++;
            i4 = i8;
            i3 = i7;
            i = i6;
            i2 = i5;
        }
        Arrays.fill(this.L, (Object) null);
    }

    /* access modifiers changed from: package-private */
    public void a(RecyclerView.r rVar, LinearLayoutManager.c cVar, RecyclerView.g.a aVar) {
        int i = this.J;
        for (int i2 = 0; i2 < this.J && cVar.a(rVar) && i > 0; i2++) {
            int i3 = cVar.f1108d;
            aVar.a(i3, Math.max(0, cVar.g));
            i -= this.O.a(i3);
            cVar.f1108d += cVar.e;
        }
    }

    public void a(RecyclerView recyclerView, int i, int i2) {
        this.O.b();
        this.O.a();
    }

    public void a(RecyclerView recyclerView, int i, int i2, int i3) {
        this.O.b();
        this.O.a();
    }

    public void a(RecyclerView recyclerView, int i, int i2, Object obj) {
        this.O.b();
        this.O.a();
    }

    public boolean a(RecyclerView.h hVar) {
        return hVar instanceof b;
    }

    public int b(int i, RecyclerView.n nVar, RecyclerView.r rVar) {
        Q();
        P();
        return super.b(i, nVar, rVar);
    }

    public int b(RecyclerView.n nVar, RecyclerView.r rVar) {
        if (this.s == 0) {
            return this.J;
        }
        if (rVar.a() < 1) {
            return 0;
        }
        return a(nVar, rVar, rVar.a() - 1) + 1;
    }

    public int b(RecyclerView.r rVar) {
        return this.Q ? i(rVar) : super.b(rVar);
    }

    public void b(RecyclerView recyclerView, int i, int i2) {
        this.O.b();
        this.O.a();
    }

    public void b(boolean z) {
        if (!z) {
            super.b(false);
            return;
        }
        throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
    }

    public int c(RecyclerView.r rVar) {
        return this.Q ? j(rVar) : super.c(rVar);
    }

    public RecyclerView.h c() {
        return this.s == 0 ? new b(-2, -1) : new b(-1, -2);
    }

    public void d(RecyclerView recyclerView) {
        this.O.b();
        this.O.a();
    }

    public int e(RecyclerView.r rVar) {
        return this.Q ? i(rVar) : super.e(rVar);
    }

    public void e(RecyclerView.n nVar, RecyclerView.r rVar) {
        if (rVar.d()) {
            N();
        }
        super.e(nVar, rVar);
        O();
    }

    /* access modifiers changed from: package-private */
    public int f(int i, int i2) {
        if (this.s != 1 || !J()) {
            int[] iArr = this.K;
            return iArr[i2 + i] - iArr[i];
        }
        int[] iArr2 = this.K;
        int i3 = this.J;
        return iArr2[i3 - i] - iArr2[(i3 - i) - i2];
    }

    public int f(RecyclerView.r rVar) {
        return this.Q ? j(rVar) : super.f(rVar);
    }

    public void g(RecyclerView.r rVar) {
        super.g(rVar);
        this.I = false;
    }

    public void k(int i) {
        if (i != this.J) {
            this.I = true;
            if (i >= 1) {
                this.J = i;
                this.O.b();
                z();
                return;
            }
            throw new IllegalArgumentException("Span count should be at least 1. Provided " + i);
        }
    }
}
