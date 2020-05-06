package android.net.shared;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import miui.os.Build;
import miui.telephony.TelephonyManager;

public class MiuiConfigCaptivePortal {
    private static final String CN_CAPTIVE_PORTAL_SERVER = "connect.rom.miui.com";
    private static final String CN_OPERATOR = "460";
    private static final String TAG = "MiuiConfigCaptivePortal";

    public static final URL getCaptivePortalServer(Context context, String url) {
        String server = getCaptivePortalServer((String) null);
        if (server == null && (url == null || !url.startsWith("http"))) {
            server = Settings.Global.getString(context.getContentResolver(), "captive_portal_server");
            if (server == null) {
                try {
                    Field dsFiled = Class.forName("com.android.server.connectivity.NetworkMonitor").getDeclaredField("DEFAULT_SERVER");
                    dsFiled.setAccessible(true);
                    server = (String) dsFiled.get((Object) null);
                } catch (ClassNotFoundException e) {
                    server = CN_CAPTIVE_PORTAL_SERVER;
                } catch (Exception e2) {
                    server = CN_CAPTIVE_PORTAL_SERVER;
                }
            } else {
                Log.d(TAG, "Can't get Settings.Global.CAPTIVE_PORTAL_SERVER");
            }
        }
        if (server == null) {
            return new URL(url);
        }
        try {
            return new URL("http", server, "/generate_204");
        } catch (MalformedURLException e3) {
            return null;
        }
    }

    public static final String getCaptivePortalServer(String server) {
        return !isInCnRegion() ? server : CN_CAPTIVE_PORTAL_SERVER;
    }

    private static final boolean isInCnRegion() {
        TelephonyManager telephonyManager = TelephonyManager.getDefault();
        boolean isExistIccCard = telephonyManager.getIccCardCount() > 0;
        int numPhones = telephonyManager.getPhoneCount();
        String networkOperator = null;
        if (isExistIccCard) {
            for (int i = 0; i < numPhones; i++) {
                networkOperator = telephonyManager.getNetworkOperatorForSlot(i);
                if (isCnFromOperator(networkOperator)) {
                    return true;
                }
            }
        }
        if (!Build.checkRegion("CN")) {
            return false;
        }
        if (!isExistIccCard || TextUtils.isEmpty(networkOperator)) {
            return true;
        }
        return false;
    }

    private static boolean isCnFromOperator(String operator) {
        String mcc = "";
        if (!TextUtils.isEmpty(operator) && operator.length() >= 3) {
            mcc = operator.substring(0, 3);
        }
        return CN_OPERATOR.equals(mcc);
    }
}
