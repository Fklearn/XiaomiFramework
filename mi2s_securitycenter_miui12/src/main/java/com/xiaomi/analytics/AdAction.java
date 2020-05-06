package com.xiaomi.analytics;

import android.text.TextUtils;
import java.util.List;

public class AdAction extends TrackAction {
    public AdAction(String str) {
        c("ad");
        b(str);
    }

    public AdAction(String str, String str2) {
        c(str);
        b(str2);
    }

    public AdAction a(List<String> list) {
        if (list != null) {
            StringBuilder sb = new StringBuilder();
            for (String next : list) {
                if (!TextUtils.isEmpty(next)) {
                    if (sb.length() > 0) {
                        sb.append("|");
                    }
                    sb.append(next);
                }
            }
            if (sb.length() > 0) {
                a("_ad_monitor_", sb.toString());
            }
        }
        return this;
    }
}
