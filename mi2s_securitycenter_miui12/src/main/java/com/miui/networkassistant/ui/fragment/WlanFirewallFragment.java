package com.miui.networkassistant.ui.fragment;

import android.content.res.Resources;
import android.os.RemoteException;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.ui.adapter.BaseFirewallAdapter;
import com.miui.networkassistant.ui.adapter.FirewallWlanListAdapter;
import com.miui.networkassistant.ui.dialog.OptionTipDialog;
import com.miui.networkassistant.ui.view.FirewallRuleView;
import com.miui.securitycenter.R;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class WlanFirewallFragment extends FirewallListFragment implements AppMonitorWrapper.AppMonitorListener, FirewallRuleView.OnRuleChangedListener {
    private static final String TAG = "com.miui.networkassistant.ui.fragment.WlanFirewallFragment";
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (view.getId() == R.id.layout_wifi_head) {
                WlanFirewallFragment.this.onWifiHeadClicked();
            }
        }
    };
    private BaseFirewallAdapter.OnItemClickListener mItemClickListener = new BaseFirewallAdapter.OnItemClickListener() {
        public void onItemClick(int i) {
            ShowAppDetailFragment.startAppDetailFragment(WlanFirewallFragment.this.mActivity, WlanFirewallFragment.this.mAdapter.getData().get(i).packageName.toString());
        }
    };
    private ImageView mSimHeadImageView;
    private View mSimHeadLayout;
    private int mSimRestrictedCount;
    private int mTotalCount;

    /* access modifiers changed from: private */
    public void onWifiHeadClicked() {
        if (getGroupChangeToRule(this.mTotalCount, this.mSimRestrictedCount) == FirewallRule.Allow) {
            toggleAllAppsWifiRule();
            return;
        }
        new OptionTipDialog(this.mActivity, new OptionTipDialog.OptionDialogListener() {
            public void onOptionUpdated(boolean z) {
                if (!z) {
                    WlanFirewallFragment.this.toggleAllAppsWifiRule();
                }
            }
        }).buildShowDialog(getString(R.string.dialog_restrict_all_wlan_title), getString(R.string.dialog_restrict_all_wlan_summary), getString(R.string.dialog_restrict_negative), getString(R.string.dialog_restrict_positive));
    }

    private void setRuleCount() {
        this.mSimRestrictedCount = 0;
        if (this.mAppList != null) {
            try {
                List<String> wifiRestrictPackages = this.mFirewallBinder.getWifiRestrictPackages();
                Iterator<AppInfo> it = this.mAppList.iterator();
                while (it.hasNext()) {
                    if (wifiRestrictPackages.contains(it.next().packageName.toString())) {
                        this.mSimRestrictedCount++;
                    }
                }
            } catch (RemoteException e) {
                Log.i(TAG, "setRuleCount", e);
            }
        }
    }

    private void setSearchListTitle(boolean z) {
        View findViewById = findViewById(R.id.layout_wifi_head);
        TextView textView = (TextView) findViewById(R.id.tv_wifi_search_head);
        if (z) {
            findViewById.setVisibility(8);
            textView.setVisibility(0);
            return;
        }
        findViewById.setVisibility(0);
        textView.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void toggleAllAppsWifiRule() {
        FirewallRule groupChangeToRule = getGroupChangeToRule(this.mTotalCount, this.mSimRestrictedCount);
        if (this.mAppList != null) {
            HashMap hashMap = new HashMap();
            Iterator<AppInfo> it = this.mAppList.iterator();
            while (it.hasNext()) {
                hashMap.put(it.next().packageName.toString(), groupChangeToRule);
            }
            try {
                this.mFirewallBinder.setWifiRuleForPackages(hashMap);
            } catch (RemoteException e) {
                Log.i(TAG, "onWifiHeadClicked", e);
            }
        }
        updateView();
        this.mAdapter.notifyDataSetChanged();
    }

    private void updateRuleHeadView(FirewallRule firewallRule) {
        this.mSimRestrictedCount = firewallRule == FirewallRule.Restrict ? this.mSimRestrictedCount + 1 : this.mSimRestrictedCount - 1;
        updateSimHeadView();
    }

    private void updateSimHeadView() {
        this.mSimHeadImageView.setImageResource(getGroupHeadImageSource(this.mTotalCount, this.mSimRestrictedCount));
        this.mSimHeadImageView.setContentDescription(getHeadViewDesp(this.mTotalCount, this.mSimRestrictedCount));
    }

    private void updateWifiRuleView(FirewallRuleView firewallRuleView, FirewallRule firewallRule) {
        firewallRuleView.setContentDescription(this.mAppContext.getString(firewallRule == FirewallRule.Allow ? R.string.firewall_allow_wlan : R.string.firewall_restrict_wlan));
        updateRuleHeadView(firewallRule);
    }

    /* access modifiers changed from: protected */
    public void initView() {
        setEmptyText((int) R.string.firewall_fragment_sys_listempty);
        this.mSimHeadLayout = findViewById(R.id.layout_wifi_head);
        this.mSimHeadLayout.setOnClickListener(this.mClickListener);
        this.mSimHeadImageView = (ImageView) findViewById(R.id.iv_wifi_head);
        this.mAdapter.setOnItemClickListener(this.mItemClickListener);
    }

    /* access modifiers changed from: protected */
    public BaseFirewallAdapter onCreateListAdapter() {
        return new FirewallWlanListAdapter(this.mActivity, this.mAppList, this.mFirewallBinder, this);
    }

    /* access modifiers changed from: protected */
    public View onCreateListTitleView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.listfragment_header_wlan_firewall, viewGroup, false);
    }

    /* access modifiers changed from: protected */
    public void onCreateSearchView(ActionMode actionMode, Menu menu) {
        setSearchListTitle(true);
    }

    /* access modifiers changed from: protected */
    public void onDestroySearchView(ActionMode actionMode) {
        setSearchListTitle(false);
    }

    /* access modifiers changed from: protected */
    public void onPostLoadDataTask() {
        this.mTotalCount = this.mAppList.size();
        TextView textView = this.mSearchInputView;
        Resources resources = this.mAppContext.getResources();
        int i = this.mTotalCount;
        textView.setHint(resources.getQuantityString(R.plurals.search_app_count_txt_na, i, new Object[]{Integer.valueOf(i)}));
    }

    public void onRuleChanged(FirewallRuleView firewallRuleView, FirewallRule firewallRule) {
        updateWifiRuleView(firewallRuleView, firewallRule);
    }

    public boolean onRuleChanging(FirewallRuleView firewallRuleView, FirewallRule firewallRule) {
        Object tag = firewallRuleView.getTag();
        if (!(tag instanceof AppInfo)) {
            return true;
        }
        try {
            return this.mFirewallBinder.setWifiRule(((AppInfo) tag).packageName.toString(), firewallRule == FirewallRule.Restrict ? FirewallRule.Allow : FirewallRule.Restrict);
        } catch (RemoteException e) {
            Log.i(TAG, "onRuleChanging:", e);
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void updateView() {
        setRuleCount();
        updateSimHeadView();
    }
}
