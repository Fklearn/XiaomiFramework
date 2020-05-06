package com.miui.luckymoney.ui.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;
import b.b.c.b.b;
import b.b.c.j.B;
import b.b.c.j.x;
import com.miui.appmanager.AppManageUtils;
import com.miui.common.expandableview.PinnedHeaderListView;
import com.miui.common.expandableview.WrapPinnedHeaderListView;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.config.FastOpenConfig;
import com.miui.luckymoney.model.FastOpenAppInfo;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.luckymoney.ui.adapter.FastOpenListAdapter;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import miui.app.Activity;
import miui.view.SearchActionMode;
import miui.widget.SlidingButton;

public class FastOpenListActivity extends BaseMiuiActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "FastOpenListActivity";
    /* access modifiers changed from: private */
    public int mChangedItem = 0;
    /* access modifiers changed from: private */
    public CommonConfig mCommonConfig;
    private CompoundButton.OnCheckedChangeListener mFastOpenChangeListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (z) {
                FastOpenListActivity.this.showOpenDialog();
            } else {
                FastOpenListActivity.this.mCommonConfig.setFastOpenEnable(z);
                FastOpenListActivity.this.updateViewState();
            }
            FastOpenListActivity.this.mListAdapter.setEnabled(z);
        }
    };
    /* access modifiers changed from: private */
    public FastOpenConfig mFastOpenConfig;
    private View mFastOpenViewGroup;
    ArrayList<FastOpenAppInfo> mInfos = new ArrayList<>();
    private CompoundButton.OnCheckedChangeListener mItemCheckedChangedListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            PackageInfo packageInfo = (PackageInfo) compoundButton.getTag();
            if (FastOpenListActivity.this.mFastOpenConfig.contains(packageInfo.packageName) == z) {
                FastOpenListActivity fastOpenListActivity = FastOpenListActivity.this;
                int unused = fastOpenListActivity.mChangedItem = fastOpenListActivity.mChangedItem + (z ? 1 : -1);
                FastOpenListActivity.this.updateHeader();
            }
            FastOpenListActivity.this.mFastOpenConfig.set(packageInfo.packageName, z);
            FastOpenListActivity.this.mFastOpenConfig.saveConfig();
        }
    };
    private View mLayoutFastOpen;
    /* access modifiers changed from: private */
    public FastOpenListAdapter mListAdapter;
    /* access modifiers changed from: private */
    public WrapPinnedHeaderListView mListView;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            FastOpenListActivity.this.mSlidingButtonFastOpen.toggle();
        }
    };
    private PackageManager mPackageManager;
    private SearchActionMode mSearchActionMode;
    private SearchActionMode.Callback mSearchActionModeCallback = new SearchActionMode.Callback() {
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return true;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            SearchActionMode searchActionMode = (SearchActionMode) actionMode;
            searchActionMode.setAnchorView(FastOpenListActivity.this.mSearchView);
            searchActionMode.setAnimateView(FastOpenListActivity.this.mListView);
            searchActionMode.getSearchInput().addTextChangedListener(FastOpenListActivity.this.mSearchTextWatcher);
            return true;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            ((SearchActionMode) actionMode).getSearchInput().removeTextChangedListener(FastOpenListActivity.this.mSearchTextWatcher);
            FastOpenListActivity.this.exitSearchMode();
            FastOpenListActivity.this.mListAdapter.updateData(FastOpenListActivity.this.mInfos);
            FastOpenListActivity.this.mListAdapter.notifyDataSetChanged();
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }
    };
    /* access modifiers changed from: private */
    public TextWatcher mSearchTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable editable) {
            String trim = editable.toString().trim();
            if (!TextUtils.isEmpty(trim)) {
                FastOpenListActivity.this.updateSearchResult(trim);
                return;
            }
            FastOpenListActivity.this.mListAdapter.updateData(FastOpenListActivity.this.mInfos);
            FastOpenListActivity.this.mListAdapter.notifyDataSetChanged();
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    };
    /* access modifiers changed from: private */
    public View mSearchView;
    /* access modifiers changed from: private */
    public SlidingButton mSlidingButtonFastOpen;

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, com.miui.luckymoney.ui.activity.FastOpenListActivity] */
    private void initData() {
        this.mInfos.clear();
        this.mChangedItem = 0;
        FastOpenAppInfo fastOpenAppInfo = new FastOpenAppInfo(false);
        FastOpenAppInfo fastOpenAppInfo2 = new FastOpenAppInfo(true);
        List<PackageInfo> a2 = b.a((Context) this).a();
        List list = AppManageUtils.a((Context) this, this.mPackageManager, AppManageUtils.d((Context) this), (HashSet<ComponentName>) new HashSet()).get(B.c());
        for (PackageInfo next : a2) {
            if (list.contains(next.packageName)) {
                if (this.mFastOpenConfig.isRestrict(next.packageName)) {
                    fastOpenAppInfo.add(next);
                } else {
                    fastOpenAppInfo2.add(next);
                }
            }
        }
        if (fastOpenAppInfo.getPackageInfos().size() > 0) {
            setSectionTitle(fastOpenAppInfo);
            this.mInfos.add(fastOpenAppInfo);
        }
        if (fastOpenAppInfo2.getPackageInfos().size() > 0) {
            setSectionTitle(fastOpenAppInfo2);
            this.mInfos.add(fastOpenAppInfo2);
        }
        this.mListAdapter.updateData(this.mInfos);
    }

    private void setSectionTitle(FastOpenAppInfo fastOpenAppInfo) {
        int i;
        Resources resources;
        int i2 = fastOpenAppInfo.isFastOpen() ? this.mChangedItem : -this.mChangedItem;
        if (fastOpenAppInfo.isFastOpen()) {
            resources = getResources();
            i = R.string.fast_open_list_open_title;
        } else {
            resources = getResources();
            i = R.string.fast_open_list_not_open_title;
        }
        fastOpenAppInfo.setTitle(String.format(resources.getString(i), new Object[]{Integer.valueOf(fastOpenAppInfo.getPackageInfos().size() + i2)}));
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.content.Context, miui.app.Activity, com.miui.luckymoney.ui.activity.FastOpenListActivity] */
    /* access modifiers changed from: private */
    public void showOpenDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.open_fast_open_mode);
        View inflate = View.inflate(this, R.layout.fast_dialog_layout, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.dialog_message)).setText(R.string.fast_open_dialog_message);
        ((TextView) inflate.findViewById(R.id.dialog_message_summary)).setText(R.string.fast_open_dialog_message_summary);
        builder.setView(inflate);
        builder.setNegativeButton(R.string.hongbao_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                FastOpenListActivity.this.mSlidingButtonFastOpen.setChecked(false);
            }
        });
        builder.setPositiveButton(R.string.fast_open_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                FastOpenListActivity.this.mCommonConfig.setFastOpenEnable(true);
                FastOpenListActivity.this.updateViewState();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialogInterface) {
                FastOpenListActivity.this.mSlidingButtonFastOpen.setChecked(false);
            }
        });
        if (!isFinishing()) {
            builder.create().show();
        }
    }

    /* access modifiers changed from: private */
    public void updateHeader() {
        Iterator<FastOpenAppInfo> it = this.mInfos.iterator();
        while (it.hasNext()) {
            setSectionTitle(it.next());
        }
        if (this.mSearchActionMode != null) {
            for (FastOpenAppInfo sectionTitle : this.mListAdapter.getData()) {
                setSectionTitle(sectionTitle);
            }
        }
        PinnedHeaderListView listView = this.mListView.getListView();
        int childCount = listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.mListAdapter.updateHeader(listView.getChildAt(i));
        }
        View currentHeader = listView.getCurrentHeader();
        if (currentHeader != null) {
            this.mListAdapter.updateHeader(currentHeader);
        }
        listView.invalidate();
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [android.content.Context, com.miui.luckymoney.ui.activity.FastOpenListActivity] */
    /* access modifiers changed from: private */
    public void updateSearchResult(String str) {
        ArrayList arrayList = new ArrayList();
        Iterator<FastOpenAppInfo> it = this.mInfos.iterator();
        while (it.hasNext()) {
            FastOpenAppInfo next = it.next();
            if (next.getPackageInfos() != null && next.getPackageInfos().size() > 0) {
                ArrayList arrayList2 = new ArrayList(10);
                Iterator<PackageInfo> it2 = next.getPackageInfos().iterator();
                while (it2.hasNext()) {
                    PackageInfo next2 = it2.next();
                    if (next2.packageName.toLowerCase().indexOf(str.toLowerCase()) >= 0 || x.a((Context) this, next2.applicationInfo).indexOf(str.toLowerCase()) >= 0) {
                        arrayList2.add(next2);
                    }
                }
                if (arrayList2.size() > 0) {
                    FastOpenAppInfo fastOpenAppInfo = new FastOpenAppInfo(next.isFastOpen());
                    fastOpenAppInfo.setTitle(next.getTitle());
                    fastOpenAppInfo.setPackageInfos(arrayList2);
                    arrayList.add(fastOpenAppInfo);
                }
            }
        }
        this.mListAdapter.updateData(arrayList);
        this.mListAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void updateViewState() {
        boolean z;
        View view;
        if (!this.mCommonConfig.isFastOpenEnable()) {
            view = this.mSearchView;
            z = false;
        } else {
            view = this.mSearchView;
            z = true;
        }
        view.setEnabled(z);
    }

    public void exitSearchMode() {
        if (this.mSearchActionMode != null) {
            this.mFastOpenViewGroup.setVisibility(0);
            this.mSearchActionMode = null;
        }
    }

    public boolean isSearchMode() {
        return this.mSearchActionMode != null;
    }

    public void onClick(View view) {
        if (view == this.mSearchView) {
            startSearchMode(this.mSearchActionModeCallback);
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, android.view.View$OnClickListener, miui.app.Activity, android.widget.AdapterView$OnItemClickListener, com.miui.luckymoney.ui.activity.FastOpenListActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        FastOpenListActivity.super.onCreate(bundle);
        setContentView(R.layout.activity_lucky_fast_open);
        k.a((Activity) this);
        this.mPackageManager = getPackageManager();
        this.mCommonConfig = CommonConfig.getInstance(this);
        this.mFastOpenConfig = FastOpenConfig.getInstance(this);
        this.mFastOpenViewGroup = findViewById(R.id.layout_fast_open_mode_group);
        this.mListView = (WrapPinnedHeaderListView) findViewById(R.id.list_view);
        this.mListView.getListView().setPinHeaders(false);
        this.mSlidingButtonFastOpen = findViewById(R.id.sliding_button_fast_open);
        this.mLayoutFastOpen = findViewById(R.id.layout_open_fast_open_mode);
        this.mListAdapter = new FastOpenListAdapter(this);
        this.mListAdapter.setOnCheckedChangeListener(this.mItemCheckedChangedListener);
        this.mListAdapter.setEnabled(this.mCommonConfig.isFastOpenEnable());
        this.mListView.setAdapter(this.mListAdapter);
        this.mListView.setOnItemClickListener(this);
        this.mSlidingButtonFastOpen.setChecked(this.mCommonConfig.isFastOpenEnable());
        this.mSlidingButtonFastOpen.setOnCheckedChangeListener(this.mFastOpenChangeListener);
        this.mSearchView = findViewById(R.id.am_search_view);
        this.mSearchView.setOnClickListener(this);
        updateViewState();
        this.mLayoutFastOpen.setOnClickListener(this.mOnClickListener);
        MiStatUtil.recordFastOpenShow();
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        SlidingButton findViewById;
        if (this.mCommonConfig.isFastOpenEnable() && (findViewById = view.findViewById(R.id.sliding_button)) != null) {
            findViewById.toggle();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        initData();
    }

    public void startSearchMode(SearchActionMode.Callback callback) {
        if (this.mSearchActionMode == null) {
            this.mFastOpenViewGroup.setVisibility(8);
            this.mSearchActionMode = startActionMode(callback);
        }
    }
}
