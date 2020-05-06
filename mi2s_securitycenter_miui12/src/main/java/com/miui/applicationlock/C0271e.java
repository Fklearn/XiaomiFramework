package com.miui.applicationlock;

import android.content.DialogInterface;
import com.miui.common.persistence.b;

/* renamed from: com.miui.applicationlock.e  reason: case insensitive filesystem */
class C0271e implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0312y f3344a;

    C0271e(C0312y yVar) {
        this.f3344a = yVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        b.b("cancel_fingerprint_guide_times", b.a("cancel_fingerprint_guide_times", 0) + 1);
    }
}
