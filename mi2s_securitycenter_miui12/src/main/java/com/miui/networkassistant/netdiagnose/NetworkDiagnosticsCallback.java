package com.miui.networkassistant.netdiagnose;

import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils;

public interface NetworkDiagnosticsCallback {
    void onNetworkDiagnosticsDone(NetworkDiagnosticsUtils.NetworkState networkState);

    void onNetworkDiagnosticsProcessChanged(int i);

    void switchView(int i, int i2, boolean z);
}
