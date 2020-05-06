package d.a.b;

import android.view.View;
import d.a.a.a;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f8663a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f8664b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ a[] f8665c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ boolean f8666d;
    final /* synthetic */ j e;

    i(j jVar, boolean z, View view, a[] aVarArr, boolean z2) {
        this.e = jVar;
        this.f8663a = z;
        this.f8664b = view;
        this.f8665c = aVarArr;
        this.f8666d = z2;
    }

    public void run() {
        if (!this.f8663a && this.e.b(this.f8664b, true, this.f8665c)) {
            this.e.a(this.f8664b, this.f8666d);
        }
    }
}
