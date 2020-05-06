package com.android.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.util.NtpTrustedTime;
import android.util.TrustedTime;
import java.lang.reflect.Field;
import java.util.ArrayList;
import miui.os.Build;

class NetworkTimeUpdateServiceInjector {
    private static final String[] CHINA_NTP_SERERS_LIST = {"hshh.org", "t1.hshh.org", "cn.ntp.org.cn"};
    private static final boolean DBG = true;
    private static final String[] NTP_SERVERS_LIST = {"2.android.pool.ntp.org", "time.nist.gov", "2.centos.pool.ntp.org", "asia.pool.ntp.org"};
    private static final String TAG = "NetworkTimeUpdateServiceInjector";
    private static final boolean hasServerField = true;
    private static ConnectivityManager sConnManager;
    private static String sDefaultNtpServer;
    private static ArrayList<String> sNtpServers = new ArrayList<>();
    private static Field sServerField = null;
    private static TrustedTime sTime;

    NetworkTimeUpdateServiceInjector() {
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
            sTime.forceSync();
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
                if (!Build.IS_GLOBAL_BUILD) {
                    for (String ntpServer2 : CHINA_NTP_SERERS_LIST) {
                        sNtpServers.add(ntpServer2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static boolean isNetChangedEvent(int event, int netEvent) {
        return event == netEvent;
    }

    static void switchNtpServer(int tryCounter) {
        if (!(sTime instanceof NtpTrustedTime)) {
            return;
        }
        if (sServerField == null || !refreshNtpServer(tryCounter)) {
            sTime.forceSync();
        }
    }

    static boolean isDataNetworkReady() {
        return isNetworkConnected();
    }

    static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = sConnManager;
        NetworkInfo netInfo = connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        Log.d(TAG, "network isn't ok");
        return false;
    }

    static void sendHandlerMessage(Handler handler, int event) {
        if (handler == null) {
            Log.d(TAG, "handler null");
        } else {
            handler.obtainMessage(event).sendToTarget();
        }
    }

    static void handleNetworkChanged(Context context, Handler handler, int event) {
        if (sConnManager == null) {
            sConnManager = (ConnectivityManager) context.getSystemService("connectivity");
        }
        if (isNetworkConnected()) {
            Log.d(TAG, "network ok,send msg to sync time");
            sendHandlerMessage(handler, event);
        }
    }
}
