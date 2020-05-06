package com.miui.common.customview.gif;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;

class a {

    /* renamed from: a  reason: collision with root package name */
    private static final String f3805a = "a";

    /* renamed from: b  reason: collision with root package name */
    private int[] f3806b;

    /* renamed from: c  reason: collision with root package name */
    private ByteBuffer f3807c;

    /* renamed from: d  reason: collision with root package name */
    private byte[] f3808d;
    private byte[] e;
    private int f;
    private int g;
    private d h;
    private short[] i;
    private byte[] j;
    private byte[] k;
    private byte[] l;
    private int[] m;
    private int n;
    private c o;
    private C0045a p;
    private Bitmap q;
    private boolean r;
    private int s;
    private int t;
    private int u;
    private int v;
    private boolean w;

    /* renamed from: com.miui.common.customview.gif.a$a  reason: collision with other inner class name */
    interface C0045a {
        Bitmap a(int i, int i2, Bitmap.Config config);

        byte[] a(int i);

        int[] b(int i);
    }

    a() {
        this(new g());
    }

    a(C0045a aVar) {
        this.f = 0;
        this.g = 0;
        this.p = aVar;
        this.o = new c();
    }

    private int a(int i2, int i3, int i4) {
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        int i9 = 0;
        for (int i10 = i2; i10 < this.t + i2; i10++) {
            byte[] bArr = this.l;
            if (i10 >= bArr.length || i10 >= i3) {
                break;
            }
            int i11 = this.f3806b[bArr[i10] & 255];
            if (i11 != 0) {
                i5 += (i11 >> 24) & 255;
                i6 += (i11 >> 16) & 255;
                i7 += (i11 >> 8) & 255;
                i8 += i11 & 255;
                i9++;
            }
        }
        int i12 = i2 + i4;
        for (int i13 = i12; i13 < this.t + i12; i13++) {
            byte[] bArr2 = this.l;
            if (i13 >= bArr2.length || i13 >= i3) {
                break;
            }
            int i14 = this.f3806b[bArr2[i13] & 255];
            if (i14 != 0) {
                i5 += (i14 >> 24) & 255;
                i6 += (i14 >> 16) & 255;
                i7 += (i14 >> 8) & 255;
                i8 += i14 & 255;
                i9++;
            }
        }
        if (i9 == 0) {
            return 0;
        }
        return ((i5 / i9) << 24) | ((i6 / i9) << 16) | ((i7 / i9) << 8) | (i8 / i9);
    }

    private Bitmap a(b bVar, b bVar2) {
        int i2;
        int i3;
        int i4;
        Bitmap bitmap;
        int i5;
        b bVar3 = bVar;
        b bVar4 = bVar2;
        int[] iArr = this.m;
        int i6 = 3;
        int i7 = 2;
        int i8 = 0;
        int i9 = 1;
        if (bVar4 != null && (i4 = bVar4.g) > 0) {
            if (i4 == 2) {
                if (!bVar3.f) {
                    i5 = this.o.l;
                } else {
                    if (this.n == 0) {
                        this.w = true;
                    }
                    i5 = 0;
                }
                Arrays.fill(iArr, i5);
            } else if (i4 == 3 && (bitmap = this.q) != null) {
                int i10 = this.v;
                bitmap.getPixels(iArr, 0, i10, 0, 0, i10, this.u);
            }
        }
        a(bVar);
        int i11 = bVar3.f3812d;
        int i12 = this.t;
        int i13 = i11 / i12;
        int i14 = bVar3.f3810b / i12;
        int i15 = bVar3.f3811c / i12;
        int i16 = bVar3.f3809a / i12;
        boolean z = this.n == 0;
        int i17 = 8;
        int i18 = 0;
        int i19 = 1;
        while (i8 < i13) {
            if (bVar3.e) {
                if (i18 >= i13) {
                    i19++;
                    if (i19 == i7) {
                        i18 = 4;
                    } else if (i19 == i6) {
                        i18 = i7;
                        i17 = 4;
                    } else if (i19 == 4) {
                        i17 = i7;
                        i18 = i9;
                    }
                }
                i3 = i18 + i17;
            } else {
                i3 = i18;
                i18 = i8;
            }
            int i20 = i18 + i14;
            if (i20 < this.u) {
                int i21 = this.v;
                int i22 = i20 * i21;
                int i23 = i22 + i16;
                int i24 = i23 + i15;
                if (i22 + i21 < i24) {
                    i24 = i22 + i21;
                }
                int i25 = this.t;
                int i26 = i8 * i25 * bVar3.f3811c;
                int i27 = ((i24 - i23) * i25) + i26;
                int i28 = i23;
                while (i28 < i24) {
                    int i29 = i13;
                    int a2 = a(i26, i27, bVar3.f3811c);
                    if (a2 != 0) {
                        iArr[i28] = a2;
                    } else if (!this.w && z) {
                        this.w = true;
                    }
                    i26 += this.t;
                    i28++;
                    i13 = i29;
                }
            }
            i8++;
            i13 = i13;
            i18 = i3;
            i6 = 3;
            i7 = 2;
            i9 = 1;
        }
        if (this.r && ((i2 = bVar3.g) == 0 || i2 == 1)) {
            if (this.q == null) {
                this.q = h();
            }
            Bitmap bitmap2 = this.q;
            int i30 = this.v;
            bitmap2.setPixels(iArr, 0, i30, 0, 0, i30, this.u);
        }
        Bitmap h2 = h();
        int i31 = this.v;
        h2.setPixels(iArr, 0, i31, 0, 0, i31, this.u);
        return h2;
    }

