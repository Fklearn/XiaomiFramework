package androidx.recyclerview.widget;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import d.i.a.c;

class P extends c {

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Q f1109c;

    P(Q q) {
        this.f1109c = q;
    }

    /* access modifiers changed from: protected */
    public void a(int i, int i2, int i3, int i4, @Nullable int[] iArr, int i5, @Nullable int[] iArr2) {
        int i6 = i3;
        int i7 = i4;
        this.f1109c.Na.b(i, i2, i3, i4, iArr, i5, iArr2);
        if (g() && this.f1109c.Ra == 2) {
            if (!this.f1109c.Pa && a() && i6 != 0) {
                this.f1109c.Ma.a(i3);
            }
            if (!this.f1109c.Qa && b() && i7 != 0) {
                this.f1109c.Ma.b(i4);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean a() {
        RecyclerView.g gVar = this.f1109c.v;
        return gVar != null && gVar.a();
    }

    /* access modifiers changed from: protected */
    public boolean a(int i, int i2, @Nullable int[] iArr, @Nullable int[] iArr2, int i3) {
        if (this.f1109c.Pa && d() == 0) {
            boolean unused = this.f1109c.Pa = false;
        }
        if (this.f1109c.Qa && e() == 0) {
            boolean unused2 = this.f1109c.Qa = false;
        }
        return this.f1109c.Na.b(i, i2, iArr, iArr2, i3);
    }

    /* access modifiers changed from: protected */
    public boolean b() {
        RecyclerView.g gVar = this.f1109c.v;
        return gVar != null && gVar.b();
    }

    /* access modifiers changed from: protected */
    public int c() {
        return this.f1109c.getHeight();
    }

    /* access modifiers changed from: protected */
    public int f() {
        return this.f1109c.getWidth();
    }

    /* access modifiers changed from: protected */
    public boolean g() {
        return this.f1109c.Q();
    }
}
