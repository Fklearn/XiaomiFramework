package com.miui.antivirus.activity;

import androidx.preference.Preference;
import b.b.b.p;
import com.miui.antivirus.activity.SettingsActivity;

class A implements Preference.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f2649a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ SettingsActivity.c f2650b;

    A(SettingsActivity.c cVar, String str) {
        this.f2650b = cVar;
        this.f2649a = str;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        p.a(this.f2649a, ((Boolean) obj).booleanValue());
        return true;
    }
}
