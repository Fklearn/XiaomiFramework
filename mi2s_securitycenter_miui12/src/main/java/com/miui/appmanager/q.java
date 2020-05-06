package com.miui.appmanager;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import miui.view.SearchActionMode;

class q implements SearchActionMode.Callback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppManagerMainActivity f3685a;

    q(AppManagerMainActivity appManagerMainActivity) {
        this.f3685a = appManagerMainActivity;
    }

    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return true;
    }

    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        SearchActionMode searchActionMode = (SearchActionMode) actionMode;
        searchActionMode.setAnchorView(this.f3685a.g);
        searchActionMode.setAnimateView(this.f3685a.i);
        searchActionMode.getSearchInput().addTextChangedListener(this.f3685a.wa);
        return true;
    }

    public void onDestroyActionMode(ActionMode actionMode) {
        ((SearchActionMode) actionMode).getSearchInput().removeTextChangedListener(this.f3685a.wa);
        this.f3685a.l();
        this.f3685a.F();
        this.f3685a.C();
    }

    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }
}
