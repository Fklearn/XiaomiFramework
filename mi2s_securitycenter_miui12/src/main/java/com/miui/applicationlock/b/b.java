package com.miui.applicationlock.b;

import android.content.Context;
import b.b.n.e;
import b.b.n.g;
import b.b.n.l;
import java.util.Map;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private Map<Long, g.a> f3257a;

    /* renamed from: b  reason: collision with root package name */
    private final e f3258b;

    /* renamed from: c  reason: collision with root package name */
    private int f3259c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f3260d;
    private long e;
    private boolean f;
    private boolean g;
    private boolean h;

    public b(Context context) {
        this.f3258b = e.a(context);
        g a2 = this.f3258b.a("applicationlock");
        if (a2 != null) {
            this.f3257a = a2.d();
            this.f3259c = a2.a();
            this.f3260d = a2.j();
            this.e = a2.b();
            this.f = a2.h();
            this.h = a2.i();
            this.g = l.a(a2.c(), a2.g(), this.e, "applicationlock");
        }
    }

    public int a() {
        return this.f3259c;
    }

    public g.a a(long j) {
        Map<Long, g.a> map;
        if (j == -1 || (map = this.f3257a) == null) {
            return null;
        }
        return map.get(Long.valueOf(j));
    }

    public long b() {
        return this.e;
    }

    public Map<Long, g.a> c() {
        return this.f3257a;
    }

    public e d() {
        return this.f3258b;
    }

    public boolean e() {
        return this.g;
    }

    public boolean f() {
        return this.h;
    }
}
