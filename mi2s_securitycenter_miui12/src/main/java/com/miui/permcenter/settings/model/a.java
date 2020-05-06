package com.miui.permcenter.settings.model;

import android.widget.CompoundButton;

class a implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CheckValuePreference f6542a;

    a(CheckValuePreference checkValuePreference) {
        this.f6542a = checkValuePreference;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (this.f6542a.f6532c != null) {
            this.f6542a.f6532c.a(z);
        }
    }
}
