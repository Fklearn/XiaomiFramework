package com.xiaomi.analytics;

import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.analytics.a.a.a;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONObject;

public abstract class Action {

    /* renamed from: a  reason: collision with root package name */
    private static Set<String> f8242a = new HashSet();

    /* renamed from: b  reason: collision with root package name */
    private JSONObject f8243b = new JSONObject();

    /* renamed from: c  reason: collision with root package name */
    private JSONObject f8244c = new JSONObject();

    static {
        f8242a.add("_event_id_");
        f8242a.add("_category_");
        f8242a.add("_action_");
        f8242a.add("_label_");
        f8242a.add("_value_");
    }

    private void b(String str) {
        if (!TextUtils.isEmpty(str) && f8242a.contains(str)) {
            throw new IllegalArgumentException("this key " + str + " is built-in, please pick another key.");
        }
    }

    /* access modifiers changed from: protected */
    public Action a(String str) {
        a("_event_id_", (Object) str);
        return this;
    }

    /* access modifiers changed from: package-private */
    public final JSONObject a() {
        return this.f8243b;
    }

    /* access modifiers changed from: package-private */
    public void a(String str, int i) {
        if (!TextUtils.isEmpty(str)) {
            try {
                this.f8243b.put(str, i);
            } catch (Exception e) {
                Log.e(a.a("Action"), "addContent int value e", e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(String str, long j) {
        if (!TextUtils.isEmpty(str)) {
            try {
                this.f8243b.put(str, j);
            } catch (Exception e) {
                Log.e(a.a("Action"), "addContent long value e", e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(String str, Object obj) {
        if (!TextUtils.isEmpty(str)) {
            try {
                this.f8243b.put(str, obj);
            } catch (Exception e) {
                Log.e(a.a("Action"), "addContent Object value e", e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(String str, String str2) {
        try {
            this.f8244c.put(str, str2);
        } catch (Exception e) {
            Log.e(a.a("Action"), "addExtra e", e);
        }
    }

    /* access modifiers changed from: package-private */
    public void a(JSONObject jSONObject) {
        if (jSONObject != null) {
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String obj = keys.next().toString();
                b(obj);
                try {
                    this.f8243b.put(obj, jSONObject.get(obj));
                } catch (Exception e) {
                    Log.e(a.a("Action"), "addContent e", e);
                }
            }
        }
    }

    public Action b(String str, int i) {
        b(str);
        a(str, i);
        return this;
    }

    public Action b(String str, long j) {
        b(str);
        a(str, j);
        return this;
    }

    public Action b(String str, String str2) {
        b(str);
        a(str, (Object) str2);
        return this;
    }

    /* access modifiers changed from: package-private */
    public final JSONObject b() {
        return this.f8244c;
    }
}
