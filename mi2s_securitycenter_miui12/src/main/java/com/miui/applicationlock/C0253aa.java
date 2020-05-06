package com.miui.applicationlock;

import android.content.DialogInterface;
import com.miui.applicationlock.c.o;

/* renamed from: com.miui.applicationlock.aa  reason: case insensitive filesystem */
class C0253aa implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f3253a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3254b;

    C0253aa(ConfirmAccessControl confirmAccessControl, int i) {
        this.f3254b = confirmAccessControl;
        this.f3253a = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f3254b.C.h(this.f3254b.U);
        o.a("cancel_dialog", this.f3253a);
        o.a(dialogInterface, "cancel_notify_dialog", this.f3253a);
        this.f3254b.C.a((String) null);
        if (!this.f3254b.r && !"numeric".equals(this.f3254b.ca)) {
            this.f3254b.e.setVisibility(4);
        }
    }
}
