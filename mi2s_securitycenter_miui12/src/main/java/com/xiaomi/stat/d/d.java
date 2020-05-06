package com.xiaomi.stat.d;

import java.io.UnsupportedEncodingException;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8509a = "Base64Utils";

    /* renamed from: b  reason: collision with root package name */
    private static char[] f8510b = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    /* renamed from: c  reason: collision with root package name */
    private static byte[] f8511c = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

    public static String a(byte[] bArr) {
        String str;
        StringBuffer stringBuffer = new StringBuffer();
        int length = bArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            int i2 = i + 1;
            byte b2 = bArr[i] & 255;
            if (i2 == length) {
                stringBuffer.append(f8510b[b2 >>> 2]);
                stringBuffer.append(f8510b[(b2 & 3) << 4]);
                str = "==";
                break;
            }
            int i3 = i2 + 1;
            byte b3 = bArr[i2] & 255;
            if (i3 == length) {
                stringBuffer.append(f8510b[b2 >>> 2]);
                stringBuffer.append(f8510b[((b2 & 3) << 4) | ((b3 & 240) >>> 4)]);
                stringBuffer.append(f8510b[(b3 & 15) << 2]);
                str = "=";
                break;
            }
            int i4 = i3 + 1;
            byte b4 = bArr[i3] & 255;
            stringBuffer.append(f8510b[b2 >>> 2]);
            stringBuffer.append(f8510b[((b2 & 3) << 4) | ((b3 & 240) >>> 4)]);
            stringBuffer.append(f8510b[((b3 & 15) << 2) | ((b4 & 192) >>> 6)]);
            stringBuffer.append(f8510b[b4 & 63]);
            i = i4;
        }
        stringBuffer.append(str);
        return stringBuffer.toString();
    }

    public static byte[] a(String str) {
        try {
            return b(str);
        } catch (UnsupportedEncodingException e) {
            k.d(f8509a, "decode e", e);
            return new byte[0];
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0082 A[LOOP:0: B:1:0x000d->B:31:0x0082, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0049 A[EDGE_INSN: B:34:0x0049->B:16:0x0049 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0049 A[EDGE_INSN: B:35:0x0049->B:16:0x0049 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0049 A[EDGE_INSN: B:37:0x0049->B:16:0x0049 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0049 A[EDGE_INSN: B:38:0x0049->B:16:0x0049 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0024 A[LOOP:2: B:8:0x0024->B:11:0x0031, LOOP_START, PHI: r5 
      PHI: (r5v1 int) = (r5v0 int), (r5v9 int) binds: [B:7:0x0021, B:11:0x0031] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static byte[] b(java.lang.String r9) {
        /*
            java.lang.StringBuffer r0 = new java.lang.StringBuffer
            r0.<init>()
            java.lang.String r1 = "US-ASCII"
            byte[] r9 = r9.getBytes(r1)
            int r1 = r9.length
            r2 = 0
        L_0x000d:
            java.lang.String r3 = "iso8859-1"
            if (r2 >= r1) goto L_0x0049
        L_0x0011:
            byte[] r4 = f8511c
            int r5 = r2 + 1
            byte r2 = r9[r2]
            byte r2 = r4[r2]
            r4 = -1
            if (r5 >= r1) goto L_0x0021
            if (r2 == r4) goto L_0x001f
            goto L_0x0021
        L_0x001f:
            r2 = r5
            goto L_0x0011
        L_0x0021:
            if (r2 != r4) goto L_0x0024
            goto L_0x0049
        L_0x0024:
            byte[] r6 = f8511c
            int r7 = r5 + 1
            byte r5 = r9[r5]
            byte r5 = r6[r5]
            if (r7 >= r1) goto L_0x0033
            if (r5 == r4) goto L_0x0031
            goto L_0x0033
        L_0x0031:
            r5 = r7
            goto L_0x0024
        L_0x0033:
            if (r5 != r4) goto L_0x0036
            goto L_0x0049
        L_0x0036:
            int r2 = r2 << 2
            r6 = r5 & 48
            int r6 = r6 >>> 4
            r2 = r2 | r6
            char r2 = (char) r2
            r0.append(r2)
        L_0x0041:
            int r2 = r7 + 1
            byte r6 = r9[r7]
            r7 = 61
            if (r6 != r7) goto L_0x0052
        L_0x0049:
            java.lang.String r9 = r0.toString()
            byte[] r9 = r9.getBytes(r3)
            return r9
        L_0x0052:
            byte[] r8 = f8511c
            byte r6 = r8[r6]
            if (r2 >= r1) goto L_0x005d
            if (r6 == r4) goto L_0x005b
            goto L_0x005d
        L_0x005b:
            r7 = r2
            goto L_0x0041
        L_0x005d:
            if (r6 != r4) goto L_0x0060
            goto L_0x0049
        L_0x0060:
            r5 = r5 & 15
            int r5 = r5 << 4
            r8 = r6 & 60
            int r8 = r8 >>> 2
            r5 = r5 | r8
            char r5 = (char) r5
            r0.append(r5)
        L_0x006d:
            int r5 = r2 + 1
            byte r2 = r9[r2]
            if (r2 != r7) goto L_0x0074
            goto L_0x0049
        L_0x0074:
            byte[] r8 = f8511c
            byte r2 = r8[r2]
            if (r5 >= r1) goto L_0x007f
            if (r2 == r4) goto L_0x007d
            goto L_0x007f
        L_0x007d:
            r2 = r5
            goto L_0x006d
        L_0x007f:
            if (r2 != r4) goto L_0x0082
            goto L_0x0049
        L_0x0082:
            r3 = r6 & 3
            int r3 = r3 << 6
            r2 = r2 | r3
            char r2 = (char) r2
            r0.append(r2)
            r2 = r5
            goto L_0x000d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.d.d.b(java.lang.String):byte[]");
    }
}
