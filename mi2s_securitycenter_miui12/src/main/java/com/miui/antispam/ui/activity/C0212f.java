package com.miui.antispam.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;

/* renamed from: com.miui.antispam.ui.activity.f  reason: case insensitive filesystem */
class C0212f implements TextWatcher {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AddPhoneListActivity f2592a;

    C0212f(AddPhoneListActivity addPhoneListActivity) {
        this.f2592a = addPhoneListActivity;
    }

    public void afterTextChanged(Editable editable) {
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        boolean unused = this.f2592a.p = charSequence.length() > 0 && this.f2592a.c();
        this.f2592a.g.getOk().setEnabled(this.f2592a.p);
    }
}
