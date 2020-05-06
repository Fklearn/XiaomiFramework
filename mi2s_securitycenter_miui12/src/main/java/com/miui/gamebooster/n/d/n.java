package com.miui.gamebooster.n.d;

import java.util.Comparator;

public class n {

    /* renamed from: a  reason: collision with root package name */
    private String f4705a;

    /* renamed from: b  reason: collision with root package name */
    private String f4706b;

    /* renamed from: c  reason: collision with root package name */
    private String f4707c;

    /* renamed from: d  reason: collision with root package name */
    private int f4708d;
    private boolean e;

    public static class a implements Comparator<n> {
        /* renamed from: a */
        public int compare(n nVar, n nVar2) {
            if (!nVar.d() || nVar2.d()) {
                return (nVar.d() || !nVar2.d()) ? 0 : 1;
            }
            return -1;
        }
    }

    public n(String str, String str2, String str3, int i, boolean z) {
        this.f4705a = str;
        this.f4706b = str2;
        this.f4707c = str3;
        this.f4708d = i;
        this.e = z;
    }

    public String a() {
        return this.f4706b;
    }

    public void a(boolean z) {
        this.e = z;
    }

    public String b() {
        return this.f4707c;
    }

    public String c() {
        return this.f4705a;
    }

    public boolean d() {
        return this.e;
    }
}
