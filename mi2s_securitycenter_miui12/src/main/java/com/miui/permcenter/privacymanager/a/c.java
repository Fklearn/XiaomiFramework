package com.miui.permcenter.privacymanager.a;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private String f6333a;

    /* renamed from: b  reason: collision with root package name */
    private int f6334b;

    /* renamed from: c  reason: collision with root package name */
    private int f6335c = 0;

    public c(String str, int i) {
        this.f6333a = str;
        this.f6334b = i;
    }

    public String a() {
        return this.f6334b + "@" + this.f6333a;
    }

    public boolean a(int i) {
        return ((1 << (i - 1)) & this.f6335c) == 0;
    }

    public int b() {
        return this.f6335c;
    }

    public void b(int i) {
        this.f6335c = (1 << (i - 1)) | this.f6335c;
    }

    public void c(int i) {
        this.f6335c = (~(1 << (i - 1))) & this.f6335c;
    }

    public void d(int i) {
        this.f6335c = i;
    }

    public boolean equals(Object obj) {
        if (obj instanceof c) {
            return a().equals(((c) obj).a());
        }
        return false;
    }

    public int hashCode() {
        return a().hashCode();
    }
}
