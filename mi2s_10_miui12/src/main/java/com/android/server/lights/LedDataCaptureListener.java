package com.android.server.lights;

import android.content.Context;
import android.os.Looper;
import com.android.server.lights.MiuiLightsService;

public class LedDataCaptureListener implements MiuiLightsService.DataCaptureListener {
    private static final String TAG = LedDataCaptureListener.class.getSimpleName();

    public LedDataCaptureListener(Context context, Looper looper) {
    }

    public void onSetLightCallback(Context mContext, int lightId, int color, int mode, int onMS, int offMS, int brightnessMode) {
    }

    public void onFrequencyCapture(Context mContext, int magnitude_max, float[] frequencies) {
    }
}
