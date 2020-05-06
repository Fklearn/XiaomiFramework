package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.LinkProperties;
import android.util.Log;
import b.b.c.c;
import b.b.c.h.f;
import b.b.o.g.e;
import com.miui.activityutil.h;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.securitycenter.R;
import java.net.InetAddress;
import java.util.HashMap;

public class GatewayCheck extends AbstractNetworkDiagoneItem {
    private static final String TAG = "NetworkDiagnostics_GatewayCheck";
    InetAddress mGateWayIpAddress;
    InetAddress mLocalIpAddress;
    boolean mPingGatewayRet = false;
    boolean mPingLocalRet = false;

    public GatewayCheck(Context context) {
        super(context);
    }

    public void check() {
        LinkProperties linkProperties = null;
        this.mLocalIpAddress = null;
        this.mGateWayIpAddress = null;
        try {
            linkProperties = (LinkProperties) e.a((Object) this.mCm, "getActiveLinkProperties", (Class<?>[]) null, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mLocalIpAddress = NetworkDiagnosticsUtils.getCurrentNetworkIp(linkProperties != null && (f.a(linkProperties) || f.b(linkProperties)) ? c.f1620c : c.f1619b);
        this.mGateWayIpAddress = NetworkDiagnosticsUtils.getGateway(this.mContext);
        InetAddress inetAddress = this.mLocalIpAddress;
        if (inetAddress != null) {
            this.mPingLocalRet = NetworkDiagnosticsUtils.isIpAvailable(inetAddress, 1).booleanValue();
        }
        InetAddress inetAddress2 = this.mGateWayIpAddress;
        if (inetAddress2 != null) {
            this.mPingGatewayRet = NetworkDiagnosticsUtils.isIpAvailable(inetAddress2, 2).booleanValue();
        }
        Log.e(TAG, "check() ping " + this.mLocalIpAddress + " ret=" + this.mPingLocalRet);
        Log.e(TAG, "check() ping " + this.mGateWayIpAddress + " ret=" + this.mPingGatewayRet);
        NetworkDiagnosticsUtils.doExec("netcfg");
        this.mIsStatusNormal = false;
        if (!networkChanged()) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("wifi", !this.mPingGatewayRet ? "gateway" : h.f2289a);
            AnalyticsHelper.trackNetworkDiagnosticsStep(hashMap);
        }
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        if (this.mPingGatewayRet) {
            Intent intent = new Intent(this.mContext, NetworkDiagnosticsTipActivity.class);
            intent.setFlags(276824064);
            intent.putExtra(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, getItemName());
            intent.putExtra(NetworkDiagnosticsTipActivity.DETAIL_KEY_NAME, R.string.network_route_exception_detail);
            this.mContext.startActivity(intent);
        } else {
            this.mDiagnosticsManager.reopenWifi();
        }
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public String getFixingWaitProgressDlgMsg() {
        Resources resources;
        int i;
        if (this.mPingGatewayRet) {
            resources = this.mContext.getResources();
            i = R.string.usage_sorted_loading_text;
        } else {
            resources = this.mContext.getResources();
            i = R.string.repair_loading_text;
        }
        return resources.getString(i);
    }

    public String getItemName() {
        return this.mContext.getResources().getString(R.string.gateway_exception_title);
    }

    public String getItemSolution() {
        Resources resources;
        int i;
        if (this.mPingGatewayRet) {
            resources = this.mContext.getResources();
            i = R.string.see_detail;
        } else {
            resources = this.mContext.getResources();
            i = R.string.repair;
        }
        return resources.getString(i);
    }

    public String getItemSummary() {
        Resources resources;
        int i;
        if (this.mPingGatewayRet) {
            resources = this.mContext.getResources();
            i = R.string.gateway_exception_summary;
        } else {
            resources = this.mContext.getResources();
            i = R.string.gateway_exception_summary2;
        }
        return resources.getString(i);
    }
}
