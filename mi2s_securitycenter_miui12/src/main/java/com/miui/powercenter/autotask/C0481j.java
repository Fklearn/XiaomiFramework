package com.miui.powercenter.autotask;

import android.text.Editable;
import com.miui.powercenter.autotask.TextEditPreference;

/* renamed from: com.miui.powercenter.autotask.j  reason: case insensitive filesystem */
class C0481j implements TextEditPreference.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0485n f6757a;

    C0481j(C0485n nVar) {
        this.f6757a = nVar;
    }

    public void a(Editable editable) {
        this.f6757a.f6671b.setName(editable.toString());
    }
}
