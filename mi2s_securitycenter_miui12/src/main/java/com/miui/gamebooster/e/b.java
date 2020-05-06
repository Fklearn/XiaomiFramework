package com.miui.gamebooster.e;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.miui.gamebooster.m.C0374e;
import com.miui.securitycenter.n;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class b {
    private static String a(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String str = list.get(i);
            if (str != null) {
                sb.append(str);
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static void a(Context context) {
        boolean z = context.getSharedPreferences("gb_gamead_data_config", 0).getBoolean("is_never_send_top_games", true);
        if (!z) {
            Log.i("TopGameUtils", "saveTopGamesForFirst: neverSend=" + z);
            return;
        }
        n.a().b(new a(context));
    }

    private static void a(Context context, String str, boolean z) {
        if (!TextUtils.isEmpty(str)) {
            try {
                Intent intent = new Intent("com.miui.securitycenter.intent.action.TOP_GAME_LIST");
                intent.putExtra("gameList", str);
                intent.putExtra("isAppend", z);
                intent.setPackage("com.miui.powerkeeper");
                context.sendBroadcast(intent, "com.miui.securitycenter.permission.TOP_GAME_LIST");
            } catch (Exception unused) {
            }
        }
    }

    public static void a(Context context, List<String> list) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("gb_gamead_data_config", 0);
            boolean z = sharedPreferences.getBoolean("is_never_send_top_games", true);
            if (z || Settings.Secure.getInt(context.getContentResolver(), "support_highfps", 0) == 1) {
                ArrayList arrayList = new ArrayList();
                if (list != null && !list.isEmpty()) {
                    arrayList.addAll(list);
                }
                for (String next : b(context)) {
                    if (!arrayList.contains(next)) {
                        arrayList.add(next);
                    }
                }
                int size = arrayList.size();
                int i = 0;
                boolean z2 = false;
                while (i < size) {
                    int i2 = i + 50;
                    String a2 = a((List<String>) arrayList.subList(i, i2 > size ? size : i2));
                    if (!TextUtils.isEmpty(a2)) {
                        a(context, a2, z2);
                        if (!z2) {
                            z2 = true;
                        }
                    }
                    i = i2;
                }
                if (z) {
                    sharedPreferences.edit().putBoolean("is_never_send_top_games", false).commit();
                }
                Log.i("TopGameUtils", "saveTopGamesToPk: " + size);
                return;
            }
            Log.i("TopGameUtils", "joyose not support!!!");
        } catch (Exception e) {
            Log.e("TopGameUtils", "save data to power keeper failed" + e);
        }
    }

    private static List<String> b(Context context) {
        ArrayList arrayList = new ArrayList();
        try {
            JSONArray optJSONArray = new JSONObject(C0374e.a(context, "top_200_games.json")).optJSONArray("packageNames");
            int length = optJSONArray.length();
            for (int i = 0; i < length; i++) {
                String optString = optJSONArray.optString(i);
                if (optString != null) {
                    arrayList.add(optString);
                }
            }
        } catch (Exception unused) {
        }
        arrayList.add("com.tencent.af");
        arrayList.add("com.t2ksports.nba2k19and");
        arrayList.add("com.t2ksports.nba2k20and");
        arrayList.add("com.t2ksports.nba2k18and");
        arrayList.add("com.miHoYo.enterprise.NGHSoD");
        arrayList.add("com.miHoYo.bh3.uc");
        arrayList.add("com.netease.lx12.mi");
        arrayList.add("com.netease.lx12");
        arrayList.add("com.netease.mrzh");
        return arrayList;
    }
}
