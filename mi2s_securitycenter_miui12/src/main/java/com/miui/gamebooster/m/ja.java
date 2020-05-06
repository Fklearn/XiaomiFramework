package com.miui.gamebooster.m;

import android.text.TextUtils;
import com.miui.activityutil.o;
import com.miui.common.persistence.b;
import java.util.ArrayList;

public class ja {
    public static boolean a(String str, String str2) {
        ArrayList<String> a2;
        if (!TextUtils.isEmpty(str2) && (a2 = b.a(str, (ArrayList<String>) new ArrayList())) != null && a2.size() > 0) {
            String str3 = null;
            int size = a2.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                String str4 = a2.get(size);
                if (!TextUtils.isEmpty(str4) && str4.contains(str2)) {
                    str3 = str4;
                    break;
                }
                size--;
            }
            if (!TextUtils.isEmpty(str3) && str3.contains(",")) {
                String[] split = str3.split(",");
                if (split.length > 1) {
                    return TextUtils.equals(o.f2310b, split[1]);
                }
            }
        }
        return false;
    }
}
