package b.b.a.d.b;

import android.content.DialogInterface;
import android.view.ActionMode;
import b.b.a.a.a;
import b.b.a.d.a.o;

class x implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ActionMode f1410a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f1411b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ y f1412c;

    x(y yVar, ActionMode actionMode, boolean z) {
        this.f1412c = yVar;
        this.f1410a = actionMode;
        this.f1411b = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        long[] jArr;
        y yVar;
        ActionMode actionMode = this.f1410a;
        if (actionMode != null) {
            actionMode.finish();
        }
        if (this.f1411b) {
            a.b();
            yVar = this.f1412c;
            jArr = ((o) yVar.f1381d).i();
        } else {
            yVar = this.f1412c;
            jArr = ((o) yVar.f1381d).j();
        }
        yVar.a(jArr);
    }
}
