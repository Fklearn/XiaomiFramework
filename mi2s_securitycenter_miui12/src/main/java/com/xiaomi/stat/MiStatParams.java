package com.xiaomi.stat;

import com.xiaomi.stat.d.j;
import com.xiaomi.stat.d.k;
import com.xiaomi.stat.d.n;
import java.io.Reader;
import java.io.StringReader;
import org.json.JSONException;
import org.json.JSONObject;

public class MiStatParams {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8346a = "MiStatParams";

    /* renamed from: b  reason: collision with root package name */
    private JSONObject f8347b;

    public MiStatParams() {
        this.f8347b = new JSONObject();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0005, code lost:
        r1 = r1.f8347b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    MiStatParams(com.xiaomi.stat.MiStatParams r1) {
        /*
            r0 = this;
            r0.<init>()
            if (r1 == 0) goto L_0x000e
            org.json.JSONObject r1 = r1.f8347b
            if (r1 == 0) goto L_0x000e
            org.json.JSONObject r1 = r0.a((org.json.JSONObject) r1)
            goto L_0x0013
        L_0x000e:
            org.json.JSONObject r1 = new org.json.JSONObject
            r1.<init>()
        L_0x0013:
            r0.f8347b = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.MiStatParams.<init>(com.xiaomi.stat.MiStatParams):void");
    }

    private JSONObject a(JSONObject jSONObject) {
        StringReader stringReader;
        Exception e;
        try {
            stringReader = new StringReader(jSONObject.toString());
            try {
                StringBuilder sb = new StringBuilder();
                while (true) {
                    int read = stringReader.read();
                    if (read != -1) {
                        sb.append((char) read);
                    } else {
                        JSONObject jSONObject2 = new JSONObject(sb.toString());
                        j.a((Reader) stringReader);
                        return jSONObject2;
                    }
                }
            } catch (Exception e2) {
                e = e2;
                try {
                    k.e(" deepCopy " + e);
                    j.a((Reader) stringReader);
                    return jSONObject;
                } catch (Throwable th) {
                    th = th;
                    j.a((Reader) stringReader);
                    throw th;
                }
            }
        } catch (Exception e3) {
            Exception exc = e3;
            stringReader = null;
            e = exc;
            k.e(" deepCopy " + e);
            j.a((Reader) stringReader);
            return jSONObject;
        } catch (Throwable th2) {
            th = th2;
            stringReader = null;
            j.a((Reader) stringReader);
            throw th;
        }
    }

    private boolean c(String str) {
        return a() && !this.f8347b.has(str) && this.f8347b.length() == 30;
    }

    /* access modifiers changed from: package-private */
    public boolean a() {
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean a(String str) {
        return n.a(str);
    }

    /* access modifiers changed from: package-private */
    public boolean b(String str) {
        return n.b(str);
    }

    public int getParamsNumber() {
        return this.f8347b.length();
    }

    public boolean isEmpty() {
        return this.f8347b.length() == 0;
    }

    public void putBoolean(String str, boolean z) {
        if (!a(str)) {
            n.e(str);
        } else if (c(str)) {
            n.a();
        } else {
            try {
                this.f8347b.put(str, z);
            } catch (JSONException e) {
                k.c(f8346a, "put value error " + e);
            }
        }
    }

    public void putDouble(String str, double d2) {
        if (!a(str)) {
            n.e(str);
        } else if (c(str)) {
            n.a();
        } else {
            try {
                this.f8347b.put(str, d2);
            } catch (JSONException e) {
                k.c(f8346a, "put value error " + e);
            }
        }
    }

    public void putInt(String str, int i) {
        if (!a(str)) {
            n.e(str);
        } else if (c(str)) {
            n.a();
        } else {
            try {
                this.f8347b.put(str, i);
            } catch (JSONException e) {
                k.c(f8346a, "put value error " + e);
            }
        }
    }

    public void putLong(String str, long j) {
        if (!a(str)) {
            n.e(str);
        } else if (c(str)) {
            n.a();
        } else {
            try {
                this.f8347b.put(str, j);
            } catch (JSONException e) {
                k.c(f8346a, "put value error " + e);
            }
        }
    }

    public void putString(String str, String str2) {
        if (!a(str)) {
            n.e(str);
        } else if (!b(str2)) {
            n.f(str2);
        } else if (c(str)) {
            n.a();
        } else {
            try {
                this.f8347b.put(str, n.c(str2));
            } catch (JSONException e) {
                k.c(f8346a, "put value error " + e);
            }
        }
    }

    public String toJsonString() {
        return this.f8347b.toString();
    }
}
