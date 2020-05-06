package d.a.b;

import android.view.View;
import d.a.a.a;

class h implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f8660a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ a[] f8661b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ j f8662c;

    h(j jVar, View view, a[] aVarArr) {
        this.f8662c = jVar;
        this.f8660a = view;
        this.f8661b = aVarArr;
    }

    public void run() {
        boolean unused = this.f8662c.b(this.f8660a, false, this.f8661b);
    }
}
