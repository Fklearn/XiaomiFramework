package com.miui.googlebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.googlebase.GoogleBaseAppInstallService;
import com.miui.googlebase.b.d;
import com.miui.networkassistant.config.Constants;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

class e extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GoogleBaseAppInstallService f5451a;

    e(GoogleBaseAppInstallService googleBaseAppInstallService) {
        this.f5451a = googleBaseAppInstallService;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Constants.System.ACTION_PACKAGE_ADDED.equals(action) || "android.intent.action.PACKAGE_REPLACED".equals(action)) {
            Log.d("GoogleBaseApp", "received " + action);
            String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
            if (GoogleBaseAppInstallService.f5425b.contains(schemeSpecificPart)) {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(this.f5451a.f5427d);
                arrayList.add(this.f5451a.e);
                File a2 = d.a((List<GoogleBaseAppInstallService.a>) arrayList, schemeSpecificPart);
                if (a2 != null) {
                    Log.i("GoogleBaseApp", "delete apk installer " + schemeSpecificPart);
                    a2.delete();
                }
                if (this.f5451a.o < 100) {
                    GoogleBaseAppInstallService googleBaseAppInstallService = this.f5451a;
                    int unused = googleBaseAppInstallService.o = googleBaseAppInstallService.o + 10;
                    GoogleBaseAppInstallService googleBaseAppInstallService2 = this.f5451a;
                    googleBaseAppInstallService2.a(googleBaseAppInstallService2.g, 10);
                }
                b bVar = new b(this.f5451a.g, (String) null);
                if (!bVar.e()) {
                    return;
                }
                if (!this.f5451a.f() && !bVar.g()) {
                    GoogleBaseAppInstallService googleBaseAppInstallService3 = this.f5451a;
                    googleBaseAppInstallService3.a(googleBaseAppInstallService3.g, false);
                    return;
                }
                this.f5451a.a(0);
            }
        }
    }
}
