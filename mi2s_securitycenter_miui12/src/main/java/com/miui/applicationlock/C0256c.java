package com.miui.applicationlock;

import android.view.View;
import com.miui.common.persistence.b;

/* renamed from: com.miui.applicationlock.c  reason: case insensitive filesystem */
class C0256c implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0312y f3269a;

    C0256c(C0312y yVar) {
        this.f3269a = yVar;
    }

    public void onClick(View view) {
        b.b("cancel_fingerprint_verify_times", b.a("cancel_fingerprint_verify_times", 0) + 1);
        this.f3269a.o.dismiss();
    }
}
