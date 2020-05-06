package androidx.preference;

import android.content.DialogInterface;

/* renamed from: androidx.preference.i  reason: case insensitive filesystem */
class C0155i implements DialogInterface.OnMultiChoiceClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0156j f1035a;

    C0155i(C0156j jVar) {
        this.f1035a = jVar;
    }

    public void onClick(DialogInterface dialogInterface, int i, boolean z) {
        boolean z2;
        boolean z3;
        C0156j jVar;
        if (z) {
            jVar = this.f1035a;
            z2 = jVar.j;
            z3 = jVar.i.add(jVar.l[i].toString());
        } else {
            jVar = this.f1035a;
            z2 = jVar.j;
            z3 = jVar.i.remove(jVar.l[i].toString());
        }
        jVar.j = z3 | z2;
    }
}
