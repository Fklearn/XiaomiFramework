package androidx.recyclerview.widget;

import androidx.recyclerview.widget.C0172m;
import java.util.ArrayList;
import java.util.Iterator;

/* renamed from: androidx.recyclerview.widget.e  reason: case insensitive filesystem */
class C0164e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ArrayList f1195a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0172m f1196b;

    C0164e(C0172m mVar, ArrayList arrayList) {
        this.f1196b = mVar;
        this.f1195a = arrayList;
    }

    public void run() {
        Iterator it = this.f1195a.iterator();
        while (it.hasNext()) {
            C0172m.b bVar = (C0172m.b) it.next();
            this.f1196b.b(bVar.f1225a, bVar.f1226b, bVar.f1227c, bVar.f1228d, bVar.e);
        }
        this.f1195a.clear();
        this.f1196b.n.remove(this.f1195a);
    }
}
