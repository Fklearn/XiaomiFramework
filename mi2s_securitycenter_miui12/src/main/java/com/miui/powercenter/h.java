package com.miui.powercenter;

import android.content.DialogInterface;
import android.widget.CheckBox;
import com.miui.powercenter.utils.o;
import com.miui.superpower.b.g;

class h implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CheckBox f7069a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f7070b;

    h(j jVar, CheckBox checkBox) {
        this.f7070b = jVar;
        this.f7069a = checkBox;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            this.f7070b.f7072a.f.setChecked(false);
        } else if (i == -1) {
            if (this.f7069a.isChecked()) {
                g.b(false);
            }
            com.miui.superpower.b.h.a("home");
            o.a(this.f7070b.f7072a.s, true, true);
        }
    }
}
