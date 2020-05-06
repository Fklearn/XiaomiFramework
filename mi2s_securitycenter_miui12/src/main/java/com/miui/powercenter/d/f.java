package com.miui.powercenter.d;

import android.content.Context;
import android.provider.Settings;

public class f implements d {
    public void a(Context context) {
        synchronized (f.class) {
            int i = Settings.System.getInt(context.getContentResolver(), "power_center_sound_mode_dialer", -1);
            int i2 = Settings.System.getInt(context.getContentResolver(), "power_center_sound_mode_click", -1);
            int i3 = Settings.System.getInt(context.getContentResolver(), "power_center_sound_mode_lock", -1);
            int i4 = Settings.System.getInt(context.getContentResolver(), "power_center_sound_mode_screenshot", -1);
            int i5 = Settings.System.getInt(context.getContentResolver(), "power_center_sound_mode_delete", -1);
            if (!(i == -1 || i == Settings.System.getInt(context.getContentResolver(), "dtmf_tone", 0))) {
                Settings.System.putInt(context.getContentResolver(), "dtmf_tone", i);
            }
            if (!(i2 == -1 || i2 == Settings.System.getInt(context.getContentResolver(), "sound_effects_enabled", 0))) {
                Settings.System.putInt(context.getContentResolver(), "sound_effects_enabled", i2);
            }
            if (!(i3 == -1 || i3 == Settings.System.getInt(context.getContentResolver(), "lockscreen_sounds_enabled", 0))) {
                Settings.System.putInt(context.getContentResolver(), "lockscreen_sounds_enabled", i3);
            }
            if (!(i4 == -1 || i4 == Settings.System.getInt(context.getContentResolver(), "has_screenshot_sound", 0))) {
                Settings.System.putInt(context.getContentResolver(), "has_screenshot_sound", i4);
            }
            if (!(i5 == -1 || i5 == Settings.System.getInt(context.getContentResolver(), "delete_sound_effect", 0))) {
                Settings.System.putInt(context.getContentResolver(), "delete_sound_effect", i5);
            }
            Settings.System.putInt(context.getContentResolver(), "power_center_sound_mode_dialer", -1);
            Settings.System.putInt(context.getContentResolver(), "power_center_sound_mode_click", -1);
            Settings.System.putInt(context.getContentResolver(), "power_center_sound_mode_lock", -1);
            Settings.System.putInt(context.getContentResolver(), "power_center_sound_mode_screenshot", -1);
            Settings.System.putInt(context.getContentResolver(), "power_center_sound_mode_delete", -1);
        }
    }

    public void b(Context context) {
        synchronized (f.class) {
            int i = Settings.System.getInt(context.getContentResolver(), "power_center_sound_mode_dialer", -1);
            int i2 = Settings.System.getInt(context.getContentResolver(), "power_center_sound_mode_click", -1);
            int i3 = Settings.System.getInt(context.getContentResolver(), "power_center_sound_mode_lock", -1);
            int i4 = Settings.System.getInt(context.getContentResolver(), "power_center_sound_mode_screenshot", -1);
            int i5 = Settings.System.getInt(context.getContentResolver(), "power_center_sound_mode_delete", -1);
            int i6 = Settings.System.getInt(context.getContentResolver(), "dtmf_tone", 1);
            int i7 = Settings.System.getInt(context.getContentResolver(), "sound_effects_enabled", 1);
            int i8 = Settings.System.getInt(context.getContentResolver(), "lockscreen_sounds_enabled", 1);
            int i9 = Settings.System.getInt(context.getContentResolver(), "has_screenshot_sound", 1);
            int i10 = Settings.System.getInt(context.getContentResolver(), "delete_sound_effect", 1);
            if (i == -1) {
                Settings.System.putInt(context.getContentResolver(), "power_center_sound_mode_dialer", i6);
            }
            if (i2 == -1) {
                Settings.System.putInt(context.getContentResolver(), "power_center_sound_mode_click", i7);
            }
            if (i3 == -1) {
                Settings.System.putInt(context.getContentResolver(), "power_center_sound_mode_lock", i8);
            }
            if (i4 == -1) {
                Settings.System.putInt(context.getContentResolver(), "power_center_sound_mode_screenshot", i9);
            }
            if (i5 == -1) {
                Settings.System.putInt(context.getContentResolver(), "power_center_sound_mode_delete", i10);
            }
            if (i6 != 0) {
                Settings.System.putInt(context.getContentResolver(), "dtmf_tone", 0);
            }
            if (i7 != 0) {
                Settings.System.putInt(context.getContentResolver(), "sound_effects_enabled", 0);
            }
            if (i8 != 0) {
                Settings.System.putInt(context.getContentResolver(), "lockscreen_sounds_enabled", 0);
            }
            if (i9 != 0) {
                Settings.System.putInt(context.getContentResolver(), "has_screenshot_sound", 0);
            }
            if (i10 != 0) {
                Settings.System.putInt(context.getContentResolver(), "delete_sound_effect", 0);
            }
        }
    }
}
