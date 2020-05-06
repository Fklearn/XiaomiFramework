package androidx.preference;

import android.content.DialogInterface;

/* renamed from: androidx.preference.f  reason: case insensitive filesystem */
class C0152f implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0153g f1034a;

    C0152f(C0153g gVar) {
        this.f1034a = gVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        C0153g gVar = this.f1034a;
        gVar.i = i;
        gVar.onClick(dialogInterface, -1);
        dialogInterface.dismiss();
    }
}
