package com.android.server.net;

import android.net.IMiuiNetworkManager;

public class MiuiNetworkManager extends IMiuiNetworkManager.Stub {
    private static MiuiNetworkManager sSelf;

    public static final MiuiNetworkManager get() {
        if (sSelf == null) {
            sSelf = new MiuiNetworkManager();
        }
        return sSelf;
    }

    public boolean setNetworkTrafficPolicy(int mode) {
        return MiuiNetworkPolicyManagerService.get().setNetworkTrafficPolicy(mode);
    }

    public boolean setRpsStatus(boolean enable) {
        return MiuiNetworkPolicyManagerService.get().setRpsStatus(enable);
    }
}
