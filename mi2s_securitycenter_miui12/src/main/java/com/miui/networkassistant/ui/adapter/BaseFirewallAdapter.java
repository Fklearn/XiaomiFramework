package com.miui.networkassistant.ui.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.u;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.FirewallRuleSet;
import com.miui.networkassistant.service.IFirewallBinder;
import com.miui.networkassistant.service.wrapper.FirewallRuleCacher;
import com.miui.networkassistant.utils.LabelLoadHelper;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public abstract class BaseFirewallAdapter<VH extends RecyclerView.u> extends RecyclerView.a<VH> {
    protected Context mContext;
    protected boolean mIsInSearch;
    protected OnItemClickListener mOnItemClickListener;
    protected FirewallRuleCacher mRuleCacher;
    protected String mSearchInput;

    protected static abstract class FirewallComparator implements Comparator<AppInfo> {
        private Collator mCollator = Collator.getInstance(Locale.CHINESE);
        private Context mContext;
        private FirewallRuleCacher mRuleCacher;

        public FirewallComparator(Context context, FirewallRuleCacher firewallRuleCacher) {
            this.mContext = context.getApplicationContext();
            this.mRuleCacher = firewallRuleCacher;
        }

        public int compare(AppInfo appInfo, AppInfo appInfo2) {
            FirewallRuleSet rule = this.mRuleCacher.getRule(appInfo.packageName.toString());
            FirewallRuleSet rule2 = this.mRuleCacher.getRule(appInfo2.packageName.toString());
            int firewallRuleCompareWeight = getFirewallRuleCompareWeight(rule);
            int firewallRuleCompareWeight2 = getFirewallRuleCompareWeight(rule2);
            if (firewallRuleCompareWeight != firewallRuleCompareWeight2) {
                return firewallRuleCompareWeight2 - firewallRuleCompareWeight;
            }
            int compare = this.mCollator.compare(LabelLoadHelper.loadLabel(this.mContext, appInfo.packageName), LabelLoadHelper.loadLabel(this.mContext, appInfo2.packageName));
            if (compare != 0) {
                return compare;
            }
            int i = (((long) (appInfo.uid - appInfo2.uid)) > 0 ? 1 : (((long) (appInfo.uid - appInfo2.uid)) == 0 ? 0 : -1));
            if (i > 0) {
                return 1;
            }
            return i == 0 ? 0 : -1;
        }

        /* access modifiers changed from: protected */
        public abstract int getFirewallRuleCompareWeight(FirewallRuleSet firewallRuleSet);
    }

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    public BaseFirewallAdapter(Activity activity, IFirewallBinder iFirewallBinder) {
        this.mContext = activity;
        this.mRuleCacher = new FirewallRuleCacher(iFirewallBinder, getFirewallRuleCacherType());
    }

    public abstract Comparator<AppInfo> getComparator();

    public abstract ArrayList<AppInfo> getData();

    /* access modifiers changed from: protected */
    public abstract int getFirewallRuleCacherType();

    public abstract void setData(ArrayList<AppInfo> arrayList);

    public void setDualCardData(boolean z, int i) {
    }

    public void setFirewallBinder(IFirewallBinder iFirewallBinder) {
        this.mRuleCacher.setFirewallBinder(iFirewallBinder);
    }

    public void setInSearch(boolean z) {
        this.mIsInSearch = z;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setSearchInput(String str) {
        this.mSearchInput = str;
    }
}
