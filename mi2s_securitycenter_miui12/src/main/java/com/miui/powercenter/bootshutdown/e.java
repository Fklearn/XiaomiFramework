package com.miui.powercenter.bootshutdown;

import android.content.DialogInterface;
import com.miui.powercenter.bootshutdown.PowerShutdownOnTime;

class e implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PowerShutdownOnTime.a f6951a;

    e(PowerShutdownOnTime.a aVar) {
        this.f6951a = aVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i != -2 && i == -1) {
            this.f6951a.f6941a.finish();
        }
        dialogInterface.dismiss();
    }
}
