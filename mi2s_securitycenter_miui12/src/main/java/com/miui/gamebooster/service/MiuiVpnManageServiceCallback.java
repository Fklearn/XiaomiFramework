package com.miui.gamebooster.service;

import com.miui.networkassistant.vpn.miui.IMiuiVpnManageServiceCallback;
import java.util.List;

public class MiuiVpnManageServiceCallback extends IMiuiVpnManageServiceCallback.Stub {
    public boolean isVpnConnected() {
        return false;
    }

    public void onQueryCouponsResult(int i, List<String> list) {
    }

    public void onVpnStateChanged(int i, int i2, String str) {
    }
}
