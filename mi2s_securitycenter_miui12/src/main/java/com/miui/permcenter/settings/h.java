package com.miui.permcenter.settings;

import android.content.DialogInterface;
import android.provider.Settings;
import android.widget.CheckBox;
import com.miui.permcenter.a.a;

class h implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CheckBox f6516a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f6517b;

    h(j jVar, CheckBox checkBox) {
        this.f6517b = jVar;
        this.f6516a = checkBox;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (!x.c() && this.f6516a.isChecked()) {
            x.a(true);
            a.f("dialog_ignore");
        }
        Settings.Secure.putInt(this.f6517b.getActivity().getContentResolver(), "PERMISSION_USE_WARNING", 1);
        this.f6517b.a();
        a.f("dialog_ok");
        a.e("permission_use_toggle");
        dialogInterface.dismiss();
    }
}
