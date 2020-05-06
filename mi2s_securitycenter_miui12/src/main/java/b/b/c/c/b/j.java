package b.b.c.c.b;

import android.content.DialogInterface;
import android.view.KeyEvent;

class j implements DialogInterface.OnKeyListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ k f1627a;

    j(k kVar) {
        this.f1627a = kVar;
    }

    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        if (i != 4) {
            return true;
        }
        dialogInterface.dismiss();
        this.f1627a.f1629b.finish();
        return true;
    }
}
