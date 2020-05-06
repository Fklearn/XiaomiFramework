package com.miui.applicationlock;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import com.miui.applicationlock.c.F;
import java.util.List;
import miui.util.FeatureParser;
import miui.view.SearchActionMode;

/* renamed from: com.miui.applicationlock.l  reason: case insensitive filesystem */
class C0285l implements SearchActionMode.Callback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0312y f3360a;

    C0285l(C0312y yVar) {
        this.f3360a = yVar;
    }

    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return true;
    }

    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        SearchActionMode searchActionMode = (SearchActionMode) actionMode;
        if (!FeatureParser.getBoolean("is_mediatek", false)) {
            searchActionMode.setAnchorView(this.f3360a.e);
            searchActionMode.setAnimateView(this.f3360a.f3469c);
        }
        searchActionMode.getSearchInput().addTextChangedListener(this.f3360a.F);
        return true;
    }

    public void onDestroyActionMode(ActionMode actionMode) {
        ((SearchActionMode) actionMode).getSearchInput().removeTextChangedListener(this.f3360a.F);
        this.f3360a.a();
        this.f3360a.f.a((List<F>) this.f3360a.j, true);
        this.f3360a.d();
    }

    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }
}
