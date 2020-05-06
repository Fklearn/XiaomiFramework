package com.miui.permcenter.settings;

import android.content.DialogInterface;
import android.widget.CheckBox;
import com.miui.permcenter.a.a;

class g implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CheckBox f6514a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f6515b;

    g(j jVar, CheckBox checkBox) {
        this.f6515b = jVar;
        this.f6514a = checkBox;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (!x.c() && this.f6514a.isChecked()) {
            x.a(true);
        }
        a.f("dialog_cancel");
        dialogInterface.dismiss();
    }
}
