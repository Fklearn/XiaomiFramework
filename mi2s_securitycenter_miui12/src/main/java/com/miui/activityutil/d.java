package com.miui.activityutil;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Cipher;

public final class d extends FilterOutputStream {

    /* renamed from: a  reason: collision with root package name */
    private static final int f2279a = 117;

    /* renamed from: b  reason: collision with root package name */
    private final Cipher f2280b;

    /* renamed from: c  reason: collision with root package name */
    private final byte[] f2281c = new byte[117];

    /* renamed from: d  reason: collision with root package name */
    private int f2282d;

    public d(OutputStream outputStream, Cipher cipher) {
        super(outputStream);
        this.f2280b = cipher;
    }

    public final void close() {
        try {
            if (this.f2282d > 0) {
                this.out.write(this.f2280b.doFinal(this.f2281c, 0, this.f2282d));
            }
            if (this.out != null) {
                this.out.flush();
            }
            OutputStream outputStream = this.out;
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        } catch (Throwable th) {
            OutputStream outputStream2 = this.out;
            if (outputStream2 != null) {
                outputStream2.close();
            }
            throw th;
        }
    }

    public final void flush() {
        this.out.flush();
    }

    public final void write(int i) {
        write(new byte[]{(byte) i}, 0, 1);
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0023 A[SYNTHETIC, Splitter:B:13:0x0023] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0044 A[Catch:{ Exception -> 0x0040 }, LOOP:0: B:17:0x0042->B:18:0x0044, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0056 A[Catch:{ Exception -> 0x0040 }] */
    /* JADX WARNING: Removed duplicated region for block: B:25:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void write(byte[] r6, int r7, int r8) {
        /*
            r5 = this;
            if (r8 != 0) goto L_0x0003
            return
        L_0x0003:
            int r0 = r5.f2282d
            int r1 = r0 + r8
            r2 = 0
            r3 = 117(0x75, float:1.64E-43)
            if (r0 != 0) goto L_0x000e
            if (r1 >= r3) goto L_0x0020
        L_0x000e:
            if (r1 >= r3) goto L_0x001a
            byte[] r0 = r5.f2281c
            int r2 = r5.f2282d
            java.lang.System.arraycopy(r6, r7, r0, r2, r8)
            r5.f2282d = r1
            return
        L_0x001a:
            int r0 = r5.f2282d
            if (r0 == 0) goto L_0x0020
            r0 = 1
            goto L_0x0021
        L_0x0020:
            r0 = r2
        L_0x0021:
            if (r0 == 0) goto L_0x0042
            int r0 = r5.f2282d     // Catch:{ Exception -> 0x0040 }
            int r0 = 117 - r0
            byte[] r1 = r5.f2281c     // Catch:{ Exception -> 0x0040 }
            int r4 = r5.f2282d     // Catch:{ Exception -> 0x0040 }
            java.lang.System.arraycopy(r6, r7, r1, r4, r0)     // Catch:{ Exception -> 0x0040 }
            byte[] r1 = r5.f2281c     // Catch:{ Exception -> 0x0040 }
            javax.crypto.Cipher r4 = r5.f2280b     // Catch:{ Exception -> 0x0040 }
            byte[] r1 = r4.doFinal(r1, r2, r3)     // Catch:{ Exception -> 0x0040 }
            java.io.OutputStream r4 = r5.out     // Catch:{ Exception -> 0x0040 }
            r4.write(r1)     // Catch:{ Exception -> 0x0040 }
            r5.f2282d = r2     // Catch:{ Exception -> 0x0040 }
            int r8 = r8 - r0
            int r7 = r7 + r0
            goto L_0x0042
        L_0x0040:
            r6 = move-exception
            goto L_0x005e
        L_0x0042:
            if (r8 < r3) goto L_0x0054
            javax.crypto.Cipher r0 = r5.f2280b     // Catch:{ Exception -> 0x0040 }
            byte[] r0 = r0.doFinal(r6, r7, r3)     // Catch:{ Exception -> 0x0040 }
            java.io.OutputStream r1 = r5.out     // Catch:{ Exception -> 0x0040 }
            r1.write(r0)     // Catch:{ Exception -> 0x0040 }
            int r7 = r7 + 117
            int r8 = r8 + -117
            goto L_0x0042
        L_0x0054:
            if (r8 <= 0) goto L_0x0068
            byte[] r0 = r5.f2281c     // Catch:{ Exception -> 0x0040 }
            java.lang.System.arraycopy(r6, r7, r0, r2, r8)     // Catch:{ Exception -> 0x0040 }
            r5.f2282d = r8     // Catch:{ Exception -> 0x0040 }
            goto L_0x0068
        L_0x005e:
            java.io.IOException r7 = new java.io.IOException
            java.lang.String r6 = r6.getMessage()
            r7.<init>(r6)
            throw r7
        L_0x0068:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.activityutil.d.write(byte[], int, int):void");
    }
}
