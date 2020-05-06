package com.miui.applicationlock.c;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import com.miui.common.persistence.b;
import java.util.ArrayList;
import java.util.List;

class n implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f3315a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f3316b;

    n(Context context, int i) {
        this.f3315a = context;
        this.f3316b = i;
    }

    public void run() {
        try {
            boolean z = false;
            if (!(Settings.Secure.getInt(this.f3315a.getContentResolver(), "com_miui_applicationlock_fingerprint_upgrade", 0) == 1)) {
                ArrayList<String> a2 = b.a("applock_verify_and_activate_fingerprint_" + this.f3316b, (ArrayList<String>) new ArrayList());
                E a3 = E.a(this.f3315a);
                List<Integer> b2 = a3.b();
                if (a3.d() && a3.c()) {
                    z = true;
                }
                if (!(!z || b2 == null || b2.size() == 0)) {
                    for (Integer valueOf : b2) {
                        a2.add(String.valueOf(valueOf));
                    }
                    b.b("applock_verify_and_activate_fingerprint_" + this.f3316b, a2);
                }
                Settings.Secure.putInt(this.f3315a.getContentResolver(), "com_miui_applicationlock_fingerprint_upgrade", 1);
            }
        } catch (Exception e) {
            Log.d("AppLockUtils", "upgradeFingerprints failed", e);
        }
    }
}
