package com.miui.permcenter.settings;

import androidx.preference.Preference;
import com.miui.permcenter.a.a;

class b implements Preference.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j f6509a;

    b(j jVar) {
        this.f6509a = jVar;
    }

    public boolean onPreferenceClick(Preference preference) {
        String str;
        if (this.f6509a.f6520b == preference) {
            this.f6509a.f();
            str = "slogan";
        } else if (this.f6509a.f6522d == preference) {
            this.f6509a.b();
            str = "danger_permission";
        } else if (this.f6509a.e == preference) {
            this.f6509a.f();
            str = "privacy_more";
        } else if (this.f6509a.g != preference) {
            return true;
        } else {
            this.f6509a.e();
            str = "privacy_url";
        }
        a.e(str);
        return true;
    }
}
