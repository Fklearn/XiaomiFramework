package com.miui.applicationlock;

import android.view.View;
import android.widget.CheckBox;

/* renamed from: com.miui.applicationlock.ba  reason: case insensitive filesystem */
class C0255ba implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CheckBox f3261a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3262b;

    C0255ba(ConfirmAccessControl confirmAccessControl, CheckBox checkBox) {
        this.f3262b = confirmAccessControl;
        this.f3261a = checkBox;
    }

    public void onClick(View view) {
        if (this.f3261a.isChecked()) {
            this.f3262b.N.getButton(-1).setEnabled(false);
            boolean unused = this.f3262b.U = true;
            return;
        }
        this.f3262b.N.getButton(-1).setEnabled(true);
    }
}
