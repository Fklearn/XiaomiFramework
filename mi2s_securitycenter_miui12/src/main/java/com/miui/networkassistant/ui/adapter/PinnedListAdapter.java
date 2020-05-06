package com.miui.networkassistant.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.miui.common.expandableview.a;
import com.miui.networkassistant.model.WhiteGroupHeader;
import com.miui.networkassistant.model.WhiteListItem;
import java.util.List;
import java.util.Map;

public abstract class PinnedListAdapter extends a {
    public static final int SORTED_BY_NAME = 0;
    public static final int SORTED_BY_STATE = 1;
    public AppSelectionAdapterListener mAdapterListener;
    public LayoutInflater mInflater;
    protected String mSearchInputStr;

    public interface AppSelectionAdapterListener {
        void onAppSelected(View view, Object obj, boolean z);
    }

    public PinnedListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public abstract void setHeaderTitle(WhiteGroupHeader.WhiteGroupHeaderType whiteGroupHeaderType, String str);

    public void setOnSelectionListener(AppSelectionAdapterListener appSelectionAdapterListener) {
        this.mAdapterListener = appSelectionAdapterListener;
    }

    public void setSearchInput(String str) {
        this.mSearchInputStr = str;
    }

    public abstract void updateData(Map<WhiteGroupHeader, List<WhiteListItem>> map, int i);
}
