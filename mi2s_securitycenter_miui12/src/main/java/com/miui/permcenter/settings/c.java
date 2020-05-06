package com.miui.permcenter.settings;

import android.provider.Settings;
import androidx.preference.Preference;
import com.miui.permcenter.a.a;

class c implements Preference.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j f6510a;

    c(j jVar) {
        this.f6510a = jVar;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        String str;
        if (this.f6510a.f6521c == preference) {
            if (!((Boolean) obj).booleanValue()) {
                Settings.Secure.putInt(this.f6510a.getActivity().getContentResolver(), "PERMISSION_USE_WARNING", -1);
                this.f6510a.a();
                str = "permission_use_close_toggle";
            } else if (x.a()) {
                Settings.Secure.putInt(this.f6510a.getActivity().getContentResolver(), "PERMISSION_USE_WARNING", 1);
                this.f6510a.a();
                str = "permission_use_toggle";
            } else {
                this.f6510a.c();
            }
            a.e(str);
        }
        return true;
    }
}
