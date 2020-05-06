package com.miui.gamebooster.m;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;

public class H {
    public static Map<String, Integer> a(Context context) {
        HashMap hashMap = new HashMap();
        SharedPreferences sharedPreferences = context.getSharedPreferences("game_booster_limit", 0);
        hashMap.put("game_booster_limit_speed", Integer.valueOf(sharedPreferences.getInt("game_booster_limit_speed", 131072)));
        hashMap.put("game_booster_limit_time", Integer.valueOf(sharedPreferences.getInt("game_booster_limit_time", 30000)));
        hashMap.put("game_booster_limit_query", Integer.valueOf(sharedPreferences.getInt("game_booster_limit_query", 86400000)));
        hashMap.put("game_booster_close_service_time", Integer.valueOf(sharedPreferences.getInt("game_booster_close_service_time", 600000)));
        return hashMap;
    }

    public static void a(Context context, int i, int i2, int i3, int i4) {
        SharedPreferences.Editor edit = context.getSharedPreferences("game_booster_limit", 0).edit();
        edit.putInt("game_booster_limit_speed", i * 1024);
        edit.putInt("game_booster_limit_time", i2 * 1000);
        edit.putInt("game_booster_limit_query", i3 * 1000);
        edit.putInt("game_booster_close_service_time", i4 * 1000);
        edit.commit();
    }
}
