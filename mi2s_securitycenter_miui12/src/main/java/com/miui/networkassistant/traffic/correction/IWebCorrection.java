package com.miui.networkassistant.traffic.correction;

import com.miui.networkassistant.traffic.correction.ITrafficCorrection;

public interface IWebCorrection {
    void queryDataUsage(String str, long j, boolean z, ITrafficCorrection.TrafficCorrectionListener trafficCorrectionListener);
}
