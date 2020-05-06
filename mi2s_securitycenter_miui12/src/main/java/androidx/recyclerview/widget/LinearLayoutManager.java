package androidx.recyclerview.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LinearLayoutManager extends RecyclerView.g implements C0179u, RecyclerView.q.a {
    int A;
    int B;
    private boolean C;
    SavedState D;
    final a E;
    private final b F;
    private int G;
    private int[] H;
    int s;
    private c t;
    B u;
    private boolean v;
    private boolean w;
    boolean x;
    private boolean y;
    private boolean z;

    @SuppressLint({"BanParcelableUsage"})
    @RestrictTo({RestrictTo.a.LIBRARY})
    public static class SavedState implements Parcelable {
        public static final Parcelable.Creator<SavedState> CREATOR = new w();
        boolean mAnchorLayoutFromEnd;
        int mAnchorOffset;
        int mAnchorPosition;

        public SavedState() {
        }

        SavedState(Parcel parcel) {
            this.mAnchorPosition = parcel.readInt();
            this.mAnchorOffset = parcel.readInt();
            this.mAnchorLayoutFromEnd = parcel.readInt() != 1 ? false : true;
        }

        public SavedState(SavedState savedState) {
            this.mAnchorPosition = savedState.mAnchorPosition;
            this.mAnchorOffset = savedState.mAnchorOffset;
            this.mAnchorLayoutFromEnd = savedState.mAnchorLayoutFromEnd;
        }

        public int describeContents() {
            return 0;
        }

        /* access modifiers changed from: package-private */
        public boolean hasValidAnchor() {
            return this.mAnchorPosition >= 0;
        }

        /* access modifiers changed from: package-private */
        public void invalidateAnchor() {
            this.mAnchorPosition = -1;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mAnchorPosition);
            parcel.writeInt(this.mAnchorOffset);
            parcel.writeInt(this.mAnchorLayoutFromEnd ? 1 : 0);
        }
    }

    static class a {

        /* renamed from: a  reason: collision with root package name */
        B f1097a;

        /* renamed from: b  reason: collision with root package name */
        int f1098b;

        /* renamed from: c  reason: collision with root package name */
        int f1099c;

        /* renamed from: d  reason: collision with root package name */
        boolean f1100d;
        boolean e;

        a() {
            b();
        }

        /* access modifiers changed from: package-private */
        public void a() {
            this.f1099c = this.f1100d ? this.f1097a.b() : this.f1097a.f();
        }

        public void a(View view, int i) {
            this.f1099c = this.f1100d ? this.f1097a.a(view) + this.f1097a.h() : this.f1097a.d(view);
            this.f1098b = i;
        }

        /* access modifiers changed from: package-private */
        public boolean a(View view, RecyclerView.r rVar) {
            RecyclerView.h hVar = (RecyclerView.h) view.getLayoutParams();
            return !hVar.c() && hVar.a() >= 0 && hVar.a() < rVar.a();
        }

        /* access modifiers changed from: package-private */
        public void b() {
            this.f1098b = -1;
            this.f1099c = Integer.MIN_VALUE;
            this.f1100d = false;
            this.e = false;
        }

        public void b(View view, int i) {
            int h = this.f1097a.h();
            if (h >= 0) {
                a(view, i);
                return;
            }
            this.f1098b = i;
            if (this.f1100d) {
                int b2 = (this.f1097a.b() - h) - this.f1097a.a(view);
                this.f1099c = this.f1097a.b() - b2;
                if (b2 > 0) {
                    int b3 = this.f1099c - this.f1097a.b(view);
                    int f = this.f1097a.f();
                    int min = b3 - (f + Math.min(this.f1097a.d(view) - f, 0));
                    if (min < 0) {
                        this.f1099c += Math.min(b2, -min);
                        return;
                    }
                    return;
                }
                return;
            }
            int d2 = this.f1097a.d(view);
            int f2 = d2 - this.f1097a.f();
            this.f1099c = d2;
            if (f2 > 0) {
                int b4 = (this.f1097a.b() - Math.min(0, (this.f1097a.b() - h) - this.f1097a.a(view))) - (d2 + this.f1097a.b(view));
                if (b4 < 0) {
                    this.f1099c -= Math.min(f2, -b4);
                }
            }
        }

        public String toString() {
            return "AnchorInfo{mPosition=" + this.f1098b + ", mCoordinate=" + this.f1099c + ", mLayoutFromEnd=" + this.f1100d + ", mValid=" + this.e + '}';
        }
    }

    protected static class b {

        /* renamed from: a  reason: collision with root package name */
        public int f1101a;

        /* renamed from: b  reason: collision with root package name */
        public boolean f1102b;

        /* renamed from: c  reason: collision with root package name */
        public boolean f1103c;

        /* renamed from: d  reason: collision with root package name */
        public boolean f1104d;

        protected b() {
        }

        /* access modifiers changed from: package-private */
        public void a() {
            this.f1101a = 0;
            this.f1102b = false;
            this.f1103c = false;
            this.f1104d = false;
        }
    }

    static class c {

        /* renamed from: a  reason: collision with root package name */
        boolean f1105a = true;

        /* renamed from: b  reason: collision with root package name */
        int f1106b;

        /* renamed from: c  reason: collision with root package name */
        int f1107c;

        /* renamed from: d  reason: collision with root package name */
        int f1108d;
        int e;
        int f;
        int g;
        int h = 0;
        int i = 0;
        boolean j = false;
        int k;
        List<RecyclerView.u> l = null;
        boolean m;

        c() {
        }

        private View b() {
            int size = this.l.size();
            for (int i2 = 0; i2 < size; i2++) {
                View view = this.l.get(i2).itemView;
                RecyclerView.h hVar = (RecyclerView.h) view.getLayoutParams();
                if (!hVar.c() && this.f1108d == hVar.a()) {
                    a(view);
                    return view;
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public View a(RecyclerView.n nVar) {
            if (this.l != null) {
                return b();
            }
            View d2 = nVar.d(this.f1108d);
            this.f1108d += this.e;
            return d2;
        }

        public void a() {
            a((View) null);
        }

        public void a(View view) {
            View b2 = b(view);
            this.f1108d = b2 == null ? -1 : ((RecyclerView.h) b2.getLayoutParams()).a();
        }

        /* access modifiers changed from: package-private */
        public boolean a(RecyclerView.r rVar) {
            int i2 = this.f1108d;
            return i2 >= 0 && i2 < rVar.a();
        }

        public View b(View view) {
            int a2;
            int size = this.l.size();
            View view2 = null;
            int i2 = Integer.MAX_VALUE;
            for (int i3 = 0; i3 < size; i3++) {
                View view3 = this.l.get(i3).itemView;
                RecyclerView.h hVar = (RecyclerView.h) view3.getLayoutParams();
                if (view3 != view && !hVar.c() && (a2 = (hVar.a() - this.f1108d) * this.e) >= 0 && a2 < i2) {
                    if (a2 == 0) {
                        return view3;
                    }
                    view2 = view3;
                    i2 = a2;
                }
            }
            return view2;
        }
    }

    public LinearLayoutManager(Context context) {
        this(context, 1, false);
    }

    public LinearLayoutManager(Context context, int i, boolean z2) {
        this.s = 1;
        this.w = false;
        this.x = false;
        this.y = false;
        this.z = true;
        this.A = -1;
        this.B = Integer.MIN_VALUE;
        this.D = null;
        this.E = new a();
        this.F = new b();
        this.G = 2;
        this.H = new int[2];
        j(i);
        a(z2);
    }

    public LinearLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        this.s = 1;
        this.w = false;
        this.x = false;
        this.y = false;
        this.z = true;
        this.A = -1;
        this.B = Integer.MIN_VALUE;
        this.D = null;
        this.E = new a();
        this.F = new b();
        this.G = 2;
        this.H = new int[2];
        RecyclerView.g.b a2 = RecyclerView.g.a(context, attributeSet, i, i2);
        j(a2.f1127a);
        a(a2.f1129c);
        b(a2.f1130d);
    }

    private View M() {
        return e(0, e());
    }

    private View N() {
        return e(e() - 1, -1);
    }

    private View O() {
        return this.x ? M() : N();
    }

    private View P() {
        return this.x ? N() : M();
    }

    private View Q() {
        return c(this.x ? 0 : e() - 1);
    }

    private View R() {
        return c(this.x ? e() - 1 : 0);
    }

    private void S() {
        this.x = (this.s == 1 || !J()) ? this.w : !this.w;
    }

    private int a(int i, RecyclerView.n nVar, RecyclerView.r rVar, boolean z2) {
        int b2;
        int b3 = this.u.b() - i;
        if (b3 <= 0) {
            return 0;
        }
        int i2 = -c(-b3, nVar, rVar);
        int i3 = i + i2;
        if (!z2 || (b2 = this.u.b() - i3) <= 0) {
            return i2;
        }
        this.u.a(b2);
        return b2 + i2;
    }

    private void a(int i, int i2, boolean z2, RecyclerView.r rVar) {
        int i3;
        this.t.m = L();
        this.t.f = i;
        int[] iArr = this.H;
        boolean z3 = false;
        iArr[0] = 0;
        iArr[1] = 0;
        a(rVar, iArr);
        int max = Math.max(0, this.H[0]);
        int max2 = Math.max(0, this.H[1]);
        if (i == 1) {
            z3 = true;
        }
        this.t.h = z3 ? max2 : max;
        c cVar = this.t;
        if (!z3) {
            max = max2;
        }
        cVar.i = max;
        int i4 = -1;
        if (z3) {
            this.t.h += this.u.c();
            View Q = Q();
            c cVar2 = this.t;
            if (!this.x) {
                i4 = 1;
            }
            cVar2.e = i4;
            c cVar3 = this.t;
            int l = l(Q);
            c cVar4 = this.t;
            cVar3.f1108d = l + cVar4.e;
            cVar4.f1106b = this.u.a(Q);
            i3 = this.u.a(Q) - this.u.b();
        } else {
            View R = R();
            this.t.h += this.u.f();
            c cVar5 = this.t;
            if (this.x) {
                i4 = 1;
            }
            cVar5.e = i4;
            c cVar6 = this.t;
            int l2 = l(R);
            c cVar7 = this.t;
            cVar6.f1108d = l2 + cVar7.e;
            cVar7.f1106b = this.u.d(R);
            i3 = (-this.u.d(R)) + this.u.f();
        }
        c cVar8 = this.t;
        cVar8.f1107c = i2;
        if (z2) {
            cVar8.f1107c -= i3;
        }
        this.t.g = i3;
    }

    private void a(a aVar) {
        f(aVar.f1098b, aVar.f1099c);
    }

    private void a(RecyclerView.n nVar, int i, int i2) {
        if (i != i2) {
            if (i2 > i) {
                for (int i3 = i2 - 1; i3 >= i; i3--) {
                    a(i3, nVar);
                }
                return;
            }
            while (i > i2) {
                a(i, nVar);
                i--;
            }
        }
    }

    private void a(RecyclerView.n nVar, c cVar) {
        if (cVar.f1105a && !cVar.m) {
            int i = cVar.g;
            int i2 = cVar.i;
            if (cVar.f == -1) {
                b(nVar, i, i2);
            } else {
                c(nVar, i, i2);
            }
        }
    }

    private boolean a(RecyclerView.n nVar, RecyclerView.r rVar, a aVar) {
        boolean z2 = false;
        if (e() == 0) {
            return false;
        }
        View g = g();
        if (g != null && aVar.a(g, rVar)) {
            aVar.b(g, l(g));
            return true;
        } else if (this.v != this.y) {
            return false;
        } else {
            View h = aVar.f1100d ? h(nVar, rVar) : i(nVar, rVar);
            if (h == null) {
                return false;
            }
            aVar.a(h, l(h));
            if (!rVar.d() && D()) {
                if (this.u.d(h) >= this.u.b() || this.u.a(h) < this.u.f()) {
                    z2 = true;
                }
                if (z2) {
                    aVar.f1099c = aVar.f1100d ? this.u.b() : this.u.f();
                }
            }
            return true;
        }
    }

    private boolean a(RecyclerView.r rVar, a aVar) {
        int i;
        boolean z2 = false;
        if (!rVar.d() && (i = this.A) != -1) {
            if (i < 0 || i >= rVar.a()) {
                this.A = -1;
                this.B = Integer.MIN_VALUE;
            } else {
                aVar.f1098b = this.A;
                SavedState savedState = this.D;
                if (savedState != null && savedState.hasValidAnchor()) {
                    aVar.f1100d = this.D.mAnchorLayoutFromEnd;
                    aVar.f1099c = aVar.f1100d ? this.u.b() - this.D.mAnchorOffset : this.u.f() + this.D.mAnchorOffset;
                    return true;
                } else if (this.B == Integer.MIN_VALUE) {
                    View b2 = b(this.A);
                    if (b2 == null) {
                        if (e() > 0) {
                            if ((this.A < l(c(0))) == this.x) {
                                z2 = true;
                            }
                            aVar.f1100d = z2;
                        }
                        aVar.a();
                    } else if (this.u.b(b2) > this.u.g()) {
                        aVar.a();
                        return true;
                    } else if (this.u.d(b2) - this.u.f() < 0) {
                        aVar.f1099c = this.u.f();
                        aVar.f1100d = false;
                        return true;
                    } else if (this.u.b() - this.u.a(b2) < 0) {
                        aVar.f1099c = this.u.b();
                        aVar.f1100d = true;
                        return true;
                    } else {
                        aVar.f1099c = aVar.f1100d ? this.u.a(b2) + this.u.h() : this.u.d(b2);
                    }
                    return true;
                } else {
                    boolean z3 = this.x;
                    aVar.f1100d = z3;
                    aVar.f1099c = z3 ? this.u.b() - this.B : this.u.f() + this.B;
                    return true;
                }
            }
        }
        return false;
    }

    private int b(int i, RecyclerView.n nVar, RecyclerView.r rVar, boolean z2) {
        int f;
        int f2 = i - this.u.f();
        if (f2 <= 0) {
            return 0;
        }
        int i2 = -c(f2, nVar, rVar);
        int i3 = i + i2;
        if (!z2 || (f = i3 - this.u.f()) <= 0) {
            return i2;
        }
        this.u.a(-f);
        return i2 - f;
    }

    private void b(a aVar) {
        g(aVar.f1098b, aVar.f1099c);
    }

    private void b(RecyclerView.n nVar, int i, int i2) {
        int e = e();
        if (i >= 0) {
            int a2 = (this.u.a() - i) + i2;
            if (this.x) {
                for (int i3 = 0; i3 < e; i3++) {
                    View c2 = c(i3);
                    if (this.u.d(c2) < a2 || this.u.f(c2) < a2) {
                        a(nVar, 0, i3);
                        return;
                    }
                }
                return;
            }
            int i4 = e - 1;
            for (int i5 = i4; i5 >= 0; i5--) {
                View c3 = c(i5);
                if (this.u.d(c3) < a2 || this.u.f(c3) < a2) {
                    a(nVar, i4, i5);
                    return;
                }
            }
        }
    }

    private void b(RecyclerView.n nVar, RecyclerView.r rVar, int i, int i2) {
        RecyclerView.n nVar2 = nVar;
        RecyclerView.r rVar2 = rVar;
        if (rVar.e() && e() != 0 && !rVar.d() && D()) {
            List<RecyclerView.u> f = nVar.f();
            int size = f.size();
            int l = l(c(0));
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < size; i5++) {
                RecyclerView.u uVar = f.get(i5);
                if (!uVar.isRemoved()) {
                    boolean z2 = true;
                    if ((uVar.getLayoutPosition() < l) != this.x) {
                        z2 = true;
                    }
                    if (z2) {
                        i3 += this.u.b(uVar.itemView);
                    } else {
                        i4 += this.u.b(uVar.itemView);
                    }
                }
            }
            this.t.l = f;
            if (i3 > 0) {
                g(l(R()), i);
                c cVar = this.t;
                cVar.h = i3;
                cVar.f1107c = 0;
                cVar.a();
                a(nVar2, this.t, rVar2, false);
            }
            if (i4 > 0) {
                f(l(Q()), i2);
                c cVar2 = this.t;
                cVar2.h = i4;
                cVar2.f1107c = 0;
                cVar2.a();
                a(nVar2, this.t, rVar2, false);
            }
            this.t.l = null;
        }
    }

    private void b(RecyclerView.n nVar, RecyclerView.r rVar, a aVar) {
        if (!a(rVar, aVar) && !a(nVar, rVar, aVar)) {
            aVar.a();
            aVar.f1098b = this.y ? rVar.a() - 1 : 0;
        }
    }

    private void c(RecyclerView.n nVar, int i, int i2) {
        if (i >= 0) {
            int i3 = i - i2;
            int e = e();
            if (this.x) {
                int i4 = e - 1;
                for (int i5 = i4; i5 >= 0; i5--) {
                    View c2 = c(i5);
                    if (this.u.a(c2) > i3 || this.u.e(c2) > i3) {
                        a(nVar, i4, i5);
                        return;
                    }
                }
                return;
            }
            for (int i6 = 0; i6 < e; i6++) {
                View c3 = c(i6);
                if (this.u.a(c3) > i3 || this.u.e(c3) > i3) {
                    a(nVar, 0, i6);
                    return;
                }
            }
        }
    }

    private View f(RecyclerView.n nVar, RecyclerView.r rVar) {
        return a(nVar, rVar, 0, e(), rVar.a());
    }

    private void f(int i, int i2) {
        this.t.f1107c = this.u.b() - i2;
        this.t.e = this.x ? -1 : 1;
        c cVar = this.t;
        cVar.f1108d = i;
        cVar.f = 1;
        cVar.f1106b = i2;
        cVar.g = Integer.MIN_VALUE;
    }

    private View g(RecyclerView.n nVar, RecyclerView.r rVar) {
        return a(nVar, rVar, e() - 1, -1, rVar.a());
    }

    private void g(int i, int i2) {
        this.t.f1107c = i2 - this.u.f();
        c cVar = this.t;
        cVar.f1108d = i;
        cVar.e = this.x ? 1 : -1;
        c cVar2 = this.t;
        cVar2.f = -1;
        cVar2.f1106b = i2;
        cVar2.g = Integer.MIN_VALUE;
    }

    private View h(RecyclerView.n nVar, RecyclerView.r rVar) {
        return this.x ? f(nVar, rVar) : g(nVar, rVar);
    }

    private int i(RecyclerView.r rVar) {
        if (e() == 0) {
            return 0;
        }
        F();
        B b2 = this.u;
        View b3 = b(!this.z, true);
        return N.a(rVar, b2, b3, a(!this.z, true), this, this.z);
    }

    private View i(RecyclerView.n nVar, RecyclerView.r rVar) {
        return this.x ? g(nVar, rVar) : f(nVar, rVar);
    }

    private int j(RecyclerView.r rVar) {
        if (e() == 0) {
            return 0;
        }
        F();
        B b2 = this.u;
        View b3 = b(!this.z, true);
        return N.a(rVar, b2, b3, a(!this.z, true), this, this.z, this.x);
    }

    private int k(RecyclerView.r rVar) {
        if (e() == 0) {
            return 0;
        }
        F();
        B b2 = this.u;
        View b3 = b(!this.z, true);
        return N.b(rVar, b2, b3, a(!this.z, true), this, this.z);
    }

    /* access modifiers changed from: package-private */
    public boolean B() {
        return (i() == 1073741824 || s() == 1073741824 || !t()) ? false : true;
    }

    public boolean D() {
        return this.D == null && this.v == this.y;
    }

    /* access modifiers changed from: package-private */
    public c E() {
        return new c();
    }

    /* access modifiers changed from: package-private */
    public void F() {
        if (this.t == null) {
            this.t = E();
        }
    }

    public int G() {
        View a2 = a(0, e(), false, true);
        if (a2 == null) {
            return -1;
        }
        return l(a2);
    }

    public int H() {
        View a2 = a(e() - 1, -1, false, true);
        if (a2 == null) {
            return -1;
        }
        return l(a2);
    }

    public int I() {
        return this.s;
    }

    /* access modifiers changed from: protected */
    public boolean J() {
        return k() == 1;
    }

    public boolean K() {
        return this.z;
    }

    /* access modifiers changed from: package-private */
    public boolean L() {
        return this.u.d() == 0 && this.u.a() == 0;
    }

    public int a(int i, RecyclerView.n nVar, RecyclerView.r rVar) {
        if (this.s == 1) {
            return 0;
        }
        return c(i, nVar, rVar);
    }

    /* access modifiers changed from: package-private */
    public int a(RecyclerView.n nVar, c cVar, RecyclerView.r rVar, boolean z2) {
        int i = cVar.f1107c;
        int i2 = cVar.g;
        if (i2 != Integer.MIN_VALUE) {
            if (i < 0) {
                cVar.g = i2 + i;
            }
            a(nVar, cVar);
        }
        int i3 = cVar.f1107c + cVar.h;
        b bVar = this.F;
        while (true) {
            if ((!cVar.m && i3 <= 0) || !cVar.a(rVar)) {
                break;
            }
            bVar.a();
            a(nVar, rVar, cVar, bVar);
            if (!bVar.f1102b) {
                cVar.f1106b += bVar.f1101a * cVar.f;
                if (!bVar.f1103c || cVar.l != null || !rVar.d()) {
                    int i4 = cVar.f1107c;
                    int i5 = bVar.f1101a;
                    cVar.f1107c = i4 - i5;
                    i3 -= i5;
                }
                int i6 = cVar.g;
                if (i6 != Integer.MIN_VALUE) {
                    cVar.g = i6 + bVar.f1101a;
                    int i7 = cVar.f1107c;
                    if (i7 < 0) {
                        cVar.g += i7;
                    }
                    a(nVar, cVar);
                }
                if (z2 && bVar.f1104d) {
                    break;
                }
            } else {
                break;
            }
        }
        return i - cVar.f1107c;
    }

    public int a(RecyclerView.r rVar) {
        return i(rVar);
    }

    /* access modifiers changed from: package-private */
    public View a(int i, int i2, boolean z2, boolean z3) {
        F();
        int i3 = 320;
        int i4 = z2 ? 24579 : 320;
        if (!z3) {
            i3 = 0;
        }
        return (this.s == 0 ? this.e : this.f).a(i, i2, i4, i3);
    }

    public View a(View view, int i, RecyclerView.n nVar, RecyclerView.r rVar) {
        int i2;
        S();
        if (e() == 0 || (i2 = i(i)) == Integer.MIN_VALUE) {
            return null;
        }
        F();
        a(i2, (int) (((float) this.u.g()) * 0.33333334f), false, rVar);
        c cVar = this.t;
        cVar.g = Integer.MIN_VALUE;
        cVar.f1105a = false;
        a(nVar, cVar, rVar, true);
        View P = i2 == -1 ? P() : O();
        View R = i2 == -1 ? R() : Q();
        if (!R.hasFocusable()) {
            return P;
        }
        if (P == null) {
            return null;
        }
        return R;
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
            if (l >= 0 && l < i3) {
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

    /* access modifiers changed from: package-private */
    public View a(boolean z2, boolean z3) {
        int e;
        int i;
        if (this.x) {
            e = 0;
            i = e();
        } else {
            e = e() - 1;
            i = -1;
        }
        return a(e, i, z2, z3);
    }

    public void a(int i, int i2, RecyclerView.r rVar, RecyclerView.g.a aVar) {
        if (this.s != 0) {
            i = i2;
        }
        if (e() != 0 && i != 0) {
            F();
            a(i > 0 ? 1 : -1, Math.abs(i), true, rVar);
            a(rVar, this.t, aVar);
        }
    }

    public void a(int i, RecyclerView.g.a aVar) {
        boolean z2;
        int i2;
        SavedState savedState = this.D;
        int i3 = -1;
        if (savedState == null || !savedState.hasValidAnchor()) {
            S();
            z2 = this.x;
            i2 = this.A;
            if (i2 == -1) {
                i2 = z2 ? i - 1 : 0;
            }
        } else {
            SavedState savedState2 = this.D;
            z2 = savedState2.mAnchorLayoutFromEnd;
            i2 = savedState2.mAnchorPosition;
        }
        if (!z2) {
            i3 = 1;
        }
        int i4 = i2;
        for (int i5 = 0; i5 < this.G && i4 >= 0 && i4 < i; i5++) {
            aVar.a(i4, 0);
            i4 += i3;
        }
    }

    public void a(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            this.D = (SavedState) parcelable;
            z();
        }
    }

    public void a(AccessibilityEvent accessibilityEvent) {
        super.a(accessibilityEvent);
        if (e() > 0) {
            accessibilityEvent.setFromIndex(G());
            accessibilityEvent.setToIndex(H());
        }
    }

    /* access modifiers changed from: package-private */
    public void a(RecyclerView.n nVar, RecyclerView.r rVar, a aVar, int i) {
    }

    /* access modifiers changed from: package-private */
    public void a(RecyclerView.n nVar, RecyclerView.r rVar, c cVar, b bVar) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        View a2 = cVar.a(nVar);
        if (a2 == null) {
            bVar.f1102b = true;
            return;
        }
        RecyclerView.h hVar = (RecyclerView.h) a2.getLayoutParams();
        if (cVar.l == null) {
            if (this.x == (cVar.f == -1)) {
                b(a2);
            } else {
                b(a2, 0);
            }
        } else {
            if (this.x == (cVar.f == -1)) {
                a(a2);
            } else {
                a(a2, 0);
            }
        }
        a(a2, 0, 0);
        bVar.f1101a = this.u.b(a2);
        if (this.s == 1) {
            if (J()) {
                i5 = r() - p();
                i4 = i5 - this.u.c(a2);
            } else {
                i4 = o();
                i5 = this.u.c(a2) + i4;
            }
            if (cVar.f == -1) {
                int i6 = cVar.f1106b;
                i = i6;
                i2 = i5;
                i3 = i6 - bVar.f1101a;
            } else {
                int i7 = cVar.f1106b;
                i3 = i7;
                i2 = i5;
                i = bVar.f1101a + i7;
            }
        } else {
            int q = q();
            int c2 = this.u.c(a2) + q;
            if (cVar.f == -1) {
                int i8 = cVar.f1106b;
                i2 = i8;
                i3 = q;
                i = c2;
                i4 = i8 - bVar.f1101a;
            } else {
                int i9 = cVar.f1106b;
                i3 = q;
                i2 = bVar.f1101a + i9;
                i = c2;
                i4 = i9;
            }
        }
        a(a2, i4, i3, i2, i);
        if (hVar.c() || hVar.b()) {
            bVar.f1103c = true;
        }
        bVar.f1104d = a2.hasFocusable();
    }

    /* access modifiers changed from: package-private */
    public void a(RecyclerView.r rVar, c cVar, RecyclerView.g.a aVar) {
        int i = cVar.f1108d;
        if (i >= 0 && i < rVar.a()) {
            aVar.a(i, Math.max(0, cVar.g));
        }
    }

    /* access modifiers changed from: protected */
    public void a(@NonNull RecyclerView.r rVar, @NonNull int[] iArr) {
        int i;
        int h = h(rVar);
        if (this.t.f == -1) {
            i = 0;
        } else {
            i = h;
            h = 0;
        }
        iArr[0] = h;
        iArr[1] = i;
    }

    public void a(String str) {
        if (this.D == null) {
            super.a(str);
        }
    }

    public void a(boolean z2) {
        a((String) null);
        if (z2 != this.w) {
            this.w = z2;
            z();
        }
    }

    public boolean a() {
        return this.s == 0;
    }

    public int b(int i, RecyclerView.n nVar, RecyclerView.r rVar) {
        if (this.s == 0) {
            return 0;
        }
        return c(i, nVar, rVar);
    }

    public int b(RecyclerView.r rVar) {
        return j(rVar);
    }

    public View b(int i) {
        int e = e();
        if (e == 0) {
            return null;
        }
        int l = i - l(c(0));
        if (l >= 0 && l < e) {
            View c2 = c(l);
            if (l(c2) == i) {
                return c2;
            }
        }
        return super.b(i);
    }

    /* access modifiers changed from: package-private */
    public View b(boolean z2, boolean z3) {
        int i;
        int e;
        if (this.x) {
            i = e() - 1;
            e = -1;
        } else {
            i = 0;
            e = e();
        }
        return a(i, e, z2, z3);
    }

    public void b(RecyclerView recyclerView, RecyclerView.n nVar) {
        super.b(recyclerView, nVar);
        if (this.C) {
            b(nVar);
            nVar.a();
        }
    }

    public void b(boolean z2) {
        a((String) null);
        if (this.y != z2) {
            this.y = z2;
            z();
        }
    }

    public boolean b() {
        return this.s == 1;
    }

    /* access modifiers changed from: package-private */
    public int c(int i, RecyclerView.n nVar, RecyclerView.r rVar) {
        if (e() == 0 || i == 0) {
            return 0;
        }
        F();
        this.t.f1105a = true;
        int i2 = i > 0 ? 1 : -1;
        int abs = Math.abs(i);
        a(i2, abs, true, rVar);
        c cVar = this.t;
        int a2 = cVar.g + a(nVar, cVar, rVar, false);
        if (a2 < 0) {
            return 0;
        }
        if (abs > a2) {
            i = i2 * a2;
        }
        this.u.a(-i);
        this.t.k = i;
        return i;
    }

    public int c(RecyclerView.r rVar) {
        return k(rVar);
    }

    public RecyclerView.h c() {
        return new RecyclerView.h(-2, -2);
    }

    public int d(RecyclerView.r rVar) {
        return i(rVar);
    }

    public int e(RecyclerView.r rVar) {
        return j(rVar);
    }

    /* access modifiers changed from: package-private */
    public View e(int i, int i2) {
        int i3;
        int i4;
        F();
        if ((i2 > i ? 1 : i2 < i ? (char) 65535 : 0) == 0) {
            return c(i);
        }
        if (this.u.d(c(i)) < this.u.f()) {
            i4 = 16644;
            i3 = 16388;
        } else {
            i4 = 4161;
            i3 = 4097;
        }
        return (this.s == 0 ? this.e : this.f).a(i, i2, i4, i3);
    }

    public void e(RecyclerView.n nVar, RecyclerView.r rVar) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        View b2;
        int i7;
        int i8;
        int i9 = -1;
        if (!(this.D == null && this.A == -1) && rVar.a() == 0) {
            b(nVar);
            return;
        }
        SavedState savedState = this.D;
        if (savedState != null && savedState.hasValidAnchor()) {
            this.A = this.D.mAnchorPosition;
        }
        F();
        this.t.f1105a = false;
        S();
        View g = g();
        if (!this.E.e || this.A != -1 || this.D != null) {
            this.E.b();
            a aVar = this.E;
            aVar.f1100d = this.x ^ this.y;
            b(nVar, rVar, aVar);
            this.E.e = true;
        } else if (g != null && (this.u.d(g) >= this.u.b() || this.u.a(g) <= this.u.f())) {
            this.E.b(g, l(g));
        }
        c cVar = this.t;
        cVar.f = cVar.k >= 0 ? 1 : -1;
        int[] iArr = this.H;
        iArr[0] = 0;
        iArr[1] = 0;
        a(rVar, iArr);
        int max = Math.max(0, this.H[0]) + this.u.f();
        int max2 = Math.max(0, this.H[1]) + this.u.c();
        if (!(!rVar.d() || (i6 = this.A) == -1 || this.B == Integer.MIN_VALUE || (b2 = b(i6)) == null)) {
            if (this.x) {
                i7 = this.u.b() - this.u.a(b2);
                i8 = this.B;
            } else {
                i8 = this.u.d(b2) - this.u.f();
                i7 = this.B;
            }
            int i10 = i7 - i8;
            if (i10 > 0) {
                max += i10;
            } else {
                max2 -= i10;
            }
        }
        if (!this.E.f1100d ? !this.x : this.x) {
            i9 = 1;
        }
        a(nVar, rVar, this.E, i9);
        a(nVar);
        this.t.m = L();
        this.t.j = rVar.d();
        this.t.i = 0;
        a aVar2 = this.E;
        if (aVar2.f1100d) {
            b(aVar2);
            c cVar2 = this.t;
            cVar2.h = max;
            a(nVar, cVar2, rVar, false);
            c cVar3 = this.t;
            i2 = cVar3.f1106b;
            int i11 = cVar3.f1108d;
            int i12 = cVar3.f1107c;
            if (i12 > 0) {
                max2 += i12;
            }
            a(this.E);
            c cVar4 = this.t;
            cVar4.h = max2;
            cVar4.f1108d += cVar4.e;
            a(nVar, cVar4, rVar, false);
            c cVar5 = this.t;
            i = cVar5.f1106b;
            int i13 = cVar5.f1107c;
            if (i13 > 0) {
                g(i11, i2);
                c cVar6 = this.t;
                cVar6.h = i13;
                a(nVar, cVar6, rVar, false);
                i2 = this.t.f1106b;
            }
        } else {
            a(aVar2);
            c cVar7 = this.t;
            cVar7.h = max2;
            a(nVar, cVar7, rVar, false);
            c cVar8 = this.t;
            i = cVar8.f1106b;
            int i14 = cVar8.f1108d;
            int i15 = cVar8.f1107c;
            if (i15 > 0) {
                max += i15;
            }
            b(this.E);
            c cVar9 = this.t;
            cVar9.h = max;
            cVar9.f1108d += cVar9.e;
            a(nVar, cVar9, rVar, false);
            c cVar10 = this.t;
            i2 = cVar10.f1106b;
            int i16 = cVar10.f1107c;
            if (i16 > 0) {
                f(i14, i);
                c cVar11 = this.t;
                cVar11.h = i16;
                a(nVar, cVar11, rVar, false);
                i = this.t.f1106b;
            }
        }
        if (e() > 0) {
            if (this.x ^ this.y) {
                int a2 = a(i, nVar, rVar, true);
                i4 = i2 + a2;
                i3 = i + a2;
                i5 = b(i4, nVar, rVar, false);
            } else {
                int b3 = b(i2, nVar, rVar, true);
                i4 = i2 + b3;
                i3 = i + b3;
                i5 = a(i3, nVar, rVar, false);
            }
            i2 = i4 + i5;
            i = i3 + i5;
        }
        b(nVar, rVar, i2, i);
        if (!rVar.d()) {
            this.u.i();
        } else {
            this.E.b();
        }
        this.v = this.y;
    }

    public int f(RecyclerView.r rVar) {
        return k(rVar);
    }

    public void g(RecyclerView.r rVar) {
        super.g(rVar);
        this.D = null;
        this.A = -1;
        this.B = Integer.MIN_VALUE;
        this.E.b();
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public int h(RecyclerView.r rVar) {
        if (rVar.c()) {
            return this.u.g();
        }
        return 0;
    }

    public void h(int i) {
        this.A = i;
        this.B = Integer.MIN_VALUE;
        SavedState savedState = this.D;
        if (savedState != null) {
            savedState.invalidateAnchor();
        }
        z();
    }

    /* access modifiers changed from: package-private */
    public int i(int i) {
        if (i == 1) {
            return (this.s != 1 && J()) ? 1 : -1;
        }
        if (i == 2) {
            return (this.s != 1 && J()) ? -1 : 1;
        }
        if (i == 17) {
            return this.s == 0 ? -1 : Integer.MIN_VALUE;
        }
        if (i == 33) {
            return this.s == 1 ? -1 : Integer.MIN_VALUE;
        }
        if (i == 66) {
            return this.s == 0 ? 1 : Integer.MIN_VALUE;
        }
        if (i != 130) {
            return Integer.MIN_VALUE;
        }
        return this.s == 1 ? 1 : Integer.MIN_VALUE;
    }

    public void j(int i) {
        if (i == 0 || i == 1) {
            a((String) null);
            if (i != this.s || this.u == null) {
                this.u = B.a(this, i);
                this.E.f1097a = this.u;
                this.s = i;
                z();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("invalid orientation:" + i);
    }

    public boolean v() {
        return true;
    }

    public Parcelable y() {
        SavedState savedState = this.D;
        if (savedState != null) {
            return new SavedState(savedState);
        }
        SavedState savedState2 = new SavedState();
        if (e() > 0) {
            F();
            boolean z2 = this.v ^ this.x;
            savedState2.mAnchorLayoutFromEnd = z2;
            if (z2) {
                View Q = Q();
                savedState2.mAnchorOffset = this.u.b() - this.u.a(Q);
                savedState2.mAnchorPosition = l(Q);
            } else {
                View R = R();
                savedState2.mAnchorPosition = l(R);
                savedState2.mAnchorOffset = this.u.d(R) - this.u.f();
            }
        } else {
            savedState2.invalidateAnchor();
        }
        return savedState2;
    }
}
