package com.miui.appcompatibility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import b.b.c.h.j;
import com.miui.appcompatibility.data.AppCompatibilityData;
import com.miui.appcompatibility.data.PackageData;
import com.miui.luckymoney.config.Constants;
import com.miui.securityscan.i.k;
import com.xiaomi.stat.a.l;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class n {

    /* renamed from: a  reason: collision with root package name */
    private static final String f3097a = (j.a() ? "https://api.sec.intl.miui.com/app/c/r" : "https://api.sec.miui.com/app/c/r");

    private static AppCompatibilityData a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        AppCompatibilityData appCompatibilityData = new AppCompatibilityData();
        ArrayList arrayList = new ArrayList();
        JSONObject jSONObject = new JSONObject(str);
        String optString = jSONObject.optString(Constants.JSON_KEY_DEVICE);
        String optString2 = jSONObject.optString("osver");
        int optInt = jSONObject.optInt("total");
        JSONArray jSONArray = jSONObject.getJSONArray("pkgs");
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
            PackageData packageData = new PackageData();
            packageData.setPkg(jSONObject2.optString("pkg"));
            packageData.setVer(jSONObject2.optString("ver"));
            packageData.setStatus(jSONObject2.optInt(l.a.B));
            arrayList.add(packageData);
        }
        appCompatibilityData.setDevice(optString);
        appCompatibilityData.setOsver(optString2);
        appCompatibilityData.setTotal(optInt);
        appCompatibilityData.setPkgs(arrayList);
        return appCompatibilityData;
    }

    public static AppCompatibilityData a(Map<String, String> map) {
        if (j.a()) {
            return null;
        }
        String valueOf = String.valueOf(Build.VERSION.SDK_INT);
        map.put(Constants.JSON_KEY_DEVICE, Build.DEVICE.toString());
        map.put("osver", valueOf);
        try {
            return a(k.a(map, f3097a, "5cdd8678-cddf-4259-ab73-48387445bba8", new j("appcompatibility_getalllistdata")));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean a() {
        Locale locale = Locale.getDefault();
        return "zh".equals(locale.getLanguage()) && "cn".equals(locale.getCountry().toLowerCase());
    }

    public static boolean a(Context context) {
        NetworkInfo activeNetworkInfo;
        if (context == null || (activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo()) == null) {
            return false;
        }
        return activeNetworkInfo.isAvailable();
    }

    public static AppCompatibilityData b(Map<String, String> map) {
        if (j.a()) {
            return null;
        }
        String valueOf = String.valueOf(Build.VERSION.SDK_INT);
        map.put(Constants.JSON_KEY_DEVICE, Build.DEVICE.toString());
        map.put("osver", valueOf);
        try {
            return a(k.a(map, f3097a, "5cdd8678-cddf-4259-ab73-48387445bba8", new j("appcompatibility_requestcompatibledata")));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