    @TargetApi(12)
    private static void a(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= 12) {
            bitmap.setHasAlpha(true);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v0, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v1, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v6, resolved type: byte} */
    /* JADX WARNING: Incorrect type for immutable var: ssa=short, code=int, for r1v22, types: [short] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(com.miui.common.customview.gif.b r28) {
        /*
            r27 = this;
            r0 = r27
            r1 = r28
            r2 = 0
            r0.f = r2
            r0.g = r2
            if (r1 == 0) goto L_0x0012
            java.nio.ByteBuffer r3 = r0.f3807c
            int r4 = r1.j
            r3.position(r4)
        L_0x0012:
            if (r1 != 0) goto L_0x001b
            com.miui.common.customview.gif.c r1 = r0.o
            int r3 = r1.f
            int r1 = r1.g
            goto L_0x001f
        L_0x001b:
            int r3 = r1.f3811c
            int r1 = r1.f3812d
        L_0x001f:
            int r3 = r3 * r1
            byte[] r1 = r0.l
            if (r1 == 0) goto L_0x0027
            int r1 = r1.length
            if (r1 >= r3) goto L_0x002f
        L_0x0027:
            com.miui.common.customview.gif.a$a r1 = r0.p
            byte[] r1 = r1.a(r3)
            r0.l = r1
        L_0x002f:
            short[] r1 = r0.i
            r4 = 4096(0x1000, float:5.74E-42)
            if (r1 != 0) goto L_0x0039
            short[] r1 = new short[r4]
            r0.i = r1
        L_0x0039:
            byte[] r1 = r0.j
            if (r1 != 0) goto L_0x0041
            byte[] r1 = new byte[r4]
            r0.j = r1
        L_0x0041:
            byte[] r1 = r0.k
            if (r1 != 0) goto L_0x004b
            r1 = 4097(0x1001, float:5.741E-42)
            byte[] r1 = new byte[r1]
            r0.k = r1
        L_0x004b:
            int r1 = r27.j()
            r5 = 1
            int r6 = r5 << r1
            int r7 = r6 + 1
            int r8 = r6 + 2
            int r1 = r1 + r5
            int r9 = r5 << r1
            int r9 = r9 - r5
            r10 = r2
        L_0x005b:
            if (r10 >= r6) goto L_0x0069
            short[] r11 = r0.i
            r11[r10] = r2
            byte[] r11 = r0.j
            byte r12 = (byte) r10
            r11[r10] = r12
            int r10 = r10 + 1
            goto L_0x005b
        L_0x0069:
            r10 = -1
            r21 = r1
            r11 = r2
            r12 = r11
            r13 = r12
            r14 = r13
            r15 = r14
            r16 = r15
            r17 = r16
            r18 = r17
            r19 = r8
            r20 = r9
            r22 = r10
        L_0x007d:
            if (r11 >= r3) goto L_0x0178
            r2 = 3
            if (r12 != 0) goto L_0x008d
            int r12 = r27.i()
            if (r12 > 0) goto L_0x008c
            r0.s = r2
            goto L_0x0178
        L_0x008c:
            r14 = 0
        L_0x008d:
            byte[] r4 = r0.f3808d
            byte r4 = r4[r14]
            r4 = r4 & 255(0xff, float:3.57E-43)
            int r4 = r4 << r15
            int r13 = r13 + r4
            int r15 = r15 + 8
            int r14 = r14 + r5
            int r12 = r12 + r10
            r23 = r17
            r4 = r21
            r5 = r22
            r26 = r16
            r16 = r11
            r11 = r19
            r19 = r18
            r18 = r26
        L_0x00a9:
            if (r15 < r4) goto L_0x015c
            r10 = r13 & r20
            int r13 = r13 >> r4
            int r15 = r15 - r4
            if (r10 != r6) goto L_0x00b8
            r4 = r1
            r11 = r8
            r20 = r9
            r5 = -1
        L_0x00b6:
            r10 = -1
            goto L_0x00a9
        L_0x00b8:
            if (r10 <= r11) goto L_0x00bd
            r0.s = r2
            goto L_0x00bf
        L_0x00bd:
            if (r10 != r7) goto L_0x00cc
        L_0x00bf:
            r21 = r4
            r22 = r5
            r17 = r23
            r2 = 0
            r4 = 4096(0x1000, float:5.74E-42)
            r5 = 1
            r10 = -1
            goto L_0x016c
        L_0x00cc:
            r2 = -1
            if (r5 != r2) goto L_0x00e0
            byte[] r5 = r0.k
            int r21 = r19 + 1
            byte[] r2 = r0.j
            byte r2 = r2[r10]
            r5[r19] = r2
            r5 = r10
            r23 = r5
            r19 = r21
            r2 = 3
            goto L_0x00b6
        L_0x00e0:
            if (r10 < r11) goto L_0x00f1
            byte[] r2 = r0.k
            int r21 = r19 + 1
            r24 = r1
            r1 = r23
            byte r1 = (byte) r1
            r2[r19] = r1
            r1 = r5
            r19 = r21
            goto L_0x00f4
        L_0x00f1:
            r24 = r1
            r1 = r10
        L_0x00f4:
            if (r1 < r6) goto L_0x010b
            byte[] r2 = r0.k
            int r21 = r19 + 1
            r23 = r6
            byte[] r6 = r0.j
            byte r6 = r6[r1]
            r2[r19] = r6
            short[] r2 = r0.i
            short r1 = r2[r1]
            r19 = r21
            r6 = r23
            goto L_0x00f4
        L_0x010b:
            r23 = r6
            byte[] r2 = r0.j
            byte r1 = r2[r1]
            r1 = r1 & 255(0xff, float:3.57E-43)
            byte[] r6 = r0.k
            int r21 = r19 + 1
            r25 = r7
            byte r7 = (byte) r1
            r6[r19] = r7
            r6 = 4096(0x1000, float:5.74E-42)
            if (r11 >= r6) goto L_0x0139
            short[] r6 = r0.i
            short r5 = (short) r5
            r6[r11] = r5
            r2[r11] = r7
            int r11 = r11 + 1
            r2 = r11 & r20
            if (r2 != 0) goto L_0x0136
            r2 = 4096(0x1000, float:5.74E-42)
            if (r11 >= r2) goto L_0x013a
            int r4 = r4 + 1
            int r20 = r20 + r11
            goto L_0x013a
        L_0x0136:
            r2 = 4096(0x1000, float:5.74E-42)
            goto L_0x013a
        L_0x0139:
            r2 = r6
        L_0x013a:
            r19 = r21
        L_0x013c:
            if (r19 <= 0) goto L_0x014f
            byte[] r5 = r0.l
            int r6 = r18 + 1
            byte[] r7 = r0.k
            int r19 = r19 + -1
            byte r7 = r7[r19]
            r5[r18] = r7
            int r16 = r16 + 1
            r18 = r6
            goto L_0x013c
        L_0x014f:
            r5 = r10
            r6 = r23
            r7 = r25
            r2 = 3
            r10 = -1
            r23 = r1
            r1 = r24
            goto L_0x00a9
        L_0x015c:
            r24 = r1
            r1 = r23
            r17 = r1
            r21 = r4
            r22 = r5
            r1 = r24
            r2 = 0
            r4 = 4096(0x1000, float:5.74E-42)
            r5 = 1
        L_0x016c:
            r26 = r19
            r19 = r11
            r11 = r16
            r16 = r18
            r18 = r26
            goto L_0x007d
        L_0x0178:
            r1 = r16
        L_0x017a:
            if (r1 >= r3) goto L_0x0184
            byte[] r2 = r0.l
            r4 = 0
            r2[r1] = r4
            int r1 = r1 + 1
            goto L_0x017a
        L_0x0184:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.customview.gif.a.a(com.miui.common.customview.gif.b):void");
    }

    private d g() {
        if (this.h == null) {
            this.h = new d();
        }
        return this.h;
    }

    private Bitmap h() {
        Bitmap a2 = this.p.a(this.v, this.u, this.w ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        a(a2);
        return a2;
    }

    private int i() {
        int j2 = j();
        if (j2 > 0) {
            try {
                if (this.f3808d == null) {
                    this.f3808d = this.p.a(255);
                }
                int i2 = this.f - this.g;
                if (i2 >= j2) {
                    System.arraycopy(this.e, this.g, this.f3808d, 0, j2);
                    this.g += j2;
                } else if (this.f3807c.remaining() + i2 >= j2) {
                    System.arraycopy(this.e, this.g, this.f3808d, 0, i2);
                    this.g = this.f;
                    k();
                    int i3 = j2 - i2;
                    System.arraycopy(this.e, 0, this.f3808d, i2, i3);
                    this.g += i3;
                } else {
                    this.s = 1;
                }
            } catch (Exception e2) {
                Log.w(f3805a, "Error Reading Block", e2);
                this.s = 1;
            }
        }
        return j2;
    }

    private int j() {
        try {
            k();
            byte[] bArr = this.e;
            int i2 = this.g;
            this.g = i2 + 1;
            return bArr[i2] & 255;
        } catch (Exception unused) {
            this.s = 1;
            return 0;
        }
    }

    private void k() {
        if (this.f <= this.g) {
            if (this.e == null) {
                this.e = this.p.a(16384);
            }
            this.g = 0;
            this.f = Math.min(this.f3807c.remaining(), 16384);
            this.f3807c.get(this.e, 0, this.f);
        }
    }

    /* access modifiers changed from: package-private */
    public int a(int i2) {
        if (i2 >= 0) {
            c cVar = this.o;
            if (i2 < cVar.f3815c) {
                return cVar.e.get(i2).i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public synchronized int a(byte[] bArr) {
        d g2 = g();
        g2.a(bArr);
        this.o = g2.a();
        if (bArr != null) {
            a(this.o, bArr);
        }
        return this.s;
    }

    /* access modifiers changed from: package-private */
    public void a() {
        this.n = (this.n + 1) % this.o.f3815c;
    }

    /* access modifiers changed from: package-private */
    public synchronized void a(c cVar, ByteBuffer byteBuffer) {
        a(cVar, byteBuffer, 1);
    }

    /* access modifiers changed from: package-private */
    public synchronized void a(c cVar, ByteBuffer byteBuffer, int i2) {
        if (i2 > 0) {
            int highestOneBit = Integer.highestOneBit(i2);
            this.s = 0;
            this.o = cVar;
            this.w = false;
            this.n = -1;
            this.f3807c = byteBuffer.asReadOnlyBuffer();
            this.f3807c.position(0);
            this.f3807c.order(ByteOrder.LITTLE_ENDIAN);
            this.r = false;
            Iterator<b> it = cVar.e.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().g == 3) {
                        this.r = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            this.t = highestOneBit;
            this.l = this.p.a(cVar.f * cVar.g);
            this.m = this.p.b((cVar.f / highestOneBit) * (cVar.g / highestOneBit));
            this.v = cVar.f / highestOneBit;
            this.u = cVar.g / highestOneBit;
        } else {
            throw new IllegalArgumentException("Sample size must be >=0, not: " + i2);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void a(c cVar, byte[] bArr) {
        a(cVar, ByteBuffer.wrap(bArr));
    }

    /* access modifiers changed from: package-private */
    public int b() {
        return this.o.f3815c;
    }

    /* access modifiers changed from: package-private */
    public int c() {
        return this.o.g;
    }

    /* access modifiers changed from: package-private */
    public int d() {
        int i2;
        if (this.o.f3815c <= 0 || (i2 = this.n) < 0) {
            return 0;
        }
        return a(i2);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00f0, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized android.graphics.Bitmap e() {
        /*
            r10 = this;
            monitor-enter(r10)
            com.miui.common.customview.gif.c r0 = r10.o     // Catch:{ all -> 0x00f1 }
            int r0 = r0.f3815c     // Catch:{ all -> 0x00f1 }
            r1 = 3
            r2 = 1
            if (r0 <= 0) goto L_0x000d
            int r0 = r10.n     // Catch:{ all -> 0x00f1 }
            if (r0 >= 0) goto L_0x003b
        L_0x000d:
            java.lang.String r0 = f3805a     // Catch:{ all -> 0x00f1 }
            boolean r0 = android.util.Log.isLoggable(r0, r1)     // Catch:{ all -> 0x00f1 }
            if (r0 == 0) goto L_0x0039
            java.lang.String r0 = f3805a     // Catch:{ all -> 0x00f1 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00f1 }
            r3.<init>()     // Catch:{ all -> 0x00f1 }
            java.lang.String r4 = "unable to decode frame, frameCount="
            r3.append(r4)     // Catch:{ all -> 0x00f1 }
            com.miui.common.customview.gif.c r4 = r10.o     // Catch:{ all -> 0x00f1 }
            int r4 = r4.f3815c     // Catch:{ all -> 0x00f1 }
            r3.append(r4)     // Catch:{ all -> 0x00f1 }
            java.lang.String r4 = " framePointer="
            r3.append(r4)     // Catch:{ all -> 0x00f1 }
            int r4 = r10.n     // Catch:{ all -> 0x00f1 }
            r3.append(r4)     // Catch:{ all -> 0x00f1 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00f1 }
            android.util.Log.d(r0, r3)     // Catch:{ all -> 0x00f1 }
        L_0x0039:
            r10.s = r2     // Catch:{ all -> 0x00f1 }
        L_0x003b:
            int r0 = r10.s     // Catch:{ all -> 0x00f1 }
            r3 = 0
            if (r0 == r2) goto L_0x00cf
            int r0 = r10.s     // Catch:{ all -> 0x00f1 }
            r4 = 2
            if (r0 != r4) goto L_0x0047
            goto L_0x00cf
        L_0x0047:
            r0 = 0
            r10.s = r0     // Catch:{ all -> 0x00f1 }
            com.miui.common.customview.gif.c r4 = r10.o     // Catch:{ all -> 0x00f1 }
            java.util.List<com.miui.common.customview.gif.b> r4 = r4.e     // Catch:{ all -> 0x00f1 }
            int r5 = r10.n     // Catch:{ all -> 0x00f1 }
            java.lang.Object r4 = r4.get(r5)     // Catch:{ all -> 0x00f1 }
            com.miui.common.customview.gif.b r4 = (com.miui.common.customview.gif.b) r4     // Catch:{ all -> 0x00f1 }
            int r5 = r10.n     // Catch:{ all -> 0x00f1 }
            int r5 = r5 - r2
            if (r5 < 0) goto L_0x0066
            com.miui.common.customview.gif.c r6 = r10.o     // Catch:{ all -> 0x00f1 }
            java.util.List<com.miui.common.customview.gif.b> r6 = r6.e     // Catch:{ all -> 0x00f1 }
            java.lang.Object r5 = r6.get(r5)     // Catch:{ all -> 0x00f1 }
        L_0x0063:
            com.miui.common.customview.gif.b r5 = (com.miui.common.customview.gif.b) r5     // Catch:{ all -> 0x00f1 }
            goto L_0x0074
        L_0x0066:
            com.miui.common.customview.gif.c r5 = r10.o     // Catch:{ all -> 0x00f1 }
            java.util.List<com.miui.common.customview.gif.b> r5 = r5.e     // Catch:{ all -> 0x00f1 }
            int r6 = r10.b()     // Catch:{ all -> 0x00f1 }
            int r6 = r6 - r2
            java.lang.Object r5 = r5.get(r6)     // Catch:{ all -> 0x00f1 }
            goto L_0x0063
        L_0x0074:
            com.miui.common.customview.gif.c r6 = r10.o     // Catch:{ all -> 0x00f1 }
            int r6 = r6.l     // Catch:{ all -> 0x00f1 }
            int[] r7 = r4.k     // Catch:{ all -> 0x00f1 }
            if (r7 != 0) goto L_0x0083
            com.miui.common.customview.gif.c r7 = r10.o     // Catch:{ all -> 0x00f1 }
            int[] r7 = r7.f3813a     // Catch:{ all -> 0x00f1 }
            r10.f3806b = r7     // Catch:{ all -> 0x00f1 }
            goto L_0x0093
        L_0x0083:
            int[] r7 = r4.k     // Catch:{ all -> 0x00f1 }
            r10.f3806b = r7     // Catch:{ all -> 0x00f1 }
            com.miui.common.customview.gif.c r7 = r10.o     // Catch:{ all -> 0x00f1 }
            int r7 = r7.j     // Catch:{ all -> 0x00f1 }
            int r8 = r4.h     // Catch:{ all -> 0x00f1 }
            if (r7 != r8) goto L_0x0093
            com.miui.common.customview.gif.c r7 = r10.o     // Catch:{ all -> 0x00f1 }
            r7.l = r0     // Catch:{ all -> 0x00f1 }
        L_0x0093:
            boolean r7 = r4.f     // Catch:{ all -> 0x00f1 }
            if (r7 == 0) goto L_0x00a4
            int[] r7 = r10.f3806b     // Catch:{ all -> 0x00f1 }
            int r8 = r4.h     // Catch:{ all -> 0x00f1 }
            r7 = r7[r8]     // Catch:{ all -> 0x00f1 }
            int[] r8 = r10.f3806b     // Catch:{ all -> 0x00f1 }
            int r9 = r4.h     // Catch:{ all -> 0x00f1 }
            r8[r9] = r0     // Catch:{ all -> 0x00f1 }
            r0 = r7
        L_0x00a4:
            int[] r7 = r10.f3806b     // Catch:{ all -> 0x00f1 }
            if (r7 != 0) goto L_0x00bb
            java.lang.String r0 = f3805a     // Catch:{ all -> 0x00f1 }
            boolean r0 = android.util.Log.isLoggable(r0, r1)     // Catch:{ all -> 0x00f1 }
            if (r0 == 0) goto L_0x00b7
            java.lang.String r0 = f3805a     // Catch:{ all -> 0x00f1 }
            java.lang.String r1 = "No Valid Color Table"
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x00f1 }
        L_0x00b7:
            r10.s = r2     // Catch:{ all -> 0x00f1 }
            monitor-exit(r10)
            return r3
        L_0x00bb:
            android.graphics.Bitmap r1 = r10.a((com.miui.common.customview.gif.b) r4, (com.miui.common.customview.gif.b) r5)     // Catch:{ all -> 0x00f1 }
            boolean r2 = r4.f     // Catch:{ all -> 0x00f1 }
            if (r2 == 0) goto L_0x00c9
            int[] r2 = r10.f3806b     // Catch:{ all -> 0x00f1 }
            int r3 = r4.h     // Catch:{ all -> 0x00f1 }
            r2[r3] = r0     // Catch:{ all -> 0x00f1 }
        L_0x00c9:
            com.miui.common.customview.gif.c r0 = r10.o     // Catch:{ all -> 0x00f1 }
            r0.l = r6     // Catch:{ all -> 0x00f1 }
            monitor-exit(r10)
            return r1
        L_0x00cf:
            java.lang.String r0 = f3805a     // Catch:{ all -> 0x00f1 }
            boolean r0 = android.util.Log.isLoggable(r0, r1)     // Catch:{ all -> 0x00f1 }
            if (r0 == 0) goto L_0x00ef
            java.lang.String r0 = f3805a     // Catch:{ all -> 0x00f1 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00f1 }
            r1.<init>()     // Catch:{ all -> 0x00f1 }
            java.lang.String r2 = "Unable to decode frame, status="
            r1.append(r2)     // Catch:{ all -> 0x00f1 }
            int r2 = r10.s     // Catch:{ all -> 0x00f1 }
            r1.append(r2)     // Catch:{ all -> 0x00f1 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00f1 }
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x00f1 }
        L_0x00ef:
            monitor-exit(r10)
            return r3
        L_0x00f1:
            r0 = move-exception
            monitor-exit(r10)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.customview.gif.a.e():android.graphics.Bitmap");
    }

    /* access modifiers changed from: package-private */
    public int f() {
        return this.o.f;
    }
}
