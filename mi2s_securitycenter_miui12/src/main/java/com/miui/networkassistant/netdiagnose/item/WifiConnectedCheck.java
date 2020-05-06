package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import android.content.Intent;
import b.b.c.h.f;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.securitycenter.R;

public class WifiConnectedCheck extends AbstractNetworkDiagoneItem {
    private static final String TAG = "NetworkDiagnostics_WifiConnectedCheck";

    public WifiConnectedCheck(Context context) {
        super(context);
    }

    private boolean checkWlanConnected() {
        return f.l(this.mContext);
    }

    private void goToWifiSetting() {
        Intent intent = new Intent("android.settings.WIFI_SETTINGS");
        intent.setFlags(276824064);
        this.mContext.startActivity(intent);
    }

    public void check() {
        this.mIsStatusNormal = checkWlanConnected();
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        goToWifiSetting();
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public String getItemName() {
        return this.mContext.getResources().getString(R.string.wifi_exception_title);
    }

    public String getItemSolution() {
        return this.mContext.getResources().getString(R.string.choose);
    }

    public String getItemSummary() {
        return this.mContext.getResources().getString(R.string.wifi_exception_summary);
    }
}
