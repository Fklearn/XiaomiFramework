package com.miui.networkassistant.traffic.correction;

import android.content.Context;
import com.miui.networkassistant.traffic.correction.impl.MiuiTrafficCorrection;
import com.miui.networkassistant.traffic.correction.impl.WebTrafficCorrection;

public class TrafficCorrectionManager {
    public static final int TRAFFIC_CORRECTION_MIUI = 0;

    private TrafficCorrectionManager() {
    }

    public static ITrafficCorrection getTrafficCorrectionInstance(int i, Context context, String str, int i2) {
        return getTrafficCorrectionInstance(i, context, str, i2, true);
    }

    private static ITrafficCorrection getTrafficCorrectionInstance(int i, Context context, String str, int i2, boolean z) {
        MiuiTrafficCorrection instance = i != 0 ? null : MiuiTrafficCorrection.getInstance(context, str, i2);
        return (instance == null || !z) ? instance : WebTrafficCorrection.getInstance(context, str, i2, instance);
    }

    public static ITrafficCorrection getTrafficCorrectionInstance(Context context, String str, int i) {
        return getTrafficCorrectionInstance(0, context, str, i);
    }
}
