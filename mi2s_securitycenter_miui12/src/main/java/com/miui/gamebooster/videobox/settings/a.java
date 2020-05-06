package com.miui.gamebooster.videobox.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.e;
import com.miui.gamebooster.m.C0382m;
import java.util.ArrayList;
import org.json.JSONArray;

public class a {
    private static String a(Context context, String str, String str2, String str3) {
        return (String) e.a(Class.forName("android.provider.MiuiSettings$SettingsCloudData"), String.class, "getCloudDataString", (Class<?>[]) new Class[]{ContentResolver.class, String.class, String.class, String.class}, context.getContentResolver(), str, str2, str3);
    }

    private static ArrayList<String> a(String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (TextUtils.isEmpty(str)) {
            return arrayList;
        }
        JSONArray jSONArray = new JSONArray(str);
        for (int i = 0; i < jSONArray.length(); i++) {
            String optString = jSONArray.optString(i);
            if (!TextUtils.isEmpty(optString)) {
                arrayList.add(optString);
            }
        }
        return arrayList;
    }

    public static void a(Context context) {
        Log.i("CloudControlHelper", "loadPicOptApps");
        try {
            ArrayList<String> a2 = a(a(context, "GbVtbAppListSettings", "SupportVppApps", ""));
            Log.i("CloudControlHelper", "vppList: " + a2);
            if (!a2.isEmpty()) {
                f.c(a2);
            }
        } catch (Exception e) {
            Log.e("CloudControlHelper", "loadPicOptApps failed : " + e.toString());
        }
    }

    public static void b(Context context) {
        Log.i("CloudControlHelper", "loadCloudVideoApps");
        try {
            ArrayList<String> a2 = a(a(context, "GbVtbAppListSettings", "VideoApps", ""));
            Log.i("CloudControlHelper", "loadCloudVideoApps: " + a2);
            if (!a2.isEmpty()) {
                C0382m.a("gamebooster", "vtb_net_support_apps", a2, context);
            }
        } catch (Exception e) {
            Log.e("CloudControlHelper", "loadCloudVideoApps failed : " + e.toString());
        }
    }
}
