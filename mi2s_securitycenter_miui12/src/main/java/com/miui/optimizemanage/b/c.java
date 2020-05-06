package com.miui.optimizemanage.b;

import android.util.Log;
import com.miui.optimizemanage.b.b;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private b f5876a;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public String f5877a;

        /* renamed from: b  reason: collision with root package name */
        public int f5878b;

        /* renamed from: c  reason: collision with root package name */
        public int f5879c;

        /* renamed from: d  reason: collision with root package name */
        public long f5880d;
        public long e;
        public long f;
        public long g;
        public long h;
        public long i;
        public boolean j;
        public boolean k;
        public long l;
    }

    public c(boolean z) {
        this.f5876a = new b(z);
    }

    public int a() {
        return this.f5876a.a();
    }

    public a a(int i) {
        a aVar = new a();
        try {
            b.a a2 = this.f5876a.a(i);
            aVar.f5877a = a2.j;
            aVar.f5878b = a2.f5873b;
            aVar.f5879c = a2.f5872a;
            aVar.f5880d = a2.o;
            aVar.e = (long) a2.q;
            aVar.f = a2.p;
            aVar.g = (long) a2.r;
            aVar.h = a2.m;
            aVar.i = a2.n;
            aVar.j = a2.z;
            aVar.k = a2.y;
            aVar.l = a2.w;
        } catch (Exception e) {
            Log.e("ProcessCpuTrackerWr", "getStats", e);
        }
        return aVar;
    }

    public void b() {
        this.f5876a.b();
    }

    public void c() {
        this.f5876a.c();
    }
}
