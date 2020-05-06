package com.miui.applicationlock;

import android.content.DialogInterface;

class G implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ChooseAccessControl f3175a;

    G(ChooseAccessControl chooseAccessControl) {
        this.f3175a = chooseAccessControl;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
    }
}
