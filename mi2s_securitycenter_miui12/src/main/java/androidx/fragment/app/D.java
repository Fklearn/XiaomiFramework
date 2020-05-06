package androidx.fragment.app;

import a.c.b;
import android.graphics.Rect;
import android.view.View;
import androidx.fragment.app.E;
import java.util.ArrayList;

class D implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f869a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ b f870b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Object f871c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ E.a f872d;
    final /* synthetic */ ArrayList e;
    final /* synthetic */ View f;
    final /* synthetic */ Fragment g;
    final /* synthetic */ Fragment h;
    final /* synthetic */ boolean i;
    final /* synthetic */ ArrayList j;
    final /* synthetic */ Object k;
    final /* synthetic */ Rect l;

    D(N n, b bVar, Object obj, E.a aVar, ArrayList arrayList, View view, Fragment fragment, Fragment fragment2, boolean z, ArrayList arrayList2, Object obj2, Rect rect) {
        this.f869a = n;
        this.f870b = bVar;
        this.f871c = obj;
        this.f872d = aVar;
        this.e = arrayList;
        this.f = view;
        this.g = fragment;
        this.h = fragment2;
        this.i = z;
        this.j = arrayList2;
        this.k = obj2;
        this.l = rect;
    }

    public void run() {
        b<String, View> a2 = E.a(this.f869a, (b<String, String>) this.f870b, this.f871c, this.f872d);
        if (a2 != null) {
            this.e.addAll(a2.values());
            this.e.add(this.f);
        }
        E.a(this.g, this.h, this.i, a2, false);
        Object obj = this.f871c;
        if (obj != null) {
            this.f869a.b(obj, (ArrayList<View>) this.j, (ArrayList<View>) this.e);
            View a3 = E.a(a2, this.f872d, this.k, this.i);
            if (a3 != null) {
                this.f869a.a(a3, this.l);
            }
        }
    }
}
