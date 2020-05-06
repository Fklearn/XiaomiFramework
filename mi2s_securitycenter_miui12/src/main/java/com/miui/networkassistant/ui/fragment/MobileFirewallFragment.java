package com.miui.networkassistant.ui.fragment;

import android.app.Activity;
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
import b.b.c.c.b.g;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.ui.adapter.BaseFirewallAdapter;
import com.miui.networkassistant.ui.adapter.FirewallMobileListAdapter;
import com.miui.networkassistant.ui.dialog.OptionTipDialog;
import com.miui.networkassistant.ui.view.FirewallRuleView;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.networkassistant.utils.TextPrepareUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MobileFirewallFragment extends FirewallListFragment implements FirewallRuleView.OnRuleChangedListener, View.OnClickListener {
    private static final String TAG = "MobileFirewallFragment";
    private int mActiveSlotNum;
    private boolean mIsDuCard;
    private ImageView[] mSimHeadImageView = new ImageView[2];
    private View[] mSimHeadLayout = new View[2];
    private int[] mSimRestrictedCount = new int[2];
    private TextView[] mSimTitleTextView = new TextView[2];
    private int mTotalCount;
    private BaseFirewallAdapter.OnItemClickListener onItemClickListener = new BaseFirewallAdapter.OnItemClickListener() {
        public void onItemClick(int i) {
            String charSequence = MobileFirewallFragment.this.mAdapter.getData().get(i).packageName.toString();
            if ("icon_system_app".equals(charSequence)) {
                g.startWithFragment(MobileFirewallFragment.this.mActivity, SystemAppFirewallFragment.class);
            } else {
                ShowAppDetailFragment.startAppDetailFragment(MobileFirewallFragment.this.mActivity, charSequence);
            }
        }
    };

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
        this.mSimHeadLayout[0] = findViewById(R.id.layout_sim1_head);
        this.mSimHeadLayout[1] = findViewById(R.id.layout_sim2_head);
        this.mSimHeadImageView[0] = (ImageView) findViewById(R.id.iv_sim1_head);
        this.mSimHeadImageView[1] = (ImageView) findViewById(R.id.iv_sim2_head);
        this.mSimTitleTextView[0] = (TextView) findViewById(R.id.tv_sim1_search_head);
        this.mSimTitleTextView[1] = (TextView) findViewById(R.id.tv_sim2_search_head);
        updateListTitleGroup(false);
        setEmptyText((int) R.string.firewall_fragment_nonesys_listempty);
        this.mAdapter.setOnItemClickListener(this.onItemClickListener);
    }

    private void onSimHeadClick(int i) {
        if (!getRoamingNetworkState()) {
            onSimHeadClicked(i);
        }
    }

    private void onSimHeadClicked(final int i) {
        if (getGroupChangeToRule(this.mTotalCount, this.mSimRestrictedCount[i]) == FirewallRule.Allow) {
            toggleAllAppsMobileRule(i);
            return;
        }
        String dualCardSuffix = this.mIsDuCard ? TextPrepareUtil.getDualCardSuffix(this.mActivity, i) : "";
        new OptionTipDialog(this.mActivity, new OptionTipDialog.OptionDialogListener() {
            public void onOptionUpdated(boolean z) {
                if (!z) {
                    MobileFirewallFragment.this.toggleAllAppsMobileRule(i);
                }
            }
        }).buildShowDialog(getString(R.string.dialog_restrict_all_mobile_title, new Object[]{dualCardSuffix}), getString(R.string.dialog_restrict_all_mobile_summary, new Object[]{dualCardSuffix}), getString(R.string.dialog_restrict_negative), getString(R.string.dialog_restrict_positive));
    }

    private void setDualCardSlot() {
        SimCardHelper instance = SimCardHelper.getInstance(this.mAppContext);
        this.mIsDuCard = instance.isDualSimInserted();
        this.mActiveSlotNum = instance.getCurrentMobileSlotNum();
    }

    private void setRuleCount() {
        int i;
        if (this.mIsDuCard) {
            setRuleCount(0);
            i = 1;
        } else {
            i = this.mActiveSlotNum;
        }
        setRuleCount(i);
    }

    private void setRuleCount(int i) {
        this.mSimRestrictedCount[i] = 0;
        if (this.mAppList != null) {
            try {
                List<String> mobileRestrictPackages = this.mFirewallBinder.getMobileRestrictPackages(i);
                Iterator<AppInfo> it = this.mAppList.iterator();
                while (it.hasNext()) {
                    if (mobileRestrictPackages.contains(it.next().packageName.toString())) {
                        int[] iArr = this.mSimRestrictedCount;
                        iArr[i] = iArr[i] + 1;
                    }
                }
            } catch (RemoteException e) {
                Log.i(TAG, "getMobileRule", e);
            }
        }
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
    public void toggleAllAppsMobileRule(int i) {
        FirewallRule groupChangeToRule = getGroupChangeToRule(this.mTotalCount, this.mSimRestrictedCount[i]);
        if (this.mAppList != null) {
            HashMap hashMap = new HashMap();
            Iterator<AppInfo> it = this.mAppList.iterator();
            while (it.hasNext()) {
                String charSequence = it.next().packageName.toString();
                if (!"icon_system_app".equals(charSequence)) {
                    hashMap.put(charSequence, groupChangeToRule);
                }
            }
            try {
                this.mFirewallBinder.setMobileRuleForPackages(hashMap, i);
            } catch (RemoteException e) {
                Log.i(TAG, "onMobileHeadClicked", e);
            }
        }
        setRuleCount(i);
        updateSimHeadView(i);
        this.mAdapter.notifyDataSetChanged();
    }

    private void updateListTitleGroup(boolean z) {
        TextView textView;
        int i;
        TextView textView2;
        if (z) {
            this.mSimHeadLayout[0].setVisibility(8);
            this.mSimHeadLayout[1].setVisibility(8);
            this.mSimTitleTextView[0].setVisibility(0);
            this.mSimTitleTextView[1].setVisibility(0);
            if (!this.mIsDuCard) {
                if (this.mActiveSlotNum == 0) {
                    textView = (TextView) findViewById(R.id.tv_sim1_search_head);
                    textView2 = this.mSimTitleTextView[1];
                } else {
                    textView = (TextView) findViewById(R.id.tv_sim2_search_head);
                    textView2 = this.mSimTitleTextView[0];
                }
                textView2.setVisibility(8);
            } else {
                return;
            }
        } else {
            this.mSimHeadLayout[0].setVisibility(0);
            this.mSimHeadLayout[1].setVisibility(0);
            this.mSimTitleTextView[0].setVisibility(8);
            this.mSimTitleTextView[1].setVisibility(8);
            if (this.mIsDuCard) {
                this.mSimHeadLayout[0].setOnClickListener(this);
                this.mSimHeadLayout[1].setOnClickListener(this);
                return;
            }
            if (this.mActiveSlotNum == 0) {
                this.mSimHeadLayout[1].setVisibility(8);
                this.mSimHeadLayout[0].setOnClickListener(this);
                i = R.id.tv_sim1_head;
            } else {
                this.mSimHeadLayout[0].setVisibility(8);
                this.mSimHeadLayout[1].setOnClickListener(this);
                i = R.id.tv_sim2_head;
            }
            textView = (TextView) findViewById(i);
        }
        textView.setText(R.string.firewall_mobile);
        findViewById(R.id.view_split_line).setVisibility(8);
    }

    private void updateRuleHeadView(FirewallRule firewallRule, int i) {
        if (firewallRule == FirewallRule.Restrict) {
            int[] iArr = this.mSimRestrictedCount;
            iArr[i] = iArr[i] + 1;
        } else {
            int[] iArr2 = this.mSimRestrictedCount;
            iArr2[i] = iArr2[i] - 1;
        }
        updateSimHeadView(i);
    }

    private void updateSearchInputView() {
        TextView textView = this.mSearchInputView;
        Resources resources = this.mAppContext.getResources();
        int i = this.mTotalCount;
        textView.setHint(resources.getQuantityString(R.plurals.search_app_count_txt_na, i, new Object[]{Integer.valueOf(i)}));
    }

    private void updateSimHeadView(int i) {
        this.mSimHeadImageView[i].setImageResource(getGroupHeadImageSource(this.mTotalCount, this.mSimRestrictedCount[i]));
        this.mSimHeadImageView[i].setContentDescription(getHeadViewDesp(this.mTotalCount, this.mSimRestrictedCount[i]));
    }

    /* access modifiers changed from: protected */
    public void initView() {
        setDualCardSlot();
        initViewDelay();
    }

    public void onClick(View view) {
        int i;
        switch (view.getId()) {
            case R.id.layout_sim1_head /*2131297200*/:
                i = 0;
                break;
            case R.id.layout_sim2_head /*2131297201*/:
                i = 1;
                break;
            default:
                return;
        }
        onSimHeadClick(i);
    }

    /* access modifiers changed from: protected */
    public BaseFirewallAdapter onCreateListAdapter() {
        return new FirewallMobileListAdapter(this.mActivity, this.mAppList, this.mFirewallBinder, this, false);
    }

    /* access modifiers changed from: protected */
    public View onCreateListTitleView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.listfragment_header_mobile_firewall, viewGroup, false);
    }

    /* access modifiers changed from: protected */
    public void onCreateSearchView(ActionMode actionMode, Menu menu) {
        updateListTitleGroup(true);
    }

    /* access modifiers changed from: protected */
    public void onDestroySearchView(ActionMode actionMode) {
        updateListTitleGroup(false);
    }

    /* access modifiers changed from: protected */
    public void onFirewallServiceConnected() {
        if (getRoamingNetworkState()) {
            showRoamingTipDialog();
        }
    }

    /* access modifiers changed from: protected */
    public void onPostLoadDataTask() {
        this.mTotalCount = this.mAppList.size();
        this.mAdapter.setDualCardData(this.mIsDuCard, this.mActiveSlotNum);
    }

    public void onRuleChanged(FirewallRuleView firewallRuleView, FirewallRule firewallRule) {
        if (firewallRuleView.getTag() instanceof AppInfo) {
            updateRuleHeadView(firewallRule, getCurrentOptSlot(firewallRuleView.getId()));
        }
    }

    public boolean onRuleChanging(FirewallRuleView firewallRuleView, FirewallRule firewallRule) {
        Object tag = firewallRuleView.getTag();
        int currentOptSlot = getCurrentOptSlot(firewallRuleView.getId());
        if (!(tag instanceof AppInfo)) {
            return true;
        }
        String charSequence = ((AppInfo) tag).packageName.toString();
        if (!getRoamingNetworkState()) {
            try {
                return this.mFirewallBinder.setMobileRule(charSequence, firewallRule == FirewallRule.Restrict ? FirewallRule.Allow : FirewallRule.Restrict, currentOptSlot);
            } catch (RemoteException e) {
                Log.i(TAG, "onRuleChanging:", e);
                return true;
            }
        } else if (currentOptSlot != this.mActiveSlotNum) {
            return false;
        } else {
            showRoamingTipDialog();
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void updateView() {
        int i;
        setRuleCount();
        updateSearchInputView();
        if (this.mIsDuCard) {
            updateSimHeadView(0);
            i = 1;
        } else {
            i = this.mActiveSlotNum;
        }
        updateSimHeadView(i);
    }
}
