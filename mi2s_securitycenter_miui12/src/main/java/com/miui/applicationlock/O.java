package com.miui.applicationlock;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import b.b.o.g.e;
import com.miui.networkassistant.config.Constants;

class O implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3199a;

    O(ConfirmAccessControl confirmAccessControl) {
        this.f3199a = confirmAccessControl;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Intent intent;
        String str;
        Log.d("ConfirmAccessControl", "reset factory data");
        if (Build.VERSION.SDK_INT > 25) {
            intent = new Intent("android.intent.action.FACTORY_RESET");
            intent.setPackage(Constants.System.ANDROID_PACKAGE_NAME);
        } else {
            Intent intent2 = new Intent("android.intent.action.MASTER_CLEAR");
            try {
                str = (String) e.a((Class<?>) Intent.class, "EXTRA_REASON");
            } catch (Exception e) {
                Log.e("ConfirmAccessControl", "intent reason exception:", e);
                str = "android.intent.extra.REASON";
            }
            intent2.putExtra(str, "MasterClearConfirm");
            intent = intent2;
        }
        intent.addFlags(268435456);
        this.f3199a.sendBroadcast(intent);
    }
}
