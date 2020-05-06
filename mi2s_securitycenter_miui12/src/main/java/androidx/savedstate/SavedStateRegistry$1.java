package androidx.savedstate;

import androidx.lifecycle.e;
import androidx.lifecycle.f;
import androidx.lifecycle.i;

class SavedStateRegistry$1 implements e {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ a f1272a;

    SavedStateRegistry$1(a aVar) {
        this.f1272a = aVar;
    }

    public void a(i iVar, f.a aVar) {
        a aVar2;
        boolean z;
        if (aVar == f.a.ON_START) {
            aVar2 = this.f1272a;
            z = true;
        } else if (aVar == f.a.ON_STOP) {
            aVar2 = this.f1272a;
            z = false;
        } else {
            return;
        }
        aVar2.f1276d = z;
    }
}
