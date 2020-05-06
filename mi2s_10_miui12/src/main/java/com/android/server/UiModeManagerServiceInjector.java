package com.android.server;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.MiuiConfiguration;
import android.miui.R;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.util.MathUtils;
import miui.util.ExquisiteModeUtils;

class UiModeManagerServiceInjector {
    private static float A = 0.0f;
    private static float B = 0.0f;
    private static float C = 0.0f;
    public static final int GAMMA_SPACE_MAX = PowerManager.BRIGHTNESS_ON;
    private static final boolean IS_MEXICO_TELCEL = "mx_telcel".equals(SystemProperties.get("ro.miui.customized.region"));
    private static float R = 0.0f;
    private static final String TAG = "UiModeManagerInjector";

    UiModeManagerServiceInjector() {
    }

    static void setDefaultFontSize(Context context) {
        if (IS_MEXICO_TELCEL && Settings.Global.getInt(context.getContentResolver(), "device_provisioned", 0) != 1) {
            Log.i(TAG, "setDefaultFontSize:" + 12);
            try {
                Configuration config = ActivityManagerNative.getDefault().getConfiguration();
                if (12 != (config.uiMode & 15)) {
                    Settings.System.putInt(context.getContentResolver(), "ui_mode_scale", 12);
                    config.fontScale = MiuiConfiguration.getFontScale(12);
                    config.uiMode = (config.uiMode & -16) | 12;
                    if (ExquisiteModeUtils.SUPPORT_EXQUISITE_MODE) {
                        config.extraConfig.updateTheme(268435456);
                    }
                    ActivityManagerNative.getDefault().updatePersistentConfiguration(config);
                }
            } catch (Exception e) {
                Log.e(TAG, "setDefaultFontSize failed:" + e.toString());
                e.printStackTrace();
            }
        }
    }

    public static void init(Context context) {
        R = context.getResources().getFloat(R.dimen.config_GammaLinearConvertRValue);
        A = context.getResources().getFloat(R.dimen.config_GammaLinearConvertAValue);
        B = context.getResources().getFloat(R.dimen.config_GammaLinearConvertBValue);
        C = context.getResources().getFloat(R.dimen.config_GammaLinearConvertCValue);
    }

    public static void updateAlpha(Context context, int mBrightness, int mMaxBrightness, int mMinBrightness) {
        int bright = convertLinearToGamma(mBrightness, mMinBrightness, mMaxBrightness);
        float ratio = 0.0f;
        if (mMaxBrightness > mMinBrightness) {
            ratio = ((float) bright) / ((float) (mMaxBrightness - mMinBrightness));
        }
        double alpha = 0.0d;
        if (((double) ratio) < 0.4d && ((double) ratio) > 0.1d) {
            alpha = Math.sqrt((0.4d - ((double) ratio)) / 2.5d);
        }
        if (((double) ratio) <= 0.1d) {
            alpha = (((double) ratio) + 0.1366d) / 0.683d;
        }
        Settings.System.putFloat(context.getContentResolver(), "contrast_alpha", (float) alpha);
    }

    public static int convertLinearToGamma(int val, int min, int max) {
        float ret;
        float normalizedVal = MathUtils.norm((float) min, (float) max, (float) val) * 12.0f;
        if (normalizedVal <= 1.0f) {
            ret = MathUtils.sqrt(normalizedVal) * R;
        } else {
            ret = (A * MathUtils.log(normalizedVal - B)) + C;
        }
        return Math.round(MathUtils.lerp(0.0f, (float) GAMMA_SPACE_MAX, ret));
    }
}
