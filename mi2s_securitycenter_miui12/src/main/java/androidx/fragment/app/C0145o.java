package androidx.fragment.app;

import android.view.View;

/* renamed from: androidx.fragment.app.o  reason: case insensitive filesystem */
class C0145o implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0146p f924a;

    C0145o(C0146p pVar) {
        this.f924a = pVar;
    }

    public void run() {
        if (this.f924a.f926b.i() != null) {
            this.f924a.f926b.a((View) null);
            C0146p pVar = this.f924a;
            t tVar = pVar.f927c;
            Fragment fragment = pVar.f926b;
            tVar.a(fragment, fragment.C(), 0, 0, false);
        }
    }
}
