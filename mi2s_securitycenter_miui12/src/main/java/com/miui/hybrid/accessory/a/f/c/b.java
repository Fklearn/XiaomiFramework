package com.miui.hybrid.accessory.a.f.c;

public abstract class b {
    public void a(int i) {
    }

    public byte[] a() {
        return null;
    }

    public int b() {
        return 0;
    }

    public abstract int b(byte[] bArr, int i, int i2);

    public int c() {
        return -1;
    }

    public int c(byte[] bArr, int i, int i2) {
        int i3 = 0;
        while (i3 < i2) {
            int b2 = b(bArr, i + i3, i2 - i3);
            if (b2 > 0) {
                i3 += b2;
            } else {
                throw new c("Cannot read. Remote side has closed. Tried to read " + i2 + " bytes, but only got " + i3 + " bytes.");
            }
        }
        return i3;
    }
}
