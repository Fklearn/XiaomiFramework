package com.miui.securitycenter.memory;

import android.util.SparseBooleanArray;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private String f7493a;

    /* renamed from: b  reason: collision with root package name */
    private String f7494b;

    /* renamed from: c  reason: collision with root package name */
    private long f7495c;

    /* renamed from: d  reason: collision with root package name */
    private SparseBooleanArray f7496d;
    private int e;
    private boolean f;
    private String g;

    public SparseBooleanArray a() {
        return this.f7496d;
    }

    public void a(int i) {
        this.e = i;
    }

    public void a(long j) {
        this.f7495c = j;
    }

    public void a(SparseBooleanArray sparseBooleanArray) {
        this.f7496d = sparseBooleanArray;
    }

    public void a(String str) {
        this.f7493a = str;
    }

    public void a(boolean z) {
        this.f = z;
    }

    public long b() {
        return this.f7495c;
    }

    public void b(String str) {
        this.f7494b = str;
    }

    public String c() {
        return this.f7494b;
    }

    public void c(String str) {
        this.g = str;
    }

    public String d() {
        return this.g;
    }

    public int e() {
        return this.e;
    }

    public boolean f() {
        return this.f;
    }

    public String toString() {
        return "MemoryModel2 [appName=" + this.f7493a + ", packageName=" + this.f7494b + ", memorySize=" + this.f7495c + ", lockState=" + this.f7496d + ", userId=" + this.e + ", isChecked=" + this.f + ", uniqueKey=" + this.g + "]";
    }
}
