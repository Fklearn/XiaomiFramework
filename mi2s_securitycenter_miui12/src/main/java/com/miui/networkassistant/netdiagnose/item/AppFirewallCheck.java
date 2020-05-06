package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import b.b.c.h.f;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.ui.activity.FirewallActivity;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import miui.provider.ExtraNetwork;

public class AppFirewallCheck extends AbstractNetworkDiagoneItem {
    int restrictAppCount = 0;

    public AppFirewallCheck(Context context) {
        super(context);
    }

    private int getMobileRestrictAppCount(ArrayList<AppInfo> arrayList) {
        Iterator<AppInfo> it = arrayList.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (ExtraNetwork.isMobileRestrict(this.mContext, it.next().packageName.toString())) {
                i++;
            }
        }
        return i;
    }

    private int getWifiRestrictAppCount(ArrayList<AppInfo> arrayList) {
        Iterator<AppInfo> it = arrayList.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (ExtraNetwork.isWifiRestrict(this.mContext, it.next().packageName.toString())) {
                i++;
            }
        }
        return i;
    }

    private void trackNetworkDiagnosticsStep(int i, boolean z) {
        if (!networkChanged() && i > 0) {
            HashMap hashMap = new HashMap(1);
            hashMap.put(z ? "other" : "wifi", "restrict");
            AnalyticsHelper.trackNetworkDiagnosticsStep(hashMap);
        }
    }

    public void check() {
        ArrayList<AppInfo> filteredAppInfosList;
        this.restrictAppCount = 0;
        if (this.mDiagnosticsManager.getCurNetworkState() == NetworkDiagnosticsUtils.NetworkState.CONNECTED && !f.h(this.mContext) && (filteredAppInfosList = AppMonitorWrapper.getInstance(this.mContext).getFilteredAppInfosList()) != null) {
            if (f.i(this.mContext)) {
                this.restrictAppCount = getMobileRestrictAppCount(filteredAppInfosList);
                trackNetworkDiagnosticsStep(this.restrictAppCount, true);
            } else if (f.l(this.mContext)) {
                this.restrictAppCount = getWifiRestrictAppCount(filteredAppInfosList);
                trackNetworkDiagnosticsStep(this.restrictAppCount, false);
            }
            if (this.restrictAppCount > 0) {
                this.mIsStatusNormal = false;
            }
        }
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        Intent intent = new Intent(this.mContext, FirewallActivity.class);
        Bundle bundle = new Bundle();
        if (f.l(this.mContext)) {
            bundle.putInt("VisibleItemIndex", 1);
            intent.putExtras(bundle);
        }
        intent.addFlags(276824064);
        this.mContext.startActivity(intent);
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public String getItemName() {
        return this.mContext.getResources().getString(R.string.permission_exception_title);
    }

    public String getItemSolution() {
        return this.mContext.getResources().getString(R.string.see_detail);
    }

    public String getItemSummary() {
        if (this.restrictAppCount <= 0) {
            return "";
        }
        Resources resources = this.mContext.getResources();
        int i = this.restrictAppCount;
        return resources.getQuantityString(R.plurals.network_restrict_count, i, new Object[]{Integer.valueOf(i)});
    }
}
