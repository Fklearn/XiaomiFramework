package androidx.fragment.app;

import android.view.View;
import java.util.ArrayList;

class B implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Object f861a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ N f862b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ View f863c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ Fragment f864d;
    final /* synthetic */ ArrayList e;
    final /* synthetic */ ArrayList f;
    final /* synthetic */ ArrayList g;
    final /* synthetic */ Object h;

    B(Object obj, N n, View view, Fragment fragment, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, Object obj2) {
        this.f861a = obj;
        this.f862b = n;
        this.f863c = view;
        this.f864d = fragment;
        this.e = arrayList;
        this.f = arrayList2;
        this.g = arrayList3;
        this.h = obj2;
    }

    public void run() {
        Object obj = this.f861a;
        if (obj != null) {
            this.f862b.b(obj, this.f863c);
            this.f.addAll(E.a(this.f862b, this.f861a, this.f864d, (ArrayList<View>) this.e, this.f863c));
        }
        if (this.g != null) {
            if (this.h != null) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(this.f863c);
                this.f862b.a(this.h, (ArrayList<View>) this.g, (ArrayList<View>) arrayList);
            }
            this.g.clear();
            this.g.add(this.f863c);
        }
    }
}
