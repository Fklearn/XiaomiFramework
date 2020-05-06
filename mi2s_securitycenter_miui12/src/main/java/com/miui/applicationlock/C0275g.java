package com.miui.applicationlock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Log;
import com.miui.applicationlock.c.o;
import java.util.List;

/* renamed from: com.miui.applicationlock.g  reason: case insensitive filesystem */
class C0275g implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0312y f3348a;

    C0275g(C0312y yVar) {
        this.f3348a = yVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            Intent a2 = o.a((Context) this.f3348a.z, "com.android.settings", "com.android.settings.faceunlock.MiuiFaceDataInput");
            List<ResolveInfo> queryIntentActivities = this.f3348a.z.getPackageManager().queryIntentActivities(a2, 65536);
            if (queryIntentActivities == null || queryIntentActivities.size() == 0) {
                Log.i("AppLockManageFragment", "go to systemUI for register");
                a2 = o.a((Context) this.f3348a.z, "com.android.systemui", "com.android.keyguard.settings.MiuiFaceDataInput");
            }
            this.f3348a.startActivityForResult(a2, 34);
        } catch (Exception e) {
            Log.e("AppLockManageFragment", "start activity error: ", e);
        }
    }
}
