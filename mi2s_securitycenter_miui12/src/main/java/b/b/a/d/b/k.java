package b.b.a.d.b;

import android.content.DialogInterface;
import android.view.ActionMode;

class k implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ActionMode f1398a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f1399b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ l f1400c;

    k(l lVar, ActionMode actionMode, boolean z) {
        this.f1400c = lVar;
        this.f1398a = actionMode;
        this.f1399b = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        ActionMode actionMode = this.f1398a;
        if (actionMode != null) {
            actionMode.finish();
        }
        this.f1400c.a(this.f1399b);
    }
}
