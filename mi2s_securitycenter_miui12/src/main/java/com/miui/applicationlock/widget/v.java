package com.miui.applicationlock.widget;

import android.text.Editable;
import android.text.TextWatcher;

class v implements TextWatcher {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f3457a;

    v(x xVar) {
        this.f3457a = xVar;
    }

    public void afterTextChanged(Editable editable) {
        if (this.f3457a.h.getText().length() >= 1 && !this.f3457a.j) {
            this.f3457a.f3461c.a(editable);
        }
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
