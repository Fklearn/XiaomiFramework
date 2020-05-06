package com.miui.networkassistant.ui.base.recyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import b.b.c.c.a.b;
import b.b.c.c.b.d;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.WhiteListItem;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import miui.util.FeatureParser;
import miui.view.SearchActionMode;
import miuix.recyclerview.widget.RecyclerView;

public abstract class BaseAppWhiteListFragment extends d {
    private final ArrayList<WhiteListItem> mDataList = new ArrayList<>();
    private View mEmptyView;
    private final ArrayList<WhiteListItem> mFilterDataList = new ArrayList<>();
    private AppMonitorWrapper mMonitorCenter;
    private AppMonitorWrapper.AppMonitorListener mMonitorCenterListener = new AppMonitorWrapper.AppMonitorListener() {
        public void onAppListUpdated() {
            if (BaseAppWhiteListFragment.this.mActivity != null) {
                BaseAppWhiteListFragment baseAppWhiteListFragment = BaseAppWhiteListFragment.this;
                baseAppWhiteListFragment.postOnUiThread(new b(baseAppWhiteListFragment) {
                    public void runOnUiThread() {
                        BaseAppWhiteListFragment.this.reLoadView();
                    }
                });
            }
        }
    };
    /* access modifiers changed from: private */
    public MultiRecycleViewHelper mMultiRecycleViewHelper;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            WhiteListItem dataItem = BaseAppWhiteListFragment.this.mMultiRecycleViewHelper.getDataItem(((Integer) compoundButton.getTag()).intValue());
            dataItem.setEnabled(z);
            BaseAppWhiteListFragment.this.onItemSwitched(dataItem, z);
            BaseAppWhiteListFragment.this.mMultiRecycleViewHelper.notifyDataSetChanged();
            BaseAppWhiteListFragment.this.mMultiRecycleViewHelper.addPageDecoration(BaseAppWhiteListFragment.this.onEnableGroupRes());
        }
    };
    /* access modifiers changed from: private */
    public RecyclerView mRecyclerView;
    protected SearchActionMode mSearchActionMode;
    /* access modifiers changed from: private */
    public SearchActionMode.Callback mSearchActionModeCallback = new SearchActionMode.Callback() {
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return true;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            SearchActionMode searchActionMode = (SearchActionMode) actionMode;
            if (!FeatureParser.getBoolean("is_mediatek", false)) {
                searchActionMode.setAnchorView(BaseAppWhiteListFragment.this.mSearchView);
                searchActionMode.setAnimateView(BaseAppWhiteListFragment.this.mRecyclerView);
            }
            searchActionMode.getSearchInput().addTextChangedListener(BaseAppWhiteListFragment.this.mSearchTextWatcher);
            return true;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            ((SearchActionMode) actionMode).getSearchInput().removeTextChangedListener(BaseAppWhiteListFragment.this.mSearchTextWatcher);
            BaseAppWhiteListFragment.this.exitSearchMode();
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }
    };
    private TextView mSearchInputView;
    /* access modifiers changed from: private */
    public TextWatcher mSearchTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable editable) {
            if (BaseAppWhiteListFragment.this.isSearchMode()) {
                BaseAppWhiteListFragment.this.updateSearchResult(editable.toString().trim());
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    };
    /* access modifiers changed from: private */
    public View mSearchView;

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager;
        Activity activity = getActivity();
        if (activity != null && (inputMethodManager = (InputMethodManager) activity.getSystemService("input_method")) != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void registerMonitorCenter() {
        this.mMonitorCenter = AppMonitorWrapper.getInstance(this.mAppContext);
        this.mMonitorCenter.registerLisener(this.mMonitorCenterListener);
    }

    /* access modifiers changed from: private */
    public void startSearchMode(SearchActionMode.Callback callback) {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            this.mSearchActionMode = activity.startActionMode(callback);
        }
    }

    private void unRegisterMonitorCenter() {
        AppMonitorWrapper appMonitorWrapper = this.mMonitorCenter;
        if (appMonitorWrapper != null) {
            appMonitorWrapper.unRegisterLisener(this.mMonitorCenterListener);
        }
    }

    /* access modifiers changed from: private */
    public void updateSearchResult(String str) {
        this.mFilterDataList.clear();
        if (!TextUtils.isEmpty(str)) {
            Iterator<WhiteListItem> it = this.mDataList.iterator();
            while (it.hasNext()) {
                WhiteListItem next = it.next();
                if (next.getAppLabel().contains(str)) {
                    this.mFilterDataList.add(next);
                }
            }
        } else {
            this.mFilterDataList.clear();
            this.mFilterDataList.addAll(this.mDataList);
        }
        this.mMultiRecycleViewHelper.setData(this.mFilterDataList);
        this.mMultiRecycleViewHelper.addPageDecoration(onEnableGroupRes());
    }

    public /* synthetic */ void a(View view, boolean z) {
        if (!z) {
            hideKeyboard(view);
        }
    }

    public void exitSearchMode() {
        if (this.mSearchActionMode != null) {
            this.mSearchActionMode = null;
        }
        reLoadView();
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        this.mMultiRecycleViewHelper = MultiRecycleViewHelper.getNewInstance(getActivity(), this.mRecyclerView);
        this.mMultiRecycleViewHelper.init(this.mOnCheckedChangeListener);
        this.mEmptyView = findViewById(R.id.empty_view);
        this.mSearchView = findViewById(R.id.search_view);
        this.mSearchInputView = (TextView) this.mSearchView.findViewById(16908297);
        if (this.mSearchInputView == null) {
            this.mSearchInputView = (TextView) this.mSearchView.findViewById(R.id.input);
            this.mSearchInputView.addTextChangedListener(this.mSearchTextWatcher);
            this.mSearchInputView.setOnFocusChangeListener(new a(this));
        }
        this.mSearchView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BaseAppWhiteListFragment baseAppWhiteListFragment = BaseAppWhiteListFragment.this;
                baseAppWhiteListFragment.startSearchMode(baseAppWhiteListFragment.mSearchActionModeCallback);
            }
        });
        registerMonitorCenter();
        onInit();
    }

    public boolean isSearchMode() {
        return this.mSearchActionMode != null;
    }

    /* access modifiers changed from: protected */
    public abstract ArrayList<WhiteListItem> onAppInfoListChange(ArrayList<AppInfo> arrayList);

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        getActivity().getWindow().setSoftInputMode(20);
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_white_list;
    }

    public void onDestroy() {
        super.onDestroy();
        unRegisterMonitorCenter();
    }

    /* access modifiers changed from: protected */
    public abstract int onEnableGroupRes();

    /* access modifiers changed from: protected */
    public abstract void onInit();

    /* access modifiers changed from: protected */
    public abstract void onItemSwitched(WhiteListItem whiteListItem, boolean z);

    /* access modifiers changed from: protected */
    public void reLoadView() {
        ArrayList<WhiteListItem> onAppInfoListChange = onAppInfoListChange(this.mMonitorCenter.getFilteredAppInfosList());
        if (onAppInfoListChange != null) {
            registerMonitorCenter();
            this.mDataList.clear();
            this.mFilterDataList.clear();
            this.mDataList.addAll(onAppInfoListChange);
            this.mFilterDataList.addAll(this.mDataList);
            this.mMultiRecycleViewHelper.setData(this.mFilterDataList);
            this.mEmptyView.setVisibility(this.mFilterDataList.size() == 0 ? 0 : 8);
            this.mMultiRecycleViewHelper.addPageDecoration(onEnableGroupRes());
            uploadSerchView(this.mDataList);
            this.mSearchInputView.setHint(this.mAppContext.getResources().getQuantityString(R.plurals.search_app_count_txt_na, this.mDataList.size(), new Object[]{Integer.valueOf(this.mDataList.size())}));
        }
    }

    public void uploadSerchView(ArrayList<WhiteListItem> arrayList) {
        if (arrayList != null) {
            String format = String.format(getResources().getQuantityString(R.plurals.find_applications, arrayList.size()), new Object[]{Integer.valueOf(arrayList.size())});
            this.mSearchInputView.setHint(format);
            this.mSearchInputView.setContentDescription(format);
        }
    }
}
