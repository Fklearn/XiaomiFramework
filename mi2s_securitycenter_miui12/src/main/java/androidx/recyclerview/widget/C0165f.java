package androidx.recyclerview.widget;

import androidx.recyclerview.widget.C0172m;
import java.util.ArrayList;
import java.util.Iterator;

/* renamed from: androidx.recyclerview.widget.f  reason: case insensitive filesystem */
class C0165f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ArrayList f1197a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0172m f1198b;

    C0165f(C0172m mVar, ArrayList arrayList) {
        this.f1198b = mVar;
        this.f1197a = arrayList;
    }

    public void run() {
        Iterator it = this.f1197a.iterator();
        while (it.hasNext()) {
            this.f1198b.a((C0172m.a) it.next());
        }
        this.f1197a.clear();
        this.f1198b.o.remove(this.f1197a);
    }
}
