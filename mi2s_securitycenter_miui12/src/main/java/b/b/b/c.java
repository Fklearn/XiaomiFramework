package b.b.b;

import android.content.Context;
import android.util.Log;
import com.miui.common.persistence.b;
import com.miui.permcenter.compact.MiuiSettingsCompat;
import com.miui.securityscan.c.a;
import miui.os.Build;

public class c {
    public static void a(Context context) {
        Log.i("CloudControlManager", "start loadCloudControlSettings");
        if (!Build.IS_INTERNATIONAL_BUILD) {
            c(context);
        }
        b(context);
    }

    private static void b(Context context) {
        if (a.f7625a) {
            Log.i("CloudControlManager", "loadCloudControlScanWhiteList");
        }
        try {
            String cloudDataString = MiuiSettingsCompat.getCloudDataString(context.getContentResolver(), "scan_white_list", "scanWhiteList", (String) null);
            String cloudDataString2 = MiuiSettingsCompat.getCloudDataString(context.getContentResolver(), "scan_white_list", "installWhiteList", (String) null);
            if (a.f7625a) {
                Log.i("CloudControlManager", "scanWhiteList: " + cloudDataString);
                Log.i("CloudControlManager", "installWhiteList: " + cloudDataString2);
            }
            b.b("key_scan_white_list", cloudDataString);
            b.b("key_install_white_list", cloudDataString2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void c(Context context) {
        if (a.f7625a) {
            Log.i("CloudControlManager", "loadCloudControlSidekick");
        }
        try {
            int cloudDataInt = MiuiSettingsCompat.getCloudDataInt(context.getContentResolver(), "antivirus_assist", "sidekickInterval", 24);
            boolean cloudDataBoolean = MiuiSettingsCompat.getCloudDataBoolean(context.getContentResolver(), "antivirus_assist", "sidekickStatus", true);
            if (a.f7625a) {
                Log.i("CloudControlManager", "interval: " + cloudDataInt);
                Log.i("CloudControlManager", "isOpen: " + cloudDataBoolean);
            }
            b.b("side_kick_interval", ((long) cloudDataInt) * 60 * 60 * 1000);
            b.b("side_kick_status", cloudDataBoolean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
