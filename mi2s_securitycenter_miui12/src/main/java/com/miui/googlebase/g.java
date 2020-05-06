package com.miui.googlebase;

import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;

class g implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GoogleBaseAppInstallService f5453a;

    g(GoogleBaseAppInstallService googleBaseAppInstallService) {
        this.f5453a = googleBaseAppInstallService;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Log.d("GoogleBaseApp", "start preinstall apps");
        this.f5453a.j();
        Dialog unused = this.f5453a.n = null;
    }
}
