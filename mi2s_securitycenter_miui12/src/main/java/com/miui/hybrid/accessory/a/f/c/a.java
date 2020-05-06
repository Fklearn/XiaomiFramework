package com.miui.hybrid.accessory.a.f.c;

public final class a extends b {

    /* renamed from: a  reason: collision with root package name */
    private byte[] f5527a;

    /* renamed from: b  reason: collision with root package name */
    private int f5528b;

    /* renamed from: c  reason: collision with root package name */
    private int f5529c;

    public void a(int i) {
        this.f5528b += i;
    }

    public void a(byte[] bArr) {
        a(bArr, 0, bArr.length);
    }

    public void a(byte[] bArr, int i, int i2) {
        this.f5527a = bArr;
        this.f5528b = i;
        this.f5529c = i + i2;
    }

    public byte[] a() {
        return this.f5527a;
    }

    public int b() {
        return this.f5528b;
    }

    public int b(byte[] bArr, int i, int i2) {
        int c2 = c();
        if (i2 > c2) {
            i2 = c2;
        }
        if (i2 > 0) {
            System.arraycopy(this.f5527a, this.f5528b, bArr, i, i2);
            a(i2);
        }
        return i2;
    }

    public int c() {
        return this.f5529c - this.f5528b;
    }
}
