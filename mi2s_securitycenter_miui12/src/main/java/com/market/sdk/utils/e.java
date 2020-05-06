package com.market.sdk.utils;

import android.os.Build;
import android.util.Log;

public class e {
    public static boolean a(String str) {
        try {
            if (!str.matches("\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}(-internal)?")) {
                return false;
            }
            String str2 = Build.VERSION.INCREMENTAL;
            if (!str2.matches("\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}(-internal)?")) {
                return false;
            }
            String replace = str2.replace("-internal", "");
            String replace2 = str.replace("-internal", "");
            String[] split = replace.split("\\.");
            String[] split2 = replace2.split("\\.");
            long parseLong = (Long.parseLong(split[0]) * 10000) + (Long.parseLong(split[1]) * 100) + Long.parseLong(split[2]);
            long parseLong2 = Long.parseLong(split2[0]) * 10000;
            long parseLong3 = Long.parseLong(split2[1]);
            Long.signum(parseLong3);
            return parseLong >= (parseLong2 + (parseLong3 * 100)) + Long.parseLong(split2[2]);
        } catch (Throwable th) {
            Log.d("MarketManager", th.toString());
            return false;
        }
    }

    public static boolean b(String str) {
        String str2 = str;
        try {
            if (!str2.matches("V\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}")) {
                return false;
            }
            String str3 = Build.VERSION.INCREMENTAL;
            if (!str3.matches("V\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}\\..*")) {
                return false;
            }
            String[] split = str3.split("\\.");
            String[] split2 = str2.split("\\.");
            long parseLong = (Long.parseLong(split[0].substring(1)) * 1000000) + (Long.parseLong(split[1]) * 10000) + (Long.parseLong(split[2]) * 100) + Long.parseLong(split[3]);
            long parseLong2 = Long.parseLong(split2[0].substring(1)) * 1000000;
            long parseLong3 = Long.parseLong(split2[1]);
            Long.signum(parseLong3);
            return parseLong >= ((parseLong2 + (parseLong3 * 10000)) + (Long.parseLong(split2[2]) * 100)) + Long.parseLong(split2[3]);
        } catch (Throwable th) {
            Log.d("MarketManager", th.toString());
            return false;
        }
    }
}
