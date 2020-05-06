package com.miui.applicationlock;

import android.content.DialogInterface;
import android.util.Log;
import com.miui.common.persistence.b;

/* renamed from: com.miui.applicationlock.u  reason: case insensitive filesystem */
class C0302u implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0312y f3384a;

    C0302u(C0312y yVar) {
        this.f3384a = yVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        b.b("cancel_fingerprint_verify_times", b.a("cancel_fingerprint_verify_times", 0) + 1);
        this.f3384a.m();
        Log.d("AppLockManageFragment", "mDialogListener");
    }
}
