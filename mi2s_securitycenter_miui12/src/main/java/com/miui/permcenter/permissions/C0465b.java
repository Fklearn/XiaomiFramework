package com.miui.permcenter.permissions;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import miui.view.SearchActionMode;

/* renamed from: com.miui.permcenter.permissions.b  reason: case insensitive filesystem */
class C0465b implements SearchActionMode.Callback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0466c f6253a;

    C0465b(C0466c cVar) {
        this.f6253a = cVar;
    }

    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return true;
    }

    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        SearchActionMode searchActionMode = (SearchActionMode) actionMode;
        searchActionMode.setAnchorView(this.f6253a.g);
        searchActionMode.setAnimateView(this.f6253a.e);
        searchActionMode.getSearchInput().addTextChangedListener(this.f6253a.l);
        return true;
    }

    public void onDestroyActionMode(ActionMode actionMode) {
        ((SearchActionMode) actionMode).getSearchInput().removeTextChangedListener(this.f6253a.l);
        this.f6253a.a();
        this.f6253a.g();
        this.f6253a.e();
    }

    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }
}
