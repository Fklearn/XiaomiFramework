package com.miui.activityutil;

public final class s {

    /* renamed from: a  reason: collision with root package name */
    private String f2320a;

    /* renamed from: b  reason: collision with root package name */
    private String f2321b;

    /* renamed from: c  reason: collision with root package name */
    private String f2322c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f2323d;

    public s(String str, String str2, String str3) {
        this.f2320a = str;
        this.f2322c = str2;
        this.f2321b = str3;
    }

    private void b(String str) {
        this.f2322c = str;
    }

    private String d() {
        String str = this.f2321b;
        return str == null ? "" : str;
    }

    private String e() {
        String str = this.f2322c;
        return str == null ? "" : str;
    }

    public final void a(String str) {
        this.f2320a = str;
    }

    public final void a(boolean z) {
        this.f2323d = z;
    }

    public final boolean a() {
        return "mounted".equals(this.f2321b);
    }

    public final String b() {
        String str = this.f2320a;
        return str == null ? "" : str;
    }

    public final boolean c() {
        return this.f2323d;
    }
}
