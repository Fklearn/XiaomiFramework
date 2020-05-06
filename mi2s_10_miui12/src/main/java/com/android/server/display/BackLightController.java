package com.android.server.display;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.miui.R;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.util.Slog;
import android.util.Spline;
import miui.os.DeviceFeature;

public class BackLightController {
    private static final float D1 = 0.048851978f;
    private static final float D2 = 0.073277965f;
    private static final float D3 = 0.34196386f;
    private static final float D4 = 0.39081582f;
    private static final float D5 = 0.073277965f;
    private static final float D6 = 0.34196386f;
    private static final float D7 = 0.5862237f;
    private static final float D8 = 0.7327797f;
    private static final boolean DEBUG = sDebuggable;
    private static final int GRAY_BRIGHTNESS_RATE = SYSTEM_RESOURCES.getInteger(R.integer.config_grayBrightnessRate);
    private static final float K1 = -15.352501f;
    private static final float K2 = 7.6762524f;
    private static final float K3 = -1.595065f;
    private static final float K4 = 1.7058333f;
    private static final float MAX_A = 0.15f;
    private static Spline MAX_BRIGHTNESS_SPLINE = null;
    private static final float MAX_DIFF = 0.058622375f;
    private static Spline MIN_BRIGHTNESS_SPLINE = null;
    private static final int NIGHT_LIGHT_BRIGHTNESS_DEFAULT = SYSTEM_RESOURCES.getInteger(R.integer.config_nightLightBrightnessDefault);
    private static final Resources SYSTEM_RESOURCES = Resources.getSystem();
    private static final String TAG = "BackLightController";
    private static final float V1 = 0.4f;
    private static final float V2 = 0.6f;
    private static int sBackLightRate = -1;
    private static boolean sDebuggable = SystemProperties.getBoolean("sys.sensor.autobacklight.dbg", false);
    private static float sGrayBrightnessFactor = 1.0f;
    private static int sLastBrightness = -1;

    static {
        try {
            MAX_BRIGHTNESS_SPLINE = Spline.createSpline(getFloatArray(SYSTEM_RESOURCES.obtainTypedArray(R.array.config_BrightnessCurvesLux)), getFloatArray(SYSTEM_RESOURCES.obtainTypedArray(R.array.config_maxBrightnessCurvesNits)));
            MIN_BRIGHTNESS_SPLINE = Spline.createSpline(getFloatArray(SYSTEM_RESOURCES.obtainTypedArray(R.array.config_BrightnessCurvesLux)), getFloatArray(SYSTEM_RESOURCES.obtainTypedArray(R.array.config_minBrightnessCurvesNits)));
        } catch (IllegalArgumentException e) {
            Slog.w(TAG, "brightness dynamic range not support.");
        }
    }

    static boolean setNightLight(int value) {
        if (value < 0 || (16711680 & value) == 0) {
            return false;
        }
        if (ScreenEffectService.sScreenEffectManager == null) {
            return true;
        }
        ScreenEffectService.sScreenEffectManager.setNightLight(value);
        return true;
    }

    static int getNightLightBrightness(int brightness, int minBrightness) {
        if (!DeviceFeature.SUPPORT_NIGHT_LIGHT || brightness != minBrightness) {
            return brightness;
        }
        if (!DeviceFeature.SUPPORT_NIGHT_LIGHT_ADJ || ScreenEffectService.sScreenEffectManager == null) {
            return NIGHT_LIGHT_BRIGHTNESS_DEFAULT;
        }
        return ScreenEffectService.sScreenEffectManager.getNightLightBrightness();
    }

