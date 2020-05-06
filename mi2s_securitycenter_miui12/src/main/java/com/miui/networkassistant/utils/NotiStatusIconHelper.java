package com.miui.networkassistant.utils;

import com.miui.securitycenter.R;

public class NotiStatusIconHelper {
    private static final int[] iconRes = {R.drawable.status_bar_flow_5, R.drawable.status_bar_flow_15, R.drawable.status_bar_flow_25, R.drawable.status_bar_flow_35, R.drawable.status_bar_flow_45, R.drawable.status_bar_flow_55, R.drawable.status_bar_flow_65, R.drawable.status_bar_flow_75, R.drawable.status_bar_flow_85, R.drawable.status_bar_flow_95, R.drawable.status_bar_flow_100, R.drawable.status_bar_flow_null};

    public static int getIconByLevel(int i) {
        return i == -1 ? iconRes[11] : iconRes[i / 10];
    }
}
