package com.miui.hybrid.accessory.a.f.b;

import com.google.android.exoplayer2.C;
import com.miui.hybrid.accessory.a.f.b.a;
import com.miui.hybrid.accessory.a.f.c.b;
import com.miui.hybrid.accessory.a.f.d;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class k extends a {
    private static int f = 10000;
    private static int g = 10000;
    private static int h = 10000;
    private static int i = 10485760;
    private static int j = 104857600;

    public static class a extends a.C0052a {
        public a() {
            super(false, true);
        }

        public a(boolean z, boolean z2, int i) {
            super(z, z2, i);
        }

        public e a(b bVar) {
            k kVar = new k(bVar, this.f5509a, this.f5510b);
            int i = this.f5511c;
            if (i != 0) {
                kVar.b(i);
            }
            return kVar;
        }
    }

    public k(b bVar, boolean z, boolean z2) {
        super(bVar, z, z2);
    }

    public d e() {
        byte l = l();
        byte l2 = l();
        int n = n();
        if (n <= f) {
            return new d(l, l2, n);
        }
        throw new f(3, "Thrift map size " + n + " out of range!");
    }

    public c g() {
        byte l = l();
        int n = n();
        if (n <= g) {
            return new c(l, n);
        }
        throw new f(3, "Thrift list size " + n + " out of range!");
    }

    public i i() {
        byte l = l();
        int n = n();
        if (n <= h) {
            return new i(l, n);
        }
        throw new f(3, "Thrift set size " + n + " out of range!");
    }

    public String q() {
        int n = n();
        if (n > i) {
            throw new f(3, "Thrift string size " + n + " out of range!");
        } else if (this.e.c() < n) {
            return a(n);
        } else {
            try {
                String str = new String(this.e.a(), this.e.b(), n, C.UTF8_NAME);
                this.e.a(n);
                return str;
            } catch (UnsupportedEncodingException unused) {
                throw new d("JVM DOES NOT SUPPORT UTF-8");
            }
        }
    }

    public ByteBuffer r() {
        int n = n();
        if (n <= j) {
            c(n);
            if (this.e.c() >= n) {
                ByteBuffer wrap = ByteBuffer.wrap(this.e.a(), this.e.b(), n);
                this.e.a(n);
                return wrap;
            }
            byte[] bArr = new byte[n];
            this.e.c(bArr, 0, n);
            return ByteBuffer.wrap(bArr);
        }
        throw new f(3, "Thrift binary size " + n + " out of range!");
    }
}
