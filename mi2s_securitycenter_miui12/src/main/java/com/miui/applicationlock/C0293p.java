package com.miui.applicationlock;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import com.miui.applicationlock.c.F;
import java.util.List;

/* renamed from: com.miui.applicationlock.p  reason: case insensitive filesystem */
class C0293p implements TextWatcher {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0312y f3370a;

    C0293p(C0312y yVar) {
        this.f3370a = yVar;
    }

    public void afterTextChanged(Editable editable) {
        if (this.f3370a.c()) {
            String unused = this.f3370a.w = editable.toString().trim();
            if (TextUtils.isEmpty(this.f3370a.w)) {
                this.f3370a.f.a((List<F>) this.f3370a.j, true);
                this.f3370a.d();
                return;
            }
            C0312y yVar = this.f3370a;
            yVar.a(yVar.w);
        }
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
