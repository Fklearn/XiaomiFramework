package com.miui.networkassistant.ui.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import b.b.c.c.b.h;
import com.miui.analytics.AnalyticsUtil;
import com.miui.common.expandableview.PinnedHeaderListView;
import com.miui.networkassistant.ui.adapter.PinnedListAdapter;
import com.miui.securitycenter.R;
import miui.view.SearchActionMode;

public abstract class PinnedListFragment extends h {
    protected PinnedListAdapter mAdapter;
    protected TextView mEmptyView;
    protected PinnedHeaderListView mList;
    protected SearchActionMode mSearchActionMode;

    public void exitSearchMode() {
        if (this.mSearchActionMode != null) {
            this.mSearchActionMode = null;
        }
    }

    public PinnedHeaderListView getListView() {
        return this.mList;
    }

    public void hideLoadingView() {
        super.hideLoadingView();
        this.mList.setEmptyView(this.mEmptyView);
    }

    public boolean isSearchMode() {
        return this.mSearchActionMode != null;
    }

    /* access modifiers changed from: protected */
    public abstract View onCreateFooterView(LayoutInflater layoutInflater, ViewGroup viewGroup);

    /* access modifiers changed from: protected */
    public abstract View onCreateHeaderView(LayoutInflater layoutInflater, ViewGroup viewGroup);

    /* access modifiers changed from: protected */
    public abstract PinnedListAdapter onCreateListAdapter();

    /* access modifiers changed from: protected */
    public void onCreateView2(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.header_layout);
        View onCreateHeaderView = onCreateHeaderView(layoutInflater, frameLayout);
        if (onCreateHeaderView != null && onCreateHeaderView.getParent() == null) {
            frameLayout.addView(onCreateHeaderView);
        }
        FrameLayout frameLayout2 = (FrameLayout) findViewById(R.id.footer_layout);
        View onCreateFooterView = onCreateFooterView(layoutInflater, viewGroup);
        if (onCreateFooterView != null && onCreateFooterView.getParent() == null) {
            frameLayout2.addView(onCreateFooterView);
        }
        this.mList = (PinnedHeaderListView) findViewById(R.id.list);
        this.mEmptyView = (TextView) findViewById(16908292);
        this.mAdapter = onCreateListAdapter();
        this.mList.setAdapter((ListAdapter) this.mAdapter);
        this.mList.setEmptyView(this.mEmptyView);
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.fragment_roaming_whilte_list;
    }

    public void onPause() {
        super.onPause();
        AnalyticsUtil.recordPageEnd(PinnedListFragment.class.getName());
    }

    public void onResume() {
        super.onResume();
        AnalyticsUtil.recordPageStart(PinnedListFragment.class.getName());
    }

    public void setEmptyImage(Drawable drawable) {
        this.mEmptyView.setCompoundDrawables((Drawable) null, drawable, (Drawable) null, (Drawable) null);
    }

    public void setEmptyText(int i) {
        this.mEmptyView.setText(i);
    }

    public void setEmptyText(CharSequence charSequence) {
        this.mEmptyView.setText(charSequence);
    }

    public void showEmptyView() {
        this.mEmptyView.setVisibility(0);
    }

    public void showLoadingView() {
        super.showLoadingView();
        this.mList.setEmptyView((View) null);
        this.mEmptyView.setVisibility(8);
    }

    public void showLoadingView(boolean z) {
        super.showLoadingView(z);
        this.mList.setEmptyView((View) null);
        this.mEmptyView.setVisibility(8);
    }

    public void startSearchMode(SearchActionMode.Callback callback) {
        if (getActivity() != null) {
            this.mSearchActionMode = getActivity().startActionMode(callback);
        }
    }
}
