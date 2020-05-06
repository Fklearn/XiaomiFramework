package com.miui.privacyapps.ui;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import b.b.k.d;
import java.util.ArrayList;
import miui.util.FeatureParser;
import miui.view.SearchActionMode;

class g implements SearchActionMode.Callback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ n f7402a;

    g(n nVar) {
        this.f7402a = nVar;
    }

    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return true;
    }

    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        SearchActionMode searchActionMode = (SearchActionMode) actionMode;
        if (!FeatureParser.getBoolean("is_mediatek", false)) {
            searchActionMode.setAnchorView(this.f7402a.f);
            searchActionMode.setAnimateView(this.f7402a.f7412c);
        }
        searchActionMode.getSearchInput().addTextChangedListener(this.f7402a.u);
        return true;
    }

    public void onDestroyActionMode(ActionMode actionMode) {
        ((SearchActionMode) actionMode).getSearchInput().removeTextChangedListener(this.f7402a.u);
        this.f7402a.a();
        this.f7402a.g.a((ArrayList<d>) this.f7402a.r);
        this.f7402a.d();
    }

    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }
}
