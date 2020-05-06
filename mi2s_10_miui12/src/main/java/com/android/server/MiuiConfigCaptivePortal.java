package com.android.server;

import android.content.Context;
import android.provider.MiuiSettings;
import android.provider.Settings;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

public class MiuiConfigCaptivePortal {
    private static final String CN_CAPTIVE_PORTAL_SERVER = "connect.rom.miui.com";

    public static final URL getCaptivePortalServer(Context context, String url) {
        String server = getCaptivePortalServer((String) null);
        if (server == null && ((url == null || !url.startsWith("http")) && (server = Settings.Global.getString(context.getContentResolver(), "captive_portal_server")) == null)) {
            try {
                Field dsFiled = Class.forName("com.android.server.connectivity.NetworkMonitor").getDeclaredField("DEFAULT_SERVER");
                dsFiled.setAccessible(true);
                server = (String) dsFiled.get((Object) null);
            } catch (ClassNotFoundException e) {
                server = CN_CAPTIVE_PORTAL_SERVER;
            } catch (Exception e2) {
                server = CN_CAPTIVE_PORTAL_SERVER;
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
        return !MiuiSettings.System.isInCnRegion() ? server : CN_CAPTIVE_PORTAL_SERVER;
    }

    public static final boolean enableDataAndWifiRoam(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "data_and_wifi_roam", 0) == 1;
    }
}
