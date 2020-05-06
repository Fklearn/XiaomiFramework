package com.miui.hybrid.accessory.a.f.b;

import com.google.android.exoplayer2.C;
import com.miui.hybrid.accessory.a.f.c.b;
import com.miui.hybrid.accessory.a.f.d;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class a extends e {
    private static final j f = new j();

    /* renamed from: a  reason: collision with root package name */
    protected boolean f5505a = false;

    /* renamed from: b  reason: collision with root package name */
    protected boolean f5506b = true;

    /* renamed from: c  reason: collision with root package name */
    protected int f5507c;

    /* renamed from: d  reason: collision with root package name */
    protected boolean f5508d = false;
    private byte[] g = new byte[1];
    private byte[] h = new byte[2];
    private byte[] i = new byte[4];
    private byte[] j = new byte[8];
    private byte[] k = new byte[1];
    private byte[] l = new byte[2];
    private byte[] m = new byte[4];
    private byte[] n = new byte[8];

    /* renamed from: com.miui.hybrid.accessory.a.f.b.a$a  reason: collision with other inner class name */
    public static class C0052a implements g {

        /* renamed from: a  reason: collision with root package name */
        protected boolean f5509a;

        /* renamed from: b  reason: collision with root package name */
        protected boolean f5510b;

        /* renamed from: c  reason: collision with root package name */
        protected int f5511c;

        public C0052a() {
            this(false, true);
        }

        public C0052a(boolean z, boolean z2) {
            this(z, z2, 0);
        }

        public C0052a(boolean z, boolean z2, int i) {
            this.f5509a = false;
            this.f5510b = true;
            this.f5509a = z;
            this.f5510b = z2;
            this.f5511c = i;
        }

        public e a(b bVar) {
            a aVar = new a(bVar, this.f5509a, this.f5510b);
            int i = this.f5511c;
            if (i != 0) {
                aVar.b(i);
            }
            return aVar;
        }
    }

    public a(b bVar, boolean z, boolean z2) {
        super(bVar);
        this.f5505a = z;
        this.f5506b = z2;
    }

    private int a(byte[] bArr, int i2, int i3) {
        c(i3);
        return this.e.c(bArr, i2, i3);
    }

    public j a() {
        return f;
    }

    public String a(int i2) {
        try {
            c(i2);
            byte[] bArr = new byte[i2];
            this.e.c(bArr, 0, i2);
            return new String(bArr, C.UTF8_NAME);
        } catch (UnsupportedEncodingException unused) {
            throw new d("JVM DOES NOT SUPPORT UTF-8");
        }
    }

    public void b() {
    }

    public void b(int i2) {
        this.f5507c = i2;
        this.f5508d = true;
    }

    public b c() {
        byte l2 = l();
        return new b("", l2, l2 == 0 ? 0 : m());
    }

    /* access modifiers changed from: protected */
    public void c(int i2) {
        if (i2 < 0) {
            throw new d("Negative length: " + i2);
        } else if (this.f5508d) {
            this.f5507c -= i2;
            if (this.f5507c < 0) {
                throw new d("Message length exceeded: " + i2);
            }
        }
    }

    public void d() {
    }

    public d e() {
        return new d(l(), l(), n());
    }

    public void f() {
    }

    public c g() {
        return new c(l(), n());
    }

    public void h() {
    }

    public i i() {
        return new i(l(), n());
    }

    public void j() {
    }

    public boolean k() {
        return l() == 1;
    }

    public byte l() {
        if (this.e.c() >= 1) {
            byte b2 = this.e.a()[this.e.b()];
            this.e.a(1);
            return b2;
        }
        a(this.k, 0, 1);
        return this.k[0];
    }

    public short m() {
        byte[] bArr = this.l;
        int i2 = 0;
        if (this.e.c() >= 2) {
            bArr = this.e.a();
            i2 = this.e.b();
            this.e.a(2);
        } else {
            a(this.l, 0, 2);
        }
        return (short) ((bArr[i2 + 1] & 255) | ((bArr[i2] & 255) << 8));
    }

    public int n() {
        byte[] bArr = this.m;
        int i2 = 0;
        if (this.e.c() >= 4) {
            bArr = this.e.a();
            i2 = this.e.b();
            this.e.a(4);
        } else {
            a(this.m, 0, 4);
        }
        return (bArr[i2 + 3] & 255) | ((bArr[i2] & 255) << 24) | ((bArr[i2 + 1] & 255) << 16) | ((bArr[i2 + 2] & 255) << 8);
    }

    public long o() {
        byte[] bArr = this.n;
        int i2 = 0;
        if (this.e.c() >= 8) {
            bArr = this.e.a();
            i2 = this.e.b();
            this.e.a(8);
        } else {
            a(this.n, 0, 8);
        }
        return ((long) (bArr[i2 + 7] & 255)) | (((long) (bArr[i2] & 255)) << 56) | (((long) (bArr[i2 + 1] & 255)) << 48) | (((long) (bArr[i2 + 2] & 255)) << 40) | (((long) (bArr[i2 + 3] & 255)) << 32) | (((long) (bArr[i2 + 4] & 255)) << 24) | (((long) (bArr[i2 + 5] & 255)) << 16) | (((long) (bArr[i2 + 6] & 255)) << 8);
    }

    public double p() {
        return Double.longBitsToDouble(o());
    }

    public String q() {
        int n2 = n();
        if (this.e.c() < n2) {
            return a(n2);
        }
        try {
            String str = new String(this.e.a(), this.e.b(), n2, C.UTF8_NAME);
            this.e.a(n2);
            return str;
        } catch (UnsupportedEncodingException unused) {
            throw new d("JVM DOES NOT SUPPORT UTF-8");
        }
    }

    public ByteBuffer r() {
        int n2 = n();
        c(n2);
        if (this.e.c() >= n2) {
            ByteBuffer wrap = ByteBuffer.wrap(this.e.a(), this.e.b(), n2);
            this.e.a(n2);
            return wrap;
        }
        byte[] bArr = new byte[n2];
        this.e.c(bArr, 0, n2);
        return ByteBuffer.wrap(bArr);
    }
}
