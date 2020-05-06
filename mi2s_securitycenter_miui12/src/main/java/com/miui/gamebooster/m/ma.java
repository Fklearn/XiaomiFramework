package com.miui.gamebooster.m;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import com.miui.common.persistence.b;
import com.miui.gamebooster.h.a;

public class ma {
    public static void a(long j) {
        b.b("voice_changer_begin_time", j);
    }

    public static void a(AudioManager audioManager, Context context, String str, int i) {
        if (!a.b(context, str)) {
            Log.i("VoiceChanger", "voice change mode off");
            audioManager.setParameters("misound_voice_change_switch=off;");
            audioManager.setParameters("misound_voice_change_pcakge=" + str);
            audioManager.setParameters("misound_voice_change_uid=" + i);
            Log.i("VoiceChanger", "parameters packageName " + str + " uid is " + i);
        }
    }

    public static void a(AudioManager audioManager, Context context, String str, String str2, int i) {
        if (!a.b(context, str2)) {
            Log.i("VoiceChanger", "voice change mode on " + str);
            audioManager.setParameters("misound_voice_change_switch=on;misound_voice_change_mode=" + str);
            audioManager.setParameters("misound_voice_change_pcakge=" + str2);
            audioManager.setParameters("misound_voice_change_uid=" + i);
            Log.i("VoiceChanger", "parameters packageName is " + str2 + " uid is " + i);
        }
    }

    public static void a(String str) {
        b.b("voice_changer_mode", str);
    }

    public static void a(boolean z) {
        b.b("is_first_show_vc_view", z);
    }

    public static boolean a() {
        return b.a("voice_changer_record_premission_allow", false);
    }

    public static long b() {
        return b.a("voice_changer_begin_time", 0);
    }

    public static void b(long j) {
        b.b("voice_changer_day_duration", j);
    }

    public static void b(boolean z) {
        b.b("voice_changer_record_premission_allow", z);
    }

    public static long c() {
        return b.a("voice_changer_day_duration", 0);
    }

    public static String d() {
        return b.a("voice_changer_mode", "original");
    }

    public static boolean e() {
        return b.a("is_first_show_vc_view", true);
    }
}
