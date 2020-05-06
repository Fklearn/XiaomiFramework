package com.miui.permcenter.privacymanager.a;

import android.text.TextUtils;

public class e {

    /* renamed from: a  reason: collision with root package name */
    private String f6339a;

    /* renamed from: b  reason: collision with root package name */
    private int f6340b;

    /* renamed from: c  reason: collision with root package name */
    private long f6341c;

    /* renamed from: d  reason: collision with root package name */
    public int f6342d;

    public e(int i) {
        this.f6342d = i;
    }

    public e(String str, int i, int i2) {
        this.f6339a = str;
        this.f6340b = i;
        this.f6342d = i2;
    }

    public e(String str, int i, long j) {
        this.f6339a = str;
        this.f6340b = i;
        this.f6341c = j;
    }

    public e(String str, int i, long j, int i2) {
        this(str, i, j);
        this.f6342d = i2;
    }

    public long a() {
        return this.f6341c;
    }

    public boolean a(e eVar) {
        return TextUtils.equals(this.f6339a, eVar.b()) && this.f6340b == eVar.d();
    }

    public String b() {
        return this.f6339a;
    }

    public String c() {
        return "AuthManager@" + this.f6340b + "@" + this.f6339a + "@" + this.f6341c;
    }

    public int d() {
        return this.f6340b;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof e)) {
            return false;
        }
        e eVar = (e) obj;
        return a(eVar) && this.f6341c == eVar.f6341c;
    }

    public int hashCode() {
        return (this.f6339a + this.f6341c + this.f6340b).hashCode();
    }
}
