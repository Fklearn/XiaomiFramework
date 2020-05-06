package com.miui.permcenter.privacymanager.behaviorrecord;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import miui.view.SearchActionMode;

class l implements SearchActionMode.Callback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppBehaviorRecordActivity f6450a;

    l(AppBehaviorRecordActivity appBehaviorRecordActivity) {
        this.f6450a = appBehaviorRecordActivity;
    }

    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return true;
    }

    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        SearchActionMode searchActionMode = (SearchActionMode) actionMode;
        searchActionMode.setAnchorView(this.f6450a.v);
        searchActionMode.setAnimateView(this.f6450a.i);
        searchActionMode.getSearchInput().addTextChangedListener(this.f6450a.E);
        return true;
    }

    public void onDestroyActionMode(ActionMode actionMode) {
        ((SearchActionMode) actionMode).getSearchInput().removeTextChangedListener(this.f6450a.E);
        this.f6450a.l();
        this.f6450a.o();
    }

    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }
}
