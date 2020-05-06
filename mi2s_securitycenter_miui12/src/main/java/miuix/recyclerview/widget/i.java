package miuix.recyclerview.widget;

import androidx.recyclerview.widget.RecyclerView;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView.u f8937a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f8938b;

    i(j jVar, RecyclerView.u uVar) {
        this.f8938b = jVar;
        this.f8937a = uVar;
    }

    public void run() {
        this.f8938b.e(this.f8937a, false);
    }
}
