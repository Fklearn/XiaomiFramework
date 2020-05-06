package androidx.appcompat.widget;

import android.view.View;
import androidx.appcompat.view.menu.v;
import androidx.appcompat.widget.ActionMenuPresenter;

/* renamed from: androidx.appcompat.widget.g  reason: case insensitive filesystem */
class C0099g extends Q {
    final /* synthetic */ ActionMenuPresenter j;
    final /* synthetic */ ActionMenuPresenter.d k;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C0099g(ActionMenuPresenter.d dVar, View view, ActionMenuPresenter actionMenuPresenter) {
        super(view);
        this.k = dVar;
        this.j = actionMenuPresenter;
    }

    public v a() {
        ActionMenuPresenter.e eVar = ActionMenuPresenter.this.y;
        if (eVar == null) {
            return null;
        }
        return eVar.b();
    }

    public boolean b() {
        ActionMenuPresenter.this.i();
        return true;
    }

    public boolean c() {
        ActionMenuPresenter actionMenuPresenter = ActionMenuPresenter.this;
        if (actionMenuPresenter.A != null) {
            return false;
        }
        actionMenuPresenter.e();
        return true;
    }
}
