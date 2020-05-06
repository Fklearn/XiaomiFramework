package com.miui.appmanager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import com.miui.appmanager.a.a;
import com.xiaomi.stat.MiStat;

class v implements TextWatcher {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppManagerMainActivity f3690a;

    v(AppManagerMainActivity appManagerMainActivity) {
        this.f3690a = appManagerMainActivity;
    }

    public void afterTextChanged(Editable editable) {
        String trim = editable.toString().trim();
        if (!TextUtils.isEmpty(trim)) {
            this.f3690a.h.setVisibility(8);
            this.f3690a.d(trim);
            if (!this.f3690a.V) {
                a.b(MiStat.Event.SEARCH);
                boolean unused = this.f3690a.V = true;
                return;
            }
            return;
        }
        this.f3690a.C();
        this.f3690a.F();
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
