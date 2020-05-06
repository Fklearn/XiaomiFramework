package com.miui.gamebooster.ui;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import com.miui.gamebooster.model.k;
import java.util.List;
import miui.util.FeatureParser;
import miui.view.SearchActionMode;

class ab implements SearchActionMode.Callback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WhiteListFragment f5046a;

    ab(WhiteListFragment whiteListFragment) {
        this.f5046a = whiteListFragment;
    }

    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return true;
    }

    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        SearchActionMode searchActionMode = (SearchActionMode) actionMode;
        if (!FeatureParser.getBoolean("is_mediatek", false)) {
            searchActionMode.setAnchorView(this.f5046a.f5027d);
            searchActionMode.setAnimateView(this.f5046a.f5026c);
        }
        searchActionMode.getSearchInput().addTextChangedListener(this.f5046a.p);
        return true;
    }

    public void onDestroyActionMode(ActionMode actionMode) {
        ((SearchActionMode) actionMode).getSearchInput().removeTextChangedListener(this.f5046a.p);
        this.f5046a.exitSearchMode();
        if (this.f5046a.f5025b != null) {
            WhiteListFragment whiteListFragment = this.f5046a;
            whiteListFragment.a((List<k>) whiteListFragment.f5025b, false);
            this.f5046a.f();
        }
    }

    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }
}
