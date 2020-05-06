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
import com.miui.networkassistant.utils.IconCacheHelper;
import com.miui.networkassistant.utils.LabelLoadHelper;
import com.miui.securitycenter.R;
import java.util.ArrayList;

public class FirewallWlanListAdapter extends BaseFirewallAdapter<ViewHolder> {
    private ArrayList<AppInfo> mAppList;
    private FirewallRuleView.OnRuleChangedListener mRuleChangedListener;

    protected static class ViewHolder extends RecyclerView.u {
        TextView appName;
        ImageView icon;
        FirewallRuleView wifiButton;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.imageview_icon);
            this.appName = (TextView) view.findViewById(R.id.textview_appname);
            this.wifiButton = (FirewallRuleView) view.findViewById(R.id.wifi_button);
        }
    }

    private static class WlanFirewallComparator extends BaseFirewallAdapter.FirewallComparator {
        public WlanFirewallComparator(Context context, FirewallRuleCacher firewallRuleCacher) {
            super(context, firewallRuleCacher);
        }

        /* access modifiers changed from: protected */
        public int getFirewallRuleCompareWeight(FirewallRuleSet firewallRuleSet) {
            return firewallRuleSet.wifiRule == FirewallRule.Restrict ? 1 : 0;
        }
    }

    public FirewallWlanListAdapter(Activity activity, ArrayList<AppInfo> arrayList, IFirewallBinder iFirewallBinder, FirewallRuleView.OnRuleChangedListener onRuleChangedListener) {
        super(activity, iFirewallBinder);
        this.mAppList = arrayList;
        this.mRuleChangedListener = onRuleChangedListener;
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
        return new WlanFirewallComparator(this.mContext, this.mRuleCacher.copy());
    }

    public ArrayList<AppInfo> getData() {
        return this.mAppList;
    }

    /* access modifiers changed from: protected */
    public int getFirewallRuleCacherType() {
        return 1;
    }

    public int getItemCount() {
        ArrayList<AppInfo> arrayList = this.mAppList;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        ArrayList<AppInfo> arrayList = this.mAppList;
        if (arrayList != null && arrayList.size() > i) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    BaseFirewallAdapter.OnItemClickListener onItemClickListener = FirewallWlanListAdapter.this.mOnItemClickListener;
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(i);
                    }
                }
            });
            AppInfo appInfo = this.mAppList.get(i);
            IconCacheHelper.getInstance().setIconToImageView(viewHolder.icon, appInfo.packageName.toString());
            setLabelTextView(viewHolder.appName, LabelLoadHelper.loadLabel(this.mContext, appInfo.packageName).toString(), this.mSearchInput);
            viewHolder.wifiButton.setTag(appInfo);
            this.mRuleCacher.notifyRuleChanged();
            FirewallRule wifiRule = this.mRuleCacher.getWifiRule(appInfo.packageName.toString());
            viewHolder.wifiButton.setRule(wifiRule);
            viewHolder.wifiButton.setRuleChangedListener(this.mRuleChangedListener);
            viewHolder.wifiButton.setContentDescription(this.mContext.getString(wifiRule == FirewallRule.Allow ? R.string.firewall_allow_wlan : R.string.firewall_restrict_wlan));
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_wlan_firewall, viewGroup, false));
    }

    public void setData(ArrayList<AppInfo> arrayList) {
        this.mAppList = arrayList;
        notifyDataSetChanged();
    }
}
