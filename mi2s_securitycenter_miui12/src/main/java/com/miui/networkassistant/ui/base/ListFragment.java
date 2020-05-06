package com.miui.networkassistant.ui.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.c.b.h;
import com.miui.analytics.AnalyticsUtil;
import com.miui.securitycenter.R;
import miui.view.SearchActionMode;

public abstract class ListFragment extends h {
    protected RecyclerView.a mAdapter;
    protected TextView mEmptyView;
    protected miuix.recyclerview.widget.RecyclerView mRecyclerView;
    protected SearchActionMode mSearchActionMode;

    public void exitSearchMode() {
        if (this.mSearchActionMode != null) {
            this.mSearchActionMode = null;
        }
    }

    public miuix.recyclerview.widget.RecyclerView getListView() {
        return this.mRecyclerView;
    }

    public void hideLoadingView() {
        super.hideLoadingView();
    }

    public boolean isSearchMode() {
        return this.mSearchActionMode != null;
    }

    /* access modifiers changed from: protected */
    public abstract View onCreateFooterView(LayoutInflater layoutInflater, ViewGroup viewGroup);

    /* access modifiers changed from: protected */
    public abstract View onCreateHeaderView(LayoutInflater layoutInflater, ViewGroup viewGroup);

    /* access modifiers changed from: protected */
    public abstract RecyclerView.a onCreateListAdapter();

    /* access modifiers changed from: protected */
    public void onCreateView2(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.header_layout);
        View onCreateHeaderView = onCreateHeaderView(layoutInflater, frameLayout);
        if (onCreateHeaderView != null && onCreateHeaderView.getParent() == null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
            layoutParams.setMargins(0, 0, 0, 0);
            frameLayout.addView(onCreateHeaderView, layoutParams);
        }
        FrameLayout frameLayout2 = (FrameLayout) findViewById(R.id.footer_layout);
        View onCreateFooterView = onCreateFooterView(layoutInflater, viewGroup);
        if (onCreateFooterView != null && onCreateFooterView.getParent() == null) {
            frameLayout2.addView(onCreateFooterView);
        }
        this.mRecyclerView = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.list_view);
        this.mRecyclerView.setSpringEnabled(false);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.mAdapter = onCreateListAdapter();
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mEmptyView = (TextView) findViewById(16908292);
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.listfragment_root;
    }

    public void onPause() {
        super.onPause();
        AnalyticsUtil.recordPageEnd(getClass().getName());
    }

    public void onResume() {
        super.onResume();
        AnalyticsUtil.recordPageStart(getClass().getName());
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

    public void showEmptyView(boolean z) {
        this.mEmptyView.setVisibility(z ? 0 : 8);
    }

    public void showLoadingView() {
        super.showLoadingView();
        this.mEmptyView.setVisibility(8);
    }

    public void showLoadingView(boolean z) {
        super.showLoadingView(z);
        this.mEmptyView.setVisibility(8);
    }

    public void startSearchMode(SearchActionMode.Callback callback) {
        if (getActivity() != null) {
            this.mSearchActionMode = getActivity().startActionMode(callback);
        }
    }
}
