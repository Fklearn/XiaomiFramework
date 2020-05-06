package com.miui.idprovider.c;

import com.google.android.exoplayer2.extractor.ts.PsExtractor;

public final class a {

    /* renamed from: a  reason: collision with root package name */
    private static final char[] f5611a = "0123456789ABCDEF".toCharArray();

    private static int a(char c2) {
        int i = 0;
        while (true) {
            char[] cArr = f5611a;
            if (i >= cArr.length) {
                return -1;
            }
            if (cArr[i] == c2) {
                return i;
            }
            i++;
        }
    }

    public static String a(byte[] bArr) {
        if (bArr == null || bArr.length == 0 || bArr.length % 2 != 0) {
            return null;
        }
        int length = bArr.length;
        char[] cArr = new char[(length * 2)];
        for (int i = 0; i < length; i++) {
            byte b2 = bArr[i] & 255;
            int i2 = i * 2;
            char[] cArr2 = f5611a;
            cArr[i2] = cArr2[b2 >>> 4];
            cArr[i2 + 1] = cArr2[b2 & 15];
        }
        return new String(cArr);
    }

    public static byte[] a(String str) {
        if (str == null || str.length() == 0 || str.length() % 2 != 0) {
            return null;
        }
        char[] charArray = str.toCharArray();
        byte[] bArr = new byte[(charArray.length / 2)];
        for (int i = 0; i < bArr.length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) ((a(charArray[i2 + 1]) & 15) | ((a(charArray[i2]) << 4) & PsExtractor.VIDEO_STREAM_MASK));
        }
        return bArr;
    }
}
