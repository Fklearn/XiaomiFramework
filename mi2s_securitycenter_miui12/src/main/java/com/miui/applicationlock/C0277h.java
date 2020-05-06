package com.miui.applicationlock;

import android.content.DialogInterface;
import com.miui.common.persistence.b;

/* renamed from: com.miui.applicationlock.h  reason: case insensitive filesystem */
class C0277h implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0312y f3351a;

    C0277h(C0312y yVar) {
        this.f3351a = yVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        b.b("cancel_face_unlock_guide_times", b.a("cancel_face_unlock_guide_times", 0) + 1);
    }
}
