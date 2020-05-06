package com.miui.permcenter.permissions;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

/* renamed from: com.miui.permcenter.permissions.a  reason: case insensitive filesystem */
class C0464a implements TextWatcher {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0466c f6252a;

    C0464a(C0466c cVar) {
        this.f6252a = cVar;
    }

    public void afterTextChanged(Editable editable) {
        String trim = editable.toString().trim();
        if (!TextUtils.isEmpty(trim)) {
            this.f6252a.a(trim);
            return;
        }
        this.f6252a.e();
        this.f6252a.g();
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
