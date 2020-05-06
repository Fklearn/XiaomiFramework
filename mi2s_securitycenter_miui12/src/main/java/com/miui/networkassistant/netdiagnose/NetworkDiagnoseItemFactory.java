package com.miui.networkassistant.netdiagnose;

import android.content.Context;
import android.text.TextUtils;
import b.b.c.h.f;
import b.b.c.j.B;
import com.miui.networkassistant.netdiagnose.item.ApnCheck;
import com.miui.networkassistant.netdiagnose.item.AppFirewallCheck;
import com.miui.networkassistant.netdiagnose.item.BgNetworkCheck;
import com.miui.networkassistant.netdiagnose.item.DNSCheck;
import com.miui.networkassistant.netdiagnose.item.GatewayCheck;
import com.miui.networkassistant.netdiagnose.item.NeedCharge;
import com.miui.networkassistant.netdiagnose.item.NetworkChangedCheck;
import com.miui.networkassistant.netdiagnose.item.NetworkConnectedCheck;
import com.miui.networkassistant.netdiagnose.item.ProtocolCheck;
import com.miui.networkassistant.netdiagnose.item.SignalStrengthCheck;
import com.miui.networkassistant.netdiagnose.item.UsbShareNetCheck;
import com.miui.networkassistant.netdiagnose.item.WifiConnectedCheck;
import com.miui.networkassistant.utils.DeviceUtil;
import java.util.ArrayList;
import java.util.List;

public class NetworkDiagnoseItemFactory {
    private static NetworkDiagnoseItemFactory sInstance;
    private AbstractNetworkDiagoneItem mApnCheck = null;
    private AbstractNetworkDiagoneItem mBgNetworkCheck = null;
    private Context mContext;
    private AbstractNetworkDiagoneItem mDnsCheckItem = null;
    private AbstractNetworkDiagoneItem mGatewayCheck = null;
    private AbstractNetworkDiagoneItem mNeedCharge = null;
    private AbstractNetworkDiagoneItem mNetworkChangedCheck = null;
    private AbstractNetworkDiagoneItem mNetworkConnectedCheck = null;
    private AbstractNetworkDiagoneItem mNetworkPermissionCheck = null;
    private AbstractNetworkDiagoneItem mProtocolCheck = null;
    private AbstractNetworkDiagoneItem mSignalStrengthCheck = null;
    private AbstractNetworkDiagoneItem mUsbShareNetCheck = null;
    private AbstractNetworkDiagoneItem mWifiConnectedCheck = null;

    private NetworkDiagnoseItemFactory(Context context) {
        this.mContext = context;
    }

    public static synchronized NetworkDiagnoseItemFactory getInstance(Context context) {
        NetworkDiagnoseItemFactory networkDiagnoseItemFactory;
        synchronized (NetworkDiagnoseItemFactory.class) {
            if (sInstance == null) {
                sInstance = new NetworkDiagnoseItemFactory(context.getApplicationContext());
            }
            networkDiagnoseItemFactory = sInstance;
        }
        return networkDiagnoseItemFactory;
    }

