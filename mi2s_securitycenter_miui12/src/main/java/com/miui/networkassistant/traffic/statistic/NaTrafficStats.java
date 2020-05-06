package com.miui.networkassistant.traffic.statistic;

import android.content.Context;
import android.net.TrafficStats;
import android.text.TextUtils;
import b.b.c.h.f;
import miui.securitycenter.net.NetworkStatWrapper;

public class NaTrafficStats extends TrafficStats {
    private static final int TYPE_RX_BYTES = 0;
    private static final int TYPE_RX_PACKETS = 1;
    private static final int TYPE_TCP_RX_PACKETS = 4;
    private static final int TYPE_TCP_TX_PACKETS = 5;
    private static final int TYPE_TX_BYTES = 2;
    private static final int TYPE_TX_PACKETS = 3;
    private static String sMobileIface;

    static {
        System.loadLibrary("nap");
    }

    public static long getMobileBytes(Context context) {
        return TrafficStats.getMobileTxBytes() + TrafficStats.getMobileRxBytes();
    }

    public static long getMobileBytes(Context context, int i) {
        return getMobileTxBytes(context, i) + getMobileRxBytes(context, i);
    }

    private static String getMobileIface(Context context) {
        if (sMobileIface == null) {
            sMobileIface = f.f(context);
        }
        return sMobileIface;
    }

    public static long getMobileRxBytes(Context context, int i) {
        String mobileIface = getMobileIface(context);
        if (TextUtils.isEmpty(mobileIface)) {
            return -1;
        }
        return getRxBytes(i, mobileIface);
    }

    public static long getMobileTxBytes(Context context, int i) {
        String mobileIface = getMobileIface(context);
        if (TextUtils.isEmpty(mobileIface)) {
            return -1;
        }
        return getTxBytes(i, mobileIface);
    }

    public static long getRxBytes(int i, String str) {
        return nativeGetUidIfaceStat(i, str, 0);
    }

    public static long getRxBytes(String str) {
        return NetworkStatWrapper.getRxBytes(str);
    }

    public static long getTotalBytes(Context context) {
        return TrafficStats.getTotalTxBytes() + TrafficStats.getTotalRxBytes();
    }

    public static long getTxBytes(int i, String str) {
        return nativeGetUidIfaceStat(i, str, 2);
    }

    public static long getTxBytes(String str) {
        return NetworkStatWrapper.getTxBytes(str);
    }

    public static long getUidBytes(Context context, int i) {
        return TrafficStats.getUidTxBytes(i) + TrafficStats.getUidRxBytes(i);
    }

    private static native long nativeGetUidIfaceStat(int i, String str, int i2);
}
