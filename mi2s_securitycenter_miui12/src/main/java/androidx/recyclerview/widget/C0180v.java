package androidx.recyclerview.widget;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/* renamed from: androidx.recyclerview.widget.v  reason: case insensitive filesystem */
class C0180v {

    /* renamed from: a  reason: collision with root package name */
    boolean f1266a = true;

    /* renamed from: b  reason: collision with root package name */
    int f1267b;

    /* renamed from: c  reason: collision with root package name */
    int f1268c;

    /* renamed from: d  reason: collision with root package name */
    int f1269d;
    int e;
    int f = 0;
    int g = 0;
    boolean h;
    boolean i;

    C0180v() {
    }

    /* access modifiers changed from: package-private */
    public View a(RecyclerView.n nVar) {
        View d2 = nVar.d(this.f1268c);
        this.f1268c += this.f1269d;
        return d2;
    }

    /* access modifiers changed from: package-private */
    public boolean a(RecyclerView.r rVar) {
        int i2 = this.f1268c;
        return i2 >= 0 && i2 < rVar.a();
    }

    public String toString() {
        return "LayoutState{mAvailable=" + this.f1267b + ", mCurrentPosition=" + this.f1268c + ", mItemDirection=" + this.f1269d + ", mLayoutDirection=" + this.e + ", mStartLine=" + this.f + ", mEndLine=" + this.g + '}';
    }
}
