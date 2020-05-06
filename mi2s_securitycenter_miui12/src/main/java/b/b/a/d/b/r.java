package b.b.a.d.b;

import android.content.DialogInterface;
import b.b.a.e.c;

class r implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ s f1405a;

    r(s sVar) {
        this.f1405a = sVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        s sVar = this.f1405a;
        c.a(sVar.m, i, sVar.r);
        s sVar2 = this.f1405a;
        sVar2.i.a(sVar2.n[i]);
        dialogInterface.dismiss();
    }
}
