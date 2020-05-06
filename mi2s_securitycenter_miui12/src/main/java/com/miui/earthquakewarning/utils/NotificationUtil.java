package com.miui.earthquakewarning.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.powercenter.utils.o;

public class NotificationUtil {
    private static final int BRIGHTNESS_SET = 225;
    private static final float VOLUME_SET = 1.5f;

    private NotificationUtil() {
    }

    public static void muteVolume(Context context) {
        setMaxVolume(context, 0);
    }

    public static void remuteVolume(Context context) {
        setMaxVolume(context, (int) ((((float) ((AudioManager) context.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).getStreamMaxVolume(3)) * 1.0f) / VOLUME_SET));
    }

    public static void resetBrightness(Context context) {
        setScreenBrightness(context, (float) Utils.getPreviousBrightness());
        setScreenMode(context, Utils.getPreviousBrightnessMode());
    }

    public static void resetGPS(Context context) {
        o.a(context, Utils.getPreviousGPS());
    }

    public static void resetVolume(Context context) {
        setMaxVolume(context, Utils.getPreviousVolume());
    }

    public static void setBrightness(Context context) {
        try {
            int i = Settings.System.getInt(context.getContentResolver(), "screen_brightness_mode");
            Utils.setPreviousBrightnessMode(i);
            Utils.setPreviousBrightness(Settings.System.getInt(context.getContentResolver(), "screen_brightness"));
            if (i == 1) {
                setScreenMode(context, 0);
            }
            setScreenBrightness(context, 225.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setGpsStatus(Context context) {
        Utils.setPreviousGPS(o.g(context));
        o.a(context, 3);
    }

    public static void setMaxVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        int streamVolume = audioManager.getStreamVolume(3);
        int streamMaxVolume = audioManager.getStreamMaxVolume(3);
        Utils.setPreviousVolume(streamVolume);
        setMaxVolume(context, (int) ((((float) streamMaxVolume) * 1.0f) / VOLUME_SET));
    }

    private static void setMaxVolume(Context context, int i) {
        ((AudioManager) context.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).setStreamVolume(3, i, 0);
    }

    private static void setScreenBrightness(Context context, float f) {
        Window window = ((Activity) context).getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.screenBrightness = f / 255.0f;
        window.setAttributes(attributes);
        Settings.System.putInt(context.getContentResolver(), "screen_brightness", (int) f);
    }

    private static void setScreenMode(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "screen_brightness_mode", i);
    }
}
