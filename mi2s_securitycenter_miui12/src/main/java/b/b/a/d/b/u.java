package b.b.a.d.b;

import android.content.DialogInterface;
import b.b.a.e.c;

class u implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ v f1407a;

    u(v vVar) {
        this.f1407a = vVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        c.a(this.f1407a.m, i, 2);
        v vVar = this.f1407a;
        vVar.i.a(vVar.n[i]);
        dialogInterface.dismiss();
    }
}
