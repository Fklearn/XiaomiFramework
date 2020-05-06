package com.miui.networkassistant.ui.fragment;

import android.app.Activity;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import b.b.c.c.b.g;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.ui.adapter.BaseFirewallAdapter;
import com.miui.networkassistant.ui.adapter.FirewallMobileListAdapter;
import com.miui.networkassistant.ui.dialog.OptionTipDialog;
import com.miui.networkassistant.ui.view.FirewallRuleView;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SystemAppFirewallFragment extends FirewallListFragment implements FirewallRuleView.OnRuleChangedListener {
    private static final String TAG = "SystemAppFirewallFragment";
    private static final int TITLE_FILED = 2131756235;
    private int mActiveSlotNum;
    private boolean mIsDuCard;
    private BaseFirewallAdapter.OnItemClickListener mItemClickListener = new BaseFirewallAdapter.OnItemClickListener() {
        public void onItemClick(int i) {
            ShowAppDetailFragment.startAppDetailFragment(SystemAppFirewallFragment.this.mActivity, SystemAppFirewallFragment.this.mAdapter.getData().get(i).packageName.toString());
        }
    };
    private TextView[] mSimTitleTextView = new TextView[2];

    private static class RoamingOptionDialogListener implements OptionTipDialog.OptionDialogListener {
        private WeakReference<Activity> mActivityRef;

        public RoamingOptionDialogListener(Activity activity) {
            this.mActivityRef = new WeakReference<>(activity);
        }

        public void onOptionUpdated(boolean z) {
            Activity activity = (Activity) this.mActivityRef.get();
            if (z && activity != null) {
                g.startWithFragment(activity, RoamingWhiteListFragment.class);
            }
        }
    }

    private void buildRestrictAndroidTipDialog(final String str, final int i) {
        new OptionTipDialog(this.mActivity, new OptionTipDialog.OptionDialogListener() {
            public void onOptionUpdated(boolean z) {
                if (z) {
                    SystemAppFirewallFragment.this.updateAppFireRule(str, FirewallRule.Restrict, i);
                }
            }
        }).buildShowDialog(this.mAppContext.getString(R.string.firewall_restrict_android_dialog_title), this.mAppContext.getString(R.string.firewall_restrict_android_dialog_content));
    }

    private int getCurrentOptSlot(int i) {
        switch (i) {
            case R.id.sim1_button /*2131297680*/:
                return 0;
            case R.id.sim2_button /*2131297681*/:
                return 1;
            default:
                return this.mActiveSlotNum;
        }
    }

    private boolean getRoamingNetworkState() {
        try {
            return TelephonyUtil.isNetworkRoaming(this.mAppContext, this.mActiveSlotNum) && TelephonyUtil.getDataRoamingEnabled(this.mAppContext) && this.mFirewallBinder.getRoamingWhiteListEnable();
        } catch (RemoteException e) {
            Log.i(TAG, "isRoamingEnable", e);
            return false;
        }
    }

    private void initViewDelay() {
        this.mSimTitleTextView[0] = (TextView) findViewById(R.id.tv_sim1_search_head);
        this.mSimTitleTextView[1] = (TextView) findViewById(R.id.tv_sim2_search_head);
        updateListTitleGroup();
        setEmptyText((int) R.string.firewall_fragment_nonesys_listempty);
        this.mAdapter.setOnItemClickListener(this.mItemClickListener);
    }

    private boolean isRestrictAndroidSystemApp(String str, int i) {
        FirewallRule firewallRule;
        try {
            firewallRule = this.mFirewallBinder.getMobileRule(str, i);
        } catch (RemoteException e) {
            Log.i(TAG, "isRestrictAndroidSystemApp", e);
            firewallRule = null;
        }
        if (TextUtils.equals(str, Constants.System.ANDROID_PACKAGE_NAME)) {
            return firewallRule == null || firewallRule == FirewallRule.Allow;
        }
        return false;
    }

    private void setDualCardData() {
        SimCardHelper instance = SimCardHelper.getInstance(this.mAppContext);
        this.mIsDuCard = instance.isDualSimInserted();
        this.mActiveSlotNum = instance.getCurrentMobileSlotNum();
    }

    private void showRoamingTipDialog() {
        String string = this.mAppContext.getString(R.string.dialog_roaming_title);
        String string2 = this.mAppContext.getString(R.string.dialog_roaming_message);
        String string3 = this.mAppContext.getString(17039369);
        String string4 = this.mAppContext.getString(R.string.add_to_whitelist_button);
        Activity activity = this.mActivity;
        new OptionTipDialog(activity, new RoamingOptionDialogListener(activity)).buildShowDialog(string, string2, string3, string4);
    }

    /* access modifiers changed from: private */
    public void updateAppFireRule(String str, FirewallRule firewallRule, int i) {
        try {
            this.mFirewallBinder.setMobileRule(str, firewallRule, i);
        } catch (RemoteException e) {
            Log.i(TAG, "setMobileRule", e);
        }
    }

    private void updateListTitleGroup() {
        TextView textView;
        TextView textView2;
        if (!this.mIsDuCard) {
            if (this.mActiveSlotNum == 0) {
                textView2 = (TextView) findViewById(R.id.tv_sim1_search_head);
                textView = this.mSimTitleTextView[1];
            } else {
                textView2 = (TextView) findViewById(R.id.tv_sim2_search_head);
                textView = this.mSimTitleTextView[0];
            }
            textView.setVisibility(8);
            textView2.setText(R.string.firewall_mobile);
            findViewById(R.id.view_split_line).setVisibility(8);
        }
    }

    private void updateSearchInputView(int i) {
        this.mSearchInputView.setHint(this.mAppContext.getResources().getQuantityString(R.plurals.search_app_count_txt_na, i, new Object[]{Integer.valueOf(i)}));
    }

    /* access modifiers changed from: protected */
    public ArrayList<AppInfo> getAppList() {
        return this.mAppMonitorWrapper.getSystemAppList();
    }

    /* access modifiers changed from: protected */
    public void initView() {
        setDualCardData();
        initViewDelay();
    }

    /* access modifiers changed from: protected */
    public BaseFirewallAdapter onCreateListAdapter() {
        return new FirewallMobileListAdapter(this.mActivity, this.mAppList, this.mFirewallBinder, this, true);
    }

    /* access modifiers changed from: protected */
    public View onCreateListTitleView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.listfragment_header_system_firewall, viewGroup, false);
    }

    /* access modifiers changed from: protected */
    public void onPostLoadDataTask() {
        this.mAdapter.setDualCardData(this.mIsDuCard, this.mActiveSlotNum);
        updateSearchInputView(this.mAppList.size());
    }

    public void onRuleChanged(FirewallRuleView firewallRuleView, FirewallRule firewallRule) {
        Object tag = firewallRuleView.getTag();
        if (tag != null && (tag instanceof AppInfo)) {
            updateAppFireRule(((AppInfo) tag).packageName.toString(), firewallRule, getCurrentOptSlot(firewallRuleView.getId()));
        }
    }

    public boolean onRuleChanging(FirewallRuleView firewallRuleView, FirewallRule firewallRule) {
        Object tag = firewallRuleView.getTag();
        if (tag == null || !(tag instanceof AppInfo)) {
            return true;
        }
        String charSequence = ((AppInfo) tag).packageName.toString();
        int currentOptSlot = getCurrentOptSlot(firewallRuleView.getId());
        if (isRestrictAndroidSystemApp(charSequence, currentOptSlot)) {
            buildRestrictAndroidTipDialog(charSequence, currentOptSlot);
            return false;
        } else if (!getRoamingNetworkState()) {
            return true;
        } else {
            if (currentOptSlot == this.mActiveSlotNum) {
                showRoamingTipDialog();
            }
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.firewall_system_title;
    }
}
