package com.miui.privacyapps.ui;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import b.b.k.d;
import java.util.ArrayList;

class f implements TextWatcher {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ n f7401a;

    f(n nVar) {
        this.f7401a = nVar;
    }

    public void afterTextChanged(Editable editable) {
        if (this.f7401a.b()) {
            String trim = editable.toString().trim();
            if (TextUtils.isEmpty(trim)) {
                this.f7401a.g.a((ArrayList<d>) this.f7401a.r);
                this.f7401a.d();
                return;
            }
            this.f7401a.a(trim);
        }
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
