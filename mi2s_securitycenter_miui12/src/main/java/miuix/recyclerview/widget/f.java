package miuix.recyclerview.widget;

import androidx.recyclerview.widget.RecyclerView;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView.u f8931a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f8932b;

    f(j jVar, RecyclerView.u uVar) {
        this.f8932b = jVar;
        this.f8931a = uVar;
    }

    public void run() {
        this.f8932b.x(this.f8931a);
    }
}
