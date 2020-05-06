package com.miui.networkassistant.utils;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UsageStateUtil {
    public static List<String> getRecentApps(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        ArrayList<UsageStats> arrayList = new ArrayList<>(((UsageStatsManager) context.getSystemService("usagestats")).queryAndAggregateUsageStats(currentTimeMillis - 604800000, currentTimeMillis).values());
        ArrayList arrayList2 = new ArrayList();
        if (arrayList.isEmpty()) {
            return arrayList2;
        }
        Collections.sort(arrayList, new Comparator<UsageStats>() {
            public int compare(UsageStats usageStats, UsageStats usageStats2) {
                if (usageStats2.getLastTimeUsed() - usageStats.getLastTimeUsed() > 0) {
                    return 1;
                }
                return usageStats2.getLastTimeUsed() - usageStats.getLastTimeUsed() == 0 ? 0 : -1;
            }
        });
        for (UsageStats packageName : arrayList) {
            arrayList2.add(packageName.getPackageName());
        }
        return arrayList2;
    }
}
