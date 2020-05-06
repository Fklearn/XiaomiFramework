package com.android.server.connectivity;

import android.content.Context;
import com.android.server.MiuiConfigCaptivePortal;

class NetworkAgentInfoInjector {
    NetworkAgentInfoInjector() {
    }

    static final boolean enableDataAndWifiRoam(Context context) {
        return MiuiConfigCaptivePortal.enableDataAndWifiRoam(context);
    }
}
