package com.miui.permcenter.privacymanager.b;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.miui.common.persistence.b;
import com.miui.securityscan.c.a;
import com.miui.support.provider.MiuiSettingsCompat$SettingsCloudData;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;

public class s {
    public static int a() {
        return b.a("sensitivePermissionNotificationPkgCnt", 1);
    }

    public static void a(Context context) {
        Log.i("SensitivePermissionCloudControlHelper", "start loadCloudControlSettings");
        if (c.a(context)) {
            b(context);
        }
    }

    public static int b() {
        return b.a("sensitivePermissionNotificationTotalCnt", 2);
    }

    private static void b(Context context) {
        if (a.f7625a) {
            Log.i("SensitivePermissionCloudControlHelper", "loadCloudControlData");
        }
        try {
            int a2 = MiuiSettingsCompat$SettingsCloudData.a(context.getContentResolver(), "SensitivePermission", "sensitivePermissionNotificationPkgCnt", 1);
            int a3 = MiuiSettingsCompat$SettingsCloudData.a(context.getContentResolver(), "SensitivePermission", "sensitivePermissionNotificationTotalCnt", 2);
            String a4 = MiuiSettingsCompat$SettingsCloudData.a(context.getContentResolver(), "SensitivePermission", "sensitivePermissionNotificationWhiteList", "");
            ArrayList arrayList = new ArrayList();
            try {
                arrayList = (ArrayList) new Gson().fromJson(new JSONArray(a4).toString(), new r().getType());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e2) {
                e2.printStackTrace();
            }
            if (a.f7625a) {
                Log.i("SensitivePermissionCloudControlHelper", "pkgCnt: " + a2);
                Log.i("SensitivePermissionCloudControlHelper", "totalCnt: " + a3);
                Log.i("SensitivePermissionCloudControlHelper", "list: " + a4);
            }
            b.b("sensitivePermissionNotificationTotalCnt", a3);
            b.b("sensitivePermissionNotificationPkgCnt", a2);
            b.b("sensitivePermissionNotificationWhiteList", (ArrayList<String>) arrayList);
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    public static ArrayList<String> c() {
        return b.a("sensitivePermissionNotificationWhiteList", (ArrayList<String>) new ArrayList());
    }
}
