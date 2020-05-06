package miuix.recyclerview.widget;

import androidx.recyclerview.widget.RecyclerView;

class h implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView.u f8935a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f8936b;

    h(j jVar, RecyclerView.u uVar) {
        this.f8936b = jVar;
        this.f8935a = uVar;
    }

    public void run() {
        this.f8936b.e(this.f8935a, true);
    }
}
