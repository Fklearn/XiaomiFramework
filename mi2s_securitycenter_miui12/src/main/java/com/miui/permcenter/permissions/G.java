package com.miui.permcenter.permissions;

import android.content.DialogInterface;
import android.util.Log;

class G implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SystemAppPermissionDialogActivity f6223a;

    G(SystemAppPermissionDialogActivity systemAppPermissionDialogActivity) {
        this.f6223a = systemAppPermissionDialogActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Log.i("SystemAppPDA", "user agreed! " + this.f6223a.y);
        dialogInterface.dismiss();
        this.f6223a.setResult(1);
        this.f6223a.finish();
    }
}
