package com.miui.networkassistant.traffic.statistic;

import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.utils.DateUtil;

public class LeisureTrafficHelper {
    private LeisureTrafficHelper() {
    }

    public static boolean isLeisureTime(SimUserInfo simUserInfo) {
        long currentTimeMillis = System.currentTimeMillis();
        long todayTimeMillis = DateUtil.getTodayTimeMillis();
        long leisureDataUsageFromTime = simUserInfo.getLeisureDataUsageFromTime() + todayTimeMillis;
        long leisureDataUsageToTime = simUserInfo.getLeisureDataUsageToTime() + todayTimeMillis;
        return leisureDataUsageFromTime > leisureDataUsageToTime ? currentTimeMillis < leisureDataUsageToTime || currentTimeMillis > leisureDataUsageFromTime : currentTimeMillis > leisureDataUsageFromTime && currentTimeMillis < leisureDataUsageToTime;
    }
}
