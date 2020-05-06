package com.miui.networkassistant.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.model.FirewallRuleSet;
import com.miui.networkassistant.service.IFirewallBinder;
import com.miui.networkassistant.service.wrapper.FirewallRuleCacher;
import com.miui.networkassistant.ui.adapter.BaseFirewallAdapter;
import com.miui.networkassistant.ui.view.FirewallRuleView;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.IconCacheHelper;
import com.miui.networkassistant.utils.LabelLoadHelper;
import com.miui.securitycenter.R;
import java.util.ArrayList;

public class FirewallMobileListAdapter extends BaseFirewallAdapter<ViewHolder> {
    /* access modifiers changed from: private */
    public int mActiveSlotNum;
    private ArrayList<AppInfo> mAppList;
    protected boolean mIsDualCard;
    private boolean mIsSystem;
    private FirewallRuleView.OnRuleChangedListener mRuleChangedListener;

    private static class MobileFirewallComparator extends BaseFirewallAdapter.FirewallComparator {
        public MobileFirewallComparator(Context context, FirewallRuleCacher firewallRuleCacher) {
            super(context, firewallRuleCacher);
        }

        /* access modifiers changed from: protected */
        public int getFirewallRuleCompareWeight(FirewallRuleSet firewallRuleSet) {
            int i = 1;
            int i2 = firewallRuleSet.mobileRule == FirewallRule.Restrict ? 1 : 0;
            if (!DeviceUtil.IS_DUAL_CARD) {
                return i2;
            }
            if (firewallRuleSet.mobileRule2 != FirewallRule.Restrict) {
                i = 0;
            }
            return i2 + i;
        }
    }

    public class ViewHolder extends RecyclerView.u {
        TextView appName;
        ImageView arrow;
        ImageView icon;
        FirewallRuleView[] simButton = new FirewallRuleView[2];

        public ViewHolder(@NonNull View view) {
            super(view);
            FirewallRuleView firewallRuleView;
            this.icon = (ImageView) view.findViewById(R.id.imageview_icon);
            this.appName = (TextView) view.findViewById(R.id.textview_appname);
            this.arrow = (ImageView) view.findViewById(R.id.iv_arrow);
            this.simButton[0] = (FirewallRuleView) view.findViewById(R.id.sim1_button);
            this.simButton[1] = (FirewallRuleView) view.findViewById(R.id.sim2_button);
            if (!FirewallMobileListAdapter.this.mIsDualCard) {
                if (FirewallMobileListAdapter.this.mActiveSlotNum == 0) {
                    firewallRuleView = this.simButton[1];
                } else if (FirewallMobileListAdapter.this.mActiveSlotNum == 1) {
                    firewallRuleView = this.simButton[0];
                } else {
                    return;
                }
                firewallRuleView.setVisibility(8);
            }
        }
    }

    public FirewallMobileListAdapter(Activity activity, ArrayList<AppInfo> arrayList, IFirewallBinder iFirewallBinder, FirewallRuleView.OnRuleChangedListener onRuleChangedListener, boolean z) {
        super(activity, iFirewallBinder);
        this.mAppList = arrayList;
        this.mRuleChangedListener = onRuleChangedListener;
        this.mIsSystem = z;
    }

    private void bindViewBySlot(ViewHolder viewHolder, AppInfo appInfo, int i) {
        boolean z = false;
        if ("icon_system_app".equals(appInfo.packageName.toString())) {
            viewHolder.simButton[i].setVisibility(8);
            viewHolder.arrow.setVisibility(0);
            return;
        }
        viewHolder.simButton[i].setVisibility(0);
        viewHolder.arrow.setVisibility(8);
        viewHolder.simButton[i].setTag(appInfo);
        this.mRuleCacher.notifyRuleChanged();
        FirewallRule mobileRule = this.mRuleCacher.getMobileRule(appInfo.packageName.toString(), i);
        viewHolder.simButton[i].setRule(mobileRule);
        viewHolder.simButton[i].setRuleChangedListener(this.mRuleChangedListener);
        if (mobileRule == FirewallRule.Allow) {
            z = true;
        }
        viewHolder.simButton[i].setContentDescription(this.mContext.getString(z ? R.string.firewall_allow_data : R.string.firewall_restrict_data));
    }

    private void handleSystemApp(ArrayList<AppInfo> arrayList) {
        if (!this.mIsSystem) {
            AppInfo appInfo = new AppInfo("icon_system_app", -10, false);
            if (this.mIsInSearch && arrayList.contains(appInfo)) {
                arrayList.remove(appInfo);
            } else if (!this.mIsInSearch && !arrayList.contains(appInfo)) {
                arrayList.add(arrayList.size(), appInfo);
            }
        }
    }

    private void setLabelTextView(TextView textView, String str, String str2) {
        TextView textView2 = textView;
        String str3 = str;
        if (TextUtils.isEmpty(str2)) {
            textView.setText(str);
        } else if (str.toLowerCase().contains(str2.toLowerCase())) {
            int indexOf = str.toLowerCase().indexOf(str2.toLowerCase());
            String substring = str3.substring(indexOf, str2.length() + indexOf);
            boolean z = true;
            String format = String.format(this.mContext.getString(R.string.search_input_txt_na), new Object[]{substring});
            String[] strArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            int length = strArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    z = false;
                    break;
                } else if (format.contains(strArr[i])) {
                    textView2.setText(Html.fromHtml(str3.replace(substring, format)));
                    break;
                } else {
                    i++;
                }
            }
            if (!z) {
                textView2.setText(Html.fromHtml(str3.replaceFirst(substring, format)));
                return;
            }
            return;
        }
    }

    public BaseFirewallAdapter.FirewallComparator getComparator() {
        return new MobileFirewallComparator(this.mContext, this.mRuleCacher.copy());
    }

    public ArrayList<AppInfo> getData() {
        return this.mAppList;
    }

    /* access modifiers changed from: protected */
    public int getFirewallRuleCacherType() {
        return DeviceUtil.IS_DUAL_CARD ? 6 : 2;
    }

    public int getItemCount() {
        ArrayList<AppInfo> arrayList = this.mAppList;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        int i2;
        ArrayList<AppInfo> arrayList = this.mAppList;
        if (arrayList != null && arrayList.size() > i) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    BaseFirewallAdapter.OnItemClickListener onItemClickListener = FirewallMobileListAdapter.this.mOnItemClickListener;
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(i);
                    }
                }
            });
            AppInfo appInfo = this.mAppList.get(i);
            IconCacheHelper.getInstance().setIconToImageView(viewHolder.icon, appInfo.packageName.toString());
            setLabelTextView(viewHolder.appName, LabelLoadHelper.loadLabel(this.mContext, appInfo.packageName).toString(), this.mSearchInput);
            if (this.mIsDualCard) {
                bindViewBySlot(viewHolder, appInfo, 0);
                i2 = 1;
            } else {
                i2 = this.mActiveSlotNum;
            }
            bindViewBySlot(viewHolder, appInfo, i2);
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_firewall, viewGroup, false));
    }

    public void setData(ArrayList<AppInfo> arrayList) {
        this.mAppList = arrayList;
        handleSystemApp(arrayList);
        notifyDataSetChanged();
    }

    public void setDualCardData(boolean z, int i) {
        this.mIsDualCard = z;
        this.mActiveSlotNum = i;
    }
}
