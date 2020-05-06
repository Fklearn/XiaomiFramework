package miuix.recyclerview.widget;

import androidx.recyclerview.widget.RecyclerView;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView.u f8929a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f8930b;

    e(j jVar, RecyclerView.u uVar) {
        this.f8930b = jVar;
        this.f8929a = uVar;
    }

    public void run() {
        this.f8930b.z(this.f8929a);
    }
}
