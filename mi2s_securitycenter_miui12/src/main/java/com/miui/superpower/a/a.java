package com.miui.superpower.a;

import android.content.Context;
import android.content.SharedPreferences;
import com.miui.permcenter.s;
import com.miui.powercenter.quickoptimize.C0522a;
import com.miui.superpower.b;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

public class a extends k {
    public a(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }

    private void a(List<String> list, List<String> list2) {
        SharedPreferences.Editor edit = this.f8066b.edit();
        edit.putString("PREF_KEY_AUTO_START_BACKUP_LIST", new JSONArray(list).toString());
        if (list2 == null || list2.isEmpty()) {
            list2 = new ArrayList<>();
        }
        edit.putString("PREF_KEY_WAKE_PATH_BACKUP_LIST", new JSONArray(list2).toString());
        edit.commit();
    }

    private ArrayList<String> e() {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            JSONArray jSONArray = new JSONArray(this.f8066b.getString("PREF_KEY_AUTO_START_BACKUP_LIST", ""));
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(jSONArray.optString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private ArrayList<String> f() {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            JSONArray jSONArray = new JSONArray(this.f8066b.getString("PREF_KEY_WAKE_PATH_BACKUP_LIST", ""));
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(jSONArray.optString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public void a(boolean z) {
        b.a((List<String>) null);
        List<String> a2 = C0522a.a(this.f8065a);
        List<String> a3 = s.a(this.f8065a);
        if ((a2 != null && !a2.isEmpty()) || (a3 != null && !a3.isEmpty())) {
            if (!a2.isEmpty()) {
                C0522a.c(this.f8065a, a2);
            }
            if (a3 != null && !a3.isEmpty()) {
                for (String a4 : a3) {
                    s.a(this.f8065a, a4, false);
                }
            }
            a(a2, a3);
            this.f8066b.edit().putBoolean("pref_key_superpower_is_autostart_backup", true).commit();
        }
    }

    public void d() {
        if (this.f8066b.getBoolean("pref_key_superpower_is_autostart_backup", false)) {
            SharedPreferences.Editor edit = this.f8066b.edit();
            ArrayList<String> e = e();
            ArrayList<String> f = f();
            if (!e.isEmpty()) {
                C0522a.a(this.f8065a, e);
                edit.putString("PREF_KEY_AUTO_START_BACKUP_LIST", (String) null);
            }
            if (!f.isEmpty()) {
                for (String a2 : f) {
                    s.a(this.f8065a, a2, true);
                }
                edit.putString("PREF_KEY_WAKE_PATH_BACKUP_LIST", (String) null);
            }
            edit.putBoolean("pref_key_superpower_is_autostart_backup", false);
            edit.commit();
        }
    }

    public String name() {
        return "bkgapp policy";
    }
}
