package com.miui.permcenter;

import java.util.HashMap;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private String f6036a;

    /* renamed from: b  reason: collision with root package name */
    private String f6037b;

    /* renamed from: c  reason: collision with root package name */
    private int f6038c;

    /* renamed from: d  reason: collision with root package name */
    private HashMap<Long, Integer> f6039d;
    private HashMap<Long, String> e;
    private boolean f;
    private boolean g;
    private boolean h;
    private boolean i;

    public int a() {
        return this.f6038c;
    }

    public String a(long j) {
        HashMap<Long, String> hashMap = this.e;
        if (hashMap == null || !hashMap.containsKey(Long.valueOf(j))) {
            return null;
        }
        return this.e.get(Long.valueOf(j));
    }

    public void a(int i2) {
        this.f6038c = i2;
    }

    public void a(String str) {
        this.f6037b = str;
    }

    public void a(HashMap<Long, Integer> hashMap) {
        this.f6039d = hashMap;
    }

    public void a(boolean z) {
        this.i = z;
    }

    public void b(String str) {
        this.f6036a = str;
    }

    public void b(HashMap<Long, String> hashMap) {
        this.e = hashMap;
    }

    public void b(boolean z) {
        this.f = z;
    }

    public boolean b() {
        return this.f;
    }

    public void c(boolean z) {
        this.g = z;
    }

    public boolean c() {
        return this.g;
    }

    public String d() {
        return this.f6037b;
    }

    public void d(boolean z) {
        this.h = z;
    }

    public String e() {
        return this.f6036a;
    }

    public HashMap<Long, Integer> f() {
        return this.f6039d;
    }

    public boolean g() {
        return this.i;
    }

    public boolean h() {
        return this.h;
    }

    public String toString() {
        return "AppPermissionInfo [packageName=" + this.f6036a + ", label=" + this.f6037b + "]";
    }
}
