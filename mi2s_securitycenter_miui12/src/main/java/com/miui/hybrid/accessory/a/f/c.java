package com.miui.hybrid.accessory.a.f;

import com.miui.hybrid.accessory.a.f.b.a;
import com.miui.hybrid.accessory.a.f.b.e;
import com.miui.hybrid.accessory.a.f.b.g;
import com.miui.hybrid.accessory.a.f.c.a;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private final e f5525a;

    /* renamed from: b  reason: collision with root package name */
    private final a f5526b;

    public c() {
        this(new a.C0052a());
    }

    public c(g gVar) {
        this.f5526b = new com.miui.hybrid.accessory.a.f.c.a();
        this.f5525a = gVar.a(this.f5526b);
    }

    public void a(a aVar, byte[] bArr) {
        try {
            this.f5526b.a(bArr);
            aVar.a(this.f5525a);
        } finally {
            this.f5525a.s();
        }
    }
}
