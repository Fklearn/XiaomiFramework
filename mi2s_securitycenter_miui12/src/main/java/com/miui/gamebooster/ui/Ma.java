package com.miui.gamebooster.ui;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import com.miui.gamebooster.model.k;
import java.util.List;
import miui.util.FeatureParser;
import miui.view.SearchActionMode;

class Ma implements SearchActionMode.Callback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SelectGameActivity f4926a;

    Ma(SelectGameActivity selectGameActivity) {
        this.f4926a = selectGameActivity;
    }

    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return true;
    }

    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        SearchActionMode searchActionMode = (SearchActionMode) actionMode;
        if (!FeatureParser.getBoolean("is_mediatek", false)) {
            searchActionMode.setAnchorView(this.f4926a.f4986d);
            searchActionMode.setAnimateView(this.f4926a.f4985c);
        }
        searchActionMode.getSearchInput().addTextChangedListener(this.f4926a.u);
        return true;
    }

    public void onDestroyActionMode(ActionMode actionMode) {
        ((SearchActionMode) actionMode).getSearchInput().removeTextChangedListener(this.f4926a.u);
        this.f4926a.m();
        if (this.f4926a.f4984b != null) {
            SelectGameActivity selectGameActivity = this.f4926a;
            selectGameActivity.a((List<k>) selectGameActivity.f4984b);
            this.f4926a.o();
        }
    }

    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }
}