    public List<AbstractNetworkDiagoneItem> getAllBrokenMobileDataItem() {
        ArrayList arrayList = new ArrayList();
        for (AbstractNetworkDiagoneItem next : getAllMobileDataItem()) {
            if (!next.getIsStatusNormal() && !TextUtils.isEmpty(next.getItemSummary())) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public List<AbstractNetworkDiagoneItem> getAllBrokenUsbShareItem() {
        ArrayList arrayList = new ArrayList();
        for (AbstractNetworkDiagoneItem next : getAllUsbShareItem()) {
            if (!next.getIsStatusNormal() && !TextUtils.isEmpty(next.getItemSummary())) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public List<AbstractNetworkDiagoneItem> getAllBrokenWifiItem() {
        ArrayList arrayList = new ArrayList();
        for (AbstractNetworkDiagoneItem next : getAllWifiItem()) {
            if (!next.getIsStatusNormal() && !TextUtils.isEmpty(next.getItemSummary())) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public List<AbstractNetworkDiagoneItem> getAllMobileDataItem() {
        ArrayList arrayList = new ArrayList();
        if (this.mApnCheck == null) {
            this.mApnCheck = new ApnCheck(this.mContext);
        }
        if (this.mProtocolCheck == null) {
            this.mProtocolCheck = new ProtocolCheck(this.mContext);
        }
        if (this.mNetworkConnectedCheck == null) {
            this.mNetworkConnectedCheck = new NetworkConnectedCheck(this.mContext);
        }
        if (this.mNetworkPermissionCheck == null) {
            this.mNetworkPermissionCheck = new AppFirewallCheck(this.mContext);
        }
        if (this.mSignalStrengthCheck == null) {
            this.mSignalStrengthCheck = new SignalStrengthCheck(this.mContext);
        }
        if (this.mNeedCharge == null) {
            this.mNeedCharge = new NeedCharge(this.mContext);
        }
        if (this.mBgNetworkCheck == null) {
            this.mBgNetworkCheck = new BgNetworkCheck(this.mContext);
        }
        arrayList.add(this.mNetworkConnectedCheck);
        arrayList.add(this.mSignalStrengthCheck);
        if (!B.g() || !DeviceUtil.IS_P_OR_LATER) {
            arrayList.add(this.mProtocolCheck);
        }
        arrayList.add(this.mNetworkPermissionCheck);
        arrayList.add(this.mBgNetworkCheck);
        arrayList.add(this.mApnCheck);
        arrayList.add(this.mNeedCharge);
        return arrayList;
    }

    public List<AbstractNetworkDiagoneItem> getAllUsbShareItem() {
        ArrayList arrayList = new ArrayList();
        if (this.mNetworkConnectedCheck == null) {
            this.mNetworkConnectedCheck = new NetworkConnectedCheck(this.mContext);
        }
        if (this.mUsbShareNetCheck == null) {
            this.mUsbShareNetCheck = new UsbShareNetCheck(this.mContext);
        }
        arrayList.add(this.mNetworkConnectedCheck);
        arrayList.add(this.mUsbShareNetCheck);
        return arrayList;
    }

    public List<AbstractNetworkDiagoneItem> getAllWifiItem() {
        ArrayList arrayList = new ArrayList();
        if (this.mWifiConnectedCheck == null) {
            this.mWifiConnectedCheck = new WifiConnectedCheck(this.mContext);
        }
        arrayList.add(this.mWifiConnectedCheck);
        if (f.l(this.mContext)) {
            if (this.mProtocolCheck == null) {
                this.mProtocolCheck = new ProtocolCheck(this.mContext);
            }
            if (this.mNetworkConnectedCheck == null) {
                this.mNetworkConnectedCheck = new NetworkConnectedCheck(this.mContext);
            }
            if (this.mNetworkPermissionCheck == null) {
                this.mNetworkPermissionCheck = new AppFirewallCheck(this.mContext);
            }
            if (this.mDnsCheckItem == null) {
                this.mDnsCheckItem = new DNSCheck(this.mContext);
            }
            if (this.mGatewayCheck == null) {
                this.mGatewayCheck = new GatewayCheck(this.mContext);
            }
            if (this.mBgNetworkCheck == null) {
                this.mBgNetworkCheck = new BgNetworkCheck(this.mContext);
            }
            if (!B.g() || !DeviceUtil.IS_P_OR_LATER) {
                arrayList.add(this.mProtocolCheck);
            }
            arrayList.add(this.mNetworkConnectedCheck);
            arrayList.add(this.mNetworkPermissionCheck);
            arrayList.add(this.mBgNetworkCheck);
            arrayList.add(this.mDnsCheckItem);
            arrayList.add(this.mGatewayCheck);
        }
        return arrayList;
    }

    public AbstractNetworkDiagoneItem getNetworkChangedCheckItem() {
        if (this.mNetworkChangedCheck == null) {
            this.mNetworkChangedCheck = new NetworkChangedCheck(this.mContext);
        }
        return this.mNetworkChangedCheck;
    }
}
