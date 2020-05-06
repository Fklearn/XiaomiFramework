package com.miui.permcenter.settings;

import androidx.preference.Preference;
import com.miui.permcenter.a.a;

class l implements Preference.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ n f6528a;

    l(n nVar) {
        this.f6528a = nVar;
    }

    public boolean onPreferenceClick(Preference preference) {
        String str;
        if (this.f6528a.f6554b == preference) {
            this.f6528a.d();
            str = "location_info";
        } else if (this.f6528a.f6555c == preference) {
            this.f6528a.b();
            str = "camera_info";
        } else if (this.f6528a.f6556d == preference) {
            this.f6528a.e();
            str = "spec_permission";
        } else if (this.f6528a.g != preference) {
            return true;
        } else {
            this.f6528a.c();
            str = "other_permission";
        }
        a.e(str);
        return true;
    }
}
