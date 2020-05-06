package com.miui.antispam.db.b;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private int f2345a;

    /* renamed from: b  reason: collision with root package name */
    private String f2346b;

    /* renamed from: c  reason: collision with root package name */
    private String f2347c;

    /* renamed from: d  reason: collision with root package name */
    private String f2348d = "";
    private int e = 0;
    private int f = 1;
    private int g;
    private int h = 0;
    private String i;

    public b() {
    }

    public b(String str) {
        this.i = str;
    }

    public b(String str, String str2, int i2, int i3) {
        this.i = str;
        this.f2346b = str2;
        this.g = i2;
        this.e = i3;
    }

    public String a() {
        return this.i;
    }

    public void a(int i2) {
        this.f2345a = i2;
    }

    public void a(String str) {
        this.f2347c = str;
    }

    public String b() {
        return this.f2346b;
    }

    public void b(int i2) {
        this.e = i2;
    }

    public void b(String str) {
        this.f2348d = str;
    }

    public int c() {
        return this.e;
    }

    public void c(int i2) {
        this.g = i2;
    }

    public void c(String str) {
        this.f2346b = str;
    }

    public int d() {
        return this.g;
    }
}
