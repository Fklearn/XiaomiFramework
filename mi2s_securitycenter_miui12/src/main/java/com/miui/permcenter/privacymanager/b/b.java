package com.miui.permcenter.privacymanager.b;

import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.d;

public abstract class b {

    /* renamed from: a  reason: collision with root package name */
    String f6348a = getClass().getSimpleName();

    private String a(String str, String str2) {
        return (TextUtils.isEmpty(str) ? d() : d(str)).concat("_").concat(str2);
    }

    private String b(String str) {
        return a(str, "count");
    }

    /* access modifiers changed from: private */
    public void c(String str) {
        com.miui.common.persistence.b.b(b(str), a(str) + 1);
    }

    private String d(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(d());
        stringBuffer.append("_");
        stringBuffer.append(str);
        return stringBuffer.toString();
    }

    private String f() {
        return a((String) null, "count");
    }

    /* access modifiers changed from: private */
    public String g() {
        return a((String) null, "timestamp");
    }

    private String h() {
        return a((String) null, "valid");
    }

    /* access modifiers changed from: private */
    public void i() {
        com.miui.common.persistence.b.b(f(), b() + 1);
    }

    public int a(String str) {
        return com.miui.common.persistence.b.a(b(str), 0);
    }

    public void a(String str, long j) {
        d.a(new a(this, j, str));
    }

    /* access modifiers changed from: package-private */
    public void a(String str, Throwable th) {
        if (a()) {
            Log.d(this.f6348a, str, th);
        }
    }

    public void a(boolean z) {
        com.miui.common.persistence.b.b(h(), z);
    }

    /* access modifiers changed from: package-private */
    public abstract boolean a();

    public int b() {
        return com.miui.common.persistence.b.a(f(), 0);
    }

    public long c() {
        return com.miui.common.persistence.b.a(g(), 0);
    }

    /* access modifiers changed from: package-private */
    public abstract String d();

    public boolean e() {
        return com.miui.common.persistence.b.a(h(), true);
    }
}
