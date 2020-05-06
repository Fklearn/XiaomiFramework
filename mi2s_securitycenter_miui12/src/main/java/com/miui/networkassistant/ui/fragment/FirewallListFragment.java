package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import b.b.c.c.b.h;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.analytics.AnalyticsUtil;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.service.FirewallService;
import com.miui.networkassistant.service.IFirewallBinder;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.traffic.statistic.PreSetGroup;
import com.miui.networkassistant.ui.adapter.BaseFirewallAdapter;
import com.miui.networkassistant.utils.LabelLoadHelper;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import miui.provider.ExtraNetwork;
import miui.view.SearchActionMode;
import miuix.recyclerview.widget.RecyclerView;

public abstract class FirewallListFragment extends h implements AppMonitorWrapper.AppMonitorListener {
    private static final int MSG_FIREWALL_APP_LIST_UPDATED = 2;
    private static final int MSG_FIREWALL_SERVICE_CONNECTED = 1;
    protected BaseFirewallAdapter mAdapter;
    protected ArrayList<AppInfo> mAppList;
    protected AppMonitorWrapper mAppMonitorWrapper;
    private ServiceConnection mConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            boolean unused = FirewallListFragment.this.mFirewallServiceConnected = true;
            FirewallListFragment.this.mFirewallBinder = IFirewallBinder.Stub.asInterface(iBinder);
            FirewallListFragment.this.mHandler.sendEmptyMessage(1);
            FirewallListFragment.this.onFirewallServiceConnected();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            FirewallListFragment firewallListFragment = FirewallListFragment.this;
            firewallListFragment.mFirewallBinder = null;
            boolean unused = firewallListFragment.mFirewallServiceConnected = false;
        }
    };
    protected View mContainerLayout;
    protected TextView mEmptyView;
    protected IFirewallBinder mFirewallBinder;
    private ContentObserver mFirewallObserver = new ContentObserver(this.mHandler) {
        public void onChange(boolean z) {
            if (!z) {
                FirewallListFragment.this.mAdapter.notifyDataSetChanged();
                FirewallListFragment.this.updateView();
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mFirewallServiceConnected;
    /* access modifiers changed from: private */
    public UIHandler mHandler = new UIHandler(this);
    /* access modifiers changed from: private */
    public boolean mIsInSearch;
    protected RecyclerView mRecyclerView;
    protected SearchActionMode mSearchActionMode;
    private SearchActionMode.Callback mSearchActionModeCallback = new SearchActionMode.Callback() {
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return true;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            FirewallListFragment.this.onCreateSearchView(actionMode, menu);
            SearchActionMode searchActionMode = (SearchActionMode) actionMode;
            searchActionMode.setAnchorView(FirewallListFragment.this.mSearchView);
            searchActionMode.setAnimateView(FirewallListFragment.this.mContainerLayout);
            searchActionMode.getSearchInput().addTextChangedListener(FirewallListFragment.this.mSearchTextWatcher);
            FirewallListFragment.this.mAdapter.setInSearch(true);
            boolean unused = FirewallListFragment.this.mIsInSearch = true;
            return true;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            FirewallListFragment.this.onDestroySearchView(actionMode);
            ((SearchActionMode) actionMode).getSearchInput().removeTextChangedListener(FirewallListFragment.this.mSearchTextWatcher);
            FirewallListFragment.this.exitSearchMode();
            FirewallListFragment.this.mAdapter.setSearchInput((String) null);
            FirewallListFragment.this.mAdapter.setInSearch(false);
            boolean unused = FirewallListFragment.this.mIsInSearch = false;
            FirewallListFragment firewallListFragment = FirewallListFragment.this;
            ArrayList<AppInfo> arrayList = firewallListFragment.mAppList;
            if (arrayList != null) {
                firewallListFragment.mAdapter.setData(arrayList);
            }
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }
    };
    protected TextView mSearchInputView;
    /* access modifiers changed from: private */
    public TextWatcher mSearchTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable editable) {
            if (FirewallListFragment.this.isSearchMode()) {
                String trim = editable.toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    FirewallListFragment.this.mAdapter.setSearchInput((String) null);
                    FirewallListFragment firewallListFragment = FirewallListFragment.this;
                    ArrayList<AppInfo> arrayList = firewallListFragment.mAppList;
                    if (arrayList != null) {
                        firewallListFragment.mAdapter.setData(arrayList);
                        return;
                    }
                    return;
                }
                FirewallListFragment.this.updateSearchResult(trim);
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    };
    protected View mSearchView;
    private View.OnClickListener onSearchViewClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            FirewallListFragment.this.startSearchMode();
        }
    };

    private static class LoadDataAsyncTask extends AsyncTask<Void, Void, ArrayList<AppInfo>> {
        private WeakReference<FirewallListFragment> mFragmentRef;

        LoadDataAsyncTask(FirewallListFragment firewallListFragment) {
            this.mFragmentRef = new WeakReference<>(firewallListFragment);
        }

        /* access modifiers changed from: protected */
        public ArrayList<AppInfo> doInBackground(Void... voidArr) {
            ArrayList<AppInfo> appList;
            FirewallListFragment firewallListFragment = (FirewallListFragment) this.mFragmentRef.get();
            if (firewallListFragment == null || (appList = firewallListFragment.getAppList()) == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            Iterator<AppInfo> it = appList.iterator();
            while (it.hasNext()) {
                AppInfo next = it.next();
                if (PreSetGroup.isPreFirewallWhiteListPackage(next.packageName.toString())) {
                    arrayList.add(next);
                }
            }
            appList.removeAll(arrayList);
            IFirewallBinder iFirewallBinder = firewallListFragment.mFirewallBinder;
            if (iFirewallBinder != null) {
                try {
                    firewallListFragment.mAdapter.setFirewallBinder(iFirewallBinder);
                    Collections.sort(appList, firewallListFragment.mAdapter.getComparator());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            return appList;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(ArrayList<AppInfo> arrayList) {
            FirewallListFragment firewallListFragment = (FirewallListFragment) this.mFragmentRef.get();
            if (firewallListFragment != null && arrayList != null && firewallListFragment.mFirewallBinder != null) {
                firewallListFragment.mAppList = arrayList;
                firewallListFragment.onPostLoadDataTask();
                firewallListFragment.mAdapter.setData(firewallListFragment.mAppList);
                firewallListFragment.updateView();
                firewallListFragment.hideLoadingView();
            }
        }
    }

    static class UIHandler extends Handler {
        private WeakReference<FirewallListFragment> mFragmentRef;

        UIHandler(FirewallListFragment firewallListFragment) {
            this.mFragmentRef = new WeakReference<>(firewallListFragment);
        }

        public void handleMessage(Message message) {
            FirewallListFragment firewallListFragment = (FirewallListFragment) this.mFragmentRef.get();
            if (firewallListFragment != null) {
                super.handleMessage(message);
                int i = message.what;
                if (i == 1 || i == 2) {
                    firewallListFragment.applyData();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public synchronized void applyData() {
        new LoadDataAsyncTask(this).execute(new Void[0]);
    }

    private void registerFirewallContentObserver() {
        ExtraNetwork.registerFirewallContentObserver(this.mAppContext, this.mFirewallObserver);
    }

    private void registerFirewallService() {
        Activity activity = this.mActivity;
        g.a((Context) activity, new Intent(activity, FirewallService.class), this.mConn, 1, B.k());
    }

    private void unRegisterFirewallContentObserver() {
        ExtraNetwork.unRegisterFirewallContentObserver(this.mAppContext, this.mFirewallObserver);
    }

    private void unRegisterFirewallService() {
        if (this.mFirewallServiceConnected) {
            this.mActivity.unbindService(this.mConn);
        }
    }

    /* access modifiers changed from: protected */
    public void exitSearchMode() {
        if (this.mSearchActionMode != null) {
            this.mSearchActionMode = null;
        }
    }

    /* access modifiers changed from: protected */
    public ArrayList<AppInfo> getAppList() {
        return this.mAppMonitorWrapper.getNonSystemAppList();
    }

    /* access modifiers changed from: protected */
    public FirewallRule getGroupChangeToRule(int i, int i2) {
        return i == i2 ? FirewallRule.Allow : i2 == 0 ? FirewallRule.Restrict : i2 > i / 2 ? FirewallRule.Restrict : FirewallRule.Allow;
    }

    /* access modifiers changed from: protected */
    public int getGroupHeadImageSource(int i, int i2) {
        return i == 0 ? R.drawable.firewall_enable_partial : i == i2 ? R.drawable.firewall_disable : i2 == 0 ? R.drawable.firewall_enable : i2 > i / 2 ? R.drawable.firewall_disable_partial : R.drawable.firewall_enable_partial;
    }

    /* access modifiers changed from: protected */
    public String getHeadViewDesp(int i, int i2) {
        return this.mAppContext.getString(i == i2 ? R.string.firewall_restrict_all : i2 == 0 ? R.string.firewall_allow_all : R.string.firewall_partial);
    }

    public void hideLoadingView() {
        super.hideLoadingView();
    }

    public boolean isSearchMode() {
        return this.mSearchActionMode != null;
    }

    public void onAppListUpdated() {
        if (isAttatched()) {
            this.mHandler.sendEmptyMessage(2);
        }
    }

    /* access modifiers changed from: protected */
    public abstract BaseFirewallAdapter onCreateListAdapter();

    /* access modifiers changed from: protected */
    public abstract View onCreateListTitleView(LayoutInflater layoutInflater, ViewGroup viewGroup);

    /* access modifiers changed from: protected */
    public void onCreateSearchView(ActionMode actionMode, Menu menu) {
    }

    /* access modifiers changed from: protected */
    public void onCreateView2(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.header_layout);
        View inflate = layoutInflater.inflate(R.layout.view_search, frameLayout, false);
        if (inflate != null && inflate.getParent() == null) {
            frameLayout.addView(inflate);
        }
        FrameLayout frameLayout2 = (FrameLayout) findViewById(R.id.list_tile_layout);
        View onCreateListTitleView = onCreateListTitleView(layoutInflater, viewGroup);
        if (onCreateListTitleView != null && onCreateListTitleView.getParent() == null) {
            frameLayout2.addView(onCreateListTitleView);
        }
        this.mContainerLayout = findViewById(R.id.container);
        this.mEmptyView = (TextView) findViewById(16908292);
        this.mEmptyView.setVisibility(8);
        this.mSearchView = findViewById(R.id.search_view);
        this.mSearchInputView = (TextView) this.mSearchView.findViewById(16908297);
        this.mSearchView.setOnClickListener(this.onSearchViewClickListener);
        this.mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.mRecyclerView.setSpringEnabled(false);
        this.mAdapter = onCreateListAdapter();
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mAppMonitorWrapper = AppMonitorWrapper.getInstance(this.mAppContext);
        this.mAppMonitorWrapper.registerLisener(this);
        registerFirewallContentObserver();
        registerFirewallService();
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.listtitlefragment_root;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        this.mAppMonitorWrapper.unRegisterLisener(this);
        unRegisterFirewallContentObserver();
        unRegisterFirewallService();
        this.mHandler.removeCallbacksAndMessages((Object) null);
    }

    /* access modifiers changed from: protected */
    public void onDestroySearchView(ActionMode actionMode) {
    }

    /* access modifiers changed from: protected */
    public void onFirewallServiceConnected() {
    }

    public void onPause() {
        super.onPause();
        AnalyticsUtil.recordPageEnd(getClass().getName());
    }

    /* access modifiers changed from: protected */
    public abstract void onPostLoadDataTask();

    public void onResume() {
        super.onResume();
        if (this.mIsInSearch) {
            this.mAdapter.notifyDataSetChanged();
        } else {
            applyData();
        }
        AnalyticsUtil.recordPageStart(getClass().getName());
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
        this.mEmptyView.setVisibility(8);
    }

    public void showLoadingView(boolean z) {
        super.showLoadingView(z);
        this.mEmptyView.setVisibility(8);
    }

    /* access modifiers changed from: protected */
    public void startSearchMode() {
        if (getActivity() != null) {
            this.mSearchActionMode = getActivity().startActionMode(this.mSearchActionModeCallback);
        }
    }

    /* access modifiers changed from: protected */
    public void updateSearchResult(String str) {
        ArrayList arrayList = new ArrayList();
        ArrayList<AppInfo> arrayList2 = this.mAppList;
        if (arrayList2 != null) {
            Iterator<AppInfo> it = arrayList2.iterator();
            while (it.hasNext()) {
                AppInfo next = it.next();
                if (LabelLoadHelper.loadLabel(this.mAppContext, next.packageName).toString().toLowerCase().indexOf(str.toLowerCase()) >= 0) {
                    arrayList.add(next);
                }
            }
        }
        if (arrayList.isEmpty()) {
            setEmptyText((int) R.string.search_result_text);
        }
        this.mAdapter.setSearchInput(str);
        this.mAdapter.setData(arrayList);
    }

    /* access modifiers changed from: protected */
    public void updateView() {
    }
}
