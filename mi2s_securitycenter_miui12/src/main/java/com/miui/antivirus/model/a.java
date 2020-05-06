package com.miui.antivirus.model;

import com.miui.antivirus.result.C0238a;

public class a extends C0238a {

    /* renamed from: a  reason: collision with root package name */
    protected int f2745a;

    /* renamed from: b  reason: collision with root package name */
    protected String f2746b;

    /* renamed from: c  reason: collision with root package name */
    protected String f2747c;

    /* renamed from: d  reason: collision with root package name */
    protected C0039a f2748d;
    protected boolean e = false;
    protected boolean f = false;
    protected boolean g = true;
    protected boolean h = true;

    /* renamed from: com.miui.antivirus.model.a$a  reason: collision with other inner class name */
    public enum C0039a {
        WIFI,
        SYSTEM,
        SMS,
        APP
    }

    public a() {
        setBaseCardType(C0238a.C0040a.SCAN);
    }

    public C0039a a() {
        return this.f2748d;
    }

    public void a(int i) {
        this.f2745a = i;
    }

    public void a(String str) {
        this.f2746b = str;
    }

    public void a(boolean z) {
        this.f = z;
    }

    public int b() {
        return this.f2745a;
    }

    public void b(boolean z) {
        this.h = z;
    }

    public String c() {
        return this.f2746b;
    }

    public void c(boolean z) {
        this.g = z;
    }

    public boolean d() {
        return this.h;
    }

    public boolean e() {
        return this.e;
    }

    public boolean f() {
        return this.g;
    }
}
