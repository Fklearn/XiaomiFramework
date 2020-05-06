package com.miui.optimizecenter.storage.d;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private Object f5732a;

    public a(Object obj) {
        this.f5732a = obj;
    }

    public String a() {
        Object a2 = b.a(this.f5732a, "getDescription");
        return a2 == null ? "" : (String) a2;
    }

    public String b() {
        Object a2 = b.a(this.f5732a, "getShortDescription");
        return a2 == null ? "" : (String) a2;
    }

    public boolean c() {
        Object a2 = b.a(this.f5732a, "isSd");
        return a2 != null && ((Boolean) a2).booleanValue();
    }

    public boolean d() {
        Object a2 = b.a(this.f5732a, "isUsb");
        return a2 != null && ((Boolean) a2).booleanValue();
    }
}
