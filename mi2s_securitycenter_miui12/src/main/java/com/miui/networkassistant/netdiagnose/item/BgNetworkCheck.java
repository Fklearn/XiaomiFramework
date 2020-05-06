package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import b.b.c.h.f;
import b.b.c.j.B;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.firewall.BackgroundPolicyService;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.traffic.statistic.PreSetGroup;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;

public class BgNetworkCheck extends AbstractNetworkDiagoneItem {
    private static final String TAG = "NetworkDiagnostics";
    private BackgroundPolicyService mBgPolicyService = BackgroundPolicyService.getInstance(this.mContext);
    private int restrictCount = 0;

    public BgNetworkCheck(Context context) {
        super(context);
    }

    public void check() {
        ArrayList<AppInfo> filteredAppInfosList;
        this.restrictCount = 0;
        this.mIsStatusNormal = true;
        if (this.mDiagnosticsManager.getCurNetworkState() == NetworkDiagnosticsUtils.NetworkState.CONNECTED && !f.h(this.mContext) && (filteredAppInfosList = AppMonitorWrapper.getInstance(this.mContext).getFilteredAppInfosList()) != null) {
            Iterator<AppInfo> it = filteredAppInfosList.iterator();
            while (it.hasNext()) {
                AppInfo next = it.next();
                String charSequence = next.packageName.toString();
                if (B.a(next.uid) >= 10000 && !PreSetGroup.isPrePolicyPackage(charSequence) && this.mBgPolicyService.isAppRestrictBackground(charSequence, next.uid)) {
                    this.restrictCount++;
                }
            }
            this.mIsStatusNormal = false;
            Log.d(TAG, "restrictCount: " + this.restrictCount);
        }
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        Intent intent = new Intent(Constants.App.ACTION_NETWORK_ASSISTANT_BG_NETWORK);
        intent.addFlags(276824064);
        this.mContext.startActivity(intent);
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public String getItemName() {
        return this.mContext.getResources().getString(R.string.bg_network_exception_title);
    }

    public String getItemSolution() {
        return this.mContext.getResources().getString(R.string.see_detail);
    }

    public String getItemSummary() {
        if (this.restrictCount <= 0) {
            return "";
        }
        Resources resources = this.mContext.getResources();
        int i = this.restrictCount;
        return resources.getQuantityString(R.plurals.network_restrict_count, i, new Object[]{Integer.valueOf(i)});
    }
}
