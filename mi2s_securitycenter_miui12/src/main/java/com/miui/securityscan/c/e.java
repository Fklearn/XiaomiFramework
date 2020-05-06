package com.miui.securityscan.c;

import android.content.Context;
import android.content.SharedPreferences;
import b.b.c.j.d;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class e {

    /* renamed from: a  reason: collision with root package name */
    private static HashMap<String, e> f7633a;

    /* renamed from: b  reason: collision with root package name */
    private final int f7634b = 0;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public final SharedPreferences f7635c;

    private e(Context context, String str) {
        this.f7635c = context.getSharedPreferences(str, 0);
    }

    public static synchronized e a(Context context, String str) {
        e eVar;
        synchronized (e.class) {
            if (f7633a == null) {
                f7633a = new HashMap<>();
            }
            eVar = f7633a.get(str);
            if (eVar == null) {
                eVar = new e(context, str);
                f7633a.put(str, eVar);
            }
        }
        return eVar;
    }

    public String a(String str, String str2) {
        return this.f7635c.getString(str, str2);
    }

    public void a(String str, Set<String> set) {
        d.a(new c(this, str, set));
    }

    public boolean a(String str) {
        return this.f7635c.contains(str);
    }

    public boolean a(String str, boolean z) {
        return this.f7635c.getBoolean(str, z);
    }

    public void b(String str) {
        SharedPreferences.Editor edit = this.f7635c.edit();
        edit.remove(str);
        edit.commit();
    }

    public boolean b(String str, String str2) {
        SharedPreferences.Editor edit = this.f7635c.edit();
        try {
            edit.putString(str, str2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return edit.commit();
    }

    public boolean b(String str, boolean z) {
        SharedPreferences.Editor edit = this.f7635c.edit();
        edit.putBoolean(str, z);
        return edit.commit();
    }

    public Set<String> c(String str) {
        return this.f7635c.getStringSet(str, new HashSet());
    }

    public void c(String str, boolean z) {
        d.a(new d(this, str, z));
    }
}
