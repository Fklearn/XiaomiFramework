package androidx.fragment.app;

import a.c.b;
import android.graphics.Rect;
import android.view.View;

class C implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Fragment f865a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Fragment f866b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ boolean f867c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ b f868d;
    final /* synthetic */ View e;
    final /* synthetic */ N f;
    final /* synthetic */ Rect g;

    C(Fragment fragment, Fragment fragment2, boolean z, b bVar, View view, N n, Rect rect) {
        this.f865a = fragment;
        this.f866b = fragment2;
        this.f867c = z;
        this.f868d = bVar;
        this.e = view;
        this.f = n;
        this.g = rect;
    }

    public void run() {
        E.a(this.f865a, this.f866b, this.f867c, (b<String, View>) this.f868d, false);
        View view = this.e;
        if (view != null) {
            this.f.a(view, this.g);
        }
    }
}
