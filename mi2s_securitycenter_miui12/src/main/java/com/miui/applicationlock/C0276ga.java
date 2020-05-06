package com.miui.applicationlock;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

/* renamed from: com.miui.applicationlock.ga  reason: case insensitive filesystem */
class C0276ga implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Intent f3349a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3350b;

    C0276ga(ConfirmAccessControl confirmAccessControl, Intent intent) {
        this.f3350b = confirmAccessControl;
        this.f3349a = intent;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            if (this.f3349a != null) {
                boolean unused = this.f3350b.X = true;
                this.f3350b.startActivityForResult(this.f3349a, 290262);
            }
        } catch (Exception unused2) {
            Log.d("ConfirmAccessControl", "can not apply action");
        }
    }
}
