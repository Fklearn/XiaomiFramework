package com.miui.gamebooster;

public class e {

    /* renamed from: a  reason: collision with root package name */
    private String f4273a;

    /* renamed from: b  reason: collision with root package name */
    private String f4274b;

    /* renamed from: c  reason: collision with root package name */
    private String f4275c;

    /* renamed from: d  reason: collision with root package name */
    private int f4276d;
    private boolean e;
    private boolean f;
    private int g;

    public e(int i) {
        this.f4273a = "";
        this.f4275c = "";
        this.g = i;
    }

    public e(String str, String str2, String str3, int i, boolean z, int i2) {
        this.f4273a = str;
        this.f4274b = str2;
        this.f4275c = str3;
        this.f4276d = i;
        this.e = z;
        this.g = i2;
    }

    public void a(boolean z) {
        this.e = z;
    }

    public boolean a() {
        return this.e;
    }

    public String b() {
        return this.f4274b;
    }

    public void b(boolean z) {
        this.f = z;
    }

    public String c() {
        return this.f4275c;
    }

    public boolean d() {
        return this.f;
    }

    public String e() {
        return this.f4273a;
    }

    public int f() {
        return this.g;
    }

    public int g() {
        return this.f4276d;
    }
}
