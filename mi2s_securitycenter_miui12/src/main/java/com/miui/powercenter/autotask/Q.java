package com.miui.powercenter.autotask;

import androidx.preference.Preference;

class Q implements Preference.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ T f6725a;

    Q(T t) {
        this.f6725a = t;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        X.a(((U) this.f6725a.f6672c).getContext(), this.f6725a.f6671b, preference.getKey(), obj, new P(this));
        return true;
    }
}
