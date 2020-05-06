package androidx.appcompat.widget;

import android.view.MenuItem;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;

class wa implements ActionMenuView.e {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Toolbar f673a;

    wa(Toolbar toolbar) {
        this.f673a = toolbar;
    }

    public boolean onMenuItemClick(MenuItem menuItem) {
        Toolbar.c cVar = this.f673a.G;
        if (cVar != null) {
            return cVar.onMenuItemClick(menuItem);
        }
        return false;
    }
}
