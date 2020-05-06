package com.miui.powercenter.d;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.provider.Settings;
import android.util.Log;
import b.b.c.j.B;
import b.b.o.g.e;
import java.util.List;
import miui.os.SystemProperties;

public class a implements d {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f6987a = SystemProperties.getBoolean("ro.hardware.fp.fod", false);

    private boolean a() {
        return f6987a;
    }

    public void a(Context context) {
        if (a() && c(context) > 0 && B.j() == 0) {
            synchronized (a.class) {
                int i = Settings.System.getInt(context.getContentResolver(), "power_center_finger_aod", -1);
                if (i != -1) {
                    if (i != Settings.Secure.getInt(context.getContentResolver(), "gxzw_icon_aod_show_enable", 0)) {
                        Settings.Secure.putInt(context.getContentResolver(), "gxzw_icon_aod_show_enable", i);
                    }
                    Settings.System.putInt(context.getContentResolver(), "power_center_finger_aod", -1);
                }
            }
        }
    }

    public void b(Context context) {
        if (a() && c(context) > 0 && B.j() == 0) {
            synchronized (a.class) {
                int i = Settings.System.getInt(context.getContentResolver(), "power_center_finger_aod", -1);
                int i2 = Settings.Secure.getInt(context.getContentResolver(), "gxzw_icon_aod_show_enable", 1);
                if (i == -1) {
                    Settings.System.putInt(context.getContentResolver(), "power_center_finger_aod", i2);
                }
                if (i2 != 0) {
                    Settings.Secure.putInt(context.getContentResolver(), "gxzw_icon_aod_show_enable", 0);
                }
            }
        }
    }

    public int c(Context context) {
        FingerprintManager fingerprintManager = (FingerprintManager) context.getApplicationContext().getSystemService("fingerprint");
        if (fingerprintManager != null) {
            try {
                List list = (List) e.a((Object) fingerprintManager, List.class, "getEnrolledFingerprints", (Class<?>[]) null, new Object[0]);
                if (list != null && !list.isEmpty()) {
                    return list.size();
                }
            } catch (Exception e) {
                Log.e("FingerAodPowerMode", "getEnrolledFingerprintsCount exception: ", e);
            }
        }
        return 0;
    }
}
