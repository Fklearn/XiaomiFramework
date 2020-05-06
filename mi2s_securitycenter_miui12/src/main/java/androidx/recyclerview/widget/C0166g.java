package androidx.recyclerview.widget;

import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;

/* renamed from: androidx.recyclerview.widget.g  reason: case insensitive filesystem */
class C0166g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ArrayList f1199a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0172m f1200b;

    C0166g(C0172m mVar, ArrayList arrayList) {
        this.f1200b = mVar;
        this.f1199a = arrayList;
    }

    public void run() {
        Iterator it = this.f1199a.iterator();
        while (it.hasNext()) {
            this.f1200b.t((RecyclerView.u) it.next());
        }
        this.f1199a.clear();
        this.f1200b.m.remove(this.f1199a);
    }
}
