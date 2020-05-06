package com.miui.gamebooster.o.a;

import android.content.ContentResolver;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.e;
import com.miui.common.persistence.b;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

public class a {
    private static String a(Context context, String str, String str2, String str3) {
        return (String) e.a(Class.forName("android.provider.MiuiSettings$SettingsCloudData"), String.class, "getCloudDataString", (Class<?>[]) new Class[]{ContentResolver.class, String.class, String.class, String.class}, context.getContentResolver(), str, str2, str3);
    }

    private static String a(String str) {
        JSONArray jSONArray = new JSONArray(str);
        String str2 = null;
        for (int i = 0; i < jSONArray.length(); i++) {
            String optString = jSONArray.optString(i);
            if (!TextUtils.isEmpty(optString)) {
                str2 = str2 == null ? optString : str2.concat(",").concat(optString);
            }
        }
        return str2;
    }

    public static List<String> a(Context context) {
        String a2 = b.a("vc_not_support_apps", "");
        ArrayList arrayList = new ArrayList();
        if (!TextUtils.isEmpty(a2)) {
            if (a2.contains(",")) {
                String[] split = a2.split(",");
                for (String add : split) {
                    arrayList.add(add);
                }
            } else {
                arrayList.add(a2);
            }
        }
        return arrayList;
    }

    public static void b(Context context) {
        try {
            String a2 = a(a(context, "GbVoiceChangerAppsSettings", "NotSupportVcApps", ""));
            if (!TextUtils.isEmpty(a2)) {
                b.b("vc_not_support_apps", a2);
            }
        } catch (Exception e) {
            Log.e("VoiceChangerCloudHelper", "loadNotSupportVcApps failed ", e);
        }
    }
}
