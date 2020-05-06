package d.a;

import android.view.View;

class q implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f8803a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Runnable f8804b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ r f8805c;

    q(r rVar, View view, Runnable runnable) {
        this.f8805c = rVar;
        this.f8803a = view;
        this.f8804b = runnable;
    }

    public void run() {
        this.f8805c.a(this.f8803a, this.f8804b);
    }
}
