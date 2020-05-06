package com.xiaomi.stat.d;

import com.google.android.exoplayer2.C;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private static final char[] f8525a = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /* renamed from: b  reason: collision with root package name */
    private static final char[] f8526b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String a(byte[] bArr, boolean z) {
        return new String(a(bArr, z ? f8525a : f8526b));
    }

    private static MessageDigest a() {
        return a("MD5");
    }

    static MessageDigest a(String str) {
        try {
            return MessageDigest.getInstance(str);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static byte[] a(String str, String str2) {
        if (str == null) {
            return null;
        }
        try {
            return str.getBytes(str2);
        } catch (UnsupportedEncodingException unused) {
            return null;
        }
    }

    public static byte[] a(byte[] bArr) {
        return a().digest(bArr);
    }

    private static char[] a(byte[] bArr, char[] cArr) {
        int length = bArr.length;
        char[] cArr2 = new char[(length << 1)];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i + 1;
            cArr2[i] = cArr[(bArr[i2] & 240) >>> 4];
            i = i3 + 1;
            cArr2[i3] = cArr[bArr[i2] & 15];
        }
        return cArr2;
    }

    public static String b(byte[] bArr) {
        return a(a(bArr), true);
    }

    private static MessageDigest b() {
        return a("SHA-256");
    }

    public static byte[] b(String str) {
        return a(a(str, C.UTF8_NAME));
    }

    public static String c(String str) {
        return a(b(str), true);
    }

    public static byte[] c(byte[] bArr) {
        return b().digest(bArr);
    }

    public static String d(String str) {
        return a(e(str), true);
    }

    public static String d(byte[] bArr) {
        return a(c(bArr), true);
    }

    public static byte[] e(String str) {
        return c(a(str, C.UTF8_NAME));
    }
}
