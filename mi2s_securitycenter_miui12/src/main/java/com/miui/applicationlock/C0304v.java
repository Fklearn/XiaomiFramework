package com.miui.applicationlock;

import android.content.DialogInterface;
import com.miui.common.persistence.b;

/* renamed from: com.miui.applicationlock.v  reason: case insensitive filesystem */
class C0304v implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0312y f3386a;

    C0304v(C0312y yVar) {
        this.f3386a = yVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        b.b("cancel_face_unlock_verify_times", b.a("cancel_face_unlock_verify_times", 0) + 1);
        this.f3386a.n();
    }
}
