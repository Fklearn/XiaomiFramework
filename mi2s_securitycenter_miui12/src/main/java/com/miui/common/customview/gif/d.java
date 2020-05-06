package com.miui.common.customview.gif;

import android.util.Log;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private final byte[] f3817a = new byte[256];

    /* renamed from: b  reason: collision with root package name */
    private ByteBuffer f3818b;

    /* renamed from: c  reason: collision with root package name */
    private c f3819c;

    /* renamed from: d  reason: collision with root package name */
    private int f3820d = 0;

    private int[] a(int i) {
        byte[] bArr = new byte[(i * 3)];
        int[] iArr = null;
        try {
            this.f3818b.get(bArr);
            iArr = new int[256];
            int i2 = 0;
            int i3 = 0;
            while (i2 < i) {
                int i4 = i3 + 1;
                int i5 = i4 + 1;
                int i6 = i5 + 1;
                int i7 = i2 + 1;
                iArr[i2] = ((bArr[i3] & 255) << 16) | -16777216 | ((bArr[i4] & 255) << 8) | (bArr[i5] & 255);
                i3 = i6;
                i2 = i7;
            }
        } catch (BufferUnderflowException e) {
            if (Log.isLoggable("GifHeaderParser", 3)) {
                Log.d("GifHeaderParser", "Format Error Reading Color Table", e);
            }
            this.f3819c.f3814b = 1;
        }
        return iArr;
    }

    private void b(int i) {
        boolean z = false;
        while (!z && !b() && this.f3819c.f3815c <= i) {
            int c2 = c();
            if (c2 == 33) {
                int c3 = c();
                if (c3 != 1) {
                    if (c3 == 249) {
                        this.f3819c.f3816d = new b();
                        g();
                    } else if (c3 != 254 && c3 == 255) {
                        e();
                        String str = "";
                        for (int i2 = 0; i2 < 11; i2++) {
                            str = str + ((char) this.f3817a[i2]);
                        }
                        if (str.equals("NETSCAPE2.0")) {
                            j();
                        }
                    }
                }
                m();
            } else if (c2 == 44) {
                c cVar = this.f3819c;
                if (cVar.f3816d == null) {
                    cVar.f3816d = new b();
                }
                d();
            } else if (c2 != 59) {
                this.f3819c.f3814b = 1;
            } else {
                z = true;
            }
        }
    }

    private boolean b() {
        return this.f3819c.f3814b != 0;
    }

    private int c() {
        try {
            return this.f3818b.get() & 255;
        } catch (Exception unused) {
            this.f3819c.f3814b = 1;
            return 0;
        }
    }

    private void d() {
        int[] iArr;
        b bVar;
        this.f3819c.f3816d.f3809a = k();
        this.f3819c.f3816d.f3810b = k();
        this.f3819c.f3816d.f3811c = k();
        this.f3819c.f3816d.f3812d = k();
        int c2 = c();
        boolean z = false;
        boolean z2 = (c2 & 128) != 0;
        int pow = (int) Math.pow(2.0d, (double) ((c2 & 7) + 1));
        b bVar2 = this.f3819c.f3816d;
        if ((c2 & 64) != 0) {
            z = true;
        }
        bVar2.e = z;
        if (z2) {
            bVar = this.f3819c.f3816d;
            iArr = a(pow);
        } else {
            bVar = this.f3819c.f3816d;
            iArr = null;
        }
        bVar.k = iArr;
        this.f3819c.f3816d.j = this.f3818b.position();
        n();
        if (!b()) {
            c cVar = this.f3819c;
            cVar.f3815c++;
            cVar.e.add(cVar.f3816d);
        }
    }

    private int e() {
        this.f3820d = c();
        int i = 0;
        if (this.f3820d > 0) {
            int i2 = 0;
            while (i < this.f3820d) {
                try {
                    i2 = this.f3820d - i;
                    this.f3818b.get(this.f3817a, i, i2);
                    i += i2;
                } catch (Exception e) {
                    if (Log.isLoggable("GifHeaderParser", 3)) {
                        Log.d("GifHeaderParser", "Error Reading Block n: " + i + " count: " + i2 + " blockSize: " + this.f3820d, e);
                    }
                    this.f3819c.f3814b = 1;
                }
            }
        }
        return i;
    }

    private void f() {
        b(Integer.MAX_VALUE);
    }

    private void g() {
        c();
        int c2 = c();
        b bVar = this.f3819c.f3816d;
        bVar.g = (c2 & 28) >> 2;
        boolean z = true;
        if (bVar.g == 0) {
            bVar.g = 1;
        }
        b bVar2 = this.f3819c.f3816d;
        if ((c2 & 1) == 0) {
            z = false;
        }
        bVar2.f = z;
        int k = k();
        if (k < 2) {
            k = 10;
        }
        b bVar3 = this.f3819c.f3816d;
        bVar3.i = k * 10;
        bVar3.h = c();
        c();
    }

    private void h() {
        String str = "";
        for (int i = 0; i < 6; i++) {
            str = str + ((char) c());
        }
        if (!str.startsWith("GIF")) {
            this.f3819c.f3814b = 1;
            return;
        }
        i();
        if (this.f3819c.h && !b()) {
            c cVar = this.f3819c;
            cVar.f3813a = a(cVar.i);
            c cVar2 = this.f3819c;
            cVar2.l = cVar2.f3813a[cVar2.j];
        }
    }

    private void i() {
        this.f3819c.f = k();
        this.f3819c.g = k();
        int c2 = c();
        this.f3819c.h = (c2 & 128) != 0;
        c cVar = this.f3819c;
        cVar.i = 2 << (c2 & 7);
        cVar.j = c();
        this.f3819c.k = c();
    }

    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP_START, MTH_ENTER_BLOCK] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void j() {
        /*
            r3 = this;
        L_0x0000:
            r3.e()
            byte[] r0 = r3.f3817a
            r1 = 0
            byte r1 = r0[r1]
            r2 = 1
            if (r1 != r2) goto L_0x001b
            byte r1 = r0[r2]
            r1 = r1 & 255(0xff, float:3.57E-43)
            r2 = 2
            byte r0 = r0[r2]
            r0 = r0 & 255(0xff, float:3.57E-43)
            com.miui.common.customview.gif.c r2 = r3.f3819c
            int r0 = r0 << 8
            r0 = r0 | r1
            r2.m = r0
        L_0x001b:
            int r0 = r3.f3820d
            if (r0 <= 0) goto L_0x0025
            boolean r0 = r3.b()
            if (r0 == 0) goto L_0x0000
        L_0x0025:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.customview.gif.d.j():void");
    }

    private int k() {
        return this.f3818b.getShort();
    }

    private void l() {
        this.f3818b = null;
        Arrays.fill(this.f3817a, (byte) 0);
        this.f3819c = new c();
        this.f3820d = 0;
    }

    private void m() {
        int c2;
        do {
            c2 = c();
            ByteBuffer byteBuffer = this.f3818b;
            byteBuffer.position(byteBuffer.position() + c2);
        } while (c2 > 0);
    }

    private void n() {
        c();
        m();
    }

    public c a() {
        if (this.f3818b == null) {
            throw new IllegalStateException("You must call setData() before parseHeader()");
        } else if (b()) {
            return this.f3819c;
        } else {
            h();
            if (!b()) {
                f();
                c cVar = this.f3819c;
                if (cVar.f3815c < 0) {
                    cVar.f3814b = 1;
                }
            }
            return this.f3819c;
        }
    }

    public d a(ByteBuffer byteBuffer) {
        l();
        this.f3818b = byteBuffer.asReadOnlyBuffer();
        this.f3818b.position(0);
        this.f3818b.order(ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public d a(byte[] bArr) {
        if (bArr != null) {
            a(ByteBuffer.wrap(bArr));
        } else {
            this.f3818b = null;
            this.f3819c.f3814b = 2;
        }
        return this;
    }
}
