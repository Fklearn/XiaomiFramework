package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import android.content.Intent;
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
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.securitycenter.R;
import java.net.InetAddress;
import java.util.HashMap;

public class NeedCharge extends AbstractNetworkDiagoneItem {
    private static final String TAG = "NetworkDiagnostics_NeedCharge";
    InetAddress mIpAddress;
    boolean mPingRet = false;

    public NeedCharge(Context context) {
        super(context);
    }

    public void check() {
        LinkProperties linkProperties = null;
        this.mIpAddress = null;
        boolean z = false;
        this.mIsStatusNormal = false;
        NetworkDiagnosticsUtils.doExec("netcfg");
        if (!f.i(this.mContext)) {
            Log.i(TAG, "check() isMobileConnected = false ");
            return;
        }
        try {
            linkProperties = (LinkProperties) e.a((Object) this.mCm, "getActiveLinkProperties", (Class<?>[]) null, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (linkProperties != null && (f.a(linkProperties) || f.b(linkProperties))) {
            z = true;
        }
        this.mIpAddress = NetworkDiagnosticsUtils.getCurrentNetworkIp(z ? c.f1620c : c.f1619b);
        InetAddress inetAddress = this.mIpAddress;
        if (inetAddress != null) {
            this.mPingRet = NetworkDiagnosticsUtils.isIpAvailable(inetAddress, 1).booleanValue();
            Log.i(TAG, "check() ping " + this.mIpAddress + " ret=" + this.mPingRet);
        }
        if (!networkChanged()) {
            HashMap hashMap = new HashMap(1);
            hashMap.put(f.l(this.mContext) ? "wifi" : "other", h.f2289a);
            AnalyticsHelper.trackNetworkDiagnosticsStep(hashMap);
        }
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        Intent intent = new Intent(this.mContext, NetworkDiagnosticsTipActivity.class);
        intent.setFlags(276824064);
        intent.putExtra(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, getItemName());
        intent.putExtra(NetworkDiagnosticsTipActivity.DETAIL_KEY_NAME, R.string.network_mobile_exception_detail);
        this.mContext.startActivity(intent);
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public String getItemName() {
        return this.mContext.getResources().getString(R.string.other_exception_title);
    }

    public String getItemSolution() {
        return this.mContext.getResources().getString(R.string.see_detail);
    }

    public String getItemSummary() {
        if (DeviceUtil.isSmartDiagnostics(this.mContext)) {
            return null;
        }
        return this.mContext.getResources().getString(R.string.other_exception_summary);
    }
}
