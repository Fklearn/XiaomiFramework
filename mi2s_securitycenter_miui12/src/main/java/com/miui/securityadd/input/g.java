package com.miui.securityadd.input;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import b.b.m.a;
import b.b.o.g.e;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import miui.os.SystemProperties;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private static boolean f7456a = true;

    public static int a() {
        try {
            return ((Integer) e.a(Class.forName("android.inputmethodservice.MiuiBottomConfig"), Integer.TYPE, "getMiuiBottomVersion", (Class<?>[]) new Class[0], new Object[0])).intValue();
        } catch (Exception e) {
            Log.e("InputUtils", "Can not find MIUI_BOTTOM_VERSION. Maybe the frameworks is not latest.", e);
            return 0;
        }
    }

    public static Bundle a(Context context, Bundle bundle) {
        Log.d("InputUtils", "close clipboard tips.");
        int i = bundle.getInt("tipsValue");
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean("putSuccess", Boolean.valueOf(Settings.Secure.putInt(context.getContentResolver(), "clipboard_expired_tips_need_to_show", i)).booleanValue());
        return bundle2;
    }

    private static Bundle a(Context context, String str, String str2) {
        return b(context, Base64.encodeToString(str.getBytes(), 0), str2);
    }

    public static String a(Context context, String str) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str, 0);
            return packageInfo != null ? packageInfo.versionName : "";
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("InputUtils", "get " + str + " version error." + e);
            return "";
        }
    }

    private static String a(Context context, String str, int i, String str2) {
        JSONArray jSONArray = new JSONArray();
        ArrayList<a> b2 = b(context, str, i, str2);
        int i2 = 0;
        while (i2 < b2.size()) {
            try {
                jSONArray.put(b2.get(i2).d());
                i2++;
            } catch (JSONException e) {
                Log.e("InputUtils", "saveContentToProvider,bean to JSONObject error.", e);
            }
        }
        return jSONArray.toString();
    }

    private static ArrayList<a> a(String str) {
        ArrayList<a> arrayList = new ArrayList<>();
        if (!TextUtils.isEmpty(str)) {
            try {
                JSONArray jSONArray = new JSONArray(str);
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    if (jSONObject != null) {
                        arrayList.add(a.a(jSONObject));
                    }
                }
            } catch (JSONException e) {
                Log.e("InputUtils", "jsonToBeanList,parse json error.", e);
            }
        }
        return arrayList;
    }

    public static void a(Context context) {
        Boolean valueOf = Boolean.valueOf(Settings.Secure.putString(context.getContentResolver(), "KEY_CLIPBOARD_LIST", ""));
        Boolean valueOf2 = Boolean.valueOf(Settings.Secure.putString(context.getContentResolver(), "KEY_CLIPBOARD_LIST_NEW", ""));
        Boolean valueOf3 = Boolean.valueOf(Settings.Secure.putString(context.getContentResolver(), "cloud_clipboard_content_saved", ""));
        if (valueOf.booleanValue() && valueOf2.booleanValue() && valueOf3.booleanValue()) {
            Log.d("InputUtils", "clear old clipboard content success.");
            Settings.Secure.putInt(context.getContentResolver(), "old_clipboard_content_need_clear", 1);
        }
    }

    public static void a(Context context, String str, boolean z, boolean z2) {
        if (!c() || !i(context) || !z || !z2 || !f7456a) {
            b(context, str);
            return;
        }
        Log.d("InputUtils", "save cloud clipboard content to provider.");
        if (InputProvider.f7446c >= 1) {
            b(context, str);
        }
        Settings.Secure.putString(context.getContentResolver(), "cloud_clipboard_cipher_content_saved", Base64.encodeToString(str.getBytes(), 0));
        String string = Settings.Secure.getString(context.getContentResolver(), "clipboard_cipher_list");
        a(context, a(context, str, 1, !TextUtils.isEmpty(string) ? new String(Base64.decode(string.getBytes(), 0)) : ""), "clipboard_cipher_list");
    }

    public static void a(Bundle bundle) {
        String string = bundle.getString("clickKey");
        String string2 = bundle.getString("clickValue");
        String string3 = bundle.getString("recordKey");
        HashMap hashMap = new HashMap(1);
        hashMap.put(string, string2);
        a.a(string3, (Map<String, String>) hashMap);
    }

    public static boolean a(String str, Context context, ContentProvider contentProvider) {
        if (TextUtils.equals(contentProvider.getCallingPackage(), d(context))) {
            return true;
        }
        Log.e("InputUtils", str + ", callingPackage is " + contentProvider.getCallingPackage());
        return false;
    }

    public static Bundle b(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "clipboard_cipher_list");
        String str = !TextUtils.isEmpty(string) ? new String(Base64.decode(string.getBytes(), 0)) : "";
        Bundle bundle = new Bundle();
        bundle.putString("savedClipboard", str);
        return bundle;
    }

    public static Bundle b(Context context, Bundle bundle) {
        String jSONArray;
        Bundle bundle2 = new Bundle();
        if (bundle.containsKey("jsonArray")) {
            jSONArray = bundle.getString("jsonArray", "");
        } else {
            if (bundle.containsKey("contentToAdd")) {
                String string = bundle.getString("contentToAdd", "");
                if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string.trim())) {
                    Log.i("InputUtils", "ClipText can't only contains blank space.");
                } else {
                    ArrayList<String> n = n(context);
                    ArrayList arrayList = new ArrayList();
                    String a2 = h.a(string);
                    arrayList.add(a2);
                    int min = Math.min(20, n.size());
                    for (int i = 0; i < min; i++) {
                        if (!TextUtils.equals(a2, n.get(i))) {
                            arrayList.add(n.get(i));
                        }
                    }
                    JSONArray jSONArray2 = new JSONArray();
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        jSONArray2.put(arrayList.get(i2));
                    }
                    jSONArray = jSONArray2.toString();
                }
            }
            return bundle2;
        }
        return b(context, jSONArray, "KEY_CLIPBOARD_LIST");
    }

    private static Bundle b(Context context, String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("putSuccess", Boolean.valueOf(Settings.Secure.putString(context.getContentResolver(), str2, str)).booleanValue());
        return bundle;
    }

    public static String b() {
        return Locale.getDefault().toString();
    }

    private static ArrayList<a> b(Context context, String str, int i, String str2) {
        ArrayList<a> arrayList = new ArrayList<>();
        arrayList.add(new a(str, i, System.currentTimeMillis()));
        ArrayList<a> a2 = a(str2);
        Log.d("InputUtils", "get savedList size : " + a2.size());
        for (int i2 = 0; i2 < a2.size(); i2++) {
            String a3 = a2.get(i2).a();
            int c2 = a2.get(i2).c();
            long b2 = a2.get(i2).b();
            if (c2 == 1) {
                if (i != 1) {
                    if (!TextUtils.equals(str, a3)) {
                        arrayList.add(0, new a(a3, c2, b2));
                    } else {
                        Settings.Secure.putString(context.getContentResolver(), "cloud_clipboard_cipher_content_saved", "");
                    }
                }
            } else if (TextUtils.equals(str, a3)) {
                continue;
            } else {
                arrayList.add(new a(a3, c2, b2));
                if (arrayList.size() >= 20) {
                    break;
                }
            }
        }
        return arrayList;
    }

    private static void b(Context context, String str) {
        Log.d("InputUtils", "send cloud content to system clipboard.");
        new Handler(Looper.getMainLooper()).post(new f(context, str));
    }

    public static void b(Context context, String str, boolean z, boolean z2) {
        if (!c() || !i(context) || !z || !z2) {
            b(context, str);
            return;
        }
        Log.d("InputUtils", "save cloud clipboard content to provider.");
        Settings.Secure.putString(context.getContentResolver(), "cloud_clipboard_content_saved", str);
        b(context, a(context, str, 1, Settings.Secure.getString(context.getContentResolver(), "KEY_CLIPBOARD_LIST_NEW")), "KEY_CLIPBOARD_LIST_NEW");
    }

    private static boolean b(String str) {
        try {
            JSONArray jSONArray = new JSONArray(str);
            return jSONArray.length() <= 0 || ((Integer) jSONArray.getJSONObject(0).get("type")).intValue() != 1;
        } catch (JSONException e) {
            Log.e("InputUtils", "JSONArray parse error.", e);
        }
    }

    public static Bundle c(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString("cloudContent", m(context));
        return bundle;
    }

    public static Bundle c(Context context, Bundle bundle) {
        Bundle bundle2 = new Bundle();
        String str = "";
        if (bundle.containsKey("jsonArray")) {
            String string = bundle.getString("jsonArray", str);
            if (b(string)) {
                Settings.Secure.putString(context.getContentResolver(), "cloud_clipboard_cipher_content_saved", str);
            }
            return a(context, string, "clipboard_cipher_list");
        }
        if (bundle.containsKey("contentToAdd")) {
            String string2 = bundle.getString("contentToAdd", str);
            String string3 = bundle.getString("contentLabel", str);
            int i = bundle.getInt("contentType", 0);
            if (TextUtils.isEmpty(string2) || TextUtils.isEmpty(string2.trim())) {
                Log.i("InputUtils", "ClipText can't only contains blank space.");
            } else if (TextUtils.equals(string3, string2) && TextUtils.equals(string3, m(context))) {
                return bundle2;
            } else {
                String a2 = h.a(string2);
                String string4 = Settings.Secure.getString(context.getContentResolver(), "clipboard_cipher_list");
                if (!TextUtils.isEmpty(string4)) {
                    str = new String(Base64.decode(string4.getBytes(), 0));
                }
                return a(context, a(context, a2, i, str), "clipboard_cipher_list");
            }
        }
        return bundle2;
    }

    public static boolean c() {
        return 1 == SystemProperties.getInt("ro.miui.support_miui_ime_bottom", 0);
    }

    public static Bundle d(Context context, Bundle bundle) {
        String a2;
        Bundle bundle2 = new Bundle();
        if (bundle.containsKey("jsonArray")) {
            a2 = bundle.getString("jsonArray", "");
        } else {
            if (bundle.containsKey("contentToAdd")) {
                String string = bundle.getString("contentToAdd", "");
                if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string.trim())) {
                    Log.i("InputUtils", "ClipText can't only contains blank space.");
                } else {
                    a2 = a(context, h.a(string), 0, Settings.Secure.getString(context.getContentResolver(), "KEY_CLIPBOARD_LIST_NEW"));
                }
            }
            return bundle2;
        }
        return b(context, a2, "KEY_CLIPBOARD_LIST_NEW");
    }

    public static String d(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "default_input_method");
        return !TextUtils.isEmpty(string) ? string.substring(0, string.indexOf(47)) : "";
    }

    public static String e(Context context) {
        String d2 = d(context);
        String a2 = a(context, d2);
        return d2 + ":" + a2;
    }

    public static Boolean f(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "full_screen_keyboard_left_function");
        String string2 = Settings.Secure.getString(context.getContentResolver(), "full_screen_keyboard_right_function");
        return Boolean.valueOf(TextUtils.isEmpty(string2) || TextUtils.equals(string, "clipboard_phrase") || TextUtils.equals(string2, "clipboard_phrase"));
    }

    public static String g(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "full_screen_keyboard_left_function");
        return TextUtils.isEmpty(string) ? "switch_input_method" : string;
    }

    public static String h(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "full_screen_keyboard_right_function");
        return TextUtils.isEmpty(string) ? "clipboard_phrase" : string;
    }

    public static boolean i(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "enable_miui_ime_bottom_view", 1) != 0;
    }

    public static boolean j(Context context) {
        if (context != null) {
            try {
                Class<?> cls = Class.forName("android.provider.MiuiSettings$Global");
                Field declaredField = cls.getDeclaredField("FORCE_FSG_NAV_BAR");
                return ((Boolean) cls.getDeclaredMethod("getBoolean", new Class[]{ContentResolver.class, String.class}).invoke(cls.newInstance(), new Object[]{context.getContentResolver(), declaredField.get(declaredField.getName())})).booleanValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean k(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "enable_miui_security_ime", 1) == 1;
    }

    public static void l(Context context) {
        boolean z = true;
        if (Settings.Secure.getInt(context.getContentResolver(), "enable_quick_paste_cloud", 1) != 1) {
            z = false;
        }
        f7456a = z;
    }

    private static String m(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "cloud_clipboard_cipher_content_saved");
        return !TextUtils.isEmpty(string) ? new String(Base64.decode(string.getBytes(), 0)) : "";
    }

    private static ArrayList<String> n(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "KEY_CLIPBOARD_LIST");
        ArrayList<String> arrayList = new ArrayList<>();
        if (string != null) {
            try {
                JSONArray jSONArray = new JSONArray(string);
                for (int i = 0; i < jSONArray.length(); i++) {
                    arrayList.add(jSONArray.optString(i));
                }
            } catch (JSONException e) {
                Log.e("InputUtils", "getClipboardList,parse json error.", e);
            }
        }
        return arrayList;
    }
}
