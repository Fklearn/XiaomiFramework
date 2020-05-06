package com.miui.applicationlock.c;

/* renamed from: com.miui.applicationlock.c.a  reason: case insensitive filesystem */
public class C0257a {

    /* renamed from: a  reason: collision with root package name */
    private String f3291a;

    /* renamed from: b  reason: collision with root package name */
    private Integer f3292b;

    /* renamed from: c  reason: collision with root package name */
    private String f3293c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f3294d = true;
    private boolean e = false;
    private int f;
    private boolean g;

    public C0257a(String str, Integer num, String str2, int i) {
        this.f3291a = str;
        this.f3292b = num;
        this.f3293c = str2;
        this.f = i;
    }

    public String a() {
        return this.f3291a;
    }

    public void a(boolean z) {
        this.e = z;
    }

    public Integer b() {
        return this.f3292b;
    }

    public void b(boolean z) {
        this.g = z;
    }

    public void c(boolean z) {
        this.f3294d = z;
    }

    public boolean c() {
        return this.f3294d;
    }

    public int d() {
        return this.f;
    }

    public String e() {
        return this.f3293c;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof C0257a)) {
            return false;
        }
        C0257a aVar = (C0257a) obj;
        return this.f3293c.equals(aVar.f3293c) && this.f == aVar.f;
    }

    public boolean f() {
        return this.e;
    }

    public boolean g() {
        return this.g;
    }

    public int hashCode() {
        return (this.f3293c.hashCode() * 31 * 31) + this.f;
    }
}
