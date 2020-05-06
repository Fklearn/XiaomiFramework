package com.miui.googlebase;

import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;

class f implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GoogleBaseAppInstallService f5452a;

    f(GoogleBaseAppInstallService googleBaseAppInstallService) {
        this.f5452a = googleBaseAppInstallService;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Log.d("GoogleBaseApp", "user cancel installation");
        this.f5452a.a(7);
        this.f5452a.stopSelf();
        Dialog unused = this.f5452a.n = null;
    }
}
