package com.miui.superpower.a;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powercenter.utils.o;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

public class e extends k {

    /* renamed from: c  reason: collision with root package name */
    private static final Set<String> f8052c = new HashSet(6);

    /* renamed from: d  reason: collision with root package name */
    private String f8053d;
    private String e;
    private String f;
    private Set<String> g = new HashSet();

    static {
        f8052c.add("long_press_power_key");
        f8052c.add("long_press_home_key");
        f8052c.add("long_press_menu_key");
        f8052c.add("long_press_back_key");
        f8052c.add("key_combination_power_home");
        f8052c.add("key_combination_power_menu");
        f8052c.add("key_combination_power_back");
    }

    public e(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
        try {
            Class<?> cls = Class.forName("android.provider.MiuiSettings$Key");
            this.f8053d = (String) b.b.o.g.e.a(cls, "NONE", String.class);
            this.e = (String) b.b.o.g.e.a(cls, "LAUNCH_VOICE_ASSISTANT", String.class);
            this.f = (String) b.b.o.g.e.a(cls, "SPLIT_SCREEN", String.class);
            this.g.add(this.e);
            this.g.add(this.f);
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        } catch (NoSuchFieldException e4) {
            e4.printStackTrace();
        }
    }

    private String a(Map<String, String> map) {
        JSONObject jSONObject = new JSONObject();
        for (String next : map.keySet()) {
            try {
                jSONObject.put(next, map.get(next));
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        return jSONObject.toString();
    }

    private Map<String, String> a(String str) {
        HashMap hashMap = new HashMap();
        if (f8052c.contains(str)) {
            hashMap.put(str, this.e);
            return hashMap;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                hashMap.put(next, jSONObject.getString(next));
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return hashMap;
    }

    public void a(boolean z) {
        if (this.g.size() != 0 && !TextUtils.isEmpty(this.f8053d) && !this.f8066b.getBoolean("pref_key_superpower_xiaoai_state", false)) {
            try {
                if (Settings.System.getInt(this.f8065a.getContentResolver(), "long_press_power_launch_xiaoai") == 1) {
                    SharedPreferences.Editor edit = this.f8066b.edit();
                    edit.putBoolean("pref_key_superpower_xiaoai_state", true);
                    edit.putInt("pref_key_superpower_xiaoai_powerkey_state", 1);
                    edit.commit();
                    Settings.System.putInt(this.f8065a.getContentResolver(), "long_press_power_launch_xiaoai", 0);
                }
            } catch (Settings.SettingNotFoundException e2) {
                e2.printStackTrace();
            }
            HashMap hashMap = new HashMap();
            for (String str : new ArrayList(f8052c)) {
                String string = Settings.System.getString(this.f8065a.getContentResolver(), str);
                if (this.g.contains(string)) {
                    hashMap.put(str, string);
                } else if (TextUtils.isEmpty(string)) {
                    try {
                        String str2 = (String) b.b.o.g.e.a(Class.forName("android.provider.MiuiSettings$Key"), String.class, "getKeyAndGestureShortcutFunction", (Class<?>[]) new Class[]{Context.class, String.class}, this.f8065a, str);
                        if (this.g.contains(str2)) {
                            hashMap.put(str, str2);
                        }
                    } catch (ClassNotFoundException e3) {
                        e3.printStackTrace();
                    } catch (IllegalAccessException e4) {
                        e4.printStackTrace();
                    } catch (NoSuchMethodException e5) {
                        e5.printStackTrace();
                    } catch (InvocationTargetException e6) {
                        e6.printStackTrace();
                    }
                }
            }
            if (hashMap.size() > 0) {
                SharedPreferences.Editor edit2 = this.f8066b.edit();
                edit2.putBoolean("pref_key_superpower_xiaoai_state", true);
                edit2.putString("pref_key_superpower_xiaoai_action_state", a((Map<String, String>) hashMap));
                edit2.commit();
                for (String putString : hashMap.keySet()) {
                    Settings.System.putString(this.f8065a.getContentResolver(), putString, this.f8053d);
                }
            }
        }
    }

    public boolean a() {
        return !o.m(this.f8065a) && this.f8066b.getBoolean("pref_key_superpower_xiaoai_state", false);
    }

    public void c() {
        Log.w("SuperPowerSaveManager", "keybutton policy restore state");
        d();
    }

    public void d() {
        if (this.g.size() != 0 && !TextUtils.isEmpty(this.f8053d) && this.f8066b.getBoolean("pref_key_superpower_xiaoai_state", false)) {
            if (this.f8066b.getInt("pref_key_superpower_xiaoai_powerkey_state", 0) == 1) {
                try {
                    if (Settings.System.getInt(this.f8065a.getContentResolver(), "long_press_power_launch_xiaoai") == 0) {
                        Settings.System.putInt(this.f8065a.getContentResolver(), "long_press_power_launch_xiaoai", 1);
                    }
                } catch (Settings.SettingNotFoundException e2) {
                    e2.printStackTrace();
                }
            }
            String string = this.f8066b.getString("pref_key_superpower_xiaoai_action_state", (String) null);
            if (!TextUtils.isEmpty(string)) {
                Map<String, String> a2 = a(string);
                for (String next : a2.keySet()) {
                    String string2 = Settings.System.getString(this.f8065a.getContentResolver(), next);
                    if (TextUtils.isEmpty(string2) || this.f8053d.equals(string2)) {
                        Settings.System.putString(this.f8065a.getContentResolver(), next, a2.get(next));
                    }
                }
            }
            SharedPreferences.Editor edit = this.f8066b.edit();
            edit.putBoolean("pref_key_superpower_xiaoai_state", false);
            edit.putInt("pref_key_superpower_xiaoai_powerkey_state", 0);
            edit.putString("pref_key_superpower_xiaoai_action_state", (String) null);
            edit.commit();
        }
    }

    public String name() {
        return "keybutton policy";
    }
}
