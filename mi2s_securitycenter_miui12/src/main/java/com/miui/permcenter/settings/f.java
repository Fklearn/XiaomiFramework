package com.miui.permcenter.settings;

import android.content.DialogInterface;
import android.provider.Settings;

class f implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j f6513a;

    f(j jVar) {
        this.f6513a = jVar;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.f6513a.f6521c.setChecked(Settings.Secure.getInt(this.f6513a.getActivity().getContentResolver(), "PERMISSION_USE_WARNING", 0) == 1);
        this.f6513a.h.removeMessages(1);
        if (this.f6513a.n != null) {
            this.f6513a.n.cancel();
        }
        x.c(false);
    }
}
