package com.android.server.location;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.util.NtpTrustedTime;
import android.util.TrustedTime;
import java.lang.reflect.Field;
import java.util.ArrayList;

/* compiled from: GpsLocationProviderInjector */
class GpsNetworkTimeUpdateServiceInjector {
    private static final boolean DBG = true;
    private static final String[] NTP_SERVERS_LIST = {"cn,ntp.org.cn", "time5.aliyun.com", "time6.aliyun.com", "ntp-sz.chl.la", "time4.aliyun.com", "2.android.pool.ntp.org", "tw.ntp.org.cn", "ntp1.aliyun.com", "clock.via.org", "dns.sjtu.edu.cn"};
    private static final String TAG = "GpsNetworkTimeUpdateServiceInjector";
    private static final boolean hasServerField = true;
    private static ConnectivityManager sConnManager;
    private static String sDefaultNtpServer;
    private static ArrayList<String> sNtpServers = new ArrayList<>();
    private static Field sServerField = null;
    private static TrustedTime sTime;

    GpsNetworkTimeUpdateServiceInjector() {
    }

    static void initReflectServerField(Object reflectInstance, String strField) throws Exception {
        sServerField = reflectInstance.getClass().getDeclaredField(strField);
        sServerField.setAccessible(true);
    }

    static String getReflectServerField(Object reflectInstance) throws Exception {
        return (String) sServerField.get(reflectInstance);
    }

    static void setReflectServerField(Object reflectInstance, String strField) throws Exception {
        sServerField.set(reflectInstance, strField);
    }

    static boolean refreshNtpServer(int tryCounter) {
        try {
            setReflectServerField(sTime, sNtpServers.get(tryCounter % sNtpServers.size()));
            Log.d(TAG, "tryCounter = " + tryCounter + ",ntpServers = " + getReflectServerField(sTime));
            sTime.forceRefresh();
            setReflectServerField(sTime, sDefaultNtpServer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static void initNtpServers(Context context, TrustedTime trustedTime) {
        sTime = trustedTime;
        sConnManager = (ConnectivityManager) context.getSystemService("connectivity");
        TrustedTime trustedTime2 = sTime;
        if (trustedTime2 instanceof NtpTrustedTime) {
            try {
                initReflectServerField(trustedTime2, "mServer");
                sDefaultNtpServer = getReflectServerField(sTime);
                sNtpServers.add(sDefaultNtpServer);
                for (String ntpServer : NTP_SERVERS_LIST) {
                    sNtpServers.add(ntpServer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void switchNtpServer(int tryCounter) {
        if (!(sTime instanceof NtpTrustedTime)) {
            return;
        }
        if (sServerField == null || !refreshNtpServer(tryCounter)) {
            sTime.forceRefresh();
        }
    }
}
