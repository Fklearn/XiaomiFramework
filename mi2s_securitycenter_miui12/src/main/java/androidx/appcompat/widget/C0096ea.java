package androidx.appcompat.widget;

import a.e.a.a;
import android.database.Cursor;

/* renamed from: androidx.appcompat.widget.ea  reason: case insensitive filesystem */
class C0096ea implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SearchView f602a;

    C0096ea(SearchView searchView) {
        this.f602a = searchView;
    }

    public void run() {
        a aVar = this.f602a.S;
        if (aVar instanceof oa) {
            aVar.changeCursor((Cursor) null);
        }
    }
}
