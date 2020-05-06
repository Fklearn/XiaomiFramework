package com.miui.powercenter.autotask;

import android.text.Editable;
import android.text.TextWatcher;

class da implements TextWatcher {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ TextEditPreference f6743a;

    da(TextEditPreference textEditPreference) {
        this.f6743a = textEditPreference;
    }

    public void afterTextChanged(Editable editable) {
        if (this.f6743a.f6730c != null) {
            this.f6743a.f6730c.a(editable);
        }
        String unused = this.f6743a.f6729b = editable.toString();
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
