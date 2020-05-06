package com.miui.applicationlock.widget;

import android.text.Editable;
import android.text.TextWatcher;

class g implements TextWatcher {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j f3436a;

    g(j jVar) {
        this.f3436a = jVar;
    }

    public void afterTextChanged(Editable editable) {
        if (this.f3436a.f3442c.getText().length() >= 1 && !this.f3436a.i) {
            this.f3436a.f3441b.a(editable);
        }
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
