package com.miui.securityscan.f;

import android.content.Context;
import b.b.n.e;
import b.b.n.g;
import b.b.n.l;
import java.util.Map;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static b f7697a;

    /* renamed from: b  reason: collision with root package name */
    private Map<Long, g.a> f7698b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f7699c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f7700d;
    private boolean e = true;
    private String f;
    private boolean g;
    private int h;
    private long i;
    private e j;
    private boolean k;

    public b(Context context) {
        this.j = e.a(context);
        g a2 = this.j.a("securitycenterScan");
        if (a2 != null) {
            this.f7698b = a2.d();
            this.f7699c = a2.f() > 0;
            this.e = a2.j();
            this.f = a2.e();
            this.i = a2.b();
            this.f7700d = this.j.a("securitycenterScan", this.i);
            this.h = a2.a();
            if (this.h == 2) {
                this.g = true;
            } else {
                this.g = false;
            }
            this.k = l.a(a2.c(), a2.g(), this.i, "securitycenterScan");
        }
    }

    public static synchronized b a(Context context) {
        b bVar;
        synchronized (b.class) {
            if (f7697a == null) {
                f7697a = new b(context.getApplicationContext());
            }
            bVar = f7697a;
        }
        return bVar;
    }

    public g.a a(long j2) {
        Map<Long, g.a> map;
        if (j2 == -1 || (map = this.f7698b) == null) {
            return null;
        }
        return map.get(Long.valueOf(j2));
    }

    public void a() {
        f7697a = null;
    }

    public boolean b() {
        return this.i != 0 && this.f7699c && !this.f7700d && this.k;
    }
}
