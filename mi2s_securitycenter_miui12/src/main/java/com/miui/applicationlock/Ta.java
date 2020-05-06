package com.miui.applicationlock;

import android.content.DialogInterface;
import android.provider.Settings;
import com.miui.applicationlock.a.i;
import com.miui.applicationlock.c.o;
import com.miui.securitycenter.R;

class Ta implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ bb f3217a;

    Ta(bb bbVar) {
        this.f3217a = bbVar;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        int unused = this.f3217a.i = 0;
        this.f3217a.f.setText(this.f3217a.getResources().getString(R.string.fingerprint_identify_msg));
        Settings.Secure.putInt(this.f3217a.I.getContentResolver(), i.f3250a, 1);
        this.f3217a.f3264b.setChecked(false);
        this.f3217a.h.a();
        o.b(this.f3217a.I, true);
    }
}
