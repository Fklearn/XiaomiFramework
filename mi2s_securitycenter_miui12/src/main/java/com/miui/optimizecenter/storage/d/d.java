package com.miui.optimizecenter.storage.d;

import java.io.File;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private Object f5735a;

    public d(Object obj) {
        this.f5735a = obj;
    }

    public a a() {
        return new a(b.a(this.f5735a, "getDisk"));
    }

    public String b() {
        Object a2 = b.a(this.f5735a, "getDiskId");
        return a2 == null ? "" : (String) a2;
    }

    public String c() {
        Object a2 = b.a(this.f5735a, "getId");
        return a2 == null ? "" : (String) a2;
    }

    public File d() {
        Object a2 = b.a(this.f5735a, "getPath");
        if (a2 == null) {
            return null;
        }
        return (File) a2;
    }

    public int e() {
        Object a2 = b.a(this.f5735a, "getState");
        if (a2 == null) {
            return -1;
        }
        return ((Integer) a2).intValue();
    }

    public int f() {
        Object a2 = b.a(this.f5735a, "getType");
        if (a2 == null) {
            return -1;
        }
        return ((Integer) a2).intValue();
    }

    public boolean g() {
        Object a2 = b.a(this.f5735a, "isMountedReadable");
        return a2 != null && ((Boolean) a2).booleanValue();
    }
}
