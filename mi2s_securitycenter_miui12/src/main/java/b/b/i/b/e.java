package b.b.i.b;

import com.miui.analytics.AnalyticsUtil;
import com.miui.luckymoney.config.Constants;
import com.xiaomi.stat.MiStat;
import java.util.HashMap;

public class e {
    public static void a(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("enter_way", str);
        AnalyticsUtil.recordCountEvent("storage_space", "channel", hashMap);
    }

    public static void b(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put(Constants.JSON_KEY_MODULE, str);
        AnalyticsUtil.recordCountEvent("storage_space", MiStat.Event.CLICK, hashMap);
    }
}
