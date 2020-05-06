package com.miui.optimizecenter.storage.a;

import androidx.annotation.NonNull;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private int f5704a;

    /* renamed from: b  reason: collision with root package name */
    private String f5705b;

    /* renamed from: c  reason: collision with root package name */
    private String f5706c;

    /* renamed from: d  reason: collision with root package name */
    private long f5707d;
    private String e;
    private d f = d.LINE;
    private boolean g;

    public c(int i, d dVar) {
        this.f5704a = i;
        this.f = dVar;
    }

    public c(d dVar) {
        this.f = dVar;
        this.f5704a = dVar.a();
    }

    public c(String str, d dVar) {
        this.f5705b = str;
        this.f = dVar;
    }

    public c(String str, String str2, String str3, d dVar) {
        this.f5705b = str;
        this.f5706c = str2;
        this.f = dVar;
        this.e = str3;
    }

    public c a(boolean z) {
        this.g = z;
        return this;
    }

    public String a() {
        return this.e;
    }

    public void a(long j) {
        this.f5707d = j;
    }

    public d b() {
        return this.f;
    }

    public long c() {
        return this.f5707d;
    }

    public String d() {
        return this.f5706c;
    }

    public String e() {
        return this.f5705b;
    }

    public int f() {
        return this.f5704a;
    }

    @NonNull
    public String toString() {
        return "Type=" + this.f;
    }
}