    static int adjustBrightness(int brightness, int minBrightness, int maxBrightness, boolean autoBrightnessEnabled, float lux, DisplayPowerState powerState, Spline nitToBrightnessSpline) {
        Spline spline;
        if (!(!autoBrightnessEnabled || (spline = MAX_BRIGHTNESS_SPLINE) == null || MIN_BRIGHTNESS_SPLINE == null || nitToBrightnessSpline == null)) {
            int dynamicMaxBrightness = Math.round(nitToBrightnessSpline.interpolate(spline.interpolate(lux)) * ((float) PowerManager.BRIGHTNESS_ON));
            int dynamicMinBrightness = Math.round(nitToBrightnessSpline.interpolate(MIN_BRIGHTNESS_SPLINE.interpolate(lux)) * ((float) PowerManager.BRIGHTNESS_ON));
            int oldBrightness = brightness;
            brightness = dynamicMinBrightness + Math.round((((float) (oldBrightness - minBrightness)) / ((float) (maxBrightness - minBrightness))) * ((float) (dynamicMaxBrightness - dynamicMinBrightness)));
            if (DEBUG) {
                Slog.i(TAG, "Dynamic brightness range for lux : oldBrightness = " + oldBrightness + " newBrightness = " + brightness + " minBrightness = " + minBrightness + " maxBrightness = " + maxBrightness + " dynamicMinBrightness = " + dynamicMinBrightness + " dynamicMaxBrightness = " + dynamicMaxBrightness + "lux = " + lux);
            }
        }
        if (DeviceFeature.SUPPORT_NIGHT_LIGHT == 0 || brightness != minBrightness || (autoBrightnessEnabled && lux != 0.0f)) {
            if ((autoBrightnessEnabled || sDebuggable) && ScreenEffectService.sScreenEffectManager != null) {
                float grayScale = ScreenEffectService.sScreenEffectManager.getGrayScale();
                if (!Float.isNaN(grayScale)) {
                    int oldBrightness2 = brightness;
                    brightness = caculateBrightness(brightness, maxBrightness, grayScale);
                    int i = sLastBrightness;
                    if (i == -1 || i != oldBrightness2 || brightness == oldBrightness2) {
                        sBackLightRate = -1;
                        sLastBrightness = oldBrightness2;
                    } else {
                        sBackLightRate = GRAY_BRIGHTNESS_RATE;
                    }
                }
            }
            if (!autoBrightnessEnabled && sBackLightRate != -1) {
                sBackLightRate = -1;
            }
            return brightness;
        }
        int nightLightBrightness = NIGHT_LIGHT_BRIGHTNESS_DEFAULT;
        if (DeviceFeature.SUPPORT_NIGHT_LIGHT_ADJ && ScreenEffectService.sScreenEffectManager != null) {
            nightLightBrightness = ScreenEffectService.sScreenEffectManager.getNightLightBrightness();
        }
        return Math.min(nightLightBrightness, minBrightness);
    }

    static int adjustBackLightRate(int inRate) {
        int i = sBackLightRate;
        if (i >= 0) {
            return i;
        }
        return inRate;
    }

    private static int caculateBrightness(int brightness, int maxBrightness, float grayScale) {
        float ratio = ((float) brightness) / ((float) maxBrightness);
        if (grayScale > V2) {
            if (ratio > 0.073277965f && ratio <= 0.34196386f) {
                sGrayBrightnessFactor = ((ratio - 0.073277965f) * K3 * (grayScale - V2)) + 1.0f;
            } else if (ratio > 0.34196386f && ratio <= D7) {
                sGrayBrightnessFactor = 1.0f - (((grayScale - V2) * MAX_DIFF) / (0.39999998f * ratio));
            } else if (ratio <= D7 || ratio >= D8) {
                sGrayBrightnessFactor = 1.0f;
            } else {
                sGrayBrightnessFactor = ((ratio - D8) * K4 * (grayScale - V2)) + 1.0f;
            }
        } else if (grayScale <= 0.0f || grayScale >= V1) {
            sGrayBrightnessFactor = 1.0f;
        } else if (ratio > D1 && ratio <= 0.073277965f) {
            sGrayBrightnessFactor = ((ratio - D1) * K1 * (grayScale - V1)) + 1.0f;
        } else if (ratio > 0.073277965f && ratio <= 0.34196386f) {
            sGrayBrightnessFactor = 1.15f - (0.375f * grayScale);
        } else if (ratio <= 0.34196386f || ratio >= D4) {
            sGrayBrightnessFactor = 1.0f;
        } else {
            sGrayBrightnessFactor = ((ratio - D4) * K2 * (grayScale - V1)) + 1.0f;
        }
        Slog.i(TAG, " grayScale = " + grayScale + " factor = " + sGrayBrightnessFactor + " inBrightness = " + brightness + " outBrightness = " + Math.round(((float) brightness) * sGrayBrightnessFactor));
        return Math.round(((float) brightness) * sGrayBrightnessFactor);
    }

    static float getGrayBrightnessFactor() {
        return sGrayBrightnessFactor;
    }

    private static float[] getFloatArray(TypedArray array) {
        int length = array.length();
        float[] floatArray = new float[length];
        for (int i = 0; i < length; i++) {
            floatArray[i] = array.getFloat(i, Float.NaN);
        }
        array.recycle();
        return floatArray;
    }
}
