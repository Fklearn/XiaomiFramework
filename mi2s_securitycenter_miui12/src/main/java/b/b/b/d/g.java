package b.b.b.d;

import android.content.DialogInterface;
import android.view.ViewTreeObserver;

class g implements ViewTreeObserver.OnWindowAttachListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f1528a;

    g(h hVar) {
        this.f1528a = hVar;
    }

    public void onWindowAttached() {
    }

    public void onWindowDetached() {
        DialogInterface.OnClickListener unused = this.f1528a.f1529a = null;
        DialogInterface.OnCancelListener unused2 = this.f1528a.f1531c = null;
    }
}
