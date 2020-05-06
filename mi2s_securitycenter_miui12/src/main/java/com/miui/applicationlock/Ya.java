package com.miui.applicationlock;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Log;
import com.miui.applicationlock.c.o;
import java.util.List;

class Ya implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ bb f3236a;

    Ya(bb bbVar) {
        this.f3236a = bbVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            Intent a2 = o.a(this.f3236a.I, "com.android.settings", "com.android.settings.faceunlock.MiuiFaceDataInput");
            List<ResolveInfo> queryIntentActivities = this.f3236a.I.getPackageManager().queryIntentActivities(a2, 65536);
            if (queryIntentActivities == null || queryIntentActivities.size() == 0) {
                Log.i("SettingLockActivity", "go to systemUI for register");
                a2 = o.a(this.f3236a.I, "com.android.systemui", "com.android.keyguard.settings.MiuiFaceDataInput");
            }
            this.f3236a.startActivityForResult(a2, 34);
        } catch (Exception e) {
            Log.e("SettingLockActivity", "start activity error: ", e);
        }
    }
}
