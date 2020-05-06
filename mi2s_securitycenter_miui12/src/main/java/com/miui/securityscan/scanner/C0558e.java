package com.miui.securityscan.scanner;

import com.miui.securityscan.scanner.O;

/* renamed from: com.miui.securityscan.scanner.e  reason: case insensitive filesystem */
public class C0558e {

    /* renamed from: a  reason: collision with root package name */
    public int f7889a;

    /* renamed from: b  reason: collision with root package name */
    public int f7890b;

    /* renamed from: c  reason: collision with root package name */
    public String f7891c;

    /* renamed from: d  reason: collision with root package name */
    public O.f f7892d;
    private int e;

    public C0558e(int i, int i2, String str) {
        this.f7892d = O.f.NORMAL;
        this.f7889a = i;
        this.f7890b = i2;
        this.f7891c = str;
    }

    public C0558e(O.f fVar) {
        this.f7892d = fVar;
        this.f7889a = -1;
        this.f7890b = -1;
        this.f7891c = null;
    }

    public C0558e(O.f fVar, int i) {
        this.f7892d = fVar;
        this.f7889a = -1;
        this.f7890b = -1;
        this.f7891c = null;
        this.e = i;
    }

    public int a() {
        return this.e;
    }
}
