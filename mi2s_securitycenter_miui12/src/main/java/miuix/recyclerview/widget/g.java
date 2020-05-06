package miuix.recyclerview.widget;

import androidx.recyclerview.widget.RecyclerView;

class g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView.u f8933a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f8934b;

    g(j jVar, RecyclerView.u uVar) {
        this.f8934b = jVar;
        this.f8933a = uVar;
    }

    public void run() {
        this.f8934b.v(this.f8933a);
    }
}
