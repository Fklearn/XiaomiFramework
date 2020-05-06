package com.miui.optimizemanage.settings;

import androidx.preference.Preference;

class e implements Preference.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ g f5995a;

    e(g gVar) {
        this.f5995a = gVar;
    }

    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.f5995a.f5997a) {
            this.f5995a.d();
            return false;
        } else if (preference != this.f5995a.h) {
            return false;
        } else {
            this.f5995a.c();
            return false;
        }
    }
}
