package com.miui.permcenter.permissions;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;

class F implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SystemAppPermissionDialogActivity f6222a;

    F(SystemAppPermissionDialogActivity systemAppPermissionDialogActivity) {
        this.f6222a = systemAppPermissionDialogActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        int i2;
        SystemAppPermissionDialogActivity systemAppPermissionDialogActivity;
        Log.i("SystemAppPDA", "user rejected!" + this.f6222a.y);
        dialogInterface.dismiss();
        if (TextUtils.equals("miui.intent.action.SYSTEM_PERMISSION_DECLARE_NEW", this.f6222a.z)) {
            systemAppPermissionDialogActivity = this.f6222a;
            i2 = 666;
        } else {
            systemAppPermissionDialogActivity = this.f6222a;
            i2 = 0;
        }
        systemAppPermissionDialogActivity.setResult(i2);
        this.f6222a.finish();
    }
}
