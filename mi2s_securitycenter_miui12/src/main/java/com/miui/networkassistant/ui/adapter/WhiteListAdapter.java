package com.miui.networkassistant.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.miui.networkassistant.model.WhiteGroupHeader;
import com.miui.networkassistant.model.WhiteListItem;
import com.miui.networkassistant.ui.view.WhiteAppListItemView;
import com.miui.networkassistant.ui.view.WhiteListHeaderView;
import com.miui.securitycenter.R;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WhiteListAdapter extends PinnedListAdapter {
    /* access modifiers changed from: private */
    public static Collator mCollator = Collator.getInstance(Locale.getDefault());
    private Comparator<WhiteListItem> mComparatorByLabel = new Comparator<WhiteListItem>() {
        public int compare(WhiteListItem whiteListItem, WhiteListItem whiteListItem2) {
            return WhiteListAdapter.mCollator.compare(whiteListItem.getAppLabel(), whiteListItem2.getAppLabel());
        }
    };
    private Comparator<WhiteListItem> mComparatorByState = new Comparator<WhiteListItem>() {
        public int compare(WhiteListItem whiteListItem, WhiteListItem whiteListItem2) {
            if (!whiteListItem.isEnabled() || whiteListItem2.isEnabled()) {
                return (whiteListItem.isEnabled() || !whiteListItem2.isEnabled()) ? 0 : 1;
            }
            return -1;
        }
    };
    private Map<WhiteGroupHeader, List<WhiteListItem>> mData = new HashMap();
    private Comparator<WhiteGroupHeader> mHeaderComparator = new Comparator<WhiteGroupHeader>() {
        public int compare(WhiteGroupHeader whiteGroupHeader, WhiteGroupHeader whiteGroupHeader2) {
            return whiteGroupHeader.getGroupHeaderType().ordinal() - whiteGroupHeader2.getGroupHeaderType().ordinal();
        }
    };
    private List<WhiteGroupHeader> mHeaders = new ArrayList();

    public WhiteListAdapter(Context context) {
        super(context);
    }

    private Comparator<WhiteListItem> getComparatorType(int i) {
        return i != 0 ? i != 1 ? this.mComparatorByLabel : this.mComparatorByState : this.mComparatorByLabel;
    }

    public int getCountForSection(int i) {
        return this.mData.get(this.mHeaders.get(i)).size();
    }

    public Object getItem(int i, int i2) {
        return Integer.valueOf(i2);
    }

    public long getItemId(int i, int i2) {
        return (long) i2;
    }

    public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mInflater.inflate(R.layout.listitem_white_app_view, (ViewGroup) null);
        }
        WhiteAppListItemView whiteAppListItemView = (WhiteAppListItemView) view;
        whiteAppListItemView.setOnSelectionListener(this.mAdapterListener);
        if (TextUtils.isEmpty(this.mSearchInputStr)) {
            whiteAppListItemView.fillData((WhiteListItem) this.mData.get(this.mHeaders.get(i)).get(i2));
        } else {
            whiteAppListItemView.fillData((WhiteListItem) this.mData.get(this.mHeaders.get(i)).get(i2), this.mSearchInputStr);
        }
        return view;
    }

    public int getSectionCount() {
        return this.mHeaders.size();
    }

    public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mInflater.inflate(R.layout.listitem_group_header_view, (ViewGroup) null);
        }
        ((WhiteListHeaderView) view).fillData(this.mHeaders.get(i));
        return view;
    }

    public void setHeaderTitle(WhiteGroupHeader.WhiteGroupHeaderType whiteGroupHeaderType, String str) {
        for (WhiteGroupHeader next : this.mHeaders) {
            if (next.getGroupHeaderType() == whiteGroupHeaderType) {
                next.setHeaderTitle(str);
            }
        }
    }

    public void updateData(Map<WhiteGroupHeader, List<WhiteListItem>> map, int i) {
        this.mData.clear();
        this.mHeaders.clear();
        for (WhiteGroupHeader next : map.keySet()) {
            this.mHeaders.add(next);
            List list = map.get(next);
            if (list.size() >= 2) {
                Collections.sort(list, getComparatorType(i));
            }
            this.mData.put(next, list);
        }
        if (this.mHeaders.size() >= 2) {
            Collections.sort(this.mHeaders, this.mHeaderComparator);
        }
        notifyDataSetChanged();
    }
}
