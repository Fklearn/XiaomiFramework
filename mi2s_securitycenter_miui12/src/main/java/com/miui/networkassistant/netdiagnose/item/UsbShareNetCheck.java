package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils;
import com.miui.securitycenter.R;
import java.net.InetAddress;

public class UsbShareNetCheck extends AbstractNetworkDiagoneItem {
    private static final String TAG = "NetworkDiagnostics_UsbShareNetCheck";
    private static final String USB_SHARE_NET_IP_ADDR = "10.0.2.2";
    private boolean mPcConnected = false;

    public UsbShareNetCheck(Context context) {
        super(context);
    }

    public void check() {
        try {
            this.mPcConnected = NetworkDiagnosticsUtils.isIpAvailable(InetAddress.getByName(USB_SHARE_NET_IP_ADDR), 1).booleanValue();
        } catch (Exception e) {
            Log.e(TAG, "check: an exception occurred.", e);
        }
        this.mIsStatusNormal = false;
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public String getItemName() {
        return this.mContext.getResources().getString(R.string.usbsharenet_exception_title);
    }

    public String getItemSolution() {
        return null;
    }

    public String getItemSummary() {
        int i;
        Resources resources;
        if (!this.mPcConnected) {
            resources = this.mContext.getResources();
            i = R.string.usbsharenet_exception_summary;
        } else {
            resources = this.mContext.getResources();
            i = R.string.usbsharenet_pc_exception_summary;
        }
        return resources.getString(i);
    }
}
