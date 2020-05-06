package com.miui.gamebooster.ui;

import android.provider.Settings;
import androidx.preference.Preference;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0384o;

class T implements Preference.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ FunctionShieldSettingsFragment f5007a;

    T(FunctionShieldSettingsFragment functionShieldSettingsFragment) {
        this.f5007a = functionShieldSettingsFragment;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        int i;
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (booleanValue) {
            a unused = this.f5007a.m;
            i = FunctionShieldSettingsFragment.a(this.f5007a);
        } else {
            a unused2 = this.f5007a.m;
            i = FunctionShieldSettingsFragment.b(this.f5007a);
        }
        a.b(i);
        if ("pref_auto_bright".equals(preference.getKey())) {
            a unused3 = this.f5007a.m;
            a.X(booleanValue);
            return true;
        } else if ("pref_eye_shield".equals(preference.getKey())) {
            a unused4 = this.f5007a.m;
            a.Y(booleanValue);
            if (!booleanValue) {
                Settings.System.putInt(this.f5007a.f4877a.getContentResolver(), (String) C0384o.b("android.provider.MiuiSettings$ScreenEffect", "GAME_MODE"), 0);
            }
            return true;
        } else if ("pref_three_finger".equals(preference.getKey())) {
            a unused5 = this.f5007a.m;
            a.aa(booleanValue);
            return true;
        } else if ("pref_pull_notification_bar".equals(preference.getKey())) {
            a unused6 = this.f5007a.m;
            a.Z(booleanValue);
            return true;
        } else if (!"pref_disable_voicetrigger".equals(preference.getKey())) {
            return false;
        } else {
            a unused7 = this.f5007a.m;
            a.F(booleanValue);
            return true;
        }
    }
}
