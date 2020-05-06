package com.miui.common.expandableview;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.miui.common.expandableview.PinnedHeaderListView;

public abstract class a extends BaseAdapter implements PinnedHeaderListView.d {
    private static int HEADER_VIEW_TYPE;
    private static int ITEM_VIEW_TYPE;
    private int mCount = -1;
    private SparseArray<Integer> mSectionCache = new SparseArray<>();
    private int mSectionCount = -1;
    private SparseArray<Integer> mSectionCountCache = new SparseArray<>();
    private SparseArray<Integer> mSectionPositionCache = new SparseArray<>();

    private int internalGetCountForSection(int i) {
        Integer num = this.mSectionCountCache.get(i);
        if (num != null) {
            return num.intValue();
        }
        int countForSection = getCountForSection(i);
        this.mSectionCountCache.put(i, Integer.valueOf(countForSection));
        return countForSection;
    }

    private int internalGetSectionCount() {
        int i = this.mSectionCount;
        if (i >= 0) {
            return i;
        }
        this.mSectionCount = getSectionCount();
        return this.mSectionCount;
    }

    public final int getCount() {
        int i = this.mCount;
        if (i >= 0) {
            return i;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < internalGetSectionCount(); i3++) {
            i2 = i2 + internalGetCountForSection(i3) + 1;
        }
        this.mCount = i2;
        return i2;
    }

    public abstract int getCountForSection(int i);

    public final Object getItem(int i) {
        return getItem(getSectionForPosition(i), getPositionInSectionForPosition(i));
    }

    public abstract Object getItem(int i, int i2);

    public final long getItemId(int i) {
        return getItemId(getSectionForPosition(i), getPositionInSectionForPosition(i));
    }

    public abstract long getItemId(int i, int i2);

    public abstract View getItemView(int i, int i2, View view, ViewGroup viewGroup);

    public final int getItemViewType(int i) {
        return isSectionHeader(i) ? getItemViewTypeCount() + getSectionHeaderViewType(getSectionForPosition(i)) : getItemViewType(getSectionForPosition(i), getPositionInSectionForPosition(i));
    }

    public int getItemViewType(int i, int i2) {
        return ITEM_VIEW_TYPE;
    }

    public int getItemViewTypeCount() {
        return 1;
    }

    public int getPositionInSectionForPosition(int i) {
        Integer num = this.mSectionPositionCache.get(i);
        if (num != null) {
            return num.intValue();
        }
        int i2 = 0;
        int i3 = 0;
        while (i2 < internalGetSectionCount()) {
            int internalGetCountForSection = internalGetCountForSection(i2) + i3 + 1;
            if (i < i3 || i >= internalGetCountForSection) {
                i2++;
                i3 = internalGetCountForSection;
            } else {
                int i4 = (i - i3) - 1;
                this.mSectionPositionCache.put(i, Integer.valueOf(i4));
                return i4;
            }
        }
        return 0;
    }

    public abstract int getSectionCount();

    public final int getSectionForPosition(int i) {
        Integer num = this.mSectionCache.get(i);
        if (num != null) {
            return num.intValue();
        }
        int i2 = 0;
        int i3 = 0;
        while (i2 < internalGetSectionCount()) {
            int internalGetCountForSection = internalGetCountForSection(i2) + i3 + 1;
            if (i < i3 || i >= internalGetCountForSection) {
                i2++;
                i3 = internalGetCountForSection;
            } else {
                this.mSectionCache.put(i, Integer.valueOf(i2));
                return i2;
            }
        }
        return 0;
    }

    public abstract View getSectionHeaderView(int i, View view, ViewGroup viewGroup);

    public int getSectionHeaderViewType(int i) {
        return HEADER_VIEW_TYPE;
    }

    public int getSectionHeaderViewTypeCount() {
        return 1;
    }

    public final View getView(int i, View view, ViewGroup viewGroup) {
        return isSectionHeader(i) ? getSectionHeaderView(getSectionForPosition(i), view, viewGroup) : getItemView(getSectionForPosition(i), getPositionInSectionForPosition(i), view, viewGroup);
    }

    public final int getViewTypeCount() {
        return getItemViewTypeCount() + getSectionHeaderViewTypeCount();
    }

    public final boolean isSectionHeader(int i) {
        int i2 = 0;
        for (int i3 = 0; i3 < internalGetSectionCount(); i3++) {
            if (i == i2) {
                return true;
            }
            if (i < i2) {
                return false;
            }
            i2 += internalGetCountForSection(i3) + 1;
        }
        return false;
    }

    public void notifyDataSetChanged() {
        this.mSectionCache.clear();
        this.mSectionPositionCache.clear();
        this.mSectionCountCache.clear();
        this.mCount = -1;
        this.mSectionCount = -1;
        super.notifyDataSetChanged();
    }

    public void notifyDataSetInvalidated() {
        this.mSectionCache.clear();
        this.mSectionPositionCache.clear();
        this.mSectionCountCache.clear();
        this.mCount = -1;
        this.mSectionCount = -1;
        super.notifyDataSetInvalidated();
    }
}
