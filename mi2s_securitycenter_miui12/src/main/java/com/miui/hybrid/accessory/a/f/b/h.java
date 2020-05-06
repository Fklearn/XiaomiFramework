package com.miui.hybrid.accessory.a.f.b;

import com.miui.hybrid.accessory.a.f.d;

public class h {

    /* renamed from: a  reason: collision with root package name */
    private static int f5521a = Integer.MAX_VALUE;

    public static void a(e eVar, byte b2) {
        a(eVar, b2, f5521a);
    }

    public static void a(e eVar, byte b2, int i) {
        if (i > 0) {
            int i2 = 0;
            switch (b2) {
                case 2:
                    eVar.k();
                    return;
                case 3:
                    eVar.l();
                    return;
                case 4:
                    eVar.p();
                    return;
                case 6:
                    eVar.m();
                    return;
                case 8:
                    eVar.n();
                    return;
                case 10:
                    eVar.o();
                    return;
                case 11:
                    eVar.r();
                    return;
                case 12:
                    eVar.a();
                    while (true) {
                        byte b3 = eVar.c().f5513b;
                        if (b3 == 0) {
                            eVar.b();
                            return;
                        } else {
                            a(eVar, b3, i - 1);
                            eVar.d();
                        }
                    }
                case 13:
                    d e = eVar.e();
                    while (i2 < e.f5519c) {
                        int i3 = i - 1;
                        a(eVar, e.f5517a, i3);
                        a(eVar, e.f5518b, i3);
                        i2++;
                    }
                    eVar.f();
                    return;
                case 14:
                    i i4 = eVar.i();
                    while (i2 < i4.f5523b) {
                        a(eVar, i4.f5522a, i - 1);
                        i2++;
                    }
                    eVar.j();
                    return;
                case 15:
                    c g = eVar.g();
                    while (i2 < g.f5516b) {
                        a(eVar, g.f5515a, i - 1);
                        i2++;
                    }
                    eVar.h();
                    return;
                default:
                    return;
            }
        } else {
            throw new d("Maximum skip depth exceeded");
        }
    }
}
